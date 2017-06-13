package com.cannontech.simulators.ivvc;

public interface IvvcSimulatorService {

    /**
     * Starts the simulator.
     * @return true if the simulator started successfully, or false if it was already running.
     */
    public boolean start();

    /**
     * Stops the simulator. Although this method will return immediately, the simulator may continue
     * running for a
     * short period afterward. It will complete its main loop before terminating. The
     * <code>isRunning()</code> method
     * can be used to determine if the simulator has stopped.
     */
    public void stop();

    /**
     * @return true if the simulator is running, false if it is stopped.
     */
    public boolean isRunning();

}
