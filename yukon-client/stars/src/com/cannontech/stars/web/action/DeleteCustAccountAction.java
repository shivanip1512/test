package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.util.*;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.util.*;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DeleteCustAccountAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
			StarsCustAccountInformation accountInfo = null;
			if (user != null)
				accountInfo = (StarsCustAccountInformation) user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			if (accountInfo == null) return null;
			
			StarsDeleteCustomerAccount delAccount = new StarsDeleteCustomerAccount();
			StarsOperation operation = new StarsOperation();
			operation.setStarsDeleteCustomerAccount( delAccount );
			
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
        	StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
        	if (user == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
            
        	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
        	if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
        	
            int energyCompanyID = user.getEnergyCompanyID();
        	com.cannontech.database.data.stars.customer.CustomerAccount account =
        			StarsLiteFactory.createCustomerAccount(liteAcctInfo, energyCompanyID);
        	Transaction.createTransaction(Transaction.DELETE, account).execute();
        	
        	if (ServerUtils.isResidentialCustomer(user)
        		&& liteAcctInfo.getCustomerAccount().getLoginID() > com.cannontech.user.UserUtils.USER_YUKON_ID) {
        		com.cannontech.database.data.user.YukonUser yukonUser = new com.cannontech.database.data.user.YukonUser();
        		yukonUser.setUserID( new Integer(liteAcctInfo.getCustomerAccount().getLoginID()) );
        		Transaction.createTransaction(Transaction.DELETE, yukonUser).execute();
        	}
        	
            SOAPServer.getEnergyCompany( energyCompanyID ).deleteCustAccountInformation( liteAcctInfo );
            ServerUtils.handleDBChange( liteAcctInfo, DBChangeMsg.CHANGE_TYPE_DELETE );
            
            StarsSuccess success = new StarsSuccess();
            success.setDescription( "Customer account deleted successfully" );
            respOper.setStarsSuccess( success );
        	
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot delete the customer account") );
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
			
			StarsSuccess resp = operation.getStarsSuccess();
			if (resp == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            ServletUtils.removeTransientAttributes( user );
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
