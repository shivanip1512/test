package com.cannontech.common.bulk.service;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.model.SimpleDevice;

public interface BulkImportMethod {
      
    public String getType();
    
    public Set<BulkFieldColumnHeader> getRequiredColumns();
    public Set<BulkFieldColumnHeader> getOptionalColumns();
    
    public SimpleDevice initDevice(Map<BulkFieldColumnHeader, String> fields) throws DeviceCreationException;
}
