/*
 * Created on Jan 30, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.util.ImportProblem;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.ImportManagerUtil;
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

	private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );
	private static final int ERROR_NUM_LIMIT = 20;
	private static final int WARNING_NUM_LIMIT = 100;
	
	StarsYukonUser user = null;
	Hashtable preprocessedData = null;
	
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
	
	long startTime = 0;
	long stopTime = 0;
	File logFile = null;
	
	public ImportStarsDataTask(StarsYukonUser user, Hashtable preprocessedData) {
		this.user = user;
		this.preprocessedData = preprocessedData;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if (preprocessedData != null) {
			String msg = null;
			
			if (status == STATUS_FINISHED) {
				msg = "Old STARS data imported successfully";
				if (logFile != null)
					msg += LINE_SEPARATOR + "For detailed information, view log file '" + logFile.getPath() + "'";
			}
			else if (status == STATUS_RUNNING) {
				msg = "";
				if (acctFieldsCnt > 0) {
					if (numAcctAdded == acctFieldsCnt)
						msg += numAcctAdded + " customer accounts imported successfully";
					else
						msg += numAcctAdded + " of " + acctFieldsCnt + " customer accounts imported";
					msg += LINE_SEPARATOR;
				}
				if (invFieldsCnt > 0) {
					if (numInvAdded + numNoDeviceName + numDeviceNameNotFound  == invFieldsCnt)
						msg += numInvAdded + " hardwares imported successfully";
					else
						msg += numInvAdded + " of " + invFieldsCnt + " hardwares imported";
					msg += " (" + numRecvrAdded + " receivers, " + numMeterAdded + " meters)" + LINE_SEPARATOR;
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
							LINE_SEPARATOR;
				}
				if (orderFieldsCnt > 0) {
					if (numOrderAdded == orderFieldsCnt)
						msg += numOrderAdded + " work orders imported successfully";
					else
						msg += numOrderAdded + " of " + orderFieldsCnt + " work orders imported";
					msg += LINE_SEPARATOR;
				}
				if (resFieldsCnt > 0) {
					if (numResAdded == resFieldsCnt)
						msg += numResAdded + " residence info imported successfully";
					else
						msg += numResAdded + " of " + resFieldsCnt + " residence information imported";
				}
			}
			
			return msg;
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (preprocessedData == null) {
			status = STATUS_ERROR;
			errorMsg = "Preprocessed data cannot be null";
			return;
		}
		
		status = STATUS_RUNNING;
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		acctFieldsList = (ArrayList) preprocessedData.get("CustomerAccount");
		invFieldsList = (ArrayList) preprocessedData.get("Inventory");
		appFieldsList = (ArrayList) preprocessedData.get("Appliance");
		orderFieldsList = (ArrayList) preprocessedData.get("WorkOrder");
		resFieldsList = (ArrayList) preprocessedData.get("CustomerResidence");
		
		acctFieldsCnt = acctFieldsList.size();
		invFieldsCnt = invFieldsList.size();
		appFieldsCnt = appFieldsList.size();
		orderFieldsCnt = orderFieldsList.size();
		resFieldsCnt = resFieldsList.size();
		
		// Map of import_account_id(Integer) to db_account_id(Integer) or LiteStarsCustAccountInformation object
		Hashtable acctIDMap = (Hashtable) preprocessedData.get( ImportManagerUtil.CUSTOMER_ACCOUNT_MAP );
		if (acctIDMap == null) {
			acctIDMap = new Hashtable();
			preprocessedData.put( ImportManagerUtil.CUSTOMER_ACCOUNT_MAP, acctIDMap );
		}
		
		// Map of import_inv_id(Integer) to db_app_ids (int[]: {relay1_app_id, relay2_app_id, relay3_app_id})
		Hashtable appIDMap = (Hashtable) preprocessedData.get( ImportManagerUtil.HW_CONFIG_APP_MAP );
		if (appIDMap == null) {
			appIDMap = new Hashtable();
			preprocessedData.put( ImportManagerUtil.HW_CONFIG_APP_MAP, appIDMap );
		}
		
		// Directory where the mapping and log files will be written into
		String path = (String) preprocessedData.get( ImportManagerUtil.CUSTOMER_FILE_PATH );
		
		String position = null;
		java.io.PrintWriter fw = null;
		
		ArrayList stackTrace = null;
		ArrayList errors = new ArrayList();
		ArrayList warnings = new ArrayList();
		ArrayList warnings2 = new ArrayList();
		
		startTime = System.currentTimeMillis();
		
		try {
			File custMapFile = new File(path, "customer.map");
			fw = new java.io.PrintWriter(new java.io.FileWriter(custMapFile, true), true);	// Append to file and auto flush
			boolean first = true;
			
			Iterator it = acctFieldsList.listIterator();
			while (it.hasNext()) {
				String[] fields = (String[]) it.next();
				position = "customer file line #" + fields[ImportManagerUtil.IDX_LINE_NUM];
				
				try {
					// To save database lookup time, only check for constraint for
					// the first import entry, then assume there won't be any problem
					ImportProblem problem = new ImportProblem();
					LiteStarsCustAccountInformation liteAcctInfo = ImportManagerUtil.newCustomerAccount(fields, user, energyCompany, first, problem);
					if (problem.getProblem() != null) {
						warnings.add( "WARNING at " + position + ": " + problem.getProblem() );
						if (warnings.size() > WARNING_NUM_LIMIT)
							throw new WebClientException( ImportProblem.TOO_MANY_WARNINGS );
					}
					
					first = false;
					
					acctIDMap.put( Integer.valueOf(fields[ImportManagerUtil.IDX_ACCOUNT_ID]), liteAcctInfo );
					fw.println(fields[ImportManagerUtil.IDX_ACCOUNT_ID] + "," + liteAcctInfo.getAccountID());
					
					numAcctAdded++;
					it.remove();	// Try to free up memory as we go
				}
				catch (Exception e) {
					if (!(e instanceof WebClientException) || e.getMessage().equals( ImportProblem.TOO_MANY_WARNINGS ))
						throw e;
					errors.add( "ERROR at " + position + ": " + e.getMessage() );
					if (errors.size() > ERROR_NUM_LIMIT)
						throw new WebClientException( ImportProblem.TOO_MANY_ERRORS );
				}
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
			}
			
			fw.close();
			fw = null;
			position = null;
			
			File hwConfigMapFile = new File(path, "hwconfig.map");
			fw = new java.io.PrintWriter(new java.io.FileWriter(hwConfigMapFile, true), true);
			
			Hashtable devCarrSettings = null;
			first = true;
			
			it = invFieldsList.listIterator();
			while (it.hasNext()) {
				String[] fields = (String[]) it.next();
				position = "inventory file line #" + fields[ImportManagerUtil.IDX_LINE_NUM];
				
				try {
					Integer acctID = Integer.valueOf( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					LiteStarsCustAccountInformation liteAcctInfo = null;
					
					if (acctID.intValue() > 0) {	
						Object obj = acctIDMap.get(acctID);
						if (obj != null) {
							if (obj instanceof LiteStarsCustAccountInformation) {
								liteAcctInfo = (LiteStarsCustAccountInformation) obj;
							}
							else if (obj instanceof Integer) {
								// This is loaded from file customer.map
								liteAcctInfo = energyCompany.getCustAccountInformation( ((Integer)obj).intValue(), true );
								if (liteAcctInfo != null)
									acctIDMap.put(acctID, liteAcctInfo);
							}
						}
						
						if (liteAcctInfo == null)
							throw new WebClientException("Cannot find customer account with id=" + acctID.intValue());
					}
					
					ImportProblem problem = new ImportProblem();
					LiteInventoryBase liteInv = ImportManagerUtil.insertLMHardware( fields, liteAcctInfo, energyCompany, first, problem );
					if (problem.getProblem() != null) {
						warnings.add( "WARNING at " + position + ": " + problem.getProblem() );
						if (warnings.size() > WARNING_NUM_LIMIT)
							throw new WebClientException( ImportProblem.TOO_MANY_WARNINGS );
					}
					
					first = false;
					it.remove();
					
					for (int i = 0; i < 3; i++) {
						if (fields[ImportManagerUtil.IDX_R1_STATUS + i].equals("1"))
							warnings2.add("Receiver (import_inv_id=" + fields[ImportManagerUtil.IDX_INV_ID] + ",db_inv_id=" + liteInv.getInventoryID() + ",serial_no=" + fields[ImportManagerUtil.IDX_SERIAL_NO] + ") relay " + (i+1) + " is out of service");
						else if (fields[ImportManagerUtil.IDX_R1_STATUS + i].equals("2"))
							warnings2.add("Receiver (import_inv_id=" + fields[ImportManagerUtil.IDX_INV_ID] + ",db_inv_id=" + liteInv.getInventoryID() + ",serial_no=" + fields[ImportManagerUtil.IDX_SERIAL_NO] + ") relay " + (i+1) + " was out before switch placed out");
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
							
							appIDMap.put( Integer.valueOf(fields[ImportManagerUtil.IDX_INV_ID]), appIDs );
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
								warnings2.add("Meter (import_inv_id=" + fields[ImportManagerUtil.IDX_INV_ID] + ",import_acct_id=" + fields[ImportManagerUtil.IDX_ACCOUNT_ID] + ",dev_name=" + fields[ImportManagerUtil.IDX_DEVICE_NAME] + ",serial_no=" + fields[ImportManagerUtil.IDX_SERIAL_NO]
										+ ") matches Yukon device (dev_id=" + deviceID + ",dev_name=" + litePao.getPaoName() + ") by serial number");
							}
						}
					}
					else {
						errors.add( "ERROR at " + position + ": " + e.getMessage() );
						if (errors.size() > ERROR_NUM_LIMIT)
							throw new WebClientException( ImportProblem.TOO_MANY_ERRORS );
					}
				}
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
			}
			
			fw.close();
			fw = null;
			position = null;
			
			it = appFieldsList.listIterator();
			while (it.hasNext()) {
				String[] fields = (String[]) it.next();
				position = "loadinfo file line #" + fields[ImportManagerUtil.IDX_LINE_NUM];
				
				try {
					Integer acctID = Integer.valueOf( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					LiteStarsCustAccountInformation liteAcctInfo = null;
					
					Object obj = acctIDMap.get(acctID);
					if (obj != null) {
						if (obj instanceof LiteStarsCustAccountInformation) {
							liteAcctInfo = (LiteStarsCustAccountInformation) obj;
						}
						else if (obj instanceof Integer) {
							liteAcctInfo = energyCompany.getCustAccountInformation( ((Integer)obj).intValue(), true );
							if (liteAcctInfo != null)
								acctIDMap.put(acctID, liteAcctInfo);
						}
					}
					
					if (liteAcctInfo == null)
						throw new WebClientException("Cannot find customer account with id = " + acctID);
					
					Integer invID = Integer.valueOf( fields[ImportManagerUtil.IDX_INV_ID] );
					int relayNum = Integer.parseInt( fields[ImportManagerUtil.IDX_RELAY_NUM] );
					int appID = 0;
					
					if (relayNum > 0) {
						int[] appIDs = (int[]) appIDMap.get( invID );
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
					errors.add( "ERROR at " + position + ": " + e.getMessage() );
					if (errors.size() > ERROR_NUM_LIMIT)
						throw new WebClientException( ImportProblem.TOO_MANY_ERRORS );
				}
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
			}
			
			position = null;
			first = true;
			
			it = orderFieldsList.listIterator();
			while (it.hasNext()) {
				String[] fields = (String[]) it.next();
				position = "workorder file line #" + fields[ImportManagerUtil.IDX_LINE_NUM];
				
				try {
					Integer acctID = Integer.valueOf( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					LiteStarsCustAccountInformation liteAcctInfo = null;
					
					if (acctID.intValue() > 0) {
						Object obj = acctIDMap.get(acctID);
						if (obj instanceof LiteStarsCustAccountInformation) {
							liteAcctInfo = (LiteStarsCustAccountInformation) obj;
						}
						else if (obj instanceof Integer) {
							// This is loaded from file customer.map
							liteAcctInfo = energyCompany.getCustAccountInformation( ((Integer)obj).intValue(), true );
							if (liteAcctInfo != null)
								acctIDMap.put(acctID, liteAcctInfo);
						}
						
						if (liteAcctInfo == null)
							throw new WebClientException("Cannot find customer account with id=" + acctID.intValue());
					}
					
					ImportProblem problem = new ImportProblem();
					ImportManagerUtil.newServiceRequest(fields, liteAcctInfo, energyCompany, first, problem);
					if (problem.getProblem() != null) {
						warnings.add( "WARNING at " + position + ": " + problem.getProblem() );
						if (warnings.size() > WARNING_NUM_LIMIT)
							throw new WebClientException( ImportProblem.TOO_MANY_WARNINGS );
					}
					
					first = false;
					numOrderAdded++;
					it.remove();
				}
				catch (Exception e) {
					if (!(e instanceof WebClientException) || e.getMessage().equals( ImportProblem.TOO_MANY_WARNINGS ))
						throw e;
					errors.add( "ERROR at " + position + ": " + e.getMessage() );
					if (errors.size() > ERROR_NUM_LIMIT)
						throw new WebClientException( ImportProblem.TOO_MANY_ERRORS );
				}
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
			}
			
			position = null;
			
			it = resFieldsList.listIterator();
			while (it.hasNext()) {
				String[] fields = (String[]) it.next();
				position = "resinfo file line #" + fields[ImportManagerUtil.IDX_LINE_NUM];
				
				try {
					Integer acctID = Integer.valueOf( fields[ImportManagerUtil.IDX_ACCOUNT_ID] );
					LiteStarsCustAccountInformation liteAcctInfo = null;
					
					if (acctID.intValue() > 0) {
						Object obj = acctIDMap.get(acctID);
						if (obj instanceof LiteStarsCustAccountInformation) {
							liteAcctInfo = (LiteStarsCustAccountInformation) obj;
						}
						else if (obj instanceof Integer) {
							// This is loaded from file customer.map
							liteAcctInfo = energyCompany.getCustAccountInformation( ((Integer)obj).intValue(), true );
							if (liteAcctInfo != null)
								acctIDMap.put(acctID, liteAcctInfo);
						}
						
						if (liteAcctInfo == null)
							throw new WebClientException("Cannot find customer account with id=" + acctID.intValue());
					}
					
					ImportManagerUtil.newResidenceInfo( fields, liteAcctInfo );
					
					numResAdded++;
					it.remove();
				}
				catch (Exception e) {
					if (!(e instanceof WebClientException) || e.getMessage().equals( ImportProblem.TOO_MANY_WARNINGS ))
						throw e;
					errors.add( "ERROR at " + position + ": " + e.getMessage() );
					if (errors.size() > ERROR_NUM_LIMIT)
						throw new WebClientException( ImportProblem.TOO_MANY_ERRORS );
				}
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
			}
			
			status = STATUS_FINISHED;
		}
		catch (OutOfMemoryError me) {
			// We must catch this error to make sure the log file is written
			status = STATUS_ERROR;
			errorMsg = "Operation caused out of memory error";
			if (position != null)
				errorMsg += " when processing " + position;
		}
		catch (Exception e) {
			if (status == STATUS_CANCELED) {
				errorMsg = "Operation is canceled by user";
				if (position != null)
					errorMsg += " after " + position + " is processed";
			}
			else {
				status = STATUS_ERROR;
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
				
				if (e instanceof WebClientException) {
					errorMsg = e.getMessage();
				}
				else {
					if (position != null)
						errorMsg = "Error at " + position;
					else
						errorMsg = "Failed to import old STARS data";
					
					stackTrace = new ArrayList();
					stackTrace.add( "\t" + e.toString() );
					for (int i = 0; i < e.getStackTrace().length; i++)
						stackTrace.add( "\tat " + e.getStackTrace()[i].toString() );
				}
			}
		}
		finally {
			if (fw != null) fw.close();
		}
		
		stopTime = System.currentTimeMillis();
		
		int minutes = (int) ((stopTime - startTime) * 0.001 / 60 + 0.5);
		int hourTaken = minutes / 60;
		int minTaken = minutes % 60;
		String timeTaken = "";
		if (hourTaken > 1)
			timeTaken += hourTaken + " hours ";
		else if (hourTaken == 1)
			timeTaken += hourTaken + " hour ";
		if (minTaken > 1)
			timeTaken += minTaken + " minutes";
		else
			timeTaken += minTaken + " minute";
		
		ArrayList logMsg = new ArrayList();
		logMsg.add("Start Time: " + StarsUtils.formatDate( new Date(startTime), energyCompany.getDefaultTimeZone() ));
		logMsg.add("Time Taken: " + timeTaken);
		logMsg.add("");
		
		if (errorMsg != null) {
			logMsg.add(errorMsg);
			if (stackTrace != null) logMsg.addAll(stackTrace);
			logMsg.add("");
		}
		
		if (acctFieldsCnt > 0) {
			if (numAcctAdded == acctFieldsCnt)
				logMsg.add(numAcctAdded + " customer accounts imported successfully");
			else
				logMsg.add(numAcctAdded + " of " + acctFieldsCnt + " customer accounts imported");
		}
		if (invFieldsCnt > 0) {
			String msg = null;
			if (numInvAdded + numNoDeviceName + numDeviceNameNotFound  == invFieldsCnt)
				msg = numInvAdded + " hardwares imported successfully";
			else
				msg = numInvAdded + " of " + invFieldsCnt + " hardwares imported";
			msg += " (" + numRecvrAdded + " receivers, " + numMeterAdded + " meters)";
			logMsg.add(msg);
			
			if (numNoDeviceName > 0)
				logMsg.add(numNoDeviceName + " hardwares ignored because device name is empty");
			if (numDeviceNameNotFound > 0)
				logMsg.add(numDeviceNameNotFound + " hardwares ignored because device name not found in Yukon");
		}
		if (appFieldsCnt > 0) {
			String msg = null;
			if (numAppImported == appFieldsCnt)
				msg = numAppImported + " appliances imported successfully";
			else
				msg = numAppImported + " of " + appFieldsCnt + " appliances imported";
			msg += " (" + numACImported + " ac," +
					numWHImported + " wh," +
					numGenImported + " gen," +
					numIrrImported + " irr," +
					numGDryImported + " gdry," +
					numHPImported + " hp," +
					numSHImported + " sh," +
					numDFImported + " df," +
					numGenlImported + " genl)";
			logMsg.add(msg);
		}
		if (orderFieldsCnt > 0) {
			if (numOrderAdded == orderFieldsCnt)
				logMsg.add(numOrderAdded + " work orders imported successfully");
			else
				logMsg.add(numOrderAdded + " of " + orderFieldsCnt + " work orders imported");
		}
		if (resFieldsCnt > 0) {
			if (numResAdded == resFieldsCnt)
				logMsg.add(numResAdded + " residence info imported successfully");
			else
				logMsg.add(numResAdded + " of " + resFieldsCnt + " residence information imported");
		}
		
		if (errors.size() > 0) {
			logMsg.add("");
			logMsg.addAll( errors );
		}
		
		if (warnings.size() > 0) {
			logMsg.add("");
			logMsg.addAll( warnings );
		}
		
		if (warnings2.size() > 0) {
			logMsg.add("");
			logMsg.addAll( warnings2 );
		}
		
		Date logDate = new Date();
		String logFileName = "import_" + StarsUtils.starsDateFormat.format(logDate) +
				"_" + StarsUtils.starsTimeFormat.format(logDate) + ".log";
		logFile = new File(path, logFileName);
		
		try {
			String[] log = new String[ logMsg.size() ];
			logMsg.toArray( log );
			StarsUtils.writeFile( logFile, log );
		}
		catch (IOException e) {
			logFile = null;
			
			if (errorMsg != null)
				errorMsg += LINE_SEPARATOR;
			else
				errorMsg = "";
			errorMsg += "Failed to write log file";
		}
		
		if ((status == STATUS_ERROR || status == STATUS_CANCELED) && logFile != null)
			errorMsg += LINE_SEPARATOR + "For detailed information, view log file '" + logFile.getPath() + "'";
	}

}
