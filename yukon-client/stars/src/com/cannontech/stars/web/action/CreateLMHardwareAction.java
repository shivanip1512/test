package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteLMCustomerEvent;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSeason;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSeasonEntry;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.InventoryManager;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.serialize.DeviceStatus;
import com.cannontech.stars.xml.serialize.InstallationCompany;
import com.cannontech.stars.xml.serialize.LMDeviceType;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardwareResponse;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustListEntry;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsLMHardware;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsLMHardwareEvent;
import com.cannontech.stars.xml.serialize.StarsLMHardwareHistory;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.stars.xml.serialize.Voltage;
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
			java.util.Hashtable selectionLists = (java.util.Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );

			StarsCreateLMHardware createHw = new StarsCreateLMHardware();
			InventoryManager.setStarsLMHardware( createHw, req );
			
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
        	
        	int energyCompanyID = user.getEnergyCompanyID();
        	LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            
            StarsCreateLMHardware createHw = reqOper.getStarsCreateLMHardware();
            LiteStarsLMHardware liteHw = null;
            conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
            
            com.cannontech.database.data.stars.hardware.LMHardwareBase hw = new com.cannontech.database.data.stars.hardware.LMHardwareBase();
            com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hw.getLMHardwareBase();
            com.cannontech.database.db.stars.hardware.InventoryBase invDB = hw.getInventoryBase();
            
            invDB.setAccountID( new Integer(liteAcctInfo.getAccountID()) );
            invDB.setInstallationCompanyID( new Integer(createHw.getInstallationCompany().getEntryID()) );
            invDB.setCategoryID( new Integer(getInventoryCategoryID(createHw.getLMDeviceType().getEntryID(), energyCompanyID)) );
            if (createHw.getReceiveDate() != null)
	            invDB.setReceiveDate( createHw.getReceiveDate() );
	        if (createHw.getInstallDate() != null)
	            invDB.setInstallDate( createHw.getInstallDate() );
	        if (createHw.getRemoveDate() != null)
	            invDB.setRemoveDate( createHw.getRemoveDate() );
            invDB.setAlternateTrackingNumber( createHw.getAltTrackingNumber() );
            invDB.setVoltageID( new Integer(createHw.getVoltage().getEntryID()) );
            invDB.setNotes( createHw.getNotes() );
            if (createHw.getDeviceLabel().trim().length() > 0)
            	invDB.setDeviceLabel( createHw.getDeviceLabel() );
            else
            	invDB.setDeviceLabel( createHw.getManufactureSerialNumber() );
            hwDB.setManufacturerSerialNumber( createHw.getManufactureSerialNumber() );
            hwDB.setLMHardwareTypeID( new Integer(createHw.getLMDeviceType().getEntryID()) );
            hw.setEnergyCompanyID( new Integer(energyCompanyID) );
            hw.setDbConnection( conn );
            
            int invID = createHw.getInventoryID();
            if (invID == -1) {
            	// Create new hardware
	            hw.add();
	            liteHw = (LiteStarsLMHardware) StarsLiteFactory.createLite( hw );
	            energyCompany.addLMHardware( liteHw );
	            
	        	if (ServerUtils.isOneWayThermostat(liteHw, energyCompany))
	        		populateThermostatTables( liteHw.getInventoryID(), energyCompany, false, conn );
	        	else if (ServerUtils.isTwoWayThermostat(liteHw, energyCompany))
	        		populateThermostatTables( liteHw.getInventoryID(), energyCompany, true, conn );
            }
            else {
            	liteHw = energyCompany.getLMHardware( invID, true );
            	if (liteHw.getAccountID() > 0) {
            		// Remove hardware from previous account
            		LiteStarsCustAccountInformation litePrevAccount = energyCompany.getCustAccountInformation( liteHw.getAccountID(), true );
            		DeleteLMHardwareAction.removeLMHardware( invID, litePrevAccount, energyCompany, liteAcctInfo, conn );
            		StarsCustAccountInformation starsPrevAccount = energyCompany.getStarsCustAccountInformation( litePrevAccount.getAccountID() );
            		if (starsPrevAccount != null)
            			DeleteLMHardwareAction.parseResponse( invID, starsPrevAccount );
            	}
            	
        		// Add hardware to the account
        		hw.setInventoryID( new Integer(invID) );
        		hw.update();
        		StarsLiteFactory.setLiteLMHardwareBase( liteHw, hw );
            }
            
            Integer inventoryID = hw.getInventoryBase().getInventoryID();
            int hwEventEntryID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE ).getEntryID();
            int installActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL ).getEntryID();
        	
            // Add "Install" to hardware events
        	com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
        	com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
        	com.cannontech.database.db.stars.event.LMCustomerEventBase eventBaseDB = event.getLMCustomerEventBase();
            
        	eventBaseDB.setEventTypeID( new Integer(hwEventEntryID) );
            eventBaseDB.setActionID( new Integer(installActID) );
            if (createHw.getInstallDate() != null)
	            eventBaseDB.setEventDateTime( createHw.getInstallDate() );
	        else
	        	eventBaseDB.setEventDateTime( new Date() );
            eventBaseDB.setNotes( "Hardware installed to account #" + liteAcctInfo.getCustomerAccount().getAccountNumber() + ". "
            		+ createHw.getInstallationNotes() );
            eventDB.setInventoryID( inventoryID );
            event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
            event.setDbConnection( conn );
            event.add();
            
            StarsLiteFactory.extendLiteStarsLMHardware( liteHw, energyCompany );
            liteAcctInfo.getInventories().add( new Integer(liteHw.getInventoryID()) );
            
            // If this is a two-way thermostat, set the account for receiving dynamic data
        	if (ServerUtils.isTwoWayThermostat(liteHw, energyCompany)) {
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
            	config.setInventoryID( inventoryID );
            	config.setApplianceID( new Integer(starsConfig.getApplianceID()) );
            	config.setAddressingGroupID( new Integer(starsConfig.getGroupID()) );
            	config = (com.cannontech.database.db.stars.hardware.LMHardwareConfiguration)
            			Transaction.createTransaction( Transaction.INSERT, config ).execute();
            	
            	for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
            		LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
            		if (liteApp.getApplianceID() == starsConfig.getApplianceID()) {
            			liteApp.setInventoryID( inventoryID.intValue() );
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

            // Send config command
            StarsLMHardware starsHw = YukonSwitchCommandAction.sendConfigCommand( energyCompany, inventoryID.intValue(), false );
            StarsCreateLMHardwareResponse resp = new StarsCreateLMHardwareResponse();
            resp.setStarsLMHardware( starsHw );
            
            respOper.setStarsCreateLMHardwareResponse( resp );
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
			
			StarsCreateLMHardwareResponse resp = operation.getStarsCreateLMHardwareResponse();
			StarsLMHardware hw = resp.getStarsLMHardware();
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            if (accountInfo == null)
            	return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
			
			StarsInventories starsInvs = accountInfo.getStarsInventories();
			int idx = 0;
			for (; idx < starsInvs.getStarsLMHardwareCount(); idx++) {
				StarsLMHardware starsHw = starsInvs.getStarsLMHardware(idx);
				if (starsHw.getDeviceLabel().compareTo( hw.getDeviceLabel() ) > 0)
					break;
			}
			starsInvs.addStarsLMHardware( idx, hw );
			
			StarsCreateLMHardware createHw = SOAPUtil.parseSOAPMsgForOperation( reqMsg ).getStarsCreateLMHardware();
			for (int i = 0; i < createHw.getStarsLMHardwareConfigCount(); i++) {
				StarsLMHardwareConfig config = createHw.getStarsLMHardwareConfig(i);
				
				for (int j = 0; j < accountInfo.getStarsAppliances().getStarsApplianceCount(); j++) {
					StarsAppliance app = accountInfo.getStarsAppliances().getStarsAppliance(j);
					if (app.getApplianceID() == config.getApplianceID()) {
						app.setInventoryID( hw.getInventoryID() );
						
						for (int k = 0; k < accountInfo.getStarsLMPrograms().getStarsLMProgramCount(); k++) {
							StarsLMProgram prog = accountInfo.getStarsLMPrograms().getStarsLMProgram(k);
							if (prog.getProgramID() == app.getLmProgramID()) {
								prog.setGroupID( config.getGroupID() );
								break;
							}
						}
						break;
					}
				}
			}
			
			if (session.getAttribute(ServletUtils.ATT_REDIRECT) == null)
				session.setAttribute( ServletUtils.ATT_REDIRECT, "/operator/Consumer/Inventory.jsp?InvNo=" + String.valueOf(idx) );
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static int getInventoryCategoryID(int deviceTypeID, int energyCompanyID) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
		int lcrTypeID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR ).getEntryID();
		int thermTypeID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_THERMOSTAT ).getEntryID();
		int eproTypeID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO ).getEntryID();
		
		if (deviceTypeID == lcrTypeID || deviceTypeID == thermTypeID)
		{
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC).getEntryID();
		}
		else if (deviceTypeID == eproTypeID)
		{
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_TWOWAYREC).getEntryID();
		}
		
		return CtiUtilities.NONE_ID;
	}
	
	public static void populateThermostatTables(int inventoryID, LiteStarsEnergyCompany energyCompany, boolean isTwoWay, java.sql.Connection conn)
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
		for (int i = 0; i < liteSeasons.size(); i++) {
			LiteLMThermostatSeason liteSeason = (LiteLMThermostatSeason) liteSeasons.get(i);
			com.cannontech.database.data.stars.hardware.LMThermostatSeason season =
					new com.cannontech.database.data.stars.hardware.LMThermostatSeason();
			StarsLiteFactory.setLMThermostatSeason( season.getLMThermostatSeason(), liteSeason );
			season.getLMThermostatSeason().setSeasonID( null );
			season.getLMThermostatSeason().setInventoryID( new Integer(inventoryID) );
			
			for (int j = 0; j < liteSeason.getSeasonEntries().size(); j++) {
				LiteLMThermostatSeasonEntry liteEntry = (LiteLMThermostatSeasonEntry) liteSeason.getSeasonEntries().get(j);
				
				if (!isTwoWay || liteEntry.getTimeOfWeekID() != weekdayID) {
					com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry entry =
							new com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry();
					StarsLiteFactory.setLMThermostatSeasonEntry( entry, liteEntry );
					entry.setEntryID( null );
					season.getLMThermostatSeasonEntries().add( entry );
				}
				else {
					for (int k= 0; k < weekdayIDs.length; k++) {
						com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry entry =
								new com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry();
						StarsLiteFactory.setLMThermostatSeasonEntry( entry, liteEntry );
						entry.setEntryID( null );
						entry.setTimeOfWeekID( new Integer(weekdayIDs[k]) );
						season.getLMThermostatSeasonEntries().add( entry );
					}
				}
			}
			
			multiDB.getDBPersistentVector().add( season );
		}
		
		multiDB.setDbConnection( conn );
		multiDB.add();
	}

}
