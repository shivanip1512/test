package com.cannontech.tdc;

/**
 * Insert the type's description here.
 * Creation date: (2/2/00 4:31:00 PM)
 * @author: 
 */
import java.util.Date;
import java.util.HashMap;

import com.cannontech.clientutils.tags.IAlarmDefs;
import com.cannontech.common.gui.util.Colors;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.state.YukonImage;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.tdc.utils.TDCDefines;

public class PointValues 
{
	//pointData can NOT be null
	private PointData pointData = null;

	//signals can be null 
	private HashMap signalHash = null;


	//device data
	private String deviceName = null;
	private String deviceType = null;

	//point data	
	private String pointState = null;
	private Integer decimalPlaces = null;

	private String previuosText = null;
	
	private int currentForegroundColor = Colors.WHITE_ID;
	private int currentBackgroundColor = Colors.BLACK_ID;

	private int originalBackgroundColor = Colors.BLACK_ID;


/**
 * PointValues constructor comment.
 * Used to make dummy pointValues ONLY
 * **/
public PointValues( int pointid, int ptType ) 
{
	super();
	setPointData( new PointData() );	
	getPointData().setId( pointid );
	getPointData().setType( ptType );
}

/**
 * Used to make temporary pointValues ONLY, note: all the color fields and PointData
 * fields are still NULL after creation
 ***/
public PointValues( int pointid, int ptType, String devName, 
			String ptState, String devType ) 
{
	super();
	
	deviceName = devName.toString();
	pointState = ptState.toString();
	deviceType = devType.toString();
	
	setPointData( new PointData() );	
	getPointData().setTime( new java.util.Date() );
	getPointData().setId( pointid );
	getPointData().setType( ptType );
}



/**
 * PointValues constructor comment.
 * Used to make PSUEDO pointValues ONLY
 */
public PointValues( Signal signal_, int ptType )
{
	super();

	setPointData( new PointData() );
	getPointData().setTime( new java.util.Date() );		
	getPointData().setId( signal_.getPointID() );
	getPointData().setType( ptType );
	getPointData().setTime( new java.util.Date() );
	getPointData().setTags( signal_.getTags() );
	
	updateSignal( signal_ );
}

/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 */
public String[] getAllText() 
{
	LiteStateGroup lpgrp = getLiteStateGroup();
	if( lpgrp == null )
		return new String[0];
	
	String[] messages = new String[ lpgrp.getStatesList().size() ];
	
	for( int i = 0; i < lpgrp.getStatesList().size(); i++ )
		messages[i] =  lpgrp.getStatesList().get(i).toString();

	return messages;
}
/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 */
public String getBackGroundColor(int i) 
{
	LiteStateGroup lpgrp = getLiteStateGroup();
	if( lpgrp == null )
		return String.valueOf( Colors.BLACK_ID );	
	
	return Colors.getColorString(
					((LiteState)lpgrp.getStatesList().get(i)).getBgColor() );
}
/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 */
public String getColor(int i) 
{
	LiteStateGroup lpgrp = getLiteStateGroup();
	if( lpgrp == null )
		return String.valueOf( Colors.WHITE_ID );	


	return Colors.getColorString( 
					((LiteState)lpgrp.getStatesList().get(i)).getFgColor() );
}

/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 */
public int getYukonImageID(int i) 
{
	LiteStateGroup lpgrp = getLiteStateGroup();
	if( lpgrp == null )
		return YukonImage.NONE_IMAGE_ID;	
	
	return ((LiteState)lpgrp.getStatesList().get(i)).getImageID();
}

/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 */
public int getCurrentBackgroundColor() 
{
	return currentBackgroundColor;
}
/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 */
public int getCurrentForegroundColor() 
{
	return currentForegroundColor;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 11:33:35 AM)
 * @return Integer
 */
public Integer getDecimalPlaces() {
	return decimalPlaces;
}

/**
 * Insert the method's description here.
 * Creation date: (8/15/00 5:14:11 PM)
 * @return java.lang.Object
 */
public int getDeviceID() {
	return getLitePoint().getPaobjectID();
}
/**
 * Insert the method's description here.
 * Creation date: (8/15/00 5:14:11 PM)
 * @return java.lang.Object
 */
public String getDeviceName() {
	return deviceName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/15/00 5:14:11 PM)
 * @return java.lang.Object
 */
public String getDeviceType() {
	return deviceType;
}
/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 */
public int getOriginalBackgroundColor() 
{
	return originalBackgroundColor;
}
/**
 * Insert the method's description here.
 * Creation date: (8/15/00 4:58:43 PM)
 * @return PointData
 */
private PointData getPointData() {
	return pointData;
}

/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 */
public String getPointName() 
{
	return getLitePoint().getPointName();
}
/**
 * Insert the method's description here.
 * Creation date: (8/16/00 10:18:48 AM)
 * @return java.lang.Object
 */
public String getPointState() {
	return pointState;
}

/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 */
public String getText(int rawState ) 
{
	LiteStateGroup lpgrp = getLiteStateGroup();
	if( lpgrp == null )
		return "DUMMY";	
			
			
	String[] messages = new String[ lpgrp.getStatesList().size() ];
	
	for( int i = 0; i < lpgrp.getStatesList().size(); i++ )
	{
		if( rawState >= 0 && rawState <= lpgrp.getStatesList().size() )		
		{
			previuosText = 
					((LiteState)lpgrp.getStatesList().get(rawState)).getStateText();

			return previuosText;
		}		
	}
	
	throw new IllegalArgumentException(
			"Unable to find the state for the RawState=" 
			+ rawState + " on pointID=" + getPointID() );
}

/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 */
public synchronized void setCurrentBackgroundColor( int i )
{
	currentBackgroundColor = i;
}
/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 */
public synchronized void setCurrentForegroundColor( int i )
{
	currentForegroundColor = i;
}

private LitePoint getLitePoint()
{
	if( getPointID() == TDCDefines.ROW_BREAK_ID )
		return LitePoint.NONE_LITE_PT;
	else
		return PointFuncs.getLitePoint( getPointID() );
}

private LiteStateGroup getLiteStateGroup()
{
	//if the LitePoint is null OR we have an Invalid type 
	LitePoint lPoint = PointFuncs.getLitePoint( getPointID() );
	if( lPoint == null || getPointType() == PointTypes.INVALID_POINT )
		return null;
	else
		return PointFuncs.getStateGroup( lPoint.getStateGroupID() );
}

/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 *
 * Accepts a rawState as a long value, then set the colors of the
 * row accordingly 
 */
public void setCurrentRowColor( int value ) 
{
	LiteStateGroup lpgrp = getLiteStateGroup();
	if( lpgrp == null )
		return;

	for( int i = 0; i < lpgrp.getStatesList().size(); i++ )
	{
		LiteState lState = (LiteState)lpgrp.getStatesList().get(i);
		if( lState.getStateRawState() == value )
		{
			currentForegroundColor = new Integer( lState.getFgColor() ).intValue();
			currentBackgroundColor = new Integer( lState.getBgColor() ).intValue();			
			return;
		}	
	}
}

/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 *
 *	Sets the row color to the colors of fg and bg.  Then assigns
 *	the previousText value to the newest text
 */
public void setCurrentRowColor( String fg, String bg, String text ) 
{
	currentForegroundColor = new Integer( fg ).intValue();
	currentBackgroundColor = new Integer( bg ).intValue();
	
	originalBackgroundColor = new Integer( bg ).intValue();
	
	previuosText = new String(text);
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 11:33:35 AM)
 * @param newDecimalPlaces java.lang.String
 */
public void setDecimalPlaces(Integer newDecimalPlaces) {
	decimalPlaces = newDecimalPlaces;
}

public void setTags( long tags_ )
{
	getPointData().setTags( tags_ );
}

/**
 * Insert the method's description here.
 * Creation date: (8/15/00 5:14:11 PM)
 * @param newDeviceName java.lang.Object
 */
public void setDeviceName(String newDeviceName) {
	deviceName = newDeviceName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/15/00 5:14:11 PM)
 * @param newDeviceType java.lang.Object
 */
public void setDeviceType(String newDeviceType) {
	deviceType = newDeviceType;
}
/**
 * Insert the method's description here.
 * Creation date: (8/15/00 4:58:43 PM)
 * @param newPointData PointData
 */
public void setPointData(PointData newPointData) 
{
	if( newPointData == null )
		throw new IllegalStateException("A setter PointData method can not take a null value");

	pointData = newPointData;
}

/**
 * Insert the method's description here.
 * Creation date: (8/16/00 10:18:48 AM)
 * @param newPointState java.lang.Object
 */
public void setPointState(String newPointState) {
	pointState = newPointState;
}

/**
 * Insert the method's description here.
 * Creation date: (8/15/00 5:14:11 PM)
 * @param newDeviceID java.lang.Object
 */
public String toString()
{
	return "PointID=" + getPointData().getId() + 
			" (" + getPointName() + "), DevID=" + getDeviceID();
}


