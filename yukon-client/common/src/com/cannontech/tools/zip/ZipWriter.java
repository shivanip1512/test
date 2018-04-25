package com.cannontech.tools.zip;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.FileUtil;

public class ZipWriter {
    private ZipOutputStream out;
    private File zipFile;
    public static final String ZIPROOTDIR = "files/";
    private static final Logger log = YukonLogManager.getLogger(ZipWriter.class);

    public File getFile() {
        return zipFile;
    }

    public ZipWriter(File zipFile) {
        out = null;
        this.zipFile = zipFile;
        try {
            OutputStream rawOut = new BufferedOutputStream(new FileOutputStream(zipFile));

            out = new ZipOutputStream(rawOut);
        } catch (FileNotFoundException e) {
            log.error("Error while trying to set up the ZipWriter", e);
        }
    }

    public Writer getBufferedWriter(final String zipDir, final String filename) {
        Writer pw = new BufferedWriter(new OutputStreamWriter(out));
        String zipEntryStr = ZIPROOTDIR + zipDir + "/" + filename;
        try {
            out.putNextEntry(new ZipEntry(zipEntryStr));
        } catch (IOException e) {
            log.warn("Error while trying to put ZipEntry in zipOutputStream", e);
            return null;
        }
        return pw;
    }

    public void writeRawInputStream(InputStream inputStream, String zipDir) {
        writeRawInputStream(inputStream, new ZipEntry(zipDir));
    }

    private void writeRawInputStream(InputStream inputStream, ZipEntry zipEntry) {
        try {
            out.putNextEntry(zipEntry);
            FileUtil.copyNoFlush(inputStream, out);
        } catch (IOException e) {
            log.error("Error while writing from a raw intput stream", e);
        }
    }

    public void writeDir(File directory, String zipDir) {
        if (directory.isDirectory() && directory.exists()) {
            if (zipDir != null && !zipDir.isEmpty()
                    && zipDir.charAt(zipDir.length() - 1) != '/')
                zipDir += "/";

            File[] filesAndDirs = directory.listFiles();

            for (File file : filesAndDirs) {
                if (file.isFile()) {
                    writeFile(file, zipDir);
                }
            }
        } else {
            log.debug("Trying to add " + directory.getName()
                          + ". It either does not exist or is not a directory. Directory Skipped.");
        }
    }

    public void writeDirAndSub(File directory, String zipDir) {
        if (zipDir != null && !zipDir.isEmpty()
                && zipDir.charAt(zipDir.length() - 1) != '/')
            zipDir += "/";

        File[] filesAndDirs = directory.listFiles();

        for (File file : filesAndDirs) {
            if (file.isDirectory())
                writeDirAndSub(file, zipDir + file.getName());
            else {
                writeFile(file, zipDir);
            }
        }
    }

    public void writeFile(File file, String zipDir) {
        if (file.isFile() && file.exists()) {
            if (!zipDir.isEmpty() && zipDir.charAt(zipDir.length() - 1) != '/')
                zipDir += "/";
            String zipFilename = ZIPROOTDIR + zipDir + file.getName();
            try {
                InputStream rawIn = new FileInputStream(file);
                ZipEntry zipEntry = new ZipEntry(zipFilename);
                zipEntry.setTime(file.lastModified());
                writeRawInputStream(rawIn, zipEntry);
            } catch (IOException e) {
                log.error("Error while trying to create the zip bundle. Deleting invalid zip file",
                          e);
                zipFile.delete();
            }
        } else {
            log.debug("Trying to add " + file.getName()
                          + ". It either does not exist or is not a file. File Skipped.");
        }
    }

    public void close() {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException ioe) {
            zipFile.delete();
            ioe.printStackTrace();
        }
    }

}