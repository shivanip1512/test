package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSeason;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSeasonEntry;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.stars.hardware.LMThermostatSeason;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.InventoryManager;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMHardware;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsLMProgram;
import com.cannontech.stars.xml.serialize.StarsMCT;
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
			
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute(ServletUtils.ATT_ENERGY_COMPANY_SETTINGS);
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
        catch (Exception e) {
            e.printStackTrace();
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
        }

		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        StarsOperation respOper = new StarsOperation();
        java.sql.Connection conn = null;
        
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
            conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
            
			LiteInventoryBase liteInv = addInventory( createHw, liteAcctInfo, energyCompany, conn );

            // Send config command
            if (liteInv instanceof LiteStarsLMHardware &&
            	AuthFuncs.checkRoleProperty( user.getYukonUser(), ConsumerInfoRole.AUTOMATIC_CONFIGURATION ))
            {
	            YukonSwitchCommandAction.sendConfigCommand(energyCompany, (LiteStarsLMHardware)liteInv, false, conn);
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
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot create the hardware") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	e2.printStackTrace();
            }
        }
        finally {
        	try {
        		if (conn != null) conn.close();
        	}
        	catch (java.sql.SQLException e) {}
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
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static StarsOperation getRequestOperation(HttpServletRequest req, TimeZone tz) {
		StarsCreateLMHardware createHw = new StarsCreateLMHardware();
		InventoryManager.setStarsInventory( createHw, req, tz );
		
		String[] appIDs = req.getParameterValues( "AppID" );
		String[] grpIDs = req.getParameterValues( "GroupID" );
		if (appIDs != null) {
			for (int i = 0; i < appIDs.length; i++) {
				StarsLMHardwareConfig config = new StarsLMHardwareConfig();
				config.setApplianceID( Integer.parseInt(appIDs[i]) );
				config.setGroupID( Integer.parseInt(grpIDs[i]) );
				createHw.addStarsLMHardwareConfig( config );
			}
		}
		
		StarsOperation operation = new StarsOperation();
		operation.setStarsCreateLMHardware( createHw );
		return operation;
	}
	
	public static LMThermostatSeason[] populateThermostatTables(LiteStarsLMHardware liteHw, LiteStarsEnergyCompany energyCompany, java.sql.Connection conn)
	throws java.sql.SQLException {
		com.cannontech.database.data.multi.MultiDBPersistent multiDB =
				new com.cannontech.database.data.multi.MultiDBPersistent();
		
		int weekdayID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_WEEKDAY).getEntryID();
		int[] weekdayIDs = new int[] {
				energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_MONDAY).getEntryID(),
				energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_TUESDAY).getEntryID(),
				energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_WEDNESDAY).getEntryID(),
				energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_THURSDAY).getEntryID(),
				energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_FRIDAY).getEntryID()
		};
		
		ArrayList liteSeasons = energyCompany.getDefaultThermostatSettings().getThermostatSeasons();
		LMThermostatSeason[] seasons = new LMThermostatSeason[ liteSeasons.size() ];
				
		for (int i = 0; i < liteSeasons.size(); i++) {
			LiteLMThermostatSeason liteSeason = (LiteLMThermostatSeason) liteSeasons.get(i);
			seasons[i] = new LMThermostatSeason();
			StarsLiteFactory.setLMThermostatSeason( seasons[i].getLMThermostatSeason(), liteSeason );
			seasons[i].getLMThermostatSeason().setSeasonID( null );
			seasons[i].getLMThermostatSeason().setInventoryID( new Integer(liteHw.getInventoryID()) );
			
			for (int j = 0; j < liteSeason.getSeasonEntries().size(); j++) {
				LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteSeason.getSeasonEntries().get(j);
				
				if (!liteHw.isTwoWayThermostat() || liteEntry.getTimeOfWeekID() != weekdayID) {
					com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry entry =
							new com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry();
					StarsLiteFactory.setLMThermostatSeasonEntry( entry, liteEntry );
					entry.setEntryID( null );
					seasons[i].getLMThermostatSeasonEntries().add( entry );
				}
				else {
					for (int k= 0; k < weekdayIDs.length; k++) {
						com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry entry =
								new com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry();
						StarsLiteFactory.setLMThermostatSeasonEntry( entry, liteEntry );
						entry.setEntryID( null );
						entry.setTimeOfWeekID( new Integer(weekdayIDs[k]) );
						seasons[i].getLMThermostatSeasonEntries().add( entry );
					}
				}
			}
			
			multiDB.getDBPersistentVector().add( seasons[i] );
		}
		
		multiDB.setDbConnection( conn );
		multiDB.add();
		
		return seasons;
	}
	
	/**
	 * Add a hardware to a customer account. If the hardware doesn't exist before,
	 * then create it and add to the inventory. If it's in the warehouse, just add
	 * it to the account. If it's currently assigned to another account, remove it
	 * from that account first, then add it to the new account. 
	 */
	public static LiteInventoryBase addInventory(StarsCreateLMHardware createHw, LiteStarsCustAccountInformation liteAcctInfo,
			LiteStarsEnergyCompany energyCompany, java.sql.Connection conn) throws java.sql.SQLException
	{
		com.cannontech.database.db.stars.hardware.InventoryBase invDB = new com.cannontech.database.db.stars.hardware.InventoryBase();
		int categoryID = ECUtils.getInventoryCategoryID(createHw.getLMDeviceType().getEntryID(), energyCompany);
		
		StarsFactory.setInventoryBase( invDB, createHw );
		invDB.setAccountID( new Integer(liteAcctInfo.getAccountID()) );
		invDB.setCategoryID( new Integer(categoryID) );
		
		LiteInventoryBase liteInv = null;
		int invID = createHw.getInventoryID();
		
		if (invID == -1) {
			// Create new hardware
			if (ECUtils.isLMHardware( categoryID )) {
				com.cannontech.database.data.stars.hardware.LMHardwareBase hw = new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hw.getLMHardwareBase();
				
				hwDB.setManufacturerSerialNumber( createHw.getManufactureSerialNumber() );
				hwDB.setLMHardwareTypeID( new Integer(createHw.getLMDeviceType().getEntryID()) );
				hw.setInventoryBase( invDB );
				hw.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				
				hw.setDbConnection( conn );
				hw.add();
				
				LiteStarsLMHardware liteHw = new LiteStarsLMHardware();
				StarsLiteFactory.setLiteStarsLMHardware( liteHw, hw );
				if (liteHw.isThermostat())
					populateThermostatTables( liteHw, energyCompany, conn );
	            
	            liteInv = liteHw;
				energyCompany.addInventory( liteInv );
			}
			else {
				com.cannontech.database.data.stars.hardware.InventoryBase inventory =
						new com.cannontech.database.data.stars.hardware.InventoryBase();
				inventory.setInventoryBase( invDB );
				inventory.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				
				inventory.setDbConnection( conn );
				inventory.add();
				
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
				
				DeleteLMHardwareAction.removeInventory(deleteHw, litePrevAccount, energyCompany, conn);
				
				StarsCustAccountInformation starsPrevAccount = energyCompany.getStarsCustAccountInformation( litePrevAccount.getAccountID() );
				if (starsPrevAccount != null)
					DeleteLMHardwareAction.parseResponse( invID, starsPrevAccount );
			}
            
			invDB.setInventoryID( new Integer(invID) );
			invDB.setDbConnection( conn );
			invDB.update();
			
			StarsLiteFactory.setLiteInventoryBase( liteInv, invDB );
		}
		
		if (liteInv instanceof LiteStarsLMHardware) {
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) liteInv;
			
			// If this is a two-way thermostat, set the account for receiving dynamic data
			if (liteHw.isTwoWayThermostat()) {
				java.util.ArrayList accountList = energyCompany.getAccountsWithGatewayEndDevice();
				synchronized (accountList) {
					if (!accountList.contains( liteAcctInfo )) accountList.add( liteAcctInfo );
				}
			}
			
			for (int i = 0; i < createHw.getStarsLMHardwareConfigCount(); i++) {
				StarsLMHardwareConfig starsConfig = createHw.getStarsLMHardwareConfig(i);
				if (starsConfig.getGroupID() == 0) continue;
            	
				com.cannontech.database.db.stars.hardware.LMHardwareConfiguration config =
						new com.cannontech.database.db.stars.hardware.LMHardwareConfiguration();
				config.setInventoryID( invDB.getInventoryID() );
				config.setApplianceID( new Integer(starsConfig.getApplianceID()) );
				config.setAddressingGroupID( new Integer(starsConfig.getGroupID()) );
				config.setDbConnection( conn );
				config.add();
            	
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
		event.setDbConnection( conn );
		event.add();
		
		// If device status is unavailable, add "Termination" event
		if (createHw.getDeviceStatus() != null &&
			energyCompany.getYukonListEntry(
				YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS, createHw.getDeviceStatus().getEntryID())
				.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL)
		{
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
			event.setDbConnection( conn );
			event.add();
		}
		
		StarsLiteFactory.extendLiteInventoryBase( liteInv, energyCompany );
		liteAcctInfo.getInventories().add( invDB.getInventoryID() );
		
		return liteInv;
	}
	
	private void parseResponse(StarsCreateLMHardware createHw, StarsInventory starsInv, StarsCustAccountInformation starsAcctInfo, HttpSession session) {
		StarsInventories starsInvs = starsAcctInfo.getStarsInventories();
		int invNo = 0;
			
		if (starsInv instanceof StarsLMHardware) {
			int idx = 0;
			for (; idx < starsInvs.getStarsLMHardwareCount(); idx++) {
				StarsLMHardware starsHw = starsInvs.getStarsLMHardware(idx);
				if (starsHw.getDeviceLabel().compareTo( starsInv.getDeviceLabel() ) > 0)
					break;
			}
				
			starsInvs.addStarsLMHardware( idx, (StarsLMHardware)starsInv );
			invNo = idx;
				
			for (int i = 0; i < createHw.getStarsLMHardwareConfigCount(); i++) {
				StarsLMHardwareConfig config = createHw.getStarsLMHardwareConfig(i);
				
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
		else if (starsInv instanceof StarsMCT) {
			int idx = 0;
			for (; idx < starsInvs.getStarsMCTCount(); idx++) {
				StarsMCT starsMCT = starsInvs.getStarsMCT(idx);
				if (starsMCT.getDeviceLabel().compareTo( starsInv.getDeviceLabel() ) > 0)
					break;
			}
				
			starsInvs.addStarsMCT( idx, (StarsMCT)starsInv );
			invNo = starsInvs.getStarsLMHardwareCount() + idx;
		}
			
		session.removeAttribute( InventoryManager.STARS_INVENTORY_TEMP );
		if (session.getAttribute(ServletUtils.ATT_REDIRECT) == null)
			session.setAttribute( ServletUtils.ATT_REDIRECT, "/operator/Consumer/Inventory.jsp?InvNo=" + String.valueOf(invNo) );
	}

}
