package com.cannontech.stars.dr.controlhistory.model;

import java.util.List;

import com.cannontech.stars.xml.serialize.ControlHistoryEntry;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ObservedControlHistory {
    private List<ControlHistoryEntry> controlHistoryEntryList = Lists.newArrayList();
    
    public boolean isBeingControlled() {
        for (ControlHistoryEntry controlHistory : controlHistoryEntryList) {
            if (controlHistory.getOpenInterval().isOpenEnd()) {
                return true;
            }
        }
        
        return false;
    }

    public List<ControlHistoryEntry> getControlHistoryList() {
        return controlHistoryEntryList;
    }
    public void addControlHistoryEntry(ControlHistoryEntry controlHistory) {
        this.controlHistoryEntryList.add(controlHistory);
    }
    
    public void addAllControlHistoryEntries(Iterable<ControlHistoryEntry> controlHistoryList) {
        Iterables.addAll(this.controlHistoryEntryList, controlHistoryList);
    }
    public void setControlHistoryList(List<ControlHistoryEntry> controlHistoryList) {
        this.controlHistoryEntryList = controlHistoryList;
    }

}
