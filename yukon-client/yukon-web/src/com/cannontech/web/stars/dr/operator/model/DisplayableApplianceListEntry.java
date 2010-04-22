package com.cannontech.web.stars.dr.operator.model;

import java.util.Comparator;

public class DisplayableApplianceListEntry {
    protected int applianceId;
    protected String applianceName;
    protected String deviceLabel;
    protected String assignedProgramName;
    
    public DisplayableApplianceListEntry(){}
    
    public DisplayableApplianceListEntry(int applianceId, String applianceName, String deviceLabel, String assignedProgramName) {
        this.applianceId = applianceId;
        this.applianceName = applianceName;
        this.deviceLabel = deviceLabel;
        this.assignedProgramName = assignedProgramName;
    }

    
    public static Comparator<DisplayableApplianceListEntry> APPLIANCE_NAME_COMPARATOR = new Comparator<DisplayableApplianceListEntry>() {

        @Override
        public int compare(DisplayableApplianceListEntry appListEntry1, DisplayableApplianceListEntry appListEntry2) {
            return appListEntry1.getApplianceName().toLowerCase().compareTo(appListEntry2.getApplianceName().toLowerCase());
        }
        
    };
    
    public int getApplianceId() {
        return applianceId;
    }
    public void setApplianceId(int applianceId) {
        this.applianceId = applianceId;
    }
    
    public String getApplianceName() {
        return applianceName;
    }
    public void setApplianceName(String applianceName) {
        this.applianceName = applianceName;
    }
    
    public String getDeviceLabel() {
        return deviceLabel;
    }

    public void setDeviceLabel(String deviceLabel) {
        this.deviceLabel = deviceLabel;
    }

    public String getAssignedProgramName() {
        return assignedProgramName;
    }
    public void setAssignedProgramName(String assignedProgramName) {
        this.assignedProgramName = assignedProgramName;
    }
}
