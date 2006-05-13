package com.cannontech.cc.model;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import com.cannontech.cc.service.enums.EconomicEventState;
import com.cannontech.common.util.TimeUtil;

@Entity
@Table(name="CCurtEconomicEvent")
public class EconomicEvent extends BaseEvent {
    private Integer id;
    private Program program;
    private Date startTime;
    private Date notificationTime;
    private Integer windowLengthMinutes;
    private EconomicEventState state;
    private EconomicEvent initialEvent;
    private Map<Integer,EconomicEventPricing> revisions = 
        new HashMap<Integer, EconomicEventPricing>();

    public EconomicEvent() {
        super();
    }

    @Override
    @Transient
    public String getDisplayName() {
        return startTime.toString();
    }

    @Override
    @Id
    @GenericGenerator(name="yukon", 
                      strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @GeneratedValue(generator="yukon")
    @Column(name = "CCurtEconomicEventId")
    public Integer getId() {
        return id;
    }

    @Override
    @Column(nullable=false)
    public Date getStartTime() {
        return startTime;
    }

    public Date getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(Date notificationTime) {
        this.notificationTime = notificationTime;
    }

    @ManyToOne
    @JoinColumn(name="CCurtProgramTypeId", nullable=false)
    public Program getProgram() {
        return program;
    }
    
    public void setProgram(Program program) {
        this.program = program;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    public EconomicEventState getState() {
        return state;
    }

    @OneToOne
    @JoinColumn(name="InitialEventId")
    public EconomicEvent getInitialEvent() {
        return initialEvent;
    }
    
    public void setInitialEvent(EconomicEvent initialEvent) {
        this.initialEvent = initialEvent;
    }
    
    @Transient
    public boolean isEventExtension() {
        return initialEvent != null;
    }
    
    public void setState(EconomicEventState state) {
        this.state = state;
    }

    @Column(nullable=false)
    public Integer getWindowLengthMinutes() {
        return windowLengthMinutes;
    }

    public void setWindowLengthMinutes(Integer windowLengthMinutes) {
        this.windowLengthMinutes = windowLengthMinutes;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    @OneToMany(mappedBy="event", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @MapKey(name="revision")
    @BatchSize(size=100)
    public Map<Integer,EconomicEventPricing> getRevisions() {
        return revisions;
    }
    
    public void setRevisions(Map<Integer, EconomicEventPricing> revisions) {
        this.revisions = revisions;
    }
    
    @Transient
    public  EconomicEventPricing getInitialRevision() {
        return revisions.get(1);
    }
    
    @Transient
    public EconomicEventPricing getLatestRevision() {
        Integer lastRevNumber = Collections.max(revisions.keySet());
        return revisions.get(lastRevNumber);
    }

    @Transient 
    public Date getStopTime() {
        Integer durationMinutes = getInitialRevision().getAffectedDurationMinutes();
        return TimeUtil.addMinutes(getStartTime(), durationMinutes);
    }
    
    @Transient
    public Integer getDuration() {
        return getInitialRevision().getAffectedDurationMinutes();
    }
    
    @Transient 
    public int getInitialWindows() {
        return getInitialRevision().getNumberOfWindows();
    }

    public void addRevision(EconomicEventPricing nextRevision) {
        nextRevision.setEvent(this);
        revisions.put(nextRevision.getRevision(), nextRevision);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EconomicEvent == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        EconomicEvent rhs = (EconomicEvent) obj;
        return new EqualsBuilder().append(id, rhs.id).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }
    @Override
    public String toString() {
        return "EconomicEvent [" + id + "]";
    }
    
}
