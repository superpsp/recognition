package org.psp.recognition.recobject.people.face;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.psp.recognition.AppProperties;
import org.psp.recognition.opencv.OpencvCascadeClassifier;
import org.psp.tools.RecognitionTools;
import org.psp.recognition.fs.FSFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.psp.recognition.recobject.RecObject;
import java.util.ArrayList;

public class FaceDetection extends RecObject {
    final static Logger LOG = LoggerFactory.getLogger(FaceDetection.class.getName());
    private static FaceDetection INSTANCE;
    private static boolean isInitPerformed = false;
    private static boolean isOn;
    private final ArrayList<String> faceResourcePatterns = new ArrayList<>();

    // From FILE source to SCREEN
    private FaceDetection() {}

    public static FaceDetection getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FaceDetection();
            LOG.debug("A new FaceDetection instance was created");

            isOn = AppProperties.getInstance().getProperties().get("detection").get("FaceDetection").equals("on");
        }
        return INSTANCE;
    }

    public boolean isOn() {
        return isOn;
    }

    public void init() {
        if (!isInitPerformed) {
            if (faceResourcePatterns.isEmpty()) {
                setFaceResources();

                super.init("FaceDetection");
            }
            isInitPerformed = true;
        }
    }

    public boolean isRecognized(FSFile fsFile) {
        init();
        setSource(fsFile);
        return super.isRecognized();
    }

    @Override
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
        LOG.debug("setFaceResources Start");

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

        LOG.debug("setFaceResources End");
    }

    @Override
    protected void preProcessFile() {
        super.preProcessFile();

        if (isObjectRecognized) {
            isObjectRecognized = false;

            for (FSFile resource : resourceFiles) {
                LOG.debug("resource = {}", resource);

                Mat grayMat = new Mat();
                Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY);

                OpencvCascadeClassifier opencvCascadeClassifier = new OpencvCascadeClassifier(resource.getAbsolutePath());
                LOG.debug("OpencvCascadeClassifier created");
//            opencvCascadeClassifier.detectMultiScale(grayMat, matOfRect);
                opencvCascadeClassifier.detectMultiScale(grayMat, matOfRect, 1.15, 5);
                LOG.debug("Detection completed");

                grayMat.release();
                try {
                    opencvCascadeClassifier.finalize();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
                LOG.info("Recognized {} objects", matOfRect.toArray().length);
                if (matOfRect.toArray().length > 0)
                    LOG.info("Used resource: {}", resource);

                if (matOfRect != null && matOfRect.toArray().length != 0) {
                    isObjectRecognized = true;
                    break;
                }
            }
        }
    }

    @Override
    protected void postProcessFile() {
        if (isObjectRecognized) {
            LOG.debug("matOfRect.toArray().length = {}", matOfRect.toArray().length);
            for (Rect rect : matOfRect.toArray()) {
                Imgproc.rectangle(mat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
            }
        }
        super.postProcessFile();
    }
}
