package com.cannontech.dr.rfn.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

public final class UnknownDevices {
    private final List<UnknownDevice> unknownDevices;
    private final int numCommunicating;
    private final int numNotCommunicating;
    private final int numNewInstallNotCommunicating;

    public UnknownDevices(List<UnknownDevice> unknownDevices, int numCommunicating,
                          int numNotCommunicating, int numNewInstallNotCommunicating) {
        this.unknownDevices = ImmutableList.copyOf(unknownDevices);
        this.numCommunicating = numCommunicating;
        this.numNotCommunicating = numNotCommunicating;
        this.numNewInstallNotCommunicating = numNewInstallNotCommunicating;
    }

    public List<UnknownDevice> getUnknownDevices() {
        return unknownDevices;
    }

    public int getNumCommunicating() {
        return numCommunicating;
    }

    public int getNumNotCommunicating() {
        return numNotCommunicating;
    }

    public int getNumNewInstallNotCommunicating() {
        return numNewInstallNotCommunicating;
    }

    public int getNumTotalBeforePaging() {
        return numCommunicating + numNotCommunicating + numNewInstallNotCommunicating;
    }

    public static class Builder {
        private List<UnknownDevice> unknownDevices = new ArrayList<>();
        private int numCommunicating;
        private int numNotCommunicating;
        private int numNewInstallNotCommunicating;

        public void addUnknownDevice(UnknownDevice unknownDevice) {
            unknownDevices.add(unknownDevice);
        }

        public void setCountForStatus(UnknownStatus status, int num) {
            switch (status) {
                case COMMUNICATING: numCommunicating = num; break;
                case NOT_COMMUNICATING: numNotCommunicating = num; break;
                case NEW_INSTALL_NOT_COMMUNICATING: numNewInstallNotCommunicating = num; break;
            }
        }

        public UnknownDevices build() {
            return new UnknownDevices(unknownDevices, numCommunicating,
                                      numNotCommunicating, numNewInstallNotCommunicating);
        }
    }
}