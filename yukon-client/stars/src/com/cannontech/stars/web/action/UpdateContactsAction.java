/*
 * Created on Oct 31, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.action;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.AdditionalContact;
import com.cannontech.stars.xml.serialize.ContactNotification;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsCustomerContact;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsUpdateContacts;
import com.cannontech.stars.xml.serialize.StarsUpdateContactsResponse;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UpdateContactsAction implements ActionBase {

	/* (non-Javadoc)
	 * @see com.cannontech.stars.web.action.ActionBase#build(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
					session.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			StarsCustomerAccount account = starsAcctInfo.getStarsCustomerAccount();
			
			StarsUpdateContacts updateContacts = new StarsUpdateContacts();
			updateContacts.setPrimaryContact( (PrimaryContact)
					StarsFactory.newStarsCustomerContact(account.getPrimaryContact(), PrimaryContact.class) );
			for (int i = 0; i < account.getAdditionalContactCount(); i++) {
				updateContacts.addAdditionalContact( (AdditionalContact)
						StarsFactory.newStarsCustomerContact(account.getAdditionalContact(i), AdditionalContact.class) );
			}
			
			int contactID = Integer.parseInt( req.getParameter("ContactID") );
			String command = req.getParameter( "Command" );
			
			if (command.equalsIgnoreCase("Update") || command.equalsIgnoreCase("New")) {
				StarsCustomerContact contact = null;
				if (command.equalsIgnoreCase("Update")) {
					if (updateContacts.getPrimaryContact().getContactID() == contactID) {
						contact = updateContacts.getPrimaryContact();
					}
					else {
						for (int i = 0; i < updateContacts.getAdditionalContactCount(); i++) {
							if (updateContacts.getAdditionalContact(i).getContactID() == contactID) {
								contact = updateContacts.getAdditionalContact(i);
								break;
							}
						}
					}
				}
				else {
					contact = new AdditionalContact();
					contact.setContactID( -1 );
					updateContacts.addAdditionalContact( (AdditionalContact)contact );
				}
				
				contact.setLastName( req.getParameter("LastName") );
				contact.setFirstName( req.getParameter("FirstName") );
				
				contact.removeAllContactNotification();
				
				String[] notifCatIDs = req.getParameterValues("NotifCat");
				String[] notifications = req.getParameterValues("Notification");
				
				for (int i = 0; i < notifCatIDs.length; i++) {
					int notifCatID = Integer.parseInt( notifCatIDs[i] );
					if (notifCatID > 0 && notifications[i].trim().length() > 0) {
						if (notifCatID == YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE || notifCatID == YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE)
							notifications[i] = ServletUtils.formatPhoneNumber( notifications[i] );
						
						ContactNotification contNotif = ServletUtils.createContactNotification( notifications[i], notifCatID );
						if (contactID == account.getPrimaryContact().getContactID() && notifCatID == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL) {
							ContactNotification email = ServletUtils.getContactNotification( account.getPrimaryContact(), YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
							if (email != null)
								contNotif.setDisabled( email.getDisabled() );
							else
								contNotif.setDisabled( true );
						}
						
						contact.addContactNotification( contNotif );
					}
				}
			}
			else {	// command = "Delete"
				for (int i = 0; i < updateContacts.getAdditionalContactCount(); i++) {
					if (updateContacts.getAdditionalContact(i).getContactID() == contactID) {
						updateContacts.removeAdditionalContact(i);
						break;
					}
				}
			}
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsUpdateContacts( updateContacts );

			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (WebClientException we) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, we.getMessage() );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.web.action.ActionBase#process(javax.xml.soap.SOAPMessage, javax.servlet.http.HttpSession)
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
            
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) session.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			if (liteAcctInfo == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
			
			StarsUpdateContacts updateContacts = reqOper.getStarsUpdateContacts();
			StarsUpdateContactsResponse resp = new StarsUpdateContactsResponse(); 
			
			LiteCustomer liteCustomer = liteAcctInfo.getCustomer();
			LiteContact litePrimContact = ContactFuncs.getContact( liteCustomer.getPrimaryContactID() );
			PrimaryContact starsPrimContact = updateContacts.getPrimaryContact();
            
			if (!StarsLiteFactory.isIdenticalCustomerContact( litePrimContact, starsPrimContact )) {
				com.cannontech.database.data.customer.Contact primContact = new com.cannontech.database.data.customer.Contact();
				StarsLiteFactory.setContact( primContact, litePrimContact, energyCompany );
				StarsFactory.setCustomerContact( primContact, starsPrimContact );
            	
            	primContact= (com.cannontech.database.data.customer.Contact)
            			Transaction.createTransaction( Transaction.UPDATE, primContact ).execute();
				
				StarsLiteFactory.setLiteContact( litePrimContact, primContact );
				StarsLiteFactory.setStarsCustomerContact( starsPrimContact, litePrimContact );
				
				ServerUtils.handleDBChange( litePrimContact, DBChangeMsg.CHANGE_TYPE_UPDATE );
			}
			
			resp.setPrimaryContact( starsPrimContact );

			Vector contactList = liteCustomer.getAdditionalContacts();
			Vector newContactList = new Vector();
            
			for (int i = 0; i < updateContacts.getAdditionalContactCount(); i++) {
				AdditionalContact starsContact = updateContacts.getAdditionalContact(i);
				LiteContact liteContact = null;
				
				for (int j = 0; j < contactList.size(); j++) {
					liteContact = (LiteContact) contactList.get(j);
					if (liteContact.getContactID() == starsContact.getContactID()) {
						contactList.remove(j);
		        		
						if (!StarsLiteFactory.isIdenticalCustomerContact(liteContact, starsContact)) {
							// Update the customer contact
							com.cannontech.database.data.customer.Contact contact = new com.cannontech.database.data.customer.Contact();
							StarsLiteFactory.setContact( contact, liteContact, energyCompany );
							StarsFactory.setCustomerContact( contact, starsContact );
			            	
							contact= (com.cannontech.database.data.customer.Contact)
									Transaction.createTransaction( Transaction.UPDATE, contact ).execute();
							
							StarsLiteFactory.setLiteContact( liteContact, contact );
							StarsLiteFactory.setStarsCustomerContact( starsContact, liteContact );
							
							ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_UPDATE );
						}
						
						resp.addAdditionalContact( starsContact );
						break;
					}
				}
            	
				if (liteContact == null) {
					// Add the new customer contact
					com.cannontech.database.data.customer.Contact contact = new com.cannontech.database.data.customer.Contact();
					StarsFactory.setCustomerContact( contact, starsContact );
		            
					contact= (com.cannontech.database.data.customer.Contact)
							Transaction.createTransaction( Transaction.INSERT, contact ).execute();
		            
					liteContact = (LiteContact) StarsLiteFactory.createLite( contact );
					ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_ADD );
					
					StarsLiteFactory.setStarsCustomerContact( starsContact, liteContact );
					resp.addAdditionalContact( starsContact );
				}
            	
				newContactList.add( liteContact );
			}
            
			// Remove customer contacts that are not in the update list
			for (int i = 0; i < contactList.size(); i++) {
				LiteContact liteContact = (LiteContact) contactList.get(i);
				com.cannontech.database.data.customer.Contact contact = new com.cannontech.database.data.customer.Contact();
				StarsLiteFactory.setContact( contact, liteContact );
            	
            	Transaction.createTransaction( Transaction.DELETE, contact ).execute();
            	
            	energyCompany.getContactAccountIDMap().remove( new Integer(liteContact.getContactID()) );
            	ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_DELETE );
			}
            
			liteCustomer.setAdditionalContacts( newContactList );
			
			respOper.setStarsUpdateContactsResponse( resp );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update the customer account information") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			catch (Exception e2) {
				CTILogger.error( e2.getMessage(), e2 );
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.web.action.ActionBase#parse(javax.xml.soap.SOAPMessage, javax.xml.soap.SOAPMessage, javax.servlet.http.HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
		try {
			StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					session.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			
			StarsUpdateContactsResponse resp = operation.getStarsUpdateContactsResponse();
			accountInfo.getStarsCustomerAccount().setPrimaryContact( resp.getPrimaryContact() );
			accountInfo.getStarsCustomerAccount().setAdditionalContact( resp.getAdditionalContact() );
			
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
