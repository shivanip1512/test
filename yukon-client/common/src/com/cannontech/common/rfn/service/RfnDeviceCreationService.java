package com.cannontech.common.rfn.service;

import java.util.Set;

import org.joda.time.Instant;

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
    
    public RfnDevice createGateway(String name, RfnIdentifier rfnIdentifier);

    /**
     * If device is not found creates device.
     * 
     * Adds an additional check. If an exactly matching device is not found, is there a "nearly matching" device with
     * the same serial number and manufacturer, but different model?
     * 
     * In addition to the serial number, manufacturer and model, this check looks at the RfnModelChange DB table
     * 
     * If there is a "nearly matching" device, but no entry in RfnModelChange, or an entry that is older than this data's
     * timestamp, changes the device's model to match the data. Adds an entry for the change to RfnModelChange. Generates an event
     * log for the change. Then processes the data.
     * 
     * If there is a "nearly matching" device and an entry in RfnModelChange with a DataTimestamp more recent than the data being
     * processed, then we probably made a model change recently, and this is out-of-order data. Changes the data to reflect the
     * newer model, then processes the data. (Device model remains unchanged). Generates an event log.
     * 
     * Change the PaoType of the device if applicable.
     * 
     * @param  dataTimestamp, use null if the time stamp is not available 
     * 
     * @throws RuntimeException if unable to create device. The exception is logged as warning. Calling method should deal with this exception.
     */
    RfnDevice getOrCreate(RfnIdentifier newDeviceIdentifier, Instant dataTimestamp);
    
    /**
     * Same as above but throws exception
     * 
     * @throws RuntimeException if unable to create device. The exception is logged as warning. Calling method should deal with this exception.
     */
    RfnDevice getOrCreate(RfnIdentifier identifier);
    
    /**
     * This method is used to create device manually from UI
     */
    RfnDevice create(RfnIdentifier rfnIdentifier, Hardware hardware, LiteYukonUser user);
    
    public String getUnknownTemplates();
}