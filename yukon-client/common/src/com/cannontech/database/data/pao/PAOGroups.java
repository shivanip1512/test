package com.cannontech.database.data.pao;


/**
 * @deprecated Use PaoCategory, PaoClass, PaoType enums
 */
@Deprecated
public final class PAOGroups implements RouteTypes, DeviceTypes, CapControlTypes
{
	public final static int INVALID = -1;
   	public final static int CAT_DEVICE = 0;
   	public final static int CAT_ROUTE = 1;
   	public final static int CAT_PORT = 2;
   	public final static int CAT_CUSTOMER = 3;
   	public final static int CAT_CAPCONTROL = 4;
   	public final static int CAT_LOADCONTROL = 5;
	  
   	public static final String STRING_CAT_DEVICE = "DEVICE";
   	public static final String STRING_CAT_ROUTE = "ROUTE";
   	public static final String STRING_CAT_PORT = "PORT";
   	public static final String STRING_CAT_CUSTOMER = "CUSTOMER";
   	public static final String STRING_CAT_CAPCONTROL = "CAPCONTROL";
   	public static final String STRING_CAT_LOADMANAGEMENT = "LOADMANAGEMENT";
   	public static final String STRING_CAT_MACS_SCHEDULE = "SCHEDULE";
   	public static final String STRING_INVALID = "(invalid)";


   	//PAOCategorys that have the same PAOClass as the category
   	public static final int CLASS_ROUTE = CAT_ROUTE;
   	public static final int CLASS_PORT = CAT_PORT;
   	public static final int CLASS_CUSTOMER = CAT_CUSTOMER;
   	public static final int CLASS_CAPCONTROL = CAT_CAPCONTROL;
   	public static final int CLASS_LOADMANAGEMENT = CAT_LOADCONTROL;
}
