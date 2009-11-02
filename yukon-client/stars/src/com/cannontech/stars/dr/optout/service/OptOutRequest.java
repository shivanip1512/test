package com.cannontech.stars.dr.optout.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.cannontech.stars.dr.optout.model.ScheduledOptOutQuestion;

/**
 * Class which holds information about an Opt Out request
 */
public class OptOutRequest {
    private Date startDate;
    private int durationInHours;
    private List<Integer> inventoryIdList;
    private List<ScheduledOptOutQuestion> questions;
    private Integer eventId;
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public int getDurationInHours() {
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
    
    public Date getStopDate() {
    	if(startDate == null) {
    		return null;
    	}
    	
    	Calendar cal = new GregorianCalendar();
    	cal.setTime(startDate);
    	cal.add(Calendar.HOUR_OF_DAY, durationInHours);
    	
    	Date stopDate = cal.getTime();
    	
    	return stopDate;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }
    
}
