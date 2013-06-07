package com.cannontech.database.db.notification;

import com.cannontech.database.SqlUtils;
import com.cannontech.dispatch.DbChangeType;
import com.cannontech.messaging.message.dispatch.DBChangeMessage;

/**
 * This type was created in VisualAge.
 */
public class AlarmCategory extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private Integer alarmCategoryID = null;
	private String categoryName = null;
	private Integer notificationGroupID = null;

	private final String CONSTRAINT_COLUMNS[] = { "AlarmCategoryID" };
	private final String SELECT_COLUMNS[] = { "CategoryName", "NotificationGroupID" };

	public static final String TABLE_NAME = "AlarmCategory";
	
/**
 * State constructor comment.
 */
public AlarmCategory() {
	super();
	initialize(null, null, null );
}
/**
 * State constructor comment.
 */
public AlarmCategory(Integer alarmStateID, String alarmStateName, Integer notificationGroupID )
{
	super();
	initialize( alarmStateID, alarmStateName, notificationGroupID );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	// Do nothing
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	// Do nothing
}
/**
 * This method was created in VisualAge.
 * @param stateGroup java.lang.Integer
 */
public static final AlarmCategory[] getAlarmCategories(Integer alCategoryID ) throws java.sql.SQLException 
{
	return getAlarmCategories(alCategoryID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.State[]
 * @param stateGroup java.lang.Integer
 */
public static final AlarmCategory[] getAlarmCategories(Integer alCategoryID, String databaseAlias) throws java.sql.SQLException 
{
	java.util.ArrayList tmpList = new java.util.ArrayList(50);
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT CategoryName,NotificationGroupID " +
				 "FROM " + TABLE_NAME + " WHERE AlarmCategoryID = ? " +
				 "ORDER BY CategoryName";

	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);

		if( conn == null )
		{
			throw new IllegalStateException("Error getting database connection.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, alCategoryID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				tmpList.add( new AlarmCategory( 
						alCategoryID, 
						rset.getString("CategoryName"),
						new Integer(rset.getInt("NotificationGroupID")) ) );				
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, pstmt, conn );	
	}


	AlarmCategory retVal[] = new AlarmCategory[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 11:46:13 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getAlarmCategoryID() {
	return alarmCategoryID;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 11:46:13 AM)
 * @return java.lang.String
 */
public java.lang.String getCategoryName() {
	return categoryName;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2001 1:45:25 PM)
 * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
 */
public DBChangeMessage[] getDBChangeMsgs(DbChangeType dbChangeType)
{
	DBChangeMessage[] msgs = { 
	        new DBChangeMessage(
	                        getAlarmCategoryID().intValue(),
	                        DBChangeMessage.CHANGE_ALARM_CATEGORY_DB,
	                        DBChangeMessage.CAT_ALARMCATEGORY,
	                        DBChangeMessage.CAT_ALARMCATEGORY,
	                        dbChangeType)
	};

	return msgs;
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 2:30:10 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getNotificationGroupID() {
	return notificationGroupID;
}
/**
 * This method was created in VisualAge.
 * @param stateGroupID java.lang.Integer
 * @param rawState java.lang.Integer
 * @param text java.lang.String
 * @param foregroundColor java.lang.Integer
 * @param backgoundColor java.lang.Integer
 */
private void initialize(Integer alCategoryID, String catName, Integer notificationGroupID )
{
	setAlarmCategoryID( alCategoryID);
	setCategoryName(catName);
	setNotificationGroupID(notificationGroupID);
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getAlarmCategoryID() };

	Object results[] = retrieve( SELECT_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SELECT_COLUMNS.length )
	{
		setCategoryName( (String) results[0] );
		setNotificationGroupID( (Integer) results[1] );
	}
	else
		throw new Error( getClass() + "::retrieve - Incorrect number of results" );
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 11:46:13 AM)
 * @param newAlarmCategoryID java.lang.Integer
 */
public void setAlarmCategoryID(java.lang.Integer newAlarmCategoryID) {
	alarmCategoryID = newAlarmCategoryID;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 11:46:13 AM)
 * @param newCategoryName java.lang.String
 */
public void setCategoryName(java.lang.String newCategoryName) {
	categoryName = newCategoryName;
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 2:30:10 PM)
 * @param newNotificationGroupID java.lang.Integer
 */
public void setNotificationGroupID(java.lang.Integer newNotificationGroupID) {
	notificationGroupID = newNotificationGroupID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() 
{
	return getCategoryName();
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object constraintValues[] = { getAlarmCategoryID() };
	
	Object setValues[] = { getCategoryName(), getNotificationGroupID() };
	
	update( TABLE_NAME, SELECT_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
