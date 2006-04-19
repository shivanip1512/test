/*
 * Created on Mar 18, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.stars.event.EventWorkOrder;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LoadWorkOrdersTask extends TimeConsumingTask {

	LiteStarsEnergyCompany energyCompany = null;
	
	int numOrderLoaded = 0;
	
	public LoadWorkOrdersTask(LiteStarsEnergyCompany energyCompany) {
		this.energyCompany = energyCompany;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if (status == STATUS_RUNNING) {
			if (numOrderLoaded > 0)
				return numOrderLoaded + " work orders loaded";
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
			" DateReported, OrderedBy, Description, DateScheduled, DateCompleted, ActionTaken, AdditionalOrderNumber, " +
			" AccountID, EnergyCompanyID " +
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
			
			while (rset.next()) {
				loadWorkOrder(rset);
				numOrderLoaded++;
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					return;
				}
			}
			if( stmt != null)
				stmt.close();
			
			sql = "SELECT EB.EVENTID, USERID, SYSTEMCATEGORYID, ACTIONID, EVENTTIMESTAMP, ORDERID " +
			" FROM " + com.cannontech.database.db.stars.event.EventBase.TABLE_NAME + " EB, " +
			com.cannontech.database.db.stars.event.EventWorkOrder.TABLE_NAME + " EWO, " +
			" ECToWorkOrderMapping map " + 
			" WHERE EB.EVENTID = EWO.EVENTID " +
			" AND map.EnergyCompanyID = " + energyCompany.getEnergyCompanyID() + 
			" AND MAP.WORKORDERID = EWO.ORDERID " +
			" ORDER BY EB.EVENTID, EVENTTIMESTAMP";
			
			stmt = conn.createStatement();
			rset = stmt.executeQuery( sql );
			while (rset.next()) {

				int orderID = rset.getInt(6);
				LiteWorkOrderBase liteWorkOrderBase = energyCompany.getWorkOrderBase(orderID, false);
                if( liteWorkOrderBase != null)
                {
            		EventWorkOrder eventWorkOrder = new EventWorkOrder();
            		eventWorkOrder.setEventID(new Integer(rset.getInt(1)));
            		eventWorkOrder.getEventBase().setUserID(new Integer(rset.getInt(2)));
            		eventWorkOrder.getEventBase().setSystemCategoryID(new Integer(rset.getInt(3)));
            		eventWorkOrder.getEventBase().setActionID(new Integer(rset.getInt(4)));
            		eventWorkOrder.getEventBase().setEventTimestamp( new Date(rset.getTimestamp(5).getTime() ));        		
            		eventWorkOrder.getEventWorkOrder().setWorkOrderID(new Integer(new Integer( orderID)));
    				liteWorkOrderBase.getEventWorkOrders().add(0, eventWorkOrder);
                }
                else {
                    CTILogger.error("WorkOrderID: " + orderID + " - Not loaded for EnergyCompanyID: " + energyCompany.getEnergyCompanyID().intValue());
                }
				if (isCanceled) {
					status = STATUS_CANCELED;
					return;
				}
			}
			energyCompany.setWorkOrdersLoaded( true );
			status = STATUS_FINISHED;
            rset.close();
			
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
		liteOrder.setAdditionalOrderNumber( rset.getString("AdditionalOrderNumber") );
		liteOrder.setAccountID( rset.getInt("AccountID") );
		liteOrder.setEnergyCompanyID( rset.getInt("EnergyCompanyID") );
		
		energyCompany.addWorkOrderBase( liteOrder );
	}
}