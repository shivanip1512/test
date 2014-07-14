package com.cannontech.cc.model;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.cannontech.core.dao.support.Identifiable;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.cc.service.NotificationReason;
import com.cannontech.cc.service.NotificationState;

public class CurtailmentEventNotif implements Identifiable, EventNotif {
    private Integer id;
    private Integer notifTypeId;
    private Date notificationTime;
    private NotificationState state;
    private CurtailmentEventParticipant participant;
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
    
    public CurtailmentEventParticipant getParticipant() {
        return participant;
    }

    public void setParticipant(CurtailmentEventParticipant participant) {
        this.participant = participant;
    }
    
    public NotificationReason getReason() {
        return reason;
    }
    
    public void setReason(NotificationReason reason) {
        this.reason = reason;
    }
    
    public CICustomerStub getCustomer() {
        return participant.getCustomer();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CurtailmentEventNotif == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        CurtailmentEventNotif rhs = (CurtailmentEventNotif) obj;
        return new EqualsBuilder().append(id, rhs.id).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }
    
    @Override
    public String toString() {
        return "CurtailmentEventNotif [" + id + "]";
    }
}
