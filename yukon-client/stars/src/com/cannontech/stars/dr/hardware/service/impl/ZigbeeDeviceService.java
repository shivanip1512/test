package com.cannontech.stars.dr.hardware.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.DigiGateway;
import com.cannontech.common.model.ZigbeeThermostat;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.stars.dr.hardware.builder.DeviceBuilder;
import com.cannontech.stars.dr.hardware.builder.HardwareBuilder;
import com.cannontech.stars.dr.hardware.dao.GatewayDeviceDao;
import com.cannontech.stars.dr.hardware.dao.ZigbeeDeviceDao;
import com.cannontech.stars.dr.hardware.model.HardwareDto;
import com.cannontech.stars.dr.hardware.model.ZigbeeDeviceAssignment;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ZigbeeDeviceService implements DeviceBuilder {
    
    private PaoDao paoDao;
    private ZigbeeDeviceDao zigbeeDeviceDao;
    private GatewayDeviceDao gatewayDeviceDao;
    
    private ImmutableMap<HardwareType,HardwareBuilder> builderMap = ImmutableMap.of();
    
    @Override
    public void createDevice(HardwareDto hardwareDto) {
        HardwareBuilder hardwareBuilder = builderMap.get(hardwareDto.getHardwareType());
        
        if (hardwareBuilder != null) {
            int paoId = paoDao.getNextPaoId();
            hardwareDto.setDeviceId(paoId);
            hardwareBuilder.createDevice(hardwareDto);
        }
        
        return;
    }
    
    @Override
    public void updateDevice(HardwareDto hardwareDto) {
        HardwareBuilder hardwareBuilder = builderMap.get(hardwareDto.getHardwareType());
        
        if (hardwareBuilder != null) {
            hardwareBuilder.updateDevice(hardwareDto);
        }
        
        return;
    }
    
    @Override
    public void deleteDevice(HardwareDto hardwareDto) {
        HardwareBuilder hardwareBuilder = builderMap.get(hardwareDto.getHardwareType());
        
        if (hardwareBuilder != null) {
            hardwareBuilder.deleteDevice(hardwareDto);
        }
        
        return;
    }
    
    public ZigbeeThermostat getZigbeeThermostat(int deviceId) {
    	return zigbeeDeviceDao.getZigbeeUtilPro(deviceId);
    }
    
    public DigiGateway getDigiGateway(int deviceId) {
    	return gatewayDeviceDao.getDigiGateway(deviceId);
    }
    
    public List<ZigbeeDeviceAssignment> getAssignedDevices(int gatewayId) {
    	return gatewayDeviceDao.getAssignedDevices(gatewayId);
    }
    
    public void assignDeviceToGateway(int deviceId, int gatewayId) {
    	gatewayDeviceDao.assignDeviceToGateway(deviceId,gatewayId);
    }
    
    public void unassignDeviceFromGateway(int deviceId) {
    	gatewayDeviceDao.unassignDeviceFromGateway(deviceId);
    }
    
    @Autowired
    public void setHardwareBuilders(List<HardwareBuilder> hardwareBuilders) {
        Builder<HardwareType,HardwareBuilder> builder = ImmutableMap.builder();
        
        for (HardwareBuilder hardwareBuilder : hardwareBuilders) {
            builder.put(hardwareBuilder.getType(), hardwareBuilder);
        }
        
        builderMap = builder.build();
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Autowired
    public void setZigbeeDeviceDao(ZigbeeDeviceDao zigbeeDeviceDao) {
        this.zigbeeDeviceDao = zigbeeDeviceDao;
    }
    
    @Autowired
    public void setGatewayDeviceDao(GatewayDeviceDao gatewayDeviceDao) {
        this.gatewayDeviceDao = gatewayDeviceDao;
    }
    
}
