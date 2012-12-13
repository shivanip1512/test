package com.cannontech.stars.dr.displayable.model;

import java.util.SortedSet;

public class DisplayableGroupedControlHistory {
    
    private String displayName;
    private SortedSet<DisplayableGroupedControlHistoryEvent> groupedHistory;

    public SortedSet<DisplayableGroupedControlHistoryEvent> getGroupedHistory() {
        return groupedHistory;
    }

    public void setGroupedHistory(SortedSet<DisplayableGroupedControlHistoryEvent> groupedHistory) {
        this.groupedHistory = groupedHistory;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
