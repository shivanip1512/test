package com.cannontech.cc.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "CCurtEEParticipantWindow",
       uniqueConstraints=@UniqueConstraint(columnNames={"CCurtEEParticipantSelectionId","CCurtEEPricingWindowId"}))
public class EconomicEventParticipantSelectionWindow implements Comparable<EconomicEventParticipantSelectionWindow>{
    private BigDecimal energyToBuy;
    private Integer id;
    private EconomicEventParticipantSelection selection;
    private EconomicEventPricingWindow window;
    
    public EconomicEventParticipantSelectionWindow() {
        super();
    }

    @Column(nullable=false)
    public BigDecimal getEnergyToBuy() {
        return energyToBuy;
    }

    @Id
    @GenericGenerator(name="yukon", strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @GeneratedValue(generator="yukon")
    @Column(name = "CCurtEEParticipantWindowId")
    public Integer getId() {
        return id;
    }

    @ManyToOne
    @JoinColumn(name="CCurtEEParticipantSelectionId", nullable=false)
    public EconomicEventParticipantSelection getSelection() {
        return selection;
    }

    @ManyToOne
    @JoinColumn(name="CCurtEEPricingWindowId", nullable=false)
    public EconomicEventPricingWindow getWindow() {
        return window;
    }

    public void setEnergyToBuy(BigDecimal energyToBuy) {
        this.energyToBuy = energyToBuy;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSelection(EconomicEventParticipantSelection selection) {
        this.selection = selection;
    }

    public void setWindow(EconomicEventPricingWindow window) {
        this.window = window;
    }

    public int compareTo(EconomicEventParticipantSelectionWindow o) {
        return window.compareTo(o.window);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EconomicEventParticipantSelectionWindow == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        EconomicEventParticipantSelectionWindow rhs = (EconomicEventParticipantSelectionWindow) obj;
        return new EqualsBuilder().append(selection, rhs.selection).append(window, rhs.window).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(selection).append(window).toHashCode();
    }
    @Override
    public String toString() {
        return "EconomicEventParticipantSelectionWindow [" + id + "]";
    }


}
