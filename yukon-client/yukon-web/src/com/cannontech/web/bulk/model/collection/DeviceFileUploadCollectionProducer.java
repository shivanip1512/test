package com.cannontech.web.bulk.model.collection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.iterator.CloseableIterator;
import com.cannontech.common.bulk.iterator.CloseableIteratorWrapper;
import com.cannontech.common.bulk.iterator.CsvColumnReaderIterator;
import com.cannontech.common.bulk.mapper.ObjectMapperFactory;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.mapper.ObjectMapperFactory.FileMapperEnum;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.MappingIterator;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.tools.csv.CSVReader;

public class DeviceFileUploadCollectionProducer implements DeviceCollectionProducer {
    private ObjectMapperFactory objectMapperFactory = null;
    private DeviceGroupCollectionHelper deviceGroupCollectionProducer = null;

    @Autowired
    public void setDeviceGroupCollectionProducer(DeviceGroupCollectionHelper deviceGroupCollectionProducer) {
        this.deviceGroupCollectionProducer = deviceGroupCollectionProducer;
    }
    
    public DeviceCollectionType getSupportedType() {
        return DeviceCollectionType.fileUpload;
    }

    public DeviceCollection createDeviceCollection(HttpServletRequest request)
    throws ServletRequestBindingException {

        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);

            if (!isMultipart) {
                throw new RuntimeException("Not multipart");
            }
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;

            MultipartFile dataFile = mRequest.getFile(getSupportedType().getParameterName("dataFile"));
            if (dataFile == null || StringUtils.isBlank(dataFile.getOriginalFilename())) {
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
        final String uploadTypeStr = ServletRequestUtils.getStringParameter(request, getSupportedType().getParameterName("uploadType"));
        ObjectMapperFactory.FileMapperEnum uploadType = ObjectMapperFactory.FileMapperEnum.valueOf(uploadTypeStr);

        InputStream inputStream = dataFile.getInputStream();
        
        CloseableIterator<SimpleDevice> deviceIterator = getDeviceIterator(inputStream, uploadType);
        
        DeviceCollection groupCollection;
        try {
            groupCollection = deviceGroupCollectionProducer.createDeviceGroupCollection(deviceIterator, originalFilename);
        } catch (ObjectMappingException e) {
        	throw new DeviceCollectionCreationException(e.getMessage()); 
        } finally {
            CtiUtilities.close(deviceIterator);
        }

        return groupCollection;
    }

    private CloseableIterator<SimpleDevice> getDeviceIterator(InputStream inputStream,
                                                    final FileMapperEnum uploadType) throws IOException {
        // Create an iterator to iterate through the file line by line
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        CSVReader csvReader = new CSVReader(inputStreamReader);
        CsvColumnReaderIterator iterator = new CsvColumnReaderIterator(csvReader, 0);
        
        if (uploadType.isHasHeader()) {
            iterator.next();
        }
        
        ObjectMapper<String, SimpleDevice> yukonDeviceMapper =
            objectMapperFactory.getFileImportMapper(uploadType);
        Iterator<SimpleDevice> deviceIterator =
            new MappingIterator<String, SimpleDevice>(iterator, yukonDeviceMapper);
        return CloseableIteratorWrapper.getCloseableIterator(deviceIterator);
    }
    
    @Autowired
    public void setObjectMapperFactory(ObjectMapperFactory objectMapperFactory) {
        this.objectMapperFactory = objectMapperFactory;
    }
}



