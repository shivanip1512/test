package com.cannontech.simulators.message.response;

import com.cannontech.common.rfn.simulation.SimulatedRfnDeviceDeletionSettings;

public class RfnDeviceDeletionSimulatorStatusResponse extends SimulatorResponseBase {

    private SimulatedRfnDeviceDeletionSettings deletionSettings;
    private boolean deviceDeletionReplyActive;

    public SimulatedRfnDeviceDeletionSettings getDeletionSettings() {
        return deletionSettings;
    }

    public void setDeletionSettings(SimulatedRfnDeviceDeletionSettings deletionSettings) {
        this.deletionSettings = deletionSettings;
    }

    public boolean isDeviceDeletionReplyActive() {
        return deviceDeletionReplyActive;
    }

    public void setDeviceDeletionReplyActive(boolean deviceDeletionReplyActive) {
        this.deviceDeletionReplyActive = deviceDeletionReplyActive;
    }
}
