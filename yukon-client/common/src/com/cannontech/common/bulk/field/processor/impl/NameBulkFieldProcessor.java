package com.cannontech.common.bulk.field.processor.impl;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dao.DeviceDao;

public class NameBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {

    private DeviceDao deviceDao;
    
    @Override
    public void updateField(SimpleDevice device, YukonDeviceDto value) {

        try {
            deviceDao.changeName(device, value.getName());
        }
        catch (DataAccessException e) {
            throw new ProcessingException("Could not change name of device with id: " + device.getDeviceId(),
                                          "changeDeviceName",
                                          e,
                                          device.getDeviceId());
        }
    }
    
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
}
