package com.cannontech.cc.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.cannontech.core.dao.support.Identifiable;
import com.cannontech.database.data.notification.NotifMap;

public class EconomicEventParticipant implements Identifiable, BaseParticipant {
    private CICustomerStub customer;
    private EconomicEvent event;
    private Integer id;
    private NotifMap notifMap = new NotifMap();
    private List<EconomicEventParticipantSelection> selections =
        new ArrayList<EconomicEventParticipantSelection>();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EconomicEventParticipant == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        EconomicEventParticipant rhs = (EconomicEventParticipant) obj;
        return new EqualsBuilder().append(event, rhs.event).append(customer, rhs.customer).isEquals();
    }

    public CICustomerStub getCustomer() {
        return customer;
    }

    public EconomicEvent getEvent() {
        return event;
    }
    
    public Integer getId() {
        return id;
    }

    public String getNotifAttribs() {
        return notifMap.getAttribs();
    }

    public NotifMap getNotifMap() {
        return notifMap;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(event).append(customer).toHashCode();
    }

    public void setCustomer(CICustomerStub customer) {
        this.customer = customer;
    }
    
    public void setEvent(EconomicEvent event) {
        this.event = event;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public void setNotifAttribs(String attribs) {
        notifMap.setAttribs(attribs);
    }
    
    @Override
    public String toString() {
        return "EconomicEventParticipant [" + id + "]@" + Integer.toHexString(System.identityHashCode(this));
    }
    
    public List<EconomicEventParticipantSelection> getSelections() {
        return selections;
    }

    public void setSelections(Collection<EconomicEventParticipantSelection> selections) {
        this.selections = new ArrayList<EconomicEventParticipantSelection>(selections);
    }
    
    public EconomicEventParticipantSelection getSelection(EconomicEventPricing revision) {
        for (EconomicEventParticipantSelection selection : selections) {
            if (selection.getPricingRevision().equals(revision)) {
                return selection;
            }
        }
        throw new IllegalArgumentException(this + " does not have data for revision " + revision);
    }

    public void addSelection(EconomicEventParticipantSelection nextSelection) {
        nextSelection.setParticipant(this);
        selections.add(nextSelection);
    }

}
