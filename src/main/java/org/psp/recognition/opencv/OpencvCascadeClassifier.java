package org.psp.recognition.opencv;

import org.opencv.objdetect.CascadeClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpencvCascadeClassifier extends CascadeClassifier {
    final static Logger LOG = LoggerFactory.getLogger(OpencvCascadeClassifier.class.getName());
    private static OpencvCascadeClassifier INSTANCE;

    private OpencvCascadeClassifier() {};

    public static OpencvCascadeClassifier getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OpencvCascadeClassifier();
        }
        return INSTANCE;
    }

    public boolean setResource(String resourceAbsolutePath) {
        if (!load(resourceAbsolutePath)) {
            LOG.error("Can not load resource file {}", resourceAbsolutePath);
            return false;
        }
        return true;
    }
}
