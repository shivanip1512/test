package com.cannontech.stars.web.servlet;

import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
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
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.DeleteCustAccountAction;
import com.cannontech.stars.web.action.DeleteLMHardwareAction;
import com.cannontech.stars.web.action.NewCustAccountAction;
import com.cannontech.stars.web.action.ProgramSignUpAction;
import com.cannontech.stars.web.action.SearchCustAccountAction;
import com.cannontech.stars.web.action.UpdateCustAccountAction;
import com.cannontech.stars.web.action.UpdateLMHardwareAction;
import com.cannontech.stars.web.action.UpdateLoginAction;
import com.cannontech.stars.web.action.UpdateThermostatScheduleAction;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.AnswerType;
import com.cannontech.stars.xml.serialize.BillingAddress;
import com.cannontech.stars.xml.serialize.CompanyAddress;
import com.cannontech.stars.xml.serialize.Email;
import com.cannontech.stars.xml.serialize.InstallationCompany;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.QuestionType;
import com.cannontech.stars.xml.serialize.SULMProgram;
import com.cannontech.stars.xml.serialize.SearchBy;
import com.cannontech.stars.xml.serialize.StarsApplianceCategory;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsCustAccount;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsCustomerAccount;
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
import com.cannontech.stars.xml.serialize.StarsLMHw;
import com.cannontech.stars.xml.serialize.StarsNewCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsProgramSignUp;
import com.cannontech.stars.xml.serialize.StarsSULMPrograms;
import com.cannontech.stars.xml.serialize.StarsSearchCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsServiceCompanies;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.stars.xml.serialize.StarsSiteInformation;
import com.cannontech.stars.xml.serialize.StarsUpdateCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardware;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsUpdateLogin;
import com.cannontech.stars.xml.serialize.StarsUpdateThermostatSchedule;
import com.cannontech.stars.xml.serialize.StarsUpdateThermostatScheduleResponse;
import com.cannontech.stars.xml.serialize.StreetAddress;
import com.cannontech.stars.xml.serialize.Substation;
import com.cannontech.stars.xml.serialize.Voltage;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

