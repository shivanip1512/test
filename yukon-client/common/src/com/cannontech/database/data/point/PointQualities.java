package com.cannontech.database.data.point;

/**
 * This type was created in VisualAge.
 */
public final class PointQualities 
{
	// point qualities constants - these mirror the #defines
	// in in a .h file on the C++ side.
	public static final int UNINTIALIZED_QUALITY = 0;
	public static final int INIT_DEFAULT_QUALITY = 1;
	public static final int INIT_LAST_KNOWN_QUALITY = 2;
	public static final int NON_UPDATED_QUALITY = 3;
	public static final int MANUAL_QUALITY = 4;
	public static final int NORMAL_QUALITY = 5;
	public static final int EXCEEDS_LOW_QUALITY = 6;
	public static final int EXCEEDS_HIGH_QUALITY = 7;		
	public static final int ABNORMAL_QUALITY = 8;
	public static final int UNKNOWN_QUALITY = 9;
	public static final int INVALID_QUALITY = 10;
	public static final int PARTIAL_INTERVAL_QUALITY = 11;
	public static final int DEVICE_FILLER_QUALITY = 12;
	public static final int QUESTIONABLE_QUALITY = 13;
	public static final int OVERFLOW_QUALITY = 14;
	public static final int POWERFAIL_QUALITY = 15;
	public static final int UNREASONABLE_QUALITY = 16;
	public static final int CONSTANT_QUALITY = 17;
	
	//All the strings associated with points and the database
	private static final String[][] pointStrings = 
	{
		// Point Qualities
		{"Uninit","Uninitialized"},
		{"Init-Def", "Init Default" },
		{"Init-Last", "Init Last Known"},
		{"Non", "Non Updated"},
		{"Man", "Manual"},
		{"Norm", "Normal"},
		{"Exc-Low", "Exceeds Low"},
		{"Exc-High", "Exceeds High"},
		{"Abnormal", "Abnormal"},   //8
		{"Unknown", "Unknown"},
		{"Inv", "Invalid"},
		{"Part-Int", "Partial Interval"},
		{"Dev-Fill", "Device Filler"},
		{"Quest", "Questionable"},
		{"Overflow", "Overflow"},
		{"Pow-Fail", "Power Fail"},
		{"Unr", "Unreasonable"}, //16
		{"Const", "Constant"}
	};	
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param typeEnum int
 */
public final static String getQuality(int typeEnum) throws CTIPointQuailtyException 
{
	if( typeEnum < 0 || typeEnum > pointStrings.length - 1 )		
		throw new CTIPointQuailtyException("PointQualities::getQuality(int) - received unknown type: " + typeEnum );
	else
		return pointStrings[typeEnum][1];
}

public final static String getQualityAbreviation(int typeEnum) throws CTIPointQuailtyException
{
	if( typeEnum < 0 || typeEnum > pointStrings.length - 1 )
		throw new CTIPointQuailtyException("PointQualities::getQualityAbreviation(int) - received unknown type: " + typeEnum );
	else
		return pointStrings[typeEnum][0];
}

/**
 * This method was created in VisualAge.
 * @return int
 * @param typeStr java.lang.String
 */
public final static int getQuality(String typeStr) throws CTIPointQuailtyException
{
	//Go through the point strings array and return it's index
	//when we find it
	for( int i = 0; i < pointStrings.length; i++ )
	{
		if( pointStrings[i].equals(typeStr) )
			return i;
	}

	//Must not have found it
	throw new CTIPointQuailtyException("PointQuality::getQuality(String) - Unrecognized type:  " + typeStr);
}
}
