/*
 * Created on Jan 29, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.io.File;
import java.util.ArrayList;

import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMProgram;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.ImportManager;
import com.cannontech.stars.web.servlet.SOAPServer;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ImportCustAccountsTask implements TimeConsumingTask {

	private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );
	
	int status = STATUS_NOT_INIT;
	boolean isCanceled = false;
	String errorMsg = null;
	
	// Generic import file column names
	private static final String COL_NAME_LABEL = "COLUMN_NAMES:";
	
	private static final String[] CUST_COLUMNS = {
		"ACCOUNT_NO",
		"ACTION",
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
	};
	
	private static final String[] HW_COLUMNS = {
		"ACCOUNT_NO",
		"ACTION",
		"DEVICE_TYPE",
		"SERIAL_NO",
		"INSTALL_DATE",
		"REMOVE_DATE",
		"SERVICE_COMPANY",
		"PROGRAM_NAME",
		"ADDR_GROUP",
		"APP_TYPE",
		"APP_KW",
	};
	
	// Column indices of the generic customer info file
	private static int acct_col = 0;
	private static final int COL_ACCOUNT_NO = acct_col++;
	private static final int COL_ACCOUNT_ACTION = acct_col++;
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
	
	// Column indices of the generic hardware info file
	private static int hw_col = 0;
	private static final int COL_HW_ACCOUNT_NO = hw_col++;
	private static final int COL_HARDWARE_ACTION = hw_col++;
	private static final int COL_DEVICE_TYPE = hw_col++;
	private static final int COL_SERIAL_NO = hw_col++;
	private static final int COL_INSTALL_DATE = hw_col++;
	private static final int COL_REMOVE_DATE = hw_col++;
	private static final int COL_SERVICE_COMPANY = hw_col++;
	private static final int COL_PROGRAM_NAME = hw_col++;
	private static final int COL_ADDR_GROUP = hw_col++;
	private static final int COL_APP_TYPE = hw_col++;
	private static final int COL_APP_KW = hw_col++;
	
	StarsYukonUser user = null;
	File custFile = null;
	File hwFile = null;
	
	boolean processingImportFile = false;
	
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
	
	public ImportCustAccountsTask (StarsYukonUser user, File custFile, File hwFile) {
		this.user = user;
		this.custFile = custFile;
		this.hwFile = hwFile;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getStatus()
	 */
	public int getStatus() {
		return status;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#cancel()
	 */
	public void cancel() {
		if (status == STATUS_RUNNING) {
			isCanceled = true;
			status = STATUS_CANCELING;
		}
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if (status != STATUS_NOT_INIT) {
			if (processingImportFile) {
				return "Processing import file(s)";
			}
			else {
				String msg = "";
				if (numAcctTotal > 0) {
					msg += (status == STATUS_FINISHED)?
							numAcctImported + " customer accounts imported successfully" :
							numAcctImported + " of " + numAcctTotal + " customer accounts imported";
					msg += " (" + numAcctAdded + " added, " + numAcctUpdated + " updated, " + numAcctRemoved + " removed)" + LINE_SEPARATOR;
				}
				if (numHwTotal > 0) {
					msg += (status == STATUS_FINISHED)?
							numHwImported + " hardwares imported successfully" :
							numHwImported + " of " + numHwTotal + " hardwares imported";
					msg += " (" + numHwAdded + " added, " + numHwUpdated + " updated, " + numHwRemoved + " removed)";
				}
				return msg;
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getErrorMsg()
	 */
	public String getErrorMsg() {
		return errorMsg;
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
        
        status = STATUS_RUNNING;
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		processingImportFile = true;
		
		ArrayList custFieldsList = null;
		ArrayList hwFieldsList = null;
		ArrayList appFieldsList = null;
		int custLineNo = 0;
		int hwLineNo = 0;
		
		int[] custColIdx = new int[ CUST_COLUMNS.length ];
		int[] hwColIdx = new int [ HW_COLUMNS.length ];
		int numCustCol = CUST_COLUMNS.length;
		int numHwCol = HW_COLUMNS.length;
		
		for (int i = 0; i < CUST_COLUMNS.length; i++)
			custColIdx[i] = -1;
		for (int i = 0; i < HW_COLUMNS.length; i++)
			hwColIdx[i] = -1;
		
		try {
        	if (custFile != null) {
				String[] custLines = ServerUtils.readFile( custFile );
				if (custLines == null) {
					status = STATUS_ERROR;
					errorMsg = "Failed to read the customer file";
					return;
				}
				
				if (custLines.length > 0 && custLines[0].startsWith( COL_NAME_LABEL )) {
					custLineNo++;
					
					String[] labels = ImportManager.parseColumns( custLines[0].substring(COL_NAME_LABEL.length()) );
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
				}
				else {
					for (int i = 0; i < CUST_COLUMNS.length; i++)
						custColIdx[i] = i;
				}
				
				custFieldsList = new ArrayList();
				
				for (int i = custLineNo; i < custLines.length; i++) {
					custLineNo++;
					if (custLines[i].trim().equals("") || custLines[i].charAt(0) == '#')
						continue;
					
					String[] columns = ImportManager.parseColumns( custLines[i] );
					if (columns.length != numCustCol)
						throw new WebClientException( "Incorrect number of fields" );
					
					String[] custFields = ImportManager.prepareFields( ImportManager.NUM_ACCOUNT_FIELDS );
					custFields[ImportManager.IDX_LINE_NUM] = String.valueOf( custLineNo );
					setCustomerFields( custFields, columns, custColIdx );
					custFieldsList.add( custFields );
					
					if (hwFile == null && hwColIdx[COL_SERIAL_NO] != -1) {
						hwFieldsList = new ArrayList();
						String[] hwFields = ImportManager.prepareFields( ImportManager.NUM_INV_FIELDS );
						hwFields[ImportManager.IDX_LINE_NUM] = String.valueOf( custLineNo );
						setHardwareFields( hwFields, columns, hwColIdx );
						hwFieldsList.add( hwFields );
						
						if (hwColIdx[COL_APP_TYPE] != -1) {
							appFieldsList = new ArrayList();
							String[] appFields = ImportManager.prepareFields( ImportManager.NUM_APP_FIELDS );
							setApplianceFields( appFields, columns, hwColIdx );
							appFieldsList.add( appFields );
						}
					}
				}
				
				numAcctTotal = custFieldsList.size();
        	}
			
			if (hwFile != null) {
				String[] hwLines = ServerUtils.readFile( hwFile );
				if (hwLines == null) {
					status = STATUS_ERROR;
					errorMsg = "Failed to read the hardware info file";
					return;
				}
				
				if (hwLines.length > 0 && hwLines[0].startsWith( COL_NAME_LABEL )) {
					hwLineNo++;
					
					String[] labels = ImportManager.parseColumns( hwLines[0].substring(COL_NAME_LABEL.length()) );
					numHwCol = labels.length;
					
					for (int i = 0; i < HW_COLUMNS.length; i++)
						hwColIdx[i] = -1;
					
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
				}
				else {
					for (int i = 0; i < HW_COLUMNS.length; i++)
						hwColIdx[i] = i;
				}
				
				hwFieldsList = new ArrayList();
				if (hwColIdx[COL_APP_TYPE] != -1) appFieldsList = new ArrayList();
				
				for (int i = hwLineNo; i < hwLines.length; i++) {
					hwLineNo++;
					if (hwLines[i].trim().equals("") || hwLines[i].charAt(0) == '#')
						continue;
					
					String[] columns = ImportManager.parseColumns( hwLines[i] );
					if (columns.length != numHwCol)
						throw new WebClientException( "Incorrect number of fields" );
					
					String[] hwFields = ImportManager.prepareFields( ImportManager.NUM_INV_FIELDS );
					hwFields[ImportManager.IDX_LINE_NUM] = String.valueOf( hwLineNo );
					setHardwareFields( hwFields, columns, hwColIdx );
					hwFieldsList.add( hwFields );
					
					if (hwColIdx[COL_APP_TYPE] != -1) {
						String[] appFields = ImportManager.prepareFields( ImportManager.NUM_APP_FIELDS );
						setApplianceFields( appFields, columns, hwColIdx );
						appFieldsList.add( appFields );
					}
				}
				
				numHwTotal = hwFieldsList.size();
			}
			
			processingImportFile = false;
			
			if (custFile != null) {
				for (int i = 0; i < numAcctTotal; i++) {
					String[] custFields = (String[]) custFieldsList.get(i);
					String[] hwFields = null;
					String[] appFields = null;
					int[][] programs = null;
					
					custLineNo = Integer.parseInt( custFields[ImportManager.IDX_LINE_NUM] );
					
					if (hwFile == null && hwFieldsList != null) {
						hwFields = (String[]) hwFieldsList.get(i);
						if (appFieldsList != null) appFields = (String[]) appFieldsList.get(i);
						if (!hwFields[ImportManager.IDX_PROGRAM_NAME].trim().equals(""))
							programs = getEnrolledProgram( hwFields, energyCompany );
					}
					
					LiteStarsCustAccountInformation liteAcctInfo = energyCompany.searchAccountByAccountNo( custFields[ImportManager.IDX_ACCOUNT_NO] );
					
					String action = custFields[ImportManager.IDX_ACCOUNT_ACTION];
					if (action.trim().equals("")) {
						if (liteAcctInfo == null)
							action = "INSERT";
						else
							action = "UPDATE";
					}
					
					if (action.equalsIgnoreCase( "INSERT" )) {
						if (liteAcctInfo != null)
							throw new WebClientException( "Cannot insert customer account: account #" + custFields[ImportManager.IDX_ACCOUNT_NO] + " already exists" );
						
						liteAcctInfo = ImportManager.newCustomerAccount( custFields, user, energyCompany, false );
					}
					else if (action.equalsIgnoreCase( "UPDATE" )) {
						if (liteAcctInfo == null)
							throw new WebClientException( "Cannot update customer account: account #" + custFields[ImportManager.IDX_ACCOUNT_NO] + " doesn't exist" );
						
						ImportManager.updateCustomerAccount( custFields, liteAcctInfo, energyCompany );
					}
					else if (action.equalsIgnoreCase( "REMOVE" )) {
						if (liteAcctInfo == null)
							throw new WebClientException( "Cannot delete customer account: account #" + custFields[ImportManager.IDX_ACCOUNT_NO] + " doesn't exist" );
						
						energyCompany.deleteCustAccountInformation( liteAcctInfo );
					}
					else {
						throw new WebClientException( "Unrecognized action type '" + action + "'" );
					}
					
					if (!action.equals("REMOVE") && hwFields != null && !hwFields[ImportManager.IDX_SERIAL_NO].trim().equals("")) {
						LiteInventoryBase liteInv = null;
						if (liteAcctInfo.getInventories().size() == 0)
							liteInv = ImportManager.insertLMHardware( hwFields, liteAcctInfo, energyCompany, true );
						else
							liteInv = ImportManager.updateLMHardware( hwFields, liteAcctInfo, energyCompany );
						
						if (programs != null) {
							ImportManager.programSignUp( programs, liteAcctInfo, new Integer(liteInv.getInventoryID()), energyCompany );
							
							if (appFields != null && !appFields[ImportManager.IDX_APP_TYPE].trim().equals("")) {
								int appID = -1;
								for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
									LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
									if (liteApp.getLmProgramID() == programs[0][0]) {
										appID = liteApp.getApplianceID();
										break;
									}
								}
								
								ImportManager.updateAppliance( appFields, appID, liteAcctInfo, energyCompany );
							}
						}
					}
					
					numAcctImported++;
					if (action.equalsIgnoreCase( "INSERT" ))
						numAcctAdded++;
					else if (action.equalsIgnoreCase( "UPDATE" ))
						numAcctUpdated++;
					else
						numAcctRemoved++;
					
					if (isCanceled) {
						status = STATUS_CANCELED;
						return;
					}
				}
			}
			
			if (hwFile != null) {
				for (int i = 0; i < numHwTotal; i++) {
					String[] hwFields = (String[]) hwFieldsList.get(i);
					String[] appFields = null;
					int[][] programs = null;
					
					hwLineNo = Integer.parseInt( hwFields[ImportManager.IDX_LINE_NUM] );
					
					if (appFieldsList != null) appFields = (String[]) appFieldsList.get(i);
					if (!hwFields[ImportManager.IDX_PROGRAM_NAME].trim().equals(""))
						programs = getEnrolledProgram( hwFields, energyCompany );
					
					LiteStarsCustAccountInformation liteAcctInfo = energyCompany.searchAccountByAccountNo( hwFields[ImportManager.IDX_ACCOUNT_ID] );
					if (liteAcctInfo == null)
						throw new WebClientException( "Account #" + hwFields[ImportManager.IDX_ACCOUNT_ID] + " doesn't exist" );
					
					String action = hwFields[ImportManager.IDX_HARDWARE_ACTION];
					if (action.trim().equals("")) action = "UPDATE";
					
					LiteInventoryBase liteInv = null;
					if (action.equalsIgnoreCase( "INSERT" ))
						liteInv = ImportManager.insertLMHardware( hwFields, liteAcctInfo, energyCompany, true );
					else if (action.equalsIgnoreCase( "UPDATE" ))
						liteInv = ImportManager.updateLMHardware( hwFields, liteAcctInfo, energyCompany );
					else if (action.equalsIgnoreCase( "REMOVE" ))
						ImportManager.removeLMHardware( hwFields, liteAcctInfo, energyCompany );
					else
						throw new WebClientException( "Unrecognized action type '" + action + "'" );
					
					if (!action.equalsIgnoreCase("REMOVE") && programs != null) {
						ImportManager.programSignUp( programs, liteAcctInfo, new Integer(liteInv.getInventoryID()), energyCompany );
						
						if (appFields != null && !appFields[ImportManager.IDX_APP_TYPE].trim().equals("")) {
							int appID = -1;
							for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
								LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
								if (liteApp.getLmProgramID() == programs[0][0]) {
									appID = liteApp.getApplianceID();
									break;
								}
							}
							
							ImportManager.updateAppliance( appFields, appID, liteAcctInfo, energyCompany );
						}
					}
					
					numHwImported++;
					if (action.equalsIgnoreCase( "INSERT" ))
						numHwAdded++;
					else if (action.equalsIgnoreCase( "UPDATE" ))
						numHwUpdated++;
					else
						numHwRemoved++;
					
					if (isCanceled) {
						status = STATUS_CANCELED;
						return;
					}
				}
			}
			
			status = STATUS_FINISHED;
		}
		catch (Exception e) {
			e.printStackTrace();
			status = STATUS_ERROR;
			
			if (custLineNo > 0)
				errorMsg = "Error encountered when processing customer file line #" + custLineNo;
			else if (hwLineNo > 0)
				errorMsg = "Error encountered when processing hardware file line #" + hwLineNo;
			else
				errorMsg = "Failed to import customer accounts";
			if (e instanceof WebClientException)
				errorMsg += ": " + e.getMessage();
		}
	}
	
	private void setCustomerFields(String[] fields, String[] columns, int[] colIdx) {
		if (colIdx[COL_ACCOUNT_NO] != -1)
			fields[ImportManager.IDX_ACCOUNT_NO] = columns[ colIdx[COL_ACCOUNT_NO] ];
		if (colIdx[COL_ACCOUNT_ACTION] != -1)
			fields[ImportManager.IDX_ACCOUNT_ACTION] = columns[ colIdx[COL_ACCOUNT_ACTION] ];
		if (colIdx[COL_LAST_NAME] != -1)
			fields[ImportManager.IDX_LAST_NAME] = columns[ colIdx[COL_LAST_NAME] ];
		if (colIdx[COL_FIRST_NAME] != -1)
			fields[ImportManager.IDX_FIRST_NAME] = columns[ colIdx[COL_FIRST_NAME] ];
		if (colIdx[COL_LAST_NAME] != -1)
			fields[ImportManager.IDX_LAST_NAME] = columns[ colIdx[COL_LAST_NAME] ];
		if (colIdx[COL_HOME_PHONE] != -1)
			fields[ImportManager.IDX_HOME_PHONE] = columns[ colIdx[COL_HOME_PHONE] ];
		if (colIdx[COL_WORK_PHONE] != -1)
			fields[ImportManager.IDX_WORK_PHONE] = columns[ colIdx[COL_WORK_PHONE] ];
		if (colIdx[COL_EMAIL] != -1)
			fields[ImportManager.IDX_EMAIL] = columns[ colIdx[COL_EMAIL] ];
		if (colIdx[COL_STREET_ADDR1] != -1)
			fields[ImportManager.IDX_STREET_ADDR1] = columns[ colIdx[COL_STREET_ADDR1] ];
		if (colIdx[COL_STREET_ADDR2] != -1)
			fields[ImportManager.IDX_STREET_ADDR2] = columns[ colIdx[COL_STREET_ADDR2] ];
		if (colIdx[COL_CITY] != -1)
			fields[ImportManager.IDX_CITY] = columns[ colIdx[COL_CITY] ];
		if (colIdx[COL_STATE] != -1)
			fields[ImportManager.IDX_STATE] = columns[ colIdx[COL_STATE] ];
		if (colIdx[COL_COUNTY] != -1)
			fields[ImportManager.IDX_COUNTY] = columns[ colIdx[COL_COUNTY] ];
		if (colIdx[COL_ZIP_CODE] != -1)
			fields[ImportManager.IDX_ZIP_CODE] = columns[ colIdx[COL_ZIP_CODE] ];
		if (colIdx[COL_MAP_NO] != -1)
			fields[ImportManager.IDX_MAP_NO] = columns[ colIdx[COL_MAP_NO] ];
		if (colIdx[COL_SUBSTATION] != -1 && !columns[colIdx[COL_SUBSTATION]].trim().equals(""))
			fields[ImportManager.IDX_SUBSTATION] = "\"" + columns[ colIdx[COL_SUBSTATION] ] + "\"";
		if (colIdx[COL_FEEDER] != -1)
			fields[ImportManager.IDX_FEEDER] = columns[ colIdx[COL_FEEDER] ];
		if (colIdx[COL_POLE] != -1)
			fields[ImportManager.IDX_POLE] = columns[ colIdx[COL_POLE] ];
		if (colIdx[COL_TRFM_SIZE] != -1)
			fields[ImportManager.IDX_TRFM_SIZE] = columns[ colIdx[COL_TRFM_SIZE] ];
		if (colIdx[COL_SERV_VOLT] != -1)
			fields[ImportManager.IDX_SERV_VOLT] = columns[ colIdx[COL_SERV_VOLT] ];
		if (colIdx[COL_USERNAME] != -1)
			fields[ImportManager.IDX_USERNAME] = columns[ colIdx[COL_USERNAME] ];
		if (colIdx[COL_PASSWORD] != -1)
			fields[ImportManager.IDX_PASSWORD] = columns[ colIdx[COL_PASSWORD] ];
	}
	
	private void setHardwareFields(String[] fields, String[] columns, int[] colIdx) {
		if (colIdx[COL_HW_ACCOUNT_NO] != -1)
			fields[ImportManager.IDX_ACCOUNT_ID] = columns[ colIdx[COL_HW_ACCOUNT_NO] ];	// Use account ID field to store account #
		if (colIdx[COL_HARDWARE_ACTION] != -1)
			fields[ImportManager.IDX_HARDWARE_ACTION] = columns[ colIdx[COL_HARDWARE_ACTION] ];
		if (colIdx[COL_SERIAL_NO] != -1)
			fields[ImportManager.IDX_SERIAL_NO] = columns[ colIdx[COL_SERIAL_NO] ];
		if (colIdx[COL_DEVICE_TYPE] != -1)
			fields[ImportManager.IDX_DEVICE_TYPE] = columns[ colIdx[COL_DEVICE_TYPE] ];
		if (colIdx[COL_INSTALL_DATE] != -1)
			fields[ImportManager.IDX_INSTALL_DATE] = columns[ colIdx[COL_INSTALL_DATE] ];
		if (colIdx[COL_REMOVE_DATE] != -1)
			fields[ImportManager.IDX_REMOVE_DATE] = columns[ colIdx[COL_REMOVE_DATE] ];
		if (colIdx[COL_SERVICE_COMPANY] != -1 && !columns[colIdx[COL_SERVICE_COMPANY]].trim().equals(""))
			fields[ImportManager.IDX_SERVICE_COMPANY] = "\"" + columns[ colIdx[COL_SERVICE_COMPANY] ] + "\"";
		if (colIdx[COL_PROGRAM_NAME] != -1)
			fields[ImportManager.IDX_PROGRAM_NAME] = columns[ colIdx[COL_PROGRAM_NAME] ];
		if (colIdx[COL_ADDR_GROUP] != -1)
			fields[ImportManager.IDX_ADDR_GROUP] = columns[ colIdx[COL_ADDR_GROUP] ];
	}
	
	private void setApplianceFields(String[] fields, String[] columns, int[] colIdx) {
		if (colIdx[COL_APP_TYPE] != -1)
			fields[ImportManager.IDX_APP_TYPE] = columns[ colIdx[COL_APP_TYPE] ];
		if (colIdx[COL_APP_KW] != -1)
			fields[ImportManager.IDX_APP_KW] = columns[ colIdx[COL_APP_KW] ];
	}
	
	private int[][] getEnrolledProgram(String[] fields, LiteStarsEnergyCompany energyCompany) throws WebClientException {
		ArrayList programs = energyCompany.getAllLMPrograms();
		
		synchronized (programs) {
			for (int i = 0; i < programs.size(); i++) {
				LiteLMProgram liteProg = (LiteLMProgram) programs.get(i);
				if (liteProg.getProgramName().equalsIgnoreCase( fields[ImportManager.IDX_PROGRAM_NAME] )) {
					int[] suProg = new int[3];
					suProg[0] = liteProg.getProgramID();
					suProg[1] = -1;	// ApplianceCategoryID (optional)
					suProg[2] = -1;	// GroupID (optional)
					
					if (!fields[ImportManager.IDX_ADDR_GROUP].trim().equals("")) {
						for (int j = 0; j < liteProg.getGroupIDs().length; j++) {
							if (PAOFuncs.getYukonPAOName( liteProg.getGroupIDs()[j] ).equalsIgnoreCase( fields[ImportManager.IDX_ADDR_GROUP] )) {
								suProg[2] = liteProg.getGroupIDs()[j];
								break;
							}
						}
						
						if (suProg[2] == -1)
							throw new WebClientException( "The group '" + fields[ImportManager.IDX_ADDR_GROUP] + "' doesn't belong to program '" + liteProg.getProgramName() + "'" );
					}
					
					return new int[][] { suProg };
				}
			}
		}
		
		throw new WebClientException( "Program '" + fields[ImportManager.IDX_PROGRAM_NAME] + "' is not found in the published programs" );
	}

}
