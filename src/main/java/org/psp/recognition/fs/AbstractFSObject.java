package org.psp.recognition.fs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

public class AbstractFSObject extends File {
    private final static Logger LOG = LoggerFactory.getLogger(AbstractFSObject.class.getName());

    public AbstractFSObject(String pathname) {
        super(pathname);
    }
}
