package com.cannontech.common.bulk.field.processor.impl;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dao.DeviceDao;

public class EnableBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {
    
    private DeviceDao deviceDao;
    
    @Override
    public void updateField(SimpleDevice device, YukonDeviceDto value) {

        Boolean enable = value.getEnable();
        
        try {
            
            if (enable == null) {
                return;
            }
            
            if (enable) {
                this.deviceDao.enableDevice(device);
            }
            else if (!enable) {
                this.deviceDao.disableDevice(device);
            }
        }
        catch (DataAccessException e) {
            throw new ProcessingException("Could not " + ((enable) ? "enable" : "disable") + " device with id: " + device.getDeviceId(), e);
        }
    }
    
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
}
