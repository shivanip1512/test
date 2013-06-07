package com.cannontech.tdc.alarms.gui;

/**
 * Insert the type's description here.
 * Creation date: (4/10/00 11:16:14 AM)
 * @author: 
 * @Version: <version>
 */
import java.util.Vector;

import com.cannontech.messaging.message.dispatch.SignalMessage;

public class AlarmingRowVector
{
	private Vector alarmingRows = null;
	public static final int ALARM_NOT_FOUND = -1;

/**
 * AlarmingRowVector constructor comment.
 */
public AlarmingRowVector() 
{
	super();

	alarmingRows = new Vector();
}
/**
 * AlarmingRowVector constructor comment.
 * @param initialCapacity int
 */
public AlarmingRowVector(int initialCapacity) 
{
	super();

	alarmingRows = new Vector(initialCapacity);
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 2:04:36 PM)
 * Version: <version>
 */
public void addElement( AlarmingRow alRow_ )
{
	getAlarmingRows().addElement( alRow_ );
}

/**
 * Insert the method's description here.
 * Creation date: (4/7/00 2:04:36 PM)
 * Version: <version>
 * @return boolean
 */
public boolean areRowNumbersGreaterAlarming( int rowNumber )
{
	for( int i = 0; i < getAlarmingRows().size(); i++ )
	{
		if( ((AlarmingRow)getAlarmingRows().elementAt(i)).getRowNumber() > rowNumber )
			return true;
	}
	
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 2:04:36 PM)
 * Version: <version>
 * @return boolean
 */
public boolean areRowNumbersLessAlarming( int rowNumber )
{
	for( int i = 0; i < getAlarmingRows().size(); i++ )
	{
		if( ((AlarmingRow)getAlarmingRows().elementAt(i)).getRowNumber() < rowNumber )
			return true;
	}
	
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 2:04:36 PM)
 * Version: <version>
 * @return boolean
 */
public boolean containsSignal( SignalMessage signal_ )
{
	for( int i = 0; i < getAlarmingRows().size(); i++ )
	{
		if( ((AlarmingRow)getAlarmingRows().get(i)).containsSignal(signal_) )
			return true;
	}
	
	return false;	
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 2:04:36 PM)
 * Version: <version>
 * @return boolean
 */
public boolean contains( Integer rowNum ) 
{
	for( int i = 0; i < getAlarmingRows().size(); i++ )
	{
		if( ((AlarmingRow)getAlarmingRows().elementAt(i)).getRowNumber() == rowNum.intValue() )
			return true;
	}
	
	return false;	
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 11:51:50 AM)
 * @return com.cannontech.tdc.alarms.gui.AlarmingRow
 * @param index int
 */
public AlarmingRow elementAt(int index) 
{
	return (AlarmingRow)getAlarmingRows().elementAt(index);
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 2:04:36 PM)
 * Version: <version>
 * @return boolean
 */
public AlarmingRow getAlarmingRow( int rowNumber )
{
	for( int i = 0; i < getAlarmingRows().size(); i++ )
	{
		if( ((AlarmingRow)getAlarmingRows().elementAt(i)).getRowNumber() == rowNumber )
			return ((AlarmingRow)getAlarmingRows().elementAt(i));
	}
	
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 2:04:36 PM)
 * Version: <version>
 * @return boolean
 */
public int getAlarmingRowLocation( int rowNumber )
{
	for( int i = 0; i < getAlarmingRows().size(); i++ )
	{
		if( ((AlarmingRow)getAlarmingRows().elementAt(i)).getRowNumber() == rowNumber )
			return i;
	}
	
	return ALARM_NOT_FOUND;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 11:48:41 AM)
 * @return java.util.Vector
 */
private java.util.Vector getAlarmingRows() 
{
	if( alarmingRows == null )
		alarmingRows = new Vector(10);
		
	return alarmingRows;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 11:55:14 AM)
 */
public void removeAllElements() 
{
	getAlarmingRows().removeAllElements();	
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 2:04:36 PM)
 * Version: <version>
 * @return boolean
 */
public synchronized void removeElement( Integer rowNum )
{
	for( int i = 0; i < getAlarmingRows().size(); i++ )
	{
		if( ((AlarmingRow)getAlarmingRows().elementAt(i)).getRowNumber() == rowNum.intValue() )
		{
			getAlarmingRows().removeElementAt( i );
			return;
		}
	}	
}


/**
 * Tells all the elements to be silent or not
 * @return void
 */
public synchronized void setAllSilenced( boolean value_ )
{
	for( int i = 0; i < getAlarmingRows().size(); i++ )
		((AlarmingRow)getAlarmingRows().get(i)).setSilenced( value_ );
}

/**
 * Tells us if at least 1 row needs to have sound
 * @return void
 */
public synchronized boolean isAnyRowUnSilenced()
{
	for( int i = 0; i < getAlarmingRows().size(); i++ )
		if( !((AlarmingRow)getAlarmingRows().get(i)).isSilenced() )
			return true; //we have at least 1 row that needs sound
			
	return false;
}

/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 11:55:14 AM)
 */
public void setElementAt( AlarmingRow alarmingRow, int index) 
{
	getAlarmingRows().setElementAt(alarmingRow, index);
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 11:54:13 AM)
 * @return int
 */
public int size() 
{
	return getAlarmingRows().size();
}
}
