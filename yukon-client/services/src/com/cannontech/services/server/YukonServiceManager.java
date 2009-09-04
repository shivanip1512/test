package com.cannontech.services.server;

public interface YukonServiceManager {
    public void loadCustomServices();
    public void shutdownServiceManager();
    public void waitForShutdown();
}
