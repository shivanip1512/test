package com.cannontech.tdc;

/**
 * Insert the type's description here.
 * Creation date: (2/2/00 4:31:00 PM)
 * @author: 
 */
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;

public class PointValues 
{
	private PointData pointData = null;

	private Object deviceName = null;
	private Object pointState = null;
	private Object deviceType = null;
	private Object deviceCurrentState = null;
	private int deviceID = 0;
	private Integer decimalPlaces = null;
	
	private String pointName = null;
	private String[] foregroundColors = null;
	private String[] pointMessages = null;
	private String[] pointRawStates = null;
	private String[] backGroundColors = null;

	private String initialState = null;
	private String normalState = null;
	private String alarmsState = null;	
	
	private String previuosText = null;
	
	private int currentForegroundColor = 0;
	private int currentBackgroundColor = 0;

	private int originalBackgroundColor = 0;
/**
 * PointValues constructor comment.
 */
// Used to make dummy pointValues ONLY
public PointValues( int pointid, int ptType, String ptName, String[] colors, String[] messages, String[] rawstate, String[] bgColor, int colorCount ) 
{
	super();

	pointName = ptName;
	pointData = new PointData();	
	pointData.setId( pointid );
	pointData.setType( ptType );
	pointData.setTime( new java.util.Date() );


	foregroundColors = new String[ colorCount ];
	pointMessages = new String[ colorCount ];
	pointRawStates = new String[ colorCount ];
	backGroundColors = new String[ colorCount ];

	for( int i = 0; i < colorCount; i++ )
	{
		foregroundColors[i] = colors[i];
		pointMessages[i] = messages[i];
		pointRawStates[i] = rawstate[i];
		backGroundColors[i] = bgColor[i];
	}

	currentForegroundColor = Integer.parseInt( foregroundColors[0] );
	currentBackgroundColor = Integer.parseInt( backGroundColors[0] );
	originalBackgroundColor = Integer.parseInt( backGroundColors[0] );
	
	previuosText = new String(pointMessages[0]);
}
/**
 * PointValues constructor comment.
 */
// Used to make temporary pointValues ONLY, note: all the color fields and PointData
// fields are still NULL after creation
public PointValues( int pointid, Object ptType, Object ptName, Object devName, 
					Object ptState, Object devType, Object devCurrentState, int devId ) 
{
	super();

	pointName = ptName.toString();
	deviceName = devName.toString();
	pointState = ptState.toString();
	deviceType = devType.toString();
	deviceCurrentState = devCurrentState.toString();
	deviceID = devId;
	
	pointData = new PointData();	
	pointData.setId( pointid );
	pointData.setType( com.cannontech.database.data.point.PointTypes.getType(ptType.toString()) );
	pointData.setTime( new java.util.Date() );

}
/**
 * PointValues constructor comment.
 */
// USED TO MAKE A NEW POINTVALUE FROM A PREVIOUS EXISTING POINTVALUE
public PointValues( PointValues point, String[] colors, String[] messages, String[] rawstate, String[] bgColor, int colorCount ) 
{
	super();
	
	pointData = new PointData();	
	pointData.setId( point.getPointData().getId() );
	pointData.setType( point.getPointData().getType() );
	deviceName = point.getDeviceName();
	pointState = point.getPointState();
	deviceType = point.getDeviceType();
	deviceCurrentState = point.getDeviceCurrentState();
	deviceID = point.getDeviceID();
	pointName = point.getPointName();
	decimalPlaces = point.getDecimalPlaces();
	
	foregroundColors = new String[ colorCount ];
	pointMessages = new String[ colorCount ];
	pointRawStates = new String[ colorCount ];
	backGroundColors = new String[ colorCount ];
	pointData.setTime( new java.util.Date() );

	for( int i = 0; i < colorCount; i++ )
	{
		foregroundColors[i] = colors[i];
		pointMessages[i] = messages[i];
		pointRawStates[i] = rawstate[i];
		backGroundColors[i] = bgColor[i];
	}

	currentForegroundColor = Integer.parseInt( foregroundColors[0] );
	currentBackgroundColor = Integer.parseInt( backGroundColors[0] );
	originalBackgroundColor = Integer.parseInt( backGroundColors[0] );

	previuosText = new String(pointMessages[0]);
}
/**
 * PointValues constructor comment.
 */
// Used to make PSUEDO pointValues ONLY
public PointValues( Signal signal, int ptType, String ptName, String[] colors, String[] messages, String[] rawstate, String[] bgColor, int colorCount ) 
{
	super();

	pointName = ptName;
	pointData = new PointData();	
	pointData.setId( signal.getId() );
	pointData.setType( ptType );
	pointData.setTime( new java.util.Date() );
	pointData.setTags( signal.getTags() );

	foregroundColors = new String[ colorCount ];
	pointMessages = new String[ colorCount ];
	pointRawStates = new String[ colorCount ];
	backGroundColors = new String[ colorCount ];

	for( int i = 0; i < colorCount; i++ )
	{
		foregroundColors[i] = colors[i];
		pointMessages[i] = messages[i];
		pointRawStates[i] = rawstate[i];
		backGroundColors[i] = bgColor[i];
	}

	currentForegroundColor = Integer.parseInt( foregroundColors[0] );
	currentBackgroundColor = Integer.parseInt( backGroundColors[0] );
	originalBackgroundColor = Integer.parseInt( backGroundColors[0] );
	
	previuosText = new String(pointMessages[0]);
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
public java.lang.Object getDeviceCurrentState() {
	return deviceCurrentState;
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
public java.lang.Object getDeviceName() {
	return deviceName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/15/00 5:14:11 PM)
 * @return java.lang.Object
 */
public java.lang.Object getDeviceType() {
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
 * @return com.cannontech.message.dispatch.message.PointData
 */
public com.cannontech.message.dispatch.message.PointData getPointData() {
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
public java.lang.Object getPointState() {
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
/**
 * Insert the method's description here.
 * Creation date: (8/15/00 5:14:11 PM)
 * @param newDeviceCurrentState java.lang.Object
 */
public void setDeviceCurrentState(java.lang.Object newDeviceCurrentState) {
	deviceCurrentState = newDeviceCurrentState;
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
public void setDeviceName(java.lang.Object newDeviceName) {
	deviceName = newDeviceName;
}
/**
 * Insert the method's description here.
 * Creation date: (8/15/00 5:14:11 PM)
 * @param newDeviceType java.lang.Object
 */
public void setDeviceType(java.lang.Object newDeviceType) {
	deviceType = newDeviceType;
}
/**
 * Insert the method's description here.
 * Creation date: (8/15/00 4:58:43 PM)
 * @param newPointData com.cannontech.message.dispatch.message.PointData
 */
public void setPointData(com.cannontech.message.dispatch.message.PointData newPointData) {
	pointData = newPointData;
}
/**
 * Insert the method's description here.
 * Creation date: (8/16/00 10:18:48 AM)
 * @param newPointState java.lang.Object
 */
public void setPointState(java.lang.Object newPointState) {
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
}
