package com.cannontech.tdc;

/**
 * Insert the type's description here.
 * Creation date: (2/2/00 4:31:00 PM)
 * @author: 
 */
import java.util.Date;
import java.util.HashMap;

import com.cannontech.clientutils.tags.IAlarmDefs;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;

public class PointValues 
{
	//pointData can NOT be null
	private PointData pointData = null;

	//signals can be null 
	private HashMap signalHash = null;


	//device data
	private String deviceName = null;
	private String deviceType = null;
	private int deviceID = 0;

	//point data	
	private String pointName = null;
	private String pointState = null;
	private Integer decimalPlaces = null;
	private String[] foregroundColors = null;
	private String[] pointMessages = null;
	private String[] pointRawStates = null;
	private String[] backGroundColors = null;
	private int[] imageIDs = null;

	private String initialState = null;
	private String normalState = null;
	private String alarmsState = null;	
	
	private String previuosText = null;
	
	private int currentForegroundColor = 0;
	private int currentBackgroundColor = 0;

	private int originalBackgroundColor = 0;

/**
 * PointValues constructor comment.
 * Used to make dummy pointValues ONLY
 * **/
public PointValues( int pointid, int ptType, String ptName, String[] colors, 
		String[] messages, String[] rawstate, String[] bgColor, int[] imgIDs, int colorCount ) 
{
	this( colors, messages, rawstate, bgColor, imgIDs, colorCount );

	pointName = ptName;
	setPointData( new PointData() );	
	getPointData().setId( pointid );
	getPointData().setType( ptType );
}

/**
 * PointValues constructor comment.
 */
protected PointValues( String[] colors, String[] messages, 
			String[] rawstate, String[] bgColor, int[] imgIDs, int colorCount ) 
{
	super();
	
	foregroundColors = new String[ colorCount ];
	pointMessages = new String[ colorCount ];
	pointRawStates = new String[ colorCount ];
	backGroundColors = new String[ colorCount ];
	imageIDs = new int[ colorCount ];

	for( int i = 0; i < colorCount; i++ )
	{
		foregroundColors[i] = colors[i];
		pointMessages[i] = messages[i];
		pointRawStates[i] = rawstate[i];
		backGroundColors[i] = bgColor[i];
		imageIDs[i] = imgIDs[i];
	}

	currentForegroundColor = Integer.parseInt( foregroundColors[0] );
	currentBackgroundColor = Integer.parseInt( backGroundColors[0] );
	originalBackgroundColor = Integer.parseInt( backGroundColors[0] );

	previuosText = new String(pointMessages[0]);
}


/**
 * Used to make temporary pointValues ONLY, note: all the color fields and PointData
 * fields are still NULL after creation
 ***/
public PointValues( int pointid, String ptType, String ptName, String devName, 
			String ptState, String devType, int devId ) 
{
	super();
	
	pointName = ptName.toString();
	deviceName = devName.toString();
	pointState = ptState.toString();
	deviceType = devType.toString();
	deviceID = devId;
	
	setPointData( new PointData() );	
	getPointData().setTime( new java.util.Date() );
	getPointData().setId( pointid );
	getPointData().setType( PointTypes.getType(ptType.toString()) );
}



/**
 * PointValues constructor comment.
 * USED TO MAKE A NEW POINTVALUE FROM A PREVIOUS EXISTING POINTVALUE
 */
public PointValues( PointValues point, String[] colors, String[] messages, 
			String[] rawstate, String[] bgColor, int[] imgIDs, int colorCount ) 
{
	this( colors, messages, rawstate, bgColor, imgIDs, colorCount );
	
	setPointData( new PointData() );	
	getPointData().setTime( new java.util.Date() );
	getPointData().setId( point.getPointData().getId() );
	getPointData().setType( point.getPointData().getType() );

	deviceName = point.getDeviceName();
	pointState = point.getPointState();
	deviceType = point.getDeviceType();
	deviceID = point.getDeviceID();
	pointName = point.getPointName();
	decimalPlaces = point.getDecimalPlaces();
	
	getSignalHash().putAll( point.getSignalHash() );
}

/**
 * PointValues constructor comment.
 * Used to make PSUEDO pointValues ONLY
 */
public PointValues( Signal signal_, int ptType, String ptName, String[] colors, 
			String[] messages, String[] rawstate, String[] bgColor, int[] imgIDs, int colorCount ) 
{
	this( colors, messages, rawstate, bgColor, imgIDs, colorCount );

	pointName = ptName;

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

	String[] messages = new String[ pointMessages.length ];
	
	for( int i = 0; i < pointMessages.length; i++ )
		messages[i] =  pointMessages[i];

	return messages;
}
/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 */
public String getBackGroundColor(int i) 
{
	return backGroundColors[ i ];
}
/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 */
public String getColor(int i) 
{
	return foregroundColors[ i ];
}

/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 */
public int getYukonImageID(int i) 
{
	return imageIDs[ i ];
}

/**
 * Insert the method's description here.
 * Creation date: (2/2/00 5:12:11 PM)
 * @return int
 */
public int getColorCount()
{
	return foregroundColors.length;
}
/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 */
public synchronized int getCurrentBackgroundColor() 
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
	return deviceID;
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
	return pointName;
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
public String getRawState(int i) 
{
	return pointRawStates[ i ];
}
/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 */
public String getText(long rawState ) 
{
	
	for( int i = 0; i < pointMessages.length; i++ )
	{
		long state = Long.valueOf( pointRawStates[i] ).longValue();
		
		if( state == rawState )
		{
			previuosText = new String( pointMessages[i] );
			return pointMessages[ i ];
		}
	}

	// shouldnt reach here, no text available for this state,
	// just use the previous text
	return previuosText;
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
/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int

	Accepts a rawState as a long value, then set the entires
	row accordingly 
 */
public void setCurrentRowColor( long value ) 
{

	for( int i = 0; i < foregroundColors.length; i++ )
	{
		long state = Long.valueOf( pointRawStates[i] ).longValue();
		
		if( state == value )
		{
			currentForegroundColor = new Integer( foregroundColors[i] ).intValue();
			currentBackgroundColor = new Integer( backGroundColors[i] ).intValue();	
				
			return;
		}
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int

 	Sets the row color to the colors of fg and bg.  Then assigns
 	the previousText value to the newest text
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
 * @param newDeviceID java.lang.Object
 */
public void setDeviceID(int newDeviceID) {
	deviceID = newDeviceID;
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
 * Creation date: (2/2/00 4:59:47 PM)
 * @param i int
 */
public void setStates( String init, String norm, String alarm ) 
{
	initialState = init;
	normalState = norm;
	alarmsState = alarm;

	// set the row value accordingly
	setCurrentRowColor( new Long( initialState ).longValue() );
}
/**
 * Insert the method's description here.
 * Creation date: (8/15/00 5:14:11 PM)
 * @param newDeviceID java.lang.Object
 */
public String toString()
{
	return getPointData().getId() + " : " + getPointName() + " DevID = " + getDeviceID();
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
