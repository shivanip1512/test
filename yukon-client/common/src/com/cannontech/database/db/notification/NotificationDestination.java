package com.cannontech.database.db.notification;

/**
 * This type was created in VisualAge.
 */
public class NotificationDestination extends com.cannontech.database.db.DBPersistent 
{
	private Integer destinationOrder = null;
	private Integer notificationGroupID = null;
	private Integer recipientID = null;

	private final String CONSTRAINT_COLUMNS[] = { "NOTIFICATIONGROUPID" };
	private final String SELECT_COLUMNS[] = { "DESTINATIONORDER", "RecipientID" };

	private static final String tableName = "NotificationDestination";
	
/**
 * State constructor comment.
 */
public NotificationDestination() {
	super();
	initialize(null, null, null );
}
/**
 * State constructor comment.
 */
public NotificationDestination(Integer notificationGroupID )
{
	super();
	initialize( null, notificationGroupID, null );
}
/**
 * State constructor comment.
 */
public NotificationDestination(Integer destinationOrder, Integer notificationGroupID, Integer locationGroupID )
{
	super();
	initialize( destinationOrder, notificationGroupID, locationGroupID );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object setValues[] = { getDestinationOrder(), getNotificationGroupID(), getRecipientID() };

	add( tableName, setValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	Object constraintValues[] = { getNotificationGroupID(), getDestinationOrder() };

	delete( tableName, CONSTRAINT_COLUMNS, constraintValues );
}

/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteAllDestinations(Integer notificationGroupID, java.sql.Connection conn )
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("DELETE FROM NotificationDestination WHERE NotificationGroupID=" + notificationGroupID,
												 conn );
	try
	{
		stmt.execute();
	}
	catch(Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}

	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 2:30:10 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDestinationOrder() {
	return destinationOrder;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.State[]
 * @param stateGroup java.lang.Integer
 */
public static final NotificationDestination[] getNotificationDestinations(Integer notificationGroupID ) throws java.sql.SQLException 
{
	return getNotificationDestinations(notificationGroupID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
													
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.State[]
 * @param stateGroup java.lang.Integer
 */
public static final NotificationDestination[] getNotificationDestinations(Integer notificationGroupID, String databaseAlias) throws java.sql.SQLException 
{
	java.util.ArrayList tmpList = new java.util.ArrayList(50);
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT DESTINATIONORDER,RecipientID " +
				 "FROM NOTIFICATIONDESTINATION WHERE NOTIFICATIONGROUPID= ? " +
				 "ORDER BY DESTINATIONORDER";

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
			pstmt.setInt( 1, notificationGroupID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				tmpList.add( new NotificationDestination( 
						new Integer(rset.getInt("DestinationOrder")), 
						notificationGroupID, 
						new Integer(rset.getInt("RecipientID")) ) );
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

	NotificationDestination retVal[] = new NotificationDestination[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
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
 * Insert the method's description here.
 * Creation date: (9/24/2001 1:41:10 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getRecipientID() {
	return recipientID;
}
/**
 * This method was created in VisualAge.
 * @param stateGroupID java.lang.Integer
 * @param rawState java.lang.Integer
 * @param text java.lang.String
 * @param foregroundColor java.lang.Integer
 * @param backgoundColor java.lang.Integer
 */
public void initialize(Integer destinationOrder, Integer notificationGroupID, Integer recGroupID )
{
	setDestinationOrder(destinationOrder);
	setNotificationGroupID(notificationGroupID);
	setRecipientID(recGroupID);
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getNotificationGroupID() };

	Object results[] = retrieve( SELECT_COLUMNS, tableName, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SELECT_COLUMNS.length )
	{
		setDestinationOrder( (Integer) results[0] );
		setRecipientID( (Integer) results[1] );
	}
	else
		throw new Error( getClass() + "::retrieve - Incorrect number of results" );
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 2:30:10 PM)
 * @param newDestinationOrder java.lang.Integer
 */
public void setDestinationOrder(java.lang.Integer newDestinationOrder) {
	destinationOrder = newDestinationOrder;
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
 * Insert the method's description here.
 * Creation date: (9/24/2001 1:41:10 PM)
 * @param newRecipientID java.lang.Integer
 */
public void setRecipientID(java.lang.Integer newRecipientID) {
	recipientID = newRecipientID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "NotificationGroupID = " + getNotificationGroupID().intValue() +
		   " DestinationOrder = " + getDestinationOrder();
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object constraintValues[] = { getNotificationGroupID() };
	
	Object setValues[] = { getDestinationOrder(), getNotificationGroupID(),  getRecipientID() };
	
	update( tableName, SELECT_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
