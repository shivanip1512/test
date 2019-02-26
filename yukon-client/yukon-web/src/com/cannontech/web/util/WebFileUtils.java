package com.cannontech.web.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import com.cannontech.common.exception.EmptyImportFileException;
import com.cannontech.common.exception.FileImportException;
import com.cannontech.common.exception.ImportFileFormatException;
import com.cannontech.common.exception.NoImportFileException;
import com.cannontech.common.util.FileUploadUtils;
import com.cannontech.util.ServletUtil;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class WebFileUtils {

    private static final Pattern PATTERN_WINDOWS_FILENAME =
        Pattern.compile("[\\w- \\(\\)`~!@#\\$%\\^&=+\\[\\]\\{\\};,.']+");

    public static File convertToTempFile(MultipartFile multipartFile, String prefix, String suffix) throws IOException {
        File tempFile = File.createTempFile(prefix, suffix);
        tempFile.deleteOnExit();
        multipartFile.transferTo(tempFile);
        return tempFile;
    }

    /**
     * Takes a MultipartFile, converts it to a temp file, then returns a CSVReader
     * for that temp file.
     *
     * @throws NoImportFileException if the import file doesn't exist
     * @throws EmptyImportFileException if the import file contains 0 bytes of data
     * @throws IOException if reader instantiation fails
     * @throws ImportFileFormatException 
     */
    public static CSVReader getTempBackedCsvReaderFromMultipartFile(MultipartFile dataFile)
            throws FileImportException, IOException {

        File tempFile = new File("");
        try {
            FileUploadUtils.validateDataUploadFileType(dataFile);
            tempFile = convertToTempFile(dataFile, "bulkImport", "");
        } catch (FileImportException e) {
            throw new FileImportException(e.getMessage());
        } catch (IOException e) {
            throw new IOException(e);
        }

        FileSystemResource fileResource = new FileSystemResource(tempFile);
        BOMInputStream bomInputStream = new BOMInputStream(fileResource.getInputStream(), ByteOrderMark.UTF_8,
                ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE);
        InputStreamReader inputStreamReader = new InputStreamReader(bomInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        return new CSVReader(bufferedReader);
    }

    public static void writeToCSV(HttpServletResponse response, List<String> headerRow,
            final List<List<String>> dataRows, String fileName) throws IOException {
        writeToCSV(response, headerRow.toArray(new String[0]), fileName, new CSVDataWriter() {
            @Override
            public void writeData(CSVWriter csvWriter) {
                for (List<String> line : dataRows) {
                    csvWriter.writeNext(line.toArray(new String[0]));
                }
            }
        });
    }

    public static void writeToCSV(HttpServletResponse response, String[] headerRow, final List<String[]> dataRows,
            String fileName) throws IOException {
        writeToCSV(response, headerRow, fileName, new CSVDataWriter() {
            @Override
            public void writeData(CSVWriter csvWriter) {
                csvWriter.writeAll(dataRows);
            }
        });
    }

    public static void writeToCSV(HttpServletResponse response, String[] headerRow, String fileName,
            CSVDataWriter dataWriter) throws IOException {

        OutputStream outputStream = setupResponse(response, fileName);

        // write out the file
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        CSVWriter csvWriter = new CSVWriter(writer);

        if (headerRow != null) {
            csvWriter.writeNext(headerRow);
        }

        dataWriter.writeData(csvWriter);

        csvWriter.close();
    }

    /**
     * Writes and existing csv file to the reponse.
     */
    public static void writeToCSV(HttpServletResponse response, File file, String fileName) throws IOException {

        OutputStream outputStream = setupResponse(response, fileName);

        // write out the file
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        CSVWriter csvWriter = new CSVWriter(writer);
        CSVReader reader = new CSVReader(new FileReader(file));

        csvWriter.writeAll(reader.readAll());

        csvWriter.close();
    }

    private static OutputStream setupResponse(HttpServletResponse response, String fileName) throws IOException {

        response.setContentType("text/csv");
        response.setHeader("Content-Type", "application/force-download");
        fileName = ServletUtil.makeWindowsSafeFileName(fileName);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        return response.getOutputStream();
    }

    public interface CSVDataWriter {
        public void writeData(CSVWriter csvWriter);
    }

    /**
     * Validates filename, without extension, against Windows basic filename excluded character set: / \ : * ?
     * " < > |
     * Blank/empty or whitespace only file names are also invalid.
     * Filename cannot start with an empty string.
     */
    public static boolean isValidWindowsFilename(String filename) {
        if (filename.startsWith(" ")) {
            return false;
        }

        Matcher matcher = PATTERN_WINDOWS_FILENAME.matcher(StringUtils.trim(filename));
        return matcher.matches();
    }

}