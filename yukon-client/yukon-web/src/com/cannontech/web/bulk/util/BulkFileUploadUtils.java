package com.cannontech.web.bulk.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.tools.csv.CSVReader;

public class BulkFileUploadUtils {

    public static BulkFileUpload getBulkFileUpload(HttpServletRequest request) {
        
        BulkFileUpload bulkFileUpload = new BulkFileUpload();
        
        if(ServletFileUpload.isMultipartContent(request)) {

            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            MultipartFile dataFile = mRequest.getFile("dataFile");

            try {

                if (dataFile == null || StringUtils.isBlank(dataFile.getOriginalFilename())) {
                    bulkFileUpload.addError("yukon.common.device.bulk.fileUpload.error.noFile");
                } 
                else {
                    InputStream inputStream = dataFile.getInputStream();
                    if (inputStream.available() <= 0) {
                        bulkFileUpload.addError("yukon.common.device.bulk.fileUpload.error.emptyFile");
                    }
                }

                File file = BulkFileUploadUtils.convertToTempFile(dataFile, "bulkImport", "");
                bulkFileUpload.setFile(file);

            } catch (IOException e) {
                bulkFileUpload.addError("yukon.common.device.bulk.fileUpload.error.noFile");
            }
        }
        else {
            bulkFileUpload.addError("yukon.common.device.bulk.fileUpload.error.noFile");
        }
        
        return bulkFileUpload;
    }
    
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
