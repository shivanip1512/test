package com.cannontech.web.stars.dr.operator.inventory.model.collection;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.bulk.collection.inventory.InventoryCollectionType;
import com.cannontech.common.bulk.iterator.CloseableIterator;
import com.cannontech.common.bulk.iterator.CloseableIteratorWrapper;
import com.cannontech.common.bulk.iterator.CsvReaderIterator;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.exception.FileImportException;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.FileUploadUtils;
import com.cannontech.common.util.MappingIterator;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.web.common.collection.CollectionCreationException;
import com.cannontech.web.common.collection.CollectionProducer;
import com.google.common.collect.Maps;
import com.opencsv.CSVReader;

public class FileUploadCollectionProducer implements CollectionProducer<InventoryCollectionType, InventoryCollection> {
    
    @Autowired private InventoryDao inventoryDao;
    @Autowired private MemoryCollectionProducer collectionProducer;
    
    private Logger logger = YukonLogManager.getLogger(FileUploadCollectionProducer.class);
    
    private static enum ColumnHeader {
        SERIAL_NUMBER,
        DEVICE_TYPE,
        ACCOUNT_NUMBER
    };
    
    @Override
    public InventoryCollectionType getSupportedType() {
        return InventoryCollectionType.fileUpload;
    }
    
    @Override
    public InventoryCollection createCollection(HttpServletRequest req) throws CollectionCreationException {
        
        try {
            
            boolean isMultipart = ServletFileUpload.isMultipartContent(req);
            
            if (!isMultipart) {
                throw new CollectionCreationException("Not multipart");
            }
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) req;
            
            MultipartFile dataFile = mRequest.getFile(getSupportedType().getParameterName("dataFile"));
            FileUploadUtils.validateDataUploadFileType(dataFile);
            String ecIdParam = getSupportedType().getParameterName("energyCompanyId");
            int ecId = ServletRequestUtils.getRequiredIntParameter(req, ecIdParam);
            
            InputStream inputStream = dataFile.getInputStream();
            
            CloseableIterator<InventoryIdentifier> iter = getInventoryIterator(inputStream, ecId);
            InventoryCollection collection = collectionProducer.createCollection(iter, dataFile.getOriginalFilename());
            CtiUtilities.close(iter);
            
            return collection;
            
        } catch (IOException | ServletRequestBindingException e) {
            throw new CollectionCreationException("Unable to create collection.", e);
        } catch (FileImportException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        
    }
    
    private CloseableIterator<InventoryIdentifier> getInventoryIterator(InputStream inputStream, final int ecId) 
    throws IOException {
        
        /* Create an iterator to iterate through the file line by line */
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        CSVReader csvReader = new CSVReader(inputStreamReader);
        
        String[] columnHeaders = csvReader.readNext();
        
        CsvReaderIterator iterator = new CsvReaderIterator(csvReader);
        
        final Map<ColumnHeader, Integer> columnHeaderIndexMap = Maps.newEnumMap(ColumnHeader.class);
        
        for (int i = 0; i < columnHeaders.length; i++) {
            try {
                ColumnHeader columnHeader = ColumnHeader.valueOf(columnHeaders[i]);
                columnHeaderIndexMap.put(columnHeader, i);
            } catch (IllegalArgumentException e) {
                throw new CollectionCreationException("invalidColumnHeaders", e);
            }
        }
        
        ObjectMapper<String[], InventoryIdentifier> inventoryMapper = new ObjectMapper<String[], InventoryIdentifier>() {
            
            @Override
            public InventoryIdentifier map(String[] from) throws ObjectMappingException {
                
                String serialNumber = from[columnHeaderIndexMap.get(ColumnHeader.SERIAL_NUMBER)];
                String deviceType = from[columnHeaderIndexMap.get(ColumnHeader.DEVICE_TYPE)];
                String accountNumber = from[columnHeaderIndexMap.get(ColumnHeader.ACCOUNT_NUMBER)];
                
                try {
                    YukonInventory yukonInventory = inventoryDao.getYukonInventory(serialNumber, ecId);
                    
                    boolean accountNumberOK = inventoryDao.checkAccountNumber(yukonInventory.getInventoryIdentifier().getInventoryId(), accountNumber);
                    boolean deviceTypeOK = inventoryDao.checkdeviceType(yukonInventory.getInventoryIdentifier().getInventoryId(), deviceType);
                    
                    if (!(accountNumberOK && deviceTypeOK)) {
                        logger.error(ColumnHeader.ACCOUNT_NUMBER + " or " + ColumnHeader.DEVICE_TYPE + " not found in system. Values " + ArrayUtils.toString(from));
                        throw new CollectionCreationException("invalidInventoryData");
                    }
                    
                    return yukonInventory.getInventoryIdentifier();
                } catch (DataAccessException e) {
                    logger.error(ColumnHeader.SERIAL_NUMBER + " not found in system. Values " + ArrayUtils.toString(from));
                    throw new CollectionCreationException("invalidInventoryData", e);
                }
            }
        };
            
        Iterator<InventoryIdentifier> inventoryIterator = 
                new MappingIterator<String[], InventoryIdentifier>(iterator, inventoryMapper);
        return CloseableIteratorWrapper.getCloseableIterator(inventoryIterator);
    }
    
}