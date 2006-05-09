package com.cannontech.cc.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import com.cannontech.enums.NotificationReason;
import com.cannontech.enums.NotificationState;

@Entity
@Table(name = "CCurtCENotif")
public class CurtailmentEventNotif {
    private Integer id;
    private Integer notifTypeId;
    private Date notificationTime;
    private NotificationState state;
    private CurtailmentEventParticipant participant;
    private NotificationReason reason;
    
    @Id
    @GenericGenerator(name="yukon", strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @GeneratedValue(generator="yukon")
    @Column(name = "CCurtCENotifId")
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

    @Column(nullable=false)
    public Integer getNotifTypeId() {
        return notifTypeId;
    }

    public void setNotifTypeId(Integer notifTypeId) {
        this.notifTypeId = notifTypeId;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    public NotificationState getState() {
        return state;
    }

    public void setState(NotificationState state) {
        this.state = state;
    }
    
    @ManyToOne
    @JoinColumn(name="CCurtCEParticipantId", nullable=false)
    public CurtailmentEventParticipant getParticipant() {
        return participant;
    }

    public void setParticipant(CurtailmentEventParticipant participant) {
        this.participant = participant;
    }
    
    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    public NotificationReason getReason() {
        return reason;
    }
    
    public void setReason(NotificationReason reason) {
        this.reason = reason;
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
        return "CurtailmentEventNotif [" + id + "]@" + Integer.toHexString(System.identityHashCode(this));
    }


}
