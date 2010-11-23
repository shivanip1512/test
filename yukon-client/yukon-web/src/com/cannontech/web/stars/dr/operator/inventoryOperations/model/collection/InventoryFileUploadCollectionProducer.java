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
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
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
import com.cannontech.web.common.collection.CollectionProducer;
import com.google.common.collect.Maps;

public class InventoryFileUploadCollectionProducer implements CollectionProducer<InventoryCollectionType, InventoryCollection> {
    
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
    public InventoryCollection createCollection(HttpServletRequest request) throws ServletRequestBindingException {

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

    private InventoryCollection handleInitialRequest(HttpServletRequest request, MultipartFile dataFile) throws ServletRequestBindingException, IOException, FileNotFoundException {
        int energyCompanyId = ServletRequestUtils.getRequiredIntParameter(request, "energyCompanyId");
        InventoryCollection collection;
        
        InputStream inputStream = dataFile.getInputStream();
        
        CloseableIterator<YukonInventory> inventoryIterator = getInventoryIterator(inputStream, energyCompanyId);
        
        collection = memoryCollectionProducer.createCollection(inventoryIterator, dataFile.getOriginalFilename());
        
        CtiUtilities.close(inventoryIterator);

        return collection;
    }

    private CloseableIterator<YukonInventory> getInventoryIterator(InputStream inputStream, final int energyCompanyId) throws IOException {
        // Create an iterator to iterate through the file line by line
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        CSVReader csvReader = new CSVReader(inputStreamReader);
        
        String[] columnHeaders = csvReader.readNext();
        
        CsvReaderIterator iterator = new CsvReaderIterator(csvReader);
        
        final Map<ColumnHeader, Integer> columnHeaderIndexMap = Maps.newEnumMap(ColumnHeader.class);
        
        for(int i = 0; i < columnHeaders.length; i++) {
            ColumnHeader columnHeader = ColumnHeader.valueOf(columnHeaders[i]);
            columnHeaderIndexMap.put(columnHeader, i);
        }
        
        ObjectMapper<String[], YukonInventory> yukonInventoryMapper = new ObjectMapper<String[], YukonInventory>() {
            
            @Override
            public InventoryIdentifier map(String[] from) throws ObjectMappingException {
                
                String serialNumber = from[columnHeaderIndexMap.get(ColumnHeader.SERIAL_NUMBER)];
                String deviceType = from[columnHeaderIndexMap.get(ColumnHeader.DEVICE_TYPE)];
                String accountNumber = from[columnHeaderIndexMap.get(ColumnHeader.ACCOUNT_NUMBER)];
                
                
                YukonInventory yukonInventory = inventoryDao.getYukonInventory(serialNumber, energyCompanyId);
                //TODO Throw some errors if device type and account don't match;
                
                return yukonInventory.getInventoryIdentifier();
            }
        };
            
        Iterator<YukonInventory> inventoryIterator = new MappingIterator<String[], YukonInventory>(iterator, yukonInventoryMapper);
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