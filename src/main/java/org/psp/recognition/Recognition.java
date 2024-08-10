package org.psp.recognition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opencv.core.Core;
import org.psp.recognition.fs.FSDirectory;

public class Recognition {
    private final static Logger LOG = LoggerFactory.getLogger(Recognition.class.getName());

    public static void main(String[] args) {
        AppProperties.getInstance().init();

        LOG.debug("Recognition start");

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        FSDirectory workDirectory = new FSDirectory(true, AppProperties.getInstance().getProperties().get("directory").get("work"));
        LOG.debug("workDirectory = {}", workDirectory);
        workDirectory.iterate();

        LOG.debug("Recognition end");
    }
}