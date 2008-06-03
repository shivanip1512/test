package com.cannontech.common.bulk.field.processor.impl;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.core.dao.DeviceDao;

public class RouteBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {
    
    private DeviceDao deviceDao;
    
    @Override
    public void updateField(YukonDevice device, YukonDeviceDto value) {
        
        try {
            this.deviceDao.changeRoute(device.getDeviceId(), value.getRoute());
        }
        catch (IllegalArgumentException e) {
            throw new ProcessingException(e.getMessage(), e);
        }
        catch (DataAccessException e) {
            throw new ProcessingException("Could not change route of device with id: " + device.getDeviceId() + ", and value: " + value.getRoute(), e);
        }
    }
    
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
}
