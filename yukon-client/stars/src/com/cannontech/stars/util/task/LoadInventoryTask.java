/*
 * Created on Mar 18, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.util.WebClientException;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LoadInventoryTask implements TimeConsumingTask {

	int status = STATUS_NOT_INIT;
	boolean isCanceled = false;
	String errorMsg = null;
	
	LiteStarsEnergyCompany energyCompany = null;
	
	int numInvTotal = 0;
	int numInvLoaded = 0;
	
	public LoadInventoryTask(LiteStarsEnergyCompany energyCompany) {
		this.energyCompany = energyCompany;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getStatus()
	 */
	public int getStatus() {
		return status;
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
		if (numInvTotal > 0)
			return numInvLoaded + " of " + numInvTotal + " hardwares loaded in inventory";
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
			String sql = "SELECT InventoryID FROM ECToInventoryMapping WHERE EnergyCompanyID = "
					+ energyCompany.getEnergyCompanyID() + " AND InventoryID >= 0";
			SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
			stmt.execute();
			
			numInvTotal = stmt.getRowCount();
			for (numInvLoaded = 0; numInvLoaded < numInvTotal; numInvLoaded++) {
				int invID = ((java.math.BigDecimal) stmt.getRow(numInvLoaded)[0]).intValue();
				if (energyCompany.getInventoryBrief(invID, false) != null) continue;
				
				if (energyCompany.loadInventory(invID) == null)
					throw new WebClientException( "Failed to load inventory with id=" + invID );
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					return;
				}
			}
			
			status = STATUS_FINISHED;
			CTILogger.info( "All inventory loaded for energy company #" + energyCompany.getEnergyCompanyID() );
		}
		catch (Exception e) {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			status = STATUS_ERROR;
			
			if (e instanceof WebClientException)
				errorMsg = e.getMessage();
			else
				errorMsg = "Failed to load inventory";
		}
	}

}
