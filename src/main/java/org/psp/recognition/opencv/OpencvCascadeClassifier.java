package org.psp.recognition.opencv;

import org.opencv.objdetect.CascadeClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpencvCascadeClassifier extends CascadeClassifier {
    final static Logger LOG = LoggerFactory.getLogger(OpencvCascadeClassifier.class.getName());

    public OpencvCascadeClassifier(String absolutePath) {
        super(absolutePath);
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
    }
}
