package org.psp.recognition.fs;

import org.psp.recognition.RecognitionTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class FSFileNameFilter implements FilenameFilter {
    private final static Logger LOG = LoggerFactory.getLogger(FSFileNameFilter.class.getName());
    private ArrayList<String> namePatterns;

    FSFileNameFilter(ArrayList<String> namePatterns) {
        LOG.debug("namePatterns = {}", namePatterns);
        this.namePatterns = namePatterns;
    }
    @Override
    public boolean accept(File dir, String name) {
        LOG.debug("dir = {}, name = {}", dir, name);
        boolean accepted = false;

        FSFile fsFile = new FSFile(dir + FSFile.getFileSeparator() + name);
        if (fsFile.isDirectory() || namePatterns == null  || namePatterns == null) {
            LOG.debug("fsFile.isDirectory()");
            accepted = true;
        } else {
            for (String namePattern : namePatterns) {
                LOG.debug("namePattern = {}", namePattern);
                if (RecognitionTools.isMatched(name, namePattern, false)) {
                    accepted = true;
                    break;
                }
            }
        }
        LOG.debug("accepted = {}", accepted);
        return accepted;
    }
}
