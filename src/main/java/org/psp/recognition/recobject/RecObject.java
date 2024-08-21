package org.psp.recognition.recobject;

import org.psp.recognition.gui.ImageViewer;
import org.psp.tools.Timing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

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
    protected ArrayList<FSFile> resourceFiles = new ArrayList<>();
    protected boolean isObjectRecognized = false;
    protected Timing timing;
    protected Mat mat;
    protected Mat matDestination;
    protected MatOfRect matOfRect;
    protected MatOfByte matOfByte;

    protected String getDestinationType() {
        return destinationType;
    }

    public void setDestination(FSFile destination) {
        this.destination = destination;
        if (destination.exists()) {
            destination.deleteFSObject();
        }
        LOG.debug("Creating destination directory {}", destination);
        if (!destination.mkdirs())
            throw new IllegalStateException("Can not create directory" + destination.getPath());

        FSFile fsFile = new FSFile(
                AppProperties.getInstance().getProperties().get("directory").get("work")
                        + File.separator + AppProperties.getInstance().getProperties().get("directory").get("processed")
        );
        if (!fsFile.exists()) {
            LOG.debug("Creating processed directory");
            if (!fsFile.mkdirs())
                throw new IllegalStateException("Can not create directory" + fsFile.getPath());
        }
    }

    protected void init(String objectName) {
        LOG.debug("objectName = {}", objectName);
        LOG.debug("destinationType = {}", AppProperties.getInstance().getProperties().get("destination").get(objectName + ".destinationType"));
        setDestinationType(AppProperties.getInstance().getProperties().get("destination").get(objectName + ".destinationType"));

        LOG.debug("destination = {}", AppProperties.getInstance().getProperties().get("destination").get(objectName + ".destination"));
        if (getDestinationType().equals("file")) {
            FSFile fsFile = new FSFile(
        AppProperties.getInstance().getProperties().get("directory").get("work")
                + File.separator + AppProperties.getInstance().getProperties().get("destination").get(objectName + ".destination")
            );
            setDestination(fsFile);
        }
    }

    protected void setResources(ArrayList<String> resourcePatterns) {
        LOG.debug("resourcePatterns = {}", resourcePatterns);

        String pathName = System.getProperty("user.dir")
                + File.separator + AppProperties.getInstance().getProperties().get("directory").get("resource");
        LOG.debug("pathName = {}", pathName);
        FSDirectory resourceDirectory = new FSDirectory(pathName, resourcePatterns, true, false);
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

    protected boolean isRecognized() {
        LOG.debug("isRecognized Start");
        LOG.debug("sourceType = {}", sourceType);
        LOG.debug("source = {}", source);

        timing = new Timing();

        if (sourceType == null || source == null) {
            throw new IllegalStateException("Both values of sourceType and source are necessary");
        }

        mat = new Mat();
        matDestination = new Mat();
        matOfRect = new MatOfRect();
        matOfByte = new MatOfByte();

        preProcess();
        if (isObjectRecognized)
            postProcess();

        if (mat != null) mat.release();
        if (matDestination != null) matDestination.release();
        if (matOfRect != null) matOfRect.release();
        if (matOfByte != null) matOfByte.release();

        LOG.debug("isRecognized End");
        return isObjectRecognized;
    }

    protected void preProcess() {
        switch (sourceType) {
            case "file":
                preProcessFile();
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
        mat = Imgcodecs.imread(source.getPath(), Imgcodecs.IMREAD_COLOR);
        LOG.debug("mat = {}", mat);
        if (mat.dataAddr() != 0) {
            isObjectRecognized = true;
        } else {
            isObjectRecognized = false;
            LOG.error("Can not open file {}", source.getPath());
        }
    }

    protected void postProcess() {
        if (isObjectRecognized) {
            LOG.debug("destinationType = {}", destinationType);
            switch (destinationType) {
                case "file":
                    postProcessFile();
                    LOG.info("Write to {}", destination.getPath() + File.separator + source.getName());
                    try {
                        Imgcodecs.imwrite(destination.getPath() + File.separator + source.getName(), mat);
                    } catch (Exception e) {
                        LOG.error(Arrays.toString(e.getStackTrace()));
                    }
                    break;
                case "screen":
                    ImageViewer imageViewer = new ImageViewer();
                    imageViewer.show(matDestination, source.getName());
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
