package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsAppliances;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteAppliance;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsLMPrograms;
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
public class DeleteApplianceAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			StarsDeleteAppliance delApp = new StarsDeleteAppliance();
			delApp.setApplianceID( Integer.parseInt(req.getParameter("AppID")) );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsDeleteAppliance( delApp );
			
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
        	
        	StarsDeleteAppliance delApp = reqOper.getStarsDeleteAppliance();
        	
        	LiteStarsAppliance liteApp = null;
        	ArrayList liteApps = liteAcctInfo.getAppliances();
        	for (int i = 0; i < liteApps.size(); i++) {
        		LiteStarsAppliance lApp = (LiteStarsAppliance) liteApps.get(i);
        		if (lApp.getApplianceID() == delApp.getApplianceID()) {
        			liteApp = lApp;
        			break;
        		}
        	}
        	if (liteApp == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find the appliance information") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
        	
        	if (liteApp.getLmProgramID() > 0) {
				// Add "termination" event to the enrolled program
				LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
				
				com.cannontech.database.data.stars.event.LMProgramEvent event =
						new com.cannontech.database.data.stars.event.LMProgramEvent();
				com.cannontech.database.db.stars.event.LMProgramEvent eventDB = event.getLMProgramEvent();
				com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
				
				event.setEnergyCompanyID( new Integer(user.getEnergyCompanyID()) );
				eventDB.setAccountID( new Integer(liteAcctInfo.getCustomerAccount().getAccountID()) );
				eventDB.setLMProgramID( new Integer(liteApp.getLmProgramID()) );
				eventBase.setEventTypeID( new Integer(energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMPROGRAM).getEntryID()) );
				eventBase.setActionID( new Integer(energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION).getEntryID()) );
				eventBase.setEventDateTime( new Date() );
				Transaction.createTransaction(Transaction.INSERT, event).execute();
	        	
	        	ArrayList liteProgs = liteAcctInfo.getLmPrograms();
	        	for (int i = 0; i < liteProgs.size(); i++) {
	        		LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteProgs.get(i);
	        		if (liteProg.getLmProgram().getProgramID() == liteApp.getLmProgramID()) {
	        			liteProgs.remove( liteProg );
	        			break;
	        		}
	        	}
        	}
        	
        	com.cannontech.database.data.stars.appliance.ApplianceBase app = new com.cannontech.database.data.stars.appliance.ApplianceBase();
        	app.setApplianceID( new Integer(delApp.getApplianceID()) );
        	Transaction.createTransaction(Transaction.DELETE, app).execute();
        	liteApps.remove( liteApp );
        	
            StarsSuccess success = new StarsSuccess();
            success.setDescription("Appliance deleted successfully");
            
            respOper.setStarsSuccess( success );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot delete the appliance") );
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
			StarsDeleteAppliance delApp = reqOper.getStarsDeleteAppliance();
			
			StarsAppliances appliances = accountInfo.getStarsAppliances();
			for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
				StarsAppliance app = appliances.getStarsAppliance(i);
				if (app.getApplianceID() == delApp.getApplianceID()) {
					appliances.removeStarsAppliance(i);
					
					StarsLMPrograms programs = accountInfo.getStarsLMPrograms();
					for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
						if (programs.getStarsLMProgram(j).getProgramID() == app.getLmProgramID()) {
							programs.removeStarsLMProgram(j);
							break;
						}
					}
					
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
