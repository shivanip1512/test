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
import com.cannontech.stars.xml.StarsCustAccountInformationFactory;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetAllCustomerAccountsResponse;
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
			StarsUser user = (StarsUser) session.getAttribute( "USER" );
			if (user == null) return null;
			
			StarsGetAllCustomerAccountsResponse accounts = (StarsGetAllCustomerAccountsResponse)
					user.getAttribute( "ALL_CUSTOMER_ACCOUNTS" );
			if (accounts == null || accounts.getStarsCustAccountBriefCount() == 0) return null;
			
            StarsOperation operation = new StarsOperation();
            StarsGetCustomerAccount getAccount = new StarsGetCustomerAccount();
            getAccount.setAccountID( accounts.getStarsCustAccountBrief(0).getAccountID() );
            operation.setStarsGetCustomerAccount( getAccount );

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

            StarsUser user = (StarsUser) session.getAttribute("USER");
            if (user == null) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }

            StarsGetCustomerAccount getAccount = reqOper.getStarsGetCustomerAccount();
            LiteStarsCustAccountInformation liteAcctInfo = com.cannontech.stars.web.servlet.SOAPServer.getCustAccountInformation(
            		new Integer(user.getEnergyCompanyID()), new Integer(getAccount.getAccountID()) );
            if (liteAcctInfo == null) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Cannot find customer account information") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
        	user.setAttribute("CUSTOMER_ACCOUNT_INFORMATION", liteAcctInfo);
        	
			StarsCustAccountInformation starsAcctInfo = StarsLiteFactory.createStarsCustAccountInformation(
					liteAcctInfo, new Integer(user.getEnergyCompanyID()), false );
			
			StarsGetCustomerAccountResponse resp = new StarsGetCustomerAccountResponse();
			resp.setStarsCustAccountInformation( starsAcctInfo );
			
            respOper.setStarsGetCustomerAccountResponse( resp );
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
			
            StarsGetCustomerAccountResponse resp = operation.getStarsGetCustomerAccountResponse();
            StarsCustAccountInformation accountInfo = resp.getStarsCustAccountInformation();
            if (accountInfo == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;

			StarsUser user = (StarsUser) session.getAttribute("USER");
            user.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION", accountInfo);
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
