package com.cannontech.database.db.starscustomer;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class CustomerActionType extends com.cannontech.database.db.DBPersistent 
{
	private Integer actionID = null;
	private String action = null;
	private String validFor = null;

	public static final String[] SETTER_COLUMNS = 
	{ 
		"Action", "ValidFor"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "ActionID" };
	
	public static final String TABLE_NAME = "CustomerActionType";
/**
 * CustomerWebSettings constructor comment.
 */
public CustomerActionType() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{ 
		getActionID(), getAction(), getValidFor()
	};

	add( TABLE_NAME, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getActionID() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:27:01 AM)
 * @return java.lang.String
 */
public java.lang.String getAction() {
	return action;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:27:01 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getActionID() {
	return actionID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:27:01 AM)
 * @return java.lang.String
 */
public java.lang.String getValidFor() {
	return validFor;
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	Object[] constraintValues =  { getActionID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setAction( (String) results[0] );
		setValidFor( (String) results[1] );
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:27:01 AM)
 * @param newAction java.lang.String
 */
public void setAction(java.lang.String newAction) {
	action = newAction;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:27:01 AM)
 * @param newActionID java.lang.Integer
 */
public void setActionID(java.lang.Integer newActionID) {
	actionID = newActionID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:27:01 AM)
 * @param newValidFor java.lang.String
 */
public void setValidFor(java.lang.String newValidFor) {
	validFor = newValidFor;
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = 
	{
		getAction(), getValidFor()
	};

	Object[] constraintValues =  { getActionID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
