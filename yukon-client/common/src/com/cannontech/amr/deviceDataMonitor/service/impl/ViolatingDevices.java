package com.cannontech.amr.deviceDataMonitor.service.impl;

import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.device.model.SimpleDevice;

public final class ViolatingDevices {
    private final Set<SimpleDevice> violatingDevices;
    private final Set<SimpleDevice> alarmsOnlyViolatingDevices;
    
    public ViolatingDevices() {
        violatingDevices = new HashSet<>();
        alarmsOnlyViolatingDevices = new HashSet<>();
    }
    
    public ViolatingDevices(Set<SimpleDevice> violatingDevices, Set<SimpleDevice> alarmsOnlyViolatingDevices) {
        this.violatingDevices = violatingDevices;
        this.alarmsOnlyViolatingDevices = alarmsOnlyViolatingDevices;
    }
    
    public Set<SimpleDevice> getViolatingDevices() {
        return violatingDevices;
    }

    public Set<SimpleDevice> getAlarmsOnlyViolatingDevices() {
        return alarmsOnlyViolatingDevices;
    }
}
