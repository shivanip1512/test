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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;

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
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.action.CreateApplianceAction;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.NewCustAccountAction;
import com.cannontech.stars.web.action.ProgramSignUpAction;
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
import com.cannontech.stars.xml.serialize.SULMProgram;
import com.cannontech.stars.xml.serialize.SecondaryEnergySource;
import com.cannontech.stars.xml.serialize.StarsCreateAppliance;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.stars.xml.serialize.StarsNewCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsProgramSignUp;
import com.cannontech.stars.xml.serialize.StarsSULMPrograms;
import com.cannontech.stars.xml.serialize.StarsSiteInformation;
import com.cannontech.stars.xml.serialize.StorageHeat;
import com.cannontech.stars.xml.serialize.StorageType;
import com.cannontech.stars.xml.serialize.StreetAddress;
import com.cannontech.stars.xml.serialize.Substation;
import com.cannontech.stars.xml.serialize.SwitchOverType;
import com.cannontech.stars.xml.serialize.Tonnage;
import com.cannontech.stars.xml.serialize.WaterHeater;
import com.cannontech.user.UserUtils;

class ReceiverType {
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
	
	private static final int RESUME_FILE_COOP = 1;
	private static final int RESUME_FILE_SUBSTATION = 2;
	private static final int RESUME_FILE_CUSTOMER = 3;
	private static final int RESUME_FILE_RECEIVER = 4;
	private static final int RESUME_FILE_CONTROLLED_LOAD = 5;
	
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
	
