package com.cannontech.database.db.customer;

/**
 * This type was created in VisualAge.
 */

public class CustomerAddress extends com.cannontech.database.db.DBPersistent 
{
	public static final int NONE_INT = 0;
	
	private Integer addressID = null;
	private String locationAddress1 = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	private String locationAddress2 = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	private String cityName = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	private String stateCode = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	private String zipCode = com.cannontech.common.util.CtiUtilities.STRING_NONE;


	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"LocationAddress1", "LocationAddress2", "CityName", 
		"StateCode", "ZipCode"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "addressID" };

	public static final String TABLE_NAME = "CustomerAddress";

/**
 * LMGroupVersacomSerial constructor comment.
 */
public CustomerAddress() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	if( getAddressID() == null )
		setAddressID( getNextAddressID( getDbConnection() ) );

	Object addValues[] = { getAddressID(), getLocationAddress1(), getLocationAddress2(),
					getCityName(), getStateCode(), getZipCode() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	String values[] = { getAddressID().toString() };

	delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:39:26 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getAddressID() {
	return addressID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:44:46 PM)
 * @return java.lang.String
 */
public java.lang.String getCityName() {
	return cityName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:44:46 PM)
 * @return java.lang.String
 */
public java.lang.String getLocationAddress1() {
	return locationAddress1;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:44:46 PM)
 * @return java.lang.String
 */
public java.lang.String getLocationAddress2() {
	return locationAddress2;
}
/**
 * Insert the method's description here.
 * Creation date: (12/14/99 10:31:33 AM)
 * @return java.lang.Integer
 */
public static synchronized Integer getNextAddressID( java.sql.Connection conn )
{
	com.cannontech.database.SqlStatement stmt =
 		new com.cannontech.database.SqlStatement(
            "SELECT AddressID FROM CustomerAddress order by AddressID",
 				conn );

	Integer returnVal = null;
	int value = 0;
														
	try
	{
		stmt.execute();
		
		for( int i = 0; i < stmt.getRowCount(); i++ )
		{
			if( value == ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue() )
				value++;
			else
				break;
		}	

	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

	return new Integer(value);
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:44:46 PM)
 * @return java.lang.String
 */
public java.lang.String getStateCode() {
	return stateCode;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:44:46 PM)
 * @return java.lang.String
 */
public java.lang.String getZipCode() {
	return zipCode;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getAddressID() };	
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setLocationAddress1( (String) results[0] );
		setLocationAddress2( (String) results[1] );
		setCityName( (String) results[2] );
		setStateCode( (String) results[3] );
		setZipCode( (String) results[4] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:39:26 PM)
 * @param newAddressID java.lang.Integer
 */
public void setAddressID(java.lang.Integer newAddressID) {
	addressID = newAddressID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:44:46 PM)
 * @param newCityName java.lang.String
 */
public void setCityName(java.lang.String newCityName) {
	cityName = newCityName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:44:46 PM)
 * @param newLocationAddress1 java.lang.String
 */
public void setLocationAddress1(java.lang.String newLocationAddress1) {
	locationAddress1 = newLocationAddress1;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:44:46 PM)
 * @param newLocationAddress2 java.lang.String
 */
public void setLocationAddress2(java.lang.String newLocationAddress2) {
	locationAddress2 = newLocationAddress2;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:44:46 PM)
 * @param newStateCode java.lang.String
 */
public void setStateCode(java.lang.String newStateCode) {
	stateCode = newStateCode;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:44:46 PM)
 * @param newZipCode java.lang.String
 */
public void setZipCode(java.lang.String newZipCode) {
	zipCode = newZipCode;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getLocationAddress1(), getLocationAddress2(),
					getCityName(), getStateCode(), getZipCode() };

	Object constraintValues[] = { getAddressID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
