/*
 * Created on Apr 28, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.YukonSwitchCommandAction;
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
public class ConfigSNRangeTask implements TimeConsumingTask {
	
	int status = STATUS_NOT_INIT;
	boolean isCanceled = false;
	String errorMsg = null;
	
	String snFrom = null;
	String snTo = null;
	Integer devTypeID = null;
	boolean configNow = false;
	HttpServletRequest request = null;
	
	ArrayList hardwareSet = new ArrayList();
	int numSuccess = 0, numFailure = 0;
	int numToBeConfigured = 0;
	
	public ConfigSNRangeTask(String snFrom, String snTo, Integer devTypeID,
		boolean configNow, HttpServletRequest request)
	{
		this.snFrom = snFrom;
		this.snTo = snTo;
		this.devTypeID = devTypeID;
		this.configNow = configNow;
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
		if (numToBeConfigured > 0) {
			if (configNow) {
				if (status == STATUS_FINISHED)
					return "The range " + snFrom + " to " + snTo + " has been configured successfully";
				else
					return numSuccess + " of " + numToBeConfigured + " hardwares configured";
			}
			else {
				if (status == STATUS_FINISHED)
					return "Configuration for the range " + snFrom + " to " + snTo + " has been saved to batch successfully";
				else
					return numSuccess + " of " + numToBeConfigured + " hardware configuration saved to batch";
			}
		}
		else {
			if (status == STATUS_FINISHED)
				return "No hardware found in the given SN range";
			else
				return "Configuring hardwares in inventory...";
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
		
		java.util.TreeMap snTable = com.cannontech.database.db.stars.hardware.LMHardwareBase.searchBySNRange(
				devTypeID.intValue(), snFrom, snTo, user.getEnergyCompanyID() );
		if (snTable == null) {
			status = STATUS_ERROR;
			errorMsg = "Failed to find hardwares in the given SN range";
			return;
		}
		
		numToBeConfigured = snTable.size();
		
		SwitchCommandQueue cmdQueue = null;
		if (!configNow) {
			cmdQueue = energyCompany.getSwitchCommandQueue();
			if (cmdQueue == null) {
				status = STATUS_ERROR;
				errorMsg = "Failed to create queue for batching switch commands";
				return;
			}
		}
		
		java.util.Iterator it = snTable.values().iterator();
		while (it.hasNext()) {
			Integer invID = (Integer) it.next();
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory(invID.intValue(), true);
			
			try {
				if (configNow) {
					YukonSwitchCommandAction.sendConfigCommand(energyCompany, liteHw, true);
					
					if (liteHw.getAccountID() > 0) {
						StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteHw.getAccountID() );
						if (starsAcctInfo != null) {
							StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteHw, energyCompany );
							YukonSwitchCommandAction.parseResponse( starsAcctInfo, starsInv );
						}
					}
				}
				else {
					SwitchCommandQueue.SwitchCommand cmd = new SwitchCommandQueue.SwitchCommand();
					cmd.setEnergyCompanyID( user.getEnergyCompanyID() );
					cmd.setAccountID( liteHw.getAccountID() );
					cmd.setInventoryID( invID.intValue() );
					cmd.setCommandType( SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE );
					
					cmdQueue.addCommand( cmd, false );
				}
				
				numSuccess++;
			}
			catch (WebClientException e) {
				CTILogger.error( e.getMessage() , e );
				hardwareSet.add( liteHw );
				numFailure++;
			}
			
			if (isCanceled) {
				status = STATUS_CANCELED;
				return;
			}
		}
		
		if (!configNow) cmdQueue.addCommand( null, true );
		
		String logMsg = "Serial Range:" + snFrom + " - " + snTo
				+ ",Device Type:" + YukonListFuncs.getYukonListEntry(devTypeID.intValue()).getEntryText();
		ActivityLogger.logEvent( user.getUserID(), ActivityLogActions.INVENTORY_CONFIG_RANGE, logMsg );
		
		status = STATUS_FINISHED;
		session.removeAttribute( InventoryManager.INVENTORY_SET );
		
		if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + "Configuration of " + numSuccess + " hardwares " +
					((configNow)? "sent out" : "scheduled") + " successfully.</span><br>";
			resultDesc += "<span class='ErrorMsg'>" + numFailure + " hardware(s) failed. They are listed below:</span><br>";
			
			session.setAttribute(InventoryManager.INVENTORY_SET_DESC, resultDesc);
			session.setAttribute(InventoryManager.INVENTORY_SET, hardwareSet);
			session.setAttribute(ServletUtils.ATT_REFERRER, request.getHeader("referer"));
			session.setAttribute(ServletUtils.ATT_REDIRECT, request.getContextPath() + "/operator/Hardware/ResultSet.jsp");
		}
	}

}
