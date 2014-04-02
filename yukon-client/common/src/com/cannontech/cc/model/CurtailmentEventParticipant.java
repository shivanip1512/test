package com.cannontech.cc.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.cannontech.core.dao.support.Identifiable;
import com.cannontech.database.data.notification.NotifMap;

public class CurtailmentEventParticipant implements Identifiable, BaseParticipant {
    private Integer id;
    private CICustomerStub customer;
    private CurtailmentEvent event;
    private NotifMap notifMap = new NotifMap();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CICustomerStub getCustomer() {
        return customer;
    }
    
    public void setCustomer(CICustomerStub customer) {
        this.customer = customer;
    }

    public CurtailmentEvent getEvent() {
        return event;
    }

    public void setEvent(CurtailmentEvent event) {
        this.event = event;
    }

    public String getNotifAttribs() {
        return notifMap.getAttribs();
    }

    public void setNotifAttribs(String attribs) {
        notifMap.setAttribs(attribs);
    }
    
    public NotifMap getNotifMap() {
        return notifMap;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CurtailmentEventParticipant == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        CurtailmentEventParticipant rhs = (CurtailmentEventParticipant) obj;
        return new EqualsBuilder().append(id, rhs.id).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }
    
    @Override
    public String toString() {
        return "CurtailmentEventParticipant [" + id + "]";
    }
}
