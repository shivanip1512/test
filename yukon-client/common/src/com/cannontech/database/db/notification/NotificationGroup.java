package com.cannontech.database.db.notification;

import com.cannontech.common.util.CtiUtilities;

/**
 * This type was created in VisualAge.
 */
public class NotificationGroup extends com.cannontech.database.db.DBPersistent
{
	private Integer notificationGroupID = null;
	private String groupName = null;
	private String emailSubject = null;
	private String emailFromAddress = null;
	private String emailMessage = null;
	private String numericalPagerMessage = CtiUtilities.STRING_NONE;
	private String disableFlag = "N";

	public static final int NONE_NOTIFICATIONGROUP_ID = 1;
	
	public final static String CONSTRAINT_COLUMNS[] = { "NOTIFICATIONGROUPID" };
	public final static String SELECTED_COLUMNS[] = 
	{ 
		"GROUPNAME", "EMAILSUBJECT", "EMAILFROMADDRESS", 
		"EMAILMESSAGE", "NUMERICPAGERMESSAGE", "DISABLEFLAG" 
	};

	public static final String TABLE_NAME = "NotificationGroup";


/**
 * StateGroup constructor comment.
 */
public NotificationGroup() {
	super();
	initialize( null, null, null, null, null, null, null );
}
/**
 * StateGroup constructor comment.
 */
public NotificationGroup(Integer stateGroupID) {
	super();
	initialize( stateGroupID, null, null, null, null, null, null );
}
/**
 * StateGroup constructor comment.
 */
public NotificationGroup(Integer stateGroupID, String name ) {
	super();
	initialize( stateGroupID, name, null, null, null, null, null );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	if( getNotificationGroupID() == null )
		setNotificationGroupID( getNextNotificationGroupID(getDbConnection()) );

		
	Object setValues[] = { getNotificationGroupID(), getGroupName(), getEmailSubject(), getEmailFromAddress(),
						getEmailMessage(), getNumericalPagerMessage(), getDisableFlag() };

	add( TABLE_NAME, setValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, "NOTIFICATIONGROUPID", getNotificationGroupID() );
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 1:07:32 PM)
 * @return java.lang.String
 */
public java.lang.String getDisableFlag() {
	return disableFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 1:07:32 PM)
 * @return java.lang.String
 */
public java.lang.String getEmailFromAddress() {
	return emailFromAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 1:07:32 PM)
 * @return java.lang.String
 */
public java.lang.String getEmailMessage() {
	return emailMessage;
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 1:07:32 PM)
 * @return java.lang.String
 */
public java.lang.String getEmailSubject() {
	return emailSubject;
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 1:07:32 PM)
 * @return java.lang.String
 */
public java.lang.String getGroupName() {
	return groupName;
}

/*
public final static Integer getNextNotificationGroupID() 
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List notificationGroups = cache.getAllContactNotificationGroups();
		java.util.Collections.sort(notificationGroups);

		int counter = 1;
		int currentID;
		 														
		for(int i=0;i<notificationGroups.size();i++)
		{
			currentID = ((com.cannontech.database.data.lite.LiteNotificationGroup)notificationGroups.get(i)).getNotificationGroupID();

			if( currentID > counter )
				break;
			else
				counter = currentID + 1;
		}		
		
		return new Integer( counter );
	}
}
*/

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/99 10:31:33 AM)
	 * @return java.lang.Integer
	 */
	public static synchronized Integer getNextNotificationGroupID( java.sql.Connection conn )
	{
		if( conn == null )
			throw new IllegalStateException("Database connection should not be null.");
	
		
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		
		try 
		{		
		    stmt = conn.createStatement();
			 rset = stmt.executeQuery(
					"SELECT Max(NotificationGroupID)+1 FROM " + TABLE_NAME );	
				
			 //get the first returned result
			 rset.next();
		    return new Integer( rset.getInt(1) );
		}
		catch (java.sql.SQLException e) 
		{
		    e.printStackTrace();
		}
		finally 
		{
		    try 
		    {
				if ( stmt != null) stmt.close();
		    }
		    catch (java.sql.SQLException e2) 
		    {
				e2.printStackTrace();
		    }
		}
		
		//strange, should not get here
		return new Integer(CtiUtilities.NONE_ZERO_ID);
	}


