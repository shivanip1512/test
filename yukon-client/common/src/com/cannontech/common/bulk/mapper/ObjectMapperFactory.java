package com.cannontech.common.bulk.mapper;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ObjectMapper;

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

}