package com.cannontech.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteAddress;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteCustomerFAQ;
import com.cannontech.database.data.lite.stars.LiteInterviewQuestion;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteLMProgramEvent;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSchedule;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.LiteSubstation;
import com.cannontech.database.data.lite.stars.LiteWebConfiguration;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.database.db.stars.ECToGenericMapping;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.application.WebClientRole;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsMsgUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.DeleteCustAccountsTask;
import com.cannontech.stars.util.task.DeleteEnergyCompanyTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.UpdateThermostatScheduleAction;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.AnswerType;
import com.cannontech.stars.xml.serialize.CompanyAddress;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.QuestionType;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustomerAddress;
import com.cannontech.stars.xml.serialize.StarsDefaultThermostatSchedules;
import com.cannontech.stars.xml.serialize.StarsEnergyCompany;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestion;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestions;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsLMProgram;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.stars.xml.serialize.StarsThermostatProgram;
import com.cannontech.stars.xml.serialize.StarsUpdateThermostatSchedule;
import com.cannontech.stars.xml.serialize.StarsUpdateThermostatScheduleResponse;
import com.cannontech.stars.xml.serialize.types.StarsThermostatTypes;
import com.cannontech.user.UserUtils;
import com.cannontech.web.navigation.CtiNavObject;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsAdmin extends HttpServlet {
    
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
        
		//we can never be too careful when checking for nulls in web apps
        CtiNavObject nav = (CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE);
        if(nav != null)
        	referer = nav.getPreviousPage();
        else
        	referer = req.getHeader("referer");
        if(referer == null) referer = req.getContextPath() + SOAPClient.LOGIN_URL;	
		
		redirect = req.getParameter( ServletUtils.ATT_REDIRECT );
		if (redirect == null) redirect = referer;
    	
		String action = req.getParameter( "action" );
		if (action == null) action = "";
		
		if (action.equalsIgnoreCase("RefreshCache")) {
			if (req.getParameter("Range").equalsIgnoreCase("All")) {
				StarsDatabaseCache.getInstance().refreshCache();
			}
			else if (req.getParameter("Range").equalsIgnoreCase("EnergyCompany")) {
				LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
				StarsDatabaseCache.getInstance().refreshCache( energyCompany );
			}
		 	
			if (session != null) session.invalidate();
			resp.sendRedirect( req.getContextPath() + SOAPClient.LOGIN_URL );
			return;
		}
        
		if (action.equalsIgnoreCase("SwitchContext")) {
			try {
				int memberID = Integer.parseInt( req.getParameter("MemberID") );
				switchContext( user, req, session, memberID );
				session = req.getSession( false );
			}
			catch (WebClientException e) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
				resp.sendRedirect( referer );
				return;
			}
		}
		
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
		else if (action.equalsIgnoreCase("UpdateSubstation"))
			updateSubstation( user, req, session );
		else if (action.equalsIgnoreCase("DeleteSubstation"))
			deleteSubstation( user, req, session );
		else if (action.equalsIgnoreCase("UpdateFAQSource"))
			updateFAQSource( user, req, session );
		else if (action.equalsIgnoreCase("UpdateFAQSubjectList"))
			updateFAQSubjectList( user, req, session );
		else if (action.equalsIgnoreCase("UpdateFAQSubject"))
			updateFAQSubject( user, req, session );
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
		else if (action.equalsIgnoreCase("UpdateOperatorLogin"))
			updateOperatorLogin( user, req, session );
		else if (action.equalsIgnoreCase("DeleteOperatorLogin"))
			deleteOperatorLogin( user, req, session );
		else if (action.equalsIgnoreCase("UpdateRoutes"))
			updateRouteList( user, req, session );
		else if (action.equalsIgnoreCase("MemberLogin"))
			memberLogin( user, req, session );
		else if (action.equalsIgnoreCase("AddMemberEnergyCompany"))
			addMemberEnergyCompany( user, req, session );
		else if (action.equalsIgnoreCase("UpdateMemberEnergyCompany"))
			updateMemberEnergyCompany( user, req, session );
		else if (action.equalsIgnoreCase("RemoveMemberEnergyCompany"))
			removeMemberEnergyCompany( user, req, session );
        
		resp.sendRedirect( redirect );
	}
	
	private void deleteCustomerAccounts(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			ServletUtils.removeTransientAttributes( session );
			
			String fromStr = req.getParameter( "From" );
			String toStr = req.getParameter( "To" );
			Integer fromAcctNo = null;
			Integer toAcctNo = null;
			
			try {
				if (fromStr.equals("") || fromStr.equals("*"))
					fromAcctNo = new Integer(Integer.MIN_VALUE);
				else
					fromAcctNo = Integer.valueOf( fromStr );
				if (toStr.equals("") || toStr.equals("*"))
					toAcctNo = new Integer(Integer.MAX_VALUE);
				else
					toAcctNo = Integer.valueOf( toStr );
			}
			catch (NumberFormatException e) {}
			
			Object[][] accounts = CustomerAccount.getAllCustomerAccounts( energyCompany.getEnergyCompanyID() );
			ArrayList acctIDList = new ArrayList();
			
			for (int i = 0; i < accounts.length; i++) {
				Integer accountID = (Integer) accounts[i][0];
				String accountNo = (String) accounts[i][1];
				
				if (fromAcctNo != null && toAcctNo != null) {
					// Account # is numeric
					try {
						int acctNo = Integer.parseInt( accountNo );
						if (acctNo >= fromAcctNo.intValue() && acctNo <= toAcctNo.intValue())
							acctIDList.add( accountID );
					}
					catch (NumberFormatException e) {}
				}
				else {
					// Account # is alphabetic
					if (accountNo.compareToIgnoreCase( fromStr ) >= 0 && accountNo.compareToIgnoreCase( toStr ) <= 0)
						acctIDList.add( accountID );
				}
			}
			
			if (acctIDList.size() > 0) {
				int[] accountIDs = new int[ acctIDList.size() ];
				for (int i = 0; i < acctIDList.size(); i++)
					accountIDs[i] = ((Integer)acctIDList.get(i)).intValue();
				
				TimeConsumingTask task = new DeleteCustAccountsTask(user, accountIDs);
				long id = ProgressChecker.addTask( task );
				
				// Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
				for (int i = 0; i < 5; i++) {
					try {
						Thread.sleep(1000);
					}
					catch (InterruptedException e) {}
					
					task = ProgressChecker.getTask(id);
					
					if (task.getStatus() == DeleteCustAccountsTask.STATUS_FINISHED) {
						session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
						ProgressChecker.removeTask( id );
						return;
					}
					
					if (task.getStatus() == DeleteCustAccountsTask.STATUS_ERROR) {
						session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
						ProgressChecker.removeTask( id );
						return;
					}
				}
				
				session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
				session.setAttribute(ServletUtils.ATT_REFERRER, redirect);
				redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Delete customer accounts failed");
		}
	}
	
	private void updateAddress(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			int addressID = Integer.parseInt( req.getParameter("AddressID") );
			boolean newAddress = (addressID <= 0);
			String referer = req.getParameter( "REFERER" );
			StarsCustomerAddress starsAddr = null;
			
			if (referer.equalsIgnoreCase("EnergyCompany.jsp")) {
				StarsEnergyCompany ecTemp = (StarsEnergyCompany) session.getAttribute( StarsAdminUtil.ENERGY_COMPANY_TEMP );
				starsAddr = ecTemp.getCompanyAddress();
			}
			else if (referer.startsWith("ServiceCompany.jsp")) {
				StarsServiceCompany scTemp = (StarsServiceCompany) session.getAttribute( StarsAdminUtil.SERVICE_COMPANY_TEMP );
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
				
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Address information created, you must submit this page to save it");
			}
			
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update address information");
			redirect = referer;
		}
	}
	
	private void updateEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			StarsEnergyCompanySettings ecSettings =
					(StarsEnergyCompanySettings) session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsEnergyCompany ec = ecSettings.getStarsEnergyCompany();
        	
			// Create the data object from the request parameters
			com.cannontech.database.data.customer.Contact contact =
					new com.cannontech.database.data.customer.Contact();
			
			boolean newContact = (energyCompany.getPrimaryContactID() == CtiUtilities.NONE_ZERO_ID);
			LiteContact liteContact = null;
			
			if (newContact) {
				contact.getContact().setContLastName( CtiUtilities.STRING_NONE );
				contact.getContact().setContFirstName( CtiUtilities.STRING_NONE );
			}
			else {
				liteContact = ContactFuncs.getContact( energyCompany.getPrimaryContactID() );
				StarsLiteFactory.setContact( contact, liteContact, energyCompany );
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
			
			if (req.getParameter("Email").length() > 0) {
				if (notifEmail != null) {
					notifEmail.setNotification( req.getParameter("Email") );
					notifEmail.setOpCode( Transaction.UPDATE );
				}
				else {
					notifEmail = new com.cannontech.database.db.contact.ContactNotification();
					notifEmail.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL) );
					notifEmail.setDisableFlag( "N" );
					notifEmail.setNotification( req.getParameter("Email") );
					notifEmail.setOpCode( Transaction.INSERT );
					
					contact.getContactNotifVect().add( notifEmail );
				}
			}
			
			if (newContact) {
				com.cannontech.database.db.customer.Address addr =
						new com.cannontech.database.db.customer.Address();
				StarsEnergyCompany ecTemp = (StarsEnergyCompany) session.getAttribute( StarsAdminUtil.ENERGY_COMPANY_TEMP );
				if (ecTemp != null)
					StarsFactory.setCustomerAddress( addr, ecTemp.getCompanyAddress() );
				else
					addr.setStateCode( "" );
				contact.setAddress( addr );
				
				contact = (com.cannontech.database.data.customer.Contact)
						Transaction.createTransaction( Transaction.INSERT, contact ).execute();
				liteContact = new LiteContact( contact.getContact().getContactID().intValue() );
				StarsLiteFactory.setLiteContact( liteContact, contact );
				
				ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_ADD );
			}
			else {
				contact = (com.cannontech.database.data.customer.Contact)
						Transaction.createTransaction( Transaction.UPDATE, contact ).execute();
				StarsLiteFactory.setLiteContact( liteContact, contact );
				
				ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_UPDATE );
			}
			
			String compName = req.getParameter("CompanyName");
			if (newContact || !energyCompany.getName().equals( compName )) {
				energyCompany.setName( compName );
				energyCompany.setPrimaryContactID( contact.getContact().getContactID().intValue() );
				Transaction.createTransaction( Transaction.UPDATE, StarsLiteFactory.createDBPersistent(energyCompany) ).execute();
			}
        	
			int routeID = Integer.parseInt(req.getParameter("Route"));
			StarsAdminUtil.updateDefaultRoute( energyCompany, routeID );
			
			// Update energy company role DEFAULT_TIME_ZONE if necessary
			LiteYukonGroup adminGroup = energyCompany.getOperatorAdminGroup();
			boolean adminGroupUpdated = false;
	        
	        adminGroupUpdated |= StarsAdminUtil.updateGroupRoleProperty(
	        		adminGroup, EnergyCompanyRole.ROLEID, EnergyCompanyRole.DEFAULT_TIME_ZONE, req.getParameter("TimeZone") );
			
			String[] operGroupNames = req.getParameter("OperatorGroup").split(",");
			String operGroupIDs = "";
			for (int i = 0; i < operGroupNames.length; i++) {
				String groupName = operGroupNames[i].trim();
				if (groupName.equals("")) continue;
				
				LiteYukonGroup group = AuthFuncs.getGroup( groupName );
				if (group == null)
					throw new WebClientException( "Operator group '" + groupName + "' doesn't exist");
				
				if (i == 0)
					operGroupIDs += String.valueOf( group.getGroupID() );
				else
					operGroupIDs += "," + String.valueOf( group.getGroupID() );
			}
			
			if (operGroupIDs.equals(""))
				throw new WebClientException( "You must select at least one operator group" );
			
			adminGroupUpdated |= StarsAdminUtil.updateGroupRoleProperty(
					adminGroup, EnergyCompanyRole.ROLEID, EnergyCompanyRole.OPERATOR_GROUP_IDS, operGroupIDs );
			
			String[] custGroupNames = req.getParameter("CustomerGroup").split(",");
			String custGroupIDs = "";
			for (int i = 0; i < custGroupNames.length; i++) {
				String groupName = custGroupNames[i].trim();
				if (groupName.equals("")) continue;
				
				LiteYukonGroup group = AuthFuncs.getGroup( groupName );
				if (group == null)
					throw new WebClientException( "Customer group '" + groupName + "' doesn't exist");
				
				if (i == 0)
					custGroupIDs += String.valueOf( group.getGroupID() );
				else
					custGroupIDs += "," + String.valueOf( group.getGroupID() );
			}
			
			adminGroupUpdated |= StarsAdminUtil.updateGroupRoleProperty(
					adminGroup, EnergyCompanyRole.ROLEID, EnergyCompanyRole.CUSTOMER_GROUP_IDS, custGroupIDs );
			
			adminGroupUpdated |= StarsAdminUtil.updateGroupRoleProperty(
					adminGroup, EnergyCompanyRole.ROLEID, EnergyCompanyRole.ADMIN_EMAIL_ADDRESS, req.getParameter("AdminEmail") );
			
			adminGroupUpdated |= StarsAdminUtil.updateGroupRoleProperty(
					adminGroup, EnergyCompanyRole.ROLEID, EnergyCompanyRole.OPTOUT_NOTIFICATION_RECIPIENTS, req.getParameter("OptOutNotif") );
			
			if (adminGroupUpdated)
				ServerUtils.handleDBChange( adminGroup, DBChangeMsg.CHANGE_TYPE_UPDATE );
			
			StarsLiteFactory.setStarsEnergyCompany( ec, energyCompany );
        	
			session.removeAttribute( StarsAdminUtil.ENERGY_COMPANY_TEMP );
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Energy company information updated successfully");
		}
		catch (Exception e) {
			if (e instanceof WebClientException) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
			}
			else {
				CTILogger.error( e.getMessage(), e );
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update energy company information");
			}
			redirect = referer;
		}
	}
	
	private void updateApplianceCategory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
		
		try {
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
				
				appCat = (com.cannontech.database.data.stars.appliance.ApplianceCategory)
						Transaction.createTransaction( Transaction.INSERT, appCat ).execute();
				
				liteAppCat = (LiteApplianceCategory) StarsLiteFactory.createLite( appCat.getApplianceCategory() );
				energyCompany.addApplianceCategory( liteAppCat );
				LiteWebConfiguration liteConfig = (LiteWebConfiguration) StarsLiteFactory.createLite( appCat.getWebConfiguration() );
				StarsDatabaseCache.getInstance().addWebConfiguration( liteConfig );
			}
			else {
				liteAppCat = energyCompany.getApplianceCategory( appCatID );
				if (!energyCompany.getApplianceCategories().contains(liteAppCat)) {
					session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Cannot update an inherited appliance category" );
					return;
				}
				
				appCat.setApplianceCategoryID( new Integer(appCatID) );
				appCatDB.setWebConfigurationID( new Integer(liteAppCat.getWebConfigurationID()) );
				
				appCat = (com.cannontech.database.data.stars.appliance.ApplianceCategory)
						Transaction.createTransaction( Transaction.UPDATE, appCat ).execute();
				
				StarsLiteFactory.setLiteApplianceCategory( liteAppCat, appCat.getApplianceCategory() );
				LiteWebConfiguration liteConfig = StarsDatabaseCache.getInstance().getWebConfiguration( appCat.getWebConfiguration().getConfigurationID().intValue() );
				StarsLiteFactory.setLiteWebConfiguration( liteConfig, appCat.getWebConfiguration() );
			}
			
			ArrayList pubProgList = new ArrayList( liteAppCat.getPublishedPrograms() );
			
			String[] progIDs = req.getParameterValues( "ProgIDs" );
			String[] deviceIDs = req.getParameterValues( "DeviceIDs" );
			String[] progDispNames = req.getParameterValues( "ProgDispNames" );
			String[] progShortNames = req.getParameterValues( "ProgShortNames" );
			String[] progDescriptions = req.getParameterValues( "ProgDescriptions" );
			String[] progDescFiles = req.getParameterValues( "ProgDescFiles" );
			String[] progCtrlOdds = req.getParameterValues( "ProgChanceOfCtrls" );
			String[] progIconNames = req.getParameterValues( "ProgIconNames" );
			
			if (progIDs != null) {
				for (int i = 0; i < progIDs.length; i++) {
					int progID = Integer.parseInt( progIDs[i] );
					int deviceID = Integer.parseInt( deviceIDs[i] );
					LiteLMProgramWebPublishing liteProg = null;
					
					for (int j = 0; j < pubProgList.size(); j++) {
						LiteLMProgramWebPublishing lProg = (LiteLMProgramWebPublishing) pubProgList.get(j);
						if (lProg.getProgramID() == progID || deviceID > 0 && lProg.getDeviceID() == deviceID) {
							pubProgList.remove(j);
							liteProg = lProg;
							break;
						}
					}
					
					String newDispName = progDispNames[i];
					if (newDispName.length() == 0 && deviceID > 0)
						newDispName = PAOFuncs.getYukonPAOName( deviceID );
					if (newDispName.length() == 0) {
						session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "The display name of a virtual program cannot be empty" );
						redirect = referer;
						return;
					}
					
					com.cannontech.database.data.stars.LMProgramWebPublishing pubProg =
							new com.cannontech.database.data.stars.LMProgramWebPublishing();
					com.cannontech.database.db.stars.LMProgramWebPublishing pubProgDB = pubProg.getLMProgramWebPublishing();
					pubProgDB.setApplianceCategoryID( new Integer(liteAppCat.getApplianceCategoryID()) );
					pubProgDB.setDeviceID( new Integer(deviceID) );
					pubProgDB.setChanceOfControlID( Integer.valueOf(progCtrlOdds[i]) );
					pubProgDB.setProgramOrder( new Integer(i+1) );
					
					if (progDispNames[i].indexOf(",") >= 0)
						progDispNames[i] = "\"" + progDispNames[i] + "\"";
					if (progShortNames[i].indexOf(",") >= 0)
						progShortNames[i] = "\"" + progShortNames[i] + "\"";
					
					com.cannontech.database.db.web.YukonWebConfiguration cfg =
							new com.cannontech.database.db.web.YukonWebConfiguration();
					cfg.setLogoLocation( progIconNames[i] );
					cfg.setAlternateDisplayName( progDispNames[i] + "," + progShortNames[i] );
					cfg.setDescription( progDescriptions[i].replaceAll(LINE_SEPARATOR, "<br>") );
					cfg.setURL( progDescFiles[i] );
					pubProg.setWebConfiguration( cfg );
					
					if (liteProg != null) {
						pubProg.setProgramID( new Integer(liteProg.getProgramID()) );
						pubProg.getLMProgramWebPublishing().setWebSettingsID( new Integer(liteProg.getWebSettingsID()) );
						pubProg = (com.cannontech.database.data.stars.LMProgramWebPublishing)
								Transaction.createTransaction( Transaction.UPDATE, pubProg ).execute();
						
						liteProg.setChanceOfControlID( pubProg.getLMProgramWebPublishing().getChanceOfControlID().intValue() );
						liteProg.setProgramOrder( pubProg.getLMProgramWebPublishing().getProgramOrder().intValue() );
						
						String oldDispName = StarsUtils.getPublishedProgramName( liteProg );
						
						LiteWebConfiguration liteCfg = StarsDatabaseCache.getInstance().getWebConfiguration( liteProg.getWebSettingsID() );
						StarsLiteFactory.setLiteWebConfiguration( liteCfg, pubProg.getWebConfiguration() );
						
						// If program display name changed, we need to update all the accounts enrolled in this program
						if (!newDispName.equals( oldDispName )) {
							for (int j = 0; j < descendants.size(); j++) {
								LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(j);
								
								ArrayList accounts = company.getActiveAccounts();
								for (int k = 0; k < accounts.size(); k++) {
									StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation) accounts.get(k);
									for (int l = 0; l < starsAcctInfo.getStarsLMPrograms().getStarsLMProgramCount(); l++) {
										StarsLMProgram program = starsAcctInfo.getStarsLMPrograms().getStarsLMProgram(l);
										if (program.getProgramID() == liteProg.getProgramID()) {
											program.setProgramName( newDispName );
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
						liteProg = (LiteLMProgramWebPublishing) StarsLiteFactory.createLite( pubProg.getLMProgramWebPublishing() );
						energyCompany.addProgram( liteProg, liteAppCat );
						
						LiteWebConfiguration liteCfg = (LiteWebConfiguration) StarsLiteFactory.createLite( pubProg.getWebConfiguration() );
						StarsDatabaseCache.getInstance().addWebConfiguration( liteCfg );
					}
				}
			}
			
			// Delete the rest of published programs
			for (int i = 0; i < pubProgList.size(); i++) {
				LiteLMProgramWebPublishing liteProg = (LiteLMProgramWebPublishing) pubProgList.get(i);
				
				// Delete all events of this program
				com.cannontech.database.data.stars.event.LMProgramEvent.deleteAllLMProgramEvents( liteProg.getProgramID() );
				
				// Set ProgramID = 0 for all appliances assigned to this program
				com.cannontech.database.db.stars.appliance.ApplianceBase.resetAppliancesByProgram( liteProg.getProgramID() );
				
				for (int j = 0; j < descendants.size(); j++) {
					LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(j);
					
					ArrayList accounts = company.getAllCustAccountInformation();
					for (int k = 0; k < accounts.size(); k++) {
						LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) accounts.get(k);
						
						for (int l = 0; l < liteAcctInfo.getAppliances().size(); l++) {
							LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(l);
							if (liteApp.getProgramID() == liteProg.getProgramID()) {
								liteApp.setProgramID( 0 );
								liteApp.setInventoryID( 0 );
								liteApp.setAddressingGroupID( 0 );
								liteApp.setLoadNumber( 0 );
							}
						}
						
						ArrayList programs = liteAcctInfo.getPrograms();
						for (int l = 0; l < programs.size(); l++) {
							if (((LiteStarsLMProgram) programs.get(l)).getProgramID() == liteProg.getProgramID()) {
								programs.remove(l);
								break;
							}
						}
						
						Iterator it = liteAcctInfo.getProgramHistory().iterator();
						while (it.hasNext()) {
							if (((LiteLMProgramEvent) it.next()).getProgramID() == liteProg.getProgramID())
								it.remove();
						}
						
						company.updateStarsCustAccountInformation( liteAcctInfo );
					}
				}
				
				com.cannontech.database.data.stars.LMProgramWebPublishing pubProg =
						new com.cannontech.database.data.stars.LMProgramWebPublishing();
				pubProg.setProgramID( new Integer(liteProg.getProgramID()) );
				pubProg.getLMProgramWebPublishing().setWebSettingsID( new Integer(liteProg.getWebSettingsID()) );
				
				Transaction.createTransaction( Transaction.DELETE, pubProg ).execute();
				
				energyCompany.deleteProgram( liteProg.getProgramID() );
				StarsDatabaseCache.getInstance().deleteWebConfiguration( liteProg.getWebSettingsID() );
			}
			
			for (int i = 0; i < descendants.size(); i++) {
				LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
				company.updateStarsEnrollmentPrograms();
			}
			
			if (newAppCat)
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Appliance category is created successfully");
			else
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Appliance category information updated successfully");
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update appliance category information");
			redirect = referer;
		}
	}
	
	private void deleteApplianceCategory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			int appCatID = Integer.parseInt( req.getParameter("AppCatID") );
			if (appCatID == -1) {
				StarsAdminUtil.deleteAllApplianceCategories( energyCompany );
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Appliance categories have been deleted successfully");
			}
			else {
				StarsAdminUtil.deleteApplianceCategory( appCatID, energyCompany );
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Appliance category has been deleted successfully");
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete appliance category");
			redirect = referer;
		}
	}
	
	private void updateServiceCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
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
			
			ContactNotification emailNotif = null;
			for (int i = 0; i < contact.getContactNotifVect().size(); i++) {
				ContactNotification notif = (ContactNotification) contact.getContactNotifVect().get(i);
				if (notif.getNotificationCatID().intValue() == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL) {
					emailNotif = notif;
					emailNotif.setOpCode( Transaction.DELETE );
					break;
				}
			}
			
			if (req.getParameter("Email").length() > 0) {
				if (emailNotif != null) {
					emailNotif.setNotification( req.getParameter("Email") );
					emailNotif.setOpCode( Transaction.UPDATE );
				}
				else {
					emailNotif = new com.cannontech.database.db.contact.ContactNotification();
					emailNotif.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL) );
					emailNotif.setDisableFlag( "N" );
					emailNotif.setNotification( req.getParameter("Email") );
					emailNotif.setOpCode( Transaction.INSERT );
					contact.getContactNotifVect().add( emailNotif );
				}
			}
			
			if (newCompany) {
				com.cannontech.database.db.customer.Address address = new com.cannontech.database.db.customer.Address();
				StarsServiceCompany scTemp = (StarsServiceCompany) session.getAttribute( StarsAdminUtil.SERVICE_COMPANY_TEMP );
				if (scTemp != null)
					StarsFactory.setCustomerAddress( address, scTemp.getCompanyAddress() );
				else
					address.setStateCode( "" );
				
				company.setAddress( address );
				company.setPrimaryContact( contact );
				company.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				company = (com.cannontech.database.data.stars.report.ServiceCompany)
						Transaction.createTransaction( Transaction.INSERT, company ).execute();
				
				liteAddr = (LiteAddress) StarsLiteFactory.createLite( company.getAddress() );
				energyCompany.addAddress( liteAddr );
				
				liteContact = (LiteContact) StarsLiteFactory.createLite( contact );
				ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_ADD );
				
				liteCompany = (LiteServiceCompany) StarsLiteFactory.createLite( company.getServiceCompany() );
				energyCompany.addServiceCompany( liteCompany );
				
				PrimaryContact starsContact = new PrimaryContact();
				StarsLiteFactory.setStarsCustomerContact(
						starsContact, ContactFuncs.getContact(company.getServiceCompany().getPrimaryContactID().intValue()) );
				
				CompanyAddress starsAddr = new CompanyAddress();
				StarsLiteFactory.setStarsCustomerAddress(
						starsAddr, energyCompany.getAddress(company.getAddress().getAddressID().intValue()) );
			}
			else {
				contact = (com.cannontech.database.data.customer.Contact)
						Transaction.createTransaction( Transaction.UPDATE, contact ).execute();
				StarsLiteFactory.setLiteContact( liteContact, contact );
				ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_UPDATE );
				
				companyDB = (com.cannontech.database.db.stars.report.ServiceCompany)
						Transaction.createTransaction( Transaction.UPDATE, companyDB ).execute();
				StarsLiteFactory.setLiteServiceCompany( liteCompany, companyDB );
			}
			
			ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
			for (int i = 0; i < descendants.size(); i++) {
				LiteStarsEnergyCompany ec = (LiteStarsEnergyCompany) descendants.get(i);
				ec.updateStarsServiceCompanies();
			}
        	
			session.removeAttribute( StarsAdminUtil.SERVICE_COMPANY_TEMP );
			if (newCompany)
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service company created successfully");
			else
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service company information updated successfully");
		}
		catch (WebClientException we) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, we.getMessage());
			redirect = referer;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update service company information");
			redirect = referer;
		}
	}
	
	private void deleteServiceCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			int companyID = Integer.parseInt( req.getParameter("CompanyID") );
			if (companyID == -1) {
				StarsAdminUtil.deleteAllServiceCompanies( energyCompany );
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service companies have been deleted successfully");
			}
			else {
				StarsAdminUtil.deleteServiceCompany( companyID, energyCompany );
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service company has been deleted successfully");
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete the service company");
			redirect = referer;
		}
	}
	
	private void updateSubstation(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			int subID = Integer.parseInt( req.getParameter("SubID") );
			boolean newSubstation = (subID == -1);
			
			com.cannontech.database.data.stars.Substation sub =
					new com.cannontech.database.data.stars.Substation();
			com.cannontech.database.db.stars.Substation subDB = sub.getSubstation();
			
			LiteSubstation liteSub = null;
			if (!newSubstation) {
				liteSub = energyCompany.getSubstation( subID );
				StarsLiteFactory.setSubstation( subDB, liteSub );
			}
        	
			subDB.setSubstationName( req.getParameter("SubName") );
			subDB.setRouteID( Integer.valueOf(req.getParameter("Route")) );
			
			if (newSubstation) {
				sub.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				sub = (com.cannontech.database.data.stars.Substation)
						Transaction.createTransaction( Transaction.INSERT, sub ).execute();
				
				liteSub = (LiteSubstation) StarsLiteFactory.createLite( sub.getSubstation() );
				energyCompany.addSubstation( liteSub );
			}
			else {
				subDB = (com.cannontech.database.db.stars.Substation)
						Transaction.createTransaction( Transaction.UPDATE, subDB ).execute();
				StarsLiteFactory.setLiteSubstation( liteSub, subDB );
			}
			
			ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
			for (int i = 0; i < descendants.size(); i++) {
				LiteStarsEnergyCompany ec = (LiteStarsEnergyCompany) descendants.get(i);
				ec.updateStarsSubstations();
			}
        	
			if (newSubstation)
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Substation created successfully");
			else
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Substation information updated successfully");
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update substation information");
			redirect = referer;
		}
	}
	
	private void deleteSubstation(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			int subID = Integer.parseInt( req.getParameter("SubID") );
			if (subID == -1) {
				StarsAdminUtil.deleteAllSubstations( energyCompany );
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Substations have been deleted successfully");
			}
			else {
				StarsAdminUtil.deleteSubstation( subID, energyCompany );
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Substation has been deleted successfully");
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete the substation");
			redirect = referer;
		}
	}
	
	private void updateFAQSource(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		LiteYukonUser liteUser = user.getYukonUser();
        
		try {
			String source = req.getParameter("Source");
			String faqLink = req.getParameter("FAQLink");
			if (!source.equalsIgnoreCase("Link")) faqLink = CtiUtilities.STRING_NONE;
			
			LiteYukonGroup[] custGroups = energyCompany.getResidentialCustomerGroups();
			LiteYukonGroup[] operGroups = energyCompany.getWebClientOperatorGroups();
			
			for (int i = 0; i < operGroups.length; i++) {
				if (AuthFuncs.getRolePropValueGroup(operGroups[i], ConsumerInfoRole.WEB_LINK_FAQ, null) != null &&
					StarsAdminUtil.updateGroupRoleProperty(operGroups[i], ConsumerInfoRole.ROLEID, ConsumerInfoRole.WEB_LINK_FAQ, faqLink))
					ServerUtils.handleDBChange( operGroups[i], DBChangeMsg.CHANGE_TYPE_UPDATE );
			}
			
			for (int i = 0; i < custGroups.length; i++) {
				if (AuthFuncs.getRolePropValueGroup(custGroups[i], ResidentialCustomerRole.WEB_LINK_FAQ, null) != null &&
					StarsAdminUtil.updateGroupRoleProperty(custGroups[i], ResidentialCustomerRole.ROLEID, ResidentialCustomerRole.WEB_LINK_FAQ, faqLink))
					ServerUtils.handleDBChange( custGroups[i], DBChangeMsg.CHANGE_TYPE_UPDATE );
			}
			
			if (source.equalsIgnoreCase("Inherited")) {
				if (energyCompany.getCustomerFAQs() != null)
					StarsAdminUtil.deleteAllFAQSubjects( energyCompany, true );
			}
			else if (source.equalsIgnoreCase("Customized")) {
				if (energyCompany.getCustomerFAQs() == null) {
					// Make a copy of the default the customer FAQs
					YukonSelectionList dftList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP );
					YukonSelectionList list = energyCompany.addYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP, dftList, true );
					
					ArrayList dftFAQs = null;
					if (energyCompany.getParent() != null)
						dftFAQs = energyCompany.getParent().getAllCustomerFAQs();
					else
						dftFAQs = StarsDatabaseCache.getInstance().getDefaultEnergyCompany().getAllCustomerFAQs();
					
					for (int i = 0; i < dftFAQs.size(); i++) {
						LiteCustomerFAQ dftFAQ = (LiteCustomerFAQ) dftFAQs.get(i);
						
						com.cannontech.database.db.stars.CustomerFAQ faq = new com.cannontech.database.db.stars.CustomerFAQ();
						faq.setQuestion( dftFAQ.getQuestion() );
						faq.setAnswer( dftFAQ.getAnswer() );
						
						for (int j = 0; j < dftList.getYukonListEntries().size(); j++) {
							if (dftFAQ.getSubjectID() == ((YukonListEntry) dftList.getYukonListEntries().get(j)).getEntryID()) {
								int subjectID = ((YukonListEntry) list.getYukonListEntries().get(j)).getEntryID();
								faq.setSubjectID( new Integer(subjectID) );
								break;
							}
						}
						
						faq = (com.cannontech.database.db.stars.CustomerFAQ)
								Transaction.createTransaction(Transaction.INSERT, faq).execute();
						
						LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) StarsLiteFactory.createLite( faq );
						energyCompany.getCustomerFAQs().add( liteFAQ );
					}
					
					ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
					for (int i = 0; i < descendants.size(); i++) {
						LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
						company.updateStarsCustomerFAQs();
					}
				}
			}
        	
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Customer FAQs updated successfully");
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update customer FAQs");
			redirect = referer;
		}
	}
	
	private void updateFAQSubjectList(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			String[] subjectIDs = req.getParameterValues("SubjectIDs");
			ArrayList newSubjects = new ArrayList();
			
			for (int i = 0; i < subjectIDs.length; i++) {
				int subjectID = Integer.parseInt( subjectIDs[i] );
				YukonListEntry subject = YukonListFuncs.getYukonListEntry( subjectID );
				subject.setEntryOrder( i+1 );
				
				com.cannontech.database.db.constants.YukonListEntry entry =
						StarsLiteFactory.createYukonListEntry( subject );
				Transaction.createTransaction( Transaction.UPDATE, entry ).execute();
				
				newSubjects.add( subject );
			}
			
			energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP ).setYukonListEntries( newSubjects );
			
			ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
			for (int i = 0; i < descendants.size(); i++) {
				LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
				company.updateStarsCustomerFAQs();
			}

			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "FAQ subjects updated successfully");
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update FAQ subjects");
			redirect = referer;
		}
	}
	
	private void updateFAQSubject(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			int subjectID = Integer.parseInt( req.getParameter("SubjectID") );
			boolean newGroup = (subjectID == -1);
			
			String subject = req.getParameter("Subject");
			String[] questions = req.getParameterValues("Questions");
			String[] answers = req.getParameterValues("Answers");
			
			ArrayList liteFAQs = energyCompany.getCustomerFAQs();
			YukonListEntry liteSubject = null;
			
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
				
				entry = (com.cannontech.database.db.constants.YukonListEntry)
						Transaction.createTransaction( Transaction.INSERT, entry ).execute();
				
				liteSubject = new YukonListEntry();
				StarsLiteFactory.setConstantYukonListEntry( liteSubject, entry );
				YukonListFuncs.getYukonListEntries().put( entry.getEntryID(), liteSubject );
				cList.getYukonListEntries().add( liteSubject );
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
							
							Transaction.createTransaction( Transaction.DELETE, faq ).execute();
							it.remove();
						}
					}
				}
				
				liteSubject = YukonListFuncs.getYukonListEntry( subjectID );
				if (!liteSubject.getEntryText().equals( subject )) {
					liteSubject.setEntryText( subject );
					com.cannontech.database.db.constants.YukonListEntry entry =
							StarsLiteFactory.createYukonListEntry( liteSubject );
					
					entry = (com.cannontech.database.db.constants.YukonListEntry)
							Transaction.createTransaction( Transaction.UPDATE, entry ).execute();
				}
			}
			
			if (questions != null) {
				for (int i = 0; i < questions.length; i++) {
					com.cannontech.database.db.stars.CustomerFAQ faq =
							new com.cannontech.database.db.stars.CustomerFAQ();
					faq.setSubjectID( new Integer(liteSubject.getEntryID()) );
					faq.setQuestion( questions[i] );
					faq.setAnswer( answers[i] );
					
					faq = (com.cannontech.database.db.stars.CustomerFAQ)
							Transaction.createTransaction( Transaction.INSERT, faq ).execute();
					
					LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) StarsLiteFactory.createLite( faq );
					synchronized (liteFAQs) { liteFAQs.add(liteFAQ); }
				}
			}
			
			ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
			for (int i = 0; i < descendants.size(); i++) {
				LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
				company.updateStarsCustomerFAQs();
			}
			
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Customer FAQs updated successfully");
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update customer FAQs");
			redirect = referer;
		}
	}
	
	private void deleteFAQSubject(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			int subjectID = Integer.parseInt( req.getParameter("SubjectID") );
			if (subjectID == -1) {
				StarsAdminUtil.deleteAllFAQSubjects( energyCompany, false );
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service companies have been deleted successfully");
			}
			else {
				StarsAdminUtil.deleteFAQSubject( subjectID, energyCompany );
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service company has been deleted successfully");
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete service company");
			redirect = referer;
		}
	}
	
	private void updateInterviewQuestions(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			StarsEnergyCompanySettings ecSettings =
					(StarsEnergyCompanySettings) session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsExitInterviewQuestions starsExitQuestions = ecSettings.getStarsExitInterviewQuestions();
			
			String type = req.getParameter("type");
			int qType = CtiUtilities.NONE_ZERO_ID;
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
					questionDB.setExpectedAnswer( new Integer(CtiUtilities.NONE_ZERO_ID) );
					question.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
					question = (com.cannontech.database.data.stars.InterviewQuestion)
							Transaction.createTransaction(Transaction.INSERT, question).execute();
					
					LiteInterviewQuestion liteQuestion = (LiteInterviewQuestion) StarsLiteFactory.createLite( question.getInterviewQuestion() );
					synchronized (liteQuestions) { liteQuestions.add(liteQuestion); }
					
					if (type.equalsIgnoreCase("Exit")) {
						StarsExitInterviewQuestion starsQuestion = new StarsExitInterviewQuestion();
						starsQuestion.setQuestionID( liteQuestion.getQuestionID() );
						starsQuestion.setQuestion( liteQuestion.getQuestion() );
						starsQuestion.setQuestionType( (QuestionType)StarsFactory.newStarsCustListEntry(
								YukonListFuncs.getYukonListEntry(liteQuestion.getQuestionType()), QuestionType.class) );
						starsQuestion.setAnswerType( (AnswerType)StarsFactory.newStarsCustListEntry(
								YukonListFuncs.getYukonListEntry(liteQuestion.getAnswerType()), AnswerType.class) );
						starsExitQuestions.addStarsExitInterviewQuestion( starsQuestion );
					}
				}
			}

			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Interview questions updated successfully");
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update interview questions");
			redirect = referer;
		}
	}
	
	private void updateCustomerSelectionList(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			String listName = req.getParameter("ListName");
			YukonSelectionList cList = energyCompany.getYukonSelectionList( listName, false, false );
			
			if (req.getParameter("Inherited") != null) {
				// Nothing need to be done is the list is already an inherited list
				if (cList == null) return;
				
				// Try to update the references from the current list to the inherited list
				StarsAdminUtil.updateListEntryReferences( energyCompany, energyCompany.getParent().getYukonSelectionList(listName) );
				
				com.cannontech.database.data.constants.YukonSelectionList list =
						new com.cannontech.database.data.constants.YukonSelectionList();
				list.setListID( new Integer(cList.getListID()) );
				Transaction.createTransaction( Transaction.DELETE, list ).execute();
				
				energyCompany.deleteYukonSelectionList( cList );
			}
			else {
				String ordering = req.getParameter("Ordering");
				String label = req.getParameter("Label");
				String whereIsList = req.getParameter("WhereIsList");
				
				String[] entryIDs = req.getParameterValues("EntryIDs");
				String[] entryTexts = req.getParameterValues("EntryTexts");
				String[] yukonDefIDs = req.getParameterValues("YukonDefIDs");
				
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
				
				if (cList != null) {
					// Update yukon selection list
					com.cannontech.database.db.constants.YukonSelectionList listDB =
							StarsLiteFactory.createYukonSelectionList( cList );
					listDB.setOrdering( ordering );
					listDB.setSelectionLabel( label );
					listDB.setWhereIsList( whereIsList );
					
					listDB = (com.cannontech.database.db.constants.YukonSelectionList)
							Transaction.createTransaction( Transaction.UPDATE, listDB ).execute();
					
					StarsLiteFactory.setConstantYukonSelectionList( cList, listDB );
					StarsAdminUtil.updateYukonListEntries( cList, entryData, energyCompany );
				}
				else {
					// Create a new selection list
					com.cannontech.database.data.constants.YukonSelectionList list =
							new com.cannontech.database.data.constants.YukonSelectionList();
					com.cannontech.database.db.constants.YukonSelectionList listDB = list.getYukonSelectionList();
					listDB.setOrdering( ordering );
					listDB.setSelectionLabel( label );
					listDB.setWhereIsList( whereIsList );
					listDB.setListName( listName );
					listDB.setUserUpdateAvailable( StarsDatabaseCache.getInstance().getDefaultEnergyCompany().getYukonSelectionList(listName).getUserUpdateAvailable() );
					list.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
					
					list = (com.cannontech.database.data.constants.YukonSelectionList)
							Transaction.createTransaction(Transaction.INSERT, list).execute();
					listDB = list.getYukonSelectionList();
					
					cList = new YukonSelectionList();
					StarsLiteFactory.setConstantYukonSelectionList( cList, listDB );
					YukonListFuncs.getYukonSelectionLists().put( listDB.getListID(), cList );
					energyCompany.getAllSelectionLists().add( cList );
					
					// Mark all entry data as new entries
					if (entryData != null) {
						for (int i = 0; i < entryData.length; i++)
							entryData[i][0] = new Integer(0);
					}
					
					StarsAdminUtil.updateYukonListEntries( cList, entryData, energyCompany );
					
					// Try to update the refenreces from the inherited list to the list we just created,
					// if this step failed, then rollback all changes by deleting the list we just created.
					try {
						StarsAdminUtil.updateListEntryReferences( energyCompany, cList );
					}
					catch (Exception e) {
						Transaction.createTransaction( Transaction.DELETE, list ).execute();
						energyCompany.deleteYukonSelectionList( cList );
						throw e;
					}
				}
			}
			
			ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
			for (int i = 0; i < descendants.size(); i++) {
				LiteStarsEnergyCompany ec = (LiteStarsEnergyCompany) descendants.get(i);
				ec.updateStarsCustomerSelectionLists();
				
				if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE))
					ec.updateStarsDefaultThermostatSchedules();
			}
			
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Customer selection list updated successfully");
		}
		catch (WebClientException e) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
			redirect = referer;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update customer selection list");
			redirect = referer;
		}
	}
	
	private void updateThermostatSchedule(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			StarsThermostatTypes thermType = StarsThermostatTypes.valueOf( req.getParameter("type") );
			int hwTypeDefID = StarsMsgUtils.getLMHardwareTypeDefID(thermType).intValue();
        	
			StarsUpdateThermostatSchedule updateSched = UpdateThermostatScheduleAction.getRequestOperation(req).getStarsUpdateThermostatSchedule();
        	
			LiteLMThermostatSchedule liteSchedule = energyCompany.getDefaultThermostatSchedule( hwTypeDefID );
			StarsUpdateThermostatScheduleResponse resp = UpdateThermostatScheduleAction.updateThermostatSchedule( updateSched, liteSchedule, energyCompany );
        	
			StarsDefaultThermostatSchedules dftSchedules = energyCompany.getStarsDefaultThermostatSchedules();
			for (int i = 0; i < dftSchedules.getStarsThermostatProgramCount(); i++) {
				StarsThermostatProgram schedule = dftSchedules.getStarsThermostatProgram(i);
				if (schedule.getThermostatType().getType() == thermType.getType()) {
					UpdateThermostatScheduleAction.parseResponse( resp, schedule );
					break;
				}
			}
			
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Default thermostat schedule updated successfully");
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update default thermostat schedule");
			redirect = referer;
		}
	}
	
	private void updateOperatorLogin(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			int userID = Integer.parseInt( req.getParameter("UserID") );
			int operGroupID = Integer.parseInt( req.getParameter("OperatorGroup") );
			String username = req.getParameter( "Username" );
			String password = req.getParameter( "Password" );
			boolean enabled = Boolean.valueOf( req.getParameter("Status") ).booleanValue();
			String status = (enabled)? UserUtils.STATUS_ENABLED : UserUtils.STATUS_DISABLED;
			
			if (userID == -1) {
				// Create new operator login
				LiteYukonGroup liteGroup = AuthFuncs.getGroup( operGroupID );
				LiteYukonUser liteUser = StarsAdminUtil.createOperatorLogin(
						username, password, status, new LiteYukonGroup[] {liteGroup}, energyCompany );
				
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Operator login created successfully" );
				redirect += "?UserID=" + liteUser.getUserID();
			}
			else {
				LiteYukonUser liteUser = YukonUserFuncs.getLiteYukonUser( userID );
				LiteYukonGroup loginGroup = AuthFuncs.getGroup( operGroupID );
				StarsAdminUtil.updateLogin( liteUser, username, password, status, loginGroup, energyCompany );
				
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Operator login updated successfully" );
			}
		}
		catch (WebClientException e) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
			redirect = referer;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update operator login");
			redirect = referer;
		}
	}
	
	private void deleteOperatorLogin(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		int userID = Integer.parseInt( req.getParameter("UserID") );
		ArrayList operLoginIDs = energyCompany.getOperatorLoginIDs();
		
		synchronized (operLoginIDs) {
			Iterator it = operLoginIDs.iterator();
			while (it.hasNext()) {
				int loginID = ((Integer) it.next()).intValue();
				if (userID == -1 || loginID == userID) {
					if (loginID == energyCompany.getUserID()) continue;
					
					com.cannontech.database.data.user.YukonUser.deleteOperatorLogin( new Integer(loginID) );
					
					LiteYukonUser liteUser = YukonUserFuncs.getLiteYukonUser( loginID );
					ServerUtils.handleDBChange(liteUser, DBChangeMsg.CHANGE_TYPE_DELETE);
					it.remove();
				}
			}
		}
	}
	
	private void createEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		ServletUtils.saveRequest( req, session, new String[] {
				"CompanyName", "AddMember", "Email", "OperatorGroup", "CustomerGroup", "Username", "Username2", "Route"} );
		
		try {
			String warning = null;
			LiteYukonGroup operGroup = null;
			
			String[] operGroupNames = req.getParameter("OperatorGroup").split(",");
			String operGroupIDs = "";
			for (int i = 0; i < operGroupNames.length; i++) {
				String groupName = operGroupNames[i].trim();
				if (groupName.equals("")) continue;
				
				LiteYukonGroup group = AuthFuncs.getGroup( groupName );
				if (group == null)
					throw new WebClientException( "Operator group '" + groupName + "' does not exist");
				
				if (i == 0)
					operGroupIDs += String.valueOf( group.getGroupID() );
				else
					operGroupIDs += "," + String.valueOf( group.getGroupID() );
				if (i == 0) operGroup = group;
			}
			
			if (operGroup == null)
				throw new WebClientException( "You must select at least one operator group" );
			
			String[] custGroupNames = req.getParameter("CustomerGroup").split(",");
			String custGroupIDs = "";
			for (int i = 0; i < custGroupNames.length; i++) {
				String groupName = custGroupNames[i].trim();
				if (groupName.equals("")) continue;
				
				LiteYukonGroup group = AuthFuncs.getGroup( groupName );
				if (group == null)
					throw new WebClientException( "Customer group '" + groupName + "' does not exist");
				
				if (i == 0)
					custGroupIDs += String.valueOf( group.getGroupID() );
				else
					custGroupIDs += "," + String.valueOf( group.getGroupID() );
			}
			
			if (YukonUserFuncs.getLiteYukonUser( req.getParameter("Username") ) != null)
				throw new WebClientException( "Username of default operator login already exists" );
			
			if (req.getParameter("Username2").length() > 0 &&
				YukonUserFuncs.getLiteYukonUser( req.getParameter("Username2") ) != null)
				throw new WebClientException( "Username of second operator login already exists" );
			
			// Create a privilege group with EnergyCompany and Administrator role
			Hashtable rolePropMap = new Hashtable();
			rolePropMap.put( new Integer(EnergyCompanyRole.OPERATOR_GROUP_IDS), operGroupIDs );
			rolePropMap.put( new Integer(EnergyCompanyRole.CUSTOMER_GROUP_IDS), custGroupIDs );
			if (req.getParameter("AddMember") == null)
				rolePropMap.put( new Integer(AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY), StarsAdminUtil.FIRST_TIME );
			else
				rolePropMap.put( new Integer(AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY), CtiUtilities.TRUE_STRING );
			
			String adminGroupName = req.getParameter("CompanyName") + " Admin Grp";
			LiteYukonGroup liteAdminGrp = StarsAdminUtil.createOperatorAdminGroup( adminGroupName, rolePropMap );
			
			// Create the default operator login
			LiteYukonGroup[] operGroups = (operGroup != null)?
					new LiteYukonGroup[] { operGroup, liteAdminGrp } : new LiteYukonGroup[] { liteAdminGrp };
			LiteYukonUser liteUser = StarsAdminUtil.createOperatorLogin(
					req.getParameter("Username"), req.getParameter("Password"), UserUtils.STATUS_ENABLED, operGroups, null );
			
			// Create primary contact of the energy company
			com.cannontech.database.data.customer.Contact contact =
					new com.cannontech.database.data.customer.Contact();
			
			contact.getContact().setContLastName( CtiUtilities.STRING_NONE );
			contact.getContact().setContFirstName( CtiUtilities.STRING_NONE );
			
			if (req.getParameter("Email").length() > 0) {
				com.cannontech.database.db.contact.ContactNotification notifEmail =
						new com.cannontech.database.db.contact.ContactNotification();
				notifEmail.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL) );
				notifEmail.setDisableFlag( "N" );
				notifEmail.setNotification( req.getParameter("Email") );
				notifEmail.setOpCode( Transaction.INSERT );
				
				contact.getContactNotifVect().add( notifEmail );
			}
			
			com.cannontech.database.db.customer.Address address = new com.cannontech.database.db.customer.Address();
			address.setStateCode( "" );
			contact.setAddress( address );
			
			contact = (com.cannontech.database.data.customer.Contact)
					Transaction.createTransaction( Transaction.INSERT, contact ).execute();
			
			LiteContact liteContact = new LiteContact( contact.getContact().getContactID().intValue() );
			StarsLiteFactory.setLiteContact( liteContact, contact );
			ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_ADD );
			
			// Create the energy company
			com.cannontech.database.db.company.EnergyCompany company =
					new com.cannontech.database.db.company.EnergyCompany();
			company.setName( req.getParameter("CompanyName") );
			company.setPrimaryContactID( contact.getContact().getContactID() );
			company.setUserID( new Integer(liteUser.getUserID()) );
			company = (com.cannontech.database.db.company.EnergyCompany)
					Transaction.createTransaction(Transaction.INSERT, company).execute();
			
			SqlStatement stmt = new SqlStatement(
					"INSERT INTO EnergyCompanyOperatorLoginList VALUES(" +
						company.getEnergyCompanyID() + ", " + liteUser.getUserID() + ")",
					CtiUtilities.getDatabaseAlias()
					);
			stmt.execute();
			
			LiteStarsEnergyCompany newCompany = new LiteStarsEnergyCompany( company );
			newCompany.setAccountsLoaded( true );
			newCompany.setInventoryLoaded( true );
			newCompany.setWorkOrdersLoaded( true );
			
			StarsDatabaseCache.getInstance().addEnergyCompany( newCompany );
			ServerUtils.handleDBChange( newCompany, DBChangeMsg.CHANGE_TYPE_ADD );
			
			// Create login for the second operator
			if (req.getParameter("Username2").length() > 0) {
				operGroups = (operGroup != null)?
						new LiteYukonGroup[] { operGroup } : new LiteYukonGroup[0];
				StarsAdminUtil.createOperatorLogin(
						req.getParameter("Username2"), req.getParameter("Password2"), UserUtils.STATUS_ENABLED, operGroups, newCompany );
			}
			
			// Assign default route to the energy company
			int routeID = Integer.parseInt( req.getParameter("Route") );
			StarsAdminUtil.updateDefaultRoute( newCompany, routeID );
			
			// Add the new energy company as a member of the current company
			if (req.getParameter("AddMember") != null)
				StarsAdminUtil.addMember( energyCompany, newCompany, liteUser.getUserID() );
			
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Energy company created successfully");
		}
		catch (WebClientException e) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to create the energy company");
		}
	}
	
	private void deleteEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		if (energyCompany.getChildren().size() > 0) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Cannot delete this energy company because its member list is not empty");
			redirect = referer;
			return;
		}
		
		TimeConsumingTask task = new DeleteEnergyCompanyTask( user.getEnergyCompanyID() );
		long id = ProgressChecker.addTask( task );
		
		// Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
		for (int i = 0; i < 5; i++) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {}
			
			task = ProgressChecker.getTask(id);
			
			if (task.getStatus() == TimeConsumingTask.STATUS_FINISHED) {
				ProgressChecker.removeTask( id );
				return;
			}
			
			if (task.getStatus() == TimeConsumingTask.STATUS_ERROR) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
				ProgressChecker.removeTask( id );
				return;
			}
		}
		
		session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}
	
	private void updateRouteList(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			ArrayList routeIDs = energyCompany.getRouteIDs();
			ArrayList oldRouteIDs = new ArrayList( routeIDs );
			
			String[] rtIDs = req.getParameterValues( "RouteIDs" );
			if (routeIDs != null) {
				for (int i = 0; i < rtIDs.length; i++) {
					Integer routeID = Integer.valueOf( rtIDs[i] );
					
					if (oldRouteIDs.contains( routeID )) {
						// Route already assigned to this energy company
						oldRouteIDs.remove( routeID );
					}
					else {
						// New route
						ECToGenericMapping map = new ECToGenericMapping();
						map.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
						map.setItemID( routeID );
						map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_ROUTE );
						Transaction.createTransaction( Transaction.INSERT, map ).execute();
						
						synchronized (routeIDs) { routeIDs.add( routeID ); }
					}
				}
			}
			
			for (int i = 0; i < oldRouteIDs.size(); i++) {
				// Routes to be removed
				int routeID = ((Integer) oldRouteIDs.get(i)).intValue();
				if (routeID == energyCompany.getDefaultRouteID())
					StarsAdminUtil.updateDefaultRoute( energyCompany, 0 );
				StarsAdminUtil.removeRoute( energyCompany, routeID );
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to update the assigned routes" );
		}
	}
	
	private void memberLogin(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		int userID = Integer.parseInt( req.getParameter("UserID") );
		LiteYukonUser memberLogin = YukonUserFuncs.getLiteYukonUser( userID );
		
		LiteYukonUser liteUser = null;
		if (memberLogin == null ||
			(liteUser = LoginController.internalLogin(
				req,
				session,
				memberLogin.getUsername(),
				memberLogin.getPassword(),
				true)) == null)
		{
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "The member login is no longer valid" );
			return;
		}
		
		redirect = AuthFuncs.getRolePropertyValue( liteUser, WebClientRole.HOME_URL );
	}
	
	public static void switchContext(StarsYukonUser user, HttpServletRequest req, HttpSession session, int memberID) throws WebClientException {
		if (memberID == user.getEnergyCompanyID()) return;
		
//		if (!AuthFuncs.checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS ))
//			throw new WebClientException( "The current user doesn't have the privilege to manage members" );
		
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		LiteStarsEnergyCompany member = StarsDatabaseCache.getInstance().getEnergyCompany( memberID );
		
		ArrayList loginIDs = energyCompany.getMemberLoginIDs();
		for (int i = 0; i < loginIDs.size(); i++) {
			LiteYukonUser liteUser = YukonUserFuncs.getLiteYukonUser( ((Integer) loginIDs.get(i)).intValue() );
			if (liteUser == null) continue;
			
			if (EnergyCompanyFuncs.getEnergyCompany( liteUser ).getEnergyCompanyID() == memberID) {
				if (LoginController.internalLogin(
						req,
						session,
						liteUser.getUsername(),
						liteUser.getPassword(),
						true) == null)
				{
					throw new WebClientException( "The member login is no longer valid" );
				}
				
				req.getSession().setAttribute( ServletUtils.ATT_CONTEXT_SWITCHED, "true" );
				return;
			}
		}
		
		throw new WebClientException( "No member login assigned to '" + member.getName() + "'" );
	}
	
	private void addMemberEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			int memberID = Integer.parseInt( req.getParameter("MemberID") );
			int loginID = Integer.parseInt( req.getParameter("LoginID") );
			StarsAdminUtil.addMember( energyCompany, StarsDatabaseCache.getInstance().getEnergyCompany(memberID), loginID );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to add new member");
		}
	}
	
	private void updateMemberEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		int memberID = Integer.parseInt( req.getParameter("MemberID") );
		int loginID = Integer.parseInt( req.getParameter("LoginID") );
		
		ArrayList loginIDs = energyCompany.getMemberLoginIDs();
		Integer prevLoginID = null;
		
		for (int i = 0; i < loginIDs.size(); i++) {
			Integer id = (Integer) loginIDs.get(i);
			LiteYukonUser liteUser = YukonUserFuncs.getLiteYukonUser( id.intValue() );
			if (EnergyCompanyFuncs.getEnergyCompany( liteUser ).getEnergyCompanyID() == memberID) {
				prevLoginID = id;
				break;
			}
		}
		
		try {
			if (prevLoginID != null) {
				if (prevLoginID.intValue() == loginID) return;
				
				if (loginID != -1) {
					String sql = "UPDATE ECToGenericMapping SET ItemID = " + loginID +
							" WHERE EnergyCompanyID = " + energyCompany.getEnergyCompanyID() +
							" AND MappingCategory = 'MemberLogin'";
					SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
					stmt.execute();
				}
				else {
					ECToGenericMapping map = new ECToGenericMapping();
					map.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
					map.setItemID( prevLoginID );
					map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER_LOGIN );
					Transaction.createTransaction( Transaction.DELETE, map ).execute();
				}
			}
			else {
				if (loginID == -1) return;
				
				ECToGenericMapping map = new ECToGenericMapping();
				map.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				map.setItemID( new Integer(loginID) );
				map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER_LOGIN );
				Transaction.createTransaction( Transaction.INSERT, map ).execute();
			}
			
			energyCompany.clearHierarchy();
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update member login");
		}
	}
	
	private void removeMemberEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			int memberID = Integer.parseInt( req.getParameter("MemberID") );
			StarsAdminUtil.removeMember( energyCompany, memberID );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to remove member(s)");
		}
	}
	
}
