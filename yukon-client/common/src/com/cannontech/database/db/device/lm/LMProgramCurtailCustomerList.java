package com.cannontech.database.db.device.lm;

/**
 * This type was created in VisualAge.
 */
public class LMProgramCurtailCustomerList extends com.cannontech.database.db.DBPersistent implements DeviceListItem
{
	private Integer programID = null;
	private Integer customerID = null;
	private Integer customerOrder = null;
	private String requireAck = null;


	public static final String SETTER_COLUMNS[] = 
	{ 
		"CustomerID", "CustomerOrder", "RequireAck"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "ProgramID" };

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
	Object addValues[] = { getProgramID(), getCustomerID(), 
					getCustomerOrder(), getRequireAck() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, "ProgramID", getProgramID() );
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
	Object constraintValues[] = { getProgramID() };	
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

	Object constraintValues[] = { getProgramID() };

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

	/**
	 * @return
	 */
	public Integer getProgramID() {
		return programID;
	}

	/**
	 * @param integer
	 */
	public void setProgramID(Integer integer) {
		programID = integer;
	}

	/* 
	 * @see com.cannontech.database.db.device.lm.DeviceListItem#getDeviceID()
	 */
	public Integer getDeviceID() {
		return getProgramID();
	}

	/* 
	 * @see com.cannontech.database.db.device.lm.DeviceListItem#setDeviceID(java.lang.Integer)
	 */
	public void setDeviceID(Integer deviceID) {
		setProgramID(deviceID);
	}

}
