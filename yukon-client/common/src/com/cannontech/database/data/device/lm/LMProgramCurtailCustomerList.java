package com.cannontech.database.data.device.lm;

/**
 * Insert the type's description here.
 * Creation date: (12/6/00 3:54:11 PM)
 * @author: 
 */
public class LMProgramCurtailCustomerList extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.device.lm.DeviceListItem
{
	private com.cannontech.database.db.device.lm.LMProgramCurtailCustomerList lmProgramCurtailCustomerList = null;
	private String customerName = null;
/**
 * LMProgramBase constructor comment.
 */
public LMProgramCurtailCustomerList() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException 
{
	getLmProgramCurtailCustomerList().add();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException
{
	getLmProgramCurtailCustomerList().delete();
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/2001 11:32:00 AM)
 * @return java.lang.String
 */
public java.lang.String getCustomerName() {
	return customerName;
}
/**
 * This method was created in VisualAge.
 */
public Integer getDeviceID()
{
	return getLmProgramCurtailCustomerList().getDeviceID();
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/2001 11:29:51 AM)
 * @return com.cannontech.database.db.device.lm.LMProgramCurtailCustomerList
 */
public com.cannontech.database.db.device.lm.LMProgramCurtailCustomerList getLmProgramCurtailCustomerList() 
{
	if( lmProgramCurtailCustomerList == null )
		lmProgramCurtailCustomerList = new com.cannontech.database.db.device.lm.LMProgramCurtailCustomerList();
		
	return lmProgramCurtailCustomerList;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException
{
	getLmProgramCurtailCustomerList().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/2001 11:32:00 AM)
 * @param newCustomerName java.lang.String
 */
public void setCustomerName(java.lang.String newCustomerName) {
	customerName = newCustomerName;
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	getLmProgramCurtailCustomerList().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) 
{
	getLmProgramCurtailCustomerList().setDeviceID(deviceID);
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/2001 11:29:51 AM)
 * @param newLmProgramCurtailCustomerList com.cannontech.database.db.device.lm.LMProgramCurtailCustomerList
 */
public void setLmProgramCurtailCustomerList(com.cannontech.database.db.device.lm.LMProgramCurtailCustomerList newLmProgramCurtailCustomerList) {
	lmProgramCurtailCustomerList = newLmProgramCurtailCustomerList;
}
/**
 * This method was created in VisualAge.
 * @return String
 */
public String toString() 
{
	if( getCustomerName() == null )
		return "";
	else
		return getCustomerName();
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException
{
	getLmProgramCurtailCustomerList().update();

}
}
