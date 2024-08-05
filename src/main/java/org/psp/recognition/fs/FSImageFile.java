package org.psp.recognition.fs;

import org.psp.recognition.recobject.people.face.FaceDetection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FSImageFile extends FSFile {
    private final static Logger LOG = LoggerFactory.getLogger(FSImageFile.class.getName());

    public FSImageFile(String pathname) {
        super(pathname);
    }

    @Override
    public boolean run() {
        FaceDetection faceDetection = FaceDetection.getInstance();
        faceDetection.setSource(this);
        faceDetection.initFaceDetection();
        if (faceDetection.isRecognized()) {
            return true;
        } else {
            return false;
        }
    }
}
