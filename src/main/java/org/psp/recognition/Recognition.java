package org.psp.recognition;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.PropertyConfigurator;
import org.opencv.core.Core;
import org.psp.recognition.fs.FSDirectory;

public class Recognition {
    private final static Logger LOG = LoggerFactory.getLogger(Recognition.class.getName());

    public static void main(String[] args) {
        AppProperties appProperties = new AppProperties();

        PropertyConfigurator.configure(appProperties.LOG_CONFIG_FILE);
        
        LOG.debug("Recognition start");

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

//        TestRecObject testRecObject = new TestRecObject();
//        if (testRecObject.isRecognized()) {
//            LOG.debug("success");
//        }

//        FaceDetection faceDetection = new FaceDetection("C:\\PSP\\Photos\\Feta.jpg");
//        FaceDetection faceDetection = new FaceDetection("C:\\PSP\\Photos\\WhatsApp Image 2022-01-29 at 21.14.11.jpeg");
//        if (faceDetection.isRecognized()) {
//            LOG.debug("Recognized");
//        } else {
//            LOG.info("Not recognized");
//        }

        FSDirectory currentDirectory = new FSDirectory(System.getProperty("user.dir"), null, "ini", 0);
        LOG.debug("currentDirectory = {}", currentDirectory);
        currentDirectory.iterate();

        FSDirectory workDirectory = new FSDirectory(AppProperties.properties.get("directory").get("work"), null, null, 1);
        LOG.debug("workDirectory = {}", workDirectory);
        workDirectory.iterate();

        LOG.debug("Recognition end");
    }
}