	public int getPointID() 
	{
		return getPointData().getId();
	}

	public double getValue() 
	{
		return getPointData().getValue();
	}

	public Date getTimeStamp() 
	{
		return getPointData().getTimeStamp();
	}

	public Date getPointDataTimeStamp() 
	{
		return getPointData().getPointDataTimeStamp();
	}

	public int getSOETag()
	{
		return getPointData().getSOE_Tag();
	}

	public long getPointQuality()
	{
		return getPointData().getQuality();
	}

	public long getTags()
	{
		return getPointData().getTags();
	}

	public int getPointType()
	{
		return getPointData().getType();
	}

	public void setSOETag( int newTag_ ) 
	{
		getPointData().setSOE_Tag( newTag_ );
	}

	public void setTime( Date newDate_ ) 
	{
		getPointData().setTime( newDate_ );
	}

	public boolean containsSignal( Signal signal_ )
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
	 * Gets an array of the signals
	 * @return a Zero length array of no signals are present.
	 */
	public Signal[] getAllSignals()
	{
		Signal[] sigs = new Signal[ getSignalHash().values().size() ];
		return (Signal[])getSignalHash().values().toArray( sigs );
	}

	/**
	 * @param signal
	 * deny our EVENT signals from being in here
	 */
	public void updateSignal(Signal signal)
	{
		if( signal != null && signal.getCondition() >= IAlarmDefs.MIN_CONDITION_ID )
			getSignalHash().put( signal, signal );
	}
		
	/**
	 * @param signal
	 * get that signal gone
	 */
	public void removeSignal(Signal signal)
	{
		getSignalHash().remove( signal );
	}

}
