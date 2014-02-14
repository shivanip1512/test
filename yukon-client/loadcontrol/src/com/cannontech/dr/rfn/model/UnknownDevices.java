package com.cannontech.dr.rfn.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

public final class UnknownDevices {
    private final List<UnknownDevice> unknownDevices;
    private final int numUnavailable;
    private final int numActive;
    private final int numInactive;
    private final int numUnreportedNew;
    private final int numUnreportedOld;

    public UnknownDevices(List<UnknownDevice> unknownDevices, int numUnavailable, int numActive, 
                          int numInactive, int numUnreportedNew, int numUnreportedOld) {
        this.unknownDevices = ImmutableList.copyOf(unknownDevices);
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
}