package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSchedule;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSeason;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSeasonEntry;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.stars.hardware.LMThermostatSeason;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.InventoryManager;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsLMProgram;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
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
public class CreateLMHardwareAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			StarsEnergyCompanySettings ecSettings =
					(StarsEnergyCompanySettings) user.getAttribute(ServletUtils.ATT_ENERGY_COMPANY_SETTINGS);
			TimeZone tz = TimeZone.getTimeZone( ecSettings.getStarsEnergyCompany().getTimeZone() );
			if (tz == null) tz = TimeZone.getDefault();

			StarsOperation operation = null;
			if (req.getParameter("InvID") != null) {
				// Request from CreateHardware.jsp
				operation = getRequestOperation( req, tz );
			}
			else {
				// Request redirected from InventoryManager
				operation = (StarsOperation) session.getAttribute(InventoryManager.STARS_INVENTORY_OPERATION);
				session.removeAttribute( InventoryManager.STARS_INVENTORY_OPERATION );
			}
			
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (WebClientException we) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, we.getMessage() );
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
            
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			if (liteAcctInfo == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
        	
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        	
			StarsCreateLMHardware createHw = reqOper.getStarsCreateLMHardware();
			LiteInventoryBase liteInv = null;
            
			try {
				liteInv = addInventory( createHw, liteAcctInfo, energyCompany );
			}
			catch (WebClientException e) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, e.getMessage()) );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			// Response will be handled here, instead of in parse()
			StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteAcctInfo.getAccountID() );
			if (starsAcctInfo != null) {
				StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteInv, energyCompany );
				parseResponse( createHw, starsInv, starsAcctInfo, session );
			}
            
			respOper.setStarsSuccess( new StarsSuccess() );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Failed to create the hardware") );
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
			
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static StarsOperation getRequestOperation(HttpServletRequest req, TimeZone tz) throws WebClientException {
		StarsCreateLMHardware createHw = new StarsCreateLMHardware();
		InventoryManager.setStarsInv( createHw, req, tz );
		
		StarsOperation operation = new StarsOperation();
		operation.setStarsCreateLMHardware( createHw );
		return operation;
	}
	
	private static LiteLMThermostatSchedule initThermostatSchedule(int hwTypeID, int accountID, int invID, LiteStarsEnergyCompany energyCompany) 
		throws TransactionException
	{
		com.cannontech.database.data.stars.hardware.LMThermostatSchedule schedule =
				new com.cannontech.database.data.stars.hardware.LMThermostatSchedule();
		com.cannontech.database.db.stars.hardware.LMThermostatSchedule scheduleDB = schedule.getLmThermostatSchedule();
		
		scheduleDB.setThermostatTypeID( new Integer(hwTypeID) );
		scheduleDB.setAccountID( new Integer(accountID) );
		scheduleDB.setInventoryID( new Integer(invID) );
		
		int weekdayID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_WEEKDAY).getEntryID();
		int[] weekdayIDs = new int[] {
				energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_MONDAY).getEntryID(),
				energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_TUESDAY).getEntryID(),
				energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_WEDNESDAY).getEntryID(),
				energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_THURSDAY).getEntryID(),
				energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_FRIDAY).getEntryID()
		};
		
		// If we are creating the default commercial thermostat schedule
		int hwTypeDefID = YukonListFuncs.getYukonListEntry( hwTypeID ).getYukonDefID();
		boolean isDftCommTstat = (hwTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT)
				&& ECUtils.isDefaultEnergyCompany( energyCompany );
		
		ArrayList liteSeasons = energyCompany.getDefaultThermostatSchedule(hwTypeDefID).getThermostatSeasons();
		for (int i = 0; i < liteSeasons.size(); i++) {
			LiteLMThermostatSeason liteSeason = (LiteLMThermostatSeason) liteSeasons.get(i);
			
			LMThermostatSeason season = new LMThermostatSeason();
			StarsLiteFactory.setLMThermostatSeason( season.getLMThermostatSeason(), liteSeason );
			season.getLMThermostatSeason().setSeasonID( null );
			
			ArrayList entries = season.getLMThermostatSeasonEntries();
			
			for (int j = 0; j < liteSeason.getSeasonEntries().size(); j++) {
				LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteSeason.getSeasonEntries().get(j);
				
				if (hwTypeDefID != YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO || liteEntry.getTimeOfWeekID() != weekdayID)
				{
					com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry entry =
							new com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry();
					StarsLiteFactory.setLMThermostatSeasonEntry( entry, liteEntry );
					entry.setEntryID( null );
					
					if (isDftCommTstat && (j % 4 == 1 || j % 4 == 2))
						entry.setTemperature( new Integer(-1) );
					entries.add( entry );
				}
				else {
					for (int k= 0; k < weekdayIDs.length; k++) {
						com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry entry =
								new com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry();
						StarsLiteFactory.setLMThermostatSeasonEntry( entry, liteEntry );
						entry.setEntryID( null );
						entry.setTimeOfWeekID( new Integer(weekdayIDs[k]) );
						
						entries.add( entry );
					}
				}
			}
			
			schedule.getThermostatSeasons().add( season );
		}
		
		schedule = (com.cannontech.database.data.stars.hardware.LMThermostatSchedule)
				Transaction.createTransaction( Transaction.INSERT, schedule ).execute();
		
		return StarsLiteFactory.createLiteLMThermostatSchedule( schedule );
	}
	
	/**
	 * Used to create the default thermostat schedule for an energy company
	 * @throws TransactionException
	 */
	public static LiteLMThermostatSchedule initThermostatSchedule(int hwTypeDefID) throws TransactionException
	{
		LiteStarsEnergyCompany energyCompany = SOAPServer.getDefaultEnergyCompany();
		int hwTypeID = energyCompany.getYukonListEntry( hwTypeDefID ).getEntryID();
		return initThermostatSchedule( hwTypeID, 0, 0, energyCompany );
	}
	
	/**
	 * Used to create the thermostat schedule for a given LM hardware (thermostat)
	 * @throws TransactionException
	 */
	public static LiteLMThermostatSchedule initThermostatSchedule(LiteStarsLMHardware liteHw, LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		return initThermostatSchedule( liteHw.getLmHardwareTypeID(), liteHw.getAccountID(), liteHw.getInventoryID(), energyCompany );
	}
	
	/**
	 * Add a hardware to a customer account or inventory.
	 * To add to a customer account: if the hardware doesn't exist before,
	 * then create it and add to the inventory. If it's in the warehouse,
	 * just add it to the account. If it's currently assigned to another account,
	 * remove it from that account first, then add it to the new account. 
	 */
	public static LiteInventoryBase addInventory(StarsCreateLMHardware createHw, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
		throws WebClientException
	{
		if (createHw.getLMHardware() != null && createHw.getLMHardware().getManufacturerSerialNumber().equals(""))
			throw new WebClientException( "Serial # cannot be empty" );
		
		try {
			com.cannontech.database.db.stars.hardware.InventoryBase invDB = new com.cannontech.database.db.stars.hardware.InventoryBase();
			StarsFactory.setInventoryBase( invDB, createHw );
			
			int categoryID = ECUtils.getInventoryCategoryID(createHw.getDeviceType().getEntryID(), energyCompany);
			invDB.setCategoryID( new Integer(categoryID) );
			
			if (liteAcctInfo != null)
				invDB.setAccountID( new Integer(liteAcctInfo.getAccountID()) );
			
			com.cannontech.database.data.stars.hardware.LMHardwareBase hw = null;
			if (createHw.getLMHardware() != null) {
				hw = new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				hw.setInventoryBase( invDB );
				
				com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hw.getLMHardwareBase();
				hwDB.setManufacturerSerialNumber( createHw.getLMHardware().getManufacturerSerialNumber() );
				hwDB.setLMHardwareTypeID( new Integer(createHw.getDeviceType().getEntryID()) );
				hwDB.setRouteID( new Integer(createHw.getLMHardware().getRouteID()) );
			}
			
			LiteInventoryBase liteInv = null;
			int invID = createHw.getInventoryID();
			
			if (invID == -1) {
				// Create new hardware
				if (createHw.getLMHardware() != null) {
					hw.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
					hw = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
							Transaction.createTransaction( Transaction.INSERT, hw ).execute();
					
					LiteStarsLMHardware liteHw = new LiteStarsLMHardware();
					StarsLiteFactory.setLiteStarsLMHardware( liteHw, hw );
					
					if (liteHw.isThermostat())
						initThermostatSchedule( liteHw, energyCompany );
	            	
					liteInv = liteHw;
					energyCompany.addInventory( liteInv );
				}
				else {
					com.cannontech.database.data.stars.hardware.InventoryBase inventory =
							new com.cannontech.database.data.stars.hardware.InventoryBase();
					inventory.setInventoryBase( invDB );
					inventory.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
					
					inventory = (com.cannontech.database.data.stars.hardware.InventoryBase)
							Transaction.createTransaction( Transaction.INSERT, inventory ).execute();
					
					liteInv = new LiteInventoryBase();
					StarsLiteFactory.setLiteInventoryBase( liteInv, invDB );
					energyCompany.addInventory( liteInv );
				}
			}
			else {
				// Add hardware in the inventory to customer account
				liteInv = energyCompany.getInventory( invID, true );
				
				if (liteInv.getAccountID() > 0) {
					// Remove hardware from previous account
					LiteStarsCustAccountInformation litePrevAccount = energyCompany.getCustAccountInformation( liteInv.getAccountID(), true );
					
					StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
					deleteHw.setInventoryID( invID );
					deleteHw.setDeleteFromInventory( false );
					
					DeleteLMHardwareAction.removeInventory(deleteHw, litePrevAccount, energyCompany);
					
					// The liteInv object is changed in the method above, so we need to retrieve it again
					liteInv = energyCompany.getInventoryBrief( invID, true );
					
					StarsCustAccountInformation starsPrevAccount = energyCompany.getStarsCustAccountInformation( litePrevAccount.getAccountID() );
					if (starsPrevAccount != null)
						DeleteLMHardwareAction.parseResponse( invID, starsPrevAccount );
				}
            	
            	if (createHw.getLMHardware() != null && liteInv instanceof LiteStarsLMHardware) {
					hw.setInventoryID( new Integer(invID) );
					hw = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
							Transaction.createTransaction( Transaction.UPDATE, hw ).execute();
            		
            		StarsLiteFactory.setLiteStarsLMHardware( (LiteStarsLMHardware)liteInv, hw );
            	}
            	else {
					invDB.setInventoryID( new Integer(invID) );
					invDB = (com.cannontech.database.db.stars.hardware.InventoryBase)
							Transaction.createTransaction( Transaction.UPDATE, invDB ).execute();
					
					StarsLiteFactory.setLiteInventoryBase( liteInv, invDB );
            	}
			}
			
			if (liteAcctInfo != null) {
				if (liteInv instanceof LiteStarsLMHardware) {
					LiteStarsLMHardware liteHw = (LiteStarsLMHardware) liteInv;
					
					// If this is a two-way thermostat, set the account for receiving dynamic data
					if (liteHw.isTwoWayThermostat()) {
						java.util.ArrayList accountList = energyCompany.getAccountsWithGatewayEndDevice();
						synchronized (accountList) {
							if (!accountList.contains( liteAcctInfo )) accountList.add( liteAcctInfo );
						}
					}
					
					for (int i = 0; i < createHw.getLMHardware().getStarsLMHardwareConfigCount(); i++) {
						StarsLMHardwareConfig starsConfig = createHw.getLMHardware().getStarsLMHardwareConfig(i);
						if (starsConfig.getGroupID() == 0) continue;
            			
						com.cannontech.database.db.stars.hardware.LMHardwareConfiguration config =
								new com.cannontech.database.db.stars.hardware.LMHardwareConfiguration();
						config.setInventoryID( invDB.getInventoryID() );
						config.setApplianceID( new Integer(starsConfig.getApplianceID()) );
						config.setAddressingGroupID( new Integer(starsConfig.getGroupID()) );
						
						config = (com.cannontech.database.db.stars.hardware.LMHardwareConfiguration)
								Transaction.createTransaction( Transaction.INSERT, config ).execute();
            			
						for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
							LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
							if (liteApp.getApplianceID() == starsConfig.getApplianceID()) {
								liteApp.setInventoryID( liteHw.getInventoryID() );
								liteApp.setAddressingGroupID( config.getAddressingGroupID().intValue() );
            					
								for (int k = 0; k < liteAcctInfo.getLmPrograms().size(); k++) {
									LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(k);
									if (liteProg.getLmProgram().getProgramID() == liteApp.getLmProgramID()) {
										liteProg.setGroupID( config.getAddressingGroupID().intValue() );
										break;
									}
								}
								break;
							}
						}
					}
				}
				
				int hwEventEntryID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE ).getEntryID();
				int installActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL ).getEntryID();
				
				// Add "Install" to hardware events
				com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
				com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
				com.cannontech.database.db.stars.event.LMCustomerEventBase eventBaseDB = event.getLMCustomerEventBase();
				
				eventBaseDB.setEventTypeID( new Integer(hwEventEntryID) );
				eventBaseDB.setActionID( new Integer(installActID) );
				eventBaseDB.setEventDateTime( new Date(liteInv.getInstallDate()) );
				eventBaseDB.setNotes( createHw.getInstallationNotes() );
				eventDB.setInventoryID( invDB.getInventoryID() );
				event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				
				event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
						Transaction.createTransaction( Transaction.INSERT, event ).execute();
				
				int statusDefID = YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL;
				if (createHw.getDeviceStatus() != null)
					statusDefID = YukonListFuncs.getYukonListEntry(
							createHw.getDeviceStatus().getEntryID() ).getYukonDefID();
				
				if (statusDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL) {
					// If device status is unavailable, add "Termination" event
					int termActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION ).getEntryID();
					
					event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
					eventDB = event.getLMHardwareEvent();
					eventBaseDB = event.getLMCustomerEventBase();
					
					eventBaseDB.setEventTypeID( new Integer(hwEventEntryID) );
					eventBaseDB.setActionID( new Integer(termActID) );
					eventBaseDB.setEventDateTime( new Date() );
					eventBaseDB.setNotes( "Event added to match the device status" );
					eventDB.setInventoryID( invDB.getInventoryID() );
					event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
					
					event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
							Transaction.createTransaction( Transaction.INSERT, event ).execute();
				}
				else if (statusDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL) {
					// If device status is temporary unavailable, add "Temp Termination" event
					int tempTermActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION ).getEntryID();
					
					event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
					eventDB = event.getLMHardwareEvent();
					eventBaseDB = event.getLMCustomerEventBase();
					
					eventBaseDB.setEventTypeID( new Integer(hwEventEntryID) );
					eventBaseDB.setActionID( new Integer(tempTermActID) );
					eventBaseDB.setEventDateTime( new Date() );
					eventBaseDB.setNotes( "Event added to match the device status" );
					eventDB.setInventoryID( invDB.getInventoryID() );
					event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
					
					event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
							Transaction.createTransaction( Transaction.INSERT, event ).execute();
				}
				
				StarsLiteFactory.extendLiteInventoryBase( liteInv, energyCompany );
				liteAcctInfo.getInventories().add( invDB.getInventoryID() );
			}
			
			return liteInv;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			throw new WebClientException( "Failed to create the hardware" );
		}
	}
	
	private void parseResponse(StarsCreateLMHardware createHw, StarsInventory starsInv, StarsCustAccountInformation starsAcctInfo, HttpSession session) {
		StarsInventories starsInvs = starsAcctInfo.getStarsInventories();
		
		int invNo = 0;
		for (;invNo < starsInvs.getStarsInventoryCount(); invNo++) {
			StarsInventory inv = starsInvs.getStarsInventory(invNo);
			if (inv.getDeviceLabel().compareTo( starsInv.getDeviceLabel() ) > 0)
				break;
		}
		
		starsInvs.addStarsInventory( invNo, starsInv );
		
		if (createHw.getLMHardware() != null) {
			for (int i = 0; i < createHw.getLMHardware().getStarsLMHardwareConfigCount(); i++) {
				StarsLMHardwareConfig config = createHw.getLMHardware().getStarsLMHardwareConfig(i);
				
				for (int j = 0; j < starsAcctInfo.getStarsAppliances().getStarsApplianceCount(); j++) {
					StarsAppliance app = starsAcctInfo.getStarsAppliances().getStarsAppliance(j);
					if (app.getApplianceID() == config.getApplianceID()) {
						app.setInventoryID( starsInv.getInventoryID() );
						
						for (int k = 0; k < starsAcctInfo.getStarsLMPrograms().getStarsLMProgramCount(); k++) {
							StarsLMProgram prog = starsAcctInfo.getStarsLMPrograms().getStarsLMProgram(k);
							if (prog.getProgramID() == app.getLmProgramID()) {
								prog.setGroupID( config.getGroupID() );
								break;
							}
						}
						break;
					}
				}
			}
		}
		
		session.removeAttribute( InventoryManager.STARS_INVENTORY_TEMP );
		if (session.getAttribute(ServletUtils.ATT_REDIRECT) == null)
			session.setAttribute( ServletUtils.ATT_REDIRECT, "/operator/Consumer/Inventory.jsp?InvNo=" + String.valueOf(invNo) );
	}

}
