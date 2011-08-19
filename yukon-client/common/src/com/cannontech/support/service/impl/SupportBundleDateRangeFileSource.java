package com.cannontech.support.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.tools.zip.ZipWriter;

public class SupportBundleDateRangeFileSource extends AbstractSupportBundleSource {
    private String zipDirectory;
    private String dirName;

    @Override
    public void addToZip(ZipWriter zipWriter, ReadableInstant start, ReadableInstant stop) {

        File dir = new File(CtiUtilities.getYukonBase() + dirName);
        List<File> logFiles = listFilesByDateModified(dir, start, stop);
        for (File file : logFiles)
            zipWriter.writeFile(file, zipDirectory);
    }

    private List<File> listFilesByDateModified(File directory, ReadableInstant start,
                                               ReadableInstant stop) {

        File[] files = directory.listFiles();
        List<File> fileList = new ArrayList<File>();

        for (File file : files) {
            File candidate = new File(directory, file.getName());
            Instant modified = new Instant(candidate.lastModified());
            if (candidate.isFile()
                        && modified.compareTo(start) >= 0
                        && modified.compareTo(stop) <= 0) {
                fileList.add(candidate);
            }
        }

        return fileList;
    }

    public void setZipDirectory(String zipDirectory) {
        this.zipDirectory = zipDirectory;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

}
