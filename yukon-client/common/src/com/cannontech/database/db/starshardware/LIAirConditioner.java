package com.cannontech.database.db.starshardware;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class LIAirConditioner extends com.cannontech.database.db.DBPersistent 
{
	private Integer loadID = null;
	private String tonage = null;
	private String type = null;

	public static final String[] SETTER_COLUMNS = 
	{ 
		"Tonage", "Type"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "LoadID" };
	
	public static final String TABLE_NAME = "LIAirConditioner";
/**
 * CustomerWebSettings constructor comment.
 */
public LIAirConditioner() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{ 
		getLoadID(), getTonage(), getType()
	};

	add( TABLE_NAME, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getLoadID() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:36:03 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLoadID() {
	return loadID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:36:03 AM)
 * @return java.lang.String
 */
public java.lang.String getTonage() {
	return tonage;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:36:03 AM)
 * @return java.lang.String
 */
public java.lang.String getType() {
	return type;
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	Object[] constraintValues =  { getLoadID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setTonage( (String) results[0] );
		setType( (String) results[1] );
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:36:03 AM)
 * @param newLoadID java.lang.Integer
 */
public void setLoadID(java.lang.Integer newLoadID) {
	loadID = newLoadID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:36:03 AM)
 * @param newTonage java.lang.String
 */
public void setTonage(java.lang.String newTonage) {
	tonage = newTonage;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:36:03 AM)
 * @param newType java.lang.String
 */
public void setType(java.lang.String newType) {
	type = newType;
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = 
	{
		getTonage(), getType()
	};

	Object[] constraintValues =  { getLoadID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
