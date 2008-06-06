package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.stars.LiteAccountSite;
import com.cannontech.database.data.lite.stars.LiteCustomerAccount;
import com.cannontech.database.data.lite.stars.LiteSiteInformation;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.customer.Address;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.BillingAddress;
import com.cannontech.stars.xml.serialize.ContactNotification;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSiteInformation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateCustomerAccount;
import com.cannontech.stars.xml.serialize.StreetAddress;
import com.cannontech.stars.xml.serialize.Substation;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class UpdateCustAccountAction implements ActionBase {

	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
					session.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );

			StarsUpdateCustomerAccount updateAccount = (StarsUpdateCustomerAccount)
					StarsFactory.newStarsCustAccount( starsAcctInfo.getStarsCustomerAccount(), StarsUpdateCustomerAccount.class );

			updateAccount.setAccountNumber( req.getParameter("AcctNo") );
			updateAccount.setIsCommercial( req.getParameter("Commercial") != null );
			if (req.getParameter("Company") != null && updateAccount.getIsCommercial())
				updateAccount.setCompany( req.getParameter("Company") );
            if(updateAccount.getIsCommercial())
                updateAccount.setCICustomerType( Integer.parseInt(req.getParameter("CommercialType")));
            if (req.getParameter("CustomerNumber") != null)
				updateAccount.setCustomerNumber( req.getParameter("CustomerNumber") );
			if (req.getParameter("RateSchedule") != null)
				updateAccount.setRateScheduleID( Integer.parseInt(req.getParameter("RateSchedule")) );
			if (req.getParameter("AltTrackNum") != null)
				updateAccount.setAltTrackingNumber( req.getParameter("AltTrackNum") );
            if (req.getParameter("CustStatus") != null)
                updateAccount.setCustStatus( req.getParameter("CustStatus") );
            if (req.getParameter("CustAtHome") != null)
                updateAccount.setCustAtHome( req.getParameter("CustAtHome") );
			updateAccount.setAccountNotes( req.getParameter("AcctNotes").replaceAll(System.getProperty("line.separator"), "<br>") );

			updateAccount.setPropertyNumber( req.getParameter("PropNo") );
			updateAccount.setPropertyNotes( req.getParameter("PropNotes").replaceAll(System.getProperty("line.separator"), "<br>") );

			StreetAddress propAddr = new StreetAddress();
			propAddr.setStreetAddr1( req.getParameter("SAddr1") );
			propAddr.setStreetAddr2( req.getParameter("SAddr2") );
			propAddr.setCity( req.getParameter("SCity") );
			propAddr.setState( req.getParameter("SState") );
			propAddr.setZip( req.getParameter("SZip") );
			propAddr.setCounty( req.getParameter("SCounty") );
			updateAccount.setStreetAddress( propAddr );

			Substation starsSub = new Substation();
			starsSub.setEntryID( Integer.parseInt(req.getParameter("Substation")) );
			
			StarsSiteInformation siteInfo = new StarsSiteInformation();
			siteInfo.setSubstation( starsSub );
			siteInfo.setFeeder( req.getParameter("Feeder") );
			siteInfo.setPole( req.getParameter("Pole") );
			siteInfo.setTransformerSize( req.getParameter("TranSize") );
			siteInfo.setServiceVoltage( req.getParameter("ServVolt") );
			updateAccount.setStarsSiteInformation( siteInfo );

			BillingAddress billAddr = new BillingAddress();
			if (req.getParameter("CopyAddress") != null) {
				billAddr.setStreetAddr1( req.getParameter("SAddr1") );
				billAddr.setStreetAddr2( req.getParameter("SAddr2") );
				billAddr.setCity( req.getParameter("SCity") );
				billAddr.setState( req.getParameter("SState") );
				billAddr.setZip( req.getParameter("SZip") );
				//billAddr.setCounty( req.getParameter("BCounty") );
			}
			else {
				billAddr.setStreetAddr1( req.getParameter("BAddr1") );
				billAddr.setStreetAddr2( req.getParameter("BAddr2") );
				billAddr.setCity( req.getParameter("BCity") );
				billAddr.setState( req.getParameter("BState") );
				billAddr.setZip( req.getParameter("BZip") );
			}
			updateAccount.setBillingAddress( billAddr );

			PrimaryContact primContact = updateAccount.getPrimaryContact();
			primContact.setLastName( req.getParameter("LastName") );
			primContact.setFirstName( req.getParameter("FirstName") );
			
			int homeIndex = -1;
			int workIndex = -1;
			int emailIndex = -1;
			
            String homePhone = req.getParameter("HomePhone");
            String workPhone = req.getParameter("WorkPhone");
            homePhone = ServletUtils.formatPhoneNumberForStorage(homePhone);
            workPhone = ServletUtils.formatPhoneNumberForStorage(workPhone);
            String email = req.getParameter("Email");
            
            for(int j = 0; j < primContact.getContactNotificationCount(); j++)
			{
				if(((ContactNotification)primContact.getContactNotification(j)).getNotifCatID() == YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE)
					homeIndex = j;
				else if(((ContactNotification)primContact.getContactNotification(j)).getNotifCatID() == YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE)
					workIndex = j;
				else if(((ContactNotification)primContact.getContactNotification(j)).getNotifCatID() == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL)
					emailIndex = j;
			}

			if (homePhone != null) 
			{
                if( homePhone.length() < 1 && homeIndex != -1) {
                    primContact.removeContactNotification(homeIndex);
                    if(workIndex > homeIndex)
                        workIndex--;
                    if(emailIndex > homeIndex)
                        emailIndex--;
                }
			    else if(homeIndex != -1)
					primContact.getContactNotification()[homeIndex].setNotification(homePhone);
				else if(homePhone.length() > 0)
					primContact.addContactNotification( ServletUtils.createContactNotification(homePhone, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE ) );
			} 
			
			if (workPhone != null)
			{
                if( workPhone.length() < 1 && workIndex != -1) {
                    primContact.removeContactNotification(workIndex);
                    if(homeIndex > workIndex)
                        homeIndex--;
                    if(emailIndex > homeIndex)
                        emailIndex--;
                }
			    else if(workIndex != -1)
					primContact.getContactNotification()[workIndex].setNotification(workPhone);
				else if(workPhone.length() > 0)
					primContact.addContactNotification( ServletUtils.createContactNotification(workPhone, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE ) );
			} 
			
			if (email != null) 
			{
                if( email.length() < 1 && emailIndex != -1) {
                    primContact.removeContactNotification(emailIndex);
                    if(homeIndex > emailIndex)
                        homeIndex--;
                    if(workIndex > emailIndex)
                        workIndex--;
                }
			    else if(emailIndex != -1) {
					primContact.getContactNotification()[emailIndex].setNotification(email);
                    primContact.getContactNotification()[emailIndex].setDisabled(req.getParameter("NotifyControl") == null);
                }
				else if(email.length() > 0) {
                    ContactNotification emailNotif = ServletUtils.createContactNotification(email, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
                    emailNotif.setDisabled( req.getParameter("NotifyControl") == null );
					primContact.addContactNotification( emailNotif );
                }
			} 
            
			updateAccount.setPrimaryContact( primContact );
			updateAccount.setAdditionalContact( starsAcctInfo.getStarsCustomerAccount().getAdditionalContact() );

			StarsOperation operation = new StarsOperation();
			operation.setStarsUpdateCustomerAccount( updateAccount );

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

	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
		StarsOperation respOper = new StarsOperation();
        
		try {
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			StarsUpdateCustomerAccount updateAccount = reqOper.getStarsUpdateCustomerAccount();
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) session.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
            
			try {
				updateCustomerAccount( updateAccount, liteAcctInfo, energyCompany );
			}
			catch (WebClientException e) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, e.getMessage()) );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			StarsSuccess success = new StarsSuccess();
			success.setDescription( "Customer account updated successfully" );
            session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Customer account updated successfully" );
            
            // Make sure wretched STARS cache and session objects show the latest changes
            StarsCustAccountInformation starsCust = energyCompany.getStarsCustAccountInformation(updateAccount.getAccountID(), true);
            session.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, starsCust);
            session.setAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo );
                        
            EventUtils.logSTARSEvent(user.getUserID(), EventUtils.EVENT_CATEGORY_ACCOUNT, YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_UPDATED, updateAccount.getAccountID(), session);
            
			respOper.setStarsSuccess( success );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Failed to update the customer account information") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			catch (Exception e2) {
				CTILogger.error( e2.getMessage(), e2 );
			}
		}
        
		return null;
	}

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
					session.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			
			StarsUpdateCustomerAccount updateAccount = SOAPUtil.parseSOAPMsgForOperation(reqMsg).getStarsUpdateCustomerAccount();
			accountInfo.setStarsCustomerAccount( (StarsCustomerAccount)
					StarsFactory.newStarsCustAccount(updateAccount, StarsCustomerAccount.class) );

			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
    
	public static void updateCustomerAccount(StarsUpdateCustomerAccount updateAccount, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
		throws WebClientException
	{
		java.sql.Connection conn = null;
		boolean autoCommit = true;
    	
		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit( false );
    		
			/* Update customer account */
			LiteCustomerAccount liteAccount = liteAcctInfo.getCustomerAccount();
            String comparableAcctNum = updateAccount.getAccountNumber();
            String existingAcctNum = liteAccount.getAccountNumber();
            /*
             * More new rotation digit stuff.  It always thinks the account exists if rotation digits
             * enter the picture; we need to make sure this doesn't happen.
             */
            String comparableDigitProperty = DaoFactory.getAuthDao().getRolePropertyValue(DaoFactory.getYukonUserDao().getLiteYukonUser(energyCompany.getUserID()), ConsumerInfoRole.ACCOUNT_NUMBER_LENGTH);
            String rotationDigitProperty = DaoFactory.getAuthDao().getRolePropertyValue(DaoFactory.getYukonUserDao().getLiteYukonUser(energyCompany.getUserID()), ConsumerInfoRole.ROTATION_DIGIT_LENGTH);
            if(rotationDigitProperty != null && rotationDigitProperty.compareTo(CtiUtilities.STRING_NONE) != 0 && Integer.parseInt(rotationDigitProperty) > 0)
            {
                if(comparableAcctNum.length() >= Integer.parseInt(rotationDigitProperty))
                    comparableAcctNum = comparableAcctNum.substring(0, comparableAcctNum.length() - Integer.parseInt(rotationDigitProperty));
                if(existingAcctNum.length() >= Integer.parseInt(rotationDigitProperty)
                    && existingAcctNum.length() > comparableAcctNum.length())
                    existingAcctNum = existingAcctNum.substring(0, existingAcctNum.length() - Integer.parseInt(rotationDigitProperty));
            }
            if(comparableDigitProperty != null && comparableDigitProperty.compareTo(CtiUtilities.STRING_NONE) != 0 && Integer.parseInt(comparableDigitProperty) > 0)
            { 
                if(comparableAcctNum.length() >= Integer.parseInt(comparableDigitProperty))
                    comparableAcctNum = comparableAcctNum.substring(0, Integer.parseInt(comparableDigitProperty));
                if(existingAcctNum.length() >= Integer.parseInt(comparableDigitProperty))
                    existingAcctNum = existingAcctNum.substring(0, Integer.parseInt(comparableDigitProperty));
            }    
            if (!existingAcctNum.equalsIgnoreCase( comparableAcctNum )) {
				// Check to see if the account number has duplicates
				if (energyCompany.searchAccountByAccountNo(updateAccount.getAccountNumber()) != null)
					throw new WebClientException( "Account number already exists" );
			}
        	
			LiteAddress liteBillAddr = energyCompany.getAddress( liteAccount.getBillingAddressID() );
			BillingAddress starsBillAddr = updateAccount.getBillingAddress();
        	
			if (!StarsLiteFactory.isIdenticalCustomerAddress( liteBillAddr, starsBillAddr )) {
				com.cannontech.database.db.customer.Address billAddr =
						(com.cannontech.database.db.customer.Address) StarsLiteFactory.createDBPersistent( liteBillAddr );
				StarsFactory.setCustomerAddress( billAddr, starsBillAddr );
        		
				billAddr.setDbConnection( conn );
				billAddr.update();
				StarsLiteFactory.setLiteAddress( liteBillAddr, billAddr );
			}
        	
			if (!StarsLiteFactory.isIdenticalCustomerAccount( liteAccount, updateAccount )) {
				com.cannontech.database.db.stars.customer.CustomerAccount account =
						(com.cannontech.database.db.stars.customer.CustomerAccount) StarsLiteFactory.createDBPersistent( liteAccount );
				StarsFactory.setCustomerAccount( account, updateAccount );
        		
				account.setDbConnection( conn );
				account.update();
				StarsLiteFactory.setLiteCustomerAccount( liteAccount, account );
			}
			
			/* Update customer */
			LiteCustomer liteCustomer = liteAcctInfo.getCustomer();

			com.cannontech.database.db.customer.Customer customerDB = new com.cannontech.database.db.customer.Customer();
			StarsLiteFactory.setCustomer( customerDB, liteCustomer );
        	
			LiteContact litePrimContact = DaoFactory.getContactDao().getContact( liteCustomer.getPrimaryContactID() );
			PrimaryContact starsPrimContact = updateAccount.getPrimaryContact();
			
			boolean primContChanged = false;
			boolean ciCustChanged = false;
			boolean altCustFieldChanged = false;
            boolean custAcctChanged = false;
        	
			//For new RateScheduleID and CustomerNumber fields
			if (liteCustomer.getCustomerNumber().compareTo( updateAccount.getCustomerNumber() ) != 0) 
			{
				// Customer number has changed
				customerDB.setCustomerNumber(updateAccount.getCustomerNumber());
                liteCustomer.setCustomerNumber(updateAccount.getCustomerNumber());
				altCustFieldChanged = true;
			}
			if (liteCustomer.getRateScheduleID() != updateAccount.getRateScheduleID()) 
			{
				// Rate schedule ID has changed
				customerDB.setRateScheduleID(new Integer(updateAccount.getRateScheduleID()));
				altCustFieldChanged = true;
			}
			
			if (liteCustomer.getAltTrackingNumber().compareTo( updateAccount.getAltTrackingNumber() ) != 0) 
			{
				// Customer number has changed
				customerDB.setAltTrackingNumber(updateAccount.getAltTrackingNumber());
				altCustFieldChanged = true;
			}
			
			if (!StarsLiteFactory.isIdenticalCustomerContact( litePrimContact, starsPrimContact )) {
				com.cannontech.database.data.customer.Contact primContact = new com.cannontech.database.data.customer.Contact();
				StarsLiteFactory.setContact( primContact, litePrimContact, energyCompany );
				StarsFactory.setCustomerContact( primContact, starsPrimContact );
        		
				primContact.setDbConnection( conn );
				primContact.update();
				
				StarsLiteFactory.setLiteContact( litePrimContact, primContact );
				primContChanged = true;
			}
        	
			if (liteCustomer instanceof LiteCICustomer && liteCustomer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI) {
				LiteCICustomer liteCICust = (LiteCICustomer) liteCustomer;
				
                /*
                 * If we are already inside this if statement, then we know the account
                 * was previously commercial.  Check to see if it is no longer the case.
                 */
                if(! updateAccount.getIsCommercial())
                {
                    com.cannontech.database.db.customer.CICustomerBase ciDB = new com.cannontech.database.db.customer.CICustomerBase();
                    ciDB.setCustomerID( customerDB.getCustomerID() );
                    ciDB.setDbConnection( conn );
                    ciDB.delete();
                    
                    customerDB.setCustomerTypeID(new Integer(CustomerTypes.CUSTOMER_RESIDENTIAL));
                    liteCustomer.setCustomerTypeID(CustomerTypes.CUSTOMER_RESIDENTIAL);
                    altCustFieldChanged = true;
                    /*AddressID may be 0 if it wasn't filled in, so don't try to delete it!*/
                    if(liteCICust.getMainAddressID() != 0) {
                        Address companyAddress = new Address();
                        companyAddress.setAddressID(liteCICust.getMainAddressID());
                        companyAddress.setDbConnection(conn);
                        companyAddress.delete();
                    }
                    
                    /*Have I mentioned how much I hate cache and random STARS session variables?*/
                    liteAcctInfo.setCustomer(liteCustomer);
                    StarsCustAccountInformation starsCust = energyCompany.getStarsCustAccountInformation(updateAccount.getAccountID(), true);
                    starsCust.getStarsCustomerAccount().setCICustomerType(0);
                    starsCust.getStarsCustomerAccount().setCompany("");
                    starsCust.getStarsCustomerAccount().setIsCommercial(false);
                }
                else 
                {
					com.cannontech.database.db.customer.CICustomerBase ciDB = new com.cannontech.database.db.customer.CICustomerBase();
					ciDB.setCustomerID( customerDB.getCustomerID() );
					ciDB.setDbConnection( conn );
					ciDB.retrieve();
        			
					ciDB.setCompanyName( updateAccount.getCompany() );
                    ciDB.setCICustType(new Integer(updateAccount.getCICustomerType()));
					ciDB.setDbConnection( conn );
					ciDB.update();
					        			
					liteCICust.setCompanyName( ciDB.getCompanyName() );
                    liteCICust.setCICustType(ciDB.getCICustType());
                    liteAcctInfo.setCustomer(liteCICust);
					ciCustChanged = true;
                    
                    /*Have I mentioned how much I hate cache and random STARS session variables?*/
                    StarsCustAccountInformation starsCust = energyCompany.getStarsCustAccountInformation(updateAccount.getAccountID(), true);
                    starsCust.getStarsCustomerAccount().setCICustomerType(ciDB.getCICustType());
                    starsCust.getStarsCustomerAccount().setCompany(ciDB.getCompanyName());
                    starsCust.getStarsCustomerAccount().setIsCommercial(true);
				}
			}
			//wasn't commercial before, so I guess we better add it now that it is
            else if( updateAccount.getIsCommercial())
            {
                com.cannontech.database.db.customer.CICustomerBase ciDB = new com.cannontech.database.db.customer.CICustomerBase();
                ciDB.setCustomerID( customerDB.getCustomerID() );
                ciDB.setCompanyName( updateAccount.getCompany() );
                ciDB.setCICustType(new Integer(updateAccount.getCICustomerType()));
                ciDB.setDbConnection( conn );
                ciDB.add();
                customerDB.setCustomerTypeID(new Integer(CustomerTypes.CUSTOMER_CI));
                liteCustomer.setCustomerTypeID(CustomerTypes.CUSTOMER_CI);
                                    
                ciCustChanged = true;
                
                /*This is chunky but it seems to cause cache problems without it*/
                LiteCICustomer liteCICustomer = new LiteCICustomer();
                liteCICustomer.setCustomerID(liteCustomer.getCustomerID());
                liteCICustomer.setCustomerTypeID(new Integer(CustomerTypes.CUSTOMER_CI));
                liteCICustomer.setAccountIDs(liteCustomer.getAccountIDs());
                liteCICustomer.setAdditionalContacts(liteCustomer.getAdditionalContacts());
                liteCICustomer.setAltTrackingNumber(liteCustomer.getAltTrackingNumber());
                liteCICustomer.setCustomerNumber(liteCustomer.getCustomerNumber());
                liteCICustomer.setEnergyCompanyID(liteCustomer.getEnergyCompanyID());
                liteCICustomer.setPrimaryContactID(liteCustomer.getPrimaryContactID());
                liteCICustomer.setRateScheduleID(liteCustomer.getRateScheduleID());
                liteCICustomer.setTemperatureUnit(liteCustomer.getTemperatureUnit());
                liteCICustomer.setTimeZone(liteCustomer.getTimeZone());
                liteCICustomer.setCICustType(ciDB.getCICustType());
                liteCICustomer.setCompanyName(ciDB.getCompanyName());
                liteAcctInfo.setCustomer(liteCICustomer);
                /*Have I mentioned how much I hate cache and random STARS session variables?*/
                StarsCustAccountInformation starsCust = energyCompany.getStarsCustAccountInformation(updateAccount.getAccountID(), true);
                starsCust.getStarsCustomerAccount().setCICustomerType(ciDB.getCICustType());
                starsCust.getStarsCustomerAccount().setCompany(ciDB.getCompanyName());
                starsCust.getStarsCustomerAccount().setIsCommercial(true);
            }
			
			if(altCustFieldChanged || ciCustChanged)
			{
				customerDB.setDbConnection(conn);
				customerDB.update();
                custAcctChanged = true;
			}
            
			/* Update account site */
			LiteAccountSite liteAcctSite = liteAcctInfo.getAccountSite();
        	
			LiteAddress liteStAddr = energyCompany.getAddress( liteAcctSite.getStreetAddressID() );
			StreetAddress starsStAddr = updateAccount.getStreetAddress();
        	
			if (!StarsLiteFactory.isIdenticalCustomerAddress( liteStAddr, starsStAddr )) {
				com.cannontech.database.db.customer.Address stAddr =
						(com.cannontech.database.db.customer.Address) StarsLiteFactory.createDBPersistent( liteStAddr );
				StarsFactory.setCustomerAddress( stAddr, starsStAddr );
        		
				stAddr.setDbConnection( conn );
				stAddr.update();
				StarsLiteFactory.setLiteAddress( liteStAddr, stAddr );
                custAcctChanged = true;
			}
        	
			if (!StarsLiteFactory.isIdenticalAccountSite( liteAcctSite, updateAccount )) {
				com.cannontech.database.db.stars.customer.AccountSite acctSite =
						(com.cannontech.database.db.stars.customer.AccountSite) StarsLiteFactory.createDBPersistent( liteAcctSite );
				StarsFactory.setAccountSite( acctSite, updateAccount );
        		
				acctSite.setDbConnection( conn );
				acctSite.update();
				StarsLiteFactory.setLiteAccountSite( liteAcctSite, acctSite );
                custAcctChanged = true;
			}
        	
			/* Update site information */
			LiteSiteInformation liteSiteInfo = liteAcctInfo.getSiteInformation();
			StarsSiteInformation starsSiteInfo = updateAccount.getStarsSiteInformation();
        	
			if (!StarsLiteFactory.isIdenticalSiteInformation(liteSiteInfo, starsSiteInfo)) {
				com.cannontech.database.db.stars.customer.SiteInformation siteInfo =
						(com.cannontech.database.db.stars.customer.SiteInformation) StarsLiteFactory.createDBPersistent( liteSiteInfo );
				StarsFactory.setSiteInformation( siteInfo, updateAccount );
        		
				siteInfo.setDbConnection( conn );
				siteInfo.update();
				StarsLiteFactory.setLiteSiteInformation( liteSiteInfo, siteInfo );
                custAcctChanged = true;
			}
			
			conn.commit();
			
			if (primContChanged) ServerUtils.handleDBChange( litePrimContact, DBChangeMsg.CHANGE_TYPE_UPDATE );
			//if (ciCustChanged || altCustFieldChanged) ServerUtils.handleDBChange( liteCustomer, DBChangeMsg.CHANGE_TYPE_UPDATE );
			/*this had been commented out.  Looks like Yao took this out in '03.  Why?  I think this might account for 
            the problem with multiple webservers not staying synced up on account information during imports and other changes!*/
            if (ciCustChanged || altCustFieldChanged || custAcctChanged) ServerUtils.handleDBChange( liteAcctInfo, DBChangeMsg.CHANGE_TYPE_UPDATE );
		}
		catch (java.sql.SQLException e) {
			System.out.println(e.getMessage());
            try {
				if (conn != null) conn.rollback();
			}
			catch (java.sql.SQLException e2) {}
    		
			throw new WebClientException( "Failed to update the customer account information", e );
		}
		finally {
			try {
				if (conn != null) {
					conn.setAutoCommit( autoCommit );
					conn.close();
				}
			}
			catch (java.sql.SQLException e) {}
		}
	}
}