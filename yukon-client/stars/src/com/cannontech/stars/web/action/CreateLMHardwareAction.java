package com.cannontech.stars.web.action;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsOperator;
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
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			if (operator == null) return null;
			java.util.Hashtable selectionLists = (java.util.Hashtable) operator.getAttribute( "CUSTOMER_SELECTION_LIST" );

			StarsCreateLMHardware createHw = new StarsCreateLMHardware();
			
			LMDeviceType type = new LMDeviceType();
			type.setEntryID( Integer.parseInt(req.getParameter("DeviceType")) );
			StarsCustSelectionList typeList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_DEVICETYPE );
			for (int i = 0; i < typeList.getStarsSelectionListEntryCount(); i++) {
				StarsSelectionListEntry entry = typeList.getStarsSelectionListEntry(i);
				if (entry.getEntryID() == type.getEntryID()) {
					type.setContent( entry.getContent() );
					break;
				}
			}
			createHw.setLMDeviceType( type );
			
			Voltage volt = new Voltage();
			volt.setEntryID( Integer.parseInt(req.getParameter("Voltage")) );
			StarsCustSelectionList voltList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_INVENTORYVOLTAGE );
			for (int i = 0; i < voltList.getStarsSelectionListEntryCount(); i++) {
				StarsSelectionListEntry entry = voltList.getStarsSelectionListEntry(i);
				if (entry.getEntryID() == volt.getEntryID()) {
					volt.setContent( entry.getContent() );
					break;
				}
			}
			createHw.setVoltage( volt );
			
			DeviceStatus status = new DeviceStatus();
			status.setEntryID( Integer.parseInt(req.getParameter("Status")) );
			StarsCustSelectionList statusList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_DEVICESTATUS );
			for (int i = 0; i < statusList.getStarsSelectionListEntryCount(); i++) {
				StarsSelectionListEntry entry = statusList.getStarsSelectionListEntry(i);
				if (entry.getEntryID() == status.getEntryID()) {
					status.setContent( entry.getContent() );
					break;
				}
			}
			createHw.setDeviceStatus( status );
			
			InstallationCompany company = new InstallationCompany();
			company.setEntryID( Integer.parseInt(req.getParameter("ServiceCompany")) );
			StarsCustSelectionList companyList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.report.ServiceCompany.LISTNAME_SERVICECOMPANY );
			for (int i = 0; i < companyList.getStarsSelectionListEntryCount(); i++) {
				StarsSelectionListEntry entry = companyList.getStarsSelectionListEntry(i);
				if (entry.getEntryID() == company.getEntryID()) {
					status.setContent( entry.getContent() );
					break;
				}
			}
			createHw.setInstallationCompany( company );
			
			createHw.setCategory( StarsLMHwFactory.getCategory(type, selectionLists).getContent() );
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
        }

		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsOperation respOper = new StarsOperation();

			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
            if (operator == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsCustAccountInformation accountInfo = (LiteStarsCustAccountInformation) operator.getAttribute( "CUSTOMER_ACCOUNT_INFORMATION" );
        	if (accountInfo == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
        	
        	Integer energyCompanyID = new Integer((int) operator.getEnergyCompanyID());
            Hashtable selectionLists = SOAPServer.getAllSelectionLists( energyCompanyID );
            
            StarsCreateLMHardware createHw = reqOper.getStarsCreateLMHardware();
            com.cannontech.database.data.stars.hardware.LMHardwareBase hw = new com.cannontech.database.data.stars.hardware.LMHardwareBase();
            com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hw.getLMHardwareBase();
            com.cannontech.database.db.stars.hardware.InventoryBase invDB = hw.getInventoryBase();
            
            invDB.setAccountID( new Integer(accountInfo.getCustomerAccount().getAccountID()) );
            invDB.setInstallationCompanyID( new Integer(createHw.getInstallationCompany().getEntryID()) );
            invDB.setCategoryID( new Integer(StarsLMHwFactory.getCategory(createHw.getLMDeviceType(), selectionLists).getEntryID()) );
            invDB.setReceiveDate( createHw.getReceiveDate() );
            invDB.setInstallDate( createHw.getInstallDate() );
            invDB.setRemoveDate( createHw.getRemoveDate() );
            invDB.setAlternateTrackingNumber( createHw.getAltTrackingNumber() );
            invDB.setVoltageID( new Integer(createHw.getVoltage().getEntryID()) );
            invDB.setNotes( createHw.getNotes() );
            
            hwDB.setManufacturerSerialNumber( createHw.getManufactureSerialNumber() );
            hwDB.setLMHardwareTypeID( new Integer(createHw.getLMDeviceType().getEntryID()) );
            
            hw.setEnergyCompanyID( energyCompanyID );
            hw = (com.cannontech.database.data.stars.hardware.LMHardwareBase) Transaction.createTransaction( Transaction.INSERT, hw ).execute();
            
            LiteLMHardware hwLite = (LiteLMHardware) StarsLiteFactory.createLite( hw );
            StarsLMHardware starsHw = (StarsLMHardware) StarsLMHwFactory.newStarsLMHw( createHw, StarsLMHardware.class );
            starsHw.setInventoryID( hw.getLMHardwareBase().getInventoryID().intValue() );
            
            // Add "Install event" to the LMHardwareEvent table
        	com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
        	com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
        	com.cannontech.database.db.stars.event.LMCustomerEventBase eventBaseDB = event.getLMCustomerEventBase();
        	
        	eventBaseDB.setEventTypeID( new Integer(StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMEREVENT ),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_LMHARDWAREEVENT).getEntryID()) );
            eventBaseDB.setActionID( new Integer(StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION ),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_INSTALL ).getEntryID()) );
            eventBaseDB.setEventDateTime( createHw.getInstallDate() );
            eventBaseDB.setNotes( createHw.getInstallationNotes() );
            
            eventDB.setInventoryID( hwDB.getInventoryID() );
            event.setEnergyCompanyID( energyCompanyID );
            
            event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
            		Transaction.createTransaction( Transaction.INSERT, event ).execute();
            
            LiteLMCustomerEvent eventLite = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
            hwLite.setLmHardwareHistory( new ArrayList() );
            hwLite.getLmHardwareHistory().add( eventLite );
            
            StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
            starsEvent.setEventAction( StarsCustListEntryFactory.getStarsCustListEntry(
            		(StarsCustSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
            		eventLite.getActionID()).getContent() );
            starsEvent.setEventDateTime( new Date(eventLite.getEventDateTime()) );
            starsEvent.setNotes( eventLite.getNotes() );
            starsEvent.setYukonDefinition( StarsCustListEntryFactory.getStarsCustListEntry(
            		(StarsCustSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
            		eventLite.getActionID()).getYukonDefinition() );
            starsHw.setStarsLMHardwareHistory( new StarsLMHardwareHistory() );
            starsHw.getStarsLMHardwareHistory().addStarsLMHardwareEvent( starsEvent );
            
            // If the device status is set to "Available", then add "Activation completed" event to the LMHardwareEvent table
            if (createHw.getDeviceStatus().getEntryID() == StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_DEVICESTATUS ),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_DEVSTAT_AVAIL).getEntryID())
            {
            	event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
        		eventDB = event.getLMHardwareEvent();
        		eventBaseDB = event.getLMCustomerEventBase();
        	
	        	eventBaseDB.setEventTypeID( new Integer(StarsCustListEntryFactory.getStarsCustListEntry(
	            		(LiteCustomerSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMEREVENT ),
	            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_LMHARDWAREEVENT).getEntryID()) );
	            eventBaseDB.setActionID( new Integer(StarsCustListEntryFactory.getStarsCustListEntry(
	            		(LiteCustomerSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
	            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_COMPLETED).getEntryID()) );
	            eventBaseDB.setEventDateTime( createHw.getInstallDate() );
	            eventBaseDB.setNotes( "Activated while installation" );
	            
	            eventDB.setInventoryID( hwDB.getInventoryID() );
	            event.setEnergyCompanyID( energyCompanyID );
	            
	            event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
	            		Transaction.createTransaction( Transaction.INSERT, event ).execute();
            
	            eventLite = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
	            hwLite.getLmHardwareHistory().add( eventLite );
	            
	            starsEvent = new StarsLMHardwareEvent();
	            starsEvent.setEventAction( StarsCustListEntryFactory.getStarsCustListEntry(
	            		(StarsCustSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
	            		eventLite.getActionID()).getContent() );
	            starsEvent.setEventDateTime( new Date(eventLite.getEventDateTime()) );
	            starsEvent.setNotes( eventLite.getNotes() );
	            starsEvent.setYukonDefinition( StarsCustListEntryFactory.getStarsCustListEntry(
	            		(StarsCustSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
	            		eventLite.getActionID()).getYukonDefinition() );
	            starsHw.getStarsLMHardwareHistory().addStarsLMHardwareEvent( starsEvent );
            }
            
            SOAPServer.getAllLMHardwares( energyCompanyID ).add( hwLite );
            
            StarsCreateLMHardwareResponse resp = new StarsCreateLMHardwareResponse();
            resp.setStarsLMHardware( starsHw );
            respOper.setStarsCreateLMHardwareResponse( resp );
            
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
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
			if (failure != null) return failure.getStatusCode();
			
			StarsCreateLMHardwareResponse resp = operation.getStarsCreateLMHardwareResponse();
			StarsLMHardware hw = resp.getStarsLMHardware();
			
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					operator.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION");
            if (accountInfo == null)
            	return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
			
			accountInfo.getStarsInventories().addStarsLMHardware( hw );
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
