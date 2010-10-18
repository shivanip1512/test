package com.cannontech.stars.xml.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class StarsConstants {
	
	public static final int FAILURE_CODE_RUNTIME_ERROR = 1;
	public static final int FAILURE_CODE_REQUEST_NULL = 2;
	public static final int FAILURE_CODE_RESPONSE_NULL = 3;
	public static final int FAILURE_CODE_NODE_NOT_FOUND = 4;
	public static final int FAILURE_CODE_SESSION_INVALID = 101;
	public static final int FAILURE_CODE_OPERATION_FAILED = 102;
	public static final int FAILURE_CODE_INVALID_PRIMARY_FIELD = 103;

    public static final String STARS_OPERATION = "stars-Operation";
    public static final String STARS_LOGIN = "stars-Login";
    public static final String STARS_LOGOFF = "stars-Logoff";
    public static final String STARS_SEARCHCUSTACCOUNT = "stars-SearchCustomerAccount";
    public static final String STARS_UPDATECUSTACCOUNT = "stars-UpdateCustomerAccount";
    public static final String STARS_SWITCHCOMMAND = "stars-SwitchCommand";
    public static final String STARS_GETLMCTRLHIST = "stars-GetLMControlHistory";
    public static final String STARS_CREATECALLREPORT = "stars-CreateCallReport";
    public static final String STARS_GETCALLREPORTHIST = "stars-GetCallReportHistory";
    public static final String STARS_CREATESERVICEREQ = "stars-CreateServiceRequest";
    public static final String STARS_GETSERVICEREQHIST = "stars-GetServiceRequestHistory";
    public static final String STARS_GETENROLLPROGRAMS = "stars-GetEnrollmentPrograms";
    
    public static final String STARS_SUCCESS = "stars-Success";
    public static final String STARS_FAILURE = "stars-Failure";
}