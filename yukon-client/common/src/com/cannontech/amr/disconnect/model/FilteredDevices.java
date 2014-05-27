package com.cannontech.amr.disconnect.model;

import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.device.model.SimpleDevice;
import com.google.common.collect.Sets;

public class FilteredDevices {
    
    private final Set<SimpleDevice> valid = new HashSet<>();
    private final Set<SimpleDevice> notConfigured = new HashSet<>();

    public Set<SimpleDevice> getValid() {
        return valid;
    }

    public void addValid(Iterable<SimpleDevice> meters) {
        valid.addAll(Sets.newHashSet(meters));
    }

    public Set<SimpleDevice> getNotConfigured() {
        return notConfigured;
    }

    public void addNotConfigured(Iterable<SimpleDevice> meters) {
        notConfigured.addAll(Sets.newHashSet(meters));
    }
}
