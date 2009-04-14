package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.user.UserUtils;

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
        	
			LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
			
			StarsDeleteCustomerAccount delAccount = SOAPUtil.parseSOAPMsgForOperation(reqMsg).getStarsDeleteCustomerAccount();
			if (delAccount.getDisableReceivers()) {
				// Disable all attached receivers enrolled in any program
				List<LiteStarsLMHardware> hwToDisable = new ArrayList<LiteStarsLMHardware>();
				
				StarsInventoryBaseDao starsInventoryBaseDao = 
					YukonSpringHook.getBean("starsInventoryBaseDao", StarsInventoryBaseDao.class);
				for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
					LiteStarsAppliance liteApp = liteAcctInfo.getAppliances().get(i);
					if (liteApp.getProgramID() > 0 && liteApp.getInventoryID() > 0) {
						LiteStarsLMHardware liteHw = (LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(liteApp.getInventoryID());
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
		throws TransactionException, WebClientException
	{
	    ApplianceDao applianceDao = YukonSpringHook.getBean("applianceDao", ApplianceDao.class);
	    
	    
	     /* Remove all the inventory from the account and move it to the warehouse.  This
	      * also includes any unenrolling that is needed.
	      */
	    List<Integer> inventories = new ArrayList<Integer>(); 
	    inventories.addAll(liteAcctInfo.getInventories());
	    for (int inventoryId : inventories) {
	        
	        StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
	        deleteHw.setInventoryID(inventoryId);
	        deleteHw.setRemoveDate(new Date());
	        
	        DeleteLMHardwareAction.removeInventory(deleteHw, liteAcctInfo, energyCompany);
	    }
	    
	    applianceDao.deleteAppliancesByAccountId(liteAcctInfo.getAccountID());

		com.cannontech.database.data.stars.customer.CustomerAccount account =
				StarsLiteFactory.createCustomerAccount(liteAcctInfo, energyCompany);
		Transaction.createTransaction( Transaction.DELETE, account ).execute();
		
		// Delete contacts from database
		LiteContact primContact = DaoFactory.getContactDao().getContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
		com.cannontech.database.data.customer.Contact contact =
				(com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent( primContact );
		Transaction.createTransaction( Transaction.DELETE, contact ).execute();
		ServerUtils.handleDBChange( primContact, DBChangeMsg.CHANGE_TYPE_DELETE );
		
		Vector<LiteContact> contacts = liteAcctInfo.getCustomer().getAdditionalContacts();
		for (int i = 0; i < contacts.size(); i++) {
			LiteContact liteContact = contacts.get(i);
			contact = (com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent( liteContact );
			Transaction.createTransaction( Transaction.DELETE, contact ).execute();
			ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_DELETE );
		}
		
		// Delete login
		int userID = primContact.getLoginID();
		if (userID != UserUtils.USER_DEFAULT_ID &&
			userID != UserUtils.USER_ADMIN_ID &&
            userID != UserUtils.USER_YUKON_ID)
			UpdateLoginAction.deleteLogin( userID );
		
		// Delete lite and stars objects
		energyCompany.deleteCustAccountInformation( liteAcctInfo );
		ServerUtils.handleDBChange( liteAcctInfo, DBChangeMsg.CHANGE_TYPE_DELETE );
	}

}
