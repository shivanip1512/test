package com.cannontech.database.db.customer;

import com.cannontech.user.UserUtils;

/**
 * This type was created in VisualAge.
 */

public class CustomerContact extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private Integer contactID = null;
	private String contFirstName = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	private String contLastName = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	private String contPhone1 = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	private String contPhone2 = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	private Integer locationID = new Integer(com.cannontech.database.db.notification.NotificationRecipient.NONE_RECIPIENT_ID);
	private Integer logInID = new Integer(UserUtils.USER_YUKON_ID);

	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"ContFirstName", "ContLastName", "ContPhone1", 
		"ContPhone2", "locationID", "LogInID"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "contactID" };

	public static final String TABLE_NAME = "CustomerContact";

	private static final String GET_CUSTOMER_CONTACT_SQL =
		"SELECT ContactID,ContFirstName,ContLastName,ContPhone1,ContPhone2,LocationID,LogInID FROM " + 
		TABLE_NAME + " WHERE LoginID=?";
	
	private static final String GET_NEXT_CONTACT_ID_SQL =
		"SELECT MAX(ContactID) FROM " + TABLE_NAME;
/**
 * LMGroupVersacomSerial constructor comment.
 */
public CustomerContact() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	if( getContactID() == null )
		setContactID( getNextContactID() );
		
	Object addValues[] = { getContactID(), getContFirstName(), getContLastName(),
					getContPhone1(), getContPhone2(), getLocationID(),
					getLogInID() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	//just delete the bridge value to the CICustContact table
	delete("CICustContact", "contactID", getContactID());

	// delete the actual contact
	String values[] = { getContactID().toString() };

	delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
}
/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteAllCustomerContacts(Integer deviceID, java.sql.Connection conn )
{
	try
	{
		if( conn == null )
			throw new IllegalStateException("Database connection should not be null.");

		// get all the soon to be deleted contacts
		CustomerContact[] contacts = getAllCustomerContacts(deviceID, conn );

			
		//delete the references between the contacts and customer
		java.sql.Statement stat = conn.createStatement();	
		stat.execute( "DELETE FROM CICustContact WHERE deviceID=" + deviceID );

		if( stat != null )
			stat.close();
			
		java.sql.Statement stmt = null;


		if( contacts.length > 0 )
		{
			// now delete the actual contacts	
			StringBuffer contactString = new StringBuffer("DELETE FROM " + TABLE_NAME + " WHERE contactID=");
			StringBuffer loginString = new StringBuffer("DELETE FROM CustomerLogin WHERE loginid=");
			int loginCnt = 0;

			//load all the contactIDs that need to be deleted by the sql statement
			for( int i = 0; i < contacts.length; i++ ) 
			{
				contactString.append( contacts[i].getContactID() );

				if( contacts[i].getLogInID().intValue() > UserUtils.USER_YUKON_ID )
				{
					loginString.append( contacts[i].getLogInID() );
					loginCnt++;
				}
				
				if( i < (contacts.length-1) ) //are we at the end yet?
				{
					contactString.append(" OR contactID=" );

					if( contacts[i].getLogInID().intValue() > UserUtils.USER_YUKON_ID )
					{
						if( loginCnt > 0 )
							loginString.append(" OR loginID=" );
						//else
						//{
							//loginString.append( contacts[i].getLogInID() );
							//loginCnt++;
						//}
							
					}
					
				}
				
			}

			//delete the contacts
com.cannontech.clientutils.CTILogger.info( "		CNTACTDELETESTRING = " + contactString.toString() );
			stmt = conn.createStatement();
			stmt.execute( contactString.toString() );
			
			if( stmt != null )
				stmt.close();

			//delete the contact customerLogins
			if( loginCnt > 0 )
			{
com.cannontech.clientutils.CTILogger.info( "		LOGINDELETESTRING = " + loginString.toString() );
				stmt = conn.createStatement();
				stmt.execute( loginString.toString() );

				if( stmt != null )
					stmt.close();
			}
	
		}
	}
	catch(Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}


	return true;
}
/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteCustomerGraphList(Integer customerID, java.sql.Connection conn )
{
	try
	{
		if( conn == null )
			throw new IllegalStateException("Database connection should not be null.");

		java.sql.Statement stat = conn.createStatement();
		
		stat.execute( "DELETE FROM " + com.cannontech.database.db.graph.GraphCustomerList.TABLE_NAME + 
					" WHERE CustomerID=" + customerID );

		if( stat != null )
			stat.close();
	}
	catch(Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}


	return true;
}
/**
 * This method was created in VisualAge.
 * @return LMControlAreaTrigger[]
 * @param stateGroup java.lang.Integer
 */
