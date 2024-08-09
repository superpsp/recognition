package org.psp.recognition.recobject.people.face;

import org.psp.recognition.AppProperties;
import org.psp.tools.RecognitionTools;
import org.psp.recognition.fs.FSFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.psp.recognition.recobject.RecObject;
import java.io.File;
import java.util.ArrayList;

public class FaceDetection extends RecObject {
    final static Logger LOG = LoggerFactory.getLogger(FaceDetection.class.getName());
    private static FaceDetection INSTANCE;
    private ArrayList<String> faceResourcePatterns = new ArrayList<>();


    // From FILE source to SCREEN
    private FaceDetection() {}

    public static FaceDetection getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FaceDetection();
            LOG.debug("A new FaceDetection instance was created");
        }
        return INSTANCE;
    }

    public void init() {
        if (faceResourcePatterns.isEmpty()) {
            setFaceResources();

            LOG.debug("destinationType = {}", AppProperties.getInstance().getProperties().get("destination").get("FaceDetection.destinationType"));
            setDestinationType(AppProperties.getInstance().getProperties().get("destination").get("FaceDetection.destinationType"));

            LOG.debug("destination = {}", AppProperties.getInstance().getProperties().get("destination").get("FaceDetection.destination"));
            if (AppProperties.getInstance().getProperties().get("destination").get("FaceDetection.destinationType").equals("file")) {
                FSFile fsFile = new FSFile(
                    AppProperties.getInstance().getProperties().get("directory").get("work")
                    + File.separator + AppProperties.getInstance().getProperties().get("destination").get("FaceDetection.destination")
                );
                if (fsFile.exists()) {
                    LOG.debug("Clearing FaceDetection.destination directory {}", fsFile);
                    fsFile.delete();
                }
                LOG.debug("Creating FaceDetection.destination directory {}", fsFile);
                fsFile.mkdirs();
                setDestination(fsFile);
            }
        }
    }

    public void setSource(FSFile source) {
        super.setSource(source);
    }

    public void addResource(FSFile fsFile) {
        LOG.debug("fsFile = {}", fsFile);
        boolean toAdd = false;
        for (String faceResourcePattern : faceResourcePatterns) {
            if (RecognitionTools.isMatched(fsFile.getName(), faceResourcePattern, false)) {
                toAdd = true;
                break;
            }
        }
        if (toAdd) {
            super.addResource(fsFile);
        }
    }

    private void setFaceResources() {
        LOG.debug("getResources Start");

        faceResourcePatterns.add("lbpcascade_frontalface.xml");
        faceResourcePatterns.add("lbpcascade_frontalface_improved.xml");
        faceResourcePatterns.add("lbpcascade_profileface.xml");
//        faceResourcePatterns.add("lbpcascade_silverware.xml");
        faceResourcePatterns.add("haarcascade_frontalcatface.xml");
        faceResourcePatterns.add("haarcascade_frontalcatface_extended.xml");
        faceResourcePatterns.add("haarcascade_frontalface_alt.xml");
        faceResourcePatterns.add("haarcascade_frontalface_alt2.xml");
        LOG.debug("faceResources = {}", faceResourcePatterns);

        setResources(faceResourcePatterns);

        LOG.debug("getResources End");
    }
}
