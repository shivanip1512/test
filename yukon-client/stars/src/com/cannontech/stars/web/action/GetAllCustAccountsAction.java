package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.stars.web.StarsUser;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.stars.xml.serialize.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GetAllCustAccountsAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
            StarsOperation operation = new StarsOperation();
            StarsGetAllCustomerAccounts getAllAccounts = new StarsGetAllCustomerAccounts();
            operation.setStarsGetAllCustomerAccounts( getAllAccounts );

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

			com.cannontech.database.db.stars.customer.CustomerAccount[] accounts = user.getCustomerAccounts();
			if (accounts == null) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_OPERATION_FAILED, "No account information found for this customer") );
                return SOAPUtil.buildSOAPMessage( respOper );
			}

			StarsGetAllCustomerAccountsResponse resp = new StarsGetAllCustomerAccountsResponse();
            for (int i = 0; i < accounts.length; i++) {
            	StarsCustAccountBrief acctBrief = new StarsCustAccountBrief();
            	acctBrief.setAccountID( accounts[i].getAccountID().intValue() );
            	acctBrief.setAccountNumber( accounts[i].getAccountNumber() );
            	acctBrief.setAccountNotes( accounts[i].getAccountNotes() );
            	
            	resp.addStarsCustAccountBrief( acctBrief );
            }
            
            respOper.setStarsGetAllCustomerAccountsResponse( resp );
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
			
			StarsUser user = (StarsUser) session.getAttribute("USER");
			if (user == null)
				return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
				
            StarsGetAllCustomerAccountsResponse resp = operation.getStarsGetAllCustomerAccountsResponse();
            user.setAttribute( "ALL_CUSTOMER_ACCOUNTS", resp );
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
