package com.cannontech.database.db.starshistory;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class CustomerProgramHistory extends com.cannontech.database.db.DBPersistent 
{
	private Integer eventID = null;
	private Integer accountID = null;
	private Integer actionID = null;
	private Integer programID = null;
	private java.util.Date eventTime = null;
	private String notes = null;

	public static final String[] SETTER_COLUMNS = 
	{ 
		"AccountID", "ActionID", "ProgramID", "EventTime", "Notes"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "EventID" };
	
	public static final String TABLE_NAME = "CustomerProgramHistory";
/**
 * CustomerWebSettings constructor comment.
 */
public CustomerProgramHistory() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{ 
		getEventID(), getAccountID(), getActionID(), getProgramID(),
		getEventTime(), getNotes()
	};

	add( TABLE_NAME, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getEventID() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:44:35 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getAccountID() {
	return accountID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:40:01 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getActionID() {
	return actionID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:44:35 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getEventID() {
	return eventID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:40:01 AM)
 * @return java.util.Date
 */
public java.util.Date getEventTime() {
	return eventTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:40:01 AM)
 * @return java.lang.String
 */
public java.lang.String getNotes() {
	return notes;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:44:35 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getProgramID() {
	return programID;
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	Object[] constraintValues =  { getEventID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setAccountID( (Integer) results[0] );
		setActionID( (Integer) results[1] );
		setProgramID( (Integer) results[2] );
		setEventTime( new java.util.Date(((java.sql.Timestamp)results[3]).getTime()) );
		setNotes( (String) results[4] );
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:44:35 AM)
 * @param newAccountID java.lang.Integer
 */
public void setAccountID(java.lang.Integer newAccountID) {
	accountID = newAccountID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:40:01 AM)
 * @param newActionID java.lang.Integer
 */
public void setActionID(java.lang.Integer newActionID) {
	actionID = newActionID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:44:35 AM)
 * @param newEventID java.lang.Integer
 */
public void setEventID(java.lang.Integer newEventID) {
	eventID = newEventID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:40:01 AM)
 * @param newEventTime java.util.Date
 */
public void setEventTime(java.util.Date newEventTime) {
	eventTime = newEventTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:40:01 AM)
 * @param newNotes java.lang.String
 */
public void setNotes(java.lang.String newNotes) {
	notes = newNotes;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:44:35 AM)
 * @param newProgramID java.lang.Integer
 */
public void setProgramID(java.lang.Integer newProgramID) {
	programID = newProgramID;
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = 
	{
		getAccountID(), getActionID(), getProgramID(),
		getEventTime(), getNotes()
	};

	Object[] constraintValues =  { getEventID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
