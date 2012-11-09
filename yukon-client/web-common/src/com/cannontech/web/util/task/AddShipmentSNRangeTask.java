package com.cannontech.web.util.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.db.hardware.Warehouse;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;

public class AddShipmentSNRangeTask extends TimeConsumingTask {

    private LiteStarsEnergyCompany energyCompany = null;
	/*TODO: We have a problem if they want to bring in non-numeric serial numbers as an add.*/
    private long snFrom = 0;
    private long snTo = 0;
    private Integer devTypeID = null;
	private Integer devStateID = null;
    private final Date recvDate = null;
	private final Integer voltageID = 0;
	private final Integer companyID = 0;
	private Integer warehouseID = 0;
    private final HttpSession session;
	
    private final List<String> serialNoSet = new ArrayList<String>();
    private int numSuccess = 0;
    private int numFailure = 0;
	
	public AddShipmentSNRangeTask(LiteStarsEnergyCompany energyCompany, String snFrom, String snTo, 
	                              Integer devTypeID, Integer devStateID, Integer warehouseID, HttpSession session) {
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
	@Override
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
			
			try  {
				com.cannontech.stars.database.data.hardware.LMHardwareBase hardware =
						new com.cannontech.stars.database.data.hardware.LMHardwareBase();
				com.cannontech.stars.database.db.hardware.LMHardwareBase hwDB = hardware.getLMHardwareBase();
				com.cannontech.stars.database.db.hardware.InventoryBase invDB = hardware.getInventoryBase();
				
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
                
                if(warehouseID.intValue() > 0) {
                   Warehouse house = new Warehouse();
                   house.setWarehouseID(warehouseID);
                   house.setInventoryID(inventoryID);
                   Transaction.createTransaction( Transaction.ADD_PARTIAL, house ).execute();
                }
				numSuccess++;
			} catch (com.cannontech.database.TransactionException e) {
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
