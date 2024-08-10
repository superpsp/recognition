package org.psp.recognition.recobject;

import org.psp.recognition.opencv.OpencvCascadeClassifier;
import org.psp.tools.Timing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgcodecs.Imgcodecs;
import org.psp.recognition.fs.FSDirectory;
import org.psp.recognition.fs.FSFile;
import org.psp.recognition.AppProperties;
import org.psp.recognition.opencv.OpencvObject;

public class RecObject {
    final static Logger LOG = LoggerFactory.getLogger(RecObject.class.getName());
    protected FSFile source;
    protected String sourceType;
    protected String destinationType;
    protected FSFile destination;
    protected ArrayList<FSFile> resourceFiles = new ArrayList<>();

    protected String getDestinationType() {
        return destinationType;
    }

    public void setDestination(FSFile destination) {
        this.destination = destination;
    }

    protected void setResources(ArrayList<String> fresourcePatterns) {
        LOG.debug("fresourcePatterns = {}", fresourcePatterns);

        String pathName = System.getProperty("user.dir")
                + File.separator + AppProperties.getInstance().getProperties().get("directory").get("resource");
        LOG.debug("pathName = {}", pathName);
        FSDirectory resourceDirectory = new FSDirectory(pathName, fresourcePatterns, true);
        resourceDirectory.iterate();

        LOG.debug("resourceFiles = {}", resourceFiles);
    }

    protected void setDestinationType(String destinationType) {
        this.destinationType = destinationType;
    }

    protected void setSourceType(String sourceType) {
        LOG.debug("sourceType = {}", sourceType);
        this.sourceType = sourceType;
    }

    protected void setSource(FSFile source) {
        LOG.debug("source = {}", source);
        this.source = source;
        if (source instanceof FSFile)
            setSourceType("file");
    }

    protected void addResource(FSFile fsFile) {
        LOG.debug("fsFile = {}", fsFile);
        resourceFiles.add(fsFile);
    }

    public ArrayList<FSFile> getResources() {
        return resourceFiles;
    }

   public boolean isRecognized() {
        LOG.debug("isRecognized Start");
        LOG.debug("sourceType = {}", sourceType);
        LOG.debug("source = {}", source);

        Timing timing = new Timing();

        if (sourceType == null || source == null) {
            throw new IllegalStateException("Both values of sourceType and source are necessary");
        }

        OpencvObject opencvObject = OpencvObject.getInstance();
        OpencvCascadeClassifier opencvCascadeClassifier = OpencvCascadeClassifier.getInstance();

        switch (sourceType) {
            case "file":
                opencvObject.setMat(Imgcodecs.imread(source.getPath()));
                LOG.debug("opencvObject.getMat() = {}", opencvObject.getMat());

                for (FSFile resource : resourceFiles) {
                    LOG.debug("resource = {}", resource);
                    if (!opencvCascadeClassifier.setResource(resource.getAbsolutePath())) {
                        break;
                    }

                    opencvCascadeClassifier.detectMultiScale(opencvObject.getMat(), opencvObject.getMatOfRect());
                    LOG.info("Recognized {} faces", opencvObject.getMatOfRect().toArray().length);
                    LOG.info("Used resource: {}", resource);

                    if (opencvObject.getMatOfRect() != null && opencvObject.getMatOfRect().toArray().length > 0) {
                        break;
                    }
                }
                if (opencvObject.getMatOfRect() != null && opencvObject.getMatOfRect().toArray().length > 0) {
                    for (Rect rect : opencvObject.getMatOfRect().toArray()) {
                        Imgproc.rectangle(opencvObject.getMat(), new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                    }
                    LOG.debug("Image {}", source);

                    LOG.debug("destinationType = {}", destinationType);
                    switch (destinationType) {
                        case "file":
                            LOG.debug("Write to {}", destination.getPath() + File.separator + source.getName());
                            Imgcodecs.imwrite(destination.getPath() + File.separator + source.getName(), opencvObject.getMat());
                            break;
                        case "screen":
                            Imgcodecs.imencode("." + source.getExtension(), opencvObject.getMat(), opencvObject.getMatOfByte());
                            ImageIcon imageIcon = new ImageIcon(opencvObject.getMatOfByte().toArray());
                            LOG.debug("Image prepared");

                            JFrame frame = new JFrame("Image");
                            JLabel label = new JLabel();
                            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            label.setIcon(imageIcon);
                            frame.getContentPane().add(label);
                            frame.pack();
                            frame.setVisible(true);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + destinationType);
                    }
                } else {
                    LOG.debug(source + " is not recognized");
                    return false;
                }
                timing.setEnd();
                LOG.info("Time of recognition: {}", timing.getBetween());
                break;
            case "video":
                break;
            case "camera":
                break;
            default:
                throw new IllegalStateException("Unexpected value of sourceType: " + sourceType);
        }

        LOG.debug("isRecognized End");
        return true;
    }
}
