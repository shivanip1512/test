package com.cannontech.clientutils.commonutils;

/**
 * Insert the type's description here.
 * Creation date: (6/14/00 11:08:03 AM)
 * @author: 
 */
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.cannontech.clientutils.CTILogger;

public class ModifiedDate extends java.util.Date 
{
	//default pattern for this guy
	private static SimpleDateFormat formatter_def = 
				new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

	//month, day, year, hour, minutes
	private static SimpleDateFormat formatter_nosecs = 
				new SimpleDateFormat("MM-dd-yyyy HH:mm");

	//month, day, hours, minutes
	private static SimpleDateFormat formatter_nosecs_noyr = 
				new SimpleDateFormat("MM-dd HH:mm");

	//month, day, year, hour, minutes, timezone
	private static SimpleDateFormat formatter_nosecs_tz = 
		new SimpleDateFormat("MM-dd-yyyy HH:mm z");


	public static final short FRMT_DEFAULT = 0;
	public static final short FRMT_NOSECS = 1;
	public static final short FRMT_NOSECS_NOYR = 2;
	public static final short FRMT_NOSECS_TZ = 3;
	
	
	//private boolean showSecs = true;
	private final short _formatID;// = FRMT_DEFAULT;

	/**
	 * ModifiedDate constructor comment.
	 */
	public ModifiedDate() {
		this( FRMT_DEFAULT );
	}

	public ModifiedDate( short formatID ) {
		super();
		_formatID = formatID;
	}

	private SimpleDateFormat getFormatter()
	{
		switch( _formatID )
		{
			case FRMT_NOSECS:
				return formatter_nosecs;
			case FRMT_NOSECS_NOYR:
				return formatter_nosecs_noyr;
			case FRMT_NOSECS_TZ:
				return formatter_nosecs_tz;
				
			default:
				return formatter_def;
		}
	}

	/**
	 * ModifiedDate constructor comment.
	 * @param date long
	 */
	public ModifiedDate(long date) {
		this( date, FRMT_DEFAULT );
	}

	public ModifiedDate(long date, short formatID) {
		super(date);
		_formatID = formatID;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (6/14/00 11:08:21 AM)
	 */
	public String getDateString() 
	{
		return toString();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/14/00 11:08:21 AM)
	 */
	public String getTimeString() 
	{
		return toString();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (6/14/00 11:08:21 AM)
	 */
	public String toString() 
	{
		return getFormatter().format( this );
	}

}
