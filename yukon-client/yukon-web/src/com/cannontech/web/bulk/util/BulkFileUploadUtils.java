package com.cannontech.web.bulk.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.web.util.WebFileUtils;

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

                File file = WebFileUtils.convertToTempFile(dataFile, "bulkImport", "");
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
}
