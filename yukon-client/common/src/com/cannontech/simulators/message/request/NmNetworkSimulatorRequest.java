package com.cannontech.simulators.message.request;

import com.cannontech.common.rfn.simulation.SimulatedNmMappingSettings;
import com.cannontech.simulators.SimulatorType;

public class NmNetworkSimulatorRequest implements SimulatorRequest {

    public enum Action {
        START, STOP, UPDATE_SETTINGS, GET_SETTINGS, SETUP;
    }
    
    private SimulatedNmMappingSettings settings;
    private Action action;
    

    public NmNetworkSimulatorRequest(SimulatedNmMappingSettings settings, Action action) {
        this.settings = settings;
        this.action = action;
    }
    
    public NmNetworkSimulatorRequest(Action action) {
        this(null, action);
    }

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.RFN_NETWORK;
    }

    public SimulatedNmMappingSettings getSettings() {
        return settings;
    }

    public Action getAction() {
        return action;
    }
}