public static final CustomerContact[] getAllCustomerContacts(Integer deviceID, java.sql.Connection conn ) throws java.sql.SQLException
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT cust.ContactID, cust.ContFirstName, cust.ContLastName, cust.ContPhone1, " +
		"cust.ContPhone2, cust.LocationID, cust.LogInID " + 
		"FROM " + TABLE_NAME + " cust, CICustContact list " +
		"WHERE list.DEVICEID= ? and list.CONTACTID=cust.CONTACTID";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be null.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, deviceID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				CustomerContact item = new CustomerContact();

				item.setDbConnection(conn);
				item.setContactID( new Integer(rset.getInt("ContactID")) );
				item.setContFirstName( rset.getString("ContFirstName") );
				item.setContLastName( rset.getString("ContLastName") );
				item.setContPhone1( rset.getString("ContPhone1") );
				item.setContPhone2( rset.getString("ContPhone2") );				
				item.setLocationID( new Integer(rset.getInt("LocationID")) );
				item.setLogInID( new Integer(rset.getInt("LogInID")) );

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
			if( rset != null ) rset.close();
			if( pstmt != null ) pstmt.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}


	CustomerContact retVal[] = new CustomerContact[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 3:07:17 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getContactID() {
	return contactID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:49:55 PM)
 * @return java.lang.String
 */
public java.lang.String getContFirstName() {
	return contFirstName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:49:55 PM)
 * @return java.lang.String
 */
public java.lang.String getContLastName() {
	return contLastName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:49:56 PM)
 * @return java.lang.String
 */
public java.lang.String getContPhone1() {
	return contPhone1;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:49:56 PM)
 * @return java.lang.String
 */
public java.lang.String getContPhone2() {
	return contPhone2;
}
/**
 * This method was created in VisualAge.
 * @return LMControlAreaTrigger[]
 * @param stateGroup java.lang.Integer
 */
public static final CustomerContact getCustomerContact(Integer loginID) throws java.sql.SQLException 
{
	return getCustomerContact(loginID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());												
}
/**
 * Retrieve a customerContact with a specific loginID.
 * @return CustomerContact[]
 * @param stateGroup java.lang.Integer
 */
public static final CustomerContact getCustomerContact(Integer loginID, String databaseAlias) throws java.sql.SQLException
{
	CustomerContact retVal = null;
	
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
			pstmt = conn.prepareStatement(GET_CUSTOMER_CONTACT_SQL);
			pstmt.setInt( 1, loginID.intValue() );
			
			rset = pstmt.executeQuery();							

			// Just one please
			if( rset.next() )
			{
				retVal = new CustomerContact();
				
				retVal.setContactID( new Integer(rset.getInt("ContactID")) );
				retVal.setContFirstName( rset.getString("ContFirstName") );
				retVal.setContLastName( rset.getString("ContLastName") );
				retVal.setContPhone1( rset.getString("ContPhone1") );
				retVal.setContPhone2( rset.getString("ContPhone2") );				
				retVal.setLocationID( new Integer(rset.getInt("LocationID")) );
				retVal.setLogInID( loginID );
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
 * Creation date: (12/19/2001 1:45:25 PM)
 * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
 */
public com.cannontech.message.dispatch.message.DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
{
	com.cannontech.message.dispatch.message.DBChangeMsg[] msgs =
	{
		new com.cannontech.message.dispatch.message.DBChangeMsg(
					getContactID().intValue(),
					com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_CUSTOMER_CONTACT_DB,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_CUSTOMERCONTACT,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_CUSTOMERCONTACT,
					typeOfChange)
	};


	return msgs;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:49:56 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLocationID() {
	return locationID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:49:56 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLogInID() {
	return logInID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public final Integer getNextContactID()
{
	java.util.ArrayList contacts = new java.util.ArrayList(30);
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT ContactID " + "FROM " + TABLE_NAME + " order by ContactID";

	try
	{		
		pstmt = getDbConnection().prepareStatement(sql.toString());
		
		rset = pstmt.executeQuery();							

		while( rset.next() )
		{
			contacts.add( new Integer(rset.getInt("ContactID")) );
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
			//if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 ); //something is up
		}	
	}


	//look for the next contactID	
	int counter = 1;
	int currentID;
	 														
	for(int i = 0; i < contacts.size(); i++)
	{
		currentID = ((Integer)contacts.get(i)).intValue();

		if( currentID > counter )
			break;
		else
			counter = currentID + 1;
	}		
	
	return new Integer( counter );
}

/**
 * Created by yao, get the next contact ID as the maximum existing ID + 1
 */
public final Integer getNextContactID2() {
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextContactID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_CONTACT_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextContactID = rset.getInt(1) + 1;
        }
        catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {
                e2.printStackTrace();
            }
        }

        return new Integer( nextContactID );
}

/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getContactID() };	
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setContFirstName( (String) results[0] );
		setContLastName( (String) results[1] );
		setContPhone1( (String) results[2] );
		setContPhone2( (String) results[3] );
		setLocationID( (Integer) results[4] );
		setLogInID( (Integer) results[5] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 3:07:17 PM)
 * @param newContactID java.lang.Integer
 */
public void setContactID(java.lang.Integer newContactID) {
	contactID = newContactID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:49:55 PM)
 * @param newContFirstName java.lang.String
 */
public void setContFirstName(java.lang.String newContFirstName) {
	contFirstName = newContFirstName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:49:55 PM)
 * @param newContLastName java.lang.String
 */
public void setContLastName(java.lang.String newContLastName) {
	contLastName = newContLastName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:49:56 PM)
 * @param newContPhone1 java.lang.String
 */
public void setContPhone1(java.lang.String newContPhone1) {
	contPhone1 = newContPhone1;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:49:56 PM)
 * @param newContPhone2 java.lang.String
 */
public void setContPhone2(java.lang.String newContPhone2) {
	contPhone2 = newContPhone2;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:49:56 PM)
 * @param newLocationID java.lang.Integer
 */
public void setLocationID(java.lang.Integer newLocationID) {
	locationID = newLocationID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 1:49:56 PM)
 * @param newLogInID java.lang.Integer
 */
public void setLogInID(java.lang.Integer newLogInID) {
	logInID = newLogInID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 9:46:10 AM)
 * @return java.lang.String
 */
public String toString() 
{
	return getContFirstName() + " " + getContLastName();
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	if( getContactID()!= null 
		 && com.cannontech.database.cache.functions.CustomerFuncs.contactExists(getContactID().intValue()) )
	{
		Object setValues[] = { getContFirstName(), getContLastName(),
						getContPhone1(), getContPhone2(), getLocationID(),
						getLogInID() };

		Object constraintValues[] = { getContactID() };

		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	else
		add();  //didnt find the contact in CACHE, so just add it

}
}
