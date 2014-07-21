package com.cannontech.web.stars.dr.operator.hardware.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.hardware.service.DeviceReadStrategy;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareReadService;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareStrategyType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;


public class HardwareReadServiceImpl implements HardwareReadService{
    @Autowired private InventoryDao inventoryDao;
    private ImmutableMap<HardwareStrategyType, DeviceReadStrategy> strategies = ImmutableMap.of();
    
    @Autowired
    public void setStrategies(List<DeviceReadStrategy> strategyList) {
        Builder<HardwareStrategyType, DeviceReadStrategy> builder = ImmutableMap.builder();
        for (DeviceReadStrategy strategy : strategyList) {
            builder.put(strategy.getType(), strategy);
        }
        strategies = builder.build();
    }
    
   @Override
    public Map<String, Object> readNow(int deviceId, YukonUserContext userContext) {
        InventoryIdentifier inventory = inventoryDao.getYukonInventoryForDeviceId(deviceId);
        HardwareType type = inventory.getHardwareType();
        
        HardwareStrategyType strategy = getStrategy(type);
        final DeviceReadStrategy impl = strategies.get(strategy);
        return impl.readNow(deviceId, userContext);
       
    }
    
    private HardwareStrategyType getStrategy(HardwareType type) throws IllegalArgumentException {
        for (HardwareStrategyType strategyType : strategies.keySet()) {
            DeviceReadStrategy deviceReadStrategy = strategies.get(strategyType);
            if (deviceReadStrategy.canHandle(type)) {
                return strategyType;
            }
        }
        throw new IllegalArgumentException("No device read strategy found for hardware type " +type);
    }
   
}
