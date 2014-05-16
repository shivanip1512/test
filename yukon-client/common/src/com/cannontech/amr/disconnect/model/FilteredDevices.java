package com.cannontech.amr.disconnect.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.device.model.SimpleDevice;
import com.google.common.collect.Lists;

public class FilteredDevices {
    
    private List<SimpleDevice> valid = new ArrayList<>();
    private List<SimpleDevice> notConfigured = new ArrayList<>();

    public List<SimpleDevice> getValid() {
        return valid;
    }

    public void addValid(Iterable<SimpleDevice> meters) {
        valid.addAll(Lists.newArrayList(meters));
    }

    public List<SimpleDevice> getNotConfigured() {
        return notConfigured;
    }

    public void addNotConfigured(Iterable<SimpleDevice> meters) {
        notConfigured.addAll(Lists.newArrayList(meters));
    }
}
