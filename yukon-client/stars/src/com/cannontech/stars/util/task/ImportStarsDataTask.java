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
import java.util.Map;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMProgram;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.web.servlet.StarsAdmin;

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
				if (acctFieldsList.size() > 0) {
					if (numAcctAdded == acctFieldsList.size())
						msg += numAcctAdded + " customer accounts imported successfully";
					else
						msg += numAcctAdded + " of " + acctFieldsList.size() + " customer accounts imported";
					msg += LINE_SEPARATOR;
				}
				if (invFieldsList.size() > 0) {
					if (numInvAdded  == invFieldsList.size())
						msg += numInvAdded + " hardwares imported successfully";
					else
						msg += numInvAdded + " of " + invFieldsList.size() + " hardwares imported";
					msg += " (" + numRecvrAdded + " receivers, " + numMeterAdded + " meters)" + LINE_SEPARATOR;
				}
				if (appFieldsList.size() > 0) {
					if (numAppImported == appFieldsList.size())
						msg += numAppImported + " appliances imported successfully";
					else
						msg += numAppImported + " of " + appFieldsList.size() + " appliances imported";
					msg += " (" + numACImported + " ac, " +
							numWHImported + " wh, " +
							numGenImported + " gen, " +
							numIrrImported + " irr, " +
							numGDryImported + " gdry, " +
							numHPImported + " hp, " +
							numSHImported + " sh, " +
							numDFImported + " df, " +
							numGenlImported + " genl)" +
							LINE_SEPARATOR;
				}
				if (orderFieldsList.size() > 0) {
					if (numOrderAdded == orderFieldsList.size())
						msg += numOrderAdded + " work orders imported successfully";
					else
						msg += numOrderAdded + " of " + orderFieldsList.size() + " work orders imported";
					msg += LINE_SEPARATOR;
				}
				if (resFieldsList.size() > 0) {
					if (numResAdded == resFieldsList.size())
						msg += numResAdded + " residence info imported successfully";
					else
						msg += numResAdded + " of " + resFieldsList.size() + " residence information imported";
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
		
		String lineNo = null;
		java.sql.Connection conn = null;
		ArrayList logMsg = new ArrayList();
		
		startTime = System.currentTimeMillis();
		
		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			for (int i = 0; i < acctFieldsList.size(); i++) {
				String[] fields = (String[]) acctFieldsList.get(i);
				lineNo = fields[StarsAdmin.IDX_LINE_NUM];
				
				LiteStarsCustAccountInformation liteAcctInfo = StarsAdmin.newCustomerAccount( fields, user, energyCompany );
				acctIDMap.put( Integer.valueOf(fields[StarsAdmin.IDX_ACCOUNT_ID]), liteAcctInfo );
				
				numAcctAdded++;
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
			}
			
			lineNo = null;
			
			for (int i = 0; i < invFieldsList.size(); i++) {
				String[] fields = (String[]) invFieldsList.get(i);
				lineNo = fields[StarsAdmin.IDX_LINE_NUM];
				
				Integer acctID = Integer.valueOf( fields[StarsAdmin.IDX_ACCOUNT_ID] );
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
				
				LiteInventoryBase liteInv = StarsAdmin.insertLMHardware( fields, liteAcctInfo, energyCompany, conn );
				
				if (liteInv.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL)
					logMsg.add("Receiver (import_inv_id=" + fields[StarsAdmin.IDX_INV_ID] + ",db_inv_id=" + liteInv.getInventoryID() + ") is out of service");
				else if (liteInv.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL)
					logMsg.add("Receiver (import_inv_id=" + fields[StarsAdmin.IDX_INV_ID] + ",db_inv_id=" + liteInv.getInventoryID() + ") is temporarily out of service");
				
				for (int j = 0; j < 3; j++) {
					if (fields[StarsAdmin.IDX_R1_STATUS + j].equals("1"))
						logMsg.add("Receiver (import_inv_id=" + fields[StarsAdmin.IDX_INV_ID] + ",db_inv_id=" + liteInv.getInventoryID() + ") relay " + (j+1) + " is out of service");
					else if (fields[StarsAdmin.IDX_R1_STATUS + j].equals("2"))
						logMsg.add("Receiver (import_inv_id=" + fields[StarsAdmin.IDX_INV_ID] + ",db_inv_id=" + liteInv.getInventoryID() + ") relay " + (j+1) + " was out before switch placed out");
				}
				
				if (liteAcctInfo != null) {
					// If load groups for relays are specified, automatically add appliances through program enrollment
					ArrayList appCats = energyCompany.getAllApplianceCategories();
					ArrayList programs = new ArrayList();
					int[] progIDs = new int[3];	// Save the program ID on each relay here so we can map them to appliance id later
					
					for (int j = 0; j < 3; j++) {
						if (fields[StarsAdmin.IDX_R1_GROUP + j].equals("") || fields[StarsAdmin.IDX_R1_GROUP + j].equals("0"))
							continue;
						
						int groupID = Integer.parseInt( fields[StarsAdmin.IDX_R1_GROUP + j] );
						int progID = 0;
						int appCatID = 0;
						
						for (int k = 0; k < appCats.size(); k++) {
							LiteApplianceCategory liteAppCat = (LiteApplianceCategory) appCats.get(k);
							for (int l = 0; l < liteAppCat.getPublishedPrograms().length; l++) {
								LiteLMProgram liteProg = liteAppCat.getPublishedPrograms()[l];
								for (int m = 0; m < liteProg.getGroupIDs().length; m++) {
									if (liteProg.getGroupIDs()[m] == groupID) {
										progID = liteProg.getProgramID();
										appCatID = liteAppCat.getApplianceCategoryID();
										progIDs[j] = progID;
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
						StarsAdmin.programSignUp( programs, liteAcctInfo, new Integer(liteInv.getInventoryID()), energyCompany, conn );
						
						int[] appIDs = new int[3];
						for (int j = 0; j < 3; j++) {
							if (progIDs[j] == 0) continue;
							
							for (int k = 0; k < liteAcctInfo.getAppliances().size(); k++) {
								LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(k);
								if (liteApp.getLmProgramID() == progIDs[j]) {
									appIDs[j] = liteApp.getApplianceID();
									break;
								}
							}
							
							if (appIDs[j] == 0)	// shouldn't happen
								throw new WebClientException("Cannot find appliance with RelayNum = " + (j+1));
						}
						
						appIDMap.put( new Integer(fields[StarsAdmin.IDX_INV_ID]), appIDs );
						numAppAdded += programs.size();
					}
				}
				
				numInvAdded++;
				if (fields[StarsAdmin.IDX_DEVICE_NAME].equals(""))
					numRecvrAdded++;
				else
					numMeterAdded++;
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
			}
			
			lineNo = null;
			
			for (int i = 0; i < appFieldsList.size(); i++) {
				String[] fields = (String[]) appFieldsList.get(i);
				lineNo = fields[StarsAdmin.IDX_LINE_NUM];
				
				Integer acctID = Integer.valueOf( fields[StarsAdmin.IDX_ACCOUNT_ID] );
				LiteStarsCustAccountInformation liteAcctInfo = null;
				
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
				
				Integer invID = Integer.valueOf( fields[StarsAdmin.IDX_INV_ID] );
				int relayNum = Integer.parseInt( fields[StarsAdmin.IDX_RELAY_NUM] );
				
				if (relayNum > 0) {
					int appID = 0;
					int[] appIDs = (int[]) appIDMap.get( invID );
					if (appIDs != null) appID = appIDs[relayNum - 1];
					
					if (appID == 0)
						throw new WebClientException("Cannot find appliance with InventoryID = " + invID + " and RelayNum = " + relayNum);
					
					StarsAdmin.updateAppliance( fields, appID, liteAcctInfo, energyCompany );
				}
				else {
					StarsAdmin.newAppliance( fields, liteAcctInfo, energyCompany );
				}
				
				numAppImported++;
				
				if (fields[StarsAdmin.IDX_APP_CAT_DEF_ID].equals("")) continue;
				int catDefID = Integer.parseInt( fields[StarsAdmin.IDX_APP_CAT_DEF_ID] );
				
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
			
			lineNo = null;
			
			for (int i = 0; i < orderFieldsList.size(); i++) {
				String[] fields = (String[]) orderFieldsList.get(i);
				lineNo = fields[StarsAdmin.IDX_LINE_NUM];
				
				Integer acctID = Integer.valueOf( fields[StarsAdmin.IDX_ACCOUNT_ID] );
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
				
				StarsAdmin.newServiceRequest( fields, liteAcctInfo, energyCompany );
				numOrderAdded++;
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
			}
			
			lineNo = null;
			
			for (int i = 0; i < resFieldsList.size(); i++) {
				String[] fields = (String[]) resFieldsList.get(i);
				lineNo = fields[StarsAdmin.IDX_LINE_NUM];
				
				Integer acctID = Integer.valueOf( fields[StarsAdmin.IDX_ACCOUNT_ID] );
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
				
				StarsAdmin.newResidenceInfo( fields, liteAcctInfo );
				numResAdded++;
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
			}
			
			status = STATUS_FINISHED;
		}
		catch (OutOfMemoryError me) {
			// We must catch this error to make sure the mapping and log files are written
			status = STATUS_ERROR;
			errorMsg = "Operation caused out of memory error";
			System.gc();
		}
		catch (Exception e) {
			if (status == STATUS_CANCELED) {
				errorMsg = "Operation is canceled by user";
			}
			else {
				e.printStackTrace();
				status = STATUS_ERROR;
				
				if (lineNo != null) {
					if (numAcctAdded < acctFieldsList.size())
						errorMsg = "Error encountered when processing customer file line #" + lineNo;
					else if (numInvAdded < invFieldsList.size())
						errorMsg = "Error encountered when processing inventory file line #" + lineNo;
					else if (numAppImported < appFieldsList.size())
						errorMsg = "Error encountered when processing loadinfo file line #" + lineNo;
					else if (numOrderAdded < orderFieldsList.size())
						errorMsg = "Error encountered when processing workorder file line #" + lineNo;
					else if (numResAdded < resFieldsList.size())
						errorMsg = "Error encountered when processing resinfo file line #" + lineNo;
				}
				if (errorMsg == null)
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
		}
		
		stopTime = System.currentTimeMillis();
		
		// Directory where the mapping and log files will be written into
		String path = (String) preprocessedData.get( "CustomerFilePath" );
		
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
		
		if (numAcctAdded > 0)
			logMsg.add(idx++, numAcctAdded + " customer accounts imported");
		if (numInvAdded > 0)
			logMsg.add(idx++, numInvAdded + " hardwares imported (" + numRecvrAdded + " receivers, " + numMeterAdded + " meters)");
		if (numAppAdded > 0)
			logMsg.add(idx++, numAppAdded + " appliances automatically added");
		if (numAppImported > 0)
			logMsg.add(idx++, numAppImported + " appliances imported (" +
					numACImported + " ac, " +
					numWHImported + " wh, " +
					numGenImported + " gen, " +
					numIrrImported + " irr, " +
					numGDryImported + " gdry, " +
					numHPImported + " hp, " +
					numSHImported + " sh, " +
					numDFImported + " df, " +
					numGenlImported + " genl)"
					);
		if (numOrderAdded > 0)
			logMsg.add(idx++, numOrderAdded + " work orders imported");
		if (numResAdded > 0)
			logMsg.add(idx++, numResAdded + " residence info imported");
		logMsg.add(idx++, "");
		
		Date logDate = new Date();
		String logFileName = "import_" + StarsAdmin.starsDateFormat.format(logDate) +
				"_" + StarsAdmin.starsTimeFormat.format(logDate) + ".log";
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
		
		if (numAcctAdded > 0) {
			// New accounts added, regenerate the 'customer.map' file
			ArrayList lines = new ArrayList();
			
			Iterator it = acctIDMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				
				Integer acctID = (Integer) entry.getKey();
				int acctID2 = -1;
				if (entry.getValue() instanceof LiteStarsCustAccountInformation)
					acctID2 = ((LiteStarsCustAccountInformation) entry.getValue()).getAccountID();
				else if (entry.getValue() instanceof Integer)
					acctID2 = ((Integer) entry.getValue()).intValue();
				
				String line = acctID.toString() + "," + String.valueOf(acctID2);
				lines.add( line );
			}
			
			File custMapFile = new File(path, "customer.map");
			try {
				ServerUtils.writeFile(custMapFile, lines);
			}
			catch (IOException e) {
				if (errorMsg != null)
					errorMsg += LINE_SEPARATOR;
				else
					errorMsg = "";
				errorMsg += "Failed to write mapping file 'customer.map'";
			}
		}
		
		if (numAppAdded > 0) {
			// New appliances added, regenerate the 'hwconfig.map' file
			ArrayList lines = new ArrayList();
			
			Iterator it = appIDMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Integer invID = (Integer) entry.getKey();
				int[] appIDs = (int[]) entry.getValue();
				
				String line = invID.toString()
						+ "," + appIDs[0]
						+ "," + appIDs[1]
						+ "," + appIDs[2];
				lines.add( line );
			}
			
			File hwConfigMapFile = new File(path, "hwconfig.map");
			try {
				ServerUtils.writeFile(hwConfigMapFile, lines);
			}
			catch (IOException e) {
				if (errorMsg != null)
					errorMsg += LINE_SEPARATOR;
				else
					errorMsg = "";
				errorMsg += "Failed to write mapping file 'hwconfig.map'";
			}
		}
		
		if ((status == STATUS_ERROR || status == STATUS_CANCELED) && logFile != null)
			errorMsg += LINE_SEPARATOR + "For detailed information, view log file '" + logFile.getPath() + "'";
	}

}
