package com.cannontech.amr.macsscheduler.model;

import java.util.Arrays;
import java.util.List;

import com.cannontech.common.device.groups.model.DeviceGroup;

public class MacsScriptOptions {

    private static final List<String> touRates = Arrays.asList("rate T", "rate A", "rate B", "rate C", "rate D");
    private static final List<String> iedTypes= Arrays.asList("s4", "alpha", "kv", "kv2", "sentinel");
        
    private String fileName;
    private String scriptText;
    private String description;
    private DeviceGroup group;
    private String groupName;
    private int porterTimeout;
    private String filePath;
    private String missedFileName;
    private String successFileName;
    
    //retry
    private int retryCount;
    private int queueOffCount;
    private int maxRetryHours;
        
    //IED
    private String touRate;
    
    //Demand Reset
    private boolean isDemandResetSelected;
    private int demandResetRetryCount;
    private String frozenDemandRegister;
    private String iedType;
    
    //billing
    private boolean isBillingSelected;
    private DeviceGroup billingGroup;
    private String billingGroupName;
    private String billingFormat;
    private int billingDemandDays;
    private int billingEnergyDays;
    private String billingFileName;
    private String billingFilePath;
    
    //Notification
    private boolean isNotificationSelected;
    private String notificationGroupName;
    private String notificationSubject;
    

    public DeviceGroup getGroup() {
        return group;
    }

    public void setGroup(DeviceGroup group) {
        this.group = group;
    }

    public int getPorterTimeout() {
        return porterTimeout;
    }

    public void setPorterTimeout(int porterTimeout) {
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

    public int getQueueOffCount() {
        return queueOffCount;
    }

    public void setQueueOffCount(int queueOffCount) {
        this.queueOffCount = queueOffCount;
    }

    public int getMaxRetryHours() {
        return maxRetryHours;
    }

    public void setMaxRetryHours(int maxRetryHours) {
        this.maxRetryHours = maxRetryHours;
    }

    public String getMissedFileName() {
        return missedFileName;
    }

    public void setMissedFileName(String missedFileName) {
        this.missedFileName = missedFileName;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public DeviceGroup getBillingGroup() {
        return billingGroup;
    }

    public void setBillingGroup(DeviceGroup billingGroup) {
        this.billingGroup = billingGroup;
    }

    public int getBillingDemandDays() {
        return billingDemandDays;
    }

    public void setBillingDemandDays(int billingDemandDays) {
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

    public int getBillingEnergyDays() {
        return billingEnergyDays;
    }

    public void setBillingEnergyDays(int billingEnergyDays) {
        this.billingEnergyDays = billingEnergyDays;
    }

    public String getTouRate() {
        return touRate;
    }

    public void setTouRate(String touRate) {
        this.touRate = touRate;
    }

    public int getDemandResetRetryCount() {
        return demandResetRetryCount;
    }

    public void setDemandResetRetryCount(int demandResetRetryCount) {
        this.demandResetRetryCount = demandResetRetryCount;
    }

    public String getFrozenDemandRegister() {
        return frozenDemandRegister;
    }

    public void setFrozenDemandRegister(String frozenDemandRegister) {
        this.frozenDemandRegister = frozenDemandRegister;
    }

    public String getIedType() {
        return iedType;
    }

    public void setIedType(String iedType) {
        this.iedType = iedType;
    }

    public String getNotificationGroupName() {
        return notificationGroupName;
    }

    public void setNotificationGroupName(String notificationGroupName) {
        this.notificationGroupName = notificationGroupName;
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
}
