package com.cannontech.database.db.starshardware;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class LMHardwareConfiguration extends com.cannontech.database.db.DBPersistent 
{
	private Integer hardwareID = null;
	private Integer loadID = null;
	private Integer groupID = null;

	public static final String[] SETTER_COLUMNS = 
	{ 
		"GroupID"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "LoadID", "HardwareID" };
	
	public static final String TABLE_NAME = "LMHardwareConfiguration";
/**
 * CustomerWebSettings constructor comment.
 */
public LMHardwareConfiguration() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{ 
		getLoadID(), getHardwareID(), getGroupID()
	};

	add( TABLE_NAME, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	String[] cnstCols = { CONSTRAINT_COLUMNS[0], CONSTRAINT_COLUMNS[1] };
	Object[] vals = { getLoadID(), getHardwareID() };
	
	delete( TABLE_NAME, cnstCols, vals );
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:17:45 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getGroupID() {
	return groupID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:16:48 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getHardwareID() {
	return hardwareID;
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
 */
public void retrieve() throws java.sql.SQLException 
{
	Object[] constraintValues =  { getLoadID(), getHardwareID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		//setHardwareID( (Integer) results[0] );
		setGroupID( (Integer) results[0] );
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:17:45 AM)
 * @param newGroupID java.lang.Integer
 */
public void setGroupID(java.lang.Integer newGroupID) {
	groupID = newGroupID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:16:48 PM)
 * @param newHardwareID java.lang.Integer
 */
public void setHardwareID(java.lang.Integer newHardwareID) {
	hardwareID = newHardwareID;
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
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = 
	{
		//getHardwareID(), 
		getGroupID()
	};

	Object[] constraintValues =  { getLoadID(), getHardwareID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
