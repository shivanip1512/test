package com.cannontech.web.stars.dr.operator.inventory.model;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.user.YukonUserContext;

public class AuditSettings {
    
    private YukonUserContext context;
    private InventoryCollection collection;
    private Instant from = new Instant().minus(Duration.standardDays(8));
    private Instant to = new Instant().minus(Duration.standardHours(24));
    
    public YukonUserContext getContext() {
        return context;
    }
    
    public void setContext(YukonUserContext context) {
        this.context = context;
    }
    
    public InventoryCollection getCollection() {
        return collection;
    }
    
    public void setCollection(InventoryCollection collection) {
        this.collection = collection;
    }
    
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