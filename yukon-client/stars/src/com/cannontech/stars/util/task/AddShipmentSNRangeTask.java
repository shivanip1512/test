/*
 * Created on Apr 27, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AddShipmentSNRangeTask extends TimeConsumingTask {

	LiteStarsEnergyCompany energyCompany = null;
	/*TODO: We have a problem if they want to bring in non-numeric serial numbers as an add.
     * 
	 */
    long snFrom = 0, snTo = 0;
	Integer devTypeID = null;
    Integer devStateID = null;
	Date recvDate = null;
	Integer voltageID = 0;
	Integer companyID = 0;
	Integer routeID = 0;
    Integer warehouseID = 0;
	HttpSession session;
	
	List<String> serialNoSet = new ArrayList<String>();
	int numSuccess = 0, numFailure = 0;
	
	public AddShipmentSNRangeTask(LiteStarsEnergyCompany energyCompany, String snFrom, String snTo, Integer devTypeID, Integer devStateID, Integer warehouseID, HttpSession session)
	{
		this.energyCompany = energyCompany;
		this.snFrom = Long.parseLong(snFrom);
		this.snTo = Long.parseLong(snTo);
		this.devTypeID = devTypeID;
        this.devStateID = devStateID;
        this.warehouseID = warehouseID;
		this.session = session;
	}

	@Override
    public String getProgressMsg() {
		long numTotal = snTo - snFrom + 1;
		if (status == STATUS_FINISHED && numFailure == 0) {
			return "The serial numbers " + snFrom + " to " + snTo + " have been added successfully.";
		}	
		return numSuccess + " of " + numTotal + " hardware entries have been added.";
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (devTypeID == null) {
			status = STATUS_ERROR;
			errorMsg = "Device type cannot be null";
			return;
		}
		
		status = STATUS_RUNNING;
		
		StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
		
		Integer categoryID = new Integer( InventoryUtils.getInventoryCategoryID(devTypeID.intValue(), energyCompany) );
		
		for (long sn = snFrom; sn <= snTo; sn++) {
			String serialNo = String.valueOf(sn);
			
			try 
            {
				com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
						new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hardware.getLMHardwareBase();
				com.cannontech.database.db.stars.hardware.InventoryBase invDB = hardware.getInventoryBase();
				
				invDB.setInstallationCompanyID( companyID );
				invDB.setCategoryID( categoryID );
                invDB.setCurrentStateID(devStateID);
				if (recvDate != null)
					invDB.setReceiveDate( recvDate );
				invDB.setVoltageID( voltageID );
				hwDB.setManufacturerSerialNumber( serialNo );
				hwDB.setLMHardwareTypeID( devTypeID );
				hwDB.setRouteID( 0 ); //DefaultRouteID for the energyCompany instead?
				hardware.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
				
				hardware = Transaction.createTransaction( Transaction.INSERT, hardware ).execute();
				
				Integer inventoryID = hardware.getLMHardwareBase().getInventoryID();
                EventUtils.logSTARSEvent(user.getUserID(), EventUtils.EVENT_CATEGORY_INVENTORY, devTypeID, inventoryID, session);
                
                if(warehouseID.intValue() > 0)
                {
                   Warehouse house = new Warehouse();
                   house.setWarehouseID(warehouseID);
                   house.setInventoryID(inventoryID);
                   Transaction.createTransaction( Transaction.ADD_PARTIAL, house ).execute();
                }
				numSuccess++;
			}
			catch (com.cannontech.database.TransactionException e) {
				CTILogger.error( e.getMessage(), e );
				serialNoSet.add( serialNo );
				numFailure++;
			}
			
			if (isCanceled) {
				status = STATUS_CANCELED;
				return;
			}
		}
		
		String logMsg = "Serial Range:" + String.valueOf(snFrom) + " - " + String.valueOf(snTo)
				+ ",Device Type:" + DaoFactory.getYukonListDao().getYukonListEntry(devTypeID.intValue()).getEntryText();

        ActivityLogger.logEvent( user.getUserID(), ActivityLogActions.INVENTORY_ADD_RANGE, logMsg );
		
		session.removeAttribute( InventoryManagerUtil.INVENTORY_SET );
		status = STATUS_FINISHED;
	}

}
