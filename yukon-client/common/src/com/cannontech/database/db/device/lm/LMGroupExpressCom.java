package com.cannontech.database.db.device.lm;

/**
 * This type was created in VisualAge.
 */
public class LMGroupExpressCom extends com.cannontech.database.db.DBPersistent 
{
	private Integer lmGroupID = null;
	private Integer routeID = null;
	private String serialNumber = IlmDefines.NONE_ADDRESS_ID.toString();
	private Integer serviceProviderID = IlmDefines.NONE_ADDRESS_ID;
	private Integer geoID = IlmDefines.NONE_ADDRESS_ID;
	private Integer substationID = IlmDefines.NONE_ADDRESS_ID;
	private Integer feederID = IlmDefines.NONE_ADDRESS_ID;
	private Integer zipID = IlmDefines.NONE_ADDRESS_ID;
	private Integer userID = IlmDefines.NONE_ADDRESS_ID;
	private Integer programID = IlmDefines.NONE_ADDRESS_ID;
	private Integer splinterID = IlmDefines.NONE_ADDRESS_ID;
	private String addressUsage = " "; //default none
	private String relayUsage = " "; //default none

	public static final String SETTER_COLUMNS[] = 
	{ 
		"RouteID", "SerialNumber", "ServiceProviderID", "GeoID",
		"SubstationID", "FeederID", "ZipID", "UserID",
		"ProgramID", "SplinterID", "AddressUsage", "RelayUsage"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "LMGroupID" };

	public static final String TABLE_NAME = "LMGroupExpressCom";
/**
 * LMGroupVersacom constructor comment.
 */
public LMGroupExpressCom() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getLmGroupID(), getRouteID(), 
		getSerialNumber(), getServiceProviderID(), getGeoID(),
		getSubstationID(), getFeederID(), getZipID(), 
		getUserID(), getProgramID(), getSplinterID(),
		getAddressUsage(), getRelayUsage() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getLmGroupID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getAddressUsage() {
	return addressUsage;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getFeederID() {
	return feederID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getGeoID() {
	return geoID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLmGroupID() {
	return lmGroupID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getProgramID() {
	return programID;
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
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @return java.lang.String
 */
public java.lang.String getSerialNumber() {
	return serialNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getServiceProviderID() {
	return serviceProviderID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSplinterID() {
	return splinterID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSubstationID() {
	return substationID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getUserID() {
	return userID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getZipID() {
	return zipID;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public final static boolean isAddressUsed( java.sql.Connection conn, int addID )
{
	int retVal = 0;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
		
	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection can not be (null).");
		}
		else if( addID > 0 ) //do not include the (none) addressID
		{
			pstmt = conn.prepareStatement("select count(AddressID) AS cnt from " + 
						TABLE_NAME + 
						" where ServiceProviderID = " + addID +
						" or GeoID = " + addID +
						" or SubstationID = " + addID +
						" or FeederID = " + addID +
						" or ProgramID = " + addID +
						" or UserID = " + addID +
						" or SplinterID = " + addID +
						" or ZipID = " + addID );

			rset = pstmt.executeQuery();							

			// Just one please
			if( rset.next() )
				retVal = rset.getInt("cnt");

			return( retVal > 0 );
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( pstmt != null ) pstmt.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}

	return false;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public final static void purgeUnusedAddresses( java.sql.Connection conn )
{
	int retVal = 0;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
		
	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection can not be (null).");
		}
		else		
		{
			//do not include the (none) addressID			
			pstmt = conn.prepareStatement(
							"delete from " + 
							LMGroupExpressComAddress.TABLE_NAME +
							" where addressid not in (select serviceproviderid from LMGroupExpressCom) " +
							"and addressid not in (select GeoID from LMGroupExpressCom) " +
							"and addressid not in (select SubstationID from LMGroupExpressCom)  " +
							"and addressid not in (select FeederID from LMGroupExpressCom) " +
							"and addressid not in (select ProgramID from LMGroupExpressCom) " +
							"and addressid not in (select SplinterID from LMGroupExpressCom) " +
							"and addressid not in (select UserID from LMGroupExpressCom) " +
							"and addressid not in (select ZipID from LMGroupExpressCom) " +
							"and addressid > " + IlmDefines.NONE_ADDRESS_ID );

			pstmt.executeUpdate();
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( pstmt != null ) pstmt.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}

}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getLmGroupID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setRouteID( (Integer) results[0] );
		setSerialNumber( (String) results[1] );
		setServiceProviderID( (Integer) results[2] );
		setGeoID( (Integer) results[3] );
		setSubstationID( (Integer) results[4] );
		setFeederID( (Integer) results[5] );
		setZipID( (Integer) results[6] );
		setUserID( (Integer) results[7] );
		setProgramID( (Integer) results[8] );
		setSplinterID( (Integer) results[9] );
		setAddressUsage( (String) results[10] );
		setRelayUsage( (String) results[11] );		
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setAddressUsage(String newValue) {
	this.addressUsage = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @param newFeederID java.lang.Integer
 */
public void setFeederID(java.lang.Integer newFeederID) {
	feederID = newFeederID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @param newGeoID java.lang.Integer
 */
public void setGeoID(java.lang.Integer newGeoID) {
	geoID = newGeoID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @param newLmGroupID java.lang.Integer
 */
public void setLmGroupID(java.lang.Integer newLmGroupID) {
	lmGroupID = newLmGroupID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @param newProgramID java.lang.Integer
 */
public void setProgramID(java.lang.Integer newProgramID) {
	programID = newProgramID;
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
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @param newSerialNumber java.lang.String
 */
public void setSerialNumber(java.lang.String newSerialNumber) {
	serialNumber = newSerialNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @param newServiceProviderID java.lang.Integer
 */
public void setServiceProviderID(java.lang.Integer newServiceProviderID) {
	serviceProviderID = newServiceProviderID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @param newSplinterID java.lang.Integer
 */
public void setSplinterID(java.lang.Integer newSplinterID) {
	splinterID = newSplinterID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @param newSubstationID java.lang.Integer
 */
public void setSubstationID(java.lang.Integer newSubstationID) {
	substationID = newSubstationID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @param newUserID java.lang.Integer
 */
public void setUserID(java.lang.Integer newUserID) {
	userID = newUserID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 9:53:12 AM)
 * @param newZipID java.lang.Integer
 */
public void setZipID(java.lang.Integer newZipID) {
	zipID = newZipID;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = {  getRouteID(), 
		getSerialNumber(), getServiceProviderID(), getGeoID(),
		getSubstationID(), getFeederID(), getZipID(), 
		getUserID(), getProgramID(), getSplinterID(),
		getAddressUsage(), getRelayUsage() };

	Object constraintValues[] = { getLmGroupID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
