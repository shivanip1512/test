package com.cannontech.core.service;

import java.util.function.Consumer;

import org.joda.time.Duration;
import org.joda.time.Instant;

public interface PorterDynamicPaoInfoService {
    
    public class VoltageProfileDetails {
        public Instant enabledUntil;
        public Duration profileInterval;
    }
    
    public void getVoltageProfileDetails(int paoId, Consumer<VoltageProfileDetails> callback);
    public VoltageProfileDetails getVoltageProfileDetails(int paoId);
}
