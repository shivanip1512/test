/*
 * Created on Mar 18, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.stars.*;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LoadWorkOrdersTask extends TimeConsumingTask {

	LiteStarsEnergyCompany energyCompany = null;
	
	int numOrderTotal = 0;
	int numOrderLoaded = 0;
	
	public LoadWorkOrdersTask(LiteStarsEnergyCompany energyCompany) {
		this.energyCompany = energyCompany;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if (status == STATUS_RUNNING) {
			if (numOrderTotal > 0)
				return numOrderLoaded + " of " + numOrderTotal + " work orders loaded";
			else
				return "Preparing for loading the work orders...";
		}
		return null;
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
		
		String sql = "SELECT OrderID, OrderNumber, WorkTypeID, CurrentStateID, ServiceCompanyID," +
			" DateReported, OrderedBy, Description, DateScheduled, DateCompleted, ActionTaken, AccountID" +
			" FROM WorkOrderBase wo, ECToWorkOrderMapping map" +
			" WHERE map.EnergyCompanyID = " + energyCompany.getEnergyCompanyID() +
			" AND map.WorkOrderID = wo.OrderID";
		
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		
		try {
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			if (conn == null) {
				CTILogger.error( getClass() + ": failed to get database connection" );
				return;
			}
			
			stmt = conn.createStatement();
			java.sql.ResultSet rset = stmt.executeQuery( sql );
			numOrderTotal = rset.getMetaData().getColumnCount();
			
			while (rset.next()) {
				loadWorkOrder(rset);
				numOrderLoaded++;
				
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
			errorMsg = "Failed to load work orders";
		}
		finally {
			try {
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {
				CTILogger.error( e.getMessage(), e );
			}
		}
	}
	
	private void loadWorkOrder(java.sql.ResultSet rset) throws java.sql.SQLException {
		int orderID = rset.getInt( "OrderID" );
		
		if (energyCompany.getWorkOrderBase( orderID, false ) != null)
			return;	// work order already loaded
		
		LiteWorkOrderBase liteOrder = new LiteWorkOrderBase();
		liteOrder.setOrderID( orderID );
		liteOrder.setOrderNumber( rset.getString("OrderNumber") );
		liteOrder.setWorkTypeID( rset.getInt("WorkTypeID") );
		liteOrder.setCurrentStateID( rset.getInt("CurrentStateID") );
		liteOrder.setServiceCompanyID( rset.getInt("ServiceCompanyID") );
		liteOrder.setDateReported( rset.getTimestamp("DateReported").getTime() );
		liteOrder.setOrderedBy( rset.getString("OrderedBy") );
		liteOrder.setDescription( rset.getString("Description") );
		liteOrder.setDateScheduled( rset.getTimestamp("DateScheduled").getTime() );
		liteOrder.setDateCompleted( rset.getTimestamp("DateCompleted").getTime() );
		liteOrder.setActionTaken( rset.getString("ActionTaken") );
		liteOrder.setAccountID( rset.getInt("AccountID") );
		
		energyCompany.addWorkOrderBase( liteOrder );
	}

}
