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
import java.util.ArrayList;
import java.util.Arrays;

public class TestYolo extends RecObject {
    final static Logger LOG = LoggerFactory.getLogger(TestYolo.class.getName());
    private static TestYolo INSTANCE;
    private static boolean isOn;
    private static boolean isInitPerformed = false;
    private final ArrayList<String> yoloResourcePatterns = new ArrayList<>();
    private final String YOLO_CLASS_NAMES = "coco.names.yolo";
    private final String YOLO_CLASS_NAMES_EXCLUDE = "yolo_exclude.yolo";
    private final String YOLO_CFG_FILE = "yolov4.cfg.yolo";
    private final String YOLO_DARKNET_MODEL = "yolov4.weights.yolo";
    private ArrayList<String> classNames;
    private ArrayList<String> excludedClassNames;
    private Net net;
    private DetectionModel detectionModel;
    private MatOfInt classIds;
    private MatOfFloat scores;

    private TestYolo() {}

    public static TestYolo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TestYolo();
            LOG.debug("A new TestYolo instance was created");

            isOn = AppProperties.getInstance().getProperties().get("detection").get("TestYolo").equals("on");
        }
        return INSTANCE;
    }

    public boolean isOn() {
        return isOn;
    }

    public void init() {
        if (!isInitPerformed) {
            LOG.debug("Start init");
            isOn = true;

            if (yoloResourcePatterns.isEmpty()) {
                setYoloResources();

                super.init("TestYolo");
            }
            classNames = getResource(YOLO_CLASS_NAMES).getLines();
            excludedClassNames = getResource(YOLO_CLASS_NAMES_EXCLUDE).getLines();

            net = Dnn.readNetFromDarknet(getResource(YOLO_CFG_FILE).getAbsolutePath(), getResource(YOLO_DARKNET_MODEL).getAbsolutePath());

            detectionModel = new DetectionModel(net);
            detectionModel.setInputParams(1 / 255.0, new Size(416, 416), new Scalar(0), true);

            classIds = new MatOfInt();
            scores = new MatOfFloat();

            isInitPerformed = true;
            LOG.debug("End init");
        }
    }

    private void setYoloResources() {
        LOG.debug("getResources Start");

        yoloResourcePatterns.add(YOLO_CLASS_NAMES);
        yoloResourcePatterns.add(YOLO_CFG_FILE);
        yoloResourcePatterns.add(YOLO_DARKNET_MODEL);
        yoloResourcePatterns.add(YOLO_CLASS_NAMES_EXCLUDE);
        LOG.debug("yoloResourcePatterns = {}", yoloResourcePatterns);

        setResources(yoloResourcePatterns);

        LOG.debug("getResources End");
    }

    public boolean isRecognized(FSFile fsFile) {
        LOG.debug("Start isRecognized");
        init();
        setSource(fsFile);
        return super.isRecognized();
    }

    @Override
    protected void preProcessFile() {
        super.preProcessFile();

        if (isObjectRecognized) {
            isObjectRecognized = false;

            try {
                detectionModel.detect(mat, classIds, scores, matOfRect, 0.6f, 0.4f);
            } catch (Exception e) {
                LOG.error(Arrays.toString(e.getStackTrace()));
            }
            LOG.debug("Detected {} objects", classIds.rows());

            int objectNumber = 0;
            if (classIds.rows() > 0) {
                for (int i = 0; i < classIds.rows(); i++) {
                    int classId = (int) classIds.get(i, 0)[0];
                    if (!excludedClassNames.contains(classNames.get(classId))) {
                        Rect box = new Rect(matOfRect.get(i, 0));
                        Imgproc.rectangle(mat, box, new Scalar(0, 255, 0), 2);

                        double score = scores.get(i, 0)[0];
                        String text = String.format("%s: %.2f", classNames.get(classId), score);
                        Imgproc.putText(mat, text, new Point(box.x, box.y - 5),
                                Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 255, 0), 2);

                        objectNumber++;
                    }
                }
            }
            if (objectNumber > 0) {
                isObjectRecognized = true;
                LOG.info("Recognised {} objects in {}", objectNumber, source.getPath());
            }
        }
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
