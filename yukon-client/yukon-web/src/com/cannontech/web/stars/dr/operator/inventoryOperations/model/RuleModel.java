package com.cannontech.web.stars.dr.operator.inventoryOperations.model;

import java.util.Date;

import com.cannontech.common.constants.YukonListEntry;

public class RuleModel {
    private FilterRuleType ruleType;
    private String groupIds;
    private String programIds;
    private Date fieldInstallDate;
    private Date programSignupDate;
    private YukonListEntry deviceType;
    private Integer serialNumberFrom;
    private Integer serialNumberTo;
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

    public Date getFieldInstallDate() {
        return fieldInstallDate;
    }

    public void setFieldInstallDate(Date fieldInstallDate) {
        this.fieldInstallDate = fieldInstallDate;
    }

    public Date getProgramSignupDate() {
        return programSignupDate;
    }

    public void setProgramSignupDate(Date programSignupDate) {
        this.programSignupDate = programSignupDate;
    }

    public YukonListEntry getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(YukonListEntry deviceType) {
        this.deviceType = deviceType;
    }

    public Integer getSerialNumberFrom() {
        return serialNumberFrom;
    }

    public void setSerialNumberFrom(Integer serialNumberFrom) {
        this.serialNumberFrom = serialNumberFrom;
    }

    public Integer getSerialNumberTo() {
        return serialNumberTo;
    }

    public void setSerialNumberTo(Integer serialNumberTo) {
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