package org.psp.recognition;

import java.io.File;
import java.util.Map;

public class AppProperties {
    public static final String LOG_CONFIG_FILE = System.getProperty("user.dir") + File.separator + "rec_log.properties";
    public static Map<String, Map<String, String>> properties;
}
