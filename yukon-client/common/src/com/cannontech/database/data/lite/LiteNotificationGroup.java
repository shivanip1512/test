package com.cannontech.database.data.lite;

/*
 */
public class LiteNotificationGroup extends LiteBase
{
	private String notificationGroupName = null;
	private java.util.ArrayList notificationDestinations = null;
/**
 * LiteDevice
 */
public LiteNotificationGroup( int nID ) 
{
	super();
	setNotificationGroupID(nID);
	setLiteType(LiteTypes.NOTIFICATION_GROUP);
}
/**
 * LiteDevice
 */
public LiteNotificationGroup( int nID, String nName ) 
{
	super();
	
	setNotificationGroupID(nID);
	notificationGroupName = new String(nName);
	setLiteType(LiteTypes.NOTIFICATION_GROUP);
}
/**
 * LiteDevice
 */
public LiteNotificationGroup( int nID, String nName, java.util.List ndList ) 
{
	super();
	setNotificationGroupID(nID);
	notificationGroupName = new String(nName);
	notificationDestinations = new java.util.ArrayList(ndList);
	setLiteType(LiteTypes.STATEGROUP);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public java.util.ArrayList getNotificationDestinationsList() {
	if( notificationDestinations == null )
		notificationDestinations = new java.util.ArrayList(6);
	return notificationDestinations;
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public int getNotificationGroupID() {
	return getLiteID();
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String getNotificationGroupName() {
	return notificationGroupName;
}
/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) {

 	com.cannontech.database.SqlStatement stmt =
 		new com.cannontech.database.SqlStatement("SELECT GroupName FROM notificationGroup WHERE NotificationGroupID = " + Integer.toString(getNotificationGroupID()), databaseAlias);

 	try
 	{
 		stmt.execute();
		notificationGroupName = ((String) stmt.getRow(0)[0]);

		stmt = new com.cannontech.database.SqlStatement(
			"SELECT r.recipientid, r.recipientname, r.emailaddress, r.emailsendtype, r.pagernumber " +
			"FROM NotificationRecipient r, NotificationDestination n WHERE n.notificationgroupID = " + Integer.toString(getNotificationGroupID()) +
			" AND n.recipientid=r.recipientid ORDER BY n.destinationorder"
			, databaseAlias);
		stmt.execute();

		LiteNotificationRecipient ls = null;
		for(int i=0;i<stmt.getRowCount();i++)
		{
			ls = new LiteNotificationRecipient( 
				((java.math.BigDecimal)stmt.getRow(i)[0]).intValue(), 
				((String) stmt.getRow(i)[1]), 
				((String) stmt.getRow(i)[2]),
				((java.math.BigDecimal) stmt.getRow(0)[3]).intValue(),
				((String) stmt.getRow(i)[4]) );
			
			getNotificationDestinationsList().add(ls);
		}
 	}
 	catch( Exception e )
 	{
 		e.printStackTrace();
 	}
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setNotificationDestinationsList(java.util.List newList) {
	this.notificationDestinations = new java.util.ArrayList(newList);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setNotificationGroupID(int newValue) 
{
	setLiteID(newValue);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setNotificationGroupName(String newValue) {
	this.notificationGroupName = new String(newValue);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String toString() {
	return notificationGroupName;
}
}
