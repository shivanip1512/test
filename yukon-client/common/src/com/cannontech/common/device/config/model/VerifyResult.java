package com.cannontech.common.device.config.model;

import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.google.common.collect.Lists;

public class VerifyResult {
    
    private Meter meter;
    private ConfigurationBase config;
    private List<String> matching = Lists.newArrayList();
    private List<String> discrepancies = Lists.newArrayList();

    public VerifyResult(Meter meter) {
        this.meter = meter;
    }

    public Meter getMeter() {
        return meter;
    }

    public void setMeter(Meter meter) {
        this.meter = meter;
    }

    public ConfigurationBase getConfig() {
        return config;
    }

    public void setConfig(ConfigurationBase config) {
        this.config = config;
    }

    public List<String> getMatching() {
        return matching;
    }
    
    public List<String> getDiscrepancies() {
        return discrepancies;
    }
    
    public boolean isSynced() {
        return getDiscrepancies().isEmpty();
    }
}