/**
 * Insert the method's description here.
 * Creation date: (11/16/00 1:07:32 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getNotificationGroupID() {
	return notificationGroupID;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.StateGroup[]
 */
public static final NotificationGroup[] getNotificationGroups() throws java.sql.SQLException {

	return getNotificationGroups(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.StateGroup[]
 */
public static final NotificationGroup[] getNotificationGroups(String databaseAlias) throws java.sql.SQLException 
{
	java.util.ArrayList tmpList = new java.util.ArrayList(50);
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT NOTIFICATIONGROUPID, GROUPNAME FROM " + TABLE_NAME;

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
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				tmpList.add( new NotificationGroup( 
						new Integer(rset.getInt("NotificationGroupID")), 
						rset.getString("GroupName") ) );
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( pstmt != null ) pstmt.close();
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}

	NotificationGroup retVal[] = new NotificationGroup[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 1:07:32 PM)
 * @return java.lang.String
 */
public java.lang.String getNumericalPagerMessage() {
	return numericalPagerMessage;
}
/**
 * This method was created in VisualAge.
 * @param stateGroupID java.lang.Integer
 * @param name java.lang.String
 */
protected void initialize( Integer notificationGroupID, String groupName, String emailSubject, String emailFromAddress, String emailMessage, String numericPagerMessage, String disableFlag)
{
	setNotificationGroupID( notificationGroupID );
	setGroupName( groupName );
	setEmailSubject( emailSubject );
	setEmailFromAddress( emailFromAddress );
	setEmailMessage( emailMessage );
	setNumericalPagerMessage( numericalPagerMessage );
	setDisableFlag( disableFlag );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	Object constraintValues[] = { getNotificationGroupID() };

	Object results[] = retrieve( SELECTED_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SELECTED_COLUMNS.length )
	{
		setGroupName( (String) results[0] );
		setEmailSubject( (String) results[1] );
		setEmailFromAddress( (String) results[2] );
		setEmailMessage( (String) results[3] );
		setNumericalPagerMessage( (String) results[4] );
		setDisableFlag( (String) results[5] );
	}
	else
		throw new Error( getClass() + "::retrieve - Incorrect number of results returned" );

	
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 1:07:32 PM)
 * @param newDisableFlag java.lang.String
 */
public void setDisableFlag(java.lang.String newDisableFlag) {
	disableFlag = newDisableFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 1:07:32 PM)
 * @param newEmailFromAddress java.lang.String
 */
public void setEmailFromAddress(java.lang.String newEmailFromAddress) {
	emailFromAddress = newEmailFromAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 1:07:32 PM)
 * @param newEmailMessage java.lang.String
 */
public void setEmailMessage(java.lang.String newEmailMessage) {
	emailMessage = newEmailMessage;
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 1:07:32 PM)
 * @param newEmailSubject java.lang.String
 */
public void setEmailSubject(java.lang.String newEmailSubject) {
	emailSubject = newEmailSubject;
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 1:07:32 PM)
 * @param newGroupName java.lang.String
 */
public void setGroupName(java.lang.String newGroupName) {
	groupName = newGroupName;
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 1:07:32 PM)
 * @param newNotificationGroupID java.lang.Integer
 */
public void setNotificationGroupID(java.lang.Integer newNotificationGroupID) {
	notificationGroupID = newNotificationGroupID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 1:07:32 PM)
 * @param newNumericalPagerMessage java.lang.String
 */
public void setNumericalPagerMessage(java.lang.String newNumericalPagerMessage) {
	numericalPagerMessage = newNumericalPagerMessage;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return getGroupName();
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object constraintValues[] = { getNotificationGroupID() };
	Object setValues[] = { getGroupName(), getEmailSubject(), getEmailFromAddress(),
						getEmailMessage(), getNumericalPagerMessage(), getDisableFlag() };

	update( TABLE_NAME, SELECTED_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
