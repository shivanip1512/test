package com.cannontech.common.device.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.device.range.DeviceAddressRange;

public class DeviceUpdateServiceImpl implements DeviceUpdateService {

    private DeviceDao deviceDao = null;
    private PaoDao paoDao = null;
    
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

    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
}
