package com.cannontech.web.stars.dr.operator.inventory.model;

import java.util.Date;

import org.joda.time.LocalDate;

public class RuleModel {
    
    /* NOTE: The Residential customer type is not currently a yukonListEntry.  Because of this,
     * we use the RESIDENTIAL_ENTRY_ID constant as a mock id, that allows us to create a yukonListEntry
     * that can be used with the CICustomerType yukonListEntries to create a customer type drop down. */
    public static final int RESIDENTIAL_MODEL_ENTRY_ID = -1;
    
    private FilterRuleType ruleType;
    private String description;

    /* Appliance Type */
    private int applianceType;
    
    /* Customer Type */
    private int modelCustomerType;
    
    /* Device Status */
    private int deviceStatusId;
    
    private LocalDate deviceStateDateFrom;
    private LocalDate deviceStateDateTo;
    
    /* Load Groups Rule */
    private String groupIds;
    
    /* Postal Code */
    private String postalCode;
    
    /* Programs Rule */
    private String programIds;
    
    /* Field Install Date Rule */
    private Date fieldInstallDate;
    
    /* Program Signup Date Rule */
    private Date programSignupDate;
    
    /* Device Type Rule */
    private int deviceType;
    
    /* Serial Number Range Rule */
    private long serialNumberFrom;
    private long serialNumberTo;
    
    /* Service Company */
    private int serviceCompanyId;
    
    /* Not Enrolled Rule */
    private String unenrolled;
    
    /* Warehouse */
    private int warehouseId;
    
    public RuleModel() {}
    public RuleModel(FilterRuleType ruleType) {
        this.ruleType = ruleType;
    }

    public FilterRuleType getRuleType() {
        return ruleType;
    }
    public void setRuleType(FilterRuleType ruleType) {
        this.ruleType = ruleType;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    /* Appliance Type */
    public int getApplianceType() {
        return applianceType;
    }
    public void setApplianceType(int applianceType) {
        this.applianceType = applianceType;
    }
    
    /* Customer type */
    public int getModelCustomerType() {
        return modelCustomerType;
    }
    public void setModelCustomerType(int modelCustomerType) {
        this.modelCustomerType = modelCustomerType;
    }

    /**
     * This method returns true if the selected type in the drop down was residential.
     */
    public boolean isResidentialCustomerType() {
        if (modelCustomerType == RESIDENTIAL_MODEL_ENTRY_ID) { 
            return true;
        }
        return false;
    }
    
    /**
     * This method returns the database entry id of for ciCustomerType that was selected.  If
     * a ciCustomerType was not selected in the drop down it will throw an IllegalArgumentException.
     */
    public int getCiCustomerTypeId(){
        if (isResidentialCustomerType()) {
            throw new IllegalArgumentException("The selected customer type is not a ci customer type.");
        }
        return modelCustomerType;
    }
    
    /* Device Status */
    public int getDeviceStatusId() {
        return deviceStatusId;
    }
    public void setDeviceStatusId(int deviceStatusId) {
        this.deviceStatusId = deviceStatusId;
    }
    
    public LocalDate getDeviceStateDateFrom() {
        return deviceStateDateFrom;
    }
    public void setDeviceStateDateFrom(LocalDate deviceStateDateFrom) {
        this.deviceStateDateFrom = deviceStateDateFrom;
    }
    
    public LocalDate getDeviceStateDateTo() {
        return deviceStateDateTo;
    }
    public void setDeviceStateDateTo(LocalDate deviceStateDateTo) {
        this.deviceStateDateTo = deviceStateDateTo;
    }
    
    /* Load Group */
    public String getGroupIds() {
        return groupIds;
    }
    public void setGroupIds(String groupIds) {
        this.groupIds = groupIds;
    }
    
    /* Program ids */
    public String getProgramIds() {
        return programIds;
    }
    public void setProgramIds(String programIds) {
        this.programIds = programIds;
    }

    /* Program Signup Date Rule */
    public Date getFieldInstallDate() {
        return fieldInstallDate;
    }
    public void setFieldInstallDate(Date fieldInstallDate) {
        this.fieldInstallDate = fieldInstallDate;
    }

    /* Postal Code */
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public Date getProgramSignupDate() {
        return programSignupDate;
    }
    public void setProgramSignupDate(Date programSignupDate) {
        this.programSignupDate = programSignupDate;
    }

    public int getDeviceType() {
        return deviceType;
    }
    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    /* Serial Number Range Rule */
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

    /* Service Company Id */
    public int getServiceCompanyId() {
        return serviceCompanyId;
    }
    public void setServiceCompanyId(int serviceCompanyId) {
        this.serviceCompanyId = serviceCompanyId;
    }
    
    public String getUnenrolled() {
        return unenrolled;
    }
    public void setUnenrolled(String unenrolled) {
        this.unenrolled = unenrolled;
    }

    /* Warehouse Id */
    public int getWarehouseId() {
        return warehouseId;
    }
    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }
}