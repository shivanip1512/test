package com.cannontech.web.stars.dr.operator.inventoryOperations.model;

import java.util.Date;

import com.cannontech.common.constants.YukonListEntry;

public class RuleModel {
    
    private FilterRuleType ruleType;
    private String description;

    /* Load Groups Rule */
    private String groupIds;
    
    /* Programs Rule */
    private String programIds;
    
    /* Field Install Date Rule */
    private Date fieldInstallDate;
    
    /* Program Signup Date Rule */
    private Date programSignupDate;
    
    /* Device Type Rule */
    private YukonListEntry deviceType;
    
    /* Serial Number Range Rule */
    private long serialNumberFrom;
    private long serialNumberTo;
    
    /* Not Enrolled Rule */
    private String unenrolled;
    
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

    public long getSerialNumberFrom() {
        return serialNumberFrom;
    }

    public void setSerialNumberFrom(long serialNumberFrom) {
        this.serialNumberFrom = serialNumberFrom;
    }

    public long getSerialNumberTo() {
        return serialNumberTo;
    }

    public void setSerialNumberTo(long serialNumberTo) {
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