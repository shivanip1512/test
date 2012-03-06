package com.cannontech.common.bulk.service.impl;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.google.common.collect.ImmutableSet;

public class RfnTemplateBulkImportMethod extends BulkImportMethodBase{
    @Autowired private DeviceCreationService deviceCreationService = null;
    
    
    private static ImmutableSet<BulkFieldColumnHeader> requiredColumns = new ImmutableSet.Builder<BulkFieldColumnHeader>()
            .add(BulkFieldColumnHeader.NAME)
            .add(BulkFieldColumnHeader.TEMPLATE)
            .add(BulkFieldColumnHeader.RFN_MODEL)
            .add(BulkFieldColumnHeader.RFN_SERIAL_NUMBER)
            .add(BulkFieldColumnHeader.RFN_MANUFACTURER)
            .build();
    
    private static ImmutableSet<BulkFieldColumnHeader> optionalColumns = new ImmutableSet.Builder<BulkFieldColumnHeader>()
            .add(BulkFieldColumnHeader.METER_NUMBER)
            .add(BulkFieldColumnHeader.ENABLE)
            .build();
    
    @Override
    public Set<BulkFieldColumnHeader> getRequiredColumns() {
        return requiredColumns;
    }
    
    @Override
    public Set<BulkFieldColumnHeader> getOptionalColumns() {
        return optionalColumns;
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
