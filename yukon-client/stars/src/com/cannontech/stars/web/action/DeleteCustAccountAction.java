package com.cannontech.stars.web.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsFailure;
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
public class DeleteCustAccountAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
			StarsCustAccountInformation accountInfo = null;
			if (user != null)
				accountInfo = (StarsCustAccountInformation) session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			if (accountInfo == null) return null;
			
			StarsDeleteCustomerAccount delAccount = new StarsDeleteCustomerAccount();
			if (req.getParameter("DisableReceivers") != null)
				delAccount.setDisableReceivers( Boolean.valueOf(req.getParameter("DisableReceivers")).booleanValue() );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsDeleteCustomerAccount( delAccount );
			
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
			StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
			if (user == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) session.getAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			if (liteAcctInfo == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
        	
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
			
			StarsDeleteCustomerAccount delAccount = SOAPUtil.parseSOAPMsgForOperation(reqMsg).getStarsDeleteCustomerAccount();
			if (delAccount.getDisableReceivers()) {
				// Disable all attached receivers enrolled in any program
				ArrayList hwToDisable = new ArrayList();
				
				for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
					LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(i);
					if (liteApp.getProgramID() > 0 && liteApp.getInventoryID() > 0) {
						LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( liteApp.getInventoryID(), true );
						if (!hwToDisable.contains( liteHw )) {
							hwToDisable.add( liteHw );
							YukonSwitchCommandAction.sendDisableCommand( energyCompany, liteHw, null );
						}
					}
				}
			}
			
			deleteCustomerAccount( liteAcctInfo, energyCompany );
            
			StarsSuccess success = new StarsSuccess();
			success.setDescription( "Customer account deleted successfully" );
			respOper.setStarsSuccess( success );
        	
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot delete the customer account") );
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
			
			StarsSuccess resp = operation.getStarsSuccess();
			if (resp == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			ServletUtils.removeTransientAttributes( session );
			
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static void deleteCustomerAccount(LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		com.cannontech.database.data.stars.customer.CustomerAccount account =
				StarsLiteFactory.createCustomerAccount(liteAcctInfo, energyCompany);
		Transaction.createTransaction( Transaction.DELETE, account ).execute();
		
		// Delete contacts from database
		LiteContact primContact = ContactFuncs.getContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
		com.cannontech.database.data.customer.Contact contact =
				(com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent( primContact );
		Transaction.createTransaction( Transaction.DELETE, contact ).execute();
		
		java.util.Vector contacts = liteAcctInfo.getCustomer().getAdditionalContacts();
		for (int i = 0; i < contacts.size(); i++) {
			LiteContact liteContact = (LiteContact) contacts.get(i);
			contact = (com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent( liteContact );
			Transaction.createTransaction( Transaction.DELETE, contact ).execute();
		}
		
		// Delete login
		int userID = primContact.getLoginID();
		if (userID != com.cannontech.user.UserUtils.USER_STARS_DEFAULT_ID &&
			userID != com.cannontech.user.UserUtils.USER_ADMIN_ID)
			UpdateLoginAction.deleteLogin( userID, null );
		
		// Delete lite and stars objects
		energyCompany.deleteCustAccountInformation( liteAcctInfo );
		energyCompany.deleteStarsCustAccountInformation( liteAcctInfo.getAccountID() );
		//ServerUtils.handleDBChange( liteAcctInfo, DBChangeMsg.CHANGE_TYPE_DELETE );
	}

}