interface ImportFileParser {
	String[] populateFields(String line) throws Exception;
}

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
    
	public static final String PREPROCESSED_STARS_DATA = "PREPROCESSED_STARS_DATA";
	public static final String UNASSIGNED_LISTS = "UNASSIGNED_LISTS";
    
	private static final int IDX_ACCOUNT_NO = 0;
	private static final int IDX_STREET_ADDR1 = 1;
	private static final int IDX_STREET_ADDR2 = 2;
	private static final int IDX_CITY = 3;
	private static final int IDX_STATE = 4;
	private static final int IDX_ZIP_CODE = 5;
	private static final int IDX_COUNTY = 6;
	private static final int IDX_LAST_NAME = 7;
	private static final int IDX_FIRST_NAME = 8;
	private static final int IDX_HOME_PHONE = 9;
	private static final int IDX_WORK_PHONE = 10;
	private static final int IDX_WORK_PHONE_EXT = 11;
	private static final int IDX_EMAIL = 12;
	private static final int IDX_SERIAL_NO = 13;
	private static final int IDX_RECEIVE_DATE = 14;
	private static final int IDX_INSTALL_DATE = 15;
	private static final int IDX_REMOVE_DATE = 16;
	private static final int IDX_DEVICE_TYPE = 17;
	private static final int IDX_HARDWARE_ACTION = 18;
	private static final int IDX_USERNAME = 19;
	private static final int IDX_PASSWORD = 20;
	private static final int IDX_SERVICE_COMPANY = 21;
	private static final int IDX_ADDR_GROUP = 22;
	private static final int IDX_CUSTOMER_TYPE = 23;
	private static final int IDX_COMPANY_NAME = 24;
	private static final int IDX_ACCOUNT_NOTES = 25;
	private static final int IDX_SUBSTATION = 26;
	private static final int IDX_FEEDER = 27;
	private static final int IDX_POLE = 28;
	private static final int IDX_TRFM_SIZE = 29;
	private static final int IDX_SERV_VOLT = 30;
	private static final int IDX_MAP_NO = 31;
	private static final int IDX_PROP_NOTES = 32;
	private static final int IDX_ALT_TRACK_NO = 33;
	private static final int IDX_DEVICE_NAME = 34;
	private static final int IDX_DEVICE_VOLT = 35;
	private static final int IDX_INV_NOTES = 36;
	private static final int IDX_ACCOUNT_ID = 37;
	private static final int NUM_FIELDS = 38;
    
    private static final String NEW_ADDRESS = "NEW_ADDRESS";
    
    private static final String HARDWARE_ACTION_INSERT = "INSERT";
    private static final String HARDWARE_ACTION_UPDATE = "UPDATE";
    private static final String HARDWARE_ACTION_REMOVE = "REMOVE";
    
    private static final java.text.SimpleDateFormat starsDateFormat =
    		new java.text.SimpleDateFormat( "yyyyMMdd" );
    
    private static StreamTokenizer prepareStreamTokenzier(String line) {
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
		st.wordChars( '@', '@' );
		st.wordChars( '(', ')' );
		st.wordChars( '!', '!' );
		st.wordChars( '%', '%' );
		
		return st;
    }
    
    private static final Hashtable parsers = new Hashtable();
    static {
    	parsers.put("Idaho", new ImportFileParser() {
			/** Idaho Power Customer Information
			 * COL_NUM:	COL_NAME
			 * 1		----
			 * 2		LAST_NAME
			 * 3		FIRST_NAME
			 * 4		----
			 * 5,6		STREET_ADDR1
			 * 7		CITY
			 * 8		STATE
			 * 9		ZIP_CODE
			 * 10		HOME_PHONE
			 * 11		ACCOUNT_NO
			 * 12		SERIAL_NO(THERMOSTAT)
			 * 13		INSTALL_DATE
			 * 14		SERVICE_COMPANY
			 */
			public String[] populateFields(String line) throws Exception {
				String[] fields = new String[ NUM_FIELDS ];
				for (int i = 0; i < NUM_FIELDS; i++)
					fields[i] = "";
				
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
    	});
    	
    	parsers.put("Portland", new ImportFileParser() {
			/** Portland General Customer Information
			 * COL_NUM:	COL_NAME
			 * 1		ACCOUNT_NO
			 * 2		COUNTY
			 * 3		----
			 * 4		LAST_NAME
			 * 5		FIRST_NAME
			 * 6		HOME_PHONE
			 * 7,8		WORK_PHONE
			 * 9		STREET_ADDR1
			 * 10		CITY
			 * 11		STATE
			 * 12		ZIP_CODE
			 * 13		EMAIL
			 * 14		----
			 * 15		SERVICE_COMPANY
			 * 16		----
			 * 17		INSTALL_DATE
			 * 18		SERIAL_NO(LCR)
			 * 19		ADDR_GROUP
			 * 20		----
			 */
			public String[] populateFields(String line) throws Exception {
				String[] fields = new String[ NUM_FIELDS ];
				for (int i = 0; i < NUM_FIELDS; i++)
					fields[i] = "";
				
				StreamTokenizer st = prepareStreamTokenzier( line );
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_COUNTY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_LAST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_FIRST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_HOME_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_WORK_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_WORK_PHONE_EXT] = "(ext." + st.sval + ")";
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_STREET_ADDR1] = st.sval;
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
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_EMAIL] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_SERVICE_COMPANY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_INSTALL_DATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_SERIAL_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ADDR_GROUP] = st.sval;
				if (st.ttype != ',') st.nextToken();
				
				fields[IDX_USERNAME] = fields[IDX_EMAIL];
				fields[IDX_PASSWORD] = fields[IDX_SERIAL_NO];
				fields[IDX_DEVICE_TYPE] = "LCR-5000";
				fields[IDX_ADDR_GROUP] = "PGE RIWH Group " + fields[IDX_ADDR_GROUP];
				
				return fields;
			}
    	});
    	
		/** San Antonio Customer Information
		 * COL_NUM:	COL_NAME
		 * 1		ACCOUNT_NO
		 * 2		LAST_NAME
		 * 3		FIRST_NAME
		 * 4		HOME_PHONE
		 * 5		WORK_PHONE
		 * 6		EMAIL
		 * 7		STREET_ADDR1
		 * 8		STREET_ADDR2
		 * 9		CITY
		 * 10		STATE
		 * 11		ZIP_CODE
		 * 12		MAP_NO
		 * 13		SERIAL_NO(THERMOSTAT)
		 * 14		INSTALL_DATE
		 * 15		REMOVE_DATE
		 * ----		(Additional required fields)
		 * 16		USERNAME
		 * 17		PASSWORD
		 * 18		HARDWARE_ACTION
		 */
    	parsers.put("San Antonio", new ImportFileParser() {
    		public String[] populateFields(String line) throws Exception {
				String[] fields = new String[ NUM_FIELDS ];
				for (int i = 0; i < NUM_FIELDS; i++)
					fields[i] = "";
					
				StreamTokenizer st = prepareStreamTokenzier( line );
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_LAST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_FIRST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_HOME_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_WORK_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_EMAIL] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_STREET_ADDR1] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_STREET_ADDR2] = st.sval;
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
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_MAP_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_SERIAL_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_INSTALL_DATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_REMOVE_DATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_USERNAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_PASSWORD] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_HARDWARE_ACTION] = st.sval;
				fields[IDX_DEVICE_TYPE] = "Thermostat";
				
    			return fields;
    		}
    	});
    	
    	/** Old STARS customer table
    	 * COL_NUM:	COL_NAME
    	 * 1		(ACCOUNT_ID)	#used accross old STARS tables
    	 * 2,3		ACCOUNT_NO
    	 * 4		CUSTOMER_TYPE
    	 * 5		LAST_NAME
    	 * 6		FIRST_NAME
    	 * 7		----
    	 * 8		COMPANY_NAME
    	 * 9		MAP_NO
    	 * 10		STREET_ADDR1
    	 * 11		STREET_ADDR2
    	 * 12		CITY
    	 * 13		STATE
    	 * 14		ZIP_CODE
    	 * 15		HOME_PHONE
    	 * 16		WORK_PHONE
    	 * 17		----
    	 * 18		ACCOUNT_NOTES
    	 */
    	parsers.put("STARS Customer", new ImportFileParser() {
    		public String[] populateFields(String line) throws Exception {
				String[] fields = new String[ NUM_FIELDS ];
				for (int i = 0; i < NUM_FIELDS; i++)
					fields[i] = "";
					
				StreamTokenizer st = prepareStreamTokenzier( line );
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_NO].concat("-" + st.sval);
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_CUSTOMER_TYPE] = st.sval;
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
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_COMPANY_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_MAP_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_STREET_ADDR1] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_STREET_ADDR2] = st.sval;
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
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_WORK_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_NOTES] = st.sval;
				
				return fields;
    		}
    	});
    	
    	/** Old STARS service info table
    	 * COL_NUM:	COL_NAME
    	 * 1		(ACCOUNT_ID)
    	 * 2		PROP_NOTES
    	 * 3		SUBSTATION
    	 * 4		FEEDER
    	 * 5		POLE
    	 * 6		TRFM_SIZE
    	 * 7		SERV_VOLT
    	 * 8,9		----
    	 * 10-18	PROP_NOTES
    	 */
		parsers.put("STARS ServiceInfo", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				String[] fields = new String[ NUM_FIELDS ];
				for (int i = 0; i < NUM_FIELDS; i++)
					fields[i] = "";
					
				StreamTokenizer st = prepareStreamTokenzier( line );

				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_PROP_NOTES].concat("MeterNumber:" + st.sval + ",");
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_SUBSTATION] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_FEEDER] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_POLE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_TRFM_SIZE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_SERV_VOLT] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				for (int i = 10; i < 18; i++) {
					st.nextToken();
					if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_PROP_NOTES].concat(st.sval + ",");
					if (st.ttype != ',') st.nextToken();
				}
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_PROP_NOTES].concat(st.sval);
				
				return fields;
			}
		});
    	
    	/** Old STARS inventory table
    	 * COL_NUM:	COL_NAME
    	 * 1		(INV_ID)
    	 * 2		SERIAL_NO
    	 * 3		(ACCOUNT_ID)
    	 * 4		ALT_TRACK_NO
    	 * 5		DEVICE_NAME
    	 * 6-8		INV_NOTES		
    	 * 9		DEVICE_TYPE
    	 * 10-12	----
    	 * 13		DEVICE_VOLT
    	 * 14		RECEIVE_DATE
    	 * 15		----
    	 * 16		SERVICE_COMPANY
    	 * 17-19	INV_NOTES
    	 */
		parsers.put("STARS Inventory", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				String[] fields = new String[ NUM_FIELDS ];
				for (int i = 0; i < NUM_FIELDS; i++)
					fields[i] = "";
					
				StreamTokenizer st = prepareStreamTokenzier( line );
				
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_SERIAL_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ALT_TRACK_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_DEVICE_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				for (int i = 6; i <= 8; i++) {
					st.nextToken();
					if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_INV_NOTES].concat(st.sval + ",");
					if (st.ttype != ',') st.nextToken();
				}
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_DEVICE_TYPE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				for (int i = 10; i <= 12; i++) {
					st.nextToken();
					if (st.ttype != ',') st.nextToken();
				}
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_DEVICE_VOLT] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_RECEIVE_DATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_SERVICE_COMPANY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				for (int i = 17; i < 19; i++) {
					st.nextToken();
					if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_INV_NOTES].concat(st.sval + ",");
					if (st.ttype != ',') st.nextToken();
				}
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_INV_NOTES].concat(st.sval);
				
				return fields;
			}
		});
    }
    
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
        
        SOAPClient.initSOAPServer( req );
        
        StarsYukonUser user = (StarsYukonUser)
        		session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
        
    	referer = req.getHeader( "referer" );
    	redirect = req.getParameter( ServletUtils.ATT_REDIRECT );
    	if (redirect == null) redirect = referer;
    	
		String action = req.getParameter( "action" );
		if (action == null) action = "";
		
		if (action.equalsIgnoreCase("DeleteCustAccounts"))
			deleteCustomerAccounts( user, req, session );
		else if (action.equalsIgnoreCase("ImportCustAccounts"))
			importCustomerAccounts( user, req, session );
		else if (action.equalsIgnoreCase("PreprocessStarsData"))
			preprocessStarsData( user, req, session );
		else if (action.equalsIgnoreCase("AssignSelectionList"))
			assignSelectionList( user, req, session );
		else if (action.equalsIgnoreCase("ImportStarsData"))
			importStarsData( user, req, session );
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
	
	private LiteStarsCustAccountInformation searchCustomerAccount(String[] fields, StarsYukonUser user, LiteStarsEnergyCompany energyCompany, HttpSession session)
		throws Exception
	{
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
				return null;
			}
			else
				throw new Exception( "Failed to process response message for NewCustAccountAction" );
		}
		
		return (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
	}
	
	private void setStarsCustAccount(StarsCustAccount account, String[] fields, LiteStarsEnergyCompany energyCompany) {
        account.setAccountNumber( fields[IDX_ACCOUNT_NO] );
        account.setIsCommercial( fields[IDX_CUSTOMER_TYPE].equalsIgnoreCase("COM") );
        account.setCompany( fields[IDX_COMPANY_NAME] );
        account.setAccountNotes( fields[IDX_ACCOUNT_NOTES] );
        account.setPropertyNumber( fields[IDX_MAP_NO] );
        account.setPropertyNotes( fields[IDX_PROP_NOTES] );

        StreetAddress propAddr = new StreetAddress();
        propAddr.setStreetAddr1( fields[IDX_STREET_ADDR1] );
        propAddr.setStreetAddr2( fields[IDX_STREET_ADDR2] );
        propAddr.setCity( fields[IDX_CITY] );
        propAddr.setState( fields[IDX_STATE] );
        propAddr.setZip( fields[IDX_ZIP_CODE] );
        propAddr.setCounty( fields[IDX_COUNTY] );
        account.setStreetAddress( propAddr );

		Substation starsSub = new Substation();
		if (fields[IDX_SUBSTATION].length() > 0) {
			// If importing old STARS data, this field is substation id
			starsSub.setEntryID( Integer.parseInt(fields[IDX_SUBSTATION]) );
		}
		else {
			YukonSelectionList subList = energyCompany.getYukonSelectionList( com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION );
			if (subList.getYukonListEntries().size() == 0)
				starsSub.setEntryID( CtiUtilities.NONE_ID );
			else
				starsSub.setEntryID( ((YukonListEntry)subList.getYukonListEntries().get(0)).getEntryID() );
		}
		
        StarsSiteInformation siteInfo = new StarsSiteInformation();
        siteInfo.setSubstation( starsSub );
        siteInfo.setFeeder( fields[IDX_FEEDER] );
        siteInfo.setPole( fields[IDX_POLE] );
        siteInfo.setTransformerSize( fields[IDX_TRFM_SIZE] );
        siteInfo.setServiceVoltage( fields[IDX_SERV_VOLT] );
        account.setStarsSiteInformation( siteInfo );
        
        BillingAddress billAddr = new BillingAddress();
        billAddr.setStreetAddr1( fields[IDX_STREET_ADDR1] );
        billAddr.setStreetAddr2( fields[IDX_STREET_ADDR2] );
        billAddr.setCity( fields[IDX_CITY] );
        billAddr.setState( fields[IDX_STATE] );
        billAddr.setZip( fields[IDX_ZIP_CODE] );
        billAddr.setCounty( fields[IDX_COUNTY] );
        account.setBillingAddress( billAddr );
        
        PrimaryContact primContact = new PrimaryContact();
        primContact.setLastName( fields[IDX_LAST_NAME] );
        primContact.setFirstName( fields[IDX_FIRST_NAME] );
        primContact.setHomePhone( ServletUtils.formatPhoneNumber(fields[IDX_HOME_PHONE]) );
        primContact.setWorkPhone( ServletUtils.formatPhoneNumber(fields[IDX_WORK_PHONE]) + fields[IDX_WORK_PHONE_EXT] );
        
        Email email = new Email();
        email.setNotification( fields[IDX_EMAIL] );
        email.setEnabled( false );
        primContact.setEmail( email );
        account.setPrimaryContact( primContact );
	}
	
	private LiteStarsCustAccountInformation newCustomerAccount(String[] fields, StarsYukonUser user, LiteStarsEnergyCompany energyCompany, HttpSession session)
		throws Exception
	{
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
		
		return (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
	}
	
	private void updateCustomerAccount(String[] fields, LiteStarsEnergyCompany energyCompany, HttpSession session)
		throws Exception
	{
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
	
	private void updateLogin(String[] fields, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, HttpSession session)
		throws Exception
	{
		LiteContact liteContact = energyCompany.getContact( liteAcctInfo.getCustomer().getPrimaryContactID(), liteAcctInfo );
		if (liteContact.getLoginID() == com.cannontech.user.UserUtils.USER_YUKON_ID
			&& fields[IDX_USERNAME].trim().length() == 0)
			return;
			
        StarsUpdateLogin updateLogin = new StarsUpdateLogin();
        updateLogin.setUsername( fields[IDX_USERNAME] );
        updateLogin.setPassword( fields[IDX_PASSWORD] );
        
        StarsOperation operation = new StarsOperation();
        operation.setStarsUpdateLogin( updateLogin );
        SOAPMessage reqMsg = SOAPUtil.buildSOAPMessage( operation );
        
		StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteAcctInfo );
		StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
		user.setAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo );
		user.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, starsAcctInfo);
        
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
	
	private int getDeviceTypeID(LiteStarsEnergyCompany energyCompany, String deviceType) {
		try {
			// If importing old STARS data, the string is the device type id
			return Integer.parseInt( deviceType );
		}
		catch (NumberFormatException e) {
			YukonSelectionList devTypeList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
			for (int i = 0; i < devTypeList.getYukonListEntries().size(); i++) {
				YukonListEntry entry = (YukonListEntry) devTypeList.getYukonListEntries().get(i);
				if (entry.getEntryText().equalsIgnoreCase( deviceType ))
					return entry.getEntryID();
			}
		}
		
		return -1;
	}
	
	private void setStarsLMHardware(StarsLMHw hardware, String[] fields, LiteStarsEnergyCompany energyCompany) {
		hardware.setManufactureSerialNumber( fields[IDX_SERIAL_NO] );
		
		if (fields[IDX_ALT_TRACK_NO].length() > 0)
			hardware.setAltTrackingNumber( fields[IDX_ALT_TRACK_NO] );
		if (fields[IDX_INV_NOTES].length() > 0)
			hardware.setNotes( fields[IDX_INV_NOTES] );
		
		if (fields[IDX_RECEIVE_DATE].length() > 0) {
			starsDateFormat.setTimeZone( energyCompany.getDefaultTimeZone() );
			try {
				hardware.setReceiveDate( starsDateFormat.parse(fields[IDX_RECEIVE_DATE]) );
			}
			catch (java.text.ParseException e) {}
		}
		
		if (fields[IDX_INSTALL_DATE].length() > 0) {
			hardware.setInstallDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(
					fields[IDX_INSTALL_DATE], energyCompany.getDefaultTimeZone()) );
		}
		
		if (fields[IDX_REMOVE_DATE].length() > 0) {
			hardware.setRemoveDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(
					fields[IDX_REMOVE_DATE], energyCompany.getDefaultTimeZone()) );
		}
		
		if (fields[IDX_DEVICE_VOLT].length() > 0) {
			// If importing old STARS data, this field is the device voltage id
			Voltage volt = new Voltage();
			volt.setEntryID( Integer.parseInt(fields[IDX_DEVICE_VOLT]) );
			hardware.setVoltage( volt );
		}
		
		if (fields[IDX_SERVICE_COMPANY].length() > 0) {
			try {
				// If importing old STARS data, this field is the service company id
				int companyID = Integer.parseInt( fields[IDX_SERVICE_COMPANY] );
				InstallationCompany company = new InstallationCompany();
				company.setEntryID( companyID );
				hardware.setInstallationCompany( company );
			}
			catch (NumberFormatException e) {
				ArrayList companies = energyCompany.getAllServiceCompanies();
				
				for (int i = 0; i < companies.size(); i++) {
					LiteServiceCompany entry = (LiteServiceCompany) companies.get(i);
					
					if (entry.getCompanyName().equalsIgnoreCase( fields[IDX_SERVICE_COMPANY] )) {
						InstallationCompany company = new InstallationCompany();
						company.setEntryID( entry.getCompanyID() );
						hardware.setInstallationCompany( company );
						break;
					}
				}
			}
		}
	}
	
	private void insertLMHardware(String[] fields, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany, HttpSession session) throws Exception
	{
		// Check inventory and build request message
		int devTypeID = getDeviceTypeID( energyCompany, fields[IDX_DEVICE_TYPE] );
		if (devTypeID == -1)
			throw new Exception("Invalid device type \"" + fields[IDX_DEVICE_TYPE] + "\"");
		
		StarsCreateLMHardware createHw = null;
		LiteStarsLMHardware liteHw = energyCompany.searchForLMHardware( devTypeID, fields[IDX_SERIAL_NO] );
		
		if (liteHw != null) {
			if (liteHw.getAccountID() == liteAcctInfo.getAccountID())
				throw new Exception("Cannot insert hardware with serial # " + fields[IDX_SERIAL_NO] + " because it already exists");
			
			createHw = new StarsCreateLMHardware();
			StarsLiteFactory.setStarsLMHw( createHw, liteHw, energyCompany );
			createHw.setInstallDate( new java.util.Date() );
			createHw.setRemoveDate( new java.util.Date(0) );
			createHw.setInstallationNotes( "" );
		}
		else {
			createHw = (StarsCreateLMHardware) StarsFactory.newStarsInventory(StarsCreateLMHardware.class);
			createHw.getLMDeviceType().setEntryID( devTypeID );
		}
		
		setStarsLMHardware( createHw, fields, energyCompany );
		
		StarsOperation operation = new StarsOperation();
		operation.setStarsCreateLMHardware( createHw );
        SOAPMessage reqMsg = SOAPUtil.buildSOAPMessage( operation );
        
		StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteAcctInfo );
		StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
		user.setAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo );
		user.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, starsAcctInfo);
        
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
	
	private void updateLMHardware(String[] fields, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany, HttpSession session) throws Exception
	{
		if (liteAcctInfo.getInventories().size() != 1)
			throw new Exception( "Cannot determine the hardware to be updated" );
		
		// Check inventory and build request message
		int devTypeID = getDeviceTypeID( energyCompany, fields[IDX_DEVICE_TYPE] );
		if (devTypeID == -1)
			throw new Exception("Invalid device type \"" + fields[IDX_DEVICE_TYPE] + "\"");
		
		StarsUpdateLMHardware updateHw = null;
		LiteStarsLMHardware liteHw = energyCompany.searchForLMHardware( devTypeID, fields[IDX_SERIAL_NO] );
		
		if (liteHw != null) {
			updateHw = new StarsUpdateLMHardware();
			StarsLiteFactory.setStarsLMHw( updateHw, liteHw, energyCompany );
			updateHw.setInstallDate( new java.util.Date() );
			updateHw.setRemoveDate( new java.util.Date(0) );
			updateHw.setInstallationNotes( "" );
		}
		else {
			updateHw = (StarsUpdateLMHardware) StarsFactory.newStarsInventory(StarsUpdateLMHardware.class);
			updateHw.getLMDeviceType().setEntryID( devTypeID );
		}
		
		setStarsLMHardware( updateHw, fields, energyCompany );
		
		int invIDOld = ((Integer) liteAcctInfo.getInventories().get(0)).intValue();
		LiteStarsLMHardware liteHwOld = (LiteStarsLMHardware) energyCompany.getInventory( invIDOld, true );
		
		StarsOperation operation = new StarsOperation();
		operation.setStarsUpdateLMHardware( updateHw );
		
		// Remove the old hardware if necessary
		if (!liteHwOld.getManufactureSerialNumber().equals( fields[IDX_SERIAL_NO] )) {
			StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
			deleteHw.setInventoryID( invIDOld );
			deleteHw.setDeleteFromInventory( false );
			if (fields[IDX_REMOVE_DATE].length() > 0) {
				deleteHw.setRemoveDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(
						fields[IDX_REMOVE_DATE], energyCompany.getDefaultTimeZone()) );
			}
			
			operation.setStarsDeleteLMHardware( deleteHw );
		}
		
        SOAPMessage reqMsg = SOAPUtil.buildSOAPMessage( operation );
        
		StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteAcctInfo );
		StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
		user.setAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo );
		user.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, starsAcctInfo);
        
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
	}
	
	private void removeLMHardware(String[] fields, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany, HttpSession session) throws Exception
	{
		// Check if the hardware to be deleted exists
		LiteStarsLMHardware liteHw = null;
		for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
			int invID = ((Integer) liteAcctInfo.getInventories().get(i)).intValue();
			LiteStarsLMHardware hardware = (LiteStarsLMHardware) energyCompany.getInventory(invID, true);
			
			if (hardware.getManufactureSerialNumber().equals( fields[IDX_SERIAL_NO] )) {
				liteHw = hardware;
				break;
			}
		}
		
		if (liteHw == null)
			throw new Exception("Cannot remove hardware with serial # " + fields[IDX_SERIAL_NO] + " because it doesn't exist");
		
		StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
		deleteHw.setInventoryID( liteHw.getInventoryID() );
		deleteHw.setDeleteFromInventory( false );
		if (fields[IDX_REMOVE_DATE].length() > 0) {
			deleteHw.setRemoveDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(
					fields[IDX_REMOVE_DATE], energyCompany.getDefaultTimeZone()) );
		}
		
		StarsOperation operation = new StarsOperation();
		operation.setStarsDeleteLMHardware( deleteHw );
		SOAPMessage reqMsg = SOAPUtil.buildSOAPMessage( operation );
        
		StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteAcctInfo );
		StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
		user.setAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo );
		user.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, starsAcctInfo);
		
		DeleteLMHardwareAction action = new DeleteLMHardwareAction();
		SOAPMessage respMsg = action.process( reqMsg, session );
        
		int status = action.parse( reqMsg, respMsg, session );
		if (status != 0) {
			if (status == StarsConstants.FAILURE_CODE_OPERATION_FAILED) {
				String errorMsg = (String) session.getAttribute( ServletUtils.ATT_ERROR_MESSAGE );
				session.removeAttribute( ServletUtils.ATT_ERROR_MESSAGE );
				throw new Exception( errorMsg );
			}
			else
				throw new Exception( "Failed to process response message for DeleteLMHardwareAction" );
		}
	}
	
	private void programSignUp(String[] fields, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany, HttpServletRequest req, HttpSession session)
		throws Exception
	{
		// Build request message
		StarsSULMPrograms programs = new StarsSULMPrograms();
		String[] catIDs = req.getParameterValues( "CatID" );
		String[] progIDs = req.getParameterValues( "ProgID" );
		
		if (progIDs != null) {
			for (int i = 0; i < progIDs.length; i++) {
				if (progIDs[i].length() == 0) continue;
				
				int progID = Integer.parseInt(progIDs[i]);
				LiteLMProgram liteProg = energyCompany.getLMProgram( progID );
				
                int groupID = 0;
                if (fields[IDX_ADDR_GROUP].length() > 0 && liteProg.getGroupIDs() != null) {
                	for (int j = 0; j < liteProg.getGroupIDs().length; j++) {
                		if (com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName( liteProg.getGroupIDs()[j] )
                				.equalsIgnoreCase( fields[IDX_ADDR_GROUP] ))
                		{
                			groupID = liteProg.getGroupIDs()[j];
                			break;
                		}
                	}
                }
				
				SULMProgram program = new SULMProgram();
				program.setProgramID( progID );
				program.setApplianceCategoryID( Integer.parseInt(catIDs[i]) );
				program.setAddressingGroupID( groupID );
				programs.addSULMProgram( program );
			}
		}
		
		StarsProgramSignUp progSignUp = new StarsProgramSignUp();
		progSignUp.setStarsSULMPrograms( programs );
		
		StarsOperation operation = new StarsOperation();
		operation.setStarsProgramSignUp( progSignUp );
        SOAPMessage reqMsg = SOAPUtil.buildSOAPMessage( operation );
        
		StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteAcctInfo );
		StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
		user.setAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, liteAcctInfo );
		user.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO, starsAcctInfo);
        
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
			int[] accountIDs = CustomerAccount.searchByAccountNumber(
					energyCompany.getEnergyCompanyID(), acctNo );
			
			if (accountIDs != null) {
				for (int i = 0; i < accountIDs.length; i++) {
					LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[i], true );
					DeleteCustAccountAction.deleteCustomerAccount( liteAcctInfo, energyCompany );
				}
				
				if (accountIDs.length > 1)
					session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, accountIDs.length + " customer accounts have been deleted" );
				else
					session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, accountIDs.length + " customer account has been deleted" );
			}
			else
	        	session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Search for account number failed");
		}
		catch (Exception e) {
			e.printStackTrace();
        	session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Delete customer accounts failed");
		}
	}
	
	private ArrayList readFile(File file, HttpSession session) {
		if (file.exists()) {
			try {
				java.io.BufferedReader fr = new java.io.BufferedReader(
						new java.io.FileReader(file) );
				ArrayList lines = new ArrayList();
				String line = null;
				
				while ((line = fr.readLine()) != null)
					if (line.length() > 0) lines.add(line);
				
				fr.close();
				return lines;
			}
			catch (IOException e) {
				e.printStackTrace();
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to read file \"" + file.getPath() + "\"");
			}
		}
		else {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Unable to find file \"" + file.getPath() + "\"");
		}
		
		return null;
	}
	
	private void importCustomerAccounts(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			String importID = ServerUtils.forceNotNone(
					AuthFuncs.getRolePropertyValue(user.getYukonUser(), ConsumerInfoRole.IMPORT_CUSTOMER_ACCOUNT) );
			ImportFileParser parser = (ImportFileParser) parsers.get(importID);
			if (parser == null)
				throw new Exception("Invalid import ID: " + importID);
			
			String importFile = req.getParameter( "ImportFile" );
			ArrayList lines = readFile( new File(importFile), session );
			
			if (lines != null) {
				int numAdded = 0;
				int numUpdated = 0;
				int numFailed = 0;
				
				for (int lineNo = 1; lineNo <= lines.size(); lineNo++) {
					String line = (String) lines.get(lineNo - 1);
					
					try {
						String[] fields = parser.populateFields( line );
						LiteStarsCustAccountInformation liteAcctInfo = searchCustomerAccount(fields, user, energyCompany, session);
						
						if (liteAcctInfo == null) {
							liteAcctInfo = newCustomerAccount( fields, user, energyCompany, session );
							if (! fields[IDX_SERIAL_NO].equalsIgnoreCase(""))
								insertLMHardware( fields, liteAcctInfo, energyCompany, session );
							programSignUp( fields, liteAcctInfo, energyCompany, req, session );
							numAdded++;
						}
						else {
							updateCustomerAccount( fields, energyCompany, session );
							//updateLogin( fields, liteAcctInfo, energyCompany, session );
							if (! fields[IDX_SERIAL_NO].equalsIgnoreCase(""))
							{
								if (fields[IDX_HARDWARE_ACTION].equalsIgnoreCase( HARDWARE_ACTION_INSERT ))
									insertLMHardware( fields, liteAcctInfo, energyCompany, session );
								else if (fields[IDX_HARDWARE_ACTION].equalsIgnoreCase( HARDWARE_ACTION_UPDATE ))
									updateLMHardware( fields, liteAcctInfo, energyCompany, session );
								else if (fields[IDX_HARDWARE_ACTION].equalsIgnoreCase( HARDWARE_ACTION_REMOVE ))
									removeLMHardware( fields, liteAcctInfo, energyCompany, session );
								else {	// Hardware action field not specified
									if (liteAcctInfo.getInventories().size() == 0)
										insertLMHardware( fields, liteAcctInfo, energyCompany, session );
									else
										updateLMHardware( fields, liteAcctInfo, energyCompany, session );
								}
							}
							
							if (liteAcctInfo.getInventories().size() > 0)
					    		programSignUp( fields, liteAcctInfo, energyCompany, req, session );
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
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to import customer accounts");
			redirect = referer;
		}
	}
	
	private void preprocessStarsData(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			File custFile = new File( req.getParameter("CustFile") );
			ArrayList custLines = readFile( custFile, session );
			if (custLines == null) return;
			
			File servInfoFile = new File( req.getParameter("ServInfoFile") );
			ArrayList servInfoLines = readFile( servInfoFile, session );
			if (servInfoLines == null) return;
			
			File invFile = new File( req.getParameter("InvFile") );
			ArrayList invLines = readFile( invFile, session );
			if (invLines == null) return;
			
			Hashtable acctIDFields = new Hashtable();
			ArrayList acctFields = new ArrayList();
			ArrayList invFields = new ArrayList();
			TreeMap subValueID = new TreeMap();
			TreeMap devTypeValueID = new TreeMap();
			TreeMap devVoltValueID = new TreeMap();
			TreeMap companyValueID = new TreeMap();
			
			Integer zero = new Integer(0);
			
			ImportFileParser parser = (ImportFileParser) parsers.get("STARS Customer");
			for (int i = 0; i < custLines.size(); i++) {
				String[] fields = parser.populateFields( (String)custLines.get(i) );
				acctFields.add( fields );
				acctIDFields.put( fields[IDX_ACCOUNT_ID], fields );
			}
			
			parser = (ImportFileParser) parsers.get("STARS ServiceInfo");
			for (int i = 0; i < servInfoLines.size(); i++) {
				String[] fields = parser.populateFields( (String)servInfoLines.get(i) );
				if (fields[IDX_SUBSTATION].length() > 0)
					subValueID.put( fields[IDX_SUBSTATION], zero );
				
				String[] custFields = (String[]) acctIDFields.get( fields[IDX_ACCOUNT_ID] );
				if (custFields != null) {
					for (int j = 0; j < NUM_FIELDS; j++)
						if (fields[j].length() > 0) custFields[j] = fields[j];
				}
			}
			
			parser = (ImportFileParser) parsers.get("STARS Inventory");
			for (int i = 0; i < invLines.size(); i++) {
				String[] fields = parser.populateFields( (String)invLines.get(i) );
				if (fields[IDX_DEVICE_TYPE].length() > 0)
					devTypeValueID.put( fields[IDX_DEVICE_TYPE], zero );
				if (fields[IDX_DEVICE_VOLT].length() > 0)
					devVoltValueID.put( fields[IDX_DEVICE_VOLT], zero );
				if (fields[IDX_SERVICE_COMPANY].length() > 0)
					companyValueID.put( fields[IDX_SERVICE_COMPANY], zero );
				
				invFields.add( fields );
			}
			
			Hashtable preprocessedData = new Hashtable();
			preprocessedData.put( "CustomerAccount", acctFields );
			preprocessedData.put( "Inventory", invFields );
			preprocessedData.put( "Substation", subValueID );
			preprocessedData.put( "DeviceType", devTypeValueID );
			preprocessedData.put( "DeviceVoltage", devVoltValueID );
			preprocessedData.put( "ServiceCompany", companyValueID );
			session.setAttribute(PREPROCESSED_STARS_DATA, preprocessedData);
			
			Hashtable unassignedLists = new Hashtable();
			if (subValueID.size() > 0)
				unassignedLists.put( "Substation", new Boolean(true) );
			if (devTypeValueID.size() > 0)
				unassignedLists.put( "DeviceType", new Boolean(true) );
			if (devVoltValueID.size() > 0)
				unassignedLists.put( "DeviceVoltage", new Boolean(true) );
			if (companyValueID.size() > 0)
				unassignedLists.put( "ServiceCompany", new Boolean(true) );
			session.setAttribute(UNASSIGNED_LISTS, unassignedLists);
		}
		catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to preprocess old STARS data");
			redirect = referer;
		}
	}
	
	private void assignSelectionList(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		StarsGetEnergyCompanySettingsResponse ecSettings =
				(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
		Hashtable selectionListTable = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
		
		String listName = req.getParameter("ListName");
		String[] values = req.getParameterValues("ImportValue");
		String[] entryIDs = req.getParameterValues("EntryID");
		String[] entryTexts = req.getParameterValues("EntryText");
		
		Hashtable preprocessedData = (Hashtable) session.getAttribute(PREPROCESSED_STARS_DATA);
		TreeMap valueIDMap = (TreeMap) preprocessedData.get(listName);
		
		java.sql.Connection conn = null;
		
		try {
			if (entryIDs != null) {
				for (int i = 0; i < values.length; i++)
					valueIDMap.put( values[i], Integer.valueOf(entryIDs[i]) );
			}
			else {
				conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
				
				if (listName.equals("ServiceCompany")) {
					// Should first delete all existing service companies, but we will assume there is none
					for (int i = 0; i < entryTexts.length; i++) {
						com.cannontech.database.data.stars.report.ServiceCompany company =
								new com.cannontech.database.data.stars.report.ServiceCompany();
						com.cannontech.database.db.stars.report.ServiceCompany companyDB = company.getServiceCompany();
						
						companyDB.setCompanyName( entryTexts[i] );
						company.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
						company.setDbConnection( conn );
						company.add();
						
						com.cannontech.database.data.customer.Contact contact =
								new com.cannontech.database.data.customer.Contact();
						contact.setCustomerContact( company.getPrimaryContact() );
						LiteContact liteContact = (LiteContact) StarsLiteFactory.createLite(contact);
						energyCompany.addContact( liteContact, null );
						
						LiteServiceCompany liteCompany = (LiteServiceCompany) StarsLiteFactory.createLite( company.getServiceCompany() );
						energyCompany.addServiceCompany( liteCompany );
						
						StarsServiceCompany starsCompany = StarsLiteFactory.createStarsServiceCompany( liteCompany, energyCompany );
						ecSettings.getStarsServiceCompanies().addStarsServiceCompany( starsCompany );
						
						valueIDMap.put( entryTexts[i], new Integer(liteCompany.getCompanyID()) );
					}
				}
				else {
					YukonSelectionList cList = energyCompany.getYukonSelectionList( listName );
					ArrayList cEntries = cList.getYukonListEntries();
					
					if (listName.equals("Substation")) {
						// Should first delete all existing substations, but we will assume there is none
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
							cEntries.add( cEntry );
							
							valueIDMap.put( entryTexts[i], new Integer(cEntry.getEntryID()) );
						}
					}
					else {
						boolean autoCommit = conn.getAutoCommit();
						conn.setAutoCommit( false );
						
						try {
							// Delete all existing entries first
							for (int i = 0; i < cEntries.size(); i++) {
								int entryID = ((YukonListEntry) cEntries.get(i)).getEntryID();
								
								try {
									com.cannontech.database.db.constants.YukonListEntry entry =
											new com.cannontech.database.db.constants.YukonListEntry();
									entry.setEntryID( new Integer(entryID) );
									entry.setDbConnection( conn );
									entry.delete();
								}
								catch (java.sql.SQLException e) {
									e.printStackTrace();
									session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Cannot delete list entry with id = " + entryID + ", make sure it is not referenced");
									// Throw a special exception so we can rollback in the outer catch block
									throw new java.sql.SQLException(null, null, -9999);
								}
							}
							
							conn.commit();
							cEntries.clear();
						}
						finally {
							conn.setAutoCommit( autoCommit );
						}
						
						for (int i = 0; i < entryTexts.length; i++) {
							com.cannontech.database.db.constants.YukonListEntry entry =
									new com.cannontech.database.db.constants.YukonListEntry();
							entry.setListID( new Integer(cList.getListID()) );
							entry.setEntryText( entryTexts[i] );
							entry.setDbConnection( conn );
							entry.add();
							
							com.cannontech.common.constants.YukonListEntry cEntry =
									new com.cannontech.common.constants.YukonListEntry();
							StarsLiteFactory.setConstantYukonListEntry( cEntry, entry );
							cEntries.add( cEntry );
							
							valueIDMap.put( entryTexts[i], new Integer(cEntry.getEntryID()) );
						}
					}
					
					StarsCustSelectionList starsList = StarsLiteFactory.createStarsCustSelectionList( cList );
					selectionListTable.put( starsList.getListName(), starsList );
				}
			}
			
			// Change the unassigned flag to false
			Hashtable unassignedLists = (Hashtable) session.getAttribute(UNASSIGNED_LISTS);
			unassignedLists.put(listName, new Boolean(false));
		}
		catch (java.sql.SQLException e) {
			if (e.getErrorCode() != -9999) {
				e.printStackTrace();
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to assign selection list");
			}
			
			try {
				if (conn != null) conn.rollback();
			}
			catch (java.sql.SQLException e2) {}
			
			redirect = referer;
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
	}
	
	private void importStarsData(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		Hashtable preprocessedData = (Hashtable) session.getAttribute(PREPROCESSED_STARS_DATA);
		ArrayList acctFields = (ArrayList) preprocessedData.get( "CustomerAccount" );
		ArrayList invFields = (ArrayList) preprocessedData.get( "Inventory" );
		TreeMap subValueID = (TreeMap) preprocessedData.get("Substation");
		TreeMap devTypeValueID = (TreeMap) preprocessedData.get("DeviceType");
		TreeMap devVoltValueID = (TreeMap) preprocessedData.get("DeviceVoltage");
		TreeMap companyValueID = (TreeMap) preprocessedData.get("ServiceCompany");
		
		// Replace import values with ids assigned to them
		for (int i = 0; i < acctFields.size(); i++) {
			String[] fields = (String[]) acctFields.get(i);
			Integer subId = (Integer) subValueID.get(fields[IDX_SUBSTATION]);
			fields[IDX_SUBSTATION] = (subId != null)? subId.toString() : "0";
		}
		
		for (int i = 0; i < invFields.size(); i++) {
			String[] fields = (String[]) invFields.get(i);
			Integer devTypeId = (Integer) devTypeValueID.get(fields[IDX_DEVICE_TYPE]);
			fields[IDX_DEVICE_TYPE] = (devTypeId != null)? devTypeId.toString() : "0";
			Integer devVoltId = (Integer) devVoltValueID.get(fields[IDX_DEVICE_VOLT]);
			fields[IDX_DEVICE_VOLT] = (devVoltId != null)? devVoltId.toString() : "0";
			Integer companyId = (Integer) companyValueID.get(fields[IDX_SERVICE_COMPANY]);
			fields[IDX_SERVICE_COMPANY] = (companyId != null)? companyId.toString() : "0";
		}
		
		Hashtable acctIDMap = new Hashtable();
		int numAcctAdded = 0;
		int numAcctFailed = 0;
		int numInvAdded = 0;
		int numInvFailed = 0;
		
		for (int lineNo = 1; lineNo <= acctFields.size(); lineNo++) {
			String[] fields = (String[]) acctFields.get(lineNo - 1);
			
			try {
				LiteStarsCustAccountInformation liteAcctInfo = newCustomerAccount( fields, user, energyCompany, session );
				acctIDMap.put( fields[IDX_ACCOUNT_ID], liteAcctInfo );
				numAcctAdded++;
			}
			catch (Exception e) {
				CTILogger.error( "Error encountered when processing customer file line #" + lineNo );
				e.printStackTrace();
				numAcctFailed++;
			}
		}
		
		java.sql.Connection conn = null;
		try {
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			for (int lineNo = 1; lineNo <= invFields.size(); lineNo++) {
				String[] fields = (String[]) invFields.get(lineNo - 1);
				
				try {
					LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) acctIDMap.get(fields[IDX_ACCOUNT_ID]);
					
					if (liteAcctInfo != null) {
						insertLMHardware( fields, liteAcctInfo, energyCompany, session );
					}
					else {
						// The hardware is not assigned to any account, add it to inventory
						int devTypeID = Integer.parseInt( fields[IDX_DEVICE_TYPE] );
						LiteStarsLMHardware liteHw = energyCompany.searchForLMHardware( devTypeID, fields[IDX_SERIAL_NO] );
						if (liteHw != null)
							throw new Exception("Cannot insert hardware with serial # " + fields[IDX_SERIAL_NO] + " because it already exists");
						
						StarsCreateLMHardware createHw = (StarsCreateLMHardware) StarsFactory.newStarsInventory(StarsCreateLMHardware.class);
						setStarsLMHardware( createHw, fields, energyCompany );
						
						com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
								new com.cannontech.database.data.stars.hardware.LMHardwareBase();
						com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hardware.getLMHardwareBase();
						com.cannontech.database.db.stars.hardware.InventoryBase invDB = hardware.getInventoryBase();
						
						StarsFactory.setInventoryBase( invDB, createHw );
						invDB.setCategoryID( new Integer(ECUtils.getInventoryCategoryID(devTypeID, energyCompany)) );
						hwDB.setManufacturerSerialNumber( fields[IDX_SERIAL_NO] );
						hwDB.setLMHardwareTypeID( new Integer(devTypeID) );
						hardware.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
						hardware.setDbConnection( conn );
						hardware.add();
						
						liteHw = new LiteStarsLMHardware();
						StarsLiteFactory.setLiteStarsLMHardware( liteHw, hardware );
						energyCompany.addInventory( liteHw );
					}
					
					numInvAdded++;
				}
				catch (Exception e) {
					CTILogger.error( "Error encountered when processing inventory file line #" + lineNo );
					e.printStackTrace();
					numInvFailed++;
				}
			}
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
		
		session.removeAttribute( PREPROCESSED_STARS_DATA );
		session.removeAttribute( UNASSIGNED_LISTS );
		
		String confirmMsg = "";
		if (numAcctAdded > 0)
			confirmMsg += numAcctAdded + " customer accounts added. ";
		if (numInvAdded > 0)
			confirmMsg += numInvAdded + " hardwares added.";
		if (confirmMsg.length() > 0)
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, confirmMsg);
		
		String errorMsg = "";
		if (numAcctFailed > 0)
			errorMsg += numAcctFailed + " customer accounts failed. ";
		if (numInvFailed > 0)
			errorMsg += numInvFailed + " hardwares failed.";
		if (errorMsg.length() > 0)
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, errorMsg);
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
				liteContact = energyCompany.getContact( energyCompany.getPrimaryContactID(), null );
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
					cfg.setAlternateDisplayName( progDispNames[i] + "," + progShortNames[i] );
					cfg.setDescription( progDescriptions[i].replaceAll("\r\n", "<br>") );
					pubProg.setWebConfiguration( cfg );
					
					if (pubPrograms[i] != null) {
						pubProg.getLMProgramWebPublishing().setWebSettingsID( new Integer(pubPrograms[i].getWebSettingsID()) );
						pubProg = (com.cannontech.database.data.stars.LMProgramWebPublishing)
								Transaction.createTransaction( Transaction.UPDATE, pubProg ).execute();
						pubPrograms[i].setChanceOfControlID( pubProg.getLMProgramWebPublishing().getChanceOfControlID().intValue() );
						
						LiteWebConfiguration liteCfg = SOAPServer.getWebConfiguration( pubProg.getWebConfiguration().getConfigurationID().intValue() );
						
						// If program display name changed, we need to update all the accounts with this program
						String[] dispNames = ServerUtils.forceNotNull( liteCfg.getAlternateDisplayName() ).split(",");
						String oldDispName = (dispNames.length > 0)? dispNames[0] : "";
						if (!oldDispName.equals( progDispNames[i] )) {
							int[] accountIDs = com.cannontech.database.db.stars.appliance.ApplianceBase.getAllAccountIDsWithProgram(new Integer(progID), energyCompany.getEnergyCompanyID());
							for (int j = 0; j < accountIDs.length; j++)
								energyCompany.deleteStarsCustAccountInformation( accountIDs[j] );
						}
						
						StarsLiteFactory.setLiteWebConfiguration( liteCfg, pubProg.getWebConfiguration() );
						energyCompany.updateStarsWebConfig( liteCfg.getConfigID() );
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
					energyCompany.deleteStarsCustAccountInformation( accountIDs[j] );
					
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
					energyCompany.deleteStarsCustAccountInformation( accountIDs[j] );
					LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[j], true );
					
					Iterator appIt = liteAcctInfo.getAppliances().iterator();
					while (appIt.hasNext()) {
						LiteStarsAppliance liteApp = (LiteStarsAppliance) appIt.next();
						if (liteApp.getApplianceCategoryID() == appCatID.intValue()) {
							appIt.remove();
							
							Iterator progIt = liteAcctInfo.getLmPrograms().iterator();
							while (progIt.hasNext()) {
								LiteLMProgram liteProg = (LiteLMProgram) progIt.next();
								if (liteProg.getProgramID() == liteApp.getLmProgramID())
									progIt.remove();
							}
						}
					}
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
				liteContact = energyCompany.getContact( liteCompany.getPrimaryContactID(), null );
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
						starsContact, energyCompany.getContact(company.getPrimaryContact().getContactID().intValue(), null) );
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
	
	private void deleteServiceCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        java.sql.Connection conn = null;
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsServiceCompanies starsCompanies = ecSettings.getStarsServiceCompanies();
			
			int companyID = Integer.parseInt( req.getParameter("CompanyID") );
			boolean deleteAll = (companyID == -1);
			
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			for (int i = starsCompanies.getStarsServiceCompanyCount() - 1; i >= 0; i--) {
				StarsServiceCompany starsCompany = starsCompanies.getStarsServiceCompany(i);
				if (!deleteAll && starsCompany.getCompanyID() != companyID) continue;
				
				Integer compID = new Integer( starsCompany.getCompanyID() );
				LiteServiceCompany liteCompany = energyCompany.getServiceCompany( compID.intValue() );
		    	
		    	// set InstallationCompanyID = 0 for all inventory assigned to this service company
		    	ArrayList inventory = energyCompany.getAllInventory();
		    	
		    	for (int j = 0; j < inventory.size(); j++) {
					LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(j);
					
		    		if (liteInv.getInstallationCompanyID() == starsCompany.getCompanyID()) {
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
				energyCompany.deleteContact( liteCompany.getPrimaryContactID() );
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
	
	private void updateCustomerSelectionList(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
		java.sql.Connection conn = null;
		boolean autoCommit = true;
        
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
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit( false );
			
			// Handle substation list
			if (listName.equalsIgnoreCase(com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION)) {
				// Create a copy of the old entry list, so we won't lose it if something goes wrong
				ArrayList oldEntries = new ArrayList();
				oldEntries.addAll( cList.getYukonListEntries() );
				
				ArrayList newEntries = new ArrayList();
				
				if (entryIDs != null) {
					for (int i = 0; i < entryIDs.length; i++) {
						int entryID = Integer.parseInt(entryIDs[i]);
						
						if (entryID == 0) {
							// This is a new entry, add it to the new entry list
							com.cannontech.database.data.stars.Substation substation =
									new com.cannontech.database.data.stars.Substation();
							substation.getSubstation().setSubstationName( entryTexts[i] );
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
							subDB.setSubstationName( entryTexts[i] );
							subDB.setDbConnection( conn );
							subDB.update();
							
							for (int j = 0; j < oldEntries.size(); j++) {
								YukonListEntry cEntry = (YukonListEntry) oldEntries.get(j);
								if (cEntry.getEntryID() == entryID) {
									cEntry.setEntryText( entryTexts[i] );
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
						session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Cannot delete substation with id = " + entryID + ", make sure it is not referenced");
						
						// Throw a special exception so we can rollback in the outer catch block
						throw new java.sql.SQLException(null, null, -9999);
					}
				}
				
				conn.commit();
				cList.setYukonListEntries( newEntries );
				
				StarsCustSelectionList starsList = StarsLiteFactory.createStarsCustSelectionList( cList );
				selectionListTable.put( starsList.getListName(), starsList );
				
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Substation list updated successfully");
				return;
			}
			
			// Handle customer opt out period list
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
					
					conn.commit();
					
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
			
			// Handle other selection lists
			com.cannontech.database.db.constants.YukonSelectionList list =
					StarsLiteFactory.createYukonSelectionList( cList );
			list.setOrdering( ordering );
			list.setSelectionLabel( label );
			list.setWhereIsList( whereIsList );
			list.setDbConnection( conn );
			list.update();
			
			// Create a copy of the old entry list, so we won't lose it if something goes wrong
			ArrayList oldEntries = new ArrayList();
			oldEntries.addAll( cList.getYukonListEntries() );
			
			ArrayList newEntries = new ArrayList();
			
			if (entryIDs != null) {
				for (int i = 0; i < entryIDs.length; i++) {
					int entryID = Integer.parseInt( entryIDs[i] );
					int entryOrder = ordering.equalsIgnoreCase("O")? (i+1) : 0;
					
					if (entryID == 0) {
						// This is a new entry, add it to the new entry list
						com.cannontech.database.db.constants.YukonListEntry entry =
								new com.cannontech.database.db.constants.YukonListEntry();
						entry.setListID( list.getListID() );
						entry.setEntryOrder( new Integer(entryOrder) );
						entry.setEntryText( entryTexts[i] );
						entry.setYukonDefID( Integer.valueOf(yukonDefIDs[i]) );
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
								entry.setEntryOrder( new Integer(entryOrder) );
								entry.setEntryText( entryTexts[i] );
								entry.setYukonDefID( Integer.valueOf(yukonDefIDs[i]) );
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
					session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Cannot delete list entry with id = " + entryID + ", make sure it is not referenced");
					
					// Throw a special exception so we can rollback in the outer catch block
					throw new java.sql.SQLException(null, null, -9999);
				}
			}
			
			conn.commit();
			
			// Update the constant objects (in both stars and core yukon cache)
			for (int i = 0; i < cList.getYukonListEntries().size(); i++) {
				YukonListEntry entry = (YukonListEntry) cList.getYukonListEntries().get(i);
				YukonListFuncs.getYukonListEntries().remove( new Integer(entry.getEntryID()) );
			}
			
			for (int i = 0; i < newEntries.size(); i++) {
				YukonListEntry entry = (YukonListEntry) newEntries.get(i);
				YukonListFuncs.getYukonListEntries().put( new Integer(entry.getEntryID()), entry );
			}
			
			StarsLiteFactory.setConstantYukonSelectionList( cList, list );
			cList.setYukonListEntries( newEntries );
			
			StarsCustSelectionList starsList = StarsLiteFactory.createStarsCustSelectionList( cList );
			selectionListTable.put( starsList.getListName(), starsList );
			
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Customer selection list updated successfully");
		}
		catch (java.sql.SQLException e) {
			if (e.getErrorCode() != -9999) {
				e.printStackTrace();
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update customer selection list");
			}
			
			try {
				if (conn != null) conn.rollback();
			}
			catch (java.sql.SQLException e2) {}
			
			redirect = referer;
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
				LiteContact liteContact = energyCompany.getContact( energyCompany.getPrimaryContactID(), null );
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
