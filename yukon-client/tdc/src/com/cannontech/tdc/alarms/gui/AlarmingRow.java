package com.cannontech.tdc.alarms.gui;

/**
 * Insert the type's description here.
 * Creation date: (4/7/00 1:53:50 PM)
 * @author: 
 * @Version: <version>
 */
import com.cannontech.message.dispatch.message.Signal;

public class AlarmingRow 
{
	private Signal signal = null;
	private int rowNumber = -1;
	private int alarmColor = -1;
	private int originalColor = -1;
	private boolean isVisible = false;
/**
 * AlarmingRow constructor comment.
 */
public AlarmingRow( int rowNumber, int alarmColor, int ogColor, Signal signal )
{
	super();

	setRowNumber( rowNumber );
	setAlarmColor( alarmColor );
	setOriginalColor( ogColor );
	setSignal( signal );
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 1:56:16 PM)
 * Version: <version>
 * @return int
 */
public void addOffset( int incrementation )
{
	rowNumber += incrementation;
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 2:04:36 PM)
 * Version: <version>
 * @return boolean
 */
public boolean equals( Object elem ) 
{
	try
	{
		if( Integer.parseInt( elem.toString() ) == this.rowNumber )
			return true;
		else
			return false;
	}
	catch( Throwable t )
	{
		return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/12/2001 1:40:45 PM)
 * @return int
 */
public int getAlarmColor() {
	return alarmColor;
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 1:56:16 PM)
 * Version: <version>
 * @return int
 */
public long getAlarmStateID()
{
	return getSignal().getAlarmStateID();
}
/**
 * Insert the method's description here.
 * Creation date: (1/12/2001 1:40:45 PM)
 * @return int
 */
public int getOriginalColor() {
	return originalColor;
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 1:56:16 PM)
 * Version: <version>
 * @return int
 */
public long getPointID()
{
	return getSignal().getId();
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 1:56:16 PM)
 * Version: <version>
 * @return int
 */
public int getRowNumber()
{
	return rowNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (7/28/00 9:34:28 AM)
 * @return com.cannontech.message.dispatch.message.Signal
 */
public com.cannontech.message.dispatch.message.Signal getSignal() {
	return signal;
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/00 3:41:32 PM)
 * @return boolean
 */
public boolean isBlinking() 
{
	return ( com.cannontech.clientutils.tags.TagUtils.isAlarm(signal.getTags()) );
}
/**
 * Insert the method's description here.
 * Creation date: (1/12/2001 1:40:45 PM)
 * @param newAlarmColor int
 */
public void setAlarmColor(int newAlarmColor) {
	alarmColor = newAlarmColor;
}
/**
 * Insert the method's description here.
 * Creation date: (1/12/2001 1:40:45 PM)
 * @param newOriginalColor int
 */
public void setOriginalColor(int newOriginalColor) {
	originalColor = newOriginalColor;
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 1:56:16 PM)
 * Version: <version>
 * @return int
 */
public void setRowNumber( int rowNum )
{
	rowNumber = rowNum;
}
/**
 * Insert the method's description here.
 * Creation date: (7/28/00 9:34:28 AM)
 * @param newSignal com.cannontech.message.dispatch.message.Signal
 */
public synchronized void setSignal(com.cannontech.message.dispatch.message.Signal newSignal) 
{
	signal = newSignal;
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 1:56:16 PM)
 * Version: <version>
 * @return int
 */
public void subtractOffset( int decrementation )
{
	rowNumber -= decrementation;
}
}
