/*
 * Created on Apr 27, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.Date;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.Pair;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.InventoryManager;
import com.cannontech.stars.web.servlet.SOAPServer;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AddSNRangeTask implements TimeConsumingTask {

	int status = STATUS_NOT_INIT;
	boolean isCanceled = false;
	String errorMsg = null;
	
	int snFrom = 0, snTo = 0;
	Integer devTypeID = null;
	Date recvDate = null;
	Integer voltageID = null;
	Integer companyID = null;
	Integer routeID = null;
	HttpServletRequest request = null;
	
	ArrayList hardwareSet = new ArrayList();
	ArrayList serialNoSet = new ArrayList();
	int numSuccess = 0, numFailure = 0;
	
	public AddSNRangeTask(int snFrom, int snTo, Integer devTypeID, Date recvDate,
		Integer voltageID, Integer companyID, Integer routeID, HttpServletRequest request)
	{
		this.snFrom = snFrom;
		this.snTo = snTo;
		this.devTypeID = devTypeID;
		this.recvDate = recvDate;
		this.voltageID = voltageID;
		this.companyID = companyID;
		this.routeID = routeID;
		this.request = request;
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
		int numTotal = snTo - snFrom + 1;
		if (status == STATUS_FINISHED && numFailure == 0)
			return "The serial numbers " + snFrom + " to " + snTo + " have been added successfully.";
		else
			return numSuccess + " of " + numTotal + " hardwares have been added.";
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
		if (devTypeID == null) {
			status = STATUS_ERROR;
			errorMsg = "Device type cannot be null";
			return;
		}
		
		HttpSession session = request.getSession(false);
		StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
		
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		Integer categoryID = new Integer( ECUtils.getInventoryCategoryID(devTypeID.intValue(), energyCompany) );
		
		status = STATUS_RUNNING;
		
		for (int sn = snFrom; sn <= snTo; sn++) {
			String serialNo = String.valueOf(sn);
			
			try {
				LiteStarsLMHardware existingHw = energyCompany.searchForLMHardware( devTypeID.intValue(), serialNo );
				if (existingHw != null) {
					hardwareSet.add( existingHw );
					numFailure++;
					continue;
				}
			}
			catch (ObjectInOtherEnergyCompanyException e) {
				hardwareSet.add( new Pair(e.getObject(), e.getEnergyCompany()) );
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
				if (recvDate != null)
					invDB.setReceiveDate( recvDate );
				invDB.setVoltageID( voltageID );
				hwDB.setManufacturerSerialNumber( serialNo );
				hwDB.setLMHardwareTypeID( devTypeID );
				hwDB.setRouteID( routeID );
				hardware.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				
				hardware = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
						Transaction.createTransaction( Transaction.INSERT, hardware ).execute();
				
				LiteStarsLMHardware liteHw = new LiteStarsLMHardware();
				StarsLiteFactory.setLiteStarsLMHardware( liteHw, hardware );
				energyCompany.addInventory( liteHw );
				
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
				+ ",Device Type:" + YukonListFuncs.getYukonListEntry(devTypeID.intValue()).getEntryText();
		ActivityLogger.logEvent( user.getUserID(), ActivityLogActions.INVENTORY_ADD_RANGE, logMsg );
		
		session.removeAttribute( InventoryManager.INVENTORY_SET );
		status = STATUS_FINISHED;
		
		if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + numSuccess + " hardwares added to inventory successfully.</span><br>" +
					"<span class='ErrorMsg'>" + numFailure + " hardwares failed (listed below).<br>" +
					"Those serial numbers may already exist in the inventory.</span><br>";
			if (serialNoSet.size() > 0) {
				resultDesc += "<br><table width='100' cellspacing='0' cellpadding='0' border='0' align='center' class='TableCell'>";
				for (int i = 0; i < serialNoSet.size(); i++) {
					String serialNo = (String) serialNoSet.get(i);
					resultDesc += "<tr><td align='center'>" + serialNo + "</td></tr>";
				}
				resultDesc += "</table><br>";
			}
			
			session.setAttribute(InventoryManager.INVENTORY_SET_DESC, resultDesc);
			if (hardwareSet.size() > 0)
				session.setAttribute(InventoryManager.INVENTORY_SET, hardwareSet);
			session.setAttribute(ServletUtils.ATT_REDIRECT, request.getContextPath() + "/operator/Hardware/ResultSet.jsp");
		}
	}

}
