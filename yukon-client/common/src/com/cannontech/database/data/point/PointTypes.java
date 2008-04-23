package com.cannontech.database.data.point;

import com.cannontech.database.data.pao.TypeBase;

/**
 * This type was created in VisualAge.
 */
public final class PointTypes implements IPointOffsets, TypeBase
{
	//System point id constants - these mirror the #defines
	// in pointtypes.h
	public static final int SYS_PID_SYSTEM			=  0;
	public static final int SYS_PID_PORTER 			= -1;
	public static final int SYS_PID_SCANNER 		= -2;
	public static final int SYS_PID_DISPATCH		= -3;
	public static final int SYS_PID_MACS	 		= -4;
	public static final int SYS_PID_CAPCONTROL 		= -5;
    public static final int SYS_PID_NOTIFCATION     = -6;
	public static final int SYS_PID_LOADMANAGEMENT	= -10;
	
	//System point id constants - client
	public static final int SYS_PID_THRESHOLD	=	-100;
    public static final int SYS_PID_MULTISPEAK  =   -110;

	//The following constants are keys into the private
	//Array of strings (not related to any c/c++ defines!)
	//The Point Types
	public static final int STATUS_POINT = 0 + TypeBase.POINT_OFFSET;
	public static final int ANALOG_POINT = 1 + TypeBase.POINT_OFFSET;
	public static final int PULSE_ACCUMULATOR_POINT = 2 + TypeBase.POINT_OFFSET;
	public static final int DEMAND_ACCUMULATOR_POINT = 3 + TypeBase.POINT_OFFSET;
	public static final int CALCULATED_POINT = 4 + TypeBase.POINT_OFFSET;
	public static final int STATUS_OUTPUT_POINT = 5 + TypeBase.POINT_OFFSET;
	public static final int ANALOG_OUTPUT_POINT = 6 + TypeBase.POINT_OFFSET;
	public static final int SYSTEM_POINT = 7 + TypeBase.POINT_OFFSET;
	public static final int CALCULATED_STATUS_POINT = 8 + TypeBase.POINT_OFFSET;

	public static final int INVALID_POINT = 9 + TypeBase.POINT_OFFSET;
	
	//Control Type constants
	public static final int CONTROLTYPE_NONE = 20 + TypeBase.POINT_OFFSET;
	public static final int CONTROLTYPE_LATCH = 21 + TypeBase.POINT_OFFSET;
	public static final int CONTROLTYPE_NORMAL = 22 + TypeBase.POINT_OFFSET;
	public static final int CONTROLTYPE_PSEUDO = 23 + TypeBase.POINT_OFFSET;

	//Accumulator point reading types
	public static final int ACCUMULATOR_DIALREAD = 24 + TypeBase.POINT_OFFSET;
	public static final int ACCUMULATOR_DEMAND = 25 + TypeBase.POINT_OFFSET;
	public static final int ACCUMULATOR_PEAKDEMAND = 26 + TypeBase.POINT_OFFSET;

	//Transducer Types
	public static final int TRANSDUCER_NONE = 27 + TypeBase.POINT_OFFSET;
	public static final int TRANSDUCER_PSEUDO = 28 + TypeBase.POINT_OFFSET;
	public static final int TRANSDUCER_DIGITAL = 29 + TypeBase.POINT_OFFSET;
	public static final int TRANSDUCER_01MA = 30 + TypeBase.POINT_OFFSET;
	public static final int TRANSDUCER_420MA = 31 + TypeBase.POINT_OFFSET;

	//Data filter types
	public static final int DATAFILTERTYPE_NONE = 32 + TypeBase.POINT_OFFSET;
	public static final int DATAFILTERTYPE_USELAST = 33 + TypeBase.POINT_OFFSET;
	public static final int DATAFILTERTYPE_USEDEFAULT = 34 + TypeBase.POINT_OFFSET;
	
	public static final int LP_PEAK_REPORT = 41 + TypeBase.POINT_OFFSET;
	public static final int LP_ARCHIVED_DATA = 42 + TypeBase.POINT_OFFSET;
	
	//More Control Type constants
	public static final int CONTROLTYPE_SBO_LATCH = 43 + TypeBase.POINT_OFFSET;
	public static final int CONTROLTYPE_SBO_PULSE = 44 + TypeBase.POINT_OFFSET;
	
