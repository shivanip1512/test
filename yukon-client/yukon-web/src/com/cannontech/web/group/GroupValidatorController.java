package com.cannontech.web.group;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.exception.FileImportException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.FileUploadUtils;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
public class GroupValidatorController {

    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @RequestMapping(value="device-group", method=RequestMethod.POST)
    public void deviceGroup(HttpServletRequest request, HttpServletResponse response, YukonUserContext userContext)
            throws ServletException, IOException {
        Map<String, Object> collectionAttributes = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        try {
            if (!ServletFileUpload.isMultipartContent(request)) {
                throw new IllegalArgumentException("No file found");
            }
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            MultipartFile file = mRequest.getFile("fileUpload.dataFile");
            FileUploadUtils.validateTabularDataUploadFileType(file);
            DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
            collectionAttributes.putAll(deviceCollection.getCollectionParameters());
            collectionAttributes.put("deviceCount", Long.toString(deviceCollection.getDeviceCount(), 10));
            collectionAttributes.put("deviceErrorCount", deviceCollection.getErrorDevices().size());
            collectionAttributes.put("deviceErrors", deviceCollection.getErrorDevices());
        } catch (DeviceCollectionCreationException e) {
            collectionAttributes.put("error", e.getMessage());
        } catch (IllegalArgumentException | FileImportException e) {
            collectionAttributes.put("error", accessor.getMessage(e.getMessage()));
        }
        
        //Neccessary for IE9
        response.setContentType("text/plain");
        String jsonString = JsonUtils.toJson(collectionAttributes);
        response.getWriter().print(jsonString);
    }
    
    @RequestMapping("downloadResult")
    public String downloadResult(@RequestParam(value = "deviceErrors", required = false) Set<String> errors,
            @RequestParam(value = "uploadFileName", required = false) String errorFileName,
            @RequestParam(value = "header", required = false) String header,
            HttpServletResponse response)
            throws IOException {
        errorFileName = FilenameUtils.removeExtension(errorFileName) + "_errors.csv";
        buildCsv(errors, header, errorFileName, response);
        return null;
    }

    private void buildCsv(Set<String> errors, String header, String errorFileName, HttpServletResponse response)
            throws IOException {

        String[] headerRow = null;
        if (header != null) {
            headerRow = new String[] { header };
        }
        List<String[]> dataRows = Lists.newArrayList();

        for (String error : errors) {
            dataRows.add(new String[] { error });
        }
        // write out the file
        WebFileUtils.writeToCSV(response, headerRow, dataRows, errorFileName);
    }
}