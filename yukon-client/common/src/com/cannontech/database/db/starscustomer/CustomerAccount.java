package com.cannontech.database.db.starscustomer;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class CustomerAccount extends com.cannontech.database.db.DBPersistent 
{
	private Integer accountID = null;
	private Integer propertyID = null;
	private String accountNumber = null;
	private Integer customerID = null;
	private Integer billingAddressID = null;
	private Integer energyCompanyID = null;
	private String accountNotes = null;

	public static final String[] SETTER_COLUMNS = 
	{ 
		"PropertyID", "AccountNumber", "CustomerID", "BillingAddressID",
		"EnergyCompanyID", "AccountNotes"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "AccountID" };
	
	public static final String TABLE_NAME = "CustomerAccount";
/**
 * CustomerWebSettings constructor comment.
 */
public CustomerAccount() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{ 
		getAccountID(), getPropertyID(), getAccountNumber(),
		getCustomerID(), getBillingAddressID(), getEnergyCompanyID(),
		getAccountNotes()
	};

	add( TABLE_NAME, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getAccountID() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:43:09 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getAccountID() {
	return accountID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:51:35 PM)
 * @return java.lang.String
 */
public java.lang.String getAccountNotes() {
	return accountNotes;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:51:35 PM)
 * @return java.lang.String
 */
public java.lang.String getAccountNumber() {
	return accountNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:51:35 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getBillingAddressID() {
	return billingAddressID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:51:35 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCustomerID() {
	return customerID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:51:35 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getEnergyCompanyID() {
	return energyCompanyID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:51:35 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPropertyID() {
	return propertyID;
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	Object[] constraintValues =  { getAccountID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setPropertyID( (Integer)results[0] );
		setAccountNumber( (String)results[1] );
		setCustomerID( (Integer)results[2] );		
		setBillingAddressID( (Integer)results[3] );
		setEnergyCompanyID( (Integer)results[4] );
		setAccountNotes( (String) results[5] );
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:43:09 PM)
 * @param newAccountID java.lang.Integer
 */
public void setAccountID(java.lang.Integer newAccountID) {
	accountID = newAccountID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:51:35 PM)
 * @param newAccountNotes java.lang.String
 */
public void setAccountNotes(java.lang.String newAccountNotes) {
	accountNotes = newAccountNotes;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:51:35 PM)
 * @param newAccountNumber java.lang.String
 */
public void setAccountNumber(java.lang.String newAccountNumber) {
	accountNumber = newAccountNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:51:35 PM)
 * @param newBillingAddressID java.lang.Integer
 */
public void setBillingAddressID(java.lang.Integer newBillingAddressID) {
	billingAddressID = newBillingAddressID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:51:35 PM)
 * @param newCustomerID java.lang.Integer
 */
public void setCustomerID(java.lang.Integer newCustomerID) {
	customerID = newCustomerID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:51:35 PM)
 * @param newEnergyCompanyID java.lang.Integer
 */
public void setEnergyCompanyID(java.lang.Integer newEnergyCompanyID) {
	energyCompanyID = newEnergyCompanyID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:51:35 PM)
 * @param newPropertyID java.lang.Integer
 */
public void setPropertyID(java.lang.Integer newPropertyID) {
	propertyID = newPropertyID;
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = 
	{
		getPropertyID(), getAccountNumber(),
		getCustomerID(), getBillingAddressID(), getEnergyCompanyID(),
		getAccountNotes()
	};

	Object[] constraintValues =  { getAccountID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
