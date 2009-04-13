package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang.StringUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.customer.Contact;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.AdditionalContact;
import com.cannontech.stars.xml.serialize.BillingAddress;
import com.cannontech.stars.xml.serialize.ContactNotification;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsNewCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSiteInformation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateLogin;
import com.cannontech.stars.xml.serialize.StreetAddress;
import com.cannontech.stars.xml.serialize.Substation;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.user.UserUtils;

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
            account.setCICustomerType( Integer.parseInt(req.getParameter("CommercialType")));
			if (req.getParameter("CustomerNumber") != null)
				account.setCustomerNumber( req.getParameter("CustomerNumber") );
			if (req.getParameter("RateSchedule") != null)
				account.setRateScheduleID( Integer.parseInt(req.getParameter("RateSchedule")) );
			if (req.getParameter("AltTrackNum") != null)
				account.setAltTrackingNumber( req.getParameter("AltTrackNum") );
            if (req.getParameter("CustStatus") != null)
                account.setCustStatus( req.getParameter("CustStatus") );
            if (req.getParameter("CustAtHome") != null)
                account.setCustAtHome( req.getParameter("CustAtHome") );
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
			
			ContactNotification homePhone = ServletUtils.createContactNotification(
					req.getParameter("HomePhone"), YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE );
			if (homePhone != null) primContact.addContactNotification( homePhone );
			
			ContactNotification workPhone = ServletUtils.createContactNotification(
					req.getParameter("WorkPhone"), YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE );
			if (workPhone != null) primContact.addContactNotification( workPhone );
			
			ContactNotification email = ServletUtils.createContactNotification(
					req.getParameter("Email"), YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
			if (email != null) {
				if (req.getParameter("NotifyControl") == null) email.setDisabled( true );
				primContact.addContactNotification( email );
			}
			 
			account.setPrimaryContact( primContact );
            
			LiteStarsEnergyCompany liteEC = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
			com.cannontech.database.data.lite.LiteYukonGroup[] custGroups = liteEC.getResidentialCustomerGroups();
			for (int i = 2; i <= 4; i++) {
				String lastName = req.getParameter("LastName" + i);
				String firstName = req.getParameter("FirstName" + i);
				
				if (lastName != null && lastName.trim().length() > 0
					|| firstName != null && firstName.trim().length() > 0) {
					
					List<String> usedNames = new ArrayList<String>();
					String primaryContactFirstName = primContact.getFirstName();
					String primaryContactLastName = primContact.getLastName();
					String primFirstInitial = StringUtils.isBlank(primaryContactFirstName) ? "" : primaryContactFirstName.toLowerCase().substring(0,1);
					usedNames.add(primFirstInitial + (StringUtils.isBlank(primaryContactLastName)? "" : primaryContactLastName));
					
					AdditionalContact contact = new AdditionalContact();
					contact.setLastName( lastName );
					contact.setFirstName( firstName );
					
					ContactNotification homePhone2 = ServletUtils.createContactNotification(
							req.getParameter("HomePhone" + i), YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE );
					if (homePhone2 != null) contact.addContactNotification( homePhone2 );
					
					ContactNotification workPhone2 = ServletUtils.createContactNotification(
							req.getParameter("WorkPhone" + i), YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE );
					if (workPhone2 != null) contact.addContactNotification( workPhone2 );
					
					/*very hackish...should not be hitting the DB in the build method...
					 * this is part of the whole HECO development rush...some day we will pay
					 */
                    //ConsumerInfoRole.CREATE_LOGIN_FOR_ACCOUNT needs to be true for this to happen
					if(DaoFactory.getAuthDao().checkRoleProperty(user.getUserID(), ConsumerInfoRole.CREATE_LOGIN_FOR_ACCOUNT))
                    {
                        com.cannontech.database.data.user.YukonUser login = new com.cannontech.database.data.user.YukonUser();
                        String time = new Long(java.util.Calendar.getInstance().getTimeInMillis()).toString();
                        String firstInitial= "";
    					if(firstName != null)
    						firstInitial = firstName.toLowerCase().substring(0,1);
    					String newUserName = firstInitial + lastName.toLowerCase();
    					if (DaoFactory.getYukonUserDao().getLiteYukonUser( newUserName ) != null || usedNames.contains(newUserName)){
                            newUserName = lastName.toLowerCase() + time.substring(time.length() - 2);
    					}
    					usedNames.add(newUserName);
    					
    					login.getYukonUser().setUsername(newUserName);
    					login.getYukonUser().setAuthType(AuthType.NONE); 
                        if(custGroups.length > 0)
                            login.getYukonGroups().addElement(((com.cannontech.database.data.user.YukonGroup)LiteFactory.convertLiteToDBPers(custGroups[0])).getYukonGroup());
    					login.getYukonUser().setStatus(UserUtils.STATUS_ENABLED);
    					login = Transaction.createTransaction(Transaction.INSERT, login).execute();
    					LiteYukonUser liteUser = new LiteYukonUser( login.getUserID().intValue() );
    					ServerUtils.handleDBChange(liteUser, DBChangeMsg.CHANGE_TYPE_ADD);		
    					contact.setLoginID(login.getUserID().intValue());
                    }
                    else
                    {
                        contact.setLoginID(UserUtils.USER_DEFAULT_ID);
                    }
					account.addAdditionalContact( contact );
				}
			}
			
			newAccount.setStarsCustomerAccount( account );
			//ConsumerInfoRole.CREATE_LOGIN_FOR_ACCOUNT needs to be true for this to happen
            String username = req.getParameter( "Username" );
			String password = req.getParameter( "Password" );
			StarsUpdateLogin login = null;
			if (username != null && username.trim().length() > 0) {
                login = new StarsUpdateLogin();
				login.setUsername( username );
				login.setPassword( password );
				login.setGroupID( Integer.parseInt(req.getParameter("CustomerGroup")) );
			}
			else if(DaoFactory.getAuthDao().checkRoleProperty(user.getUserID(), ConsumerInfoRole.CREATE_LOGIN_FOR_ACCOUNT))
			{
                login = new StarsUpdateLogin();
				String lastName = primContact.getLastName();
				String firstName = primContact.getFirstName();
				String firstInitial = "";
                String time = new Long(java.util.Calendar.getInstance().getTimeInMillis()).toString();
                
				if(firstName != null && firstName.length() > 0)
					firstInitial = firstName.toLowerCase().substring(0,1);
					
				if(lastName == null)
				{
					lastName = account.getAccountNumber();
					firstInitial = "#";
				}
				if(DaoFactory.getYukonUserDao().getLiteYukonUser( firstInitial + lastName ) != null)
					login.setUsername(lastName.toLowerCase() + time.substring(time.length() - 2));
				else
					login.setUsername(firstInitial + lastName.toLowerCase());
				login.setPassword(time);
				/*String groupIDs = DaoFactory.getEnergyCompanyDao().getEnergyCompanyProperty(user.getYukonUser(), EnergyCompanyRole.CUSTOMER_GROUP_IDS);
				Integer defaultGroupID = new Integer(0);
				if(groupIDs != null)
					groupIDs.*/
				login.setGroupID(custGroups[0].getGroupID());
				//login.setStatus(UserUtils.STATUS_ENABLED);
				//how do I set energy company id for this type of login?????
				//login.setStatus(UserUtils.STATUS_ENABLED);

			}
			
			if(login != null)
			    newAccount.setStarsUpdateLogin(login);
            
			session.setAttribute( ServletUtils.ATT_NEW_CUSTOMER_ACCOUNT, newAccount );
			
			// Format the phone number after the information has been saved
			if (homePhone != null)
				homePhone.setNotification( ServletUtils.formatPhoneNumberForStorage(homePhone.getNotification()) );
			if (workPhone != null)
				workPhone.setNotification( ServletUtils.formatPhoneNumberForStorage(workPhone.getNotification()) );
			
			for (int i = 0; i < account.getAdditionalContactCount(); i++) {
				ContactNotification homePhone2 = ServletUtils.getContactNotification(
						account.getAdditionalContact(i), YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE );
				ContactNotification workPhone2 = ServletUtils.getContactNotification(
						account.getAdditionalContact(i), YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE );
				
				if (homePhone2 != null)
					homePhone2.setNotification( ServletUtils.formatPhoneNumberForStorage(homePhone2.getNotification()) );
				if (workPhone2 != null)
					workPhone2.setNotification( ServletUtils.formatPhoneNumberForStorage(workPhone2.getNotification()) );
			}
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsNewCustomerAccount( newAccount );

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
            
			LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
			LiteStarsCustAccountInformation liteAcctInfo = null;
            
			try {
				liteAcctInfo = newCustomerAccount( newAccount, energyCompany );
				session.setAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo );
			}
			catch (WebClientException e) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, e.getMessage()) );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			// Response will be handled here, instead of in parse()
			session.removeAttribute( ServletUtils.ATT_NEW_CUSTOMER_ACCOUNT );
			ServletUtils.removeTransientAttributes( session );
			
			StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteAcctInfo );
			session.setAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, starsAcctInfo );
            
			StarsSuccess success = new StarsSuccess();
			success.setDescription( "Customer account created successfully" );
			respOper.setStarsSuccess( success );
            EventUtils.logSTARSEvent(user.getUserID(), EventUtils.EVENT_CATEGORY_ACCOUNT, YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_CREATED, liteAcctInfo.getAccountID(), session);
            			
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Failed to create the customer account") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			catch (Exception e2) {
				CTILogger.error( e2.getMessage(), e2 );
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
			
			StarsFailure failure = respOper.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			StarsSuccess success = respOper.getStarsSuccess();
			if (success == null)
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static LiteStarsCustAccountInformation newCustomerAccount(StarsNewCustomerAccount newAccount,
		LiteStarsEnergyCompany energyCompany, boolean checkConstraint, boolean authTypeChanged) throws WebClientException
	{
		try {
			StarsCustomerAccount starsAccount = newAccount.getStarsCustomerAccount();
			StarsUpdateLogin updateLogin = newAccount.getStarsUpdateLogin();
        	
			if (checkConstraint && energyCompany.searchAccountByAccountNo(starsAccount.getAccountNumber()) != null)
				throw new WebClientException( "The account number already exists" );
    		
			// Check to see if the login is valid
			if (updateLogin != null) {
				if (updateLogin.getUsername().trim().length() == 0)
					throw new WebClientException( "Username cannot be empty" );
				if (updateLogin.getPassword().trim().length() == 0)
					throw new WebClientException( "Password cannot be empty" );
				if (DaoFactory.getYukonUserDao().getLiteYukonUser( updateLogin.getUsername() ) != null)
					throw new WebClientException( "Username already exists" );
			}
    		
			/* Create yukon user */
			int userID = com.cannontech.user.UserUtils.USER_DEFAULT_ID;
			if (updateLogin != null)
				userID = UpdateLoginAction.createLogin( updateLogin, null, energyCompany, authTypeChanged ).getUserID();
        	
			/* Create contacts */
			com.cannontech.database.data.customer.Contact primContact = new com.cannontech.database.data.customer.Contact();
			StarsFactory.setCustomerContact( primContact, starsAccount.getPrimaryContact() );
			primContact.getContact().setLogInID( new Integer(userID) );
			primContact = Transaction.createTransaction(Transaction.INSERT, primContact).execute();
        	
			ArrayList<Contact> addContacts = new ArrayList<Contact>();
			for (int i = 0; i < starsAccount.getAdditionalContactCount(); i++) {
				Contact contact =
						new com.cannontech.database.data.customer.Contact();
				StarsFactory.setCustomerContact( contact, starsAccount.getAdditionalContact(i) );
				contact = Transaction.createTransaction(Transaction.INSERT, contact).execute();
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
				customerDB.setCustomerTypeID( new Integer(CustomerTypes.CUSTOMER_CI));
				
				((com.cannontech.database.data.customer.CICustomerBase)customer).getCiCustomerBase().setCompanyName( starsAccount.getCompany() );
				
                ((com.cannontech.database.data.customer.CICustomerBase)customer).getCiCustomerBase().setCICustType(newAccount.getStarsCustomerAccount().getCICustomerType() );
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
			if (timeZone == null) {
	            timeZone = energyCompany.getDefaultTimeZone().getID();
			}
			if (timeZone == null)
				timeZone = "(none)";
			customerDB.setTimeZone( timeZone );
        	
			int[] contactIDs = new int[ addContacts.size() ];
			for (int i = 0; i < addContacts.size(); i++) {
				com.cannontech.database.data.customer.Contact contact =
						addContacts.get(i);
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
            acctSiteDB.setCustAtHome( starsAccount.getCustAtHome() );
            acctSiteDB.setCustomerStatus( starsAccount.getCustStatus() );
			/* CustomerAccount->AccountSite: End */
			
			accountDB.setAccountNumber( starsAccount.getAccountNumber() );
			accountDB.setAccountNotes( starsAccount.getAccountNotes() );
            customer.getCustomer().setCustomerNumber(starsAccount.getCustomerNumber());
			customer.getCustomer().setRateScheduleID(new Integer(starsAccount.getRateScheduleID()));
			customer.getCustomer().setAltTrackingNumber(starsAccount.getAltTrackingNumber());
            customer.getCustomer().setTemperatureUnit(DaoFactory.getAuthDao().getRolePropertyValue(energyCompany.getUser().getUserID(),
                                                      EnergyCompanyRole.DEFAULT_TEMPERATURE_UNIT));
			account.setCustomer( customer );
			account.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
        	
			account = Transaction.createTransaction( Transaction.INSERT, account ).execute();
        	
			/* Create lite objects */
			LiteContact liteContact = new LiteContact( primContact.getContact().getContactID().intValue() );
			ServerUtils.handleDBChange(liteContact, DBChangeMsg.CHANGE_TYPE_ADD);
			
			for (int i = 0; i < addContacts.size(); i++) {
				com.cannontech.database.data.customer.Contact contact =
						addContacts.get(i);
				liteContact = new LiteContact( contact.getContact().getContactID().intValue() );
				ServerUtils.handleDBChange(liteContact, DBChangeMsg.CHANGE_TYPE_ADD);
			}
        	
			LiteCustomer liteCustomer = (customer instanceof com.cannontech.database.data.customer.CICustomerBase)?
				new LiteCICustomer( customerDB.getCustomerID().intValue() ) :
				new LiteCustomer( customerDB.getCustomerID().intValue() );
			ServerUtils.handleDBChange(liteCustomer, DBChangeMsg.CHANGE_TYPE_ADD);
			
		    final StarsCustAccountInformationDao starsCustAccountInformationDao = 
		        YukonSpringHook.getBean("starsCustAccountInformationDao", StarsCustAccountInformationDao.class);
		    
			LiteStarsCustAccountInformation liteAcctInfo = starsCustAccountInformationDao.getById(account.getCustomerAccount().getAccountID(),
			                                                                                      energyCompany.getEnergyCompanyID());
			ServerUtils.handleDBChange( liteAcctInfo, DBChangeMsg.CHANGE_TYPE_ADD );
            return liteAcctInfo;
		}
		catch (CommandExecutionException e) {
			CTILogger.error( e.getMessage(), e );
			throw new WebClientException( "Failed to create the customer account", e );
		}
	}
	
	public static LiteStarsCustAccountInformation newCustomerAccount(StarsNewCustomerAccount newAccount, LiteStarsEnergyCompany energyCompany)
		throws WebClientException
	{
		return newCustomerAccount(newAccount, energyCompany, true, false);
	}

}
