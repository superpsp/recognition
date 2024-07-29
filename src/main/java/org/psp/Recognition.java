package org.psp;

import java.io.File;

import org.psp.recobject.TestRecObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.PropertyConfigurator;

public class Recognition {
    private final static Logger LOG = LoggerFactory.getLogger(Recognition.class.getName());
    private static final String LOG_CONFIG_FILE = System.getProperty("user.dir") + File.separator + "rec_log.properties";

    public static void main(String[] args) {
        PropertyConfigurator.configure(LOG_CONFIG_FILE);
        
        LOG.debug("Recognition start");

        TestRecObject testRecObject = new TestRecObject();
        if (testRecObject.isRecognized()) {
            LOG.debug("success");
        }

        LOG.debug("Recognition end");
    }
}