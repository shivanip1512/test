package com.cannontech.clientutils.alarms;

/**
 * Insert the type's description here.
 * Creation date: (1/9/2001 2:56:48 PM)
 * @author: 
 */
import com.cannontech.message.dispatch.message.Signal;

public final class AlarmUtils {
/**
 * AlarmUtils constructor comment.
 */
private AlarmUtils() {
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
 */
/* returning true means the alarm is not cleared and is blinking */
public static boolean isAlarm(int tags) 
{
	try
	{
		checkAlarmStateValidity(tags);
		return ( isAnyAlarm(tags) && ((tags & Signal.MASK_ANY_ALARM) == Signal.MASK_ANY_ALARM) );
	}
	catch( IllegalAlarmSateException e )
	{
		e.printStackTrace(System.out);
		return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/00 3:35:39 PM)
 * @return boolean
 */
/* returning true means the alarm is not cleared and is not blinking */
public static boolean isAlarmAcked(int tags)
{
	try
	{
		checkAlarmStateValidity(tags);
		return ( isAnyAlarm(tags) && ((tags & Signal.TAG_ACKNOWLEDGED_ALARM) == Signal.TAG_ACKNOWLEDGED_ALARM) );
	}
	catch( IllegalAlarmSateException e )
	{
		e.printStackTrace(System.out);
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
}
