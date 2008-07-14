package com.cannontech.common.device.service;

import java.util.List;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.user.YukonUserContext;

public interface DeviceUpdateService {

    /**
     * Checks if new address is valid for device, throws {@link IllegalArgumentException} if not.
     * Otherwise delgates to {@link DeviceDao} to perform address change.
     * @param device
     * @param newAddress
     * @throws IllegalArgumentException
     */
    public void changeAddress(YukonDevice device, int newAddress) throws IllegalArgumentException;
    
    /**
     * Checks if route name is a valid route (a known route exists with that name), throws
     * {@link IllegalArgumentException} if not. Otherwise delegates to {@link DeviceDao} to update.
     * @param device
     * @param newAddress
     * @throws IllegalArgumentException
     */
    public void changeRoute(YukonDevice device, String newRouteName) throws IllegalArgumentException;
    
    public void changeRoute(YukonDevice device, int newRouteId) throws IllegalArgumentException;
    
    public void changeMeterNumber(YukonDevice device, String newMeterNumber) throws IllegalArgumentException;
    
    public void routeDiscovery(YukonDevice device, List<Integer> routeIds, YukonUserContext userContext);
}
