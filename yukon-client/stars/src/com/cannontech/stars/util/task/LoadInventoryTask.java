/*
 * Created on Mar 18, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.Hashtable;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.util.WebClientException;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LoadInventoryTask extends TimeConsumingTask {

	LiteStarsEnergyCompany energyCompany = null;
	
	int numInvTotal = 0;
	int numInvLoaded = 0;
	
	public LoadInventoryTask(LiteStarsEnergyCompany energyCompany) {
		this.energyCompany = energyCompany;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if (status == STATUS_RUNNING) {
			if (numInvTotal > 0)
				return numInvLoaded + " of " + numInvTotal + " devices loaded in inventory";
			else
				return "Preparing for loading the inventory...";
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
		
		String sql = "SELECT ce.EventID, EventTypeID, ActionID, EventDateTime, Notes, InventoryID" +
			" FROM LMCustomerEventBase ce, LMHardwareEvent he, ECToLMCustomerEventMapping map" +
			" WHERE map.EnergyCompanyID = " + energyCompany.getEnergyCompanyID() +
			" AND map.EventID = ce.EventID AND ce.EventID = he.EventID ORDER BY ce.EventID";
		
		String sql2 = "SELECT inv.InventoryID, AccountID, InstallationCompanyID, CategoryID," +
			" ReceiveDate, InstallDate, RemoveDate, AlternateTrackingNumber, VoltageID," +
			" Notes, DeviceID, DeviceLabel, ManufacturerSerialNumber, LMHardwareTypeID," +
			" RouteID, ConfigurationID" +
			" FROM InventoryBase inv LEFT OUTER JOIN LMHardwareBase hw ON inv.InventoryID = hw.InventoryID," +
			" ECToInventoryMapping map" +
			" WHERE map.EnergyCompanyID = " + energyCompany.getEnergyCompanyID() +
			" AND map.InventoryID >= 0 AND map.InventoryID = inv.InventoryID";
		
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
			
			Hashtable invEventMap = new Hashtable();
			while (rset.next()) {
				Integer invID = new Integer( rset.getInt("InventoryID") );
				
				ArrayList events = (ArrayList) invEventMap.get( invID );
				if (events == null) {
					events = new ArrayList();
					invEventMap.put( invID, events );
				}
				
				LiteLMHardwareEvent liteEvent = new LiteLMHardwareEvent();
				liteEvent.setEventID( rset.getInt("EventID") );
				liteEvent.setEventTypeID( rset.getInt("EventTypeID") );
				liteEvent.setActionID( rset.getInt("ActionID") );
				liteEvent.setEventDateTime( rset.getTimestamp("EventDateTime").getTime() );
				liteEvent.setNotes( rset.getString("Notes") );
				liteEvent.setInventoryID( invID.intValue() );
				events.add( liteEvent );
			}
			rset.close();
			
			rset = stmt.executeQuery( sql2 );
			numInvTotal = rset.getMetaData().getColumnCount();
			
			while (rset.next()) {
				loadInventory(rset, invEventMap);
				numInvLoaded++;
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					return;
				}
			}
			
			energyCompany.setInventoryLoaded( true );
			status = STATUS_FINISHED;
			
			CTILogger.info( "All inventory loaded for energy company #" + energyCompany.getEnergyCompanyID() );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			status = STATUS_ERROR;
			
			if (e instanceof WebClientException)
				errorMsg = e.getMessage();
			else
				errorMsg = "Failed to load inventory";
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
	
	private void loadInventory(java.sql.ResultSet rset, Hashtable invEventMap) throws java.sql.SQLException {
		int invID = rset.getInt( "InventoryID" );
		
		if (energyCompany.getInventoryBrief( invID, false ) != null)
			return;	// device already loaded
		
		LiteInventoryBase liteInv = null;
		
		String serialNo = rset.getString( "ManufacturerSerialNumber" );
		if (serialNo != null) {
			// This is a LM hardware
			liteInv = new LiteStarsLMHardware();
			((LiteStarsLMHardware)liteInv).setManufacturerSerialNumber( serialNo );
			((LiteStarsLMHardware)liteInv).setLmHardwareTypeID( rset.getInt("LMHardwareTypeID") );
			((LiteStarsLMHardware)liteInv).setRouteID( rset.getInt("RouteID") );
			((LiteStarsLMHardware)liteInv).setConfigurationID( rset.getInt("ConfigurationID") );
		}
		else {
			// This is a MCT or other kinds of devices
			liteInv = new LiteInventoryBase();
		}
		
		liteInv.setInventoryID( invID );
		liteInv.setAccountID( rset.getInt("AccountID") );
		liteInv.setInstallationCompanyID( rset.getInt("InstallationCompanyID") );
		liteInv.setCategoryID( rset.getInt("CategoryID") );
		liteInv.setReceiveDate( rset.getTimestamp("ReceiveDate").getTime() );
		liteInv.setInstallDate( rset.getTimestamp("InstallDate").getTime() );
		liteInv.setRemoveDate( rset.getTimestamp("RemoveDate").getTime() );
		liteInv.setAlternateTrackingNumber( rset.getString("AlternateTrackingNumber") );
		liteInv.setVoltageID( rset.getInt("VoltageID") );
		liteInv.setNotes( rset.getString("Notes") );
		liteInv.setDeviceID( rset.getInt("DeviceID") );
		liteInv.setDeviceLabel( rset.getString("DeviceLabel") );
		
		ArrayList events = (ArrayList) invEventMap.get( new Integer(invID) );
		if (events != null) liteInv.setInventoryHistory( events );
		liteInv.updateDeviceStatus();
		
		energyCompany.addInventory( liteInv );
	}

}
