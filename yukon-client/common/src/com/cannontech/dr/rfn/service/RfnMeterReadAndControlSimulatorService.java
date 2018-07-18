package com.cannontech.dr.rfn.service;

import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.RfnMeterReadAndControlDisconnectSimulatorSettings;
import com.cannontech.dr.rfn.model.RfnMeterReadAndControlReadSimulatorSettings;

public interface RfnMeterReadAndControlSimulatorService {

    public void startSimulator(RfnMeterReadAndControlDisconnectSimulatorSettings settings);
    
    public void stopSimulator();

    boolean startMeterReadReply(RfnMeterReadAndControlReadSimulatorSettings settings);

    void stopMeterReadReply();

    boolean startMeterDisconnectReply(RfnMeterReadAndControlDisconnectSimulatorSettings settings);

    void stopMeterDisconnectReply();
    
    void startSimulatorWithCurrentSettings();
    
    public RfnMeterReadAndControlDisconnectSimulatorSettings getCurrentSettings();

    public RfnDataSimulatorStatus getStatus();

    public boolean isMeterReadReplyActive();
    
    public boolean isMeterDisconnectReplyActive();
    
    public boolean isMeterReadReplyStopping();
    
    public boolean isMeterDisconnectReplyStopping();
    
    public RfnMeterReadAndControlReadSimulatorSettings getCurrentReadSettings();
    
    public RfnMeterReadAndControlDisconnectSimulatorSettings getCurrentDisconnectSettings();
}
