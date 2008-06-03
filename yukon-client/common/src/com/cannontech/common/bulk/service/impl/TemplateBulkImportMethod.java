package com.cannontech.common.bulk.service.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;

public class TemplateBulkImportMethod extends BulkImportMethodBase {
    private DeviceCreationService deviceCreationService = null;
    
    @Override
    public Set<BulkFieldColumnHeader> getRequiredColumns() {
        
        Set<BulkFieldColumnHeader> requiredColumns = new HashSet<BulkFieldColumnHeader>();
        requiredColumns.add(BulkFieldColumnHeader.TEMPLATE);
        requiredColumns.add(BulkFieldColumnHeader.NAME);
        return requiredColumns;
    }
    
    @Override
    public YukonDevice initDevice(Map<BulkFieldColumnHeader, String> fields) throws DeviceCreationException {
        
        String creationFieldStringValue = fields.get(BulkFieldColumnHeader.TEMPLATE);
        String nameFieldStringValue = fields.get(BulkFieldColumnHeader.NAME);
        
        YukonDevice device = deviceCreationService.createDeviceByTemplate(creationFieldStringValue, nameFieldStringValue, true);
        
        return device;
    }

    @Autowired
    public void setDeviceCreationService(DeviceCreationService deviceCreationService) {
        this.deviceCreationService = deviceCreationService;
    }
    
}
