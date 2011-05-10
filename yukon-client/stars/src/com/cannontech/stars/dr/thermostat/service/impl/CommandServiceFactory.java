package com.cannontech.stars.dr.thermostat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.stars.dr.thermostat.service.ThermostatCommandExecutionService;

public class CommandServiceFactory {
    
    private ZigbeeCommandService zigbeeCommandService;
    private ExpressComCommandService expressComCommandService;
    
    /**
     * In the future this will need to be smarter, perhaps each service would have
     * a list of hardware types it supported and this guy could build a map of those.
     * For now this will do.
     * @param type
     * @return
     */
    public ThermostatCommandExecutionService getCommandService(HardwareType type) {
        ThermostatCommandExecutionService thermostatCommandExecutionService;
        if (type.isZigbee()) {
            thermostatCommandExecutionService = zigbeeCommandService;
        } else {
            thermostatCommandExecutionService = expressComCommandService;
        }
        return thermostatCommandExecutionService;
    }

    @Autowired
    public void setExpressComCommandService(ExpressComCommandService expressComCommandService) {
        this.expressComCommandService = expressComCommandService;
    }
    
    @Autowired
    public void setZigbeeCommandService(ZigbeeCommandService zigbeeCommandService) {
        this.zigbeeCommandService = zigbeeCommandService;
    }
    
}