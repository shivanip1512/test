package com.cannontech.common.pao.notes.filter.model;

import java.util.Date;
import java.util.Set;

public class PaoNotesFilter {
    
    private Set<Integer> paoIds;
    private String text;
    private Date startDate;
    private Date endDate;
    private String user;
    
    public Set<Integer> getPaoIds() {
        return paoIds;
    }
    public void setPaoIds(Set<Integer> paoIds) {
        this.paoIds = paoIds;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
}
