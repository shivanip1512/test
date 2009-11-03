package com.cannontech.services;

public interface YukonServiceManager {
    public void loadCustomServices();
    public void shutdownServiceManager();
    public void waitForShutdown();
}
