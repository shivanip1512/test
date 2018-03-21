package com.cannontech.common.bulk.collection.device.model;


import java.util.Date;
import java.util.List;

import com.cannontech.common.device.commands.CommandRequestExecutionStatus;

/**
 * Filter for recent results page.
 */
public class CollectionActionFilter {
    private List<CollectionAction> actions;
    private Date startDate;
    private Date endDate;
    private List<CommandRequestExecutionStatus> statuses;
    private List<String> userNames;
    private String userIds;
    
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
    public List<String> getUserNames() {
        return userNames;
    }
    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
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
    public String getUserIds() {
        return userIds;
    }
    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }
}
