package com.cannontech.database.data.notification;

import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.notification.AlarmCategory;
import com.cannontech.database.db.point.PointAlarming;

/**
 * This type was created in VisualAge.
 */

public class NotificationGroup extends DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private com.cannontech.database.db.notification.NotificationGroup notificationGroup = null;
	
	private NotifDestinationMap[] notifDestinationMap = new NotifDestinationMap[0];
	private ContactNotifGroupMap[] contactMap = new ContactNotifGroupMap[0];
	private CustomerNotifGroupMap[] customerMap = new CustomerNotifGroupMap[0];


	public static final String SQL_NOTIFDEST_NOTIFGRP = 
		"SELECT RecipientID, Attribs " +
		"FROM " + NotifDestinationMap.TABLE_NAME +
		" WHERE NotificationGroupID = ?";

	public static final String SQL_CONTACT_NOTIFGRP =
		"SELECT ContactID, Attribs " +
		"FROM " + ContactNotifGroupMap.TABLE_NAME +
		" WHERE NotificationGroupID = ?";

	public static final String SQL_CUSTOMER_NOTIFGRP = 
		"SELECT CustomerID, Attribs " +
		"FROM " + CustomerNotifGroupMap.TABLE_NAME +
		" WHERE NotificationGroupID = ?";


	private static final int NOTFIF_DEST_MAP = 0;
	private static final int CONTACT_MAP = 1;
	private static final int CUSTOMER_MAP = 2;

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

	for( int i = 0; i < getNotifDestinationMap().length; i++ )
	{
		Object addValues[] = { 	
			getNotificationGroup().getNotificationGroupID(),
			new Integer(getNotifDestinationMap()[i].getRecipientID()),
			getNotifDestinationMap()[i].getAttribs()
		};
		add(NotifDestinationMap.TABLE_NAME, addValues);
	}
			
			
	for( int i = 0; i < getContactMap().length; i++ )
	{
		Object addValues[] = { 	
			new Integer(getContactMap()[i].getContactID()),
			getNotificationGroup().getNotificationGroupID(),
			getContactMap()[i].getAttribs()
		};
		add(ContactNotifGroupMap.TABLE_NAME, addValues);
	}

	for( int i = 0; i < getCustomerMap().length; i++ )
	{
		Object addValues[] = { 	
			new Integer(getCustomerMap()[i].getCustomerID()),
			getNotificationGroup().getNotificationGroupID(),
			getCustomerMap()[i].getAttribs()
		};
		add(CustomerNotifGroupMap.TABLE_NAME, addValues);
	}

}

