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
import com.cannontech.database.db.stars.ECToGenericMapping;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
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
    
    public static final String ENERGY_COMPANY_TEMP = "ENERGY_COMPANY_TEMP";
    public static final String SERVICE_COMPANY_TEMP = "SERVICE_COMPANY_TEMP";

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
        
        LiteYukonUser liteUser = (LiteYukonUser)
        		session.getAttribute( ServletUtils.ATT_YUKON_USER );
        if (liteUser == null) {
        	resp.sendRedirect( loginURL ); return;
        }
        
        StarsYukonUser user = (StarsYukonUser)
        		session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
        
    	String referer = req.getHeader( "referer" );
    	String redirect = req.getParameter( "REDIRECT" );
    	
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
        
        if (redirect != null)
        	resp.sendRedirect( redirect );
        else
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
		st.wordChars( '.', '.' );
		st.wordChars( '#', '#' );
		
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
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
    	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
		if (liteAcctInfo == null)
			throw new Exception( "Cannot find customer account information!" );
		
		LiteCustomerContact liteContact = energyCompany.getCustomerContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
		if (liteContact.getLoginID() == com.cannontech.user.UserUtils.USER_YUKON_ID
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
							accounts[i].getAccountID().intValue(), true );
					DeleteCustAccountAction.deleteCustomerAccount( liteAcctInfo, energyCompany );
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
			boolean newAddress = (addressID <= 0);
			String referer = req.getParameter( "REFERER" );
			
			if (referer.equalsIgnoreCase("Admin_EnergyCompany.jsp")) {
				StarsEnergyCompany ecTemp = (StarsEnergyCompany) session.getAttribute( ENERGY_COMPANY_TEMP );
				starsAddr = ecTemp.getCompanyAddress();
			}
			else if (referer.startsWith("Admin_ServiceCompany.jsp")) {
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
		}
	}
	
	private void updateEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsEnergyCompany ec = ecSettings.getStarsEnergyCompany();
			
			boolean newContact = (energyCompany.getPrimaryContactID() == CtiUtilities.NONE_ID);
			LiteCustomerContact liteContact = null;
			if (newContact) {
				liteContact = new LiteCustomerContact();
				liteContact.setLastName( CtiUtilities.STRING_NONE );
				liteContact.setFirstName( CtiUtilities.STRING_NONE );
			}
			else
				liteContact = energyCompany.getCustomerContact( energyCompany.getPrimaryContactID() );
				
			liteContact.setHomePhone( req.getParameter("PhoneNo") );
			liteContact.setWorkPhone( req.getParameter("FaxNo") );
			liteContact.setEmail( LiteCustomerContact.ContactNotification.newInstance(false, req.getParameter("Email")) );
			
			com.cannontech.database.data.customer.Contact contact =
					new com.cannontech.database.data.customer.Contact();
			StarsLiteFactory.setContact( contact, liteContact, false );
			
			if (newContact) {
				com.cannontech.database.db.customer.Address addr =
						new com.cannontech.database.db.customer.Address();
				StarsEnergyCompany ecTemp = (StarsEnergyCompany) session.getAttribute( ENERGY_COMPANY_TEMP );
				if (ecTemp != null)
					StarsFactory.setCustomerAddress( addr, ecTemp.getCompanyAddress() );
				contact.setAddress( addr );
				
				contact.setContactID( null );
				contact = (com.cannontech.database.data.customer.Contact)
						Transaction.createTransaction( Transaction.INSERT, contact ).execute();
				
				CompanyAddress starsAddr = new CompanyAddress();
				StarsLiteFactory.setStarsCustomerAddress(
						starsAddr, energyCompany.getAddress(contact.getContact().getAddressID().intValue()) );
				ec.setCompanyAddress( starsAddr );
			}
			else {
				contact = (com.cannontech.database.data.customer.Contact)
						Transaction.createTransaction( Transaction.UPDATE, contact ).execute();
			}
			
			ec.setMainPhoneNumber( liteContact.getHomePhone() );
			ec.setMainFaxNumber( liteContact.getWorkPhone() );
			ec.setEmail( liteContact.getEmail().getNotification() );
			
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
		        Map rolePropertyIDMap = (Map) cache.getYukonUserRolePropertyIDLookupMap().get( liteUser );
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
		        			liteGroup, ResidentialCustomerRole.CUSTOMIZED_UTIL_EMAIL_LINK, "(none)" );
		        	
		        	boolean customizedEmail = Boolean.valueOf( req.getParameter("CustomizedEmail") ).booleanValue();
		        	if (CtiUtilities.isTrue(value) != customizedEmail) {
			        	String sql = "UPDATE YukonGroupRole SET Value = '" + customizedEmail + "'" +
			        			" WHERE GroupID = " + liteGroup.getGroupID() +
			        			" AND RoleID = " + ResidentialCustomerRole.ROLEID +
			        			" AND RolePropertyID = " + ResidentialCustomerRole.CUSTOMIZED_UTIL_EMAIL_LINK;
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
			
			com.cannontech.database.db.web.YukonWebConfiguration config =
					new com.cannontech.database.db.web.YukonWebConfiguration();
			config.setLogoLocation( req.getParameter("IconName") );
			if (Boolean.valueOf( req.getParameter("SameAsName") ).booleanValue())
				config.setAlternateDisplayName( req.getParameter("Name") );
			else
				config.setAlternateDisplayName( req.getParameter("DispName") );
			config.setDescription( req.getParameter("Description").replaceAll("\r\n", "<br>") );
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
				appCat.setApplianceCategoryID( new Integer(appCatID) );
				appCatDB.setWebConfigurationID( new Integer(liteAppCat.getWebConfigurationID()) );
				appCat = (com.cannontech.database.data.stars.appliance.ApplianceCategory)
						Transaction.createTransaction( Transaction.UPDATE, appCat ).execute();
						
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
					cfg.setAlternateDisplayName( progShortNames[i] );
					cfg.setDescription( progDescriptions[i].replaceAll("\r\n", "<br>") );
					cfg.setURL( progDispNames[i] );
					pubProg.setWebConfiguration( cfg );
					
					if (pubPrograms[i] != null) {
						pubProg.getLMProgramWebPublishing().setWebSettingsID( new Integer(pubPrograms[i].getWebSettingsID()) );
						pubProg = (com.cannontech.database.data.stars.LMProgramWebPublishing)
								Transaction.createTransaction( Transaction.UPDATE, pubProg ).execute();
								
						pubPrograms[i].setChanceOfControlID( pubProg.getLMProgramWebPublishing().getChanceOfControlID().intValue() );
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
				
				// Unenroll the program from all customers currently enrolled in it
				int[] accountIDs = com.cannontech.database.db.stars.appliance.ApplianceBase.getAllAccountIDsWithProgram(programID, energyCompany.getEnergyCompanyID());
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
						Transaction.createTransaction(Transaction.UPDATE, appDB).execute();
						liteApp.setLmProgramID( CtiUtilities.NONE_ID );
					}
					
					ArrayList programs = liteAcctInfo.getLmPrograms();
					for (int k = 0; k < programs.size(); k++) {
						if (((LiteStarsLMProgram) programs.get(k)).getLmProgram().getProgramID() == liteProg.getProgramID()) {
							programs.remove(k);
							break;
						}
					}
				}
				
				com.cannontech.database.data.stars.LMProgramWebPublishing pubProg =
						new com.cannontech.database.data.stars.LMProgramWebPublishing();
				pubProg.setApplianceCategoryID( applianceCategoryID );
				pubProg.setLMProgramID( programID );
				pubProg.getLMProgramWebPublishing().setWebSettingsID( new Integer(liteProg.getWebSettingsID()) );
				Transaction.createTransaction(Transaction.DELETE, pubProg).execute();
			
				energyCompany.deleteLMProgram( liteProg.getProgramID() );
				SOAPServer.deleteWebConfiguration( liteProg.getWebSettingsID() );
				energyCompany.deleteStarsWebConfig( liteProg.getWebSettingsID() );
			}
			
			StarsApplianceCategory starsAppCat = StarsLiteFactory.createStarsApplianceCategory( liteAppCat, energyCompany.getLiteID() );
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
			
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
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
				
				for (int j = 0; j < accountIDs.length; j++) {
					LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[j], false );
					if (liteAcctInfo != null) energyCompany.deleteCustAccountInformation( liteAcctInfo );
					energyCompany.deleteStarsCustAccountInformation( accountIDs[j] );
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
			LiteCustomerContact liteContact = null;
			LiteAddress liteAddr = null;
			
			StarsServiceCompany starsCompany = null;
			
			if (!newCompany) {
				liteCompany = energyCompany.getServiceCompany( companyID );
				StarsLiteFactory.setServiceCompany( companyDB, liteCompany );
				liteContact = energyCompany.getCustomerContact( liteCompany.getPrimaryContactID() );
				StarsLiteFactory.setContact( contact, liteContact, false );
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
				
				liteContact = (LiteCustomerContact) StarsLiteFactory.createLite( contact );
				energyCompany.addCustomerContact( liteContact );
				
				liteCompany = (LiteServiceCompany) StarsLiteFactory.createLite( company.getServiceCompany() );
				energyCompany.addServiceCompany( liteCompany );
				
				starsCompany = new StarsServiceCompany();
				starsCompany.setCompanyID( liteCompany.getCompanyID() );
				starsCompanies.addStarsServiceCompany( starsCompany );
				
				PrimaryContact starsContact = new PrimaryContact();
				StarsLiteFactory.setStarsCustomerContact(
						starsContact, energyCompany.getCustomerContact(company.getPrimaryContact().getContactID().intValue()) );
				starsCompany.setPrimaryContact( starsContact );
				
				CompanyAddress starsAddr = new CompanyAddress();
				StarsLiteFactory.setStarsCustomerAddress(
						starsAddr, energyCompany.getAddress(company.getAddress().getAddressID().intValue()) );
				starsCompany.setCompanyAddress( starsAddr );
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
				
				starsCompany.getPrimaryContact().setLastName( liteContact.getLastName() );
				starsCompany.getPrimaryContact().setFirstName( liteContact.getFirstName() );
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
        }
	}
	
	private void deleteServiceCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        java.sql.Connection conn = null;
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsServiceCompanies starsCompanies = ecSettings.getStarsServiceCompanies();
			
			int companyID = Integer.parseInt( req.getParameter("CompanyID") );
			boolean deleteAll = (companyID == -1);
			
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			for (int i = starsCompanies.getStarsServiceCompanyCount() - 1; i >= 0; i--) {
				StarsServiceCompany starsCompany = starsCompanies.getStarsServiceCompany(i);
				if (!deleteAll && starsCompany.getCompanyID() != companyID) continue;
				
				Integer compID = new Integer( starsCompany.getCompanyID() );
				LiteServiceCompany liteCompany = energyCompany.getServiceCompany( compID.intValue() );
		    	
		    	// set InstallationCompanyID = 0 for all inventory assigned to this service company
		    	ArrayList hardwares = energyCompany.getAllLMHardwares();
		    	for (int j = 0; j < hardwares.size(); j++) {
		    		LiteLMHardwareBase liteHw = (LiteLMHardwareBase) hardwares.get(j);
		    		if (liteHw.getInstallationCompanyID() == starsCompany.getCompanyID()) {
		    			com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
		    					(com.cannontech.database.data.stars.hardware.LMHardwareBase) StarsLiteFactory.createDBPersistent( liteHw );
		    			com.cannontech.database.db.stars.hardware.InventoryBase inventory = hardware.getInventoryBase();
		    			inventory.setInstallationCompanyID( new Integer(CtiUtilities.NONE_ID) );
		    			inventory.setDbConnection( conn );
		    			inventory.update();
		    			
		    			liteHw.setInstallationCompanyID( CtiUtilities.NONE_ID );
		    		}
		    	}
		    	
		    	// set ServiceCompanyID = 0 for all work orders assigned to this service company
		    	ArrayList orders = energyCompany.getAllWorkOrders();
		    	for (int j = 0; j < orders.size(); j++) {
		    		LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) orders.get(j);
		    		if (liteOrder.getServiceCompanyID() == starsCompany.getCompanyID()) {
		    			com.cannontech.database.db.stars.report.WorkOrderBase order =
		    					(com.cannontech.database.db.stars.report.WorkOrderBase) StarsLiteFactory.createDBPersistent( liteOrder );
		    			order.setServiceCompanyID( new Integer(CtiUtilities.NONE_ID) );
		    			order.setDbConnection( conn );
		    			order.update();

		    			liteOrder.setServiceCompanyID( CtiUtilities.NONE_ID );
		    		}
		    	}
				
				com.cannontech.database.data.stars.report.ServiceCompany company =
						new com.cannontech.database.data.stars.report.ServiceCompany();
				StarsLiteFactory.setServiceCompany( company.getServiceCompany(), liteCompany );
				company.setDbConnection( conn );
				company.delete();
				
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
        	boolean newCustomizedFAQ = Boolean.valueOf( req.getParameter("CustomizedFAQ") ).booleanValue();
        	boolean oldCustomizedFAQ = AuthFuncs.checkRoleProperty( liteUser, ConsumerInfoRole.CUSTOMIZED_FAQ_LINK );
			
	        DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	        LiteYukonRole consumerInfoRole = AuthFuncs.getRole( ConsumerInfoRole.ROLEID );
        	
        	LiteYukonGroup operatorGroup = null;
        	Iterator it = ((List) cache.getYukonUserGroupMap().get( liteUser )).iterator();
        	while (it.hasNext()) {
        		LiteYukonGroup group = (LiteYukonGroup) it.next();
        		Map roleMap = (Map) cache.getYukonGroupRolePropertyMap().get( group );
        		if (roleMap.get( consumerInfoRole ) != null) {
        			operatorGroup = group;
        			break;
        		}
        	}
        	LiteYukonGroup customerGroup = energyCompany.getResidentialCustomerGroup();
        	
        	boolean changed = false;
        	
        	if (newCustomizedFAQ != oldCustomizedFAQ) {
	        	String sql = "UPDATE YukonGroupRole SET Value = '" + newCustomizedFAQ + "'" +
	        			" WHERE GroupID = " + operatorGroup.getGroupID() +
	        			" AND RoleID = " + ConsumerInfoRole.ROLEID +
	        			" AND RolePropertyID = " + ConsumerInfoRole.CUSTOMIZED_FAQ_LINK;
        		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
        				sql, CtiUtilities.getDatabaseAlias() );
        		stmt.execute();
        		
	        	sql = "UPDATE YukonGroupRole SET Value = '" + newCustomizedFAQ + "'" +
	        			" WHERE GroupID = " + customerGroup.getGroupID() +
	        			" AND RoleID = " + ResidentialCustomerRole.ROLEID +
	        			" AND RolePropertyID = " + ResidentialCustomerRole.CUSTOMIZED_FAQ_LINK;
	        	stmt.setSQLString( sql );
	        	stmt.execute();
	        	
	        	changed = true;
        	}
        	
        	if (newCustomizedFAQ) {
        		String newFAQLink = req.getParameter("FAQLink");
        		String oldFAQLink = AuthFuncs.getRolePropertyValue( liteUser, ConsumerInfoRole.WEB_LINK_FAQ );
        		
        		if (!newFAQLink.equals(oldFAQLink)) {
		        	String sql = "UPDATE YukonGroupRole SET Value = '" + newFAQLink + "'" +
		        			" WHERE GroupID = " + operatorGroup.getGroupID() +
		        			" AND RoleID = " + ConsumerInfoRole.ROLEID +
		        			" AND RolePropertyID = " + ConsumerInfoRole.WEB_LINK_FAQ;
	        		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
	        				sql, CtiUtilities.getDatabaseAlias() );
	        		stmt.execute();
	        		
		        	sql = "UPDATE YukonGroupRole SET Value = '" + newFAQLink + "'" +
		        			" WHERE GroupID = " + customerGroup.getGroupID() +
		        			" AND RoleID = " + ResidentialCustomerRole.ROLEID +
		        			" AND RolePropertyID = " + ResidentialCustomerRole.WEB_LINK_FAQ;
		        	stmt.setSQLString( sql );
		        	stmt.execute();
		        	
		        	changed = true;
        		}
        	}
        	
        	if (changed) {
	        	ServerUtils.handleDBChange( operatorGroup, DBChangeMsg.CHANGE_TYPE_UPDATE );
	        	ServerUtils.handleDBChange( customerGroup, DBChangeMsg.CHANGE_TYPE_UPDATE );
        	}

        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "FAQ link updated successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update FAQ link");
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
			
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
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
			
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
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
			String[] entryTexts = req.getParameterValues("EntryTexts");
			String[] yukonDefIDs = req.getParameterValues("YukonDefIDs");
			
			YukonSelectionList cList = energyCompany.getYukonSelectionList( listName );
			Hashtable selectionListTable = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
			
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			if (listName.equalsIgnoreCase(com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION)) {
				Iterator it = cList.getYukonListEntries().iterator();
				while (it.hasNext()) {
					YukonListEntry cEntry = (YukonListEntry) it.next();
					it.remove();
					
					com.cannontech.database.data.stars.Substation substation =
							new com.cannontech.database.data.stars.Substation();
					substation.setSubstationID( new Integer(cEntry.getEntryID()) );
					substation.setDbConnection( conn );
					substation.delete();
				}
				
				if (entryTexts != null) {
					if (ordering.equalsIgnoreCase("A"))
						Arrays.sort( entryTexts );
					
					for (int i = 0; i < entryTexts.length; i++) {
						com.cannontech.database.data.stars.Substation substation =
								new com.cannontech.database.data.stars.Substation();
						substation.getSubstation().setSubstationName( entryTexts[i] );
						substation.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
						substation.setDbConnection( conn );
						substation.add();
						
			        	YukonListEntry cEntry = new YukonListEntry();
			        	cEntry.setEntryID( substation.getSubstation().getSubstationID().intValue() );
			        	cEntry.setEntryText( substation.getSubstation().getSubstationName() );
						cList.getYukonListEntries().add( cEntry );
					}
				}
				
				StarsCustSelectionList starsList = StarsLiteFactory.createStarsCustSelectionList( cList );
				selectionListTable.put( starsList.getListName(), starsList );
	
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Substation list updated successfully");
	        	return;
			}
			
			if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD_CUS)) {
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
			
			Iterator it = cList.getYukonListEntries().iterator();
			while (it.hasNext()) {
				YukonListEntry cEntry = (YukonListEntry) it.next();
				it.remove();
				YukonListFuncs.getYukonListEntries().remove( new Integer(cEntry.getEntryID()) );
			}
			com.cannontech.database.db.constants.YukonListEntry
					.deleteAllListEntries( new Integer(cList.getListID()), conn );
			
			com.cannontech.database.db.constants.YukonSelectionList list =
					StarsLiteFactory.createYukonSelectionList( cList );
			list.setOrdering( ordering );
			list.setSelectionLabel( label );
			list.setWhereIsList( whereIsList );
			list.setDbConnection( conn );
			list.update();
			
			StarsLiteFactory.setConstantYukonSelectionList( cList, list );
			
			if (entryTexts != null) {
				for (int i = 0; i < entryTexts.length; i++) {
					com.cannontech.database.db.constants.YukonListEntry entry =
							new com.cannontech.database.db.constants.YukonListEntry();
					entry.setListID( list.getListID() );
					if (ordering.equalsIgnoreCase("O"))
						entry.setEntryOrder( new Integer(i+1) );
					else
						entry.setEntryOrder( new Integer(0) );
					entry.setEntryText( entryTexts[i] );
					entry.setYukonDefID( Integer.valueOf(yukonDefIDs[i]) );
					entry.setDbConnection( conn );
					entry.add();
					
					com.cannontech.common.constants.YukonListEntry cEntry =
							new com.cannontech.common.constants.YukonListEntry();
					StarsLiteFactory.setConstantYukonListEntry( cEntry, entry );
					YukonListFuncs.getYukonListEntries().put( entry.getEntryID(), cEntry );
					cList.getYukonListEntries().add( cEntry );
				}
			}
			
			StarsCustSelectionList starsList = StarsLiteFactory.createStarsCustSelectionList( cList );
			selectionListTable.put( starsList.getListName(), starsList );

        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Customer selection list updated successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update customer selection list");
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
        	LiteLMHardwareBase liteHw = energyCompany.getLMHardware( dftSettings.getInventoryID(), true );
        	StarsUpdateThermostatScheduleResponse resp = action.updateThermostatSchedule( updateSched, liteHw, energyCompany );
        	action.parseResponse( resp, dftSettings );
			
        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Default thermostat schedule updated successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update default thermostat schedule");
        }
	}
	
	private void createEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		try {
			String warning = null;
			
			String operGroupName = req.getParameter("OperatorGroup");
			LiteYukonGroup operGroup = AuthFuncs.getGroup( operGroupName );
			if (operGroup == null) {
				if (warning == null) warning = "Warning: ";
				warning += "group '" + operGroupName + "' doesn't exist";
			}
			
			String custGroupName = req.getParameter("CustomerGroup");
			LiteYukonGroup custGroup = AuthFuncs.getGroup( custGroupName );
			if (custGroup == null) {
				if (warning == null) warning = "Warning: ";
				else warning += ", ";
				warning += "group '" + custGroupName + "' doesn't exist";
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
			CustomerAccount[] accounts = CustomerAccount.searchByAccountNumber(
					energyCompany.getEnergyCompanyID(), "%" );
			if (accounts != null) {
				for (int i = 0; i < accounts.length; i++) {
					LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation(
							accounts[i].getAccountID().intValue(), true );
					DeleteCustAccountAction.deleteCustomerAccount( liteAcctInfo, energyCompany );
				}
			}
			
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			// Delete all inventories
			String sql = "SELECT InventoryID FROM ECToInventoryMapping WHERE EnergyCompanyID = ?";
			java.sql.PreparedStatement stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, user.getEnergyCompanyID() );
			
			java.sql.ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				int inventoryID = rset.getInt(1);
				com.cannontech.database.data.stars.hardware.InventoryBase inventory =
						new com.cannontech.database.data.stars.hardware.InventoryBase();
				inventory.setInventoryID( new Integer(inventoryID) );
				inventory.setDbConnection( conn );
				inventory.delete();
			}
			
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
				if (cList.getListID() == energyCompany.FAKE_LIST_ID) continue;
				
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
			
			while (rset.next()) {
				int userID = rset.getInt(1);
				if (userID == energyCompany.getUserID()) continue;
				
				com.cannontech.database.data.user.YukonUser yukonUser =
						new com.cannontech.database.data.user.YukonUser();
				yukonUser.setUserID( new Integer(userID) );
				yukonUser.setDbConnection( conn );
				yukonUser.deleteOperatorLogin();
				
				ServerUtils.handleDBChange( YukonUserFuncs.getLiteYukonUser(userID), DBChangeMsg.CHANGE_TYPE_DELETE );
			}
			
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
				LiteCustomerContact liteContact = energyCompany.getCustomerContact( energyCompany.getPrimaryContactID() );
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
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to create the energy company");
		}
        finally {
        	try {
	        	if (conn != null) conn.close();
        	}
        	catch (java.sql.SQLException e) {}
        }
	}
	
}
