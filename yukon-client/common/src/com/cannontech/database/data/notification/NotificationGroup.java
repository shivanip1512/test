package com.cannontech.database.data.notification;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;

/**
 * This type was created in VisualAge.
 */

public class NotificationGroup extends DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private com.cannontech.database.db.notification.NotificationGroup notificationGroup = null;
	private java.util.Vector destinationVector = null;

	//contains ints of ContactIDs
	private int[] contactIDs = new int[0];

	//contains ints of CustomerIDs
	private int[] customerIDs = new int[0];

	public static final String SQL_CUSTOMER_NOTIFGRP = 
		"SELECT CustomerID " +
		"FROM CustomerNotifGroupMap " +
		"WHERE NotificationGroupID = ?";

	public static final String SQL_CONTACT_NOTIFGRP =
		"SELECT ContactID " +
		"FROM ContactNotifGroupMap " +
		"WHERE NotificationGroupID = ?";


/**
 * StatusPoint constructor comment.
 */
public NotificationGroup() {
	super();
}
/**
 * StatusPoint constructor comment.
 */
public NotificationGroup(Integer notificationGroupID) {
	super();
	getNotificationGroup().setNotificationGroupID(notificationGroupID);
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException
{
	getNotificationGroup().add();

	if( getDestinationVector() != null )
		for( int i = 0; i < getDestinationVector().size(); i++ )
			((DBPersistent) getDestinationVector().elementAt(i)).add();
			
			
	for( int i = 0; i < getContactIDs().length; i++ )
	{
		Object addValues[] = 
		{ 	
			new Integer(getContactIDs()[i]),
			getNotificationGroup().getNotificationGroupID()
		};
	
		//just add the bridge value to the mapping table
		// showing that this contact belongs to the current NotificationGroup 
		add("ContactNotifGroupMap", addValues);
	}

	for( int i = 0; i < getCustomerIDs().length; i++ )
	{
		Object addValues[] = 
		{ 	
			new Integer(getCustomerIDs()[i]),
			getNotificationGroup().getNotificationGroupID()
		};
	
		//just add the bridge value to the mapping table
		// showing that this contact belongs to the current NotificationGroup 
		add("CustomerNotifGroupMap", addValues);
	}
}

/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void delete() throws java.sql.SQLException 
{
	delete("ContactNotifGroupMap", "NotificationGroupID", getNotificationGroup().getNotificationGroupID());
	delete("CustomerNotifGroupMap", "NotificationGroupID", getNotificationGroup().getNotificationGroupID());
	
	// remove all the destantions for this group
	com.cannontech.database.db.notification.NotificationDestination.deleteAllDestinations( 
            getNotificationGroup().getNotificationGroupID(), getDbConnection() );
	
	getNotificationGroup().delete();
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
					getNotificationGroup().getNotificationGroupID().intValue(),
					com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_NOTIFICATION_GROUP_DB,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_NOTIFCATIONGROUP,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_NOTIFCATIONGROUP,
					typeOfChange)
	};


	return msgs;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.PointControl
 */
