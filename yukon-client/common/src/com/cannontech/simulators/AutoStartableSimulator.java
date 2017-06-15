package com.cannontech.simulators;

/**
 * Interface that exists to provide an abstraction for simulator services.
 */
public interface AutoStartableSimulator {
    
    /**
     * Get this simulator's settings from the database and return them as SimulatorSettingsInterface
     * settings and then start the simulator.
     */
    void startSimulatorWithCurrentSettings();
}