	private Hashtable coopMap = null;
	private Hashtable substationMap = null;
	private Hashtable customerMap = null;
	private Hashtable receiverMap = null;
	private Hashtable routeMap = null;
	private Hashtable receiverTypeMap = null;
	
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
		else if (status == STATUS_ERROR) {
			String msg = getImportProgress();
			if (msg.length() == 0) msg = null;
			return msg;
		}
		else {
			if (progressMsg != null)
				return progressMsg + NEW_LINE + NEW_LINE + getImportProgress();
			else
				return "Converting DSM database...";
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
				importSubstation();
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					throw new WebClientException();
				}
			}
			
			if (resumeFile <= RESUME_FILE_CUSTOMER) {
				progressMsg = "Converting customers...";
				importCustomer();
			}
			
			if (resumeFile <= RESUME_FILE_RECEIVER) {
				progressMsg = "Converting receivers...";
				importReceiver();
			}
			
			if (resumeFile <= RESUME_FILE_CONTROLLED_LOAD) {
				if (resumeFile < RESUME_FILE_CONTROLLED_LOAD || resumeLine == 0) {
					progressMsg = "Converting selection lists...";
					importSelectionList();
					
					if (isCanceled) {
						status = STATUS_CANCELED;
						throw new WebClientException();
					}
				}
				
				progressMsg = "Converting controlled load...";
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
			status = STATUS_ERROR;
			
			CTILogger.error( e.getMessage(), e );
			if (status == STATUS_CANCELED) {
				errorMsg = "Operation is canceled by user";
				if (errorLocation != null)
					errorMsg += " before processing " + errorLocation;
				else if (resumeLocation != null)
					errorMsg += " before processing " + resumeLocation;
			}
			else {
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
	 *     "_route.map", "_receivertype.map"
	 */
	public static void generateConfigFiles(File importDir) throws Exception {
		File file = new File(importDir, "coop.out");
		if (!file.exists())
			throw new WebClientException( "File \"coop.out\" not found!" );
		
		TreeMap coopMap = new TreeMap();
		String[] lines = StarsUtils.readFile( file, false );
		for (int i = 0; i < lines.length; i++) {
			String[] fields = StarsUtils.splitString( lines[i], DELIM );
			Integer coopID = Integer.valueOf( fields[1].trim() );
			coopMap.put( coopID, fields[0].trim() );
		}
		
		PrintWriter fw = null;
		try {
			fw = new PrintWriter(new FileWriter(new File(importDir, "_route.map")));
			fw.println("# This file defines the mapping from coops to their assigned route macros.");
			fw.println("# Complete the file by adding the route name to the end of each line.");
			
			Iterator it = coopMap.keySet().iterator();
			while (it.hasNext()) {
				Integer coopID = (Integer) it.next();
				String coopName = (String) coopMap.get( coopID );
				fw.println( coopID + "," + coopName + "," );
			}
		}
		finally {
			if (fw != null) fw.close();
		}
		
		file = new File(importDir, "receiver.out");
		if (!file.exists())
			throw new WebClientException( "File \"receiver.out\" not found!" );
		
		BufferedReader fr = null;
		HashSet recvTypeSet = new HashSet();
		
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
			fw = new PrintWriter(new FileWriter(new File(importDir, "_receivertype.map")));
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
					receiverMap.put( fields[0], Integer.valueOf(fields[1]) );
				}
			}
		}
		return receiverMap;
	}
	
	private Hashtable getRouteMap() throws Exception {
		if (routeMap == null) {
			routeMap = new Hashtable();
			LiteYukonPAObject[] liteRoutes = PAOFuncs.getAllLiteRoutes();
			
			File file = new File(importDir, "_route.map");
			String[] lines = StarsUtils.readFile( file, false );
			
			for (int i = 0; i < lines.length; i++) {
				String[] fields = StarsUtils.splitString( lines[i], "," );
				
				Integer routeID = null;
				if (fields[2].length() > 0) {
					for (int j = 0; j < liteRoutes.length; j++) {
						if (liteRoutes[j].getPaoName().equalsIgnoreCase( fields[2] )) {
							routeID = new Integer(liteRoutes[j].getYukonID());
							break;
						}
					}
					if (routeID == null)
						throw new WebClientException( "Route \"" + fields[2] + "\" was not found in Yukon" );
				}
				
				if (routeID != null)
					routeMap.put( Integer.valueOf(fields[0]), routeID );
			}
		}
		return routeMap;
	}
	
	private Hashtable getReceiverTypeMap() throws Exception {
		if (receiverTypeMap == null) {
			receiverTypeMap = new Hashtable();
			
			File file = new File(importDir, "_receivertype.map");
			String[] lines = StarsUtils.readFile( file, false );
			
			for (int i = 0; i < lines.length; i++) {
				String[] fields = StarsUtils.splitString( lines[i], "," );
				
				Integer devTypeID = null;
				if (fields[3].length() > 0) {
					YukonSelectionList list = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
					int entryID = getListEntryID( list, fields[3] );
					if (entryID == 0)
						throw new WebClientException( "Device type \"" + fields[3] + "\" was undefined in STARS" );
					devTypeID = new Integer(entryID);
				}
				
				if (devTypeID != null) {
					ReceiverType rt = new ReceiverType( fields[0], fields[1], fields[2] );
					receiverTypeMap.put( rt, devTypeID );
				}
			}
		}
		return receiverTypeMap;
	}
	
	private int getListEntryID(YukonSelectionList list, String entryText) {
		for (int i = 0; i < list.getYukonListEntries().size(); i++) {
			YukonListEntry entry = (YukonListEntry) list.getYukonListEntries().get(i);
			if (entry.getEntryText().equals( entryText ))
				return entry.getEntryID();
		}
		return 0;
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
				rolePropMap.put( new Integer(AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY), CtiUtilities.TRUE_STRING );
				// For testing, turn on the "delete energy company" property
				rolePropMap.put( new Integer(AdministratorRole.ADMIN_DELETE_ENERGY_COMPANY), CtiUtilities.TRUE_STRING );
				
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
				
				coopMap.put( coopID, member.getEnergyCompanyID() );
				fw.println( coopID + "," + member.getEnergyCompanyID() );
				
				numCoopImported++;
			}
			
			errorLocation = null;
			resumeLocation = "substation.out";
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
				
				substationMap.put( subID, new Integer(sub.getSubstationID()) );
				fw.println( subID + "," + sub.getSubstationID() );
				
				numSubstationImported++;
			}
			
			errorLocation = null;
			resumeLocation = "customer.out";
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
						fields[27] += NEW_LINE + nextLine;
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
				
				LiteStarsCustAccountInformation liteAcctInfo = NewCustAccountAction.newCustomerAccount(newAccount, member);
				
				CustomerPK pk = new CustomerPK( fields[0].trim(), fields[1].trim(), fields[2].trim(), coopID.intValue() );
				customerMap.put( pk, new Integer(liteAcctInfo.getAccountID()) );
				fw.println( pk.toString() + "," + liteAcctInfo.getAccountID() );
				
				numCustomerImported++;
			}
			
			errorLocation = null;
			resumeLocation = "receiver.out";
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
					importLog.println(errorLocation + ": invalid member coop id \"" + coopID + "\", record ignored.");
					continue;
				}
				
				LiteStarsEnergyCompany member = StarsDatabaseCache.getInstance().getEnergyCompany( memberID.intValue() );
				
				ReceiverType rt = new ReceiverType( fields[18].trim(), fields[7].trim(), fields[23].trim() );
				Integer deviceTypeID = (Integer) getReceiverTypeMap().get( rt );
				if (deviceTypeID == null) {
					importLog.println(errorLocation + ": cannot find a matching device type for \"" + rt.toString() + "\", receiver is skipped.");
					continue;
				}
				
				StarsCreateLMHardware inv = (StarsCreateLMHardware) StarsFactory.newStarsInv(StarsCreateLMHardware.class);
				inv.setAltTrackingNumber( fields[24].trim() );
				inv.setNotes( rt.toString() );
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
				
				receiverMap.put( serialNo, new Integer(liteHw.getInventoryID()) );
				fw.println( serialNo + "," + liteHw.getInventoryID() );
				
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
				
				UpdateLMHardwareConfigAction.updateLMConfiguration( starsCfg, liteHw, member );
				
				numReceiverImported++;
			}
			
			errorLocation = null;
			resumeLocation = "controlledload.out";
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
		ArrayList whCapacityList = new ArrayList();
		
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
					
					if (fields[22].trim().length() > 0) {
						Integer whCapacity = Integer.valueOf( fields[22].trim() );
						if (!whCapacityList.contains( whCapacity ))
							whCapacityList.add( whCapacity );
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
		Collections.sort( whCapacityList );
		
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
		
		entryData = new Object[whCapacityList.size() + 1][3];
		entryData[0][0] = ZERO;
		entryData[0][1] = "";
		entryData[0][2] = ZERO;
		for (int i = 0; i < whCapacityList.size(); i++) {
			entryData[i+1][0] = ZERO;
			entryData[i+1][1] = whCapacityList.get(i).toString();
			entryData[i+1][2] = ZERO;
		}
		
		list = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_WH_NUM_OF_GALLONS );
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
	 * 16 controltype					char(9)		Maps to LM programs
	 * 17 cooltype						char(5)		ApplianceAirConditioner->TypeID
	 * 18 heatgain						float
	 * 19 waterheater_controltype		char(25)	Maps to LM programs
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
		
		int appCatDftID = 0;
		LiteApplianceCategory appCatAC = null;
		LiteApplianceCategory appCatWH = null;
		LiteApplianceCategory appCatDF = null;
		LiteApplianceCategory appCatSH = null;
		LiteApplianceCategory appCatIrr = null;
		
		for (int i = 0; i < energyCompany.getApplianceCategories().size(); i++) {
			LiteApplianceCategory appCat = (LiteApplianceCategory) energyCompany.getApplianceCategories().get(i);
			int appCatDefID = YukonListFuncs.getYukonListEntry( appCat.getCategoryID() ).getYukonDefID();
			if (appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DEFAULT)
				appCatDftID = appCat.getApplianceCategoryID();
			else if (appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER)
				appCatAC = appCat;
			else if (appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER)
				appCatWH = appCat;
			else if (appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL)
				appCatDF = appCat;
			else if (appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT)
				appCatSH = appCat;
			else if (appCatDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION)
				appCatIrr = appCat;
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
					importLog.println(errorLocation + ": invalid member coop id \"" + coopID + "\", record ignored.");
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
				int progID = 0;
				
				StarsCreateAppliance app = new StarsCreateAppliance();
				app.setApplianceCategoryID( appCatDftID );
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
				
				if (coolType.length() > 0) {
					AirConditioner ac = new AirConditioner();
					ac.setTonnage( new Tonnage() );
					
					ACType type = new ACType();
					type.setEntryID( getListEntryID(coolTypeList, coolType) );
					ac.setACType( type );
					
					app.setAirConditioner( ac );
					app.setApplianceCategoryID( appCatAC.getApplianceCategoryID() );
					
					if (fields[16].trim().length() > 0) {
						for (int i = 0; i < appCatAC.getPublishedPrograms().size(); i++) {
							LiteLMProgramWebPublishing prog = (LiteLMProgramWebPublishing) appCatAC.getPublishedPrograms().get(i);
							String progName = StarsUtils.getPublishedProgramName( prog );
							if (progName.equalsIgnoreCase( fields[16].trim() )) {
								progID = prog.getProgramID();
								break;
							}
						}
					}
				}
				else if (primaryType.length() > 0) {
					if (primaryType.equalsIgnoreCase("Storage")) {
						StorageHeat sh = new StorageHeat();
						sh.setStorageType( new StorageType() );
						app.setStorageHeat( sh );
						app.setApplianceCategoryID( appCatSH.getApplianceCategoryID() );
						
						if (appCatSH.getPublishedPrograms().size() > 0)
							progID = ((LiteLMProgramWebPublishing) appCatSH.getPublishedPrograms().get(0)).getProgramID();
					}
					else {
						DualFuel df = new DualFuel();
						df.setSwitchOverType( new SwitchOverType() );
						
						SecondaryEnergySource energySrc = new SecondaryEnergySource();
						if (fields[14].trim().length() > 0)
							energySrc.setEntryID( getListEntryID(backupTypeList, fields[14].trim()) );
						df.setSecondaryEnergySource( energySrc );
						
						if (fields[15].trim().length() > 0)
							df.setSecondaryKWCapacity( Integer.parseInt(fields[15].trim()) );
						
						app.setDualFuel( df );
						app.setApplianceCategoryID( appCatDF.getApplianceCategoryID() );
						
						notes += "Primary type: " + primaryType;
						
						if (appCatDF.getPublishedPrograms().size() > 0)
							progID = ((LiteLMProgramWebPublishing) appCatDF.getPublishedPrograms().get(0)).getProgramID();
					}
				}
				else if (whType.length() > 0) {
					WaterHeater wh = new WaterHeater();
					wh.setEnergySource( new EnergySource() );
					
					NumberOfGallons numGal = new NumberOfGallons();
					if (fields[22].trim().length() > 0)
						numGal.setEntryID( getListEntryID(whCapacityList, fields[22].trim()) );
					wh.setNumberOfGallons( numGal );
					
					app.setWaterHeater( wh );
					app.setApplianceCategoryID( appCatWH.getApplianceCategoryID() );
					
					if (fields[20].trim().length() > 0)
						notes += "Insulation type: " + fields[20].trim();
					
					if (fields[19].trim().length() > 0) {
						for (int i = 0; i < appCatWH.getPublishedPrograms().size(); i++) {
							LiteLMProgramWebPublishing prog = (LiteLMProgramWebPublishing) appCatWH.getPublishedPrograms().get(i);
							String progName = StarsUtils.getPublishedProgramName( prog );
							if (progName.equalsIgnoreCase( fields[19].trim() )) {
								progID = prog.getProgramID();
								break;
							}
						}
					}
				}
				else {
					importLog.println(errorLocation + ": unknown load type, assign to the generic appliance category.");
				}
				
				app.setNotes( notes );
				
				LiteStarsAppliance liteApp = CreateApplianceAction.createAppliance( app, liteAcctInfo, member );
				
				if (progID > 0) {
					Integer invID = (Integer) getReceiverMap().get( fields[0].trim() );
					if (invID == null) {
						importLog.println(errorLocation + ": unable to find receiver \"" + fields[0].trim() + "\", program enrollment skipped.");
						continue;
					}
					
					LiteStarsLMHardware liteHw = (LiteStarsLMHardware) member.getInventory( invID.intValue(), true );
					
					SULMProgram suProg = new SULMProgram();
					suProg.setProgramID( progID );
					suProg.setApplianceCategoryID( liteApp.getApplianceCategoryID() );
					StarsSULMPrograms suPrograms = new StarsSULMPrograms();
					suPrograms.addSULMProgram( suProg );
					StarsProgramSignUp progSignUp = new StarsProgramSignUp();
					progSignUp.setStarsSULMPrograms( suPrograms );
					
					ProgramSignUpAction.updateProgramEnrollment( progSignUp, liteAcctInfo, liteHw, member );
				}
				
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
