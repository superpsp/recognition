package org.psp.recognition;

import org.apache.log4j.PropertyConfigurator;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import org.psp.recognition.fs.FSDirectory;

public class AppProperties {
    private static AppProperties INSTANCE;
    private static final String LOG_CONFIG_FILE = System.getProperty("user.dir") + File.separator + "rec_log.properties";
    private Map<String, Map<String, String>> properties;

    private AppProperties() {};

    public static AppProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppProperties();
        }
        return INSTANCE;
    }

    public void init() {
        PropertyConfigurator.configure(LOG_CONFIG_FILE);
        findIniFile();
    }

    private static void findIniFile() {
        ArrayList<String> iniPatterns = new ArrayList<>();
        iniPatterns.add("recognition.ini");
        FSDirectory currentDirectory = new FSDirectory(System.getProperty("user.dir"), iniPatterns, false);
        currentDirectory.iterate();
    }

    public void setProperties(Map<String, Map<String, String>> properties) {
        this.properties = properties;
    }

    public Map<String, Map<String, String>> getProperties() {
        return properties;
    }
}
