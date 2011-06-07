package com.cannontech.stars.dr.hardware.builder.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.stars.dr.hardware.builder.HardwareTypeExtensionService;
import com.cannontech.stars.dr.hardware.model.HardwareDto;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class HardwareTypeExtensionServiceImpl implements HardwareTypeExtensionService {
    
    private ImmutableMap<HardwareType,HardwareTypeExtensionProvider> builderMap = ImmutableMap.of();
    
    @Override
    public void createDevice(HardwareDto hardwareDto) {
        HardwareTypeExtensionProvider hardwareBuilder = builderMap.get(hardwareDto.getHardwareType());
        
        if (hardwareBuilder == null) return;

        hardwareBuilder.createDevice(hardwareDto);
        
    }
    
    @Override
    public void updateDevice(HardwareDto hardwareDto) {
        HardwareTypeExtensionProvider hardwareBuilder = builderMap.get(hardwareDto.getHardwareType());
        
        if (hardwareBuilder == null) return;
        
        hardwareBuilder.updateDevice(hardwareDto);
    }
    
    @Override
    public void deleteDevice(PaoIdentifier pao, InventoryIdentifier inventoryId) {
        HardwareTypeExtensionProvider hardwareBuilder = builderMap.get(inventoryId.getHardwareType());
        
        if (hardwareBuilder == null) return;
        
        hardwareBuilder.deleteDevice(pao, inventoryId);
    }
    
    @Override
    public void retrieveDevice(HardwareDto hardwareDto) {
        HardwareTypeExtensionProvider provider = builderMap.get(hardwareDto.getHardwareType());
        
        if (provider == null) return;
        
        provider.retrieveDevice(hardwareDto);
    }
    
    @Override
    public void validateDevice(HardwareDto hardwareDto, Errors errors) {
        HardwareTypeExtensionProvider provider = builderMap.get(hardwareDto.getHardwareType());
        
        if (provider == null) return;
        
        provider.validateDevice(hardwareDto,errors);
    }
    
    @Autowired
    public void setHardwareBuilders(List<HardwareTypeExtensionProvider> hardwareBuilders) {
        Builder<HardwareType,HardwareTypeExtensionProvider> builder = ImmutableMap.builder();
        
        for (HardwareTypeExtensionProvider hardwareBuilder : hardwareBuilders) {
            builder.put(hardwareBuilder.getType(), hardwareBuilder);
        }
        
        builderMap = builder.build();
    }

}