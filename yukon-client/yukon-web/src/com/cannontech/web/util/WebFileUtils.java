package com.cannontech.web.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.web.multipart.MultipartFile;

import com.cannontech.tools.csv.CSVReader;

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
}
