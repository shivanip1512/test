package com.cannontech.common.bulk.field.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.mapper.PaoNameToYukonDeviceMapper;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ObjectMapper;


public class NameBulkField extends BulkFieldBase<String, YukonDevice> {

    private PaoNameToYukonDeviceMapper paoNameToYukonDeviceMapper;
    
    @Override
    public ObjectMapper<String, YukonDevice> getIdentifierMapper() {
        
        return paoNameToYukonDeviceMapper;
    }
    
    @Autowired
    public void setPaoNameToYukonDeviceMapper(
            PaoNameToYukonDeviceMapper paoNameToYukonDeviceMapper) {
        this.paoNameToYukonDeviceMapper = paoNameToYukonDeviceMapper;
    }
    
}
