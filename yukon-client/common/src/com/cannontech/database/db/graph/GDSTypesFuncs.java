/*
 * Created on Jan 20, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.graph;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class GDSTypesFuncs implements GDSTypes
{
	public static int getTypeInt(String type)
	{
		for( int i = 0; i < validTypeStrings.length; i++)
		{
			if( validTypeStrings[i].equalsIgnoreCase(type) )
				return validTypeInts[i];
		}
//		TYPE FOUND MAY BE OF A DATE.  AN ACTUAL DATE OR THE DATE FORMAT MAYBE FOUND INSTEAD
		if( com.cannontech.util.ServletUtil.parseDateStringLiberally(type) != null
			||  type.equalsIgnoreCase("mm/dd/yy"))
			return DATE_GRAPH_TYPE;
		
//		TYPE NOT FOUND, default to Graph		
		return BASIC_GRAPH_TYPE;
	}
	public static String getType(int type)
	{
		// must take off primary point mask because there is no string representation of it.
		if( isPrimaryType(type))
			type = type - PRIMARY_TYPE;
	
		for( int i = 0; i < validTypeInts.length; i++)
		{
			if( validTypeInts[i] == type )
				return validTypeStrings[i];
		}
	
		// TYPE NOT FOUND, default to Graph
		return BASIC_GRAPH_TYPE_STRING;
	}

	public static boolean isGraphType(int type)
	{
		if((type & GRAPH_TYPE) == GRAPH_TYPE)
			return true;

		return false;
	}
	public static boolean isYesterdayType(int type)
	{
		if ((type & YESTERDAY_TYPE) == YESTERDAY_TYPE)
			return true;

		return false;
	}
	public static boolean isPeakType(int type)
	{
		if ((type & PEAK_TYPE) == PEAK_TYPE)
			return true;

		return false;
	}
	public static boolean isUsageType(int type)
	{
		if ((type & USAGE_TYPE) == USAGE_TYPE)
			return true;

		return false;
	}
	public static boolean isPrimaryType(int type)	//OLD PEAK
	{
		if ((type & PRIMARY_TYPE) == PRIMARY_TYPE)
			return true;

		return false;
	}
	public static boolean isMarkerType(int type)
	{
		if((type & MARKER_TYPE) == MARKER_TYPE)
			return true;

		return false;
	}

	public static boolean isDateType(int type)
	{
		if((type & DATE_TYPE) == DATE_TYPE)
			return true;

		return false;
	}
}
