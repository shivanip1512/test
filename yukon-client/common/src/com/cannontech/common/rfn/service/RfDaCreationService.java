package com.cannontech.common.rfn.service;

import org.springframework.jmx.export.annotation.ManagedAttribute;

import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;

public interface RfDaCreationService {

    /**
     * Creates an rfn device using {@link DeviceCreationService} using an
     * expected pao template name derived from the {@link RfdaIdentifier}.
     * If the device is a dr device, the stars tables will also be stubbed out (InventoryBase, LmHardwareBase)
     * Returns the {@link RfdaDevice} created.  Use this method for creation due to a NM archive request.
     */
    public RfnDevice create(final RfnIdentifier rfnIdentifier);
    
    public void incrementNewDeviceCreated();
    
    @ManagedAttribute
    public int getNewDeviceCreated();
    
}