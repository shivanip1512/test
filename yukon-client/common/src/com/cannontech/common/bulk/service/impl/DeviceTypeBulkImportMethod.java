package com.cannontech.common.bulk.service.impl;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
import com.google.common.collect.ImmutableSet;

public class DeviceTypeBulkImportMethod extends BulkImportMethodBase {
    
    @Autowired private DeviceCreationService deviceCreationService = null;
    @Autowired private PaoDao paoDao = null;
    
    private static ImmutableSet<BulkFieldColumnHeader> requiredColumns = new ImmutableSet.Builder<BulkFieldColumnHeader>()
            .add(BulkFieldColumnHeader.NAME)
            .add(BulkFieldColumnHeader.DEVICE_TYPE)
            .add(BulkFieldColumnHeader.ADDRESS)
            .add(BulkFieldColumnHeader.ROUTE)
            .build();
    
    private static ImmutableSet<BulkFieldColumnHeader> optionalColumns = new ImmutableSet.Builder<BulkFieldColumnHeader>()
            .add(BulkFieldColumnHeader.DISCONNECT_ADDRESS)
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
    public SimpleDevice initDevice(Map<BulkFieldColumnHeader, String> fields) throws DeviceCreationException {
        
        SimpleDevice device = null;
        
        try {
            
            String deviceTypeStr = fields.get(BulkFieldColumnHeader.DEVICE_TYPE);
            PaoType paoType = PaoType.getForDbString(deviceTypeStr);
            String name = fields.get(BulkFieldColumnHeader.NAME);
            int address = Integer.valueOf(fields.get(BulkFieldColumnHeader.ADDRESS));
            String routeStr = fields.get(BulkFieldColumnHeader.ROUTE);
            
            Integer routeId = paoDao.getRouteIdForRouteName(routeStr);
            if (routeId == null) {
                throw new DeviceCreationException("Could not create device by type: Invalid route name.");
            }

            device = deviceCreationService.createCarrierDeviceByDeviceType(paoType, name, address, routeId, true);

        } catch (NumberFormatException e) {
            throw new DeviceCreationException("Could not create device by type: Non-numeric address value.", "nonNumericAddress", e);
        } catch (DeviceCreationException e) {
            throw new DeviceCreationException("Could not create new device", "invalidNewDevice", e);
        } catch(IllegalArgumentException e) {
            throw new DeviceCreationException("Could not create device by type: Invalid device type.", "invalidDeviceType", e);
        }

        return device;
    }

}
