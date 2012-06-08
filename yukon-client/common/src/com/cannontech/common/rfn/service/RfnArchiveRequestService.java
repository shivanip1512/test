package com.cannontech.common.rfn.service;

import org.springframework.jmx.export.annotation.ManagedAttribute;

import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;

public interface RfnArchiveRequestService {

    /**
     * Creates an rfn device using {@link DeviceCreationService} using an
     * expected pao template name derived from the {@link RfnIdentifier}.
     * If the device is a dr device, the stars tables will also be filled (InventoryBase, LmHardwareBase)
     * Returns the {@link RfnDevice} created.
     */
    public RfnDevice createDevice(final RfnIdentifier rfnIdentifier);
    
    public void incrementProcessedArchiveRequest();
    
    public void incrementDeviceLookupAttempt();
    
    public void incrementNewDeviceCreated();
    
    @ManagedAttribute
    public int getWorkerCount();
    
    @ManagedAttribute
    public int getQueueSize();
    
    @ManagedAttribute
    public String getUnkownTempaltes();
    
    @ManagedAttribute
    public int getDeviceLookupAttempt();
    
    @ManagedAttribute
    public int getNewDeviceCreated();
    
    @ManagedAttribute
    public int getProcessedArchiveRequest();
    
}