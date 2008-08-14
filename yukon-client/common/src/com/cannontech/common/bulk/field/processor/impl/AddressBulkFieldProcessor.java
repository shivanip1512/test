package com.cannontech.common.bulk.field.processor.impl;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.service.DeviceUpdateService;


public class AddressBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {

    private DeviceUpdateService deviceUpdateService;
    
    @Override
    public YukonDevice updateField(YukonDevice device, YukonDeviceDto value) throws ProcessingException  {

        try {
            deviceUpdateService.changeAddress(device, value.getAddress());
        }
        catch (DataAccessException e) {
            throw new ProcessingException("Unable to update address.");
        }
        catch (IllegalArgumentException e) {
            throw new ProcessingException(e.getMessage());
        }
        
        return device;
    }
    
    @Required
    public void setDeviceUpdateService(DeviceUpdateService deviceUpdateService) {
        this.deviceUpdateService = deviceUpdateService;
    }
}
