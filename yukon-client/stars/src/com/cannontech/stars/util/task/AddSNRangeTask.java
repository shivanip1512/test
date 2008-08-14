/*
 * Created on Apr 27, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.InventoryBean;
import com.cannontech.stars.web.util.InventoryManagerUtil;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AddSNRangeTask extends TimeConsumingTask {

	LiteStarsEnergyCompany energyCompany = null;
	int snFrom = 0, snTo = 0;
	Integer devTypeID = null;
    Integer devStateID = null;
	Date recvDate = null;
	Integer voltageID = null;
	Integer companyID = null;
	Integer routeID = null;
	HttpServletRequest request = null;
	
	List<LiteStarsLMHardware> hardwareSet = new ArrayList<LiteStarsLMHardware>();
	List<String> serialNoSet = new ArrayList<String>();
	int numSuccess = 0, numFailure = 0;
	
	public AddSNRangeTask(LiteStarsEnergyCompany energyCompany, int snFrom, int snTo, Integer devTypeID, Integer devStateID,
		Date recvDate, Integer voltageID, Integer companyID, Integer routeID, HttpServletRequest request)
	{
		this.energyCompany = energyCompany;
		this.snFrom = snFrom;
		this.snTo = snTo;
		this.devTypeID = devTypeID;
        this.devStateID = devStateID;
		this.recvDate = recvDate;
		this.voltageID = voltageID;
		this.companyID = companyID;
		this.routeID = routeID;
		this.request = request;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	@Override
	public String getProgressMsg() {
		int numTotal = snTo - snFrom + 1;
		if (status == STATUS_FINISHED && numFailure == 0)
			return "The serial numbers " + snFrom + " to " + snTo + " have been added successfully.";
		else
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
		
		HttpSession session = request.getSession(false);
		StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
		
		Integer categoryID = new Integer( InventoryUtils.getInventoryCategoryID(devTypeID.intValue(), energyCompany) );
		
        /*
         * Let's cheat a little and use the inventoryBean filtering to look for serial range.
         */
        InventoryBean iBean = (InventoryBean) session.getAttribute("inventoryBean");
        if(iBean == null)
        {
            session.setAttribute("inventoryBean", new InventoryBean());
            iBean = (InventoryBean) session.getAttribute("inventoryBean");
        }
        
        List<FilterWrapper> tempList = new ArrayList<FilterWrapper>();
        String serialStart = Integer.toString(snFrom);
        String serialEnd = Integer.toString(snTo);
        String devType = Integer.toString(devTypeID);
        tempList.add(new FilterWrapper(String.valueOf(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE), devType, devType));
        tempList.add(new FilterWrapper(String.valueOf(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MAX), serialEnd, serialEnd));
        tempList.add(new FilterWrapper(String.valueOf(YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SERIAL_RANGE_MIN), serialStart, serialStart));
        iBean.setFilterByList(tempList);
        
        iBean.setShipmentCheck(true);
        List<LiteInventoryBase> found = iBean.getLimitedHardwareList();
        LiteInventoryBase liteInv = null;
        Map<String, LiteStarsLMHardware> foundMap = new HashMap<String, LiteStarsLMHardware>(found.size());
        for(int j = 0; j < found.size(); j++)
        {
        	liteInv = iBean.getInventoryList().get(j);
            
            /*
             * if this needs to do meters, will have to add a clause
             */
            if(liteInv != null && liteInv instanceof LiteStarsLMHardware)
                foundMap.put(((LiteStarsLMHardware)liteInv).getManufacturerSerialNumber(), ((LiteStarsLMHardware)liteInv));
        }
        
		for (int sn = snFrom; sn <= snTo; sn++) {
			String serialNo = String.valueOf(sn);
			
			LiteStarsLMHardware existingHw = foundMap.get(serialNo);
			if (existingHw != null) 
            {
				hardwareSet.add( existingHw );
				numFailure++;
				continue;
			}
			
			try {
				com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
						new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hardware.getLMHardwareBase();
				com.cannontech.database.db.stars.hardware.InventoryBase invDB = hardware.getInventoryBase();
				
				invDB.setInstallationCompanyID( companyID );
				invDB.setCategoryID( categoryID );
                invDB.setCurrentStateID( devStateID );
				if (recvDate != null)
					invDB.setReceiveDate( recvDate );
				invDB.setVoltageID( voltageID );
				hwDB.setManufacturerSerialNumber( serialNo );
				hwDB.setLMHardwareTypeID( devTypeID );
				hwDB.setRouteID( routeID );
				hardware.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				
				hardware = Transaction.createTransaction( Transaction.INSERT, hardware ).execute();
				
				LiteStarsLMHardware liteHw = new LiteStarsLMHardware();
				StarsLiteFactory.setLiteStarsLMHardware( liteHw, hardware );
				energyCompany.addInventory( liteHw );
				
                EventUtils.logSTARSEvent(user.getUserID(), EventUtils.EVENT_CATEGORY_INVENTORY, devStateID, liteHw.getInventoryID(), session);
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
			if (hardwareSet.size() > 0)
				session.setAttribute(InventoryManagerUtil.INVENTORY_SET, hardwareSet);
			session.setAttribute(ServletUtils.ATT_REDIRECT, request.getContextPath() + "/operator/Hardware/ResultSet.jsp");
			if (request.getParameter(ServletUtils.CONFIRM_ON_MESSAGE_PAGE) != null)
				session.setAttribute(ServletUtils.ATT_REFERRER, session.getAttribute(ServletUtils.ATT_MSG_PAGE_REFERRER));
		}
	}

}
