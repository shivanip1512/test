package com.cannontech.web.dr.eatonCloud.model;

import org.joda.time.Instant;

public class EatonCloudSecretExpiryTime {
    
    // secret 1
    private Instant expiryTime1;
    // secret 2
    private Instant expiryTime2;
    
    public EatonCloudSecretExpiryTime(Instant expiryTime1, Instant expiryTime2) {
        this.expiryTime1 = expiryTime1;
        this.expiryTime2 = expiryTime2;
    }

    public Instant getExpiryTime1() {
        return expiryTime1;
    }

    public Instant getExpiryTime2() {
        return expiryTime2;
    }
    
    @Override
    public String toString() {
        return "EatonCloudSecretExpiryTime [expiryTime1=" + expiryTime1.toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS")
                + ", expiryTime2=" + expiryTime2.toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS") + "]";
    }

}
