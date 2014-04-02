package com.cannontech.cc.model;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.cannontech.core.dao.support.Identifiable;


public class EconomicEventPricing implements Identifiable{
    private Date creationTime;
    private EconomicEvent event;
    private Integer id;
    private Integer revision;
    private Map<Integer, EconomicEventPricingWindow> windows = 
        new HashMap<Integer, EconomicEventPricingWindow>();
    
    public EconomicEventPricing() {
    }
    
    public Date getCreationTime() {
        return creationTime;
    }

    public EconomicEvent getEvent() {
        return event;
    }

    public Integer getId() {
        return id;
    }

    public Integer getRevision() {
        return revision;
    }
    
    public Integer getNumberOfWindows() {
        return windows.size();
    }
    
    public Integer getAffectedDurationMinutes() {
        return getNumberOfWindows() * getEvent().getWindowLengthMinutes();
    }
    
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public void setEvent(EconomicEvent event) {
        this.event = event;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }
    
    public Map<Integer, EconomicEventPricingWindow> getWindows() {
        return windows;
    }

    public void setWindows(Iterable<EconomicEventPricingWindow> iterable) {
        for (EconomicEventPricingWindow window : iterable) {
            windows.put(window.getOffset(), window);
        }
    }
    
    public int getFirstAffectedWindowOffset() {
        return Collections.min(windows.keySet());
    }
    
    public EconomicEventPricingWindow getFirstAffectedWindow() {
        return windows.get(getFirstAffectedWindowOffset());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EconomicEventPricing == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        EconomicEventPricing rhs = (EconomicEventPricing) obj;
        return new EqualsBuilder().append(event, rhs.event).append(revision,rhs.revision).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(event).append(revision).toHashCode();
    }
    
    @Override
    public String toString() {
        return "EconomicEventPricing [" + id + "]@" + Integer.toHexString(System.identityHashCode(this));
    }

    public void addWindow(EconomicEventPricingWindow window) {
        window.setPricingRevision(this);
        windows.put(window.getOffset(), window);
    }

    public EconomicEventPricing getPrevious() {
        if (revision <= 1) {
            throw new IllegalArgumentException("This is the initial revision.");
        }
        int previousNumber = revision - 1;
        EconomicEventPricing previousRev = getEvent().getRevisions().get(previousNumber);
        if (previousRev == null) {
            throw new IllegalArgumentException("Revision " + previousNumber + " does not exist in " + this);
        }
        return previousRev;
    }
}
