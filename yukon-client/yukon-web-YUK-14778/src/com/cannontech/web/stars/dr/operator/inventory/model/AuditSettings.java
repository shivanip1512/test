package com.cannontech.web.stars.dr.operator.inventory.model;

import org.joda.time.Duration;
import org.joda.time.Instant;

public class AuditSettings {
    
    private Instant from = new Instant().minus(Duration.standardDays(8));
    private Instant to = new Instant().minus(Duration.standardHours(24));
    
    public Instant getFrom() {
        return from;
    }
    
    public void setFrom(Instant from) {
        this.from = from;
    }
    
    public Instant getTo() {
        return to;
    }
    
    public void setTo(Instant to) {
        this.to = to;
    }
    
}