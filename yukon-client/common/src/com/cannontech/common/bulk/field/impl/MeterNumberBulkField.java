package com.cannontech.common.bulk.field.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.mapper.MeterNumberToYukonDeviceMapper;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ObjectMapper;


public class MeterNumberBulkField extends BulkFieldBase<String, YukonDevice> {
    
    private MeterNumberToYukonDeviceMapper meterNumberToYukonDeviceMapper;
    
    @Override
    public ObjectMapper<String, YukonDevice> getIdentifierMapper() {
        return meterNumberToYukonDeviceMapper;
    }
    
    @Autowired
    public void setMeterNumberToYukonDeviceMapper(
            MeterNumberToYukonDeviceMapper meterNumberToYukonDeviceMapper) {
        this.meterNumberToYukonDeviceMapper = meterNumberToYukonDeviceMapper;
    }
}
