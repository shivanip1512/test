package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.ContactNotification;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustomerContact;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateControlNotification;
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
public class UpdateControlNotificationAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
        	ContactNotification email = new ContactNotification();
			email.setNotification( req.getParameter("Email").trim() );
			if (email.getNotification().length() > 0)
	        	email.setDisabled( req.getParameter("NotifyControl") == null );
	        else
	        	email.setDisabled( true );
        	
        	StarsUpdateControlNotification updateNotif = new StarsUpdateControlNotification();
        	updateNotif.setContactNotification( email );
        	
        	StarsOperation operation = new StarsOperation();
        	operation.setStarsUpdateControlNotification( updateNotif );
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

			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            int energyCompanyID = user.getEnergyCompanyID();
            LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            
        	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) session.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	LiteContact litePrimContact = ContactFuncs.getContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
			LiteContactNotification liteNotifEmail = ContactFuncs.getContactNotification( litePrimContact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
            
            ContactNotification email = reqOper.getStarsUpdateControlNotification().getContactNotification();
            
            if (liteNotifEmail == null && email.getNotification().length() > 0
            	|| liteNotifEmail != null && (!liteNotifEmail.getNotification().equals(email.getNotification()) || liteNotifEmail.isDisabled() != email.getDisabled()))
        	{
				com.cannontech.database.data.customer.Contact primContact = new com.cannontech.database.data.customer.Contact();
				StarsLiteFactory.setContact( primContact, litePrimContact, energyCompany );
				
				com.cannontech.database.db.contact.ContactNotification notifEmail = null;
				for (int i = 0; i < primContact.getContactNotifVect().size(); i++) {
					com.cannontech.database.db.contact.ContactNotification notif =
							(com.cannontech.database.db.contact.ContactNotification) primContact.getContactNotifVect().get(i);
					if (notif.getNotificationCatID().intValue() == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL) {
						notifEmail = notif;
						break;
					}
				}
				
				if (notifEmail != null) {
					if (email.getNotification().length() > 0) {
						notifEmail.setDisableFlag( email.getDisabled()? "Y" : "N" );
						notifEmail.setNotification( email.getNotification() );
					}
					else {
						notifEmail.setOpCode( Transaction.DELETE );
					}
				}
				else {
					notifEmail = new com.cannontech.database.db.contact.ContactNotification();
					notifEmail.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL) );
					notifEmail.setDisableFlag( email.getDisabled()? "Y" : "N" );
					notifEmail.setNotification( email.getNotification() );
					notifEmail.setOpCode( Transaction.INSERT );
					primContact.getContactNotifVect().add( notifEmail );
				}
				
				primContact = (com.cannontech.database.data.customer.Contact)
		            	Transaction.createTransaction(Transaction.UPDATE, primContact).execute();
		        
		        StarsLiteFactory.setLiteContact( litePrimContact, primContact );
	            ServerUtils.handleDBChange( litePrimContact, DBChangeMsg.CHANGE_TYPE_UPDATE );
            }
            
            StarsSuccess success = new StarsSuccess();
            success.setDescription( "Control notification updated successfully" );
            
            respOper.setStarsSuccess( success );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update control notification") );
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
			
			if (operation.getStarsSuccess() == null)
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			StarsCustomerContact primContact = accountInfo.getStarsCustomerAccount().getPrimaryContact();
			ContactNotification emailNotif = ServletUtils.getContactNotification( primContact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
			
			ContactNotification email = SOAPUtil.parseSOAPMsgForOperation( reqMsg ).getStarsUpdateControlNotification().getContactNotification();
			if (emailNotif == null) {
				if (email.getNotification().length() > 0)
					primContact.addContactNotification( email );
			}
			else {
				for (int i = 0; i < primContact.getContactNotificationCount(); i++) {
					if (primContact.getContactNotification(i) == emailNotif) {
						if (email.getNotification().length() > 0)
							primContact.setContactNotification(i, email);
						else
							primContact.removeContactNotification(i);
					}
				}
			}
			
            return 0;
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
