package com.cannontech.common.bulk.collection.device.model;


import java.util.Date;
import java.util.List;

import com.cannontech.common.device.commands.CommandRequestExecutionStatus;

public class CollectionActionFilter {
    private List<CollectionAction> actions;
    private Date startDate = new Date();
    private Date endDate = new Date();
    private List<CommandRequestExecutionStatus> statuses;
    private String userName;
    private String action;
    
    public List<CommandRequestExecutionStatus> getStatuses() {
        return statuses;
    }
    public void setStatuses(List<CommandRequestExecutionStatus> statuses) {
        this.statuses = statuses;
    }
    public List<CollectionAction> getActions() {
        return actions;
    }
    public void setActions(List<CollectionAction> actions) {
        this.actions = actions;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
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
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
}
