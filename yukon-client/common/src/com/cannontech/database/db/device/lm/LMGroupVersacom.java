package com.cannontech.database.db.device.lm;

/**
 * This type was created in VisualAge.
 */
public class LMGroupVersacom extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private Integer routeID = null;
	private Integer utilityAddress = new Integer(0);
	private Integer sectionAddress = new Integer(0);
	private Integer classAddress = new Integer(0);
	private Integer divisionAddress = new Integer(0);
	private String addressUsage = "    ";
	private String relayUsage = "1      ";
	private String serialAddress = "0";

	public static final String SETTER_COLUMNS[] = 
	{ 
		"RouteID", "UtilityAddress", "SectionAddress", "ClassAddress",
		"DivisionAddress", "AddressUsage", "RelayUsage", "SerialAddress"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "LMGroupVersacom";
/**
 * LMGroupVersacom constructor comment.
 */
public LMGroupVersacom() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getRouteID(), 
		getUtilityAddress(), getSectionAddress(), 
		getClassAddress(), getDivisionAddress(), getAddressUsage(), 
		getRelayUsage(), getSerialAddress() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getDeviceID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getAddressUsage() {
	return addressUsage;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getClassAddress() {
	return classAddress;
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
public Integer getDivisionAddress() {
	return divisionAddress;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getRelayUsage() {
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
public Integer getSectionAddress() {
	return sectionAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2001 5:15:22 PM)
 * @return java.lang.String
 */
public java.lang.String getSerialAddress() {
	return serialAddress;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getUtilityAddress() {
	return utilityAddress;
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
		setRouteID( (Integer) results[0] );
		setUtilityAddress( (Integer) results[1] );
		setSectionAddress( (Integer) results[2] );
		setClassAddress( (Integer) results[3] );
		setDivisionAddress( (Integer) results[4] );
		setAddressUsage( (String) results[5] );
		setRelayUsage( (String) results[6] );
		setSerialAddress( (String) results[7] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved: " + results.length);

}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setAddressUsage(String newValue) {
	this.addressUsage = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setClassAddress(Integer newValue) {
	this.classAddress = newValue;
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
public void setDivisionAddress(Integer newValue) {
	this.divisionAddress = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setRelayUsage(String newValue) {
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
public void setSectionAddress(Integer newValue) {
	this.sectionAddress = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2001 5:15:22 PM)
 * @param newSerialAddress java.lang.String
 */
public void setSerialAddress(java.lang.String newSerialAddress) {
	serialAddress = newSerialAddress;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setUtilityAddress(Integer newValue) {
	this.utilityAddress = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getRouteID(), getUtilityAddress(), 
		getSectionAddress(), getClassAddress(), getDivisionAddress(), 
		getAddressUsage(), getRelayUsage(), getSerialAddress() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
