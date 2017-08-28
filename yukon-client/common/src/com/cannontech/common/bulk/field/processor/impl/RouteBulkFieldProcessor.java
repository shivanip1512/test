package com.cannontech.common.bulk.field.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.core.service.PaoLoadingService;

public class RouteBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {
    
    private DeviceUpdateService deviceUpdateService;
    @Autowired private PaoLoadingService paoLoadingService;
    @Override
    public void updateField(SimpleDevice device, YukonDeviceDto value) {
        
        try {
            this.deviceUpdateService.changeRoute(device, value.getRoute());
        }
        catch (IllegalArgumentException e) {
            throw new ProcessingException(e.getMessage(), "invalidRouteName", e, value.getRoute(), paoLoadingService.getDisplayablePao(device).getName());
        }
        catch (DataAccessException e) {
            throw new ProcessingException("Could not change route of device with id: " + device.getDeviceId() + ", and value: " + value.getRoute(),
                                          "changeRoute",
                                          e,
                                          device.getDeviceId(),paoLoadingService.getDisplayablePao(device).getName(),
                                          value.getRoute());
        }
    }
    
    @Required
    public void setDeviceUpdateService(DeviceUpdateService deviceUpdateService) {
        this.deviceUpdateService = deviceUpdateService;
    }
}
