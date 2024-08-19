package org.psp.recognition.fs;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;

public class AbstractFSObject extends File {
    private final static Logger LOG = LoggerFactory.getLogger(AbstractFSObject.class.getName());
    protected boolean isToDelete = false;

    public AbstractFSObject(String pathname) {
        super(pathname);
    }

    public void deleteFSObject() {
        if (isToDelete) {
            LOG.debug("Deleting {}", this.getPath());
            try {
                FileUtils.deleteDirectory(this);
            } catch (IOException e) {
                throw new IllegalStateException("Can not delete " + this.getPath());
            }
        }
    }
}
