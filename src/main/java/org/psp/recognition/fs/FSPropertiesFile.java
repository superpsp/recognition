package org.psp.recognition.fs;

import java.io.IOException;
import java.util.Map;
import org.ini4j.Ini;
import org.psp.recognition.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toMap;

public class FSPropertiesFile extends FSFile{
    private final static Logger LOG = LoggerFactory.getLogger(FSPropertiesFile.class.getName());

    public FSPropertiesFile(String pathname) {
        super(pathname);
    }

    @Override
    public boolean run() {
        Ini ini;
        try {
            ini = new Ini(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AppProperties.getInstance().setProperties(ini.entrySet().stream().collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
        LOG.debug("properties = {}", AppProperties.getInstance().getProperties());
        return true;
    }
}
