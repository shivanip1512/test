package com.cannontech.common.device.config.model;

import java.util.List;

import com.cannontech.amr.meter.model.YukonMeter;
import com.google.common.collect.Lists;

public class VerifyResult {
    private YukonMeter meter;
    private LightDeviceConfiguration config;
    private List<String> matching = Lists.newArrayList();
    private List<String> discrepancies = Lists.newArrayList();

    public VerifyResult(YukonMeter meter) {
        this.meter = meter;
    }

    public YukonMeter getMeter() {
        return meter;
    }

    public void setMeter(YukonMeter meter) {
        this.meter = meter;
    }

    public LightDeviceConfiguration getConfig() {
        return config;
    }

    public void setConfig(LightDeviceConfiguration config) {
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