public java.util.Vector getDestinationVector() {
	if( destinationVector == null )
		destinationVector = new java.util.Vector();
		
	return destinationVector;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.PointControl
 */
public com.cannontech.database.db.notification.NotificationGroup getNotificationGroup() 
{
	if( notificationGroup == null )
		notificationGroup = new com.cannontech.database.db.notification.NotificationGroup();
		
	return notificationGroup;
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasAlarmCategory(Integer pointID) throws java.sql.SQLException 
{	
	return hasAlarmCategory(pointID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasAlarmCategory(Integer groupID, String databaseAlias) throws java.sql.SQLException 
{
	SqlStatement stmt =
		new SqlStatement(
			"SELECT NotificationGroupID FROM " + com.cannontech.database.db.notification.AlarmCategory.TABLE_NAME + " WHERE NotificationGroupID=" + groupID,
			databaseAlias );

	try
	{
		stmt.execute();
		return (stmt.getRowCount() > 0 );
	}
	catch( Exception e )
	{ 
		return false;
	}
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasPointAlarming(Integer recipientID) throws java.sql.SQLException 
{	
	return hasPointAlarming(recipientID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasPointAlarming(Integer groupID, String databaseAlias) throws java.sql.SQLException 
{
	SqlStatement stmt =
		new SqlStatement(
			"SELECT NotificationGroupID FROM " + com.cannontech.database.db.point.PointAlarming.TABLE_NAME + " WHERE NotificationGroupID=" + groupID,
			databaseAlias );

	try
	{
		stmt.execute();
		return (stmt.getRowCount() > 0 );
	}
	catch( Exception e )
	{
		return false;
	}
}

/**
 * Returns the Customer IDs that are inside the NotifcationGroup
 * identified by the given ID
 * 
 */
public static final int[] getAllNotifGroupCustomerIDs(Integer notifGroupID, java.sql.Connection conn ) throws java.sql.SQLException
{
	return getIDs( SQL_CUSTOMER_NOTIFGRP, notifGroupID, conn );
}

/**
 * Returns the conact IDs that are inside the NotifcationGroup
 * identified by the given ID
 * 
 */
public static final int[] getAllNotifGroupContactIDs(Integer notifGroupID, java.sql.Connection conn ) throws java.sql.SQLException
{
	return getIDs( SQL_CONTACT_NOTIFGRP, notifGroupID, conn );
}

/**
 * Generic call to get an int[] of ids from the given query
 * identified by the given ID
 * 
 */
protected static final int[] getIDs(String sql, Integer notifGroupID, java.sql.Connection conn ) throws java.sql.SQLException
{
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	NativeIntVector intVect = new NativeIntVector(16);

	try
	{
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be null.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, notifGroupID.intValue() );
				
			rset = pstmt.executeQuery();							
		
			while( rset.next() )
			{
				intVect.add( rset.getInt(1) );
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
	
	return intVect.toArray();
}


/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void retrieve() throws java.sql.SQLException {

	getNotificationGroup().retrieve();
	destinationVector = new java.util.Vector();
	
	try
	{
		com.cannontech.database.db.notification.NotificationDestination rArray[] = com.cannontech.database.db.notification.NotificationDestination.getNotificationDestinations( getNotificationGroup().getNotificationGroupID() );

		for( int i = 0; i < rArray.length; i++ )
		{
			rArray[i].setDbConnection(getDbConnection());
			destinationVector.addElement( rArray[i] );
		}
	}
	catch(java.sql.SQLException e )
	{
		//not necessarily an error
	}

	setContactIDs(
		getAllNotifGroupContactIDs(
			getNotificationGroup().getNotificationGroupID(),
			getDbConnection()) );

	setCustomerIDs(
		getAllNotifGroupCustomerIDs(
			getNotificationGroup().getNotificationGroupID(),
			getDbConnection()) );

}


/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getNotificationGroup().setDbConnection(conn);
	
	java.util.Vector v = getDestinationVector();

	if( v != null )
	{
		for( int i = 0; i < v.size(); i++ )
			((DBPersistent) v.elementAt(i)).setDbConnection(conn);
	}
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.Device
 */
public void setDestinationVector(java.util.Vector newValue) {
	this.destinationVector = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.Device
 */
public void setNotificationGroup(com.cannontech.database.db.notification.NotificationGroup newValue) {
	this.notificationGroup = newValue;
}
/**
 * This method was created in VisualAge.
 */
public void setNotificatoinGroupID(Integer notificatoinGroupID) {
	getNotificationGroup().setNotificationGroupID(notificatoinGroupID);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() 
{
	return getNotificationGroup().getGroupName();
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{	
	getNotificationGroup().update();
	com.cannontech.database.db.notification.NotificationDestination.deleteAllDestinations( 
         getNotificationGroup().getNotificationGroupID(),
         getDbConnection() );
	
	if( getDestinationVector() != null )
		for( int i = 0; i < getDestinationVector().size(); i++ )
			((DBPersistent) getDestinationVector().elementAt(i)).add();

	delete("ContactNotifGroupMap", "NotificationGroupID", getNotificationGroup().getNotificationGroupID());
	delete("CustomerNotifGroupMap", "NotificationGroupID", getNotificationGroup().getNotificationGroupID());

	for( int i = 0; i < getContactIDs().length; i++ )
	{
		Object addValues[] = { 	
			new Integer(getContactIDs()[i]),
			getNotificationGroup().getNotificationGroupID()
		};
		add("ContactNotifGroupMap", addValues);
	}

	for( int i = 0; i < getCustomerIDs().length; i++ )
	{
		Object addValues[] = { 	
			new Integer(getCustomerIDs()[i]),
			getNotificationGroup().getNotificationGroupID()
		};
		add("CustomerNotifGroupMap", addValues);
	}

}

	/**
	 * @return
	 */
	public int[] getContactIDs()
	{
		return contactIDs;
	}

	/**
	 * @return
	 */
	public int[] getCustomerIDs()
	{
		return customerIDs;
	}

	/**
	 * @param is
	 */
	public void setContactIDs(int[] is)
	{
		contactIDs = is;
	}

	/**
	 * @param is
	 */
	public void setCustomerIDs(int[] is)
	{
		customerIDs = is;
	}

}
