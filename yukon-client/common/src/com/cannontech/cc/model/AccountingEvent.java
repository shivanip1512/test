package com.cannontech.cc.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.cannontech.common.util.TimeUtil;



@Entity
@Table(name="CCurtAcctEvent")
public class AccountingEvent extends BaseEvent {
    private Integer id;
    private Program program;
    private Date startTime;
    private String reason;
    private Integer duration;
    private Integer identifier = new Integer(0);

    @Id
    @GenericGenerator(name="yukon", strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @GeneratedValue(generator="yukon")
    @Column(name = "CCurtAcctEventId")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Type(type="com.cannontech.hibernate.HibernateEscapedString")
    @Column(name="reason", nullable=false)
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    @Transient
    public Date getNotificationTime() {
        return startTime;
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
    public String toString() {
        return "AccountingEvent " + getDisplayName() + " [" + id + "]";
    }

    @Override @Transient
    public String getStateDescription() {
        return "NORMAL";
    }

}
