package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteLMConfiguration;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.ExpressCom;
import com.cannontech.stars.xml.serialize.SA205;
import com.cannontech.stars.xml.serialize.SA305;
import com.cannontech.stars.xml.serialize.SASimple;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareConfigResponse;
import com.cannontech.stars.xml.serialize.VersaCom;
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
					/*if (loadNo > 0) {
						for (int j = 0; j < updateHwConfig.getStarsLMHardwareConfigCount(); j++) {
							if (updateHwConfig.getStarsLMHardwareConfig(j).getLoadNumber() == loadNo)
								throw new WebClientException( "Relay #" + loadNo + " has been selected more than once" );
						}
					}*/
					
					StarsLMHardwareConfig config = new StarsLMHardwareConfig();
					config.setGroupID( Integer.parseInt(grpIDs[i]) );
					config.setProgramID( Integer.parseInt(progIDs[i]) );
					config.setLoadNumber( loadNo );
					updateHwConfig.addStarsLMHardwareConfig( config );
				}
			}
			
			if (req.getParameter("UseHardwareAddressing") != null) {
				StarsLMConfiguration config = new StarsLMConfiguration();
				setStarsLMConfiguration( config, req );
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
            
			StarsInventoryBaseDao starsInventoryBaseDao = 
				YukonSpringHook.getBean("starsInventoryBaseDao", StarsInventoryBaseDao.class);
			StarsUpdateLMHardwareConfig updateHwConfig = reqOper.getStarsUpdateLMHardwareConfig();
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(updateHwConfig.getInventoryID());
            
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
		
		if (starsHwConfig.getExpressCom() != null) {
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
		else if (starsHwConfig.getVersaCom() != null) {
			com.cannontech.database.db.stars.hardware.LMConfigurationVersaCom vcom =
					new com.cannontech.database.db.stars.hardware.LMConfigurationVersaCom();
			vcom.setUtilityID( new Integer(starsHwConfig.getVersaCom().getUtility()) );
			vcom.setSection( new Integer(starsHwConfig.getVersaCom().getSection()) );
			vcom.setClassAddress( new Integer(starsHwConfig.getVersaCom().getClassAddress()) );
			vcom.setDivisionAddress( new Integer(starsHwConfig.getVersaCom().getDivision()) );
			config.setVersaCom( vcom );
		}
		else if (starsHwConfig.getSA205() != null) {
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
		else if (starsHwConfig.getSASimple() != null) {
			com.cannontech.database.db.stars.hardware.LMConfigurationSASimple simple =
					new com.cannontech.database.db.stars.hardware.LMConfigurationSASimple();
			simple.setOperationalAddress( starsHwConfig.getSASimple().getOperationalAddress() );
			config.setSASimple( simple );
		}
		
		try {
			if (liteHw.getConfigurationID() == 0) {
				config = Transaction.createTransaction( Transaction.INSERT, config ).execute();
				
				com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
						new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hw.getLMHardwareBase();
				StarsLiteFactory.setLMHardwareBase( hw, liteHw );
				hwDB.setConfigurationID( config.getLMConfigurationBase().getConfigurationID() );
				
				hwDB = Transaction.createTransaction( Transaction.UPDATE, hwDB ).execute();
				
				liteHw.setConfigurationID( hwDB.getConfigurationID().intValue() );
				LiteLMConfiguration liteCfg = new LiteLMConfiguration();
				StarsLiteFactory.setLiteLMConfiguration( liteCfg, config );
				liteHw.setLMConfiguration( liteCfg );
			}
			else {
				config.setConfigurationID( new Integer(liteHw.getConfigurationID()) );
				
				// Check to see if the configuration is in both the parent and the child table
				if (config.getExpressCom() != null && liteHw.getLMConfiguration().getExpressCom() == null) {
					Transaction.createTransaction( Transaction.INSERT, config.getExpressCom() ).execute();
					Transaction.createTransaction( Transaction.UPDATE, configDB ).execute();
				}
				else if (config.getVersaCom() != null && liteHw.getLMConfiguration().getVersaCom() == null) {
					Transaction.createTransaction( Transaction.INSERT, config.getVersaCom() ).execute();
					Transaction.createTransaction( Transaction.UPDATE, configDB ).execute();
				}
				else if (config.getSA205() != null && liteHw.getLMConfiguration().getSA205() == null) {
					Transaction.createTransaction( Transaction.INSERT, config.getSA205() ).execute();
					Transaction.createTransaction( Transaction.UPDATE, configDB ).execute();
				}
				else if (config.getSA305() != null && liteHw.getLMConfiguration().getSA305() == null) {
					Transaction.createTransaction( Transaction.INSERT, config.getSA305() ).execute();
					Transaction.createTransaction( Transaction.UPDATE, configDB ).execute();
				}
				else if (config.getSASimple() != null && liteHw.getLMConfiguration().getSASimple() == null) {
					Transaction.createTransaction( Transaction.INSERT, config.getSASimple() ).execute();
					Transaction.createTransaction( Transaction.UPDATE, configDB ).execute();
				}
				else {
					config = Transaction.createTransaction( Transaction.UPDATE, config ).execute();
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
		List<LiteStarsLMHardware> hwsToConfig = null;
		
		// save configuration first, so its available to compute groupID later on
        if (updateHwConfig.getStarsLMConfiguration() != null) {
            updateLMConfiguration( updateHwConfig.getStarsLMConfiguration(), liteHw, energyCompany );
            
            hwsToConfig = new ArrayList<LiteStarsLMHardware>();
            hwsToConfig.add( liteHw );
        }
        
		if (liteHw.getAccountID() > 0) {
            CustomerAccountDao customerAccountDao = YukonSpringHook.getBean("customerAccountDao", CustomerAccountDao.class);
            CustomerAccount customerAccount = customerAccountDao.getById(liteHw.getAccountID());
            
            List<ProgramEnrollment> requests = new ArrayList<ProgramEnrollment>();
			for (int i = 0; i < updateHwConfig.getStarsLMHardwareConfigCount(); i++) {
				StarsLMHardwareConfig starsConfig = updateHwConfig.getStarsLMHardwareConfig(i);
				
				ProgramEnrollment enrollment = new ProgramEnrollment();
				enrollment.setInventoryId(liteHw.getInventoryID());
				enrollment.setAssignedProgramId( starsConfig.getProgramID() );
				enrollment.setLmGroupId( starsConfig.getGroupID() );
				enrollment.setRelay( starsConfig.getLoadNumber() );
				requests.add( enrollment );
			}
			
            LiteYukonUser currentUser = DaoFactory.getYukonUserDao().getLiteYukonUser(userID);
            
            ProgramEnrollmentService programEnrollmentService = YukonSpringHook.getBean("starsProgramEnrollmentService", ProgramEnrollmentService.class);            
			hwsToConfig = programEnrollmentService.applyEnrollmentRequests(customerAccount, requests, liteHw, currentUser);
			
			if (!hwsToConfig.contains( liteHw ))
				hwsToConfig.add( 0, liteHw );
            // refresh account info, after update program enrollment			
            liteAcctInfo = energyCompany.getCustAccountInformation( liteHw.getAccountID(), true );			
		}
		
		StarsInventories starsInvs = new StarsInventories();
		boolean disabled = false;
		
		String trackHwAddr = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING );
		boolean useHardwareAddressing = Boolean.valueOf( trackHwAddr ).booleanValue();
		
		for (int i = 0; i < hwsToConfig.size(); i++) {
			LiteStarsLMHardware lHw = hwsToConfig.get(i);
			
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
				LiteStarsLMHardware lHw = hwsToConfig.get(i);
				if (!lHw.equals( liteHw ))
					logMsg += "," + lHw.getManufacturerSerialNumber();
			}
		}
		
		String action = null;
		if (updateHwConfig.getSaveConfigOnly())
			action = ActivityLogActions.HARDWARE_SAVE_CONFIG_ONLY_ACTION;
		else if (updateHwConfig.getSaveToBatch())
			action = ActivityLogActions.HARDWARE_SAVE_TO_BATCH_ACTION;
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
		for (LiteStarsAppliance liteApp : liteAcctInfo.getAppliances()) {
			if (liteApp.getInventoryID() == liteHw.getInventoryID())
				return true;
		}
		
		return false;
	}
	
	public static void saveSwitchCommand(LiteStarsLMHardware liteHw, String commandType,
		LiteStarsEnergyCompany energyCompany)
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

    public static void setStarsLMConfiguration(StarsLMConfiguration starsCfg,
            HttpServletRequest req) throws WebClientException {
        String[] clps = req.getParameterValues("ColdLoadPickup");
        String[] tds = req.getParameterValues("TamperDetect");

        if (clps != null && clps.length > 0) {
            String clp = clps[0];
            for (int i = 1; i < clps.length; i++)
                clp += "," + clps[i];
            starsCfg.setColdLoadPickup(clp);
        }

        if (tds != null && tds.length > 0) {
            String td = tds[0];
            for (int i = 1; i < tds.length; i++)
                td += "," + tds[i];
            starsCfg.setTamperDetect(td);
        }

        if (req.getParameter("XCOM_SPID") != null) {
            ExpressCom xcom = new ExpressCom();

            xcom.setServiceProvider(parseNumber(req.getParameter("XCOM_SPID"),
                                                             0,
                                                             65534,
                                                             "SPID"));
            xcom.setGEO(parseNumber(req.getParameter("XCOM_GEO"),
                                                 0,
                                                 65534,
                                                 0,
                                                 "GEO"));
            xcom.setSubstation(parseNumber(req.getParameter("XCOM_SUB"),
                                                        0,
                                                        65534,
                                                        0,
                                                        "SUB"));
            xcom.setZip(parseNumber(req.getParameter("XCOM_ZIP"),
                                                 0,
                                                 16777214,
                                                 0,
                                                 "ZIP"));
            xcom.setUserAddress(parseNumber(req.getParameter("XCOM_USER"),
                                                         0,
                                                         65534,
                                                         0,
                                                         "USER"));

            String[] feeders = req.getParameterValues("XCOM_FEED");
            int feeder = 0;
            if (feeders != null) {
                for (int i = 0; i < feeders.length; i++)
                    feeder += Integer.parseInt(feeders[i]);
            }
            xcom.setFeeder(feeder);

            String program = "";
            String[] programs = req.getParameterValues("XCOM_Program");
            if (programs != null) {
                for (int i = 0; i < programs.length; i++) {
                    parseNumber(programs[i], 0, 254, 0, "Program");
                    program += programs[i].trim();
                    if (i < programs.length - 1)
                        program += ",";
                }
            }
            xcom.setProgram(program);

            String splinter = "";
            String[] splinters = req.getParameterValues("XCOM_Splinter");
            if (splinters != null) {
                for (int i = 0; i < splinters.length; i++) {
                    parseNumber(splinters[i],
                                             0,
                                             254,
                                             0,
                                             "Splinter");
                    splinter += splinters[i].trim();
                    if (i < splinters.length - 1)
                        splinter += ",";
                }
            }
            xcom.setSplinter(splinter);

            starsCfg.setExpressCom(xcom);
        } else if (req.getParameter("VCOM_Utility") != null) {
            VersaCom vcom = new VersaCom();

            vcom.setUtility(parseNumber(req.getParameter("VCOM_Utility"),
                                                     0,
                                                     255,
                                                     "Utility"));
            vcom.setSection(parseNumber(req.getParameter("VCOM_Section"),
                                                     1,
                                                     254,
                                                     0,
                                                     "Section"));

            String[] classAddrs = req.getParameterValues("VCOM_Class");
            int classAddr = 0;
            if (classAddrs != null) {
                for (int i = 0; i < classAddrs.length; i++)
                    classAddr += Integer.parseInt(classAddrs[i]);
            }
            vcom.setClassAddress(classAddr);

            String[] divisions = req.getParameterValues("VCOM_Division");
            int division = 0;
            if (divisions != null) {
                for (int i = 0; i < divisions.length; i++)
                    division += Integer.parseInt(divisions[i]);
            }
            vcom.setDivision(division);

            starsCfg.setVersaCom(vcom);
        } else if (req.getParameter("SA205_Slot1") != null) {
            SA205 sa205 = new SA205();

            sa205.setSlot1(parseNumber(req.getParameter("SA205_Slot1"),
                                                    0,
                                                    4095,
                                                    0,
                                                    "Slot Address"));
            sa205.setSlot2(parseNumber(req.getParameter("SA205_Slot2"),
                                                    0,
                                                    4095,
                                                    0,
                                                    "Slot Address"));
            sa205.setSlot3(parseNumber(req.getParameter("SA205_Slot3"),
                                                    0,
                                                    4095,
                                                    0,
                                                    "Slot Address"));
            sa205.setSlot4(parseNumber(req.getParameter("SA205_Slot4"),
                                                    0,
                                                    4095,
                                                    0,
                                                    "Slot Address"));
            sa205.setSlot5(parseNumber(req.getParameter("SA205_Slot5"),
                                                    0,
                                                    4095,
                                                    0,
                                                    "Slot Address"));
            sa205.setSlot6(parseNumber(req.getParameter("SA205_Slot6"),
                                                    0,
                                                    4095,
                                                    0,
                                                    "Slot Address"));

            starsCfg.setSA205(sa205);
        } else if (req.getParameter("SA305_Utility") != null) {
            SA305 sa305 = new SA305();

            sa305.setUtility(parseNumber(req.getParameter("SA305_Utility"),
                                                      0,
                                                      15,
                                                      "Utility"));
            sa305.setGroup(parseNumber(req.getParameter("SA305_Group"),
                                                    0,
                                                    63,
                                                    0,
                                                    "Group"));
            sa305.setDivision(parseNumber(req.getParameter("SA305_Division"),
                                                       0,
                                                       63,
                                                       0,
                                                       "Division"));
            sa305.setSubstation(parseNumber(req.getParameter("SA305_Substation"),
                                                         0,
                                                         1023,
                                                         0,
                                                         "Substation"));

            int rateFam = new Integer(req.getParameter("SA305_RateRate")).intValue() / 16;
            int rateMem = new Integer(req.getParameter("SA305_RateRate")).intValue() % 16;
            sa305.setRateFamily(parseNumber(new Integer(rateFam).toString(),
                                                         0,
                                                         7,
                                                         "Rate Family"));
            sa305.setRateMember(parseNumber(new Integer(rateMem).toString(),
                                                         0,
                                                         15,
                                                         "Rate Member"));
            // Rate Hierarchy should not be on the config page; it is part of
            // the control command only
            sa305.setRateHierarchy(0);
            // sa305.setRateHierarchy(
            // ServletUtils.parseNumber(req.getParameter("SA305_RateHierarchy"),
            // 0, 1, "Rate Hierarchy") );

            starsCfg.setSA305(sa305);
        } else if (req.getParameter("Simple_Address") != null) {
            SASimple simple = new SASimple();
            simple.setOperationalAddress(req.getParameter("Simple_Address"));
            starsCfg.setSASimple(simple);
        }
    }

    public static int parseNumber(String str, int lowerLimit, int upperLimit, String fieldName) throws WebClientException {
        if (str == null || str.trim().equals(""))
            throw new WebClientException( "The '" + fieldName + "' field cannot be empty" );
        
        try {
            int value = Integer.parseInt( str );
            if (value < lowerLimit || value > upperLimit)
                throw new WebClientException("The value of '" + fieldName + "' must be between " + lowerLimit + " and " + upperLimit);
            return value;
        }
        catch (NumberFormatException e) {
            throw new WebClientException( "Invalid numeric value \"" + str + "\"" );
        }
    }
    
    public static int parseNumber(String str, int lowerLimit, int upperLimit, int num_unset, String fieldName) throws WebClientException {
        if (str == null || str.trim().equals(""))
            return num_unset;
        return parseNumber( str, lowerLimit, upperLimit, fieldName );
    }
}
