package com.cannontech.sensus;


public interface SensusMessageHandler {
    public void processMessage(int repId, int appCode, char[] message);
}
