package org.psp.recognition.fs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;

public class FSFileNameFilter implements FilenameFilter {
    private final static Logger LOG = LoggerFactory.getLogger(FSFileNameFilter.class.getName());
    private String nameMask;
    private String extensionMask;

    FSFileNameFilter(String nameMask, String extensionMask) {
        LOG.debug("nameMask = {}, extensionMask = {}", nameMask, extensionMask);
        this.nameMask = nameMask;
        this.extensionMask = extensionMask;
    }
    @Override
    public boolean accept(File dir, String name) {
//        LOG.debug("dir = {}, name = {}", dir, name);
        boolean accepted = true;

        FSFile fsFile = new FSFile(dir + FSFile.getFileSeparator() + name);
        if (!fsFile.isDirectory()) {
            if (nameMask != null && !name.contains(nameMask))
                accepted = false;
            if (extensionMask != null && !name.endsWith("." + extensionMask))
                accepted = false;
        }
//        LOG.debug("accepted = {}", accepted);
        return accepted;
    }
}
