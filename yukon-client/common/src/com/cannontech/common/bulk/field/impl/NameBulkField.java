package com.cannontech.common.bulk.field.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.mapper.PaoNameToYukonDeviceMapper;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.ObjectMapper;


public class NameBulkField extends BulkFieldBase<String, SimpleDevice> {

    private PaoNameToYukonDeviceMapper paoNameToYukonDeviceMapper;
    
    @Override
    public ObjectMapper<String, SimpleDevice> getIdentifierMapper() {
        
        return paoNameToYukonDeviceMapper;
    }
    
    @Autowired
    public void setPaoNameToYukonDeviceMapper(
            PaoNameToYukonDeviceMapper paoNameToYukonDeviceMapper) {
        this.paoNameToYukonDeviceMapper = paoNameToYukonDeviceMapper;
    }
    
}
