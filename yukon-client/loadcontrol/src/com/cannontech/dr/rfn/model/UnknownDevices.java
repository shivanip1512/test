package com.cannontech.dr.rfn.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

public final class UnknownDevices {
    private final List<UnknownDevice> unknownDevices;
    private final int numUnavailable;
    private final int numAvailable;
    private final int numUnreportedNew;
    private final int numUnreportedOld;

    public UnknownDevices(List<UnknownDevice> unknownDevices, int numUnavailable,
                          int numAvailable, int numUnreportedNew, int numUnreportedOld) {
        this.unknownDevices = ImmutableList.copyOf(unknownDevices);
        this.numUnavailable = numUnavailable;
        this.numAvailable = numAvailable;
        this.numUnreportedNew = numUnreportedNew;
        this.numUnreportedOld = numUnreportedOld;
    }

    public List<UnknownDevice> getUnknownDevices() {
        return unknownDevices;
    }

    public int getNumUnavailable() {
        return numUnavailable;
    }

    public int getNumAvailable() {
        return numAvailable;
    }

    public int getNumUnreportedNew() {
        return numUnreportedNew;
    }

    public int getNumUnreportedOld() {
        return numUnreportedOld;
    }

    public int getNumTotalBeforePaging() {
        return numUnavailable + numAvailable + numUnreportedNew + numUnreportedOld;
    }

    public static class Builder {
        private List<UnknownDevice> unknownDevices = new ArrayList<>();
        private int numUnavailable;
        private int numAvailable;
        private int numUnreportedNew;
        private int numUnreportedOld;

        public void addUnknownDevice(UnknownDevice unknownDevice) {
            unknownDevices.add(unknownDevice);
        }

        public void setCountForStatus(UnknownStatus status, int num) {
            switch (status) {
                case AVAILABLE: numAvailable = num; break;
                case UNAVAILABLE: numUnavailable = num; break;
                case UNREPORTED_NEW: numUnreportedNew = num; break;
                case UNREPORTED_OLD: numUnreportedOld = num; break;
            }
        }

        public UnknownDevices build() {
            return new UnknownDevices(unknownDevices, numUnavailable,
                                      numAvailable, numUnreportedNew, numUnreportedOld);
        }
    }
}