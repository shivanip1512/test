package com.cannontech.common.rfn.service;

import java.util.Set;

import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface RfnDeviceCreationService {
    
    public static final String GATEWAY_1_MODEL_STRING = "RFGateway";
    public static final String GATEWAY_2_MODEL_STRING = "GWY800";
    public static final String GATEWAY_3_MODEL_STRING = "VGW";
    public static final String GATEWAY_4_MODEL_STRING = "GWY801";
    
    public static final String GW_MANUFACTURER_EATON =  "EATON";
    
    public static final Set<String> ALL_GATEWAY_MODELS = Set.of(GATEWAY_1_MODEL_STRING,
                                                                GATEWAY_2_MODEL_STRING,
                                                                GATEWAY_3_MODEL_STRING,
                                                                GATEWAY_4_MODEL_STRING);
    
    /**
     * This method is for DR devices only.
     * Creates an rfn dr device, use this method when creating a device as an operator.
     */
    public RfnDevice create(final RfnIdentifier rfnIdentifier, Hardware hardware, LiteYukonUser user);
    
    public RfnDevice createGateway(String name, RfnIdentifier rfnIdentifier);
    
    /**
     * If device is not found creates device. Returns null if unable to create device
     */
    RfnDevice createIfNotFound(RfnIdentifier identifier);
    
}