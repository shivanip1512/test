package com.cannontech.common.device.config.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.meter.model.Meter;

public class VerifyResult {
    
    Meter meter;
    ConfigurationBase config;
    List<String> matching;
    List<String> discrepancies;

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
        if(matching == null) {
            matching = new ArrayList<String>();
        }
        return matching;
    }
    
    public void setMatching(List<String> matching) {
        this.matching = matching;
    }

    public List<String> getDiscrepancies() {
        if(discrepancies == null) {
            discrepancies = new ArrayList<String>();
        }
        return discrepancies;
    }
    
    public void setDiscrepancies(List<String> discrepancies) {
        this.discrepancies = discrepancies;
    }
    
    public boolean isSynced() {
        return getDiscrepancies().isEmpty();
    }
}