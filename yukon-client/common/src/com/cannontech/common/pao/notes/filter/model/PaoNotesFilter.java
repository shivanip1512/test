package com.cannontech.common.pao.notes.filter.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.cannontech.common.device.groups.model.DeviceGroup;

public class PaoNotesFilter {
    
    private Set<Integer> paoIds;
    private String text;
    private Date startDate;
    private Date endDate;
    private String user;
    private List<DeviceGroup> deviceGroups;
    private PaoSelectionMethod paoSelectionMethod;
    
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
    
}
