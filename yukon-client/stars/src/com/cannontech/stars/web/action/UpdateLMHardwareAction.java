package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteLMHardwareBase;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.DeviceStatus;
import com.cannontech.stars.xml.serialize.InstallationCompany;
import com.cannontech.stars.xml.serialize.LMDeviceType;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsLMHardware;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardware;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareResponse;
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
public class UpdateLMHardwareAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			java.util.Hashtable selectionLists = (java.util.Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );

			StarsUpdateLMHardware updateHw = new StarsUpdateLMHardware();
			
			LMDeviceType type = new LMDeviceType();
			type.setContent( req.getParameter("DeviceType") );
			updateHw.setLMDeviceType( type );	// Not Updatable (NU)
			
			Voltage volt = new Voltage();
			volt.setContent( req.getParameter("Voltage") );
			updateHw.setVoltage( volt );	// NU
			
			DeviceStatus status = new DeviceStatus();
			status.setEntryID( Integer.parseInt(req.getParameter("Status")) );
			updateHw.setDeviceStatus( status );		// NU
			
			InstallationCompany company = new InstallationCompany();
			company.setEntryID( Integer.parseInt(req.getParameter("ServiceCompany")) );
			StarsCustSelectionList companyList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.report.ServiceCompany.LISTNAME_SERVICECOMPANY );
			for (int i = 0; i < companyList.getStarsSelectionListEntryCount(); i++) {
				StarsSelectionListEntry entry = companyList.getStarsSelectionListEntry(i);
				if (entry.getEntryID() == company.getEntryID()) {
					company.setContent( entry.getContent() );
					break;
				}
			}
			updateHw.setInstallationCompany( company );
			
			updateHw.setInventoryID( Integer.parseInt(req.getParameter("InvID")) );
			updateHw.setCategory( "" );		// NU
			updateHw.setManufactureSerialNumber( req.getParameter("SerialNo") );
			updateHw.setAltTrackingNumber( req.getParameter("AltTrackNo") );
			if (req.getParameter("ReceiveDate") != null && req.getParameter("ReceiveDate").length() > 0)
				updateHw.setReceiveDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("ReceiveDate")) );
			else
				updateHw.setReceiveDate( new Date(0) );
			if (req.getParameter("InstallDate") != null && req.getParameter("InstallDate").length() > 0)
				updateHw.setInstallDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("InstallDate")) );
			else
				updateHw.setInstallDate( new Date(0) );
			if (req.getParameter("RemoveDate") != null && req.getParameter("RemoveDate").length() > 0)
				updateHw.setRemoveDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("RemoveDate")) );
			else
				updateHw.setRemoveDate( new Date(0) );
			updateHw.setNotes( req.getParameter("Notes") );
			updateHw.setInstallationNotes( req.getParameter("InstallNotes") );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsUpdateLMHardware( updateHw );
			
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
            
            StarsUpdateLMHardware updateHw = reqOper.getStarsUpdateLMHardware();
            LiteLMHardwareBase liteHw = energyCompany.getLMHardware( updateHw.getInventoryID(), true );
            
            com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
            		(com.cannontech.database.data.stars.hardware.LMHardwareBase) StarsLiteFactory.createDBPersistent( liteHw );
            hw.getLMHardwareBase().setManufacturerSerialNumber( updateHw.getManufactureSerialNumber() );
            hw.getInventoryBase().setAlternateTrackingNumber( updateHw.getAltTrackingNumber() );
            hw.getInventoryBase().setInstallDate( updateHw.getInstallDate() );
            hw.getInventoryBase().setReceiveDate( updateHw.getReceiveDate() );
            hw.getInventoryBase().setRemoveDate( updateHw.getRemoveDate() );
            hw.getInventoryBase().setNotes( updateHw.getNotes() );
            hw.getInventoryBase().setInstallationCompanyID( new Integer(updateHw.getInstallationCompany().getEntryID()) );
            
            hw = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
            		Transaction.createTransaction( Transaction.UPDATE, hw ).execute();
            StarsLiteFactory.setLiteLMHardware( liteHw, hw );
            
            // Update the "install" and "config" event if necessary
            int installEntryID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL).getEntryID();
            int configEntryID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CONFIG).getEntryID();
            
            ArrayList hwHist = liteHw.getLmHardwareHistory();
            for (int i = 0; i < hwHist.size(); i++) {
            	LiteLMHardwareEvent liteEvent = (LiteLMHardwareEvent) hwHist.get(i);
            	if (liteEvent.getActionID() == installEntryID) {
            		long installTime = liteEvent.getEventDateTime();
            		boolean timeChanged = Math.abs(installTime - updateHw.getInstallDate().getTime()) > 1000;
            		
            		if (timeChanged || !liteEvent.getNotes().equals( updateHw.getInstallationNotes() )) {
            			com.cannontech.database.data.stars.event.LMHardwareEvent event =
            					(com.cannontech.database.data.stars.event.LMHardwareEvent) StarsLiteFactory.createDBPersistent( liteEvent );
            			com.cannontech.database.db.stars.event.LMCustomerEventBase eventDB = event.getLMCustomerEventBase();
            			
            			eventDB.setEventDateTime( updateHw.getInstallDate() );
            			eventDB.setNotes( updateHw.getInstallationNotes() );
            			eventDB = (com.cannontech.database.db.stars.event.LMCustomerEventBase)
            					Transaction.createTransaction( Transaction.UPDATE, eventDB ).execute();
            					
            			StarsLiteFactory.setLiteLMCustomerEvent( liteEvent, eventDB );
            		}
            		
            		if (++i < hwHist.size()) {
		            	liteEvent = (LiteLMHardwareEvent) hwHist.get(i);
		            	if (liteEvent.getActionID() == configEntryID && Math.abs(installTime - liteEvent.getEventDateTime()) < 1000 && timeChanged) {
		            		// "Config" event happened at the same time as the "install event"
	            			com.cannontech.database.data.stars.event.LMHardwareEvent event =
	            					(com.cannontech.database.data.stars.event.LMHardwareEvent) StarsLiteFactory.createDBPersistent( liteEvent );
	            			com.cannontech.database.db.stars.event.LMCustomerEventBase eventDB = event.getLMCustomerEventBase();
	            			
	            			eventDB.setEventDateTime( updateHw.getInstallDate() );
	            			eventDB = (com.cannontech.database.db.stars.event.LMCustomerEventBase)
	            					Transaction.createTransaction( Transaction.UPDATE, eventDB ).execute();
	            					
	            			StarsLiteFactory.setLiteLMCustomerEvent( liteEvent, eventDB );
		            	}
            		}
            		
            		break;
            	}
            }
            
            StarsUpdateLMHardwareResponse resp = new StarsUpdateLMHardwareResponse();
            resp.setStarsLMHardware( StarsLiteFactory.createStarsLMHardware(liteHw, energyCompanyID) );
            
            respOper.setStarsUpdateLMHardwareResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update the hardware information") );
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
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            if (accountInfo == null)
            	return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
			
			StarsUpdateLMHardwareResponse resp = operation.getStarsUpdateLMHardwareResponse();
			StarsLMHardware hw = resp.getStarsLMHardware();
			
			StarsInventories starsInvs = accountInfo.getStarsInventories();
			for (int i = 0; i < starsInvs.getStarsLMHardwareCount(); i++) {
				if (starsInvs.getStarsLMHardware(i).getInventoryID() == hw.getInventoryID()) {
					starsInvs.setStarsLMHardware(i, hw);
					session.setAttribute( ServletUtils.ATT_REDIRECT, "/operator/Consumer/Inventory.jsp?InvNo=" + String.valueOf(i) );
					break;
				}
			}
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
