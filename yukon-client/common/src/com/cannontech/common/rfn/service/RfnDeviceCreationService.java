package com.cannontech.common.rfn.service;

import org.springframework.jmx.export.annotation.ManagedAttribute;

import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface RfnDeviceCreationService {

    /**
     * Creates an rfn device using {@link DeviceCreationService} using an
     * expected pao template name derived from the {@link RfnIdentifier}.
     * If the device is a dr device, the stars tables will also be stubbed out (InventoryBase, LmHardwareBase)
     * Returns the {@link RfnDevice} created.  Use this method for creation due to a NM archive request.
     */
    public RfnDevice create(final RfnIdentifier rfnIdentifier);
    
    /**
     * This method is for DR devices only.
     * Creates an rfn dr device, use this method when creating a device as an operator.
     */
    public RfnDevice create(final RfnIdentifier rfnIdentifier, Hardware hardware, LiteYukonUser user);
    
    public void incrementDeviceLookupAttempt();
    
    public void incrementNewDeviceCreated();
    
    @ManagedAttribute
    public String getUnkownTempaltes();
    
    @ManagedAttribute
    public int getDeviceLookupAttempt();
    
    @ManagedAttribute
    public int getNewDeviceCreated();
    
}