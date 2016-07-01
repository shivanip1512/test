package com.cannontech.common.bulk.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.DeviceDao;

public final class PaoIdToYukonDeviceMapper implements ObjectMapper<Integer, SimpleDevice> {
    private DeviceDao deviceDao = null;

    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public SimpleDevice map(Integer id) throws ObjectMappingException {

        try {
            return deviceDao.getYukonDevice(id);
        } catch (IncorrectResultSizeDataAccessException  e) {
            throw new ObjectMappingException("Device with id: " + id + " not found.", "invalidId", id, e);
        }
    }
}