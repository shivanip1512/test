/*
 * Created on Mar 12, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.device.lm;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMDirectCustomerList extends com.cannontech.database.db.DBPersistent implements DeviceListItem
{
	private Integer programID = null;
	private Integer customerID = null;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"CustomerID"
	};


	public static final String CONSTRAINT_COLUMNS[] = { "ProgramID" };

	public static final String TABLE_NAME = "LMDirectCustomerList";
/**
 * LMGroupVersacomSerial constructor comment.
 */
public LMDirectCustomerList() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getCustomerID() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, "ProgramID", getDeviceID() );
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getDeviceID() {
	return programID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/12/2004 3:20:23 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCustomerID() {
	return customerID;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getDeviceID() };	
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setCustomerID( (Integer) results[0] );

	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}

/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDeviceID(Integer newValue) {
	programID = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (3/12/2004 3:20:23 PM)
 * @param newCustomerID java.lang.Integer
 */
public void setCustomerID(java.lang.Integer newCustomerID) {
	customerID = newCustomerID;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getCustomerID() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}