package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.InventoryManager;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsLMHardware;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardware;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareResponse;
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

			StarsUpdateLMHardware updateHw = new StarsUpdateLMHardware();
			if (req.getParameter("SerialNo") != null) {
				// This comes from operator side
				InventoryManager.setStarsLMHardware( updateHw, req );
			}
			else {
				// This comes from consumer side
				updateHw.setInventoryID( Integer.parseInt(req.getParameter("InvID")) );
				updateHw.setDeviceLabel( req.getParameter("DeviceLabel") );
			}
			
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
            LiteStarsLMHardware liteHw = energyCompany.getLMHardware( updateHw.getInventoryID(), true );
            
            com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
            		(com.cannontech.database.data.stars.hardware.LMHardwareBase) StarsLiteFactory.createDBPersistent( liteHw );
            
            boolean fromOperator = (updateHw.getManufactureSerialNumber() != null);
            if (fromOperator) {
	            hw.getLMHardwareBase().setManufacturerSerialNumber( updateHw.getManufactureSerialNumber() );
	            hw.getInventoryBase().setAlternateTrackingNumber( updateHw.getAltTrackingNumber() );
	            if (updateHw.getInstallDate() != null)
		            hw.getInventoryBase().setInstallDate( updateHw.getInstallDate() );
		        else
		        	hw.getInventoryBase().setInstallDate( new Date(0) );
		        if (updateHw.getReceiveDate() != null)
		            hw.getInventoryBase().setReceiveDate( updateHw.getReceiveDate() );
		        else
		        	hw.getInventoryBase().setReceiveDate( new Date(0) );
		        if (updateHw.getRemoveDate() != null)
		            hw.getInventoryBase().setRemoveDate( updateHw.getRemoveDate() );
		        else
		        	hw.getInventoryBase().setRemoveDate( new Date(0) );
	            hw.getInventoryBase().setNotes( updateHw.getNotes() );
	            hw.getInventoryBase().setInstallationCompanyID( new Integer(updateHw.getInstallationCompany().getEntryID()) );
	            if (updateHw.getDeviceLabel().trim().length() > 0)
	            	hw.getInventoryBase().setDeviceLabel( updateHw.getDeviceLabel() );
	            else
	            	hw.getInventoryBase().setDeviceLabel( updateHw.getManufactureSerialNumber() );
            }
            else {
	            if (updateHw.getDeviceLabel().trim().length() > 0)
	            	hw.getInventoryBase().setDeviceLabel( updateHw.getDeviceLabel() );
	            else
	            	hw.getInventoryBase().setDeviceLabel( liteHw.getManufactureSerialNumber() );
            }
            
            hw = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
            		Transaction.createTransaction( Transaction.UPDATE, hw ).execute();
            StarsLiteFactory.setLiteLMHardwareBase( liteHw, hw );
            
            if (fromOperator) {
	            // Update the "install" event if necessary
	            int installEntryID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL).getEntryID();
	            
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
	            		break;
	            	}
	            }
            }
            
            StarsUpdateLMHardwareResponse resp = new StarsUpdateLMHardwareResponse();
            resp.setStarsLMHardware( StarsLiteFactory.createStarsLMHardware(liteHw, energyCompany) );
            
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
