package com.cannontech.database.data.notification;

/**
 * This type was created in VisualAge.
 */

public class NotificationRecipient extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private com.cannontech.database.db.notification.NotificationRecipient notificationRecipient = null;
/**
 * StatusPoint constructor comment.
 */
public NotificationRecipient() {
	super();
}
/**
 * StatusPoint constructor comment.
 */
public NotificationRecipient(Integer recID)
{
	super();
	getNotificationRecipient().setRecipientID(recID);
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{	
	getNotificationRecipient().add();
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void delete() throws java.sql.SQLException 
{
	getNotificationRecipient().delete();
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
					getNotificationRecipient().getRecipientID().intValue(),
					com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_NOTIFICATION_RECIPIENT_DB,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_NOTIFICATIONRECIPIENT,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_NOTIFICATIONRECIPIENT,
					typeOfChange)
	};


	return msgs;
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/00 10:55:20 AM)
 * @return com.cannontech.database.db.notification.NotificationRecipient
 */
public com.cannontech.database.db.notification.NotificationRecipient getNotificationRecipient() 
{
	if( notificationRecipient == null )
		notificationRecipient = new com.cannontech.database.db.notification.NotificationRecipient();
		
	return notificationRecipient;
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
		new com.cannontech.database.SqlStatement("SELECT RecipientID FROM " + com.cannontech.database.db.point.PointAlarming.TABLE_NAME + " WHERE RecipientID=" + groupID,
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
public void retrieve() throws java.sql.SQLException 
{
	getNotificationRecipient().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getNotificationRecipient().setDbConnection(conn);
	
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/00 10:55:20 AM)
 * @param newGroupRecipient com.cannontech.database.db.notification.NotificationRecipient
 */
public void setNotificationRecipient(com.cannontech.database.db.notification.NotificationRecipient newNotificationRecipient ) 
{
	notificationRecipient = newNotificationRecipient;
}
/**
 * This method was created in VisualAge.
 */
public void setRecipientID(Integer recID) 
{
	getNotificationRecipient().setRecipientID(recID);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {

	if( getNotificationRecipient() != null )
		return getNotificationRecipient().getRecipientName();
	else
		return null;
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{	
	getNotificationRecipient().update();
}
}
