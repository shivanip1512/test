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
public class LoadWorkOrdersTask implements TimeConsumingTask {

	int status = STATUS_NOT_INIT;
	boolean isCanceled = false;
	String errorMsg = null;
	
	LiteStarsEnergyCompany energyCompany = null;
	
	int numOrderTotal = 0;
	int numOrderLoaded = 0;
	
	public LoadWorkOrdersTask(LiteStarsEnergyCompany energyCompany) {
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
		if (numOrderTotal > 0)
			return numOrderLoaded + " of " + numOrderTotal + " work orders loaded";
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
			String sql = "SELECT WorkOrderID FROM ECToWorkOrderMapping WHERE EnergyCompanyID=" + energyCompany.getEnergyCompanyID();
			SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
			stmt.execute();
			
			numOrderTotal = stmt.getRowCount();
			
			for (int numOrderLoaded = 0; numOrderLoaded < numOrderTotal; numOrderLoaded++) {
				int orderID = ((java.math.BigDecimal) stmt.getRow(numOrderLoaded)[0]).intValue();
				if (energyCompany.getWorkOrderBase(orderID, true) == null)
					throw new WebClientException( "Failed to load work order with id=" + orderID );
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					return;
				}
			}
			
			energyCompany.setWorkOrdersLoaded( true );
			status = STATUS_FINISHED;
			
			CTILogger.info( "All work orders loaded for energy company #" + energyCompany.getEnergyCompanyID() );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			status = STATUS_ERROR;
			
			if (e instanceof WebClientException)
				errorMsg = e.getMessage();
			else
				errorMsg = "Failed to load work orders";
		}
	}

}
