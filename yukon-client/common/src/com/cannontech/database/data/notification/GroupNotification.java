package com.cannontech.database.data.notification;

/**
 * This type was created in VisualAge.
 */

public class GroupNotification extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private com.cannontech.database.db.notification.NotificationGroup notificationGroup = null;
	private java.util.Vector destinationVector = null;
/**
 * StatusPoint constructor comment.
 */
public GroupNotification() {
	super();
}
/**
 * StatusPoint constructor comment.
 */
public GroupNotification(Integer notificationGroupID) {
	super();
	getNotificationGroup().setNotificationGroupID(notificationGroupID);
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException {
	
	getNotificationGroup().add();

	if( getDestinationVector() != null )
		for( int i = 0; i < getDestinationVector().size(); i++ )
			((com.cannontech.database.db.DBPersistent) getDestinationVector().elementAt(i)).add();
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void delete() throws java.sql.SQLException 
{
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
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT NotificationGroupID FROM " + com.cannontech.database.db.notification.AlarmCategory.TABLE_NAME + " WHERE NotificationGroupID=" + groupID,
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
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT NotificationGroupID FROM " + com.cannontech.database.db.point.PointAlarming.TABLE_NAME + " WHERE NotificationGroupID=" + groupID,
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

	//for( int i = 0; i < getDestinationVector().size(); i++ )
		//((com.cannontech.database.db.DBPersistent) getDestinationVector().elementAt(i)).retrieve();

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
			((com.cannontech.database.db.DBPersistent) v.elementAt(i)).setDbConnection(conn);
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
			((com.cannontech.database.db.DBPersistent) getDestinationVector().elementAt(i)).add();
}
}
