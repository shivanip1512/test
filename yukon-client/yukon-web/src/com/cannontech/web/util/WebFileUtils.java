package com.cannontech.web.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import com.cannontech.tools.csv.CSVReader;
import com.cannontech.web.exceptions.EmptyImportFileException;
import com.cannontech.web.exceptions.NoImportFileException;

public class WebFileUtils {

	public static File convertToTempFile(MultipartFile multipartFile, String prefix, String suffix) throws IOException{
        
        File tempFile = File.createTempFile(prefix, suffix);
        tempFile.deleteOnExit();
        multipartFile.transferTo(tempFile);
        return tempFile;
    }
    
    public static CSVReader getCSVReaderFromMultipartFile(MultipartFile dataFile) throws IOException  {
        
        InputStreamReader inputStreamReader = new InputStreamReader(dataFile.getInputStream());
        BufferedReader reader = new BufferedReader(inputStreamReader);
        return new CSVReader(reader);
    }
    
    public static CSVReader getCSVReaderFromFilePath(String filePath) throws IOException  {
        
        FileReader fileReader = new FileReader(filePath);
        BufferedReader reader = new BufferedReader(fileReader);
        return new CSVReader(reader);
    }
    
    /**
     * Takes a MultipartFile, converts it to a temp file, then returns a CSVReader
     * for that temp file.
     * @throws NoImportFileException if the import file doesn't exist
     * @throws EmptyImportFileException if the import file contains 0 bytes of data
     * @throws IOException if reader instantiation fails
     */
    public static CSVReader getTempBackedCsvReaderFromMultipartFile(MultipartFile dataFile) 
        throws NoImportFileException, EmptyImportFileException, IOException {
        
        File tempFile = new File("");
        try {
            if (dataFile == null || StringUtils.isBlank(dataFile.getOriginalFilename())) {
                throw new NoImportFileException();
            } else if(dataFile.getInputStream().available() <= 0){
                throw new EmptyImportFileException();
            } else {
                tempFile = convertToTempFile(dataFile, "bulkImport", "");
            }
        } catch(IOException e) {
            throw new NoImportFileException(e);
        }
        
        FileSystemResource fileResource = new FileSystemResource(tempFile);
        InputStreamReader inputStreamReader = new InputStreamReader(fileResource.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        return new CSVReader(bufferedReader);
    }
}
