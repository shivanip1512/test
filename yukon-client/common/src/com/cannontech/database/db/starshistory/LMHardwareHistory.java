package com.cannontech.database.db.starshistory;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class LMHardwareHistory extends com.cannontech.database.db.DBPersistent 
{
	private Integer hstEventID = null;
	private Integer loadID = null;
	private Integer actionID = null;
	private java.util.Date eventTime = null;
	private String notes = null;

	public static final String[] SETTER_COLUMNS = 
	{ 
		"LoadID", "ActionID", "EventTime", "Notes"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "HstEventID" };
	
	public static final String TABLE_NAME = "LMHardwareHistory";
/**
 * CustomerWebSettings constructor comment.
 */
public LMHardwareHistory() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{ 
		getHstEventID(), getLoadID(), getActionID(),
		getEventTime(), getNotes()
	};

	add( TABLE_NAME, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getHstEventID() );
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
 * Creation date: (3/25/2002 9:40:01 AM)
 * @return java.util.Date
 */
public java.util.Date getEventTime() {
	return eventTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:40:01 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getHstEventID() {
	return hstEventID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:17:45 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLoadID() {
	return loadID;
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
 */
public void retrieve() throws java.sql.SQLException 
{
	Object[] constraintValues =  { getHstEventID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setLoadID( (Integer) results[0] );
		setActionID( (Integer) results[1] );
		setEventTime( new java.util.Date(((java.sql.Timestamp)results[2]).getTime()) );
		setNotes( (String) results[3] );
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
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
 * Creation date: (3/25/2002 9:40:01 AM)
 * @param newEventTime java.util.Date
 */
public void setEventTime(java.util.Date newEventTime) {
	eventTime = newEventTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:40:01 AM)
 * @param newHstEventID java.lang.Integer
 */
public void setHstEventID(java.lang.Integer newHstEventID) {
	hstEventID = newHstEventID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:17:45 AM)
 * @param newLoadID java.lang.Integer
 */
public void setLoadID(java.lang.Integer newLoadID) {
	loadID = newLoadID;
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
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = 
	{
		getLoadID(), getActionID(),
		getEventTime(), getNotes()
	};

	Object[] constraintValues =  { getHstEventID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
