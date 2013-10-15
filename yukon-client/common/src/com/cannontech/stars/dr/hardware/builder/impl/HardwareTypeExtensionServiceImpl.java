package com.cannontech.stars.dr.hardware.builder.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.dr.hardware.builder.HardwareTypeExtensionService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class HardwareTypeExtensionServiceImpl implements HardwareTypeExtensionService {
    @Autowired private DbChangeManager dbChangeManager;
    
    private ImmutableMap<HardwareType,HardwareTypeExtensionProvider> builderMap = ImmutableMap.of();
    
    @Override
    public void createDevice(Hardware hardware) {
        HardwareTypeExtensionProvider hardwareBuilder = builderMap.get(hardware.getHardwareType());
        
        if (hardwareBuilder == null) return;

        hardwareBuilder.createDevice(hardware);

        dbChangeManager.processDbChange(hardware.getInventoryId(), DBChangeMsg.CHANGE_INVENTORY_DB,
            DBChangeMsg.CAT_INVENTORY_DB, DbChangeType.UPDATE);
    }
    
    @Override
    public void updateDevice(Hardware hardware) {
        HardwareTypeExtensionProvider hardwareBuilder = builderMap.get(hardware.getHardwareType());
        
        if (hardwareBuilder == null) return;
        
        hardwareBuilder.updateDevice(hardware);
    }
    
    @Override
    public void preDeleteCleanup(YukonPao pao, InventoryIdentifier inventoryId) {
        HardwareTypeExtensionProvider hardwareBuilder = builderMap.get(inventoryId.getHardwareType());
        
        if (hardwareBuilder == null) return;
        
        hardwareBuilder.preDeleteCleanup(pao, inventoryId);
    }
    
    @Override
    public void deleteDevice(YukonPao pao, InventoryIdentifier inventoryId) {
        HardwareTypeExtensionProvider hardwareBuilder = builderMap.get(inventoryId.getHardwareType());
        
        if (hardwareBuilder == null) return;
        
        hardwareBuilder.deleteDevice(pao, inventoryId);
    }
    
    @Override
    public void moveDeviceToInventory(YukonPao pao, InventoryIdentifier inventoryId) {
        HardwareTypeExtensionProvider hardwareBuilder = builderMap.get(inventoryId.getHardwareType());
        
        if (hardwareBuilder == null) return;
        
        hardwareBuilder.moveDeviceToInventory(pao, inventoryId);
    }
    
    @Override
    public void retrieveDevice(Hardware hardware) {
        HardwareTypeExtensionProvider provider = builderMap.get(hardware.getHardwareType());
        
        if (provider == null) return;
        
        provider.retrieveDevice(hardware);
    }
    
    @Override
    public void validateDevice(Hardware hardware, Errors errors) {
        HardwareTypeExtensionProvider provider = builderMap.get(hardware.getHardwareType());
        
        if (provider == null) return;
        
        provider.validateDevice(hardware,errors);
    }
    
    @Autowired
    public void setHardwareBuilders(List<HardwareTypeExtensionProvider> hardwareBuilders) {
        Builder<HardwareType,HardwareTypeExtensionProvider> builder = ImmutableMap.builder();
        
        for (HardwareTypeExtensionProvider hardwareBuilder : hardwareBuilders) {
            for (HardwareType type : hardwareBuilder.getTypes()) {
                builder.put(type, hardwareBuilder);
            }
        }
        
        builderMap = builder.build();
    }

}