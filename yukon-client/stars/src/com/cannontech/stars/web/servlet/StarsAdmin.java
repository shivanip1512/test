package com.cannontech.stars.web.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.*;
import com.cannontech.common.util.*;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.*;
import com.cannontech.database.data.lite.*;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.servlet.LCConnectionServlet;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.*;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.web.loadcontrol.LoadcontrolCache;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsAdmin extends HttpServlet {

    private static final String loginURL = "/login.jsp";
	
	private static final int IDX_ACCOUNT_NO = 0;
	private static final int IDX_STREET_ADDR1 = 1;
	private static final int IDX_STREET_ADDR2 = 2;
	private static final int IDX_CITY = 3;
	private static final int IDX_STATE = 4;
	private static final int IDX_ZIP_CODE = 5;
	private static final int IDX_LAST_NAME = 6;
	private static final int IDX_FIRST_NAME = 7;
	private static final int IDX_HOME_PHONE = 8;
	private static final int IDX_WORK_PHONE = 9;
	private static final int IDX_EMAIL = 10;
	private static final int IDX_SERIAL_NO = 11;
	private static final int IDX_INSTALL_DATE = 12;
	private static final int IDX_DEVICE_TYPE = 13;
	private static final int IDX_USERNAME = 14;
	private static final int IDX_PASSWORD = 15;
	private static final int IDX_SERVICE_COMPANY = 16;
	private static final int NUM_FIELDS = 17;
    
    private static final String NEW_ADDRESS = "NEW_ADDRESS";
	
	/**
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null) {
        	resp.sendRedirect( loginURL ); return;
        }
        
        SOAPClient.initSOAPServer( req );
        
        StarsYukonUser user = (StarsYukonUser)
        		session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
        if (user == null) {
        	resp.sendRedirect( loginURL ); return;
        }
        
    	String referer = req.getHeader( "referer" );
		String action = req.getParameter( "action" );
		if (action == null) action = "";
		
		if (action.equalsIgnoreCase( "DeleteCustAccounts" ))
			deleteCustomerAccounts( user, req, session );
		else if (action.equalsIgnoreCase( "ImportCustAccounts" ))
			importCustomerAccounts( user, req, session );
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
		else if (action.equalsIgnoreCase("UpdateFAQSubjects"))
			updateCustomerFAQSubjects( user, req, session );
        
    	resp.sendRedirect( referer );
	}
	
	/** Idaho Power Customer Information
	 * COL_NUM:	COL_NAME
	 * 1		----
	 * 2		LASTNAME
	 * 3		FIRSTNAME
	 * 4		----
	 * 5,6		STREETADDR1
	 * 7		CITY
	 * 8		STATE
	 * 9		ZIPCODE
	 * 10		HOMEPHONE
	 * 11		ACCOUNTNO
	 * 12		SERIALNO(THERMOSTAT)
	 * 13		INSTALLDATE
	 * 14		SERVICECOMPANY
	 */
	private String[] populateFieldsIdaho(String line) throws Exception {
		String[] fields = new String[ NUM_FIELDS ];
		for (int i = 0; i < NUM_FIELDS; i++)
			fields[i] = "";
			
		StreamTokenizer st = new StreamTokenizer( new StringReader(line) );
		st.resetSyntax();
		st.ordinaryChar( ',' );
		st.quoteChar( '"' );
		st.wordChars( 'a', 'z' );
		st.wordChars( 'A', 'Z' );
		st.wordChars( '0', '9' );
		st.wordChars( ' ', ' ' );
		st.wordChars( '-', '-' );
		st.wordChars( '/', '/' );
		
		st.nextToken();
		if (st.ttype != ',') st.nextToken();
		st.nextToken();
		if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_LAST_NAME] = st.sval;
		if (st.ttype != ',') st.nextToken();
		st.nextToken();
		if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_FIRST_NAME] = st.sval;
		if (st.ttype != ',') st.nextToken();
		st.nextToken();
		if (st.ttype != ',') st.nextToken();
		st.nextToken();
		if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_STREET_ADDR1] = st.sval;
		if (st.ttype != ',') st.nextToken();
		st.nextToken();
		if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
			fields[IDX_STREET_ADDR1] += " " + st.sval;
		if (st.ttype != ',') st.nextToken();
		st.nextToken();
		if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_CITY] = st.sval;
		if (st.ttype != ',') st.nextToken();
		st.nextToken();
		if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_STATE] = st.sval;
		if (st.ttype != ',') st.nextToken();
		st.nextToken();
		if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ZIP_CODE] = st.sval;
		if (st.ttype != ',') st.nextToken();
		st.nextToken();
		if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_HOME_PHONE] = st.sval;
		if (st.ttype != ',') st.nextToken();
		st.nextToken();
		if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_NO] = st.sval;
		if (st.ttype != ',') st.nextToken();
		st.nextToken();
		if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_SERIAL_NO] = st.sval;
		if (st.ttype != ',') st.nextToken();
		st.nextToken();
		if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_INSTALL_DATE] = st.sval;
		if (st.ttype != ',') st.nextToken();
		st.nextToken();
		if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_SERVICE_COMPANY] = st.sval;
		
		fields[IDX_USERNAME] = fields[IDX_FIRST_NAME].substring(0,1).toLowerCase()
							 + fields[IDX_LAST_NAME].toLowerCase();
		fields[IDX_PASSWORD] = fields[IDX_SERIAL_NO];
		fields[IDX_DEVICE_TYPE] = "Thermostat";
		if (fields[IDX_SERVICE_COMPANY].equalsIgnoreCase("Western"))
			fields[IDX_SERVICE_COMPANY] = "Western Heating";
		else if (fields[IDX_SERVICE_COMPANY].equalsIgnoreCase("Ridgeway"))
			fields[IDX_SERVICE_COMPANY] = "Ridgeway Industrial";
		else if (fields[IDX_SERVICE_COMPANY].equalsIgnoreCase("Access"))
			fields[IDX_SERVICE_COMPANY] = "Access Heating";
		
		return fields;
	}
	
	private boolean searchCustomerAccount(String[] fields, LiteStarsEnergyCompany energyCompany, HttpSession session)
		throws Exception {
        StarsSearchCustomerAccount searchAccount = new StarsSearchCustomerAccount();
        SearchBy searchBy = new SearchBy();
        searchBy.setEntryID( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_ACCT_NO).getEntryID() );
        searchAccount.setSearchBy( searchBy );
        searchAccount.setSearchValue( fields[IDX_ACCOUNT_NO] );
        
        StarsOperation operation = new StarsOperation();
        operation.setStarsSearchCustomerAccount( searchAccount );
        SOAPMessage reqMsg = SOAPUtil.buildSOAPMessage( operation );
        
        SearchCustAccountAction action = new SearchCustAccountAction();
		SOAPMessage respMsg = action.process( reqMsg, session );
		
		int status = action.parse( reqMsg, respMsg, session );
		if (status != 0) {
			if (status == StarsConstants.FAILURE_CODE_OPERATION_FAILED) {
				session.removeAttribute( ServletUtils.ATT_ERROR_MESSAGE );
				return false;
			}
			else
				throw new Exception( "Failed to process response message for NewCustAccountAction" );
		}
		
		return true;
	}
	
	private void setStarsCustAccount(StarsCustAccount account, String[] fields, LiteStarsEnergyCompany energyCompany) {
        account.setAccountNumber( fields[IDX_ACCOUNT_NO] );
        account.setIsCommercial( false );
        account.setCompany( "" );
        account.setAccountNotes( "" );
        account.setPropertyNumber( "" );
        account.setPropertyNotes( "" );

        StreetAddress propAddr = new StreetAddress();
        propAddr.setStreetAddr1( fields[IDX_STREET_ADDR1] );
        propAddr.setStreetAddr2( fields[IDX_STREET_ADDR2] );
        propAddr.setCity( fields[IDX_CITY] );
        propAddr.setState( fields[IDX_STATE] );
        propAddr.setZip( fields[IDX_ZIP_CODE] );
        propAddr.setCounty( "" );
        account.setStreetAddress( propAddr );

		Substation starsSub = new Substation();
		YukonSelectionList subList = energyCompany.getYukonSelectionList( com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION );
		if (subList.getYukonListEntries().size() == 0)
			starsSub.setEntryID( CtiUtilities.NONE_ID );
		else
			starsSub.setEntryID( ((YukonListEntry)subList.getYukonListEntries().get(0)).getEntryID() );
		
        StarsSiteInformation siteInfo = new StarsSiteInformation();
        siteInfo.setSubstation( starsSub );
        siteInfo.setFeeder( "" );
        siteInfo.setPole( "" );
        siteInfo.setTransformerSize( "" );
        siteInfo.setServiceVoltage( "" );
        account.setStarsSiteInformation( siteInfo );

        BillingAddress billAddr = new BillingAddress();
        billAddr.setStreetAddr1( fields[IDX_STREET_ADDR1] );
        billAddr.setStreetAddr2( fields[IDX_STREET_ADDR2] );
        billAddr.setCity( fields[IDX_CITY] );
        billAddr.setState( fields[IDX_STATE] );
        billAddr.setZip( fields[IDX_ZIP_CODE] );
        account.setBillingAddress( billAddr );

        PrimaryContact primContact = new PrimaryContact();
        primContact.setLastName( fields[IDX_LAST_NAME] );
        primContact.setFirstName( fields[IDX_FIRST_NAME] );
        primContact.setHomePhone( ServletUtils.formatPhoneNumber(fields[IDX_HOME_PHONE]) );
        primContact.setWorkPhone( ServletUtils.formatPhoneNumber(fields[IDX_WORK_PHONE]) );
        
        Email email = new Email();
        email.setNotification( fields[IDX_EMAIL] );
        email.setEnabled( false );
        primContact.setEmail( email );
        account.setPrimaryContact( primContact );
	}
	
	private void newCustomerAccount(String[] fields, LiteStarsEnergyCompany energyCompany, HttpSession session)
		throws Exception {
		// Build the request message
		StarsNewCustomerAccount newAccount = new StarsNewCustomerAccount();
		
		StarsCustomerAccount account = new StarsCustomerAccount();
		setStarsCustAccount( account, fields, energyCompany );
		newAccount.setStarsCustomerAccount( account );

		if (fields[IDX_USERNAME].trim().length() > 0) {
			StarsUpdateLogin login = new StarsUpdateLogin();
			login.setUsername( fields[IDX_USERNAME] );
			login.setPassword( fields[IDX_PASSWORD] );
			newAccount.setStarsUpdateLogin( login );
		}
		
        StarsOperation operation = new StarsOperation();
        operation.setStarsNewCustomerAccount( newAccount );
		SOAPMessage reqMsg = SOAPUtil.buildSOAPMessage( operation );
		
		NewCustAccountAction action = new NewCustAccountAction();
		SOAPMessage respMsg = action.process( reqMsg, session );
		
		int status = action.parse( reqMsg, respMsg, session );
		if (status != 0) {
			if (status == StarsConstants.FAILURE_CODE_OPERATION_FAILED) {
				String errorMsg = (String) session.getAttribute( ServletUtils.ATT_ERROR_MESSAGE );
				session.removeAttribute( ServletUtils.ATT_ERROR_MESSAGE );
				throw new Exception( errorMsg );
			}
			else
				throw new Exception( "Failed to process response message for NewCustAccountAction" );
		}
	}
	
	private void updateCustomerAccount(String[] fields, LiteStarsEnergyCompany energyCompany, HttpSession session)
		throws Exception {
        StarsUpdateCustomerAccount updateAccount = new StarsUpdateCustomerAccount();
        setStarsCustAccount( updateAccount, fields, energyCompany );

        StarsOperation operation = new StarsOperation();
        operation.setStarsUpdateCustomerAccount( updateAccount );
        SOAPMessage reqMsg = SOAPUtil.buildSOAPMessage( operation );
        
        UpdateCustAccountAction action = new UpdateCustAccountAction();
		SOAPMessage respMsg = action.process( reqMsg, session );
		
		int status = action.parse( reqMsg, respMsg, session );
		if (status != 0) {
			if (status == StarsConstants.FAILURE_CODE_OPERATION_FAILED) {
				String errorMsg = (String) session.getAttribute( ServletUtils.ATT_ERROR_MESSAGE );
				session.removeAttribute( ServletUtils.ATT_ERROR_MESSAGE );
				throw new Exception( errorMsg );
			}
			else
				throw new Exception( "Failed to process response message for UpdateCustAccountAction" );
		}
	}
	
	private void updateLogin(String[] fields, StarsYukonUser user, HttpSession session)
		throws Exception {
    	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
		if (liteAcctInfo == null)
			throw new Exception( "Cannot find customer account information!" );
		
		if (liteAcctInfo.getCustomerAccount().getLoginID() == com.cannontech.user.UserUtils.USER_YUKON_ID
			&& fields[IDX_USERNAME].trim().length() == 0)
			return;
			
        StarsUpdateLogin updateLogin = new StarsUpdateLogin();
        updateLogin.setUsername( fields[IDX_USERNAME] );
        updateLogin.setPassword( fields[IDX_PASSWORD] );
        
        StarsOperation operation = new StarsOperation();
        operation.setStarsUpdateLogin( updateLogin );
        SOAPMessage reqMsg = SOAPUtil.buildSOAPMessage( operation );
        
        UpdateLoginAction action = new UpdateLoginAction();
		SOAPMessage respMsg = action.process( reqMsg, session );
		
		int status = action.parse( reqMsg, respMsg, session );
		if (status != 0) {
			if (status == StarsConstants.FAILURE_CODE_OPERATION_FAILED) {
				String errorMsg = (String) session.getAttribute( ServletUtils.ATT_ERROR_MESSAGE );
				session.removeAttribute( ServletUtils.ATT_ERROR_MESSAGE );
				throw new Exception( errorMsg );
			}
			else
				throw new Exception( "Failed to process response message for UpdateLoginAction" );
		}
	}
	
	private void setStarsLMHardware(StarsLMHw hardware, String[] fields, LiteStarsEnergyCompany energyCompany) {
		LMDeviceType type = new LMDeviceType();
		YukonSelectionList devTypeList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
		type.setEntryID( CtiUtilities.NONE_ID );
		type.setContent( "(none)" );
		for (int i = 0; i < devTypeList.getYukonListEntries().size(); i++) {
			YukonListEntry entry = (YukonListEntry) devTypeList.getYukonListEntries().get(i);
			if (entry.getEntryText().equalsIgnoreCase( fields[IDX_DEVICE_TYPE] )) {
				type.setEntryID( entry.getEntryID() );
				type.setContent( entry.getEntryText() );
				break;
			}
		}
		hardware.setLMDeviceType( type );
		
		Voltage volt = new Voltage();
		YukonSelectionList voltList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE );
		if (voltList.getYukonListEntries().size() == 0)
			volt.setEntryID( CtiUtilities.NONE_ID );
		else
			volt.setEntryID( ((YukonListEntry)voltList.getYukonListEntries().get(0)).getEntryID() );
		hardware.setVoltage( volt );
		
		InstallationCompany company = new InstallationCompany();
		company.setEntryID( CtiUtilities.NONE_ID );
		ArrayList companies = energyCompany.getAllServiceCompanies();
		for (int i = 0; i < companies.size(); i++) {
			LiteServiceCompany entry = (LiteServiceCompany) companies.get(i);
			if (entry.getCompanyName().equalsIgnoreCase( fields[IDX_SERVICE_COMPANY] )) {
				company.setEntryID( entry.getCompanyID() );
				break;
			}
		}
		hardware.setInstallationCompany( company );
		
		hardware.setCategory( "" );
		hardware.setManufactureSerialNumber( fields[IDX_SERIAL_NO] );
		hardware.setAltTrackingNumber( "" );
		if (fields[IDX_INSTALL_DATE].length() > 0)
			hardware.setInstallDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(fields[IDX_INSTALL_DATE]) );
		hardware.setNotes( "" );
		hardware.setInstallationNotes( "" );
	}
	
	private void createLMHardware(String[] fields, StarsYukonUser user, HttpSession session)
		throws Exception {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		// Build request message
		StarsCreateLMHardware createHw = new StarsCreateLMHardware();
		setStarsLMHardware( createHw, fields, energyCompany );
		
		StarsOperation operation = new StarsOperation();
		operation.setStarsCreateLMHardware( createHw );
        SOAPMessage reqMsg = SOAPUtil.buildSOAPMessage( operation );
        
        CreateLMHardwareAction action = new CreateLMHardwareAction();
        SOAPMessage respMsg = action.process( reqMsg, session );
        
        int status = action.parse( reqMsg, respMsg, session );
		if (status != 0) {
			if (status == StarsConstants.FAILURE_CODE_OPERATION_FAILED) {
				String errorMsg = (String) session.getAttribute( ServletUtils.ATT_ERROR_MESSAGE );
				session.removeAttribute( ServletUtils.ATT_ERROR_MESSAGE );
				throw new Exception( errorMsg );
			}
			else
				throw new Exception( "Failed to process response message for CreateLMHardwareAction" );
		}
	}
	
	private boolean updateLMHardware(String[] fields, StarsYukonUser user, HttpSession session)
		throws Exception {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
    	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
		if (liteAcctInfo == null)
			throw new Exception( "Cannot find customer account information!" );
		
		if (liteAcctInfo.getInventories().size() != 1)
			throw new Exception( "Cannot determine the LM hardware to be updated" );
		int invID = ((Integer) liteAcctInfo.getInventories().get(0)).intValue();
		LiteLMHardwareBase liteHw = energyCompany.getLMHardware( invID, true );
		
		StarsUpdateLMHardware updateHw = new StarsUpdateLMHardware();
		setStarsLMHardware( updateHw, fields, energyCompany );
		updateHw.setInventoryID( invID );
		
		StarsOperation operation = new StarsOperation();
		operation.setStarsUpdateLMHardware( updateHw );
        SOAPMessage reqMsg = SOAPUtil.buildSOAPMessage( operation );
        
        UpdateLMHardwareAction action = new UpdateLMHardwareAction();
        SOAPMessage respMsg = action.process( reqMsg, session );
        
        int status = action.parse( reqMsg, respMsg, session );
		if (status != 0) {
			if (status == StarsConstants.FAILURE_CODE_OPERATION_FAILED) {
				String errorMsg = (String) session.getAttribute( ServletUtils.ATT_ERROR_MESSAGE );
				session.removeAttribute( ServletUtils.ATT_ERROR_MESSAGE );
				throw new Exception( errorMsg );
			}
			else
				throw new Exception( "Failed to process response message for UpdateLMHardwareAction" );
		}
		
		return !liteHw.getManufactureSerialNumber().equals( fields[IDX_SERIAL_NO] );
	}
	
	private void programSignUp(StarsYukonUser user, HttpSession session, HttpServletRequest req)
		throws Exception {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		// Build request message
		StarsProgramSignUp progSignUp = new StarsProgramSignUp();
		StarsSULMPrograms programs = new StarsSULMPrograms();
		progSignUp.setStarsSULMPrograms( programs );
		
		String[] catIDs = req.getParameterValues( "CatID" );
		String[] progIDs = req.getParameterValues( "ProgID" );
		if (progIDs != null) {
			for (int i = 0; i < progIDs.length; i++) {
				if (progIDs[i].length() == 0) continue;
				
				SULMProgram program = new SULMProgram();
				program.setProgramID( Integer.parseInt(progIDs[i]) );
				program.setApplianceCategoryID( Integer.parseInt(catIDs[i]) );
				programs.addSULMProgram( program );
			}
		}
		
		StarsOperation operation = new StarsOperation();
		operation.setStarsProgramSignUp( progSignUp );
        SOAPMessage reqMsg = SOAPUtil.buildSOAPMessage( operation );
        
        ProgramSignUpAction action = new ProgramSignUpAction();
        SOAPMessage respMsg = action.process( reqMsg, session );
        
        int status = action.parse( reqMsg, respMsg, session );
		if (status != 0) {
			if (status == StarsConstants.FAILURE_CODE_OPERATION_FAILED) {
				String errorMsg = (String) session.getAttribute( ServletUtils.ATT_ERROR_MESSAGE );
				session.removeAttribute( ServletUtils.ATT_ERROR_MESSAGE );
				throw new Exception( errorMsg );
			}
			else
				throw new Exception( "Failed to process response message for ProgramSignUpAction" );
		}
	}
	
	private void deleteCustomerAccounts(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			String acctNo = req.getParameter( "AcctNo" ).replace( '*', '%' );
			CustomerAccount[] accounts = CustomerAccount.searchByAccountNumber(
					energyCompany.getEnergyCompanyID(), acctNo );
			
			if (accounts != null) {
				for (int i = 0; i < accounts.length; i++) {
					LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation(
							accounts[i].getAccountID().intValue(), false );
							
					if (liteAcctInfo != null) {
			        	com.cannontech.database.data.stars.customer.CustomerAccount account =
			        			StarsLiteFactory.createCustomerAccount(liteAcctInfo, user.getEnergyCompanyID());
			        	Transaction.createTransaction(Transaction.DELETE, account).execute();
			        	
			        	int userID = liteAcctInfo.getCustomerAccount().getLoginID();
			        	if (userID > com.cannontech.user.UserUtils.USER_YUKON_ID)
			        		UpdateLoginAction.deleteLogin( null, userID );
			        	
			            energyCompany.deleteCustAccountInformation( liteAcctInfo );
			            energyCompany.deleteStarsCustAccountInformation( liteAcctInfo.getAccountID() );
			            ServerUtils.handleDBChange( liteAcctInfo, DBChangeMsg.CHANGE_TYPE_DELETE );
					}
					else {
			        	com.cannontech.database.data.stars.customer.CustomerAccount account =
			        			new com.cannontech.database.data.stars.customer.CustomerAccount();
			        	account.setAccountID( accounts[i].getAccountID() );
			        	account = (com.cannontech.database.data.stars.customer.CustomerAccount)
			        			Transaction.createTransaction(Transaction.RETRIEVE, account).execute();
			        	Transaction.createTransaction(Transaction.DELETE, account).execute();
			        	
			        	int userID = account.getCustomerAccount().getLoginID().intValue();
			        	if (userID > com.cannontech.user.UserUtils.USER_YUKON_ID)
			        		UpdateLoginAction.deleteLogin( null, userID );
					}
				}
				
				if (accounts.length > 1)
					session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, accounts.length + " customer accounts have been deleted" );
				else
					session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, accounts.length + " customer account has been deleted" );
			}
			else
	        	session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Search for account number failed");
		}
		catch (Exception e) {
			e.printStackTrace();
        	session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Delete customer accounts failed");
		}
	}
	
	private void importCustomerAccounts(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			String importFile = req.getParameter( "ImportFile" );
			String appCat = req.getParameter( "AppCat" );
			String progName = req.getParameter( "ProgName" );
			
			BufferedReader fr = new BufferedReader( new FileReader(importFile) );
			if (fr != null) {
				String line = null;
				int lineNo = 0;
				int numAdded = 0;
				int numUpdated = 0;
				int numFailed = 0;
				
				while ((line = fr.readLine()) != null) {
					lineNo++;
					try {
						String[] fields = populateFieldsIdaho( line );
						if (!searchCustomerAccount( fields, energyCompany, session )) {
							newCustomerAccount( fields, energyCompany, session );
							createLMHardware( fields, user, session );
							programSignUp( user, session, req );
							numAdded++;
						}
						else {
							updateCustomerAccount( fields, energyCompany, session );
							updateLogin( fields, user, session );
							boolean serialNoChanged = updateLMHardware( fields, user, session );
/*								
								boolean progEnrollChanged = false;
						    	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
								String[] catIDs = req.getParameterValues( "CatID" );
								String[] progIDs = req.getParameterValues( "ProgID" );
								
						    	if (progIDs != null) {
						    		int numProgEnrolled = 0;
					    			ArrayList programs = liteAcctInfo.getLmPrograms();
					    			for (int i = 0; i < progIDs.length; i++) {
					    				if (progIDs[i].length() == 0) continue;
					    				int progID = Integer.parseInt( progIDs[i] );
					    				numProgEnrolled++;
					    				
					    				boolean progFound = false;
					    				for (int j = 0; j < programs.size(); j++) {
						    				LiteStarsLMProgram liteProg = (LiteStarsLMProgram) programs.get(j);
						    				if (liteProg.getLmProgram().getProgramID() == progID) {
						    					progFound = true;
						    					break;
						    				}
					    				}
					    				if (!progFound) {
					    					progEnrollChanged = true;
					    					break;
					    				}
					    			}
					    			if (numProgEnrolled != programs.size()) progEnrollChanged = true;
						    	}
*/						    	
				    		programSignUp( user, session, req );
					    	numUpdated++;
						}
					}
					catch (Exception e2) {
						CTILogger.error( "Error encountered when processing line #" + lineNo );
						e2.printStackTrace();
						numFailed++;
					}
				}
				
				if (numAdded + numUpdated > 0) {
					StringBuffer confirmMsg = new StringBuffer();
					if (numAdded > 0)
						confirmMsg.append(numAdded).append(" customer accounts added");
					if (numUpdated > 0) {
						if (numAdded > 0) confirmMsg.append(", ");
						confirmMsg.append(numUpdated).append(" customer accounts updated");
					}
					session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, confirmMsg.toString());
				}
				if (numFailed > 0)
					session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, numFailed + " customer accounts failed");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Import customer accounts failed");
		}
	}
	
	private void updateAddress(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsCustomerAddress starsAddr = null;
			
			int addressID = Integer.parseInt( req.getParameter("AddressID") );
			boolean newAddress = (addressID == -1);
			String referer = req.getParameter( "REFERER" );
			
			if (referer.equalsIgnoreCase("Admin_EnergyCompany.jsp"))
				starsAddr = ecSettings.getStarsEnergyCompany().getCompanyAddress();
			else if (referer.startsWith("Admin_ServiceCompany.jsp")) {
				if (newAddress)
					starsAddr = new CompanyAddress();
				else {
					StarsServiceCompanies companies = ecSettings.getStarsServiceCompanies();
					for (int i = 0; i < companies.getStarsServiceCompanyCount(); i++) {
						if (companies.getStarsServiceCompany(i).getCompanyAddress().getAddressID() == addressID) {
							starsAddr = companies.getStarsServiceCompany(i).getCompanyAddress();
							break;
						}
					}
					if (starsAddr == null)
						throw new Exception ("Cannot find CompanyAddress object with addressID = " + addressID);
				}
			}
			
			starsAddr.setStreetAddr1( req.getParameter("StreetAddr1") );
			starsAddr.setStreetAddr2( req.getParameter("StreetAddr2") );
			starsAddr.setCity( req.getParameter("City") );
			starsAddr.setState( req.getParameter("State") );
			starsAddr.setZip( req.getParameter("Zip") );
			starsAddr.setCounty( req.getParameter("County") );
			
			if (newAddress) {
				// Just store the StarsCustomerAddress object in memory
				session.setAttribute( NEW_ADDRESS, starsAddr );
			}
			else {
				LiteAddress liteAddr = energyCompany.getAddress( starsAddr.getAddressID() );
	        	com.cannontech.database.db.customer.Address addr =
	        			(com.cannontech.database.db.customer.Address) StarsLiteFactory.createDBPersistent( liteAddr );
	        	StarsFactory.setCustomerAddress( addr, starsAddr );
	        	
	        	addr = (com.cannontech.database.db.customer.Address)
	        			Transaction.createTransaction( Transaction.UPDATE, addr ).execute();
	        	StarsLiteFactory.setLiteAddress( liteAddr, addr );
			}
        	
        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Address information updated successfully");
		}
		catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update address information");
		}
	}
	
	private void updateEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsEnergyCompany ec = ecSettings.getStarsEnergyCompany();
			
			ec.setCompanyName( req.getParameter("CompanyName") );
			ec.setMainPhoneNumber( req.getParameter("PhoneNo") );
			ec.setMainFaxNumber( req.getParameter("FaxNo") );
			ec.setEmail( req.getParameter("Email") );
			ec.setTimeZone( req.getParameter("TimeZone") );
			
			energyCompany.setName( ec.getCompanyName() );
			LiteCustomerContact liteContact = energyCompany.getCustomerContact( energyCompany.getPrimaryContactID() );
			liteContact.setHomePhone( ec.getMainPhoneNumber() );
			liteContact.setWorkPhone( ec.getMainFaxNumber() );
			liteContact.setEmail( LiteCustomerContact.ContactNotification.newInstance(false, ec.getEmail()) );
			
			Transaction.createTransaction( Transaction.UPDATE,  StarsLiteFactory.createDBPersistent(liteContact) ).execute();
			Transaction.createTransaction( Transaction.UPDATE, StarsLiteFactory.createDBPersistent(energyCompany) ).execute();
			
	        DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	        
	        // Update energy company role DEFAULT_TIME_ZONE if necessary
	        {
		        LiteYukonUser liteUser = YukonUserFuncs.getLiteYukonUser( energyCompany.getUserID() );
		        Map rolePropertyIDMap = (Map) cache.getYukonUserRolePropertyIDLookupMap().get( liteUser );
		        Pair rolePropertyPair = (Pair) rolePropertyIDMap.get( new Integer(EnergyCompanyRole.DEFAULT_TIME_ZONE) );
		        String value = (String) rolePropertyPair.getSecond();
		        
		        if (!value.equalsIgnoreCase( ec.getTimeZone() )) {
		        	String sql = "UPDATE YukonUserRole SET Value = '" + ec.getTimeZone() + "'" +
		        			" WHERE UserID = " + liteUser.getUserID() +
		        			" AND RoleID = " + com.cannontech.roles.YukonRoleDefs.ENERGY_COMPANY_ROLDID +
		        			" AND RolePropertyID = " + EnergyCompanyRole.DEFAULT_TIME_ZONE;
		        	com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
		        			sql, CtiUtilities.getDatabaseAlias() );
		        	stmt.execute();
		        	ServerUtils.handleDBChange( liteUser, com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE );
		        }
	        }
			
			// Update residential customer role CUSTOMIZED_UTIL_EMAIL_LINK if necessary
			/*{
				String customerGroupName = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.CUSTOMER_GROUP_NAME );
		        LiteYukonGroup liteGroup = null;
		        
		        synchronized (cache) {
		        	Iterator it = cache.getAllYukonGroups().iterator();
		        	while (it.hasNext()) {
		        		LiteYukonGroup g = (LiteYukonGroup) it.next();
		        		if (g.getGroupName().equalsIgnoreCase( customerGroupName )) {
		        			liteGroup = g;
		        			break;
		        		}
		        	}
		        }
		        
		        if (liteGroup != null) {
		        	Map rolePropertyMap = (Map) cache.getYukonGroupRolePropertyMap().get( liteGroup );
		        	LiteYukonRole liteRole = AuthFuncs.getRole( com.cannontech.roles.ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_ROLEID );
		        	Map roleProperties = (Map) rolePropertyMap.get( liteRole );
		        	LiteYukonRoleProperty liteRoleProperty = AuthFuncs.getRoleProperty( com.cannontech.roles.consumer.ResidentialCustomerRole.CUSTOMIZED_UTIL_EMAIL_LINK );
		        	String value = (String) roleProperties.get( liteRoleProperty );
		        	
		        	boolean customizedEmail = Boolean.valueOf( req.getParameter("CustomizedEmail") ).booleanValue();
		        	if (CtiUtilities.isTrue(value) != customizedEmail) {
			        	String sql = "UPDATE YukonGroupRole SET Value = '" + customizedEmail + "'" +
			        			" WHERE GroupID = " + liteGroup.getGroupID() +
			        			" AND RoleID = " + liteRole.getRoleID() +
			        			" AND RolePropertyID = " + liteRoleProperty.getRolePropertyID();
			        	com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
			        			sql, CtiUtilities.getDatabaseAlias() );
			        	stmt.execute();
			        	// There is no DBChangeMsg defined for this kind of updates yet, so we will just release all caches
			        	ServerUtils.handleDBChange( null, 0 );
		        	}
		        }
			}*/
        	
        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Energy company information updated successfully");
		}
		catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update energy company information");
		}
	}
	
	private void updateApplianceCategory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsEnrollmentPrograms starsAppCats = ecSettings.getStarsEnrollmentPrograms();
			
			int appCatID = Integer.parseInt( req.getParameter("AppCatID") );
			boolean newAppCat = (appCatID == -1);
			
			StarsApplianceCategory starsAppCat = null;
			if (newAppCat) {
				starsAppCat = new StarsApplianceCategory();
				starsAppCat.setApplianceCategoryID( appCatID );
				starsAppCat.setStarsWebConfig( new StarsWebConfig() );
				starsAppCats.addStarsApplianceCategory( starsAppCat );
			}
			else {
				for (int i = 0; i < starsAppCats.getStarsApplianceCategoryCount(); i++) {
					if (starsAppCats.getStarsApplianceCategory(i).getApplianceCategoryID() == appCatID) {
						starsAppCat = starsAppCats.getStarsApplianceCategory(i);
						break;
					}
				}
				if (starsAppCat == null)
					throw new Exception( "Cannot find StarsApplianceCategory object with appliancecategoryID = " + appCatID);
			}
			
			starsAppCat.setDescription( req.getParameter("Name") );
			starsAppCat.setCategoryID( Integer.parseInt(req.getParameter("Category")) );
			
			StarsWebConfig starsConfig = starsAppCat.getStarsWebConfig();
			starsConfig.setLogoLocation( req.getParameter("IconName") );
			starsConfig.setAlternateDisplayName( req.getParameter("DispName") );
			starsConfig.setDescription( req.getParameter("Description") );
			starsConfig.setURL( "" );
			
			String[] progIDs = req.getParameterValues( "ProgIDs" );
			String[] progDispNames = req.getParameterValues( "ProgDispNames" );
			String[] progShortNames = req.getParameterValues( "ProgShortNames" );
			String[] progDescriptions = req.getParameterValues( "ProgDescriptions" );
			String[] progCtrlOdds = req.getParameterValues( "ProgChanceOfCtrls" );
			String[] progIconNames = req.getParameterValues( "ProgIconNames" );
			
			ArrayList starsEnrPrograms = new ArrayList();
			if (progIDs != null) {
				LCConnectionServlet cs = (LCConnectionServlet) getServletContext().getAttribute(LCConnectionServlet.SERVLET_CONTEXT_ID);
				LoadcontrolCache cache = cs.getCache();
				LMProgramDirect[] allPrograms = cache.getDirectPrograms();
				
				for (int i = 0; i < progIDs.length; i++) {
					StarsEnrLMProgram starsProg = null;
					
					int progID = Integer.parseInt( progIDs[i] );
					for (int j = 0; j < starsAppCat.getStarsEnrLMProgramCount(); j++) {
						if (starsAppCat.getStarsEnrLMProgram(j).getProgramID() == progID) {
							starsProg = starsAppCat.getStarsEnrLMProgram(j);
							break;
						}
					}
					if (starsProg == null) {
						starsProg = new StarsEnrLMProgram();
						starsProg.setProgramID( progID );
						starsProg.setStarsWebConfig( new StarsWebConfig() );
						
						for (int j = 0; j < allPrograms.length; j++) {
							if (allPrograms[j].getYukonID().intValue() == progID) {
								starsProg.setProgramName( allPrograms[j].getYukonName() );
								
								Vector groups = allPrograms[j].getLoadControlGroupVector();
								for (int k = 0; k < groups.size(); k++) {
									if (groups.get(k) instanceof LMGroupBase) {
										LMGroupBase group = (LMGroupBase) groups.get(k);
										AddressingGroup starsGroup = new AddressingGroup();
										starsGroup.setEntryID( group.getYukonID().intValue() );
										starsGroup.setContent( group.getYukonName() );
										starsProg.addAddressingGroup( starsGroup );
									}
								}
							}
						}
					}
					starsEnrPrograms.add( starsProg );
					
					StarsWebConfig starsCfg = starsProg.getStarsWebConfig();
					starsCfg.setLogoLocation( starsConfig.getLogoLocation() + "," + progIconNames[i] );
					starsCfg.setAlternateDisplayName( progShortNames[i] );
					starsCfg.setDescription( progDescriptions[i] );
					if (progDispNames[i].equals( starsProg.getProgramName() ))
						starsCfg.setURL( "" );
					else
						starsCfg.setURL( progDispNames[i] );
					
					int ctrlOddsID = Integer.parseInt(progCtrlOdds[i]);
					if (ctrlOddsID > 0) {
						ChanceOfControl ctrlOdds = new ChanceOfControl();
						starsProg.setChanceOfControl( ctrlOdds );
						ctrlOdds.setEntryID( ctrlOddsID );
						ctrlOdds.setContent( energyCompany.getYukonListEntry(
								YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL, ctrlOdds.getEntryID()).getEntryText() );
					}
					else
						starsProg.setChanceOfControl( null );
				}
			}
			
			StarsEnrLMProgram[] starsEnrProgs = new StarsEnrLMProgram[ starsEnrPrograms.size() ];
			starsEnrPrograms.toArray( starsEnrProgs );
			starsAppCat.setStarsEnrLMProgram( starsEnrProgs );
			
			// Update db and lite objects
			com.cannontech.database.db.web.YukonWebConfiguration config =
					new com.cannontech.database.db.web.YukonWebConfiguration();
			StarsFactory.setWebConfig( config, starsConfig );
			
			com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
					new com.cannontech.database.data.stars.appliance.ApplianceCategory();
			com.cannontech.database.db.stars.appliance.ApplianceCategory appCatDB = appCat.getApplianceCategory();
			appCatDB.setCategoryID( new Integer(starsAppCat.getCategoryID()) );
			appCatDB.setDescription( starsAppCat.getDescription() );
			appCat.setWebConfiguration( config );
			
			LiteApplianceCategory liteAppCat = null;
			if (newAppCat) {
				appCat.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				appCat = (com.cannontech.database.data.stars.appliance.ApplianceCategory)
						Transaction.createTransaction( Transaction.INSERT, appCat ).execute();
				liteAppCat = (LiteApplianceCategory) StarsLiteFactory.createLite( appCat.getApplianceCategory() );
				energyCompany.addApplianceCategory( liteAppCat );
				starsAppCat.setApplianceCategoryID( liteAppCat.getApplianceCategoryID() );
			}
			else {
				appCat.setApplianceCategoryID( new Integer(appCatID) );
				appCat = (com.cannontech.database.data.stars.appliance.ApplianceCategory)
						Transaction.createTransaction( Transaction.UPDATE, appCat ).execute();
				liteAppCat = energyCompany.getApplianceCategory( appCatID );
				StarsLiteFactory.setLiteApplianceCategory( liteAppCat, appCat.getApplianceCategory() );
			}
			
			Integer applianceCategoryID = appCat.getApplianceCategory().getApplianceCategoryID();
			
			// Delete all the existing published programs, then add the new ones
			if (liteAppCat.getPublishedPrograms() != null) {
				com.cannontech.database.db.stars.LMProgramWebPublishing.deleteAllLMProgramWebPublishing( applianceCategoryID );
				
				for (int i = 0 ;i < liteAppCat.getPublishedPrograms().length; i++) {
					int configID = liteAppCat.getPublishedPrograms()[i].getWebSettingsID();
					com.cannontech.database.db.web.YukonWebConfiguration cfg =
							new com.cannontech.database.db.web.YukonWebConfiguration();
					cfg.setConfigurationID( new Integer(configID) );
					Transaction.createTransaction( Transaction.DELETE, cfg ).execute();
					
					SOAPServer.deleteWebConfiguration( configID );
					energyCompany.deleteStarsWebConfig( configID );
				}
			}
			
			LiteLMProgram[] pubPrograms = new LiteLMProgram[ starsAppCat.getStarsEnrLMProgramCount() ];
			for (int i = 0; i < starsAppCat.getStarsEnrLMProgramCount(); i++) {
				StarsEnrLMProgram starsProg = starsAppCat.getStarsEnrLMProgram(i);
				
				com.cannontech.database.db.web.YukonWebConfiguration cfg =
						new com.cannontech.database.db.web.YukonWebConfiguration();
				StarsFactory.setWebConfig( cfg, starsProg.getStarsWebConfig() );
				
				com.cannontech.database.data.stars.LMProgramWebPublishing pubProg =
						new com.cannontech.database.data.stars.LMProgramWebPublishing();
				com.cannontech.database.db.stars.LMProgramWebPublishing pubProgDB = pubProg.getLMProgramWebPublishing();
				pubProgDB.setApplianceCategoryID( applianceCategoryID );
				pubProgDB.setLMProgramID( new Integer(starsProg.getProgramID()) );
				if (starsProg.getChanceOfControl() == null)
					pubProgDB.setChanceOfControlID( new Integer(0) );
				else
					pubProgDB.setChanceOfControlID( new Integer(starsProg.getChanceOfControl().getEntryID()) );
				pubProg.setWebConfiguration( cfg );
				
				pubProg = (com.cannontech.database.data.stars.LMProgramWebPublishing)
						Transaction.createTransaction( Transaction.INSERT, pubProg ).execute();
				
				pubPrograms[i] = new LiteLMProgram();
				pubPrograms[i].setProgramID( starsProg.getProgramID() );
				pubPrograms[i].setProgramName( starsProg.getProgramName() );
				pubPrograms[i].setWebSettingsID( pubProg.getLMProgramWebPublishing().getWebSettingsID().intValue() );
				pubPrograms[i].setChanceOfControlID( pubProg.getLMProgramWebPublishing().getChanceOfControlID().intValue() );
				pubPrograms[i].setProgramCategory( "LMPrograms" );
				
				int[] groupIDs = new int[starsProg.getAddressingGroupCount()];
				for (int j = 0; j < starsProg.getAddressingGroupCount(); j++)
					groupIDs[j] = starsProg.getAddressingGroup(j).getEntryID();
				pubPrograms[i].setGroupIDs( groupIDs );
			}
			liteAppCat.setPublishedPrograms( pubPrograms );
        	
        	if (newAppCat)
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Appliance category is created successfully");
        	else
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Appliance category information updated successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update appliance category information");
        }
	}
	
	private void deleteApplianceCategory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsEnrollmentPrograms starsAppCats = ecSettings.getStarsEnrollmentPrograms();
			
			int applianceCategoryID = Integer.parseInt( req.getParameter("AppCatID") );
			boolean deleteAll = (applianceCategoryID == -1);
			
			for (int i = starsAppCats.getStarsApplianceCategoryCount() - 1; i >= 0; i--) {
				StarsApplianceCategory starsAppCat = starsAppCats.getStarsApplianceCategory(i);
				if (!deleteAll && starsAppCat.getApplianceCategoryID() != applianceCategoryID) continue;
				
				Integer appCatID = new Integer( starsAppCat.getApplianceCategoryID() );
				LiteApplianceCategory liteAppCat = energyCompany.getApplianceCategory( appCatID.intValue() );
				
				com.cannontech.database.db.stars.LMProgramWebPublishing.deleteAllLMProgramWebPublishing( appCatID );
				for (int j = 0; j < liteAppCat.getPublishedPrograms().length; j++) {
					int configID = liteAppCat.getPublishedPrograms()[j].getWebSettingsID();
					com.cannontech.database.db.web.YukonWebConfiguration cfg =
							new com.cannontech.database.db.web.YukonWebConfiguration();
					cfg.setConfigurationID( new Integer(configID) );
					Transaction.createTransaction( Transaction.DELETE, cfg ).execute();
					
					SOAPServer.deleteWebConfiguration( configID );
					energyCompany.deleteStarsWebConfig( configID );
				}
				
				int[] accountIDs = com.cannontech.database.db.stars.appliance.ApplianceBase.getAllAccountIDsWithCategory( appCatID );
				int[] applianceIDs = com.cannontech.database.db.stars.appliance.ApplianceBase.getAllApplianceIDsWithCategory( appCatID );
				
				com.cannontech.database.data.stars.appliance.ApplianceBase app =
						new com.cannontech.database.data.stars.appliance.ApplianceBase();
				for (int j = 0; j < applianceIDs.length; j++) {
					app.setApplianceID( new Integer(applianceIDs[j]) );
					Transaction.createTransaction( Transaction.DELETE, app ).execute();
				}
				
				com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
						new com.cannontech.database.data.stars.appliance.ApplianceCategory();
				StarsLiteFactory.setApplianceCategory( appCat.getApplianceCategory(), liteAppCat );
				Transaction.createTransaction( Transaction.DELETE, appCat ).execute();
				
				for (int j = 0; j < accountIDs.length; j++) {
					LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[j], false );
					if (liteAcctInfo != null) energyCompany.deleteCustAccountInformation( liteAcctInfo );
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
			LiteCustomerContact liteContact = null;
			LiteAddress liteAddr = null;
			
			StarsServiceCompany starsCompany = null;
			
			if (!newCompany) {
				liteCompany = energyCompany.getServiceCompany( companyID );
				StarsLiteFactory.setServiceCompany( companyDB, liteCompany );
				liteContact = energyCompany.getCustomerContact( liteCompany.getPrimaryContactID() );
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
				CompanyAddress starsAddr = (CompanyAddress) session.getAttribute(NEW_ADDRESS);
				session.removeAttribute( NEW_ADDRESS );
				if (starsAddr == null)
					starsAddr = (CompanyAddress) StarsFactory.newStarsCustomerAddress( CompanyAddress.class );
				StarsFactory.setCustomerAddress( address, starsAddr );
				
				company.setAddress( address );
				company.setPrimaryContact( contactDB );
				company.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				company = (com.cannontech.database.data.stars.report.ServiceCompany)
						Transaction.createTransaction( Transaction.INSERT, company ).execute();
				
				liteAddr = (LiteAddress) StarsLiteFactory.createLite( company.getAddress() );
				energyCompany.addAddress( liteAddr );
				
				liteContact = (LiteCustomerContact) StarsLiteFactory.createLite( contact );
				energyCompany.addCustomerContact( liteContact );
				
				liteCompany = (LiteServiceCompany) StarsLiteFactory.createLite( company.getServiceCompany() );
				energyCompany.addServiceCompany( liteCompany );
				
				starsCompany = new StarsServiceCompany();
				starsCompany.setCompanyID( liteCompany.getCompanyID() );
				starsCompanies.addStarsServiceCompany( starsCompany );
				
				starsAddr.setAddressID( liteAddr.getAddressID() );
				starsCompany.setCompanyAddress( starsAddr );
				
				PrimaryContact starsContact = (PrimaryContact) StarsFactory.newStarsCustomerContact(PrimaryContact.class);
				starsContact.setContactID( liteContact.getContactID() );
				starsCompany.setPrimaryContact( starsContact );
			}
			else {
				contactDB = (com.cannontech.database.db.contact.Contact)
						Transaction.createTransaction( Transaction.UPDATE, contactDB ).execute();
				StarsLiteFactory.setLiteCustomerContact( liteContact, contact );
				
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
			}
			
			starsCompany.setCompanyName( liteCompany.getCompanyName() );
			starsCompany.setMainPhoneNumber( liteCompany.getMainPhoneNumber() );
			starsCompany.setMainFaxNumber( liteCompany.getMainFaxNumber() );
			starsCompany.getPrimaryContact().setLastName( liteContact.getLastName() );
			starsCompany.getPrimaryContact().setFirstName( liteContact.getFirstName() );
        	
        	if (newCompany)
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service company is created successfully");
        	else
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service company information updated successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update service company information");
        }
	}
	
	private void deleteServiceCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsServiceCompanies starsCompanies = ecSettings.getStarsServiceCompanies();
			
			int companyID = Integer.parseInt( req.getParameter("CompanyID") );
			boolean deleteAll = (companyID == -1);
			
			for (int i = starsCompanies.getStarsServiceCompanyCount() - 1; i >= 0; i--) {
				StarsServiceCompany starsCompany = starsCompanies.getStarsServiceCompany(i);
				if (!deleteAll && starsCompany.getCompanyID() != companyID) continue;
				
				Integer compID = new Integer( starsCompany.getCompanyID() );
				LiteServiceCompany liteCompany = energyCompany.getServiceCompany( compID.intValue() );
				
				com.cannontech.database.data.stars.report.ServiceCompany company =
						new com.cannontech.database.data.stars.report.ServiceCompany();
				StarsLiteFactory.setServiceCompany( company.getServiceCompany(), liteCompany );
				Transaction.createTransaction( Transaction.DELETE, company ).execute();
				
				energyCompany.deleteAddress( liteCompany.getAddressID() );
				energyCompany.deleteCustomerContact( liteCompany.getPrimaryContactID() );
				energyCompany.deleteServiceCompany( compID.intValue() );
				
				starsCompanies.removeStarsServiceCompany( i );
			}
			
			if (deleteAll)
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service companies have been deleted successfully");
			else
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service company has been deleted successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete service company");
        }
	}
	
	private void updateCustomerFAQSubjects(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsCustomerFAQs starsFAQs = ecSettings.getStarsCustomerFAQs();

        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "FAQ subjects have been updated successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update FAQ subjects");
        }
	}
	
}
