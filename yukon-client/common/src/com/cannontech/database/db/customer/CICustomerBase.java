package com.cannontech.database.db.customer;

/**
 * This type was created in VisualAge.
 */

public class CICustomerBase extends com.cannontech.database.db.DBPersistent 
{
	public static final int NONE_INT = 0;

	/* Set attributes to null when a user must enter them*/
	private Integer deviceID = null;
	private Integer addressID = new Integer(NONE_INT);
	private String mainPhoneNumber = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	private String mainFaxNumber = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	private Double custFPL = new Double(0.0);
	private Integer primeContactID = new Integer(NONE_INT);
	private String custTimeZone = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	private String curtailmentAgreement = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	private Double curtailAmount = new Double(0.0);

	public static final String SETTER_COLUMNS[] = 
	{ 
		"AddressID", "MainPhoneNumber", "MainFaxNumber",
		"CustFPL", "PrimeContactID", "CustTimeZone", "CurtailmentAgreement",
		"CurtailAmount"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "deviceID" };

	public static final String TABLE_NAME = "CICustomerBase";

	private static final String GET_CUSTOMER_FROM_CONTACT_SQL =
			"SELECT " +
			TABLE_NAME + ".DeviceID," +										
			TABLE_NAME + ".AddressID," +
			TABLE_NAME + ".MainPhoneNumber," +
			TABLE_NAME + ".MainFaxNumber," +
			TABLE_NAME + ".CustFPL," +
			TABLE_NAME + ".PrimeContactID," +
			TABLE_NAME + ".CustTimeZone," +
			TABLE_NAME + ".CurtailmentAgreement," +
			TABLE_NAME + ".CurtailAmount " +
			"FROM " + TABLE_NAME + ",CICustContact WHERE " +
			TABLE_NAME + ".DeviceID=CICustContact.DeviceID AND CICustContact.ContactID=?";
/**
 * LMGroupVersacomSerial constructor comment.
 */
public CICustomerBase() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getAddressID(),
					getMainPhoneNumber(),
					getMainFaxNumber(), getCustFPL(), getPrimeContactID(),
					getCustTimeZone(), getCurtailmentAgreement(),
					getCurtailAmount() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	String values[] = { getDeviceID().toString() };

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
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.State[]
 * @param stateGroup java.lang.Integer
 */
public static final com.cannontech.database.db.graph.GraphCustomerList[] getAllGraphCustomerList(Integer customerID, java.sql.Connection conn) throws java.sql.SQLException
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT GraphDefinitionID,CustomerID,CustomerOrder " +
				 "FROM " + com.cannontech.database.db.graph.GraphCustomerList.TABLE_NAME + 
				 " WHERE CustomerID= ?";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be null.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, customerID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				com.cannontech.database.db.graph.GraphCustomerList item = new com.cannontech.database.db.graph.GraphCustomerList();

				item.setDbConnection(conn);
				item.setGraphDefinitionID( new Integer(rset.getInt("GraphDefinitionID")) );
				item.setCustomerID( new Integer(rset.getInt("CustomerID")) );
				item.setCustomerOrder( new Integer(rset.getInt("CustomerOrder")) );

				tmpList.add( item );
			}
					
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
			if( rset != null ) rset.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}


	com.cannontech.database.db.graph.GraphCustomerList retVal[] = new com.cannontech.database.db.graph.GraphCustomerList[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2001 3:29:40 PM)
 * @return java.lang.Double
 */
public java.lang.Double getCurtailAmount() {
	return curtailAmount;
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2001 2:27:57 PM)
 * @return java.lang.String
 */
public java.lang.String getCurtailmentAgreement() {
	return curtailmentAgreement;
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2001 3:29:40 PM)
 * @return java.lang.Double
 */
public java.lang.Double getCustFPL() {
	return custFPL;
}
/**
 * Insert the method's description here.
 * Creation date: (12/14/99 10:31:33 AM)
 * @return java.lang.Integer
 */
public static synchronized Integer getCustomerAddressID( Integer customerID, java.sql.Connection conn )
{
	java.sql.PreparedStatement stat = null;
	java.sql.ResultSet rs = null;
	int value = -1;

	try
	{
		stat = conn.prepareStatement(
			 "select addressid, deviceid from cicustomerbase where deviceid = ?" );
		
		stat.setInt( 1, customerID.intValue() );
		rs = stat.executeQuery();

		while( rs.next() )
			value = rs.getInt("addressid");

	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( stat != null )
				stat.close();
			if( rs != null )
				rs.close();
		}
		catch(java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}

	return new Integer(value);
}
/**
 * Retrieve a CICustomerBase with a specific contactID.
 * @return CustomerContact[]
 * @param stateGroup java.lang.Integer
 */
