package com.cannontech.stars.web.action;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.data.stars.customer.CustomerAccount;
import com.cannontech.stars.web.StarsUser;
import com.cannontech.stars.web.util.CommonUtils;
import com.cannontech.stars.xml.StarsCustAccountInfoFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInfo;
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
            StarsOperation operation = new StarsOperation();
            StarsGetCustomerAccount getAccount = new StarsGetCustomerAccount();
            getAccount.setAccountNo( 0 );
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
                StarsFailure failure = new StarsFailure();
                failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
                failure.setDescription("Session invalidated, please login again");
                respOper.setStarsFailure( failure );
                return SOAPUtil.buildSOAPMessage( respOper );
            }

            StarsGetCustomerAccount getAccount = reqOper.getStarsGetCustomerAccount();
            CustomerAccount account = null;
			CustomerAccount[] accounts = user.getCustomerAccounts();
			if (getAccount.getAccountNo() < accounts.length)
				account = accounts[ getAccount.getAccountNo() ];

            if (account == null) {
                StarsFailure failure = new StarsFailure();
                failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
                failure.setDescription("No customer account found, account No. out of boundary");
                respOper.setStarsFailure( failure );
                return SOAPUtil.buildSOAPMessage( respOper );
            }

        	user.setAttribute("CUSTOMER_ACCOUNT", account);
            Hashtable selectionLists = com.cannontech.stars.util.CommonUtils.getSelectionListTable(
            		new Integer(user.getEnergyCompanyID()) );
            
			StarsCustAccountInfo accountInfo = StarsCustAccountInfoFactory.getStarsCustAccountInfo(
					account, selectionLists, StarsGetCustomerAccountResponse.class );
            respOper.setStarsGetCustomerAccountResponse( (StarsGetCustomerAccountResponse) accountInfo );

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
			
            StarsCustAccountInfo accountInfo = operation.getStarsGetCustomerAccountResponse();
            if (accountInfo == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;

			StarsUser user = (StarsUser) session.getAttribute("USER");
            user.setAttribute(CommonUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION", accountInfo);
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
