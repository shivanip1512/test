package com.cannontech.simulators.ivvc;

import com.cannontech.ivvc.model.IvvcSimulatorSettings;
import com.cannontech.simulators.AutoStartableSimulator;

public interface IvvcSimulatorService extends AutoStartableSimulator {

    /**
     * Starts the simulator.
     * @return true if the simulator started successfully, or false if it was already running.
     */
    boolean start(IvvcSimulatorSettings settings);

    /**
     * Stops the simulator. Although this method will return immediately, the simulator may continue
     * running for a
     * short period afterward. It will complete its main loop before terminating. The
     * <code>isRunning()</code> method
     * can be used to determine if the simulator has stopped.
     */
    void stop();

    /**
     * @return true if the simulator is running, false if it is stopped.
     */
    boolean isRunning();

    /**
     * Gets the IVVC simulator settings from the database.
     */
    IvvcSimulatorSettings getCurrentSettings();
}