public static final CICustomerBase getCustomerContact(Integer contactID) throws java.sql.SQLException
{
 	return getCustomerContact(contactID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
}
/**
 * Retrieve a CICustomerBase with a specific contactID.
 * @return CustomerContact[]
 * @param stateGroup java.lang.Integer
 */
public static final CICustomerBase getCustomerContact(Integer contactID, String databaseAlias) throws java.sql.SQLException
{
 	CICustomerBase retVal = null;
	
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
		
	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);

		if( conn == null )
		{
			throw new IllegalStateException("Error getting database connection.");
		}
		else
		{
com.cannontech.clientutils.CTILogger.info(GET_CUSTOMER_FROM_CONTACT_SQL);
			pstmt = conn.prepareStatement(GET_CUSTOMER_FROM_CONTACT_SQL);
			pstmt.setInt( 1, contactID.intValue() );
			
			rset = pstmt.executeQuery();							

			// Just one please
			if( rset.next() )
			{
				retVal = new CICustomerBase();

				retVal.setDeviceID( new Integer(rset.getInt("DeviceID")));				
				retVal.setAddressID( new Integer(rset.getInt("AddressID")));
				retVal.setMainPhoneNumber( rset.getString("MainPhoneNumber"));
				retVal.setMainFaxNumber( rset.getString("MainFaxNumber"));
				retVal.setCustFPL( new Double( rset.getFloat("CustFPL")));
				retVal.setPrimeContactID( new Integer( rset.getInt("PrimeContactID")));
				retVal.setCustTimeZone( rset.getString("CustTimeZone"));
				retVal.setCurtailAmount( new Double( rset.getFloat("CurtailAmount")));
			}					
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
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}

	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:39:26 PM)
 * @return java.lang.String
 */
public java.lang.String getCustTimeZone() {
	return custTimeZone;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:39:26 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDeviceID() {
	return deviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:39:26 PM)
 * @return java.lang.String
 */
public java.lang.String getMainFaxNumber() {
	return mainFaxNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:39:26 PM)
 * @return java.lang.String
 */
public java.lang.String getMainPhoneNumber() {
	return mainPhoneNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:39:26 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPrimeContactID() {
	return primeContactID;
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
		setAddressID( (Integer) results[0] );
		setMainPhoneNumber( (String) results[1] );
		setMainFaxNumber( (String) results[2] );
		setCustFPL( (Double)results[3] );
		setPrimeContactID( (Integer) results[4] );
		setCustTimeZone( (String) results[5] );
		setCurtailmentAgreement( (String) results[6] );
		setCurtailAmount( (Double)results[7] );
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
 * Creation date: (10/16/2001 3:29:40 PM)
 * @param newCurtailAmount java.lang.Double
 */
public void setCurtailAmount(java.lang.Double newCurtailAmount) {
	curtailAmount = newCurtailAmount;
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2001 2:27:57 PM)
 * @param newCurtailmentAgreement java.lang.String
 */
public void setCurtailmentAgreement(java.lang.String newCurtailmentAgreement) {
	curtailmentAgreement = newCurtailmentAgreement;
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2001 3:29:40 PM)
 * @param newCustFPL java.lang.Double
 */
public void setCustFPL(java.lang.Double newCustFPL) {
	custFPL = newCustFPL;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:39:26 PM)
 * @param newCustTimeZone java.lang.String
 */
public void setCustTimeZone(java.lang.String newCustTimeZone) {
	custTimeZone = newCustTimeZone;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:39:26 PM)
 * @param newDeviceID java.lang.Integer
 */
public void setDeviceID(java.lang.Integer newDeviceID) {
	deviceID = newDeviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:39:26 PM)
 * @param newMainFaxNumber java.lang.String
 */
public void setMainFaxNumber(java.lang.String newMainFaxNumber) {
	mainFaxNumber = newMainFaxNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:39:26 PM)
 * @param newMainPhoneNumber java.lang.String
 */
public void setMainPhoneNumber(java.lang.String newMainPhoneNumber) {
	mainPhoneNumber = newMainPhoneNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:39:26 PM)
 * @param newPrimeContactID java.lang.Integer
 */
public void setPrimeContactID(java.lang.Integer newPrimeContactID) {
	primeContactID = newPrimeContactID;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getAddressID(), getMainPhoneNumber(),
					getMainFaxNumber(), getCustFPL(), getPrimeContactID(),
					getCustTimeZone(), getCurtailmentAgreement(),
					getCurtailAmount() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