	public static final int[] SCANABLE_POINT_TYPES = {PULSE_ACCUMULATOR_POINT, DEMAND_ACCUMULATOR_POINT};
	
	
	//All the strings associated with points and the database
	private static final String[] pointStrings = 
	{
		// Point Types
		"Status",
		"Analog",
		"PulseAccumulator",
		"DemandAccumulator",
		"CalcAnalog",
		"StatusOutput", //5
		"AnalogOutput",
		"System",
		"CalcStatus",
		"INVALID",   //9
		"","","","","","","","","","",  //room for future point types
		"None",  	//20
		"Latch",
		"Normal",
		"Pseudo",
		"Dial Read",
		"Demand",
		"Peak Demand",  //26
		"None",
		"Pseudo",
		"Digital",
		"0-1ma",
		"4-20ma",
		"None",
		"Use Last",
		"Use Default", //34
		"Outage 1",
		"Outage 2",
		"Outage 3",
		"Outage 4",
		"Outage 5",
		"Outage 6", //40
		"LP Peak Report",
		"LP Archived Data",
		"SBO Latch",
		"SBO Pulse"		
	};

	//point archive types
	public static final String ARCHIVE_NONE = "None";
	public static final String ARCHIVE_ON_CHANGE = "On Change";
	public static final String ARCHIVE_ON_TIMER = "On Timer";  // added the 'r' to match db editor
	public static final String ARCHIVE_ON_UPDATE = "On Update";
	public static final String ARCHIVE_ON_TIMER_OR_UPDATE = "timer|update"; // UI calls this "On Timer Or Update"

	//point update types
	public static final String UPDATE_FIRST_CHANGE = "On First Change";
	public static final String UPDATE_ALL_CHANGE = "On All Change";
	public static final String UPDATE_TIMER = "On Timer";
	public static final String UPDATE_TIMER_CHANGE = "On Timer+Change";
	public static final String UPDATE_HISTORICAL = "Historical";


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param typeEnum int
 */
public final static String getType(int typeEnum) {

	if( typeEnum < 0 || typeEnum > pointStrings.length - 1 )
			throw new Error("PointTypes::getType(int) - received unknow type: " + typeEnum );			
	else
		return pointStrings[typeEnum];
	
}
/**
 * This method was created in VisualAge.
 * @return int
 * @param typeStr java.lang.String
 */
public final static int getType(String typeStr) {

	//Go through the point strings array and return it's index
	//when we find it
	for( int i = 0; i < pointStrings.length; i++ )
	{
		if( pointStrings[i].equalsIgnoreCase(typeStr) )
			return i;
	}

	//Must not have found it
	throw new Error("PointTypes::getType(String) - Unrecognized type:  '" + typeStr + "'");
}

public static String[] convertPointTypes(Integer[] pointTypes) {
    if(pointTypes == null) {
        return new String[0];
    }
    String[] pointTypesStr = new String[pointTypes.length];

    for(int i = 0; i < pointTypes.length; i++) {
        pointTypesStr[i] = PointTypes.getType(pointTypes[i]);
    }
    return pointTypesStr;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param typeEnum int
 */
public final static boolean hasControl(String controlString) 
{

	return controlString.equalsIgnoreCase(PointTypes.getType(PointTypes.CONTROLTYPE_NORMAL))
				|| controlString.equalsIgnoreCase(PointTypes.getType(PointTypes.CONTROLTYPE_LATCH))
				|| controlString.equalsIgnoreCase(PointTypes.getType(PointTypes.CONTROLTYPE_SBO_LATCH))
				|| controlString.equalsIgnoreCase(PointTypes.getType(PointTypes.CONTROLTYPE_SBO_PULSE));
	
}
/**
 * This method was created in VisualAge.
 * @return int
 * @param typeStr java.lang.String
 */
public final static boolean isValidPointType( int ptType )
{
	return( ptType == com.cannontech.database.data.point.PointTypes.ANALOG_POINT
		 	 || ptType == com.cannontech.database.data.point.PointTypes.STATUS_POINT
		 	 || ptType == com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT
		 	 || ptType == com.cannontech.database.data.point.PointTypes.DEMAND_ACCUMULATOR_POINT
		 	 || ptType == com.cannontech.database.data.point.PointTypes.CALCULATED_POINT 
		 	 || ptType == com.cannontech.database.data.point.PointTypes.CALCULATED_STATUS_POINT);
}
}
