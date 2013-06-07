package com.cannontech.database.db.contact;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.customer.Customer;
import com.cannontech.database.db.user.YukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dispatch.DbChangeType;
import com.cannontech.messaging.message.dispatch.DBChangeMessage;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.UserUtils;

/**
 * This type was created in VisualAge.
 */

public class Contact extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private Integer contactID = null;
	private String contFirstName = CtiUtilities.STRING_NONE;
	private String contLastName = CtiUtilities.STRING_NONE;
	private Integer logInID = new Integer(UserUtils.USER_DEFAULT_ID);
	private Integer addressID = new Integer(CtiUtilities.NONE_ZERO_ID);
	
	private static NextValueHelper nextValueHelper = YukonSpringHook.getNextValueHelper();

	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"ContFirstName", "ContLastName", 
		"LoginID", "AddressID"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "ContactID" };

	public static final String TABLE_NAME = "Contact";

//	private static final String GET_CUSTOMER_CONTACT_SQL =
//		"SELECT ContactID,ContFirstName,ContLastName,LoginID,AddressID " + 
//		"FROM " + TABLE_NAME + " WHERE LoginID=?";
	

	/**
	 * Contact constructor comment.
	 */
	public Contact() {
		super();
	}
	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException 
	{
		if (getContactID() == null)
			setContactID( getNextContactID() );
			
		Object addValues[] = 
		{ 
			getContactID(), getContFirstName(), getContLastName(),
			getLogInID(), getAddressID()
		};
	
		add( TABLE_NAME, addValues );
	}
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException 
	{
		//just delete the bridge value to the mapping table
		delete("CustomerAdditionalContact", "ContactID", getContactID());
	
		// delete the actual contact
		Integer values[] = { getContactID() };
	
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
	}
	/**
	 * This method was created by Cannon Technologies Inc.
	 * @return boolean
	 * @param deviceID java.lang.Integer
	 */
	public static boolean deleteAllAdditionalContacts(Integer customerID, java.sql.Connection conn )
	{
		try
		{
			if( conn == null )
				throw new IllegalStateException("Database connection should not be null.");
	
			// get all the soon to be deleted contacts
			//Contact[] contacts = getAllCustomerContacts(customerID, conn );
			//delete the references between the contacts and customer
			java.sql.Statement stat = conn.createStatement();	
			stat.execute(
				"DELETE FROM CustomerAdditionalContact WHERE CustomerID=" + customerID );
	
			if( stat != null )
				stat.close();
				
/*
			java.sql.Statement stmt = null;
	
			if( contacts.length > 0 )
			{
				// now delete the actual contacts	
				StringBuffer contactString = new StringBuffer("DELETE FROM " + TABLE_NAME + " WHERE contactID=");

				//load all the contactIDs that need to be deleted by the sql statement
				for( int i = 0; i < contacts.length; i++ ) 
				{
					contactString.append( contacts[i].getContactID() );
	
					if( i < (contacts.length-1) ) //are we at the end yet?
					{
						contactString.append(" OR contactID=" );
					}
					
				}
	
				//delete the contacts
	CTILogger.info( "		CNTACTDELETESTRING = " + contactString.toString() );
				stmt = conn.createStatement();
				stmt.execute( contactString.toString() );
				
				if( stmt != null )
					stmt.close();
			}
*/
		}
		catch(Exception e)
		{
			CTILogger.error( e.getMessage(), e );
			return false;
		}
	
	
		return true;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return LMControlAreaTrigger[]
	 * @param stateGroup java.lang.Integer
	 */
	public static final Contact[] getAllCustomerContacts(Integer customerID, java.sql.Connection conn ) throws java.sql.SQLException
	{
		List<Contact> tmpList = new ArrayList<Contact>(30);
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		String sql = 
			"SELECT cnt.ContactID, cnt.ContFirstName, cnt.ContLastName, " +
			"cnt.LogInID, cnt.AddressID " + 
			"FROM " + TABLE_NAME + " cnt, CustomerAdditionalContact list " +
			"WHERE list.CustomerID= ? and list.CONTACTID=cnt.CONTACTID " +
			"order by list.ordering";
	
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
					Contact item = new Contact();
	
					item.setDbConnection(conn);
					item.setContactID( new Integer(rset.getInt("ContactID")) );
					item.setContFirstName( rset.getString("ContFirstName") );
					item.setContLastName( rset.getString("ContLastName") );
					item.setLogInID( new Integer(rset.getInt("LogInID")) );
					item.setAddressID( new Integer(rset.getInt("AddressID")) );
	
					tmpList.add( item );
				}
						
			}		
		}
		catch( java.sql.SQLException e )
		{
			CTILogger.error( e.getMessage(), e );
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
				CTILogger.error( e2.getMessage(), e2 );//something is up
			}	
		}
	
	
		Contact retVal[] = new Contact[ tmpList.size() ];
		tmpList.toArray( retVal );
		
		return retVal;
	}
	
	/**
	 * Returns an array of Contacts IDs that are not assigned to a customer
	 * 
	 */
	public static final int[] getOrphanedContacts() throws java.sql.SQLException
	{
		NativeIntVector idVect = new NativeIntVector(32);
		java.sql.Connection conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		String sql = "select cnt.contactid from " + TABLE_NAME
                + " cnt where not exists (select 1 from CustomerAdditionalContact ca where "
                + "ca.contactid = cnt.contactid) and not exists (select 1 from "
                + Customer.TABLE_NAME
                + " cs where cs.primarycontactid = cnt.contactid) order by cnt.contfirstname, "
                + "cnt.contlastname";

		try
		{		
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
	
			while( rset.next() )
			{
				idVect.add( rset.getInt(1) );
			}
		}
		catch( java.sql.SQLException e )
		{
			CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			try
			{
				if( rset != null ) rset.close();
				if( pstmt != null ) pstmt.close();
				if( conn != null ) conn.close();
			} 
			catch( java.sql.SQLException e2 )
			{
				CTILogger.error( e2.getMessage(), e2 );//something is up
			}	
		}
	
	
		return idVect.toArray();
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
	 * Creation date: (12/19/2001 1:45:25 PM)
	 * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
	 */
	public DBChangeMessage[] getDBChangeMsgs(DbChangeType dbChangeType) {

	    DBChangeMessage[] msgs = {
	            new DBChangeMessage(
	                            getContactID().intValue(),
	                            DBChangeMessage.CHANGE_CONTACT_DB,
	                            DBChangeMessage.CAT_CUSTOMERCONTACT,
	                            DBChangeMessage.CAT_CUSTOMERCONTACT,
	                            dbChangeType)
		};	
	
		return msgs;
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
	 * Method to get the next available contact id
	 * @return Next contact id
	 */
    public static final Integer getNextContactID() {
        int contactId = nextValueHelper.getNextValue(TABLE_NAME);
        return contactId;
    }	

	/**
	 * Returns YukonUsers IDs that are not used by a contact. Also the
	 * NONE userid is always returned along with the given ignoreID.
	 * 
	 */
	public final static int[] findAvailableUserIDs( int ignoreID ) 
	{		
		String sql =
			"select userid from " + YukonUser.TABLE_NAME + " where userid not in" +
			"(select loginid from " + Contact.TABLE_NAME +
			" where loginid != " + UserUtils.USER_DEFAULT_ID + 
			" and loginid != " + ignoreID + ")";
	
		NativeIntVector intVect = new NativeIntVector(32);

		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		
		try 
		{
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			stmt = conn.createStatement();
			rset = stmt.executeQuery( sql );	
				
			while( rset.next() )
				intVect.add( rset.getInt(1) );
		}
		catch( Exception e ) 
		{
			CTILogger.error( "A db error occurred", e );
		}
		finally 
		{
			SqlUtils.close(rset, stmt, conn );
		}	

		return intVect.toArray();
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
			setLogInID( (Integer) results[2] );
			setAddressID( (Integer) results[3] );
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
		Object setValues[] =
		{ 
			getContFirstName(), getContLastName(),
			getLogInID(), getAddressID()
		};

		Object constraintValues[] = { getContactID() };

		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );

	}

	/**
	 * Returns the addressID.
	 * @return Integer
	 */
	public Integer getAddressID() {
		return addressID;
	}

	/**
	 * Sets the addressID.
	 * @param addressID The addressID to set
	 */
	public void setAddressID(Integer addressID) {
		this.addressID = addressID;
	}

}
