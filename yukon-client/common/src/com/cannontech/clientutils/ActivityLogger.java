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
	public static void log(int userID, int accountID, int customerID, int energyCompanyID,
		String action, String description)
	{
		ActivityLog actLog = new ActivityLog();
		actLog.setUserID( userID );
		actLog.setAccountID( accountID );
		actLog.setCustomerID( customerID );
		actLog.setEnergyCompanyID( energyCompanyID );
		actLog.setAction( action );
		actLog.setDescription( description );
		
		try {
			Transaction.createTransaction(Transaction.INSERT, actLog).execute();
		}
		catch (TransactionException e) {
			CTILogger.error( e.getMessage(), e );
		}
	}

}
