package com.cannontech.common.device.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.device.range.DeviceAddressRange;
import com.cannontech.user.YukonUserContext;

public class DeviceUpdateServiceImpl implements DeviceUpdateService {

    private DeviceDao deviceDao = null;
    private PaoDao paoDao = null;
    private RouteDiscoveryService routeDiscoveryService = null;
    private CommandRequestDeviceExecutor commandRequestDeviceExecutor = null;
    
    private Logger log = YukonLogManager.getLogger(DeviceUpdateServiceImpl.class);
    
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
    
    public void routeDiscovery(final YukonDevice device, List<Integer> routeIds, final YukonUserContext userContext) {
        
        // callback to set routeId and do putconfig when route is discovered
        SimpleCallback<Integer> routeFoundCallback = new SimpleCallback<Integer> () {
            
            @Override
            public void handle(Integer routeId) throws Exception {
                
                if (routeId == null) {
                    
                    log.warn("Route was not found for device '" + paoDao.getYukonPAOName(device.getDeviceId()) + "' (" + device.getDeviceId() + ").");
                
                } else {
                    
                    // update route
                    changeRoute(device, routeId);

                    // putconfig command
                    if (DeviceTypesFuncs.isMCT410(device.getType())) {

                        CommandRequestDevice configCmd = new CommandRequestDevice();
                        configCmd.setDevice(device);
                        configCmd.setCommand("putconfig emetcon intervals");

                        commandRequestDeviceExecutor.execute(configCmd, userContext.getYukonUser());
                    }
                }
            }
        };
        
        // start route discovery
        routeDiscoveryService.routeDiscovery(device, routeIds, routeFoundCallback, userContext.getYukonUser());
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
    
    @Autowired
    public void setCommandRequestDeviceExecutor(
            CommandRequestDeviceExecutor commandRequestDeviceExecutor) {
        this.commandRequestDeviceExecutor = commandRequestDeviceExecutor;
    }
}
