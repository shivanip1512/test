package com.cannontech.database.data.lite;

/*
 */
public class LiteAlarmCategory extends LiteBase
{
	private String categoryName = null;
	private int notificationGroupID;
/**
 * LiteDevice
 */
public LiteAlarmCategory( int alID ) {
	super();
	setAlarmStateID(alID);
	setLiteType(LiteTypes.ALARM_CATEGORIES);
}
/**
 * LiteDevice
 */
public LiteAlarmCategory( int alID, String alName) 
{
	super();
	setAlarmStateID(alID);
	setCategoryName(alName);
	setLiteType(LiteTypes.ALARM_CATEGORIES);
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/00 3:45:12 PM)
 * @return int
 */
public int getAlarmStateID() 
{
	return getLiteID();
}
/**
 * Insert the method's description here.
 * Creation date: (2/19/2002 4:02:30 PM)
 * @return java.lang.String
 */
public java.lang.String getCategoryName() {
	return categoryName;
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public int getNotificationGroupID() {
	return notificationGroupID;
}
/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) 
{

	com.cannontech.database.SqlStatement s = 
      new com.cannontech.database.SqlStatement(
         "SELECT CategoryName, NotificationGroupID " +
   		"FROM " + com.cannontech.database.db.notification.AlarmCategory.TABLE_NAME +
   		" WHERE AlarmCategoryID = " + getAlarmStateID(),
         com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

	try 
	{
      s.execute();

      if( s.getRowCount() <= 0 )
         throw new IllegalStateException("Unable to find AlarmCategory with stateID = " + getLiteID() );

		setCategoryName( s.getRow(0)[0].toString() );
		setNotificationGroupID( new Integer(s.getRow(0)[1].toString()).intValue() );
	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (12/4/00 3:45:12 PM)
 * @param newAlarmStateID int
 */
public void setAlarmStateID(int newAlarmStateID) 
{
	setLiteID(newAlarmStateID);
}
/**
 * Insert the method's description here.
 * Creation date: (2/19/2002 4:02:30 PM)
 * @param newCategoryName java.lang.String
 */
public void setCategoryName(java.lang.String newCategoryName) {
	categoryName = newCategoryName;
}
/**
 * Insert the method's description here.
 * Creation date: (11/10/00 5:47:04 PM)
 * @param newNotificationGroupID int
 */
public void setNotificationGroupID(int newNotificationGroupID) {
	notificationGroupID = newNotificationGroupID;
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String toString() 
{
	return getCategoryName();
}
}
