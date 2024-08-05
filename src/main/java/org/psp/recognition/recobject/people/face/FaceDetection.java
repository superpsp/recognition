package org.psp.recognition.recobject.people.face;

import org.psp.recognition.fs.FSFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.psp.recognition.recobject.RecObject;

import java.util.ArrayList;

public class FaceDetection extends RecObject {
    final static Logger LOG = LoggerFactory.getLogger(FaceDetection.class.getName());
    private static FaceDetection INSTANCE;


    // From FILE source to SCREEN
    private FaceDetection() {}

    public static FaceDetection getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FaceDetection();
            LOG.debug("A new FaceDetection instance was created");
        }
        return INSTANCE;
    }

    public void initFaceDetection() {
        if (getDestinationType() == null)
            setDestinationType("screen");
        if (getResources() == null)
            resources = new ArrayList<>();
            setResources("face");
    }

    public void setSource(FSFile source) {
        super.setSource(source);
    }

//    private void getFaceResources() {
//        LOG.debug("getResources Start");
//
////        ArrayList<String> faceResources = new ArrayList<>();
////        faceResources.add("lbpcascade_frontalface.xml");
////        faceResources.add("lbpcascade_frontalface_improved.xml");
////        faceResources.add("lbpcascade_profileface.xml");
////        faceResources.add("lbpcascade_silverware.xml");
////        faceResources.add("haarcascade_frontalcatface.xml");
////        faceResources.add("haarcascade_frontalcatface_extended.xml");
////        faceResources.add("haarcascade_frontalface_alt.xml");
////        faceResources.add("haarcascade_frontalface_alt2.xml");
////        LOG.debug("faceResources = {}", faceResources);
//
//        setResources("face");
//
//        LOG.debug("getResources End");
//    }
}
