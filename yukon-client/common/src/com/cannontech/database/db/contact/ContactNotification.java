package com.cannontech.database.db.contact;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.db.notification.NotificationDestination;
import com.cannontech.database.db.point.PointAlarming;

/**
 * This type was created in VisualAge.
 */
public class ContactNotification extends com.cannontech.database.db.DBPersistent 
{
//	public static final int EMAIL_NOTIFYTYPE = 1;
//	public static final int PAGER_NOTIFYTYPE = 2;
//
	public static final int DUMMY_CONTACTID = -99999;
//	public static final String NONE_STRING = "None";
//
//	public static final int NONE_RECIPIENT_ID = 0;
//
//	//possible values for recipientType
//	public static final String TYPE_EMAIL = "EMAIL";
//	public static final String TYPE_PAGER = "PAGER";
	
	private Integer contactNotifID = null;
	private Integer contactID = null;
	private Integer notificationCatID= null;
	private String disableFlag = "N";
	private String notification = CtiUtilities.STRING_NONE;
	
	//temp fix for this DB object being inside a DATA object as a set
	public int opcode = Transaction.UPDATE;




	private final String CONSTRAINT_COLUMNS[] = { "ContactNotifID" };
	
	private final String SELECT_COLUMNS[] = 
	{  
		"ContactID", "NotificationCategoryID", "DisableFlag", "Notification" 
	};

	public static final String TABLE_NAME = "ContactNotification";

	
	/**
	 * ContactNotification constructor comment.
	 */
	public ContactNotification() 
	{
		super();
	}
	/**
	 * ContactNotification constructor comment.
	 */
	public ContactNotification(Integer contNotifID_, Integer contactID_, Integer notifID_,
			String disableFlag_, String notif_ )
	{
		this();
		setContactNotifID( contNotifID_ );
		setContactID( contactID_ );
		setNotificationCatID( notifID_ );
		setDisableFlag( disableFlag_);
		setNotification( notif_ );
	}

	private void deleteDependencies() throws java.sql.SQLException
	{
		// ANY AND ALL REFERENCES TO THIS TABLE SHOULD BE DELETED HERE
		delete( PointAlarming.TABLE_NAME, "RecipientID", getContactNotifID() ); 
		delete( NotificationDestination.TABLE_NAME, "RecipientID", getContactNotifID() );
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/17/00 4:28:38 PM)
	 * @return boolean
	 */
	public boolean equals(Object o)
	{
		if( o == null )
			return false;
		else if( o instanceof ContactNotification )
		{
			return ((ContactNotification)o).getContactNotifID().equals( getContactNotifID() );
		}
		else
			return false;
	}

