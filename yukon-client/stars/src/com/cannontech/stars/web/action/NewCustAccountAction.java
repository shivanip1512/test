package com.cannontech.stars.web.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPClient;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.AdditionalContact;
import com.cannontech.stars.xml.serialize.BillingAddress;
import com.cannontech.stars.xml.serialize.Email;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.StarsAppliances;
import com.cannontech.stars.xml.serialize.StarsCallReportHistory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsLMPrograms;
import com.cannontech.stars.xml.serialize.StarsNewCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsServiceRequestHistory;
import com.cannontech.stars.xml.serialize.StarsSiteInformation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateLogin;
import com.cannontech.stars.xml.serialize.StarsUser;
import com.cannontech.stars.xml.serialize.StreetAddress;
import com.cannontech.stars.xml.serialize.Substation;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * <p>Title: NewCustAccountAction.java</p>
 * <p>Description: </p>
 * <p>Generation Time: 07/26/2002 11:10:58 AM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */

public class NewCustAccountAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
            StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) return null;
            
			StarsNewCustomerAccount newAccount = new StarsNewCustomerAccount();
			StarsCustomerAccount account = new StarsCustomerAccount();
			
            account.setAccountNumber( req.getParameter("AcctNo") );
            account.setIsCommercial( Boolean.valueOf(req.getParameter("Commercial")).booleanValue() );
            account.setCompany( req.getParameter("Company") );
            account.setAccountNotes( req.getParameter("AcctNotes").replaceAll(System.getProperty("line.separator"), "<br>") );
            account.setPropertyNumber( req.getParameter("PropNo") );
            account.setPropertyNotes( req.getParameter("PropNotes").replaceAll(System.getProperty("line.separator"), "<br>") );

            StreetAddress propAddr = new StreetAddress();
            propAddr.setStreetAddr1( req.getParameter("SAddr1") );
            propAddr.setStreetAddr2( req.getParameter("SAddr2") );
            propAddr.setCity( req.getParameter("SCity") );
            propAddr.setState( req.getParameter("SState") );
            propAddr.setZip( req.getParameter("SZip") );
            propAddr.setCounty( req.getParameter("SCounty") );
            account.setStreetAddress( propAddr );

			Substation starsSub = new Substation();
			starsSub.setEntryID( Integer.parseInt(req.getParameter("Substation")) );
			
            StarsSiteInformation siteInfo = new StarsSiteInformation();
            siteInfo.setSubstation( starsSub );
            siteInfo.setFeeder( req.getParameter("Feeder") );
            siteInfo.setPole( req.getParameter("Pole") );
            siteInfo.setTransformerSize( req.getParameter("TranSize") );
            siteInfo.setServiceVoltage( req.getParameter("ServVolt") );
            account.setStarsSiteInformation( siteInfo );

            BillingAddress billAddr = new BillingAddress();
            if (req.getParameter("CopyAddress") != null) {
	            billAddr.setStreetAddr1( req.getParameter("SAddr1") );
	            billAddr.setStreetAddr2( req.getParameter("SAddr2") );
	            billAddr.setCity( req.getParameter("SCity") );
	            billAddr.setState( req.getParameter("SState") );
	            billAddr.setZip( req.getParameter("SZip") );
            }
            else {
	            billAddr.setStreetAddr1( req.getParameter("BAddr1") );
	            billAddr.setStreetAddr2( req.getParameter("BAddr2") );
	            billAddr.setCity( req.getParameter("BCity") );
	            billAddr.setState( req.getParameter("BState") );
	            billAddr.setZip( req.getParameter("BZip") );
            }
            account.setBillingAddress( billAddr );

            PrimaryContact primContact = new PrimaryContact();
            primContact.setLastName( req.getParameter("LastName") );
            primContact.setFirstName( req.getParameter("FirstName") );
            primContact.setHomePhone( ServletUtils.formatPhoneNumber(req.getParameter("HomePhone")) );
            primContact.setWorkPhone( ServletUtils.formatPhoneNumber(req.getParameter("WorkPhone")) );
            
            Email email = new Email();
            email.setNotification( req.getParameter("Email") );
            email.setEnabled( Boolean.valueOf(req.getParameter("NotifyControl")).booleanValue() );
            primContact.setEmail( email );
            account.setPrimaryContact( primContact );
            
	        for (int i = 2; i <= 4; i++) {
	            String lastName = req.getParameter("LastName" + i);
	            String firstName = req.getParameter("FirstName" + i);
	
	            if (lastName != null && lastName.trim().length() > 0
	                && firstName != null && firstName.trim().length() > 0) {
	                AdditionalContact contact = new AdditionalContact();
	                contact.setLastName( lastName );
	                contact.setFirstName( firstName );
	                contact.setHomePhone( ServletUtils.formatPhoneNumber(req.getParameter("HomePhone" + i)) );
	                contact.setWorkPhone( ServletUtils.formatPhoneNumber(req.getParameter("WorkPhone" + i)) );
	                account.addAdditionalContact( contact );
	            }
	        }
			newAccount.setStarsCustomerAccount( account );
			
			String username = req.getParameter( "Username" );
			String password = req.getParameter( "Password" );
			if (username != null && username.trim().length() > 0) {
				StarsUpdateLogin login = new StarsUpdateLogin();
				login.setUsername( username );
				login.setPassword( password );
				newAccount.setStarsUpdateLogin( login );
			}
			
			user.setAttribute( ServletUtils.ATT_NEW_CUSTOMER_ACCOUNT, newAccount );

            StarsOperation operation = new StarsOperation();
            operation.setStarsNewCustomerAccount( newAccount );

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
			StarsNewCustomerAccount newAccount = reqOper.getStarsNewCustomerAccount();
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) {
                respOper.setStarsFailure( StarsFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
			LiteStarsCustAccountInformation liteAcctInfo = null;
            
            try {
				liteAcctInfo = newCustomerAccount( newAccount, user, energyCompany );
            }
            catch (WebClientException e) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_SESSION_INVALID, e.getMessage()) );
				return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            if (SOAPServer.isClientLocal()) {
            	StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteAcctInfo );
				ServletUtils.removeTransientAttributes( user );
				user.setAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, starsAcctInfo );
            }
            
			StarsSuccess success = new StarsSuccess();
			success.setDescription( "Customer account created successfully" );
			respOper.setStarsSuccess( success );
			
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot create the customer account") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	e2.printStackTrace();
            }
        }

        return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation respOper = SOAPUtil.parseSOAPMsgForOperation( respMsg );
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );

			StarsFailure failure = respOper.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			StarsSuccess success = respOper.getStarsSuccess();
			if (success == null)
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			user.removeAttribute( ServletUtils.ATT_NEW_CUSTOMER_ACCOUNT );
			
			if (!SOAPClient.isServerLocal()) {
				StarsNewCustomerAccount newAccount = reqOper.getStarsNewCustomerAccount();
				StarsCustAccountInformation accountInfo = new StarsCustAccountInformation();
				
				accountInfo.setStarsCustomerAccount( newAccount.getStarsCustomerAccount() );
				if (newAccount.getStarsUpdateLogin() != null) {
					StarsUser login = new StarsUser();
					login.setUsername( newAccount.getStarsUpdateLogin().getUsername() );
					login.setPassword( newAccount.getStarsUpdateLogin().getPassword() );
					accountInfo.setStarsUser( login );
				}
				
				accountInfo.setStarsLMPrograms( new StarsLMPrograms() );
				accountInfo.setStarsAppliances( new StarsAppliances() );
				accountInfo.setStarsInventories( new StarsInventories() );
				accountInfo.setStarsCallReportHistory( new StarsCallReportHistory() );
				accountInfo.setStarsServiceRequestHistory( new StarsServiceRequestHistory() );
				
				ServletUtils.removeTransientAttributes( user );
				user.setAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, accountInfo );
			}
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static LiteStarsCustAccountInformation newCustomerAccount(StarsNewCustomerAccount newAccount, StarsYukonUser user, LiteStarsEnergyCompany energyCompany)
	throws WebClientException, CommandExecutionException {
		StarsCustomerAccount starsAccount = newAccount.getStarsCustomerAccount();
		StarsUpdateLogin updateLogin = newAccount.getStarsUpdateLogin();
        
		// Check to see if the account number has duplicates
		String sql = "SELECT 1 FROM CustomerAccount acct, ECToAccountMapping map "
				   + "WHERE acct.AccountID = map.AccountID AND map.EnergyCompanyID = " + user.getEnergyCompanyID()
				   + " AND UPPER(acct.AccountNumber) = UPPER('" + starsAccount.getAccountNumber() + "')";
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		stmt.execute();
		
		if (stmt.getRowCount() > 0)
			throw new WebClientException( "The account number already exists, please enter a different one." );
        
		// Check to see if the login is valid
		if (updateLogin != null) {
			if (updateLogin.getUsername().trim().length() == 0 || updateLogin.getPassword().trim().length() == 0)
				throw new WebClientException( "Username and password cannot be empty" );
		    
			if (!UpdateLoginAction.checkLogin( updateLogin ))
				throw new WebClientException( "Username already exists, please enter a different one" );
		}
    	
		/* Create yukon user */
		int userID = com.cannontech.user.UserUtils.USER_YUKON_ID;
		if (updateLogin != null)
			userID = UpdateLoginAction.createLogin( updateLogin, null, energyCompany ).getUserID();
        
		/* Create contacts */
		com.cannontech.database.data.customer.Contact primContact = new com.cannontech.database.data.customer.Contact();
		StarsFactory.setCustomerContact( primContact, starsAccount.getPrimaryContact() );
		primContact.getContact().setLogInID( new Integer(userID) );
		primContact = (com.cannontech.database.data.customer.Contact)
				Transaction.createTransaction(Transaction.INSERT, primContact).execute();
        
		ArrayList addContacts = new ArrayList();
		for (int i = 0; i < starsAccount.getAdditionalContactCount(); i++) {
			com.cannontech.database.data.customer.Contact contact =
					new com.cannontech.database.data.customer.Contact();
			StarsFactory.setCustomerContact( contact, starsAccount.getAdditionalContact(i) );
			contact = (com.cannontech.database.data.customer.Contact)
					Transaction.createTransaction(Transaction.INSERT, contact).execute();
			addContacts.add( contact );
		}
        
		/* CustomerAccount: Begin */
		com.cannontech.database.data.stars.customer.CustomerAccount account =
				new com.cannontech.database.data.stars.customer.CustomerAccount();
		com.cannontech.database.db.stars.customer.CustomerAccount accountDB = account.getCustomerAccount();
        
		/* CustomerAccount->Customer: Begin */
		com.cannontech.database.data.customer.Customer customer = null;
		if (starsAccount.getIsCommercial())
			customer = new com.cannontech.database.data.customer.CICustomerBase();
		else
			customer = new com.cannontech.database.data.customer.Customer();
        
		com.cannontech.database.db.customer.Customer customerDB = customer.getCustomer();
		customerDB.setPrimaryContactID( primContact.getContact().getContactID() );
        
		if (starsAccount.getIsCommercial()) {
			customerDB.setCustomerTypeID( new Integer(CustomerTypes.CUSTOMER_CI) );
			
			((com.cannontech.database.data.customer.CICustomerBase)customer).getCiCustomerBase().setCompanyName( starsAccount.getCompany() );
			
			com.cannontech.database.db.customer.Address custAddr = ((com.cannontech.database.data.customer.CICustomerBase)customer).getAddress();
			StarsFactory.setCustomerAddress( custAddr, starsAccount.getStreetAddress() );
			
			com.cannontech.database.db.company.EnergyCompany engCompany = new com.cannontech.database.db.company.EnergyCompany();
			engCompany.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
			((com.cannontech.database.data.customer.CICustomerBase)customer).setEnergyCompany( engCompany );
		}
		else {
			customerDB.setCustomerTypeID( new Integer(CustomerTypes.CUSTOMER_RESIDENTIAL) );
		}
        
		String timeZone = starsAccount.getTimeZone();
		if (timeZone == null)
			timeZone = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.DEFAULT_TIME_ZONE );
		if (timeZone == null)
			timeZone = "(none)";
		customerDB.setTimeZone( timeZone );
        
		int[] contactIDs = new int[ addContacts.size() ];
		for (int i = 0; i < addContacts.size(); i++) {
			com.cannontech.database.data.customer.Contact contact =
					(com.cannontech.database.data.customer.Contact) addContacts.get(i);
			contactIDs[i] = contact.getContact().getContactID().intValue();
		}
		customer.setCustomerContactIDs( contactIDs );
		/* CustomerAccount->Customer: End */
        
		com.cannontech.database.db.customer.Address billAddr = account.getBillingAddress();
		StarsFactory.setCustomerAddress( billAddr, starsAccount.getBillingAddress() );
        
		/* CustomerAccount->AccountSite: Begin */
		com.cannontech.database.data.stars.customer.AccountSite acctSite = account.getAccountSite();
		com.cannontech.database.db.stars.customer.AccountSite acctSiteDB = acctSite.getAccountSite();
        
		com.cannontech.database.db.customer.Address propAddr = acctSite.getStreetAddress();
		StarsFactory.setCustomerAddress( propAddr, starsAccount.getStreetAddress() );
		
		/* CustomerAccount->AccountSite->SiteInformation: Begin */
		com.cannontech.database.data.stars.customer.SiteInformation siteInfo = acctSite.getSiteInformation();
		com.cannontech.database.db.stars.customer.SiteInformation siteInfoDB = siteInfo.getSiteInformation();
		StarsSiteInformation starsSiteInfo = starsAccount.getStarsSiteInformation();
        
		siteInfoDB.setSubstationID( new Integer(starsSiteInfo.getSubstation().getEntryID()) );
		siteInfoDB.setFeeder( starsSiteInfo.getFeeder() );
		siteInfoDB.setPole( starsSiteInfo.getPole() );
		siteInfoDB.setTransformerSize( starsSiteInfo.getTransformerSize() );
		siteInfoDB.setServiceVoltage( starsSiteInfo.getServiceVoltage() );
		/* CustomerAccount->AccountSite->SiteInformation: End */
        
		acctSiteDB.setSiteNumber( starsAccount.getPropertyNumber() );
		acctSiteDB.setPropertyNotes( starsAccount.getPropertyNotes() );
		/* CustomerAccount->AccountSite: End */
		
		accountDB.setAccountNumber( starsAccount.getAccountNumber() );
		accountDB.setAccountNotes( starsAccount.getAccountNotes() );
		account.setCustomer( customer );
		account.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
        
		account = (com.cannontech.database.data.stars.customer.CustomerAccount)
				Transaction.createTransaction( Transaction.INSERT, account ).execute();
        
		/* Create lite objects */
		LiteStarsCustAccountInformation liteAcctInfo = energyCompany.addCustAccountInformation( account );
		user.setAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo );
        
		LiteContact liteContact = (LiteContact) StarsLiteFactory.createLite( primContact );
		energyCompany.addContact( liteContact, liteAcctInfo );
		for (int i = 0; i < addContacts.size(); i++) {
			liteContact = (LiteContact) StarsLiteFactory.createLite( (com.cannontech.database.data.customer.Contact) addContacts.get(i) );
			energyCompany.addContact( liteContact, liteAcctInfo );
		}
        
		//ServerUtils.handleDBChange( liteAcctInfo, DBChangeMsg.CHANGE_TYPE_ADD );
		return liteAcctInfo;
	}

}
