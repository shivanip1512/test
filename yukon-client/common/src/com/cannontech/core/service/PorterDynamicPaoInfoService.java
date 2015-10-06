package com.cannontech.core.service;

import java.util.function.Consumer;

import org.joda.time.Duration;
import org.joda.time.Instant;

public interface PorterDynamicPaoInfoService {
    
    class VoltageProfileDetails {
        public Instant enabledUntil;
        public Duration profileInterval;
    }
    
    void getVoltageProfileDetails(int paoId, Consumer<VoltageProfileDetails> callback);
    VoltageProfileDetails getVoltageProfileDetails(int paoId);
}
