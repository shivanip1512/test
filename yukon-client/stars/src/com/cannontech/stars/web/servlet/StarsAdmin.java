package com.cannontech.stars.web.servlet;

import java.io.*;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.*;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteLMHardwareBase;
import com.cannontech.database.data.lite.stars.LiteLMProgram;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.*;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

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
        
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
    	String referer = req.getHeader( "referer" );
		String action = req.getParameter( "action" );
		if (action == null) action = "";
		
		if (action.equalsIgnoreCase( "DeleteCustAccounts" )) {
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
		else if (action.equalsIgnoreCase( "ImportCustAccounts" )) {
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

}
