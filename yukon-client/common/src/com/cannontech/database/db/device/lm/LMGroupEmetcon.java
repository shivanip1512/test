package com.cannontech.database.db.device.lm;

/**
 * This type was created in VisualAge.
 */
public class LMGroupEmetcon extends com.cannontech.database.db.DBPersistent {
	private Integer deviceID = null;
	private Integer goldAddress = null;
	private Integer silverAddress = null;
	private Character addressUsage = null;
	private Character relayUsage = null;
	private Integer routeID = null;

	public static final String tableName = "LMGroupEmetcon";
/**
 * LMGroupEmetcon constructor comment.
 */
public LMGroupEmetcon() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	Object addValues[] = { getDeviceID(), getGoldAddress(), getSilverAddress(), getAddressUsage(), getRelayUsage(), getRouteID() };

	add( tableName, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {
	delete( tableName, "DeviceID", getDeviceID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public Character getAddressUsage() {
	return addressUsage;
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
public Integer getGoldAddress() {
	return goldAddress;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public Character getRelayUsage() {
	return relayUsage;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getRouteID() {
	return routeID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getSilverAddress() {
	return silverAddress;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {
	String selectColumns[] = { "GoldAddress", "SilverAddress", "AddressUsage", "RelayUsage", "RouteID" };
	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };

	Object results[] = retrieve( selectColumns, tableName, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setGoldAddress( (Integer) results[0] );
		setSilverAddress( (Integer) results[1] );
		setAddressUsage( new Character( ((String) results[2]).charAt(0)) );
		setRelayUsage( new Character( ((String) results[3]).charAt(0)) );
		setRouteID( (Integer) results[4] );	
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Character
 */
public void setAddressUsage(Character newValue) {
	this.addressUsage = newValue;
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
public void setGoldAddress(Integer newValue) {
	this.goldAddress = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Character
 */
public void setRelayUsage(Character newValue) {
	this.relayUsage = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setRouteID(Integer newValue) {
	this.routeID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setSilverAddress(Integer newValue) {
	this.silverAddress = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	String setColumns[] = { "GoldAddress", "SilverAddress", "AddressUsage", "RelayUsage", "RouteID" };
	Object setValues[] = { getGoldAddress(), getSilverAddress(), getAddressUsage(), getRelayUsage(), getRouteID() };

	String constraintColumns[] = { "DeviceID" };
	Object constraintValues[] = { getDeviceID() };

	update( tableName, setColumns, setValues, constraintColumns, constraintValues );
}
}
