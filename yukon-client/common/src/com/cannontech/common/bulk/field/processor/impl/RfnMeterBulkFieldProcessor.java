package com.cannontech.common.bulk.field.processor.impl;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.cannontech.amr.rfn.dao.RfnMeterDao;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.model.SimpleDevice;

public class RfnMeterBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {

    private RfnMeterDao rfnMeterDao;
    
    @Override
    public void updateField(SimpleDevice device, YukonDeviceDto value) {

        try {
            RfnMeter meter = rfnMeterDao.getMeter(device);
            meter.getMeterIdentifier().setSensorSerialNumber(value.getRfnSerialNumber());
            meter.getMeterIdentifier().setSensorManufacturer(value.getRfnManufacturer());
            meter.getMeterIdentifier().setSensorModel(value.getRfnModel());
            rfnMeterDao.updateMeter(meter);
        }
        catch (DataAccessException e) {
            throw new ProcessingException("Could not change serial number, manufacturer, model of device with id " + device.getDeviceId() + ": " + e.getMessage(), e);
        }
    }
    
    @Required
    public void setRfnMeterDao(RfnMeterDao rfnMeterDao) {
        this.rfnMeterDao = rfnMeterDao;
    }
}