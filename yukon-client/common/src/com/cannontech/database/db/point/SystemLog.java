package com.cannontech.database.db.point;

import com.cannontech.database.data.point.SystemLogData;

/**
 * This type was created in VisualAge.
 */
public class SystemLog extends com.cannontech.database.db.DBPersistent implements SystemLogData 
{
	private Integer logID = null;
	private Integer pointID = null;
	private java.util.Date dateTime = null;
	private Integer soe_tag = null;
	private Integer type = null;
	private Integer priority = null;
	private String action = null;
	private String description = null;
	private String userName = null;

	/* Log types, match this with log types in yukon.h */
	public static final int TYPE_GENERAL = 1;
	public static final int TYPE_POINT_CHANGE = 2; //May not be used
	public static final int TYPE_LOADMANAGEMENT = 3;
	public static final int TYPE_USER = 4;
	public static final int TYPE_MACS = 7; 
	public static final int TYPE_ALARM = 8;

	public static final String TYPE_GENERAL_STRING = "General";
	public static final String TYPE_POINT_CHANGE_STRING = "Point Change"; //May not be used
	public static final String TYPE_LOADMANAGEMENT_STRING = "Load Management";
	public static final String TYPE_USER_STRING = "Yukon User";
	public static final String TYPE_MACS_STRING = "MACS"; 
	public static final String TYPE_ALARM_STRING = "Alarming";
	
	//THE TYPE id VALUE IS NOT REPRESENTATIVE OF THE INDEX OF THE ITEMS!!!
	public static final int[] LOG_TYPES = {
		
		TYPE_GENERAL,
		TYPE_POINT_CHANGE,
		TYPE_LOADMANAGEMENT,
		TYPE_USER,
		TYPE_MACS,	
		TYPE_ALARM
	};
	public static final String[] LOG_TYPE_STRINGS = {
		"General",
		"Point Change",
		"Load Management",
		"Yukon User",
		"MACS",
		"Alarming"
	};	
	
	public static final String CONSTRAINT_COLUMNS[] = { "LogID" };
	public static final String COLUMNS[] = { "PointID", 
		"DateTime", "SOE_Tag", "Type",
		"Priority", "Action", "Description", "UserName" };

	public final static String TABLE_NAME = "SystemLog";
/**
 * SystemLog constructor comment.
 */
public SystemLog() {
	super();
}
/**
 * SystemLog constructor comment.
 */
public SystemLog(Integer pointID) {
	super();
}
/**
 * @param id_
 * @return
 */
public static String getLogTypeStringFromID(int id_)
{
	switch (id_)
	{
		case TYPE_GENERAL:
			return TYPE_GENERAL_STRING;
		case TYPE_POINT_CHANGE:
			return TYPE_POINT_CHANGE_STRING;
		case TYPE_LOADMANAGEMENT:
			return TYPE_LOADMANAGEMENT_STRING;
		case TYPE_USER:
			return TYPE_USER_STRING;
		case TYPE_MACS:
			return TYPE_MACS_STRING;
		case TYPE_ALARM:
			return TYPE_ALARM_STRING;
		default :
			return "Invalid";
	}
}
/**
 * @param logType_
 * @return
 */
public static int getLogTypeIDFromString(String logType_)
{
	for (int i = 0; i < LOG_TYPE_STRINGS.length; i++)
	{
		if( LOG_TYPE_STRINGS[i].equalsIgnoreCase(logType_))
			return LOG_TYPES[i];
	}
	return -1;	//not found
} 
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	Object addValues[] = { getLogID(), getPointID(), 
		getDateTime(), getSoe_tag(), getType(), 
		getPriority(), getAction(), getDescription() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( TABLE_NAME, "LogID", getLogID() );
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:07:44 AM)
 * @return java.lang.String
 */
public java.lang.String getAction() {
	return action;
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:07:44 AM)
 * @return java.util.Date
 */
public java.util.Date getDateTime() {
	return dateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:07:44 AM)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	return description;
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:07:44 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLogID() {
	return logID;
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
 * Creation date: (5/29/2001 11:07:44 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPriority() {
	return priority;
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:07:44 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSoe_tag() {
	return soe_tag;
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:07:44 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:07:44 AM)
 * @return java.lang.String
 */
public java.lang.String getUserName() {
	return userName;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getLogID() };

	Object results[] = retrieve( COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == COLUMNS.length )
	{
		setPointID( (Integer) results[0] );
		setDateTime( (java.util.Date) results[1] );
		setSoe_tag( (Integer) results[2] );
		setType( (Integer) results[3] );
		setPriority( (Integer) results[4] );
		setAction( (String) results[5] );
		setDescription( (String) results[6] );
		setUserName( (String) results[7] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
	
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:07:44 AM)
 * @param newAction java.lang.String
 */
public void setAction(java.lang.String newAction) {
	action = newAction;
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:07:44 AM)
 * @param newDateTime java.util.Date
 */
public void setDateTime(java.util.Date newDateTime) {
	dateTime = newDateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:07:44 AM)
 * @param newDescription java.lang.String
 */
public void setDescription(java.lang.String newDescription) {
	description = newDescription;
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:07:44 AM)
 * @param newLogID java.lang.Integer
 */
public void setLogID(java.lang.Integer newLogID) {
	logID = newLogID;
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
 * Creation date: (5/29/2001 11:07:44 AM)
 * @param newPriority java.lang.Integer
 */
public void setPriority(java.lang.Integer newPriority) {
	priority = newPriority;
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:07:44 AM)
 * @param newSoe_tag java.lang.Integer
 */
public void setSoe_tag(java.lang.Integer newSoe_tag) {
	soe_tag = newSoe_tag;
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:07:44 AM)
 * @param newType java.lang.Integer
 */
public void setType(java.lang.Integer newType) {
	type = newType;
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:07:44 AM)
 * @param newUserName java.lang.String
 */
public void setUserName(java.lang.String newUserName) {
	userName = newUserName;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getPointID(), 
		getDateTime(), getSoe_tag(), getType(), 
		getPriority(), getAction(), getDescription() };
	
	Object constraintValues[] = { getLogID() };

	update( TABLE_NAME, COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
