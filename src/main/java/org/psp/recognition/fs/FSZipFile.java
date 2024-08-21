package org.psp.recognition.fs;

import org.psp.recognition.AppProperties;
import org.psp.recognition.recobject.TestYoloV8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import net.lingala.zip4j.ZipFile;

public class FSZipFile extends FSFile {
    private final static Logger LOG = LoggerFactory.getLogger(FSZipFile.class.getName());

    public FSZipFile(String pathname) {
        super(pathname);
    }

    @Override
    public boolean run() {
        LOG.debug("{} unzipping", this.getPath());

        ZipFile zipFile = new ZipFile(this.getPath());
        try {
            zipFile.extractAll(zipFile.getFile().getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FSDirectory fsDirectory = new FSDirectory(this.getPath().replace(".zip", ""), null, true, true);
        LOG.debug("Directory {} was created", fsDirectory.getPath());
        if (!fsDirectory.iterate()) {
            throw new IllegalStateException("Can not iterate directory " + fsDirectory.getPath());
        }
        FSFile fsFile = new FSFile(
                AppProperties.getInstance().getProperties().get("directory").get("work")
                        + File.separator + AppProperties.getInstance().getProperties().get("directory").get("processed")
                        + File.separator + this.getName()
        );
        if (!this.renameTo(fsFile))
            throw new IllegalStateException("Can not move file " + this.getName() + " to " + fsFile.getPath());

        TestYoloV8 testYoloV8 = TestYoloV8.getInstance();
        if (!testYoloV8.getRecognisedObjects().isEmpty()) {
            FSFile recognisedFile = new FSFile(AppProperties.getInstance().getProperties().get("directory").get("work") + FSFile.separator + "recognized.txt");
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(recognisedFile,true);
                for (String object : testYoloV8.getRecognisedObjects()) {
                    fileWriter.write(object + "\r\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }
}
