package com.cannontech.common.bulk.field.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.mapper.AddressToYukonDeviceMapper;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.ObjectMapper;


public class AddressBulkField extends BulkFieldBase<String, SimpleDevice> {

    private AddressToYukonDeviceMapper addressToYukonDeviceMapper;
    
    @Override
    public ObjectMapper<String, SimpleDevice> getIdentifierMapper() {
        
        return addressToYukonDeviceMapper;
    }
    
    @Autowired
    public void setAddressToYukonDeviceMapper(
            AddressToYukonDeviceMapper addressToYukonDeviceMapper) {
        this.addressToYukonDeviceMapper = addressToYukonDeviceMapper;
    }
}
