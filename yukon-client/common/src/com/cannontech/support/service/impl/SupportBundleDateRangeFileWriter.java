package com.cannontech.support.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.tools.zip.ZipWriter;

public class SupportBundleDateRangeFileWriter extends AbstractSupportBundleWriter {
    private static final Logger log =
        YukonLogManager.getLogger(SupportBundleDateRangeFileWriter.class);
    private String zipDirectory;
    private String dirName;

    @Override
    public void addToZip(ZipWriter zipWriter, ReadableInstant start, ReadableInstant stop) {
        File dir = new File(CtiUtilities.getYukonBase() + dirName);

        if (dir.isDirectory()) {
            List<File> logFiles = listFilesByDateModified(dir, start, stop);
            for (File file : logFiles) {
                zipWriter.writeFile(file, zipDirectory);
            }
        } else {
            log.warn("Cannot add files in directory '" + dir.getAbsoluteFile() 
                     + "' because it is not a valid directory.");
        }
    }

    private List<File> listFilesByDateModified(File directory, ReadableInstant start,
                                               ReadableInstant stop) {
        File[] files = directory.listFiles();
        List<File> fileList = new ArrayList<File>();

        for (File file : files) {
            File candidate = new File(directory, file.getName());
            if (isFileInDateRange(candidate,start,stop)) {
                fileList.add(candidate);
            }
        }

        return fileList;
    }
    
    protected boolean isFileInDateRange(File candidate, ReadableInstant start,
            ReadableInstant stop)  {
        Instant modified = new Instant(candidate.lastModified());
    	return (candidate.isFile()
                && modified.isAfter(start)
                && modified.isBefore(stop));
    }

    public void setZipDirectory(String zipDirectory) {
        this.zipDirectory = zipDirectory;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }
}
