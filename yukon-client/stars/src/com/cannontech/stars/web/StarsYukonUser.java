package com.cannontech.stars.web;

import java.util.*;
import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.util.ServerUtils;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsYukonUser {

	private LiteYukonUser yukonUser = null;
	private int userID = 0;
	private int energyCompanyID = 0;
	private int[] accountIDs = null;
	private Hashtable attributes = new Hashtable();
	
	public StarsYukonUser(LiteYukonUser user) {
		yukonUser = user;
		userID = user.getUserID();
		init();
	}
	
	public int getUserID() {
		return userID;
	}
	
	public LiteYukonUser getYukonUser() {
		if (yukonUser == null)
			yukonUser = com.cannontech.database.cache.functions.YukonUserFuncs.getLiteYukonUser( userID );
		return yukonUser;
	}
	
	public void resetYukonUser() {
		yukonUser = null;
	}
	
	public int getEnergyCompanyID() {
		return energyCompanyID;
	}
	
	public int[] getCustomerAccountIDs() {
		return accountIDs;
	}
	
	public Object getAttribute(Object name) {
		return attributes.get( name );
	}
	
	public Enumeration getAttributeNames() {
		return attributes.keys();
	}
	
	public void setAttribute(Object name, Object value) {
		attributes.put( name, value );
	}
	
	public void removeAttribute(Object name) {
		attributes.remove( name );
	}
	
	public synchronized Integer getIncAttribute(Object name) {
		Integer value = (Integer) attributes.get( name );
		if (value != null) {
			Integer newValue = new Integer( value.intValue()+1 );
			attributes.put( name, newValue );
		}
		return value;
	}
	
	private void init() {
		if (ServerUtils.isOperator(this)) {
			energyCompanyID = EnergyCompanyFuncs.getEnergyCompany( getYukonUser() ).getLiteID();
		}
		else if (ServerUtils.isCICustomer(this)) {
			energyCompanyID = EnergyCompanyFuncs.getEnergyCompany( getYukonUser() ).getLiteID();
		}
		else if (ServerUtils.isResidentialCustomer(this)) {
			String sql = "SELECT map.EnergyCompanyID, acct.AccountID "
					   + "FROM CustomerAccount acct, ECToAccountMapping map "
					   + "WHERE acct.LoginID = " + getYukonUser().getUserID() + " AND acct.AccountID = map.AccountID";
			com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
					sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
					
			try {
				stmt.execute();
				accountIDs = new int[ stmt.getRowCount() ];
				for (int i = 0; i < stmt.getRowCount(); i++) {
					energyCompanyID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
					accountIDs[i] = ((java.math.BigDecimal) stmt.getRow(i)[1]).intValue();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
