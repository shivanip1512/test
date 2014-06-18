package com.cannontech.common.rfn.service.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.events.loggers.RfnDeviceEventLogService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfDaCreationService;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;

public class RfDaCreationServiceImpl implements RfDaCreationService {
    
    @Autowired private DeviceCreationService deviceCreationService;
    @Autowired private RfnDeviceEventLogService rfnDeviceEventLogService;
    @Autowired private DbChangeManager dbChangeManager;

    private final AtomicInteger newDeviceCreated = new AtomicInteger();

    @Override
    @Transactional
    public RfnDevice create(final RfnIdentifier rfnIdentifier) {
        String deviceName = "RFDA_" + rfnIdentifier.getCombinedIdentifier();
        
        YukonDevice newDevice = deviceCreationService.createRfnDeviceByDeviceType(PaoType.RF_DA, deviceName, 
                rfnIdentifier.getSensorModel(), rfnIdentifier.getSensorManufacturer(), 
                rfnIdentifier.getSensorSerialNumber(), true);
        RfnDevice device = new RfnDevice(newDevice.getPaoIdentifier(), rfnIdentifier);
        
        rfnDeviceEventLogService.createdNewDeviceAutomatically(device.getPaoIdentifier().getPaoId(), 
                device.getRfnIdentifier().getCombinedIdentifier(), "N/A", deviceName);

        dbChangeManager.processPaoDbChange(newDevice, DbChangeType.ADD);

        return device;
    }
    
    @Override
    public void incrementNewDeviceCreated() {
        newDeviceCreated.incrementAndGet();
    }
    
    @Override
    @ManagedAttribute
    public int getNewDeviceCreated() {
        return newDeviceCreated.get();
    }
    
}