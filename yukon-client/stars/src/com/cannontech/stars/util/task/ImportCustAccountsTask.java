/*
 * Created on Jan 29, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.io.*;
import java.util.*;

import org.apache.commons.fileupload.FileItem;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.util.ImportProblem;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.action.DeleteCustAccountAction;
import com.cannontech.stars.web.util.ImportManagerUtil;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.user.UserUtils;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ImportCustAccountsTask extends TimeConsumingTask {

	private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );
	
	// Generic import file column names
	private static final String COL_NAME_LABEL = "COLUMN_NAMES:";
	
	private static final String[] CUST_COLUMNS = {
		"ACCOUNT_NO",
		"CUST_ACTION",
		"LAST_NAME",
		"FIRST_NAME",
		"HOME_PHONE",
		"WORK_PHONE",
		"EMAIL",
		"STREET_ADDR1",
		"STREET_ADDR2",
		"CITY",
		"STATE",
		"COUNTY",
		"ZIP_CODE",
		"MAP_NO",
		"SUBSTATION",
		"FEEDER",
		"POLE",
		"TRFM_SIZE",
		"SERV_VOLT",
		"USERNAME",
		"PASSWORD",
		"LOGIN_GROUP",
        "COMPANY_NAME",
        "IVR_PIN",
        "IVR_USERNAME"
	};
    
	private static final String[] HW_COLUMNS = {
		"ACCOUNT_NO",
		"HW_ACTION",
		"DEVICE_TYPE",
		"SERIAL_NO",
		"INSTALL_DATE",
		"REMOVE_DATE",
		"SERVICE_COMPANY",
		"PROGRAM_NAME",
		"ADDR_GROUP",
		"APP_TYPE",
		"APP_KW",
        "APP_RELAY_NUM",
        "OPTION_PARAMS",
        "DEVICE_LABEL"
	};
	
	// Column indices of the generic customer info file
	private static int acct_col = 0;
	private static final int COL_ACCOUNT_NO = acct_col++;
	private static final int COL_CUST_ACTION = acct_col++;
	private static final int COL_LAST_NAME = acct_col++;
	private static final int COL_FIRST_NAME = acct_col++;
	private static final int COL_HOME_PHONE = acct_col++;
	private static final int COL_WORK_PHONE = acct_col++;
	private static final int COL_EMAIL = acct_col++;
	private static final int COL_STREET_ADDR1 = acct_col++;
	private static final int COL_STREET_ADDR2 = acct_col++;
	private static final int COL_CITY = acct_col++;
	private static final int COL_STATE = acct_col++;
	private static final int COL_COUNTY = acct_col++;
	private static final int COL_ZIP_CODE = acct_col++;
	private static final int COL_MAP_NO = acct_col++;
	private static final int COL_SUBSTATION = acct_col++;
	private static final int COL_FEEDER = acct_col++;
	private static final int COL_POLE = acct_col++;
	private static final int COL_TRFM_SIZE = acct_col++;
	private static final int COL_SERV_VOLT = acct_col++;
	private static final int COL_USERNAME = acct_col++;
	private static final int COL_PASSWORD = acct_col++;
	private static final int COL_LOGIN_GROUP = acct_col++;
    private static final int COL_COMPANY_NAME = acct_col++;
    private static final int COL_IVR_PIN = acct_col++;
    private static final int COL_IVR_USERNAME = acct_col++;
	
    // Column indices of the generic hardware info file
	private static int hw_col = 0;
	private static final int COL_HW_ACCOUNT_NO = hw_col++;
	private static final int COL_HW_ACTION = hw_col++;
	private static final int COL_DEVICE_TYPE = hw_col++;
	private static final int COL_SERIAL_NO = hw_col++;
	private static final int COL_INSTALL_DATE = hw_col++;
	private static final int COL_REMOVE_DATE = hw_col++;
	private static final int COL_SERVICE_COMPANY = hw_col++;
	private static final int COL_PROGRAM_NAME = hw_col++;
	private static final int COL_ADDR_GROUP = hw_col++;
	private static final int COL_APP_TYPE = hw_col++;
	private static final int COL_APP_KW = hw_col++;
    private static final int COL_APP_RELAY_NUMBER = hw_col++;
    private static final int COL_OPTION_PARAMS = hw_col++;
    private static final int COL_DEVICE_LABEL = hw_col++;
    
	LiteStarsEnergyCompany energyCompany = null;
	FileItem custFile = null;
	FileItem hwFile = null;
	String email = null;
	boolean preScan = false;
    boolean insertSpecified = false;
    boolean seasonalLoad = false;
    int userID = UserUtils.USER_DEFAULT_ID;
    boolean firstFinishedPass = true;
    
	PrintWriter importLog = null;
	String position = null;
	
	int numAcctTotal = 0;
	int numAcctImported = 0;
	int numAcctAdded = 0;
	int numAcctUpdated = 0;
	int numAcctRemoved = 0;
	
	int numHwTotal = 0;
	int numHwImported = 0;
	int numHwAdded = 0;
	int numHwUpdated = 0;
	int numHwRemoved = 0;
    
    int comparableDigitEndIndex = 0;
    
    private Map hwLines;
	private Map custLines;

	public ImportCustAccountsTask() {
		
	}
	
	public ImportCustAccountsTask (LiteStarsEnergyCompany energyCompany, FileItem custFile, FileItem hwFile, String email, boolean preScan, Integer userID) 
    {
		this.energyCompany = energyCompany;
		this.custFile = custFile;
		this.hwFile = hwFile;
		this.email = email;
		this.preScan = preScan;
        if(userID != null)
            this.userID = userID.intValue();
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if (status != STATUS_NOT_INIT) {
			if (preScan) {
				String msg = "Pre-scanning import file(s)";
				if (position != null) msg += ": " + position;
				return msg;
			}
			else
				return getImportProgress();
		}
		
		return null;
	}
	
	public Collection getErrorList() {
		if (custLines != null) {
			return cleanList(custLines.values());
		}
		if (hwLines != null) {
			return cleanList(hwLines.values());
		}
		return new ArrayList();
	}
	
	private Collection cleanList(final Collection c) {
		List list = new ArrayList();
		for (java.util.Iterator i = c.iterator(); i.hasNext();) {
			String[] value = (String[]) i.next();
			if (value.length > 1 && value[1] != null) {
				list.add(value);
			}
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (custFile == null && hwFile == null) {
			status = STATUS_ERROR;
			errorMsg = "Import files cannot all be null";
			return;
		}
        
        File logFile = null;
		
        ArrayList custFieldsList = null;
		ArrayList hwFieldsList = null;
		ArrayList appFieldsList = null;
		
		int[] custColIdx = new int[ CUST_COLUMNS.length ];
		int[] hwColIdx = new int [ HW_COLUMNS.length ];
		int numCustCol = CUST_COLUMNS.length;
		int numHwCol = HW_COLUMNS.length;
		
		for (int i = 0; i < CUST_COLUMNS.length; i++)
			custColIdx[i] = -1;
		for (int i = 0; i < HW_COLUMNS.length; i++)
			hwColIdx[i] = -1;
		
		// Pre-scan the import file(s). If customer and hardware information is in the same file,
		// no optimization will be preformed.
		Map custFieldsMap = new HashMap();	// Map from account # (String) to customer fields (String[])
		Map hwFieldsMap = new HashMap();	// Map from serial # (String) to account # (String)

		status = STATUS_RUNNING;
		
        try {
			final String fs = System.getProperty( "file.separator" );
            String ecName = energyCompany.getName();
            if(ecName.indexOf('<') > -1) {
                ecName = "EnergyCompany" + energyCompany.getEnergyCompanyID();
            }
            File uploadDir = new File(
                    ServerUtils.getStarsTempDir() + fs + ServerUtils.UPLOAD_DIR + fs + ecName);
            if (!uploadDir.exists()) uploadDir.mkdirs();
			
			Date now = new Date();
			String logFileName = StarsUtils.starsDateFormat.format(now) + "_" + StarsUtils.starsTimeFormat.format(now) + ".log";
			logFile = new File(uploadDir, logFileName);
			
			importLog = new PrintWriter(new FileWriter(logFile), true);
			importLog.println("Start time: " + StarsUtils.formatDate( now, energyCompany.getDefaultTimeZone() ));
			importLog.println();
			
			int errors = 0;
			
			if (custFile != null) {
				custFieldsList = new ArrayList();
				ArrayList addedUsernames = new ArrayList();
				ArrayList removedUsernames = new ArrayList();
				custLines = new TreeMap();
				boolean hwInfoContained = false;
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(custFile.getInputStream()));
				String line = null;
				int lineNo = 0;
				Integer lineNoKey;
				
				while ((line = reader.readLine()) != null) {
					lineNo++;
					lineNoKey = new Integer(lineNo);
					
					if (line.trim().length() == 0 || line.charAt(0) == '#')
						continue;

					custLines.put(new Integer(lineNo), new String[]{line, null});
					
					position = "customer file line #" + lineNo;
					
					if (lineNo == 1) {
						if (line.startsWith( COL_NAME_LABEL )) {
							String[] labels = StarsUtils.splitString( line.substring(COL_NAME_LABEL.length()), "," );
							numCustCol = labels.length;
							
							for (int i = 0; i < labels.length; i++) {
								for (int j = 0; j < CUST_COLUMNS.length; j++) {
									if (labels[i].equalsIgnoreCase( CUST_COLUMNS[j] )) {
										custColIdx[j] = i;
										break;
									}
								}
								
								if (hwFile == null) {
									// There could be only one file which also contains hardware information
									for (int j = 0; j < HW_COLUMNS.length; j++) {
										if (labels[i].equalsIgnoreCase( HW_COLUMNS[j] )) {
											hwColIdx[j] = i;
											break;
										}
									}
								}
							}
							
							if (custColIdx[COL_ACCOUNT_NO] == -1)
								throw new WebClientException( "The required column '" + CUST_COLUMNS[COL_ACCOUNT_NO] + "' is missing" );
							
							hwInfoContained = (hwColIdx[COL_SERIAL_NO] != -1);
							if (hwInfoContained && hwColIdx[COL_DEVICE_TYPE] == -1)
								throw new WebClientException( "The required column '" + HW_COLUMNS[COL_DEVICE_TYPE] + "' is missing" );
							
							continue;
						}
						else {
							for (int i = 0; i < CUST_COLUMNS.length; i++)
								custColIdx[i] = i;
						}
					}
					
					String[] columns = StarsUtils.splitString( line, "," );
					if (columns.length > numCustCol) {
						errors++;
						String[] value = (String[]) custLines.get(lineNoKey);
						value[1] = "[line: " + lineNo + " error: Incorrect number of fields]";
						custLines.put(lineNoKey, value);
						continue;
					}

					String[] custFields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_ACCOUNT_FIELDS );
					custFields[ImportManagerUtil.IDX_LINE_NUM] = String.valueOf(lineNo);
					setCustomerFields( custFields, columns, custColIdx );

					if (custFields[ImportManagerUtil.IDX_ACCOUNT_NO].trim().length() == 0) {
						errors++;
						String[] value = (String[]) custLines.get(lineNoKey);
						value[1] = "[line: " + lineNo + " error: Account # cannot be empty]";
						custLines.put(lineNoKey, value);
						continue;
					}

					insertSpecified = custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION].equalsIgnoreCase("INSERT");
                    /*
                     * Some customers use rotation digits on the end of account numbers.
                     * EXAMPLE: if a customer changed at account 123456, the whole account number
                     * plus rotation digits might change like this: 12345610 to 12345620, so we want
                     * to only consider the accountnumber itself.  The number of digits to consider
                     * as valid comparable, non-rotation digits of the account number is expressed in a role property. 
                     */
                    LiteStarsCustAccountInformation liteAcctInfo = energyCompany.searchAccountByAccountNo( custFields[ImportManagerUtil.IDX_ACCOUNT_NO] );
					
					if (!preScan) {
						try {
							liteAcctInfo = importAccount( custFields, energyCompany );
						} catch (Exception ex) {
							errors++;
							String[] value = (String[]) custLines.get(lineNoKey);
							value[1] = "[line: " + lineNo + " error: " + ex.getMessage() + "]";
							custLines.put(lineNoKey, value);
							continue;
						}
					}
					else {
						if (custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION].equalsIgnoreCase("REMOVE")) {
							// The current record is a "REMOVE"
							String[] prevFields = (String[]) custFieldsMap.get( custFields[ImportManagerUtil.IDX_ACCOUNT_NO] );
							if (prevFields != null) {
								if (prevFields[ImportManagerUtil.IDX_ACCOUNT_ACTION].equalsIgnoreCase("REMOVE")) {
									// If a "REMOVE" record already exists in the list, do nothing but report a warning
									importLog.println( "WARNING at " + position + ": account #" + custFields[ImportManagerUtil.IDX_ACCOUNT_NO] + " already removed earlier, record ignored" );
									continue;
								}
								else {
									// Found a "INSERT" or "UPDATE" record in the list, remove the old record,
									// and add the new "REMOVE" record if necessary 
									if (!hwInfoContained) custFieldsList.remove( prevFields );
									if (hwInfoContained || !prevFields[ImportManagerUtil.IDX_ACCOUNT_ACTION].equalsIgnoreCase("INSERT"))
										custFieldsList.add( custFields );
									custFieldsMap.put( custFields[ImportManagerUtil.IDX_ACCOUNT_NO], custFields );
									
									if (liteAcctInfo != null) {
										LiteYukonUser login = DaoFactory.getContactDao().getYukonUser( liteAcctInfo.getCustomer().getPrimaryContactID() );
										if (login != null && login.getUserID() != UserUtils.USER_DEFAULT_ID && !removedUsernames.contains( login.getUsername() ))
											removedUsernames.add( login.getUsername() );
									}
									else if (prevFields[ImportManagerUtil.IDX_USERNAME].trim().length() > 0) {
										addedUsernames.remove( prevFields[ImportManagerUtil.IDX_USERNAME] );
									}
								}
							}
							else {
								if (liteAcctInfo != null) {
									// Found the account in the database, add the "REMOVE" record to the list
									custFieldsList.add( custFields );
									custFieldsMap.put( custFields[ImportManagerUtil.IDX_ACCOUNT_NO], custFields );
									
									LiteYukonUser login = DaoFactory.getContactDao().getYukonUser( liteAcctInfo.getCustomer().getPrimaryContactID() );
									if (login != null && login.getUserID() != UserUtils.USER_DEFAULT_ID && !removedUsernames.contains( login.getUsername() ))
										removedUsernames.add( login.getUsername() );
								}
								else {
									// Account not found in the database, do nothing but report a warning
									importLog.println( "WARNING at " + position + ": account #" + custFields[ImportManagerUtil.IDX_ACCOUNT_NO] + " doesn't exist, record ignored" );
									continue;
								}
							}
						}
						else {
							// The current record is a "INSERT" or "UPDATE"
							boolean checkUsername = false;
							
							String[] prevFields = (String[]) custFieldsMap.get( custFields[ImportManagerUtil.IDX_ACCOUNT_NO] );
							if (prevFields != null) {
								if (prevFields[ImportManagerUtil.IDX_ACCOUNT_ACTION].equalsIgnoreCase("REMOVE")) {
									// If a "REMOVE" record already exists in the list, add the new record with action set to "INSERT"
									custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION] = "INSERT";
									custFieldsList.add( custFields );
									custFieldsMap.put( custFields[ImportManagerUtil.IDX_ACCOUNT_NO], custFields );
									
									checkUsername = custFields[ImportManagerUtil.IDX_USERNAME].trim().length() > 0;
								}
								else {
									// If a "INSERT" or "UPDATE" record already exists in the list, replace the old record
									// with the new record except the action field, also keep the old user login fields if provided
									if (prevFields[ImportManagerUtil.IDX_USERNAME].trim().length() > 0) {
										custFields[ImportManagerUtil.IDX_USERNAME] = prevFields[ImportManagerUtil.IDX_USERNAME];
										custFields[ImportManagerUtil.IDX_PASSWORD] = prevFields[ImportManagerUtil.IDX_PASSWORD];
									}
									
									if (!hwInfoContained) {
										custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION] = prevFields[ImportManagerUtil.IDX_ACCOUNT_ACTION];
										custFieldsList.remove( prevFields );
									}
									else
										custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION] = "UPDATE";
									
									custFieldsList.add( custFields );
									custFieldsMap.put( custFields[ImportManagerUtil.IDX_ACCOUNT_NO], custFields );
									
									checkUsername = prevFields[ImportManagerUtil.IDX_USERNAME].trim().length() == 0
											&& custFields[ImportManagerUtil.IDX_USERNAME].trim().length() > 0;
								}
							}
							else {
								// Add the new record to the list with the action field set accordingly
								if (liteAcctInfo != null) {
									custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION] = "UPDATE";
									
									LiteYukonUser login = DaoFactory.getContactDao().getYukonUser( liteAcctInfo.getCustomer().getPrimaryContactID() );
									if (login != null && login.getUserID() != UserUtils.USER_DEFAULT_ID)
										custFields[ImportManagerUtil.IDX_USERNAME] = login.getUsername();
									else
										checkUsername = custFields[ImportManagerUtil.IDX_USERNAME].trim().length() > 0;
								}
								else {
									custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION] = "INSERT";
									checkUsername = custFields[ImportManagerUtil.IDX_USERNAME].trim().length() > 0;
								}
								
								custFieldsList.add( custFields );
								custFieldsMap.put( custFields[ImportManagerUtil.IDX_ACCOUNT_NO], custFields );
							}
							
							// Check if the username already exists
							if (checkUsername) {
								if (custFields[ImportManagerUtil.IDX_PASSWORD].length() == 0) {
									errors++;
									String[] value = (String[]) custLines.get(lineNoKey);
									value[1] = "[line: " + lineNo + " error: Password cannot be empty]";
									custLines.put(lineNoKey, value);
									continue;
								}	

								String username = custFields[ImportManagerUtil.IDX_USERNAME];
								if (addedUsernames.contains( username )) {
									errors++;
									String[] value = (String[]) custLines.get(lineNoKey);
									value[1] = "[line: " + lineNo + " error: Username would have already been added by the import program]";
									custLines.put(lineNoKey, value);
									continue;
								}

								if (removedUsernames.contains( username ))
									removedUsernames.remove( username );
								else if (DaoFactory.getYukonUserDao().getLiteYukonUser( username ) != null) {
									errors++;
									String[] value = (String[]) custLines.get(lineNoKey);
									value[1] = "[line: " + lineNo + " error: Username already exists]";
									custLines.put(lineNoKey, value);
									continue;
								}
								addedUsernames.add( username );
							}
						}
					}
					
					if (hwInfoContained) {
						if (preScan) {
							if (hwFieldsList == null) hwFieldsList = new ArrayList();
							if (hwColIdx[COL_APP_TYPE] != -1 && appFieldsList == null)
								appFieldsList = new ArrayList();
						}
						
						String[] hwFields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_INV_FIELDS );
						hwFields[ImportManagerUtil.IDX_LINE_NUM] = String.valueOf(lineNo);
						setHardwareFields( hwFields, columns, hwColIdx );
						
						/*
                         * Need to check for seasonal load option (same relay, different app, same switch, different prog
						 */
                        if(hwFields[ImportManagerUtil.IDX_OPTION_PARAMS].trim().length() > 0)
                        {
                            seasonalLoad = hwFields[ImportManagerUtil.IDX_OPTION_PARAMS].equalsIgnoreCase(ImportManagerUtil.SEASONAL_LOAD_OPTION);
                        }
                        
						if (custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION].equalsIgnoreCase("REMOVE")
							|| hwFields[ImportManagerUtil.IDX_SERIAL_NO].trim().length() == 0)
						{
							// If customer action is "REMOVE", or serial # field is empty, this record is for customer action only
							if (preScan) {
								hwFieldsList.add( null );
								if (appFieldsList != null) appFieldsList.add( null );
							}
							continue;
						}
						
						if (hwFields[ImportManagerUtil.IDX_DEVICE_TYPE].trim().length() == 0) {
							errors++;
							String[] value = (String[]) custLines.get(lineNoKey);
							value[1] = "[line: " + lineNo + " error: Device type cannot be empty]";
							custLines.put(lineNoKey, value);
							continue;
						}

                        insertSpecified = hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION].equalsIgnoreCase("INSERT");
                        
						YukonSelectionList devTypeList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
						YukonListEntry deviceType = DaoFactory.getYukonListDao().getYukonListEntry( devTypeList, hwFields[ImportManagerUtil.IDX_DEVICE_TYPE] );
						if (deviceType == null) {
							errors++;
							String[] value = (String[]) custLines.get(lineNoKey);
							value[1] = "[line: " + lineNo + " error: Invalid device type \"" + hwFields[ImportManagerUtil.IDX_DEVICE_TYPE] + "\"]";
							custLines.put(lineNoKey, value);
							continue;
						}
						String[] appFields = null;
						if (hwColIdx[COL_APP_TYPE] != -1) {
							appFields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
							setApplianceFields( appFields, columns, hwColIdx );
						}
						
						if (!preScan) {
							try {
								LiteInventoryBase liteInv = importHardware( hwFields, liteAcctInfo, energyCompany );

								if (hwFields[ImportManagerUtil.IDX_PROGRAM_NAME].trim().length() > 0
										&& !hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION].equalsIgnoreCase("REMOVE"))
								{

									int[][] programs = getEnrolledProgram( hwFields, energyCompany );
									if (programs != null) {
										programSignUp( programs, appFields, liteAcctInfo, liteInv, energyCompany );
									}
								}
							} catch (WebClientException e) {
								errors++;
								String[] value = (String[]) custLines.get(lineNoKey);
								value[1] = "[line: " + lineNo + " error: " + e.getMessage() + "]";
								custLines.put(lineNoKey, value);
								continue;
							}
						}
						else {
							String acctNo = (String) hwFieldsMap.get( hwFields[ImportManagerUtil.IDX_SERIAL_NO] );
							if (acctNo == null) {
								LiteStarsLMHardware liteHw = energyCompany.searchForLMHardware( deviceType.getEntryID(), hwFields[ImportManagerUtil.IDX_SERIAL_NO] );
								if (liteHw != null && liteHw.getAccountID() > 0)
									acctNo = energyCompany.getBriefCustAccountInfo( liteHw.getAccountID(), true ).getCustomerAccount().getAccountNumber();
							}
							
							if (hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION].equalsIgnoreCase("REMOVE")) {
								// Remove a hardware from an account, if hardware doesn't exist in the account, report a warning
								if (acctNo == null || !acctNo.equalsIgnoreCase( custFields[ImportManagerUtil.IDX_ACCOUNT_NO] )) {
									importLog.println( "WARNING at " + position + ": serial #" + hwFields[ImportManagerUtil.IDX_SERIAL_NO] + " not found in the customer account" );
									if (preScan) {
										hwFieldsList.add( null );
										if (appFieldsList != null) appFieldsList.add( null );
									}
									continue;
								}
								
								if (preScan) {
									hwFieldsList.add( hwFields );
									hwFieldsMap.put( hwFields[ImportManagerUtil.IDX_SERIAL_NO], "" );
								}
							}
							else {
								// Insert/update a hardware in an account, if hardware already exists in another account, report an error 
								if (acctNo != null && !acctNo.equals("") && !acctNo.equalsIgnoreCase( custFields[ImportManagerUtil.IDX_ACCOUNT_NO] )) {
									errors++;
									String[] value = (String[]) custLines.get(lineNoKey);
									value[1] = "[line: " + lineNo + " error: Cannot import hardware, serial #" + hwFields[ImportManagerUtil.IDX_SERIAL_NO] + " already exists in account #" + acctNo + "]";
									custLines.put(lineNoKey, value);
									continue;
								}

								if (acctNo != null && acctNo.equals( custFields[ImportManagerUtil.IDX_ACCOUNT_NO] ))
									hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION] = "UPDATE";
								else
									hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION] = "INSERT";
								
								if (preScan) {
									hwFieldsList.add( hwFields );
									hwFieldsMap.put( hwFields[ImportManagerUtil.IDX_SERIAL_NO], custFields[ImportManagerUtil.IDX_ACCOUNT_NO] );
								}
							}
							
							if (appFieldsList != null) appFieldsList.add( appFields );
						}
					}
					
					if (isCanceled) {
						status = STATUS_CANCELED;
						throw new Exception();
					}
				}
				
				numAcctTotal = custFieldsList.size();
			}

			if (errors != 0) throw new Exception("Failed to import " + errors + " lines");
			
			if (hwFile != null) {
				hwFieldsList = new ArrayList();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(hwFile.getInputStream()));
				String line = null;
				hwLines = new TreeMap();
				int lineNo = 0;
				Integer lineNoKey;
				
				while ((line = reader.readLine()) != null) {
					lineNo++;
					lineNoKey = new Integer(lineNo);
					
					if (line.trim().length() == 0 || line.charAt(0) == '#')
						continue;

					hwLines.put(new Integer(lineNo), new String[]{line, null});
					
					position = "hardware file line #" + lineNo;
					
					if (lineNo == 1) {
						if (line.startsWith( COL_NAME_LABEL )) {
							String[] labels = StarsUtils.splitString( line.substring(COL_NAME_LABEL.length()), "," );
							numHwCol = labels.length;
							
							for (int i = 0; i < labels.length; i++) {
								for (int j = 0; j < HW_COLUMNS.length; j++) {
									if (labels[i].equalsIgnoreCase( HW_COLUMNS[j] )) {
										hwColIdx[j] = i;
										break;
									}
								}
							}
							
							if (hwColIdx[COL_ACCOUNT_NO] == -1)
								throw new WebClientException( "The required column '" + HW_COLUMNS[COL_ACCOUNT_NO] + "' is missing" );
							if (hwColIdx[COL_SERIAL_NO] == -1)
								throw new WebClientException( "The required column '" + HW_COLUMNS[COL_SERIAL_NO] + "' is missing" );
							if (hwColIdx[COL_DEVICE_TYPE] == -1)
								throw new WebClientException( "The required column '" + HW_COLUMNS[COL_DEVICE_TYPE] + "' is missing" );
							
							continue;
						}
						else {
							for (int i = 0; i < HW_COLUMNS.length; i++)
								hwColIdx[i] = i;
						}
					}
					
					String[] columns = StarsUtils.splitString( line, "," );;
					if (columns.length > numHwCol) {
						errors++;
						String[] value = (String[]) hwLines.get(lineNoKey);
						value[1] = "[line: " + lineNo + " error: Incorrect number of fields]";
						custLines.put(lineNoKey, value);
						continue;
					}

					String[] hwFields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_INV_FIELDS );
					hwFields[ImportManagerUtil.IDX_LINE_NUM] = String.valueOf(lineNo);
					setHardwareFields( hwFields, columns, hwColIdx );
					
					if (hwFields[ImportManagerUtil.IDX_ACCOUNT_ID].trim().length() == 0) {
						errors++;
						String[] value = (String[]) hwLines.get(lineNoKey);
						value[1] = "[line: " + lineNo + " error: Account # cannot be empty]";
						custLines.put(lineNoKey, value);
						continue;
					}
					if (hwFields[ImportManagerUtil.IDX_SERIAL_NO].trim().length() == 0) {
						errors++;
						String[] value = (String[]) hwLines.get(lineNoKey);
						value[1] = "[line: " + lineNo + " error: Serial # cannot be empty]";
						custLines.put(lineNoKey, value);
						continue;
					}
					if (hwFields[ImportManagerUtil.IDX_DEVICE_TYPE].trim().length() == 0) {
						errors++;
						String[] value = (String[]) hwLines.get(lineNoKey);
						value[1] = "[line: " + lineNo + " error: Device type cannot be empty]";
						custLines.put(lineNoKey, value);
						continue;
					}
					
					YukonSelectionList devTypeList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
					YukonListEntry deviceType = DaoFactory.getYukonListDao().getYukonListEntry( devTypeList, hwFields[ImportManagerUtil.IDX_DEVICE_TYPE] );
					if (deviceType == null) {
						errors++;
						String[] value = (String[]) hwLines.get(lineNoKey);
						value[1] = "[line: " + lineNo + " error: Invalid device type \"" + hwFields[ImportManagerUtil.IDX_DEVICE_TYPE] + "\"]";
						custLines.put(lineNoKey, value);
						continue;
					}
					
					String[] custFields = null;
					if (preScan)
						custFields = (String[]) custFieldsMap.get( hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					LiteStarsCustAccountInformation liteAcctInfo = null;
					
                    /*
                     * Need to check for seasonal load option (same relay, different app, same switch, different prog
                     */
                    if(hwFields[ImportManagerUtil.IDX_OPTION_PARAMS].trim().length() > 0)
                    {
                        seasonalLoad = hwFields[ImportManagerUtil.IDX_OPTION_PARAMS].equalsIgnoreCase(ImportManagerUtil.SEASONAL_LOAD_OPTION);
                    }
                    
					if (custFields != null) {
						if (custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION].equals("REMOVE")) {
							if (hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION].equalsIgnoreCase("REMOVE")) {
								importLog.println( "WARNING at " + position + ": account #" + hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] + " is removed by the import program, record ignored" );
								continue;
							}
							else {
								errors++;
								String[] value = (String[]) hwLines.get(lineNoKey);
								value[1] = "[line: " + lineNo + " error: Cannot import hardware, account #" + hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] + " will be removed by the import program]";
								custLines.put(lineNoKey, value);
								continue;
							}
						}
					}
					else 
                    {
                        /*
                         * Some customers use rotation digits on the end of account numbers.
                         * EXAMPLE: if a customer changed at account 123456, the whole account number
                         * plus rotation digits might change like this: 12345610 to 12345620, so we want
                         * to only consider the accountnumber itself.  The number of digits to consider
                         * as valid comparable, non-rotation digits of the account number is expressed in a role property. 
                         */
                        liteAcctInfo = energyCompany.searchAccountByAccountNo( hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] );
						if (liteAcctInfo == null) {
							if (hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION].equalsIgnoreCase("REMOVE")) {
								importLog.println( "WARNING at " + position + ": account #" + hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] + " doesn't exist, record ignored" );
								continue;
							}
							else {
								errors++;
								String[] value = (String[]) hwLines.get(lineNoKey);
								value[1] = "[line: " + lineNo + " error: Cannot import hardware, account #" + hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] + " doesn't exist]";
								custLines.put(lineNoKey, value);
								continue;
							}
						}
					}
					
					String[] appFields = null;
					if (hwColIdx[COL_APP_TYPE] != -1) {
						appFields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
						setApplianceFields( appFields, columns, hwColIdx );
						if (preScan && appFieldsList == null)
							appFieldsList = new ArrayList();
					}
					
					if (!preScan) {
						LiteInventoryBase liteInv;
						try {
							liteInv = importHardware( hwFields, liteAcctInfo, energyCompany );                            

							if (hwFields[ImportManagerUtil.IDX_PROGRAM_NAME].trim().length() > 0
									&& !hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION].equalsIgnoreCase("REMOVE"))
							{

								int[][] programs = getEnrolledProgram( hwFields, energyCompany );
								if (programs != null) {
									programSignUp( programs, appFields, liteAcctInfo, liteInv, energyCompany );
								}
							}
						} catch (WebClientException e) {
							errors++;
							String[] value = (String[]) hwLines.get(lineNoKey);
							value[1] = "[line: " + lineNo + " error: " + e.getMessage() + "]";
							custLines.put(lineNoKey, value);
							continue;
						}
					}
					else {
						String acctNo = (String) hwFieldsMap.get( hwFields[ImportManagerUtil.IDX_SERIAL_NO] );
						if (acctNo == null) {
							LiteStarsLMHardware liteHw = energyCompany.searchForLMHardware( deviceType.getEntryID(), hwFields[ImportManagerUtil.IDX_SERIAL_NO] );
							if (liteHw != null && liteHw.getAccountID() > 0)
								acctNo = energyCompany.getBriefCustAccountInfo( liteHw.getAccountID(), true ).getCustomerAccount().getAccountNumber();
						}
						
						if (hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION].equalsIgnoreCase("REMOVE")) {
							if (acctNo == null || !acctNo.equalsIgnoreCase( hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] )) {
								importLog.println( "WARNING at " + position + ": serial #" + hwFields[ImportManagerUtil.IDX_SERIAL_NO] + " not found in the customer account, record ignored" );
								continue;
							}
							
							if (preScan) {
								hwFieldsList.add( hwFields );
								hwFieldsMap.put( hwFields[ImportManagerUtil.IDX_SERIAL_NO], "" );
							}
						}
						else {
							if (acctNo != null && !acctNo.equals("") && (custFields != null && !acctNo.equalsIgnoreCase( custFields[ImportManagerUtil.IDX_ACCOUNT_NO] ))) {
								errors++;
								String[] value = (String[]) hwLines.get(lineNoKey);
								value[1] = "[line: " + lineNo + " error: Cannot import hardware, serial #" + hwFields[ImportManagerUtil.IDX_SERIAL_NO] + " already exists in another account]";
								custLines.put(lineNoKey, value);
								continue;
							}

							if (acctNo != null && acctNo.equals( hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] ))
								hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION] = "UPDATE";
							else
								hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION] = "INSERT";
							
							if (preScan) {
								hwFieldsList.add( hwFields );
								hwFieldsMap.put( hwFields[ImportManagerUtil.IDX_SERIAL_NO], hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] );
							}
						}
						
						if (appFieldsList != null) appFieldsList.add( appFields );
					}
					
					if (isCanceled) {
						status = STATUS_CANCELED;
						throw new Exception();
					}
				}
				
				numHwTotal = hwFieldsList.size();
			}

			if (errors != 0) {
				/*
				final String separator = System.getProperty("file.separator");
				final String fileName = ServerUtils.getStarsTempDir() + separator + "stars_import.log";
				final String csvFileName = ServerUtils.getStarsTempDir() + separator + "stars_import.csv";

				final java.io.FileWriter writer = new java.io.FileWriter(new File(fileName));
				final java.io.FileWriter csvWriter = new java.io.FileWriter(new File(csvFileName));

				for (int x = 0; x < errorList.size(); x++) {
					String line = (String) errorList.get(x);
					writer.write(line + LINE_SEPARATOR);
					writer.flush();

					String csvLine = line.split("\\]\\s")[1];
					csvWriter.write(csvLine + LINE_SEPARATOR);
					csvWriter.flush();
				}

				writer.close();
				csvWriter.close();
				 */
				throw new Exception("Failed to import " + errors + " lines");
			}
			
			status = STATUS_FINISHED;
		}
		catch (Exception e) 
        {
			if (status == STATUS_CANCELED) {
				errorMsg = "Operation was canceled by user";
				if (position != null)
					errorMsg += " after " + position + " is processed";
			}
			else {
				CTILogger.error( e.getMessage(), e );
				status = STATUS_ERROR;

				errorMsg = e.getMessage();
				/*
				if (position != null)
					errorMsg = "An error occured at " + position;
				else
					errorMsg = "The Import process failed";
				if (e instanceof WebClientException)
					errorMsg += ": " + e.getMessage();
				 */
			}

            ActivityLogger.logEvent( userID, ActivityLogActions.IMPORT_CUSTOMER_ACCOUNT_ACTION, errorMsg );
		}
		//finally {
		if (importLog != null) {
			importLog.println();
			importLog.println("Stop time: " + StarsUtils.formatDate( new Date(), energyCompany.getDefaultTimeZone() ));
			importLog.println();
			if (errorMsg != null) importLog.println( errorMsg );
			importLog.println(getImportProgress());
			importLog.close();

			try {
				if (email != null && email.trim().length() > 0 && !preScan)
					sendImportLog( logFile, email, energyCompany );
			}
			catch (Exception e) {
				status = STATUS_ERROR;
				if (errorMsg != null)
					errorMsg += LINE_SEPARATOR + "Failed to send the import log by email";
				else
					errorMsg = "Failed to send the import log by email";
			}
		}
		//}

	}
	
	private String getImportProgress() 
    {
		String msg = "";
		if (status == STATUS_FINISHED) 
        {
			if (custFile != null)
				msg += numAcctImported + " customer accounts imported successfully" +
					" (" + numAcctAdded + " added, " + numAcctUpdated + " updated, " + numAcctRemoved + " removed)";
			if (hwFile != null) 
            {
				if (msg.length() > 0) msg += LINE_SEPARATOR;
				msg += numHwImported + " hardware imported successfully" +
					" (" + numHwAdded + " added, " + numHwUpdated + " updated, " + numHwRemoved + " removed)";
			}
            if(firstFinishedPass)
                ActivityLogger.logEvent( userID, ActivityLogActions.IMPORT_CUSTOMER_ACCOUNT_ACTION, msg );
            firstFinishedPass = false;
		}
		else {
			if (custFile != null) {
				if (numAcctTotal == 0)
					msg += numAcctImported + " customer accounts imported";
				else
					msg += numAcctImported + " of " + numAcctTotal + " customer accounts imported";
				msg += " (" + numAcctAdded + " added, " + numAcctUpdated + " updated, " + numAcctRemoved + " removed)";
			}
			if (hwFile != null) {
				if (msg.length() > 0) msg += LINE_SEPARATOR;
				if (numHwTotal == 0)
					msg += numHwImported + " hardware imported successfully";
				else
					msg += numHwImported + " of " + numHwTotal + " hardware imported";
				msg += " (" + numHwAdded + " added, " + numHwUpdated + " updated, " + numHwRemoved + " removed)";
			}
		}
        
		return msg;
	}
	
	private void setCustomerFields(String[] fields, String[] columns, int[] colIdx) {
		if (colIdx[COL_ACCOUNT_NO] >= 0 && colIdx[COL_ACCOUNT_NO] < columns.length)
			fields[ImportManagerUtil.IDX_ACCOUNT_NO] = columns[ colIdx[COL_ACCOUNT_NO] ];
		if (colIdx[COL_CUST_ACTION] >= 0 && colIdx[COL_CUST_ACTION] < columns.length)
			fields[ImportManagerUtil.IDX_ACCOUNT_ACTION] = columns[ colIdx[COL_CUST_ACTION] ];
		if (colIdx[COL_LAST_NAME] >= 0 && colIdx[COL_LAST_NAME] < columns.length)
			fields[ImportManagerUtil.IDX_LAST_NAME] = columns[ colIdx[COL_LAST_NAME] ];
		if (colIdx[COL_FIRST_NAME] >= 0 && colIdx[COL_FIRST_NAME] < columns.length)
			fields[ImportManagerUtil.IDX_FIRST_NAME] = columns[ colIdx[COL_FIRST_NAME] ];
		if (colIdx[COL_LAST_NAME] >= 0 && colIdx[COL_LAST_NAME] < columns.length)
			fields[ImportManagerUtil.IDX_LAST_NAME] = columns[ colIdx[COL_LAST_NAME] ];
		if (colIdx[COL_HOME_PHONE] >= 0 && colIdx[COL_HOME_PHONE] < columns.length)
			fields[ImportManagerUtil.IDX_HOME_PHONE] = columns[ colIdx[COL_HOME_PHONE] ];
		if (colIdx[COL_WORK_PHONE] >= 0 && colIdx[COL_WORK_PHONE] < columns.length)
			fields[ImportManagerUtil.IDX_WORK_PHONE] = columns[ colIdx[COL_WORK_PHONE] ];
		if (colIdx[COL_EMAIL] >= 0 && colIdx[COL_EMAIL] < columns.length)
			fields[ImportManagerUtil.IDX_EMAIL] = columns[ colIdx[COL_EMAIL] ];
		if (colIdx[COL_STREET_ADDR1] >= 0 && colIdx[COL_STREET_ADDR1] < columns.length)
			fields[ImportManagerUtil.IDX_STREET_ADDR1] = columns[ colIdx[COL_STREET_ADDR1] ];
		if (colIdx[COL_STREET_ADDR2] >= 0 && colIdx[COL_STREET_ADDR2] < columns.length)
			fields[ImportManagerUtil.IDX_STREET_ADDR2] = columns[ colIdx[COL_STREET_ADDR2] ];
		if (colIdx[COL_CITY] >= 0 && colIdx[COL_CITY] < columns.length)
			fields[ImportManagerUtil.IDX_CITY] = columns[ colIdx[COL_CITY] ];
		if (colIdx[COL_STATE] >= 0 && colIdx[COL_STATE] < columns.length)
			fields[ImportManagerUtil.IDX_STATE] = columns[ colIdx[COL_STATE] ];
		if (colIdx[COL_COUNTY] >= 0 && colIdx[COL_COUNTY] < columns.length)
			fields[ImportManagerUtil.IDX_COUNTY] = columns[ colIdx[COL_COUNTY] ];
		if (colIdx[COL_ZIP_CODE] >= 0 && colIdx[COL_ZIP_CODE] < columns.length)
			fields[ImportManagerUtil.IDX_ZIP_CODE] = columns[ colIdx[COL_ZIP_CODE] ];
		if (colIdx[COL_MAP_NO] >= 0 && colIdx[COL_MAP_NO] < columns.length)
			fields[ImportManagerUtil.IDX_MAP_NO] = columns[ colIdx[COL_MAP_NO] ];
		if (colIdx[COL_SUBSTATION] >= 0 && colIdx[COL_SUBSTATION] < columns.length && columns[colIdx[COL_SUBSTATION]].trim().length() > 0)
			fields[ImportManagerUtil.IDX_SUBSTATION] = "\"" + columns[ colIdx[COL_SUBSTATION] ] + "\"";
		if (colIdx[COL_FEEDER] >= 0 && colIdx[COL_FEEDER] < columns.length)
			fields[ImportManagerUtil.IDX_FEEDER] = columns[ colIdx[COL_FEEDER] ];
		if (colIdx[COL_POLE] >= 0 && colIdx[COL_POLE] < columns.length)
			fields[ImportManagerUtil.IDX_POLE] = columns[ colIdx[COL_POLE] ];
		if (colIdx[COL_TRFM_SIZE] >= 0 && colIdx[COL_TRFM_SIZE] < columns.length)
			fields[ImportManagerUtil.IDX_TRFM_SIZE] = columns[ colIdx[COL_TRFM_SIZE] ];
		if (colIdx[COL_SERV_VOLT] >= 0 && colIdx[COL_SERV_VOLT] < columns.length)
			fields[ImportManagerUtil.IDX_SERV_VOLT] = columns[ colIdx[COL_SERV_VOLT] ];
		if (colIdx[COL_USERNAME] >= 0 && colIdx[COL_USERNAME] < columns.length)
			fields[ImportManagerUtil.IDX_USERNAME] = columns[ colIdx[COL_USERNAME] ];
		if (colIdx[COL_PASSWORD] >= 0 && colIdx[COL_PASSWORD] < columns.length)
			fields[ImportManagerUtil.IDX_PASSWORD] = columns[ colIdx[COL_PASSWORD] ];
		if (colIdx[COL_LOGIN_GROUP] >= 0 && colIdx[COL_LOGIN_GROUP] < columns.length)
			fields[ImportManagerUtil.IDX_LOGIN_GROUP] = columns[ colIdx[COL_LOGIN_GROUP] ];
        if (colIdx[COL_COMPANY_NAME] >= 0 && colIdx[COL_COMPANY_NAME] < columns.length)
            fields[ImportManagerUtil.IDX_COMPANY_NAME] = columns[ colIdx[COL_COMPANY_NAME] ];
        if (colIdx[COL_IVR_PIN] >= 0 && colIdx[COL_IVR_PIN] < columns.length)
            fields[ImportManagerUtil.IDX_IVR_PIN] = columns[ colIdx[COL_IVR_PIN] ];
        if (colIdx[COL_IVR_USERNAME] >= 0 && colIdx[COL_IVR_USERNAME] < columns.length)
            fields[ImportManagerUtil.IDX_IVR_USERNAME] = columns[ colIdx[COL_IVR_USERNAME] ];
    }
	
	private void setHardwareFields(String[] fields, String[] columns, int[] colIdx) {
		if (colIdx[COL_HW_ACCOUNT_NO] >= 0 && colIdx[COL_HW_ACCOUNT_NO] < columns.length)
			fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[ colIdx[COL_HW_ACCOUNT_NO] ];	// Use account ID field to store account #
		if (colIdx[COL_HW_ACTION] >= 0 && colIdx[COL_HW_ACTION] < columns.length)
			fields[ImportManagerUtil.IDX_HARDWARE_ACTION] = columns[ colIdx[COL_HW_ACTION] ];
		if (colIdx[COL_SERIAL_NO] >= 0 && colIdx[COL_SERIAL_NO] < columns.length)
			fields[ImportManagerUtil.IDX_SERIAL_NO] = columns[ colIdx[COL_SERIAL_NO] ];
		if (colIdx[COL_DEVICE_TYPE] >= 0 && colIdx[COL_DEVICE_TYPE] < columns.length)
			fields[ImportManagerUtil.IDX_DEVICE_TYPE] = columns[ colIdx[COL_DEVICE_TYPE] ];
		if (colIdx[COL_INSTALL_DATE] >= 0 && colIdx[COL_INSTALL_DATE] < columns.length)
			fields[ImportManagerUtil.IDX_INSTALL_DATE] = columns[ colIdx[COL_INSTALL_DATE] ];
		if (colIdx[COL_REMOVE_DATE] >= 0 && colIdx[COL_REMOVE_DATE] < columns.length)
			fields[ImportManagerUtil.IDX_REMOVE_DATE] = columns[ colIdx[COL_REMOVE_DATE] ];
		if (colIdx[COL_SERVICE_COMPANY] >= 0 && colIdx[COL_SERVICE_COMPANY] < columns.length && !columns[colIdx[COL_SERVICE_COMPANY]].trim().equals(""))
			fields[ImportManagerUtil.IDX_SERVICE_COMPANY] = "\"" + columns[ colIdx[COL_SERVICE_COMPANY] ] + "\"";
		if (colIdx[COL_PROGRAM_NAME] >= 0 && colIdx[COL_PROGRAM_NAME] < columns.length)
			fields[ImportManagerUtil.IDX_PROGRAM_NAME] = columns[ colIdx[COL_PROGRAM_NAME] ];
		if (colIdx[COL_ADDR_GROUP] >= 0 && colIdx[COL_ADDR_GROUP] < columns.length)
			fields[ImportManagerUtil.IDX_ADDR_GROUP] = columns[ colIdx[COL_ADDR_GROUP] ];
        if (colIdx[COL_OPTION_PARAMS] >= 0 && colIdx[COL_OPTION_PARAMS] < columns.length)
            fields[ImportManagerUtil.IDX_OPTION_PARAMS] = columns[ colIdx[COL_OPTION_PARAMS] ];
        if (colIdx[COL_DEVICE_LABEL] >= 0 && colIdx[COL_DEVICE_LABEL] < columns.length)
            fields[ImportManagerUtil.IDX_DEVICE_LABEL] = columns[ colIdx[COL_DEVICE_LABEL] ];
	}
	
	private void setApplianceFields(String[] fields, String[] columns, int[] colIdx) {
		if (colIdx[COL_APP_TYPE] >= 0 && colIdx[COL_APP_TYPE] < columns.length)
			fields[ImportManagerUtil.IDX_APP_TYPE] = columns[ colIdx[COL_APP_TYPE] ];
		if (colIdx[COL_APP_KW] >= 0 && colIdx[COL_APP_KW] < columns.length)
			fields[ImportManagerUtil.IDX_APP_KW] = columns[ colIdx[COL_APP_KW] ];
		if (colIdx[COL_APP_RELAY_NUMBER] >= 0 && colIdx[COL_APP_RELAY_NUMBER] < columns.length)
            fields[ImportManagerUtil.IDX_RELAY_NUM] = columns[ colIdx[COL_APP_RELAY_NUMBER] ];
	}
	
	private int[][] getEnrolledProgram(String[] fields, LiteStarsEnergyCompany energyCompany) throws WebClientException {
        List<LiteLMProgramWebPublishing> programs = energyCompany.getAllPrograms();
		
		synchronized (programs) {
			for (int i = 0; i < programs.size(); i++) {
				LiteLMProgramWebPublishing liteProg = programs.get(i);
				String progName = StarsUtils.getPublishedProgramName( liteProg );

				if (progName.equalsIgnoreCase( fields[ImportManagerUtil.IDX_PROGRAM_NAME] )) {
					int[] suProg = new int[4];
					suProg[0] = liteProg.getProgramID();
					suProg[1] = -1;	// ApplianceCategoryID (optional)
					suProg[2] = -1;	// GroupID (optional)
					suProg[3] = -1;	// LoadNumber (optional)
					
					if (fields[ImportManagerUtil.IDX_ADDR_GROUP].trim().length() > 0) {
						for (int j = 0; j < liteProg.getGroupIDs().length; j++) 
                        {
                            try
                            {
                                if (DaoFactory.getPaoDao().getYukonPAOName( liteProg.getGroupIDs()[j] ).equalsIgnoreCase( fields[ImportManagerUtil.IDX_ADDR_GROUP] )) 
                                {
                                    suProg[2] = liteProg.getGroupIDs()[j];
                                    break;
                                }
                            }
                            catch(NotFoundException e) {}
						}
						
						if (suProg[2] == -1)
							throw new WebClientException( "The group '" + fields[ImportManagerUtil.IDX_ADDR_GROUP] + "' doesn't belong to program '" + progName + "'" );
					}
					
					return new int[][] { suProg };
				}
			}
		}
		
		throw new WebClientException( "Program '" + fields[ImportManagerUtil.IDX_PROGRAM_NAME] + "' is not found in the published programs" );
	}
	
	private LiteInventoryBase importHardware(String[] hwFields, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
		throws Exception
	{
		LiteInventoryBase liteInv = null;
		ImportProblem problem = new ImportProblem();
		
		for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
			int invID = ((Integer) liteAcctInfo.getInventories().get(i)).intValue();
			LiteInventoryBase lInv = energyCompany.getInventoryBrief( invID, true );
			if (lInv instanceof LiteStarsLMHardware && ((LiteStarsLMHardware)lInv).getManufacturerSerialNumber().equals(hwFields[ImportManagerUtil.IDX_SERIAL_NO])) {
				liteInv = lInv;
				break;
			}
		}
		
		if (hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION].equalsIgnoreCase( "REMOVE" )) {
			if (liteInv == null)
				throw new WebClientException("Cannot remove hardware, serial #" + hwFields[ImportManagerUtil.IDX_SERIAL_NO] + " not found in the customer account");
			ImportManagerUtil.removeLMHardware( hwFields, liteAcctInfo, energyCompany, problem );
			numHwRemoved++;
		}
		else if (liteInv == null) {
			liteInv = ImportManagerUtil.insertLMHardware( hwFields, liteAcctInfo, energyCompany, true, problem );
			numHwAdded++;
		}
		else if (!insertSpecified) {
			liteInv = ImportManagerUtil.updateLMHardware( hwFields, liteInv, liteAcctInfo, energyCompany, problem );
			numHwUpdated++;
		}
		
		if (problem.getProblem() != null)
			importLog.println( "WARNING at " + position + ": " + problem.getProblem() );
		
		numHwImported++;
		return liteInv;
	}
	
	private LiteStarsCustAccountInformation importAccount(String[] custFields, LiteStarsEnergyCompany energyCompany)
		throws Exception
	{
		LiteStarsCustAccountInformation liteAcctInfo = energyCompany.searchAccountByAccountNo( custFields[ImportManagerUtil.IDX_ACCOUNT_NO] );
		
		if (custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION].equalsIgnoreCase( "REMOVE" )) {
			if (liteAcctInfo == null)
				throw new WebClientException( "Cannot delete customer account: account #" + custFields[ImportManagerUtil.IDX_ACCOUNT_NO] + " doesn't exist" );
			DeleteCustAccountAction.deleteCustomerAccount( liteAcctInfo, energyCompany );
			
			numAcctRemoved++;
			numAcctImported++;
			return null;
		}
		else {
			ImportProblem problem = new ImportProblem();
			
			if (liteAcctInfo == null) {
				liteAcctInfo = ImportManagerUtil.newCustomerAccount( custFields, energyCompany, false, problem );
				numAcctAdded++;
			}
			else if (!insertSpecified) {
				ImportManagerUtil.updateCustomerAccount( custFields, liteAcctInfo, energyCompany, problem );
				numAcctUpdated++;
			}
			
			if (problem.getProblem() != null)
				importLog.println( "WARNING at " + position + ": " + problem.getProblem() );
			
			numAcctImported++;
			return liteAcctInfo;
		}
	}
	
	private void programSignUp(int[][] programs, String[] appFields, LiteStarsCustAccountInformation liteAcctInfo,
		LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany) throws Exception
	{
        int relay = 0;
        boolean relayNotFound = true;
        ArrayList currentProgramIDs = new ArrayList(liteAcctInfo.getAppliances().size());
        if(appFields[ImportManagerUtil.IDX_RELAY_NUM].trim().length() > 0)
        {
            relay = Integer.parseInt(appFields[ImportManagerUtil.IDX_RELAY_NUM]);
        }
        
        for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) 
        {
            LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(i);
             if(liteApp.getInventoryID() == liteInv.getInventoryID())
             {
                 if (liteApp.getProgramID() == programs[0][0] && (liteApp.getLoadNumber() == relay))
                 {
                    return;
                 }
                 
                 if(liteApp.getLoadNumber() == relay && !seasonalLoad)
                 {
                     relayNotFound = false;
                 }
                 else
                 {
                     currentProgramIDs.add(new Integer(liteApp.getProgramID()));
                 }
             }
		}
		
        /*
         * We are looking for the specified relay.  If it exists on any appliance connected to
         * the same switch, then we will want to update that specific appliance
         * and enrollment.  If it doesn't exist on any app for this switch, then we know that it
         * should be a new separate app and enrollment while maintaining the old enrollment.
         *--We only add a new enrollment and appliance on the same relay when the seasonal load
         *flag is set.
         */
        if((relayNotFound && relay > -1) || seasonalLoad)
        {
            int[] newEnrollment = programs[0];
            programs = new int[currentProgramIDs.size() + 1][];
            programs[0] = newEnrollment;
            for(int j = 0; j < currentProgramIDs.size(); j++)
            {
                int[] suProg = new int[4];
                suProg[0] = ((Integer)currentProgramIDs.get(j)).intValue();
                suProg[1] = -1; // ApplianceCategoryID (optional)
                suProg[2] = -1; // GroupID (optional)
                suProg[3] = -1; // LoadNumber (optional)
                programs[j + 1] = suProg; 
            }
        }

        ImportManagerUtil.programSignUp( programs, liteAcctInfo, liteInv, energyCompany );
		
		if (appFields != null && appFields[ImportManagerUtil.IDX_APP_TYPE].trim().length() > 0) {
			int appID = -1;
			for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
				LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
				if (liteApp.getProgramID() == programs[0][0]) {
					appID = liteApp.getApplianceID();
					break;
				}
			}
			
			ImportManagerUtil.updateAppliance( appFields, appID, liteAcctInfo, energyCompany );
		}
	}
	
	private void sendImportLog(File importLog, String email, LiteStarsEnergyCompany energyCompany) throws Exception {
		EmailMessage emailMsg = new EmailMessage();
		emailMsg.setFrom( energyCompany.getAdminEmailAddress() );
		emailMsg.setTo( email );
		emailMsg.setSubject( "Import Log" );
		emailMsg.setBody( "The log file containing information of the import process is attached." + LINE_SEPARATOR + LINE_SEPARATOR );
		emailMsg.addAttachment( importLog, null );
		
		emailMsg.send();
	}

}
