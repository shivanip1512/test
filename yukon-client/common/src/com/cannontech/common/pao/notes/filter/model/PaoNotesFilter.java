package com.cannontech.common.pao.notes.filter.model;

import java.util.List;
import java.util.Set;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.DateRange;

public class PaoNotesFilter {
    
    private Set<Integer> paoIds;
    private String text;
    private DateRange dateRange = new DateRange();
    private String user;
    private List<DeviceGroup> deviceGroups;
    private PaoSelectionMethod paoSelectionMethod;
    private String deviceGroupNames;
    
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
    
    public String getUser() {
        return user;
    }
    
    public void setUser(String user) {
        this.user = user;
    }

    public List<DeviceGroup> getDeviceGroups() {
        return deviceGroups;
    }

    public void setDeviceGroups(List<DeviceGroup> deviceGroups) {
        this.deviceGroups = deviceGroups;
    }

    public PaoSelectionMethod getPaoSelectionMethod() {
        return paoSelectionMethod;
    }

    public void setPaoSelectionMethod(PaoSelectionMethod paoSelectionMethod) {
        this.paoSelectionMethod = paoSelectionMethod;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    public String getDeviceGroupNames() {
        return deviceGroupNames;
    }

    public void setDeviceGroupNames(String deviceGroupNames) {
        this.deviceGroupNames = deviceGroupNames;
    }
}
