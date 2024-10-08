package org.psp.recognition.fs;

import org.psp.recognition.recobject.TestYolo;
import org.psp.recognition.recobject.TestYoloV8;
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
        LOG.debug("FSResourceFile = {}", this.getAbsolutePath());

        FaceDetection faceDetection = FaceDetection.getInstance();
        if (faceDetection.isOn())
            faceDetection.addResource(this);

        TestYolo testYolo = TestYolo.getInstance();
        if (testYolo.isOn())
            testYolo.addResource(this);

        TestYoloV8 testYoloV8 = TestYoloV8.getInstance();
        if (testYoloV8.isOn())
            testYoloV8.addResource(this);
        return true;
    }
}
