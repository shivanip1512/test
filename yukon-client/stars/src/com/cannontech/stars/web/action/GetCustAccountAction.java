package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPClient;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsGetCustomerAccountResponse;
import com.cannontech.stars.xml.serialize.StarsOperation;
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
public class GetCustAccountAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
            StarsGetCustomerAccount getAccount = new StarsGetCustomerAccount();
            int accountID = -1;
            if (req.getParameter("AccountID") != null)
	            try {
	            	accountID = Integer.parseInt( req.getParameter("AccountID") );
	            }
	            catch (NumberFormatException nfe) {}
            getAccount.setAccountID( accountID );
            
            StarsOperation operation = new StarsOperation();
            operation.setStarsGetCustomerAccount( getAccount );

            return SOAPUtil.buildSOAPMessage( operation );
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
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

			LiteYukonUser liteUser = (LiteYukonUser) session.getAttribute( ServletUtils.ATT_YUKON_USER );
            if (liteUser == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            StarsYukonUser user = SOAPServer.getStarsYukonUser( liteUser );
        	LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        	
            StarsGetCustomerAccount getAccount = reqOper.getStarsGetCustomerAccount();
            LiteStarsCustAccountInformation liteAcctInfo = null;
            
            if (getAccount.getAccountID() == -1) {
            	/* Get the first customer account after user login */
				int[] accountIDs = user.getCustomerAccountIDs();
				if (accountIDs == null || accountIDs.length == 0) {
	                respOper.setStarsFailure( StarsFactory.newStarsFailure(
	                		StarsConstants.FAILURE_CODE_OPERATION_FAILED, "No account information found for this customer") );
	                return SOAPUtil.buildSOAPMessage( respOper );
				}
				
				liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[0], true );
            }
            else
	            liteAcctInfo = energyCompany.getCustAccountInformation( getAccount.getAccountID(), true );
            
            if (liteAcctInfo == null) {
                respOper.setStarsFailure( StarsFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            if (liteAcctInfo.hasTwoWayThermostat(energyCompany)) {
            	// Get up-to-date thermostat settings
				energyCompany.updateThermostatSettings( liteAcctInfo );
            }
            
			session.setAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo );
    		
    		StarsCustAccountInformation starsAcctInfo = null;
    		if (SOAPServer.isClientLocal()) {
    			starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteAcctInfo );
				session.setAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, starsAcctInfo );
    		}
        	else
				starsAcctInfo = StarsLiteFactory.createStarsCustAccountInformation(
					liteAcctInfo, energyCompany, ServerUtils.isOperator(user) );
			
			StarsGetCustomerAccountResponse resp = new StarsGetCustomerAccountResponse();
			resp.setStarsCustAccountInformation( starsAcctInfo );
			
            respOper.setStarsGetCustomerAccountResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot get the customer account information") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	CTILogger.error( e2.getMessage(), e2 );
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
			
            StarsGetCustomerAccountResponse resp = operation.getStarsGetCustomerAccountResponse();
            StarsCustAccountInformation accountInfo = resp.getStarsCustAccountInformation();
            if (accountInfo == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			if (!SOAPClient.isServerLocal())
				session.setAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, accountInfo );
            
            return 0;
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
