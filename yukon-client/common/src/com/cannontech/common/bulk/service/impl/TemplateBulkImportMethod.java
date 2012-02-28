package com.cannontech.common.bulk.service.impl;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.model.SimpleDevice;

public class TemplateBulkImportMethod extends BulkImportMethodBase {
    @Autowired
    private DeviceCreationService deviceCreationService = null;

    @Override
    public Set<BulkFieldColumnHeader> getRequiredColumns() {

        Set<BulkFieldColumnHeader> requiredColumns = new LinkedHashSet<BulkFieldColumnHeader>();
        requiredColumns.add(BulkFieldColumnHeader.NAME);
        requiredColumns.add(BulkFieldColumnHeader.TEMPLATE);
        return requiredColumns;
    }

    @Override
    public Set<BulkFieldColumnHeader> getOptionalColumns() {

        Set<BulkFieldColumnHeader> requiredColumns = new TreeSet<BulkFieldColumnHeader>();
        requiredColumns.add(BulkFieldColumnHeader.METER_NUMBER);
        requiredColumns.add(BulkFieldColumnHeader.ROUTE);
        requiredColumns.add(BulkFieldColumnHeader.DISCONNECT_ADDRESS);
        requiredColumns.add(BulkFieldColumnHeader.ENABLE);
        requiredColumns.add(BulkFieldColumnHeader.ADDRESS);
        return requiredColumns;
    }

    @Override
    public SimpleDevice initDevice(Map<BulkFieldColumnHeader, String> fields)
            throws DeviceCreationException {

        String creationFieldStringValue = fields.get(BulkFieldColumnHeader.TEMPLATE);
        String nameFieldStringValue = fields.get(BulkFieldColumnHeader.NAME);

        SimpleDevice device =
            deviceCreationService.createDeviceByTemplate(creationFieldStringValue,
                                                         nameFieldStringValue,
                                                         true);

        return device;
    }
}
