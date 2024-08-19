package org.psp.recognition.fs;

import org.psp.recognition.recobject.TestRecObject;
import org.psp.recognition.recobject.TestYolo;
import org.psp.recognition.recobject.TestYoloV8;
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
        boolean isFaceRecognized = false;
        boolean isRecObjectRecognized = false;
        boolean isYoloRecognized = false;
        boolean isYoloV8Recognized = false;

        FaceDetection faceDetection = FaceDetection.getInstance();
        if (faceDetection.isOn())
            isFaceRecognized = faceDetection.isRecognized(this);

        TestRecObject testRecObject = TestRecObject.getInstance();
        if (testRecObject.isOn())
            isRecObjectRecognized = testRecObject.isRecognized(this);

        TestYolo testYolo = TestYolo.getInstance();
        if (testYolo.isOn())
            isYoloRecognized = testYolo.isRecognized(this);

        TestYoloV8 testYoloV8 = TestYoloV8.getInstance();
        if (testYoloV8.isOn())
            isYoloRecognized = testYoloV8.isRecognized(this);

        return isFaceRecognized || isRecObjectRecognized || isYoloRecognized;
    }
}
