package com.cannontech.database.db.point;

import com.cannontech.common.util.CtiUtilities;

/**
 * This type was created in VisualAge.
 */
public class PointAlarming extends com.cannontech.database.db.DBPersistent 
{
	public static final int NONE_NOTIFICATIONID = 1;
//	public static final int NONE_LOCATIONID = 0;
	public static final String NONE_VALUE_STRING = "(none)";
	public static final String EXCLUDE_NOTIFY_VALUE_STRING = "Exclude Notify";
	public static final String AUTO_ACK_VALUE_STRING = "Auto Ack";
	public static final String BOTH_OPTIONS_VALUE_STRING = "Exclude Notify & Auto Ack";
	public static final String DEFAULT_EXCLUDE_NOTIFY = "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN";
	private static String tmp = new String();

   
   public static final int ALARM_STATE_COUNT = 32;
		
	// we need to initialize our char mask	
	static 
	{
		for( int i = 0; i < ALARM_STATE_COUNT; i++ )
			tmp += '\u0001';  // Do not have nulls!! This is the Second char in a ASCII list, int value of 1
	};	
	public static final String DEFAULT_ALARM_STATES = tmp;
	
	
	private Integer pointID = null;
	private String alarmStates = DEFAULT_ALARM_STATES;
	private String excludeNotifyStates = DEFAULT_EXCLUDE_NOTIFY;
	private String notifyOnAcknowledge = "N";
	private Integer notificationGroupID = new Integer(PointAlarming.NONE_NOTIFICATIONID);
	private Integer recipientID = new Integer(CtiUtilities.NONE_ZERO_ID);

		
	public static final String CONSTRAINT_COLUMNS[] = { "POINTID" };
	
	public static final String SETTER_COLUMNS[] = { 
			"ALARMSTATES", "EXCLUDENOTIFYSTATES", "NOTIFYONACKNOWLEDGE",
			"NOTIFICATIONGROUPID", "RECIPIENTID" };
	

	public final static String TABLE_NAME = "PointAlarming";
/**
 * PointUnit constructor comment.
 */
public PointAlarming() {
	super();
}
/**
 * PointUnit constructor comment.
 */
public PointAlarming(Integer pointID, String alarmStates, String excludeNotifyStates, 
		String notifyOnAcknowledge, Integer notificationGroupID, Integer newRecipientID )
{
	super();
	initialize(pointID, alarmStates, excludeNotifyStates, notifyOnAcknowledge, notificationGroupID, newRecipientID );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getPointID(), getAlarmStates(), getExcludeNotifyStates(), 
		getNotifyOnAcknowledge(), getNotificationGroupID(), getRecipientID() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getPointID() );
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 11:45:06 AM)
 * @return java.lang.String
 */
public java.lang.String getAlarmStates() {
	return alarmStates;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 11:45:06 AM)
 * @return java.lang.String
 */
public java.lang.String getExcludeNotifyStates() {
	return excludeNotifyStates;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 11:45:06 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getNotificationGroupID() {
	return notificationGroupID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 11:45:06 AM)
 * @return java.lang.String
 */
public java.lang.String getNotifyOnAcknowledge() {
	return notifyOnAcknowledge;
}

public void setNotifyOnAck( boolean notify ) {
	
	if( notify ) {
		if( isNotifyOnClear() ) setNotifyOnAcknowledge( "B" );
		else setNotifyOnAcknowledge( "A" );
	}
	else {
		if( isNotifyOnClear() ) setNotifyOnAcknowledge( "C" );
		else setNotifyOnAcknowledge( "N" );
	}
}

public void setNotifyOnClear( boolean notify ) {
	
	if( notify ) {
		if( isNotifyOnAck() ) setNotifyOnAcknowledge( "B" );
		else setNotifyOnAcknowledge( "C" );
	}
	else {
		if( isNotifyOnAck() ) setNotifyOnAcknowledge( "A" );
		else setNotifyOnAcknowledge( "N" );
	}
}

/**
 * Possible values: A, C, B, N
 * A: notify on ACK only
 * C: notify on CLEAR only
 * B: notify on ACK or CLEAR (both)
 * N: notify on none
 */
public boolean isNotifyOnClear() {	
	return
		getNotifyOnAcknowledge().charAt(0) == 'C'
		|| getNotifyOnAcknowledge().charAt(0) == 'B';
}

public boolean isNotifyOnAck() {	
	return
		getNotifyOnAcknowledge().charAt(0) == 'A'
		|| getNotifyOnAcknowledge().charAt(0) == 'B';
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getPointID() {
	return pointID;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 11:42:36 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getRecipientID() {
	return recipientID;
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 * @param units java.lang.String
 * @param logFrequency java.lang.Integer
 * @param defaultValue java.lang.Double
 */
private void initialize(Integer pointID, String alarmStates, String excludeNotifyStates, 
		String notifyOnAcknowledge, Integer notificationGroupID, Integer newRecipientID )
{
	setPointID( pointID );
	setAlarmStates( alarmStates ) ;
	setExcludeNotifyStates( excludeNotifyStates );
	setNotifyOnAcknowledge( notifyOnAcknowledge );
	setNotificationGroupID( notificationGroupID );
	setRecipientID( newRecipientID );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getPointID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setAlarmStates( (String) results[0] );
		setExcludeNotifyStates( (String) results[1] );
		setNotifyOnAcknowledge( (String) results[2] );
		setNotificationGroupID( (Integer) results[3] );
		setRecipientID( (Integer) results[4] );		
	}
	//else
		//throw new Error(getClass() + " - Incorrect Number of results retrieved");	
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 11:45:06 AM)
 * @param newAlarmStates java.lang.String
 */
public void setAlarmStates(java.lang.String newAlarmStates) {
	alarmStates = newAlarmStates;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 11:45:06 AM)
 * @param newExcludeNotifyStates java.lang.String
 */
public void setExcludeNotifyStates(java.lang.String newExcludeNotifyStates) {
	excludeNotifyStates = newExcludeNotifyStates;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 11:45:06 AM)
 * @param newNotificationGroupID java.lang.Integer
 */
public void setNotificationGroupID(java.lang.Integer newNotificationGroupID) {
	notificationGroupID = newNotificationGroupID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 11:45:06 AM)
 * @param newNotifyOnAcknowledge java.lang.String
 */
public void setNotifyOnAcknowledge(java.lang.String newNotifyOnAcknowledge) {
	notifyOnAcknowledge = newNotifyOnAcknowledge;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPointID(Integer newValue) {
	this.pointID = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 11:42:36 AM)
 * @param newRecipientID java.lang.Integer
 */
public void setRecipientID(java.lang.Integer newRecipientID) {
	recipientID = newRecipientID;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getAlarmStates(), getExcludeNotifyStates(), 
				getNotifyOnAcknowledge(), getNotificationGroupID(), getRecipientID() };

	Object constraintValues[] = { getPointID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
