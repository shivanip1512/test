package com.cannontech.dr.rfn.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

public final class UnknownDevices {
    private final List<UnknownDevice> unknownDevices;
    private final int numTotalBeforePaging;
    private final int numUnavailable;
    private final int numActive;
    private final int numInactive;
    private final int numUnreportedNew;
    private final int numUnreportedOld;

    public UnknownDevices(List<UnknownDevice> unknownDevices, int numTotalBeforePaging, int numUnavailable,
                          int numActive, int numInactive, int numUnreportedNew, int numUnreportedOld) {
        this.unknownDevices = ImmutableList.copyOf(unknownDevices);
        this.numTotalBeforePaging = numTotalBeforePaging;
        this.numUnavailable = numUnavailable;
        this.numActive = numActive;
        this.numInactive = numInactive;
        this.numUnreportedNew = numUnreportedNew;
        this.numUnreportedOld = numUnreportedOld;
    }

    public List<UnknownDevice> getUnknownDevices() {
        return unknownDevices;
    }

    public int getNumUnavailable() {
        return numUnavailable;
    }

    public int getNumActive() {
        return numActive;
    }

    public int getNumInactive() {
        return numInactive;
    }

    public int getNumUnreportedNew() {
        return numUnreportedNew;
    }

    public int getNumUnreportedOld() {
        return numUnreportedOld;
    }

    public int getNumTotalBeforePaging() {
        return numTotalBeforePaging;
    }

    public static class Builder {
        private List<UnknownDevice> unknownDevices = new ArrayList<>();
        private int numUnavailable;
        private int numActive;
        private int numInactive;
        private int numUnreportedNew;
        private int numUnreportedOld;
        private int numTotalBeforePaging;

        public void addUnknownDevice(UnknownDevice unknownDevice) {
            unknownDevices.add(unknownDevice);
            switch (unknownDevice.getUnknownStatus()) {
                case ACTIVE: numActive++; break;
                case INACTIVE: numInactive++; break;
                case UNAVAILABLE: numUnavailable++; break;
                case UNREPORTED_NEW: numUnreportedNew++; break;
                case UNREPORTED_OLD: numUnreportedOld++; break;
            }
        }

        public void setNumTotalBeforePaging(int num) {
            numTotalBeforePaging = num;
        }

        public UnknownDevices build() {
            return new UnknownDevices(unknownDevices, numTotalBeforePaging, numUnavailable,
                                      numActive, numInactive, numUnreportedNew, numUnreportedOld);
        }
    }
}