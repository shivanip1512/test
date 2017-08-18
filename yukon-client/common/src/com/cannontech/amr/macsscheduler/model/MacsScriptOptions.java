package com.cannontech.amr.macsscheduler.model;

import java.util.Arrays;
import java.util.List;

public class MacsScriptOptions {

    private static final List<String> touRates = Arrays.asList("rate T", "rate A", "rate B", "rate C", "rate D");
    private static final List<String> iedTypes= Arrays.asList("s4", "alpha", "kv", "kv2", "sentinel");
        
    private String fileName;
    private String scriptText;
    private String description;
    private String groupName;
    private Integer porterTimeout;
    private String filePath;
    private String missedFileName;
    private String successFileName;
    
    //retry
    private Integer retryCount;
    private Integer queueOffCount;
    private Integer maxRetryHours;
        
    //IED
    private String touRate;
    
    //Demand Reset
    private boolean isDemandResetSelected;
    private Integer demandResetRetryCount;
    private String iedType;
    
    //billing
    private boolean isBillingSelected;
    private String billingGroupName;
    private String billingFormat;
    private Integer billingDemandDays;
    private Integer billingEnergyDays;
    private String billingFileName;
    private String billingFilePath;
    
    //Notification
    private boolean isNotificationSelected;
    private int notificationGroupId;
    private String notificationSubject;
    
    public Integer getPorterTimeout() {
        return porterTimeout;
    }

    public void setPorterTimeout(Integer porterTimeout) {
        this.porterTimeout = porterTimeout;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSuccessFileName() {
        return successFileName;
    }

    public void setSuccessFileName(String successFileName) {
        this.successFileName = successFileName;
    }

    public Integer getQueueOffCount() {
        return queueOffCount;
    }

    public void setQueueOffCount(Integer queueOffCount) {
        this.queueOffCount = queueOffCount;
    }

    public Integer getMaxRetryHours() {
        return maxRetryHours;
    }

    public void setMaxRetryHours(Integer maxRetryHours) {
        this.maxRetryHours = maxRetryHours;
    }

    public String getMissedFileName() {
        return missedFileName;
    }

    public void setMissedFileName(String missedFileName) {
        this.missedFileName = missedFileName;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getBillingDemandDays() {
        return billingDemandDays;
    }

    public void setBillingDemandDays(Integer billingDemandDays) {
        this.billingDemandDays = billingDemandDays;
    }

    public String getBillingFileName() {
        return billingFileName;
    }

    public void setBillingFileName(String billingFileName) {
        this.billingFileName = billingFileName;
    }

    public String getBillingFilePath() {
        return billingFilePath;
    }

    public void setBillingFilePath(String billingFilePath) {
        this.billingFilePath = billingFilePath;
    }

    public Integer getBillingEnergyDays() {
        return billingEnergyDays;
    }

    public void setBillingEnergyDays(Integer billingEnergyDays) {
        this.billingEnergyDays = billingEnergyDays;
    }

    public String getTouRate() {
        return touRate;
    }

    public void setTouRate(String touRate) {
        this.touRate = touRate;
    }

    public Integer getDemandResetRetryCount() {
        return demandResetRetryCount;
    }

    public void setDemandResetRetryCount(Integer demandResetRetryCount) {
        this.demandResetRetryCount = demandResetRetryCount;
    }

    public String getIedType() {
        return iedType;
    }

    public void setIedType(String iedType) {
        this.iedType = iedType;
    }

    public String getNotificationSubject() {
        return notificationSubject;
    }

    public void setNotificationSubject(String notificationSubject) {
        this.notificationSubject = notificationSubject;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getScriptText() {
        return scriptText;
    }

    public void setScriptText(String scriptText) {
        this.scriptText = scriptText;
    }

    public boolean isDemandResetSelected() {
        return isDemandResetSelected;
    }

    public void setDemandResetSelected(boolean isDemandResetSelected) {
        this.isDemandResetSelected = isDemandResetSelected;
    }

    public boolean isBillingSelected() {
        return isBillingSelected;
    }

    public void setBillingSelected(boolean isBillingSelected) {
        this.isBillingSelected = isBillingSelected;
    }

    public boolean isNotificationSelected() {
        return isNotificationSelected;
    }

    public void setNotificationSelected(boolean isNotificationSelected) {
        this.isNotificationSelected = isNotificationSelected;
    }

    public String getBillingFormat() {
        return billingFormat;
    }

    public void setBillingFormat(String billingFormat) {
        this.billingFormat = billingFormat;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getBillingGroupName() {
        return billingGroupName;
    }

    public void setBillingGroupName(String billingGroupName) {
        this.billingGroupName = billingGroupName;
    }
    
    public static List<String> getTouRates() {
        return touRates;
    }
    
    public static List<String> getIedTypes() {
        return iedTypes;
    }

    public int getNotificationGroupId() {
        return notificationGroupId;
    }

    public void setNotificationGroupId(int notificationGroupId) {
        this.notificationGroupId = notificationGroupId;
    }
}
