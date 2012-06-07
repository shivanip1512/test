package com.cannontech.stars.dr.optout.service;

import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.survey.model.Result;
import com.cannontech.stars.dr.optout.model.ScheduledOptOutQuestion;
import com.google.common.collect.Multimap;

/**
 * Class which holds information about an Opt Out request
 */
public class OptOutRequest {
    private Instant startDate;
    private long durationInHours;
    private List<Integer> inventoryIdList;
    private List<ScheduledOptOutQuestion> questions;
    private List<Result> surveyResults;
    private Multimap<Integer, Integer> surveyIdsByInventoryId;
    private Integer eventId;

    public Instant getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }
    
    public long getDurationInHours() {
        return durationInHours;
    }
    
    public void setDurationInHours(long durationInHours) {
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

    public List<Result> getSurveyResults() {
        return surveyResults;
    }

    public void setSurveyResults(List<Result> surveyResults) {
        this.surveyResults = surveyResults;
    }

    public Multimap<Integer, Integer> getSurveyIdsByInventoryId() {
        return surveyIdsByInventoryId;
    }

    public void setSurveyIdsByInventoryId(
            Multimap<Integer, Integer> surveyIdsByInventoryId) {
        this.surveyIdsByInventoryId = surveyIdsByInventoryId;
    }

    public Instant getStopDate() {
    	if(startDate == null) {
    		return null;
    	}
    	
    	// Converts the duration in hours to a duration
    	Duration optOutDuration = Duration.standardHours(durationInHours);
    	
    	Instant stopDate = startDate.toInstant().plus(optOutDuration);
    	
    	return stopDate;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }
}
