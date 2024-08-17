package org.psp.recognition.fs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

public class AbstractFSObject extends File {
    private final static Logger LOG = LoggerFactory.getLogger(AbstractFSObject.class.getName());
    protected boolean isToDelete = false;

    public AbstractFSObject(String pathname) {
        super(pathname);
    }

    protected void deleteFSObject() {
        if (isToDelete) {
            if (!this.delete())
                throw new IllegalStateException("Can not delete " + this.getPath());
            LOG.debug("{} deleted", this.getPath());
        }
    }
}
