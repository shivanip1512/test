package com.cannontech.stars.web.action;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.data.stars.customer.CustomerAccount;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsUser;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsCustAccountInformationFactory;
import com.cannontech.stars.xml.StarsFailureFactory;
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

			StarsOperator operator = (StarsOperator) session.getAttribute( "OPERATOR" );
            StarsUser user = (StarsUser) session.getAttribute("USER");
            if (operator == null && user == null) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            int energyCompanyID = (operator != null) ? (int) operator.getEnergyCompanyID() : user.getEnergyCompanyID();

            StarsGetCustomerAccount getAccount = reqOper.getStarsGetCustomerAccount();
            LiteStarsCustAccountInformation liteAcctInfo = null;
            
            if (getAccount.getAccountID() == -1) {
            	/* Get the first customer account after user login */
				com.cannontech.database.db.stars.customer.CustomerAccount[] accounts = user.getCustomerAccounts();
				if (accounts == null || accounts.length == 0) {
	                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
	                		StarsConstants.FAILURE_CODE_OPERATION_FAILED, "No account information found for this customer") );
	                return SOAPUtil.buildSOAPMessage( respOper );
				}
				
				liteAcctInfo = SOAPServer.getCustAccountInformation( energyCompanyID, accounts[0].getAccountID().intValue(), true );
            }
            else
	            liteAcctInfo = SOAPServer.getCustAccountInformation( energyCompanyID, getAccount.getAccountID(), true );
            
            if (liteAcctInfo == null) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            if (operator != null)
            	operator.setAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo );
            else
        		user.setAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo );
        	
			StarsCustAccountInformation starsAcctInfo = StarsLiteFactory.createStarsCustAccountInformation(
					liteAcctInfo, energyCompanyID, (operator != null) );
			
			StarsGetCustomerAccountResponse resp = new StarsGetCustomerAccountResponse();
			resp.setStarsCustAccountInformation( starsAcctInfo );
			
            respOper.setStarsGetCustomerAccountResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot get the customer account information") );
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
			
            StarsGetCustomerAccountResponse resp = operation.getStarsGetCustomerAccountResponse();
            StarsCustAccountInformation accountInfo = resp.getStarsCustAccountInformation();
            if (accountInfo == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsUser user = (StarsUser) session.getAttribute("USER");
			if (operator != null)
				operator.setAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, accountInfo );
			else
            	user.setAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, accountInfo );
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
