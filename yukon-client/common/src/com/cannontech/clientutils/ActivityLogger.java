/*
 * Created on Feb 23, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.clientutils;

import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.db.activity.ActivityLog;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ActivityLogger {
	
	/**
	 * The log method with every parameter (except the PaoID). If
	 * you don't care about some of them, set their values to -1.
	 */
	public static void logEvent(int userID, int accountID, int energyCompanyID, int customerID,
		String action, String description)
	{
		ActivityLog actLog = new ActivityLog();
		actLog.setUserID( userID );
		actLog.setAccountID( accountID );
		actLog.setEnergyCompanyID( energyCompanyID );
		actLog.setCustomerID( customerID );
		actLog.setAction( action );
		actLog.setDescription( description );
		
		try {
			Transaction.createTransaction(Transaction.INSERT, actLog).execute();
		}
		catch (TransactionException e) {
			CTILogger.error( "Failed to write action '" + action + "' to activity log", e );
		}
	}
	
	/**
	 * The log method used by LoginController when the login attempt failed
	 */
	public static void logEvent(String action, String description) {
		logEvent(-1, -1, -1, -1, action, description);
	}
	
	/**
	 * The log method used by LoginController. The rest of the fields
	 * will be filled as much as we can.
	 */
	public static void logEvent(int userID, String action, String description) {
		int accountID = -1;
		int energyCompanyID = -1;
		int customerID = -1;
		
		LiteYukonUser liteUser = YukonUserFuncs.getLiteYukonUser( userID );
		
		LiteEnergyCompany liteComp = EnergyCompanyFuncs.getEnergyCompany( liteUser );
		if (liteComp != null) energyCompanyID = liteComp.getEnergyCompanyID();
		
		LiteContact liteContact = YukonUserFuncs.getLiteContact( userID );
		if (liteContact != null) {
			LiteCustomer liteCust = ContactFuncs.getCustomer( liteContact.getContactID() );
			
			if (liteCust != null) {
				customerID = liteCust.getCustomerID();
				
				if (!liteCust.isExtended())
					liteCust.retrieve( com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
				
				java.util.Vector acctIDs = liteCust.getAccountIDs();
				if (acctIDs.size() > 0) {
					// This customer has residential information
					energyCompanyID = liteCust.getEnergyCompanyID();
					accountID = ((Integer) acctIDs.get(0)).intValue();
				}
			}
		}
		
		logEvent(userID, accountID, energyCompanyID, customerID, action, description);
	}

}
