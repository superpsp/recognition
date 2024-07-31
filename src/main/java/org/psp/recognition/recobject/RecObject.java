package org.psp.recognition.recobject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.swing.*;

public abstract class RecObject {
    final static Logger LOG = LoggerFactory.getLogger(RecObject.class.getName());

    public enum SourceType {
        FILE
        , VIDEO
        , CAMERA
    }

    public enum DestinationType {
        FILE
        , SCREEN
    }

    private SourceType sourceType;
    private String source;
    private DestinationType destinationType;
    private String destination;

    public SourceType getSourceType() {
        return sourceType;
    }

    protected void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public String getSource() {
        return source;
    }

    protected void setSource(String source) {
        this.source = source;
    }

    public DestinationType getDestinationType() {
        return destinationType;
    }

    protected void setDestinationType(DestinationType destinationType) {
        this.destinationType = destinationType;
    }

    public String getDestination() {
        return destination;
    }

    protected void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isRecognized() {
        LOG.debug("isRecognized Start");
        LOG.debug("sourceType = {}", sourceType);
        LOG.debug("source = {}", source);

        if (sourceType == null || source == null) {
            throw new IllegalStateException("Both values of sourceType and source are necessary: " + sourceType);
        }

        switch (sourceType) {
            case FILE:
                JFrame frame = new JFrame("Image");
                JLabel label = new JLabel();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                LOG.debug("JFrame prepared");

                Mat mat = Imgcodecs.imread(source);
                MatOfByte buf = new MatOfByte();
                Imgcodecs.imencode(".jpg", mat, buf);
                LOG.debug("Image prepared");

                ImageIcon imageIcon = new ImageIcon(buf.toArray());
                label.setIcon(imageIcon);
                frame.getContentPane().add(label);
                frame.pack();
                frame.setVisible(true);
                break;
            case VIDEO:
                break;
            case CAMERA:
                break;
            default:
                throw new IllegalStateException("Unexpected value of sourceType: " + sourceType);
        }

        LOG.debug("isRecognized End");
        return true;
    }
}
