package com.cannontech.database.db.starscustomer;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class CustomerServiceOrder extends com.cannontech.database.db.DBPersistent 
{
	private Integer orderID = null;
	private Integer accountID = null;
	private Integer companyID = null;
	private Integer actionID = null;
	private Integer orderNumber = null;
	private String description = null;
	private java.util.Date dateCreated = null;


	public static final String[] SETTER_COLUMNS = 
	{ 
		"AccountID", "CompanyID", "ActionID", "OrderNumber",
		"Description", "DateCreated"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "OrderID" };
	
	public static final String TABLE_NAME = "CustomerServiceOrder";
/**
 * CustomerWebSettings constructor comment.
 */
public CustomerServiceOrder() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{ 
		getOrderID(), getAccountID(), getCompanyID(),
		getActionID(), getOrderNumber(), getDescription(),
		getDateCreated()
	};

	add( TABLE_NAME, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getOrderID() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:03:07 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getAccountID() {
	return accountID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:03:07 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getActionID() {
	return actionID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:03:07 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCompanyID() {
	return companyID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:03:08 PM)
 * @return java.util.Date
 */
public java.util.Date getDateCreated() {
	return dateCreated;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:48:03 PM)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	return description;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:03:08 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getOrderID() {
	return orderID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:03:08 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getOrderNumber() {
	return orderNumber;
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	Object[] constraintValues =  { getOrderID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setAccountID( (Integer) results[0] );
		setCompanyID( (Integer) results[1] );
		setActionID( (Integer) results[2] );
		setOrderNumber( (Integer) results[3] );
		setDescription( (String) results[4] );
		setDateCreated( new java.util.Date(((java.sql.Timestamp)results[5]).getTime()) );		
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:03:07 PM)
 * @param newAccountID java.lang.Integer
 */
public void setAccountID(java.lang.Integer newAccountID) {
	accountID = newAccountID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:03:07 PM)
 * @param newActionID java.lang.Integer
 */
public void setActionID(java.lang.Integer newActionID) {
	actionID = newActionID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:03:07 PM)
 * @param newCompanyID java.lang.Integer
 */
public void setCompanyID(java.lang.Integer newCompanyID) {
	companyID = newCompanyID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:03:08 PM)
 * @param newDateCreated java.util.Date
 */
public void setDateCreated(java.util.Date newDateCreated) {
	dateCreated = newDateCreated;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:48:03 PM)
 * @param newDescription java.lang.String
 */
public void setDescription(java.lang.String newDescription) {
	description = newDescription;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:03:08 PM)
 * @param newOrderID java.lang.Integer
 */
public void setOrderID(java.lang.Integer newOrderID) {
	orderID = newOrderID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 5:03:08 PM)
 * @param newOrderNumber java.lang.Integer
 */
public void setOrderNumber(java.lang.Integer newOrderNumber) {
	orderNumber = newOrderNumber;
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = 
	{
		getAccountID(), getCompanyID(),
		getActionID(), getOrderNumber(), getDescription(),
		getDateCreated()
	};

	Object[] constraintValues =  { getOrderID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
