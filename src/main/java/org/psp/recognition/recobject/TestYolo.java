package org.psp.recognition.recobject;

import org.opencv.core.*;
import org.opencv.dnn.DetectionModel;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.psp.recognition.AppProperties;
import org.psp.recognition.fs.FSFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class TestYolo extends RecObject {
    final static Logger LOG = LoggerFactory.getLogger(TestYolo.class.getName());
    private static TestYolo INSTANCE;
    private static boolean isInitPerformed = false;
    private final ArrayList<String> yoloResourcePatterns = new ArrayList<>();
    private final String YOLO_CLASS_NAMES = "coco.names.yolo";
    private final String YOLO_CFG_FILE = "yolov4.cfg.yolo";
    private final String YOLO_DARKNET_MODEL = "yolov4.weights.yolo";
    private ArrayList<String> classNames;
    private Net net;
    private DetectionModel detectionModel;
    private MatOfInt classIds;
    private MatOfFloat scores;

    private TestYolo() {}

    public static TestYolo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TestYolo();
            LOG.debug("A new TestYolo instance was created");
        }
        return INSTANCE;
    }

    public void init() {
        if (!isInitPerformed && AppProperties.getInstance().getProperties().get("detection").get("TestYolo").equals("on")) {
            isOn = true;

            if (yoloResourcePatterns.isEmpty()) {
                setFaceResources();

                LOG.debug("destinationType = {}", AppProperties.getInstance().getProperties().get("destination").get("TestYolo.destinationType"));
                setDestinationType(AppProperties.getInstance().getProperties().get("destination").get("TestYolo.destinationType"));

                LOG.debug("destination = {}", AppProperties.getInstance().getProperties().get("destination").get("TestYolo.destination"));
                if (AppProperties.getInstance().getProperties().get("destination").get("TestYolo.destinationType").equals("file")) {
                    FSFile fsFile = new FSFile(
                            AppProperties.getInstance().getProperties().get("directory").get("work")
                                    + File.separator + AppProperties.getInstance().getProperties().get("destination").get("TestYolo.destination")
                    );
                    setDestination(fsFile);
                }
            }
            classNames = getResource(YOLO_CLASS_NAMES).getLines();

            net = Dnn.readNetFromDarknet(getResource(YOLO_CFG_FILE).getAbsolutePath(), getResource(YOLO_DARKNET_MODEL).getAbsolutePath());

            detectionModel = new DetectionModel(net);
            detectionModel.setInputParams(1 / 255.0, new Size(416, 416), new Scalar(0), true);

            classIds = new MatOfInt();
            scores = new MatOfFloat();

            isInitPerformed = true;
        } else {
            LOG.info("TestYolo is switched off");
            return;
        }
    }

    private void setFaceResources() {
        LOG.debug("getResources Start");

        yoloResourcePatterns.add(YOLO_CLASS_NAMES);
        yoloResourcePatterns.add(YOLO_CFG_FILE);
        yoloResourcePatterns.add(YOLO_DARKNET_MODEL);
        LOG.debug("yoloResourcePatterns = {}", yoloResourcePatterns);

        setResources(yoloResourcePatterns);

        LOG.debug("getResources End");
    }

    @Override
    protected void preProcess() {
        super.preProcess();

        try {
            detectionModel.detect(mat, classIds, scores, matOfRect, 0.6f, 0.4f);
            LOG.debug("Detected {} objects", classIds.rows());
        } catch (Exception e) {
            LOG.error(Arrays.toString(e.getStackTrace()));
        }

        if (classIds.rows() > 0) {
            isObjectRecognized = true;

            for (int i = 0; i < classIds.rows(); i++) {
                Rect box = new Rect(matOfRect.get(i, 0));
                Imgproc.rectangle(mat, box, new Scalar(0, 255, 0), 2);

                int classId = (int) classIds.get(i, 0)[0];
                double score = scores.get(i, 0)[0];
                String text = String.format("%s: %.2f", classNames.get(classId), score);
                Imgproc.putText(mat, text, new Point(box.x, box.y - 5),
                        Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 255, 0), 2);
            }
        }
//        classIds.release();
//        scores.release();
//        detectionModel.finalize();
    }

    private FSFile getResource(String resource) {
        FSFile fsFile = null;

        for (FSFile resourceFile : resourceFiles) {
            if (resourceFile.getName().equals(resource)) {
                fsFile = resourceFile;
                break;
            }
        }
        if (fsFile == null)
            throw new IllegalStateException("Can not find resource file " + resource);

        return fsFile;
    }

    @Override
    public void addResource(FSFile fsFile) {
        super.addResource(fsFile);
    }
}
