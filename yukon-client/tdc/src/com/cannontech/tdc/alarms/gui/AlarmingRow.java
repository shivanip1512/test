package com.cannontech.tdc.alarms.gui;

/**
 * Insert the type's description here.
 * Creation date: (4/7/00 1:53:50 PM)
 * @author: 
 * @Version: <version>
 */
import java.util.HashMap;
import java.util.Iterator;

import com.cannontech.clientutils.tags.IAlarmDefs;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.messaging.message.dispatch.SignalMessage;

public class AlarmingRow 
{
	//private Signal signal = null;
	//signals can be null 
	private HashMap signalHash = null;
	
	private int rowNumber = -1;
	private int alarmColor = -1;
	private int originalColor = -1;
	private boolean isSilenced = false;
	
	private boolean isBlinking = false;


/**
 * AlarmingRow constructor comment.
 */
public AlarmingRow( int rowNumber, int alarmColor, int ogColor )
{
	super();

	setRowNumber( rowNumber );
	setAlarmColor( alarmColor );
	setOriginalColor( ogColor );
//	setSignal( signal );
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
public int getRowNumber()
{
	return rowNumber;
}

public boolean containsSignal( SignalMessage signal_ )
{
	return ( getSignalHash().get(signal_) != null );
}
  
/**
 * @return
 */
private HashMap getSignalHash()
{
	if( signalHash == null )
		signalHash = new HashMap(16);

	return signalHash;
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/00 3:41:32 PM)
 * @return boolean
 */
public synchronized boolean isBlinking() 
{
	return isBlinking;
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
 * @param signal
 * deny our EVENT signals from being in here
 */
public synchronized void updateSignal(SignalMessage signal)
{
	if( signal != null && signal.getCondition() >= IAlarmDefs.MIN_CONDITION_ID )
	{
		getSignalHash().put(signal, signal );
		
		updateBlinkingState();
	}

}

private synchronized void updateBlinkingState()
{
	isBlinking = false;  //reset the blinking attribute
	
	
	//build up the isBlinking value
	Iterator iter = getSignalHash().values().iterator();
	while( iter.hasNext() )
	{
		SignalMessage sig = (SignalMessage)iter.next();
		
		//we may or may not need to blink, if we are blinking, remain blinking
		isBlinking |= TagUtils.isAlarmUnacked(sig.getTags());
	}	
}

/**
 * @param signal
 * get that signal gone
 */
public synchronized void removeSignal(SignalMessage signal)
{
	if( signal == null )
		return;

	getSignalHash().remove( signal );
	
	updateBlinkingState();	
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

	/**
	 * @return
	 */
	public boolean isSilenced()
	{
		return isSilenced;
	}

	/**
	 * @param b
	 */
	public void setSilenced(boolean b)
	{
		isSilenced = b;
	}

}
