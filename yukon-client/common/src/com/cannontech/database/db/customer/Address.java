package com.cannontech.database.db.customer;

import com.cannontech.common.util.CtiUtilities;

/**
 * This type was created in VisualAge.
 */

public class Address extends com.cannontech.database.db.DBPersistent 
{
	private Integer addressID = null;
	private String locationAddress1 = CtiUtilities.STRING_NONE;
	private String locationAddress2 = CtiUtilities.STRING_NONE;
	private String cityName = CtiUtilities.STRING_NONE;
	private String stateCode = "MN";
	private String zipCode = CtiUtilities.STRING_NONE;
	private String county = CtiUtilities.STRING_NONE;


	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"LocationAddress1", "LocationAddress2", "CityName", 
		"StateCode", "ZipCode", "County"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "AddressID" };

	public static final String TABLE_NAME = "Address";
	
/**
 * Address constructor comment.
 */
public Address() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	if( getAddressID() == null )
		setAddressID( getNextAddressID( getDbConnection() ) );

	Object addValues[] =
	{ 
		getAddressID(), getLocationAddress1(), getLocationAddress2(),
		getCityName(), getStateCode(), getZipCode(), getCounty()
	};

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
	if( conn == null )
		throw new IllegalStateException("Database connection should not be null.");

	
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	
	try 
	{		
	    stmt = conn.createStatement();
		 rset = stmt.executeQuery( 
				"SELECT Max(AddressID)+1 FROM " + TABLE_NAME );	
			
		 //get the first returned result
		 rset.next();
	    return new Integer( rset.getInt(1) );
	}
	catch (java.sql.SQLException e) 
	{
	    e.printStackTrace();
	}
	finally 
	{
	    try 
	    {
			if ( stmt != null) stmt.close();
	    }
	    catch (java.sql.SQLException e2) 
	    {
			e2.printStackTrace();
	    }
	}
	
	//strange, should not get here
	return new Integer(CtiUtilities.NONE_ZERO_ID);
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
		setCounty( (String) results[5] );
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
	Object setValues[] = 
	{ 
		getLocationAddress1(), getLocationAddress2(),
		getCityName(), getStateCode(), getZipCode(), getCounty()
	};

	Object constraintValues[] = { getAddressID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}


	/**
	 * Returns the county.
	 * @return String
	 */
	public String getCounty() {
		return county;
	}

	/**
	 * Sets the county.
	 * @param county The county to set
	 */
	public void setCounty(String county) {
		this.county = county;
	}

}
