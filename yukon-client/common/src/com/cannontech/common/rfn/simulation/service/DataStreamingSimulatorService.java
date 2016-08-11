package com.cannontech.common.rfn.simulation.service;

import com.cannontech.common.rfn.simulation.SimulatedDataStreamingSettings;

/**
 * This service simulates Network Manager for the purposes of Data Streaming. When running, it pulls data streaming
 * related messages from queues and replies to them, just like Network Manager would.
 */
public interface DataStreamingSimulatorService {
    
    /**
     * Updates the settings for the simulator, changing how it replies to messages.
     */
    public void setSettings(SimulatedDataStreamingSettings settings);
    
    /**
     * Starts the simulator.
     * @return true if the simulator started successfully, or false if it was already running.
     */
    public boolean start();
    
    /**
     * Stops the simulator. Although this method will return immediately, the simulator may continue running for a
     * short period afterward. It will complete its main loop before terminating. The <code>isRunning()</code> method
     * can be used to determine if the simulator has stopped.
     */
    public void stop();
    
    /**
     * Get the settings the simulator is currently using to determine how to reply to requests.
     */
    public SimulatedDataStreamingSettings getSettings();
    
    /**
     * @return true if the simulator is running, false if it is stopped.
     */
    public boolean isRunning();
}
