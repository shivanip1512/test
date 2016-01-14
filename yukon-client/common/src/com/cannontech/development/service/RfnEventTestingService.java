package com.cannontech.development.service;

import java.io.IOException;

import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.development.model.RfnTestEvent;
import com.cannontech.development.model.RfnTestMeterReading;
import com.cannontech.development.service.impl.DRReport;

public interface RfnEventTestingService {
    
    /**
     * Sends out events and alarm messages on the respective ActiveMQ queues and returns the number of events/alarms sent
     */
    int sendEventsAndAlarms(RfnTestEvent event);
    int sendMeterArchiveRequests(RfnTestMeterReading reading);
    
    void sendRfDaArchiveRequest(int serial, String manufacturer, String model);
    void sendLcrArchiveRequest(int serialFrom, int serialTo, String manufacturer, String model);
    public int sendLcrReadArchive(int serialFrom, int serialTo, int days, DRReport drReport) throws IOException;
    void calculationStressTest();

    void sendLocationResponse(int serialFrom, int serialTo, String manufacturer, String model, double latitude,
            double longitude);
}
