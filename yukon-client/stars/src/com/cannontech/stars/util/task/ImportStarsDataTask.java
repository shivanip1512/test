/*
 * Created on Jan 30, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteSubstation;
import com.cannontech.stars.util.ImportProblem;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.util.ImportManagerUtil;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.stars.xml.serialize.AddressingGroup;
import com.cannontech.stars.xml.serialize.StarsApplianceCategory;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ImportStarsDataTask extends TimeConsumingTask {

	interface ImportFileParser {
		String[] populateFields(String line) throws Exception;
	}

	private static final String NEW_LINE = System.getProperty( "line.separator" );
	private static final Integer ZERO = new Integer(0);
	private static final int ERROR_NUM_LIMIT = 20;
	private static final int WARNING_NUM_LIMIT = 100;
	
	private static final int RESUME_FILE_CUSTOMER = 1;
	private static final int RESUME_FILE_INVENTORY = 2;
	private static final int RESUME_FILE_LOADINFO = 3;
	private static final int RESUME_FILE_WORKORDER = 4;
	private static final int RESUME_FILE_RESINFO = 5;
	
	private static int resumeFile = 0;
	private static int resumeLine = 0;
	
	LiteStarsEnergyCompany energyCompany = null;
	Hashtable processedData = null;
	File importDir = null;
	
	Hashtable customerMap = null;	// Map from import_account_id(Integer) to db_account_id(Integer)
	Hashtable hwConfigMap = null;	// Map from import_inv_id(Integer) to int[]{relay1_db_app_id, relay2_db_app_id, relay3_db_app_id}
	
	ArrayList acctFieldsList = null;
	ArrayList invFieldsList = null;
	ArrayList appFieldsList = null;
	ArrayList orderFieldsList = null;
	ArrayList resFieldsList = null;
	
	int acctFieldsCnt = 0;
	int invFieldsCnt = 0;
	int appFieldsCnt = 0;
	int orderFieldsCnt = 0;
	int resFieldsCnt = 0;
	
	int numAcctAdded = 0;
	int numInvAdded = 0;
	int numRecvrAdded = 0;
	int numMeterAdded = 0;
	int numAppAdded = 0;
	int numAppImported = 0;
	int numACImported = 0;
	int numWHImported = 0;
	int numGenImported = 0;
	int numIrrImported = 0;
	int numGDryImported = 0;
	int numHPImported = 0;
	int numSHImported = 0;
	int numDFImported = 0;
	int numGenlImported = 0;
	int numOrderAdded = 0;
	int numResAdded = 0;
	
	int numNoDeviceName = 0;
	int numDeviceNameNotFound = 0;
	
	public ImportStarsDataTask(LiteStarsEnergyCompany energyCompany, Hashtable processedData, File importDir) {
		this.energyCompany = energyCompany;
		this.processedData = processedData;
		this.importDir = importDir;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if (processedData != null) {
			if (status == STATUS_FINISHED) {
				return "Old STARS data has been imported successfully. Please check the \"_import.log\" for detailed information.";
			}
			else if (status == STATUS_RUNNING) {
				return getImportProgress();
			}
			else {
				String msg = getImportProgress();
				if (msg.length() == 0) msg = null;
				return msg;
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (processedData == null) {
			status = STATUS_ERROR;
			errorMsg = "Processed data cannot be null";
			return;
		}
		
		status = STATUS_RUNNING;
		
		acctFieldsList = (ArrayList) processedData.get("CustomerAccount");
		invFieldsList = (ArrayList) processedData.get("Inventory");
		appFieldsList = (ArrayList) processedData.get("Appliance");
		orderFieldsList = (ArrayList) processedData.get("WorkOrder");
		resFieldsList = (ArrayList) processedData.get("CustomerResidence");
		
		acctFieldsCnt = acctFieldsList.size();
		invFieldsCnt = invFieldsList.size();
		appFieldsCnt = appFieldsList.size();
		orderFieldsCnt = orderFieldsList.size();
		resFieldsCnt = resFieldsList.size();
		
		String position = null;
		String resumeLocation = null;
		int numErrors = 0;
		int numWarnings = 0;
		
		PrintWriter importLog = null;
		PrintWriter fw = null;
		
		try {
			importLog = new PrintWriter(new FileWriter(new File(importDir, "_import.log"), true), true);
			importLog.println("============================================================");
			importLog.println("Start time: " + StarsUtils.formatDate( new Date(), energyCompany.getDefaultTimeZone() ));
			importLog.println();
			
			Hashtable customerMap = getCustomerMap();
			Hashtable hwConfigMap = getHwConfigMap();
			
			File custMapFile = new File(importDir, "_customer.map");
			fw = new PrintWriter(new FileWriter(custMapFile, true), true);	// Append to file and auto flush
			
			boolean first = true;
			
			Iterator it = acctFieldsList.listIterator();
			while (it.hasNext()) {
				String[] fields = (String[]) it.next();
				position = "\"customer.txt\" line #" + fields[ImportManagerUtil.IDX_LINE_NUM];
				resumeLocation = "customer.txt " + fields[ImportManagerUtil.IDX_LINE_NUM];
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
				
				try {
					// To save database lookup time, only check for constraint for
					// the first import entry, then assume there won't be any problem
					ImportProblem problem = new ImportProblem();
					LiteStarsCustAccountInformation liteAcctInfo = ImportManagerUtil.newCustomerAccount(fields, energyCompany, first, problem);
					if (problem.getProblem() != null) {
						importLog.println( "WARNING at " + position + ": " + problem.getProblem() );
						if (++numWarnings > WARNING_NUM_LIMIT)
							throw new WebClientException( ImportProblem.TOO_MANY_WARNINGS );
					}
					
					first = false;
					
					customerMap.put( Integer.valueOf(fields[ImportManagerUtil.IDX_ACCOUNT_ID]), new Integer(liteAcctInfo.getAccountID()) );
					fw.println(fields[ImportManagerUtil.IDX_ACCOUNT_ID] + "," + liteAcctInfo.getAccountID());
					
					numAcctAdded++;
					it.remove();	// Try to free up memory as we go
				}
				catch (Exception e) {
					if (!(e instanceof WebClientException) || e.getMessage().equals( ImportProblem.TOO_MANY_WARNINGS ))
						throw e;
					importLog.println( "ERROR at " + position + ": " + e.getMessage() );
					if (++numErrors > ERROR_NUM_LIMIT)
						throw new WebClientException( ImportProblem.TOO_MANY_ERRORS );
				}
			}
			
			fw.close();
			fw = null;
			
			position = null;
			resumeLocation = "invent.txt";
			
			File hwConfigMapFile = new File(importDir, "_hwconfig.map");
			fw = new PrintWriter(new FileWriter(hwConfigMapFile, true), true);
			
			Hashtable devCarrSettings = null;
			first = true;
			
			it = invFieldsList.listIterator();
			while (it.hasNext()) {
				String[] fields = (String[]) it.next();
				position = "\"invent.txt\" line #" + fields[ImportManagerUtil.IDX_LINE_NUM];
				resumeLocation = "invent.txt " + fields[ImportManagerUtil.IDX_LINE_NUM];
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
				
				try {
					Integer importAcctID = Integer.valueOf( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					LiteStarsCustAccountInformation liteAcctInfo = null;
					if (importAcctID.intValue() > 0) {
						Integer accountID = (Integer) customerMap.get( importAcctID );
						if (accountID != null)
							liteAcctInfo = energyCompany.getCustAccountInformation( accountID.intValue(), true );
						if (liteAcctInfo == null)
							throw new WebClientException("Cannot find customer account with import_account_id = " + importAcctID);
					}
					
					ImportProblem problem = new ImportProblem();
					LiteInventoryBase liteInv = ImportManagerUtil.insertLMHardware( fields, liteAcctInfo, energyCompany, first, problem );
					if (problem.getProblem() != null) {
						importLog.println( "WARNING at " + position + ": " + problem.getProblem() );
						if (++numWarnings > WARNING_NUM_LIMIT)
							throw new WebClientException( ImportProblem.TOO_MANY_WARNINGS );
					}
					
					first = false;
					it.remove();
					
					for (int i = 0; i < 3; i++) {
						if (fields[ImportManagerUtil.IDX_R1_STATUS + i].equals("1"))
							importLog.println("Receiver (import_inv_id=" + fields[ImportManagerUtil.IDX_INV_ID] + ",db_inv_id=" + liteInv.getInventoryID() + ",serial_no=" + fields[ImportManagerUtil.IDX_SERIAL_NO] + ") relay " + (i+1) + " is out of service");
						else if (fields[ImportManagerUtil.IDX_R1_STATUS + i].equals("2"))
							importLog.println("Receiver (import_inv_id=" + fields[ImportManagerUtil.IDX_INV_ID] + ",db_inv_id=" + liteInv.getInventoryID() + ",serial_no=" + fields[ImportManagerUtil.IDX_SERIAL_NO] + ") relay " + (i+1) + " was out before switch placed out");
					}
					
					if (liteAcctInfo != null) {
						// If load groups for relays are specified, automatically add appliances through program enrollment
						StarsEnrollmentPrograms enrProgs = energyCompany.getStarsEnrollmentPrograms();
						ArrayList progList = new ArrayList();
						
						for (int i = 0; i < 3; i++) {
							if (fields[ImportManagerUtil.IDX_R1_GROUP + i].equals("") || fields[ImportManagerUtil.IDX_R1_GROUP + i].equals("0"))
								continue;
							
							int groupID = Integer.parseInt( fields[ImportManagerUtil.IDX_R1_GROUP + i] );
							int progID = 0;
							int appCatID = 0;
							int loadNo = i + 1;
							
							for (int j = 0; j < enrProgs.getStarsApplianceCategoryCount(); j++) {
								StarsApplianceCategory appCat = enrProgs.getStarsApplianceCategory(j);
								for (int k = 0; k < appCat.getStarsEnrLMProgramCount(); k++) {
									StarsEnrLMProgram program = appCat.getStarsEnrLMProgram(k);
									for (int l = 0; l < program.getAddressingGroupCount(); l++) {
										if (program.getAddressingGroup(l).getEntryID() == groupID) {
											progID = program.getProgramID();
											appCatID = appCat.getApplianceCategoryID();
											break;
										}
									}
									if (progID > 0) break;
								}
								if (progID > 0) break;
							}
							
							if (progID == 0)
								throw new WebClientException( "Cannot find LM program for load group id = " + groupID );
							
							progList.add( new int[] {progID, appCatID, groupID, loadNo} );
						}
						
						if (progList.size() > 0) {
							int[][] programs = new int[ progList.size() ][];
							progList.toArray( programs );
							ImportManagerUtil.programSignUp( programs, liteAcctInfo, liteInv, energyCompany );
							
							int[] appIDs = new int[3];
							for (int i = 0; i < programs.length; i++) {
								int loadNo = programs[i][3];
								
								for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
									LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
									if (liteApp.getProgramID() == programs[i][0]) {
										appIDs[loadNo - 1] = liteApp.getApplianceID();
										break;
									}
								}
								
								if (appIDs[loadNo - 1] == 0)	// shouldn't happen
									throw new WebClientException("Cannot find appliance with RelayNum = " + loadNo);
							}
							
							hwConfigMap.put( Integer.valueOf(fields[ImportManagerUtil.IDX_INV_ID]), appIDs );
							fw.println(fields[ImportManagerUtil.IDX_INV_ID] + "," + appIDs[0] + "," + appIDs[1] + "," + appIDs[2]);
							
							numAppAdded += programs.length;
						}
					}
					
					numInvAdded++;
					if (liteInv instanceof LiteStarsLMHardware)
						numRecvrAdded++;
					else
						numMeterAdded++;
				}
				catch (Exception e) {
					if (!(e instanceof WebClientException) || e.getMessage().equals( ImportProblem.TOO_MANY_WARNINGS ))
						throw e;
					if (e.getMessage().equals( ImportProblem.NO_DEVICE_NAME )) {
						numNoDeviceName++;
					}
					else if (e.getMessage().equals( ImportProblem.DEVICE_NAME_NOT_FOUND )) {
						numDeviceNameNotFound++;
						
						if (fields[ImportManagerUtil.IDX_SERIAL_NO].length() > 0) {
							if (devCarrSettings == null) {
								devCarrSettings = new Hashtable();
								
								String sql = "SELECT DeviceID, Address FROM DeviceCarrierSettings";
								SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
								stmt.execute();
								
								for (int i = 0; i < stmt.getRowCount(); i++) {
									int deviceID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
									int address = ((java.math.BigDecimal) stmt.getRow(i)[1]).intValue();
									devCarrSettings.put( String.valueOf(address), new Integer(deviceID) );
								}
							}
							
							Integer deviceID = (Integer) devCarrSettings.get( fields[ImportManagerUtil.IDX_SERIAL_NO] );
							if (deviceID != null) {
								LiteYukonPAObject litePao = PAOFuncs.getLiteYukonPAO( deviceID.intValue() );
								importLog.println("Meter (import_inv_id=" + fields[ImportManagerUtil.IDX_INV_ID] + ",import_acct_id=" + fields[ImportManagerUtil.IDX_ACCOUNT_ID]
										+ ",dev_name=" + fields[ImportManagerUtil.IDX_DEVICE_NAME] + ",serial_no=" + fields[ImportManagerUtil.IDX_SERIAL_NO]
										+ ") matches Yukon device (dev_id=" + deviceID + ",dev_name=" + litePao.getPaoName() + ") by serial number");
							}
						}
					}
					else {
						importLog.println( "ERROR at " + position + ": " + e.getMessage() );
						if (++numErrors > ERROR_NUM_LIMIT)
							throw new WebClientException( ImportProblem.TOO_MANY_ERRORS );
					}
				}
			}
			
			fw.close();
			fw = null;
			
			position = null;
			resumeLocation = "loadinfo.txt";
			
			it = appFieldsList.listIterator();
			while (it.hasNext()) {
				String[] fields = (String[]) it.next();
				position = "\"loadinfo.txt\" line #" + fields[ImportManagerUtil.IDX_LINE_NUM];
				resumeLocation = "loadinfo.txt " + fields[ImportManagerUtil.IDX_LINE_NUM];
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
				
				try {
					Integer importAcctID = Integer.valueOf( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					LiteStarsCustAccountInformation liteAcctInfo = null;
					if (importAcctID.intValue() > 0) {
						Integer accountID = (Integer) customerMap.get( importAcctID );
						if (accountID != null)
							liteAcctInfo = energyCompany.getCustAccountInformation( accountID.intValue(), true );
						if (liteAcctInfo == null)
							throw new WebClientException("Cannot find customer account with import_account_id = " + importAcctID);
					}
					
					Integer invID = Integer.valueOf( fields[ImportManagerUtil.IDX_INV_ID] );
					int relayNum = Integer.parseInt( fields[ImportManagerUtil.IDX_RELAY_NUM] );
					int appID = 0;
					
					if (relayNum > 0) {
						int[] appIDs = (int[]) hwConfigMap.get( invID );
						if (appIDs != null) appID = appIDs[relayNum - 1];
					}
					
					if (appID > 0 && fields[ImportManagerUtil.IDX_AVAIL_FOR_CTRL].equalsIgnoreCase("Y"))
						ImportManagerUtil.updateAppliance( fields, appID, liteAcctInfo, energyCompany );
					else
						ImportManagerUtil.newAppliance( fields, liteAcctInfo, energyCompany );
					
					numAppImported++;
					it.remove();
					
					if (fields[ImportManagerUtil.IDX_APP_CAT_DEF_ID].equals("")) continue;
					int catDefID = Integer.parseInt( fields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] );
					
					if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER)
						numACImported++;
					else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER)
						numWHImported++;
					else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR)
						numGenImported++;
					else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION)
						numIrrImported++;
					else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER)
						numGDryImported++;
					else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP)
						numHPImported++;
					else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT)
						numSHImported++;
					else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL)
						numDFImported++;
					else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DEFAULT)
						numGenlImported++;
				}
				catch (Exception e) {
					if (!(e instanceof WebClientException) || e.getMessage().equals( ImportProblem.TOO_MANY_WARNINGS ))
						throw e;
					importLog.println( "ERROR at " + position + ": " + e.getMessage() );
					if (++numErrors > ERROR_NUM_LIMIT)
						throw new WebClientException( ImportProblem.TOO_MANY_ERRORS );
				}
			}
			
			position = null;
			resumeLocation = "workordr.txt";
			
			first = true;
			
			it = orderFieldsList.listIterator();
			while (it.hasNext()) {
				String[] fields = (String[]) it.next();
				position = "\"workordr.txt\" line #" + fields[ImportManagerUtil.IDX_LINE_NUM];
				resumeLocation = "workordr.txt " + fields[ImportManagerUtil.IDX_LINE_NUM];
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
				
				try {
					Integer importAcctID = Integer.valueOf( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					LiteStarsCustAccountInformation liteAcctInfo = null;
					if (importAcctID.intValue() > 0) {
						Integer accountID = (Integer) customerMap.get( importAcctID );
						if (accountID != null)
							liteAcctInfo = energyCompany.getCustAccountInformation( accountID.intValue(), true );
						if (liteAcctInfo == null)
							throw new WebClientException("Cannot find customer account with import_account_id = " + importAcctID);
					}
					
					ImportProblem problem = new ImportProblem();
					ImportManagerUtil.newServiceRequest(fields, liteAcctInfo, energyCompany, first, problem);
					if (problem.getProblem() != null) {
						importLog.println( "WARNING at " + position + ": " + problem.getProblem() );
						if (++numWarnings > WARNING_NUM_LIMIT)
							throw new WebClientException( ImportProblem.TOO_MANY_WARNINGS );
					}
					
					first = false;
					numOrderAdded++;
					it.remove();
				}
				catch (Exception e) {
					if (!(e instanceof WebClientException) || e.getMessage().equals( ImportProblem.TOO_MANY_WARNINGS ))
						throw e;
					importLog.println( "ERROR at " + position + ": " + e.getMessage() );
					if (++numErrors > ERROR_NUM_LIMIT)
						throw new WebClientException( ImportProblem.TOO_MANY_ERRORS );
				}
			}
			
			position = null;
			resumeLocation = "resinfo.txt";
			
			it = resFieldsList.listIterator();
			while (it.hasNext()) {
				String[] fields = (String[]) it.next();
				position = "\"resinfo.txt\" line #" + fields[ImportManagerUtil.IDX_LINE_NUM];
				resumeLocation = "resinfo.txt " + fields[ImportManagerUtil.IDX_LINE_NUM];
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
				
				try {
					Integer importAcctID = Integer.valueOf( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					LiteStarsCustAccountInformation liteAcctInfo = null;
					if (importAcctID.intValue() > 0) {
						Integer accountID = (Integer) customerMap.get( importAcctID );
						if (accountID != null)
							liteAcctInfo = energyCompany.getCustAccountInformation( accountID.intValue(), true );
						if (liteAcctInfo == null)
							throw new WebClientException("Cannot find customer account with import_account_id = " + importAcctID);
					}
					
					ImportManagerUtil.newResidenceInfo( fields, liteAcctInfo );
					
					numResAdded++;
					it.remove();
				}
				catch (Exception e) {
					if (!(e instanceof WebClientException) || e.getMessage().equals( ImportProblem.TOO_MANY_WARNINGS ))
						throw e;
					importLog.println( "ERROR at " + position + ": " + e.getMessage() );
					if (++numErrors > ERROR_NUM_LIMIT)
						throw new WebClientException( ImportProblem.TOO_MANY_ERRORS );
				}
			}
			
			importLog.println();
			importLog.println("Stop time: " + StarsUtils.formatDate( new Date(), energyCompany.getDefaultTimeZone() ));
			importLog.println();
			importLog.println(getImportProgress());
			importLog.println();
			importLog.println("@DONE");
			
			status = STATUS_FINISHED;
		}
		catch (Exception e) {
			if (status == STATUS_CANCELED) {
				errorMsg = "Operation is canceled by user";
				if (position != null)
					errorMsg += " before processing " + position;
				else if (resumeLocation != null)
					errorMsg += " before processing " + resumeLocation;
			}
			else {
				status = STATUS_ERROR;
				CTILogger.error( e.getMessage(), e );
				
				if (position != null) {
					errorMsg = "An error occured at " + position;
					if (e instanceof WebClientException)
						errorMsg += ": " + e.getMessage();
				}
				else {
					if (e instanceof WebClientException)
						errorMsg = e.getMessage();
					else
						errorMsg = "An error occured in the process of importing old STARS data";
				}
			}
			
			if (importLog != null) {
				importLog.println();
				importLog.println("Stop time: " + StarsUtils.formatDate( new Date(), energyCompany.getDefaultTimeZone() ));
				importLog.println();
				importLog.println( errorMsg );
				importLog.println(getImportProgress());
				if (resumeLocation != null) {
					importLog.println();
					importLog.println("@RESUME " + resumeLocation);
				}
				importLog.println();
			}
			
			errorMsg += NEW_LINE + "Please checked the \"_import.log\" for detailed information.";
		}
		finally {
			if (importLog != null) importLog.close();
			if (fw != null) fw.close();
		}
	}
	
	private String getImportProgress() {
		String msg = "";
		if (acctFieldsCnt > 0) {
			if (numAcctAdded == acctFieldsCnt)
				msg += numAcctAdded + " customer accounts imported successfully";
			else
				msg += numAcctAdded + " of " + acctFieldsCnt + " customer accounts imported";
			msg += NEW_LINE;
		}
		if (invFieldsCnt > 0) {
			if (numInvAdded + numNoDeviceName + numDeviceNameNotFound  == invFieldsCnt)
				msg += numInvAdded + " hardwares imported successfully";
			else
				msg += numInvAdded + " of " + invFieldsCnt + " hardwares imported";
			msg += " (" + numRecvrAdded + " receivers, " + numMeterAdded + " meters)" + NEW_LINE;
		}
		if (appFieldsCnt > 0) {
			if (numAppImported == appFieldsCnt)
				msg += numAppImported + " appliances imported successfully";
			else
				msg += numAppImported + " of " + appFieldsCnt + " appliances imported";
			msg += " (" + numACImported + " ac," +
					numWHImported + " wh," +
					numGenImported + " gen," +
					numIrrImported + " irr," +
					numGDryImported + " gdry," +
					numHPImported + " hp," +
					numSHImported + " sh," +
					numDFImported + " df," +
					numGenlImported + " genl)" +
					NEW_LINE;
		}
		if (orderFieldsCnt > 0) {
			if (numOrderAdded == orderFieldsCnt)
				msg += numOrderAdded + " work orders imported successfully";
			else
				msg += numOrderAdded + " of " + orderFieldsCnt + " work orders imported";
			msg += NEW_LINE;
		}
		if (resFieldsCnt > 0) {
			if (numResAdded == resFieldsCnt)
				msg += numResAdded + " residence info imported successfully";
			else
				msg += numResAdded + " of " + resFieldsCnt + " residence information imported";
		}
		return msg;
	}
	
	private static void getResumeLocation(File importDir) throws Exception {
		File file = new File(importDir, "_import.log");
		if (!file.exists()) file.createNewFile();
		
		String[] lines = StarsUtils.readFile( file, false );
		for (int i = lines.length - 1; i >= 0; i--) {
			if (lines[i].startsWith("@DONE"))
				throw new WebClientException("The data in this directory has already been imported. If you want to import it again, please remove the \"_import.log\" file first.");
			
			if (lines[i].startsWith("@RESUME")) {
				StringTokenizer st = new StringTokenizer( lines[i].substring(7) );
				if (st.hasMoreTokens()) {
					String fileName = st.nextToken();
					if (fileName.equalsIgnoreCase("customer.txt"))
						resumeFile = RESUME_FILE_CUSTOMER;
					else if (fileName.equalsIgnoreCase("invent.txt"))
						resumeFile = RESUME_FILE_INVENTORY;
					else if (fileName.equalsIgnoreCase("loadinfo.txt"))
						resumeFile = RESUME_FILE_LOADINFO;
					else if (fileName.equalsIgnoreCase("workordr.txt"))
						resumeFile = RESUME_FILE_WORKORDER;
					else if (fileName.equalsIgnoreCase("resinfo.txt"))
						resumeFile = RESUME_FILE_RESINFO;
				}
				if (st.hasMoreTokens()) {
					resumeLine = Integer.parseInt( st.nextToken() );
				}
				return;
			}
		}
	}
	
	private Hashtable getCustomerMap() throws Exception {
		if (customerMap == null) {
			customerMap = new Hashtable();
			File file = new File(importDir, "_customer.map");
			if (file.exists()) {
				String[] lines = StarsUtils.readFile( file, false );
				for (int i = 0; i < lines.length; i++) {
					String[] fields = StarsUtils.splitString( lines[i], "," );
					customerMap.put( Integer.valueOf(fields[0]), Integer.valueOf(fields[1]) );
				}
			}
		}
		return customerMap;
	}
	
	private Hashtable getHwConfigMap() throws Exception {
		if (hwConfigMap == null) {
			hwConfigMap = new Hashtable();
			File file = new File(importDir, "_hwconfig.map");
			if (file.exists()) {
				String[] lines = StarsUtils.readFile( file, false );
				for (int i = 0; i < lines.length; i++) {
					String[] fields = StarsUtils.splitString( lines[i], "," );
					Integer invID = Integer.valueOf( fields[0] );
					int[] appIDs = new int[3];
					for (int j = 0; j < 3; j++)
						appIDs[j] = Integer.parseInt( fields[j+1] );
					hwConfigMap.put( invID, appIDs );
				}
			}
		}
		return hwConfigMap;
	}
    
	private static StreamTokenizer prepareStreamTokenzier(String line) {
		StreamTokenizer st = new StreamTokenizer( new StringReader(line) );
		st.resetSyntax();
		st.wordChars( 0, 255 );
		st.ordinaryChar( ',' );
		st.quoteChar( '"' );
		
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
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = ImportManagerUtil.prepareFields(ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.NUM_INV_FIELDS);
				
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_LAST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_FIRST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_STREET_ADDR1] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[ImportManagerUtil.IDX_STREET_ADDR1] += " " + st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_CITY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_STATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_ZIP_CODE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_HOME_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_ACCOUNT_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERIAL_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_INSTALL_DATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERVICE_COMPANY] = st.sval;
				
				fields[ImportManagerUtil.IDX_USERNAME] = fields[ImportManagerUtil.IDX_FIRST_NAME].substring(0,1).toLowerCase()
									 + fields[ImportManagerUtil.IDX_LAST_NAME].toLowerCase();
				fields[ImportManagerUtil.IDX_PASSWORD] = fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERIAL_NO];
				
				fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_DEVICE_TYPE] = "ExpressStat";
				
				if (fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERVICE_COMPANY].equalsIgnoreCase("Western"))
					fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERVICE_COMPANY] = "\"Western Heating\"";
				else if (fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERVICE_COMPANY].equalsIgnoreCase("Ridgeway"))
					fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERVICE_COMPANY] = "\"Ridgeway Industrial\"";
				else if (fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERVICE_COMPANY].equalsIgnoreCase("Access"))
					fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERVICE_COMPANY] = "\"Access Heating\"";
				
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
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = ImportManagerUtil.prepareFields(ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.NUM_INV_FIELDS);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_ACCOUNT_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_COUNTY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_LAST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_FIRST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_HOME_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_WORK_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_WORK_PHONE_EXT] = "(ext." + st.sval + ")";
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_STREET_ADDR1] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_CITY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_STATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_ZIP_CODE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_EMAIL] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERVICE_COMPANY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_INSTALL_DATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERIAL_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_ADDR_GROUP] = st.sval;
				if (st.ttype != ',') st.nextToken();
				
				fields[ImportManagerUtil.IDX_USERNAME] = fields[ImportManagerUtil.IDX_EMAIL];
				fields[ImportManagerUtil.IDX_PASSWORD] = fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERIAL_NO];
				
				fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_DEVICE_TYPE] = "LCR-5000";
				fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_ADDR_GROUP] = "PGE RIWH Group " + fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_ADDR_GROUP];
				
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
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = ImportManagerUtil.prepareFields(ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.NUM_INV_FIELDS);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_ACCOUNT_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_LAST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_FIRST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_HOME_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_WORK_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_EMAIL] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_STREET_ADDR1] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_STREET_ADDR2] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_CITY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_STATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_ZIP_CODE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_MAP_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_SERIAL_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_INSTALL_DATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_REMOVE_DATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_USERNAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.IDX_PASSWORD] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_HARDWARE_ACTION] = st.sval;
				
				fields[ImportManagerUtil.NUM_ACCOUNT_FIELDS + ImportManagerUtil.IDX_DEVICE_TYPE] = "ExpressStat";
				
				return fields;
			}
		});
	}
	
	private static Integer getTextEntryID(String text, String listName, LiteStarsEnergyCompany energyCompany) {
		if (listName.equals("ServiceCompany")) {
			if (text.equals("")) return null;
			
			ArrayList companies = energyCompany.getAllServiceCompanies();
			for (int i = 0; i < companies.size(); i++) {
				LiteServiceCompany liteCompany = (LiteServiceCompany) companies.get(i);
				if (text.equalsIgnoreCase( liteCompany.getCompanyName() ))
					return new Integer( liteCompany.getCompanyID() );
			}
		}
		else if (listName.equals("Substation")) {
			if (text.equals("")) return null;
			
			ArrayList substations = energyCompany.getAllSubstations();
			for (int i = 0; i < substations.size(); i++) {
				LiteSubstation liteSub = (LiteSubstation) substations.get(i);
				if (text.equalsIgnoreCase( liteSub.getSubstationName() ))
					return new Integer( liteSub.getSubstationID() );
			}
		}
		else if (listName.equals("LoadType")) {
			if (text.equals("")) return null;
			
			ArrayList appCats = energyCompany.getAllApplianceCategories();
			for (int i = 0; i < appCats.size(); i++) {
				LiteApplianceCategory liteAppCat = (LiteApplianceCategory) appCats.get(i);
				if (text.equalsIgnoreCase( liteAppCat.getDescription() ))
					return new Integer( liteAppCat.getApplianceCategoryID() );
			}
		}
		else if (listName.equals("LoadGroup")) {
			if (text.equals("")) return null;
			
			StarsEnrollmentPrograms programs = energyCompany.getStarsEnrollmentPrograms();
			for (int i = 0; i < programs.getStarsApplianceCategoryCount(); i++) {
				StarsApplianceCategory category = programs.getStarsApplianceCategory(i);
				for (int j = 0; j < category.getStarsEnrLMProgramCount(); j++) {
					StarsEnrLMProgram program = category.getStarsEnrLMProgram(j);
					for (int k = 0; k < program.getAddressingGroupCount(); k++) {
						AddressingGroup group = program.getAddressingGroup(k);
						if (group.getContent().equalsIgnoreCase( text ))
							return new Integer( group.getEntryID() );
					}
				}
			}
		}
		else {
			YukonSelectionList list = energyCompany.getYukonSelectionList( listName );
			if (list != null) {
				for (int i = 0; i < list.getYukonListEntries().size(); i++) {
					YukonListEntry entry = (YukonListEntry) list.getYukonListEntries().get(i);
					if (text.equalsIgnoreCase( entry.getEntryText().trim() ))
						return new Integer( entry.getEntryID() );
				}
			}
		}
		
		return ZERO;
	}
	
	/**
	 * For some string including a label (e.g. "R2-GROUP:WHH"), get the text part of it (i.e. "WHH").
	 * If the text is empty or it represents the numerical value -1, return null.
	 * Otherwise, the text is considered "meaningful", and is returned.
	 */
	private static String getMeaningfulText(String str) {
		if (str != null && str.length() > 0) {
			String text = str.substring( str.indexOf(':') + 1 );
			if (text.length() > 0) {
				try {
					if (Double.parseDouble(text) == -1.0)
						return null;
				}
				catch (NumberFormatException e) {}
				
				return text;
			}
		}
		
		return null;
	}
	
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
	private static String[] parseStarsCustomer(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_ACCOUNT_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 18)
			throw new WebClientException( "Incorrect number of fields in customer file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_ACCOUNT_NO] = columns[1];
		if (columns[2].length() > 0)
			fields[ImportManagerUtil.IDX_ACCOUNT_NO] += "-" + columns[2];
		fields[ImportManagerUtil.IDX_CUSTOMER_TYPE] = columns[3];
		fields[ImportManagerUtil.IDX_LAST_NAME] = columns[4];
		fields[ImportManagerUtil.IDX_FIRST_NAME] = columns[5];
		fields[ImportManagerUtil.IDX_COMPANY_NAME] = columns[7];
		fields[ImportManagerUtil.IDX_MAP_NO] = columns[8];
		fields[ImportManagerUtil.IDX_STREET_ADDR1] = columns[9];
		fields[ImportManagerUtil.IDX_STREET_ADDR2] = columns[10];
		fields[ImportManagerUtil.IDX_CITY] = columns[11];
		fields[ImportManagerUtil.IDX_STATE] = columns[12];
		fields[ImportManagerUtil.IDX_ZIP_CODE] = columns[13];
		fields[ImportManagerUtil.IDX_HOME_PHONE] = columns[14];
		fields[ImportManagerUtil.IDX_WORK_PHONE] = columns[15];
		fields[ImportManagerUtil.IDX_ACCOUNT_NOTES] = columns[17];
		
		return fields;
	}
	
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
	 * 10-17	PROP_NOTES
	 */
	private static String[] parseStarsServiceInfo(String line, Hashtable userLabels) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_ACCOUNT_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 17)
			throw new WebClientException( "Incorrect number of fields in service info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		if (columns[1].length() > 0)
			fields[ImportManagerUtil.IDX_PROP_NOTES] += "MeterNumber: " + columns[1] + NEW_LINE;
		fields[ImportManagerUtil.IDX_SUBSTATION] = columns[2];
		fields[ImportManagerUtil.IDX_FEEDER] = columns[3];
		fields[ImportManagerUtil.IDX_POLE] = columns[4];
		fields[ImportManagerUtil.IDX_TRFM_SIZE] = columns[5];
		fields[ImportManagerUtil.IDX_SERV_VOLT] = columns[6];
		if (columns[9].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_SI_CHAR1) != null) {
			String text = getMeaningfulText( columns[9] );
			if (text != null)
				fields[ImportManagerUtil.IDX_PROP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_SI_CHAR1) + ": " + text + NEW_LINE;
		}
		if (columns[10].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_SI_CHAR2) != null) {
			String text = getMeaningfulText( columns[10] );
			if (text != null)
				fields[ImportManagerUtil.IDX_PROP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_SI_CHAR2) + ": " + text + NEW_LINE;
		}
		if (columns[11].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_SI_DROPBOX1) != null) {
			String text = getMeaningfulText( columns[11] );
			if (text != null)
				fields[ImportManagerUtil.IDX_PROP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_SI_DROPBOX1) + ": " + text + NEW_LINE;
		}
		if (columns[12].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_SI_DROPBOX2) != null) {
			String text = getMeaningfulText( columns[12] );
			if (text != null)
				fields[ImportManagerUtil.IDX_PROP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_SI_DROPBOX2) + ": " + text + NEW_LINE;
		}
		if (columns[13].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_SI_CHECKBOX1) != null) {
			String text = getMeaningfulText( columns[13] );
			if (text != null)
				fields[ImportManagerUtil.IDX_PROP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_SI_CHECKBOX1) + ": " + text + NEW_LINE;
		}
		if (columns[14].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_SI_CHECKBOX2) != null) {
			String text = getMeaningfulText( columns[14] );
			if (text != null)
				fields[ImportManagerUtil.IDX_PROP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_SI_CHECKBOX2) + ": " + text + NEW_LINE;
		}
		if (columns[15].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_SI_NUMERIC1) != null) {
			String text = getMeaningfulText( columns[15] );
			if (text != null)
				fields[ImportManagerUtil.IDX_PROP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_SI_NUMERIC1) + ": " + text + NEW_LINE;
		}
		if (columns[16].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_SI_NUMERIC2) != null) {
			String text = getMeaningfulText( columns[16] );
			if (text != null)
				fields[ImportManagerUtil.IDX_PROP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_SI_NUMERIC2) + ": " + text + NEW_LINE;
		}
		
		return fields;
	}
    
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
	private static String[] parseStarsInventory(String line, Hashtable userLabels) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields(ImportManagerUtil.NUM_INV_FIELDS);
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 19)
			throw new WebClientException( "Incorrect number of fields in inventory file" );
		
		fields[ImportManagerUtil.IDX_INV_ID] = columns[0];
		fields[ImportManagerUtil.IDX_SERIAL_NO] = columns[1];
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[2];
		fields[ImportManagerUtil.IDX_ALT_TRACK_NO] = columns[3];
		fields[ImportManagerUtil.IDX_DEVICE_NAME] = columns[4];
		if (columns[5].length() > 0)
			fields[ImportManagerUtil.IDX_INV_NOTES] += "MapNumber: " + columns[5] + NEW_LINE;
		if (columns[6].length() > 0)
			fields[ImportManagerUtil.IDX_INV_NOTES] += "OriginAddr1: " + columns[6] + NEW_LINE;
		if (columns[7].length() > 0)
			fields[ImportManagerUtil.IDX_INV_NOTES] += "OriginAddr2: " + columns[7] + NEW_LINE;
		fields[ImportManagerUtil.IDX_DEVICE_TYPE] = columns[8];
		fields[ImportManagerUtil.IDX_DEVICE_VOLTAGE] = columns[12];
		fields[ImportManagerUtil.IDX_RECEIVE_DATE] = columns[13];
		fields[ImportManagerUtil.IDX_SERVICE_COMPANY] = columns[15];
		if (columns[16].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_DI_CHAR1) != null) {
			String text = getMeaningfulText( columns[16] );
			if (text != null)
				fields[ImportManagerUtil.IDX_INV_NOTES] += userLabels.get(ImportManagerUtil.LABEL_DI_CHAR1) + ": " + text + NEW_LINE;
		}
		if (columns[17].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_DI_DROPBOX1) != null) {
			String text = getMeaningfulText( columns[17] );
			if (text != null)
				fields[ImportManagerUtil.IDX_INV_NOTES] += userLabels.get(ImportManagerUtil.LABEL_DI_DROPBOX1) + ": " + text + NEW_LINE;
		}
		
		return fields;
	}
	
	/** Old STARS receiver table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(INV_ID)
	 * 3		INSTALL_DATE
	 * 4-10		INV_NOTES
	 * 11		DEVICE_STATUS
	 * 12,15,18	R1_GROUP,R2_GROUP,R3_GROUP
	 * 13,16,19	INV_NOTES
	 * 14,17,20	R1_STATUS,R2_STATUS,R3_STATUS
	 */
	private static String[] parseStarsReceiver(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_INV_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 20)
			throw new WebClientException( "Incorrect number of fields in receiver file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_INV_ID] = columns[1];
		fields[ImportManagerUtil.IDX_INSTALL_DATE] = columns[2];
		for (int i = 3; i <= 5; i++) {
			if (columns[i].indexOf(':') < columns[i].length() - 1)
				fields[ImportManagerUtil.IDX_INV_NOTES] += columns[i] + NEW_LINE;
		}
		if (columns[6].length() > 0)
			fields[ImportManagerUtil.IDX_INV_NOTES] += "Technician: " + columns[6] + NEW_LINE;
		if (columns[7].length() > 0)
			fields[ImportManagerUtil.IDX_INV_NOTES] += "Location: " + columns[7] + NEW_LINE;
		if (columns[8].length() > 0) {
			if (getMeaningfulText(columns[8]) != null)
				fields[ImportManagerUtil.IDX_INV_NOTES] += columns[8] + NEW_LINE;
		}
		if (columns[9].length() > 0)
			fields[ImportManagerUtil.IDX_INV_NOTES] += columns[9] + NEW_LINE;
		fields[ImportManagerUtil.IDX_DEVICE_STATUS] = columns[10];
		for (int i = 0; i < 3; i++) {
			fields[ImportManagerUtil.IDX_R1_GROUP + i] = columns[11+3*i].substring( "RX-GROUP:".length() );
			if (columns[12+3*i].length() > 0) {
				if (getMeaningfulText(columns[12+3*i]) != null)
					fields[ImportManagerUtil.IDX_INV_NOTES] += columns[12+3*i] + NEW_LINE;
			}
			fields[ImportManagerUtil.IDX_R1_STATUS + i] = columns[13+3*i].substring( "RX-STATUS:".length() );
		}
		
		return fields;
	}
	
	/** Old STARS meter table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(INV_ID)
	 * 3		DEVICE_NAME
	 * 4		INV_NOTES
	 * 5		INSTALL_DATE
	 * 6		INV_NOTES		
	 */
	private static String[] parseStarsMeter(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_INV_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 6)
			throw new WebClientException( "Incorrect number of fields in meter file" );

		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_INV_ID] = columns[1];
		fields[ImportManagerUtil.IDX_DEVICE_NAME] = columns[2];
		if (columns[3].length() > 0)
			fields[ImportManagerUtil.IDX_INV_NOTES] += "Technician: " + columns[3] + NEW_LINE;
		fields[ImportManagerUtil.IDX_INSTALL_DATE] = columns[4];
		if (columns[5].length() > 0)
			fields[ImportManagerUtil.IDX_INV_NOTES] += columns[5];
		
		return fields;
	}
	
	/** Old STARS load info table
	 * COL_NUM:	COL_NAME
	 * 1		APP_ID
	 * 2		(ACCOUNT_ID)
	 * 3		(INV_ID)
	 * 4		RELAY_NUM
	 * 5		APP_DESC
	 * 6		APP_TYPE
	 * 7		APP_NOTES
	 * 8		MANUFACTURER
	 * 9-11		APP_NOTES
	 * 12		AVAIL_FOR_CTRL
	 * 13		YEAR_MADE
	 * 14-21	APP_NOTES
	 */
	private static String[] parseStarsLoadInfo(String line, Hashtable userLabels) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 21)
			throw new WebClientException( "Incorrect number of fields in load info file" );
		
		fields[ImportManagerUtil.IDX_APP_ID] = columns[0];
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[1];
		fields[ImportManagerUtil.IDX_INV_ID] = columns[2];
		fields[ImportManagerUtil.IDX_RELAY_NUM] = columns[3];
		fields[ImportManagerUtil.IDX_APP_DESC] = columns[4];
		fields[ImportManagerUtil.IDX_APP_TYPE] = columns[5];
		if (columns[6].length() > 0)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "EquipCode: " + columns[6] + NEW_LINE;
		fields[ImportManagerUtil.IDX_MANUFACTURER] = columns[7];
		if (columns[8].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_CONTRACTOR1) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_CONTRACTOR1) + ": " + columns[8] + NEW_LINE;
		if (columns[9].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_CONTRACTOR2) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_CONTRACTOR2) + ": " + columns[9] + NEW_LINE;
		if (columns[10].length() > 0)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "WarrantyInfo: " + columns[10] + NEW_LINE;
		fields[ImportManagerUtil.IDX_AVAIL_FOR_CTRL] = columns[11];
		fields[ImportManagerUtil.IDX_YEAR_MADE] = columns[12];
		if (columns[13].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_LI_CHAR1) != null) {
			String text = getMeaningfulText( columns[13] );
			if (text != null)
				fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_LI_CHAR1) + ": " + text + NEW_LINE;
		}
		if (columns[14].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_LI_DROPBOX1) != null) {
			String text = getMeaningfulText( columns[14] );
			if (text != null)
				fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_LI_DROPBOX1) + ": " + text + NEW_LINE;
		}
		if (columns[15].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_LI_DROPBOX2) != null) {
			String text = getMeaningfulText( columns[15] );
			if (text != null)
				fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_LI_DROPBOX2) + ": " + text + NEW_LINE;
		}
		if (columns[16].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_LI_DROPBOX3) != null) {
			String text = getMeaningfulText( columns[16] );
			if (text != null)
				fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_LI_DROPBOX3) + ": " + text + NEW_LINE;
		}
		if (columns[17].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_LI_CHECKBOX1) != null) {
			String text = getMeaningfulText( columns[17] );
			if (text != null)
				fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_LI_CHECKBOX1) + ": " + text + NEW_LINE;
		}
		if (columns[18].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_LI_CHECKBOX2) != null) {
			String text = getMeaningfulText( columns[18] );
			if (text != null)
				fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_LI_CHECKBOX2) + ": " + text + NEW_LINE;
		}
		if (columns[19].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_LI_NUMERIC1) != null) {
			String text = getMeaningfulText( columns[19] );
			if (text != null)
				fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_LI_NUMERIC1) + ": " + text + NEW_LINE;
		}
		if (columns[20].length() > 0 && userLabels.get(ImportManagerUtil.LABEL_LI_NUMERIC2) != null) {
			String text = getMeaningfulText( columns[20] );
			if (text != null)
				fields[ImportManagerUtil.IDX_APP_NOTES] += userLabels.get(ImportManagerUtil.LABEL_LI_NUMERIC2) + ": " + text + NEW_LINE;
		}
		
		return fields;
	}
	
	/** Old STARS work order table
	 * COL_NUM:	COL_NAME
	 * 1		ORDER_NO
	 * 2		(ACCOUNT_ID)
	 * 3		(INV_ID)
	 * 4-8		----
	 * 9		DATE_REPORTED
	 * 10		DATE_COMPLETED
	 * 11		ORDER_STATUS
	 * 12		ORDER_TYPE
	 * 13		ORDER_DESC
	 * 14		ACTION_TAKEN
	 * 15		TIME_SCHEDULED
	 * 16		DATE_SCHEDULED
	 * 17-19	ACTION_TAKEN
	 * 20		SERVICE_COMPANY
	 */
	private static String[] parseStarsWorkOrder(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_ORDER_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 20)
			throw new WebClientException( "Incorrect number of fields in work order file" );
		
		fields[ImportManagerUtil.IDX_ORDER_NO] = columns[0];
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[1];
		fields[ImportManagerUtil.IDX_INV_ID] = columns[2];
		fields[ImportManagerUtil.IDX_DATE_REPORTED] = columns[8];
		fields[ImportManagerUtil.IDX_DATE_COMPLETED] = columns[9];
		fields[ImportManagerUtil.IDX_ORDER_STATUS] = columns[10];
		fields[ImportManagerUtil.IDX_ORDER_TYPE] = columns[11];
		fields[ImportManagerUtil.IDX_ORDER_DESC] = columns[12] + NEW_LINE;
		fields[ImportManagerUtil.IDX_ACTION_TAKEN] = columns[13] + NEW_LINE;
		fields[ImportManagerUtil.IDX_TIME_SCHEDULED] = columns[14];
		if (fields[ImportManagerUtil.IDX_TIME_SCHEDULED].length() > 0)
			fields[ImportManagerUtil.IDX_ORDER_DESC] += "Time Scheduled: " + fields[ImportManagerUtil.IDX_TIME_SCHEDULED];
		fields[ImportManagerUtil.IDX_DATE_SCHEDULED] = columns[15];
		if (columns[16].length() > 0) {
			String text = getMeaningfulText( columns[16] );
			if (text != null && text.equalsIgnoreCase("YES")) {
				fields[ImportManagerUtil.IDX_ACTION_TAKEN] += "Overtime: " + text + NEW_LINE;
				try {
					if (Double.parseDouble( columns[17] ) > 0)
						fields[ImportManagerUtil.IDX_ACTION_TAKEN] += "Overtime Hours: " + columns[17] + NEW_LINE;
				}
				catch (NumberFormatException e) {}
			}
		}
		if (columns[18].length() > 0)
			fields[ImportManagerUtil.IDX_ACTION_TAKEN] += "Technician: " + columns[18];
		fields[ImportManagerUtil.IDX_ORDER_CONTRACTOR] = columns[19];
		
		return fields;
	}
	
	/** Old STARS residence info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		OWNERSHIP_TYPE
	 * 3		RES_TYPE
	 * 4		CONSTRUCTION_TYPE
	 * 5		DECADE_BUILT
	 * 6		SQUARE_FEET
	 * 7		NUM_OCCUPANTS
	 * 8		GENERAL_COND
	 * 9		INSULATION_DEPTH
	 * 10		MAIN_FUEL_TYPE
	 * 11		RES_NOTES
	 * 12		MAIN_COOLING_SYS
	 * 13,14	RES_NOTES
	 * 15		MAIN_HEATING_SYS
	 * 16-21	RES_NOTES
	 */
	private static String[] parseStarsResidenceInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_RES_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 21)
			throw new WebClientException( "Incorrect number of fields in residence info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_OWNERSHIP_TYPE] = columns[1];
		fields[ImportManagerUtil.IDX_RES_TYPE] = columns[2];
		fields[ImportManagerUtil.IDX_CONSTRUCTION_TYPE] = columns[3];
		fields[ImportManagerUtil.IDX_DECADE_BUILT] = columns[4];
		fields[ImportManagerUtil.IDX_SQUARE_FEET] = columns[5];
		fields[ImportManagerUtil.IDX_NUM_OCCUPANTS] = columns[6];
		fields[ImportManagerUtil.IDX_GENERAL_COND] = columns[7];
		fields[ImportManagerUtil.IDX_INSULATION_DEPTH] = columns[8];
		fields[ImportManagerUtil.IDX_MAIN_FUEL_TYPE] = columns[9];
		//fields[IDX_RES_NOTES] += "SetBackThermostat: " + columns[10] + NEW_LINE;
		fields[ImportManagerUtil.IDX_MAIN_COOLING_SYS] = columns[11];
		//fields[IDX_RES_NOTES] += "CoolingSystemYearInstalled: " + columns[12] + NEW_LINE;
		//fields[IDX_RES_NOTES] += "CoolingSystemEff.: " + columns[13] + NEW_LINE;
		fields[ImportManagerUtil.IDX_MAIN_HEATING_SYS] = columns[14];
		//fields[IDX_RES_NOTES] += "HeatingSystemYearInstalled: " + columns[15] + NEW_LINE;
		//fields[IDX_RES_NOTES] += "HeatingSystemEfficiency: " + columns[16] + NEW_LINE;
		//fields[IDX_RES_NOTES] += "EnergyAudit: " + columns[17] + NEW_LINE;
		//fields[IDX_RES_NOTES] += "HeatLoss: " + columns[18] + NEW_LINE;
		//fields[IDX_RES_NOTES] += "HeatGain: " + columns[19] + NEW_LINE;
		//fields[IDX_RES_NOTES] += "EnergyManagementParticipant: " + columns[20];
		
		return fields;
	}
	
	/** Old STARS AC info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_KW
	 * 4		APP_NOTES
	 * 5		AC_TONNAGE
	 * 6,7		NOTES
	 */
	private static String[] parseStarsACInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 7)
			throw new WebClientException( "Incorrect number of fields in AC info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_APP_ID] = columns[1];
		fields[ImportManagerUtil.IDX_APP_KW] = columns[2];
		if (getMeaningfulText(columns[3]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "Rebate: " + columns[3] + NEW_LINE;
		fields[ImportManagerUtil.IDX_AC_TONNAGE] = columns[4];
		if (getMeaningfulText(columns[5]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "BTU_Hour: " + columns[5] + NEW_LINE;
		fields[ImportManagerUtil.IDX_APP_NOTES] += columns[6];
			
		return fields;
	}
	
	/** Old STARS WH info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_KW
	 * 4		APP_NOTES
	 * 5		WH_NUM_GALLONS
	 * 6		WH_NUM_ELEMENTS
	 * 7		WH_ENERGY_SRC
	 * 8		WH_LOCATION
	 * 9		APP_NOTES
	 */
	private static String[] parseStarsWHInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 9)
			throw new WebClientException( "Incorrect number of fields in WH info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_APP_ID] = columns[1];
		fields[ImportManagerUtil.IDX_APP_KW] = columns[2];
		if (getMeaningfulText(columns[3]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "Rebate: " + columns[3] + NEW_LINE;
		fields[ImportManagerUtil.IDX_WH_NUM_GALLONS] = columns[4];
		fields[ImportManagerUtil.IDX_WH_NUM_ELEMENTS] = columns[5];
		fields[ImportManagerUtil.IDX_WH_ENERGY_SRC] = columns[6];
		if (columns[7].length() > 0)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "Location: " + columns[7] + NEW_LINE;
		fields[ImportManagerUtil.IDX_APP_NOTES] += columns[8];
		
		return fields;
	}
	
	/** Old STARS generator info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_NOTES
	 * 4		GEN_FUEL_CAP
	 * 5		GEN_START_DELAY
	 * 6		GEN_CAPACITY
	 * 7		GEN_TRAN_SWITCH_MFC
	 * 8		GEN_TRAN_SWITCH_TYPE
	 */
	private static String[] parseStarsGeneratorInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 8)
			throw new WebClientException( "Incorrect number of fields in generator info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_APP_ID] = columns[1];
		if (getMeaningfulText(columns[2]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "StandbyKW: " + columns[2] + NEW_LINE;
		fields[ImportManagerUtil.IDX_GEN_FUEL_CAP] = columns[3];
		fields[ImportManagerUtil.IDX_GEN_START_DELAY] = columns[4];
		fields[ImportManagerUtil.IDX_GEN_CAPACITY] = columns[5];
		fields[ImportManagerUtil.IDX_GEN_TRAN_SWITCH_MFC] = columns[6];
		fields[ImportManagerUtil.IDX_GEN_TRAN_SWITCH_TYPE] = columns[7];
		
		return fields;
	}
	
	/** Old STARS irrigation info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_KW
	 * 4		APP_NOTES
	 * 5		IRR_TYPE
	 * 6		IRR_ENERGY_SRC
	 * 7		IRR_HORSE_POWER
	 * 8		IRR_METER_VOLT
	 * 9		IRR_METER_LOC
	 * 10		IRR_SOIL_TYPE
	 */
	private static String[] parseStarsIrrigationInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 10)
			throw new WebClientException( "Incorrect number of fields in irrigation info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_APP_ID] = columns[1];
		fields[ImportManagerUtil.IDX_APP_KW] = columns[2];
		if (getMeaningfulText(columns[3]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "Rebate: " + columns[3] + NEW_LINE;
		fields[ImportManagerUtil.IDX_IRR_TYPE] = columns[4];
		fields[ImportManagerUtil.IDX_IRR_ENERGY_SRC] = columns[5];
		fields[ImportManagerUtil.IDX_IRR_HORSE_POWER] = columns[6];
		fields[ImportManagerUtil.IDX_IRR_METER_VOLT] = columns[7];
		fields[ImportManagerUtil.IDX_IRR_METER_LOC] += columns[8];
		fields[ImportManagerUtil.IDX_IRR_SOIL_TYPE] = columns[9];
		
		return fields;
	}
	
	/** Old STARS grain dryer info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_KW
	 * 4		APP_NOTES
	 * 5		GDRY_TYPE
	 * 6		GDRY_ENERGY_SRC
	 * 7		GDRY_HORSE_POWER
	 * 8		GDRY_HEAT_SRC
	 * 9		GDRY_BIN_SIZE
	 */
	private static String[] parseStarsGrainDryerInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 9)
			throw new WebClientException( "Incorrect number of fields in grain dryer info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_APP_ID] = columns[1];
		fields[ImportManagerUtil.IDX_APP_KW] = columns[2];
		if (getMeaningfulText(columns[3]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "Rebate: " + columns[3] + NEW_LINE;
		fields[ImportManagerUtil.IDX_GDRY_TYPE] = columns[4];
		fields[ImportManagerUtil.IDX_GDRY_ENERGY_SRC] = columns[5];
		fields[ImportManagerUtil.IDX_GDRY_HORSE_POWER] = columns[6];
		fields[ImportManagerUtil.IDX_GDRY_HEAT_SRC] = columns[7];
		fields[ImportManagerUtil.IDX_GDRY_BIN_SIZE] = columns[8];
		
		return fields;
	}
	
	/** Old STARS heat pump info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_KW
	 * 4		APP_NOTES
	 * 5		HP_TYPE
	 * 6		HP_SIZE
	 * 7		HP_STANDBY_SRC
	 * 8		HP_RESTART_DELAY
	 * 9		APP_NOTES
	 */
	private static String[] parseStarsHeatPumpInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 9)
			throw new WebClientException( "Incorrect number of fields in heat pump info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_APP_ID] = columns[1];
		fields[ImportManagerUtil.IDX_APP_KW] = columns[2];
		if (getMeaningfulText(columns[3]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "Rebate: " + columns[3] + NEW_LINE;
		fields[ImportManagerUtil.IDX_HP_TYPE] = columns[4];
		fields[ImportManagerUtil.IDX_HP_SIZE] = columns[5];
		fields[ImportManagerUtil.IDX_HP_STANDBY_SRC] = columns[6];
		fields[ImportManagerUtil.IDX_HP_RESTART_DELAY] = columns[7];
		fields[ImportManagerUtil.IDX_APP_NOTES] += columns[8];
		
		return fields;
	}
	
	/** Old STARS storage heat info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_KW
	 * 4		APP_NOTES
	 * 5		SH_TYPE
	 * 6		SH_CAPACITY
	 * 7		SH_RECHARGE_TIME
	 * 8,9		APP_NOTES
	 */
	private static String[] parseStarsStorageHeatInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 9)
			throw new WebClientException( "Incorrect number of fields in storage heat info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_APP_ID] = columns[1];
		fields[ImportManagerUtil.IDX_APP_KW] = columns[2];
		if (getMeaningfulText(columns[3]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "Rebate: " + columns[3] + NEW_LINE;
		fields[ImportManagerUtil.IDX_SH_TYPE] = columns[4];
		fields[ImportManagerUtil.IDX_SH_CAPACITY] = columns[5];
		fields[ImportManagerUtil.IDX_SH_RECHARGE_TIME] = columns[6];
		if (getMeaningfulText(columns[7]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "ContractHours: " + columns[7] + NEW_LINE;
		fields[ImportManagerUtil.IDX_APP_NOTES] += columns[8];
		
		return fields;
	}
	
	/** Old STARS dual fuel info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_KW
	 * 4,5		APP_NOTES
	 * 6		DF_2ND_ENERGY_SRC
	 * 7		DF_2ND_CAPACITY
	 * 8		DF_SWITCH_OVER_TYPE
	 * 9		APP_NOTES
	 */
	private static String[] parseStarsDualFuelInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 9)
			throw new WebClientException( "Incorrect number of fields in dual fuel info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_APP_ID] = columns[1];
		fields[ImportManagerUtil.IDX_APP_KW] = columns[2];
		if (getMeaningfulText(columns[3]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "Rebate: " + columns[3] + NEW_LINE;
		if (getMeaningfulText(columns[4]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "PrimarySize: " + columns[4] + NEW_LINE;
		fields[ImportManagerUtil.IDX_DF_2ND_ENERGY_SRC] = columns[5];
		fields[ImportManagerUtil.IDX_DF_2ND_CAPACITY] = columns[6];
		fields[ImportManagerUtil.IDX_DF_SWITCH_OVER_TYPE] = columns[7];
		fields[ImportManagerUtil.IDX_APP_NOTES] += columns[8];
		
		return fields;
	}
	
	/** Old STARS general info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_KW
	 * 4,5		APP_NOTES
	 */
	private static String[] parseStarsGeneralInfo(String line) throws Exception {
		String[] fields = ImportManagerUtil.prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
		String[] columns = StarsUtils.splitString( line, "," );
		if (columns.length != 5)
			throw new WebClientException( "Incorrect number of fields in general info file" );
		
		fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[0];
		fields[ImportManagerUtil.IDX_APP_ID] = columns[1];
		fields[ImportManagerUtil.IDX_APP_KW] = columns[2];
		if (getMeaningfulText(columns[3]) != null)
			fields[ImportManagerUtil.IDX_APP_NOTES] += "Rebate: " + columns[3] + NEW_LINE;
		fields[ImportManagerUtil.IDX_APP_NOTES] += columns[4];
		
		return fields;
	}
	
	private static void dumpSelectionLists(Hashtable preprocessedData, File importDir, LiteStarsEnergyCompany energyCompany) {
		ArrayList lines = new ArrayList();
		
		for (int i = 0; i < ImportManagerUtil.LIST_NAMES.length; i++) {
			if (ImportManagerUtil.LIST_NAMES[i][1].length() == 0) continue;
			
			TreeMap valueIDMap = (TreeMap) preprocessedData.get( ImportManagerUtil.LIST_NAMES[i][0] );
			if (valueIDMap.size() == 0) continue;
			
			lines.add( "[" + ImportManagerUtil.LIST_NAMES[i][0] + "]" );
			
			Iterator it = valueIDMap.keySet().iterator();
			while (it.hasNext()) {
				String value = (String) it.next();
				Integer id = (Integer) valueIDMap.get( value );
				
				if (id == null) {
					if (value.trim().length() > 0)
						lines.add( "@" + value + "=" );
				}
				else {
					String line = value + "=";
					if (id.intValue() > 0) {
						if (ImportManagerUtil.LIST_NAMES[i][0].equals("ServiceCompany")) {
							ArrayList companies = energyCompany.getAllServiceCompanies();
							for (int j = 0; j < companies.size(); j++) {
								LiteServiceCompany liteCompany = (LiteServiceCompany) companies.get(j);
								if (liteCompany.getCompanyID() == id.intValue()) {
									line += "\"" + liteCompany.getCompanyName() + "\"";
									break;
								}
							}
						}
						else if (ImportManagerUtil.LIST_NAMES[i][0].equals("LoadType")) {
							ArrayList appCats = energyCompany.getAllApplianceCategories();
							for (int j = 0; j < appCats.size(); j++) {
								LiteApplianceCategory liteAppCat = (LiteApplianceCategory) appCats.get(j);
								if (liteAppCat.getApplianceCategoryID() == id.intValue()) {
									line += "\"" + liteAppCat.getDescription() + "\"";
									break;
								}
							}
						}
						else if (ImportManagerUtil.LIST_NAMES[i][0].equals("LoadGroup")) {
							line += "\"" + PAOFuncs.getYukonPAOName( id.intValue() ) + "\"";
						}
						else {
							YukonSelectionList list = energyCompany.getYukonSelectionList( ImportManagerUtil.LIST_NAMES[i][0] );
							for (int j = 0; j < list.getYukonListEntries().size(); j++) {
								YukonListEntry entry = (YukonListEntry) list.getYukonListEntries().get(j);
								if (entry.getEntryID() == id.intValue()) {
									line += "\"" + entry.getEntryText() + "\"";
									break;
								}
							}
						}
					}
					
					lines.add( line );
				}
			}
			
			lines.add( "" );
		}
		
		String[] lns = new String[ lines.size() ];
		lines.toArray( lns );
		
		File custListFile = new File( importDir, "_custlist.map" );
		try {
			StarsUtils.writeFile( custListFile, lns );
		}
		catch (IOException e) {
			CTILogger.error( e.getMessage(), e );
		}
	}
	
	public static void importSelectionLists(File selListFile, LiteStarsEnergyCompany energyCompany) throws Exception {
		String[] lines = StarsUtils.readFile( selListFile );
		if (lines == null)
			throw new WebClientException("Unable to read file '" + selListFile.getPath() + "'");
		
		String listName = null;
		ArrayList listEntries = null;
		boolean isInList = false;
		
		for (int i = 0; i < lines.length; i++) {
			if (!isInList) {
				if (!lines[i].startsWith("[")) continue;
				
				for (int j = 0; j < ImportManagerUtil.LIST_NAMES.length; j++) {
					if (ImportManagerUtil.LIST_NAMES[j][2].equals( lines[i] )) {
						listName = ImportManagerUtil.LIST_NAMES[j][0];
						listEntries = new ArrayList();
						isInList = true;
						break;
					}
				}
			}
			else if (lines[i].trim().length() > 0) {
				if (lines[i].endsWith("="))
					lines[i] = lines[i].substring(0, lines[i].length() - 1);
				listEntries.add( lines[i] );
			}
			else {
				// Find the end of a list, update the list entries
				isInList = false;
				
				if (listName.equals("ServiceCompany")) {
					StarsAdminUtil.deleteAllServiceCompanies( energyCompany );
					
					for (int j = 0; j < listEntries.size(); j++) {
						String entry = (String) listEntries.get(j);
						StarsAdminUtil.createServiceCompany( entry, energyCompany );
					}
				}
				else if (listName.equals("Substation")) {
					StarsAdminUtil.deleteAllSubstations( energyCompany );
					
					for (int j = 0; j < listEntries.size(); j++) {
						String entry = (String) listEntries.get(j);
						String subName = null;
						int routeID = 0;
						
						int pos = entry.indexOf('=');
						if (pos >= 0) {
							subName = entry.substring(0, pos);
							String routeName = entry.substring(pos + 1);
							if (routeName.length() > 0) {
								LiteYukonPAObject[] routes = PAOFuncs.getAllLiteRoutes();
								for (int k = 0; k < routes.length; k++) {
									if (routes[k].getPaoName().equalsIgnoreCase( routeName )) {
										routeID = routes[k].getYukonID();
										break;
									}
								}
							}
						}
						else {
							subName = entry;
						}
						
						StarsAdminUtil.createSubstation( subName, routeID, energyCompany );
					}
				}
				else if (listName.equals("LoadType")) {
					for (int j = 0; j < listEntries.size(); j++) {
						String entry = (String) listEntries.get(j);
						StarsAdminUtil.createApplianceCategory( entry, energyCompany );
					}
				}
				else {
					// Always add an empty entry at the beginning of the list
					listEntries.add(0, " ");
					
					Object[][] entryData = new Object[ listEntries.size() ][];
					for (int j = 0; j < listEntries.size(); j++) {
						entryData[j] = new Object[3];
						entryData[j][0] = ZERO;
						entryData[j][1] = listEntries.get(j);
						entryData[j][2] = ZERO;
					}
					
					YukonSelectionList cList = energyCompany.getYukonSelectionList(listName, false, false);
					if (cList == null)
						throw new WebClientException("Cannot import data into an inherited selection list.");
					
					StarsAdminUtil.updateYukonListEntries( cList, entryData, energyCompany );
				}
			}
		}
	}
	
	public static void importAppSettings(File appSettingsFile, HttpSession session) throws WebClientException {
		String[] lines = StarsUtils.readFile( appSettingsFile );
		if (lines == null)
			throw new WebClientException( "Unable to read file '" + appSettingsFile.getPath() + "'" );
		
		String sectionName = null;
		Hashtable userLabels = new Hashtable();
		
		Hashtable deviceTypes = new Hashtable();
		deviceTypes.put( "201", "LCR-1000" );
		deviceTypes.put( "202", "LCR-2000" );
		deviceTypes.put( "203", "LCR-3000" );
		deviceTypes.put( "204", "LCR-4000" );
		deviceTypes.put( "10", "MCT-210" );
		deviceTypes.put( "11", "MCT-212" );
		deviceTypes.put( "12", "MCT-213" );
		deviceTypes.put( "20", "MCT-224" );
		deviceTypes.put( "21", "MCT-226" );
		deviceTypes.put( "30", "MCT-240" );
		deviceTypes.put( "31", "MCT-242" );
		deviceTypes.put( "32", "MCT-248" );
		deviceTypes.put( "40", "MCT-250" );
		deviceTypes.put( "50", "MCT-260" );
		deviceTypes.put( "60", "MCT-310" );
		deviceTypes.put( "64", "MCT-310ID" );
		deviceTypes.put( "68", "MCT-318" );
		deviceTypes.put( "69", "MCT-310IL" );
		deviceTypes.put( "70", "MCT-318L" );
		deviceTypes.put( "75", "MCT-360" );
		deviceTypes.put( "80", "MCT-370" );
		
		Hashtable woStatus = new Hashtable();
		woStatus.put( "0", ImportManagerUtil.WO_STATUS_CLOSED );
		woStatus.put( "1", ImportManagerUtil.WO_STATUS_OPEN );
		woStatus.put( "2", ImportManagerUtil.WO_STATUS_SCHEDULED );
		woStatus.put( "99", ImportManagerUtil.WO_STATUS_WAITING );
		woStatus.put( "65535", ImportManagerUtil.WO_STATUS_OPEN );
		
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].trim().length() == 0) continue;
			if (lines[i].charAt(0) == ';') continue;
			
			if (lines[i].charAt(0) == '[') {
				sectionName = lines[i];
				continue;
			}
			
			int pos = lines[i].indexOf('=');
			if (pos < 0) continue;
			String name = lines[i].substring( 0, pos );
			String value = lines[i].substring( pos+1 );
			
			if (sectionName.equalsIgnoreCase("[User Labels]")) {
				if (!value.startsWith("@"))
					userLabels.put( name, value );
			}
			else if (sectionName.equalsIgnoreCase("[Device Types]")) {
				deviceTypes.put( value, name );
			}
			else if (sectionName.equalsIgnoreCase("[Work Order Status]")) {
				woStatus.put( value, name );
			}
		}
		
		session.setAttribute( ImportManagerUtil.USER_LABELS, userLabels );
		session.setAttribute( ImportManagerUtil.DEVICE_TYPES, deviceTypes );
		session.setAttribute( ImportManagerUtil.WORK_ORDER_STATUS, woStatus );
	}
	
	public static void preProcessStarsData(File importDir, HttpSession session, LiteStarsEnergyCompany energyCompany) throws WebClientException {
		try {
			getResumeLocation(importDir);
			
			File custFile = new File( importDir, "customer.txt" );
			File servInfoFile = new File( importDir, "servinfo.txt" );
			File invFile = new File( importDir, "invent.txt" );
			File recvrFile = new File( importDir, "receiver.txt" );
			File meterFile = new File( importDir, "meter.txt" );
			File loadInfoFile = new File( importDir, "loadinfo.txt" );
			File acInfoFile = new File( importDir, "acinfo.txt" );
			File whInfoFile = new File( importDir, "whinfo.txt" );
			File genInfoFile = new File( importDir, "geninfo.txt" );
			File irrInfoFile = new File( importDir, "irrinfo.txt" );
			File gdryInfoFile = new File( importDir, "gdryinfo.txt" );
			File hpInfoFile = new File( importDir, "hpinfo.txt" );
			File shInfoFile = new File( importDir, "shinfo.txt" );
			File dfInfoFile = new File( importDir, "dfinfo.txt" );
			File genlInfoFile = new File( importDir, "genlinfo.txt" );
			File workOrderFile = new File( importDir, "workordr.txt" );
			File resInfoFile = new File( importDir, "resinfo.txt" );
			
			Hashtable preprocessedData = (Hashtable) session.getAttribute(ImportManagerUtil.PREPROCESSED_DATA);
			if (preprocessedData != null) {
				// Clear up the old data from memory
				ArrayList acctFieldsList = (ArrayList) preprocessedData.get("CustomerAccount");
				if (acctFieldsList != null) {
					acctFieldsList.clear();
					preprocessedData.remove( "CustomerAccount" );
				}
				
				ArrayList invFieldsList = (ArrayList) preprocessedData.get("Inventory");
				if (invFieldsList != null) {
					invFieldsList.clear();
					preprocessedData.remove( "Inventory" );
				}
				 
				ArrayList appFieldsList = (ArrayList) preprocessedData.get("Appliance");
				if (appFieldsList != null) {
					appFieldsList.clear();
					preprocessedData.remove( "Appliance" );
				}
				
				ArrayList orderFieldsList = (ArrayList) preprocessedData.get("WorkOrder");
				if (orderFieldsList != null) {
					orderFieldsList.clear();
					preprocessedData.remove( "WorkOrder" );
				}
				
				ArrayList resFieldsList = (ArrayList) preprocessedData.get("CustomerResidence");
				if (resFieldsList != null) {
					resFieldsList.clear();
					preprocessedData.remove( "CustomerResidence" );
				}
			}
			else
				preprocessedData = new Hashtable();
			
			Hashtable userLabels = (Hashtable) session.getAttribute( ImportManagerUtil.USER_LABELS );
			if (userLabels == null) userLabels = new Hashtable();
			Hashtable deviceTypes = (Hashtable) session.getAttribute( ImportManagerUtil.DEVICE_TYPES );
			if (deviceTypes == null) deviceTypes = new Hashtable();
			Hashtable woStatus = (Hashtable) session.getAttribute( ImportManagerUtil.WORK_ORDER_STATUS );
			if (woStatus == null) woStatus = new Hashtable();
			
			Hashtable acctIDFields = new Hashtable();	// Map from account id(Integer) to fields(String[])
			Hashtable invIDFields = new Hashtable();	// Map from inventory id(Integer) to fields(String[])
			Hashtable acctIDAppFields = new Hashtable();	// Map from account id(Integer) to (Map from appliance id (Integer) to fields(String[]))
			
			ArrayList acctFieldsList = new ArrayList();		// List of all account fields(String[])
			ArrayList invFieldsList = new ArrayList();		// List of all inventory fields(String[])
			ArrayList appFieldsList = new ArrayList();		// List of all appliance fields(String[])
			ArrayList orderFieldsList = new ArrayList();	// List of all work order fields(String[])
			ArrayList resFieldsList = new ArrayList();		// List of all residence fields(String[])
			
			// Sorted maps of import value(String) to id(Integer), filled in assignSelectionList()
			TreeMap[] valueIDMaps = new TreeMap[ ImportManagerUtil.LIST_NAMES.length ];
			for (int i = 0; i < ImportManagerUtil.LIST_NAMES.length; i++) {
				valueIDMaps[i] = (TreeMap) preprocessedData.get( ImportManagerUtil.LIST_NAMES[i][0] );
				if (valueIDMaps[i] == null) valueIDMaps[i] = new TreeMap();
			}
			
			if (resumeFile <= RESUME_FILE_CUSTOMER) {
				if (custFile == null)
					throw new WebClientException("File \"customer.txt\" not found!");
				BufferedReader fr = null;
				
				try {
					fr = new BufferedReader(new FileReader(custFile));
					String line = null;
					int lineNo = 0;
					
					while ((line = fr.readLine()) != null) {
						lineNo++;
						if (line.trim().length() == 0 || line.charAt(0) == '#')
							continue;
						if (resumeFile == RESUME_FILE_CUSTOMER && lineNo < resumeLine)
							continue;
						
						String[] fields = parseStarsCustomer( line );
						fields[ImportManagerUtil.IDX_LINE_NUM] = String.valueOf(lineNo);
						acctFieldsList.add( fields );
						acctIDFields.put( fields[ImportManagerUtil.IDX_ACCOUNT_ID], fields );
					}
				}
				finally {
					if (fr != null) fr.close();
				}
				
				String[] servInfoLines = StarsUtils.readFile( servInfoFile, false );
				if (servInfoLines != null) {
					for (int i = 0; i < servInfoLines.length; i++) {
						String[] fields = parseStarsServiceInfo( servInfoLines[i], userLabels );
						String[] custFields = (String[]) acctIDFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
						
						if (custFields != null) {
							for (int j = 0; j < custFields.length; j++)
								if (fields[j].length() > 0) custFields[j] = fields[j];
							
							for (int j = 0; j < ImportManagerUtil.SERVINFO_LIST_FIELDS.length; j++) {
								int listIdx = ImportManagerUtil.SERVINFO_LIST_FIELDS[j][0];
								int fieldIdx = ImportManagerUtil.SERVINFO_LIST_FIELDS[j][1];
								if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
									Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
									if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
								}
							}
						}
					}
				}
			}
			
			if (resumeFile <= RESUME_FILE_INVENTORY) {
				if (invFile == null)
					throw new WebClientException("File \"invent.txt\" not found!");
				BufferedReader fr = null;
				
				try {
					fr = new BufferedReader(new FileReader(invFile));
					String line = null;
					int lineNo = 0;
					
					while ((line = fr.readLine()) != null) {
						lineNo++;
						if (line.trim().length() == 0 || line.charAt(0) == '#')
							continue;
						if (resumeFile == RESUME_FILE_INVENTORY && lineNo < resumeLine)
							continue;
						
						String[] fields = parseStarsInventory( line, userLabels );
						fields[ImportManagerUtil.IDX_LINE_NUM] = String.valueOf(lineNo);
						
						if (fields[ImportManagerUtil.IDX_DEVICE_TYPE].equals("") || fields[ImportManagerUtil.IDX_DEVICE_TYPE].equals("-1"))
							continue;
						
						invFieldsList.add( fields );
						invIDFields.put( fields[ImportManagerUtil.IDX_INV_ID], fields );
						
						for (int j = 0; j < ImportManagerUtil.INV_LIST_FIELDS.length; j++) {
							int listIdx = ImportManagerUtil.INV_LIST_FIELDS[j][0];
							int fieldIdx = ImportManagerUtil.INV_LIST_FIELDS[j][1];
							
							if (fieldIdx == ImportManagerUtil.IDX_DEVICE_TYPE) {
								String text = (String) deviceTypes.get( fields[fieldIdx] );
								if (text == null)
									throw new WebClientException( "Inventory file line #" + lineNo + ": invalid device type " + fields[fieldIdx] );
								if (text.startsWith("MCT"))
									fields[fieldIdx] = "MCT";
								else
									fields[fieldIdx] = text;
							}
							
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
				finally {
					if (fr != null) fr.close();
				}
				
				String[] recvrLines = StarsUtils.readFile( recvrFile, false );
				if (recvrLines != null) {
					for (int i = 0; i < recvrLines.length; i++) {
						String[] fields = parseStarsReceiver( recvrLines[i] );
						String[] invFields = (String[]) invIDFields.get( fields[ImportManagerUtil.IDX_INV_ID] );
						
						if (invFields != null) {
							// Use hardware action field as a marker of whether an entry in the
							// inventory file has a corresponding entry in the receiver or meter file
							invFields[ImportManagerUtil.IDX_HARDWARE_ACTION] = "Receiver";
							
							for (int j = 0; j < invFields.length; j++) {
								if (fields[j].length() > 0) {
									if (j == ImportManagerUtil.IDX_INV_NOTES && invFields[j].length() > 0)
										invFields[j] += fields[j];
									else
										invFields[j] = fields[j];
								}
							}
							
							for (int j = 0; j < ImportManagerUtil.RECV_LIST_FIELDS.length; j++) {
								int listIdx = ImportManagerUtil.RECV_LIST_FIELDS[j][0];
								int fieldIdx = ImportManagerUtil.RECV_LIST_FIELDS[j][1];
								
								if (fieldIdx == ImportManagerUtil.IDX_DEVICE_STATUS) {
									if (fields[fieldIdx].equals("SwitchStatus:1"))
										fields[fieldIdx] = "Temp Unavail";
									else if (fields[fieldIdx].equals("SwitchStatus:4"))
										fields[fieldIdx] = "Unavailable";
									else if (fields[fieldIdx].equals("SwitchStatus:8"))
										fields[fieldIdx] = "Available";
								}
								
								if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
									Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
									if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
								}
							}
						}
					}
				}
				
				String[] meterLines = StarsUtils.readFile( meterFile, false );
				if (meterLines != null) {
					for (int i = 0; i < meterLines.length; i++) {
						String[] fields = parseStarsMeter( meterLines[i] );
						String[] invFields = (String[]) invIDFields.get( fields[ImportManagerUtil.IDX_INV_ID] );
						
						if (invFields != null) {
							invFields[ImportManagerUtil.IDX_HARDWARE_ACTION] = "Meter";
							
							for (int j = 0; j < invFields.length; j++) {
								if (fields[j].length() > 0) {
									if (j == ImportManagerUtil.IDX_INV_NOTES && invFields[j].length() > 0)
										invFields[j] += fields[j];
									else
										invFields[j] = fields[j];
								}
							}
						}
					}
				}
				
				java.util.Iterator it = invFieldsList.iterator();
				while (it.hasNext()) {
					String[] fields = (String[]) it.next();
					if (fields[ImportManagerUtil.IDX_HARDWARE_ACTION].equals("")) {
						it.remove();
						invIDFields.remove( fields[ImportManagerUtil.IDX_INV_ID] );
					}
				}
			}
			
			if (resumeFile <= RESUME_FILE_LOADINFO) {
				if (loadInfoFile == null)
					throw new WebClientException("File \"loadinfo.txt\" not found!");
				BufferedReader fr = null;
				
				try {
					fr = new BufferedReader(new FileReader(loadInfoFile));
					String line = null;
					int lineNo = 0;
					
					while ((line = fr.readLine()) != null) {
						lineNo++;
						if (line.trim().length() == 0 || line.charAt(0) == '#')
							continue;
						if (resumeFile == RESUME_FILE_LOADINFO && lineNo < resumeLine)
							continue;
						
						String[] fields = parseStarsLoadInfo( line, userLabels );
						if (fields[ImportManagerUtil.IDX_APP_DESC].equals("")) continue;
						
						fields[ImportManagerUtil.IDX_LINE_NUM] = String.valueOf(lineNo);
						appFieldsList.add( fields );
						
						Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
						if (appIDFields == null) {
							appIDFields = new Hashtable();
							acctIDAppFields.put( fields[ImportManagerUtil.IDX_ACCOUNT_ID], appIDFields );
						}
						appIDFields.put( fields[ImportManagerUtil.IDX_APP_ID], fields );
						
						for (int j = 0; j < ImportManagerUtil.APP_LIST_FIELDS.length; j++) {
							int listIdx = ImportManagerUtil.APP_LIST_FIELDS[j][0];
							int fieldIdx = ImportManagerUtil.APP_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
				finally {
					if (fr != null) fr.close();
				}
				
				String[] acInfoLines = StarsUtils.readFile( acInfoFile, false );
				if (acInfoLines != null) {
					for (int i = 0; i < acInfoLines.length; i++) {
						String[] fields = parseStarsACInfo( acInfoLines[i] );
						String[] appFields = null;
						
						Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
						if (appIDFields != null) 
							appFields = (String[]) appIDFields.get( fields[ImportManagerUtil.IDX_APP_ID] );
						
						if (appFields != null) {
							// Set the appliance category field (which will be used to decide the appliance type later)
							appFields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER);
							
							for (int j = 0; j < appFields.length; j++) {
								if (fields[j].length() > 0) {
									if (j == ImportManagerUtil.IDX_APP_NOTES && appFields[j].length() > 0)
										appFields[j] += fields[j];
									else
										appFields[j] = fields[j];
								}
							}
							
							for (int j = 0; j < ImportManagerUtil.AC_LIST_FIELDS.length; j++) {
								int listIdx = ImportManagerUtil.AC_LIST_FIELDS[j][0];
								int fieldIdx = ImportManagerUtil.AC_LIST_FIELDS[j][1];
								if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
									Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
									if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
								}
							}
						}
					}
				}
				
				String[] whInfoLines = StarsUtils.readFile( whInfoFile, false );
				if (whInfoLines != null) {
					for (int i = 0; i < whInfoLines.length; i++) {
						String[] fields = parseStarsWHInfo( whInfoLines[i] );
						String[] appFields = null;
						
						Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
						if (appIDFields != null) 
							appFields = (String[]) appIDFields.get( fields[ImportManagerUtil.IDX_APP_ID] );
						
						if (appFields != null) {
							appFields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER);
							
							for (int j = 0; j < appFields.length; j++) {
								if (fields[j].length() > 0) {
									if (j == ImportManagerUtil.IDX_APP_NOTES && appFields[j].length() > 0)
										appFields[j] += fields[j];
									else
										appFields[j] = fields[j];
								}
							}
							
							for (int j = 0; j < ImportManagerUtil.WH_LIST_FIELDS.length; j++) {
								int listIdx = ImportManagerUtil.WH_LIST_FIELDS[j][0];
								int fieldIdx = ImportManagerUtil.WH_LIST_FIELDS[j][1];
								if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
									Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
									if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
								}
							}
						}
					}
				}
				
				String[] genInfoLines = StarsUtils.readFile( genInfoFile, false );
				if (genInfoLines != null) {
					for (int i = 0; i < genInfoLines.length; i++) {
						String[] fields = parseStarsGeneratorInfo( genInfoLines[i] );
						String[] appFields = null;
						
						Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
						if (appIDFields != null) 
							appFields = (String[]) appIDFields.get( fields[ImportManagerUtil.IDX_APP_ID] );
						
						if (appFields != null) {
							appFields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR);
							
							for (int j = 0; j < appFields.length; j++) {
								if (fields[j].length() > 0) {
									if (j == ImportManagerUtil.IDX_APP_NOTES && appFields[j].length() > 0)
										appFields[j] += fields[j];
									else
										appFields[j] = fields[j];
								}
							}
							
							for (int j = 0; j < ImportManagerUtil.GEN_LIST_FIELDS.length; j++) {
								int listIdx = ImportManagerUtil.GEN_LIST_FIELDS[j][0];
								int fieldIdx = ImportManagerUtil.GEN_LIST_FIELDS[j][1];
								if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
									Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
									if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
								}
							}
						}
					}
				}
				
				String[] irrInfoLines = StarsUtils.readFile( irrInfoFile, false );
				if (irrInfoLines != null) {
					for (int i = 0; i < irrInfoLines.length; i++) {
						String[] fields = parseStarsIrrigationInfo( irrInfoLines[i] );
						String[] appFields = null;
						
						Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
						if (appIDFields != null) 
							appFields = (String[]) appIDFields.get( fields[ImportManagerUtil.IDX_APP_ID] );
						
						if (appFields != null) {
							appFields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION);
							
							for (int j = 0; j < appFields.length; j++) {
								if (fields[j].length() > 0) {
									if (j == ImportManagerUtil.IDX_APP_NOTES && appFields[j].length() > 0)
										appFields[j] += fields[j];
									else
										appFields[j] = fields[j];
								}
							}
							
							for (int j = 0; j < ImportManagerUtil.IRR_LIST_FIELDS.length; j++) {
								int listIdx = ImportManagerUtil.IRR_LIST_FIELDS[j][0];
								int fieldIdx = ImportManagerUtil.IRR_LIST_FIELDS[j][1];
								if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
									Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
									if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
								}
							}
						}
					}
				}
				
				String[] gdryInfoLines = StarsUtils.readFile( gdryInfoFile, false );
				if (gdryInfoLines != null) {
					for (int i = 0; i < gdryInfoLines.length; i++) {
						String[] fields = parseStarsGrainDryerInfo( gdryInfoLines[i] );
						String[] appFields = null;
						
						Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
						if (appIDFields != null) 
							appFields = (String[]) appIDFields.get( fields[ImportManagerUtil.IDX_APP_ID] );
						
						if (appFields != null) {
							appFields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER);
							
							for (int j = 0; j < appFields.length; j++) {
								if (fields[j].length() > 0) {
									if (j == ImportManagerUtil.IDX_APP_NOTES && appFields[j].length() > 0)
										appFields[j] += fields[j];
									else
										appFields[j] = fields[j];
								}
							}
							
							for (int j = 0; j < ImportManagerUtil.GDRY_LIST_FIELDS.length; j++) {
								int listIdx = ImportManagerUtil.GDRY_LIST_FIELDS[j][0];
								int fieldIdx = ImportManagerUtil.GDRY_LIST_FIELDS[j][1];
								if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
									Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
									if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
								}
							}
						}
					}
				}
				
				String[] hpInfoLines = StarsUtils.readFile( hpInfoFile, false );
				if (hpInfoLines != null) {
					for (int i = 0; i < hpInfoLines.length; i++) {
						String[] fields = parseStarsHeatPumpInfo( hpInfoLines[i] );
						String[] appFields = null;
						
						Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
						if (appIDFields != null) 
							appFields = (String[]) appIDFields.get( fields[ImportManagerUtil.IDX_APP_ID] );
						
						if (appFields != null) {
							appFields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP);
							
							for (int j = 0; j < appFields.length; j++) {
								if (fields[j].length() > 0) {
									if (j == ImportManagerUtil.IDX_APP_NOTES && appFields[j].length() > 0)
										appFields[j] += fields[j];
									else
										appFields[j] = fields[j];
								}
							}
							
							for (int j = 0; j < ImportManagerUtil.HP_LIST_FIELDS.length; j++) {
								int listIdx = ImportManagerUtil.HP_LIST_FIELDS[j][0];
								int fieldIdx = ImportManagerUtil.HP_LIST_FIELDS[j][1];
								if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
									Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
									if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
								}
							}
						}
					}
				}
				
				String[] shInfoLines = StarsUtils.readFile( shInfoFile, false );
				if (shInfoLines != null) {
					for (int i = 0; i < shInfoLines.length; i++) {
						String[] fields = parseStarsStorageHeatInfo( shInfoLines[i] );
						String[] appFields = null;
						
						Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
						if (appIDFields != null) 
							appFields = (String[]) appIDFields.get( fields[ImportManagerUtil.IDX_APP_ID] );
						
						if (appFields != null) {
							appFields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT);
							
							for (int j = 0; j < appFields.length; j++) {
								if (fields[j].length() > 0) {
									if (j == ImportManagerUtil.IDX_APP_NOTES && appFields[j].length() > 0)
										appFields[j] += fields[j];
									else
										appFields[j] = fields[j];
								}
							}
							
							for (int j = 0; j < ImportManagerUtil.SH_LIST_FIELDS.length; j++) {
								int listIdx = ImportManagerUtil.SH_LIST_FIELDS[j][0];
								int fieldIdx = ImportManagerUtil.SH_LIST_FIELDS[j][1];
								if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
									Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
									if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
								}
							}
						}
					}
				}
				
				String[] dfInfoLines = StarsUtils.readFile( dfInfoFile, false );
				if (dfInfoLines != null) {
					for (int i = 0; i < dfInfoLines.length; i++) {
						String[] fields = parseStarsDualFuelInfo( dfInfoLines[i] );
						String[] appFields = null;
					
						Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
						if (appIDFields != null) 
							appFields = (String[]) appIDFields.get( fields[ImportManagerUtil.IDX_APP_ID] );
					
						if (appFields != null) {
							appFields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL);
						
							for (int j = 0; j < appFields.length; j++) {
								if (fields[j].length() > 0) {
									if (j == ImportManagerUtil.IDX_APP_NOTES && appFields[j].length() > 0)
										appFields[j] += fields[j];
									else
										appFields[j] = fields[j];
								}
							}
						
							for (int j = 0; j < ImportManagerUtil.DF_LIST_FIELDS.length; j++) {
								int listIdx = ImportManagerUtil.DF_LIST_FIELDS[j][0];
								int fieldIdx = ImportManagerUtil.DF_LIST_FIELDS[j][1];
								if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
									Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
									if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
								}
							}
						}
					}
				}
				
				String[] genlInfoLines = StarsUtils.readFile( genlInfoFile, false );
				if (genlInfoLines != null) {
					for (int i = 0; i < genlInfoLines.length; i++) {
						String[] fields = parseStarsGeneralInfo( genlInfoLines[i] );
						String[] appFields = null;
						
						Hashtable appIDFields = (Hashtable) acctIDAppFields.get( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
						if (appIDFields != null) 
							appFields = (String[]) appIDFields.get( fields[ImportManagerUtil.IDX_APP_ID] );
						
						if (appFields != null) {
							appFields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DEFAULT);
							
							for (int j = 0; j < appFields.length; j++) {
								if (fields[j].length() > 0) {
									if (j == ImportManagerUtil.IDX_APP_NOTES && appFields[j].length() > 0)
										appFields[j] += fields[j];
									else
										appFields[j] = fields[j];
								}
							}
						}
					}
				}
			}
			
			if (resumeFile <= RESUME_FILE_WORKORDER) {
				if (workOrderFile == null)
					throw new WebClientException("File \"workordr.txt\" not found!");
				BufferedReader fr = null;
				
				try {
					fr = new BufferedReader(new FileReader(workOrderFile));
					String line = null;
					int lineNo = 0;
					
					while ((line = fr.readLine()) != null) {
						lineNo++;
						if (line.trim().length() == 0 || line.charAt(0) == '#')
							continue;
						if (resumeFile == RESUME_FILE_WORKORDER && lineNo < resumeLine)
							continue;
						
						String[] fields = parseStarsWorkOrder( line );
						fields[ImportManagerUtil.IDX_LINE_NUM] = String.valueOf(lineNo);
						orderFieldsList.add( fields );
						
						for (int j = 0; j < ImportManagerUtil.ORDER_LIST_FIELDS.length; j++) {
							int listIdx = ImportManagerUtil.ORDER_LIST_FIELDS[j][0];
							int fieldIdx = ImportManagerUtil.ORDER_LIST_FIELDS[j][1];
							
							if (fieldIdx == ImportManagerUtil.IDX_ORDER_STATUS) {
								String text = (String) woStatus.get( fields[fieldIdx] );
								if (text == null)
									throw new WebClientException( "Work order file line #" + lineNo + ": invalid work order status " + fields[fieldIdx] );
								if (text.equalsIgnoreCase(ImportManagerUtil.WO_STATUS_CLOSED))
									fields[fieldIdx] = "Completed";
								else if (text.equalsIgnoreCase(ImportManagerUtil.WO_STATUS_SCHEDULED))
									fields[fieldIdx] = "Scheduled";
								else if (text.equalsIgnoreCase(ImportManagerUtil.WO_STATUS_OPEN) || text.equalsIgnoreCase(ImportManagerUtil.WO_STATUS_WAITING))
									fields[fieldIdx] = "Pending";
							}
							
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
				finally {
					if (fr != null) fr.close();
				}
			}
			
			if (resumeFile <= RESUME_FILE_RESINFO) {
				if (resInfoFile == null)
					throw new WebClientException("File \"resinfo.txt\" not found!");
				BufferedReader fr = null;
				
				try {
					fr = new BufferedReader(new FileReader(resInfoFile));
					String line = null;
					int lineNo = 0;
					
					while ((line = fr.readLine()) != null) {
						lineNo++;
						if (line.trim().length() == 0 || line.charAt(0) == '#')
							continue;
						if (resumeFile == RESUME_FILE_RESINFO && lineNo < resumeLine)
							continue;
						
						String[] fields = parseStarsResidenceInfo( line );
						fields[ImportManagerUtil.IDX_LINE_NUM] = String.valueOf(lineNo);
						resFieldsList.add( fields );
						
						for (int j = 0; j < ImportManagerUtil.RES_LIST_FIELDS.length; j++) {
							int listIdx = ImportManagerUtil.RES_LIST_FIELDS[j][0];
							int fieldIdx = ImportManagerUtil.RES_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], ImportManagerUtil.LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
				finally {
					if (fr != null) fr.close();
				}
			}
			
			session.setAttribute( ImportManagerUtil.CUSTOMER_FILE_PATH, importDir );
			
			preprocessedData.put( "CustomerAccount", acctFieldsList );
			preprocessedData.put( "Inventory", invFieldsList );
			preprocessedData.put( "Appliance", appFieldsList );
			preprocessedData.put( "WorkOrder", orderFieldsList );
			preprocessedData.put( "CustomerResidence", resFieldsList );
			for (int i = 0; i < ImportManagerUtil.LIST_NAMES.length; i++)
				preprocessedData.put( ImportManagerUtil.LIST_NAMES[i][0], valueIDMaps[i] );
			session.setAttribute(ImportManagerUtil.PREPROCESSED_DATA, preprocessedData);
			
			Hashtable unassignedLists = (Hashtable) session.getAttribute(ImportManagerUtil.UNASSIGNED_LISTS);
			if (unassignedLists == null) {
				unassignedLists = new Hashtable();
				session.setAttribute(ImportManagerUtil.UNASSIGNED_LISTS, unassignedLists);
			}
			
			for (int i = 0; i < ImportManagerUtil.LIST_NAMES.length; i++) {
				if (valueIDMaps[i].containsValue(ZERO) && ImportManagerUtil.LIST_NAMES[i][1].length() > 0)
					unassignedLists.put( ImportManagerUtil.LIST_NAMES[i][0], new Boolean(true) );
			}
			
			dumpSelectionLists( preprocessedData, importDir, energyCompany );
		}
		catch (Exception e) {
			if (e instanceof WebClientException)
				throw (WebClientException)e;
			CTILogger.error( e.getMessage(), e );
			throw new WebClientException("Failed to preprocess old STARS data");
		}
	}
	
	public static void assignSelectionList(HttpServletRequest req, HttpSession session, LiteStarsEnergyCompany energyCompany) throws WebClientException {
		String listName = req.getParameter("ListName");
		String[] values = req.getParameterValues("ImportValue");
		String[] enabled = req.getParameterValues("Enabled");
		String[] entryIDs = req.getParameterValues("EntryID");
		String[] entryTexts = req.getParameterValues("EntryText");
		
		Hashtable preprocessedData = (Hashtable) session.getAttribute(ImportManagerUtil.PREPROCESSED_DATA);
		TreeMap valueIDMap = (TreeMap) preprocessedData.get(listName);
		
		for (int i = 0; i < values.length; i++)
			valueIDMap.put( values[i], null );
		
		ArrayList newEntries = new ArrayList();
		if (enabled != null) {
			int entryIDIdx = 0;
			for (int i = 0; i < enabled.length; i++) {
				int idx = Integer.parseInt( enabled[i] );
				
				String listEntry = req.getParameter("ListEntry" + idx);
				if (listEntry != null && listEntry.equals("New"))
					newEntries.add( values[idx] );
				else
					valueIDMap.put( values[idx], Integer.valueOf(entryIDs[entryIDIdx++]) );
			}
		}
		
		if (entryTexts != null) {
			// Create new list entries
			try {
				if (listName.equals("ServiceCompany")) {
					for (int i = 0; i < entryTexts.length; i++) {
						LiteServiceCompany liteCompany = StarsAdminUtil.createServiceCompany( entryTexts[i], energyCompany );
						valueIDMap.put( newEntries.get(i), new Integer(liteCompany.getCompanyID()) );
					}
				}
				else if (listName.equals("Substation")) {
					for (int i = 0; i < entryTexts.length; i++) {
						LiteSubstation liteSub = StarsAdminUtil.createSubstation( entryTexts[i], 0, energyCompany );
						valueIDMap.put( newEntries.get(i), new Integer(liteSub.getSubstationID()) );
					}
				}
				else {
					YukonSelectionList cList = energyCompany.getYukonSelectionList(listName, false, false);
					if (cList == null)
						throw new WebClientException("Cannot update an inherited selection list.");
					
					ArrayList entries = cList.getYukonListEntries();
					
					Object[][] entryData = new Object[entries.size() + entryTexts.length][];
					for (int i = 0; i < entries.size(); i++) {
						YukonListEntry cEntry = (YukonListEntry) entries.get(i);
						entryData[i] = new Object[3];
						entryData[i][0] = new Integer( cEntry.getEntryID() );
						entryData[i][1] = cEntry.getEntryText();
						entryData[i][2] = new Integer( cEntry.getYukonDefID() );
					}
					
					for (int i = 0; i < entryTexts.length; i++) {
						entryData[entries.size()+i] = new Object[3];
						entryData[entries.size()+i][0] = ZERO;
						entryData[entries.size()+i][1] = entryTexts[i];
						entryData[entries.size()+i][2] = ZERO;
					}
					
					StarsAdminUtil.updateYukonListEntries( cList, entryData, energyCompany );
					energyCompany.updateStarsCustomerSelectionLists();
					
					for (int i = 0; i < newEntries.size(); i++) {
						for (int j = 0; j < cList.getYukonListEntries().size(); j++) {
							YukonListEntry cEntry = (YukonListEntry) cList.getYukonListEntries().get(j);
							if (cEntry.getEntryText().equals(entryTexts[i]) && cEntry.getYukonDefID() == 0) {
								valueIDMap.put( newEntries.get(i), new Integer(cEntry.getEntryID()) );
								break;
							}
						}
					}
				}
			}
			catch (Exception e) {
				if (e instanceof WebClientException)
					throw (WebClientException)e;
				CTILogger.error( e.getMessage(), e );
				throw new WebClientException("Failed to assign import values to selection list");
			}
		}
		
		// Change the unassigned flag to false
		Hashtable unassignedLists = (Hashtable) session.getAttribute(ImportManagerUtil.UNASSIGNED_LISTS);
		unassignedLists.put(listName, new Boolean(false));
	}
	
	public static Hashtable postProcessStarsData(Hashtable preprocessedData, File importDir, LiteStarsEnergyCompany energyCompany) {
		dumpSelectionLists( preprocessedData, importDir, energyCompany );
		
		ArrayList acctFieldsList = (ArrayList) preprocessedData.get("CustomerAccount");
		ArrayList invFieldsList = (ArrayList) preprocessedData.get("Inventory");
		ArrayList appFieldsList = (ArrayList) preprocessedData.get("Appliance");
		ArrayList orderFieldsList = (ArrayList) preprocessedData.get("WorkOrder");
		ArrayList resFieldsList = (ArrayList) preprocessedData.get("CustomerResidence");
		
		TreeMap[] valueIDMaps = new TreeMap[ ImportManagerUtil.LIST_NAMES.length ];
		for (int i = 0; i < ImportManagerUtil.LIST_NAMES.length; i++)
			valueIDMaps[i] = (TreeMap) preprocessedData.get( ImportManagerUtil.LIST_NAMES[i][0] );
		
		// Replace import values with ids assigned to them
		for (int i = 0; i < acctFieldsList.size(); i++) {
			String[] fields = (String[]) acctFieldsList.get(i);
			
			for (int j = 0; j < ImportManagerUtil.SERVINFO_LIST_FIELDS.length; j++) {
				int listIdx = ImportManagerUtil.SERVINFO_LIST_FIELDS[j][0];
				int fieldIdx = ImportManagerUtil.SERVINFO_LIST_FIELDS[j][1];
				Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
				fields[fieldIdx] = (id != null)? id.toString() : "0";
			}
		}
		
		for (int i = 0; i < invFieldsList.size(); i++) {
			String[] fields = (String[]) invFieldsList.get(i);
			
			for (int j = 0; j < ImportManagerUtil.INV_LIST_FIELDS.length; j++) {
				int listIdx = ImportManagerUtil.INV_LIST_FIELDS[j][0];
				int fieldIdx = ImportManagerUtil.INV_LIST_FIELDS[j][1];
				Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
				fields[fieldIdx] = (id != null)? id.toString() : "0";
			}
			
			int categoryID = InventoryUtils.getInventoryCategoryID( Integer.parseInt(fields[ImportManagerUtil.IDX_DEVICE_TYPE]), energyCompany );
			if (InventoryUtils.isLMHardware( categoryID )) {
				for (int j = 0; j < ImportManagerUtil.RECV_LIST_FIELDS.length; j++) {
					int listIdx = ImportManagerUtil.RECV_LIST_FIELDS[j][0];
					int fieldIdx = ImportManagerUtil.RECV_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
		}
		
		for (int i = 0; i < appFieldsList.size(); i++) {
			String[] fields = (String[]) appFieldsList.get(i);
			
			for (int j = 0; j < ImportManagerUtil.APP_LIST_FIELDS.length; j++) {
				int listIdx = ImportManagerUtil.APP_LIST_FIELDS[j][0];
				int fieldIdx = ImportManagerUtil.APP_LIST_FIELDS[j][1];
				Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
				fields[fieldIdx] = (id != null)? id.toString() : "0";
			}
			
			if (fields[ImportManagerUtil.IDX_APP_CAT_DEF_ID].equals("")) continue;
			int catDefID = Integer.parseInt( fields[ImportManagerUtil.IDX_APP_CAT_DEF_ID] );
			
			if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER) {
				for (int j = 0; j < ImportManagerUtil.AC_LIST_FIELDS.length; j++) {
					int listIdx = ImportManagerUtil.AC_LIST_FIELDS[j][0];
					int fieldIdx = ImportManagerUtil.AC_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER) {
				for (int j = 0; j < ImportManagerUtil.WH_LIST_FIELDS.length; j++) {
					int listIdx = ImportManagerUtil.WH_LIST_FIELDS[j][0];
					int fieldIdx = ImportManagerUtil.WH_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR) {
				for (int j = 0; j < ImportManagerUtil.GEN_LIST_FIELDS.length; j++) {
					int listIdx = ImportManagerUtil.GEN_LIST_FIELDS[j][0];
					int fieldIdx = ImportManagerUtil.GEN_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION) {
				for (int j = 0; j < ImportManagerUtil.IRR_LIST_FIELDS.length; j++) {
					int listIdx = ImportManagerUtil.IRR_LIST_FIELDS[j][0];
					int fieldIdx = ImportManagerUtil.IRR_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER) {
				for (int j = 0; j < ImportManagerUtil.GDRY_LIST_FIELDS.length; j++) {
					int listIdx = ImportManagerUtil.GDRY_LIST_FIELDS[j][0];
					int fieldIdx = ImportManagerUtil.GDRY_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP) {
				for (int j = 0; j < ImportManagerUtil.HP_LIST_FIELDS.length; j++) {
					int listIdx = ImportManagerUtil.HP_LIST_FIELDS[j][0];
					int fieldIdx = ImportManagerUtil.HP_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT) {
				for (int j = 0; j < ImportManagerUtil.SH_LIST_FIELDS.length; j++) {
					int listIdx = ImportManagerUtil.SH_LIST_FIELDS[j][0];
					int fieldIdx = ImportManagerUtil.SH_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL) {
				for (int j = 0; j < ImportManagerUtil.DF_LIST_FIELDS.length; j++) {
					int listIdx = ImportManagerUtil.DF_LIST_FIELDS[j][0];
					int fieldIdx = ImportManagerUtil.DF_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
		}
		
		for (int i = 0; i < orderFieldsList.size(); i++) {
			String[] fields = (String[]) orderFieldsList.get(i);
			
			for (int j = 0; j < ImportManagerUtil.ORDER_LIST_FIELDS.length; j++) {
				int listIdx = ImportManagerUtil.ORDER_LIST_FIELDS[j][0];
				int fieldIdx = ImportManagerUtil.ORDER_LIST_FIELDS[j][1];
				Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
				fields[fieldIdx] = (id != null)? id.toString() : "0";
			}
		}
		
		for (int i = 0; i < resFieldsList.size(); i++) {
			String[] fields = (String[]) resFieldsList.get(i);
			
			for (int j = 0; j < ImportManagerUtil.RES_LIST_FIELDS.length; j++) {
				int listIdx = ImportManagerUtil.RES_LIST_FIELDS[j][0];
				int fieldIdx = ImportManagerUtil.RES_LIST_FIELDS[j][1];
				Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
				fields[fieldIdx] = (id != null)? id.toString() : "0";
			}
		}
		
		return preprocessedData;
	}

}
