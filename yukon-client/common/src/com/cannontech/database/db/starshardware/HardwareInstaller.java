package com.cannontech.database.db.starshardware;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class HardwareInstaller extends com.cannontech.database.db.DBPersistent 
{
	private Integer companyID = null;
	private String companyName = null;
	private Integer addressID = null;
	private String mainPhoneNumber = null;
	private String mainFaxNumber = null;
	private Integer primaryContactID = null;
	private String hiType= null;

	public static final String[] SETTER_COLUMNS = 
	{ 
		"CompanyName", "AddressID", "MainPhoneNumber",
		"MainFaxNumber", "PrimaryContactID", "HIType"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "CompanyID" };
	
	public static final String TABLE_NAME = "HardwareInstaller";
/**
 * CustomerWebSettings constructor comment.
 */
public HardwareInstaller() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{ 
		getCompanyID(), getCompanyName(), getAddressID(),
		getMainPhoneNumber(), getMainFaxNumber(),
		getPrimaryContactID(), getHiType()
	};

	add( TABLE_NAME, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getCompanyID() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:31:21 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getAddressID() {
	return addressID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:31:21 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCompanyID() {
	return companyID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:31:21 AM)
 * @return java.lang.String
 */
public java.lang.String getCompanyName() {
	return companyName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:31:21 AM)
 * @return java.lang.String
 */
public java.lang.String getHiType() {
	return hiType;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:31:21 AM)
 * @return java.lang.String
 */
public java.lang.String getMainFaxNumber() {
	return mainFaxNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:31:21 AM)
 * @return java.lang.String
 */
public java.lang.String getMainPhoneNumber() {
	return mainPhoneNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:31:21 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPrimaryContactID() {
	return primaryContactID;
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	Object[] constraintValues =  { getCompanyID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setCompanyName( (String) results[0] );
		setAddressID( (Integer) results[1] );
		setMainPhoneNumber( (String) results[2] );
		setMainFaxNumber( (String) results[3] );
		setPrimaryContactID( (Integer) results[4] );
		setHiType( (String) results[5] );
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:31:21 AM)
 * @param newAddressID java.lang.Integer
 */
public void setAddressID(java.lang.Integer newAddressID) {
	addressID = newAddressID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:31:21 AM)
 * @param newCompanyID java.lang.Integer
 */
public void setCompanyID(java.lang.Integer newCompanyID) {
	companyID = newCompanyID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:31:21 AM)
 * @param newCompanyName java.lang.String
 */
public void setCompanyName(java.lang.String newCompanyName) {
	companyName = newCompanyName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:31:21 AM)
 * @param newHiType java.lang.String
 */
public void setHiType(java.lang.String newHiType) {
	hiType = newHiType;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:31:21 AM)
 * @param newMainFaxNumber java.lang.String
 */
public void setMainFaxNumber(java.lang.String newMainFaxNumber) {
	mainFaxNumber = newMainFaxNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:31:21 AM)
 * @param newMainPhoneNumber java.lang.String
 */
public void setMainPhoneNumber(java.lang.String newMainPhoneNumber) {
	mainPhoneNumber = newMainPhoneNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2002 9:31:21 AM)
 * @param newPrimaryContactID java.lang.Integer
 */
public void setPrimaryContactID(java.lang.Integer newPrimaryContactID) {
	primaryContactID = newPrimaryContactID;
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = 
	{
		getCompanyName(), getAddressID(),
		getMainPhoneNumber(), getMainFaxNumber(),
		getPrimaryContactID(), getHiType()
	};

	Object[] constraintValues =  { getCompanyID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
