/*
 * Created on Oct 31, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.action;

import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.AdditionalContact;
import com.cannontech.stars.xml.serialize.Email;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
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
					user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			
			StarsUpdateContacts updateContacts = new StarsUpdateContacts();

			String lastName = req.getParameter("LastName");
			String firstName = req.getParameter("FirstName");

			if (lastName != null && lastName.trim().length() > 0
				&& firstName != null && firstName.trim().length() > 0) {
				PrimaryContact primContact = new PrimaryContact();
				primContact.setLastName( lastName );
				primContact.setFirstName( firstName );
				primContact.setHomePhone( ServletUtils.formatPhoneNumber(req.getParameter("HomePhone")) );
				primContact.setWorkPhone( ServletUtils.formatPhoneNumber(req.getParameter("WorkPhone")) );
				primContact.setEmail( (Email) StarsFactory.newStarsContactNotification(
						starsAcctInfo.getStarsCustomerAccount().getPrimaryContact().getEmail().getEnabled(),
						req.getParameter("Email"), Email.class) );
				updateContacts.setPrimaryContact( primContact );
			}

			updateContacts.removeAllAdditionalContact();

			for (int i = 2; i <= 4; i++) {
				lastName = req.getParameter("LastName" + i);
				firstName = req.getParameter("FirstName" + i);

				if (lastName != null && lastName.trim().length() > 0
					&& firstName != null && firstName.trim().length() > 0) {
					AdditionalContact contact = new AdditionalContact();
					contact.setContactID( Integer.parseInt(req.getParameter("ContactID" + i)) );
					contact.setLastName( lastName );
					contact.setFirstName( firstName );
					contact.setHomePhone( ServletUtils.formatPhoneNumber(req.getParameter("HomePhone" + i)) );
					contact.setWorkPhone( ServletUtils.formatPhoneNumber(req.getParameter("WorkPhone" + i)) );
					contact.setEmail( (Email) StarsFactory.newStarsContactNotification(
							false, req.getParameter("Email" + i), Email.class) );
					updateContacts.addAdditionalContact( contact );
				}
			}
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsUpdateContacts( updateContacts );

			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (ServletException se) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, se.getMessage() );
		}
		catch (Exception e) {
			e.printStackTrace();
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
            
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			if (liteAcctInfo == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
			
			StarsUpdateContacts updateContacts = reqOper.getStarsUpdateContacts();
			StarsUpdateContactsResponse resp = new StarsUpdateContactsResponse(); 
			
			LiteCustomer liteCustomer = liteAcctInfo.getCustomer();
			LiteContact litePrimContact = energyCompany.getContact( liteCustomer.getPrimaryContactID(), liteAcctInfo );
			PrimaryContact starsPrimContact = updateContacts.getPrimaryContact();
            
			if (!StarsLiteFactory.isIdenticalCustomerContact( litePrimContact, starsPrimContact )) {
				com.cannontech.database.data.customer.Contact primContact =
						(com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent( litePrimContact );
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
							com.cannontech.database.data.customer.Contact contact =
									(com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent( liteContact );
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
					energyCompany.addContact( liteContact, liteAcctInfo );
					
					StarsLiteFactory.setStarsCustomerContact( starsContact, liteContact );
					resp.addAdditionalContact( starsContact );
				}
            	
				newContactList.add( liteContact );
			}
            
			// Remove customer contacts that are not in the update list
			for (int i = 0; i < contactList.size(); i++) {
				LiteContact liteContact = (LiteContact) contactList.get(i);
				com.cannontech.database.data.customer.Contact contact =
						(com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent( liteContact );
            	
            	Transaction.createTransaction( Transaction.DELETE, contact ).execute();
				energyCompany.deleteContact( liteContact.getContactID() );
			}
            
			liteCustomer.setAdditionalContacts( newContactList );
			
			respOper.setStarsUpdateContactsResponse( resp );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			e.printStackTrace();
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update the customer account information") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			catch (Exception e2) {
				e2.printStackTrace();
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
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			
			StarsUpdateContactsResponse resp = operation.getStarsUpdateContactsResponse();
			accountInfo.getStarsCustomerAccount().setPrimaryContact( resp.getPrimaryContact() );
			accountInfo.getStarsCustomerAccount().setAdditionalContact( resp.getAdditionalContact() );
			
			return 0;
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
