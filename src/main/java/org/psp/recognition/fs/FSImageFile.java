package org.psp.recognition.fs;

import org.psp.recognition.recobject.TestRecObject;
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
        boolean isRecognized = false;

        FaceDetection faceDetection = FaceDetection.getInstance();
        faceDetection.init();
        if (faceDetection.isOn()) {
            faceDetection.setSource(this);
            isRecognized = faceDetection.isRecognized();
        }

        TestRecObject testRecObject = TestRecObject.getInstance();
        testRecObject.init();
        if (testRecObject.isOn()) {
            isRecognized = testRecObject.test();
        }
        return isRecognized;
    }
}
