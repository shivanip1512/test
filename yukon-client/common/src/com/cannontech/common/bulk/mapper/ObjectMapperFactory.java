package com.cannontech.common.bulk.mapper;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Factory interface which generates ObjectMappers
 */
public interface ObjectMapperFactory {

    /**
     * Method to get an object mapper that maps a pao name into a yukon device
     * @return Pao name to yukon device mapper
     */
    public ObjectMapper<String, YukonDevice> createPaoNameToYukonDeviceMapper();

    /**
     * Method to get an object mapper that maps a meternumber into a yukon
     * device
     * @return Meternumber to yukon device mapper
     */
    public ObjectMapper<String, YukonDevice> createMeterNumberToYukonDeviceMapper();

    /**
     * Method to get an object mapper that maps an address into a yukon device
     * @return Address to yukon device mapper
     */
    public ObjectMapper<String, YukonDevice> createAddressToYukonDeviceMapper();

    /**
     * Method to get an object mapper that maps a LiteYukonPAObject into a yukon
     * device
     * @return LiteYukonPAObject to yukon device mapper
     */
    public ObjectMapper<LiteYukonPAObject, YukonDevice> createLiteYukonPAObjectToYukonDeviceMapper();

    /**
     * Method to get an object mapper that maps a bulk importer file line into a
     * yukon device
     * @return Bulk Importer line to yukon device mapper
     */
    public ObjectMapper<String, YukonDevice> createBulkImporterToYukonDeviceMapper();

}