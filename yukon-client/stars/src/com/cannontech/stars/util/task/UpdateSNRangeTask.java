/*
 * Created on Apr 28, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ECUtils;
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
	
	String snFrom = null;
	String snTo = null;
	Integer devTypeID = null;
	Integer newDevTypeID = null;
	Date recvDate = null;
	Integer companyID = null;
	Integer voltageID = null;
	HttpServletRequest request = null;
	
	ArrayList hardwareSet = new ArrayList();
	int numSuccess = 0, numFailure = 0;
	int numToBeUpdated = 0;
	
	public UpdateSNRangeTask(String snFrom, String snTo, Integer devTypeID, Integer newDevTypeID,
		Date recvDate, Integer companyID, Integer voltageID, HttpServletRequest request)
	{
		this.snFrom = snFrom;
		this.snTo = snTo;
		this.devTypeID = devTypeID;
		this.newDevTypeID = newDevTypeID;
		this.recvDate = recvDate;
		this.companyID = companyID;
		this.voltageID = voltageID;
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
			if (status == STATUS_FINISHED)
				return "The range " + snFrom + " to " + snTo + " has been updated successfully";
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
		HttpSession session = request.getSession(false);
		StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
		
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		int categoryID = ECUtils.getInventoryCategoryID( devTypeID.intValue(), energyCompany );
		
		status = STATUS_RUNNING;
		
		TreeMap snTable = com.cannontech.database.db.stars.hardware.LMHardwareBase.searchBySNRange(
				devTypeID.intValue(), snFrom, snTo, user.getEnergyCompanyID() );
		if (snTable == null) {
			status = STATUS_ERROR;
			errorMsg = "Failed to find hardwares in the given SN range";
			return;
		}
		
		numToBeUpdated = snTable.size();
		
		java.util.Iterator it = snTable.values().iterator();
		while (it.hasNext()) {
			Integer invID = (Integer) it.next();
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventoryBrief( invID.intValue(), true );
			
			try {
				com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
						new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				com.cannontech.database.db.stars.hardware.InventoryBase invDB = hardware.getInventoryBase();
				
				if (newDevTypeID != null) {
					StarsLiteFactory.setLMHardwareBase( hardware, liteHw );
					hardware.getInventoryBase().setCategoryID(
							new Integer(ECUtils.getInventoryCategoryID(newDevTypeID.intValue(), energyCompany)) );
					hardware.getLMHardwareBase().setLMHardwareTypeID( newDevTypeID );
				}
				else {
					StarsLiteFactory.setInventoryBase( invDB, liteHw );
				}
				
				if (companyID != null)
					invDB.setInstallationCompanyID( companyID );
				if (recvDate != null)
					invDB.setReceiveDate( recvDate );
				if (voltageID != null)
					invDB.setVoltageID( voltageID );
				
				if (newDevTypeID != null) {
					hardware = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
							Transaction.createTransaction( Transaction.UPDATE, hardware ).execute();
					
					StarsLiteFactory.setLiteStarsLMHardware( liteHw, hardware );
					if (liteHw.isExtended()) {
						liteHw.updateThermostatType();
						if (liteHw.isThermostat())
							liteHw.setThermostatSettings( energyCompany.getThermostatSettings(liteHw) );
						else
							liteHw.setThermostatSettings( null );
					}
				}
				else {
					invDB = (com.cannontech.database.db.stars.hardware.InventoryBase)
							Transaction.createTransaction( Transaction.UPDATE, invDB ).execute();
					StarsLiteFactory.setLiteInventoryBase( liteHw, invDB );
				}
				
				if (liteHw.getAccountID() > 0) {
					StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteHw.getAccountID() );
					if (starsAcctInfo != null) {
						if (!liteHw.isExtended()) StarsLiteFactory.extendLiteInventoryBase( liteHw, energyCompany );
						
						for (int i = 0; i < starsAcctInfo.getStarsInventories().getStarsInventoryCount(); i++) {
							StarsInventory starsInv = starsAcctInfo.getStarsInventories().getStarsInventory(i);
							if (starsInv.getInventoryID() == invID.intValue()) {
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
		
		status = STATUS_FINISHED;
		session.removeAttribute( InventoryManager.INVENTORY_SET );
		
		if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + numSuccess + " hardwares updated successfully.</span><br>" +
					"<span class='ErrorMsg'>" + numFailure + " hardwares failed. They are listed below:</span><br>";
			
			session.setAttribute(InventoryManager.INVENTORY_SET_DESC, resultDesc);
			session.setAttribute(InventoryManager.INVENTORY_SET, hardwareSet);
			session.setAttribute(ServletUtils.ATT_REFERRER, request.getHeader("referer"));
			session.setAttribute(ServletUtils.ATT_REDIRECT, request.getContextPath() + "/operator/Hardware/ResultSet.jsp");
		}
	}

}
