package org.psp.recognition.recobject;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.Boost;
import org.opencv.ml.Ml;
import org.psp.recognition.AppProperties;
import org.psp.recognition.fs.FSFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRecObject extends RecObject {
    final static Logger LOG = LoggerFactory.getLogger(TestRecObject.class.getName());
    private static TestRecObject INSTANCE;
    private static boolean isInitPerformed = false;
    private static boolean isOn;

    private TestRecObject() {}

    public static TestRecObject getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TestRecObject();
            LOG.debug("A new TestRecObject instance was created");

            isOn = AppProperties.getInstance().getProperties().get("detection").get("TestRecObject").equals("on");
        }
        return INSTANCE;
    }

    public boolean isOn() {
        return isOn;
    }

    public void init() {
        if (!isInitPerformed) {
            super.init("TestRecObject");
            isInitPerformed = true;
        }
    }

    public boolean isRecognized(FSFile fsFile) {
        init();
        setSource(fsFile);
        return super.isRecognized();
    }
    @Override
    protected void preProcess() {
        super.preProcess();
//        removeChannel();
//        smoothAverage();
//        smoothGaussian();
//        smoothMedian();
//        smoothBilateral();
//        adaBoost();
//        transformSobel();
//        transformLaplacian();
        transformCanny();
//        transformHoughLines();
        isObjectRecognized = true;
    }

    private void removeChannel() {
        int totalBytes = (int)(mat.total() * mat.elemSize());
        byte[] buffer = new byte[totalBytes];
        matDestination = mat;
        matDestination.get(0, 0, buffer);

        for (int i = 0; i  < totalBytes; i++) {
            if( i%3 == 0) buffer[i] = 0; // i%3 - blue {Blue, Green, Red}
//            if( i%2 == 0) buffer[i] = 0; // i%2 - green {Blue, Green, Red} ??????????????
//            if( i%1 == 0) buffer[i] = 0; // i%1 - red {Blue, Green, Red} ??????????????
        }
        matDestination.put(0, 0, buffer);
    }

    private void smoothAverage() {
//        Imgproc.blur(mat, matDestination, new Size(10.0, 10.0));
        Imgproc.blur(mat, matDestination, new Size(10.0, 10.0), new Point(), Core.BORDER_REPLICATE);
    }

    private void smoothGaussian() {
        Imgproc.GaussianBlur(mat, matDestination, new Size(9.0, 9.0), 0); // Size %3 !%2
    }

    private void smoothMedian() {
        Imgproc.medianBlur(mat, matDestination, 9); // kize odd and greater than 1
    }

    private void smoothBilateral() {
        Imgproc.bilateralFilter(mat, matDestination
                , 10 // int d parameter is the diameter of the considered neighborhood.
                        // If it is non-positive, the diameter will be calculated from the sigmaSpace parameter.
                , 10.0
                , 10.0
                , Core.BORDER_REPLICATE
        ); // kize odd and greater than 1
    }

    private void adaBoost() {
//        Name/Feature Height (h1) Hair (h2) Beard (h3) Gender (f(x))
//        Katherine       1.69        Long    Absent      Female
//        Dan             1.76        Short   Absent      Male
//        Sam             1.80        Short   Absent      Male
//        Laurent         1.83        Short   Present     Male
//        Sara            1.77        Short   Absent      Female

        Mat tempMat = new Mat(5, 3, CvType.CV_32FC1, new Scalar(0));
        mat = tempMat;

        mat.put(0, 0, new float[]{1.69f, 1, 0});
        mat.put(1, 0, new float[]{1.76f, 0, 0});
        mat.put(2, 0, new float[]{1.80f, 0, 0});
        mat.put(3, 0, new float[]{1.77f, 0, 0});
        mat.put(4, 0, new float[]{1.83f, 0, 1});
        LOG.debug("mat.cols() = {}", mat.cols());

        tempMat = new Mat(5, 1, CvType.CV_32SC1, new Scalar(0));
        matDestination = tempMat;

        matDestination.put(0,0, new int[]{0,1,1,0,1});

        Boost boost = Boost.create();
        boost.setBoostType(Boost.DISCRETE);
        boost.setWeakCount(mat.cols());
        boost.setMinSampleCount(4);

        boost.train(mat, Ml.ROW_SAMPLE, matDestination);

        for (int i = 0; i < 5; i++) {
            LOG.info("Result = {}", boost.predict(mat.row(i)));
        }

        tempMat = new Mat(1,3,CvType.CV_32FC1, new Scalar(0));
        tempMat.put(0, 0, new float[]{1.60f, 1, 0});
        LOG.info(tempMat.dump());
        LOG.info("New (woman) = {}", boost.predict(tempMat));

        tempMat.put(0, 0, new float[]{1.8f, 0, 1});
        LOG.info("New (man) = {}", boost.predict(tempMat));

        tempMat.put(0, 0, new float[]{1.7f, 1, 0});
        LOG.info("New (?) = {}", boost.predict(tempMat));

//        boost.finalize();
        tempMat.release();
    }

    private void transformSobel() {
        Imgproc.Sobel(mat, matDestination, -1, 2, 0);
    }

    private void transformLaplacian() {
        Imgproc.Laplacian(mat, matDestination, -1);
    }

    private void transformCanny() {
        Imgproc.Canny(mat, matDestination, 10, 50, 3, false);
    }

    private void transformHoughLines() {
        Mat canny = new Mat();
        Imgproc.Canny(mat, canny, 10, 50, 5, false);
        Mat linesP = new Mat();
        Imgproc.HoughLinesP(canny, linesP, 1, Math.PI/180, 238);

        matDestination = mat.clone();
        for (int i = 0; i < linesP.cols(); i++) {
            double[] val = linesP.get(0, i);
            Imgproc.line(matDestination, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(0, 0, 255), 2);
        }
        canny.release();
        linesP.release();
    }
}
