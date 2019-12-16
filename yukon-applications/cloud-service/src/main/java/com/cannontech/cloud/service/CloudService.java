package com.cannontech.cloud.service;

/**
 * This is base interface, any cloud service should extend this.
 * Currently as only Azure is supported, so this has only one child
 */
public interface CloudService {

    /**
     * Create connection to a cloud service
     */
    public abstract void createConnection();

    /**
     * Checks if a service should be started or not.
     * This helps on starting/stopping individual service
     */
    public abstract boolean shouldStart();

    /**
     * Starts a individual service, what a service has to do starts from here.
     */
    public abstract void start();

    /**
     * It starts all the cloud services if they have to be started.
     */
    public default void startServices() {
        // Start all cloud services
        if (shouldStart()) {
            start();
        }
    }

}
