package com.cannontech.database.data.lite;

/*
 */
public class LiteNotificationRecipient extends LiteBase
{
	private String recipientName = null;
	private String emailAddress = null;
	private int emailSendType;
	private String pagerNumber = null;
/**
 * LiteDevice
 */
public LiteNotificationRecipient( int recID ) 
{
	super();
	setRecipientID( recID );
	setLiteType(LiteTypes.GROUP_RECIPIENT);
}
/**
 * LiteDevice
 */
public LiteNotificationRecipient( int recID, String recName, String emailAddr, int newEmailSendType, String pagerNum ) 
{
	super();
	setRecipientID( recID );
	
	setRecipientName( recName );
	setEmailAddress( emailAddr );
	setEmailSendType( newEmailSendType );
	setPagerNumber( pagerNum );
	
	setLiteType(LiteTypes.GROUP_RECIPIENT);
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/00 4:28:38 PM)
 * @return java.lang.String
 */
public java.lang.String getEmailAddress() {
	return emailAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 12:45:33 PM)
 * @return int
 */
public int getEmailSendType() {
	return emailSendType;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/00 4:34:44 PM)
 * @return java.lang.String
 */
public java.lang.String getPagerNumber() {
	return pagerNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 12:16:49 PM)
 * @return int
 */
public int getRecipientID() {
	return getLiteID();
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 12:16:49 PM)
 * @return java.lang.String
 */
public java.lang.String getRecipientName() {
	return recipientName;
}
/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) 
{
 	com.cannontech.database.SqlStatement stmt =
 		new com.cannontech.database.SqlStatement("SELECT RecipientName, EmailAddress, EmailSendType, PagerNumber " + 
	 		"FROM NotificationRecipient WHERE RecipientID = " + getRecipientID(), databaseAlias);

 	try
 	{
 		stmt.execute();
		setRecipientName( (String) stmt.getRow(0)[0] );
		setEmailAddress( (String) stmt.getRow(0)[1] );
		setEmailSendType( ((java.math.BigDecimal) stmt.getRow(0)[2]).intValue() );
		setPagerNumber( (String)stmt.getRow(0)[3] );
 	}
 	catch( Exception e )
 	{
 		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
 	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/00 4:28:38 PM)
 * @param newEmailAddress java.lang.String
 */
public void setEmailAddress(java.lang.String newEmailAddress) {
	emailAddress = newEmailAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 12:45:33 PM)
 * @param newEmailSendType int
 */
public void setEmailSendType(int newEmailSendType) {
	emailSendType = newEmailSendType;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/00 4:34:44 PM)
 * @param newPagerNumber java.lang.String
 */
public void setPagerNumber(java.lang.String newPagerNumber) {
	pagerNumber = newPagerNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 12:16:49 PM)
 * @param newRecipientID int
 */
public void setRecipientID(int newRecipientID) 
{
	setLiteID(newRecipientID);
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 12:16:49 PM)
 * @param newRecipientName java.lang.String
 */
public void setRecipientName(java.lang.String newRecipientName) {
	recipientName = newRecipientName;
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String toString() 
{
	return getRecipientName();
}
}
