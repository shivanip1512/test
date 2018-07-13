package com.cannontech.dr.rfn.service;

import com.cannontech.dr.rfn.model.RfnDataSimulatorStatus;
import com.cannontech.dr.rfn.model.RfnNetworkManagerSimulatorSettings;

public interface RfnNetworkManagerSimulatorService {

    public void startSimulator(RfnNetworkManagerSimulatorSettings settings);
    
    public void stopSimulator();

    boolean startMeterReadReply(RfnNetworkManagerSimulatorSettings settings);

    void stopMeterReadReply();

    boolean startMeterDisconnectReply(RfnNetworkManagerSimulatorSettings settings);

    void stopMeterDisconnectReply();
    
    void startSimulatorWithCurrentSettings();
    
    public RfnNetworkManagerSimulatorSettings getCurrentSettings();

    public RfnDataSimulatorStatus getStatus();
}
