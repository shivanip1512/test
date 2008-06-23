package com.cannontech.stars.dr.optout.service;

import java.util.Date;
import java.util.List;

import com.cannontech.stars.dr.optout.model.ScheduledOptOutQuestion;

public class OptOutRequest {
    private Date startDate;
    private int durationInHours;
    private List<Integer> inventoryIdList;
    private List<ScheduledOptOutQuestion> questions;
    
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
    
}
