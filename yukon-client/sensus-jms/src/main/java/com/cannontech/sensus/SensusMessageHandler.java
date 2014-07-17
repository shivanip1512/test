package com.cannontech.sensus;



public interface SensusMessageHandler {
    public void processMessage(int repId, int appCode, boolean isSequenceNew, char[] message);
}
