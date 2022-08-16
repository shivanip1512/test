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
            String errorMessage;
            String key;
            SimpleDevice existingDevice = deviceDao.findYukonDeviceObjectByName(value.getName());
            if (existingDevice != null) {
                key = "changeDeviceNameAlreadyExists";
                errorMessage = "Could not change name of device with id: " + device.getDeviceId() + ". Another device with this name already exists";
            } else {
                key = "changeDeviceName";
                errorMessage = "Could not change name of device with id: " + device.getDeviceId();
            }
            throw new ProcessingException(errorMessage, key, e, Integer.toString(device.getDeviceId()));
        }
    }
    
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
}
