package com.cannontech.database.data.web;

/**
 * Creation date: (6/4/2001 1:55:07 PM)
 * @author: Aaron Lauinger
 */

import com.cannontech.database.db.web.EnergyCompany;
import com.cannontech.database.db.web.EnergyCompanyOperatorLoginList;
import com.cannontech.database.db.web.OperatorLogin;

public class Operator extends com.cannontech.database.db.DBPersistent 
{	
	private EnergyCompany energyCompany = null;
	private OperatorLogin operatorLogin = null;

	private String databaseAlias = null;
/**
 * Operator constructor comment.
 */
public Operator() {
	super();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	throw new RuntimeException("Not implemented");
}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException 
{
	throw new RuntimeException("Not implemented");
}
/**
 * Creation date: (6/7/2001 3:15:46 PM)
 * @return java.lang.String
 */
public java.lang.String getDatabaseAlias() {
	return databaseAlias;
}
/**
 * Creation date: (6/4/2001 2:23:36 PM)
 * @return com.cannontech.database.db.web.EnergyCompany
 */
public com.cannontech.database.db.web.EnergyCompany getEnergyCompany() {
	if( energyCompany == null )
		energyCompany = new EnergyCompany();
		
	return energyCompany;
}
/**
 * Creation date: (6/25/2001 1:28:29 PM)
 * @return long
 */
public long getEnergyCompanyID() {
	return getEnergyCompany().getId();
}
/**
 * Creation date: (6/4/2001 2:29:16 PM)
 * @param id long
 */
public long getLoginID() 
{
	return getOperatorLogin().getOperatorLoginID();
}
/**
 * Creation date: (6/5/2001 3:07:58 PM)
 * @return java.lang.String
 */
public String getLoginType() {
	return getOperatorLogin().getLoginType();
}
/**
 * Creation date: (6/4/2001 2:23:36 PM)
 * @return com.cannontech.database.db.web.OperatorLogin
 */
public com.cannontech.database.db.web.OperatorLogin getOperatorLogin() {
	if( operatorLogin == null )
		operatorLogin = new OperatorLogin();
		
	return operatorLogin;
}
/**
 * Creation date: (6/5/2001 3:11:14 PM)
 * @return boolean
 */
public boolean isCurtailmentOperator() {
	return (getLoginType().indexOf(OperatorLogin.CURTAILMENT) >= 0 );
}
/**
 * Creation date: (6/5/2001 3:10:07 PM)
 * @return boolean
 */
public boolean isDirectLoadControlOperator() {
	return (getLoginType().indexOf(OperatorLogin.LOADCONTROL) >= 0 );
}
/**
 * Creation date: (6/5/2001 3:11:25 PM)
 * @return boolean
 */
public boolean isEnergyExchangeOperator() {
	return (getLoginType().indexOf(OperatorLogin.ENERGYEXCHANGE) >= 0 );
}
/**
 * Creation date: (6/5/2001 1:43:09 PM)
 * @return boolean
 */
public boolean isTrendingOperator() {
	return (getLoginType().indexOf(OperatorLogin.READMETER) >= 0 );
}
/**
 * This method was created by a SmartGuide.
 */
public void retrieve() throws java.sql.SQLException 
{
	EnergyCompany co = getEnergyCompany();
 	co.setId(EnergyCompanyOperatorLoginList.getEnergyCompanyID( getLoginID(), getDbConnection().toString() ));

 	co.setDbConnection( getDbConnection() );
 	co.retrieve();
 	co.setDbConnection(null);

 	com.cannontech.clientutils.CTILogger.info(co);

 	OperatorLogin login = getOperatorLogin();
 	login.setDbConnection( getDbConnection() );
 	login.retrieve();
 	login.setDbConnection(null);

	setDatabaseAlias( getDbConnection().toString() ); 	
}
/**
 * Creation date: (6/7/2001 3:15:46 PM)
 * @param newDatabaseAlias java.lang.String
 */
public void setDatabaseAlias(java.lang.String newDatabaseAlias) {
	databaseAlias = newDatabaseAlias;
}
/**
 * Creation date: (6/4/2001 2:23:36 PM)
 * @param newEnergyCompany com.cannontech.database.db.web.EnergyCompany
 */
public void setEnergyCompany(com.cannontech.database.db.web.EnergyCompany newEnergyCompany) {
	energyCompany = newEnergyCompany;
}
/**
 * Creation date: (6/4/2001 2:29:16 PM)
 * @param id long
 */
public void setLoginID(long id) 
{
	getOperatorLogin().setOperatorLoginID(id);
}
/**
 * Creation date: (6/4/2001 2:23:36 PM)
 * @param newOperatorLogin com.cannontech.database.db.web.OperatorLogin
 */
public void setOperatorLogin(com.cannontech.database.db.web.OperatorLogin newOperatorLogin) 
{
	operatorLogin = newOperatorLogin;
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	throw new RuntimeException("Not implemented");
}
}
