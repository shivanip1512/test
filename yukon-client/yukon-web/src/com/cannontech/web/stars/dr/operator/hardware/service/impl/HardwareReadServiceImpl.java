package com.cannontech.web.stars.dr.operator.hardware.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareReadNowStrategy;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareReadService;
import com.cannontech.web.stars.dr.operator.hardware.service.HardwareStrategyType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;


public class HardwareReadServiceImpl implements HardwareReadService{
    @Autowired private InventoryDao inventoryDao;
    private ImmutableMap<HardwareStrategyType, HardwareReadNowStrategy> strategies = ImmutableMap.of();
    
    @Autowired
    public void setStrategies(List<HardwareReadNowStrategy> strategyList) {
        Builder<HardwareStrategyType, HardwareReadNowStrategy> builder = ImmutableMap.builder();
        for (HardwareReadNowStrategy strategy : strategyList) {
            builder.put(strategy.getType(), strategy);
        }
        strategies = builder.build();
    }
    
   @Override
    public Map<String, Object> readNow(int deviceId, YukonUserContext userContext) {
        InventoryIdentifier inventory = inventoryDao.getYukonInventoryForDeviceId(deviceId);
        HardwareType type = inventory.getHardwareType();
        
        HardwareStrategyType strategy = getStrategy(type);
        final HardwareReadNowStrategy impl = strategies.get(strategy);
        return impl.readNow(deviceId, userContext);
       
    }
    
    private HardwareStrategyType getStrategy(HardwareType type) throws IllegalArgumentException {
        for (HardwareStrategyType strategyType : strategies.keySet()) {
            HardwareReadNowStrategy deviceReadStrategy = strategies.get(strategyType);
            if (deviceReadStrategy.canHandle(type)) {
                return strategyType;
            }
        }
        throw new IllegalArgumentException("No device read strategy found for hardware type " +type);
    }
   
}
