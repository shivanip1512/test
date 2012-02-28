package com.cannontech.common.bulk.service.impl;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.model.SimpleDevice;

public class RfnDeviceTypeBulkImportMethod extends BulkImportMethodBase {

    @Autowired
    private DeviceCreationService deviceCreationService = null;

    @Override
    public Set<BulkFieldColumnHeader> getRequiredColumns() {

        Set<BulkFieldColumnHeader> requiredColumns = new LinkedHashSet<BulkFieldColumnHeader>();
        requiredColumns.add(BulkFieldColumnHeader.NAME);
        requiredColumns.add(BulkFieldColumnHeader.DEVICE_TYPE);
        requiredColumns.add(BulkFieldColumnHeader.RFN_MODEL);
        requiredColumns.add(BulkFieldColumnHeader.RFN_SERIAL_NUMBER);
        requiredColumns.add(BulkFieldColumnHeader.RFN_MANUFACTURER);
        return requiredColumns;
    }

    @Override
    public Set<BulkFieldColumnHeader> getOptionalColumns() {

        SortedSet<BulkFieldColumnHeader> requiredColumns = new TreeSet<BulkFieldColumnHeader>();
        requiredColumns.add(BulkFieldColumnHeader.METER_NUMBER);
        requiredColumns.add(BulkFieldColumnHeader.ENABLE);
        return requiredColumns;
    }

    @Override
    public SimpleDevice initDevice(Map<BulkFieldColumnHeader, String> fields)
            throws DeviceCreationException {

        String deviceTypeStr = fields.get(BulkFieldColumnHeader.DEVICE_TYPE);
        String name = fields.get(BulkFieldColumnHeader.NAME);
        String rfnSerialNumberStringValue = fields.get(BulkFieldColumnHeader.RFN_SERIAL_NUMBER);
        String rfnModelStringValue = fields.get(BulkFieldColumnHeader.RFN_MODEL);
        String rfnManufacturerStringValue = fields.get(BulkFieldColumnHeader.RFN_MANUFACTURER);

        SimpleDevice device = deviceCreationService.createRfnDeviceByDeviceType(deviceTypeStr,
                                                              name,
                                                              rfnModelStringValue,
                                                              rfnManufacturerStringValue,
                                                              rfnSerialNumberStringValue,
                                                              true);
        return device;
    }

}
