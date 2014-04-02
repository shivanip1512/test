package com.cannontech.cc.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.cannontech.core.dao.support.Identifiable;

public class EconomicEventParticipantSelection implements Identifiable{
    public enum SelectionState {DEFAULT, MANUAL};
    private String connectionAudit;
    private Integer id;
    private EconomicEventParticipant participant;
    private EconomicEventPricing pricingRevision;
    private Date submitTime;
    private SelectionState state;
    private Set<EconomicEventParticipantSelectionWindow> selectionWindows = 
        new HashSet<EconomicEventParticipantSelectionWindow>();

    public EconomicEventParticipantSelection() {
    }
    
    public String getConnectionAudit() {
        return connectionAudit;
    }
    
    public Integer getId() {
        return id;
    }

    public EconomicEventParticipant getParticipant() {
        return participant;
    }

    public EconomicEventPricing getPricingRevision() {
        return pricingRevision;
    }

    public Date getSubmitTime() {
        return submitTime;
    }
    
    public Set<EconomicEventParticipantSelectionWindow> getSelectionWindows() {
        return selectionWindows;
    }
    
    public List<EconomicEventParticipantSelectionWindow> getSortedSelectionWindows() {
        ArrayList<EconomicEventParticipantSelectionWindow> temp = 
            new ArrayList<EconomicEventParticipantSelectionWindow>(selectionWindows);
        Collections.sort(temp); // there aren't many and they'll usually be sorted
        return temp;
    }
    
    public void setSelectionWindows(Collection<EconomicEventParticipantSelectionWindow> selectionWindowCol) {
            selectionWindows.addAll(selectionWindowCol);
    }
    
    public EconomicEventParticipantSelectionWindow 
    getSelectionWindow(EconomicEventPricingWindow pricingWindow) {
        for (EconomicEventParticipantSelectionWindow selectionWindow : selectionWindows) {
            if (selectionWindow.getWindow().equals(pricingWindow)) {
                return selectionWindow;
            }
        }
        throw new IllegalArgumentException(this + " does not have data for revision " + pricingWindow);
    }

    public void setConnectionAudit(String connectionAudit) {
        this.connectionAudit = connectionAudit;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setParticipant(EconomicEventParticipant participant) {
        this.participant = participant;
    }

    public void setPricingRevision(EconomicEventPricing pricingRevision) {
        this.pricingRevision = pricingRevision;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public void addWindow(EconomicEventParticipantSelectionWindow nextSelectionWindow) {
        nextSelectionWindow.setSelection(this);
        selectionWindows.add(nextSelectionWindow);
    }

    public SelectionState getState() {
        return state;
    }

    public void setState(SelectionState state) {
        this.state = state;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EconomicEventParticipantSelection == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        EconomicEventParticipantSelection rhs = (EconomicEventParticipantSelection) obj;
        return new EqualsBuilder().append(pricingRevision, rhs.pricingRevision).append(participant, rhs.participant).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(pricingRevision).append(participant).toHashCode();
    }
}
