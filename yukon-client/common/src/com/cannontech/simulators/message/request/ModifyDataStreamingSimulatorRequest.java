package com.cannontech.simulators.message.request;

import com.cannontech.common.rfn.simulation.SimulatedDataStreamingSettings;
import com.cannontech.simulators.SimulatorType;

/**
 * Request to modify the state of the data streaming simulator. If stopSimulator is set to true, the simulator will
 * stop (ignoring any other settings in the message). Otherwise, the simulator will take the specified settings and
 * start.
 */
public class ModifyDataStreamingSimulatorRequest implements SimulatorRequest {
    private boolean stopSimulator;
    private SimulatedDataStreamingSettings settings;
    
    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.DATA_STREAMING;
    }
    
    public void setStopSimulator(boolean stopSimulator) {
        this.stopSimulator = stopSimulator;
    }
    
    public boolean isStopSimulator() {
        return stopSimulator;
    }
    
    public void setSettings(SimulatedDataStreamingSettings settings) {
        this.settings = settings;
    }
    
    public SimulatedDataStreamingSettings getSettings() {
        return settings;
    }
}
