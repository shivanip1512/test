package com.cannontech.cbc.cyme;

import com.cannontech.common.pao.YukonPao;

public interface CymeSimulatorService {
	public void startLoadProfileSimulation(YukonPao substationBusPao);
	public void stopLoadProfileSimulation(YukonPao substationBusPao);
	
    public void handleOpenBank(int bankId);
    public void handleCloseBank(int bankId);
    public void handleTapUp(int regulatorId);
    public void handleTapDown(int regulatorId);
    public void handleScanDevice(int deviceId);
    public void handleRefreshSystem(int deviceId);
}
