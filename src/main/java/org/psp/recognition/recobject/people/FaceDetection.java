package org.psp.recognition.recobject.people;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.psp.recognition.recobject.RecObject;

public class FaceDetection extends RecObject {
    final static Logger LOG = LoggerFactory.getLogger(FaceDetection.class.getName());

    // From FILE source to SCREEN
    public FaceDetection(String source) {
        setSourceType(SourceType.FILE);
        setSource(source);
        setDestinationType(DestinationType.SCREEN);
    }
}
