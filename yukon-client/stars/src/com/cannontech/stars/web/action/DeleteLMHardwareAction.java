package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;
import java.util.*;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
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
public class DeleteLMHardwareAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			StarsDeleteLMHardware delHw = new StarsDeleteLMHardware();
			delHw.setInventoryID( Integer.parseInt(req.getParameter("InvID")) );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsDeleteLMHardware( delHw );
			
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
        	
        	StarsDeleteLMHardware delHw = reqOper.getStarsDeleteLMHardware();
        	
        	LiteLMHardwareBase liteHw = null;
        	ArrayList invIDs = liteAcctInfo.getInventories();
        	for (int i = 0; i < invIDs.size(); i++) {
        		if (((Integer) invIDs.get(i)).intValue() == delHw.getInventoryID()) {
        			liteHw = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() ).deleteLMHardware( delHw.getInventoryID() );
        			invIDs.remove(i);
        			break;
        		}
        	}
        	if (liteHw == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find the hardware information") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
        	
        	ArrayList liteApps = liteAcctInfo.getAppliances();
        	for (int i = 0; i < liteApps.size(); i++) {
        		LiteStarsAppliance liteApp = (LiteStarsAppliance) liteApps.get(i);
        		if (liteApp.getInventoryID() == liteHw.getInventoryID())
        			liteApp.setInventoryID( 0 );
        	}
        	
        	if (liteAcctInfo.getThermostatSettings().getInventoryID() == liteHw.getInventoryID())
        		liteAcctInfo.setThermostatSettings( null );
        	
        	com.cannontech.database.data.stars.hardware.InventoryBase inv =
        			new com.cannontech.database.data.stars.hardware.InventoryBase();
        	inv.setInventoryID( new Integer(liteHw.getInventoryID()) );
        	Transaction.createTransaction(Transaction.DELETE, inv).execute();
        	
            StarsSuccess success = new StarsSuccess();
            success.setDescription("Hardware deleted successfully");
            
            respOper.setStarsSuccess( success );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot delete the hardware") );
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
			
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			StarsDeleteLMHardware delHw = reqOper.getStarsDeleteLMHardware();
			
			StarsAppliances appliances = accountInfo.getStarsAppliances();
			for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
				if (appliances.getStarsAppliance(i).getInventoryID() == delHw.getInventoryID())
					appliances.getStarsAppliance(i).setInventoryID( 0 );
			}
			
			StarsInventories inventories = accountInfo.getStarsInventories();
			for (int i = 0; i < inventories.getStarsLMHardwareCount(); i++) {
				if (inventories.getStarsLMHardware(i).getInventoryID() == delHw.getInventoryID()) {
					inventories.removeStarsLMHardware(i);
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
