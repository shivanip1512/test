/*
 * Created on Jan 5, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteSubstation;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.action.CreateApplianceAction;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.NewCustAccountAction;
import com.cannontech.stars.web.action.UpdateLMHardwareConfigAction;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.ACType;
import com.cannontech.stars.xml.serialize.AirConditioner;
import com.cannontech.stars.xml.serialize.BillingAddress;
import com.cannontech.stars.xml.serialize.ContactNotification;
import com.cannontech.stars.xml.serialize.DeviceType;
import com.cannontech.stars.xml.serialize.DualFuel;
import com.cannontech.stars.xml.serialize.EnergySource;
import com.cannontech.stars.xml.serialize.LMHardware;
import com.cannontech.stars.xml.serialize.Location;
import com.cannontech.stars.xml.serialize.Manufacturer;
import com.cannontech.stars.xml.serialize.NumberOfGallons;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.SA205;
import com.cannontech.stars.xml.serialize.SASimple;
import com.cannontech.stars.xml.serialize.SecondaryEnergySource;
import com.cannontech.stars.xml.serialize.StarsCreateAppliance;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.stars.xml.serialize.StarsNewCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsSiteInformation;
import com.cannontech.stars.xml.serialize.StorageHeat;
import com.cannontech.stars.xml.serialize.StorageType;
import com.cannontech.stars.xml.serialize.StreetAddress;
import com.cannontech.stars.xml.serialize.Substation;
import com.cannontech.stars.xml.serialize.SwitchOverType;
import com.cannontech.stars.xml.serialize.Tonnage;
import com.cannontech.stars.xml.serialize.WaterHeater;
import com.cannontech.user.UserUtils;

class CustomerPK {
	public String mappage = null;
	public String mapsection = null;
	public String mapid = null;
	int coopid = 0;
	
	public CustomerPK (String mappage, String mapsection, String mapid, int coopid) {
		this.mappage = mappage;
		this.mapsection = mapsection;
		this.mapid = mapid;
		this.coopid = coopid;
	}
	
	public boolean equals(Object obj) {
		CustomerPK pk = (CustomerPK) obj;
		return mappage.equals(pk.mappage) && mapsection.equals(pk.mapsection) && mapid.equals(pk.mapid) && (coopid == pk.coopid);
	}
	
	public int hashCode() {
		return toString().hashCode();
	}
	
	public String toString() {
		return mappage + "," + mapsection + "," + mapid + "," + coopid;
	}
}

class ReceiverType implements Comparable {
	public String type = null;
	public String model = null;
	public String frequency = null;
	
	public ReceiverType(String s1, String s2, String s3) {
		type = s1;
		model = s2;
		frequency = s3;
	}
	
	public boolean equals(Object obj) {
		ReceiverType rt = (ReceiverType) obj;
		return type.equalsIgnoreCase(rt.type) && model.equalsIgnoreCase(rt.model) && frequency.equalsIgnoreCase(rt.frequency);
	}
	
	public int hashCode() {
		return toString().hashCode();
	}
	
	public String toString() {
		return type + "," + model + "," + frequency;
	}
	
	public int compareTo(Object o) {
		ReceiverType rt = (ReceiverType) o;
		return toString().compareTo( rt.toString() );
	}
}

class ControlType implements Comparable {
	public String loadType = null;
	public String controlType = null;
	
	public ControlType(String s1, String s2) {
		loadType = s1;
		controlType = s2;
	}
	
	public boolean equals(Object obj) {
		ControlType ct = (ControlType) obj;
		return loadType.equalsIgnoreCase(ct.loadType) && controlType.equalsIgnoreCase(ct.controlType);
	}
	
	public int hashCode() {
		return toString().hashCode();
	}
	
	public String toString() {
		return loadType + "," + controlType;
	}
	
	public int compareTo(Object o) {
		ControlType ct = (ControlType) o;
		return toString().compareTo( ct.toString() );
	}
}

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ImportDSMDataTask extends TimeConsumingTask {
	
	private static final String DELIM = "~";
	private final String NEW_LINE = System.getProperty( "line.separator" );
	private final Integer ZERO = new Integer(0);
	
	private final int RESUME_FILE_COOP = 1;
	private final int RESUME_FILE_SUBSTATION = 2;
	private final int RESUME_FILE_CUSTOMER = 3;
	private final int RESUME_FILE_RECEIVER = 4;
	private final int RESUME_FILE_SELECTION_LIST = 5;
	private final int RESUME_FILE_CONTROLLED_LOAD = 6;
	
	private LiteStarsEnergyCompany energyCompany = null;
	private File importDir = null;
	
	private PrintWriter importLog = null;
	private String errorLocation = null;
	private String resumeLocation = null;
	private String progressMsg = null;
	
	private int resumeFile = 0;
	private int resumeLine = 0;
	
	private int numCoopImported = 0;
	private int numSubstationImported = 0;
	private int numCustomerImported = 0;
	private int numReceiverImported = 0;
	private int numCtrlLoadImported = 0;
	
	private SimpleDateFormat dateParser = new SimpleDateFormat("MMM dd yyyy hh:mm:ss:SSSa");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	private Hashtable coopMap = null;	// Map from DSM coop_id (Integer) to STARS EnergyCompanyID (Integer)
	private Hashtable substationMap = null;	// Map from DSM cp_substation_id (Integer) to STARS SubstationID (Integer)
	private Hashtable customerMap = null;	// Map from DSM customer (CustomerPK) to STARS CustomerID (Integer)
	private Hashtable receiverMap = null;	// Map from DSM serial_no (String) to Integer[] {STARS Inventory ID, Slot #5 Code}
	 
	private Hashtable routeMap = null;	// Map from DSM coop_id (Integer) to STARS default route ID (Integer)
	private Hashtable receiverTypeMap = null;	// Map from DSM receiver type (ReceiverType) to STARS device type ID (Integer)
	private Hashtable loadTypeMap = null;	// Map from DSM load_id (Integer) to Object[] {DSM load type (String), STARS appliance category ID (Integer)}
	private Hashtable functionTable = null;	// Map from SA205 slot#5 code (Integer) to functions on each relay (String[])
	private ArrayList programTable = null;	// Array of Object[] {SA205 function (String), STARS appliance category ID (Integer), STARS program ID (Integer)}
	
	public ImportDSMDataTask(LiteStarsEnergyCompany energyCompany, File importDir) {
		this.energyCompany = energyCompany;
		this.importDir = importDir;
		dateParser.setTimeZone( energyCompany.getDefaultTimeZone() );
		dateFormat.setTimeZone( energyCompany.getDefaultTimeZone() );
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if (status == STATUS_FINISHED) {
			return "DSM database has been converted successfully. Please check the \"_import.log\" for detailed information.";
		}
		else if (status == STATUS_RUNNING){
			if (progressMsg != null)
				return progressMsg + NEW_LINE + NEW_LINE + getImportProgress();
			else
				return "Converting DSM database...";
		}
		else {
			String msg = getImportProgress();
			if (msg.length() == 0) msg = null;
			return msg;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			status = STATUS_RUNNING;
			
			getResumeLocation();
			
			importLog = new PrintWriter(new FileWriter(new File(importDir, "_import.log"), true), true);
			importLog.println("============================================================");
			importLog.println("Start time: " + StarsUtils.formatDate( new Date(), energyCompany.getDefaultTimeZone() ));
			importLog.println();
			
			if (resumeFile <= RESUME_FILE_COOP) {
				progressMsg = "Converting member coops...";
				importCoop();
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
			}
			
			if (resumeFile <= RESUME_FILE_SUBSTATION) {
				progressMsg = "Converting substations...";
				errorLocation = null;
				resumeLocation = "substation.out";
				
				importSubstation();
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
			}
			
			if (resumeFile <= RESUME_FILE_CUSTOMER) {
				progressMsg = "Converting customers...";
				errorLocation = null;
				resumeLocation = "customer.out";
				
				importCustomer();
			}
			
			if (resumeFile <= RESUME_FILE_RECEIVER) {
				progressMsg = "Converting receivers...";
				errorLocation = null;
				resumeLocation = "receiver.out";
				
				importReceiver();
			}
			
			if (resumeFile <= RESUME_FILE_SELECTION_LIST) {
				progressMsg = "Converting selection lists...";
				errorLocation = null;
				resumeLocation = "selectionlist";
				
				importSelectionList();
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
			}
			
			if (resumeFile <= RESUME_FILE_CONTROLLED_LOAD) {
				progressMsg = "Converting controlled load...";
				errorLocation = null;
				resumeLocation = "controlledload.out";
				
				importControlledLoad();
			}
			
			importLog.println();
			importLog.println("Stop time: " + StarsUtils.formatDate( new Date(), energyCompany.getDefaultTimeZone() ));
			importLog.println();
			importLog.print(getImportProgress());
			importLog.println();
			importLog.println("@DONE");
			
			status = STATUS_FINISHED;
		}
		catch (Exception e) {
			if (status == STATUS_CANCELED) {
				errorMsg = "Operation is canceled by user";
				if (errorLocation != null)
					errorMsg += " before processing " + errorLocation;
				else if (resumeLocation != null)
					errorMsg += " before processing " + resumeLocation;
			}
			else {
				status = STATUS_ERROR;
				CTILogger.error( e.getMessage(), e );
				
				if (errorLocation != null) {
					errorMsg = "An error occured at " + errorLocation;
					if (e instanceof WebClientException)
						errorMsg += ": " + e.getMessage();
				}
				else {
					if (e instanceof WebClientException)
						errorMsg = e.getMessage();
					else
						errorMsg = "An error occured in the process of converting the DSM database";
				}
			}
			
			if (importLog != null) {
				importLog.println();
				importLog.println("Stop time: " + StarsUtils.formatDate( new Date(), energyCompany.getDefaultTimeZone() ));
				importLog.println();
				importLog.println( errorMsg );
				importLog.print(getImportProgress());
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
		}
	}
	
	/**
	 * Generate config files used as input to the conversion program. The config files are populated with
	 * data from the DSM database, as well as instructions on how to complete the rest of the file.
	 * The following config files will be generated:
	 *     "$route.map", "$receivertype.map", "$loadtype.map"
	 */
	public static void generateConfigFiles(File importDir) throws Exception {
		File file = new File(importDir, "coop.out");
		if (!file.exists())
			throw new WebClientException( "File \"coop.out\" not found!" );
		
		String[] lines = StarsUtils.readFile( file, false );
		PrintWriter fw = null;
		
		try {
			fw = new PrintWriter(new FileWriter(new File(importDir, "$route.map")));
			fw.println("# This file defines the mapping from coops to their assigned route macros.");
			fw.println("# Complete the file by adding the route name to the end of each line.");
			
			for (int i = 0; i < lines.length; i++) {
				String[] fields = StarsUtils.splitString( lines[i], DELIM );
				fw.println( fields[1].trim() + "," + fields[0].trim() + "," );
			}
		}
		finally {
			if (fw != null) fw.close();
		}
		
		file = new File(importDir, "receiver.out");
		if (!file.exists())
			throw new WebClientException( "File \"receiver.out\" not found!" );
		
		BufferedReader fr = null;
		TreeSet recvTypeSet = new TreeSet();
		
		try {
			fr = new BufferedReader(new FileReader(file));
			String line = null;
			
			while ((line = fr.readLine()) != null) {
				if (line.trim().length() == 0 || line.charAt(0) == '#')
					continue;
				
				String[] fields = StarsUtils.splitString( line, DELIM );
				ReceiverType rt = new ReceiverType( fields[18].trim(), fields[7].trim(), fields[23].trim() );
				recvTypeSet.add( rt );
			}
		}
		finally {
			if (fr != null) fr.close();
		}
		
		fw = null;
		try {
			fw = new PrintWriter(new FileWriter(new File(importDir, "$receivertype.map")));
			fw.println("# This file defines the mapping from receiver types in DSM to device types in STARS.");
			fw.println("# The existing data is in the form of: \"receiver_type,receiver_model,frequency,\"");
			fw.println("# Complete the file by adding the corresponding device type to the end of each line.");
			
			Iterator it = recvTypeSet.iterator();
			while (it.hasNext()) {
				ReceiverType rt = (ReceiverType) it.next();
				fw.println( rt.toString() + "," );
			}
		}
		finally {
			if (fw != null) fw.close();
		}
		
		file = new File(importDir, "load_type.out");
		if (!file.exists())
			throw new WebClientException( "File \"load_type.out\" not found!" );
		
		lines = StarsUtils.readFile( file, false );
		fw = null;
		
		try {
			fw = new PrintWriter(new FileWriter(new File(importDir, "$loadtype.map")));
			fw.println("# This file defines the mapping from load types in DSM to appliance categories in STARS.");
			fw.println("# Complete the file by adding the corresponding appliance category to the end of each line.");
			
			for (int i = 0; i < lines.length; i++) {
				String[] fields = StarsUtils.splitString( lines[i], DELIM );
				fw.println( fields[1].trim() + "," + fields[0].trim() + "," );
			}
		}
		finally {
			if (fw != null) fw.close();
		}
	}
	
	private void getResumeLocation() throws Exception {
		File file = new File(importDir, "_import.log");
		if (!file.exists()) file.createNewFile();
		
		String[] lines = StarsUtils.readFile( file, false );
		for (int i = lines.length - 1; i >= 0; i--) {
			if (lines[i].startsWith("@DONE"))
				throw new WebClientException("The data in this directory has already been converted. If you want to convert it again, please remove the \"_import.log\" file first.");
			
			if (lines[i].startsWith("@RESUME")) {
				StringTokenizer st = new StringTokenizer( lines[i].substring(7) );
				if (st.hasMoreTokens()) {
					String fileName = st.nextToken();
					if (fileName.equalsIgnoreCase("coop.out"))
						resumeFile = RESUME_FILE_COOP;
					else if (fileName.equalsIgnoreCase("substation.out"))
						resumeFile = RESUME_FILE_SUBSTATION;
					else if (fileName.equalsIgnoreCase("customer.out"))
						resumeFile = RESUME_FILE_CUSTOMER;
					else if (fileName.equalsIgnoreCase("receiver.out"))
						resumeFile = RESUME_FILE_RECEIVER;
					else if (fileName.equalsIgnoreCase("selectionlist"))
						resumeFile = RESUME_FILE_SELECTION_LIST;
					else if (fileName.equalsIgnoreCase("controlledload.out"))
						resumeFile = RESUME_FILE_CONTROLLED_LOAD;
				}
				if (st.hasMoreTokens()) {
					resumeLine = Integer.parseInt( st.nextToken() );
				}
				return;
			}
		}
	}
	
	private String getImportProgress() {
		String progress = "";
		if (numCoopImported > 0)
			progress += numCoopImported + " member coops imported" + NEW_LINE;
		if (numSubstationImported > 0)
			progress += numSubstationImported + " substations imported" + NEW_LINE;
		if (numCustomerImported > 0)
			progress += numCustomerImported + " customers imported" + NEW_LINE;
		if (numReceiverImported > 0)
			progress += numReceiverImported + " receivers imported" + NEW_LINE;
		if (numCtrlLoadImported > 0)
			progress += numCtrlLoadImported + " controlled load imported" + NEW_LINE;
		return progress;
	}
	
	private long getInitAccountNo() throws Exception {
		String sql = "SELECT MAX(AccountNumber) FROM CustomerAccount WHERE AccountNumber LIKE '000%'";
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
		stmt.execute();
		
		long accountNo = 0;
		if (stmt.getRowCount() > 0)
			accountNo = Long.parseLong((String)stmt.getRow(0)[0]);
		
		return ++accountNo;
	}
	
	private Hashtable getCoopMap() throws Exception {
		if (coopMap == null) {
			coopMap = new Hashtable();
			File file = new File(importDir, "_coop.map");
			if (file.exists()) {
				String[] lines = StarsUtils.readFile( file, false );
				for (int i = 0; i < lines.length; i++) {
					String[] fields = StarsUtils.splitString( lines[i], "," );
					coopMap.put( Integer.valueOf(fields[0]), Integer.valueOf(fields[1]) );
				}
			}
		}
		return coopMap;
	}
	
	private Hashtable getSubstationMap() throws Exception {
		if (substationMap == null) {
			substationMap = new Hashtable();
			File file = new File(importDir, "_substation.map");
			if (file.exists()) {
				String[] lines = StarsUtils.readFile( file, false );
				for (int i = 0; i < lines.length; i++) {
					String[] fields = StarsUtils.splitString( lines[i], "," );
					substationMap.put( Integer.valueOf(fields[0]), Integer.valueOf(fields[1]) );
				}
			}
		}
		return substationMap;
	}
	
	private Hashtable getCustomerMap() throws Exception {
		if (customerMap == null) {
			customerMap = new Hashtable();
			File file = new File(importDir, "_customer.map");
			if (file.exists()) {
				String[] lines = StarsUtils.readFile( file, false );
				for (int i = 0; i < lines.length; i++) {
					String[] fields = StarsUtils.splitString( lines[i], "," );
					CustomerPK pk = new CustomerPK( fields[0], fields[1], fields[2], Integer.parseInt(fields[3]) );
					customerMap.put( pk, Integer.valueOf(fields[4]) );
				}
			}
		}
		return customerMap;
	}
	
	private Hashtable getReceiverMap() throws Exception {
		if (receiverMap == null) {
			receiverMap = new Hashtable();
			File file = new File(importDir, "_receiver.map");
			if (file.exists()) {
				String[] lines = StarsUtils.readFile( file, false );
				for (int i = 0; i < lines.length; i++) {
					String[] fields = StarsUtils.splitString( lines[i], "," );
					Integer invID = Integer.valueOf(fields[1]);
					Integer code = Integer.valueOf(fields[2]);
					receiverMap.put( fields[0], new Integer[]{invID, code} );
				}
			}
		}
		return receiverMap;
	}
	
	private Hashtable getRouteMap() throws Exception {
		if (routeMap == null) {
			routeMap = new Hashtable();
			LiteYukonPAObject[] liteRoutes = PAOFuncs.getAllLiteRoutes();
			
			File file = new File(importDir, "$route.map");
			String[] lines = StarsUtils.readFile( file, false );
			
			for (int i = 0; i < lines.length; i++) {
				String[] fields = StarsUtils.splitString( lines[i], "," );
				
				if (fields[2].length() > 0) {
					Integer routeID = null;
					for (int j = 0; j < liteRoutes.length; j++) {
						if (liteRoutes[j].getPaoName().equalsIgnoreCase( fields[2] )) {
							routeID = new Integer(liteRoutes[j].getYukonID());
							routeMap.put( Integer.valueOf(fields[0]), routeID );
							break;
						}
					}
					if (routeID == null)
						throw new WebClientException( "Route \"" + fields[2] + "\" was not found in Yukon" );
				}
			}
		}
		return routeMap;
	}
	
	private Hashtable getReceiverTypeMap() throws Exception {
		if (receiverTypeMap == null) {
			receiverTypeMap = new Hashtable();
			
			File file = new File(importDir, "$receivertype.map");
			String[] lines = StarsUtils.readFile( file, false );
			
			for (int i = 0; i < lines.length; i++) {
				String[] fields = StarsUtils.splitString( lines[i], "," );
				
				if (fields[3].length() > 0) {
					YukonSelectionList list = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
					YukonListEntry deviceType = YukonListFuncs.getYukonListEntry( list, fields[3] );
					if (deviceType == null)
						throw new WebClientException( "Device type \"" + fields[3] + "\" was not defined in STARS" );
					
					ReceiverType rt = new ReceiverType( fields[0], fields[1], fields[2] );
					receiverTypeMap.put( rt, new Integer(deviceType.getEntryID()) );
				}
			}
		}
		return receiverTypeMap;
	}
	
	private Hashtable getLoadTypeMap() throws Exception {
		if (loadTypeMap == null) {
			loadTypeMap = new Hashtable();
			
			ArrayList appCats = energyCompany.getApplianceCategories();
			File file = new File(importDir, "$loadtype.map");
			String[] lines = StarsUtils.readFile( file, false );
			
			for (int i = 0; i < lines.length; i++) {
				String[] fields = StarsUtils.splitString( lines[i], "," );
				
				if (fields[2].length() > 0) {
					Integer appCatID = null;
					for (int j = 0; j < appCats.size(); j++) {
						LiteApplianceCategory liteAppCat = (LiteApplianceCategory) appCats.get(j);
						if (liteAppCat.getDescription().equalsIgnoreCase( fields[2] )) {
							appCatID = new Integer(liteAppCat.getApplianceCategoryID());
							loadTypeMap.put( Integer.valueOf(fields[0]), new Object[] {fields[1], appCatID} );
							break;
						}
					}
					
					if (appCatID == null)
						throw new WebClientException( "Appliance category \"" + fields[2] + "\" was not defined in STARS" );
				}
			}
		}
		return loadTypeMap;
	}
	
	private Hashtable getFunctionTable() throws Exception {
		if (functionTable == null) {
			functionTable = new Hashtable();
			
			File file = new File(importDir, "$sa205coding.map");
			if (!file.exists())
				throw new WebClientException("File \"$sa205coding.map\" not found!");
			
			String[] lines = StarsUtils.readFile( file, false );
			for (int i = 0; i < lines.length; i++) {
				String[] fields = StarsUtils.splitString( lines[i], "," );
				Integer code = Integer.valueOf( fields[0] );
				String[] functions = new String[] {fields[1], fields[2], fields[3], fields[4]};
				functionTable.put( code, functions );
			}
		}
		return functionTable;
	}
	
	private ArrayList getProgramTable() throws Exception {
		if (programTable == null) {
			programTable = new ArrayList();
			ArrayList appCats = energyCompany.getApplianceCategories();
			
			File file = new File(importDir, "$program.map");
			if (!file.exists())
				throw new WebClientException("File \"$program.map\" not found!");
			
			String[] lines = StarsUtils.readFile( file, false );
			for (int i = 0; i < lines.length; i++) {
				String[] fields = StarsUtils.splitString( lines[i], "," );
				Integer appCatID = null;
				Integer programID = null;
				
				for (int j = 0; j < appCats.size(); j++) {
					LiteApplianceCategory liteAppCat = (LiteApplianceCategory) appCats.get(j);
					if (liteAppCat.getDescription().equalsIgnoreCase( fields[1] )) {
						appCatID = new Integer(liteAppCat.getApplianceCategoryID());
						
						for (int k = 0; k < liteAppCat.getPublishedPrograms().size(); k++) {
							LiteLMProgramWebPublishing liteProg = (LiteLMProgramWebPublishing) liteAppCat.getPublishedPrograms().get(k);
							if (StarsUtils.getPublishedProgramName(liteProg).equalsIgnoreCase( fields[2] )) {
								programID = new Integer(liteProg.getProgramID());
								break;
							}
						}
						
						if (programID == null)
							throw new WebClientException("LM program \"" + fields[2] + "\" was not defined in appliance category \"" + fields[1] + "\"");
						break;
					}
				}
				
				if (appCatID == null)
					throw new WebClientException( "Appliance category \"" + fields[1] + "\" was not defined in STARS" );
				
				programTable.add( new Object[] {fields[0], appCatID, programID} );
			}
		}
		return programTable;
	}
	
	private int[] getMatchingProgram(Integer code, int applianceCategoryID) throws Exception {
		String[] functions = (String[]) getFunctionTable().get( code );
		if (functions == null)
			throw new WebClientException("no functions are defined for code \"" + code + "\", program enrollment skipped.");
		
		ArrayList programTable = getProgramTable();
		for (int i = 0; i < functions.length; i++) {
			String[] subFunctions = StarsUtils.splitString( functions[i], DELIM );
			for (int j = 0; j < subFunctions.length; j++) {
				for (int k = 0; k < programTable.size(); k++) {
					Object[] program = (Object[]) programTable.get(k);
					String funcName = (String) program[0];
					int appCatID = ((Integer)program[1]).intValue();
					int programID = ((Integer)program[2]).intValue();
					
					if (subFunctions[j].trim().startsWith( funcName )) {
						if (appCatID == applianceCategoryID) {
							int relayNo = i + 1;
							return new int[] {programID, relayNo};
						}
						break;
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Coop file format:
	 * 0 name						char(20)	EnergyCompany->Name
	 * 1 coop_id					int
	 * 2 company					char(3)
	 * 3 test_primary_transmitter	char(8)
	 * Primary key: coop_id
	 */
	private void importCoop() throws Exception {
		File file = new File(importDir, "coop.out");
		if (!file.exists()) {
			errorMsg = "File \"coop.out\" not found!";
			throw new WebClientException( errorMsg );
		}
		
		Hashtable coopMap = getCoopMap();
		
		File mapFile = new File(importDir, "_coop.map");
		PrintWriter fw = null;
		BufferedReader fr = null;
		
		try {
			fw = new PrintWriter(new FileWriter(mapFile, true), true);
			fr = new BufferedReader(new FileReader(file));
			String line = null;
			int lineNo = 0;
			
			while ((line = fr.readLine()) != null) {
				lineNo++;
				if (line.trim().length() == 0 || line.charAt(0) == '#')
					continue;
				if (resumeFile == RESUME_FILE_COOP && lineNo < resumeLine)
					continue;
				
				errorLocation = "\"coop.out\" line #" + lineNo;
				resumeLocation = "coop.out " + lineNo;
				
				String[] fields = StarsUtils.splitString(line, DELIM);
				
				String coopName = fields[0].trim();
				Integer coopID = Integer.valueOf( fields[1].trim() );
				
				// Create operator groups
				LiteYukonGroup[] operGroups = energyCompany.getWebClientOperatorGroups();
				String operGroupIDs = "";	// IDs of newly created groups
				LiteYukonGroup[] allGroups = new LiteYukonGroup[operGroups.length + 1];	// reserve a space for the amdin group
				
				for (int j = 0; j < operGroups.length; j++) {
					String grpName = operGroups[j].getGroupName() + " - " + coopName;
					allGroups[j] = StarsAdminUtil.copyYukonGroup( operGroups[j], grpName );
					if (j == 0)
						operGroupIDs += String.valueOf( allGroups[j].getGroupID() );
					else
						operGroupIDs += "," + String.valueOf( allGroups[j].getGroupID() );
				}
				
				Hashtable rolePropMap = new Hashtable();
				rolePropMap.put( new Integer(EnergyCompanyRole.OPERATOR_GROUP_IDS), operGroupIDs );
				rolePropMap.put( new Integer(EnergyCompanyRole.CUSTOMER_GROUP_IDS), "" );
				rolePropMap.put( new Integer(EnergyCompanyRole.SINGLE_ENERGY_COMPANY), CtiUtilities.FALSE_STRING );
				rolePropMap.put( new Integer(EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING), CtiUtilities.TRUE_STRING );
				rolePropMap.put( new Integer(AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY), CtiUtilities.TRUE_STRING );
				rolePropMap.put( new Integer(AdministratorRole.ADMIN_VIEW_BATCH_COMMANDS), CtiUtilities.TRUE_STRING );
				rolePropMap.put( new Integer(AdministratorRole.ADMIN_VIEW_OPT_OUT_EVENTS), CtiUtilities.TRUE_STRING );
				
				String adminGrpName = coopName + " Admin Grp";
				allGroups[allGroups.length - 1] = StarsAdminUtil.createOperatorAdminGroup(adminGrpName, rolePropMap);
				
				// Create the default operator login
				LiteYukonUser liteUser = StarsAdminUtil.createOperatorLogin(
						"member-" + coopID, "member-" + coopID, UserUtils.STATUS_ENABLED, allGroups, null );
				
				// Create primary contact for the member
				com.cannontech.database.data.customer.Contact contact = new com.cannontech.database.data.customer.Contact();
				com.cannontech.database.db.customer.Address address = new com.cannontech.database.db.customer.Address();
				address.setStateCode( "" );
				contact.setAddress( address );
				contact = (com.cannontech.database.data.customer.Contact)
						Transaction.createTransaction( Transaction.INSERT, contact ).execute();
				
				LiteContact liteContact = new LiteContact( contact.getContact().getContactID().intValue() );
				StarsLiteFactory.setLiteContact( liteContact, contact );
				ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_ADD );
				
				// Create the member
				com.cannontech.database.db.company.EnergyCompany company =
						new com.cannontech.database.db.company.EnergyCompany();
				company.setName( coopName );
				company.setPrimaryContactID( contact.getContact().getContactID() );
				company.setUserID( new Integer(liteUser.getUserID()) );
				company = (com.cannontech.database.db.company.EnergyCompany)
						Transaction.createTransaction(Transaction.INSERT, company).execute();
				
				SqlStatement stmt = new SqlStatement(
						"INSERT INTO EnergyCompanyOperatorLoginList VALUES(" + company.getEnergyCompanyID() + ", " + liteUser.getUserID() + ")",
						CtiUtilities.getDatabaseAlias() );
				stmt.execute();
				
				LiteStarsEnergyCompany member = new LiteStarsEnergyCompany( company );
				StarsDatabaseCache.getInstance().addEnergyCompany( member );
				ServerUtils.handleDBChange( member, DBChangeMsg.CHANGE_TYPE_ADD );
				
				// Assign default route to the member
				Integer routeID = (Integer) getRouteMap().get( coopID );
				if (routeID != null)
					StarsAdminUtil.updateDefaultRoute( member, routeID.intValue() );
				
				StarsAdminUtil.addMember( energyCompany, member, liteUser.getUserID() );
				numCoopImported++;
				
				coopMap.put( coopID, member.getEnergyCompanyID() );
				fw.println( coopID + "," + member.getEnergyCompanyID() );
			}
		}
		finally {
			if (fr != null) fr.close();
			if (fw != null) fw.close();
		}
	}
	
	/**
	 * Substation file format:
	 * 0 coop_substation_id		int
	 * 1 cp_substation_id		smallint
	 * 2 name					char(20)	Substation->SubstationName
	 * 3 coop_id				smallint
	 * Primary key: cp_substation_id
	 */
	private void importSubstation() throws Exception {
		File file = new File(importDir, "substation.out");
		if (!file.exists()) {
			errorMsg = "File \"substation.out\" not found!";
			throw new WebClientException( errorMsg );
		}
		
		Hashtable substationMap = getSubstationMap();
		
		File mapFile = new File(importDir, "_substation.map");
		PrintWriter fw = null;
		BufferedReader fr = null;
		
		try {
			fw = new PrintWriter(new FileWriter(mapFile, true), true);
			fr = new BufferedReader(new FileReader(file));
			String line = null;
			int lineNo = 0;
			
			while ((line = fr.readLine()) != null) {
				lineNo++;
				if (line.trim().length() == 0 || line.charAt(0) == '#')
					continue;
				if (resumeFile == RESUME_FILE_SUBSTATION && lineNo < resumeLine)
					continue;
				
				errorLocation = "\"substation.out\" line #" + lineNo;
				resumeLocation = "substation.out " + lineNo;
				
				String[] fields = StarsUtils.splitString( line, DELIM );
				
				Integer subID = Integer.valueOf( fields[1].trim() );
				String subName = fields[2].trim();
				Integer coopID = Integer.valueOf( fields[3].trim() );
				
				int newCoopID = ((Integer) getCoopMap().get(coopID)).intValue();
				LiteStarsEnergyCompany member = StarsDatabaseCache.getInstance().getEnergyCompany( newCoopID );
				
				LiteSubstation sub = StarsAdminUtil.createSubstation( subName, CtiUtilities.NONE_ZERO_ID, member );
				numSubstationImported++;
				
				substationMap.put( subID, new Integer(sub.getSubstationID()) );
				fw.println( subID + "," + sub.getSubstationID() );
			}
		}
		finally {
			if (fr != null) fr.close();
			if (fw != null) fw.close();
		}
	}
	
	/**
	 * Customer file format:
	 * 0  mappage					char(5)
	 * 1  mapsection				char(5)
	 * 2  mapid						char(12)	Three fields concatenated into AccountSite->SiteNumber
	 * 3  coop_id					int
	 * 4  cp_substation_id			int
	 * 5  lastname					varchar(32)
	 * 6  firstname					varchar(32)
	 * 7  middleinitial				char(1)		Append to first name if exists
	 * 8  reaclass					char(20)	CustomerAccount->Notes(?)
	 * 9  otherid					char(12)
	 * 10 account_number			char(16)	CustomerAccount->AccountNumber
	 * 11 service_feeder			char(2)		SiteInformation->Feeder
	 * 12 service_phase				char(3)		AccountSite->Notes(?)
	 * 13 service_sectiondevice		char(12)	AccountSite->Notes(?)
	 * 14 service_transformersize	char(12)	SiteInformation->TransformerSize
	 * 15 mail_address1				char(25)
	 * 16 mail_address2				char(25)
	 * 17 mail_city					char(20)
	 * 18 mail_state				char(2)
	 * 19 mail_zip					char(9)		Mail address saved to Address table referenced by CustomerAccount->BillingAddressID
	 * 20 site_address1				char(25)
	 * 21 site_address2				char(25)
	 * 22 site_city					char(20)
	 * 23 site_state				char(2)
	 * 24 site_zip					char(9)		Site address saved to Address table referenced by AccountSite->StreetAddressID
	 * 25 home_phone				char(12)	ContactNotification as home phone
	 * 26 site_phone				char(12)	ContactNotification as phone number
	 * 27 comment					char(255)	CustomerAccount->Notes
	 * Primary key: (mappage, mapsection, mapid, coop_id)
	 */
	private void importCustomer() throws Exception {
		File file = new File(importDir, "customer.out");
		if (!file.exists()) {
			errorMsg = "File \"customer.out\" not found!";
			throw new WebClientException( errorMsg );
		}
		
		Hashtable customerMap = getCustomerMap();
		
		File mapFile = new File(importDir, "_customer.map");
		PrintWriter fw = null;
		BufferedReader fr = null;
		
		DecimalFormat acctNoFormat = new DecimalFormat("00000000");
		long initAcctNo = getInitAccountNo();
		
		try {
			fw = new PrintWriter(new FileWriter(mapFile, true), true);
			fr = new BufferedReader(new FileReader(file));
			String line = fr.readLine();
			int lineNo = 0;
			
			while (line != null) {
				lineNo++;
				if (line.trim().length() == 0 || line.charAt(0) == '#') {
					line = fr.readLine();
					continue;
				}
				if (resumeFile == RESUME_FILE_CUSTOMER && lineNo < resumeLine) {
					line = fr.readLine();
					continue;
				}
				
				errorLocation = "\"customer.out\" line #" + lineNo;
				resumeLocation = "customer.out " + lineNo;
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
				
				String[] fields = StarsUtils.splitString( line, DELIM );
				
				//The comment field could take multiple lines
				while (true) {
					String nextLine = fr.readLine();
					if (nextLine == null || nextLine.indexOf(DELIM) >= 0) {
						line = nextLine;
						break;
					}
					else {
						lineNo++;
						fields[27] += NEW_LINE + nextLine.trim();
					}
				}
				
				Integer coopID = Integer.valueOf( fields[3].trim() );
				Integer memberID = (Integer) getCoopMap().get( coopID );
				if (memberID == null) {
					importLog.println(errorLocation + ": invalid member coop id \"" + coopID + "\", record ignored.");
					continue;
				}
				
				LiteStarsEnergyCompany member = StarsDatabaseCache.getInstance().getEnergyCompany( memberID.intValue() );
				
				String accountNo = (fields[10].trim().length() > 0)? fields[10].trim() : acctNoFormat.format(initAcctNo++);
				if (member.searchAccountByAccountNo(accountNo) != null) {
					importLog.println(errorLocation + ": account number \"" + accountNo + "\" already exists, record ignored.");
					continue;
				}
				
				StarsCustomerAccount account = new StarsCustomerAccount();
				account.setAccountNumber( accountNo );
				account.setIsCommercial( false );
				account.setCompany( "" );
				account.setPropertyNumber( fields[0].trim() + "-" + fields[1].trim() + "-" + fields[2].trim() );
				
				String acctNotes = "";
				if (fields[8].trim().length() > 0)
					acctNotes += "REA Class: " + fields[8].trim() + NEW_LINE;
				if (fields[27].trim().length() > 0)
					acctNotes += fields[27].trim();
				account.setAccountNotes( acctNotes );
				
				String propNotes = "";
				if (fields[12].trim().length() > 0)
					propNotes += "Service Phase: " + fields[12].trim() + NEW_LINE;
				if (fields[13].trim().length() > 0)
					propNotes += "Service Device: " + fields[13].trim();
				account.setPropertyNotes( propNotes );
				
				StreetAddress propAddr = new StreetAddress();
				propAddr.setStreetAddr1( fields[20].trim() );
				propAddr.setStreetAddr2( fields[21].trim() );
				propAddr.setCity( fields[22].trim() );
				propAddr.setState( fields[23].trim() );
				propAddr.setZip( fields[24].trim() );
				propAddr.setCounty( "" );
				account.setStreetAddress( propAddr );
				
				Substation starsSub = new Substation();
				if (fields[4].trim().length() > 0) {
					Integer subID = (Integer) getSubstationMap().get( Integer.valueOf(fields[4].trim()) );
					starsSub.setEntryID( subID.intValue() );
				}
				else
					starsSub.setEntryID( CtiUtilities.NONE_ZERO_ID );
					
				StarsSiteInformation siteInfo = new StarsSiteInformation();
				siteInfo.setSubstation( starsSub );
				siteInfo.setFeeder( fields[11].trim() );
				siteInfo.setPole( "" );
				siteInfo.setTransformerSize( fields[14].trim() );
				siteInfo.setServiceVoltage( "" );
				account.setStarsSiteInformation( siteInfo );
				
				BillingAddress billAddr = new BillingAddress();
				billAddr.setStreetAddr1( fields[15].trim() );
				billAddr.setStreetAddr2( fields[16].trim() );
				billAddr.setCity( fields[17].trim() );
				billAddr.setState( fields[18].trim() );
				billAddr.setZip( fields[19].trim() );
				billAddr.setCounty( "" );
				account.setBillingAddress( billAddr );
				
				PrimaryContact primContact = new PrimaryContact();
				primContact.setLastName( fields[5].trim() );
				
				String firstName = fields[6].trim();
				if (fields[7].trim().length() > 0)
					firstName += " " + fields[7].trim();
				primContact.setFirstName( firstName );
				
				try {
					ContactNotification homePhone = ServletUtils.createContactNotification(
							ServletUtils.formatPhoneNumber(fields[25].trim()), YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE );
					if (homePhone != null) primContact.addContactNotification( homePhone );
				}
				catch (WebClientException e) {
					CTILogger.error( e.getMessage() );
				}
				
				try {
					ContactNotification sitePhone = ServletUtils.createContactNotification(
							ServletUtils.formatPhoneNumber(fields[26].trim()), YukonListEntryTypes.YUK_ENTRY_ID_PHONE );
					if (sitePhone != null) primContact.addContactNotification( sitePhone );
				}
				catch (WebClientException e) {
					CTILogger.error( e.getMessage() );
				}
				
				account.setPrimaryContact( primContact );
				
				StarsNewCustomerAccount newAccount = new StarsNewCustomerAccount();
				newAccount.setStarsCustomerAccount( account );
				
				LiteStarsCustAccountInformation liteAcctInfo = NewCustAccountAction.newCustomerAccount(newAccount, member, false);
				numCustomerImported++;
				
				CustomerPK pk = new CustomerPK( fields[0].trim(), fields[1].trim(), fields[2].trim(), coopID.intValue() );
				customerMap.put( pk, new Integer(liteAcctInfo.getAccountID()) );
				fw.println( pk.toString() + "," + liteAcctInfo.getAccountID() );
			}
		}
		finally {
			if (fr != null) fr.close();
			if (fw != null) fw.close();
		}
	}
	
	/**
	 * Receiver file format:
	 * 0  receiver_serialnumber		int			LMHardwareBase->ManufacturerSerialNumber
	 * 1  primary_transmitter_id	char(8)
	 * 2  secondary_transmitter_id	char(8)		Map transmitters to route ID
	 * 3  mappage					char(5)
	 * 4  mapsection				char(5)
	 * 5  mapid						char(12)	Three fields concatenated into AccountSite->SiteNumber
	 * 6  coop_id					int
	 * 7  receiver_model			varchar(20)	InventoryBase->Notes
	 * 8  receiver_installdate		datetime	InventoryBase->InstallDate
	 * 9  receiver_codedate			datetime
	 * 10 receiver_codemodifieddate	datetime
	 * 11 receiver_function			int			LMHardwareConfiguration->LoadNumber
	 * 12 receiver_code_1			int
	 * 13 receiver_code_2			int
	 * 14 receiver_code_3			int
	 * 15 receiver_code_4			int
	 * 16 receiver_code_5			int
	 * 17 receiver_code_6			int			LMConfigurationSA205->Slot1-6
	 * 18 receiver_type				char(8)		LMHardwareBase->LMHardwareTypeID
	 * 19 receiver_clpcount			int			LMConfigurationBase->ColdLoadPickup
	 * 20 receiver_tdcount			int			LMConfigurationBase->TamperDetect
	 * 21 ovrtime					datetime
	 * 22 ovrhours					int
	 * 23 frequency					char(9)		InventoryBase->Notes
	 * 24 company_number			int			InventoryBase->AlternateTrackingNumber
	 * Primary key: receiver_serialnumber
	 */
	private void importReceiver() throws Exception {
		File file = new File(importDir, "receiver.out");
		if (!file.exists()) {
			errorMsg = "File \"receiver.out\" not found!";
			throw new WebClientException( errorMsg );
		}
		
		Hashtable receiverMap = getReceiverMap();
		
		File mapFile = new File(importDir, "_receiver.map");
		PrintWriter fw = null;
		BufferedReader fr = null;
		
		try {
			fw = new PrintWriter(new FileWriter(mapFile, true), true);
			fr = new BufferedReader(new FileReader(file));
			String line = null;;
			int lineNo = 0;
			
			while ((line = fr.readLine()) != null) {
				lineNo++;
				if (line.trim().length() == 0 || line.charAt(0) == '#')
					continue;
				if (resumeFile == RESUME_FILE_RECEIVER && lineNo < resumeLine)
					continue;
				
				errorLocation = "\"receiver.out\" line #" + lineNo;
				resumeLocation = "receiver.out " + lineNo;
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
				
				String[] fields = StarsUtils.splitString( line, DELIM );
				String serialNo = fields[0].trim();
				
				Integer coopID = Integer.valueOf( fields[6].trim() );
				Integer memberID = (Integer) getCoopMap().get( coopID );
				if (memberID == null) {
					importLog.println(errorLocation + ": invalid member coop id \"" + coopID + "\", receiver record ignored.");
					continue;
				}
				
				LiteStarsEnergyCompany member = StarsDatabaseCache.getInstance().getEnergyCompany( memberID.intValue() );
				
				ReceiverType rt = new ReceiverType( fields[18].trim(), fields[7].trim(), fields[23].trim() );
				Integer deviceTypeID = (Integer) getReceiverTypeMap().get( rt );
				if (deviceTypeID == null) {
					importLog.println(errorLocation + ": cannot find a matching device type for \"" + rt.toString() + "\", receiver record ignored.");
					continue;
				}
				
				StarsCreateLMHardware inv = (StarsCreateLMHardware) StarsFactory.newStarsInv(StarsCreateLMHardware.class);
				inv.setAltTrackingNumber( fields[24].trim() );
				if (fields[8].trim().length() > 0)
					inv.setInstallDate( dateParser.parse(fields[8].trim()) );
				
				DeviceType devType = new DeviceType();
				devType.setEntryID( deviceTypeID.intValue() );
				inv.setDeviceType( devType );
				
				LMHardware hw = new LMHardware();
				hw.setManufacturerSerialNumber( serialNo );
				inv.setLMHardware( hw );
				
				LiteStarsCustAccountInformation liteAcctInfo = null;
				CustomerPK pk = new CustomerPK( fields[3].trim(), fields[4].trim(), fields[5].trim(), coopID.intValue() );
				if (!(pk.mapid.equalsIgnoreCase("STOCK") || pk.mappage.equalsIgnoreCase("STOCK") || pk.mapsection.equalsIgnoreCase("STOCK"))) {
					Integer acctID = (Integer) getCustomerMap().get(pk);
					if (acctID != null)
						liteAcctInfo = member.getBriefCustAccountInfo( acctID.intValue(), true );
					else
						importLog.println(errorLocation + ": unable to find customer record for \"" + pk.toString() + "\", add receiver to the warehouse.");
				}
				
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) CreateLMHardwareAction.addInventory( inv, liteAcctInfo, member );
				
				int function = 1;
				if (fields[11].trim().length() > 0)
					function = Integer.parseInt( fields[11].trim() );
				int clpCount = 0;
				if (fields[19].trim().length() > 0)
					clpCount = Integer.parseInt( fields[19].trim() );
				int tdCount = 0;
				if (fields[20].trim().length() > 0)
					tdCount = Integer.parseInt( fields[20].trim() );
				
				StarsLMConfiguration starsCfg = new StarsLMConfiguration();
				if (clpCount != 0) {
					String[] clp = {"", "", "", ""};
					clp[function - 1] = String.valueOf(clpCount);
					starsCfg.setColdLoadPickup( clp[0] + "," + clp[1] + "," + clp[2] + "," + clp[3] );
				}
				if (tdCount != 0) {
					String[] td = {"", "", "", ""};
					td[function - 1] = String.valueOf(tdCount);
					starsCfg.setTamperDetect( td[0] + "," + td[1] + "," + td[2] + "," + td[3] );
				}
				
				int hwConfigType = InventoryUtils.getHardwareConfigType( deviceTypeID.intValue() );
				if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA205) {
					SA205 sa205 = new SA205();
					if (fields[12].trim().length() > 0)
						sa205.setSlot1( Integer.parseInt(fields[12].trim()) );
					if (fields[13].trim().length() > 0)
						sa205.setSlot2( Integer.parseInt(fields[13].trim()) );
					if (fields[14].trim().length() > 0)
						sa205.setSlot3( Integer.parseInt(fields[14].trim()) );
					if (fields[15].trim().length() > 0)
						sa205.setSlot4( Integer.parseInt(fields[15].trim()) );
					if (fields[16].trim().length() > 0)
						sa205.setSlot5( Integer.parseInt(fields[16].trim()) );
					if (fields[17].trim().length() > 0)
						sa205.setSlot6( Integer.parseInt(fields[17].trim()) );
					starsCfg.setSA205( sa205 );
				}
				else {
					SASimple simple = new SASimple();
					if (fields[12].trim().length() > 0)
						simple.setOperationalAddress( fields[12].trim() );
					starsCfg.setSASimple( simple );
				}
				
				UpdateLMHardwareConfigAction.updateLMConfiguration( starsCfg, liteHw, member );
				numReceiverImported++;
				
				int slotAddr5 = 0;
				if (starsCfg.getSA205() != null) slotAddr5 = starsCfg.getSA205().getSlot5();
				receiverMap.put( serialNo, new Integer[] {new Integer(liteHw.getInventoryID()), new Integer(slotAddr5)} );
				fw.println( serialNo + "," + liteHw.getInventoryID() + "," + slotAddr5 );
			}
		}
		finally {
			if (fr != null) fr.close();
			if (fw != null) fw.close();
		}
	}
	
	/**
	 * XXXtype file format:
	 * 0 XXXtype		char(?)		YukonListEntry->EntryText
	 * 1 XXXtype_id		int
	 * Includes backuptype, cooltype, primarytype
	 */
	private void importSelectionList() throws Exception {
		ArrayList coolTypeList = new ArrayList();
		ArrayList primaryTypeList = new ArrayList();
		ArrayList backupTypeList = new ArrayList();
		
		File file = new File(importDir, "cooltype.out");
		if (file.exists()) {
			String[] lines = StarsUtils.readFile(file, false);
			for (int i = 0; i < lines.length; i++) {
				String[] fields = StarsUtils.splitString( lines[i], DELIM );
				coolTypeList.add( fields[0].trim() );
			}
		}
		
		file = new File(importDir, "primarytype.out");
		if (file.exists()) {
			String[] lines = StarsUtils.readFile(file, false);
			for (int i = 0; i < lines.length; i++) {
				String[] fields = StarsUtils.splitString( lines[i], DELIM );
				primaryTypeList.add( fields[0].trim() );
			}
		}
		
		file = new File(importDir, "backuptype.out");
		if (file.exists()) {
			String[] lines = StarsUtils.readFile(file, false);
			for (int i = 0; i < lines.length; i++) {
				String[] fields = StarsUtils.splitString( lines[i], DELIM );
				backupTypeList.add( fields[0].trim() );
			}
		}
		
		file = new File(importDir, "controlledload.out");
		if (file.exists()) {
			BufferedReader fr = null;
			int lineNo = 0;
			
			try {
				fr = new BufferedReader(new FileReader(file));
				String line = null;;
				
				while ((line = fr.readLine()) != null) {
					lineNo++;
					if (line.trim().length() == 0 || line.charAt(0) == '#')
						continue;
					
					String[] fields = StarsUtils.splitString( line, DELIM );
					
					String coolType = fields[17].trim();
					if (coolType.length() > 0) {
						if (!coolTypeList.contains( coolType ))
							coolTypeList.add( coolType );
					}
					
					String primaryType = fields[13].trim();
					if (primaryType.length() > 0) {
						if (!primaryTypeList.contains( primaryType ))
							primaryTypeList.add( primaryType );
					}
					
					String backupType = fields[14].trim();
					if (backupType.length() > 0) {
						if (!backupTypeList.contains( backupType ))
							backupTypeList.add( backupType );
					}
				}
			}
			catch (Exception e) {
				if (lineNo > 0)
					errorMsg = "Error at \"controlledload.out\" line #" + lineNo;
				throw e;
			}
			finally {
				if (fr != null) fr.close();
			}
		}
		
		Collections.sort( coolTypeList );
		Collections.sort( primaryTypeList );
		Collections.sort( backupTypeList );
		
		Object[][] entryData = new Object[coolTypeList.size() + 1][3];
		entryData[0][0] = ZERO;
		entryData[0][1] = "";
		entryData[0][2] = ZERO;
		for (int i = 0; i < coolTypeList.size(); i++) {
			entryData[i+1][0] = ZERO;
			entryData[i+1][1] = coolTypeList.get(i);
			entryData[i+1][2] = ZERO;
		}
		
		YukonSelectionList list = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE );
		StarsAdminUtil.updateYukonListEntries( list, entryData, energyCompany );
		
//		entryData = new Object[primaryTypeList.size() + 1][3];
//		entryData[0][0] = ZERO;
//		entryData[0][1] = "";
//		entryData[0][2] = ZERO;
//		for (int i = 0; i < primaryTypeList.size(); i++) {
//			entryData[i+1][0] = ZERO;
//			entryData[i+1][1] = primaryTypeList.get(i);
//			entryData[i+1][2] = ZERO;
//		}
//		
//		list = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_ );
//		StarsAdminUtil.updateYukonListEntries( list, entryData, energyCompany );
		
		entryData = new Object[backupTypeList.size() + 1][3];
		entryData[0][0] = ZERO;
		entryData[0][1] = "";
		entryData[0][2] = ZERO;
		for (int i = 0; i < backupTypeList.size(); i++) {
			entryData[i+1][0] = ZERO;
			entryData[i+1][1] = backupTypeList.get(i);
			entryData[i+1][2] = ZERO;
		}
		
		list = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_DF_SECONDARY_SOURCE );
		StarsAdminUtil.updateYukonListEntries( list, entryData, energyCompany );
	}
	
	/**
	 * Controlledload file format:
	 * 0  receiver_serialnumber			int			LMHardwareBase->ManufacturerSerialNumber
	 * 1  load_id						int
	 * 2  mappage						char(5)
	 * 3  mapsection					char(5)
	 * 4  mapid							char(12)	Three fields concatenated into AccountSite->SiteNumber
	 * 5  coop_id						int
	 * 6  kwload						float		ApplianceBase->KWCapacity
	 * 7  summerdemand					float
	 * 8  installdate					datetime	ApplianceBase->Notes
	 * 9  annualkwh						float
	 * 10 winterdemand					float
	 * 11 heatpumpload					int
	 * 12 dualfuel_annualusage			int
	 * 13 dualfuel_primarytype			char(20)	ApplianceBase->Notes (new field in ApplianceDualFuel)
	 * 14 dualfuel_backuptype			char(12)	ApplianceDualFuel->SecondaryEnergySourceID
	 * 15 dualfuel_backupcapacity		int			ApplianceDualFuel->SecondaryKWCapacity
	 * 16 controltype					char(9)
	 * 17 cooltype						char(5)		ApplianceAirConditioner->TypeID
	 * 18 heatgain						float
	 * 19 waterheater_controltype		char(25)
	 * 20 waterheater_insulationtype	char(12)	ApplianceBase->Notes
	 * 21 waterheater_heattrap			char(1)
	 * 22 waterheater_capacity			int			ApplianceWaterHeater->NumberofGallonsID
	 * 23 lowflowrate					int
	 * 24 preinstallrate				int
	 * 25 lowflowinstalldate			datetime
	 * 26 waterheater_type				char(12)
	 * 27 rebate_status					char(1)
	 * 28 rebate_status_date			datetime
	 * 29 rebate_amount					float
	 * Primary key: (receiver_serialnumber, load_id, mappage, mapsection, mapid, coop_id, installdate)
	 * Load type specific information:
	 * 11-15: Space Heating
	 * 16-18: Cooling
	 * 19-26: Water Heater
	 * Other: General
	 */
	private void importControlledLoad() throws Exception {
		File file = new File(importDir, "controlledload.out");
		if (!file.exists()) {
			errorMsg = "File \"controlledload.out\" not found!";
			throw new WebClientException( errorMsg );
		}
		
		YukonSelectionList coolTypeList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE );
		YukonSelectionList backupTypeList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_DF_SECONDARY_SOURCE );
		YukonSelectionList whCapacityList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_WH_NUM_OF_GALLONS );
		
		LiteApplianceCategory appCatDft = null;
		LiteApplianceCategory appCatAC = null;
		LiteApplianceCategory appCatWH = null;
		LiteApplianceCategory appCatDF = null;
		LiteApplianceCategory appCatSH = null;
		
		for (int i = 0; i < energyCompany.getApplianceCategories().size(); i++) {
			LiteApplianceCategory appCat = (LiteApplianceCategory) energyCompany.getApplianceCategories().get(i);
			int appCatDefID = YukonListFuncs.getYukonListEntry( appCat.getCategoryID() ).getYukonDefID();
			if (appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DEFAULT)
				appCatDft = appCat;
			else if (appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER)
				appCatAC = appCat;
			else if (appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER)
				appCatWH = appCat;
			else if (appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL)
				appCatDF = appCat;
			else if (appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT)
				appCatSH = appCat;
		}
		
		BufferedReader fr = null;
		
		try {
			fr = new BufferedReader(new FileReader(file));
			String line = null;;
			int lineNo = 0;
			
			while ((line = fr.readLine()) != null) {
				lineNo++;
				if (line.trim().length() == 0 || line.charAt(0) == '#')
					continue;
				if (resumeFile == RESUME_FILE_CONTROLLED_LOAD && lineNo < resumeLine)
					continue;
				
				errorLocation = "\"controlledload.out\" line #" + lineNo;
				resumeLocation = "controlledload.out " + lineNo;
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
				
				String[] fields = StarsUtils.splitString( line, DELIM );
				String coolType = fields[17].trim();
				String primaryType = fields[13].trim();
				String whType = fields[26].trim();
				
				Integer coopID = Integer.valueOf( fields[5].trim() );
				Integer memberID = (Integer) getCoopMap().get( coopID );
				if (memberID == null) {
					importLog.println(errorLocation + ": invalid member coop id \"" + coopID + "\", load record ignored.");
					continue;
				}
				
				LiteStarsEnergyCompany member = StarsDatabaseCache.getInstance().getEnergyCompany( memberID.intValue() );
				
				CustomerPK pk = new CustomerPK( fields[2].trim(), fields[3].trim(), fields[4].trim(), coopID.intValue() );
				Integer acctID = (Integer) getCustomerMap().get(pk);
				if (acctID == null) {
					importLog.println(errorLocation + ": unable to find customer record \"" + pk + "\", load record ignored.");
					continue;
				}
				
				LiteStarsCustAccountInformation liteAcctInfo = member.getBriefCustAccountInfo( acctID.intValue(), true );
				
				StarsCreateAppliance app = new StarsCreateAppliance();
				app.setApplianceCategoryID( appCatDft.getApplianceCategoryID() );
				app.setModelNumber( "" );
				app.setEfficiencyRating( -1 );
				
				if (fields[6].trim().length() > 0)
					app.setKWCapacity( Math.round(Float.parseFloat(fields[6].trim())) );
				
				Location location = new Location();
				location.setEntryID( CtiUtilities.NONE_ZERO_ID );
				app.setLocation( location );
				
				Manufacturer mfc = new Manufacturer();
				mfc.setEntryID( CtiUtilities.NONE_ZERO_ID );
				app.setManufacturer( mfc );
				
				String notes = "";
				if (fields[8].trim().length() > 0)
					notes += "Install date: " + dateFormat.format(dateParser.parse(fields[8].trim())) + NEW_LINE;
				
				Integer[] receiverInfo = (Integer[]) getReceiverMap().get( fields[0].trim() );
				String loadType = null;
				
				if (coolType.length() > 0) {
					AirConditioner ac = new AirConditioner();
					ac.setTonnage( new Tonnage() );
					
					ACType type = new ACType();
					YukonListEntry acType = YukonListFuncs.getYukonListEntry(coolTypeList, coolType);
					if (acType != null) type.setEntryID( acType.getEntryID() );
					ac.setACType( type );
					
					app.setAirConditioner( ac );
					app.setApplianceCategoryID( appCatAC.getApplianceCategoryID() );
					
					if (fields[16].trim().length() > 0)
						notes += "Control type: " + fields[16].trim();
					loadType = "Cooling";
				}
				else if (primaryType.length() > 0) {
					int appCatID = primaryType.equalsIgnoreCase("Storage")?
						appCatSH.getApplianceCategoryID() : appCatDF.getApplianceCategoryID();
					if (receiverInfo != null) {
						// Test if our "guessed" appliance category (SH or DF) is correct or not
						// by looking for a matching program in the function table.
						try {
							if (getMatchingProgram(receiverInfo[1], appCatID) == null) {
								int appCatID2 = (appCatID == appCatSH.getApplianceCategoryID())?
									appCatDF.getApplianceCategoryID() : appCatSH.getApplianceCategoryID();
								if (getMatchingProgram(receiverInfo[1], appCatID2) != null)
									appCatID = appCatID2;
							}
						}
						catch (WebClientException e) {}
					}
					
					app.setApplianceCategoryID( appCatID );
					notes += "Primary type: " + primaryType;
					
					if (appCatID == appCatSH.getApplianceCategoryID()) {
						StorageHeat sh = new StorageHeat();
						sh.setStorageType( new StorageType() );
						app.setStorageHeat( sh );
						
						loadType = "Space Heating (Storage Heat)";
					}
					else {
						DualFuel df = new DualFuel();
						df.setSwitchOverType( new SwitchOverType() );
						
						SecondaryEnergySource energySrc = new SecondaryEnergySource();
						if (fields[14].trim().length() > 0) {
							YukonListEntry es = YukonListFuncs.getYukonListEntry(backupTypeList, fields[14].trim());
							if (es != null) energySrc.setEntryID( es.getEntryID() );
						}
						df.setSecondaryEnergySource( energySrc );
						
						if (fields[15].trim().length() > 0)
							df.setSecondaryKWCapacity( Integer.parseInt(fields[15].trim()) );
						app.setDualFuel( df );
						
						loadType = "Space Heating (Dual Fuel)";
					}
				}
				else if (whType.length() > 0) {
					WaterHeater wh = new WaterHeater();
					wh.setEnergySource( new EnergySource() );
					
					NumberOfGallons numGal = new NumberOfGallons();
					if (fields[22].trim().length() > 0) {
						// Try to find the best matching in the WH capacity list. The list entries should look like:
						// "", "Less than 30", "30", "40", "50", "85", "105", "Greater than 105"
						int numGallons = Integer.parseInt( fields[22].trim() );
						if (numGallons < 25)
							numGal.setEntryID( ((YukonListEntry)whCapacityList.getYukonListEntries().get(1)).getEntryID() );
						else if (numGallons >= 25 && numGallons < 35)
							numGal.setEntryID( ((YukonListEntry)whCapacityList.getYukonListEntries().get(2)).getEntryID() );
						else if (numGallons >= 35 && numGallons < 45)
							numGal.setEntryID( ((YukonListEntry)whCapacityList.getYukonListEntries().get(3)).getEntryID() );
						else if (numGallons >= 45 && numGallons < 73)
							numGal.setEntryID( ((YukonListEntry)whCapacityList.getYukonListEntries().get(4)).getEntryID() );
						else if (numGallons >= 73 && numGallons < 95)
							numGal.setEntryID( ((YukonListEntry)whCapacityList.getYukonListEntries().get(5)).getEntryID() );
						else if (numGallons >= 95 && numGallons < 120)
							numGal.setEntryID( ((YukonListEntry)whCapacityList.getYukonListEntries().get(6)).getEntryID() );
						else
							numGal.setEntryID( ((YukonListEntry)whCapacityList.getYukonListEntries().get(7)).getEntryID() );
					}
					wh.setNumberOfGallons( numGal );
					
					app.setWaterHeater( wh );
					app.setApplianceCategoryID( appCatWH.getApplianceCategoryID() );
					
					if (fields[19].trim().length() > 0)
						notes += "Control type: " + fields[19].trim() + NEW_LINE;
					if (fields[20].trim().length() > 0)
						notes += "Insulation type: " + fields[20].trim();
					loadType = "Water Heater";
				}
				else {
					Object[] loadTypeInfo = null;
					try {
						Integer loadID = Integer.valueOf( fields[1].trim() );
						loadTypeInfo = (Object[]) getLoadTypeMap().get( loadID );
					}
					catch (NumberFormatException e) {}
					
					if (loadType == null) {
						String msg = "unknown load type \"" + fields[1].trim() + "\" under receiver \"" + fields[0].trim() + "\"";
						if (receiverInfo != null)
							msg += ", code \"" + receiverInfo[1] + "\"";
						importLog.println(errorLocation + ": " + msg + ", load record ignored.");
						continue;
					}
					
					int appCatID = ((Integer)loadTypeInfo[1]).intValue();
					app.setApplianceCategoryID( appCatID );
					LiteApplianceCategory liteAppCat = member.getApplianceCategory( appCatID );
					
					notes += "Load type: " + loadTypeInfo[0];
					loadType = (String) loadTypeInfo[0];
				}
				
				app.setNotes( notes );
				
				if (receiverInfo != null) {
					try {
						int[] programInfo = getMatchingProgram( receiverInfo[1], app.getApplianceCategoryID() );
						if (programInfo != null) {
//							String[] functions = (String[]) getFunctionTable().get( receiverInfo[1] );
//							importLog.println(errorLocation + ": load type \"" + loadType + "\" matches function #" + programInfo[1] + " \"" + functions[programInfo[1] - 1] + "\" under code \"" + receiverInfo[1] + "\"");
							app.setInventoryID( ((Integer)receiverInfo[0]).intValue() );
							app.setProgramID( programInfo[0] );
							app.setLoadNumber( programInfo[1] );
						}
						else {
							importLog.println(errorLocation + ": load type \"" + loadType + "\" doesn't match any function under code \"" + receiverInfo[1] + "\", program enrollment skipped.");
						}
					}
					catch (WebClientException e) {
						importLog.println(errorLocation + ": " + e.getMessage());
					}
				}
				else {
					importLog.println(errorLocation + ": unable to find receiver \"" + fields[0].trim() + "\", program enrollment skipped.");
				}
				
				LiteStarsAppliance liteApp = CreateApplianceAction.createAppliance( app, liteAcctInfo, member );
				if (liteApp.getProgramID() > 0) member.reloadCustAccountInformation( liteAcctInfo );
				numCtrlLoadImported++;
			}
			
			errorLocation = null;
			resumeLocation = null;
		}
		finally {
			if (fr != null) fr.close();
		}
	}
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Required parameters: EnergyCompanyID ImportDir");
			return;
		}
		
//		try {
//			ImportDSMDataTask.generateConfigFiles( new File(args[1]) );
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
		int energyCompanyID = Integer.parseInt(args[0]);
		ImportDSMDataTask task = new ImportDSMDataTask( StarsDatabaseCache.getInstance().getEnergyCompany(energyCompanyID), new File(args[1]) );
		task.run();
	}

}
