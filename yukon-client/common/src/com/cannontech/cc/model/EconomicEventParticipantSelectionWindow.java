package com.cannontech.cc.model;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.cannontech.core.dao.support.Identifiable;

public class EconomicEventParticipantSelectionWindow implements Identifiable, Comparable<EconomicEventParticipantSelectionWindow>{
    private BigDecimal energyToBuy;
    private Integer id;
    private EconomicEventParticipantSelection selection;
    private EconomicEventPricingWindow window;
    
    public EconomicEventParticipantSelectionWindow() {
        super();
    }

    public BigDecimal getEnergyToBuy() {
        return energyToBuy;
    }

    public Integer getId() {
        return id;
    }

    public EconomicEventParticipantSelection getSelection() {
        return selection;
    }

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
