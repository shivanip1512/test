package com.cannontech.support.service.impl;

import java.io.File;

import org.apache.log4j.Logger;
import org.joda.time.ReadableInstant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.tools.zip.ZipWriter;

public class SupportBundleFileWriter extends AbstractSupportBundleWriter {
    private String zipDirectory;
    private String fileOrDirName;

    private static final Logger log = YukonLogManager.getLogger(SupportBundleFileWriter.class);
    
    @Override
    public void addToZip(ZipWriter zipWriter, ReadableInstant start, ReadableInstant stop) {
        File fileOrDir = new File(CtiUtilities.getYukonBase() + fileOrDirName);

        if (fileOrDir.isDirectory()) {
            zipWriter.writeDir(fileOrDir, zipDirectory);
        } else if (fileOrDir.isFile()) {
            zipWriter.writeFile(fileOrDir, zipDirectory);
        } else {
            log.info("Not a valid file or directory - Could not be added to Support Bundle: '"
                     + fileOrDirName + "'");
        }
    }

    public void setZipDirectory(String zipDirectory) {
        this.zipDirectory = zipDirectory;
    }

    public void setFileOrDirName(String fileOrDirName) {
        this.fileOrDirName = fileOrDirName;
    }
}
