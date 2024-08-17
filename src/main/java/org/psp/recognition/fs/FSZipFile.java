package org.psp.recognition.fs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

//        if (!fsDirectory.mkdirs()) {
//            throw new IllegalStateException("Can not create directory " + this.getName());
//        }
//        LOG.debug("Directory {} was created", fsDirectory.getPath());

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
        return true;
    }
}
