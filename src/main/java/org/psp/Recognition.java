package org.psp;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.PropertyConfigurator;

public class Recognition {
    private final static Logger LOG = LoggerFactory.getLogger(Recognition.class.getName());
    private static final String LOG_CONFIG_FILE = System.getProperty("user.dir") + File.separator + "rec_log.properties";

    public static void main(String[] args) {
        PropertyConfigurator.configure(LOG_CONFIG_FILE);
        
        LOG.debug("Recognition start");
        System.out.printf("Hello and welcome!\n");

        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }
        LOG.debug("Recognition end");
    }
}