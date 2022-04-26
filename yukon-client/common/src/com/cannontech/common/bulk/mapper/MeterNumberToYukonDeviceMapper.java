package com.cannontech.common.bulk.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.DeviceDao;

public final class MeterNumberToYukonDeviceMapper implements ObjectMapper<String, SimpleDevice> {
    
    private DeviceDao deviceDao = null;


    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public SimpleDevice map(String from) throws ObjectMappingException {

        try {
            return deviceDao.getYukonDeviceObjectByMeterNumber(from);
        } catch (IncorrectResultSizeDataAccessException  e) {
            if (e.getActualSize() > 1) {
                throw new ObjectMappingException("More than one device with meter number: '" + from + "' was found.", "duplicateMeterNumber", from, e);
            }
            throw new ObjectMappingException("Device with meter number: '" + from + "' not found.","invalidMeterNumber", from, e);
        }
    }
}