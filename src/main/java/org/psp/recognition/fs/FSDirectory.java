package org.psp.recognition.fs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;

public class FSDirectory extends AbstractFSObject {
    private final static Logger LOG = LoggerFactory.getLogger(FSDirectory.class.getName());
    private boolean isToDelete = false;
    private String nameMask = null;
    private String extensionMask = null;
    private int level = 1; // with subdirectories

    public FSDirectory(String pathname, boolean isToDelete) {
        super(pathname);
        this.isToDelete = isToDelete;
    }

    public FSDirectory(String pathname, String nameMask, String extensionMask, int level) {
        super(pathname);
        this.nameMask = nameMask;
        this.extensionMask = extensionMask;
        this.level = level;
    }

    public FSDirectory(String pathname) {
        super(pathname);
    }

    public boolean iterate() {
        FSFileNameFilter fsFileNameFilter = new FSFileNameFilter(nameMask, extensionMask);
        File[] fileList = this.listFiles(fsFileNameFilter);
        LOG.debug("fileList = ", fileList);
        for (File fsItem : fileList) {
            LOG.debug("fsItem = {}", fsItem);

            if (fsItem.isDirectory() && level == 1) {
                LOG.debug("Listing directory {}", fsItem);
                FSDirectory directory = new FSDirectory(fsItem.getPath(), nameMask, extensionMask, level);
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
        return true;
    }
}
