package com.cannontech.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
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
        } else {
            validateEmptyFile(file.getInputStream());
        }
    }
    
    private static void validateEmptyFile(InputStream inputStream) throws EmptyImportFileException, IOException {
        if (inputStream.available() <= 0) {
            throw new EmptyImportFileException("yukon.web.import.error.emptyFile");
        } else {
            BOMInputStream bomInputStream = new BOMInputStream(inputStream, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE,
                ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE);
            InputStreamReader inputStreamReader = new InputStreamReader(bomInputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String x = reader.readLine();
            reader.close();
            if (StringUtils.isBlank(x)) {
                throw new EmptyImportFileException("yukon.web.import.error.emptyFile");
            }
        }
    }

    /**
     * Validate file type and throw exception if it is not valid CSV file
     * 
     * Here ; is used as a separator so when we use ; as a separator with parser,
     * And when reader read that file it reads whole line of CSV file in single read and it will be stored as a 
     * single entry in string array while reading.
     * 
     * @throws ImportFileFormatException
     * @throws IOException
     */
    private static void validateUploadFileType(File file) throws ImportFileFormatException, IOException {
        try (Reader reader = Files.newBufferedReader(Paths.get(file.getAbsolutePath()));
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
             //csvReader will skip first line while reading CSV file and it will return null if we try to read file other than text or CSV file.
             Reader fileReader = new FileReader(file.getAbsoluteFile());) {
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader csvReaderWithDelimeter = new CSVReaderBuilder(fileReader).withCSVParser(parser).build();
            String[] csvData = csvReaderWithDelimeter.readNext();
            String[] csvRecord = csvReader.readNext();
            if (csvData != null && csvRecord == null) {
                if (csvReaderWithDelimeter.readNext() != null)
                    throw new ImportFileFormatException("yukon.common.validDataFileRequired.error");
            }
        }
    }

    /**
     * Validate file type and empty file return true if it is valid CSV non empty file 
     */
    public static boolean validateCsvFileContentType(Path path) {

        try {
            if (StringUtils.isBlank(path.toFile().getName()) || !Files.isRegularFile(path)) {
                return false;
            }
            validateUploadFileType(path.toFile());
            validateEmptyFile(new FileInputStream(path.toFile()));
        } catch (ImportFileFormatException e) {
            log.error("Import file must be text or CSV " + path.toFile().getName());
            return false;
        } catch (EmptyImportFileException e) {
            log.warn("File " + path.toFile().getName() + " is empty");
            return false;
        } catch (IOException e) {
            // do nothing
        }
        return true;
    }
}
