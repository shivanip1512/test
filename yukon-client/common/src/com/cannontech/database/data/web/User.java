package com.cannontech.database.data.web;

/**
 * User bean for a web session.
 * Creation date: (3/27/2001 2:19:04 PM)
 * @author: Aaron Lauinger
 */

import java.util.Date;

import com.cannontech.database.db.customer.CICustomerBase;
import com.cannontech.database.db.customer.CustomerContact;
import com.cannontech.database.db.customer.CustomerLogin;
import com.cannontech.database.db.customer.CustomerWebSettings;

public class User extends com.cannontech.database.db.DBPersistent {

	private static String DEFAULT_DATABASE_ALIAS = "yukon";
	
	private CICustomerBase customerBase;
	private CustomerContact customerContact;
	private CustomerLogin customerLogin;
	private CustomerWebSettings customerWebSettings;
	
	private String databaseAlias = DEFAULT_DATABASE_ALIAS;
/**
 * User constructor comment.
 */
public User() {
	super();
}
/**
 * User should never be added through this class.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{	
	throw new RuntimeException("Add not supported");
}
/**
 * User should never be deleted from this class.
 */
public void delete() throws java.sql.SQLException
{
	throw new RuntimeException("delete not supported");
	
}
/**
 * Creation date: (3/29/2001 3:49:18 PM)
 * @return com.cannontech.database.db.device.customer.CICustomerBase
 */
public com.cannontech.database.db.customer.CICustomerBase getCustomerBase() {
	if( customerBase == null )
		customerBase = new com.cannontech.database.db.customer.CICustomerBase();
		
	return customerBase;
}
/**
 * Creation date: (3/29/2001 3:49:18 PM)
 * @return com.cannontech.database.db.device.customer.CustomerContact
 */
public com.cannontech.database.db.customer.CustomerContact getCustomerContact() {
	return customerContact;
}
/**
 * Creation date: (3/29/2001 6:10:24 PM)
 * @return int
 */
public int getCustomerId() {
	return getCustomerBase().getDeviceID().intValue();
}
/**
 * Creation date: (3/27/2001 2:20:40 PM)
 * @return com.cannontech.database.db.device.customer.CustomerLogin
 */
public com.cannontech.database.db.customer.CustomerLogin getCustomerLogin() {
	
	if( customerLogin == null )
	{
		customerLogin = new CustomerLogin();
	}
	
	return customerLogin;
}
/**
 * Creation date: (3/27/2001 2:20:40 PM)
 * @return com.cannontech.database.db.device.customer.CustomerWebSettings
 */
public com.cannontech.database.db.customer.CustomerWebSettings getCustomerWebSettings() {
	
	if( customerWebSettings == null )
	{
		customerWebSettings = new CustomerWebSettings();
	}
			
	return customerWebSettings;
}
/**
 * Creation date: (3/28/2001 1:43:31 PM)
 * @return java.lang.String
 */
public java.lang.String getDatabaseAlias() {
	return databaseAlias;
}
public String getHomeURL()
{
	return getCustomerWebSettings().getHomeURL();
}
public long getId()
{
	return customerLogin.getLoginID().longValue();
}
public Date getLastLogin()
{
	return getCustomerLogin().getLastLogin();
}
public int getLoginCount()
{
	return getCustomerLogin().getLoginCount();
}
public String getLoginType()
{
	return getCustomerLogin().getLoginType();
}
public String getLogo()
{
	return getCustomerWebSettings().getLogo();
}
public String getPassword()
{
	return getCustomerLogin().getUserPassword();
}
public String getUsername()
{
	return getCustomerLogin().getUserName();
}
/**
 * Creation date: (4/11/2001 11:26:41 AM)
 * @return boolean
 */
public boolean isCurtailmentUser() {
	return (getLoginType().indexOf(CustomerLogin.CURTAILMENT) >= 0 );
}
/**
 * Creation date: (6/5/2001 4:15:50 PM)
 * @return boolean
 */
public boolean isEnergyExchangeUser() {
	return (getLoginType().indexOf(CustomerLogin.ENERGYEXCHANGE) >= 0 );
}
/**
 * Creation date: (4/11/2001 11:26:41 AM)
 * @return boolean
 */
public boolean isLoadControlUser() {
	return (getLoginType().indexOf(CustomerLogin.LOADCONTROL) >= 0 );
}
/**
 * Creation date: (4/11/2001 11:26:41 AM)
 * @return boolean
 */
public boolean isTrendingUser() {
	return (getLoginType().indexOf(CustomerLogin.READMETER) >= 0 );
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	CustomerLogin login = getCustomerLogin();	
	login.setDbConnection( getDbConnection() );
	login.retrieve();
	login.setDbConnection(null);

	String dbAlias = getDbConnection().toString();
	CustomerContact contact;

	setCustomerContact( (contact = CustomerContact.getCustomerContact( login.getLoginID(), dbAlias)));	
	setCustomerBase( CICustomerBase.getCustomerContact( contact.getContactID(), dbAlias));

	CustomerWebSettings settings = getCustomerWebSettings();
	settings.setId( getCustomerBase().getDeviceID().longValue() );
	settings.setDbConnection( getDbConnection() );
	settings.retrieve();
	settings.setDbConnection(null);

	setDatabaseAlias( getDbConnection().toString() );

} 
/**
 * Creation date: (3/29/2001 3:49:18 PM)
 * @param newCustomerBase com.cannontech.database.db.device.customer.CICustomerBase
 */
public void setCustomerBase(com.cannontech.database.db.customer.CICustomerBase newCustomerBase) {
	customerBase = newCustomerBase;
}
/**
 * Creation date: (3/29/2001 3:49:18 PM)
 * @param newCustomerContact com.cannontech.database.db.device.customer.CustomerContact
 */
public void setCustomerContact(com.cannontech.database.db.customer.CustomerContact newCustomerContact) {
	customerContact = newCustomerContact;
}
/**
 * Creation date: (3/27/2001 2:20:40 PM)
 * @param newCustomerLogin com.cannontech.database.db.device.customer.CustomerLogin
 */
public void setCustomerLogin(com.cannontech.database.db.customer.CustomerLogin newCustomerLogin) {
	customerLogin = newCustomerLogin;
}
/**
 * Creation date: (3/27/2001 2:20:40 PM)
 * @param newCustomerWebSettings com.cannontech.database.db.device.customer.CustomerWebSettings
 */
public void setCustomerWebSettings(com.cannontech.database.db.customer.CustomerWebSettings newCustomerWebSettings) {
	customerWebSettings = newCustomerWebSettings;
}
/**
 * Creation date: (3/28/2001 1:43:31 PM)
 * @param newDatabaseAlias java.lang.String
 */
public void setDatabaseAlias(java.lang.String newDatabaseAlias) {
	databaseAlias = newDatabaseAlias;
}
public void setHomeURL(String URL)
{
	getCustomerWebSettings().setHomeURL(URL);
}
public void setId(long id)
{
	getCustomerLogin().setLoginID(new Integer((int)id));
	getCustomerWebSettings().setId(id);
}
public void setLastLogin(Date last)
{
	getCustomerLogin().setLastLogin(last);
}
public void setLoginCount(int count)
{
	getCustomerLogin().setLoginCount(count);
}
public void setLoginType(String loginType)
{
	getCustomerLogin().setLoginType(loginType);
}
public void setLogo(String logo)
{
	getCustomerWebSettings().setLogo(logo);
}
public void setPassword(String password)
{
	getCustomerLogin().setUserPassword(password);
}
public void setUsername(String username)
{
	getCustomerLogin().setUserName(username);
}
/**
 * User should not be updated from this class.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	throw new RuntimeException("update not supported");
}
}
