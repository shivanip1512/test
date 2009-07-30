package com.cannontech.common.bulk.field.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.mapper.MeterNumberToYukonDeviceMapper;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.ObjectMapper;


public class MeterNumberBulkField extends BulkFieldBase<String, SimpleDevice> {
    
    private MeterNumberToYukonDeviceMapper meterNumberToYukonDeviceMapper;
    
    @Override
    public ObjectMapper<String, SimpleDevice> getIdentifierMapper() {
        return meterNumberToYukonDeviceMapper;
    }
    
    @Autowired
    public void setMeterNumberToYukonDeviceMapper(
            MeterNumberToYukonDeviceMapper meterNumberToYukonDeviceMapper) {
        this.meterNumberToYukonDeviceMapper = meterNumberToYukonDeviceMapper;
    }
}
