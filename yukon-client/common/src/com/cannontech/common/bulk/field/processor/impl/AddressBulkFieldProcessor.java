package com.cannontech.common.bulk.field.processor.impl;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.core.dao.DeviceDao;


public class AddressBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {

    private DeviceDao deviceDao;
    
    @Override
    public void updateField(YukonDevice device, YukonDeviceDto value) throws ProcessingException  {

        try {
            int d = device.getDeviceId();
            int t = device.getType();
            int a = value.getAddress();
            
            deviceDao.changeAddress(d, t, a);
        }
        catch (DataAccessException e) {
            throw new ProcessingException("Unable to update address.");
        }
        catch (IllegalArgumentException e) {
            throw new ProcessingException(e.getMessage());
        }
    }
    
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
}