	/**
	 * keep this consistent with .equals() pleez
	 * o1.equals(o2) => o1.hashCode() == o2.hashCode()
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() 
	{
		if( getContactNotifID() == null )
			return super.hashCode();
		else
			return getContactNotifID().intValue();
	}
	
	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException 
	{
		if( getContactNotifID() == null )
			setContactNotifID( getNextContactNotifID(getDbConnection()) );

		Object setValues[] = 
		{ 
			getContactNotifID(), getContactID(), getNotificationCatID(), 
			getDisableFlag(), getNotification()
		};
	
		add( TABLE_NAME, setValues );
	}
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException 
	{
		deleteDependencies();

		Object constraintValues[] = { getContactNotifID() };
	
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}
	
	/**
	 * This method was created by Cannon Technologies Inc.
	 * @return boolean
	 * @param deviceID java.lang.Integer	
	 */
/*
	public static boolean deleteAllContactNotification(int contactID_, java.sql.Connection conn )
	{
		String sql1 = 
            "DELETE FROM " + TABLE_NAME + 
				" WHERE Contact=" + contactID_;

		String sql2 = 
            "DELETE FROM " + PointAlarming.TABLE_NAME + 
				" WHERE RecipientID=" + contactID_;
	
		String sql3 = 
            "DELETE FROM " + NotificationDestination.TABLE_NAME + 
				" WHERE RecipientID=" + contactID_;
	
		try
		{		
			if( conn == null )
			{
				throw new IllegalStateException("Error getting database connection.");
			}
			else
			{
				conn.createStatement().execute( sql2 );
				conn.createStatement().execute( sql3 );
				conn.createStatement().execute( sql1 );
			}		
		}
		catch( java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			return false;
		}
	
		return true;
	}
*/
	/**
	 * This method was created in VisualAge.
	 * @return com.cannontech.database.db.point.State[]
	 * @param stateGroup java.lang.Integer
	 */
	public static final ContactNotification[] getAllContactNotifications( java.sql.Connection conn ) throws java.sql.SQLException 
	{
		java.util.ArrayList tmpList = new java.util.ArrayList(50);
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
		
		String sql = 
			"SELECT ContactNotifID, ContactID, NotificationCategoryID, " + 
			"DisableFlag, Notification " +
			"FROM " + TABLE_NAME + " ORDER BY Notification";
	
		try
		{		
			if( conn == null )
			{
				throw new IllegalStateException("Error getting database connection.");
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				
				rset = pstmt.executeQuery();							
		
				while( rset.next() )
				{
					tmpList.add( new ContactNotification( 
								new Integer(rset.getInt(1)),
								new Integer(rset.getInt(2)),
								new Integer(rset.getInt(3)),
								rset.getString(4),
								rset.getString(5) ) );
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
	
		ContactNotification retVal[] = new ContactNotification[ tmpList.size() ];
		tmpList.toArray( retVal );
		
		return retVal;
	}


	/**
	 * This method was created in VisualAge.
	 * @return com.cannontech.database.db.point.State[]
	 * @param stateGroup java.lang.Integer
	 */
	public static final ContactNotification[] getContactNotifications( java.sql.Connection conn, int contactID_ ) throws java.sql.SQLException 
	{
		java.util.ArrayList tmpList = new java.util.ArrayList(50);
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
		
		String sql = 
			"SELECT ContactNotifID, ContactID, NotificationCategoryID, " + 
			"DisableFlag, Notification " +
			"FROM " + TABLE_NAME + " " +
			"WHERE ContactID = " + contactID_ + " " + 
			"ORDER BY Notification";
	
		try
		{		
			if( conn == null )
			{
				throw new IllegalStateException("Error getting database connection.");
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				
				rset = pstmt.executeQuery();							
		
				while( rset.next() )
				{
					tmpList.add( new ContactNotification( 
								new Integer(rset.getInt(1)),
								new Integer(rset.getInt(2)),
								new Integer(rset.getInt(3)),
								rset.getString(4),
								rset.getString(5) ) );
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
			} 
			catch( java.sql.SQLException e2 )
			{
				com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
			}	
		}
	
		ContactNotification retVal[] = new ContactNotification[ tmpList.size() ];
		tmpList.toArray( retVal );
		
		return retVal;
	}


	/**
	 * This method was created in VisualAge.
	 * @return com.cannontech.database.db.point.State[]
	 * @param stateGroup java.lang.Integer
	 */
	public static final void deleteAllContactNotifications( java.sql.Connection conn, int contactID_ ) throws java.sql.SQLException 
	{
		String sql1 = 
			"DELETE FROM " + NotificationDestination.TABLE_NAME + " " + 
			"WHERE RecipientID in (select ContactNotifID " +
			"from " + TABLE_NAME + " where ContactID = " + contactID_ + ")"; 


		String sql2 = 
			"DELETE FROM " + TABLE_NAME + " " + 
			"WHERE ContactID = " + contactID_; 
	
		try
		{		
			if( conn == null )
			{
				throw new IllegalStateException("Error getting database connection.");
			}
			else
			{
				conn.prepareStatement(sql1.toString()).executeUpdate();
				
				conn.prepareStatement(sql2.toString()).executeUpdate();
			}		
		}
		catch( java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}	
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return NotificationRecipient
	 * @param Integer
	 */
/*	
	public final static ContactNotification getNotificationRecipient( Integer contactID_ )
	{
		ContactNotification cntNotif = new ContactNotification(); 
		cntNotif.setContactID( contactID_ );
	
		try
		{
			com.cannontech.database.Transaction t =
					com.cannontech.database.Transaction.createTransaction(
							com.cannontech.database.Transaction.RETRIEVE,
							cntNotif);
	
			cntNotif = (ContactNotification)t.execute();
		}
		catch (Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		
		return cntNotif;
	}
*/	
	/**
	 * Insert the method's description here.
	 * Creation date: (11/16/00 2:30:10 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getDisableFlag() {
		return disableFlag;
	}
		
	/**
	 * retrieve method comment.
	 */
	public void retrieve() throws java.sql.SQLException 
	{
		Object constraintValues[] = { getContactNotifID() };
	
		Object results[] = retrieve( SELECT_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SELECT_COLUMNS.length )
		{
			setContactID( (Integer) results[0] );
			setNotificationCatID( (Integer) results[1] );
			setDisableFlag( (String) results[2] );
			setNotification( (String) results[3] );
		}
		else
			throw new Error( getClass() + "::retrieve - Incorrect number of results" );
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/16/00 2:30:10 PM)
	 * @param newDisableFlag java.lang.String
	 */
	public void setDisableFlag(java.lang.String newDisableFlag) {
		disableFlag = newDisableFlag;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public String toString() 
	{
		return getNotification();
	}
	/**
	 * update method comment.
	 */
	public void update() throws java.sql.SQLException 
	{
		Object constraintValues[] = { getContactNotifID() };
		
		Object setValues[] = 
		{ 
			getContactID(),
			getNotificationCatID(), 
			getDisableFlag(), 
			getNotification()
		};
		
		update( TABLE_NAME, SELECT_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/99 10:31:33 AM)
	 * @return java.lang.Integer
	 */
	public static synchronized Integer getNextContactNotifID( java.sql.Connection conn )
	{
		if( conn == null )
			throw new IllegalStateException("Database connection should not be null.");
	
		
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		
		try 
		{		
		    stmt = conn.createStatement();
			 rset = stmt.executeQuery( "SELECT Max(ContactNotifID)+1 FROM " + TABLE_NAME );	
				
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
		return new Integer(CtiUtilities.NONE_ID);
	}
	
	/**
	 * Returns the contactID.
	 * @return Integer
	 */
	public Integer getContactID() {
		return contactID;
	}

	/**
	 * Returns the notification.
	 * @return String
	 */
	public String getNotification() {
		return notification;
	}

	/**
	 * Returns the notificationCatID.
	 * @return Integer
	 */
	public Integer getNotificationCatID() {
		return notificationCatID;
	}

	/**
	 * Sets the contactID.
	 * @param contactID The contactID to set
	 */
	public void setContactID(Integer contactID) {
		this.contactID = contactID;
	}

	/**
	 * Sets the notification.
	 * @param notification The notification to set
	 */
	public void setNotification(String notification) {
		this.notification = notification;
	}

	/**
	 * Sets the notificationCatID.
	 * @param notificationCatID The notificationCatID to set
	 */
	public void setNotificationCatID(Integer notificationCatID) {
		this.notificationCatID = notificationCatID;
	}

	/**
	 * Returns the contactNotifID.
	 * @return Integer
	 */
	public Integer getContactNotifID() {
		return contactNotifID;
	}
	
	/**
	 * Sets the contactNotifID.
	 * @param contactNotifID The contactNotifID to set
	 */
	public void setContactNotifID(Integer contactNotifID) {
		this.contactNotifID = contactNotifID;
	}

}
