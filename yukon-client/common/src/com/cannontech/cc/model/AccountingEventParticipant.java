package com.cannontech.cc.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.cannontech.database.data.notification.NotifMap;

public class AccountingEventParticipant implements BaseParticipant {
    private Integer id;
    private CICustomerStub customer;
    private AccountingEvent event;
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

    public AccountingEvent getEvent() {
        return event;
    }

    public void setEvent(AccountingEvent event) {
        this.event = event;
    }

    public NotifMap getNotifMap() {
        return notifMap;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AccountingEventParticipant == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        AccountingEventParticipant rhs = (AccountingEventParticipant) obj;
        return new EqualsBuilder().append(event, rhs.event).append(customer, rhs.customer).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(event).append(customer).toHashCode();
    }
    
    @Override
    public String toString() {
        return customer + " in " + event + " [" + id + "]";
    }
}
