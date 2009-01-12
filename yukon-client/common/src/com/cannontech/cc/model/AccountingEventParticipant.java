package com.cannontech.cc.model;

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

import com.cannontech.database.data.notification.NotifMap;

@Entity
@Table(name = "CCurtAcctEventParticipant",
       uniqueConstraints=@UniqueConstraint(columnNames={"CCurtAcctEventId","CustomerId"}))
public class AccountingEventParticipant implements BaseParticipant {
    private Integer id;
    private CICustomerStub customer;
    private AccountingEvent event;
    private NotifMap notifMap = new NotifMap();

    @Id
    @GenericGenerator(name="yukon", strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @GeneratedValue(generator="yukon")
    @Column(name="CCurtAcctEventParticipantId")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name="CustomerId", nullable=false)
    public CICustomerStub getCustomer() {
        return customer;
    }
    
    public void setCustomer(CICustomerStub customer) {
        this.customer = customer;
    }

    @ManyToOne
    @JoinColumn(name="CCurtAcctEventId", nullable=false)
    public AccountingEvent getEvent() {
        return event;
    }

    public void setEvent(AccountingEvent event) {
        this.event = event;
    }

    @Transient
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
