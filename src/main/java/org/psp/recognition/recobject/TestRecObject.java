package org.psp.recognition.recobject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRecObject extends RecObject {
    final static Logger LOG = LoggerFactory.getLogger(TestRecObject.class.getName());

    public TestRecObject() {
        LOG.debug("TestRecObject Start");
//        setSourceType("file");
        LOG.debug("TestRecObject End");
    }
}
