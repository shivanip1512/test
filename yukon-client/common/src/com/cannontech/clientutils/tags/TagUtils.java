package com.cannontech.clientutils.tags;

/**
 * Insert the type's description here.
 * Creation date: (1/9/2001 2:56:48 PM)
 * @author: 
 */
import com.cannontech.message.dispatch.message.Signal;

public final class TagUtils {
/**
 * TagUtils constructor comment.
 */
private TagUtils() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2001 1:32:02 PM)
 * @param tags int
 */
private static void checkAlarmStateValidity(int tags) throws IllegalAlarmSateException
{
	//if( isAnyAlarm(tags)  && ((tags & Signal.TAG_UNACKNOWLEDGED_ALARM) == Signal.TAG_UNACKNOWLEDGED_ALARM) )
		//throw new IllegalAlarmSateException("Alarm tags(X) = '" + Integer.toHexString(tags) + "' are invalid.");
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/00 3:35:39 PM)
 * @return boolean
 * Builds up a string representing all the tags states we are in.
 */
public static String getTagString(int tags) 
{
	StringBuffer buff = new StringBuffer();
	
	if( isAlarmActive(tags) )
		buff.append( buff.length() > 0 ? "|ALRM" : "ALRM" );
		
	if( !isControlEnabled(tags) )
		buff.append( buff.length() > 0 ? "|NoCntrl" : "NoCntrl" );
		
	if( isControlPending(tags) )
		buff.append( buff.length() > 0 ? "|PendCntrl" : "PendCntrl" );
		
	if( isDeviceOutOfService(tags) || isPointOutOfService(tags) )
		buff.append( buff.length() > 0 ? "|Disabled" : "Disabled" );
		
		
	if( buff.length() <= 0 )
		return "Normal";
	else
		return buff.toString();
}

/**
 	public final static int TAG_ACTIVE_ALARM					= 0x80000000; //active
	public final static int TAG_UNACKNOWLEDGED_ALARM		= 0x40000000; //tag_unack
* 
 * true means the alarm is not cleared and has been ACKED 
 **/
public static boolean isAlarmActive(int tags)
{
	try
	{
		checkAlarmStateValidity(tags);
		return
			(tags & Signal.TAG_ACTIVE_ALARM) != 0;
	}
	catch( IllegalAlarmSateException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}

}
/**
 * Insert the method's description here.
 * Creation date: (8/9/00 3:35:39 PM)
 * @return boolean
 */
/* returning true means the alarm is not cleared and is blinking */
public static boolean isAlarmUnacked(int tags) 
{
	try
	{
		checkAlarmStateValidity(tags);
		return 
			(tags & Signal.TAG_UNACKNOWLEDGED_ALARM) != 0;
	}
	catch( IllegalAlarmSateException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (8/9/00 3:35:39 PM)
 * @return boolean
 */
public static boolean isAnyAlarm(int tags) 
{
	return ( (tags & Signal.MASK_ANY_ALARM) != 0 );
}

public static boolean isControlPending(int tags) 
{
	return ( (tags & Signal.TAG_CONTROL_PENDING) != 0 );
}

/**
 * Insert the method's description here.
 * Creation date: (8/31/00 12:00:37 PM)
 * @param tags long
 */
public static boolean isControlEnabled(long tags ) 
{
	return( (tags & Signal.MASK_ANY_CONTROL_DISABLE) == 0 );
}
/**
 * Insert the method's description here.
 * Creation date: (8/31/00 12:00:37 PM)
 * @param tags long
 */
public static boolean isControllablePoint(long tags ) 
{
	return( (tags & Signal.TAG_ATTRIB_CONTROL_AVAILABLE) != 0 );
}
/**
 * Insert the method's description here.
 * Creation date: (8/31/00 12:00:37 PM)
 * @param tags long
 */
public static boolean isDeviceControlInhibited(long tags) 
{
	return( (tags & Signal.TAG_DISABLE_CONTROL_BY_DEVICE) > 0 );	
}
/**
 * Insert the method's description here.
 * Creation date: (8/31/00 12:00:37 PM)
 * @param tags long
 */
public static boolean isDeviceOutOfService(long tags) 
{
	return( (tags & Signal.TAG_DISABLE_DEVICE_BY_DEVICE) > 0 );	
}
/**
 * Insert the method's description here.
 * Creation date: (8/31/00 12:00:37 PM)
 * @param tags long
 */
public static boolean isPointControlInhibited(long tags) 
{
	return( (tags & Signal.TAG_DISABLE_CONTROL_BY_POINT) > 0 );
}
/**
 * Insert the method's description here.
 * Creation date: (8/31/00 12:00:37 PM)
 * @param tags long
 */
public static boolean isPointOutOfService(long tags) 
{
	return( (tags & Signal.TAG_DISABLE_POINT_BY_POINT) > 0 );	
}

}