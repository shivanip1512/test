package com.cannontech.development.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.DecoderException;

import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.development.model.RfnTestEvent;
import com.cannontech.development.model.RfnTestMeterReading;
import com.cannontech.development.service.impl.DRReport;

public interface RfnEventTestingService {
    
    /**
     * Sends out events and alarm messages on the respective ActiveMQ queues and returns the number of events/alarms sent
     */
    int sendEventsAndAlarms(RfnTestEvent event);
    int sendMeterReadArchiveRequests(RfnTestMeterReading reading);
    public int sendLcrReadArchive(int serialFrom, int serialTo, int days, DRReport drReport) throws IOException, DecoderException;
    void calculationStressTest();

    void sendLocationResponse(int serialFrom, int serialTo, String manufacturer, String model, double latitude,
            double longitude);
    
    /**
     * Gets a RFN meter types, grouped by manufacturer and/or type.  
     */
    Map<String, List<RfnManufacturerModel>> getGroupedRfnTypes();
    
    /**
     * Sends an RFN Config Notification message to Porter.  
     */
    int sendConfigNotification(RfnTestMeterReading reading);
    
}
