package com.cannontech.database.db.device.lm;

/**
 * This type was created in VisualAge.
 */
public class LMEnergyExchangeCustomerList extends com.cannontech.database.db.DBPersistent implements DeviceListItem
{
	private Integer deviceID = null;
	private Integer lmCustomerDeviceID = null;
	private Integer customerOrder = null;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"LMCustomerDeviceID", "CustomerOrder"
	};


	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "LMEnergyExchangeCustomerList";
/**
 * LMGroupVersacomSerial constructor comment.
 */
public LMEnergyExchangeCustomerList() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getLmCustomerDeviceID(), 
					getCustomerOrder() };

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
 * Creation date: (5/7/2001 2:20:23 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCustomerOrder() {
	return customerOrder;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getDeviceID() {
	return deviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 2:20:23 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLmCustomerDeviceID() {
	return lmCustomerDeviceID;
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
		setLmCustomerDeviceID( (Integer) results[0] );
		setCustomerOrder( (Integer) results[1] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 2:20:23 PM)
 * @param newCustomerOrder java.lang.Integer
 */
public void setCustomerOrder(java.lang.Integer newCustomerOrder) {
	customerOrder = newCustomerOrder;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDeviceID(Integer newValue) {
	this.deviceID = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 2:20:23 PM)
 * @param newLmCustomerDeviceID java.lang.Integer
 */
public void setLmCustomerDeviceID(java.lang.Integer newLmCustomerDeviceID) {
	lmCustomerDeviceID = newLmCustomerDeviceID;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getLmCustomerDeviceID(), 
					getCustomerOrder() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
