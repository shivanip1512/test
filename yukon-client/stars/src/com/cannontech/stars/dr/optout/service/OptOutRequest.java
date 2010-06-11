package com.cannontech.stars.dr.optout.service;

import java.util.List;

import org.joda.time.Duration;
import org.joda.time.ReadableInstant;

import com.cannontech.stars.dr.optout.model.ScheduledOptOutQuestion;

/**
 * Class which holds information about an Opt Out request
 */
public class OptOutRequest {
    private ReadableInstant startDate;
    private long durationInHours;
    private List<Integer> inventoryIdList;
    private List<ScheduledOptOutQuestion> questions;
    private Integer eventId;
    
    public ReadableInstant getStartDate() {
        return startDate;
    }
    
    public void setStartDate(ReadableInstant startDate) {
        this.startDate = startDate;
    }
    
    public long getDurationInHours() {
        return durationInHours;
    }
    
    public void setDurationInHours(int durationInHours) {
        this.durationInHours = durationInHours;
    }
    
    public List<Integer> getInventoryIdList() {
        return inventoryIdList;
    }
    
    public void setInventoryIdList(List<Integer> inventoryIdList) {
        this.inventoryIdList = inventoryIdList;
    }
    
    public List<ScheduledOptOutQuestion> getQuestions() {
        return questions;
    }
    
    public void setQuestions(List<ScheduledOptOutQuestion> questions) {
        this.questions = questions;
    }
    
    public ReadableInstant getStopDate() {
    	if(startDate == null) {
    		return null;
    	}
    	
    	// Converts the duration in hours to a duration
    	Duration optOutDuration = Duration.standardHours(durationInHours);
    	
    	ReadableInstant stopDate = startDate.toInstant().plus(optOutDuration);
    	
    	return stopDate;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }
    
}
