package com.cannontech.database.db.notification;

/**
 * This type was created in VisualAge.
 */
public class NotificationRecipient extends com.cannontech.database.db.DBPersistent 
{
	public static final int EMAIL_NOTIFYTYPE = 1;
	public static final int PAGER_NOTIFYTYPE = 2;

	public static final int DUMMY_LOCATIONID = -999;
	public static final String NONE_STRING = "None";

	public static final int NONE_RECIPIENT_ID = -1;

	//possible values for recipientType
	public static final String TYPE_EMAIL = "EMAIL";
	public static final String TYPE_PAGER = "PAGER";
	
	private Integer recipientID = null;
	private String recipientName = null;
	private String emailAddress = null;
	private Integer emailSendType = new Integer(EMAIL_NOTIFYTYPE);
	private String pagerNumber = null;
	private String disableFlag = "N";
	private String recipientType = TYPE_EMAIL;

	private final String CONSTRAINT_COLUMNS[] = { "RecipientID" };
	
	private final String SELECT_COLUMNS[] = { "RecipientName", "EMAILADDRESS", 
		"EMAILSENDTYPE", "PAGERNUMBER", "DISABLEFLAG", "RecipientType" };

	public static final String TABLE_NAME = "NotificationRecipient";	
/**
 * State constructor comment.
 */
public NotificationRecipient() {
	super();
	initialize(null, null, null, null, null, null, null );
}
/**
 * State constructor comment.
 */
public NotificationRecipient(Integer locationID, String locationName, String emailAddress, Integer emailSendType, String pagerNumber, String disableFlag, String recType )
{
	super();
	initialize( locationID, locationName, emailAddress, emailSendType, pagerNumber, disableFlag, recType );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object setValues[] = { getRecipientID(), getRecipientName(), getEmailAddress(), getEmailSendType(),
			getPagerNumber(), getDisableFlag(), getRecipientType() };

	add( TABLE_NAME, setValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	Object constraintValues[] = { getRecipientID() };

	delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}
/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteAllRecipients(int locationID )
{
	return deleteAllRecipients(locationID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteAllRecipients(int locationID, String databaseAlias)
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("DELETE FROM " + TABLE_NAME + " WHERE locationID=" + locationID,
												 databaseAlias );												 
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
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.State[]
 * @param stateGroup java.lang.Integer
 */
public static final NotificationRecipient[] getAllNotificationGroupRecipients() throws java.sql.SQLException 
{
	return getAllNotificationGroupRecipients(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.State[]
 * @param stateGroup java.lang.Integer
 */
public static final NotificationRecipient[] getAllNotificationGroupRecipients(String databaseAlias) throws java.sql.SQLException 
{
	java.util.ArrayList tmpList = new java.util.ArrayList(50);
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT RecipientID,RecipientName,EMAILADDRESS, " + 
			"EMAILSENDTYPE,PAGERNUMBER,DISABLEFLAG, RecipientType" +
			"FROM " + TABLE_NAME + " " +
			"ORDER BY LOCATIONNAME";

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
				tmpList.add( new NotificationRecipient( 
							new Integer(rset.getInt("LocationID")),
							rset.getString("LocationName"),
							rset.getString("EmailAddress"),
							new Integer(rset.getInt("EmailSendType")),
							rset.getString("PagerNumber"),
							rset.getString("DisableFlag"),
							rset.getString("RecipientType") ) );
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

	NotificationRecipient retVal[] = new NotificationRecipient[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 2:30:10 PM)
 * @return java.lang.String
 */
public java.lang.String getDisableFlag() {
	return disableFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (11/21/00 4:21:44 PM)
 * @return java.lang.String
 */
public java.lang.String getEmailAddress() {
	return emailAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (11/21/00 4:21:44 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getEmailSendType() {
	return emailSendType;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public final static Integer getNextLocationID() 
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List recipients = cache.getAllNotificationRecipients();
		java.util.Collections.sort(recipients);

		int counter = 1;
		int currentID;
		 														
		for(int i=0;i<recipients.size();i++)
		{
			currentID = ((com.cannontech.database.data.lite.LiteNotificationRecipient)recipients.get(i)).getRecipientID();

			if( currentID > counter )
				break;
			else
				counter = currentID + 1;
		}		
		
		return new Integer( counter );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/21/00 4:21:44 PM)
 * @return java.lang.String
 */
public java.lang.String getPagerNumber() {
	return pagerNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 1:16:21 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getRecipientID() {
	return recipientID;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 1:16:21 PM)
 * @return java.lang.String
 */
public java.lang.String getRecipientName() {
	return recipientName;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 1:16:21 PM)
 * @return java.lang.String
 */
public java.lang.String getRecipientType() {
	return recipientType;
}
/**
 * This method was created in VisualAge.
 * @param stateGroupID java.lang.Integer
 * @param rawState java.lang.Integer
 * @param text java.lang.String
 * @param foregroundColor java.lang.Integer
 * @param backgoundColor java.lang.Integer
 */
private void initialize(Integer recID, String recName, String emailAddress, Integer emailSendType, String pagerNumber, String disableFlag, String recType )
{
	setRecipientID(recID);
	setRecipientName(recName);
	setEmailAddress(emailAddress);
	setEmailSendType(emailSendType);
	setPagerNumber(pagerNumber);
	setDisableFlag(disableFlag);
	setRecipientType( recType );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getRecipientID() };

	Object results[] = retrieve( SELECT_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SELECT_COLUMNS.length )
	{
		setRecipientName( (String) results[0] );
		setEmailAddress( (String) results[1] );
		setEmailSendType( (Integer) results[2] );
		setPagerNumber( (String) results[3] );
		setDisableFlag( (String) results[4] );
		setRecipientType( (String) results[5] );		
	}
	else
		throw new Error( getClass() + "::retrieve - Incorrect number of results" );
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 2:30:10 PM)
 * @param newDisableFlag java.lang.String
 */
public void setDisableFlag(java.lang.String newDisableFlag) {
	disableFlag = newDisableFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (11/21/00 4:21:44 PM)
 * @param newEmailAddress java.lang.String
 */
public void setEmailAddress(java.lang.String newEmailAddress) {
	emailAddress = newEmailAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (11/21/00 4:21:44 PM)
 * @param newEmailSendType java.lang.Integer
 */
public void setEmailSendType(java.lang.Integer newEmailSendType) {
	emailSendType = newEmailSendType;
}
/**
 * Insert the method's description here.
 * Creation date: (11/21/00 4:21:44 PM)
 * @param newPagerNumber java.lang.String
 */
public void setPagerNumber(java.lang.String newPagerNumber) {
	pagerNumber = newPagerNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 1:16:21 PM)
 * @param newRecipientID java.lang.Integer
 */
public void setRecipientID(java.lang.Integer newRecipientID) {
	recipientID = newRecipientID;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 1:16:21 PM)
 * @param newRecipientName java.lang.String
 */
public void setRecipientName(java.lang.String newRecipientName) {
	recipientName = newRecipientName;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 1:16:21 PM)
 * @param newRecipientType java.lang.String
 */
public void setRecipientType(java.lang.String newRecipientType) {
	recipientType = newRecipientType;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() 
{
	return getRecipientName();
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object constraintValues[] = { getRecipientID() };
	
	Object setValues[] = { getRecipientName(), getEmailAddress(), getEmailSendType(),
			getPagerNumber(), getDisableFlag(), getRecipientType() };
	
	update( TABLE_NAME, SELECT_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
