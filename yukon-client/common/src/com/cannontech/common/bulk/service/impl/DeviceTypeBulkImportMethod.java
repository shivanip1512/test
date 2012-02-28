package com.cannontech.common.bulk.service.impl;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;

public class DeviceTypeBulkImportMethod extends BulkImportMethodBase {
    
    @Autowired private DeviceCreationService deviceCreationService = null;
    @Autowired private PaoDao paoDao = null;
    
    @Override
    public Set<BulkFieldColumnHeader> getRequiredColumns() {
        
        Set<BulkFieldColumnHeader> requiredColumns = new LinkedHashSet<BulkFieldColumnHeader>();
        requiredColumns.add(BulkFieldColumnHeader.NAME);
        requiredColumns.add(BulkFieldColumnHeader.DEVICE_TYPE);
        requiredColumns.add(BulkFieldColumnHeader.ADDRESS);
        requiredColumns.add(BulkFieldColumnHeader.ROUTE);
        return requiredColumns;
    }
    
    public Set<BulkFieldColumnHeader> getOptionalColumns() {

        Set<BulkFieldColumnHeader> requiredColumns = new LinkedHashSet<BulkFieldColumnHeader>();
        requiredColumns.add(BulkFieldColumnHeader.DISCONNECT_ADDRESS);
        requiredColumns.add(BulkFieldColumnHeader.METER_NUMBER);
        requiredColumns.add(BulkFieldColumnHeader.ENABLE);
        return requiredColumns;
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
    
            device = deviceCreationService.createCarrierDeviceByDeviceType(paoType.getDeviceTypeId(), name, address, routeId, true);
            
        } catch (NumberFormatException e) {
            throw new DeviceCreationException("Could not create device by type: Non-numeric address value.", e);
        } catch (DeviceCreationException e) {
            throw new DeviceCreationException("Could not create new device", e);
        } catch(IllegalArgumentException e) {
            throw new DeviceCreationException("Could not create device by type: Invalid device type.", e);
        }

        return device;
    }

}
