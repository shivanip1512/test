package com.cannontech.common.device.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.device.range.DeviceAddressRange;

public class DeviceUpdateServiceImpl implements DeviceUpdateService {

    private DeviceDao deviceDao = null;
    private PaoDao paoDao = null;
    private RouteDiscoveryService routeDiscoveryService = null;
    
    public void changeAddress(YukonDevice device, int newAddress) throws IllegalArgumentException {

        boolean validAddressForType = DeviceAddressRange.isValidRange(device.getType(), newAddress);

        if (!validAddressForType) {
            throw new IllegalArgumentException("Address not in valid range for device type: " + newAddress);
        }

        deviceDao.changeAddress(device, newAddress);
    }

    public void changeRoute(YukonDevice device, String newRouteName) throws IllegalArgumentException {

        Integer routeId = paoDao.getRouteIdForRouteName(newRouteName);

        if (routeId == null) {
            throw new IllegalArgumentException("Invalid route name: " + newRouteName);
        }

        deviceDao.changeRoute(device, routeId);
    }
    
    public void changeRoute(YukonDevice device, int newRouteId) throws IllegalArgumentException {

        deviceDao.changeRoute(device, newRouteId);
    }

    public void changeMeterNumber(YukonDevice device, String newMeterNumber) throws IllegalArgumentException {
    
        if (StringUtils.isBlank(newMeterNumber)) {
            throw new IllegalArgumentException("Blank meter number.");
        }
        
        deviceDao.changeMeterNumber(device, newMeterNumber);
    }
    
    public void routeDiscovery(YukonDevice device, List<Integer> routeIds, Boolean doPutconfig) throws Exception {
        
        routeDiscoveryService.routeDiscovery(device, routeIds, doPutconfig);
    }
    
    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setRouteDiscoveryService(
            RouteDiscoveryService routeDiscoveryService) {
        this.routeDiscoveryService = routeDiscoveryService;
    }
}
