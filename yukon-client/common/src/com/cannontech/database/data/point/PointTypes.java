package com.cannontech.database.data.point;

/**
 * This type was created in VisualAge.
 */
public final class PointTypes implements IPointOffsets
{
	//System point id constants - these mirror the #defines
	// in pointtypes.h
	public static final int SYS_PID_SYSTEM			=	0;
	public static final int SYS_PID_PORTER 			=	-1;
	public static final int SYS_PID_SCANNER 		= 	-2;
	public static final int SYS_PID_DISPATCH		=   -3;
	public static final int SYS_PID_MACS	 		=	-4;
	public static final int SYS_PID_LOADMANAGEMENT	=	-10;


	
	//The following constants are keys into the private
	//Array of strings (not related to any c/c++ defines!)
	//The Point Types
	public static final int STATUS_POINT = 0;
	public static final int ANALOG_POINT = 1;
	public static final int PULSE_ACCUMULATOR_POINT = 2;
	public static final int DEMAND_ACCUMULATOR_POINT = 3;	
	public static final int CALCULATED_POINT = 4;
	public static final int STATUS_OUTPUT_POINT = 5;
	public static final int ANALOG_OUTPUT_POINT = 6;
	public static final int SYSTEM_POINT = 7;
	public static final int INVALID_POINT = 8;

	//Control Type constants
	public static final int CONTROLTYPE_NONE = 20;
	public static final int CONTROLTYPE_LATCH = 21;
	public static final int CONTROLTYPE_NORMAL = 22;
	public static final int CONTROLTYPE_PSEUDO = 23;
	
	//Accumulator point reading types
	public static final int ACCUMULATOR_DIALREAD = 24;
	public static final int ACCUMULATOR_DEMAND = 25;
	public static final int ACCUMULATOR_PEAKDEMAND = 26;

	//Transducer Types
	public static final int TRANSDUCER_NONE = 27;
	public static final int TRANSDUCER_PSEUDO = 28;
	public static final int TRANSDUCER_DIGITAL = 29;
	public static final int TRANSDUCER_01MA = 30;
	public static final int TRANSDUCER_420MA = 31;

	//Data filter types
	public static final int DATAFILTERTYPE_NONE = 32;
	public static final int DATAFILTERTYPE_USELAST = 33;
	public static final int DATAFILTERTYPE_USEDEFAULT = 34;
	
	
	//All the strings associated with points and the database
	private static final String[] pointStrings = 
	{
		// Point Types
		"Status",
		"Analog",
		"PulseAccumulator",
		"DemandAccumulator",
		"CalcAnalog",
		"StatusOutput",
		"AnalogOutput",
		"System",
		"INVALID",   //8
		"","","","","","","","","","","",  //9-19 (room for future point types)
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
		"Use Default" //34
	};	
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
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param typeEnum int
 */
public final static boolean hasControl(String controlString) 
{

	return controlString.equalsIgnoreCase(PointTypes.getType(PointTypes.CONTROLTYPE_NORMAL))
				|| controlString.equalsIgnoreCase(PointTypes.getType(PointTypes.CONTROLTYPE_LATCH) );
	
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
		 	 || ptType == com.cannontech.database.data.point.PointTypes.CALCULATED_POINT );
}
}
