package com.cannontech.common.bulk.service.impl;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.model.SimpleDevice;

public class RfnTemplateBulkImportMethod extends BulkImportMethodBase{
    @Autowired private DeviceCreationService deviceCreationService = null;
    
    @Override
    public Set<BulkFieldColumnHeader> getRequiredColumns() {
        
        Set<BulkFieldColumnHeader> requiredColumns = new LinkedHashSet<BulkFieldColumnHeader>();
        requiredColumns.add(BulkFieldColumnHeader.NAME);
        requiredColumns.add(BulkFieldColumnHeader.TEMPLATE);
        requiredColumns.add(BulkFieldColumnHeader.RFN_MODEL);
        requiredColumns.add(BulkFieldColumnHeader.RFN_SERIAL_NUMBER);
        requiredColumns.add(BulkFieldColumnHeader.RFN_MANUFACTURER);
        return requiredColumns;
    }
    
    @Override
    public Set<BulkFieldColumnHeader> getOptionalColumns() {

        Set<BulkFieldColumnHeader> requiredColumns = new LinkedHashSet<BulkFieldColumnHeader>();
        requiredColumns.add(BulkFieldColumnHeader.METER_NUMBER);
        requiredColumns.add(BulkFieldColumnHeader.ENABLE);
        return requiredColumns;
    }
    
    @Override
    public SimpleDevice initDevice(Map<BulkFieldColumnHeader, String> fields)
            throws DeviceCreationException {

        String creationFieldStringValue = fields.get(BulkFieldColumnHeader.TEMPLATE);
        String nameFieldStringValue = fields.get(BulkFieldColumnHeader.NAME);
        String rfnSerialNumberStringValue = fields.get(BulkFieldColumnHeader.RFN_SERIAL_NUMBER);
        String rfnModelStringValue = fields.get(BulkFieldColumnHeader.RFN_MODEL);
        String rfnManufacturerStringValue = fields.get(BulkFieldColumnHeader.RFN_MANUFACTURER);

        SimpleDevice device = deviceCreationService.createRfnDeviceByTemplate(creationFieldStringValue,
                                                            nameFieldStringValue,
                                                            rfnModelStringValue,
                                                            rfnManufacturerStringValue,
                                                            rfnSerialNumberStringValue,
                                                            true);

        return device;
    }

}
