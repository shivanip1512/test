/*
 * Created on Feb 23, 2004
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
 * ActivityLogger provides a mechanism for applications to log interesting things that
 * a yukon user might do along with other possibly interesting information about the user.
 * i.e. account id, energy company id
 * 
 * It is intended to be useful to generate reports from.
 * @author yao
 * @author aaron
 */
public class ActivityLogger {
	
	/**
	 * The log method with every parameter (except the PaoID). If
	 * you don't care about some of them, set their values to -1.
	 */
	public static void logEvent(int userID, int accountID, int energyCompanyID, int customerID,
		String action, String description)
	{
		logEvent(userID, accountID, energyCompanyID, customerID, -1, action, description);
	}
	
	/**
	 * The log method used by LoginController when the login attempt failed
	 */
	public static void logEvent(String action, String description) {
		logEvent(-1, -1, -1, -1, -1, action, description);
	}
	
	public static void logEvent(int userID, int paoID, String action, String description)
	{
		logEvent(userID, -1, -1, -1, paoID, action, description);	
	}
	
	/**
	 * The log method used by LoginController. The rest of the fields
	 * will be filled as much as we can.
	 */
	public static void logEvent(int userID, String action, String description) {
		logEvent(userID, -1, -1, -1, -1, action, description);
	}
	
	/**
	 * logEvent with all possible parameters.
	 * Set any parameters that aren't relevant to -1 and attempts will be made to
	 * fill them automagically.
	 * @param userID
	 * @param accountID
	 * @param energyCompanyID
	 * @param customerID
	 * @param paoID
	 * @param action
	 * @param description
	 */
	public static void logEvent(int userID, int accountID, int energyCompanyID, 
								int customerID, int paoID, String action, String description)
	{
		if (userID != -1) {
			if(energyCompanyID == -1)
			{	
				LiteYukonUser liteUser = YukonUserFuncs.getLiteYukonUser( userID );
				
				LiteEnergyCompany liteComp = EnergyCompanyFuncs.getEnergyCompany( liteUser );
				if (liteComp != null)
				{ 			
					energyCompanyID = liteComp.getEnergyCompanyID();
				}
			}
			
			if(customerID == -1 || energyCompanyID == -1 || accountID == -1)
			{		
				LiteContact liteContact = YukonUserFuncs.getLiteContact( userID );
				if (liteContact != null) {
					LiteCustomer liteCust = ContactFuncs.getCustomer( liteContact.getContactID() );
					
					if (liteCust != null) {
						if(customerID == -1)
						{								
							customerID = liteCust.getCustomerID();
						}
						
						java.util.Vector acctIDs = liteCust.getAccountIDs();
						if (acctIDs.size() > 0) {
							// This customer has residential information
							if(energyCompanyID == -1)
							{					
								energyCompanyID = liteCust.getEnergyCompanyID();
							}
							if(accountID == -1)
							{					
								accountID = ((Integer) acctIDs.get(0)).intValue();
							}
						}
					}
				}
			}
		}
		
		ActivityLog actLog = new ActivityLog();
		actLog.setUserID( userID );
		actLog.setAccountID( accountID );
		actLog.setEnergyCompanyID( energyCompanyID );
		actLog.setCustomerID( customerID );
		actLog.setAction( action );
		actLog.setPaoID(paoID);
		actLog.setDescription( description );
		
		try {
			Transaction.createTransaction(Transaction.INSERT, actLog).execute();
		}
		catch (TransactionException e) {
			CTILogger.error( "Failed to write action '" + action + "' to activity log", e );
		}
	}
}
