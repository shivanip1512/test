package com.cannontech.cc.model;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "CCurtEEPricing", 
       uniqueConstraints=@UniqueConstraint(columnNames={"CCurtEconomicEventId","revision"}))
public class EconomicEventPricing {
    private Date creationTime;
    private EconomicEvent event;
    private Integer id;
    private Integer revision;
    private Map<Integer, EconomicEventPricingWindow> windows = 
        new HashMap<Integer, EconomicEventPricingWindow>();
    
    public EconomicEventPricing() {
    }
    
    @Column(nullable=false)
    public Date getCreationTime() {
        return creationTime;
    }

    @ManyToOne
    @JoinColumn(name="CCurtEconomicEventId", nullable=false)
    public EconomicEvent getEvent() {
        return event;
    }

    @Id
    @GenericGenerator(name="yukon", strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @GeneratedValue(generator="yukon")
    @Column(name="CCurtEEPricingId")
    public Integer getId() {
        return id;
    }

    @Column(nullable=false)
    public Integer getRevision() {
        return revision;
    }
    
    @Transient
    public Integer getNumberOfWindows() {
        return windows.size();
    }
    
    @Transient
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
    
    @OneToMany(mappedBy="pricingRevision", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @MapKey(name="offset")
    @BatchSize(size=100)
    public Map<Integer, EconomicEventPricingWindow> getWindows() {
        return windows;
    }

    public void setWindows(Map<Integer, EconomicEventPricingWindow> windows) {
        this.windows = windows;
    }
    
    @Transient
    public int getFirstAffectedWindowOffset() {
        return Collections.min(windows.keySet());
    }
    
    @Transient
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

    @Transient
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
