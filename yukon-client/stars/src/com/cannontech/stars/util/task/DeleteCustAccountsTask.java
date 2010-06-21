/*
 * Created on Jan 29, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.AccountAction;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeleteCustAccountsTask extends TimeConsumingTask {
	
	StarsYukonUser user = null;
	int[] accountIDs = null;
	int numAcctDeleted = 0;
	
	public DeleteCustAccountsTask(StarsYukonUser user, int[] accountIDs) {
		this.user = user;
		this.accountIDs = accountIDs;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if (accountIDs != null) {
			if (status == STATUS_FINISHED)
				return numAcctDeleted + " customer accounts deleted successfully";
			else
				return numAcctDeleted + " of " + accountIDs.length + " customer accounts deleted";
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		if (accountIDs == null) {
			status = STATUS_ERROR;
			errorMsg = "Account IDs cannot be null";
		}
		else {
			status = STATUS_RUNNING;
			LiteStarsCustAccountInformation liteAcctInfo = null;
			
			try {
				for (numAcctDeleted = 0; numAcctDeleted < accountIDs.length; numAcctDeleted++) {
					liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[numAcctDeleted], true );
					AccountAction.deleteCustomerAccount( liteAcctInfo, energyCompany );
					
					if (isCanceled) {
						status = STATUS_CANCELED;
						return;
					}
				}
				
				status = STATUS_FINISHED;
			}
			catch (Exception e) {
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
				
				status = STATUS_ERROR;
				if (liteAcctInfo != null)
					errorMsg = "Failed to delete customer account id = " + liteAcctInfo.getAccountID();
				else
					errorMsg = "Failed to delete customer accounts";
			}
		}
	}

}