/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void delete() throws java.sql.SQLException 
{
	delete(NotifDestinationMap.TABLE_NAME, "NotificationGroupID", getNotificationGroup().getNotificationGroupID());
	delete(ContactNotifGroupMap.TABLE_NAME, "NotificationGroupID", getNotificationGroup().getNotificationGroupID());
	delete(CustomerNotifGroupMap.TABLE_NAME, "NotificationGroupID", getNotificationGroup().getNotificationGroupID());
	
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
//public java.util.Vector getDestinationVector() {
//	if( destinationVector == null )
//		destinationVector = new java.util.Vector();
//		
//	return destinationVector;
//}
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
			"SELECT NotificationGroupID FROM " + AlarmCategory.TABLE_NAME + " WHERE NotificationGroupID=" + groupID,
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
			"SELECT NotificationGroupID FROM " + PointAlarming.TABLE_NAME + " WHERE NotificationGroupID=" + groupID,
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
public static final NotifDestinationMap[] getAllNotifGroupDestinations(Integer notifGroupID, java.sql.Connection conn ) throws java.sql.SQLException
{
	Vector map = getMappings(
			SQL_NOTIFDEST_NOTIFGRP, notifGroupID, NOTFIF_DEST_MAP, conn );

	return (NotifDestinationMap[])
			map.toArray( new NotifDestinationMap[map.size()] );
}

/**
 * Returns the conact IDs that are inside the NotifcationGroup
 * identified by the given ID
 * 
 */
public static final ContactNotifGroupMap[] getAllNotifGroupContacts(Integer notifGroupID, java.sql.Connection conn ) throws java.sql.SQLException
{
	Vector map = getMappings(
			SQL_CONTACT_NOTIFGRP, notifGroupID, CONTACT_MAP, conn );

	return (ContactNotifGroupMap[])
			map.toArray( new ContactNotifGroupMap[map.size()] );
}

/**
 * Returns the Customer IDs that are inside the NotifcationGroup
 * identified by the given ID
 * 
 */
public static final CustomerNotifGroupMap[] getAllNotifGroupCustomers(Integer notifGroupID, java.sql.Connection conn ) throws java.sql.SQLException
{
	Vector map = getMappings(
			SQL_CUSTOMER_NOTIFGRP, notifGroupID, CUSTOMER_MAP, conn );

	return (CustomerNotifGroupMap[])
			map.toArray( new CustomerNotifGroupMap[map.size()] );
}

/**
 * Generic call to get an int[] of ids from the given query
 * identified by the given ID
 * 
 */
protected static final Vector getMappings(String sql, Integer notifGroupID, int type, java.sql.Connection conn ) throws java.sql.SQLException
{
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	Vector objVect = new Vector(16);

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
				if( type == NOTFIF_DEST_MAP )
					objVect.add(
						new NotifDestinationMap(
							rset.getInt(1),
							rset.getString(2)) );					
				else if( type == CONTACT_MAP )
					objVect.add(
						new ContactNotifGroupMap(
							rset.getInt(1),
							rset.getString(2)) );
				else if( type == CUSTOMER_MAP )
					objVect.add(
						new CustomerNotifGroupMap(
							rset.getInt(1),
							rset.getString(2)) );
								
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
	
	objVect.trimToSize();
	return objVect;
}


/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void retrieve() throws java.sql.SQLException {

	getNotificationGroup().retrieve();

	setNotifDestinationMap(
		com.cannontech.database.data.notification.NotificationGroup.getAllNotifGroupDestinations(
			getNotificationGroup().getNotificationGroupID(), getDbConnection()) );

	setContactMap(
		com.cannontech.database.data.notification.NotificationGroup.getAllNotifGroupContacts(
			getNotificationGroup().getNotificationGroupID(), getDbConnection()) );

	setCustomerMap(
		com.cannontech.database.data.notification.NotificationGroup.getAllNotifGroupCustomers(
		getNotificationGroup().getNotificationGroupID(), getDbConnection()) );

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
	
//	java.util.Vector v = getDestinationVector();
//	if( v != null )
//	{
//		for( int i = 0; i < v.size(); i++ )
//			((DBPersistent) v.elementAt(i)).setDbConnection(conn);
//	}

}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.Device
 */
//public void setDestinationVector(java.util.Vector newValue) {
//	this.destinationVector = newValue;
//}
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

	delete(NotifDestinationMap.TABLE_NAME, "NotificationGroupID", getNotificationGroup().getNotificationGroupID());
	delete(ContactNotifGroupMap.TABLE_NAME, "NotificationGroupID", getNotificationGroup().getNotificationGroupID());
	delete(CustomerNotifGroupMap.TABLE_NAME, "NotificationGroupID", getNotificationGroup().getNotificationGroupID());

	for( int i = 0; i < getNotifDestinationMap().length; i++ )
	{
		Object addValues[] = { 	
			getNotificationGroup().getNotificationGroupID(),
			new Integer(getNotifDestinationMap()[i].getRecipientID()),
			getNotifDestinationMap()[i].getAttribs()
		};
		add(NotifDestinationMap.TABLE_NAME, addValues);
	}
	
	for( int i = 0; i < getContactMap().length; i++ )
	{
		Object addValues[] = { 	
			new Integer(getContactMap()[i].getContactID()),
			getNotificationGroup().getNotificationGroupID(),
			getContactMap()[i].getAttribs()
		};
		add(ContactNotifGroupMap.TABLE_NAME, addValues);
	}

	for( int i = 0; i < getCustomerMap().length; i++ )
	{
		Object addValues[] = { 	
			new Integer(getCustomerMap()[i].getCustomerID()),
			getNotificationGroup().getNotificationGroupID(),
			getCustomerMap()[i].getAttribs()
		};
		add(CustomerNotifGroupMap.TABLE_NAME, addValues);
	}


}


	/**
	 * @return
	 */
	public ContactNotifGroupMap[] getContactMap()
	{
		return contactMap;
	}

	/**
	 * @return
	 */
	public CustomerNotifGroupMap[] getCustomerMap()
	{
		return customerMap;
	}

	/**
	 * @param maps
	 */
	public void setContactMap(ContactNotifGroupMap[] maps)
	{
		contactMap = maps;
	}

	/**
	 * @param maps
	 */
	public void setCustomerMap(CustomerNotifGroupMap[] maps)
	{
		customerMap = maps;
	}

	/**
	 * @return
	 */
	public NotifDestinationMap[] getNotifDestinationMap()
	{
		return notifDestinationMap;
	}

	/**
	 * @param maps
	 */
	public void setNotifDestinationMap(NotifDestinationMap[] maps)
	{
		notifDestinationMap = maps;
	}

}
