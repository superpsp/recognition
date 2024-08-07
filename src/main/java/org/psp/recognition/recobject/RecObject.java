package org.psp.recognition.recobject;

import org.psp.recognition.AppProperties;
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

    protected void setResources(ArrayList<String> fresourcePatterns) {
        LOG.debug("fresourcePatterns = {}", fresourcePatterns);

        String pathName = System.getProperty("user.dir")
                + File.separator + AppProperties.properties.get("directory").get("resource");
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

        switch (sourceType) {
            case "file":
                JFrame frame = new JFrame("Image");
                JLabel label = new JLabel();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                LOG.debug("JFrame prepared");

                Mat tempMat = new Mat();

                Mat mat = Imgcodecs.imread(source.getPath());
                CascadeClassifier cascadeClassifier;
                MatOfRect matOfRect = null;
                for (FSFile resource : resourceFiles) {
                    LOG.debug("resource = {}", resource);
                    cascadeClassifier = new CascadeClassifier(resource.getAbsolutePath());
                    matOfRect = new MatOfRect();
                    cascadeClassifier.detectMultiScale(mat, matOfRect);
                    LOG.debug("Recognized {} faces", matOfRect.toArray().length);
                    if (matOfRect != null && matOfRect.toArray().length > 0) break;
                }
                if (matOfRect != null && matOfRect.toArray().length > 0) {
                    MatOfByte buf = new MatOfByte();
                    for (Rect rect : matOfRect.toArray()) {
                        Imgproc.rectangle(mat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                    }
                    Imgcodecs.imencode("." + source.getExtension(), mat, buf);
                    LOG.debug("Image prepared");

                    ImageIcon imageIcon = new ImageIcon(buf.toArray());
                    label.setIcon(imageIcon);
                    frame.getContentPane().add(label);
                    frame.pack();
                    frame.setVisible(true);
                } else {
                    LOG.debug(source + " is not recognized");
                    return false;
                }
                timing.setEnd();
                LOG.info(source + ", time of recognition: {}", timing.getBetween());
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
