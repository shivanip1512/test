package com.cannontech.graph.model;

/**
 * Insert the type's description here.
 * Creation date: (7/10/2001 5:01:43 PM)
 * @author: 
 */
public class TrendProperties
{
	private static long resolutionInMillis = 1000;	//default to millis
		
	public static String rangeLabel1 = "Reading";
	public  static String rangeLabel2 = "Reading";
	
	
	public static void setRangeLabel_primary(String newLabel)
	{
		rangeLabel1 = newLabel;
	}
	public static void setRangeLabel_secondary(String newLabel)
	{
		rangeLabel2 = newLabel;
	}
	public static String getRangeLabel_primary()
	{
		return rangeLabel1;
	}
	public static String getRangeLabel_secondary()
	{
		return rangeLabel2;
	}

	/**
	 * Returns the resolutionInMillis.
	 * @return long
	 */
	public static long getResolutionInMillis()
	{
		return resolutionInMillis;
	}

	/**
	 * Sets the resolutionInMillis.
	 * @param resolutionInMillis The resolutionInMillis to set
	 */
	public static void setResolutionInMillis(long resolutionInMillis)
	{
		TrendProperties.resolutionInMillis = resolutionInMillis;
	}

}

