package com.cannontech.database.data.pao;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * This type was created in VisualAge.
 */
@Deprecated /* Use PaoCategory, PaoClass, PaoType enums */
public final class PAOGroups implements RouteTypes, PortTypes, DeviceTypes, CapControlTypes
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
	
/**
 * This method was created in VisualAge.
 * @return int
 * @param classString java.lang.String
 */
public final static String getCategory( int intCategory ) 
{
	switch( intCategory )
	{
		case CAT_DEVICE:
			return STRING_CAT_DEVICE;
			
		case CAT_PORT:
			return STRING_CAT_PORT;

		case CAT_ROUTE:
			return STRING_CAT_ROUTE;

		case CAT_CUSTOMER:
			return STRING_CAT_CUSTOMER;

		case CAT_CAPCONTROL:
			return STRING_CAT_CAPCONTROL;
			
		case CAT_LOADCONTROL:
			return STRING_CAT_LOADMANAGEMENT;
			
		default:
			return STRING_INVALID;
	}


}
/**
 * This method was created in VisualAge.
 * @return int
 * @param classString java.lang.String
 */
public final static int getCategory( String strCategory ) 
{

	if( strCategory.equalsIgnoreCase(STRING_CAT_DEVICE) )
		return CAT_DEVICE;
	else if( strCategory.equalsIgnoreCase(STRING_CAT_PORT) )
		return CAT_PORT;
	else if( strCategory.equalsIgnoreCase(STRING_CAT_ROUTE) )
		return CAT_ROUTE;
	else if( strCategory.equalsIgnoreCase(STRING_CAT_CUSTOMER) )
		return CAT_CUSTOMER;
	else if( strCategory.equalsIgnoreCase(STRING_CAT_CAPCONTROL) )
		return CAT_CAPCONTROL;
	else if( strCategory.equalsIgnoreCase(STRING_CAT_LOADMANAGEMENT) )
		return CAT_LOADCONTROL;
	else
		return INVALID;
}


/**
 * This method was created in VisualAge.
 * @return int
 * @param classString java.lang.String
 */
public final static int getPAOClass( String category, String paoClass )
{
	//Maybe change the DeviceClass to PAOClasses and have every PaoClass int be unique
	if( paoClass.equalsIgnoreCase(PAOGroups.STRING_CAT_CAPCONTROL) )
	{
		return PAOGroups.CLASS_CAPCONTROL;
	}
	if( category.equalsIgnoreCase(PAOGroups.STRING_CAT_DEVICE)
		 || category.equalsIgnoreCase(PAOGroups.STRING_CAT_LOADMANAGEMENT) )
	{
		return DeviceClasses.getClass( paoClass );
	}
	else if( category.equalsIgnoreCase(PAOGroups.STRING_CAT_ROUTE) )
	{
		return PAOGroups.CLASS_ROUTE;
	}
	else if( category.equalsIgnoreCase(PAOGroups.STRING_CAT_PORT) )
	{
		return PAOGroups.CLASS_PORT;
	}
	else if( category.equalsIgnoreCase(PAOGroups.STRING_CAT_CUSTOMER) )
	{
		return PAOGroups.CLASS_CUSTOMER;
	}
	else
		return PAOGroups.INVALID;
}
/**
 * This method was created in VisualAge.
 * @return int
 * @param classString java.lang.String
 */
public final static String getPAOClass( int category, int paoClass )
{

	//Maybe change the DeviceClass to PAOClasses and have every PaoClass int be unique
	if( category == CAT_CAPCONTROL )
	{
		return PAOGroups.STRING_CAT_CAPCONTROL;
	}
	if( category == CAT_DEVICE
		 || category == CAT_LOADCONTROL )
	{
		return DeviceClasses.getClass( paoClass );
	}
	else if( category == CAT_ROUTE )
	{
		return PAOGroups.STRING_CAT_ROUTE;
	}
	else if( category == CAT_PORT )
	{
		return PAOGroups.STRING_CAT_PORT;
	}
	else if( category == CAT_CUSTOMER )
	{
		return PAOGroups.STRING_CAT_CUSTOMER;
	}
	else
		return STRING_INVALID;
}

/****
 * This method was created in VisualAge.
 * @return int
 * @param typeString java.lang.String
 */
public final static boolean isCapControl( LiteYukonPAObject lite )
{
	return ( (lite.getPaoType().getPaoCategory() == PaoCategory.CAPCONTROL
				   || lite.getPaoType().getPaoCategory() == PaoCategory.DEVICE )
				 && lite.getPaoType().getPaoClass() == PaoClass.CAPCONTROL);
}

/**
 * This method was created in VisualAge.
 * @return boolean
 * @param type java.lang.String
 */
public static final boolean isDialupPort(int type) 
{
	switch( type )
	{
		case DIALOUT_POOL:
		case LOCAL_DIALUP:
		case LOCAL_DIALBACK:
		case TSERVER_DIALUP:
			return true;
		default:
			return false;	
	}

}

/**
 * This method was created in VisualAge.
 * @return boolean
 * @param type java.lang.String
 */
public static final boolean isDialupPort(String type) 
{
	type = type.trim().toLowerCase();
	int intType = PaoType.getPaoTypeId(type);

	return isDialupPort(intType);
}

public static final boolean isTcpPortEligible(int type) {
    PaoType paoType = PaoType.getForId(type);
    return isTcpPortEligible(paoType);
}

public static final boolean isTcpPortEligible(PaoType type) {
    
    switch (type) {
        case CBC_7020:
        case CBC_7022:
        case CBC_7023:
        case CBC_7024:
        case CBC_8020:
        case CBC_8024:
        case CBC_DNP:
        case RTU_DNP:
        case FAULT_CI:
        case NEUTRAL_MONITOR: {
            return true;
        }
        default: {
            return false;
        }
    }
}

/**
 * This method was created in VisualAge.
 * @return int
 * @param typeString java.lang.String
 */
public final static boolean isLoadManagement( LiteYukonPAObject lite )
{
	return( lite.getPaoType().getPaoClass() == PaoClass.GROUP || 
	        lite.getPaoType().getPaoClass() == PaoClass.LOADMANAGEMENT );
}

public final static String[] convertPaoTypes(Integer[] paoTypes) {
    String[] str = new String[paoTypes.length];
    for (int i = 0; i < paoTypes.length; i++) {
        str[i] = PaoType.getPaoTypeString(paoTypes[i]);
    }
    return str;
}

public final static String[] convertPaoCategories(Integer[] paoCategories) {
    String[] str = new String[paoCategories.length];
    for (int i = 0; i < paoCategories.length; i++) {
        str[i] = getCategory(paoCategories[i]);
    }
    return str;
}

}
