package com.cannontech.dr.assetavailability;

import java.util.Date;

import org.joda.time.Instant;

public final class ApplianceRuntime {
    private final Instant readDate;
    private final Double runtime;
    
    public static final ApplianceRuntime NONE = new ApplianceRuntime((Instant)null, null);
    
    public ApplianceRuntime(Instant readDate, Double runtime) {
        this.readDate = readDate;
        this.runtime = runtime;
    }
    
    public ApplianceRuntime(Date readDate, Double runtime) {
        this(new Instant(readDate), runtime);
    }

    public Instant getReadDate() {
        return readDate;
    }

    public Double getRuntimeMinutes() {
        return runtime;
    }
}
