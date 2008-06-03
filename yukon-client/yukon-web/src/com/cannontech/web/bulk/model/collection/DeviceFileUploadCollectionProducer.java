package com.cannontech.web.bulk.model.collection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.iterator.CloseableIterator;
import com.cannontech.common.bulk.iterator.CloseableIteratorWrapper;
import com.cannontech.common.bulk.iterator.InputStreamIterator;
import com.cannontech.common.bulk.mapper.ObjectMapperFactory;
import com.cannontech.common.bulk.mapper.ObjectMapperFactory.FileMapperEnum;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.util.YukonDeviceToIdMapper;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.MappingIterator;
import com.cannontech.common.util.ObjectMapper;

public class DeviceFileUploadCollectionProducer extends DeviceCollectionProducerBase {
    private ObjectMapperFactory objectMapperFactory = null;
    private DeviceGroupCollectionHelper deviceGroupCollectionProducer = null;

    @Autowired
    public void setDeviceGroupCollectionProducer(DeviceGroupCollectionHelper deviceGroupCollectionProducer) {
        this.deviceGroupCollectionProducer = deviceGroupCollectionProducer;
    }
    
    public String getSupportedType() {
        return "fileUpload";
    }

    public DeviceCollection createDeviceCollection(HttpServletRequest request)
    throws ServletRequestBindingException {

        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);

            if (!isMultipart) {
                throw new RuntimeException("Not multipart");
            }
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;

            MultipartFile dataFile = mRequest.getFile(getParameterName("dataFile"));
            if (dataFile == null) {
                throw new RuntimeException("dataFile is null");
            }
            return handleInitialRequest(request, dataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private DeviceCollection handleInitialRequest(HttpServletRequest request, MultipartFile dataFile)
        throws ServletRequestBindingException, IOException, FileNotFoundException {
        final String originalFilename = dataFile.getOriginalFilename();
        final String uploadTypeStr = ServletRequestUtils.getStringParameter(request, getParameterName("uploadType"));
        ObjectMapperFactory.FileMapperEnum uploadType = ObjectMapperFactory.FileMapperEnum.valueOf(uploadTypeStr);

        InputStream inputStream = dataFile.getInputStream();
        
        CloseableIterator<YukonDevice> deviceIterator =
            getDeviceIterator(inputStream, uploadType);
        
        DeviceCollection groupCollection;
        try {
            MappingIterator<YukonDevice, Integer> deviceIds =
                new MappingIterator<YukonDevice, Integer>(deviceIterator,
                                                          new YukonDeviceToIdMapper());
            groupCollection = deviceGroupCollectionProducer.createDeviceGroupCollection(deviceIds);
        } finally {
            CtiUtilities.close(deviceIterator);
        }

        return groupCollection;
    }

    private CloseableIterator<YukonDevice> getDeviceIterator(InputStream inputStream,
                                                    final FileMapperEnum uploadType) throws IOException {
        // Create an iterator to iterate through the file line by line
        CloseableIterator<String> iterator = new InputStreamIterator(inputStream);
        if (uploadType.isHasHeader()) {
            iterator.next();
        }
        
        ObjectMapper<String, YukonDevice> yukonDeviceMapper =
            objectMapperFactory.getFileImportMapper(uploadType);
        Iterator<YukonDevice> deviceIterator =
            new MappingIterator<String, YukonDevice>(iterator, yukonDeviceMapper);
        return CloseableIteratorWrapper.getCloseableIterator(deviceIterator);
    }
    
    @Autowired
    public void setObjectMapperFactory(ObjectMapperFactory objectMapperFactory) {
        this.objectMapperFactory = objectMapperFactory;
    }
}



