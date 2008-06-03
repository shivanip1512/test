package com.cannontech.common.bulk.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.DeviceDao;

public final class AddressToYukonDeviceMapper implements ObjectMapper<String, YukonDevice> {

    private DeviceDao deviceDao = null;

    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public YukonDevice map(String from) throws ObjectMappingException {

        try {
            return deviceDao.getYukonDeviceObjectByAddress(from);
        } catch (IncorrectResultSizeDataAccessException  e) {
            throw new ObjectMappingException("Device with address: " + from + "' not found.", e);
        }
    }
}