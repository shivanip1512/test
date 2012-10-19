package com.cannontech.database.data.lite;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.LazyList;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.contact.Contact;
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
	private List<LiteContactNotification> liteContactNotifications = LazyList.ofInstance(LiteContactNotification.class);
	
	public LiteContact() {
	    super();
	    setLiteType(LiteTypes.CONTACT);
	}

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
    public LiteContact( int contID_, String fName_, String lName_, int loginId ) {
        this(contID_);
        setContFirstName( fName_ );
        setContLastName( lName_ );
        setLoginID(loginId);
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
	public String toString() {
	    if( (getContLastName() == null && getContFirstName() == null) || (getContLastName().equalsIgnoreCase("") && getContFirstName().equalsIgnoreCase("")) ) {
	        return CtiUtilities.STRING_NONE + "," + CtiUtilities.STRING_NONE;
	    }
		
	    if( getContLastName() == null ) {
				return getContFirstName();
		} else {
			if( getContFirstName() == null ) {
				return getContLastName();
			}else {
				return getContLastName() + ", " + getContFirstName();
			}
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
        PreparedStatement pstmt = null;
        java.sql.Connection conn = null;
        ResultSet rset = null;
        try {
            conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias());
            
            String sql = "SELECT c.ContFirstName, c.ContLastName, c.LoginID, c.AddressID " + 
                        " FROM " + Contact.TABLE_NAME + " c " +
                        " WHERE c.ContactID = ? ";
            
            pstmt = conn.prepareStatement( sql );
            pstmt.setInt( 1, getContactID());
            rset = pstmt.executeQuery();                

            if (rset.next())
            {
                setContFirstName( rset.getString(1));
                setContLastName( rset.getString(2));
                setLoginID( rset.getInt(3));
                setAddressID( rset.getInt(4));
            }
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            }
            catch (java.sql.SQLException e) {
                com.cannontech.clientutils.CTILogger.error( "Unable to load Contact", e );
            }
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
	 * @return List<LiteContactNotifications>
	 */
    public List<LiteContactNotification> getLiteContactNotifications() {
        return liteContactNotifications;
    }
    public void setNotifications(List<LiteContactNotification> notificationList) {
        liteContactNotifications = notificationList;
    }
	
}
