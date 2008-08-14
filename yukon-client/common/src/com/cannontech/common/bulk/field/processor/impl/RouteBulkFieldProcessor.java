package com.cannontech.common.bulk.field.processor.impl;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.service.DeviceUpdateService;

public class RouteBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {
    
    private DeviceUpdateService deviceUpdateService;
    
    @Override
    public YukonDevice updateField(YukonDevice device, YukonDeviceDto value) {
        
        try {
            this.deviceUpdateService.changeRoute(device, value.getRoute());
        }
        catch (IllegalArgumentException e) {
            throw new ProcessingException(e.getMessage(), e);
        }
        catch (DataAccessException e) {
            throw new ProcessingException("Could not change route of device with id: " + device.getDeviceId() + ", and value: " + value.getRoute(), e);
        }
        
        return device;
    }
    
    @Required
    public void setDeviceUpdateService(DeviceUpdateService deviceUpdateService) {
        this.deviceUpdateService = deviceUpdateService;
    }
}
