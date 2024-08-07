package org.psp.recognition.fs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;

public class FSDirectory extends AbstractFSObject {
    private final static Logger LOG = LoggerFactory.getLogger(FSDirectory.class.getName());
    private boolean isToDelete = false;
    private ArrayList<String> namePatterns;
    private boolean withSubdirectories = false; // with subdirectories

    public FSDirectory(String pathname, boolean isToDelete) {
        super(pathname);
        this.isToDelete = isToDelete;
    }

    public FSDirectory(boolean withSubdirectories, String pathname) {
        super(pathname);
        this.withSubdirectories = withSubdirectories;
    }

    public FSDirectory(String pathname, ArrayList<String> namePatterns, boolean withSubdirectories) {
        super(pathname);
        this.namePatterns = namePatterns;
        this.withSubdirectories = withSubdirectories;
    }

    public FSDirectory(String pathname) {
        super(pathname);
    }

    public boolean iterate() {
        FSFileNameFilter fsFileNameFilter = new FSFileNameFilter(namePatterns);
        File[] fileList = this.listFiles(fsFileNameFilter);
        if (fileList != null) {
            for (File fsItem : fileList) {
                LOG.debug("fsItem = {}", fsItem);

                if (fsItem.isDirectory() && withSubdirectories) {
                    LOG.debug("Listing directory {}", fsItem);
                    FSDirectory directory = new FSDirectory(fsItem.getPath(), namePatterns, withSubdirectories);
                    if (!directory.iterate())
                        throw new IllegalStateException("Can not list " + directory);
                } else {
                    LOG.debug("directory = {}", this.getPath());
                    FSFile file = new FSFile(fsItem.getPath());
                    if (!file.run())
                        throw new IllegalStateException("Can not run " + file);
                }
                if (isToDelete) {
                    LOG.debug("Delete fsItem {}", fsItem);
                    this.delete();
                }
            }
        }
        return true;
    }
}
