package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

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
import com.cannontech.stars.xml.serialize.Email;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
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
        	Email email = new Email();
			email.setNotification( req.getParameter("Email").trim() );
			if (email.getNotification().length() > 0)
	        	email.setEnabled( Boolean.valueOf(req.getParameter("NotifyControl")).booleanValue() );
	        else
	        	email.setEnabled( false );
        	
        	StarsUpdateControlNotification updateNotif = new StarsUpdateControlNotification();
        	updateNotif.setEmail( email );
        	
        	StarsOperation operation = new StarsOperation();
        	operation.setStarsUpdateControlNotification( updateNotif );
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
            
            int energyCompanyID = user.getEnergyCompanyID();
            LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            
        	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	LiteContact litePrimContact = energyCompany.getContact( liteAcctInfo.getCustomer().getPrimaryContactID(), liteAcctInfo );
            
            StarsUpdateControlNotification updateNotif = reqOper.getStarsUpdateControlNotification();
            if (updateNotif.getEmail() != null) {
            	LiteContactNotification liteNotifEmail = ContactFuncs.getContactNotification( litePrimContact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
            	
            	if (!StarsLiteFactory.isIdenticalContactNotification( liteNotifEmail, updateNotif.getEmail() )) {
					com.cannontech.database.data.customer.Contact primContact =
							(com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent( litePrimContact );
					
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
						if (updateNotif.getEmail().getNotification().length() > 0) {
							notifEmail.setDisableFlag( updateNotif.getEmail().getEnabled()? "N" : "Y" );
							notifEmail.setNotification( updateNotif.getEmail().getNotification() );
						}
						else {
							notifEmail.setOpCode( Transaction.DELETE );
						}
					}
					else {
						notifEmail = new com.cannontech.database.db.contact.ContactNotification();
						notifEmail.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL) );
						notifEmail.setDisableFlag( updateNotif.getEmail().getEnabled()? "N" : "Y" );
						notifEmail.setNotification( updateNotif.getEmail().getNotification() );
						notifEmail.setOpCode( Transaction.INSERT );
						
						primContact.getContactNotifVect().add( notifEmail );
					}
					
					primContact = (com.cannontech.database.data.customer.Contact)
			            	Transaction.createTransaction(Transaction.UPDATE, primContact).execute();
			        StarsLiteFactory.setLiteContact( litePrimContact, primContact );
	            	
		            ServerUtils.handleDBChange( litePrimContact, DBChangeMsg.CHANGE_TYPE_UPDATE );
            	}
            }
            
            StarsSuccess success = new StarsSuccess();
            success.setDescription( "Control notification updated successfully" );
            
            respOper.setStarsSuccess( success );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update control notification") );
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
			
			if (operation.getStarsSuccess() == null)
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsUpdateControlNotification updateNotif = SOAPUtil.parseSOAPMsgForOperation( reqMsg ).getStarsUpdateControlNotification();
			if (updateNotif.getEmail() != null) {
				StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
				StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
						user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
				accountInfo.getStarsCustomerAccount().getPrimaryContact().setEmail( updateNotif.getEmail() );
			}
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
