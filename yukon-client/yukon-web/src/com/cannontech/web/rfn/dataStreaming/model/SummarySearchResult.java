package com.cannontech.web.rfn.dataStreaming.model;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.rfn.model.RfnGateway;

public class SummarySearchResult {
    private YukonMeter meter;
    private RfnGateway gateway;
    private DataStreamingConfig config;
    
    public YukonMeter getMeter() {
        return meter;
    }
    public void setMeter(YukonMeter meter) {
        this.meter = meter;
    }
    public RfnGateway getGateway() {
        return gateway;
    }
    public void setGateway(RfnGateway gateway) {
        this.gateway = gateway;
    }
    public DataStreamingConfig getConfig() {
        return config;
    }
    public void setConfig(DataStreamingConfig config) {
        this.config = config;
    }
    
}
