package com.cannontech.stars.web.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteLMConfiguration;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.SULMProgram;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsProgramSignUp;
import com.cannontech.stars.xml.serialize.StarsSULMPrograms;
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
			updateHwConfig.setSaveToBatch( Boolean.valueOf(req.getParameter("SaveToBatch")).booleanValue() );
			updateHwConfig.setSaveConfigOnly( Boolean.valueOf(req.getParameter("SaveConfigOnly")).booleanValue() );
			
			String[] progIDs = req.getParameterValues( "ProgID" );
			String[] grpIDs = req.getParameterValues( "GroupID" );
			String[] loadNos = req.getParameterValues( "LoadNo" );
			
			if (progIDs != null) {
				for (int i = 0; i < progIDs.length; i++) {
					int loadNo = Integer.parseInt( loadNos[i] );
					for (int j = 0; j < updateHwConfig.getStarsLMHardwareConfigCount(); j++) {
						if (updateHwConfig.getStarsLMHardwareConfig(j).getLoadNumber() == loadNo)
							throw new WebClientException( "Load #" + loadNo + " has been selected more than once" );
					}
					
					StarsLMHardwareConfig config = new StarsLMHardwareConfig();
					config.setGroupID( Integer.parseInt(grpIDs[i]) );
					config.setProgramID( Integer.parseInt(progIDs[i]) );
					config.setLoadNumber( loadNo );
					updateHwConfig.addStarsLMHardwareConfig( config );
				}
			}
			
			if (req.getParameter("UseHardwareAddressing") != null) {
				StarsLMConfiguration config = new StarsLMConfiguration();
				InventoryManagerUtil.setStarsLMConfiguration( config, req );
				updateHwConfig.setStarsLMConfiguration( config );
			}
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsUpdateLMHardwareConfig( updateHwConfig );
			
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (WebClientException e) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
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
        	
			LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
            
			StarsUpdateLMHardwareConfig updateHwConfig = reqOper.getStarsUpdateLMHardwareConfig();
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( updateHwConfig.getInventoryID(), true );
            
			try {
				StarsUpdateLMHardwareConfigResponse resp = updateLMHardwareConfig(
						updateHwConfig, liteHw, user.getUserID(), energyCompany );
				respOper.setStarsUpdateLMHardwareConfigResponse( resp );
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
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Failed to update the hardware configuration.") );
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
			
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			
			parseResponse( accountInfo, operation.getStarsUpdateLMHardwareConfigResponse() );
			
			StarsUpdateLMHardwareConfig updateCfg = SOAPUtil.parseSOAPMsgForOperation( reqMsg ).getStarsUpdateLMHardwareConfig();
			if (updateCfg.getSaveConfigOnly())
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Hardware configuration updated successfully." );
			else if (updateCfg.getSaveToBatch())
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Configuration command saved to batch successfully." );
			else
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Configuration command has been sent out successfully." );
			
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	/**
	 * Update the hardware addressing information
	 */
	public static void updateLMConfiguration(StarsLMConfiguration starsHwConfig, LiteStarsLMHardware liteHw, LiteStarsEnergyCompany energyCompany)
		throws WebClientException
	{
		com.cannontech.database.data.stars.hardware.LMConfigurationBase config =
				new com.cannontech.database.data.stars.hardware.LMConfigurationBase();
		com.cannontech.database.db.stars.hardware.LMConfigurationBase configDB = config.getLMConfigurationBase();
		
		if (starsHwConfig.getColdLoadPickup() != null) {
			if (starsHwConfig.getColdLoadPickup().length() > 0)
				configDB.setColdLoadPickup( starsHwConfig.getColdLoadPickup() );
			else
				configDB.setColdLoadPickup( CtiUtilities.STRING_NONE );
		}
		if (starsHwConfig.getTamperDetect() != null) {
			if (starsHwConfig.getTamperDetect().length() > 0)
				configDB.setTamperDetect( starsHwConfig.getTamperDetect() );
			else
				configDB.setTamperDetect( CtiUtilities.STRING_NONE );
		}
		
		if (starsHwConfig.getSA205() != null) {
			com.cannontech.database.db.stars.hardware.LMConfigurationSA205 sa205 =
					new com.cannontech.database.db.stars.hardware.LMConfigurationSA205();
			sa205.setSlot1( new Integer(starsHwConfig.getSA205().getSlot1()) );
			sa205.setSlot2( new Integer(starsHwConfig.getSA205().getSlot2()) );
			sa205.setSlot3( new Integer(starsHwConfig.getSA205().getSlot3()) );
			sa205.setSlot4( new Integer(starsHwConfig.getSA205().getSlot4()) );
			sa205.setSlot5( new Integer(starsHwConfig.getSA205().getSlot5()) );
			sa205.setSlot6( new Integer(starsHwConfig.getSA205().getSlot6()) );
			config.setSA205( sa205 );
		}
		else if (starsHwConfig.getSA305() != null) {
			com.cannontech.database.db.stars.hardware.LMConfigurationSA305 sa305 =
					new com.cannontech.database.db.stars.hardware.LMConfigurationSA305();
			sa305.setUtility( new Integer(starsHwConfig.getSA305().getUtility()) );
			sa305.setGroupAddress( new Integer(starsHwConfig.getSA305().getGroup()) );
			sa305.setDivision( new Integer(starsHwConfig.getSA305().getDivision()) );
			sa305.setSubstation( new Integer(starsHwConfig.getSA305().getSubstation()) );
			sa305.setRateFamily( new Integer(starsHwConfig.getSA305().getRateFamily()) );
			sa305.setRateMember( new Integer(starsHwConfig.getSA305().getRateMember()) );
			sa305.setRateHierarchy( new Integer(starsHwConfig.getSA305().getRateHierarchy()) );
			config.setSA305( sa305 );
		}
		else if (starsHwConfig.getVersaCom() != null) {
			com.cannontech.database.db.stars.hardware.LMConfigurationVersaCom vcom =
					new com.cannontech.database.db.stars.hardware.LMConfigurationVersaCom();
			vcom.setUtilityID( new Integer(starsHwConfig.getVersaCom().getUtility()) );
			vcom.setSection( new Integer(starsHwConfig.getVersaCom().getSection()) );
			vcom.setClassAddress( new Integer(starsHwConfig.getVersaCom().getClassAddress()) );
			vcom.setDivisionAddress( new Integer(starsHwConfig.getVersaCom().getDivision()) );
			config.setVersaCom( vcom );
		}
		else if (starsHwConfig.getExpressCom() != null) {
			com.cannontech.database.db.stars.hardware.LMConfigurationExpressCom xcom =
					new com.cannontech.database.db.stars.hardware.LMConfigurationExpressCom();
			xcom.setServiceProvider( new Integer(starsHwConfig.getExpressCom().getServiceProvider()) );
			xcom.setGEO( new Integer(starsHwConfig.getExpressCom().getGEO()) );
			xcom.setSubstation( new Integer(starsHwConfig.getExpressCom().getSubstation()) );
			xcom.setFeeder( new Integer(starsHwConfig.getExpressCom().getFeeder()) );
			xcom.setZip( new Integer(starsHwConfig.getExpressCom().getZip()) );
			xcom.setUserAddress( new Integer(starsHwConfig.getExpressCom().getUserAddress()) );
			xcom.setProgram( starsHwConfig.getExpressCom().getProgram() );
			xcom.setSplinter( starsHwConfig.getExpressCom().getSplinter() );
			config.setExpressCom( xcom );
		}
		
		try {
			if (liteHw.getConfigurationID() == 0) {
				config = (com.cannontech.database.data.stars.hardware.LMConfigurationBase)
						Transaction.createTransaction( Transaction.INSERT, config ).execute();
				
				com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
						new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hw.getLMHardwareBase();
				StarsLiteFactory.setLMHardwareBase( hw, liteHw );
				hwDB.setConfigurationID( config.getLMConfigurationBase().getConfigurationID() );
				
				hwDB = (com.cannontech.database.db.stars.hardware.LMHardwareBase)
						Transaction.createTransaction( Transaction.UPDATE, hwDB ).execute();
				
				liteHw.setConfigurationID( hwDB.getConfigurationID().intValue() );
				LiteLMConfiguration liteCfg = new LiteLMConfiguration();
				StarsLiteFactory.setLiteLMConfiguration( liteCfg, config );
				liteHw.setLMConfiguration( liteCfg );
			}
			else {
				config.setConfigurationID( new Integer(liteHw.getConfigurationID()) );
				
				// Get the hardware configuration if it is not retrived yet
				if (!liteHw.isExtended())
					StarsLiteFactory.extendLiteInventoryBase( liteHw, energyCompany );
				
				// Check to see if the configuration is in both the parent and the child table
				if (config.getSA205() != null && liteHw.getLMConfiguration().getSA205() == null) {
					Transaction.createTransaction( Transaction.INSERT, config.getSA205() ).execute();
					Transaction.createTransaction( Transaction.UPDATE, configDB ).execute();
				}
				else if (config.getSA305() != null && liteHw.getLMConfiguration().getSA305() == null) {
					Transaction.createTransaction( Transaction.INSERT, config.getSA305() ).execute();
					Transaction.createTransaction( Transaction.UPDATE, configDB ).execute();
				}
				else if (config.getVersaCom() != null && liteHw.getLMConfiguration().getVersaCom() == null) {
					Transaction.createTransaction( Transaction.INSERT, config.getVersaCom() ).execute();
					Transaction.createTransaction( Transaction.UPDATE, configDB ).execute();
				}
				else if (config.getExpressCom() != null && liteHw.getLMConfiguration().getExpressCom() == null) {
					Transaction.createTransaction( Transaction.INSERT, config.getExpressCom() ).execute();
					Transaction.createTransaction( Transaction.UPDATE, configDB ).execute();
				}
				else {
					config = (com.cannontech.database.data.stars.hardware.LMConfigurationBase)
							Transaction.createTransaction( Transaction.UPDATE, config ).execute();
				}
				
				StarsLiteFactory.setLiteLMConfiguration( liteHw.getLMConfiguration(), config );
			}
		}
		catch (TransactionException e) {
			CTILogger.error( e.getMessage(), e );
			throw new WebClientException( "Failed to update the hardware addressing tables" );
		}
	}
	
	/**
	 * @return Hardwares that have been configured
	 */
	public static StarsUpdateLMHardwareConfigResponse updateLMHardwareConfig(StarsUpdateLMHardwareConfig updateHwConfig,
		LiteStarsLMHardware liteHw, int userID, LiteStarsEnergyCompany energyCompany) throws WebClientException
	{
		LiteStarsCustAccountInformation liteAcctInfo = null;
		ArrayList hwsToConfig = null;
		
		if (liteHw.getAccountID() > 0) {
			liteAcctInfo = energyCompany.getCustAccountInformation( liteHw.getAccountID(), true );
			
			StarsProgramSignUp progSignUp = new StarsProgramSignUp();
			progSignUp.setStarsSULMPrograms( new StarsSULMPrograms() );
			
			for (int i = 0; i < updateHwConfig.getStarsLMHardwareConfigCount(); i++) {
				StarsLMHardwareConfig starsConfig = updateHwConfig.getStarsLMHardwareConfig(i);
				
				SULMProgram suProg = new SULMProgram();
				suProg.setProgramID( starsConfig.getProgramID() );
				suProg.setAddressingGroupID( starsConfig.getGroupID() );
				suProg.setLoadNumber( starsConfig.getLoadNumber() );
				progSignUp.getStarsSULMPrograms().addSULMProgram( suProg );
			}
			
			hwsToConfig = ProgramSignUpAction.updateProgramEnrollment( progSignUp, liteAcctInfo, liteHw, energyCompany );
			
			if (!hwsToConfig.contains( liteHw ))
				hwsToConfig.add( 0, liteHw );
		}
		
		if (updateHwConfig.getStarsLMConfiguration() != null) {
			updateLMConfiguration( updateHwConfig.getStarsLMConfiguration(), liteHw, energyCompany );
			
			hwsToConfig = new ArrayList();
			hwsToConfig.add( liteHw );
		}
		
		StarsInventories starsInvs = new StarsInventories();
		boolean disabled = false;
		
		String trackHwAddr = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING );
		boolean useHardwareAddressing = Boolean.valueOf( trackHwAddr ).booleanValue();
		
		for (int i = 0; i < hwsToConfig.size(); i++) {
			LiteStarsLMHardware lHw = (LiteStarsLMHardware) hwsToConfig.get(i);
			
			if (!updateHwConfig.getSaveConfigOnly()) {
				boolean toConfig = true;
				if (liteAcctInfo != null && !useHardwareAddressing)
					toConfig = isToConfig(lHw, liteAcctInfo);
				if (lHw.equals( liteHw )) disabled = !toConfig;
				
				if (updateHwConfig.getSaveToBatch()) {
					String commandType = (toConfig)?
							SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE : SwitchCommandQueue.SWITCH_COMMAND_DISABLE;
					saveSwitchCommand( lHw, commandType, energyCompany );
				}
				else {
					if (toConfig)
						YukonSwitchCommandAction.sendConfigCommand( energyCompany, lHw, true, null );
					else
						YukonSwitchCommandAction.sendDisableCommand( energyCompany, lHw, null );
				}
			}
			
			if (liteAcctInfo != null) {
				StarsInventory starsInv = StarsLiteFactory.createStarsInventory( lHw, energyCompany );
				starsInvs.addStarsInventory( starsInv );
			}
		}
        
		// Log activity
		String logMsg = "Serial #:" + liteHw.getManufacturerSerialNumber();
		if (!disabled) {
			for (int i = 0; i < hwsToConfig.size(); i++) {
				LiteStarsLMHardware lHw = (LiteStarsLMHardware) hwsToConfig.get(i);
				if (!lHw.equals( liteHw ))
					logMsg += "," + lHw.getManufacturerSerialNumber();
			}
		}
		
		String action = null;
		if (updateHwConfig.getSaveConfigOnly())
			action = ActivityLogActions.HARDWARE_CONFIGURATION_ACTION_SAVE_ONLY;
		else if (disabled)
			action = ActivityLogActions.HARDWARE_DISABLE_ACTION;
		else
			action = ActivityLogActions.HARDWARE_CONFIGURATION_ACTION;
		 
		int customerID = (liteAcctInfo != null)? liteAcctInfo.getCustomer().getCustomerID() : 0;
		ActivityLogger.logEvent(userID, liteHw.getAccountID(), energyCompany.getLiteID(), customerID, action, logMsg );
	    
		if (liteAcctInfo != null) {
			StarsUpdateLMHardwareConfigResponse resp = new StarsUpdateLMHardwareConfigResponse();
			resp.setStarsInventories( starsInvs );
			resp.setStarsLMPrograms( StarsLiteFactory.createStarsLMPrograms(liteAcctInfo, energyCompany) );
			resp.setStarsAppliances( StarsLiteFactory.createStarsAppliances(liteAcctInfo.getAppliances(), energyCompany) );
			return resp;
		}
		
		return null;
	}
	
	public static boolean isToConfig(LiteStarsLMHardware liteHw, LiteStarsCustAccountInformation liteAcctInfo) {
		for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
			LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
			if (liteApp.getInventoryID() == liteHw.getInventoryID())
				return true;
		}
		
		return false;
	}
	
	public static void saveSwitchCommand(LiteStarsLMHardware liteHw, String commandType,
		LiteStarsEnergyCompany energyCompany) throws WebClientException
	{
		SwitchCommandQueue.SwitchCommand cmd = new SwitchCommandQueue.SwitchCommand();
		cmd.setEnergyCompanyID( energyCompany.getLiteID() );
		cmd.setAccountID( liteHw.getAccountID() );
		cmd.setInventoryID( liteHw.getInventoryID() );
		cmd.setCommandType( commandType );
		
		SwitchCommandQueue.getInstance().addCommand( cmd, true );
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
