package org.psp.recognition;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.PropertyConfigurator;
import org.opencv.core.Core;
import org.psp.recognition.recobject.people.face.FaceDetection;

public class Recognition {
    private final static Logger LOG = LoggerFactory.getLogger(Recognition.class.getName());
    private static final String LOG_CONFIG_FILE = System.getProperty("user.dir") + File.separator + "rec_log.properties";

    public static void main(String[] args) {
        PropertyConfigurator.configure(LOG_CONFIG_FILE);
        
        LOG.debug("Recognition start");

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

//        TestRecObject testRecObject = new TestRecObject();
//        if (testRecObject.isRecognized()) {
//            LOG.debug("success");
//        }

        FaceDetection faceDetection = new FaceDetection("C:\\PSP\\Photos\\Feta.jpg");
        if (faceDetection.isRecognized()) {
            LOG.debug("Success");
        }

        LOG.debug("Recognition end");
    }
}