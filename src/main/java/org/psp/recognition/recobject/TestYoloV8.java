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

public class TestYoloV8 extends RecObject {
    final static Logger LOG = LoggerFactory.getLogger(TestYoloV8.class.getName());
    private static TestYoloV8 INSTANCE;
    private static boolean isOn;
    private static boolean isInitPerformed = false;
    private final ArrayList<String> yoloResourcePatterns = new ArrayList<>();
    private final String YOLO_CLASS_NAMES = "coco.names.yolo";
    private final String YOLO_CLASS_NAMES_EXCLUDE = "yolo_exclude.yolo";
    private final String YOLO_MODEL = "yolov9m.onnx.yolo";
    private ArrayList<String> classNames;
    private ArrayList<String> excludedClassNames;
    private ArrayList<String> recognisedObjects;
    private Net net;
    private Mat blob;
    private Mat predict;
    private Mat mask;
    private Mat score;
    private MatOfRect2d boxes;
    private DetectionModel detectionModel;
    private MatOfInt classIds;
    private MatOfFloat scores;

    private TestYoloV8() {}

    public static TestYoloV8 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TestYoloV8();
            LOG.debug("A new TestYoloV8 instance was created");

            isOn = AppProperties.getInstance().getProperties().get("detection").get("TestYoloV8").equals("on");
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

                super.init(getClass().getSimpleName());
            }
            classNames = getResource(YOLO_CLASS_NAMES).getLines();
            excludedClassNames = getResource(YOLO_CLASS_NAMES_EXCLUDE).getLines();

            net = Dnn.readNetFromONNX(getResource(YOLO_MODEL).getAbsolutePath());

            classIds = new MatOfInt();
            scores = new MatOfFloat();

            recognisedObjects = new ArrayList<>();

            isInitPerformed = true;
            LOG.debug("End init");
        }
    }

    private void setYoloResources() {
        LOG.debug("getResources Start");

        yoloResourcePatterns.add(YOLO_CLASS_NAMES);
        yoloResourcePatterns.add(YOLO_MODEL);
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

            blob = Dnn.blobFromImage(mat, 1/255.0, new Size(640, 640));
            net.setInput(blob);
            predict = net.forward();
            mask = predict.reshape(0, 1).reshape(0, predict.size(1));
            LOG.debug("blob = {}, predict = {}, mask.cols() = {}", blob, predict, mask.cols());

            double width = mat.cols() / 640.0;
            double heigh = mat.rows() / 640.0;
            LOG.debug("width = {}, heigh = {}", width, heigh);

            Rect2d[] rect2ds = new Rect2d[mask.cols()];

            float[] scoref = new float[mask.cols()];
            int[] classIds = new int[mask.cols()];

            for (int i = 0; i < mask.cols(); i++) {
                double[] x = mask.col(i).get(0, 0);
                double[] y = mask.col(i).get(1, 0);
                double[] w = mask.col(i).get(2, 0);
                double[] h = mask.col(i).get(3, 0);

                rect2ds[i] = new Rect2d((x[0] - w[0] / 2) * width, (y[0] - h[0] / 2) * heigh
                        , w[0] * width, h[0] * heigh);

                score = mask.col(i).submat(4, predict.size(1) - 1, 0, 1);
                Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(score);

                scoref[i] = (float) minMaxLocResult.maxVal;
                classIds[i] = (int) minMaxLocResult.maxLoc.y;
            }
            MatOfRect2d boxes = new MatOfRect2d(rect2ds);
            MatOfFloat scores = new MatOfFloat(scoref);
            MatOfInt indices = new MatOfInt(classIds);
            LOG.debug("boxes = {}, scores = {}, indices = {}", boxes, scores, indices);
            Dnn.NMSBoxes(boxes, scores, 0.3f, 0.5f, indices);

            int objectNumber = 0;
            if (indices.rows() > 0) {
                LOG.debug("Detected {} objects", indices.rows());
                String className;
                try {
                    int[] result = indices.toArray();
                    LOG.debug("result = {}", result);

                    for (int i : result) {
                        className = classNames.get(classIds[i]);
                        if (!excludedClassNames.contains(className)) {
                            Imgproc.rectangle(mat, new Rect(rect2ds[i].tl(), rect2ds[i].size())
                                    , new Scalar(0, 255, 0), 1);
                            String text = String.format("%s: %.2f", className, scoref[i]);
                            Imgproc.putText(mat, text, rect2ds[i].tl()
                                    , Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 255, 0));

                            recognisedObjects.add(source.getPath() + ":" + className + ":" + scoref[i]);
                            objectNumber++;
                        }
                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage());
                    LOG.error(Arrays.toString(e.getStackTrace()));
                }
            } else {
                LOG.debug("Score threshold is less than expected for {}", source.getPath());
            }
//            blob.release();
//            predict.release();
//            mask.release();
//            score.release();
            boxes.release();
            scores.release();
            indices.release();

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

    public ArrayList<String> getRecognisedObjects() {
        return recognisedObjects;
    }
}
