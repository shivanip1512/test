package com.cannontech.cc.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import com.cannontech.common.util.TimeUtil;

@Entity
@Table(name = "CCurtEEPricingWindow",
       uniqueConstraints=@UniqueConstraint(columnNames={"CCurtEEPricingId","offset"}))
public class EconomicEventPricingWindow implements Comparable<EconomicEventPricingWindow> {
    private BigDecimal energyPrice = new BigDecimal(0);
    private Integer id;
    private Integer offset = 0;
    private EconomicEventPricing pricingRevision;

    public EconomicEventPricingWindow() {
    }

    @Column(nullable=false)
    public BigDecimal getEnergyPrice() {
        return energyPrice;
    }

    @Id
    @GenericGenerator(name="yukon", strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @GeneratedValue(generator="yukon")
    @Column(name="CCurtEEPricingWindowId")
    public Integer getId() {
        return id;
    }

    @Column(nullable=false)
    public Integer getOffset() {
        return offset;
    }

    @ManyToOne
    @JoinColumn(name="CCurtEEPricingId", nullable=false)
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
    
    @Transient
    public Date getStartTime() {
        EconomicEvent event = getPricingRevision().getEvent();
        int totalOffset = getOffset() * event.getWindowLengthMinutes();
        return TimeUtil.addMinutes(event.getStartTime(), totalOffset);
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
        return new EqualsBuilder().append(id, rhs.id).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }
    
    @Override
    public String toString() {
        return "EconomicEventPricingWindow [" + id + "]@" + Integer.toHexString(System.identityHashCode(this));
    }
    
    public int compareTo(EconomicEventPricingWindow o) {
        return this.offset.compareTo(o.offset);
    }

}
