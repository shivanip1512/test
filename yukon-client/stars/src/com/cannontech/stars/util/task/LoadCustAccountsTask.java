/*
 * Created on Mar 18, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.util.WebClientException;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LoadCustAccountsTask implements TimeConsumingTask {

	int status = STATUS_NOT_INIT;
	boolean isCanceled = false;
	String errorMsg = null;
	
	LiteStarsEnergyCompany energyCompany = null;
	
	int numAcctTotal = 0;
	int numAcctLoaded = 0;
	
	public LoadCustAccountsTask(LiteStarsEnergyCompany energyCompany) {
		this.energyCompany = energyCompany;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getStatus()
	 */
	public int getStatus() {
		return status;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#setStatus(int)
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#cancel()
	 */
	public void cancel() {
		if (status == STATUS_RUNNING) {
			isCanceled = true;
			status = STATUS_CANCELING;
		}
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if (numAcctTotal > 0)
			return numAcctLoaded + " of " + numAcctTotal + " customer accounts loaded";
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getErrorMsg()
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (energyCompany == null) {
			status = STATUS_ERROR;
			errorMsg = "Energy company cannot be null";
			return;
		}
		
		status = STATUS_RUNNING;
		
		try {
			Object[][] accounts = com.cannontech.database.db.stars.customer.CustomerAccount.getAllCustomerAccounts( energyCompany.getEnergyCompanyID() );
			if (accounts != null) {
				numAcctTotal = accounts.length;
				
				for (numAcctLoaded = 0; numAcctLoaded < numAcctTotal; numAcctLoaded++) {
					int accountID = ((Integer)accounts[numAcctLoaded][0]).intValue();
					if (energyCompany.getBriefCustAccountInfo(accountID, true) == null)
						throw new WebClientException( "Failed to load customer account with id=" + accountID );
					
					if (isCanceled) {
						status = STATUS_CANCELED;
						return;
					}
				}
			}
			
			energyCompany.setAccountsLoaded( true );
			status = STATUS_FINISHED;
			
			CTILogger.info( "All customer accounts loaded for energy company #" + energyCompany.getEnergyCompanyID() );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			status = STATUS_ERROR;
			
			if (e instanceof WebClientException)
				errorMsg = e.getMessage();
			else
				errorMsg = "Failed to load customer accounts";
		}
	}

}
