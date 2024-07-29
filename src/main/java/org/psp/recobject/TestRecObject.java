package org.psp.recobject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRecObject implements RecObject {
    final static Logger LOG = LoggerFactory.getLogger(TestRecObject.class.getName());

    @Override
    public boolean isRecognized() {
        LOG.debug("Start");
        LOG.debug("End");
        return true;
    }
}
