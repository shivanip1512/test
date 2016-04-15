package com.cannontech.common.bulk.field.processor.impl;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;

public class RfnMeterBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {

    private RfnDeviceDao rfnDeviceDao;
    
    @Override
    public void updateField(SimpleDevice device, YukonDeviceDto value) {

        try {
            RfnDevice meter = rfnDeviceDao.getDevice(device);
            RfnIdentifier updatedIdentifier = new RfnIdentifier(value.getRfnSerialNumber(), value.getRfnManufacturer(), value.getRfnModel());
            rfnDeviceDao.updateDevice(new RfnDevice(meter.getName(), meter.getPaoIdentifier(), updatedIdentifier));
        }
        catch (DataAccessException e) {
            throw new ProcessingException("Could not change serial number, manufacturer, model of device with id " + device.getDeviceId() + ": " + e.getMessage(),
                                          "changeRfnDevice",
                                          e,
                                          device.getDeviceId());
        }
    }
    
    @Required
    public void setRfnDeviceDao(RfnDeviceDao rfnDeviceDao) {
        this.rfnDeviceDao = rfnDeviceDao;
    }
}