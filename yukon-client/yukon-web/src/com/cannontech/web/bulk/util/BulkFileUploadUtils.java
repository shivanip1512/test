package com.cannontech.web.bulk.util;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.common.exception.FileImportException;
import com.cannontech.common.util.FileUploadUtils;
import com.cannontech.web.util.WebFileUtils;

public class BulkFileUploadUtils {
    
    public static  BulkFileUpload getBulkFileUpload(HttpServletRequest request, String fileInputName) {
        BulkFileUpload bulkFileUpload = new BulkFileUpload();
        
        if(ServletFileUpload.isMultipartContent(request)) {

            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            MultipartFile dataFile = mRequest.getFile(StringUtils.defaultIfEmpty(fileInputName, "dataFile"));

            try {
                FileUploadUtils.validateTabularDataUploadFileType(dataFile);
                File file = WebFileUtils.convertToTempFile(dataFile, dataFile.getOriginalFilename(), "");
                bulkFileUpload.setName(dataFile.getOriginalFilename());
                bulkFileUpload.setFile(file);
            } catch (IOException e) {
                bulkFileUpload.addError("yukon.common.device.bulk.fileUpload.error.noFile");
            } catch (FileImportException e) {
                bulkFileUpload.addError(e.getMessage());
            }
        } else {
            bulkFileUpload.addError("yukon.common.device.bulk.fileUpload.error.noFile");
        }
        
        return bulkFileUpload;
    }

    public static BulkFileUpload getBulkFileUpload(HttpServletRequest request) {
        return getBulkFileUpload(request, null);
    }
}