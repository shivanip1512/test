package com.cannontech.database.db.device;

/**
 * This type was created in VisualAge.
 */
public class DeviceIDLCRemote extends com.cannontech.database.db.DBPersistent 
{
	//possible values for ccuAmpUseType
	public static final String AMPUSE_ALTERNATING = "Alternating";
	public static final String AMPUSE_DEF_1_FAIL_2 = "Default 1 Fail 2";
	public static final String AMPUSE_DEF_2_FAIL_1 = "Default 2 Fail 1";
	public static final String AMPUSE_ALT_FAILOVER = "Alt w/Failover";
	public static final String AMPUSE_AMP1 = "Amp 1";
	public static final String AMPUSE_AMP2 = "Amp 2";	

	private Integer deviceID = null;
	private Integer address = null;
	private Integer postCommWait = new Integer(0);
	private String ccuAmpUseType = AMPUSE_AMP1;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"Address", "PostCommWait", "CCUAmpUseType"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };
	
	public static final String TABLE_NAME = "DeviceIDLCRemote";
/**
 * DeviceTwoWayFlags constructor comment.
 */
public DeviceIDLCRemote() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = { getDeviceID(), getAddress(), 
			getPostCommWait(), getCcuAmpUseType() };

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
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getAddress() {
	return address;
}
/**
 * Insert the method's description here.
 * Creation date: (9/18/2001 11:46:15 AM)
 * @return java.lang.String
 */
public java.lang.String getCcuAmpUseType() {
	return ccuAmpUseType;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getDeviceID() {
	return deviceID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getPostCommWait() {
	return postCommWait;
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
		setAddress( (Integer) results[0] );
		setPostCommWait( (Integer) results[1] );
		setCcuAmpUseType( (String) results[2] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setAddress(Integer newValue) {
	this.address = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (9/18/2001 11:46:15 AM)
 * @param newCcuAmpUseType java.lang.String
 */
public void setCcuAmpUseType(java.lang.String newCcuAmpUseType) {
	ccuAmpUseType = newCcuAmpUseType;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDeviceID(Integer newValue) {
	this.deviceID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPostCommWait(Integer newValue) {
	this.postCommWait = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[]= { getAddress(), getPostCommWait(), getCcuAmpUseType() };
	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
