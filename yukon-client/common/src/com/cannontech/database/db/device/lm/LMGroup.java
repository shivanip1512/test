package com.cannontech.database.db.device.lm;

/**
 * This type was created in VisualAge.
 */

public class LMGroup extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private Double kwCapacity = new Double(0.0);


	public static final String SETTER_COLUMNS[] = 
	{ 
		"KWCapacity"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "LMGroup";

/**
 * LMGroupVersacomSerial constructor comment.
 */
public LMGroup() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getKwCapacity() };

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
public Integer getDeviceID() {
	return deviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 12:27:26 PM)
 * @return Double
 */
public Double getKwCapacity() {
	return kwCapacity;
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
		setKwCapacity( (Double) results[0] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

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
 * Creation date: (3/16/2001 12:27:26 PM)
 * @param newKwCapacity Double
 */
public void setKwCapacity(Double newKwCapacity) {
	kwCapacity = newKwCapacity;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getKwCapacity() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
