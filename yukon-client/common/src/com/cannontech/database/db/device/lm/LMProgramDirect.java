package com.cannontech.database.db.device.lm;

/**
 * This type was created in VisualAge.
 */
public class LMProgramDirect extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;

	public static final String SETTER_COLUMNS[] = 
	{ 
		""
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "LMProgramDirect";

/**
 * LMGroupVersacomSerial constructor comment.
 */
public LMProgramDirect() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, "DeviceID", getDeviceID() );
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2001 2:42:06 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDeviceID() {
	return deviceID;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
/*	Object constraintValues[] = { getDeviceID() };	
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		//setGroupSelectionMethod( (String) results[0] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
*/
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2001 2:42:06 PM)
 * @param newDeviceID java.lang.Integer
 */
public void setDeviceID(java.lang.Integer newDeviceID) {
	deviceID = newDeviceID;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
/*	Object setValues[] = { getGroupSelectionMethod() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
*/
}
}
