package org.psp.recognition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opencv.core.Core;
import org.psp.recognition.fs.FSDirectory;

public class Recognition {
    private final static Logger LOG = LoggerFactory.getLogger(Recognition.class.getName());

    public static void main(String[] args) {
        AppProperties.getInstance().init();

//        PropertyConfigurator.configure(appProperties.getLogConfigFfile());

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

        FSDirectory workDirectory = new FSDirectory(true, AppProperties.getInstance().getProperties().get("directory").get("work"));
        LOG.debug("workDirectory = {}", workDirectory);
        workDirectory.iterate();

        LOG.debug("Recognition end");
    }
}