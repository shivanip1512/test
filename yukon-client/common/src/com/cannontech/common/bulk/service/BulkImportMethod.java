package com.cannontech.common.bulk.service;

import java.util.Map;
import java.util.Set;

import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.creation.DeviceCreationException;

public interface BulkImportMethod {
    
    public Set<BulkFieldColumnHeader> getRequiredColumns();
    public YukonDevice initDevice(Map<BulkFieldColumnHeader, String> fields) throws DeviceCreationException;
}
