package com.cannontech.common.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cannontech.common.exception.EmptyImportFileException;
import com.cannontech.common.exception.FileImportException;
import com.cannontech.common.exception.ImportFileFormatException;
import com.cannontech.common.exception.NoImportFileException;

public class FileUploadUtils {
    public static void validateDataUploadFileType(MultipartFile file) throws IOException, FileImportException {
        validateFileUpload(file);
        if (!file.getContentType().startsWith("text") && !file.getContentType().endsWith("excel")) {
            throw new ImportFileFormatException("yukon.common.validDataFileRequired.error");
        }
    }

    public static void validateImageUploadFileType(MultipartFile file) throws IOException, FileImportException {
        validateFileUpload(file);
        if (!file.getContentType().startsWith("image") && !file.getContentType().endsWith("image")) {
            throw new ImportFileFormatException("yukon.common.validImageFileRequired.error");
        }
    }

    public static void validateKeyUploadFileType(MultipartFile file) throws IOException, FileImportException {
        validateFileUpload(file);
        if (!file.getContentType().startsWith("octet-stream") && !file.getContentType().endsWith("octet-stream")
            && !file.getContentType().endsWith("keys")) {
            throw new ImportFileFormatException("yukon.common.validKeyFileRequired.error");
        }
    }

    public static void validateCertUploadFileType(MultipartFile file) throws IOException, FileImportException {
        validateFileUpload(file);
        if (!file.getContentType().startsWith("octet-stream") && !file.getContentType().endsWith("octet-stream")
            && !file.getContentType().endsWith("cert")) {
            throw new ImportFileFormatException("yukon.common.validCertFileRequired.error");
        }
    }

    private static void validateFileUpload(MultipartFile file) throws FileImportException, IOException {
        if (file == null || StringUtils.isBlank(file.getOriginalFilename())) {
            throw new NoImportFileException("yukon.web.import.error.noImportFile");
        } else if (file.getInputStream().available() <= 0) {
            throw new EmptyImportFileException("yukon.web.import.error.emptyFile");
        }
    }
    
    /**
     * Validate file type and return true if it is valid CSV file
     */
    public static boolean validateCsvFileContentType(Path path) {

        try {
            String contentType = Files.probeContentType(path);
            if (contentType != null && (contentType.startsWith("text") || contentType.endsWith("excel"))) {
                return true;
            }
        } catch (IOException e) {
            // do nothing
        }
        return false;
    }
}
