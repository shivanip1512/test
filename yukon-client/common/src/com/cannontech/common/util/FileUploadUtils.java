package com.cannontech.common.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.EmptyImportFileException;
import com.cannontech.common.exception.FileImportException;
import com.cannontech.common.exception.ImportFileFormatException;
import com.cannontech.common.exception.NoImportFileException;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class FileUploadUtils {

    private static final Logger log = YukonLogManager.getLogger(FileUploadUtils.class);

    public static void validateDataUploadFileType(MultipartFile file) throws IOException, FileImportException {
        validateFileUpload(file);
        File importFile = File.createTempFile(file.getName(), StringUtils.EMPTY);
        importFile.deleteOnExit();
        file.transferTo(importFile);
        validateUploadFileType(importFile);
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
     * Validate file type and throw exception if it is not valid CSV file
     * 
     * @throws ImportFileFormatException
     * @throws IOException
     */
    private static void validateUploadFileType(File file) throws ImportFileFormatException, IOException {
        try (Reader reader = Files.newBufferedReader(Paths.get(file.getAbsolutePath()));
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
             Reader fileReader = new FileReader(file.getAbsoluteFile());) {
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader csvReaderWithDelimeter = new CSVReaderBuilder(fileReader).withCSVParser(parser).build();
            String[] csvData = csvReaderWithDelimeter.readNext();
            String[] csvRecord = csvReader.readNext();
            if (file.length() > 2 && (csvData != null && csvRecord == null)) {
                if (csvReaderWithDelimeter.readNext() != null)
                    throw new ImportFileFormatException("yukon.common.validDataFileRequired.error");
            }
        }
    }

    /**
     * Validate file type and return true if it is valid CSV file
     */
    public static boolean validateCsvFileContentType(Path path) {

        try {
            validateUploadFileType(path.toFile());
        } catch (ImportFileFormatException e) {
            log.error("Import file must be text or CSV "+ path.toFile().getName());
            return false;
        } catch (IOException e) {
            // do nothing
        }
        return true;
    }
}
