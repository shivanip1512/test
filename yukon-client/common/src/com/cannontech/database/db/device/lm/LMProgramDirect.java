package com.cannontech.database.db.device.lm;

/**
 * This type was created in VisualAge.
 */
public class LMProgramDirect extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private Integer notifyInterval = null;
	private String heading = null;
	private String messageHeader = null;
	private String messageFooter = null;
	private String canceledMsg = null;
	private String stoppedEarlyMsg = null;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"NOTIFYINTERVAL", "HEADING", "MESSAGEHEADER", "MESSAGEFOOTER", "CANCELEDMSG", "STOPPEDEARLYMSG"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "LMProgramDirect";

/**
 * LMGroupVersacomSerial constructor comment.
 */
public LMProgramDirect() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getNotifyInterval(), getHeading(),
						   getMessageHeader(), getMessageFooter(), getCanceledMsg(),
						   getStoppedEarlyMsg() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, "DeviceID", getDeviceID() );
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2001 2:42:06 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDeviceID() {
	return deviceID;
}

public Integer getNotifyInterval() {
	return notifyInterval;
}

public String getHeading() {
	return heading;
}

public String getMessageHeader() {
	return messageHeader;
}

public String getMessageFooter() {
	return messageFooter;
}

public String getCanceledMsg() {
	return canceledMsg;
}

public String getStoppedEarlyMsg() {
	return stoppedEarlyMsg;
}

public static final LMDirectNotificationGroupList[] getAllNotificationGroupsList(Integer programDeviceID, java.sql.Connection conn) throws java.sql.SQLException
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	
	String sql = "select l.ProgramID, l.CustomerID " +
				 "from " +
				 LMDirectNotificationGroupList.TABLE_NAME + " l " +
				 "where l.ProgramID = ? " +
				 "order by l.CustomerID";

	try
	{		
		//conn = com.cannontech.database.PoolManager.getInstance().getConnection(
							//com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be null.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, programDeviceID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				LMDirectNotificationGroupList customer = new LMDirectNotificationGroupList();
				
				customer.setDeviceID( new Integer(rset.getInt("ProgramID")) );
				customer.setCustomerID( new Integer(rset.getInt("CustomerID")) );

				tmpList.add( customer );
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
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}


	LMDirectNotificationGroupList retVal[] = new LMDirectNotificationGroupList[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}

/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getDeviceID() };	
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	if( results.length == SETTER_COLUMNS.length )
	{
		setNotifyInterval( (Integer) results[0] );
		setHeading( (String) results[1] );
		setMessageHeader( (String) results[2] );
		setMessageFooter( (String) results[3] );
		setCanceledMsg( (String) results[4] );
		setStoppedEarlyMsg( (String) results[5] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2001 2:42:06 PM)
 * @param newDeviceID java.lang.Integer
 */
public void setDeviceID(java.lang.Integer newDeviceID) {
	deviceID = newDeviceID;
}

public void setNotifyInterval(Integer newInterval) {
	notifyInterval = newInterval;
}

public void setHeading(String newHeading) {
	heading = newHeading;
}

public void setMessageHeader(String newHeader) {
	messageHeader = newHeader;
}

public void setMessageFooter(String newFooter) {
	messageFooter = newFooter;
}

public void setCanceledMsg(String newMsg) {
	canceledMsg = newMsg;
}

public void setStoppedEarlyMsg(String newMsg) {
	stoppedEarlyMsg = newMsg;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getNotifyInterval(), getHeading(), getMessageHeader(),
							getMessageFooter(), getCanceledMsg(), getStoppedEarlyMsg() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );

}
}
