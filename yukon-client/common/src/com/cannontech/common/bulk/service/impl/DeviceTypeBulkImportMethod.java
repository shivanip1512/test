package com.cannontech.common.bulk.service.impl;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

public class DeviceTypeBulkImportMethod extends BulkImportMethodBase {
    
    private DeviceCreationService deviceCreationService = null;
    private PaoGroupsWrapper paoGroupsWrapper = null;
    private PaoDao paoDao = null;
    
    @Autowired
    public void setDeviceCreationService(DeviceCreationService deviceCreationService) {
        this.deviceCreationService = deviceCreationService;
    }

    @Autowired
    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
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
    public YukonDevice initDevice(Map<BulkFieldColumnHeader, String> fields) throws DeviceCreationException {
        
        YukonDevice device = null;
        
        try {
            
            String deviceTypeStr = fields.get(BulkFieldColumnHeader.DEVICE_TYPE);
            int deviceType = paoGroupsWrapper.getDeviceType(deviceTypeStr);
            String name = fields.get(BulkFieldColumnHeader.NAME);
            int address = Integer.valueOf(fields.get(BulkFieldColumnHeader.ADDRESS));
            String routeStr = fields.get(BulkFieldColumnHeader.ROUTE);
            
            Integer routeId = paoDao.getRouteIdForRouteName(routeStr);
            if (routeId == null) {
                throw new DeviceCreationException("Could not create device by type: Invalid route name.");
            }
    
            device = deviceCreationService.createDeviceByDeviceType(deviceType, name, address, routeId, true);
            
        } catch (NumberFormatException e) {
            throw new DeviceCreationException("Could not create device by type: Non-numeric address value.", e);
            
        } catch (SQLException e) {
            throw new DeviceCreationException("Could not create new device.", e);
        } catch (DeviceCreationException e) {
            throw new DeviceCreationException("Could not create new device: " + e.getMessage());
        }

        return device;
    }

}
