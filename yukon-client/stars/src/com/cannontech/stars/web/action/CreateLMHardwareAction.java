package com.cannontech.stars.web.action;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.*;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.util.*;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.util.*;
import com.cannontech.stars.xml.*;

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
			
			LMDeviceType type = (LMDeviceType) StarsCustListEntryFactory.newStarsCustListEntry(
					StarsCustListEntryFactory.getStarsCustListEntryByID(
						selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, Integer.parseInt(req.getParameter("DeviceType"))),
					LMDeviceType.class );
			createHw.setLMDeviceType( type );
			
			Voltage volt = (Voltage) StarsCustListEntryFactory.newStarsCustListEntry(
					StarsCustListEntryFactory.getStarsCustListEntryByID(
						selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE, Integer.parseInt(req.getParameter("Voltage"))),
					Voltage.class );
			createHw.setVoltage( volt );
			
			DeviceStatus status = (DeviceStatus) StarsCustListEntryFactory.newStarsCustListEntry(
					StarsCustListEntryFactory.getStarsCustListEntryByID(
						selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS, Integer.parseInt(req.getParameter("Status"))),
					DeviceStatus.class );
			createHw.setDeviceStatus( status );
			
			InstallationCompany company = (InstallationCompany) StarsCustListEntryFactory.newStarsCustListEntry(
					StarsCustListEntryFactory.getStarsCustListEntryByID(
						selectionLists, com.cannontech.database.db.stars.report.ServiceCompany.LISTNAME_SERVICECOMPANY, Integer.parseInt(req.getParameter("ServiceCompany"))),
					InstallationCompany.class );
			createHw.setInstallationCompany( company );
			
			createHw.setCategory( "" );
			createHw.setManufactureSerialNumber( req.getParameter("SerialNo") );
			createHw.setAltTrackingNumber( req.getParameter("AltTrackNo") );
			if (req.getParameter("ReceiveDate") != null && req.getParameter("ReceiveDate").length() > 0)
				createHw.setReceiveDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("ReceiveDate")) );
			else
				createHw.setReceiveDate( new Date(0) );
			if (req.getParameter("InstallDate") != null && req.getParameter("InstallDate").length() > 0)
				createHw.setInstallDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("InstallDate")) );
			else
				createHw.setInstallDate( new Date(0) );
			if (req.getParameter("RemoveDate") != null && req.getParameter("RemoveDate").length() > 0)
				createHw.setRemoveDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("RemoveDate")) );
			else
				createHw.setRemoveDate( new Date(0) );
			createHw.setNotes( req.getParameter("Notes") );
			createHw.setInstallationNotes( req.getParameter("InstallNotes") );
			
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
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
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
            invDB.setCategoryID( new Integer(getInventoryCategory(createHw.getLMDeviceType(), energyCompanyID).getEntryID()) );
            invDB.setReceiveDate( createHw.getReceiveDate() );
            invDB.setInstallDate( createHw.getInstallDate() );
            invDB.setRemoveDate( createHw.getRemoveDate() );
            invDB.setAlternateTrackingNumber( createHw.getAltTrackingNumber() );
            invDB.setVoltageID( new Integer(createHw.getVoltage().getEntryID()) );
            invDB.setNotes( createHw.getNotes() );
            
            hwDB.setManufacturerSerialNumber( createHw.getManufactureSerialNumber() );
            hwDB.setLMHardwareTypeID( new Integer(createHw.getLMDeviceType().getEntryID()) );
            
            hw.setEnergyCompanyID( new Integer(energyCompanyID) );
            hw = (com.cannontech.database.data.stars.hardware.LMHardwareBase) Transaction.createTransaction( Transaction.INSERT, hw ).execute();
            
            LiteLMHardwareBase liteHw = (LiteLMHardwareBase) StarsLiteFactory.createLite( hw );
            StarsLMHardware starsHw = StarsLiteFactory.createStarsLMHardware( liteHw, energyCompanyID );
            
            // Add "Install" to hardware events
        	com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
        	com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
        	com.cannontech.database.db.stars.event.LMCustomerEventBase eventBaseDB = event.getLMCustomerEventBase();
            
            int hwEventEntryID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE ).getEntryID();
            int installActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL ).getEntryID();
        	
        	eventBaseDB.setEventTypeID( new Integer(hwEventEntryID) );
            eventBaseDB.setActionID( new Integer(installActID) );
            eventBaseDB.setEventDateTime( createHw.getInstallDate() );
            eventBaseDB.setNotes( createHw.getInstallationNotes() );
            
            eventDB.setInventoryID( hwDB.getInventoryID() );
            event.setEnergyCompanyID( new Integer(energyCompanyID) );
            
            event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
            		Transaction.createTransaction( Transaction.INSERT, event ).execute();
            
            LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
            liteHw.setLmHardwareHistory( new ArrayList() );
            liteHw.getLmHardwareHistory().add( liteEvent );
            
            StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
            StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteEvent );
            starsHw.setStarsLMHardwareHistory( new StarsLMHardwareHistory() );
            starsHw.getStarsLMHardwareHistory().addStarsLMHardwareEvent( starsEvent );
            
            // If the device status is set to "Available", then add "Config" to hardware events
            YukonListEntry availStatEntry = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL );
            int configActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CONFIG ).getEntryID();
            
            if (createHw.getDeviceStatus().getEntryID() == availStatEntry.getEntryID())
            {
            	event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
        		eventDB = event.getLMHardwareEvent();
        		eventBaseDB = event.getLMCustomerEventBase();
        	
	        	eventBaseDB.setEventTypeID( new Integer(hwEventEntryID) );
	            eventBaseDB.setActionID( new Integer(configActID) );
	            eventBaseDB.setEventDateTime( createHw.getInstallDate() );
	            eventBaseDB.setNotes( "Configured while installation" );
	            
	            eventDB.setInventoryID( hwDB.getInventoryID() );
	            event.setEnergyCompanyID( new Integer(energyCompanyID) );
	            
	            event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
	            		Transaction.createTransaction( Transaction.INSERT, event ).execute();
            
	            liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
	            liteHw.getLmHardwareHistory().add( liteEvent );
	            
	            starsEvent = new StarsLMHardwareEvent();
	            StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteEvent );
	            starsHw.getStarsLMHardwareHistory().addStarsLMHardwareEvent( starsEvent );
	            
	            DeviceStatus status = new DeviceStatus();
	            StarsLiteFactory.setStarsCustListEntry( status, availStatEntry );
	            starsHw.setDeviceStatus( status );
            }
            
            ArrayList lmHardwareList = energyCompany.getAllLMHardwares();
            synchronized (lmHardwareList) { lmHardwareList.add( liteHw ); }
            liteAcctInfo.getInventories().add( new Integer(liteHw.getInventoryID()) );
            
            // If this is a thermostat, add lite object for thermostat settings
			int thermTypeID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_THERMOSTAT ).getEntryID();
        	if (liteHw.getLmHardwareTypeID() == thermTypeID)
            	liteAcctInfo.setThermostatSettings( SOAPServer.getEnergyCompany( energyCompanyID ).getThermostatSettings(liteHw.getInventoryID()) );
            
            StarsCreateLMHardwareResponse resp = new StarsCreateLMHardwareResponse();
            resp.setStarsLMHardware( starsHw );
            
            boolean newServiceCompany = true;
            if (liteAcctInfo.getServiceCompanies() == null)
            	liteAcctInfo.setServiceCompanies( new ArrayList() );
            for (int i = 0; i < liteAcctInfo.getServiceCompanies().size(); i++) {
            	int companyID = ((Integer) liteAcctInfo.getServiceCompanies().get(i)).intValue();
            	if (liteHw.getInstallationCompanyID() == companyID) {
            		newServiceCompany = false;
            		break;
            	}
            }
            
            if (newServiceCompany) {
            	LiteServiceCompany liteCompany = energyCompany.getServiceCompany( liteHw.getInstallationCompanyID() );
            	StarsServiceCompany starsCompany = StarsLiteFactory.createStarsServiceCompany( liteCompany, energyCompanyID );
            	resp.setStarsServiceCompany( starsCompany );
            	liteAcctInfo.getServiceCompanies().add( new Integer(liteCompany.getCompanyID()) );
            }
            
            respOper.setStarsCreateLMHardwareResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
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
			int i = -1;
			for (i = starsInvs.getStarsLMHardwareCount() - 1; i >= 0; i--) {
				StarsLMHardware starsHw = starsInvs.getStarsLMHardware(i);
				if (starsHw.getLMDeviceType().getContent().compareTo( hw.getLMDeviceType().getContent() ) <= 0)
					break;
			}
			starsInvs.addStarsLMHardware( i+1, hw );
			session.setAttribute( ServletUtils.ATT_REDIRECT, "/operator/Consumer/Inventory.jsp?InvNo=" + String.valueOf(i+1) );
			
			StarsServiceCompany company = resp.getStarsServiceCompany();
			if (company != null)
				accountInfo.getStarsServiceCompanies().addStarsServiceCompany( company );
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static YukonListEntry getInventoryCategory(StarsCustListEntry deviceType, int energyCompanyID) {
		if (deviceType.getContent().startsWith("LCR")	// LCR-XXXX
			|| deviceType.getContent().startsWith("Thermostat"))	// Thermostat
		{
			return SOAPServer.getEnergyCompany(energyCompanyID).getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC);
		}
		
		return null;
	}

}
