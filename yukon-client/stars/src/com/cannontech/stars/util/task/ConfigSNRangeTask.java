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
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.roles.operator.AdministratorRole;
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
	
	ArrayList invToConfig = null;
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
			if (status == STATUS_FINISHED && numFailure == 0) {
				if (invToConfig.size() == 1 && invToConfig.get(0) instanceof Integer[]) {
					Integer[] snRange = (Integer[]) invToConfig.get(0);
					return "The serial numbers " + snRange[1] + " to " + snRange[2] + " have been configured successfully.";
				}
				else
					return numSuccess + " hardwares have been configured successfully.";
			}
			else
				return numSuccess + " of " + numToBeConfigured + " hardwares have been configured.";
		}
		else {
			if (status == STATUS_FINISHED && numFailure == 0) {
				if (invToConfig.size() == 1 && invToConfig.get(0) instanceof Integer[]) {
					Integer[] snRange = (Integer[]) invToConfig.get(0);
					return "The serial numbers " + snRange[1] + " to " + snRange[2] + " have been saved to batch.";
				}
				else
					return numSuccess + " hardware configuration saved to batch.";
			}
			else
				return numSuccess + " of " + numToBeConfigured + " hardware configuration saved to batch.";
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
		
		invToConfig = (ArrayList) session.getAttribute( InventoryManager.SN_RANGE_TO_CONFIG );
		if (invToConfig == null) {
			status = STATUS_ERROR;
			errorMsg = "There is no hardware to configure";
			return;
		}
		
		status = STATUS_RUNNING;
		
		boolean searchMembers = AuthFuncs.checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS )
				&& energyCompany.getChildren().size() > 0;
		hwsToConfig = new ArrayList();
		
		for (int i = 0; i < invToConfig.size(); i++) {
			if (invToConfig.get(i) instanceof Integer[]) {
				Integer[] snRange = (Integer[]) invToConfig.get(i);
				
				if (!searchMembers) {
					ArrayList hwsInRange = ECUtils.getLMHardwareInRange( energyCompany, snRange[0], snRange[1], snRange[2] );
					for (int j = 0; j < hwsInRange.size(); j++) {
						if (!hwsToConfig.contains( hwsInRange.get(j) ))
							hwsToConfig.add( hwsInRange.get(j) );
					}
				}
				else {
					ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
					for (int j = 0; j < descendants.size(); j++) {
						LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(j);
						ArrayList hwsInRange = ECUtils.getLMHardwareInRange( company, snRange[0], snRange[1], snRange[2] );
						for (int k = 0; k < hwsInRange.size(); k++) {
							if (!isHardwareContained( hwsToConfig, (LiteStarsLMHardware)hwsInRange.get(k) ))
								hwsToConfig.add( new Pair(hwsInRange.get(k), company) );
						}
					}
				}
			}
			else if (invToConfig.get(i) instanceof Pair) {
				if (!isHardwareContained( hwsToConfig, (LiteStarsLMHardware)((Pair)invToConfig.get(i)).getFirst() ))
					hwsToConfig.add( invToConfig.get(i) );
			}
			else {
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
			LiteStarsLMHardware liteHw = null;
			LiteStarsEnergyCompany company = null;
			if (hwsToConfig.get(i) instanceof Pair) {
				liteHw = (LiteStarsLMHardware) ((Pair)hwsToConfig.get(i)).getFirst();
				company = (LiteStarsEnergyCompany) ((Pair)hwsToConfig.get(i)).getSecond();
			}
			else {
				liteHw = (LiteStarsLMHardware) hwsToConfig.get(i);
				company = energyCompany;
			}
			
			try {
				if (hwConfig != null)
					UpdateLMHardwareConfigAction.updateLMConfiguration( hwConfig, liteHw );
				
				if (configNow) {
					YukonSwitchCommandAction.sendConfigCommand(company, liteHw, true, options);
					
					if (liteHw.getAccountID() > 0) {
						StarsCustAccountInformation starsAcctInfo = company.getStarsCustAccountInformation( liteHw.getAccountID() );
						if (starsAcctInfo != null) {
							StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteHw, company );
							YukonSwitchCommandAction.parseResponse( starsAcctInfo, starsInv );
						}
					}
				}
				else {
					SwitchCommandQueue.SwitchCommand cmd = new SwitchCommandQueue.SwitchCommand();
					cmd.setEnergyCompanyID( company.getLiteID() );
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
				if (errorMsg == null) errorMsg = e.getMessage();
				hardwareSet.add( hwsToConfig.get(i) );
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
			if (invToConfig.get(i) instanceof Integer[]) {
				Integer[] snRange = (Integer[]) invToConfig.get(i);
				logMsg += snRange[1] + " - " + snRange[2] + ",";
			}
			else {
				Object hwObj = invToConfig.get(i);
				if (hwObj instanceof Pair) hwObj = ((Pair)hwObj).getFirst();
				logMsg += ((LiteStarsLMHardware)hwObj).getManufacturerSerialNumber() + ",";
			}
		}
		if (options != null) logMsg += options;
		ActivityLogger.logEvent( user.getUserID(), ActivityLogActions.INVENTORY_CONFIG_RANGE, logMsg );
		
		status = STATUS_FINISHED;
		
		if (numFailure == 0)
			session.removeAttribute( InventoryManager.SN_RANGE_TO_CONFIG );
		session.removeAttribute( InventoryManager.INVENTORY_SET );
		
		if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + "Configuration of " + numSuccess + " hardwares " +
					((configNow)? "sent out" : "scheduled") + " successfully.</span><br>";
			resultDesc += "<span class='ErrorMsg'>" + numFailure + " hardware(s) failed (listed below).</span><br>";
			if (errorMsg != null)
				resultDesc += "<span class='ErrorMsg'>First error message: " + errorMsg + "</span><br>";
			
			session.setAttribute(InventoryManager.INVENTORY_SET_DESC, resultDesc);
			session.setAttribute(InventoryManager.INVENTORY_SET, hardwareSet);
			session.setAttribute(ServletUtils.ATT_REDIRECT, request.getContextPath() + "/operator/Hardware/ResultSet.jsp");
		}
	}
	
	static boolean isHardwareContained(ArrayList hwList, LiteStarsLMHardware liteHw) {
		for (int i = 0; i < hwList.size(); i++) {
			Object hwObj = hwList.get(i);
			if (hwObj instanceof Pair) hwObj = ((Pair)hwObj).getFirst();
			if (hwObj.equals( liteHw )) return true;
		}
		return false;
	}

}
