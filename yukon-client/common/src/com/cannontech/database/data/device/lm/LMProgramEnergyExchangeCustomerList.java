package com.cannontech.database.data.device.lm;

/**
 * Insert the type's description here.
 * Creation date: (12/6/00 3:54:11 PM)
 * @author: 
 */
public class LMProgramEnergyExchangeCustomerList extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.device.lm.DeviceListItem
{
	private com.cannontech.database.db.device.lm.LMEnergyExchangeCustomerList lmEnergyExchangeCustomerList= null;
	private String customerName = null;
/**
 * LMProgramBase constructor comment.
 */
public LMProgramEnergyExchangeCustomerList() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException 
{
	getLmEnergyExchangeCustomerList().add();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException
{
	getLmEnergyExchangeCustomerList().delete();
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
	return getLmEnergyExchangeCustomerList().getDeviceID();
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/2001 1:53:57 PM)
 * @return com.cannontech.database.db.device.lm.LMEnergyExchangeCustomerList
 */
public com.cannontech.database.db.device.lm.LMEnergyExchangeCustomerList getLmEnergyExchangeCustomerList() 
{
	if( lmEnergyExchangeCustomerList == null )
		lmEnergyExchangeCustomerList = new com.cannontech.database.db.device.lm.LMEnergyExchangeCustomerList();

	return lmEnergyExchangeCustomerList;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException
{
	getLmEnergyExchangeCustomerList().retrieve();
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
	getLmEnergyExchangeCustomerList().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) 
{
	getLmEnergyExchangeCustomerList().setDeviceID(deviceID);
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/2001 1:53:57 PM)
 * @param newLmEnergyExchangeCustomerList com.cannontech.database.db.device.lm.LMEnergyExchangeCustomerList
 */
public void setLmEnergyExchangeCustomerList(com.cannontech.database.db.device.lm.LMEnergyExchangeCustomerList newLmEnergyExchangeCustomerList) {
	lmEnergyExchangeCustomerList = newLmEnergyExchangeCustomerList;
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
	getLmEnergyExchangeCustomerList().update();

}
}
