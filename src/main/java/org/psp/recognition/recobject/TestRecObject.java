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
            LOG.debug("destinationType = {}", AppProperties.getInstance().getProperties().get("destination").get("TestRecObject.destinationType"));
            setDestinationType(AppProperties.getInstance().getProperties().get("destination").get("TestRecObject.destinationType"));

            isOn = true;
        } else {
            LOG.info("TestRecObject is switched off");
        }
    }

    @Override
    protected void preProcess() {
        super.preProcess();

        int totalBytes = (int)(mat.total() * mat.elemSize());
        byte[] buffer = new byte[totalBytes];
        mat.get(0, 0, buffer);

        for (int i = 0; i  <totalBytes; i++) {
            if( i%3 == 0) buffer[i] = 0;
        }
        mat.put(0, 0, buffer);

        isObjectRecognized = true;
    }
}
