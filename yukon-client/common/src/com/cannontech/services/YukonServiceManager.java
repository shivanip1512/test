package com.cannontech.services;

public interface YukonServiceManager {
    public void loadServices();
    public void shutdownServiceManager();
    public void waitForShutdown();
}
