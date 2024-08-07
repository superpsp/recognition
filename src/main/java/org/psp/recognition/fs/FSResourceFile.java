package org.psp.recognition.fs;

import org.psp.recognition.recobject.people.face.FaceDetection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FSResourceFile extends FSFile {
    private final static Logger LOG = LoggerFactory.getLogger(FSResourceFile.class.getName());

    public FSResourceFile(String pathname) {
        super(pathname);
    }

    @Override
    public boolean run() {
        LOG.debug("FSResourceFile = {}", this.getPath());
        FaceDetection faceDetection = FaceDetection.getInstance();
        faceDetection.addResource(this);
        return true;
    }
}
