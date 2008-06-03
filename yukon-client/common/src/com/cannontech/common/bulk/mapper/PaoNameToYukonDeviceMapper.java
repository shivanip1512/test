package com.cannontech.common.bulk.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.DeviceDao;

public final class PaoNameToYukonDeviceMapper implements ObjectMapper<String, YukonDevice> {
    
    private DeviceDao deviceDao = null;

    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public YukonDevice map(String from) throws ObjectMappingException {

        try {
            return deviceDao.getYukonDeviceObjectByName(from);
        }
        catch (IncorrectResultSizeDataAccessException e) {
            throw new ObjectMappingException("Device with name '" + from + "' not found.");
        }
    }
}