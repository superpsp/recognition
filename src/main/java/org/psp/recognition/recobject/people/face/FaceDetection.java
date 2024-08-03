package org.psp.recognition.recobject.people.face;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.psp.recognition.recobject.RecObject;

import java.io.File;
import java.util.ArrayList;

public class FaceDetection extends RecObject {
    final static Logger LOG = LoggerFactory.getLogger(FaceDetection.class.getName());

    // From FILE source to SCREEN
    public FaceDetection(String source) {
        setSourceType(SourceType.FILE);
        setSource(source);
        setDestinationType(DestinationType.SCREEN);
        getFaceResources();
    }

    private void getFaceResources() {
        LOG.debug("getResources Start");

        ArrayList<String> faceResources = new ArrayList<>();
        faceResources.add(new File("src\\main\\resources\\haarcascades\\haarcascade_frontalcatface.xml").getAbsolutePath());
        faceResources.add(new File("src\\main\\resources\\haarcascades\\haarcascade_frontalcatface_extended.xml").getAbsolutePath());
        faceResources.add(new File("src\\main\\resources\\haarcascades\\haarcascade_frontalface_alt.xml").getAbsolutePath());
        faceResources.add(new File("src\\main\\resources\\haarcascades\\haarcascade_frontalface_alt2.xml").getAbsolutePath());
        LOG.debug("faceResources = {}", faceResources);

        setResources(faceResources);

        LOG.debug("getResources End");
    }
}
