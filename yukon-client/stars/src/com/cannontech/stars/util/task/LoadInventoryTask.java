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
	
	int numInvLoaded = 0;
    
    private final int LM_HARDWARE_DBLOAD = 1;
    private final int METER_HARDWARE_DBLOAD = 2;
    private final int STANDARD_INVENTORY_DBLOAD = 3;
	
	public LoadInventoryTask(LiteStarsEnergyCompany energyCompany) {
		this.energyCompany = energyCompany;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if (status == STATUS_RUNNING) {
			if (numInvLoaded > 0)
				return numInvLoaded + " of items loaded in inventory";
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
		
		String sql_lm_event = "SELECT ce.EventID, EventTypeID, ActionID, EventDateTime, Notes, InventoryID" +
			" FROM LMCustomerEventBase ce, LMHardwareEvent he, ECToLMCustomerEventMapping map" +
			" WHERE map.EnergyCompanyID = " + energyCompany.getEnergyCompanyID() +
			" AND map.EventID = ce.EventID AND ce.EventID = he.EventID ORDER BY ce.EventID";
		
		/*String sql2 = "SELECT inv.InventoryID, AccountID, InstallationCompanyID, CategoryID," +
			" ReceiveDate, InstallDate, RemoveDate, AlternateTrackingNumber, VoltageID," +
			" Notes, DeviceID, DeviceLabel, CurrentStateID, ManufacturerSerialNumber, LMHardwareTypeID," +
			" RouteID, ConfigurationID, MeterNumber, MeterTypeID " +
			" FROM InventoryBase inv LEFT OUTER JOIN LMHardwareBase hw ON inv.InventoryID = hw.InventoryID " +
			" LEFT OUTER JOIN MeterHardwareBase mhb ON inv.InventoryID = mhb.InventoryID, " + 
			" ECToInventoryMapping map" +
			" WHERE map.EnergyCompanyID = " + energyCompany.getEnergyCompanyID() +
			" AND map.InventoryID >= 0 AND map.InventoryID = inv.InventoryID";*/
			
        String sql_lmHardware = "SELECT lmb.InventoryID, ManufacturerSerialNumber, LMHardwareTypeID," +
            " RouteID, ConfigurationID " + 
            " FROM LMHardwareBase lmb, ECToInventoryMapping map " +
            " WHERE map.EnergyCompanyID = " + energyCompany.getEnergyCompanyID() +
            " AND map.InventoryID >= 0 AND map.InventoryID = lmb.InventoryID";
        
		String sql_meterHardware = "SELECT mhb.InventoryID, MeterNumber, MeterTypeID " +
    		" FROM MeterHardwareBase mhb, ECToInventoryMapping map " +
    		" WHERE map.EnergyCompanyID = " + energyCompany.getEnergyCompanyID() +
    		" AND map.InventoryID >= 0 AND map.InventoryID = mhb.InventoryID";

        String sql_inventoryBase = "SELECT inv.InventoryID, AccountID, InstallationCompanyID, CategoryID," +
            " ReceiveDate, InstallDate, RemoveDate, AlternateTrackingNumber, VoltageID," +
            " Notes, DeviceID, DeviceLabel, CurrentStateID " +
            " FROM InventoryBase inv, ECToInventoryMapping map " +
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
			
            /*
             * Grab events first
             */
			stmt = conn.createStatement();
			java.sql.ResultSet rset = stmt.executeQuery( sql_lm_event );
			
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
            
            /*
             * Now do LMHardwareBase, these will be put in inventory map
             */
           	rset = stmt.executeQuery( sql_lmHardware );
			
            while (rset.next()) 
            {
				loadInventory(rset, LM_HARDWARE_DBLOAD, null );
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					return;
				}
			}
            
            rset.close();
            
            /*
             * Now do MeterHardwareBase and put them in the inventory map
             */
            rset = stmt.executeQuery( sql_meterHardware );
            
            while (rset.next()) 
            {
                loadInventory(rset, METER_HARDWARE_DBLOAD, null);
                
                if (isCanceled) {
                    status = STATUS_CANCELED;
                    return;
                }
            }
            
            rset.close();
            
            /*
             * Now make sure everybody both in the inventory map and 
             * MCTs, etc, get their inventoryBase information.  Either will be
             * populated into the map or the current map value will be changed.
             */
            rset = stmt.executeQuery( sql_inventoryBase );
            
            while (rset.next()) 
            {
                loadInventory(rset, STANDARD_INVENTORY_DBLOAD, invEventMap);
                numInvLoaded++;
                
                if (isCanceled) {
                    status = STATUS_CANCELED;
                    return;
                }
            }
            rset.close();
            
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
    
	/*
	private void loadInventory(java.sql.ResultSet rset, Hashtable invEventMap) throws java.sql.SQLException {
		int invID = rset.getInt( "InventoryID" );
		
		if (energyCompany.getInventoryBrief( invID, false ) != null)
			return;	// device already loaded
		
		LiteInventoryBase liteInv = null;
		
		String serialNo = rset.getString( "ManufacturerSerialNumber" );
		String meterNumber = rset.getString( "MeterNumber" );
		if (serialNo != null) {
			// This is a LM hardware
			liteInv = new LiteStarsLMHardware();
			((LiteStarsLMHardware)liteInv).setManufacturerSerialNumber( serialNo );
			((LiteStarsLMHardware)liteInv).setLmHardwareTypeID( rset.getInt("LMHardwareTypeID") );
			((LiteStarsLMHardware)liteInv).setRouteID( rset.getInt("RouteID") );
			((LiteStarsLMHardware)liteInv).setConfigurationID( rset.getInt("ConfigurationID") );
		}
		else if( meterNumber != null)
		{
			//This is a MeterHardwareBase Inventory object
			liteInv = new LiteMeterHardwareBase();
			((LiteMeterHardwareBase)liteInv).setMeterNumber( meterNumber );
			((LiteMeterHardwareBase)liteInv).setMeterTypeID( rset.getInt("MeterTypeID"));
		}
		else {
			// This is a MCT or other kinds of devices
			liteInv = new LiteInventoryBase();
		}
		
		liteInv.setInventoryID( invID );
		liteInv.setAccountID( rset.getInt("AccountID") );
		liteInv.setInstallationCompanyID( rset.getInt("InstallationCompanyID") );
		liteInv.setCategoryID( rset.getInt("CategoryID") );
		if(rset.getTimestamp("ReceiveDate") != null)
            liteInv.setReceiveDate( rset.getTimestamp("ReceiveDate").getTime() );
		if(rset.getTimestamp("InstallDate") != null)
            liteInv.setInstallDate( rset.getTimestamp("InstallDate").getTime() );
        if(rset.getTimestamp("RemoveDate") != null)
            liteInv.setRemoveDate( rset.getTimestamp("RemoveDate").getTime() );
		liteInv.setAlternateTrackingNumber( rset.getString("AlternateTrackingNumber") );
		liteInv.setVoltageID( rset.getInt("VoltageID") );
		liteInv.setNotes( rset.getString("Notes") );
		liteInv.setDeviceID( rset.getInt("DeviceID") );
		liteInv.setDeviceLabel( rset.getString("DeviceLabel") );
        liteInv.setCurrentStateID( rset.getInt("CurrentStateID") );
		
		ArrayList events = (ArrayList) invEventMap.get( new Integer(invID) );
		if (events != null) liteInv.setInventoryHistory( events );
		liteInv.updateDeviceStatus();
		
		energyCompany.addInventory( liteInv );
	}
    */
    
    private void loadInventory(java.sql.ResultSet rset, int dbLoadType, Hashtable invEventMap) throws java.sql.SQLException {
        int invID = rset.getInt( "InventoryID" );
        
        LiteInventoryBase liteInv = energyCompany.getInventoryFromMap(invID);

        /*
         * As long as loadInventory is only ever called in the run of this task, which should certainly
         * be the case since it is a private method, then we can assume that the order of resultsets
         * entering this method will be as expected: LM first, then meter, then from the regular 
         * inventory query.
         */
        if ( dbLoadType == LM_HARDWARE_DBLOAD ) 
        {
            // This is a LM hardware
            if(liteInv == null)
            {
                liteInv = new LiteStarsLMHardware();
                liteInv.setInventoryID(invID);
                energyCompany.addInventory( liteInv );
            }
            ((LiteStarsLMHardware)liteInv).setManufacturerSerialNumber( rset.getString( "ManufacturerSerialNumber" ) );
            ((LiteStarsLMHardware)liteInv).setLmHardwareTypeID( rset.getInt("LMHardwareTypeID") );
            ((LiteStarsLMHardware)liteInv).setRouteID( rset.getInt("RouteID") );
            ((LiteStarsLMHardware)liteInv).setConfigurationID( rset.getInt("ConfigurationID") );
        }
        else if( dbLoadType == METER_HARDWARE_DBLOAD )
        {
            //This is a MeterHardwareBase Inventory object
            if(liteInv == null)
            {
                liteInv = new LiteMeterHardwareBase();
                liteInv.setInventoryID(invID);
                energyCompany.addInventory( liteInv );
            }
            ((LiteMeterHardwareBase)liteInv).setMeterNumber( rset.getString( "MeterNumber" ) );
            ((LiteMeterHardwareBase)liteInv).setMeterTypeID( rset.getInt("MeterTypeID"));
        }
        else if( dbLoadType == STANDARD_INVENTORY_DBLOAD )
        {
            /*
             * --Now we want to flesh out the regular inventory base info
             * since we have already probably loaded the LMHardware and Meter specific info
             * --MCT or other kinds of devices will also be handled here
             * --only do the events now that we are getting the InventoryBase settings themselves
             */
            if(liteInv == null)
            {
                liteInv = new LiteInventoryBase();
                liteInv.setInventoryID(invID);
                energyCompany.addInventory( liteInv );
            }
                
            liteInv.setInventoryID( invID );
            liteInv.setAccountID( rset.getInt("AccountID") );
            liteInv.setInstallationCompanyID( rset.getInt("InstallationCompanyID") );
            liteInv.setCategoryID( rset.getInt("CategoryID") );
            if(rset.getTimestamp("ReceiveDate") != null)
                liteInv.setReceiveDate( rset.getTimestamp("ReceiveDate").getTime() );
            if(rset.getTimestamp("InstallDate") != null)
                liteInv.setInstallDate( rset.getTimestamp("InstallDate").getTime() );
            if(rset.getTimestamp("RemoveDate") != null)
                liteInv.setRemoveDate( rset.getTimestamp("RemoveDate").getTime() );
            liteInv.setAlternateTrackingNumber( rset.getString("AlternateTrackingNumber") );
            liteInv.setVoltageID( rset.getInt("VoltageID") );
            liteInv.setNotes( rset.getString("Notes") );
            liteInv.setDeviceID( rset.getInt("DeviceID") );
            liteInv.setDeviceLabel( rset.getString("DeviceLabel") );
            liteInv.setCurrentStateID( rset.getInt("CurrentStateID") );
            
            ArrayList events = (ArrayList) invEventMap.get( new Integer(invID) );
            if (events != null) liteInv.setInventoryHistory( events );
            liteInv.updateDeviceStatus();
        }
     }

}
