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
public interface ScriptParameters
{
    public static final String COMMENT = "#";
    public static final String ENDLINE = "\r\n";
    public static final String SET = "set ";

    //Strings for code block comments
    public static final String START = "START_";
    public static final String END = "END_";
    
    public static final String HEADER = "HEADER";
    public static final String PARAMETER_LIST = "PARAMETER_LIST";
    public static final String MAIN_CODE = "MAIN_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";
    public static final String BILLING = "BILLING";
    public static final String FOOTER = "FOOTER";

    /** Strings for the file parameters, allows us to re/generate or edit an existing file*/
    /** When adding new parameters, please add set default value and a description text in paramToValueMap and paramToDescMap getters*/
    public static final String SCHEDULE_NAME_PARAM = "ScheduleName";
    public static final String SCRIPT_FILE_NAME_PARAM = "ScriptFileName";
    public static final String SCRIPT_DESC_PARAM = "ScriptDescription";
    public static final String GROUP_NAME_PARAM = "GroupName";
    public static final String GROUP_TYPE_PARAM = "GroupType";
    public static final String PORTER_TIMEOUT_PARAM = "PorterTimeout";
    public static final String FILE_PATH_PARAM = "FilePath";
    public static final String MISSED_FILE_NAME_PARAM = "MissedFileName";
    public static final String SUCCESS_FILE_NAME_PARAM = "SuccessFileName";
    
    //Notification parameters
    public static final String NOTIFICATION_FLAG_PARAM = "NotificationFlag";
    public static final String NOTIFY_GROUP_PARAM = "NotifyGroup";
    public static final String EMAIL_SUBJECT_PARAM = "EmailSubject";
    
    //Retry (within a read script by collectiongroup, not a retry read of a list)
    public static final String READ_WITH_RETRY_FLAG_PARAM = "ReadWithRetryFlag";
    public static final String RETRY_COUNT_PARAM = "RetryCount";
    public static final String QUEUE_OFF_COUNT_PARAM = "QueueOffCount";
    public static final String MAX_RETRY_HOURS_PARAM = "MaxRetryHours";
    
    //Billing parameters
    public static final String BILLING_FLAG_PARAM = "BillingFlag";
    public static final String BILLING_FILE_NAME_PARAM = "BillingFileName";
    public static final String BILLING_FILE_PATH_PARAM = "BillingFilePath";
    public static final String BILLING_FORMAT_PARAM = "BillingFormat";
    public static final String BILLING_GROUP_TYPE_PARAM = "BillingGroupType";
    public static final String BILLING_ENERGY_DAYS_PARAM = "BillingEnergyDays";
    public static final String BILLING_DEMAND_DAYS_PARAM = "BillingDemandDays";
    
    //IED parameters
    public static final String IED_FLAG_PARAM = "IEDFlag";
    public static final String TOU_RATE_PARAM = "TOURate";
    public static final String RESET_COUNT_PARAM = "ResetCount";
    public static final String READ_FROZEN_PARAM = "ReadFrozen";    

    //VERY CUSTOM, string command to Read Frozen 
    public static final String READ_FROZEN_ALPHA_COMMAND_STRING = "putconfig emetcon ied class 72 02";
	public static final String READ_FROZEN_S4_COMMAND_STRING = "putconfig emetcon ied class 0 1";
}
