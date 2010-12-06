package com.cannontech.web.stars.dr.operator.inventoryOperations.model.collection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.common.bulk.collection.inventory.YukonCollection;
import com.cannontech.common.bulk.collection.inventory.InventoryCollectionType;
import com.cannontech.common.bulk.iterator.CloseableIterator;
import com.cannontech.common.bulk.iterator.CloseableIteratorWrapper;
import com.cannontech.common.bulk.iterator.CsvReaderIterator;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.MappingIterator;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.web.common.collection.CollectionCreationException;
import com.cannontech.web.common.collection.CollectionProducer;
import com.google.common.collect.Maps;

public class InventoryFileUploadCollectionProducer implements CollectionProducer<InventoryCollectionType, YukonCollection> {
    
    private InventoryDao inventoryDao;
    private MemoryCollectionProducer memoryCollectionProducer;
    
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
    public YukonCollection createCollection(HttpServletRequest request) throws ServletRequestBindingException {

        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);

            if (!isMultipart) {
                throw new RuntimeException("Not multipart");
            }
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;

            MultipartFile dataFile = mRequest.getFile(getSupportedType().getParameterName("dataFile"));
            if (dataFile == null || StringUtils.isBlank(dataFile.getOriginalFilename())) {
                throw new CollectionCreationException("noFile");
            }
            return handleInitialRequest(request, dataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private YukonCollection handleInitialRequest(HttpServletRequest request, MultipartFile dataFile) throws ServletRequestBindingException, IOException, FileNotFoundException {
        int energyCompanyId = ServletRequestUtils.getRequiredIntParameter(request, "energyCompanyId");
        YukonCollection collection;
        
        InputStream inputStream = dataFile.getInputStream();
        
        CloseableIterator<InventoryIdentifier> inventoryIterator = getInventoryIterator(inputStream, energyCompanyId);
        
        collection = memoryCollectionProducer.createCollection(inventoryIterator, dataFile.getOriginalFilename());
        
        CtiUtilities.close(inventoryIterator);

        return collection;
    }

    private CloseableIterator<InventoryIdentifier> getInventoryIterator(InputStream inputStream, final int energyCompanyId) throws IOException {
        /* Create an iterator to iterate through the file line by line */
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        CSVReader csvReader = new CSVReader(inputStreamReader);
        
        String[] columnHeaders = csvReader.readNext();
        
        CsvReaderIterator iterator = new CsvReaderIterator(csvReader);
        
        final Map<ColumnHeader, Integer> columnHeaderIndexMap = Maps.newEnumMap(ColumnHeader.class);
        
        for(int i = 0; i < columnHeaders.length; i++) {
            try {
                ColumnHeader columnHeader = ColumnHeader.valueOf(columnHeaders[i]);
                columnHeaderIndexMap.put(columnHeader, i);
            } catch (IllegalArgumentException e) {
                throw new CollectionCreationException("invalidColumnHeaders", e);
            }
        }
        
        ObjectMapper<String[], InventoryIdentifier> yukonInventoryMapper = new ObjectMapper<String[], InventoryIdentifier>() {
            
            @Override
            public InventoryIdentifier map(String[] from) throws ObjectMappingException {
                
                String serialNumber = from[columnHeaderIndexMap.get(ColumnHeader.SERIAL_NUMBER)];
                String deviceType = from[columnHeaderIndexMap.get(ColumnHeader.DEVICE_TYPE)];
                String accountNumber = from[columnHeaderIndexMap.get(ColumnHeader.ACCOUNT_NUMBER)];
                
                try {
                    YukonInventory yukonInventory = inventoryDao.getYukonInventory(serialNumber, energyCompanyId);
                    
                    boolean accountNumberOK = inventoryDao.checkAccountNumber(yukonInventory.getInventoryIdentifier().getInventoryId(), accountNumber);
                    boolean deviceTypeOK = inventoryDao.checkdeviceType(yukonInventory.getInventoryIdentifier().getInventoryId(), deviceType);
                    
                    if (!(accountNumberOK && deviceTypeOK)) {
                        throw new CollectionCreationException("invalidInventoryData");
                    }
                    
                    return yukonInventory.getInventoryIdentifier();
                } catch (DataAccessException e) {
                    throw new CollectionCreationException("invalidInventoryData", e);
                }
            }
        };
            
        Iterator<InventoryIdentifier> inventoryIterator = new MappingIterator<String[], InventoryIdentifier>(iterator, yukonInventoryMapper);
        return CloseableIteratorWrapper.getCloseableIterator(inventoryIterator);
    }

    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }
    
    @Autowired
    public void setMemoryCollectionProducer(MemoryCollectionProducer memoryCollectionProducer) {
        this.memoryCollectionProducer = memoryCollectionProducer;
    }
    
}