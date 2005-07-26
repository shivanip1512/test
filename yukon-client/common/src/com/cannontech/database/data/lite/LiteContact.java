package com.cannontech.database.data.lite;

import java.util.Vector;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.user.UserUtils;

/*
 */
public class LiteContact extends LiteBase
{
	private String contFirstName = null;
	private String contLastName = null;
	private int loginID = UserUtils.USER_YUKON_ID;
	private int addressID = CtiUtilities.NONE_ZERO_ID;

	public static final LiteContact NONE_LITE_CONTACT =
			new LiteContact( CtiUtilities.NONE_ZERO_ID, 
					null, CtiUtilities.STRING_NONE );

	//contains instances of com.cannontech.database.data.lite.LiteContactNotification
	private Vector liteContactNotifications = null;

	/**
	 * LiteContact
	 */
	public LiteContact( int contID_ ) {
		super();
		setContactID(contID_);
		setLiteType(LiteTypes.CONTACT);
	}

	/**
	 * LiteContact
	 */
	public LiteContact( int contID_, String fName_, String lName_ ) {
		this(contID_);
		setContFirstName( fName_ );
		setContLastName( lName_ );
	}

	/**
	 * LiteContact
	 */
	public LiteContact( int contID_, String contactFirstName_, 
			String contactLastName_, int loginID_, int addressID_ ) 
	{
		this( contID_ );
		setContFirstName( contactFirstName_ );
		setContLastName( contactLastName_ );
		setLoginID( loginID_ );
		setAddressID( addressID_ );
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2001 9:29:21 AM)
	 * @return int
	 */
	public int getContactID() {
		return getLiteID();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2001 9:29:21 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getContFirstName() {
		return contFirstName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2001 9:29:21 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getContLastName() {
		return contLastName;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2001 9:29:21 AM)
	 * @param newContactID int
	 */
	public void setContactID(int newContactID) 
	{
		setLiteID(newContactID);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2001 9:29:21 AM)
	 * @param newContFirstName java.lang.String
	 */
	public void setContFirstName(java.lang.String newContFirstName) {
		contFirstName = newContFirstName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2001 9:29:21 AM)
	 * @param newContLastName java.lang.String
	 */
	public void setContLastName(java.lang.String newContLastName) {
		contLastName = newContLastName;
	}
	
	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	public String toString() 
	{
		if( getContLastName() == null )
		{
			if( getContFirstName() == null )
				return super.toString();
			else
				return getContFirstName();
		}		
		else
		{
			if( getContFirstName() == null )
				return getContLastName();
			else
				return getContLastName() + ", " + getContFirstName();
		}

	}

	/**
	 * Returns the userID.
	 * @return int
	 */
	public int getLoginID() {
		return loginID;
	}

	/**
	 * Sets the userID.
	 * @param userID The userID to set
	 */
	public void setLoginID(int loginID_) {
		this.loginID = loginID_;
	}

	/**
	 * retrieve method comment.
	 */
	public void retrieve(String databaseAlias) 
	{
	 	com.cannontech.database.SqlStatement stmt =
	 		new com.cannontech.database.SqlStatement(
				"SELECT c.ContFirstName, c.ContLastName, c.LoginID, c.AddressID " + 
		 		"FROM " + Contact.TABLE_NAME + " c " +
		 		" WHERE c.ContactID = " + getContactID(),
		 		databaseAlias);
	
	 	try
	 	{
	 		stmt.execute();

			setContFirstName( (String) stmt.getRow(0)[0] );
			setContLastName( (String) stmt.getRow(0)[1] );
			setLoginID( ((java.math.BigDecimal) stmt.getRow(0)[2]).intValue() );
			setAddressID( ((java.math.BigDecimal) stmt.getRow(0)[3]).intValue() );
	 

			//load up our ContactNotification objects			
		 	stmt =
		 		new com.cannontech.database.SqlStatement(
					"SELECT cn.ContactNotifID, cn.ContactID, cn.NotificationCategoryID, " + 
					"cn.DisableFlag, cn.Notification " + 
					"FROM " + ContactNotification.TABLE_NAME + " cn " +
					"where cn.ContactID = " + getContactID() + " " +
					"order by cn.Ordering, cn.Notification",
					databaseAlias );
			
			//refresh our notification list
			getLiteContactNotifications().removeAllElements();
			
			stmt.execute();
            
            getLiteContactNotifications().ensureCapacity(stmt.getRowCount());

			for( int i = 0; i < stmt.getRowCount(); i++ )
			{
				LiteContactNotification ln = new LiteContactNotification(
					((java.math.BigDecimal) stmt.getRow(i)[0]).intValue(),
					((java.math.BigDecimal) stmt.getRow(i)[1]).intValue(),
					((java.math.BigDecimal) stmt.getRow(i)[2]).intValue(),
					(String) stmt.getRow(i)[3],
					(String) stmt.getRow(i)[4] );

				getLiteContactNotifications().add( ln );
			}
	 
	 	}
	 	catch( Exception e )
	 	{
	 		com.cannontech.clientutils.CTILogger.error( "Unable to load Contact", e );
	 	}
	}

	/**
	 * Returns the addressID.
	 * @return int
	 */
	public int getAddressID() {
		return addressID;
	}

	/**
	 * Sets the addressID.
	 * @param addressID The addressID to set
	 */
	public void setAddressID(int addressID) {
		this.addressID = addressID;
	}

	/**
	 * Returns the liteContactNotifications.
	 * @return Vector
	 */
	public Vector getLiteContactNotifications() 
	{
		if( liteContactNotifications == null )
			liteContactNotifications = new Vector(4);

		return liteContactNotifications;
	}

	public String getNotifactionsStrings()
	{		
		StringBuffer ret = new StringBuffer("");
		
		for( int i = 0; i < getLiteContactNotifications().size(); i++ )
		{
			LiteContactNotification notif = 
					(LiteContactNotification)getLiteContactNotifications().get(i);
					
			ret.append( notif.getNotification() + "," );;
		}

		if( ret.length() > 0 )
			ret.deleteCharAt( ret.length() - 1 ); //remove that extra ,
		
		return ret.toString();
	}
	
}
