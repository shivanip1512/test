package com.cannontech.common.bulk.field.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.yukon.IDatabaseCache;

public class NameBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {

    @Autowired private IDatabaseCache dbCache;
    private DeviceDao deviceDao;
    
    @Override
    public void updateField(SimpleDevice device, YukonDeviceDto value) {

        try {
            deviceDao.changeName(device, value.getName());
        } catch (DataAccessException e) {
            String deviceName = dbCache.getAllPaosMap().get(device.getDeviceId()).getPaoName();
            String key;
            boolean alreadyExists = dbCache.getAllPaosMap().values().stream()
                    .filter(d -> d.getPaoName().equalsIgnoreCase(value.getName())).findFirst().isPresent();
            String errorMessage = "Could not change name of device from "
                    + deviceName + " to " + value.getName() + ".";
            if (alreadyExists) {
                key = "changeDeviceNameAlreadyExists";
                errorMessage = errorMessage + " Another device with that name already exists.";
            } else {
                key = "changeDeviceName";
            }
            throw new ProcessingException(errorMessage, key, e, deviceName, value.getName());
        }
    }
    
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
}
