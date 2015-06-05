package com.cannontech.web.group;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.util.JsonUtils;

@Controller
public class GroupValidatorController {

    @Autowired private DeviceCollectionFactory deviceCollectionFactory;

    @RequestMapping(value="device-group", method=RequestMethod.POST)
    public void deviceGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String,String> collectionAttributes = new HashMap<>();
        
        try {
            if (!ServletFileUpload.isMultipartContent(request)) {
                throw new IllegalArgumentException("No file found");
            }
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            MultipartFile file = mRequest.getFile("fileUpload.dataFile");
            if (file == null || StringUtils.isBlank(file.getOriginalFilename())) {
                throw new IllegalArgumentException("Blank file.");
            }
            if(!file.getContentType().startsWith("text") && !file.getContentType().endsWith("excel")) {
                throw new IllegalArgumentException("File must be text or csv");
            }
            DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
            collectionAttributes.putAll(deviceCollection.getCollectionParameters());
            collectionAttributes.put("deviceCount", Long.toString(deviceCollection.getDeviceCount(), 10));
        } catch (DeviceCollectionCreationException e) {
            collectionAttributes.put("error", e.getMessage());
        } catch (IllegalArgumentException e) {
            collectionAttributes.put("error", e.getMessage());
        }
        //Neccessary for IE9
        response.setContentType("text/plain");
        String jsonString = JsonUtils.toJson(collectionAttributes);
        response.getWriter().print(jsonString);
    }
}