package com.cannontech.common.bulk.collection.device.model;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.util.Range;

public class CollectionActionFilter {
    private List<CollectionAction> actions;
    private Range<Instant> range;
    private List<CommandRequestExecutionStatus> statuses;
    private String userName;
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
    public Range<Instant> getRange() {
        return range;
    }
    public void setRange(Range<Instant> range) {
        this.range = range;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
