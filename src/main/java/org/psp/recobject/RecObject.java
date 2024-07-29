package org.psp.recobject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface RecObject {
    final static Logger LOG = LoggerFactory.getLogger(RecObject.class.getName());

    public boolean isRecognized();
}
