package com.cannontech.cbc.cyme;

public interface CymeSimulatorService {
    public void handleOpenBank(int bankId);
    public void handleCloseBank(int bankId);
    public void handleTapUp(int regulatorId);
    public void handleTapDown(int regulatorId);
    public void handleScanDevice(int deviceId);
    public void handleRefreshSystem(int deviceId);
}
