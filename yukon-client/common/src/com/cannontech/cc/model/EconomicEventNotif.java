package com.cannontech.cc.model;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.cannontech.database.data.notification.NotifType;
import com.cannontech.cc.service.NotificationReason;
import com.cannontech.cc.service.NotificationState;

public class EconomicEventNotif implements EventNotif {
    private Integer id;
    private Integer notifTypeId;
    private Date notificationTime;
    private NotificationState state;
    private EconomicEventParticipant participant;
    private EconomicEventPricing revision;
    private NotificationReason reason;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(Date notificationTime) {
        this.notificationTime = notificationTime;
    }

    public Integer getNotifTypeId() {
        return notifTypeId;
    }

    public void setNotifTypeId(Integer notifTypeId) {
        this.notifTypeId = notifTypeId;
    }
    
    public NotifType getNotifType() {
        return NotifType.values()[notifTypeId];
    }
    
    public void setNotifType(NotifType type) {
        this.notifTypeId = type.ordinal();
    }

    public NotificationState getState() {
        return state;
    }

    public void setState(NotificationState state) {
        this.state = state;
    }
    
    public EconomicEventParticipant getParticipant() {
        return participant;
    }

    public void setParticipant(EconomicEventParticipant participant) {
        this.participant = participant;
    }
    
    public NotificationReason getReason() {
        return reason;
    }
    
    public void setReason(NotificationReason reason) {
        this.reason = reason;
    }
    
    public EconomicEventPricing getRevision() {
        return revision;
    }
    
    public void setRevision(EconomicEventPricing revision) {
        this.revision = revision;
    }
    
    public CICustomerStub getCustomer() {
        return participant.getCustomer();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EconomicEventNotif == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        EconomicEventNotif rhs = (EconomicEventNotif) obj;
        return new EqualsBuilder().append(id, rhs.id).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }
    
    @Override
    public String toString() {
        return "EconomicEventNotif [" + id + "]";
    }
}
