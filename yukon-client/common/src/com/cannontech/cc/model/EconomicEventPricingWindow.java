package com.cannontech.cc.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.support.Identifiable;

public class EconomicEventPricingWindow implements Identifiable, Comparable<EconomicEventPricingWindow> {
    private BigDecimal energyPrice = new BigDecimal(0);
    private Integer id;
    private Integer offset = 0;
    private EconomicEventPricing pricingRevision;

    public EconomicEventPricingWindow() {
    }

    public BigDecimal getEnergyPrice() {
        return energyPrice;
    }

    public Integer getId() {
        return id;
    }

    public Integer getOffset() {
        return offset;
    }

    public EconomicEventPricing getPricingRevision() {
        return pricingRevision;
    }

    public void setEnergyPrice(BigDecimal energyPrice) {
        this.energyPrice = energyPrice;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public void setPricingRevision(EconomicEventPricing pricingRevision) {
        this.pricingRevision = pricingRevision;
    }
    
    public Date getStartTime() {
        EconomicEvent event = getPricingRevision().getEvent();
        int totalOffset = getOffset() * event.getWindowLengthMinutes();
        return TimeUtil.addMinutes(event.getStartTime(), totalOffset);
    }

    public Date getStopTime() {
    	EconomicEvent event = getPricingRevision().getEvent();
        int windowLength = event.getWindowLengthMinutes();
        return TimeUtil.addMinutes(getStartTime(), windowLength);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EconomicEventPricingWindow == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        EconomicEventPricingWindow rhs = (EconomicEventPricingWindow) obj;
        return new EqualsBuilder().append(pricingRevision, rhs.pricingRevision).append(offset, rhs.offset).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(pricingRevision).append(offset).toHashCode();
    }
    
    @Override
    public String toString() {
        return "EconomicEventPricingWindow [" + id + "]@" + Integer.toHexString(System.identityHashCode(this));
    }
    
    public int compareTo(EconomicEventPricingWindow o) {
        return this.offset.compareTo(o.offset);
    }
}
