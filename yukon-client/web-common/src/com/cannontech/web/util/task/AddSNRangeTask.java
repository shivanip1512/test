package com.cannontech.web.util.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.util.YukonListEntryHelper;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.google.common.collect.Lists;

public class AddSNRangeTask extends TimeConsumingTask {

	private LiteStarsEnergyCompany energyCompany = null;
	private long snFrom = 0;
	private long snTo = 0;
	private Integer devTypeID = null;
	private Integer devStateID = null;
	private Date recvDate = null;
	private Integer voltageID = null;
	private Integer companyID = null;
	private Integer routeID = null;
	private final boolean confirmOnMessagePage;
	private final String redirect;
	private final HttpSession session;
	
	private final List<LiteLmHardwareBase> hardwareSet = new ArrayList<LiteLmHardwareBase>();
	private final List<String> serialNoSet = new ArrayList<String>();
	private int numSuccess = 0;
	private int numFailure = 0;
	
	public AddSNRangeTask(LiteStarsEnergyCompany energyCompany, long snFrom, long snTo, Integer devTypeID, Integer devStateID,
		Date recvDate, Integer voltageID, Integer companyID, Integer routeID, boolean confirmOnMessagePage, 
		    String redirect, HttpSession session) {
		this.energyCompany = energyCompany;
		this.snFrom = snFrom;
		this.snTo = snTo;
		this.devTypeID = devTypeID;
        this.devStateID = devStateID;
		this.recvDate = recvDate;
		this.voltageID = voltageID;
		this.companyID = companyID;
		this.routeID = routeID;
		this.confirmOnMessagePage = confirmOnMessagePage;
		this.redirect = redirect;
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
        
		
        // Get any existing hardware in the serial number range and create a map keyed on serial number
		int deviceTypeDefinitionId = 
			YukonListEntryHelper.getYukonDefinitionId(
					energyCompany, 
					YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, 
					devTypeID);
        StarsSearchDao starsSearchDao = YukonSpringHook.getBean("starsSearchDao", StarsSearchDao.class);
        List<LiteLmHardwareBase> existingHardware = null;
        try {
            List<YukonEnergyCompany> ecList = Lists.newArrayList();
            ecList.add(energyCompany);
            existingHardware = starsSearchDao.searchLMHardwareBySerialNumberRange(snFrom,
                                                                                  snTo,
                                                                                  deviceTypeDefinitionId,
                                                                                  ecList);           
        } catch (PersistenceException e){
            status = STATUS_ERROR;
            errorMsg = e.getMessage();
            return;
        }
        
        Map<String, LiteLmHardwareBase> foundMap = new HashMap<String, LiteLmHardwareBase>();
        for(LiteLmHardwareBase hardware : existingHardware) {
        	foundMap.put(hardware.getManufacturerSerialNumber(), hardware);
        }
        
        // Create a piece of hardware for each sn in the range
		for (long sn = snFrom; sn <= snTo; sn++) {
			String serialNo = String.valueOf(sn);
			
			LiteLmHardwareBase existingHw = foundMap.get(serialNo);
			if (existingHw != null) {
				hardwareSet.add( existingHw );
				numFailure++;
				continue;
			}
			
			try {
				com.cannontech.stars.database.data.hardware.LMHardwareBase hardware =
						new com.cannontech.stars.database.data.hardware.LMHardwareBase();
				com.cannontech.stars.database.db.hardware.LMHardwareBase hwDB = hardware.getLMHardwareBase();
				com.cannontech.stars.database.db.hardware.InventoryBase invDB = hardware.getInventoryBase();
				
				invDB.setInstallationCompanyID( companyID );
				invDB.setCategoryID( categoryID );
                invDB.setCurrentStateID( devStateID );
				if (recvDate != null) {
					invDB.setReceiveDate( recvDate );
				}
				invDB.setVoltageID( voltageID );
				hwDB.setManufacturerSerialNumber( serialNo );
				hwDB.setLMHardwareTypeID( devTypeID );
				hwDB.setRouteID( routeID );
				hardware.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
				
				hardware = Transaction.createTransaction( Transaction.INSERT, hardware ).execute();
				
				Integer inventoryID = hardware.getLMHardwareBase().getInventoryID();
                EventUtils.logSTARSEvent(user.getUserID(), EventUtils.EVENT_CATEGORY_INVENTORY, devStateID, inventoryID, session);
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
		
		if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + numSuccess + " hardware entries added to inventory successfully.</span><br>" +
					"<span class='ErrorMsg'>" + numFailure + " hardware entries failed (listed below).<br>" +
					"Those serial numbers may already exist in the inventory.</span><br>";
			if (serialNoSet.size() > 0) {
				resultDesc += "<br><table width='100' cellspacing='0' cellpadding='0' border='0' align='center' class='TableCell'>";
				for (int i = 0; i < serialNoSet.size(); i++) {
					String serialNo = serialNoSet.get(i);
					resultDesc += "<tr><td align='center'>" + serialNo + "</td></tr>";
				}
				resultDesc += "</table><br>";
			}
			
			session.setAttribute(InventoryManagerUtil.INVENTORY_SET_DESC, resultDesc);
			if (hardwareSet.size() > 0) {
				session.setAttribute(InventoryManagerUtil.INVENTORY_SET, hardwareSet);
			}
			session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
			if (confirmOnMessagePage) {
				session.setAttribute(ServletUtils.ATT_REFERRER, session.getAttribute(ServletUtils.ATT_MSG_PAGE_REFERRER));
			}
		}
	}

}
