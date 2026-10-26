package org.psp.recognition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opencv.core.Core;
import org.psp.recognition.fs.FSDirectory;

import java.util.ArrayList;

public class Recognition {
    private final static Logger LOG = LoggerFactory.getLogger(Recognition.class.getName());
    private final static String JPEG_PATTERN = "jpeg";
    private final static String JPG_PATTERN = "jpg";

    public static void main(String[] args) {
        AppProperties.getInstance().init();

        LOG.debug("Recognition start");

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        ArrayList<String> namePatterns = new ArrayList<>();
        namePatterns.add(JPEG_PATTERN);
        namePatterns.add(JPG_PATTERN);
        FSDirectory workDirectory = new FSDirectory(AppProperties.getInstance().getProperties().get("directory").get("work"), namePatterns, false, false);
        LOG.debug("workDirectory = {}", workDirectory);
        workDirectory.iterate();

        LOG.debug("Recognition end");
    }
}