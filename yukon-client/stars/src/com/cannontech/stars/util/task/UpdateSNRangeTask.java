/*
 * Created on Apr 28, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
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
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventory;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UpdateSNRangeTask implements TimeConsumingTask {
	
	int status = STATUS_NOT_INIT;
	boolean isCanceled = false;
	String errorMsg = null;
	
	Integer snFrom = null;
	Integer snTo = null;
	Integer devTypeID = null;
	Integer newDevTypeID = null;
	Date recvDate = null;
	Integer voltageID = null;
	Integer companyID = null;
	Integer routeID = null;
	HttpServletRequest request = null;
	
	ArrayList hardwareSet = new ArrayList();
	int numSuccess = 0, numFailure = 0;
	int numToBeUpdated = 0;
	
	public UpdateSNRangeTask(Integer snFrom, Integer snTo, Integer devTypeID, Integer newDevTypeID,
		Date recvDate, Integer voltageID, Integer companyID, Integer routeID, HttpServletRequest request)
	{
		this.snFrom = snFrom;
		this.snTo = snTo;
		this.devTypeID = devTypeID;
		this.newDevTypeID = newDevTypeID;
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
		if (numToBeUpdated > 0) {
			String snToStr = (snTo != null)? " to " + snTo : " and above";
			if (status == STATUS_FINISHED)
				return "The SN range " + snFrom + snToStr + " has been updated successfully";
			else
				return numSuccess + " of " + numToBeUpdated + " hardwares updated";
		}
		else {
			if (status == STATUS_FINISHED)
				return "No hardware found in the given SN range";
			else
				return "Updating hardwares in inventory...";
		}
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
		int categoryID = ECUtils.getInventoryCategoryID( devTypeID.intValue(), energyCompany );
		
		status = STATUS_RUNNING;
		
		ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
		boolean showEnergyCompany = energyCompany.getChildren().size() > 0;
		boolean devTypeChanged = newDevTypeID != null && newDevTypeID.intValue() != devTypeID.intValue();
		
		ArrayList hwList = ECUtils.getLMHardwareInRange( energyCompany, devTypeID.intValue(), snFrom, snTo );
		numToBeUpdated = hwList.size();
		
		for (int i = 0; i < hwList.size(); i++) {
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hwList.get(i);
			
			if (devTypeChanged) {
				boolean hwExist = false;
				try {
					hwExist = energyCompany.searchForLMHardware( newDevTypeID.intValue(), liteHw.getManufacturerSerialNumber() ) != null;
				}
				catch (ObjectInOtherEnergyCompanyException e) {
					hwExist = true;
				}
				
				if (hwExist) {
					hardwareSet.add( liteHw );
					numFailure++;
					continue;
				}
			}
			
			try {
				com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
						new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				com.cannontech.database.db.stars.hardware.InventoryBase invDB = hardware.getInventoryBase();
				com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hardware.getLMHardwareBase();
				
				StarsLiteFactory.setLMHardwareBase( hardware, liteHw );
				if (newDevTypeID != null) {
					invDB.setCategoryID( new Integer(ECUtils.getInventoryCategoryID(newDevTypeID.intValue(), energyCompany)) );
					hwDB.setLMHardwareTypeID( newDevTypeID );
				}
				if (companyID != null)
					invDB.setInstallationCompanyID( companyID );
				if (recvDate != null)
					invDB.setReceiveDate( recvDate );
				if (voltageID != null)
					invDB.setVoltageID( voltageID );
				if (routeID != null)
					hwDB.setRouteID( routeID );
				
				hardware = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
						Transaction.createTransaction( Transaction.UPDATE, hardware ).execute();
				StarsLiteFactory.setLiteStarsLMHardware( liteHw, hardware );
				
				if (devTypeChanged && liteHw.isExtended()) {
					liteHw.updateThermostatType();
					if (liteHw.isThermostat())
						liteHw.setThermostatSettings( energyCompany.getThermostatSettings(liteHw) );
					else
						liteHw.setThermostatSettings( null );
				}
				
				if (liteHw.getAccountID() > 0) {
					StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteHw.getAccountID() );
					if (starsAcctInfo != null) {
						if (!liteHw.isExtended()) StarsLiteFactory.extendLiteInventoryBase( liteHw, energyCompany );
						
						for (int j = 0; j < starsAcctInfo.getStarsInventories().getStarsInventoryCount(); j++) {
							StarsInventory starsInv = starsAcctInfo.getStarsInventories().getStarsInventory(j);
							if (starsInv.getInventoryID() == liteHw.getInventoryID()) {
								StarsLiteFactory.setStarsInv( starsInv, liteHw, energyCompany );
								break;
							}
						}
					}
				}
				
				numSuccess++;
			}
			catch (com.cannontech.database.TransactionException e) {
				CTILogger.error( e.getMessage(), e );
				hardwareSet.add( liteHw );
				numFailure++;
			}
			
			if (isCanceled) {
				status = STATUS_CANCELED;
				return;
			}
		}
		
		String logMsg = "Serial Range:" + snFrom + ((snTo != null)? " - " + snTo : " and above")
				+ ",Old Device Type:" + YukonListFuncs.getYukonListEntry(devTypeID.intValue()).getEntryText();
		if (newDevTypeID != null)
			logMsg += ",New Device Type:" + YukonListFuncs.getYukonListEntry(newDevTypeID.intValue()).getEntryText();
		ActivityLogger.logEvent( user.getUserID(), ActivityLogActions.INVENTORY_UPDATE_RANGE, logMsg );
		
		status = STATUS_FINISHED;
		session.removeAttribute( InventoryManager.INVENTORY_SET );
		
		if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + numSuccess + " hardwares updated successfully.</span><br>" +
					"<span class='ErrorMsg'>" + numFailure + " hardwares failed (serial numbers in new device type may already exist). They are listed below:</span><br>";
			
			session.setAttribute(InventoryManager.INVENTORY_SET_DESC, resultDesc);
			session.setAttribute(InventoryManager.INVENTORY_SET, hardwareSet);
			session.setAttribute(ServletUtils.ATT_REFERRER, request.getHeader("referer"));
			session.setAttribute(ServletUtils.ATT_REDIRECT, request.getContextPath() + "/operator/Hardware/ResultSet.jsp");
		}
	}

}
