package com.cannontech.database.db.starscustomer;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class CustomerSubstation extends com.cannontech.database.db.DBPersistent 
{
	private Integer substationID = null;
	private String substationName = null;
	private Integer routeID = null;

	public static final String[] SETTER_COLUMNS = 
	{ 
		"SubstationName", "RouteID"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "SubstationID" };
	
	public static final String TABLE_NAME = "CustomerSubstation";
/**
 * CustomerWebSettings constructor comment.
 */
public CustomerSubstation() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{ 
		getSubstationID(), getSubstationName(), getRouteID()
	};

	add( TABLE_NAME, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getSubstationID() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:13:41 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getRouteID() {
	return routeID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:12:04 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSubstationID() {
	return substationID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:12:04 PM)
 * @return java.lang.String
 */
public java.lang.String getSubstationName() {
	return substationName;
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	Object[] constraintValues =  { getSubstationID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setSubstationName( (String) results[0] );
		setRouteID( (Integer) results[1] );
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:13:41 PM)
 * @param newRouteID java.lang.Integer
 */
public void setRouteID(java.lang.Integer newRouteID) {
	routeID = newRouteID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:12:04 PM)
 * @param newSubstationID java.lang.Integer
 */
public void setSubstationID(java.lang.Integer newSubstationID) {
	substationID = newSubstationID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:12:04 PM)
 * @param newSubstationName java.lang.String
 */
public void setSubstationName(java.lang.String newSubstationName) {
	substationName = newSubstationName;
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = 
	{
		getSubstationName(), getRouteID()
	};

	Object[] constraintValues =  { getSubstationID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
