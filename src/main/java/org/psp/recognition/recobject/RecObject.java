package org.psp.recognition.recobject;

import org.psp.tools.Timing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.ArrayList;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.psp.recognition.fs.FSDirectory;
import org.psp.recognition.fs.FSFile;
import org.psp.recognition.AppProperties;

public class RecObject {
    final static Logger LOG = LoggerFactory.getLogger(RecObject.class.getName());
    protected FSFile source;
    protected String sourceType;
    protected String destinationType;
    protected FSFile destination;
    protected boolean isOn = false;
    protected ArrayList<FSFile> resourceFiles = new ArrayList<>();
    protected boolean isObjectRecognized = false;
    protected Timing timing;
    protected Mat mat;
    protected MatOfRect matOfRect;
    protected MatOfByte matOfByte;

    protected String getDestinationType() {
        return destinationType;
    }

    public void setDestination(FSFile destination) {
        this.destination = destination;
        if (destination.exists()) {
            LOG.debug("Clearing destination directory {}", destination);
            destination.delete();
        }
        LOG.debug("Creating destination directory {}", destination);
        destination.mkdirs();
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

    public void setSource(FSFile source) {
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

        timing = new Timing();

        if (sourceType == null || source == null) {
            throw new IllegalStateException("Both values of sourceType and source are necessary");
        }

        mat = new Mat();
        matOfRect = new MatOfRect();
        matOfByte = new MatOfByte();

        preProcess();
        postProcess();

        if (mat != null) mat.release();
        if (matOfRect != null) matOfRect.release();
        if (matOfByte != null) matOfByte.release();

        LOG.debug("isRecognized End");
        return isObjectRecognized;
    }

    public boolean isOn() {
        return isOn;
    }

    protected void preProcess() {
        switch (sourceType) {
            case "file":
                preProcessFile();
                break;
            case "video":
                break;
            case "camera":
                break;
            default:
                throw new IllegalStateException("Unexpected value of sourceType: " + sourceType);
        }
    }

    protected void preProcessFile() {
        LOG.debug("Image {}", source);
        mat = Imgcodecs.imread(source.getPath());
        if (mat.dataAddr() == 0) {
            throw new IllegalStateException("Can not open file " + source.getPath());
        }
        LOG.debug("mat = {}", mat);
    }

    protected void postProcess() {
        if (isObjectRecognized) {
            LOG.debug("destinationType = {}", destinationType);
            switch (destinationType) {
                case "file":
                    LOG.debug("Write to {}", destination.getPath() + File.separator + source.getName());
                    Imgcodecs.imwrite(destination.getPath() + File.separator + source.getName(), mat);
                    break;
                case "screen":
                    ImageViewer imageViewer = new ImageViewer();
                    imageViewer.show(mat, source.getName());
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + destinationType);
            }
        } else {
            LOG.debug(source + " is not recognized");
        }
        timing.setEnd();
        LOG.info("Time of recognition: {}", timing.getBetween());
    }
    protected void postProcessFile() {}
}
