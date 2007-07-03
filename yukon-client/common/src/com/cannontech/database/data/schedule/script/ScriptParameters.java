/*
 * Created on Dec 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.database.data.schedule.script;

/**
 * @author stacey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public enum ScriptParameters
{
    /** Strings for the file parameters, allows us to re/generate or edit an existing file*/
    /** When adding new parameters, please add set default value and a description text in paramToValueMap and paramToDescMap getters*/
    SCHEDULE_NAME_PARAM("ScheduleName"),
    SCRIPT_FILE_NAME_PARAM("ScriptFileName"),
    SCRIPT_DESC_PARAM("ScriptDescription"),
    GROUP_NAME_PARAM("GroupName"),
    GROUP_TYPE_PARAM("GroupType"), // this isn't so much a group as a keyword now
    PORTER_TIMEOUT_PARAM("PorterTimeout"),
    FILE_PATH_PARAM("FilePath"),
    MISSED_FILE_NAME_PARAM("MissedFileName"),
    SUCCESS_FILE_NAME_PARAM("SuccessFileName"),
    
    //Notification parameters
    NOTIFICATION_FLAG_PARAM("NotificationFlag"),
    NOTIFY_GROUP_PARAM("NotifyGroup"),
    EMAIL_SUBJECT_PARAM("EmailSubject"),
    
    //Retry (within a read script by collectiongroup, not a retry read of a list)
    READ_WITH_RETRY_FLAG_PARAM("ReadWithRetryFlag"),
    RETRY_COUNT_PARAM("RetryCount"),
    QUEUE_OFF_COUNT_PARAM("QueueOffCount"),
    MAX_RETRY_HOURS_PARAM("MaxRetryHours"),
    
    //Billing parameters
    BILLING_FLAG_PARAM("BillingFlag"),
    BILLING_FILE_NAME_PARAM("BillingFileName"),
    BILLING_FILE_PATH_PARAM("BillingFilePath"),
    BILLING_FORMAT_PARAM("BillingFormat"),
    BILLING_GROUP_TYPE_PARAM("BillingGroupType"), // this isn't so much a group as a keyword now
    BILLING_ENERGY_DAYS_PARAM("BillingEnergyDays"),
    BILLING_DEMAND_DAYS_PARAM("BillingDemandDays"),
    BILLING_GROUP_NAME_PARAM("BillGroupName"),
    
    //IED parameters
    IED_FLAG_PARAM("IEDFlag"),
    TOU_RATE_PARAM("TOURate"),
    RESET_COUNT_PARAM("ResetCount"),
    READ_FROZEN_PARAM("ReadFrozen"),    
    ;
    
    private final String value;

    private ScriptParameters(String value) {
        this.value = value;
    }
    
    public static ScriptParameters lookup(String value) {
        ScriptParameters[] parameters = ScriptParameters.values();
        for (ScriptParameters parameter : parameters) {
            if (parameter.value.equals(value)) {
                return parameter;
            }
        }
        throw new IllegalArgumentException("Can't find a value for: " + value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}
