package org.psp.opencv;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opencv.core.Mat;
import static org.opencv.imgcodecs.Imgcodecs.imread;

public class OpenCVTest {
    private final static Logger LOG = LoggerFactory.getLogger(OpenCVTest.class.getName());

    public void testMethod() {
        LOG.debug("Start");

        Mat image = imread("C://PSP/Photos/Feta.jpg", 1);
//        Mat image1 = HandleImgUtils.correct(image);
        Imgcodecs.imwrite("C://PSP/Photos/Feta_opencv1.jpg", image);
        Mat mat = image.clone();
        Imgproc.Canny(image, mat, 60, 200);
        Imgcodecs.imwrite("C://PSP/Photos/Feta_opencv2.jpg", mat);

        LOG.debug("End");
    }
}
