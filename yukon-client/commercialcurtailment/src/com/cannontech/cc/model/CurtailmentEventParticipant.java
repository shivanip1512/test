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
@Table(name = "CCurtCEParticipant",
       uniqueConstraints=@UniqueConstraint(columnNames={"CCurtCurtailmentEventId","CustomerId"}))
public class CurtailmentEventParticipant {
    private Integer id;
    private CICustomerStub customer;
    private CurtailmentEvent event;
    private NotifMap notifMap = new NotifMap();

    @Id
    @GenericGenerator(name="yukon", strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @GeneratedValue(generator="yukon")
    @Column(name = "CCurtCEParticipantId")
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
    @JoinColumn(name="CCurtCurtailmentEventId", nullable=false)
    public CurtailmentEvent getEvent() {
        return event;
    }

    public void setEvent(CurtailmentEvent event) {
        this.event = event;
    }

    @Column(nullable=false)
    public String getNotifAttribs() {
        return notifMap.getAttribs();
    }

    public void setNotifAttribs(String attribs) {
        notifMap.setAttribs(attribs);
    }
    
    @Transient
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
