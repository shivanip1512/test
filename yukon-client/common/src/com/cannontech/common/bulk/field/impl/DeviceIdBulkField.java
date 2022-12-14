package com.cannontech.common.bulk.field.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.mapper.PaoIdToYukonDeviceMapper;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.ObjectMapper;


public class DeviceIdBulkField extends BulkFieldBase<Integer, SimpleDevice> {
    
    private PaoIdToYukonDeviceMapper paoIdToYukonDeviceMapper;
    
    @Autowired
    public void setPaoIdToYukonDeviceMapper(PaoIdToYukonDeviceMapper paoIdToYukonDeviceMapper) {
        this.paoIdToYukonDeviceMapper = paoIdToYukonDeviceMapper;
    }

    @Override
    public ObjectMapper<Integer, SimpleDevice> getIdentifierMapper() {
        return paoIdToYukonDeviceMapper;
    }
}
