package com.cannontech.billing;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 2:08:15 PM)
 * @author: 
 */
public final class FileFormatTypes {

	// Constants representing types of file formats
	// ** THESE MUST MATCH THE DATABASE TABLE BILLINGFILEFORMATS **
	// ** IF YOU ADD TO THESE VALUES, YOU MUST ADD TO THE DATABASE
	// ** TABLE THE FORMATS THAT ARE VALID FOR EACH CUSTOMER.
	public static final int INVALID = -1;
	public static final int SEDC = 0;
	public static final int CADP = 1;
	public static final int CADPXL2 = 2;
	public static final int WLT_40 = 3;
	public static final int CTICSV = 4;
	public static final int OPU = 5;
	public static final int DAFFRON = 6;
	public static final int CADPXL1 = 7;
	public static final int NCDC = 8;
	public static final int CTIStandard2 = 9;
	public static final int MVRS = 10;
/*

	
	public static final String FORMAT_MVRS = "MVRS";
	public static final String FORMAT_SEDC = "SEDC";
	public static final String FORMAT_CAPD = "CAPD";
	public static final String FORMAT_CAPDXL2 = "CAPDXL2";
	public static final String FORMAT_NCDC = "NCDC";
	public static final String FORMAT_CTISTANDARD2 = "CTIStandard2";
	public static final String FORMAT_CTICSV = "CTI-CSV";
	public static final String FORMAT_OPU = "OPU";
	public static final String FORMAT_WLT_40 = "WLT-40";
*/
	//Set DEFAULT array values in case the table doesn't exist.
	private static int[] validFormatIds =
	{
		SEDC, CADP, CADPXL2, WLT_40, CTICSV, OPU, DAFFRON
	};
	
	private static String[] validFormatTypes =
	{
		"SEDC", "CADP", "CADPXL2", "WLT-40", "CTI-CSV", "OPU", "DAFFRON"
	};
/*
	//***  IF CHANGEING THESE, MUST DOUBLE CHECK THE QUERY THEY USE ***
	//ONLY THE FIRST 4 WERE PAO'D QUERIES, THE OTHERS HAVE BEEN LEFT BEHIND
	public static final String[] SUPPORTED_FORMATS =
	{
		FORMAT_SEDC,
		FORMAT_CAPD,
		FORMAT_CAPDXL2,
		FORMAT_WLT_40,
		FORMAT_CTICSV,
		FORMAT_OPU
		//FORMAT_MVRS,
		//FORMAT_NCDC,
		//FORMAT_CTISTANDARD2,
	};
	*/
	

/**
 * This method was created in VisualAge.
 * @return int
 * @param typeStr java.lang.String
 */
public final static int getFormatID(String typeStr)
{
	//Go through the valid FormatTypes Array and return 
	//  the int associated with it from valid FormatIDs Array
	for(int i = 0; i < validFormatTypes.length; i++)
	{
		if( validFormatTypes[i].equalsIgnoreCase(typeStr) )
			return validFormatIds[i];
	}

	//Must not have found it
	throw new Error("FileFormatTypes::getFormatID(String) - Unrecognized type: " + typeStr);
}
/**
 * Insert the method's description here.
 * Creation date: (5/18/00 2:34:54 PM)
 */
public final static String getFormatType(int typeEnum) 
{
	//Go through the valid FormatIDs Array and return 
	//  the int associated with it from valid FormatTypes Array
	for(int i = 0; i < validFormatIds.length; i++)
	{
		if( validFormatIds[i] == typeEnum )
			return validFormatTypes[i];
	}

	//Must not have found it
	throw new Error("FileFormatTypes::getFormatType(int) - received unknown type: " + typeEnum );			
}
public static int[] getValidFormatIDs()
{
	return validFormatIds;
}
public static  String[] getValidFormatTypes()
{
	return validFormatTypes;
}
public static void setValidFormatIDs(int[] formatIDs)
{
	validFormatIds = formatIDs;
}
public static void setValiedFormatTypes (String[] formatTypes)
{
	validFormatTypes = formatTypes;
}
}
