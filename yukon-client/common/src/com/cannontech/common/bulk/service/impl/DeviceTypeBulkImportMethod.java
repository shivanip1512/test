package com.cannontech.common.bulk.service.impl;

import java.util.HashSet;
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
    
    private DeviceCreationService deviceCreationService = null;
    private PaoDao paoDao = null;
    
    @Autowired
    public void setDeviceCreationService(DeviceCreationService deviceCreationService) {
        this.deviceCreationService = deviceCreationService;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Override
    public Set<BulkFieldColumnHeader> getRequiredColumns() {
        
        Set<BulkFieldColumnHeader> requiredColumns = new HashSet<BulkFieldColumnHeader>();
        requiredColumns.add(BulkFieldColumnHeader.DEVICE_TYPE);
        requiredColumns.add(BulkFieldColumnHeader.NAME);
        requiredColumns.add(BulkFieldColumnHeader.ADDRESS);
        requiredColumns.add(BulkFieldColumnHeader.ROUTE);
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
        }

        return device;
    }

}
