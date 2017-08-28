package com.cannontech.common.bulk.field.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.PaoLoadingService;

public class EnableBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {
    
    private DeviceDao deviceDao;
    @Autowired private DeviceUpdateService deviceUpdateService;
    @Autowired private PaoLoadingService paoLoadingService;
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
                this.deviceUpdateService.disableDevice(device);
            }
        }
        catch (DataAccessException e) {
            String status = (enable) ? "enable" : "disable";
            throw new ProcessingException("Could not " + status + " device with id: " + device.getDeviceId(),
                                          status+"Device",
                                          e,
                                          device.getDeviceId(), paoLoadingService.getDisplayablePao(device).getName());
        }
    }
    
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
}
