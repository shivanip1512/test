package com.cannontech.database.db.point;

/**
 * This type was created in VisualAge.
 */
public class SystemLog extends com.cannontech.database.db.DBPersistent 
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


	public static final int TYPE_ALARM = 8;


	public static final String CONSTRAINT_COLUMNS[] = { "LogID" };
	public static final String COLUMNS[] = { "PointID", 
		"DateTime", "SOE_Tag", "Type",
		"Priority", "Action", "Description", "UserName" };

	public final static String TABLE_NAME = "SystemLog";
/**
 * PointUnit constructor comment.
 */
public SystemLog() {
	super();
}
/**
 * PointUnit constructor comment.
 */
public SystemLog(Integer pointID) {
	super();
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
