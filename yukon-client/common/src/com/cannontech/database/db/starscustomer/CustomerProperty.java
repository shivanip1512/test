package com.cannontech.database.db.starscustomer;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class CustomerProperty extends com.cannontech.database.db.DBPersistent 
{
	private Integer propertyID = null;
	private Integer serviceID = null;
	private String propertyNumber = null;
	private Integer streetAddress = null;
	private String propertyNotes = null;

	public static final String[] SETTER_COLUMNS = 
	{ 
		"ServiceID", "PropertyNumber", "StreetAddress", "PropertyNotes"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "PropertyID" };
	
	public static final String TABLE_NAME = "CustomerProperty";
/**
 * CustomerWebSettings constructor comment.
 */
public CustomerProperty() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{ 
		getPropertyID(), getServiceID(), getPropertyNumber(),
		getStreetAddress(), getPropertyNotes()
	};

	add( TABLE_NAME, addValues );
}
/**
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getPropertyID() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:56:20 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPropertyID() {
	return propertyID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:56:20 PM)
 * @return java.lang.String
 */
public java.lang.String getPropertyNotes() {
	return propertyNotes;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:56:20 PM)
 * @return java.lang.String
 */
public java.lang.String getPropertyNumber() {
	return propertyNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:56:20 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getServiceID() {
	return serviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:56:20 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getStreetAddress() {
	return streetAddress;
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	Object[] constraintValues =  { getPropertyID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setServiceID( (Integer) results[0] );
		setPropertyNumber( (String) results[1] );
		setStreetAddress( (Integer) results[2] );
		setPropertyNotes( (String) results[3] );
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:56:20 PM)
 * @param newPropertyID java.lang.Integer
 */
public void setPropertyID(java.lang.Integer newPropertyID) {
	propertyID = newPropertyID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:56:20 PM)
 * @param newPropertyNotes java.lang.String
 */
public void setPropertyNotes(java.lang.String newPropertyNotes) {
	propertyNotes = newPropertyNotes;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:56:20 PM)
 * @param newPropertyNumber java.lang.String
 */
public void setPropertyNumber(java.lang.String newPropertyNumber) {
	propertyNumber = newPropertyNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:56:20 PM)
 * @param newServiceID java.lang.Integer
 */
public void setServiceID(java.lang.Integer newServiceID) {
	serviceID = newServiceID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/2002 4:56:20 PM)
 * @param newStreetAddress java.lang.Integer
 */
public void setStreetAddress(java.lang.Integer newStreetAddress) {
	streetAddress = newStreetAddress;
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = 
	{
		getServiceID(), getPropertyNumber(),
		getStreetAddress(), getPropertyNotes()
	};

	Object[] constraintValues =  { getPropertyID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
