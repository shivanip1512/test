package com.cannontech.web.stars.dr.operator.inventoryOperations.model;

import com.cannontech.common.constants.YukonListEntry;

public class RuleModel {
    private FilterRuleType ruleType;
    private String groupIds;
    private String programIds;
    private String fieldInstallDate;
    private String programSignupDate;
    private YukonListEntry deviceType;
    private String serialNumberFrom;
    private String serialNumberTo;
    private String unenrolled;
    private String description;

    public RuleModel() {
        
    }
    
    public RuleModel(FilterRuleType ruleType) {
        this.ruleType = ruleType;
    }

    public FilterRuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(FilterRuleType ruleType) {
        this.ruleType = ruleType;
    }

    public String getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(String groupIds) {
        this.groupIds = groupIds;
    }

    public String getProgramIds() {
        return programIds;
    }

    public void setProgramIds(String programIds) {
        this.programIds = programIds;
    }

    public String getFieldInstallDate() {
        return fieldInstallDate;
    }

    public void setFieldInstallDate(String fieldInstallDate) {
        this.fieldInstallDate = fieldInstallDate;
    }

    public String getProgramSignupDate() {
        return programSignupDate;
    }

    public void setProgramSignupDate(String programSignupDate) {
        this.programSignupDate = programSignupDate;
    }

    public YukonListEntry getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(YukonListEntry deviceType) {
        this.deviceType = deviceType;
    }

    public String getSerialNumberFrom() {
        return serialNumberFrom;
    }

    public void setSerialNumberFrom(String serialNumberFrom) {
        this.serialNumberFrom = serialNumberFrom;
    }

    public String getSerialNumberTo() {
        return serialNumberTo;
    }

    public void setSerialNumberTo(String serialNumberTo) {
        this.serialNumberTo = serialNumberTo;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnenrolled() {
        return unenrolled;
    }

    public void setUnenrolled(String unenrolled) {
        this.unenrolled = unenrolled;
    }

}