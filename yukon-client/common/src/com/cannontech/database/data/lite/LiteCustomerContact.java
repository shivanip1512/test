package com.cannontech.database.data.lite;

/*
 */
public class LiteCustomerContact extends LiteBase
{
	private String contFirstName = null;
	private String contLastName = null;
	private int loginID = com.cannontech.database.db.customer.CustomerLogin.NONE_LOGIN_ID;

	//what customer this contact is related to
	private int deviceID = 0;
/**
 * LiteDevice
 */
public LiteCustomerContact( int contID ) {
	super();
	setContactID(contID);
	setLiteType(LiteTypes.CUSTOMER_CONTACT);
}
/**
 * LiteDevice
 */
public LiteCustomerContact( int contID, String contactFirstName, String contactLastName, int devID, int loginid ) 
{
	super();
	setContactID(contID);
	contFirstName = new String(contactFirstName);
	contLastName = new String(contactLastName);
	deviceID = devID;
	setLiteType(LiteTypes.CUSTOMER_CONTACT);
	loginID = loginid;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 9:29:21 AM)
 * @return int
 */
public int getContactID() {
	return getLiteID();
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 9:29:21 AM)
 * @return java.lang.String
 */
public java.lang.String getContFirstName() {
	return contFirstName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 9:29:21 AM)
 * @return java.lang.String
 */
public java.lang.String getContLastName() {
	return contLastName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 9:34:19 AM)
 * @return int
 */
public int getDeviceID() {
	return deviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 2:35:57 PM)
 * @return int
 */
public int getLoginID() {
	return loginID;
}
/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) 
{
 	com.cannontech.database.SqlStatement stmt =
 		new com.cannontech.database.SqlStatement(
	 		"SELECT cus.ContFirstName, cus.ContLastName, cont.deviceID, cus.LoginID " + 
	 		"FROM CustomerContact cus, CICustContact cont " + 
	 		"WHERE cus.contactID = " + getContactID() + " and " +
	 		"cus.contactID=cont.contactID", databaseAlias);

 	try
 	{
 		stmt.execute();

 		if( stmt.getRowCount() > 0 )
 		{
			setContFirstName( ((String) stmt.getRow(0)[0]) );
			setContLastName( ((String) stmt.getRow(0)[1]) );
			setDeviceID( ((java.math.BigDecimal) stmt.getRow(0)[2]).intValue() );
			setLoginID( ((java.math.BigDecimal) stmt.getRow(0)[3]).intValue() );
 		}

 	}
 	catch( Exception e )
 	{
 		e.printStackTrace();
 	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 9:29:21 AM)
 * @param newContactID int
 */
public void setContactID(int newContactID) 
{
	setLiteID(newContactID);
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 9:29:21 AM)
 * @param newContFirstName java.lang.String
 */
public void setContFirstName(java.lang.String newContFirstName) {
	contFirstName = newContFirstName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 9:29:21 AM)
 * @param newContLastName java.lang.String
 */
public void setContLastName(java.lang.String newContLastName) {
	contLastName = newContLastName;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 9:34:19 AM)
 * @param newDeviceID int
 */
public void setDeviceID(int newDeviceID) {
	deviceID = newDeviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 2:35:57 PM)
 * @param newLoginID int
 */
public void setLoginID(int newLoginID) {
	loginID = newLoginID;
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String toString() {
	return getContLastName() + ", " + getContFirstName();
}
}
