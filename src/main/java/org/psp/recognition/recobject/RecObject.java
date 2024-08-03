package org.psp.recognition.recobject;

import org.psp.tools.Timing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.util.ArrayList;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgcodecs.Imgcodecs;

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
    private ArrayList<String> resources;

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

    public ArrayList<String> getResources() {
        return resources;
    }
    protected void setResources(ArrayList<String> resources) {
        this.resources = resources;
    }

    public boolean isRecognized() {
        LOG.debug("isRecognized Start");
        LOG.debug("sourceType = {}", sourceType);
        LOG.debug("source = {}", source);

        Timing timing = new Timing();

        if (sourceType == null || source == null) {
            throw new IllegalStateException("Both values of sourceType and source are necessary");
        }

        switch (sourceType) {
            case FILE:
                JFrame frame = new JFrame("Image");
                JLabel label = new JLabel();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                LOG.debug("JFrame prepared");

                Mat tempMat = new Mat();

                Mat mat = Imgcodecs.imread(source);
                CascadeClassifier cascadeClassifier;
                MatOfRect matOfRect = null;
                for (String resource : resources) {
                    LOG.debug("resource = {}", resource);
                    cascadeClassifier = new CascadeClassifier(resource);
                    matOfRect = new MatOfRect();
                    cascadeClassifier.detectMultiScale(mat, matOfRect);
                    LOG.debug("Recognized {} faces", matOfRect.toArray().length);
                    if (matOfRect.toArray().length > 0) break;
                }
                if (matOfRect.toArray().length > 0) {
                    MatOfByte buf = new MatOfByte();
                    for (Rect rect : matOfRect.toArray()) {
                        Imgproc.rectangle(mat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                    }
                    Imgcodecs.imencode(".jpg", mat, buf);
                    LOG.debug("Image prepared");

                    ImageIcon imageIcon = new ImageIcon(buf.toArray());
                    label.setIcon(imageIcon);
                    frame.getContentPane().add(label);
                    frame.pack();
                    frame.setVisible(true);
                    timing.setEnd();
                    LOG.info(source + ", time of recognition: {}", timing.getBetween());
                } else {
                    LOG.debug(source + " is not recognized");
                    return false;
                }
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
