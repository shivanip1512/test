package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;
import java.util.*;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.util.*;
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
public class UpdateLMHardwareAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			if (operator == null) return null;
			java.util.Hashtable selectionLists = (java.util.Hashtable) operator.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );

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

			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
            if (operator == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) operator.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
        	
        	int energyCompanyID = (int) operator.getEnergyCompanyID();
            Hashtable selectionLists = SOAPServer.getAllSelectionLists( energyCompanyID );
            
            StarsUpdateLMHardware updateHw = reqOper.getStarsUpdateLMHardware();
            LiteLMHardwareBase liteHw = SOAPServer.getLMHardware( energyCompanyID, updateHw.getInventoryID(), true );
            
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
            
            // Update the "install" event if necessary
            int installEntryID = StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION ),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_INSTALL ).getEntryID();
            
            ArrayList hwHist = liteHw.getLmHardwareHistory();
            for (int i = 0; i < hwHist.size(); i++) {
            	LiteLMHardwareEvent liteEvent = (LiteLMHardwareEvent) hwHist.get(i);
            	if (liteEvent.getActionID() == installEntryID) {
            		if (!liteEvent.getNotes().equals( updateHw.getInstallationNotes() )) {
            			com.cannontech.database.data.stars.event.LMHardwareEvent event =
            					(com.cannontech.database.data.stars.event.LMHardwareEvent) StarsLiteFactory.createDBPersistent( liteEvent );
            			com.cannontech.database.db.stars.event.LMCustomerEventBase eventDB = event.getLMCustomerEventBase();
            			
            			eventDB.setNotes( updateHw.getInstallationNotes() );
            			eventDB = (com.cannontech.database.db.stars.event.LMCustomerEventBase)
            					Transaction.createTransaction( Transaction.UPDATE, eventDB ).execute();
            					
            			StarsLiteFactory.setLiteLMCustomerEvent( liteEvent, eventDB );
            		}
            		break;
            	}
            }
            
            StarsUpdateLMHardwareResponse resp = new StarsUpdateLMHardwareResponse();
            resp.setStarsLMHardware( StarsLiteFactory.createStarsLMHardware(liteHw, selectionLists) );
            
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
            	LiteServiceCompany liteCompany = SOAPServer.getServiceCompany( energyCompanyID, liteHw.getInstallationCompanyID() );
            	StarsServiceCompany starsCompany = StarsLiteFactory.createStarsServiceCompany( liteCompany, energyCompanyID );
            	resp.setStarsServiceCompany( starsCompany );
            	ServerUtils.updateServiceCompanies( liteAcctInfo, energyCompanyID );
            }
            
            respOper.setStarsUpdateLMHardwareResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
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
			
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					operator.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            if (accountInfo == null)
            	return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
			
			StarsUpdateLMHardwareResponse resp = operation.getStarsUpdateLMHardwareResponse();
			StarsLMHardware hw = resp.getStarsLMHardware();
			
			StarsInventories starsInvs = accountInfo.getStarsInventories();
			for (int i = 0; i < starsInvs.getStarsLMHardwareCount(); i++) {
				if (starsInvs.getStarsLMHardware(i).getInventoryID() == hw.getInventoryID()) {
					starsInvs.setStarsLMHardware(i, hw);
					session.setAttribute( ServletUtils.ATT_REDIRECT, "/OperatorDemos/Consumer/Inventory.jsp?InvNo=" + String.valueOf(i) );
					break;
				}
			}
			
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

}
