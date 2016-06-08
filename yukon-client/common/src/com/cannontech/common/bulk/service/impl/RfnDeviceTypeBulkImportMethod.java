package com.cannontech.common.bulk.service.impl;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.google.common.collect.ImmutableSet;

public class RfnDeviceTypeBulkImportMethod extends BulkImportMethodBase {

    @Autowired
    private DeviceCreationService deviceCreationService;
    
    
    private static ImmutableSet<BulkFieldColumnHeader> requiredColumns = new ImmutableSet.Builder<BulkFieldColumnHeader>()
            .add(BulkFieldColumnHeader.NAME)
            .add(BulkFieldColumnHeader.DEVICE_TYPE)
            .add(BulkFieldColumnHeader.RFN_MODEL)
            .add(BulkFieldColumnHeader.RFN_SERIAL_NUMBER)
            .add(BulkFieldColumnHeader.RFN_MANUFACTURER)
            .build();
    
    private static ImmutableSet<BulkFieldColumnHeader> optionalColumns = new ImmutableSet.Builder<BulkFieldColumnHeader>()
            .add(BulkFieldColumnHeader.METER_NUMBER)
            .add(BulkFieldColumnHeader.ENABLE)
            .add(BulkFieldColumnHeader.LATITUDE)
            .add(BulkFieldColumnHeader.LONGITUDE)
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
        PaoType paoType = null;
        try {

            String deviceTypeStr = fields.get(BulkFieldColumnHeader.DEVICE_TYPE);
            paoType = PaoType.getForDbString(deviceTypeStr);
            String name = fields.get(BulkFieldColumnHeader.NAME);
            String rfnSerialNumberStringValue = fields.get(BulkFieldColumnHeader.RFN_SERIAL_NUMBER);
            String rfnModelStringValue = fields.get(BulkFieldColumnHeader.RFN_MODEL);
            String rfnManufacturerStringValue = fields.get(BulkFieldColumnHeader.RFN_MANUFACTURER);
            RfnIdentifier rfId = new RfnIdentifier(rfnSerialNumberStringValue, rfnManufacturerStringValue, rfnModelStringValue);

            SimpleDevice device = deviceCreationService.createRfnDeviceByDeviceType(paoType, name, rfId, true);
            return device;

        } catch (IllegalArgumentException e) {
            throw new DeviceCreationException("Could not create device by type: Invalid device type.",
                                              "invalidDeviceType",
                                              e);
        }
    }

}
