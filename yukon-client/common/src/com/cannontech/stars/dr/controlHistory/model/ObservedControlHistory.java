package com.cannontech.stars.dr.controlHistory.model;

import java.util.List;

import com.cannontech.stars.xml.serialize.ControlHistoryEntry;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ObservedControlHistory {
    private List<ControlHistoryEntry> controlHistoryEntryList = Lists.newArrayList();
    
    public boolean isBeingControlled() {
        if (controlHistoryEntryList.size() != 0) {
            return controlHistoryEntryList.get(controlHistoryEntryList.size()-1).isCurrentlyControlling();
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
