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
import com.cannontech.common.util.Pair;
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
import com.cannontech.stars.web.action.UpdateLMHardwareConfigAction;
import com.cannontech.stars.web.action.YukonSwitchCommandAction;
import com.cannontech.stars.web.servlet.InventoryManager;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;

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
	
	ArrayList hwsToConfig = null;
	boolean configNow = false;
	HttpServletRequest request = null;
	
	ArrayList hardwareSet = new ArrayList();
	int numSuccess = 0, numFailure = 0;
	int numToBeConfigured = 0;
	
	public ConfigSNRangeTask(boolean configNow, HttpServletRequest request)
	{
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
		if (configNow) {
			if (status == STATUS_FINISHED)
				return numSuccess + " hardwares have been configured successfully";
			else
				return numSuccess + " of " + numToBeConfigured + " hardwares configured";
		}
		else {
			if (status == STATUS_FINISHED)
				return numSuccess + " hardware configuration saved to batch successfully";
			else
				return numSuccess + " of " + numToBeConfigured + " hardware configuration saved to batch";
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
		
		ArrayList invToConfig = (ArrayList) session.getAttribute( InventoryManager.INVENTORY_TO_CONFIG );
		if (invToConfig == null) {
			status = STATUS_ERROR;
			errorMsg = "There is no hardware to configure";
			return;
		}
		
		status = STATUS_RUNNING;
		
		hwsToConfig = new ArrayList();
		int devTypeID = 0;
		
		for (int i = 0; i < invToConfig.size(); i++) {
			if (invToConfig.get(i) instanceof Pair) {
				devTypeID = ((Integer) ((Pair)invToConfig.get(i)).getFirst()).intValue();
				Integer[] snRange = (Integer[]) ((Pair)invToConfig.get(i)).getSecond();
				ArrayList hwsInRange = ECUtils.getLMHardwareInRange( energyCompany, devTypeID, snRange[0], snRange[1] );
				
				for (int j = 0; j < hwsInRange.size(); j++) {
					if (!hwsToConfig.contains( hwsInRange.get(j) ))
						hwsToConfig.add( hwsInRange.get(j) );
				}
			}
			else {
				devTypeID = ((LiteStarsLMHardware)invToConfig.get(i)).getLmHardwareTypeID();
				if (!hwsToConfig.contains( invToConfig.get(i) ))
					hwsToConfig.add( invToConfig.get(i) );
			}
		}
		
		numToBeConfigured = hwsToConfig.size();
		if (numToBeConfigured == 0) {
			status = STATUS_ERROR;
			errorMsg = "There is no hardware to configure";
			return;
		}
		
		StarsLMConfiguration hwConfig = null;
		String options = null;
		
		if (Boolean.valueOf( request.getParameter("UseConfig") ).booleanValue()) {
			// User has specified a new configuration
			if (request.getParameter("UseHardwareAddressing") != null) {
				hwConfig = new StarsLMConfiguration();
				try {
					InventoryManager.setStarsLMConfiguration( hwConfig, request );
				}
				catch (WebClientException e) {
					CTILogger.error( e.getMessage(), e );
					status = STATUS_ERROR;
					errorMsg = e.getMessage();
					return;
				}
			}
			else {
				int groupID = Integer.parseInt( request.getParameter("Group") );
				options = "GroupID:" + groupID;
			}
		}
		
		if (request.getParameter("Route") != null) {
			int routeID = Integer.parseInt( request.getParameter("Route") );
			if (routeID == 0) routeID = energyCompany.getDefaultRouteID();
			if (options != null)
				options += ";RouteID:" + routeID;
			else
				options = "RouteID:" + routeID;
		}
		
		SwitchCommandQueue cmdQueue = null;
		if (!configNow) {
			cmdQueue = energyCompany.getSwitchCommandQueue();
			if (cmdQueue == null) {
				status = STATUS_ERROR;
				errorMsg = "Failed to create queue for batching switch commands";
				return;
			}
		}
		
		for (int i = 0; i < hwsToConfig.size(); i++) {
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hwsToConfig.get(i);
			
			try {
				if (hwConfig != null)
					UpdateLMHardwareConfigAction.updateLMConfiguration( hwConfig, liteHw );
				
				if (configNow) {
					YukonSwitchCommandAction.sendConfigCommand(energyCompany, liteHw, true, options);
					
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
					cmd.setInventoryID( liteHw.getInventoryID() );
					cmd.setCommandType( SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE );
					cmd.setInfoString( options );
					
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
		
		String logMsg = "Serial Range:";
		for (int i = 0; i < invToConfig.size(); i++) {
			if (invToConfig.get(i) instanceof Pair) {
				Integer[] snRange = (Integer[]) ((Pair)invToConfig.get(i)).getSecond();
				logMsg += snRange[0] + " - " + snRange[1] + ",";
			}
			else {
				logMsg += ((LiteStarsLMHardware)invToConfig.get(i)).getManufacturerSerialNumber() + ",";
			}
		}
		logMsg += "Device Type:" + YukonListFuncs.getYukonListEntry(devTypeID).getEntryText();
		if (options != null) logMsg += "," + options;
		ActivityLogger.logEvent( user.getUserID(), ActivityLogActions.INVENTORY_CONFIG_RANGE, logMsg );
		
		status = STATUS_FINISHED;
		
		session.removeAttribute( InventoryManager.INVENTORY_TO_CONFIG );
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
