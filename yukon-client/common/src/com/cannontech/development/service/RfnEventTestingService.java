package com.cannontech.development.service;

import java.io.IOException;

import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.development.model.RfnTestEvent;
import com.cannontech.development.service.impl.DRReport;

public interface RfnEventTestingService {
    
    /**
     * Sends out events and alarm messages on the respective ActiveMQ queues and returns the number of events/alarms sent
     */
    int sendEventsAndAlarms(RfnTestEvent event);
    
    /**
     * TODO: move all of these passed in variables into a model object
     */
    int sendMeterArchiveRequests(int serialFrom, int serialTo, String manufacturer, String model, Double value, RfnMeterReadingType type, boolean random, String uom, 
                                 boolean quad1, boolean quad2, boolean quad3, boolean quad4, boolean max, boolean min, boolean avg,
                                 boolean phaseA, boolean phaseB, boolean phaseC, boolean touRateA, boolean touRateB, boolean touRateC,
                                 boolean touRateD, boolean touRateE, boolean netFlow, boolean coincident, boolean harmonic, boolean cumulative);
    
    void sendRfDaArchiveRequest(int serial, String manufacturer, String model);
    void sendLcrArchiveRequest(int serialFrom, int serialTo, String manufacturer, String model);
    public int sendLcrReadArchive(int serialFrom, int serialTo, int days, DRReport drReport) throws IOException;
    void calculationStressTest();
}
