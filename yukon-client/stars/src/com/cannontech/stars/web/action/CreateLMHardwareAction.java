package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteLMCustomerEvent;
import com.cannontech.database.data.lite.stars.LiteLMHardwareBase;
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
			session.removeAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD );
        	if (req.getParameter("Wizard") != null && Boolean.valueOf(req.getParameter("Wizard")).booleanValue())
				session.setAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD, req.getParameter("Wizard") );
				
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			java.util.Hashtable selectionLists = (java.util.Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );

			StarsCreateLMHardware createHw = new StarsCreateLMHardware();
			
			LMDeviceType type = (LMDeviceType) StarsFactory.newStarsCustListEntry(
					ServletUtils.getStarsCustListEntryByID(
						selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, Integer.parseInt(req.getParameter("DeviceType"))),
					LMDeviceType.class );
			createHw.setLMDeviceType( type );
			
			Voltage volt = (Voltage) StarsFactory.newStarsCustListEntry(
					ServletUtils.getStarsCustListEntryByID(
						selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE, Integer.parseInt(req.getParameter("Voltage"))),
					Voltage.class );
			createHw.setVoltage( volt );
			
			InstallationCompany company = new InstallationCompany();
			company.setEntryID( Integer.parseInt(req.getParameter("ServiceCompany")) );
			createHw.setInstallationCompany( company );
			
			createHw.setCategory( "" );
			createHw.setManufactureSerialNumber( req.getParameter("SerialNo") );
			createHw.setAltTrackingNumber( req.getParameter("AltTrackNo") );
			if (req.getParameter("ReceiveDate") != null && req.getParameter("ReceiveDate").length() > 0)
				createHw.setReceiveDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("ReceiveDate")) );
			if (req.getParameter("InstallDate") != null && req.getParameter("InstallDate").length() > 0)
				createHw.setInstallDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("InstallDate")) );
			if (req.getParameter("RemoveDate") != null && req.getParameter("RemoveDate").length() > 0)
				createHw.setRemoveDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("RemoveDate")) );
			createHw.setNotes( req.getParameter("Notes") );
			createHw.setInstallationNotes( req.getParameter("InstallNotes") );
			
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
            com.cannontech.database.data.stars.hardware.LMHardwareBase hw = new com.cannontech.database.data.stars.hardware.LMHardwareBase();
            com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hw.getLMHardwareBase();
            com.cannontech.database.db.stars.hardware.InventoryBase invDB = hw.getInventoryBase();
            
            invDB.setAccountID( new Integer(liteAcctInfo.getCustomerAccount().getAccountID()) );
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
            hwDB.setManufacturerSerialNumber( createHw.getManufactureSerialNumber() );
            hwDB.setLMHardwareTypeID( new Integer(createHw.getLMDeviceType().getEntryID()) );
            hw.setEnergyCompanyID( new Integer(energyCompanyID) );
            hw = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
            		Transaction.createTransaction( Transaction.INSERT, hw ).execute();
            
            // Add "Install" to hardware events
        	com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
        	com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
        	com.cannontech.database.db.stars.event.LMCustomerEventBase eventBaseDB = event.getLMCustomerEventBase();
            
            Integer invID = hw.getInventoryBase().getInventoryID();
            int hwEventEntryID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE ).getEntryID();
            int installActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL ).getEntryID();
        	
        	eventBaseDB.setEventTypeID( new Integer(hwEventEntryID) );
            eventBaseDB.setActionID( new Integer(installActID) );
            if (createHw.getInstallDate() != null)
	            eventBaseDB.setEventDateTime( createHw.getInstallDate() );
            eventBaseDB.setNotes( createHw.getInstallationNotes() );
            eventDB.setInventoryID( invID );
            event.setEnergyCompanyID( new Integer(energyCompanyID) );
            event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
            		Transaction.createTransaction( Transaction.INSERT, event ).execute();
			
            for (int i = 0; i < createHw.getStarsLMHardwareConfigCount(); i++) {
            	StarsLMHardwareConfig starsConfig = createHw.getStarsLMHardwareConfig(i);
            	com.cannontech.database.db.stars.hardware.LMHardwareConfiguration config =
            			new com.cannontech.database.db.stars.hardware.LMHardwareConfiguration();
            	config.setInventoryID( invID );
            	config.setApplianceID( new Integer(starsConfig.getApplianceID()) );
            	config.setAddressingGroupID( new Integer(starsConfig.getGroupID()) );
            	config = (com.cannontech.database.db.stars.hardware.LMHardwareConfiguration)
            			Transaction.createTransaction( Transaction.INSERT, config ).execute();
            	
            	for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
            		LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
            		if (liteApp.getApplianceID() == starsConfig.getApplianceID()) {
            			liteApp.setInventoryID( invID.intValue() );
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
            
            LiteLMHardwareBase liteHw = (LiteLMHardwareBase) StarsLiteFactory.createLite( hw );
            ArrayList lmHardwareList = energyCompany.getAllLMHardwares();
            synchronized (lmHardwareList) { lmHardwareList.add( liteHw ); }
            liteAcctInfo.getInventories().add( new Integer(liteHw.getInventoryID()) );
            
            // If this is a thermostat, add lite object for thermostat settings
        	if (ServerUtils.isOneWayThermostat(liteHw, energyCompany)) {
            	liteHw.setThermostatSettings( energyCompany.getThermostatSettings(liteHw, false) );
        	}
        	else if (ServerUtils.isTwoWayThermostat(liteHw, energyCompany)) {
        		populateThermostatTables( liteHw.getInventoryID(), energyCompany );
            	liteHw.setThermostatSettings( energyCompany.getThermostatSettings(liteHw, true) );
	            java.util.ArrayList accountList = energyCompany.getAccountsWithGatewayEndDevice();
	            synchronized (accountList) {
	            	if (!accountList.contains( liteAcctInfo )) accountList.add( liteAcctInfo );
	            }
        	}

            // Send config command
            YukonSwitchCommandAction.sendConfigCommand( energyCompany, invID.intValue(), false );
            
            StarsLMHardware starsHw = StarsLiteFactory.createStarsLMHardware( liteHw, energyCompanyID );
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
			int idx = -1;
			for (idx = starsInvs.getStarsLMHardwareCount() - 1; idx >= 0; idx--) {
				StarsLMHardware starsHw = starsInvs.getStarsLMHardware(idx);
				if (starsHw.getLMDeviceType().getContent().compareTo( hw.getLMDeviceType().getContent() ) <= 0)
					break;
			}
			starsInvs.addStarsLMHardware( idx+1, hw );
			
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
			
			if (session.getAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD ) == null)
				session.setAttribute( ServletUtils.ATT_REDIRECT, "/operator/Consumer/Inventory.jsp?InvNo=" + String.valueOf(idx+1) );
			session.removeAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD );
			
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
		
		return com.cannontech.common.util.CtiUtilities.NONE_ID;
	}
	
	public static void populateThermostatTables(int inventoryID, LiteStarsEnergyCompany energyCompany) {
		com.cannontech.database.data.multi.MultiDBPersistent multiDB =
				new com.cannontech.database.data.multi.MultiDBPersistent();
		
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
				com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry entry =
						new com.cannontech.database.db.stars.hardware.LMThermostatSeasonEntry();
				StarsLiteFactory.setLMThermostatSeasonEntry( entry, liteEntry );
				entry.setEntryID( null );
				season.getLMThermostatSeasonEntries().add( entry );
			}
			
			multiDB.getDBPersistentVector().add( season );
		}
		
		try {
			Transaction.createTransaction( Transaction.INSERT, multiDB ).execute();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
