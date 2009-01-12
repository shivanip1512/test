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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.cannontech.cc.service.enums.CurtailmentEventState;
import com.cannontech.common.util.TimeUtil;



@Entity
@Table(name="CCurtCurtailmentEvent")
public class CurtailmentEvent extends BaseEvent {
    private Program program;
    private Date startTime;
    private Date notificationTime;
    private String message;
    private Integer duration;
    private Integer id;
    private Integer identifier = new Integer(0);
    private CurtailmentEventState state;

    @Id
    @GenericGenerator(name="yukon", strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @GeneratedValue(generator="yukon")
    @Column(name = "CCurtCurtailmentEventId")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Type(type="com.cannontech.hibernate.HibernateEscapedString")
    @Column(name="message", nullable=false)
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Column(name="notificationTime", nullable=false)
    public Date getNotificationTime() {
        return notificationTime;
    }
    
    public void setNotificationTime(Date notificationTime) {
        this.notificationTime = notificationTime;
    }
    
    @ManyToOne
    @JoinColumn(name="CCurtProgramId", nullable=false)
    public Program getProgram() {
        return program;
    }
    
    public void setProgram(Program program) {
        this.program = program;
    }
    
    @Column(name="startTime", nullable=false)
    public Date getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Integer eventDuration) {
        this.duration = eventDuration;
    }
    
    public void setState(CurtailmentEventState state) {
        this.state = state;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    public CurtailmentEventState getState() {
        return state;
    }
    
    @Column(name="duration", nullable=false)
    public Integer getDuration() {
        return duration;
    }
    
    @Transient
    public Date getStopTime() {
        return TimeUtil.addMinutes(getStartTime(), getDuration());
    }
    
    @Column(nullable=false)
    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }
    
    @Override
    @Transient
    public String getStateDescription() {
        return state.toString();
    }

    @Override
    public String toString() {
        return "CurtailmentEvent [" + id + "]@" + Integer.toHexString(System.identityHashCode(this));
    }

}
