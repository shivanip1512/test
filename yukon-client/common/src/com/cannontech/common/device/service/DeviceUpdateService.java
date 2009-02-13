package com.cannontech.common.device.service;

import java.util.List;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LiteYukonUser;

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
    
    public void routeDiscovery(YukonDevice device, List<Integer> routeIds, LiteYukonUser liteYukonUser);
    
    /**
     * Method to change a device's type. Note: the returned device must be saved
     * to complete the change
     * @param currentDevice - Device to change
     * @param newDefinition - Definition of type to change to
     * @return The changed device
     */
    public abstract DeviceBase changeDeviceType(DeviceBase currentDevice,
            DeviceDefinition newDefinition);

    /**
     * Method to change a device's type
     * @param currentDevice - Device to change
     * @param newDefinition - Definition of type to change to
     */
    public YukonDevice changeDeviceType(YukonDevice currentDevice,
            DeviceDefinition newDefinition);
}
