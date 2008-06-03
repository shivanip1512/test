package com.cannontech.common.bulk.field.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.mapper.AddressToYukonDeviceMapper;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ObjectMapper;


public class AddressBulkField extends BulkFieldBase<String, YukonDevice> {

    private AddressToYukonDeviceMapper addressToYukonDeviceMapper;
    
    @Override
    public ObjectMapper<String, YukonDevice> getIdentifierMapper() {
        
        return addressToYukonDeviceMapper;
    }

    @Autowired
    public void setAddressToYukonDeviceMapper(
            AddressToYukonDeviceMapper addressToYukonDeviceMapper) {
        this.addressToYukonDeviceMapper = addressToYukonDeviceMapper;
    }
}
