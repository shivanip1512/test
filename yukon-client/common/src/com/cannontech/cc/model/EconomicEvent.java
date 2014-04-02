package com.cannontech.cc.model;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.cannontech.cc.service.enums.EconomicEventState;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.support.Identifiable;

public class EconomicEvent extends BaseEvent implements Identifiable {
    private Integer id;
    private Program program;
    private Date startTime;
    private Date notificationTime;
    private Integer windowLengthMinutes;
    private EconomicEventState state;
    private EconomicEvent initialEvent;
    private Integer identifier = new Integer(0);
    private Map<Integer,EconomicEventPricing> revisions = 
        new HashMap<Integer, EconomicEventPricing>();

    public EconomicEvent() {
        super();
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }

    public Date getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(Date notificationTime) {
        this.notificationTime = notificationTime;
    }

    public Program getProgram() {
        return program;
    }
    
    public void setProgram(Program program) {
        this.program = program;
    }

    public EconomicEventState getState() {
        return state;
    }

    public EconomicEvent getInitialEvent() {
        return initialEvent;
    }
    
    public void setInitialEvent(EconomicEvent initialEvent) {
        this.initialEvent = initialEvent;
    }
    
    public boolean isEventExtension() {
        return initialEvent != null;
    }
    
    public void setState(EconomicEventState state) {
        this.state = state;
    }

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
    
    public Map<Integer,EconomicEventPricing> getRevisions() {
        return revisions;
    }
    
    public void setRevisions(Iterable<EconomicEventPricing> iterable) {
        for (EconomicEventPricing pricing : iterable) {
            revisions.put(pricing.getRevision(), pricing);
        }
    }
    
    public  EconomicEventPricing getInitialRevision() {
        return revisions.get(1);
    }
    
    public EconomicEventPricing getLatestRevision() {
        if (revisions.size() == 0) return null;
        Integer lastRevNumber = Collections.max(revisions.keySet());
        return revisions.get(lastRevNumber);
    }

    public Date getStopTime() {
        Integer durationMinutes = getInitialRevision().getAffectedDurationMinutes();
        return TimeUtil.addMinutes(getStartTime(), durationMinutes);
    }
    
    public Integer getDuration() {
        return getInitialRevision().getAffectedDurationMinutes();
    }
    
    public int getInitialWindows() {
        return getInitialRevision().getNumberOfWindows();
    }

    public void addRevision(EconomicEventPricing nextRevision) {
        nextRevision.setEvent(this);
        revisions.put(nextRevision.getRevision(), nextRevision);
    }
    
    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }
    
    @Override
    public String getStateDescription() {
        return state.toString();
    }
    
    @Override
    public String toString() {
        return "EconomicEvent [" + id + "]";
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
}
