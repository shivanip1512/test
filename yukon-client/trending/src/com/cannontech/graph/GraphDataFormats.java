package com.cannontech.graph;

/**
 * Insert the type's description here.
 * Creation date: (7/16/2001 9:47:47 AM)
 * @author: 
 */
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;

public interface GraphDataFormats
{
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	public static SimpleDateFormat extendedDateFormat = new SimpleDateFormat( "MMM d, yyyy");//old datePopupFormat

	
	public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	public static SimpleDateFormat extendedTimeFormat = new SimpleDateFormat("HH:mm:ss");
	
		
	public static SimpleDateFormat dateTimeformat = new SimpleDateFormat("M/dd/yyyy  HH:mm:ss");
	public static SimpleDateFormat extendedDateTimeformat = new SimpleDateFormat("EEE MMM d HH:mm:ss a z yyyy");		

	public static SimpleDateFormat dwellValuesDateTimeformat = new SimpleDateFormat("MMM d, HH:mm:ss");

	public static DecimalFormat valueFormat = new DecimalFormat();
}
