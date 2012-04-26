package com.cannontech.support.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.joda.time.ReadableInstant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.tools.zip.ZipWriter;

public class SupportBundleFileSystemToCsvWriter extends AbstractSupportBundleWriter {
    private static final Logger log =
        YukonLogManager.getLogger(SupportBundleFileSystemToCsvWriter.class);

    private String zipDirectory;
    private String zipFilename;

    @Override
    public void addToZip(ZipWriter zipWriter, ReadableInstant start, ReadableInstant stop) {
        Writer pw = zipWriter.getBufferedWriter(zipDirectory, zipFilename);

        CSVWriter csvWriter = new CSVWriter(pw);

        String[] firstLine = { "File", "Size", "MD5" };
        csvWriter.writeNext(firstLine);

        File directory = new File(CtiUtilities.getYukonBase());
        writeDirToCsv(directory, "", csvWriter);

        try {
            pw.flush();
        } catch (IOException e) {
            log.warn("Error while trying to flush the print writer for the CSVWriter", e);
        }

    }

    private void writeDirToCsv(File directory, String path, CSVWriter csvWriter) {
        File[] filesAndDirs = directory.listFiles();

        // If java cannot access a directory, listFiles() returns null
        if (filesAndDirs != null) {
            for (File file : filesAndDirs) {
                if (file.isDirectory()) {
                    writeDirToCsv(file, path + file.getName() + "/", csvWriter);
                } else if (file.isFile()){
                    String[] nextLine =
                    { path + file.getName(), String.valueOf(file.length()), getMD5(file) };
                    csvWriter.writeNext(nextLine);
                }
            }
        } else {
            log.warn("Unable to access directory " + directory.getAbsolutePath() + ". It cannot be added to csv output.");
            String[] nextLine =
            { path, "0", "-Unable to include directory-" };
            csvWriter.writeNext(nextLine);
        }
    }

    private String getMD5(File file) {
        java.security.MessageDigest md5Hasher;
        try {
            md5Hasher = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            log.error("MD5 algorithm not supported", noSuchAlgorithmException);
            return "-Unable to hash-";
        }

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                md5Hasher.update(buffer, 0, read);
            }
        } catch (IOException ioException) {
            log.warn("Unable to hash file " + file.getAbsolutePath() + file.getName() + " The file might be used by another process.");
            return "-Unable to hash-";
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }

        byte[] digest = md5Hasher.digest();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public void setZipDirectory(String zipDirectory) {
        this.zipDirectory = zipDirectory;
    }

    public void setZipFilename(String zipFilename) {
        this.zipFilename = zipFilename;
    }
}
