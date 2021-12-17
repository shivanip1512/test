package com.cannontech.common.rfn.simulation.service;

import com.cannontech.common.rfn.simulation.SimulatedRfnDeviceDeletionSettings;
import com.cannontech.simulators.AutoStartableSimulator;

public interface RfnDeviceDeletionSimulatorService extends AutoStartableSimulator {

    @Override
    void startSimulatorWithCurrentSettings();

    boolean startRfnDeviceDeletionReply(SimulatedRfnDeviceDeletionSettings settings);

    void stopRfnDeviceDeletionReply();

    public boolean isRfnDeviceDeletionReplyActive();

    public boolean isRfnDeviceDeletionReplyStopping();

    public SimulatedRfnDeviceDeletionSettings getDeletionSettings();
}
