package com.cannontech.database.db.starshardware;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class CustomerHardwareBase extends com.cannontech.database.db.DBPersistent 
{
	private Integer hardwareID = null;
	private Integer accountID = null;
	private Integer companyID = null;
	private String category = null;
	private java.util.Date receiveDate = null;
	private java.util.Date installDate = null;
	private java.util.Date removeDate = null;
	private String alternateTrackingNumber = null;
	private Float voltage = null;
	private String notes = null;
	private Integer deviceID = null;
	
	public static final String[] SETTER_COLUMNS = 
	{ 
		"AccountID", "CompanyID", "Category", "ReceiveDate", "InstallDate",
		"RemoveDate", "AlternateTrackingNumber", "Voltage",
		"Notes", "DeviceID"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "HardwareID" };
	
	public static final String TABLE_NAME = "CustomerHardwareBase";
/**
 * CustomerWebSettings constructor comment.
 */
public CustomerHardwareBase() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{ 
		getHardwareID(), getAccountID(), getCompanyID(), getCategory(), getReceiveDate(),
		getInstallDate(), getRemoveDate(), getAlternateTrackingNumber(),
		getVoltage(), getNotes(), getDeviceID()
	};

	add( TABLE_NAME, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getHardwareID() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:22:19 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getAccountID() {
	return accountID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:22:19 AM)
 * @return java.lang.String
 */
public java.lang.String getAlternateTrackingNumber() {
	return alternateTrackingNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:22:19 AM)
 * @return java.lang.String
 */
public java.lang.String getCategory() {
	return category;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 2:03:57 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCompanyID() {
	return companyID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:22:19 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDeviceID() {
	return deviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:16:48 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getHardwareID() {
	return hardwareID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:22:19 AM)
 * @return java.util.Date
 */
public java.util.Date getInstallDate() {
	return installDate;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:22:19 AM)
 * @return java.lang.String
 */
public java.lang.String getNotes() {
	return notes;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:22:19 AM)
 * @return java.util.Date
 */
public java.util.Date getReceiveDate() {
	return receiveDate;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:22:19 AM)
 * @return java.util.Date
 */
public java.util.Date getRemoveDate() {
	return removeDate;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:22:19 AM)
 * @return java.lang.Float
 */
public java.lang.Float getVoltage() {
	return voltage;
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	Object[] constraintValues =  { getHardwareID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setAccountID( (Integer) results[0] );
		setCompanyID( (Integer) results[1] );
		setCategory( (String) results[2] );
		setReceiveDate( new java.util.Date(((java.sql.Timestamp)results[3]).getTime()) );
		setInstallDate( new java.util.Date(((java.sql.Timestamp)results[4]).getTime()) );
		setRemoveDate( new java.util.Date(((java.sql.Timestamp)results[5]).getTime()) );
		setAlternateTrackingNumber( (String) results[6] );
		setVoltage( (Float) results[7] );
		setNotes( (String) results[8] );
		setDeviceID( (Integer) results[9] );
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:22:19 AM)
 * @param newAccountID java.lang.Integer
 */
public void setAccountID(java.lang.Integer newAccountID) {
	accountID = newAccountID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:22:19 AM)
 * @param newAlternateTrackingNumber java.lang.String
 */
public void setAlternateTrackingNumber(java.lang.String newAlternateTrackingNumber) {
	alternateTrackingNumber = newAlternateTrackingNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:22:19 AM)
 * @param newCategory java.lang.String
 */
public void setCategory(java.lang.String newCategory) {
	category = newCategory;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 2:03:57 PM)
 * @param newCompanyID java.lang.Integer
 */
public void setCompanyID(java.lang.Integer newCompanyID) {
	companyID = newCompanyID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:22:19 AM)
 * @param newDeviceID java.lang.Integer
 */
public void setDeviceID(java.lang.Integer newDeviceID) {
	deviceID = newDeviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:16:48 PM)
 * @param newHardwareID java.lang.Integer
 */
public void setHardwareID(java.lang.Integer newHardwareID) {
	hardwareID = newHardwareID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:22:19 AM)
 * @param newInstallDate java.util.Date
 */
public void setInstallDate(java.util.Date newInstallDate) {
	installDate = newInstallDate;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:22:19 AM)
 * @param newNotes java.lang.String
 */
public void setNotes(java.lang.String newNotes) {
	notes = newNotes;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:22:19 AM)
 * @param newReceiveDate java.util.Date
 */
public void setReceiveDate(java.util.Date newReceiveDate) {
	receiveDate = newReceiveDate;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:22:19 AM)
 * @param newRemoveDate java.util.Date
 */
public void setRemoveDate(java.util.Date newRemoveDate) {
	removeDate = newRemoveDate;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:22:19 AM)
 * @param newVoltage java.lang.Float
 */
public void setVoltage(java.lang.Float newVoltage) {
	voltage = newVoltage;
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = 
	{
		getAccountID(), getCompanyID(), getCategory(), getReceiveDate(),
		getInstallDate(), getRemoveDate(), getAlternateTrackingNumber(),
		getVoltage(), getNotes(), getDeviceID()
	};

	Object[] constraintValues =  { getHardwareID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
