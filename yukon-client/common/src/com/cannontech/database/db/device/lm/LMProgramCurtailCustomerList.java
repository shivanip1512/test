package com.cannontech.database.db.device.lm;

/**
 * This type was created in VisualAge.
 */
public class LMProgramCurtailCustomerList extends com.cannontech.database.db.DBPersistent implements DeviceListItem
{
	private Integer deviceID = null;
	private Integer customerID = null;
	private Integer customerOrder = null;
	private String requireAck = null;


	public static final String SETTER_COLUMNS[] = 
	{ 
		"LMCustomerDeviceID", "CustomerOrder", "RequireAck"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "LMProgramCurtailCustomerList";

/**
 * LMGroupVersacomSerial constructor comment.
 */
public LMProgramCurtailCustomerList() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getCustomerID(), 
					getCustomerOrder(), getRequireAck() };

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
 * @return java.lang.String
 */
public java.lang.String getRequireAck() {
	return requireAck;
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
		setCustomerOrder( (Integer) results[1] );
		setRequireAck( (String) results[2] );
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
 * @param newRequireAck java.lang.String
 */
public void setRequireAck(java.lang.String newRequireAck) {
	requireAck = newRequireAck;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getCustomerID(), 
					getCustomerOrder(), getRequireAck() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
	/**
	 * Returns the customerID.
	 * @return Integer
	 */
	public Integer getCustomerID() {
		return customerID;
	}

	/**
	 * Sets the customerID.
	 * @param customerID The customerID to set
	 */
	public void setCustomerID(Integer customerID) {
		this.customerID = customerID;
	}

}
