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
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.customer.CustomerAdditionalContact;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.UserUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
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
import com.cannontech.stars.xml.serialize.StarsUpdateLogin;
import com.cannontech.stars.xml.serialize.StarsUpdateContactsResponse;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.database.data.lite.LiteFactory;


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
				String lastName = req.getParameter("LastName");
				String firstName = req.getParameter("FirstName");
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
					
					/*very hackish...should not be hitting the DB in the build method...
					 * this is part of the whole HECO development rush...some day we will pay
					 */
					com.cannontech.database.data.user.YukonUser login = new com.cannontech.database.data.user.YukonUser();
					LiteStarsEnergyCompany liteEC = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
					com.cannontech.database.data.lite.LiteYukonGroup[] custGroups = liteEC.getResidentialCustomerGroups();
					String firstInitial= "";
					if(firstName != null)
						firstInitial = firstName.toLowerCase().substring(0,1);
					login.getYukonUser().setUsername(firstInitial + lastName.toLowerCase());
					login.getYukonUser().setPassword(new Long(java.util.Calendar.getInstance().getTimeInMillis()).toString()); 
					login.getYukonGroups().addElement(((com.cannontech.database.data.user.YukonGroup)LiteFactory.convertLiteToDBPers(custGroups[0])).getYukonGroup());
					login.getYukonUser().setStatus(UserUtils.STATUS_ENABLED);
					//login.setEnergyCompany()
					login = (YukonUser)
						Transaction.createTransaction(Transaction.INSERT, login).execute();
					LiteYukonUser liteUser = new LiteYukonUser( login.getUserID().intValue() );
					ServerUtils.handleDBChange(liteUser, DBChangeMsg.CHANGE_TYPE_ADD);
					contact.setLoginID(login.getUserID().intValue());
					updateContacts.addAdditionalContact( (AdditionalContact)contact );
				}				
				contact.setLastName( lastName );
				contact.setFirstName( firstName );
				
				contact.removeAllContactNotification();
				
				String[] notifCatIDs = req.getParameterValues("NotifCat");
				String[] notifications = req.getParameterValues("Notification");
				//not sure why these arrays are arriving unmatched...better deal with it quickly
				int tricksy = 0;
				if(notifCatIDs.length > notifications.length)
					tricksy = notifCatIDs.length - notifications.length;
				
				for (int i = tricksy; i < notifCatIDs.length; i++) {
					int notifCatID = Integer.parseInt( notifCatIDs[i] );
					if (notifCatID > 0 && notifications[i - tricksy].trim().length() > 0) {
						if (notifCatID == YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE || notifCatID == YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE
							|| notifCatID == YukonListEntryTypes.YUK_ENTRY_ID_CELL_PHONE)
							notifications[i - tricksy] = ServletUtils.formatPhoneNumber( notifications[i - tricksy] );
						
						ContactNotification contNotif = ServletUtils.createContactNotification( notifications[i - tricksy], notifCatID );
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
            
			LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
			
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
		            
					contact = (com.cannontech.database.data.customer.Contact)
							Transaction.createTransaction( Transaction.INSERT, contact ).execute();
					
					//also need to add the link	between the customer and this new contact	
					CustomerAdditionalContact newAdditional = new CustomerAdditionalContact();
					newAdditional.setCustomerID(new Integer(liteCustomer.getCustomerID()));
		            newAdditional.setContactID(contact.getContact().getContactID());
		            newAdditional.setOrdering(new Integer(i));
					Transaction.createTransaction( Transaction.INSERT, newAdditional ).execute();
		            
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
			ServerUtils.handleDBChange( liteCustomer, DBChangeMsg.CHANGE_TYPE_UPDATE );
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
