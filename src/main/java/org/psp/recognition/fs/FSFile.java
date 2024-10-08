package org.psp.recognition.fs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class FSFile extends FSDirectory {
    private final static Logger LOG = LoggerFactory.getLogger(FSFile.class.getName());

    public FSFile(String pathname) {
        super(pathname, null, false, false);
    }
    public FSFile(String pathname, boolean isToDelete) {
        super(pathname, null, false, isToDelete);
    }
    public boolean run() {
        LOG.debug("{} running", this.getPath());

        String extension = getExtension();
        if (this.isFile()) LOG.debug("extension = {}", extension);
        if (extension != null && !extension.isEmpty()) {
            switch (extension.toLowerCase()) {
                case "zip":
                    FSZipFile fsZipFile = new FSZipFile(this.getPath());
                    if (!fsZipFile.run()) {
                        throw new IllegalStateException("Can not process file " + this.getPath());
                    }
                    break;
                case "yolo":
                case "xml":
                    FSResourceFile resourceFile = new FSResourceFile(this.getPath());
                    if (!resourceFile.run()) {
                        throw new IllegalStateException("Can not process file " + this.getPath());
                    }
                    break;
                case "jpg":
                case "jpeg":
                case "png":
                    FSImageFile fsImageFile = new FSImageFile(this.getPath());
                    if (!fsImageFile.run()) {
                        LOG.debug("Can not process file " + this.getPath());
                    }
                    break;
                case "ini":
                    FSPropertiesFile fsPropertiesFile = new FSPropertiesFile(this.getPath());
                    if (!fsPropertiesFile.run()) {
                        throw new IllegalStateException("Can not process file " + this.getPath());
                    }
                    break;
            }
        }
        return true;
    }

    protected String getExtension() {
        return FilenameUtils.getExtension(this.getName());
    }

    protected static String getFileSeparator() {
        return File.separator;
    }

    public ArrayList<String> getLines() {
        ArrayList<String> lines = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(this);
            while (scanner.hasNextLine())
                lines.add(scanner.nextLine());
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Can not read file " + this.getPath());
        }
        return lines;
    }
}
