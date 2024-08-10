package org.psp.recognition.recobject;

import org.psp.recognition.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRecObject extends RecObject {
    final static Logger LOG = LoggerFactory.getLogger(TestRecObject.class.getName());
    private static TestRecObject INSTANCE;

    private TestRecObject() {}

    public static TestRecObject getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TestRecObject();
            LOG.debug("A new TestRecObject instance was created");
        }
        return INSTANCE;
    }

    public void init() {
        if (AppProperties.getInstance().getProperties().get("detection").get("TestRecObject").equals("on")) {
            isOn = true;
        } else {
            LOG.info("TestRecObject is switched off");
            return;
        }
    }
}
