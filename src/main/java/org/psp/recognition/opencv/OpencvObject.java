package org.psp.recognition.opencv;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpencvObject {
    final static Logger LOG = LoggerFactory.getLogger(OpencvObject.class.getName());
    private static OpencvObject INSTANCE;
    private Mat mat;
    private MatOfRect matOfRect;
    private MatOfByte matOfByte;

    private OpencvObject() {
        mat = new Mat();
        matOfRect = new MatOfRect();
        matOfByte = new MatOfByte();
    }

    public static OpencvObject getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OpencvObject();
        }
        return INSTANCE;
    }

    public Mat getMat() {
        return mat;
    }

    public void setMat(Mat mat) {
        this.mat = mat;
    }

    public MatOfRect getMatOfRect() {
        return matOfRect;
    }

    public void setMatOfRect(MatOfRect matOfRect) {
        this.matOfRect = matOfRect;
    }

    public MatOfByte getMatOfByte() {
        return matOfByte;
    }

    public void setMatOfByte(MatOfByte matOfByte) {
        this.matOfByte = matOfByte;
    }
}
