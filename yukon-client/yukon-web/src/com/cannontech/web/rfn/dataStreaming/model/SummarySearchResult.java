package com.cannontech.web.rfn.dataStreaming.model;

import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;

public class SummarySearchResult {
    private RfnDevice meter;
    private RfnGateway gateway;
    private DataStreamingConfig config;
    
    public RfnDevice getMeter() {
        return meter;
    }
    public void setMeter(RfnDevice meter) {
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
