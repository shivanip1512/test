package com.cannontech.stars.web.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.SULMProgram;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsProgramSignUp;
import com.cannontech.stars.xml.serialize.StarsSULMPrograms;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareConfigResponse;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class UpdateLMHardwareConfigAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;

			StarsUpdateLMHardwareConfig updateHwConfig = new StarsUpdateLMHardwareConfig();
			updateHwConfig.setInventoryID( Integer.parseInt(req.getParameter("InvID")) );
			updateHwConfig.setSaveToBatch( req.getParameter("action").equalsIgnoreCase("SaveLMHardwareConfig") );
			updateHwConfig.setSaveConfigOnly( req.getParameter("action").equalsIgnoreCase("UpdateLMHardwareConfig") );
			
			String[] progIDs = req.getParameterValues( "ProgID" );
			String[] grpIDs = req.getParameterValues( "GroupID" );
			if (progIDs != null) {
				for (int i = 0; i < progIDs.length; i++) {
					StarsLMHardwareConfig config = new StarsLMHardwareConfig();
					config.setGroupID( Integer.parseInt(grpIDs[i]) );
					config.setProgramID( Integer.parseInt(progIDs[i]) );
					updateHwConfig.addStarsLMHardwareConfig( config );
				}
			}
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsUpdateLMHardwareConfig( updateHwConfig );
			
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
		}

		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
		StarsOperation respOper = new StarsOperation();
        
		try {
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );

			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) session.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			if (liteAcctInfo == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
        	
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
            
			StarsUpdateLMHardwareConfig updateHwConfig = reqOper.getStarsUpdateLMHardwareConfig();
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( updateHwConfig.getInventoryID(), true );
            
			StarsProgramSignUp progSignUp = new StarsProgramSignUp();
			progSignUp.setStarsSULMPrograms( new StarsSULMPrograms() );
            
			for (int i = 0; i < updateHwConfig.getStarsLMHardwareConfigCount(); i++) {
				StarsLMHardwareConfig starsConfig = updateHwConfig.getStarsLMHardwareConfig(i);
				
				SULMProgram suProg = new SULMProgram();
				suProg.setProgramID( starsConfig.getProgramID() );
				suProg.setAddressingGroupID( starsConfig.getGroupID() );
				progSignUp.getStarsSULMPrograms().addSULMProgram( suProg );
			}
            
			try {
				if (updateHwConfig.getSaveToBatch()) {
					saveLMHardwareConfig( progSignUp, liteHw, energyCompany );
					StarsSuccess success = new StarsSuccess();
					success.setDescription( "Configuration command saved to batch successfully" );
					respOper.setStarsSuccess( success );
				}
				else {
					StarsUpdateLMHardwareConfigResponse resp = updateLMHardwareConfig(
							progSignUp, !updateHwConfig.getSaveConfigOnly(), liteHw, liteAcctInfo, user.getUserID(), energyCompany );
					respOper.setStarsUpdateLMHardwareConfigResponse( resp );
				}
			}
			catch (WebClientException e) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, e.getMessage()) );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Failed to send out the hardware configuration") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			catch (Exception e2) {
				CTILogger.error( e2.getMessage(), e2 );
			}
		}

		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
		try {
			StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			if (operation.getStarsSuccess() != null) {
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, operation.getStarsSuccess().getDescription() );
				return 0;
			}
			
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			
			parseResponse( accountInfo, operation.getStarsUpdateLMHardwareConfigResponse() );
			session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Hardware configuration updated successfully" );
			
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	/**
	 * @return Hardwares that have been configured
	 */
	public static StarsUpdateLMHardwareConfigResponse updateLMHardwareConfig(StarsProgramSignUp progSignUp, boolean sendConfig, LiteStarsLMHardware liteHw,
		LiteStarsCustAccountInformation liteAcctInfo, int userID, LiteStarsEnergyCompany energyCompany) throws WebClientException
	{
		ArrayList hwsToConfig = ProgramSignUpAction.updateProgramEnrollment( progSignUp, liteAcctInfo, liteHw, energyCompany );
		
		if (!hwsToConfig.contains( liteHw ))
			hwsToConfig.add( 0, liteHw );
		
		StarsInventories starsInvs = new StarsInventories();
		
		for (int i = 0; i < hwsToConfig.size(); i++) {
			LiteStarsLMHardware lHw = (LiteStarsLMHardware) hwsToConfig.get(i);
			
			if (sendConfig) {
				// Send out config/disable command
				boolean toConfig = false;
				
				for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
					LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
					if (liteApp.getInventoryID() == lHw.getInventoryID()) {
						toConfig = true;
						break;
					}
				}
				
				if (toConfig)
					YukonSwitchCommandAction.sendConfigCommand( energyCompany, lHw, true );
				else
					YukonSwitchCommandAction.sendDisableCommand( energyCompany, lHw );
			}
			
			StarsInventory starsInv = StarsLiteFactory.createStarsInventory( lHw, energyCompany );
			starsInvs.addStarsInventory( starsInv );
		}
        
		StarsUpdateLMHardwareConfigResponse resp = new StarsUpdateLMHardwareConfigResponse();
		resp.setStarsLMPrograms( StarsLiteFactory.createStarsLMPrograms(liteAcctInfo, energyCompany) );
		resp.setStarsAppliances( StarsLiteFactory.createStarsAppliances(liteAcctInfo.getAppliances(), energyCompany) );
		resp.setStarsInventories( starsInvs );
        
		// Log activity
		String logMsg = "Serial #:" + liteHw.getManufacturerSerialNumber();
		for (int i = 0; i < hwsToConfig.size(); i++) {
			LiteStarsLMHardware lHw = (LiteStarsLMHardware) hwsToConfig.get(i);
			if (!lHw.equals( liteHw ))
				logMsg += "," + lHw.getManufacturerSerialNumber();
		}
		
		String action = (sendConfig)? ActivityLogActions.HARDWARE_CONFIGURATION_ACTION
									: ActivityLogActions.HARDWARE_SAVE_CONFIGURATION_ACTION;
		ActivityLogger.logEvent(userID, liteAcctInfo.getAccountID(), energyCompany.getLiteID(),
				liteAcctInfo.getCustomer().getCustomerID(), action, logMsg );
		
		return resp;
	}
	
	public static void saveLMHardwareConfig(StarsProgramSignUp progSignUp, LiteStarsLMHardware liteHw, LiteStarsEnergyCompany energyCompany)
		throws WebClientException
	{
		SwitchCommandQueue queue = energyCompany.getSwitchCommandQueue();
		if (queue == null)
			throw new WebClientException( "Failed to save the configuration command to batch" );
		
		if (progSignUp.getStarsSULMPrograms().getSULMProgramCount() > 0) {
			SwitchCommandQueue.SwitchCommand cmd = new SwitchCommandQueue.SwitchCommand();
			cmd.setEnergyCompanyID( energyCompany.getLiteID() );
			cmd.setAccountID( liteHw.getAccountID() );
			cmd.setInventoryID( liteHw.getInventoryID() );
			cmd.setCommandType( SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE );
			
			SULMProgram suProg = progSignUp.getStarsSULMPrograms().getSULMProgram(0);
			String infoStr = suProg.getProgramID() + "," + suProg.getAddressingGroupID();
			for (int i = 1; i < progSignUp.getStarsSULMPrograms().getSULMProgramCount(); i++) {
				suProg = progSignUp.getStarsSULMPrograms().getSULMProgram(i);
				infoStr += "," + suProg.getProgramID() + "," + suProg.getAddressingGroupID();
			}
			cmd.setInfoString( infoStr );
			
			queue.addCommand( cmd, true );
		}
		else {
			SwitchCommandQueue.SwitchCommand cmd = new SwitchCommandQueue.SwitchCommand();
			cmd.setEnergyCompanyID( energyCompany.getLiteID() );
			cmd.setAccountID( liteHw.getAccountID() );
			cmd.setInventoryID( liteHw.getInventoryID() );
			cmd.setCommandType( SwitchCommandQueue.SWITCH_COMMAND_DISABLE );
			queue.addCommand( cmd, true );
		}
	}
	
	public static void parseResponse(StarsCustAccountInformation starsAcctInfo, StarsUpdateLMHardwareConfigResponse resp) {
		if (resp.getStarsLMPrograms() != null)
			starsAcctInfo.setStarsLMPrograms( resp.getStarsLMPrograms() );
		
		if (resp.getStarsAppliances() != null)
			starsAcctInfo.setStarsAppliances( resp.getStarsAppliances() );
		
		if (resp.getStarsInventories() != null) {
			for (int i = 0; i < resp.getStarsInventories().getStarsInventoryCount(); i++) {
				StarsInventory starsInv = resp.getStarsInventories().getStarsInventory(i);
				
				StarsInventories inventories = starsAcctInfo.getStarsInventories();
				for (int j = 0; j < inventories.getStarsInventoryCount(); j++) {
					StarsInventory inv = inventories.getStarsInventory(j);
					if (inv.getInventoryID() == starsInv.getInventoryID()) {
						inventories.setStarsInventory(j, starsInv);
						break;
					}
				}
			}
		}
	}

}
