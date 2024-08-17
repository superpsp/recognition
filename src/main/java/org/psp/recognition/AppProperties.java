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
    private ArrayList<String> destinations = new ArrayList<>();

    private AppProperties() {};

    public static AppProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppProperties();
        }
        return INSTANCE;
    }

    public void init() {
        PropertyConfigurator.configure(LOG_CONFIG_FILE);
        setIniFile();
    }

    private static void setIniFile() {
        ArrayList<String> iniPatterns = new ArrayList<>();
        iniPatterns.add("recognition.ini");
        FSDirectory currentDirectory = new FSDirectory(System.getProperty("user.dir"), iniPatterns, false, false);
        currentDirectory.iterate();
    }

    public void setProperties(Map<String, Map<String, String>> properties) {
        this.properties = properties;
        setDestinations();
    }

    public Map<String, Map<String, String>> getProperties() {
        return properties;
    }

    public ArrayList<String> getDestinations() {
        return destinations;
    }

    public void setDestinations() {
        this.destinations.add(properties.get("destination").get("FaceDetection.destination"));
        this.destinations.add(properties.get("destination").get("TestRecObject.destination"));
    }
}
