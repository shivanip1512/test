package com.cannontech.stars.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

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
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.device.lm.LMGroupExpressCom;
import com.cannontech.database.data.device.lm.MacroGroup;
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
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteLMProgramEvent;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSchedule;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.LiteWebConfiguration;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.macro.GenericMacro;
import com.cannontech.database.db.macro.MacroTypes;
import com.cannontech.database.db.stars.ECToGenericMapping;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.application.WebClientRole;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.servlet.LoginController;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.DeleteCustAccountsTask;
import com.cannontech.stars.util.task.DeleteEnergyCompanyTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.UpdateLMHardwareAction;
import com.cannontech.stars.web.action.UpdateThermostatScheduleAction;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.AnswerType;
import com.cannontech.stars.xml.serialize.CompanyAddress;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.QuestionType;
import com.cannontech.stars.xml.serialize.StarsApplianceCategory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustomerAddress;
import com.cannontech.stars.xml.serialize.StarsCustomerFAQ;
import com.cannontech.stars.xml.serialize.StarsCustomerFAQGroup;
import com.cannontech.stars.xml.serialize.StarsCustomerFAQs;
import com.cannontech.stars.xml.serialize.StarsDefaultThermostatSchedules;
import com.cannontech.stars.xml.serialize.StarsEnergyCompany;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestion;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestions;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMPrograms;
import com.cannontech.stars.xml.serialize.StarsServiceCompanies;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.stars.xml.serialize.StarsThermostatProgram;
import com.cannontech.stars.xml.serialize.StarsUpdateThermostatSchedule;
import com.cannontech.stars.xml.serialize.StarsUpdateThermostatScheduleResponse;
import com.cannontech.stars.xml.serialize.types.StarsThermostatTypes;
import com.cannontech.user.UserUtils;

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
		
		if (action.equalsIgnoreCase("RefreshCache")) {
			if (req.getParameter("Range").equalsIgnoreCase("All")) {
				SOAPServer.refreshCache();
			}
			else if (req.getParameter("Range").equalsIgnoreCase("EnergyCompany")) {
				LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
				SOAPServer.refreshCache( energyCompany );
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
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
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
				
				DeleteCustAccountsTask task = new DeleteCustAccountsTask(user, accountIDs);
				long id = ProgressChecker.addTask( task );
				
				// Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
				for (int i = 0; i < 5; i++) {
					try {
						Thread.sleep(1000);
					}
					catch (InterruptedException e) {}
					
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
				redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Delete customer accounts failed");
		}
	}
	
	private void updateAddress(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			StarsEnergyCompanySettings ecSettings =
					(StarsEnergyCompanySettings) session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
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
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update address information");
			redirect = referer;
		}
	}
	
	public static void updateDefaultRoute(LiteStarsEnergyCompany energyCompany, int routeID) throws Exception {
		if (energyCompany.getDefaultRouteID() != routeID) {
			if (energyCompany.getDefaultRouteID() == LiteStarsEnergyCompany.INVALID_ROUTE_ID) {
				// Assign the default route to the energy company
				LMGroupExpressCom grpDftRoute = (LMGroupExpressCom) LMFactory.createLoadManagement( PAOGroups.LM_GROUP_EXPRESSCOMM );
				grpDftRoute.setPAOName( energyCompany.getName() + " Default Route" );
				grpDftRoute.setRouteID( new Integer(routeID) );
				grpDftRoute = (LMGroupExpressCom) Transaction.createTransaction( Transaction.INSERT, grpDftRoute ).execute();
				ServerUtils.handleDBChangeMsg( grpDftRoute.getDBChangeMsgs(DBChangeMsg.CHANGE_TYPE_ADD)[0] );
				
				MacroGroup grpSerial = (MacroGroup) LMFactory.createLoadManagement( PAOGroups.MACRO_GROUP );
				grpSerial.setPAOName( energyCompany.getName() + " Serial Group" );
				GenericMacro macro = new GenericMacro();
				macro.setChildID( grpDftRoute.getPAObjectID() );
				macro.setChildOrder( new Integer(0) );
				macro.setMacroType( MacroTypes.GROUP );
				grpSerial.getMacroGroupVector().add( macro );
				grpSerial = (MacroGroup) Transaction.createTransaction( Transaction.INSERT, grpSerial ).execute();
				ServerUtils.handleDBChangeMsg( grpSerial.getDBChangeMsgs(DBChangeMsg.CHANGE_TYPE_ADD)[0] );
				
				String sql = "INSERT INTO OperatorSerialGroup VALUES (" + energyCompany.getUserID() + ", " + grpSerial.getPAObjectID() + ")";
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
			}
			else if (routeID > 0 || energyCompany.getDefaultRouteID() > 0) {
				if (routeID < 0) routeID = 0;
				
				String sql = "SELECT exc.LMGroupID FROM LMGroupExpressCom exc, GenericMacro macro, OperatorSerialGroup opgrp " +
						"WHERE opgrp.LoginID = " + energyCompany.getUserID() + " AND opgrp.LMGroupID = macro.OwnerID " +
						"AND macro.MacroType = '" + MacroTypes.GROUP + "' AND macro.ChildID = exc.LMGroupID AND exc.SerialNumber = '0'";
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
				
				if (stmt.getRowCount() == 0)
					throw new Exception( "Not able to find the default route group, sql = \"" + sql + "\"" );
				int groupID = ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue();
				
				LMGroupExpressCom group = new LMGroupExpressCom();
				group.setLMGroupID( new Integer(groupID) );
				group = (LMGroupExpressCom) Transaction.createTransaction( Transaction.RETRIEVE, group ).execute();
				
				com.cannontech.database.db.device.lm.LMGroupExpressCom grpDB = group.getLMGroupExpressComm();
				grpDB.setRouteID( new Integer(routeID) );
				Transaction.createTransaction( Transaction.UPDATE, grpDB ).execute();
				ServerUtils.handleDBChangeMsg( group.getDBChangeMsgs(DBChangeMsg.CHANGE_TYPE_UPDATE)[0] );
			}
			
			energyCompany.setDefaultRouteID( routeID );
		}
	}
	
	public static void removeDefaultRoute(LiteStarsEnergyCompany energyCompany) throws Exception {
		String sql = "SELECT exc.LMGroupID, opgrp.LMGroupID FROM LMGroupExpressCom exc, GenericMacro macro, OperatorSerialGroup opgrp " +
				"WHERE opgrp.LoginID = " + energyCompany.getUserID() + " AND opgrp.LMGroupID = macro.OwnerID " +
				"AND macro.MacroType = '" + MacroTypes.GROUP + "' AND macro.ChildID = exc.LMGroupID AND exc.SerialNumber = '0'";
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
		stmt.execute();
		
		if (stmt.getRowCount() > 0) {
			int dftRtGrpID = ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue();
			int serialGrpID = ((java.math.BigDecimal) stmt.getRow(0)[1]).intValue();
			
			sql = "DELETE FROM OperatorSerialGroup WHERE LMGroupID = " + serialGrpID;
			stmt.setSQLString( sql );
			stmt.execute();
			
			MacroGroup grpSerial = (MacroGroup) LMFactory.createLoadManagement( PAOGroups.MACRO_GROUP );
			grpSerial.setDeviceID( new Integer(serialGrpID) );
			Transaction.createTransaction( Transaction.DELETE, grpSerial ).execute();
			ServerUtils.handleDBChangeMsg( grpSerial.getDBChangeMsgs(DBChangeMsg.CHANGE_TYPE_DELETE)[0] );
			
			LMGroupExpressCom grpDftRoute = (LMGroupExpressCom) LMFactory.createLoadManagement( PAOGroups.LM_GROUP_EXPRESSCOMM );
			grpDftRoute.setLMGroupID( new Integer(dftRtGrpID) );
			Transaction.createTransaction( Transaction.DELETE, grpDftRoute ).execute();
			ServerUtils.handleDBChangeMsg( grpDftRoute.getDBChangeMsgs(DBChangeMsg.CHANGE_TYPE_DELETE)[0] );
		}
	}
	
	private void updateEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			StarsEnergyCompanySettings ecSettings =
					(StarsEnergyCompanySettings) session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
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
				addr.setStateCode( " " );
				StarsEnergyCompany ecTemp = (StarsEnergyCompany) session.getAttribute( ENERGY_COMPANY_TEMP );
				if (ecTemp != null)
					StarsFactory.setCustomerAddress( addr, ecTemp.getCompanyAddress() );
				contact.setAddress( addr );
				
				contact = (com.cannontech.database.data.customer.Contact)
						Transaction.createTransaction( Transaction.INSERT, contact ).execute();
				liteContact = new LiteContact( contact.getContact().getContactID().intValue() );
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
			ec.setMainPhoneNumber( ECUtils.getNotification(liteNotifPhone) );
			
			LiteContactNotification liteNotifFax = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_FAX );
			ec.setMainFaxNumber( ECUtils.getNotification(liteNotifFax) );
			
			LiteContactNotification liteNotifEmail = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
			ec.setEmail( ECUtils.getNotification(liteNotifEmail) );
			
			String compName = req.getParameter("CompanyName");
			if (newContact || !energyCompany.getName().equals( compName )) {
				energyCompany.setName( compName );
				energyCompany.setPrimaryContactID( contact.getContact().getContactID().intValue() );
				Transaction.createTransaction( Transaction.UPDATE, StarsLiteFactory.createDBPersistent(energyCompany) ).execute();
				
				ec.setCompanyName( compName );
			}
			
			// Update energy company role DEFAULT_TIME_ZONE if necessary
			LiteYukonGroup adminGroup = energyCompany.getOperatorAdminGroup();
			String value = AuthFuncs.getRolePropValueGroup(adminGroup, EnergyCompanyRole.DEFAULT_TIME_ZONE, CtiUtilities.STRING_NONE);
			boolean adminGroupUpdated = false;
	        
			String timeZone = req.getParameter("TimeZone");
			if (!value.equalsIgnoreCase( timeZone )) {
				String sql = "UPDATE YukonGroupRole SET Value = '" + timeZone + "'" +
						" WHERE GroupID = " + adminGroup.getGroupID() +
						" AND RoleID = " + EnergyCompanyRole.ROLEID +
						" AND RolePropertyID = " + EnergyCompanyRole.DEFAULT_TIME_ZONE;
				com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
						sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
	        	
	        	adminGroupUpdated = true;
				ec.setTimeZone( timeZone );
			}
        	
			int routeID = Integer.parseInt(req.getParameter("Route"));
			updateDefaultRoute( energyCompany, routeID );
			
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
			
			value = AuthFuncs.getRolePropValueGroup(adminGroup, EnergyCompanyRole.OPERATOR_GROUP_IDS, CtiUtilities.STRING_NONE);
			if (!value.equalsIgnoreCase( operGroupIDs )) {
				String sql = "UPDATE YukonGroupRole SET Value = '" + operGroupIDs + "'" +
						" WHERE GroupID = " + adminGroup.getGroupID() +
						" AND RoleID = " + EnergyCompanyRole.ROLEID +
						" AND RolePropertyID = " + EnergyCompanyRole.OPERATOR_GROUP_IDS;
				com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
						sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
	        	
				adminGroupUpdated = true;
			}
			
			value = AuthFuncs.getRolePropValueGroup(adminGroup, EnergyCompanyRole.CUSTOMER_GROUP_IDS, CtiUtilities.STRING_NONE);
			if (!value.equalsIgnoreCase( custGroupIDs )) {
				String sql = "UPDATE YukonGroupRole SET Value = '" + custGroupIDs + "'" +
						" WHERE GroupID = " + adminGroup.getGroupID() +
						" AND RoleID = " + EnergyCompanyRole.ROLEID +
						" AND RolePropertyID = " + EnergyCompanyRole.CUSTOMER_GROUP_IDS;
				com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
						sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
	        	
				adminGroupUpdated = true;
			}
			
			String optOutNotif = req.getParameter( "OptOutNotif" );
			value = AuthFuncs.getRolePropValueGroup(adminGroup, EnergyCompanyRole.OPTOUT_NOTIFICATION_RECIPIENTS, CtiUtilities.STRING_NONE);
			
			if (!value.equalsIgnoreCase( optOutNotif )) {
				String sql = "UPDATE YukonGroupRole SET Value = '" + optOutNotif + "'" +
						" WHERE GroupID = " + adminGroup.getGroupID() +
						" AND RoleID = " + EnergyCompanyRole.ROLEID +
						" AND RolePropertyID = " + EnergyCompanyRole.OPTOUT_NOTIFICATION_RECIPIENTS;
				com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
						sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
	        	
				adminGroupUpdated = true;
			}
			
			if (adminGroupUpdated)
				ServerUtils.handleDBChange( adminGroup, com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE );
        	
			session.removeAttribute( ENERGY_COMPANY_TEMP );
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
	
	public static LiteApplianceCategory createApplianceCategory(String appCatName, LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		com.cannontech.database.db.web.YukonWebConfiguration config =
				new com.cannontech.database.db.web.YukonWebConfiguration();
		config.setLogoLocation( "yukon/Icons/Load.gif" );
		config.setAlternateDisplayName( appCatName );
		config.setDescription( "" );
		config.setURL( "" );
		
		int dftCatID = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DEFAULT).getEntryID();
		
		com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
				new com.cannontech.database.data.stars.appliance.ApplianceCategory();
		com.cannontech.database.db.stars.appliance.ApplianceCategory appCatDB = appCat.getApplianceCategory();
		
		appCatDB.setCategoryID( new Integer(dftCatID) );
		appCatDB.setDescription( appCatName );
		appCat.setWebConfiguration( config );
		appCat.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
		
		appCat = (com.cannontech.database.data.stars.appliance.ApplianceCategory)
				Transaction.createTransaction( Transaction.INSERT, appCat ).execute();
		
		LiteApplianceCategory liteAppCat = (LiteApplianceCategory) StarsLiteFactory.createLite( appCat.getApplianceCategory() );
		energyCompany.addApplianceCategory( liteAppCat );
		LiteWebConfiguration liteConfig = (LiteWebConfiguration) StarsLiteFactory.createLite( appCat.getWebConfiguration() );
		SOAPServer.addWebConfiguration( liteConfig );
		
		StarsApplianceCategory starsAppCat = StarsLiteFactory.createStarsApplianceCategory( liteAppCat, energyCompany );
		energyCompany.getStarsEnrollmentPrograms().addStarsApplianceCategory( starsAppCat );
		
		return liteAppCat;
	}
	
	private void updateApplianceCategory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
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
				SOAPServer.addWebConfiguration( liteConfig );
			}
			else {
				liteAppCat = energyCompany.getApplianceCategory( appCatID );
				if (liteAppCat.getDirectOwner() != energyCompany) {
					session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Cannot update an inherited appliance category" );
					return;
				}
				
				appCat.setApplianceCategoryID( new Integer(appCatID) );
				appCatDB.setWebConfigurationID( new Integer(liteAppCat.getWebConfigurationID()) );
				
				appCat = (com.cannontech.database.data.stars.appliance.ApplianceCategory)
						Transaction.createTransaction( Transaction.UPDATE, appCat ).execute();
				
				StarsLiteFactory.setLiteApplianceCategory( liteAppCat, appCat.getApplianceCategory() );
				LiteWebConfiguration liteConfig = SOAPServer.getWebConfiguration( appCat.getWebConfiguration().getConfigurationID().intValue() );
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
						if (lProg.getProgramID() == progID || lProg.getDeviceID() == deviceID) {
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
						
						String oldDispName = ECUtils.getPublishedProgramName( liteProg );
						
						LiteWebConfiguration liteCfg = SOAPServer.getWebConfiguration( liteProg.getWebSettingsID() );
						StarsLiteFactory.setLiteWebConfiguration( liteCfg, pubProg.getWebConfiguration() );
						
						// If program display name changed, we need to update all the accounts enrolled in this program
						if (!newDispName.equals( oldDispName )) {
							for (int j = 0; j < descendants.size(); j++) {
								LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(j);
								int[] accountIDs = com.cannontech.database.db.stars.appliance.ApplianceBase.getAllAccountIDsWithProgram(liteProg.getProgramID(), company.getLiteID());
								
								for (int k = 0; k < accountIDs.length; k++) {
									StarsCustAccountInformation starsAcctInfo = company.getStarsCustAccountInformation( accountIDs[k] );
									if (starsAcctInfo != null) {
										StarsLMPrograms starsProgs = starsAcctInfo.getStarsLMPrograms();
										
										for (int l = 0; l < starsProgs.getStarsLMProgramCount(); l++) {
											if (starsProgs.getStarsLMProgram(l).getProgramID() == liteProg.getProgramID()) {
												starsProgs.getStarsLMProgram(l).setProgramName( newDispName );
												break;
											}
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
						SOAPServer.addWebConfiguration( liteCfg );
					}
				}
			}
			
			// Delete the rest of published programs
			for (int i = 0; i < pubProgList.size(); i++) {
				LiteLMProgramWebPublishing liteProg = (LiteLMProgramWebPublishing) pubProgList.get(i);
				
				for (int j = 0; j < descendants.size(); j++) {
					LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(j);
					
					// Delete all events of this program
					com.cannontech.database.data.stars.event.LMProgramEvent.deleteAllLMProgramEvents(
							company.getLiteID(), liteProg.getProgramID() );
					
					// Unenroll the program from all customers currently enrolled in it
					int[] accountIDs = com.cannontech.database.db.stars.appliance.ApplianceBase.getAllAccountIDsWithProgram(
							liteProg.getProgramID(), company.getLiteID() );
					
					com.cannontech.database.data.stars.appliance.ApplianceBase app =
							new com.cannontech.database.data.stars.appliance.ApplianceBase();
					com.cannontech.database.db.stars.appliance.ApplianceBase appDB = app.getApplianceBase();
					
					for (int k = 0; k < accountIDs.length; k++) {
						LiteStarsCustAccountInformation liteAcctInfo = company.getCustAccountInformation( accountIDs[k], true );
						
						for (int l = 0; l < liteAcctInfo.getAppliances().size(); l++) {
							LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(l);
							if (liteApp.getProgramID() != liteProg.getProgramID()) continue;
							
							liteApp.setProgramID( CtiUtilities.NONE_ID );
							liteApp.setInventoryID( CtiUtilities.NONE_ID );
							liteApp.setAddressingGroupID( CtiUtilities.NONE_ID );
							liteApp.setLoadNumber( 0 );
							
							StarsLiteFactory.setApplianceBase( app, liteApp );
							app = (com.cannontech.database.data.stars.appliance.ApplianceBase)
									Transaction.createTransaction( Transaction.UPDATE, app ).execute();
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
				SOAPServer.deleteWebConfiguration( liteProg.getWebSettingsID() );
			}
			
			for (int i = 0; i < descendants.size(); i++) {
				LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
				StarsEnrollmentPrograms starsAppCats = company.getStarsEnrollmentPrograms();
				StarsApplianceCategory starsAppCat = StarsLiteFactory.createStarsApplianceCategory( liteAppCat, company );
				
				if (newAppCat) {
					starsAppCats.addStarsApplianceCategory( starsAppCat );
				}
				else {
					for (int j = 0; j < starsAppCats.getStarsApplianceCategoryCount(); j++) {
						if (starsAppCats.getStarsApplianceCategory(j).getApplianceCategoryID() == appCatID) {
							starsAppCats.setStarsApplianceCategory( j, starsAppCat );
							break;
						}
					}
				}
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
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		ArrayList descendants = ECUtils.getAllDescendants( energyCompany );
        
		try {
			ArrayList liteAppCats = energyCompany.getAllApplianceCategories();
			
			int appCatID = Integer.parseInt( req.getParameter("AppCatID") );
			boolean deleteAll = (appCatID == -1);
			
			for (int i = 0; i < liteAppCats.size(); i++) {
				LiteApplianceCategory liteAppCat = (LiteApplianceCategory) liteAppCats.get(i);
				if (!deleteAll && liteAppCat.getApplianceCategoryID() != appCatID
					|| liteAppCat.getDirectOwner() != energyCompany)
					continue;
				
				int[] programIDs = new int[ liteAppCat.getPublishedPrograms().size() ];
				for (int j = 0; j < liteAppCat.getPublishedPrograms().size(); j++)
					programIDs[j] = ((LiteLMProgramWebPublishing) liteAppCat.getPublishedPrograms().get(j)).getProgramID();
				Arrays.sort( programIDs );
				
				for (int j = 0; j < descendants.size(); j++) {
					LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(j);
					
					// Delete all program events
					for (int k = 0; k < programIDs.length; k++) {
						com.cannontech.database.data.stars.event.LMProgramEvent.deleteAllLMProgramEvents(
								company.getLiteID(), programIDs[k] );
					}
					
					int[] accountIDs = com.cannontech.database.db.stars.appliance.ApplianceBase.getAllAccountIDsWithCategory(
							liteAppCat.getApplianceCategoryID(), company.getLiteID() );
					int[] applianceIDs = com.cannontech.database.db.stars.appliance.ApplianceBase.getAllApplianceIDsWithCategory(
							liteAppCat.getApplianceCategoryID(), company.getLiteID() );
					
					com.cannontech.database.data.stars.appliance.ApplianceBase app =
							new com.cannontech.database.data.stars.appliance.ApplianceBase();
					for (int k = 0; k < applianceIDs.length; k++) {
						app.setApplianceID( new Integer(applianceIDs[k]) );
						Transaction.createTransaction( Transaction.DELETE, app ).execute();
					}
					
					for (int k = 0; k < accountIDs.length; k++) {
						LiteStarsCustAccountInformation liteAcctInfo = company.getCustAccountInformation( accountIDs[k], true );
						
						Iterator appIt = liteAcctInfo.getAppliances().iterator();
						while (appIt.hasNext()) {
							LiteStarsAppliance liteApp = (LiteStarsAppliance) appIt.next();
							if (liteApp.getApplianceCategoryID() == liteAppCat.getApplianceCategoryID()) {
								appIt.remove();
							}
						}
						
						Iterator progIt = liteAcctInfo.getPrograms().iterator();
						while (progIt.hasNext()) {
							int progID = ((LiteStarsLMProgram) progIt.next()).getProgramID();
							if (Arrays.binarySearch(programIDs, progID) >= 0)
								progIt.remove();
						}
						
						Iterator it = liteAcctInfo.getProgramHistory().iterator();
						while (it.hasNext()) {
							int progID = ((LiteLMProgramEvent) it.next()).getProgramID();
							if (Arrays.binarySearch(programIDs, progID) >= 0)
								it.remove();
						}
						
						company.updateStarsCustAccountInformation( liteAcctInfo );
					}
				}
				
				com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
						new com.cannontech.database.data.stars.appliance.ApplianceCategory();
				StarsLiteFactory.setApplianceCategory( appCat.getApplianceCategory(), liteAppCat );
				Transaction.createTransaction( Transaction.DELETE, appCat ).execute();
				
				energyCompany.deleteApplianceCategory( liteAppCat.getApplianceCategoryID() );
				SOAPServer.deleteWebConfiguration( liteAppCat.getWebConfigurationID() );
				
				for (int j = 0; j < liteAppCat.getPublishedPrograms().size(); j++) {
					LiteLMProgramWebPublishing liteProg = (LiteLMProgramWebPublishing) liteAppCat.getPublishedPrograms().get(j);
					
					com.cannontech.database.db.web.YukonWebConfiguration cfg =
							new com.cannontech.database.db.web.YukonWebConfiguration();
					cfg.setConfigurationID( new Integer(liteProg.getWebSettingsID()) );
					Transaction.createTransaction( Transaction.DELETE, cfg ).execute();
					
					SOAPServer.deleteWebConfiguration( liteProg.getWebSettingsID() );
				}
				
				for (int j = 0; j < descendants.size(); j++) {
					LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(j);
					StarsEnrollmentPrograms starsAppCats = company.getStarsEnrollmentPrograms();
					
					for (int k = 0; k < starsAppCats.getStarsApplianceCategoryCount(); k++) {
						if (starsAppCats.getStarsApplianceCategory(k).getApplianceCategoryID() == liteAppCat.getApplianceCategoryID()) {
							starsAppCats.removeStarsApplianceCategory( k );
							break;
						}
					}
				}
			}
			
			if (deleteAll)
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Appliance categories have been deleted successfully");
			else
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Appliance category has been deleted successfully");
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete appliance category");
			redirect = referer;
		}
	}
	
	public static LiteServiceCompany createServiceCompany(String companyName, LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		com.cannontech.database.data.stars.report.ServiceCompany company =
				new com.cannontech.database.data.stars.report.ServiceCompany();
		com.cannontech.database.db.stars.report.ServiceCompany companyDB = company.getServiceCompany();
		
		companyDB.setCompanyName( companyName );
		company.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
		
		company = (com.cannontech.database.data.stars.report.ServiceCompany)
				Transaction.createTransaction( Transaction.INSERT, company ).execute();
		
		com.cannontech.database.data.customer.Contact contact =
				new com.cannontech.database.data.customer.Contact();
		contact.setCustomerContact( company.getPrimaryContact() );
		LiteContact liteContact = (LiteContact) StarsLiteFactory.createLite(contact);
		energyCompany.addContact( liteContact, null );
		
		LiteServiceCompany liteCompany = (LiteServiceCompany) StarsLiteFactory.createLite( companyDB );
		energyCompany.addServiceCompany( liteCompany );
		
		StarsServiceCompany starsCompany = new StarsServiceCompany();
		StarsLiteFactory.setStarsServiceCompany( starsCompany, liteCompany, energyCompany );
		energyCompany.getStarsServiceCompanies().addStarsServiceCompany( starsCompany );
		
		return liteCompany;
	}

	private void updateServiceCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		StarsServiceCompanies starsCompanies = energyCompany.getStarsServiceCompanies();
        
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
	
	public static void deleteServiceCompany(int companyID, LiteStarsEnergyCompany energyCompany)
		throws TransactionException
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
				
				invDB = (com.cannontech.database.db.stars.hardware.InventoryBase)
						Transaction.createTransaction( Transaction.UPDATE, invDB ).execute();
	    		
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
				
				order = (com.cannontech.database.db.stars.report.WorkOrderBase)
						Transaction.createTransaction( Transaction.UPDATE, order ).execute();
				
				liteOrder.setServiceCompanyID( CtiUtilities.NONE_ID );
			}
		}
		
		LiteServiceCompany liteCompany = energyCompany.getServiceCompany( companyID );
		
		com.cannontech.database.data.stars.report.ServiceCompany company =
				new com.cannontech.database.data.stars.report.ServiceCompany();
		StarsLiteFactory.setServiceCompany( company.getServiceCompany(), liteCompany );
		
		Transaction.createTransaction( Transaction.DELETE, company ).execute();
		
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
	
	public static void deleteAllServiceCompanies(LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		ArrayList companies = energyCompany.getAllServiceCompanies();
		
		for (int i = 0; i < companies.size(); i++) {
			LiteServiceCompany liteCompany = (LiteServiceCompany) companies.get(i);
			deleteServiceCompany( liteCompany.getCompanyID(), energyCompany );
		}
	}
	
	private void deleteServiceCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			int companyID = Integer.parseInt( req.getParameter("CompanyID") );
			if (companyID == -1) {
				deleteAllServiceCompanies( energyCompany );
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service companies have been deleted successfully");
			}
			else {
				deleteServiceCompany( companyID, energyCompany );
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service company has been deleted successfully");
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete the service company");
			redirect = referer;
		}
	}
	
	private void updateCustomerFAQLink(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		LiteYukonUser liteUser = user.getYukonUser();
        
		try {
			boolean customizedFAQ = Boolean.valueOf( req.getParameter("CustomizedFAQ") ).booleanValue();
			String faqLink = req.getParameter("FAQLink");
			String value = AuthFuncs.getRolePropertyValue( liteUser, ConsumerInfoRole.WEB_LINK_FAQ, "(none)" );
			
			LiteYukonGroup[] customerGroups = energyCompany.getResidentialCustomerGroups();
			LiteYukonGroup[] operatorGroups = energyCompany.getWebClientOperatorGroups();
        	
			String sql = null;
			com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
					sql, CtiUtilities.getDatabaseAlias() );
        	
			if (customizedFAQ && !value.equals(faqLink) ||
				!customizedFAQ && ServerUtils.forceNotNone(value).length() > 0)
			{
				if (!customizedFAQ) faqLink = "(none)";
        		
				for (int i = 0; i < customerGroups.length; i++) {
					if (AuthFuncs.getRolePropValueGroup(customerGroups[i], ResidentialCustomerRole.WEB_LINK_FAQ, null) != null) {
						sql = "UPDATE YukonGroupRole SET Value = '" + faqLink + "'" +
								" WHERE GroupID = " + customerGroups[i].getGroupID() +
								" AND RoleID = " + ResidentialCustomerRole.ROLEID +
								" AND RolePropertyID = " + ResidentialCustomerRole.WEB_LINK_FAQ;
						stmt.setSQLString( sql );
						stmt.execute();
						
						ServerUtils.handleDBChange( customerGroups[i], DBChangeMsg.CHANGE_TYPE_UPDATE );
					}
				}
	        	
				for (int i = 0; i < operatorGroups.length; i++) {
					if (AuthFuncs.getRolePropValueGroup(operatorGroups[i], ConsumerInfoRole.WEB_LINK_FAQ, null) != null) {
						sql = "UPDATE YukonGroupRole SET Value = '" + faqLink + "'" +
								" WHERE GroupID = " + operatorGroups[i].getGroupID() +
								" AND RoleID = " + ConsumerInfoRole.ROLEID +
								" AND RolePropertyID = " + ConsumerInfoRole.WEB_LINK_FAQ;
						stmt.setSQLString( sql );
						stmt.execute();
					}
	        		
					ServerUtils.handleDBChange( operatorGroups[i], DBChangeMsg.CHANGE_TYPE_UPDATE );
				}
			}
        	
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "FAQ link updated successfully");
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update FAQ link");
			redirect = referer;
		}
	}
	
	private void updateCustomerFAQSubjects(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			StarsEnergyCompanySettings ecSettings =
					(StarsEnergyCompanySettings) session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsCustomerFAQs starsFAQs = ecSettings.getStarsCustomerFAQs();
			
			String[] subjectIDs = req.getParameterValues("SubjectIDs");
			
			for (int i = 0; i < subjectIDs.length; i++) {
				int subjectID = Integer.parseInt( subjectIDs[i] );
				YukonListEntry subject = YukonListFuncs.getYukonListEntry( subjectID );
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
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update FAQ subjects");
			redirect = referer;
		}
	}
	
	private void updateCustomerFAQs(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			StarsEnergyCompanySettings ecSettings =
					(StarsEnergyCompanySettings) session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsCustomerFAQs starsFAQs = ecSettings.getStarsCustomerFAQs();
			
			int subjectID = Integer.parseInt( req.getParameter("SubjectID") );
			boolean newGroup = (subjectID == -1);
			
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
				
				entry = (com.cannontech.database.db.constants.YukonListEntry)
						Transaction.createTransaction( Transaction.INSERT, entry ).execute();
				
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
							
							Transaction.createTransaction( Transaction.DELETE, faq ).execute();
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
				
				YukonListEntry cEntry = YukonListFuncs.getYukonListEntry( subjectID );
				if (!cEntry.getEntryText().equals( subject )) {
					com.cannontech.database.db.constants.YukonListEntry entry = StarsLiteFactory.createYukonListEntry( cEntry );
					entry.setEntryText( subject );
					
					entry = (com.cannontech.database.db.constants.YukonListEntry)
							Transaction.createTransaction( Transaction.UPDATE, entry ).execute();
					
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
					
					faq = (com.cannontech.database.db.stars.CustomerFAQ)
							Transaction.createTransaction( Transaction.INSERT, faq ).execute();
					
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
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update customer FAQs");
			redirect = referer;
		}
	}
	
	private void deleteFAQSubject(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			StarsEnergyCompanySettings ecSettings =
					(StarsEnergyCompanySettings) session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsCustomerFAQs starsFAQs = ecSettings.getStarsCustomerFAQs();
			
			int subjectID = Integer.parseInt( req.getParameter("SubjectID") );
			boolean deleteAll = (subjectID == -1);
			
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
							
							Transaction.createTransaction( Transaction.DELETE, faq ).execute();
							it.remove();
						}
					}
				}
				
				YukonListEntry cEntry = YukonListFuncs.getYukonListEntry( starsGroup.getSubjectID() );
				com.cannontech.database.db.constants.YukonListEntry entry =
						StarsLiteFactory.createYukonListEntry( cEntry );
				
				Transaction.createTransaction( Transaction.DELETE, entry ).execute();
				
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
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete service company");
			redirect = referer;
		}
	}
	
	private void updateInterviewQuestions(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			StarsEnergyCompanySettings ecSettings =
					(StarsEnergyCompanySettings) session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
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
	
	/**
	 * Update entries of a customer selection list. The entryData parameter
	 * is an array of {entryID(Integer), entryText(String), yukDefID(Integer)}
	 */
	public static void updateYukonListEntries(YukonSelectionList cList, Object[][] entryData, LiteStarsEnergyCompany energyCompany)
		throws WebClientException, java.sql.SQLException
	{
		java.sql.Connection conn = null;
		boolean autoCommit = true;
		
		try {
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
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
						CTILogger.error( e.getMessage(), e );
						conn.rollback();
						throw new WebClientException("Cannot delete substation with id = " + entryID + ", make sure it is not referenced", e);
					}
				}
				
				conn.commit();
				
				// Order the substation list alphabetically
				Collections.sort( newEntries, ServerUtils.YUK_LIST_ENTRY_ALPHA_CMPTR );
				
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
						CTILogger.error( e.getMessage(), e );
						conn.rollback();
						throw new WebClientException("Cannot delete list entry with id = " + entryID + ", make sure it is not referenced", e);
					}
				}
				
				conn.commit();
				
				// Sort the entry list by the ordering specified in the selection list
				if (cList.getOrdering().equalsIgnoreCase("A"))
					Collections.sort( newEntries, ServerUtils.YUK_LIST_ENTRY_ALPHA_CMPTR );
				
				// Update the constant objects
				Properties cListEntries = YukonListFuncs.getYukonListEntries();
				synchronized (cListEntries) {
					for (int i = 0; i < cList.getYukonListEntries().size(); i++) {
						YukonListEntry entry = (YukonListEntry) cList.getYukonListEntries().get(i);
						YukonListFuncs.getYukonListEntries().remove( new Integer(entry.getEntryID()) );
					}
					
					for (int i = 0; i < newEntries.size(); i++) {
						YukonListEntry entry = (YukonListEntry) newEntries.get(i);
						YukonListFuncs.getYukonListEntries().put( new Integer(entry.getEntryID()), entry );
					}
				}
				
				cList.setYukonListEntries( newEntries );
			}
		}
		finally {
			if (conn != null) {
				conn.setAutoCommit( autoCommit );
				conn.close();
			}
		}
	}
	
	private void updateCustomerSelectionList(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			String listName = req.getParameter("ListName");
			String ordering = req.getParameter("Ordering");
			String label = req.getParameter("Label");
			String whereIsList = req.getParameter("WhereIsList");
			
			String[] entryIDs = req.getParameterValues("EntryIDs");
			String[] entryTexts = req.getParameterValues("EntryTexts");
			String[] yukonDefIDs = req.getParameterValues("YukonDefIDs");
			
			YukonSelectionList cList = energyCompany.getYukonSelectionList( listName );
			
			if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD_CUS)) {
				// Handle customer opt out period list
				boolean sameAsOp = Boolean.valueOf( req.getParameter("SameAsOp") ).booleanValue();
				if (sameAsOp && cList == null) return;
				
				if (sameAsOp && cList != null) {
					// Remove the OptOutPeriodCustomer list
					com.cannontech.database.data.constants.YukonSelectionList list =
							new com.cannontech.database.data.constants.YukonSelectionList();
					list.setListID( new Integer(cList.getListID()) );
					
					Transaction.createTransaction( Transaction.DELETE, list ).execute();
					
					for (int i = 0; i < cList.getYukonListEntries().size(); i++) {
						YukonListEntry cEntry = (YukonListEntry) cList.getYukonListEntries().get(i);
						YukonListFuncs.getYukonListEntries().remove( new Integer(cEntry.getEntryID()) );
					}
					
					YukonListFuncs.getYukonSelectionLists().remove( new Integer(cList.getListID()) );
					energyCompany.deleteYukonSelectionList( cList.getListID() );
					energyCompany.updateStarsCustomerSelectionLists();
					
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
			
			if (!listName.equalsIgnoreCase(com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION)) {
				// Update yukon selection list
				com.cannontech.database.db.constants.YukonSelectionList list =
						StarsLiteFactory.createYukonSelectionList( cList );
				list.setOrdering( ordering );
				list.setSelectionLabel( label );
				list.setWhereIsList( whereIsList );
				
				list = (com.cannontech.database.db.constants.YukonSelectionList)
						Transaction.createTransaction( Transaction.UPDATE, list ).execute();
				
				StarsLiteFactory.setConstantYukonSelectionList( cList, list );
			}
			
			updateYukonListEntries( cList, entryData, energyCompany );
			
			energyCompany.updateStarsCustomerSelectionLists();
			
			if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE)) {
				energyCompany.updateStarsDefaultThermostatSchedules();
				
				if (user.getUserID() == energyCompany.getUserID() && user.getYukonUser().getStatus().equalsIgnoreCase(UserUtils.STATUS_FIRST_TIME)) {
					// Change the status of the default operator login from "FirstTime" to "Enabled"
					com.cannontech.database.db.user.YukonUser dbUser = (com.cannontech.database.db.user.YukonUser)
							StarsLiteFactory.createDBPersistent( user.getYukonUser() );
					dbUser.setStatus( UserUtils.STATUS_ENABLED );
					
					dbUser = (com.cannontech.database.db.user.YukonUser)
							Transaction.createTransaction( Transaction.UPDATE, dbUser ).execute();
					ServerUtils.handleDBChange( user.getYukonUser(), com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE );
				}
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
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			StarsThermostatTypes thermType = StarsThermostatTypes.valueOf( req.getParameter("type") );
			int hwTypeDefID = ECUtils.getLMHardwareTypeDefID(thermType).intValue();
        	
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
	
	private LiteYukonUser createOperatorLogin(String username, String password, String status, LiteYukonGroup[] operGroups,
		LiteStarsEnergyCompany energyCompany) throws Exception
	{
		if (YukonUserFuncs.getLiteYukonUser( username ) != null)
			throw new WebClientException( "Username already exists" );
		
		com.cannontech.database.data.user.YukonUser yukonUser = new com.cannontech.database.data.user.YukonUser();
		com.cannontech.database.db.user.YukonUser userDB = yukonUser.getYukonUser();
		
		userDB.setUsername( username );
		userDB.setPassword( password );
		userDB.setStatus( status );
		
		for (int i = 0; i < operGroups.length; i++) {
			com.cannontech.database.db.user.YukonGroup group =
					new com.cannontech.database.db.user.YukonGroup();
			group.setGroupID( new Integer(operGroups[i].getGroupID()) );
			yukonUser.getYukonGroups().add( group );
		}
		
		yukonUser = (com.cannontech.database.data.user.YukonUser)
				Transaction.createTransaction(Transaction.INSERT, yukonUser).execute();
		
		if (energyCompany != null) {
			SqlStatement stmt = new SqlStatement(
					"INSERT INTO EnergyCompanyOperatorLoginList VALUES(" +
						energyCompany.getEnergyCompanyID() + ", " + userDB.getUserID() + ")",
					CtiUtilities.getDatabaseAlias()
					);
			stmt.execute();
			
			ArrayList operLoginIDs = energyCompany.getOperatorLoginIDs();
			synchronized (operLoginIDs) {
				if (!operLoginIDs.contains( userDB.getUserID() ))
					operLoginIDs.add(userDB.getUserID());
			}
		}
		
		LiteYukonUser liteUser = new LiteYukonUser(
				userDB.getUserID().intValue(),
				userDB.getUsername(),
				userDB.getPassword(),
				userDB.getStatus()
				);
		ServerUtils.handleDBChange( liteUser, DBChangeMsg.CHANGE_TYPE_ADD );
		
		return liteUser;
	}
	
	private void updateOperatorLogin(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			int userID = Integer.parseInt( req.getParameter("UserID") );
			String username = req.getParameter( "Username" );
			String password = req.getParameter( "Password" );
			boolean enabled = Boolean.valueOf( req.getParameter("Status") ).booleanValue();
			String status = (enabled)? UserUtils.STATUS_ENABLED : UserUtils.STATUS_DISABLED;
			
			if (userID == -1) {
				// Create new operator login
				LiteYukonGroup liteGroup = AuthFuncs.getGroup( Integer.parseInt(req.getParameter("OperatorGroup")) );
				LiteYukonUser liteUser = createOperatorLogin( username, password, status,
						new LiteYukonGroup[] { liteGroup }, energyCompany );
				
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Operator login created successfully" );
				redirect += "?UserID=" + liteUser.getUserID();
			}
			else {
				LiteYukonUser liteUser = YukonUserFuncs.getLiteYukonUser( userID );
				if (!username.equals( liteUser.getUsername() ) && YukonUserFuncs.getLiteYukonUser( username ) != null)
					throw new WebClientException( "Username '" + username + "' already exists" );
				
				com.cannontech.database.db.user.YukonUser dbUser = (com.cannontech.database.db.user.YukonUser)
						StarsLiteFactory.createDBPersistent( liteUser );
				dbUser.setUsername( username );
				dbUser.setPassword( password );
				dbUser.setStatus( (enabled)? UserUtils.STATUS_ENABLED : UserUtils.STATUS_DISABLED );
				
				dbUser = (com.cannontech.database.db.user.YukonUser)
						Transaction.createTransaction( Transaction.UPDATE, dbUser ).execute();
				ServerUtils.handleDBChange( liteUser, com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE );
				
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
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
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
					throw new WebClientException( "Operator group '" + groupName + "' doesn't exist");
				
				if (i == 0)
					operGroupIDs += String.valueOf( group.getGroupID() );
				else
					operGroupIDs += "," + String.valueOf( group.getGroupID() );
				if (i == 0) operGroup = group;
			}
			
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
			
			if (YukonUserFuncs.getLiteYukonUser( req.getParameter("Username") ) != null)
				throw new WebClientException( "Username of default operator login already exists" );
			
			if (req.getParameter("Username2").length() > 0 &&
				YukonUserFuncs.getLiteYukonUser( req.getParameter("Username2") ) != null)
				throw new WebClientException( "Username of second operator login already exists" );
			
			// Create a privilege group with EnergyCompany and Administrator role
			String dftOperGroupName = (operGroupNames.length > 0)?
					operGroupNames[0] + " Admin Grp" : "Web Client Operators Admin Grp";
			
			if (AuthFuncs.getGroup( dftOperGroupName ) != null) {
				int num = 2;
				while (true) {
					String groupName = dftOperGroupName + num;
					if (AuthFuncs.getGroup( groupName ) == null) {
						dftOperGroupName = groupName;
						break;
					}
					num++;
				}
			}
			
			com.cannontech.database.data.user.YukonGroup dftGroup =
					new com.cannontech.database.data.user.YukonGroup();
			com.cannontech.database.db.user.YukonGroup dftGroupDB = dftGroup.getYukonGroup();
			
			dftGroupDB.setGroupName( dftOperGroupName );
			dftGroupDB.setGroupDescription( "Privilege group for the energy company's default operator login" );
			
			LiteYukonRoleProperty[] roleProps = RoleFuncs.getRoleProperties( EnergyCompanyRole.ROLEID );
			for (int i = 0; i < roleProps.length; i++) {
				com.cannontech.database.db.user.YukonGroupRole groupRole =
						new com.cannontech.database.db.user.YukonGroupRole();
				
				groupRole.setRoleID( new Integer(EnergyCompanyRole.ROLEID) );
				groupRole.setRolePropertyID( new Integer(roleProps[i].getRolePropertyID()) );
				if (roleProps[i].getRolePropertyID() == EnergyCompanyRole.CUSTOMER_GROUP_IDS)
					groupRole.setValue( custGroupIDs );
				else if (roleProps[i].getRolePropertyID() == EnergyCompanyRole.OPERATOR_GROUP_IDS)
					groupRole.setValue( operGroupIDs );
				else
					groupRole.setValue( CtiUtilities.STRING_NONE );
				
				dftGroup.getYukonGroupRoles().add( groupRole );
			}
			
			roleProps = RoleFuncs.getRoleProperties( AdministratorRole.ROLEID );
			for (int i = 0; i < roleProps.length; i++) {
				com.cannontech.database.db.user.YukonGroupRole groupRole =
						new com.cannontech.database.db.user.YukonGroupRole();
				
				groupRole.setRoleID( new Integer(AdministratorRole.ROLEID) );
				groupRole.setRolePropertyID( new Integer(roleProps[i].getRolePropertyID()) );
				if (roleProps[i].getRolePropertyID() == AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY)
					groupRole.setValue( CtiUtilities.TRUE_STRING );
				else
					groupRole.setValue( CtiUtilities.STRING_NONE );
				
				dftGroup.getYukonGroupRoles().add( groupRole );
			}
			
			dftGroup = (com.cannontech.database.data.user.YukonGroup)
					Transaction.createTransaction(Transaction.INSERT, dftGroup).execute();
			
			LiteYukonGroup liteDftGroup = new LiteYukonGroup( dftGroup.getGroupID().intValue() );
			ServerUtils.handleDBChange( liteDftGroup, DBChangeMsg.CHANGE_TYPE_ADD );
			
			// Create the default operator login
			LiteYukonGroup[] operGroups = (operGroup != null)?
					new LiteYukonGroup[] { operGroup, liteDftGroup } : new LiteYukonGroup[] { liteDftGroup };
			LiteYukonUser liteUser = createOperatorLogin(
					req.getParameter("Username"), req.getParameter("Password"), UserUtils.STATUS_FIRST_TIME, operGroups, null );
			
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
			
			com.cannontech.database.db.customer.Address addr =
					new com.cannontech.database.db.customer.Address();
			addr.setStateCode( " " );
			contact.setAddress( addr );
			
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
			SOAPServer.addEnergyCompany( newCompany );
			ServerUtils.handleDBChange( newCompany, DBChangeMsg.CHANGE_TYPE_ADD );
			
			// Create login for the second operator
			if (req.getParameter("Username2").length() > 0) {
				operGroups = (operGroup != null)?
						new LiteYukonGroup[] { operGroup } : new LiteYukonGroup[0];
				createOperatorLogin( req.getParameter("Username2"), req.getParameter("Password2"), UserUtils.STATUS_ENABLED, operGroups, newCompany );
			}
			
			// Assign default route to the energy company
			int routeID = Integer.parseInt( req.getParameter("Route") );
			updateDefaultRoute( newCompany, routeID );
			
			// Add the new energy company as a member of the current company
			if (req.getParameter("AddMember") != null) {
				LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
				addMember( energyCompany, newCompany.getLiteID(), liteUser.getUserID() );
			}
			
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
		DeleteEnergyCompanyTask task = new DeleteEnergyCompanyTask(user);
		long id = ProgressChecker.addTask( task );
		
		// Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
		for (int i = 0; i < 5; i++) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {}
			
			if (task.getStatus() == DeleteEnergyCompanyTask.STATUS_FINISHED) {
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
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}
	
	public static void removeRoute(LiteStarsEnergyCompany energyCompany, int routeID)
		throws TransactionException
	{
		ArrayList routeIDs = energyCompany.getRouteIDs();
		Integer rtID = new Integer(routeID);
		if (!routeIDs.contains( rtID )) return;
		
		ArrayList inventory = energyCompany.loadAllInventory();
		synchronized (inventory) {
			for (int i = 0; i < inventory.size(); i++) {
				if (!(inventory.get(i) instanceof LiteStarsLMHardware)) continue;
				
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) inventory.get(i);
				if (liteHw.getRouteID() == routeID) {
					com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
							new com.cannontech.database.data.stars.hardware.LMHardwareBase();
					StarsLiteFactory.setLMHardwareBase( hw, liteHw );
					hw.getLMHardwareBase().setRouteID( new Integer(CtiUtilities.NONE_ID) );
					
					Transaction.createTransaction( Transaction.UPDATE, hw.getLMHardwareBase() ).execute();
					liteHw.setRouteID( CtiUtilities.NONE_ID );
					
					if (liteHw.getAccountID() > 0) {
						StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteHw.getAccountID() );
						if (starsAcctInfo != null) {
							StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteHw, energyCompany );
							UpdateLMHardwareAction.parseResponse( liteHw.getInventoryID(), starsInv, starsAcctInfo, null );
						}
					}
				}
			}
		}
		
		ECToGenericMapping map = new ECToGenericMapping();
		map.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
		map.setItemID( rtID );
		map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_ROUTE );
		Transaction.createTransaction( Transaction.DELETE, map ).execute();
		
		synchronized (routeIDs) { routeIDs.remove(rtID); }
	}
	
	private void updateRouteList(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
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
				removeRoute( energyCompany, routeID );
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
		
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		LiteStarsEnergyCompany member = SOAPServer.getEnergyCompany( memberID );
		
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
	
	public static void addMember(LiteStarsEnergyCompany energyCompany, int memberID, int loginID) throws Exception {
		ECToGenericMapping map = new ECToGenericMapping();
		map.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
		map.setItemID( new Integer(memberID) );
		map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER );
		Transaction.createTransaction( Transaction.INSERT, map ).execute();
		
		LiteStarsEnergyCompany member = SOAPServer.getEnergyCompany( memberID );
		ArrayList members = energyCompany.getChildren();
		synchronized (members) { members.add(member); }
		
		if (loginID != -1) {
			map.setItemID( new Integer(loginID) );
			map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER_LOGIN );
			Transaction.createTransaction( Transaction.INSERT, map ).execute();
			
			ArrayList loginIDs = energyCompany.getMemberLoginIDs();
			synchronized (loginIDs) { loginIDs.add(new Integer(loginID)); }
		}
	}
	
	private void addMemberEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			int memberID = Integer.parseInt( req.getParameter("MemberID") );
			int loginID = Integer.parseInt( req.getParameter("LoginID") );
			addMember( energyCompany, memberID, loginID );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to add new member");
		}
	}
	
	private void updateMemberEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		int memberID = Integer.parseInt( req.getParameter("MemberID") );
		int loginID = Integer.parseInt( req.getParameter("LoginID") );
		
		ArrayList loginIDs = energyCompany.getMemberLoginIDs();
		Integer prevLoginID = null;
		
		synchronized (loginIDs) {
			for (int i = 0; i < loginIDs.size(); i++) {
				Integer id = (Integer) loginIDs.get(i);
				LiteYukonUser liteUser = YukonUserFuncs.getLiteYukonUser( id.intValue() );
				if (EnergyCompanyFuncs.getEnergyCompany( liteUser ).getEnergyCompanyID() == memberID) {
					prevLoginID = id;
					break;
				}
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
					
					synchronized (loginIDs) {
						loginIDs.remove( prevLoginID );
						loginIDs.add( new Integer(loginID) );
					}
				}
				else {
					ECToGenericMapping map = new ECToGenericMapping();
					map.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
					map.setItemID( prevLoginID );
					map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER_LOGIN );
					Transaction.createTransaction( Transaction.DELETE, map ).execute();
					
					synchronized (loginIDs) { loginIDs.remove(prevLoginID); }
				}
			}
			else {
				if (loginID == -1) return;
				
				ECToGenericMapping map = new ECToGenericMapping();
				map.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				map.setItemID( new Integer(loginID) );
				map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER_LOGIN );
				Transaction.createTransaction( Transaction.INSERT, map ).execute();
				
				synchronized (loginIDs) { loginIDs.add(new Integer(loginID)); }
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update member login");
		}
	}
	
	public static void removeMember(LiteStarsEnergyCompany energyCompany, int memberID) throws Exception {
		ArrayList members = energyCompany.getChildren();
		ArrayList loginIDs = energyCompany.getMemberLoginIDs();
		
		synchronized (members) {
			Iterator it = members.iterator();
			while (it.hasNext()) {
				LiteStarsEnergyCompany member = (LiteStarsEnergyCompany) it.next();
				if (memberID != -1 && member.getLiteID() != memberID) continue;
				
				ECToGenericMapping map = new ECToGenericMapping();
				map.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				map.setItemID( member.getEnergyCompanyID() );
				map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER );
				Transaction.createTransaction( Transaction.DELETE, map ).execute();
				
				it.remove();
				
				synchronized (loginIDs) {
					for (int i = 0; i < loginIDs.size(); i++) {
						Integer loginID = (Integer) loginIDs.get(i);
						LiteYukonUser liteUser = YukonUserFuncs.getLiteYukonUser( loginID.intValue() );
						
						if (EnergyCompanyFuncs.getEnergyCompany( liteUser ).getEnergyCompanyID() == member.getLiteID()) {
							map.setItemID( loginID );
							map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER_LOGIN );
							Transaction.createTransaction( Transaction.DELETE, map ).execute();
							
							loginIDs.remove(i);
							break;
						}
					}
				}
			}
		}
	}
	
	private void removeMemberEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			int memberID = Integer.parseInt( req.getParameter("MemberID") );
			removeMember( energyCompany, memberID );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to remove member(s)");
		}
	}
	
}
