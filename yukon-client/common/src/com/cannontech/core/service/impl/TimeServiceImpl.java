package com.cannontech.core.service.impl;

import org.joda.time.Instant;

import com.cannontech.core.service.TimeService;

public class TimeServiceImpl implements TimeService {
    
    @Override
    public Instant now() {
        return Instant.now();
    }
}
