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
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMProgram;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.util.ImportProblem;
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
public class ImportStarsDataTask implements TimeConsumingTask {

	private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );
	
	int status = STATUS_NOT_INIT;
	boolean isCanceled = false;
	String errorMsg = null;
	
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
	int numNoLoadDescription = 0;
	
	long startTime = 0;
	long stopTime = 0;
	File logFile = null;
	
	public ImportStarsDataTask(StarsYukonUser user, Hashtable preprocessedData) {
		this.user = user;
		this.preprocessedData = preprocessedData;
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
		isCanceled = true;
		status = STATUS_CANCELING;
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
					if (numInvAdded  == invFieldsCnt)
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
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getErrorMsg()
	 */
	public String getErrorMsg() {
		return errorMsg;
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
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
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
		Hashtable acctIDMap = (Hashtable) preprocessedData.get( "CustomerAccountMap" );
		if (acctIDMap == null) {
			acctIDMap = new Hashtable();
			preprocessedData.put( "CustomerAccountMap", acctIDMap );
		}
		
		// Map of import_inv_id(Integer) to db_app_ids (int[]: {relay1_app_id, relay2_app_id, relay3_app_id})
		Hashtable appIDMap = (Hashtable) preprocessedData.get( "HwConfigAppMap" );
		if (appIDMap == null) {
			appIDMap = new Hashtable();
			preprocessedData.put( "HwConfigAppMap", appIDMap );
		}
		
		// Directory where the mapping and log files will be written into
		String path = (String) preprocessedData.get( "CustomerFilePath" );
		
		String position = null;
		ArrayList logMsg = new ArrayList();
		
		java.sql.Connection conn = null;
		java.io.PrintWriter fw = null;
		
		startTime = System.currentTimeMillis();
		
		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			File custMapFile = new File(path, "customer.map");
			fw = new java.io.PrintWriter(new java.io.FileWriter(custMapFile, true), true);	// Append to file and auto flush
			boolean first = true;
			
			Iterator it = acctFieldsList.listIterator();
			while (it.hasNext()) {
				String[] fields = (String[]) it.next();
				position = "customer file line #" + fields[ImportManager.IDX_LINE_NUM];
				
				// To save database lookup time, only check for constraint for
				// the first import entry, then assume there won't be any problem
				LiteStarsCustAccountInformation liteAcctInfo = ImportManager.newCustomerAccount(fields, user, energyCompany, first);
				first = false;
				
				acctIDMap.put( Integer.valueOf(fields[ImportManager.IDX_ACCOUNT_ID]), liteAcctInfo );
				fw.println(fields[ImportManager.IDX_ACCOUNT_ID] + "," + liteAcctInfo.getAccountID());
				
				numAcctAdded++;
				it.remove();	// Try to free up memory as we go
				
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
			first = true;
			
			it = invFieldsList.listIterator();
			while (it.hasNext()) {
				String[] fields = (String[]) it.next();
				position = "inventory file line #" + fields[ImportManager.IDX_LINE_NUM];
				
				Integer acctID = Integer.valueOf( fields[ImportManager.IDX_ACCOUNT_ID] );
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
				
				LiteInventoryBase liteInv = null;
				try {
					liteInv = ImportManager.insertLMHardware(fields, liteAcctInfo, energyCompany, conn, first);
				}
				catch (ImportProblem ipe) {
					if (ipe.getMessage().equals( ImportProblem.NO_DEVICE_NAME )) {
						numNoDeviceName++;
					}
					else if (ipe.getMessage().equals( ImportProblem.DEVICE_NAME_NOT_FOUND )) {
						numDeviceNameNotFound++;
						
						if (fields[ImportManager.IDX_SERIAL_NO].length() > 0) {
							String sql = "SELECT DeviceID FROM DeviceCarrierSettings WHERE Address = ?";
							java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
							pstmt.setString(1, fields[ImportManager.IDX_SERIAL_NO]);
							java.sql.ResultSet rset = pstmt.executeQuery();
							
							if (rset.next()) {
								int deviceID = rset.getInt(1);
								LiteYukonPAObject litePao = PAOFuncs.getLiteYukonPAO( deviceID );
								logMsg.add("Meter (import_inv_id=" + fields[ImportManager.IDX_INV_ID] + ",import_acct_id=" + fields[ImportManager.IDX_ACCOUNT_ID] + ",dev_name=" + fields[ImportManager.IDX_DEVICE_NAME] + ",serial_no=" + fields[ImportManager.IDX_SERIAL_NO] + ") "
										+ "matches Yukon device (dev_id=" + deviceID + ",dev_name=" + litePao.getPaoName() + ") by serial number");
							}
							else {
								logMsg.add("Meter (import_inv_id=" + fields[ImportManager.IDX_INV_ID] + ",import_acct_id=" + fields[ImportManager.IDX_ACCOUNT_ID] + ",dev_name=" + fields[ImportManager.IDX_DEVICE_NAME] + ",serial_no=" + fields[ImportManager.IDX_SERIAL_NO] + ") doesn't match any device in Yukon");
							}
						}
					}
					else {
						logMsg.add("Error at " + position + ": " + ipe.getMessage());
					}
					
					continue;
				}
				finally {
					first = false;
					it.remove();
				}
				
				for (int i = 0; i < 3; i++) {
					if (fields[ImportManager.IDX_R1_STATUS + i].equals("1"))
						logMsg.add("Receiver (import_inv_id=" + fields[ImportManager.IDX_INV_ID] + ",db_inv_id=" + liteInv.getInventoryID() + ",serial_no=" + fields[ImportManager.IDX_SERIAL_NO] + ") relay " + (i+1) + " is out of service");
					else if (fields[ImportManager.IDX_R1_STATUS + i].equals("2"))
						logMsg.add("Receiver (import_inv_id=" + fields[ImportManager.IDX_INV_ID] + ",db_inv_id=" + liteInv.getInventoryID() + ",serial_no=" + fields[ImportManager.IDX_SERIAL_NO] + ") relay " + (i+1) + " was out before switch placed out");
				}
				
				if (liteAcctInfo != null) {
					// If load groups for relays are specified, automatically add appliances through program enrollment
					ArrayList appCats = energyCompany.getAllApplianceCategories();
					ArrayList programs = new ArrayList();
					int[] progIDs = new int[3];	// Save the program ID on each relay here so we can map them to appliance id later
					
					for (int i = 0; i < 3; i++) {
						if (fields[ImportManager.IDX_R1_GROUP + i].equals("") || fields[ImportManager.IDX_R1_GROUP + i].equals("0"))
							continue;
						
						int groupID = Integer.parseInt( fields[ImportManager.IDX_R1_GROUP + i] );
						int progID = 0;
						int appCatID = 0;
						
						for (int j = 0; j < appCats.size(); j++) {
							LiteApplianceCategory liteAppCat = (LiteApplianceCategory) appCats.get(j);
							for (int k = 0; k < liteAppCat.getPublishedPrograms().length; k++) {
								LiteLMProgram liteProg = liteAppCat.getPublishedPrograms()[k];
								for (int l = 0; l < liteProg.getGroupIDs().length; l++) {
									if (liteProg.getGroupIDs()[l] == groupID) {
										progID = liteProg.getProgramID();
										appCatID = liteAppCat.getApplianceCategoryID();
										progIDs[i] = progID;
										break;
									}
								}
								if (progID > 0) break;
							}
							if (progID > 0) break;
						}
						
						if (progID == 0)
							throw new WebClientException( "Cannot find LM program for load group id = " + groupID );
						
						programs.add( new int[] {progID, appCatID, groupID} );
					}
					
					if (programs.size() > 0) {
						ImportManager.programSignUp( programs, liteAcctInfo, new Integer(liteInv.getInventoryID()), energyCompany, conn );
						
						int[] appIDs = new int[3];
						for (int i = 0; i < 3; i++) {
							if (progIDs[i] == 0) continue;
							
							for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
								LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
								if (liteApp.getLmProgramID() == progIDs[i]) {
									appIDs[i] = liteApp.getApplianceID();
									break;
								}
							}
							
							if (appIDs[i] == 0)	// shouldn't happen
								throw new WebClientException("Cannot find appliance with RelayNum = " + (i+1));
						}
						
						appIDMap.put( Integer.valueOf(fields[ImportManager.IDX_INV_ID]), appIDs );
						fw.println(fields[ImportManager.IDX_INV_ID] + "," + appIDs[0] + "," + appIDs[1] + "," + appIDs[2]);
						
						numAppAdded += programs.size();
					}
				}
				
				numInvAdded++;
				if (liteInv instanceof LiteStarsLMHardware)
					numRecvrAdded++;
				else
					numMeterAdded++;
				
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
				position = "loadinfo file line #" + fields[ImportManager.IDX_LINE_NUM];
				
				if (fields[ImportManager.IDX_APP_DESC].equals("")) {
					numNoLoadDescription++;
					continue;
				}
				
				Integer acctID = Integer.valueOf( fields[ImportManager.IDX_ACCOUNT_ID] );
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
				
				Integer invID = Integer.valueOf( fields[ImportManager.IDX_INV_ID] );
				int relayNum = Integer.parseInt( fields[ImportManager.IDX_RELAY_NUM] );
				int appID = 0;
				
				if (relayNum > 0) {
					int[] appIDs = (int[]) appIDMap.get( invID );
					if (appIDs != null) appID = appIDs[relayNum - 1];
				}
				
				if (appID > 0 && fields[ImportManager.IDX_AVAIL_FOR_CTRL].equalsIgnoreCase("Y"))
					ImportManager.updateAppliance( fields, appID, liteAcctInfo, energyCompany );
				else
					ImportManager.newAppliance( fields, liteAcctInfo, energyCompany );
				
				numAppImported++;
				it.remove();
				
				if (fields[ImportManager.IDX_APP_CAT_DEF_ID].equals("")) continue;
				int catDefID = Integer.parseInt( fields[ImportManager.IDX_APP_CAT_DEF_ID] );
				
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
				position = "workorder file line #" + fields[ImportManager.IDX_LINE_NUM];
				
				Integer acctID = Integer.valueOf( fields[ImportManager.IDX_ACCOUNT_ID] );
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
				
				ImportManager.newServiceRequest(fields, liteAcctInfo, energyCompany, first);
				first = false;
				
				numOrderAdded++;
				it.remove();
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
			}
			
			position = null;
			
			it = resFieldsList.listIterator();
			while (it.hasNext()) {
				String[] fields = (String[]) it.next();
				position = "resinfo file line #" + fields[ImportManager.IDX_LINE_NUM];
				
				Integer acctID = Integer.valueOf( fields[ImportManager.IDX_ACCOUNT_ID] );
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
				
				ImportManager.newResidenceInfo( fields, liteAcctInfo );
				
				numResAdded++;
				it.remove();
				
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
		}
		catch (Exception e) {
			if (status == STATUS_CANCELED) {
				errorMsg = "Operation is canceled by user";
			}
			else {
				e.printStackTrace();
				status = STATUS_ERROR;
				
				if (position != null)
					errorMsg = "Error at " + position;
				else
					errorMsg = "Failed to import old STARS data";
				if (e instanceof WebClientException)
					errorMsg += ": " + e.getMessage();
			}
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
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
		
		int idx = 0;
		logMsg.add(idx++, "Start Time: " + ServerUtils.formatDate( new Date(startTime), java.util.TimeZone.getDefault() ));
		logMsg.add(idx++, "Time Taken: " + timeTaken);
		logMsg.add(idx++, "");
		
		if (errorMsg != null) {
			logMsg.add(idx++, errorMsg);
			logMsg.add(idx++, "");
		}
		
		if (acctFieldsCnt > 0) {
			if (numAcctAdded == acctFieldsCnt)
				logMsg.add(idx++, numAcctAdded + " customer accounts imported successfully");
			else
				logMsg.add(idx++, numAcctAdded + " of " + acctFieldsCnt + " customer accounts imported");
		}
		if (invFieldsCnt > 0) {
			String msg = null;
			if (numInvAdded  == invFieldsCnt)
				msg = numInvAdded + " hardwares imported successfully";
			else
				msg = numInvAdded + " of " + invFieldsCnt + " hardwares imported";
			msg += " (" + numRecvrAdded + " receivers, " + numMeterAdded + " meters)";
			logMsg.add(idx++, msg);
			
			if (numNoDeviceName > 0)
				logMsg.add(idx++, numNoDeviceName + " hardwares ignored because device name is empty");
			if (numDeviceNameNotFound > 0)
				logMsg.add(idx++, numDeviceNameNotFound + " hardwares ignored because no matching device name found in Yukon");
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
			logMsg.add(idx++, msg);
			
			if (numNoLoadDescription > 0)
				logMsg.add(idx++, numNoLoadDescription + " appliances ignored because load description is empty");
		}
		if (orderFieldsCnt > 0) {
			if (numOrderAdded == orderFieldsCnt)
				logMsg.add(idx++, numOrderAdded + " work orders imported successfully");
			else
				logMsg.add(idx++, numOrderAdded + " of " + orderFieldsCnt + " work orders imported");
		}
		if (resFieldsCnt > 0) {
			if (numResAdded == resFieldsCnt)
				logMsg.add(idx++, numResAdded + " residence info imported successfully");
			else
				logMsg.add(idx++, numResAdded + " of " + resFieldsCnt + " residence information imported");
		}
		logMsg.add(idx++, "");
		
		Date logDate = new Date();
		String logFileName = "import_" + ServerUtils.starsDateFormat.format(logDate) +
				"_" + ServerUtils.starsTimeFormat.format(logDate) + ".log";
		logFile = new File(path, logFileName);
		
		try {
			ServerUtils.writeFile(logFile, logMsg);
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
