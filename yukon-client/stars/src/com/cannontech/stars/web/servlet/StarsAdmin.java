package com.cannontech.stars.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.database.PoolManager;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteAddress;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteCustomerFAQ;
import com.cannontech.database.data.lite.stars.LiteInterviewQuestion;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMProgram;
import com.cannontech.database.data.lite.stars.LiteLMProgramEvent;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.LiteWebConfiguration;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.ECToGenericMapping;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.DeleteCustAccountsTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.DeleteCustAccountAction;
import com.cannontech.stars.web.action.UpdateThermostatScheduleAction;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.AnswerType;
import com.cannontech.stars.xml.serialize.CompanyAddress;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.QuestionType;
import com.cannontech.stars.xml.serialize.StarsApplianceCategory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsCustomerAddress;
import com.cannontech.stars.xml.serialize.StarsCustomerFAQ;
import com.cannontech.stars.xml.serialize.StarsCustomerFAQGroup;
import com.cannontech.stars.xml.serialize.StarsCustomerFAQs;
import com.cannontech.stars.xml.serialize.StarsDefaultThermostatSettings;
import com.cannontech.stars.xml.serialize.StarsEnergyCompany;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestion;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestions;
import com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse;
import com.cannontech.stars.xml.serialize.StarsLMPrograms;
import com.cannontech.stars.xml.serialize.StarsServiceCompanies;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.stars.xml.serialize.StarsUpdateThermostatSchedule;
import com.cannontech.stars.xml.serialize.StarsUpdateThermostatScheduleResponse;
import com.cannontech.stars.xml.util.SOAPUtil;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsAdmin extends HttpServlet {
    
    public static final String ENERGY_COMPANY_TEMP = "ENERGY_COMPANY_TEMP";
    public static final String SERVICE_COMPANY_TEMP = "SERVICE_COMPANY_TEMP";
    
	private static final String NEW_ADDRESS = "NEW_ADDRESS";
	private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );
    
    private String referer = null;
    private String redirect = null;
	
	/**
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null) {
        	resp.sendRedirect( req.getContextPath() + SOAPClient.LOGIN_URL );
        	return;
        }
        
        StarsYukonUser user = (StarsYukonUser)
        		session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
        
    	referer = req.getHeader( "referer" );
    	redirect = req.getParameter( ServletUtils.ATT_REDIRECT );
    	if (redirect == null) redirect = referer;
    	
		String action = req.getParameter( "action" );
		if (action == null) action = "";
		
		if (action.equalsIgnoreCase("DeleteCustAccounts"))
			deleteCustomerAccounts( user, req, session );
		else if (action.equalsIgnoreCase("UpdateAddress"))
			updateAddress( user, req, session );
		else if (action.equalsIgnoreCase("UpdateEnergyCompany"))
			updateEnergyCompany( user, req, session );
		else if (action.equalsIgnoreCase("UpdateApplianceCategory"))
			updateApplianceCategory( user, req, session );
		else if (action.equalsIgnoreCase("DeleteApplianceCategory"))
			deleteApplianceCategory( user, req, session );
		else if (action.equalsIgnoreCase("UpdateServiceCompany"))
			updateServiceCompany( user, req, session );
		else if (action.equalsIgnoreCase("DeleteServiceCompany"))
			deleteServiceCompany( user, req, session );
		else if (action.equalsIgnoreCase("UpdateFAQLink"))
			updateCustomerFAQLink( user, req, session );
		else if (action.equalsIgnoreCase("UpdateFAQSubjects"))
			updateCustomerFAQSubjects( user, req, session );
		else if (action.equalsIgnoreCase("UpdateCustomerFAQs"))
			updateCustomerFAQs( user, req, session );
		else if (action.equalsIgnoreCase("DeleteFAQSubject"))
			deleteFAQSubject( user, req, session );
		else if (action.equalsIgnoreCase("UpdateInterviewQuestions"))
			updateInterviewQuestions( user, req, session );
		else if (action.equalsIgnoreCase("UpdateSelectionList"))
			updateCustomerSelectionList( user, req, session );
		else if (action.equalsIgnoreCase("UpdateThermostatSchedule"))
			updateThermostatSchedule( user, req, session );
		else if (action.equalsIgnoreCase("NewEnergyCompany"))
			createEnergyCompany( user, req, session );
		else if (action.equalsIgnoreCase("DeleteEnergyCompany"))
			deleteEnergyCompany( user, req, session );
        
    	resp.sendRedirect( redirect );
	}
	
	private void deleteCustomerAccounts(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			String acctNo = req.getParameter( "AcctNo" );
			int[] accountIDs = CustomerAccount.searchByAccountNumber(
					energyCompany.getEnergyCompanyID(), acctNo.replace('*', '%') );
			
			if (accountIDs != null) {
				if (accountIDs.length < 20) {
					for (int i = 0; i < accountIDs.length; i++) {
						LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[i], true );
						DeleteCustAccountAction.deleteCustomerAccount( liteAcctInfo, energyCompany );
					}
					
					if (accountIDs.length > 1)
						session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, accountIDs.length + " customer accounts have been deleted");
					else
						session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, accountIDs.length + " customer account have been deleted");
				}
				else {
					// Too many accounts to be deleted, it may take a while
					DeleteCustAccountsTask task = new DeleteCustAccountsTask(user, accountIDs);
					long id = ProgressChecker.addTask( task );
					session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
					redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
				}
			}
			else
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Search for account number failed");
		}
		catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Delete customer accounts failed");
		}
	}
	
	private void updateAddress(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsCustomerAddress starsAddr = null;
			
			int addressID = Integer.parseInt( req.getParameter("AddressID") );
			boolean newAddress = (addressID <= 0);
			String referer = req.getParameter( "REFERER" );
			
			if (referer.equalsIgnoreCase("EnergyCompany.jsp")) {
				StarsEnergyCompany ecTemp = (StarsEnergyCompany) session.getAttribute( ENERGY_COMPANY_TEMP );
				starsAddr = ecTemp.getCompanyAddress();
			}
			else if (referer.startsWith("ServiceCompany.jsp")) {
				StarsServiceCompany scTemp = (StarsServiceCompany) session.getAttribute( SERVICE_COMPANY_TEMP );
				starsAddr = scTemp.getCompanyAddress();
			}
			
			if (!newAddress) {
				LiteAddress liteAddr = energyCompany.getAddress( starsAddr.getAddressID() );
	        	com.cannontech.database.db.customer.Address addr =
	        			(com.cannontech.database.db.customer.Address) StarsLiteFactory.createDBPersistent( liteAddr );
	        	addr.setLocationAddress1( req.getParameter("StreetAddr1") );
	        	addr.setLocationAddress2( req.getParameter("StreetAddr2") );
	        	addr.setCityName( req.getParameter("City") );
	        	addr.setStateCode( req.getParameter("State") );
	        	addr.setZipCode( req.getParameter("Zip") );
	        	addr.setCounty( req.getParameter("County") );
	        	
	        	addr = (com.cannontech.database.db.customer.Address)
	        			Transaction.createTransaction( Transaction.UPDATE, addr ).execute();
	        	StarsLiteFactory.setLiteAddress( liteAddr, addr );
	        	StarsLiteFactory.setStarsCustomerAddress( starsAddr, liteAddr );
	        	
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Address information updated successfully");
			}
			else {
				starsAddr.setStreetAddr1( req.getParameter("StreetAddr1") );
				starsAddr.setStreetAddr2( req.getParameter("StreetAddr2") );
				starsAddr.setCity( req.getParameter("City") );
				starsAddr.setState( req.getParameter("State") );
				starsAddr.setZip( req.getParameter("Zip") );
				starsAddr.setCounty( req.getParameter("County") );
				
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Address information created, you must submit this page to finally save it");
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update address information");
			redirect = referer;
		}
	}
	
	private void updateEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsEnergyCompany ec = ecSettings.getStarsEnergyCompany();
        	
			// Create the data object from the request parameters
			com.cannontech.database.data.customer.Contact contact =
					new com.cannontech.database.data.customer.Contact();
			
			boolean newContact = (energyCompany.getPrimaryContactID() == CtiUtilities.NONE_ID);
			LiteContact liteContact = null;
			
			if (newContact) {
				contact.getContact().setContLastName( CtiUtilities.STRING_NONE );
				contact.getContact().setContFirstName( CtiUtilities.STRING_NONE );
			}
			else {
				liteContact = ContactFuncs.getContact( energyCompany.getPrimaryContactID() );
				StarsLiteFactory.setContact( contact, liteContact );
			}
			
			com.cannontech.database.db.contact.ContactNotification notifPhone = null;
			com.cannontech.database.db.contact.ContactNotification notifFax = null;
			com.cannontech.database.db.contact.ContactNotification notifEmail = null;
			
			for (int i = 0; i < contact.getContactNotifVect().size(); i++) {
				com.cannontech.database.db.contact.ContactNotification notif =
						(com.cannontech.database.db.contact.ContactNotification) contact.getContactNotifVect().get(i);
				// Set all the opcode to DELETE first, then change it to UPDATE or add INSERT accordingly
				notif.setOpCode( Transaction.DELETE );
				
				if (notif.getNotificationCatID().intValue() == YukonListEntryTypes.YUK_ENTRY_ID_PHONE)
					notifPhone = notif;
				else if (notif.getNotificationCatID().intValue() == YukonListEntryTypes.YUK_ENTRY_ID_FAX)
					notifFax = notif;
				else if (notif.getNotificationCatID().intValue() == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL)
					notifEmail = notif;
			}
			
			if (req.getParameter("PhoneNo").length() > 0) {
				if (notifPhone != null) {
					notifPhone.setNotification( req.getParameter("PhoneNo") );
					notifPhone.setOpCode( Transaction.UPDATE );
				}
				else {
					notifPhone = new com.cannontech.database.db.contact.ContactNotification();
					notifPhone.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_PHONE) );
					notifPhone.setDisableFlag( "Y" );
					notifPhone.setNotification( req.getParameter("PhoneNo") );
					notifPhone.setOpCode( Transaction.INSERT );
					
					contact.getContactNotifVect().add( notifPhone );
				}
			}
			
			if (req.getParameter("FaxNo").length() > 0) {
				if (notifFax != null) {
					notifFax.setNotification( req.getParameter("FaxNo") );
					notifFax.setOpCode( Transaction.UPDATE );
				}
				else {
					notifFax = new com.cannontech.database.db.contact.ContactNotification();
					notifFax.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_FAX) );
					notifFax.setDisableFlag( "Y" );
					notifFax.setNotification( req.getParameter("FaxNo") );
					notifFax.setOpCode( Transaction.INSERT );
					
					contact.getContactNotifVect().add( notifFax );
				}
			}
			
			if (req.getParameter("FaxNo").length() > 0) {
				if (notifFax != null) {
					notifFax.setNotification( req.getParameter("FaxNo") );
					notifFax.setOpCode( Transaction.UPDATE );
				}
				else {
					notifFax = new com.cannontech.database.db.contact.ContactNotification();
					notifFax.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_FAX) );
					notifFax.setDisableFlag( "Y" );
					notifFax.setNotification( req.getParameter("FaxNo") );
					notifFax.setOpCode( Transaction.INSERT );
					
					contact.getContactNotifVect().add( notifFax );
				}
			}
			
			boolean customizedEmail = Boolean.valueOf( req.getParameter("CustomizedEmail") ).booleanValue();
			String email = req.getParameter("Email");
			
			if (!customizedEmail && email.length() > 0) {
				if (notifEmail != null) {
					notifEmail.setNotification( email );
					notifEmail.setOpCode( Transaction.UPDATE );
				}
				else {
					notifEmail = new com.cannontech.database.db.contact.ContactNotification();
					notifEmail.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL) );
					notifEmail.setDisableFlag( "Y" );
					notifEmail.setNotification( email );
					notifEmail.setOpCode( Transaction.INSERT );
					
					contact.getContactNotifVect().add( notifEmail );
				}
			}
			
			if (newContact) {
				com.cannontech.database.db.customer.Address addr =
						new com.cannontech.database.db.customer.Address();
				addr.setStateCode( " " );
				StarsEnergyCompany ecTemp = (StarsEnergyCompany) session.getAttribute( ENERGY_COMPANY_TEMP );
				if (ecTemp != null)
					StarsFactory.setCustomerAddress( addr, ecTemp.getCompanyAddress() );
				contact.setAddress( addr );
				
				contact = (com.cannontech.database.data.customer.Contact)
						Transaction.createTransaction( Transaction.INSERT, contact ).execute();
				StarsLiteFactory.setLiteContact( liteContact, contact );
				energyCompany.addContact( liteContact, null );
				
				CompanyAddress starsAddr = new CompanyAddress();
				StarsLiteFactory.setStarsCustomerAddress(
						starsAddr, energyCompany.getAddress(contact.getContact().getAddressID().intValue()) );
				ec.setCompanyAddress( starsAddr );
			}
			else {
				contact = (com.cannontech.database.data.customer.Contact)
						Transaction.createTransaction( Transaction.UPDATE, contact ).execute();
				StarsLiteFactory.setLiteContact( liteContact, contact );
			}
			
			LiteContactNotification liteNotifPhone = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_PHONE );
			ec.setMainPhoneNumber( ServerUtils.getNotification(liteNotifPhone) );
			
			LiteContactNotification liteNotifFax = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_FAX );
			ec.setMainFaxNumber( ServerUtils.getNotification(liteNotifFax) );
			
			LiteContactNotification liteNotifEmail = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
			ec.setEmail( ServerUtils.getNotification(liteNotifEmail) );
			
			String compName = req.getParameter("CompanyName");
			if (newContact || !energyCompany.getName().equals( compName )) {
				energyCompany.setName( compName );
				energyCompany.setPrimaryContactID( contact.getContact().getContactID().intValue() );
				Transaction.createTransaction( Transaction.UPDATE, StarsLiteFactory.createDBPersistent(energyCompany) ).execute();
				
				ec.setCompanyName( compName );
			}
			
	        DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	        
	        // Update energy company role DEFAULT_TIME_ZONE if necessary
	        {
		        LiteYukonUser liteUser = YukonUserFuncs.getLiteYukonUser( energyCompany.getUserID() );
		        java.util.Map rolePropertyIDMap = (java.util.Map) cache.getYukonUserRolePropertyIDLookupMap().get( liteUser );
		        Pair rolePropertyPair = (Pair) rolePropertyIDMap.get( new Integer(EnergyCompanyRole.DEFAULT_TIME_ZONE) );
		        String value = (String) rolePropertyPair.getSecond();
		        
		        String timeZone = req.getParameter("TimeZone");
		        if (!value.equalsIgnoreCase( timeZone )) {
		        	String sql = "UPDATE YukonUserRole SET Value = '" + timeZone + "'" +
		        			" WHERE UserID = " + liteUser.getUserID() +
		        			" AND RoleID = " + EnergyCompanyRole.ROLEID +
		        			" AND RolePropertyID = " + EnergyCompanyRole.DEFAULT_TIME_ZONE;
		        	com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
		        			sql, CtiUtilities.getDatabaseAlias() );
		        	stmt.execute();
		        	ServerUtils.handleDBChange( liteUser, com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE );
		        	
		        	ec.setTimeZone( timeZone );
		        }
	        }
			
			// Update ResidentialCustomer role property CUSTOMIZED_UTIL_EMAIL_LINK if necessary
			{
		        LiteYukonGroup liteGroup = energyCompany.getResidentialCustomerGroup();
		        if (liteGroup != null) {
		        	String value = AuthFuncs.getRolePropValueGroup(
		        			liteGroup, ResidentialCustomerRole.WEB_LINK_UTIL_EMAIL, "(none)" );
		        	
		        	if (customizedEmail && !value.equals(email) ||
		        		!customizedEmail && ServerUtils.forceNotNone(value).length() > 0)
		        	{
			        	if (!customizedEmail) email = "(none)";
			        	String sql = "UPDATE YukonGroupRole SET Value = '" + email + "'" +
			        			" WHERE GroupID = " + liteGroup.getGroupID() +
			        			" AND RoleID = " + ResidentialCustomerRole.ROLEID +
			        			" AND RolePropertyID = " + ResidentialCustomerRole.WEB_LINK_UTIL_EMAIL;
			        	com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
			        			sql, CtiUtilities.getDatabaseAlias() );
			        	stmt.execute();
			        	
			        	ServerUtils.handleDBChange( liteGroup, DBChangeMsg.CHANGE_TYPE_UPDATE );
		        	}
		        }
			}
        	
        	session.removeAttribute( ENERGY_COMPANY_TEMP );
        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Energy company information updated successfully");
		}
		catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update energy company information");
			redirect = referer;
		}
	}
	
	private void updateApplianceCategory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		java.sql.Connection conn = null;
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsEnrollmentPrograms starsAppCats = ecSettings.getStarsEnrollmentPrograms();
			
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			int appCatID = Integer.parseInt( req.getParameter("AppCatID") );
			boolean newAppCat = (appCatID == -1);
			
			com.cannontech.database.db.web.YukonWebConfiguration config =
					new com.cannontech.database.db.web.YukonWebConfiguration();
			config.setLogoLocation( req.getParameter("IconName") );
			if (Boolean.valueOf( req.getParameter("SameAsName") ).booleanValue())
				config.setAlternateDisplayName( req.getParameter("Name") );
			else
				config.setAlternateDisplayName( req.getParameter("DispName") );
			config.setDescription( req.getParameter("Description").replaceAll(LINE_SEPARATOR, "<br>") );
			config.setURL( "" );
			
			com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
					new com.cannontech.database.data.stars.appliance.ApplianceCategory();
			com.cannontech.database.db.stars.appliance.ApplianceCategory appCatDB = appCat.getApplianceCategory();
			appCatDB.setCategoryID( Integer.valueOf(req.getParameter("Category")) );
			appCatDB.setDescription( req.getParameter("Name") );
			appCat.setWebConfiguration( config );
			
			LiteApplianceCategory liteAppCat = null;
			if (newAppCat) {
				appCat.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				appCat.setDbConnection( conn );
				appCat.add();
				
				liteAppCat = (LiteApplianceCategory) StarsLiteFactory.createLite( appCat.getApplianceCategory() );
				energyCompany.addApplianceCategory( liteAppCat );
				LiteWebConfiguration liteConfig = (LiteWebConfiguration) StarsLiteFactory.createLite( appCat.getWebConfiguration() );
				SOAPServer.addWebConfiguration( liteConfig );
			}
			else {
				liteAppCat = energyCompany.getApplianceCategory( appCatID );
				appCat.setApplianceCategoryID( new Integer(appCatID) );
				appCatDB.setWebConfigurationID( new Integer(liteAppCat.getWebConfigurationID()) );
				appCat.setDbConnection( conn );
				appCat.update();
				
				StarsLiteFactory.setLiteApplianceCategory( liteAppCat, appCat.getApplianceCategory() );
				LiteWebConfiguration liteConfig = SOAPServer.getWebConfiguration( appCat.getWebConfiguration().getConfigurationID().intValue() );
				StarsLiteFactory.setLiteWebConfiguration( liteConfig, appCat.getWebConfiguration() );
				energyCompany.updateStarsWebConfig( liteConfig.getConfigID() );
			}
			
			Integer applianceCategoryID = appCat.getApplianceCategory().getApplianceCategoryID();
			
			ArrayList pubProgList = new ArrayList();
			if (liteAppCat.getPublishedPrograms() != null) {
				for (int i = 0; i < liteAppCat.getPublishedPrograms().length; i++)
					pubProgList.add( liteAppCat.getPublishedPrograms()[i] );
			}
			
			String[] progIDs = req.getParameterValues( "ProgIDs" );
			String[] progDispNames = req.getParameterValues( "ProgDispNames" );
			String[] progShortNames = req.getParameterValues( "ProgShortNames" );
			String[] progDescriptions = req.getParameterValues( "ProgDescriptions" );
			String[] progCtrlOdds = req.getParameterValues( "ProgChanceOfCtrls" );
			String[] progIconNames = req.getParameterValues( "ProgIconNames" );
			
			if (progIDs != null) {
				LiteLMProgram[] pubPrograms = new LiteLMProgram[ progIDs.length ];
				for (int i = 0; i < progIDs.length; i++) {
					int progID = Integer.parseInt( progIDs[i] );
					
					for (int j = 0; j < pubProgList.size(); j++) {
						LiteLMProgram liteProg = (LiteLMProgram) pubProgList.get(j);
						if (liteProg.getProgramID() == progID) {
							pubProgList.remove(j);
							pubPrograms[i] = liteProg;
							break;
						}
					}
					
					com.cannontech.database.data.stars.LMProgramWebPublishing pubProg =
							new com.cannontech.database.data.stars.LMProgramWebPublishing();
					com.cannontech.database.db.stars.LMProgramWebPublishing pubProgDB = pubProg.getLMProgramWebPublishing();
					pubProgDB.setApplianceCategoryID( applianceCategoryID );
					pubProgDB.setLMProgramID( new Integer(progID) );
					pubProgDB.setChanceOfControlID( Integer.valueOf(progCtrlOdds[i]) );
					
					com.cannontech.database.db.web.YukonWebConfiguration cfg =
							new com.cannontech.database.db.web.YukonWebConfiguration();
					cfg.setLogoLocation( config.getLogoLocation() + "," + progIconNames[i] );
					cfg.setAlternateDisplayName( progDispNames[i] + "," + progShortNames[i] );
					cfg.setDescription( progDescriptions[i].replaceAll(LINE_SEPARATOR, "<br>") );
					pubProg.setWebConfiguration( cfg );
					
					if (pubPrograms[i] != null) {
						pubProg.getLMProgramWebPublishing().setWebSettingsID( new Integer(pubPrograms[i].getWebSettingsID()) );
						pubProg.setDbConnection( conn );
						pubProg.update();
						
						pubPrograms[i].setChanceOfControlID( pubProg.getLMProgramWebPublishing().getChanceOfControlID().intValue() );
						
						// If program display name changed, we need to update all the accounts with this program
						LiteWebConfiguration liteCfg = SOAPServer.getWebConfiguration( pubPrograms[i].getWebSettingsID() );
						
						String[] fields = ServerUtils.forceNotNull( liteCfg.getAlternateDisplayName() ).split(",");
						String oldDispName = (fields.length > 0)? fields[0] : "";
						if (oldDispName.length() == 0) oldDispName = pubPrograms[i].getProgramName();
						
						String newDispName = progDispNames[i];
						if (newDispName.length() == 0) newDispName = pubPrograms[i].getProgramName();
						
						StarsLiteFactory.setLiteWebConfiguration( liteCfg, pubProg.getWebConfiguration() );
						energyCompany.updateStarsWebConfig( liteCfg.getConfigID() );
						
						if (!newDispName.equals( oldDispName )) {
							int[] accountIDs = com.cannontech.database.db.stars.appliance.ApplianceBase.getAllAccountIDsWithProgram(new Integer(progID), energyCompany.getEnergyCompanyID());
							
							for (int j = 0; j < accountIDs.length; j++) {
								StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( accountIDs[j] );
								if (starsAcctInfo != null) {
									StarsLMPrograms starsProgs = starsAcctInfo.getStarsLMPrograms();
									
									for (int k = 0; k < starsProgs.getStarsLMProgramCount(); k++) {
										if (starsProgs.getStarsLMProgram(k).getProgramID() == progID) {
											starsProgs.getStarsLMProgram(k).setProgramName( newDispName );
											break;
										}
									}
								}
							}
						}
					}
					else {
						pubProg = (com.cannontech.database.data.stars.LMProgramWebPublishing)
								Transaction.createTransaction( Transaction.INSERT, pubProg ).execute();
						pubPrograms[i] = (LiteLMProgram) StarsLiteFactory.createLite( pubProg.getLMProgramWebPublishing() );
						energyCompany.addLMProgram( pubPrograms[i] );
						
						LiteWebConfiguration liteCfg = (LiteWebConfiguration) StarsLiteFactory.createLite( pubProg.getWebConfiguration() );
						SOAPServer.addWebConfiguration( liteCfg );
					}
				}
				
				liteAppCat.setPublishedPrograms( pubPrograms );
			}
			else
				liteAppCat.setPublishedPrograms( new LiteLMProgram[0] );
			
			// Delete the rest of published programs
			for (int i = 0; i < pubProgList.size(); i++) {
				LiteLMProgram liteProg = (LiteLMProgram) pubProgList.get(i);
				Integer programID = new Integer( liteProg.getProgramID() );
				
				// Delete all events of this program
				com.cannontech.database.data.stars.event.LMProgramEvent.deleteAllLMProgramEvents(
						energyCompany.getEnergyCompanyID(), programID, conn );
				
				// Unenroll the program from all customers currently enrolled in it
				int[] accountIDs = com.cannontech.database.db.stars.appliance.ApplianceBase.getAllAccountIDsWithProgram(
						programID, energyCompany.getEnergyCompanyID() );
				
				com.cannontech.database.data.stars.appliance.ApplianceBase app =
						new com.cannontech.database.data.stars.appliance.ApplianceBase();
				com.cannontech.database.db.stars.appliance.ApplianceBase appDB = app.getApplianceBase();
				
				for (int j = 0; j < accountIDs.length; j++) {
					LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[j], true );
					
					for (int k = 0; k < liteAcctInfo.getAppliances().size(); k++) {
						LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(k);
						if (liteApp.getLmProgramID() != liteProg.getProgramID()) continue;
						
						StarsLiteFactory.setApplianceBase( app, liteApp );
						appDB.setLMProgramID( new Integer(CtiUtilities.NONE_ID) );
						appDB.setDbConnection( conn );
						appDB.update();
						
						liteApp.setLmProgramID( CtiUtilities.NONE_ID );
					}
					
					ArrayList programs = liteAcctInfo.getLmPrograms();
					for (int k = 0; k < programs.size(); k++) {
						if (((LiteStarsLMProgram) programs.get(k)).getLmProgram().getProgramID() == liteProg.getProgramID()) {
							programs.remove(k);
							break;
						}
					}
					
					Iterator it = liteAcctInfo.getProgramHistory().iterator();
					while (it.hasNext()) {
						if (((LiteLMProgramEvent) it.next()).getProgramID() == liteProg.getProgramID())
							it.remove();
					}
					
					energyCompany.updateStarsCustAccountInformation( liteAcctInfo );
				}
				
				com.cannontech.database.data.stars.LMProgramWebPublishing pubProg =
						new com.cannontech.database.data.stars.LMProgramWebPublishing();
				pubProg.setApplianceCategoryID( applianceCategoryID );
				pubProg.setLMProgramID( programID );
				pubProg.getLMProgramWebPublishing().setWebSettingsID( new Integer(liteProg.getWebSettingsID()) );
				pubProg.setDbConnection( conn );
				pubProg.delete();
			
				energyCompany.deleteLMProgram( liteProg.getProgramID() );
				SOAPServer.deleteWebConfiguration( liteProg.getWebSettingsID() );
				energyCompany.deleteStarsWebConfig( liteProg.getWebSettingsID() );
			}
			
			StarsApplianceCategory starsAppCat = StarsLiteFactory.createStarsApplianceCategory( liteAppCat, energyCompany );
			if (newAppCat) {
				starsAppCats.addStarsApplianceCategory( starsAppCat );
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Appliance category is created successfully");
			}
			else {
				for (int i = 0; i < starsAppCats.getStarsApplianceCategoryCount(); i++) {
					if (starsAppCats.getStarsApplianceCategory(i).getApplianceCategoryID() == appCatID) {
						starsAppCats.setStarsApplianceCategory( i, starsAppCat );
						break;
					}
				}
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Appliance category information updated successfully");
			}
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update appliance category information");
			redirect = referer;
        }
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
	}
	
	private void deleteApplianceCategory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        java.sql.Connection conn = null;
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsEnrollmentPrograms starsAppCats = ecSettings.getStarsEnrollmentPrograms();
			
			int applianceCategoryID = Integer.parseInt( req.getParameter("AppCatID") );
			boolean deleteAll = (applianceCategoryID == -1);
			
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			for (int i = starsAppCats.getStarsApplianceCategoryCount() - 1; i >= 0; i--) {
				StarsApplianceCategory starsAppCat = starsAppCats.getStarsApplianceCategory(i);
				if (!deleteAll && starsAppCat.getApplianceCategoryID() != applianceCategoryID) continue;
				
				Integer appCatID = new Integer( starsAppCat.getApplianceCategoryID() );
				LiteApplianceCategory liteAppCat = energyCompany.getApplianceCategory( appCatID.intValue() );
				
				com.cannontech.database.db.stars.LMProgramWebPublishing.deleteAllLMProgramWebPublishing( appCatID, conn );
				for (int j = 0; j < liteAppCat.getPublishedPrograms().length; j++) {
					int configID = liteAppCat.getPublishedPrograms()[j].getWebSettingsID();
					com.cannontech.database.db.web.YukonWebConfiguration cfg =
							new com.cannontech.database.db.web.YukonWebConfiguration();
					cfg.setConfigurationID( new Integer(configID) );
					cfg.setDbConnection( conn );
					cfg.delete();
					
					SOAPServer.deleteWebConfiguration( configID );
					energyCompany.deleteStarsWebConfig( configID );
				}
				
				int[] programIDs = new int[ liteAppCat.getPublishedPrograms().length ];
				for (int j = 0; j < liteAppCat.getPublishedPrograms().length; j++) {
					programIDs[j] = liteAppCat.getPublishedPrograms()[j].getProgramID();
					
					// Delete all program events
					com.cannontech.database.data.stars.event.LMProgramEvent.deleteAllLMProgramEvents(
							energyCompany.getEnergyCompanyID(), new Integer(programIDs[j]), conn );
				}
				Arrays.sort( programIDs );
				
				int[] accountIDs = com.cannontech.database.db.stars.appliance.ApplianceBase.getAllAccountIDsWithCategory( appCatID );
				int[] applianceIDs = com.cannontech.database.db.stars.appliance.ApplianceBase.getAllApplianceIDsWithCategory( appCatID );
				
				com.cannontech.database.data.stars.appliance.ApplianceBase app =
						new com.cannontech.database.data.stars.appliance.ApplianceBase();
				for (int j = 0; j < applianceIDs.length; j++) {
					app.setApplianceID( new Integer(applianceIDs[j]) );
					app.setDbConnection( conn );
					app.delete();
				}
				
				com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
						new com.cannontech.database.data.stars.appliance.ApplianceCategory();
				StarsLiteFactory.setApplianceCategory( appCat.getApplianceCategory(), liteAppCat );
				appCat.setDbConnection( conn );
				appCat.delete();
				
				SOAPServer.deleteWebConfiguration( liteAppCat.getWebConfigurationID() );
				energyCompany.deleteStarsWebConfig( liteAppCat.getWebConfigurationID() );
				
				for (int j = 0; j < accountIDs.length; j++) {
					LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[j], true );
					
					Iterator appIt = liteAcctInfo.getAppliances().iterator();
					while (appIt.hasNext()) {
						LiteStarsAppliance liteApp = (LiteStarsAppliance) appIt.next();
						if (liteApp.getApplianceCategoryID() == appCatID.intValue()) {
							appIt.remove();
							
							Iterator progIt = liteAcctInfo.getLmPrograms().iterator();
							while (progIt.hasNext()) {
								LiteStarsLMProgram liteProg = (LiteStarsLMProgram) progIt.next();
								if (liteProg.getLmProgram().getProgramID() == liteApp.getLmProgramID())
									progIt.remove();
							}
						}
					}
					
					Iterator it = liteAcctInfo.getProgramHistory().iterator();
					while (it.hasNext()) {
						int progID = ((LiteLMProgramEvent) it.next()).getProgramID();
						if (Arrays.binarySearch(programIDs, progID) >= 0)
							it.remove();
					}
					
					energyCompany.updateStarsCustAccountInformation( liteAcctInfo );
				}
				
				energyCompany.deleteApplianceCategory( appCatID.intValue() );
				starsAppCats.removeStarsApplianceCategory( i );
			}
			
			if (deleteAll)
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Appliance categories have been deleted successfully");
			else
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Appliance category has been deleted successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete appliance category");
			redirect = referer;
        }
        finally {
        	try {
	        	if (conn != null) conn.close();
        	}
        	catch (java.sql.SQLException e) {}
        }
	}

	private void updateServiceCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsServiceCompanies starsCompanies = ecSettings.getStarsServiceCompanies();
			
			int companyID = Integer.parseInt( req.getParameter("CompanyID") );
			boolean newCompany = (companyID == -1);
			
			com.cannontech.database.data.stars.report.ServiceCompany company =
					new com.cannontech.database.data.stars.report.ServiceCompany();
			com.cannontech.database.db.stars.report.ServiceCompany companyDB = company.getServiceCompany();
			
			com.cannontech.database.data.customer.Contact contact =
					new com.cannontech.database.data.customer.Contact();
			com.cannontech.database.db.contact.Contact contactDB = contact.getContact();
			
			LiteServiceCompany liteCompany = null;
			LiteContact liteContact = null;
			LiteAddress liteAddr = null;
			
			StarsServiceCompany starsCompany = null;
			
			if (!newCompany) {
				liteCompany = energyCompany.getServiceCompany( companyID );
				StarsLiteFactory.setServiceCompany( companyDB, liteCompany );
				liteContact = ContactFuncs.getContact( liteCompany.getPrimaryContactID() );
				StarsLiteFactory.setContact( contact, liteContact );
				liteAddr = energyCompany.getAddress( liteCompany.getAddressID() );
        	}
        	
			companyDB.setCompanyName( req.getParameter("CompanyName") );
			companyDB.setMainPhoneNumber( ServletUtils.formatPhoneNumber(req.getParameter("PhoneNo")) );
			companyDB.setMainFaxNumber( ServletUtils.formatPhoneNumber(req.getParameter("FaxNo")) );
			companyDB.setHIType( req.getParameter("Type") );
			contactDB.setContLastName( req.getParameter("ContactLastName") );
			contactDB.setContFirstName( req.getParameter("ContactFirstName") );
			
			if (newCompany) {
				com.cannontech.database.db.customer.Address address = new com.cannontech.database.db.customer.Address();
				StarsServiceCompany scTemp = (StarsServiceCompany) session.getAttribute( SERVICE_COMPANY_TEMP );
				if (scTemp != null)
					StarsFactory.setCustomerAddress( address, scTemp.getCompanyAddress() );
				
				company.setAddress( address );
				company.setPrimaryContact( contactDB );
				company.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				company = (com.cannontech.database.data.stars.report.ServiceCompany)
						Transaction.createTransaction( Transaction.INSERT, company ).execute();
				
				liteAddr = (LiteAddress) StarsLiteFactory.createLite( company.getAddress() );
				energyCompany.addAddress( liteAddr );
				
				liteContact = (LiteContact) StarsLiteFactory.createLite( contact );
				energyCompany.addContact( liteContact, null );
				
				liteCompany = (LiteServiceCompany) StarsLiteFactory.createLite( company.getServiceCompany() );
				energyCompany.addServiceCompany( liteCompany );
				
				starsCompany = new StarsServiceCompany();
				starsCompany.setCompanyID( liteCompany.getCompanyID() );
				starsCompanies.addStarsServiceCompany( starsCompany );
				
				PrimaryContact starsContact = new PrimaryContact();
				StarsLiteFactory.setStarsCustomerContact(
						starsContact, ContactFuncs.getContact(company.getPrimaryContact().getContactID().intValue()) );
				starsCompany.setPrimaryContact( starsContact );
				
				CompanyAddress starsAddr = new CompanyAddress();
				StarsLiteFactory.setStarsCustomerAddress(
						starsAddr, energyCompany.getAddress(company.getAddress().getAddressID().intValue()) );
				starsCompany.setCompanyAddress( starsAddr );
			}
			else {
				contactDB = (com.cannontech.database.db.contact.Contact)
						Transaction.createTransaction( Transaction.UPDATE, contactDB ).execute();
				StarsLiteFactory.setLiteContact( liteContact, contact );
				
				companyDB = (com.cannontech.database.db.stars.report.ServiceCompany)
						Transaction.createTransaction( Transaction.UPDATE, companyDB ).execute();
				StarsLiteFactory.setLiteServiceCompany( liteCompany, companyDB );
				
				for (int i = 0; i < starsCompanies.getStarsServiceCompanyCount(); i++) {
					if (starsCompanies.getStarsServiceCompany(i).getCompanyID() == companyID) {
						starsCompany = starsCompanies.getStarsServiceCompany(i);
						break;
					}
				}
				if (starsCompany == null)
					throw new Exception ("Cannot find the StarsServiceCompany object with companyID = " + companyID);
				
				starsCompany.getPrimaryContact().setLastName( liteContact.getContLastName() );
				starsCompany.getPrimaryContact().setFirstName( liteContact.getContFirstName() );
			}
			
			starsCompany.setCompanyName( liteCompany.getCompanyName() );
			starsCompany.setMainPhoneNumber( liteCompany.getMainPhoneNumber() );
			starsCompany.setMainFaxNumber( liteCompany.getMainFaxNumber() );
        	
        	session.removeAttribute( SERVICE_COMPANY_TEMP );
        	if (newCompany)
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service company created successfully");
        	else
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service company information updated successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update service company information");
			redirect = referer;
        }
	}
	
	public static void deleteServiceCompany(int companyID, LiteStarsEnergyCompany energyCompany, java.sql.Connection conn)
		throws java.sql.SQLException
	{
		// set InstallationCompanyID = 0 for all inventory assigned to this service company
		ArrayList inventory = energyCompany.getAllInventory();
		
		for (int j = 0; j < inventory.size(); j++) {
			LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(j);
			
			if (liteInv.getInstallationCompanyID() == companyID) {
				com.cannontech.database.db.stars.hardware.InventoryBase invDB =
						new com.cannontech.database.db.stars.hardware.InventoryBase();
				StarsLiteFactory.setInventoryBase( invDB, liteInv );
		    	
				invDB.setInstallationCompanyID( new Integer(CtiUtilities.NONE_ID) );
				invDB.setDbConnection( conn );
				invDB.update();
		    	
				liteInv.setInstallationCompanyID( CtiUtilities.NONE_ID );
			}
		}
		
		// set ServiceCompanyID = 0 for all work orders assigned to this service company
		ArrayList orders = energyCompany.getAllWorkOrders();
		for (int j = 0; j < orders.size(); j++) {
			LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) orders.get(j);
			if (liteOrder.getServiceCompanyID() == companyID) {
				com.cannontech.database.db.stars.report.WorkOrderBase order =
						(com.cannontech.database.db.stars.report.WorkOrderBase) StarsLiteFactory.createDBPersistent( liteOrder );
				order.setServiceCompanyID( new Integer(CtiUtilities.NONE_ID) );
				order.setDbConnection( conn );
				order.update();
				
				liteOrder.setServiceCompanyID( CtiUtilities.NONE_ID );
			}
		}
		
		LiteServiceCompany liteCompany = energyCompany.getServiceCompany( companyID );
		
		com.cannontech.database.data.stars.report.ServiceCompany company =
				new com.cannontech.database.data.stars.report.ServiceCompany();
		StarsLiteFactory.setServiceCompany( company.getServiceCompany(), liteCompany );
		company.setDbConnection( conn );
		company.delete();
		
		energyCompany.deleteAddress( liteCompany.getAddressID() );
		energyCompany.deleteContact( liteCompany.getPrimaryContactID() );
		energyCompany.deleteServiceCompany( companyID );
		
		StarsServiceCompanies starsCompanies = energyCompany.getStarsServiceCompanies();
		for (int i = 0; i < starsCompanies.getStarsServiceCompanyCount(); i++) {
			if (starsCompanies.getStarsServiceCompany(i).getCompanyID() == companyID) {
				starsCompanies.removeStarsServiceCompany(i);
				break;
			}
		}
	}
	
	public static void deleteAllServiceCompanies(LiteStarsEnergyCompany energyCompany, java.sql.Connection conn)
		throws java.sql.SQLException
	{
		ArrayList companies = energyCompany.getAllServiceCompanies();
		
		for (int i = 0; i < companies.size(); i++) {
			LiteServiceCompany liteCompany = (LiteServiceCompany) companies.get(i);
			deleteServiceCompany( liteCompany.getCompanyID(), energyCompany, conn );
		}
	}
	
	private void deleteServiceCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        java.sql.Connection conn = null;
        
        try {
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			int companyID = Integer.parseInt( req.getParameter("CompanyID") );
			
			if (companyID == -1) {
				deleteAllServiceCompanies( energyCompany, conn );
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service companies have been deleted successfully");
			}
			else {
				deleteServiceCompany( companyID, energyCompany, conn );
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service company has been deleted successfully");
			}
        }
        catch (java.sql.SQLException e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete service company");
			redirect = referer;
        }
        finally {
        	try {
	        	if (conn != null) conn.close();
        	}
        	catch (java.sql.SQLException e) {}
        }
	}
	
	private void updateCustomerFAQLink(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        LiteYukonUser liteUser = user.getYukonUser();
        
        try {
        	boolean customizedFAQ = Boolean.valueOf( req.getParameter("CustomizedFAQ") ).booleanValue();
    		String faqLink = req.getParameter("FAQLink");
        	String value = AuthFuncs.getRolePropertyValue( liteUser, ConsumerInfoRole.WEB_LINK_FAQ, "(none)" );
			
        	LiteYukonGroup customerGroup = energyCompany.getResidentialCustomerGroup();
        	LiteYukonGroup operatorGroup = energyCompany.getWebClientOperatorGroup();
        	
        	boolean updateCustGroup = AuthFuncs.getRolePropValueGroup(customerGroup, ResidentialCustomerRole.WEB_LINK_FAQ, null) != null;
        	boolean updateOperGroup = AuthFuncs.getRolePropValueGroup(operatorGroup, ConsumerInfoRole.WEB_LINK_FAQ, null) != null;
        	
    		String sql = null;
    		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
    				sql, CtiUtilities.getDatabaseAlias() );
        	
        	if (customizedFAQ && !value.equals(faqLink) ||
        		!customizedFAQ && ServerUtils.forceNotNone(value).length() > 0)
        	{
        		if (!customizedFAQ) faqLink = "(none)";
    			if (updateCustGroup) {
		        	sql = "UPDATE YukonGroupRole SET Value = '" + faqLink + "'" +
		        			" WHERE GroupID = " + customerGroup.getGroupID() +
		        			" AND RoleID = " + ResidentialCustomerRole.ROLEID +
		        			" AND RolePropertyID = " + ResidentialCustomerRole.WEB_LINK_FAQ;
		        	stmt.setSQLString( sql );
		        	stmt.execute();
    			}
	        	
	        	if (updateOperGroup) {
		        	sql = "UPDATE YukonGroupRole SET Value = '" + faqLink + "'" +
		        			" WHERE GroupID = " + operatorGroup.getGroupID() +
		        			" AND RoleID = " + ConsumerInfoRole.ROLEID +
		        			" AND RolePropertyID = " + ConsumerInfoRole.WEB_LINK_FAQ;
	        	}
	        	else {
		        	sql = "UPDATE YukonUserRole SET Value = '" + faqLink + "'" +
		        			" WHERE UserID = " + liteUser.getUserID() +
		        			" AND RoleID = " + ConsumerInfoRole.ROLEID +
		        			" AND RolePropertyID = " + ConsumerInfoRole.WEB_LINK_FAQ;
	        	}
	        	stmt.setSQLString( sql );
        		stmt.execute();
        	}
        	
    		if (updateCustGroup)
	        	ServerUtils.handleDBChange( customerGroup, DBChangeMsg.CHANGE_TYPE_UPDATE );
	        if (updateOperGroup)
	        	ServerUtils.handleDBChange( operatorGroup, DBChangeMsg.CHANGE_TYPE_UPDATE );
	        else
	        	ServerUtils.handleDBChange( liteUser, DBChangeMsg.CHANGE_TYPE_UPDATE );

        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "FAQ link updated successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update FAQ link");
			redirect = referer;
        }
	}
	
	private void updateCustomerFAQSubjects(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsCustomerFAQs starsFAQs = ecSettings.getStarsCustomerFAQs();
			
			String[] subjectIDs = req.getParameterValues("SubjectIDs");
			
			for (int i = 0; i < subjectIDs.length; i++) {
				int subjectID = Integer.parseInt( subjectIDs[i] );
				YukonListEntry subject = energyCompany.getYukonListEntry( YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP, subjectID );
				subject.setEntryOrder( i+1 );
				
				com.cannontech.database.db.constants.YukonListEntry entry =
						StarsLiteFactory.createYukonListEntry( subject );
				Transaction.createTransaction( Transaction.UPDATE, entry ).execute();
				
				// Reorder the StarsCustomerFAQGroup objects
				for (int j = i; j < starsFAQs.getStarsCustomerFAQGroupCount(); j++) {
					StarsCustomerFAQGroup group = starsFAQs.getStarsCustomerFAQGroup(j);
					if (group.getSubjectID() == subjectID) {
						starsFAQs.removeStarsCustomerFAQGroup(j);
						starsFAQs.addStarsCustomerFAQGroup(i, group);
						break;
					}
				}
			}

        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "FAQ subjects updated successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update FAQ subjects");
			redirect = referer;
        }
	}
	
	private void updateCustomerFAQs(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        java.sql.Connection conn = null;
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsCustomerFAQs starsFAQs = ecSettings.getStarsCustomerFAQs();
			
			int subjectID = Integer.parseInt( req.getParameter("SubjectID") );
			boolean newGroup = (subjectID == -1);
			
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			String subject = req.getParameter("Subject");
			String[] questions = req.getParameterValues("Questions");
			String[] answers = req.getParameterValues("Answers");
			
			ArrayList liteFAQs = energyCompany.getAllCustomerFAQs();
			StarsCustomerFAQGroup starsGroup = null;
			
			if (newGroup) {
				YukonSelectionList cList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP );
				
				int nextOrderNo = 1;
				for (int i = 0; i < cList.getYukonListEntries().size(); i++) {
					YukonListEntry cEntry = (YukonListEntry) cList.getYukonListEntries().get(i);
					if (cEntry.getEntryOrder() >= nextOrderNo)
						nextOrderNo = cEntry.getEntryOrder() + 1;
				}
				
				com.cannontech.database.db.constants.YukonListEntry entry =
						new com.cannontech.database.db.constants.YukonListEntry();
				entry.setListID( new Integer(cList.getListID()) );
				entry.setEntryOrder( new Integer(nextOrderNo) );
				entry.setEntryText( subject );
				entry.setDbConnection( conn );
				entry.add();
				
				com.cannontech.common.constants.YukonListEntry cEntry =
						new com.cannontech.common.constants.YukonListEntry();
				StarsLiteFactory.setConstantYukonListEntry( cEntry, entry );
				YukonListFuncs.getYukonListEntries().put( entry.getEntryID(), cEntry );
				cList.getYukonListEntries().add( cEntry );
				
				starsGroup = new StarsCustomerFAQGroup();
				starsGroup.setSubjectID( cEntry.getEntryID() );
				starsGroup.setSubject( cEntry.getEntryText() );
				starsFAQs.addStarsCustomerFAQGroup( starsGroup );
			}
			else {
				synchronized (liteFAQs) {
					Iterator it = liteFAQs.iterator();
					while (it.hasNext()) {
						LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) it.next();
						if (liteFAQ.getSubjectID() == subjectID) {
							com.cannontech.database.db.stars.CustomerFAQ faq =
									new com.cannontech.database.db.stars.CustomerFAQ();
							faq.setQuestionID( new Integer(liteFAQ.getQuestionID()) );
							faq.setDbConnection( conn );
							faq.delete();
							
							it.remove();
						}
					}
				}
				
				for (int i = 0; i < starsFAQs.getStarsCustomerFAQGroupCount(); i++) {
					if (starsFAQs.getStarsCustomerFAQGroup(i).getSubjectID() == subjectID) {
						starsGroup = starsFAQs.getStarsCustomerFAQGroup(i);
						starsGroup.removeAllStarsCustomerFAQ();
						break;
					}
				}
				
				YukonListEntry cEntry = energyCompany.getYukonListEntry(
						YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP, subjectID );
				if (!cEntry.getEntryText().equals( subject )) {
					com.cannontech.database.db.constants.YukonListEntry entry = StarsLiteFactory.createYukonListEntry( cEntry );
					entry.setEntryText( subject );
					entry.setDbConnection( conn );
					entry.update();
					
					cEntry.setEntryText( subject );
					starsGroup.setSubject( subject );
				}
			}
			
			if (questions != null) {
				for (int i = 0; i < questions.length; i++) {
					com.cannontech.database.db.stars.CustomerFAQ faq =
							new com.cannontech.database.db.stars.CustomerFAQ();
					faq.setSubjectID( new Integer(starsGroup.getSubjectID()) );
					faq.setQuestion( questions[i] );
					faq.setAnswer( answers[i] );
					faq.setDbConnection( conn );
					faq.add();
					
					LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) StarsLiteFactory.createLite( faq );
					synchronized (liteFAQs) { liteFAQs.add(liteFAQ); }
					
					StarsCustomerFAQ starsFAQ = new StarsCustomerFAQ();
					starsFAQ.setQuestionID( liteFAQ.getQuestionID() );
					starsFAQ.setQuestion( liteFAQ.getQuestion() );
					starsFAQ.setAnswer( liteFAQ.getAnswer() );
					starsGroup.addStarsCustomerFAQ( starsFAQ );
				}
			}

        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Customer FAQs updated successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update customer FAQs");
			redirect = referer;
        }
        finally {
        	try {
	        	if (conn != null) conn.close();
        	}
        	catch (java.sql.SQLException e) {}
        }
	}
	
	private void deleteFAQSubject(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        java.sql.Connection conn = null;
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsCustomerFAQs starsFAQs = ecSettings.getStarsCustomerFAQs();
			
			int subjectID = Integer.parseInt( req.getParameter("SubjectID") );
			boolean deleteAll = (subjectID == -1);
			
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			ArrayList liteFAQs = energyCompany.getAllCustomerFAQs();
			
			for (int i = starsFAQs.getStarsCustomerFAQGroupCount() - 1; i >= 0; i--) {
				StarsCustomerFAQGroup starsGroup = starsFAQs.getStarsCustomerFAQGroup(i);
				if (!deleteAll && starsGroup.getSubjectID() != subjectID) continue;
				
				synchronized (liteFAQs) {
					Iterator it = liteFAQs.iterator();
					while (it.hasNext()) {
						LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) it.next();
						if (liteFAQ.getSubjectID() == starsGroup.getSubjectID()) {
							com.cannontech.database.db.stars.CustomerFAQ faq =
									new com.cannontech.database.db.stars.CustomerFAQ();
							faq.setQuestionID( new Integer(liteFAQ.getQuestionID()) );
							faq.setDbConnection( conn );
							faq.delete();
							
							it.remove();
						}
					}
				}
				
				YukonListEntry cEntry = energyCompany.getYukonListEntry(
						YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP, starsGroup.getSubjectID() );
				com.cannontech.database.db.constants.YukonListEntry entry =
						StarsLiteFactory.createYukonListEntry( cEntry );
				entry.setDbConnection( conn );
				entry.delete();
				
				YukonSelectionList cList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP );
				cList.getYukonListEntries().remove( cEntry );
				YukonListFuncs.getYukonListEntries().remove( entry.getEntryID() );
				
				starsFAQs.removeStarsCustomerFAQGroup(i);
			}
			
			if (deleteAll)
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service companies have been deleted successfully");
			else
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service company has been deleted successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete service company");
			redirect = referer;
        }
        finally {
        	try {
	        	if (conn != null) conn.close();
        	}
        	catch (java.sql.SQLException e) {}
        }
	}
	
	private void updateInterviewQuestions(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsExitInterviewQuestions starsExitQuestions = ecSettings.getStarsExitInterviewQuestions();
			
			String type = req.getParameter("type");
			int qType = CtiUtilities.NONE_ID;
			if (type.equalsIgnoreCase("Exit"))
				qType = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_QUE_TYPE_EXIT ).getEntryID();
			
			String[] questions = req.getParameterValues("Questions");
			String[] answerTypes = req.getParameterValues("AnswerTypes");
			
			ArrayList liteQuestions = energyCompany.getAllInterviewQuestions();

			synchronized (liteQuestions) {
				Iterator it = liteQuestions.iterator();
				while (it.hasNext()) {
					LiteInterviewQuestion liteQuestion = (LiteInterviewQuestion) it.next();
					if (liteQuestion.getQuestionType() == qType) {
						com.cannontech.database.data.stars.InterviewQuestion question =
								new com.cannontech.database.data.stars.InterviewQuestion();
						question.setQuestionID( new Integer(liteQuestion.getQuestionID()) );
						Transaction.createTransaction(Transaction.DELETE, question).execute();
						it.remove();
					}
				}
			}
			
			if (type.equalsIgnoreCase("Exit"))
				starsExitQuestions.removeAllStarsExitInterviewQuestion();
			
			if (questions != null) {
				for (int i = 0; i < questions.length; i++) {
					com.cannontech.database.data.stars.InterviewQuestion question =
							new com.cannontech.database.data.stars.InterviewQuestion();
					com.cannontech.database.db.stars.InterviewQuestion questionDB = question.getInterviewQuestion();
					questionDB.setQuestionType( new Integer(qType) );
					questionDB.setQuestion( questions[i] );
					questionDB.setMandatory( "N" );
					questionDB.setDisplayOrder( new Integer(i+1) );
					questionDB.setAnswerType( Integer.valueOf(answerTypes[i]) );
					questionDB.setExpectedAnswer( new Integer(CtiUtilities.NONE_ID) );
					question.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
					question = (com.cannontech.database.data.stars.InterviewQuestion)
							Transaction.createTransaction(Transaction.INSERT, question).execute();
					
					LiteInterviewQuestion liteQuestion = (LiteInterviewQuestion) StarsLiteFactory.createLite( question.getInterviewQuestion() );
					synchronized (liteQuestions) { liteQuestions.add(liteQuestion); }
					
					if (type.equalsIgnoreCase("Exit")) {
						StarsExitInterviewQuestion starsQuestion = new StarsExitInterviewQuestion();
						starsQuestion.setQuestionID( liteQuestion.getQuestionID() );
						starsQuestion.setQuestion( liteQuestion.getQuestion() );
						starsQuestion.setQuestionType( (QuestionType)
								StarsFactory.newStarsCustListEntry(
									energyCompany.getYukonListEntry(YukonSelectionListDefs.YUK_LIST_NAME_QUESTION_TYPE, liteQuestion.getQuestionType()),
									QuestionType.class
									)
								);
						starsQuestion.setAnswerType( (AnswerType)
								StarsFactory.newStarsCustListEntry(
									energyCompany.getYukonListEntry(YukonSelectionListDefs.YUK_LIST_NAME_ANSWER_TYPE, liteQuestion.getAnswerType()),
									AnswerType.class
									)
								);
						starsExitQuestions.addStarsExitInterviewQuestion( starsQuestion );
					}
				}
			}

        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Interview questions updated successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update interview questions");
			redirect = referer;
        }
	}
	
	/**
	 * Update entries of a customer selection list. The entryData parameter
	 * is an array of {entryID(Integer), entryText(String), yukDefID(Integer)}
	 */
	public static void updateYukonListEntries(YukonSelectionList cList, Object[][] entryData, LiteStarsEnergyCompany energyCompany, java.sql.Connection conn)
		throws java.sql.SQLException, WebClientException
	{
		boolean autoCommit = true;
		
		try {
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit( false );
			
			if (cList.getListName().equalsIgnoreCase(com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION)) {
				// Handle substation list
				// Create a copy of the old entry list, so we won't lose it if something goes wrong
				ArrayList oldEntries = new ArrayList();
				oldEntries.addAll( cList.getYukonListEntries() );
				
				ArrayList newEntries = new ArrayList();
				
				if (entryData != null) {
					for (int i = 0; i < entryData.length; i++) {
						int entryID = ((Integer)entryData[i][0]).intValue();
						
						if (entryID == 0) {
							// This is a new entry, add it to the new entry list
							com.cannontech.database.data.stars.Substation substation =
									new com.cannontech.database.data.stars.Substation();
							substation.getSubstation().setSubstationName( (String)entryData[i][1] );
							substation.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
							substation.setDbConnection( conn );
							substation.add();
							
							YukonListEntry cEntry = new YukonListEntry();
							cEntry.setEntryID( substation.getSubstation().getSubstationID().intValue() );
							cEntry.setEntryText( substation.getSubstation().getSubstationName() );
							newEntries.add( cEntry );
						}
						else {
							// This is an existing entry, update it
							com.cannontech.database.db.stars.Substation subDB =
									new com.cannontech.database.db.stars.Substation();
							subDB.setSubstationID( new Integer(entryID) );
							subDB.setSubstationName( (String)entryData[i][1] );
							subDB.setDbConnection( conn );
							subDB.update();
							
							for (int j = 0; j < oldEntries.size(); j++) {
								YukonListEntry cEntry = (YukonListEntry) oldEntries.get(j);
								if (cEntry.getEntryID() == entryID) {
									cEntry.setEntryText( (String)entryData[i][1] );
									newEntries.add( oldEntries.remove(j) );
									break;
								}
							}
						}
					}
				}
				
				// Delete all the remaining entries
				for (int i = 0; i < oldEntries.size(); i++) {
					int entryID = ((YukonListEntry) oldEntries.get(i)).getEntryID();
					
					try {
						com.cannontech.database.data.stars.Substation substation =
								new com.cannontech.database.data.stars.Substation();
						substation.setSubstationID( new Integer(entryID) );
						substation.setDbConnection( conn );
						substation.delete();
					}
					catch (java.sql.SQLException e) {
						e.printStackTrace();
						throw new WebClientException("Cannot delete substation with id = " + entryID + ", make sure it is not referenced");
					}
				}
				
				conn.commit();
				cList.setYukonListEntries( newEntries );
			}
			else {
				// Create a copy of the old entry list, so we won't lose it if something goes wrong
				ArrayList oldEntries = new ArrayList();
				oldEntries.addAll( cList.getYukonListEntries() );
				
				ArrayList newEntries = new ArrayList();
				
				if (entryData != null) {
					for (int i = 0; i < entryData.length; i++) {
						int entryID = ((Integer)entryData[i][0]).intValue();
						
						if (entryID == 0) {
							// This is a new entry, add it to the new entry list
							com.cannontech.database.db.constants.YukonListEntry entry =
									new com.cannontech.database.db.constants.YukonListEntry();
							entry.setListID( new Integer(cList.getListID()) );
							entry.setEntryOrder( new Integer(i+1) );
							entry.setEntryText( (String)entryData[i][1] );
							entry.setYukonDefID( (Integer)entryData[i][2] );
							entry.setDbConnection( conn );
							entry.add();
							
							com.cannontech.common.constants.YukonListEntry cEntry =
									new com.cannontech.common.constants.YukonListEntry();
							StarsLiteFactory.setConstantYukonListEntry( cEntry, entry );
							newEntries.add( cEntry );
						}
						else {
							// This is an existing entry, update it
							for (int j = 0; j < oldEntries.size(); j++) {
								YukonListEntry cEntry = (YukonListEntry) oldEntries.get(j);
								
								if (cEntry.getEntryID() == entryID) {
									com.cannontech.database.db.constants.YukonListEntry entry = StarsLiteFactory.createYukonListEntry(cEntry);
									entry.setEntryOrder( new Integer(i+1) );
									entry.setEntryText( (String)entryData[i][1] );
									entry.setYukonDefID( (Integer)entryData[i][2] );
									entry.setDbConnection( conn );
									entry.update();
									
									StarsLiteFactory.setConstantYukonListEntry(cEntry, entry);
									newEntries.add( oldEntries.remove(j) );
									break;
								}
							}
						}
					}
				}
				
				// Delete all the remaining entries
				for (int i = 0; i < oldEntries.size(); i++) {
					int entryID = ((YukonListEntry) oldEntries.get(i)).getEntryID();
					
					try {
						com.cannontech.database.db.constants.YukonListEntry entry =
								new com.cannontech.database.db.constants.YukonListEntry();
						entry.setEntryID( new Integer(entryID) );
						entry.setDbConnection( conn );
						entry.delete();
					}
					catch (java.sql.SQLException e) {
						e.printStackTrace();
						throw new WebClientException("Cannot delete list entry with id = " + entryID + ", make sure it is not referenced");
					}
				}
				
				conn.commit();
				cList.setYukonListEntries( newEntries );
				
				// Update the constant objects (in both stars and core yukon cache)
				for (int i = 0; i < cList.getYukonListEntries().size(); i++) {
					YukonListEntry entry = (YukonListEntry) cList.getYukonListEntries().get(i);
					YukonListFuncs.getYukonListEntries().remove( new Integer(entry.getEntryID()) );
				}
				
				for (int i = 0; i < newEntries.size(); i++) {
					YukonListEntry entry = (YukonListEntry) newEntries.get(i);
					YukonListFuncs.getYukonListEntries().put( new Integer(entry.getEntryID()), entry );
				}
			}
		}
		catch (WebClientException e) {
			conn.rollback();
			throw e;
		}
		finally {
			conn.setAutoCommit( autoCommit );
		}
	}
	
	private void updateCustomerSelectionList(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		java.sql.Connection conn = null;
        
		try {
			String listName = req.getParameter("ListName");
			String ordering = req.getParameter("Ordering");
			String label = req.getParameter("Label");
			String whereIsList = req.getParameter("WhereIsList");
			
			String[] entryIDs = req.getParameterValues("EntryIDs");
			String[] entryTexts = req.getParameterValues("EntryTexts");
			String[] yukonDefIDs = req.getParameterValues("YukonDefIDs");
			
			YukonSelectionList cList = energyCompany.getYukonSelectionList( listName );
			Hashtable selectionListTable = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
			
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD_CUS)) {
				// Handle customer opt out period list
				boolean sameAsOp = Boolean.valueOf( req.getParameter("SameAsOp") ).booleanValue();
				if (sameAsOp && cList == null) return;
				
				if (sameAsOp && cList != null) {
					// Remove the OptOutPeriodCustomer list
					com.cannontech.database.data.constants.YukonSelectionList list =
							new com.cannontech.database.data.constants.YukonSelectionList();
					list.setListID( new Integer(cList.getListID()) );
					list.setDbConnection( conn );
					list.delete();
					
					for (int i = 0; i < cList.getYukonListEntries().size(); i++) {
						YukonListEntry cEntry = (YukonListEntry) cList.getYukonListEntries().get(i);
						YukonListFuncs.getYukonListEntries().remove( new Integer(cEntry.getEntryID()) );
					}
					
					energyCompany.deleteYukonSelectionList( cList.getListID() );
					YukonListFuncs.getYukonSelectionLists().remove( new Integer(cList.getListID()) );
					selectionListTable.remove( cList.getListName() );
					
					session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Customer selection list updated successfully");
					return;
				}
				
				if (!sameAsOp && cList == null) {
					// Add a new list of OptOutPeriodCustomer
					YukonSelectionList opList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD );
					cList = energyCompany.addYukonSelectionList( listName, opList, false );
				}
			}
			
			Object[][] entryData = null;
			if (entryIDs != null) {
				entryData = new Object[ entryIDs.length ][];
				for (int i = 0; i < entryIDs.length; i++) {
					entryData[i] = new Object[3];
					entryData[i][0] = Integer.valueOf( entryIDs[i] );
					entryData[i][1] = entryTexts[i];
					entryData[i][2] = Integer.valueOf( yukonDefIDs[i] );
				}
			}
			
			updateYukonListEntries( cList, entryData, energyCompany, conn );
			
			if (!listName.equalsIgnoreCase(com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION)) {
				// Update yukon selection list
				com.cannontech.database.db.constants.YukonSelectionList list =
						StarsLiteFactory.createYukonSelectionList( cList );
				list.setOrdering( ordering );
				list.setSelectionLabel( label );
				list.setWhereIsList( whereIsList );
				list.setDbConnection( conn );
				list.update();
				
				StarsLiteFactory.setConstantYukonSelectionList( cList, list );
			}
			
			StarsCustSelectionList starsList = StarsLiteFactory.createStarsCustSelectionList( cList );
			selectionListTable.put( starsList.getListName(), starsList );
			
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Customer selection list updated successfully");
		}
		catch (WebClientException e) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
			redirect = referer;
		}
		catch (java.sql.SQLException e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update customer selection list");
			redirect = referer;
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
	}
	
	private void updateThermostatSchedule(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
        try {
        	StarsDefaultThermostatSettings dftSettings = energyCompany.getStarsDefaultThermostatSettings();
        	
        	UpdateThermostatScheduleAction action = new UpdateThermostatScheduleAction();
        	SOAPMessage reqMsg = action.build( req, session );
        	StarsUpdateThermostatSchedule updateSched = SOAPUtil.parseSOAPMsgForOperation( reqMsg ).getStarsUpdateThermostatSchedule();
        	
        	LiteStarsLMHardware liteHw = energyCompany.getDefaultLMHardware();
        	StarsUpdateThermostatScheduleResponse resp = UpdateThermostatScheduleAction.updateThermostatSchedule( updateSched, liteHw, energyCompany );
			UpdateThermostatScheduleAction.parseResponse( resp, dftSettings );
			
        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Default thermostat schedule updated successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update default thermostat schedule");
			redirect = referer;
        }
	}
	
	private void createEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		try {
			String warning = null;
			LiteYukonGroup operGroup = null;
			LiteYukonGroup custGroup = null;
			
			String operGroupName = req.getParameter("OperatorGroup");
			if (operGroupName.length() > 0) {
				operGroup = AuthFuncs.getGroup( operGroupName );
				if (operGroup == null) {
					if (warning == null) warning = "Warning: ";
					warning += "group \"" + operGroupName + "\" doesn't exist";
				}
			}
			
			String custGroupName = req.getParameter("CustomerGroup");
			if (custGroupName.length() > 0) {
				custGroup = AuthFuncs.getGroup( custGroupName );
				if (custGroup == null) {
					if (warning == null) warning = "Warning: ";
					else warning += ", ";
					warning += "group '" + custGroupName + "' doesn't exist";
				}
			}
			
			com.cannontech.database.data.user.YukonUser yukonUser =
					new com.cannontech.database.data.user.YukonUser();
			yukonUser.getYukonUser().setUsername( req.getParameter("Username") );
			yukonUser.getYukonUser().setPassword( req.getParameter("Password") );
			yukonUser.getYukonUser().setStatus( com.cannontech.user.UserUtils.STATUS_ENABLED );
			
			// Assign the operator group to login
			if (operGroup != null) {
				com.cannontech.database.db.user.YukonGroup group =
						new com.cannontech.database.db.user.YukonGroup();
				group.setGroupID( new Integer(operGroup.getGroupID()) );
				yukonUser.getYukonGroups().add( group );
			}
			
			// Assign the EnergyCompany role to login
			LiteYukonRoleProperty[] roleProps = RoleFuncs.getRoleProperties( EnergyCompanyRole.ROLEID );
			for (int i = 0; i < roleProps.length; i++) {
				com.cannontech.database.db.user.YukonUserRole userRole =
						new com.cannontech.database.db.user.YukonUserRole();
				userRole.setRoleID( new Integer(EnergyCompanyRole.ROLEID) );
				userRole.setRolePropertyID( new Integer(roleProps[i].getRolePropertyID()) );
				if (roleProps[i].getRolePropertyID() == EnergyCompanyRole.CUSTOMER_GROUP_NAME && custGroup != null)
					userRole.setValue( custGroupName );
				else if (roleProps[i].getRolePropertyID() == EnergyCompanyRole.OPERATOR_GROUP_NAME && operGroup != null)
					userRole.setValue( operGroupName );
				else
					userRole.setValue( CtiUtilities.STRING_NONE );
				yukonUser.getYukonUserRoles().add( userRole );
			}
			
			// Assign the Administrator role to login, and allow configuring energy company
			roleProps = RoleFuncs.getRoleProperties( AdministratorRole.ROLEID );
			for (int i = 0; i < roleProps.length; i++) {
				com.cannontech.database.db.user.YukonUserRole userRole =
						new com.cannontech.database.db.user.YukonUserRole();
				userRole.setRoleID( new Integer(AdministratorRole.ROLEID) );
				userRole.setRolePropertyID( new Integer(roleProps[i].getRolePropertyID()) );
				userRole.setValue( CtiUtilities.TRUE_STRING );
				if (roleProps[i].getRolePropertyID() == AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY)
					yukonUser.getYukonUserRoles().add( userRole );
				else
					userRole.setValue( CtiUtilities.STRING_NONE );
			}
			
			yukonUser = (com.cannontech.database.data.user.YukonUser)
					Transaction.createTransaction(Transaction.INSERT, yukonUser).execute();
			com.cannontech.database.db.user.YukonUser userDB = yukonUser.getYukonUser();
			
			com.cannontech.database.db.company.EnergyCompany company =
					new com.cannontech.database.db.company.EnergyCompany();
			company.setName( req.getParameter("CompanyName") );
			company.setPrimaryContactID( new Integer(CtiUtilities.NONE_ID) );
			company.setUserID( userDB.getUserID() );
			company = (com.cannontech.database.db.company.EnergyCompany)
					Transaction.createTransaction(Transaction.INSERT, company).execute();
			
			LiteStarsEnergyCompany energyCompany = new LiteStarsEnergyCompany( company );
			SOAPServer.addEnergyCompany( energyCompany );
			ServerUtils.handleDBChange( energyCompany, DBChangeMsg.CHANGE_TYPE_ADD );
			
			String sql = "INSERT INTO EnergyCompanyOperatorLoginList VALUES(" +
					company.getEnergyCompanyID() + ", " + userDB.getUserID() + ")";
			com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
					sql, CtiUtilities.getDatabaseAlias() );
			stmt.execute();
			
			LiteYukonUser liteUser = new LiteYukonUser(
					userDB.getUserID().intValue(),
					userDB.getUsername(),
					userDB.getPassword(),
					userDB.getStatus()
					);
			ServerUtils.handleDBChange( liteUser, DBChangeMsg.CHANGE_TYPE_ADD );
			
			// Create login for the second operator
			if (req.getParameter("Username2").length() > 0) {
				yukonUser = new com.cannontech.database.data.user.YukonUser();
				yukonUser.getYukonUser().setUsername( req.getParameter("Username2") );
				yukonUser.getYukonUser().setPassword( req.getParameter("Password2") );
				yukonUser.getYukonUser().setStatus( com.cannontech.user.UserUtils.STATUS_ENABLED );
				
				if (operGroup != null) {
					com.cannontech.database.db.user.YukonGroup group =
							new com.cannontech.database.db.user.YukonGroup();
					group.setGroupID( new Integer(operGroup.getGroupID()) );
					yukonUser.getYukonGroups().add( group );
				}
				
				yukonUser = (com.cannontech.database.data.user.YukonUser)
						Transaction.createTransaction(Transaction.INSERT, yukonUser).execute();
				userDB = yukonUser.getYukonUser();
				
				sql = "INSERT INTO EnergyCompanyOperatorLoginList VALUES(" +
						company.getEnergyCompanyID() + ", " + userDB.getUserID() + ")";
				stmt.setSQLString( sql );
				stmt.execute();
				
				liteUser = new LiteYukonUser(
						userDB.getUserID().intValue(),
						userDB.getUsername(),
						userDB.getPassword(),
						userDB.getStatus()
						);
				ServerUtils.handleDBChange( liteUser, DBChangeMsg.CHANGE_TYPE_ADD );
			}
			
        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Energy company created successfully");
        	if (warning != null) session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, warning);
		}
		catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to create the energy company");
		}
	}
	
	private void deleteEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        java.sql.Connection conn = null;
		
		try {
			// Delete all customer accounts
			int[] accountIDs = CustomerAccount.searchByAccountNumber(
					energyCompany.getEnergyCompanyID(), "%" );
			if (accountIDs != null) {
				for (int i = 0; i < accountIDs.length; i++) {
					LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[i], true );
					DeleteCustAccountAction.deleteCustomerAccount( liteAcctInfo, energyCompany );
				}
			}
			
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			// Delete all inventories
			String sql = "SELECT InventoryID FROM ECToInventoryMapping WHERE EnergyCompanyID = ?";
			java.sql.PreparedStatement stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, user.getEnergyCompanyID() );
			java.sql.ResultSet rset = stmt.executeQuery();
			
			java.sql.Connection conn2 = null;
			try {
				conn2 = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
				
				while (rset.next()) {
					int inventoryID = rset.getInt(1);
					com.cannontech.database.data.stars.hardware.InventoryBase inventory =
							new com.cannontech.database.data.stars.hardware.InventoryBase();
					inventory.setInventoryID( new Integer(inventoryID) );
					inventory.setDbConnection( conn2 );
					inventory.delete();
				}
			}
			finally {
				if (conn2 != null) conn2.close();
			}
			
			rset.close();
			stmt.close();
			
			// Delete all substations
			ECToGenericMapping[] substations = ECToGenericMapping.getAllMappingItems(
					energyCompany.getEnergyCompanyID(), com.cannontech.database.db.stars.Substation.TABLE_NAME );
			if (substations != null) {
				for (int i = 0; i < substations.length; i++) {
					com.cannontech.database.data.stars.Substation substation =
							new com.cannontech.database.data.stars.Substation();
					substation.setSubstationID( substations[i].getItemID() );
					substation.setDbConnection( conn );
					substation.delete();
				}
			}
			
			// Delete all service companies
			for (int i = 0; i < energyCompany.getAllServiceCompanies().size(); i++) {
				LiteServiceCompany liteCompany = (LiteServiceCompany) energyCompany.getAllServiceCompanies().get(i);
				com.cannontech.database.data.stars.report.ServiceCompany company =
						new com.cannontech.database.data.stars.report.ServiceCompany();
				StarsLiteFactory.setServiceCompany( company.getServiceCompany(), liteCompany );
				company.setDbConnection( conn );
				company.delete();
			}
			
			// Delete all appliance categories
			for (int i = 0; i < energyCompany.getAllApplianceCategories().size(); i++) {
				LiteApplianceCategory liteAppCat = (LiteApplianceCategory) energyCompany.getAllApplianceCategories().get(i);
				
				com.cannontech.database.db.stars.LMProgramWebPublishing.deleteAllLMProgramWebPublishing(
						new Integer(liteAppCat.getApplianceCategoryID()), conn );
				for (int j = 0; j < liteAppCat.getPublishedPrograms().length; j++) {
					int configID = liteAppCat.getPublishedPrograms()[j].getWebSettingsID();
					com.cannontech.database.db.web.YukonWebConfiguration cfg =
							new com.cannontech.database.db.web.YukonWebConfiguration();
					cfg.setConfigurationID( new Integer(configID) );
					cfg.setDbConnection( conn );
					cfg.delete();
				}
				
				com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
						new com.cannontech.database.data.stars.appliance.ApplianceCategory();
				StarsLiteFactory.setApplianceCategory( appCat.getApplianceCategory(), liteAppCat );
				appCat.setDbConnection( conn );
				appCat.delete();
			}
			
			// Delete all interview questions
			for (int i = 0; i < energyCompany.getAllInterviewQuestions().size(); i++) {
				LiteInterviewQuestion liteQuestion = (LiteInterviewQuestion) energyCompany.getAllInterviewQuestions().get(i);
				com.cannontech.database.data.stars.InterviewQuestion question =
						new com.cannontech.database.data.stars.InterviewQuestion();
				question.setQuestionID( new Integer(liteQuestion.getQuestionID()) );
				question.setDbConnection( conn );
				question.delete();
			}
			
			// Delete all customer FAQs
			for (int i = 0; i < energyCompany.getAllCustomerFAQs().size(); i++) {
				LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) energyCompany.getAllCustomerFAQs().get(i);
				com.cannontech.database.db.stars.CustomerFAQ faq =
						new com.cannontech.database.db.stars.CustomerFAQ();
				faq.setQuestionID( new Integer(liteFAQ.getQuestionID()) );
				faq.setDbConnection( conn );
				faq.delete();
			}
			
			// Delete customer selection lists
			for (int i = 0; i < energyCompany.getAllSelectionLists().size(); i++) {
				YukonSelectionList cList = (YukonSelectionList) energyCompany.getAllSelectionLists().get(i);
				if (cList.getListID() == LiteStarsEnergyCompany.FAKE_LIST_ID) continue;
				
				Integer listID = new Integer( cList.getListID() );
				com.cannontech.database.data.constants.YukonSelectionList list =
						new com.cannontech.database.data.constants.YukonSelectionList();
				list.setListID( listID );
				list.setDbConnection( conn );
				list.delete();
				
				YukonListFuncs.getYukonSelectionLists().remove( listID );
				for (int j = 0; j < cList.getYukonListEntries().size(); j++) {
					YukonListEntry cEntry = (YukonListEntry) cList.getYukonListEntries().get(j);
					YukonListFuncs.getYukonListEntries().remove( new Integer(cEntry.getEntryID()) );
				}
			}
			
			// Delete operator logins (except the default login)
			sql = "SELECT OperatorLoginID FROM EnergyCompanyOperatorLoginList WHERE EnergyCompanyID = ?";
			stmt = conn.prepareStatement( sql );
			stmt.setInt(1, energyCompany.getLiteID());
			rset = stmt.executeQuery();
			
			try {
				conn2 = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
				
				while (rset.next()) {
					int userID = rset.getInt(1);
					if (userID == energyCompany.getUserID()) continue;
					
					com.cannontech.database.data.user.YukonUser yukonUser =
							new com.cannontech.database.data.user.YukonUser();
					yukonUser.setUserID( new Integer(userID) );
					yukonUser.setDbConnection( conn2 );
					yukonUser.deleteOperatorLogin();
					
					ServerUtils.handleDBChange( YukonUserFuncs.getLiteYukonUser(userID), DBChangeMsg.CHANGE_TYPE_DELETE );
				}
			}
			finally {
				if (conn2 != null) conn2.close();
			}
			
			rset.close();
			stmt.close();
			
			// Delete the energy company!
			com.cannontech.database.data.company.EnergyCompanyBase ec =
					new com.cannontech.database.data.company.EnergyCompanyBase();
			ec.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
			ec.getEnergyCompany().setPrimaryContactID( new Integer(energyCompany.getPrimaryContactID()) );
			ec.setDbConnection( conn );
			ec.delete();
			
			SOAPServer.deleteEnergyCompany( energyCompany.getLiteID() );
			ServerUtils.handleDBChange( energyCompany, DBChangeMsg.CHANGE_TYPE_DELETE );
			if (energyCompany.getPrimaryContactID() != CtiUtilities.NONE_ID) {
				LiteContact liteContact = ContactFuncs.getContact( energyCompany.getPrimaryContactID() );
				ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_DELETE );
			}
			
			if (energyCompany.getUserID() != com.cannontech.user.UserUtils.USER_YUKON_ID) {
				com.cannontech.database.data.user.YukonUser yukonUser =
						new com.cannontech.database.data.user.YukonUser();
				yukonUser.setUserID( new Integer(energyCompany.getUserID()) );
				yukonUser.setDbConnection( conn );
				yukonUser.deleteOperatorLogin();
				
				ServerUtils.handleDBChange( YukonUserFuncs.getLiteYukonUser(energyCompany.getUserID()), DBChangeMsg.CHANGE_TYPE_DELETE );
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete the energy company");
			redirect = referer;
		}
        finally {
        	try {
	        	if (conn != null) conn.close();
        	}
        	catch (java.sql.SQLException e) {}
        }
	}
	
}
