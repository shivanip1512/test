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

	String sqlString = "SELECT CategoryName, NotificationGroupID " +
		"FROM " + com.cannontech.database.db.notification.AlarmCategory.TABLE_NAME +
		" WHERE AlarmCategoryID = " + getAlarmStateID();

	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	try 
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( databaseAlias );
		stmt = conn.createStatement();
		rset = stmt.executeQuery(sqlString);

		while (rset.next())
		{
			setCategoryName( rset.getString(1) );
			setNotificationGroupID( rset.getInt(2) );
		}

	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if( stmt != null )
				stmt.close();
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}

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
