package com.cannontech.database.data.lite.stars;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonListFuncs;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.stars.appliance.ApplianceAirConditioner;
import com.cannontech.database.db.stars.appliance.ApplianceDualFuel;
import com.cannontech.database.db.stars.appliance.ApplianceGenerator;
import com.cannontech.database.db.stars.appliance.ApplianceGrainDryer;
import com.cannontech.database.db.stars.appliance.ApplianceHeatPump;
import com.cannontech.database.db.stars.appliance.ApplianceIrrigation;
import com.cannontech.database.db.stars.appliance.ApplianceStorageHeat;
import com.cannontech.database.db.stars.appliance.ApplianceWaterHeater;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.operator.OddsForControlRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.util.OptOutEventQueue;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCallReport;
import com.cannontech.stars.xml.serialize.ControlHistory;
import com.cannontech.stars.xml.serialize.ControlSummary;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsCustomerFAQs;
import com.cannontech.stars.xml.serialize.StarsCustomerSelectionLists;
import com.cannontech.stars.xml.serialize.StarsDefaultThermostatSettings;
import com.cannontech.stars.xml.serialize.StarsEnergyCompany;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestion;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestions;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
import com.cannontech.stars.xml.serialize.StarsServiceCompanies;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.stars.xml.serialize.StarsThermostatSeason;
import com.cannontech.stars.xml.serialize.StarsThermostatSettings;
import com.cannontech.stars.xml.serialize.StarsWebConfig;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
import com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteStarsEnergyCompany extends LiteBase {
	
	public static final int FAKE_LIST_ID = -9999;	// Magic number for YukonSelectionList ID, used for substation and service company list
	public static final int INVALID_ROUTE_ID = -1;	// Mark that a valid default route id is not found, and prevent futher attempts
	
	private String name = null;
	private int primaryContactID = CtiUtilities.NONE_ID;
	private int userID = com.cannontech.user.UserUtils.USER_YUKON_ID;
	
	private ArrayList custAccountInfos = null;	// List of LiteStarsCustAccountInformation
	private ArrayList customerContacts = null;	// List of LiteCustomerContact
	private ArrayList addresses = null;			// List of LiteAddress
	private ArrayList lmPrograms = null;			// List of LiteLMProgram
	private ArrayList lmHardwares = null;			// List of LiteLMHardwareBase
	private ArrayList lmCtrlHists = null;			// List of LiteStarsLMControlHistory
	private ArrayList appCategories = null;		// List of LiteApplianceCategory
	private ArrayList workOrders = null;			// List of LiteWorkOrderBase
	private ArrayList serviceCompanies = null;	// List of LiteServiceCompany
	private ArrayList selectionLists = null;		// List of YukonSelectionList
	private ArrayList interviewQuestions = null;	// List of LiteInterviewQuestion
	private ArrayList customerFAQs = null;		// List of LiteCustomerFAQ
	private LiteStarsThermostatSettings dftThermSettings = null;
	
	private Object[][] thermModeSettings = null;	// Map between webConfigurationID(Integer) and StarsThermoModeSettings
	private int nextCallNo = 0;
	private int nextOrderNo = 0;
	
	private int dftRouteID = CtiUtilities.NONE_ID;
	private OptOutEventQueue optOutEventQueue = null;
	private ArrayList accountsWithGatewayEndDevice = null;	// List of LiteStarsCustAccountInformation
	
	
	// Cached XML messages
	private StarsEnergyCompany starsEnergyCompany = null;
	private StarsEnrollmentPrograms starsEnrPrograms = null;
	private StarsCustomerFAQs starsCustFAQs = null;
	private StarsServiceCompanies starsServCompanies = null;
	private StarsExitInterviewQuestions starsExitQuestions = null;
	private StarsDefaultThermostatSettings starsThermSettings = null;
	private Hashtable starsCustSelLists = null;	// Map String(list name) to StarsSelectionListEntry
	private Hashtable starsWebConfigs = null;		// Map Integer(web config ID) to StarsWebConfig
	private Hashtable starsCustAcctInfos = null;	// Map Integer(account ID) to StarsCustAccountInformation
	private Hashtable starsLMCtrlHists = null;	// Map Integer(group ID) to StarsLMControlHistory
	
	
	public LiteStarsEnergyCompany() {
		super();
		setLiteType( LiteTypes.ENERGY_COMPANY );
	}
	
	public LiteStarsEnergyCompany(int companyID) {
		super();
		setLiteID( companyID );
		setLiteType( LiteTypes.ENERGY_COMPANY );
	}
	
	public LiteStarsEnergyCompany(com.cannontech.database.db.company.EnergyCompany energyCompany) {
		super();
		setLiteType( LiteTypes.ENERGY_COMPANY );
		setLiteID( energyCompany.getEnergyCompanyID().intValue() );
		setName( energyCompany.getName() );
		setPrimaryContactID( energyCompany.getPrimaryContactID().intValue() );
		setUserID( energyCompany.getUserID().intValue() );
	}
	
	public Integer getEnergyCompanyID() {
		return new Integer( getLiteID() );
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the primaryContactID.
	 * @return int
	 */
	public int getPrimaryContactID() {
		return primaryContactID;
	}

	/**
	 * Sets the primaryContactID.
	 * @param primaryContactID The primaryContactID to set
	 */
	public void setPrimaryContactID(int primaryContactID) {
		this.primaryContactID = primaryContactID;
	}

	/**
	 * Returns the userID.
	 * @return int
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * Sets the userID.
	 * @param userID The userID to set
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}

	
	public int getDefaultRouteID() {
		if (dftRouteID == INVALID_ROUTE_ID) return dftRouteID;
		
		if (dftRouteID == CtiUtilities.NONE_ID) {
			String dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();
			
		    String sql = "select GENERICMACRO.CHILDID from OPERATORSERIALGROUP,GENERICMACRO "
		    		   + "WHERE GENERICMACRO.OWNERID=OPERATORSERIALGROUP.LMGROUPID AND OPERATORSERIALGROUP.LOGINID=" + getUserID()
		    		   + " ORDER BY GENERICMACRO.CHILDORDER";
		    		   
		    Object[][] serialGroupIDs = com.cannontech.util.ServletUtil.executeSQL(
		    		dbAlias, sql, new Class[] { Integer.class } );
			
			// get a serial group whose serial number is set to 0, the route id of this group is the default route id
		    if (serialGroupIDs != null && serialGroupIDs.length > 0) {
		    	
				// get versacom serial groups
			    sql = "SELECT YUKONPAOBJECT.PAONAME,LMGROUPVERSACOM.SERIALADDRESS,LMGROUPVERSACOM.DEVICEID,LMGROUPVERSACOM.ROUTEID "
			    	+ "FROM YUKONPAOBJECT,LMGROUPVERSACOM WHERE YUKONPAOBJECT.PAOBJECTID=LMGROUPVERSACOM.DEVICEID AND ";
			    for (int i = 0; i < serialGroupIDs.length; i++) {
			        if( i == 0 )
			            sql += "(LMGROUPVERSACOM.DEVICEID=" + serialGroupIDs[i][0];
			        else
			            sql += " OR LMGROUPVERSACOM.DEVICEID=" + serialGroupIDs[i][0];
			    }
			    sql += ")";
			
				Object[][] versacomNameSerial = com.cannontech.util.ServletUtil.executeSQL(
						dbAlias, sql, new Class[] { String.class, Integer.class, Integer.class, Integer.class } );
				
				if (versacomNameSerial != null) {
					for (int i = 0; i < versacomNameSerial.length; i++) {
						if (((Integer) versacomNameSerial[i][1]).intValue() == 0) {
							dftRouteID = ((Integer) versacomNameSerial[i][3]).intValue();
							return dftRouteID;
						}
					}
				}
				
			  	// get expresscom serial groups 
				sql = "SELECT YUKONPAOBJECT.PAONAME,LMGROUPEXPRESSCOM.SERIALNUMBER,LMGROUPEXPRESSCOM.LMGROUPID,LMGROUPEXPRESSCOM.ROUTEID "
					+ "FROM YUKONPAOBJECT,LMGROUPEXPRESSCOM WHERE YUKONPAOBJECT.PAOBJECTID=LMGROUPEXPRESSCOM.LMGROUPID AND ";
			    for (int i = 0; i < serialGroupIDs.length; i++) {
			        if( i == 0 )
			            sql += "(LMGROUPEXPRESSCOM.LMGROUPID=" + serialGroupIDs[i][0];
			        else
			            sql += " OR LMGROUPEXPRESSCOM.LMGROUPID=" + serialGroupIDs[i][0];
			    }
			    sql += ")";
			   
			    Object[][] expresscomNameSerial = com.cannontech.util.ServletUtil.executeSQL(
			    		dbAlias, sql, new Class[] { String.class, Integer.class, Integer.class, Integer.class } );
				
				if (expresscomNameSerial != null) {
					for (int i = 0; i < expresscomNameSerial.length; i++) {
						if (((Integer) expresscomNameSerial[i][1]).intValue() == 0) {
							dftRouteID = ((Integer) expresscomNameSerial[i][3]).intValue();
							return dftRouteID;
						}
					}
				}
		    }
			
	    	CTILogger.info( "WARNING: no default route id found for energy company #" + getLiteID() );
	    	dftRouteID = INVALID_ROUTE_ID;
		}
    	
		return dftRouteID;
	}

	/**
	 * Returns the optOutEventQueue.
	 * @return OptOutEventQueue
	 */
	public OptOutEventQueue getOptOutEventQueue() {
		if (optOutEventQueue == null)
			try {
				optOutEventQueue = new OptOutEventQueue( getEnergyCompanySetting(EnergyCompanyRole.OPTOUT_COMMAND_FILE) );
				optOutEventQueue.syncFromFile();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		return optOutEventQueue;
	}

	/**
	 * Returns the accountsWithGatewayEndDevice.
	 * @return ArrayList
	 */
	public ArrayList getAccountsWithGatewayEndDevice() {
		if (accountsWithGatewayEndDevice == null)
			accountsWithGatewayEndDevice = new ArrayList();
		return accountsWithGatewayEndDevice;
	}
	
	
	public void init() {
		getAllSelectionLists();
		//loadDefaultThermostatSettings();
		
		if (getLiteID() != SOAPServer.DEFAULT_ENERGY_COMPANY_ID) {
			getAllApplianceCategories();
			getAllServiceCompanies();
			getAllInterviewQuestions();
			getAllCustomerFAQs();
		}
	}
	
	public void clear() {
		custAccountInfos = null;
		customerContacts = null;
		addresses = null;
		lmPrograms = null;
		lmHardwares = null;
		lmCtrlHists = null;
		appCategories = null;
		workOrders = null;
		serviceCompanies = null;
		selectionLists = null;
		interviewQuestions = null;
		customerFAQs = null;
		dftThermSettings = null;
		thermModeSettings = null;
		nextCallNo = 0;
		nextOrderNo = 0;
		
		dftRouteID = CtiUtilities.NONE_ID;
		optOutEventQueue = null;
		accountsWithGatewayEndDevice = null;
		
		starsEnergyCompany = null;
		starsEnrPrograms = null;
		starsCustFAQs = null;
		starsServCompanies = null;
		starsExitQuestions = null;
		starsThermSettings = null;
		starsCustSelLists = null;
		starsWebConfigs = null;
		starsCustAcctInfos = null;
		starsLMCtrlHists = null;
	}
	
	public String getEnergyCompanySetting(int rolePropertyID) {
		return AuthFuncs.getRolePropertyValue(
				YukonUserFuncs.getLiteYukonUser(getUserID()), rolePropertyID);
	}
	
	public LiteYukonGroup getResidentialCustomerGroup() {
		String customerGroupName = getEnergyCompanySetting( EnergyCompanyRole.CUSTOMER_GROUP_NAME );
		return AuthFuncs.getGroup( customerGroupName );
	}
    
    public synchronized ArrayList getAllLMPrograms() {
    	if (lmPrograms == null)
    		getAllApplianceCategories();
    	return lmPrograms;
    }
    
    public synchronized ArrayList getAllApplianceCategories() {
    	if (appCategories == null) {
    		appCategories = new ArrayList();
    		lmPrograms = new ArrayList();
    		
    		com.cannontech.database.db.stars.appliance.ApplianceCategory[] appCats =
    				com.cannontech.database.db.stars.appliance.ApplianceCategory.getAllApplianceCategories( getEnergyCompanyID() );
    				
    		for (int i = 0; i < appCats.length; i++) {
    			LiteApplianceCategory appCat = (LiteApplianceCategory) StarsLiteFactory.createLite( appCats[i] );
    			
    			com.cannontech.database.db.stars.LMProgramWebPublishing[] pubProgs =
    					com.cannontech.database.db.stars.LMProgramWebPublishing.getAllLMProgramWebPublishing( appCats[i].getApplianceCategoryID() );
    			ArrayList pubProgList = new ArrayList();
    			
    			for (int j = 0; j < pubProgs.length; j++) {
    				LiteLMProgram program = (LiteLMProgram) StarsLiteFactory.createLite(pubProgs[j]);
					lmPrograms.add( program );
					pubProgList.add( program );
    			}
    			
    			LiteLMProgram[] pubPrograms = new LiteLMProgram[ pubProgList.size() ];
    			pubProgList.toArray( pubPrograms );
    			appCat.setPublishedPrograms( pubPrograms );
    			
    			appCategories.add( appCat );
    		}
	    	
	    	CTILogger.info( "All appliance categories loaded for energy company #" + getEnergyCompanyID() );
    	}
    	
    	return appCategories;
    }

	public synchronized ArrayList getAllSelectionLists() {
		if (selectionLists == null) {
	        selectionLists = new ArrayList();
	        
	        com.cannontech.database.db.stars.ECToGenericMapping[] items =
					com.cannontech.database.db.stars.ECToGenericMapping.getAllMappingItems( getEnergyCompanyID(), YukonSelectionList.TABLE_NAME );
	        if (items != null) {
	        	for (int i = 0; i < items.length; i++)
	        		selectionLists.add( YukonListFuncs.getYukonSelectionList(items[i].getItemID().intValue()) );
	        }
	        
	        // Get substation list
	        YukonSelectionList subList = new YukonSelectionList();
	        subList.setListID( FAKE_LIST_ID );
	        subList.setListName( com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION );
	        subList.setOrdering( "O" );
	        subList.setSelectionLabel( "" );
	        subList.setWhereIsList( "" );
	        subList.setUserUpdateAvailable( "Y" );
	        
	        com.cannontech.database.db.stars.Substation[] subs =
	        		com.cannontech.database.db.stars.Substation.getAllSubstations( getEnergyCompanyID() );
	        if (subs != null) {
		        ArrayList entries = subList.getYukonListEntries();
		        for (int i = 0; i < subs.length; i++) {
		        	YukonListEntry entry = new YukonListEntry();
		        	entry.setEntryID( subs[i].getSubstationID().intValue() );
		        	entry.setEntryText( subs[i].getSubstationName() );
		        	entries.add( entry );
		        }
		        
		        selectionLists.add( subList );
	        }
			
			CTILogger.info( "All customer selection lists loaded for energy company #" + getEnergyCompanyID() );
		}
		
		return selectionLists;
	}
	
	public YukonSelectionList getYukonSelectionList(String listName, boolean useDefault) {
		ArrayList selectionLists = getAllSelectionLists();
		synchronized (selectionLists) {
			for (int i = 0; i < selectionLists.size(); i++) {
				YukonSelectionList list = (YukonSelectionList) selectionLists.get(i);
				if (list.getListName().equalsIgnoreCase(listName))
					return list;
			}
		}
		
		if (useDefault) {
			if (getLiteID() != SOAPServer.DEFAULT_ENERGY_COMPANY_ID) {
				YukonSelectionList dftList = SOAPServer.getDefaultEnergyCompany().getYukonSelectionList( listName, false );
				if (dftList != null) {
					// If the list is user updatable, return a duplicate of the default list; otherwise return the default list itself
					if (dftList.getUserUpdateAvailable().equalsIgnoreCase("Y"))
						return addYukonSelectionList( listName, dftList, true );
					else
						return dftList;
				}
			}
		}
		
		return null;
	}
	
	public YukonSelectionList getYukonSelectionList(String listName) {
		return getYukonSelectionList(listName, true);
	}
	
	public YukonSelectionList addYukonSelectionList(String listName, YukonSelectionList dftList, boolean populateDefault) {
		try {
			com.cannontech.database.data.constants.YukonSelectionList list =
					new com.cannontech.database.data.constants.YukonSelectionList();
			com.cannontech.database.db.constants.YukonSelectionList listDB = list.getYukonSelectionList();
			listDB.setOrdering( dftList.getOrdering() );
			listDB.setSelectionLabel( dftList.getSelectionLabel() );
			listDB.setWhereIsList( dftList.getWhereIsList() );
			listDB.setListName( listName );
			listDB.setUserUpdateAvailable( dftList.getUserUpdateAvailable() );
			list.setEnergyCompanyID( getEnergyCompanyID() );
			
			list = (com.cannontech.database.data.constants.YukonSelectionList)
					Transaction.createTransaction(Transaction.INSERT, list).execute();
			listDB = list.getYukonSelectionList();
			
			YukonSelectionList cList = new YukonSelectionList();
			StarsLiteFactory.setConstantYukonSelectionList(cList, listDB);
			
			YukonListFuncs.getYukonSelectionLists().put( listDB.getListID(), cList );
			ArrayList allLists = getAllSelectionLists();
			synchronized (allLists) { allLists.add(cList); }
			
			if (populateDefault) {
				for (int i = 0; i < dftList.getYukonListEntries().size(); i++) {
					YukonListEntry dftEntry = (YukonListEntry) dftList.getYukonListEntries().get(i);
					
					com.cannontech.database.db.constants.YukonListEntry entry =
							new com.cannontech.database.db.constants.YukonListEntry();
					entry.setListID( listDB.getListID() );
					entry.setEntryOrder( new Integer(dftEntry.getEntryOrder()) );
					entry.setEntryText( dftEntry.getEntryText() );
					entry.setYukonDefID( new Integer(dftEntry.getYukonDefID()) );
					entry = (com.cannontech.database.db.constants.YukonListEntry)
							Transaction.createTransaction(Transaction.INSERT, entry).execute();
					
					YukonListEntry cEntry = new YukonListEntry();
					StarsLiteFactory.setConstantYukonListEntry( cEntry, entry );
					
					YukonListFuncs.getYukonListEntries().put( entry.getEntryID(), cEntry );
					cList.getYukonListEntries().add( cEntry );
				}
			}
			
			return cList;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void deleteYukonSelectionList(int listID) {
		ArrayList selectionLists = getAllSelectionLists();
		synchronized (selectionLists) {
			for (int i = 0; i < selectionLists.size(); i++) {
				YukonSelectionList list = (YukonSelectionList) selectionLists.get(i);
				if (list.getListID() == listID) {
					selectionLists.remove(i);
					break;
				}
			}
		}
	}
	
	public YukonListEntry getYukonListEntry(int yukonDefID) {
		ArrayList selectionLists = getAllSelectionLists();
		for (int i = 0; i < selectionLists.size(); i++) {
			YukonSelectionList list = (YukonSelectionList) selectionLists.get(i);
			if (list.getListID() == FAKE_LIST_ID) continue;
			
			ArrayList entries = list.getYukonListEntries();
			for (int j = 0; j < entries.size(); j++) {
				YukonListEntry entry = (YukonListEntry) entries.get(j);
				if (entry.getYukonDefID() == yukonDefID)
					return entry;
			}
		}
		
		// Search the default energy company if list entry is not found here
		if (getLiteID() != SOAPServer.DEFAULT_ENERGY_COMPANY_ID)
			return SOAPServer.getDefaultEnergyCompany().getYukonListEntry( yukonDefID );
			
		return null;
	}
	
	public YukonListEntry getYukonListEntry(String listName, int entryID) {
		YukonSelectionList list = getYukonSelectionList( listName );
		if (list != null) {
			ArrayList entries = list.getYukonListEntries();
			for (int i = 0; i < entries.size(); i++) {
				YukonListEntry entry = (YukonListEntry) entries.get(i);
				if (entry.getEntryID() == entryID)
					return entry;
			}
		}
		
		return null;
	}
	
	public synchronized ArrayList getAllServiceCompanies() {
		if (serviceCompanies == null) {
			serviceCompanies = new ArrayList();
			
	        com.cannontech.database.db.stars.report.ServiceCompany[] companies =
	        		com.cannontech.database.db.stars.report.ServiceCompany.getAllServiceCompanies( getEnergyCompanyID() );
	        for (int i = 0; i < companies.length; i++)
	        	serviceCompanies.add( StarsLiteFactory.createLite(companies[i]) );
			
			CTILogger.info( "All service companies loaded for energy company #" + getEnergyCompanyID() );
		}
		
		return serviceCompanies;
	}
	
	public synchronized void loadDefaultThermostatSettings() {
		if (dftThermSettings != null && thermModeSettings != null) return;
		
		int dftInventoryID = -1;
		boolean useDefault = false;
		
		String sql = "SELECT inv.InventoryID FROM ECToInventoryMapping map, "
				   + com.cannontech.database.db.stars.hardware.InventoryBase.TABLE_NAME + " inv "
				   + "WHERE inv.InventoryID = map.InventoryID AND map.EnergyCompanyID = " + getEnergyCompanyID()
				   + " AND inv.InventoryID < 0";
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			if (stmt.getRowCount() == 0) {
	        	if (getLiteID() == SOAPServer.DEFAULT_ENERGY_COMPANY_ID) {
					CTILogger.info( "No default thermostat settings found!!!" );
					return;
	        	}
	        	
	        	// Create a default LM hardware (InventoryID < 0),
	        	// populate the thermostat schedule and manual event table with default values
	        	sql = "SELECT MIN(InventoryID) - 1 FROM InventoryBase";
	        	stmt.setSQLString( sql );
	        	stmt.execute();
	        	if (stmt.getRowCount() > 0)
	        		dftInventoryID = ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue();
	        	
	        	com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
	        			new com.cannontech.database.data.stars.hardware.LMHardwareBase();
	        	hardware.setInventoryID( new Integer(dftInventoryID) );
	        	hardware.getInventoryBase().setNotes( "Default Thermostat" );
	        	hardware.getLMHardwareBase().setManufacturerSerialNumber( "0" );
	        	hardware.setEnergyCompanyID( getEnergyCompanyID() );
	        	hardware = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
	        			Transaction.createTransaction(Transaction.INSERT, hardware).execute();
				
	        	com.cannontech.stars.web.action.CreateLMHardwareAction.populateThermostatTables(
	        			dftInventoryID, SOAPServer.getDefaultEnergyCompany() );
				
				com.cannontech.database.data.stars.event.LMThermostatManualEvent event =
						new com.cannontech.database.data.stars.event.LMThermostatManualEvent();
				event.getLMCustomerEventBase().setEventTypeID( new Integer(
						getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMTHERMOSTAT_MANUAL).getEntryID()) );
				event.getLMCustomerEventBase().setActionID( new Integer(
						getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_MANUAL_OPTION).getEntryID()) );
				event.getLMCustomerEventBase().setEventDateTime( new Date() );
				
				event.getLmThermostatManualEvent().setInventoryID( new Integer(dftInventoryID) );
				event.getLmThermostatManualEvent().setPreviousTemperature( new Integer(72) );
				event.getLmThermostatManualEvent().setHoldTemperature( "N" );
				event.getLmThermostatManualEvent().setOperationStateID( new Integer(
						getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_DEFAULT).getEntryID()) );
				event.getLmThermostatManualEvent().setFanOperationID( new Integer(
						getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_DEFAULT).getEntryID()) );
				
				event.setEnergyCompanyID( getEnergyCompanyID() );
				event = (com.cannontech.database.data.stars.event.LMThermostatManualEvent)
						Transaction.createTransaction(Transaction.INSERT, event).execute();
	        	
				LiteLMHardwareBase liteHw = (LiteLMHardwareBase) StarsLiteFactory.createLite( hardware );
				liteHw.setThermostatSettings( getThermostatSettings(liteHw, false) );
				addLMHardware( liteHw );
			}
			
			dftInventoryID = ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue();
	        dftThermSettings = new LiteStarsThermostatSettings();
	        dftThermSettings.setInventoryID( dftInventoryID );
	        
	        com.cannontech.database.data.stars.hardware.LMThermostatSeason[] seasons =
	        		com.cannontech.database.data.stars.hardware.LMThermostatSeason.getAllLMThermostatSeasons(
	        			new Integer(dftInventoryID) );
	        			
	        if (seasons != null && seasons.length == 2) {
		        dftThermSettings.setThermostatSeasons( new ArrayList() );
		        thermModeSettings = new Object[ seasons.length ][];
		        
		        for (int j = 0; j < seasons.length; j++) {
		        	LiteLMThermostatSeason liteSeason = (LiteLMThermostatSeason) StarsLiteFactory.createLite( seasons[j] );
		        	dftThermSettings.getThermostatSeasons().add( liteSeason );
		        	
		        	thermModeSettings[j] = new Object[2];
		        	thermModeSettings[j][0] = new Integer( liteSeason.getWebConfigurationID() );
		        	LiteWebConfiguration liteConfig = SOAPServer.getWebConfiguration( liteSeason.getWebConfigurationID() );
		        	if (liteConfig.getUrl().equalsIgnoreCase("Cool"))	// Temporarily use URL field to define cool/heat mode
		        		thermModeSettings[j][1] = StarsThermoModeSettings.COOL;
		        	else
		        		thermModeSettings[j][1] = StarsThermoModeSettings.HEAT;
		        }
	        }
	        else if (getLiteID() != SOAPServer.DEFAULT_ENERGY_COMPANY_ID)
	        	useDefault = true;
	        else
				CTILogger.info( "No default thermostat schedules found!!!" );
	        
	        com.cannontech.database.data.stars.event.LMThermostatManualEvent[] events =
	        		com.cannontech.database.data.stars.event.LMThermostatManualEvent.getAllLMThermostatManualEvents( new Integer(dftInventoryID) );
	        if (events != null && events.length >= 1) {
        		dftThermSettings.getThermostatManualEvents().add(
        			(LiteLMThermostatManualEvent) StarsLiteFactory.createLite(events[0]) );
	        }
	        else if (getLiteID() != SOAPServer.DEFAULT_ENERGY_COMPANY_ID)
	        	useDefault = true;
	        else
				CTILogger.info( "No default thermostat option found!!!" );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if (useDefault) {
			LiteStarsThermostatSettings dftSettings = SOAPServer.getDefaultEnergyCompany().getDefaultThermostatSettings();
			if (dftThermSettings == null) {
				dftThermSettings = new LiteStarsThermostatSettings();
				dftThermSettings.setInventoryID( dftSettings.getInventoryID() );
			}
			
			if (dftThermSettings.getThermostatSeasons().size() == 0) {
				dftThermSettings.setThermostatSeasons( dftSettings.getThermostatSeasons() );
				
				thermModeSettings = new Object[ dftThermSettings.getThermostatSeasons().size() ][];
				for (int i = 0; i < dftThermSettings.getThermostatSeasons().size(); i++) {
					LiteLMThermostatSeason liteSeason = (LiteLMThermostatSeason) dftThermSettings.getThermostatSeasons().get(i);
					
					thermModeSettings[i] = new Object[2];
					thermModeSettings[i][0] = new Integer(liteSeason.getWebConfigurationID());
		        	LiteWebConfiguration liteConfig = SOAPServer.getWebConfiguration( liteSeason.getWebConfigurationID() );
		        	if (liteConfig.getAlternateDisplayName().equalsIgnoreCase("Summer"))
		        		thermModeSettings[i][1] = StarsThermoModeSettings.COOL;
		        	else
		        		thermModeSettings[i][1] = StarsThermoModeSettings.HEAT;
				}
			}
			
			if (dftThermSettings.getThermostatManualEvents().size() == 0)
				dftThermSettings.setThermostatManualEvents( dftSettings.getThermostatManualEvents() );
		}
		
		CTILogger.info( "Default thermostat settings loaded for energy company #" + getEnergyCompanyID() );
	}
	
	public LiteStarsThermostatSettings getDefaultThermostatSettings() {
		if (dftThermSettings == null)
			loadDefaultThermostatSettings();
		return dftThermSettings;
	}
	
	public StarsThermoModeSettings getThermModeSetting(int configID) {
		if (thermModeSettings == null)
			loadDefaultThermostatSettings();
		if (thermModeSettings == null) return null;
		
		for (int i = 0; i < thermModeSettings.length; i++) {
			if (((Integer) thermModeSettings[i][0]).intValue() == configID)
				return (StarsThermoModeSettings) thermModeSettings[i][1];
		}
		return null;
	}
	
	public Integer getThermSeasonWebConfigID(StarsThermoModeSettings setting) {
		if (thermModeSettings == null)
			loadDefaultThermostatSettings();
		if (thermModeSettings == null) return null;
		
		for (int i = 0; i < thermModeSettings.length; i++) {
			if (((StarsThermoModeSettings) thermModeSettings[i][1]).getType() == setting.getType())
				return (Integer) thermModeSettings[i][0];
		}
		return null;
	}
	
	public synchronized ArrayList getAllInterviewQuestions() {
		if (interviewQuestions == null) {
			interviewQuestions = new ArrayList();
			
			com.cannontech.database.db.stars.InterviewQuestion[] questions =
					com.cannontech.database.db.stars.InterviewQuestion.getAllInterviewQuestions( getEnergyCompanyID() );
			for (int i = 0; i < questions.length; i++) {
				LiteInterviewQuestion liteQuestion = (LiteInterviewQuestion) StarsLiteFactory.createLite( questions[i] );
				interviewQuestions.add( liteQuestion );
			}
			
			CTILogger.info( "All interview questions loaded for energy company #" + getEnergyCompanyID() );
		}
		
		return interviewQuestions;
	}
	
	public synchronized ArrayList getAllCustomerFAQs() {
		if (customerFAQs == null) {
			customerFAQs = new ArrayList();
			
			String listName = YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP;
			YukonSelectionList list = getYukonSelectionList( listName, false );
			
			if (list == null) {
				if (getLiteID() == SOAPServer.DEFAULT_ENERGY_COMPANY_ID) return customerFAQs;
				
				// Make a copy of the default the customer FAQs
				YukonSelectionList dftList = SOAPServer.getDefaultEnergyCompany().getYukonSelectionList( listName, false );
				list = addYukonSelectionList( listName, dftList, true );
				ArrayList dftFAQs = SOAPServer.getDefaultEnergyCompany().getAllCustomerFAQs();
				
				for (int i = 0; i < dftFAQs.size(); i++) {
					LiteCustomerFAQ dftFAQ = (LiteCustomerFAQ) dftFAQs.get(i);
					com.cannontech.database.db.stars.CustomerFAQ faq =
							new com.cannontech.database.db.stars.CustomerFAQ();
					faq.setQuestion( dftFAQ.getQuestion() );
					faq.setAnswer( dftFAQ.getAnswer() );
					
					for (int j = 0; j < dftList.getYukonListEntries().size(); j++) {
						if (dftFAQ.getSubjectID() == ((YukonListEntry) dftList.getYukonListEntries().get(j)).getEntryID()) {
							int subjectID = ((YukonListEntry) list.getYukonListEntries().get(j)).getEntryID();
							faq.setSubjectID( new Integer(subjectID) );
							break;
						}
					}
					
					try {
						faq = (com.cannontech.database.db.stars.CustomerFAQ)
								Transaction.createTransaction(Transaction.INSERT, faq).execute();
						LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) StarsLiteFactory.createLite( faq );
						customerFAQs.add( liteFAQ );
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			else {
				for (int i = 0; i < list.getYukonListEntries().size(); i++) {
					int subjectID = ((YukonListEntry) list.getYukonListEntries().get(i)).getEntryID();
					com.cannontech.database.db.stars.CustomerFAQ[] faqs =
							com.cannontech.database.db.stars.CustomerFAQ.getCustomerFAQs( new Integer(subjectID) );
					for (int j = 0; j < faqs.length; j++) {
						LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) StarsLiteFactory.createLite( faqs[j] );
						customerFAQs.add( liteFAQ );
					}
				}
			}
/*			
			com.cannontech.database.db.stars.CustomerFAQ[] faqs =
					com.cannontech.database.db.stars.CustomerFAQ.getAllCustomerFAQs( getEnergyCompanyID() );
			for (int i = 0; i < faqs.length; i++) {
				LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) StarsLiteFactory.createLite( faqs[i] );
				customerFAQs.add( liteFAQ );
			}
*/			
			CTILogger.info( "All customer FAQs loaded for energy company #" + getEnergyCompanyID() );
		}
		
		return customerFAQs;
	}
	
	/**
	 * Find the next to the largest call number with pattern "CTI#(NUMBER)", e.g. "CTI#10"
	 */
	public synchronized String getNextCallNumber() {
		if (nextCallNo == 0) {
			String sql = "SELECT CallNumber FROM CallReportBase call, ECToCallReportMapping map "
					   + "WHERE map.EnergyCompanyID = " + getEnergyCompanyID() + " AND call.CallID = map.CallReportID "
					   + "AND CallNumber like '" + ServerUtils.CTI_NUMBER + "%' ORDER BY CallID DESC";
			com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
					sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
					
			try {
				stmt.execute();
				int maxCallNo = 0;
				
				for (int i = 0; i < stmt.getRowCount(); i++) {
					try {
						String callNoStr = (String) stmt.getRow(i)[0];
						int callNo = Integer.parseInt( callNoStr.substring(ServerUtils.CTI_NUMBER.length()) );
						if (callNo > maxCallNo) maxCallNo = callNo;
					}
					catch (NumberFormatException nfe) {}
				}
				
				nextCallNo = maxCallNo + 1;
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return String.valueOf( nextCallNo++ );
	}
	
	public synchronized void resetNextCallNumber() {
		nextCallNo = 0;
	}
	
	/**
	 * Find the next to the largest order number with pattern "CTI#(NUMBER)", e.g. "CTI#10"
	 */
	public synchronized String getNextOrderNumber() {
		if (nextOrderNo == 0) {
			String sql = "SELECT OrderNumber FROM WorkOrderBase service, ECToWorkOrderMapping map "
					   + "WHERE map.EnergyCompanyID = " + getEnergyCompanyID() + " AND service.OrderID = map.WorkOrderID "
					   + "AND OrderNumber like '" + ServerUtils.CTI_NUMBER + "%' ORDER BY OrderID DESC";
			com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
					sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
					
			try {
				stmt.execute();
				int maxOrderNo = 0;
				
				for (int i = 0; i < stmt.getRowCount(); i++) {
					try {
						String orderNoStr = (String) stmt.getRow(i)[0];
						int orderNo = Integer.parseInt( orderNoStr.substring(ServerUtils.CTI_NUMBER.length()) );
						if (orderNo > maxOrderNo) maxOrderNo = orderNo;
					}
					catch (NumberFormatException nfe) {}
				}
				
				nextOrderNo = maxOrderNo + 1;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return String.valueOf( nextOrderNo++ );
	}
	
	public synchronized void resetNextOrderNumber() {
		nextOrderNo = 0;
	}
	
	public ArrayList getAllCustomerContacts() {
		if (customerContacts == null)
			customerContacts = new ArrayList();
		
		return customerContacts;
	}
	
	public ArrayList getAllAddresses() {
		if (addresses == null)
			addresses = new ArrayList();
		
		return addresses;
	}
	
	public ArrayList getAllLMHardwares() {
		if (lmHardwares == null)
			lmHardwares = new ArrayList();
			
		return lmHardwares;
	}
	
	public ArrayList getAllWorkOrders() {
		if (workOrders == null)
			workOrders = new ArrayList();
		
		return workOrders;
	}
	
	public ArrayList getAllCustAccountInformation() {
		if (custAccountInfos == null)
			custAccountInfos = new ArrayList();
		
		return custAccountInfos;
	}
	
	public ArrayList getAllLMControlHistory() {
		if (lmCtrlHists == null)
			lmCtrlHists = new ArrayList();
		
		return lmCtrlHists;
	}
	
	
	public LiteInterviewQuestion[] getInterviewQuestions(int qType) {
		ArrayList qList = new ArrayList();
		ArrayList questions = getAllInterviewQuestions();
		
		synchronized (questions) {
			for (int i = 0; i < questions.size(); i++) {
				LiteInterviewQuestion liteQ = (LiteInterviewQuestion) questions.get(i);
				if (liteQ.getQuestionType() == qType)
					qList.add( liteQ );
			}
		}
		
		LiteInterviewQuestion[] qs = new LiteInterviewQuestion[ qList.size() ];
		qList.toArray( qs );
		
		return qs;
	}

	
	public LiteApplianceCategory getApplianceCategory(int applianceCategoryID) {
		ArrayList appCats = getAllApplianceCategories();
		synchronized (appCats) {
			for (int i = 0; i < appCats.size(); i++) {
				LiteApplianceCategory appCat = (LiteApplianceCategory) appCats.get(i);
				if (appCat.getApplianceCategoryID() == applianceCategoryID)
					return appCat;
			}
		}
		
		return null;
	}
	
	public void addApplianceCategory(LiteApplianceCategory appCat) {
		ArrayList appCats = getAllApplianceCategories();
		synchronized (appCats) { appCats.add( appCat ); }
	}
	
	public LiteApplianceCategory deleteApplianceCategory(int applianceCategoryID) {
		ArrayList appCats = getAllApplianceCategories();
		synchronized (appCats) {
			for (int i = 0; i < appCats.size(); i++) {
				LiteApplianceCategory appCat = (LiteApplianceCategory) appCats.get(i);
				if (appCat.getApplianceCategoryID() == applianceCategoryID) {
					for (int j = 0; j < appCat.getPublishedPrograms().length; j++)
						deleteLMProgram( appCat.getPublishedPrograms()[j].getProgramID() );
					appCats.remove( i );
					return appCat;
				}
			}
		}
		
		return null;
	}
	
	public LiteServiceCompany getServiceCompany(int serviceCompanyID) {
		ArrayList serviceCompanies = getAllServiceCompanies();
		synchronized (serviceCompanies) {
			for (int i = 0; i < serviceCompanies.size(); i++) {
				LiteServiceCompany serviceCompany = (LiteServiceCompany) serviceCompanies.get(i);
				if (serviceCompany.getCompanyID() == serviceCompanyID)
					return serviceCompany;
			}
		}
		
		return null;
	}
	
	public void addServiceCompany(LiteServiceCompany serviceCompany) {
		ArrayList serviceCompanies = getAllServiceCompanies();
		synchronized (serviceCompanies) { serviceCompanies.add(serviceCompany); }
	}
	
	public LiteServiceCompany deleteServiceCompany(int serviceCompanyID) {
		ArrayList serviceCompanies = getAllServiceCompanies();
		synchronized (serviceCompanies) {
			for (int i = 0; i < serviceCompanies.size(); i++) {
				LiteServiceCompany serviceCompany = (LiteServiceCompany) serviceCompanies.get(i);
				if (serviceCompany.getCompanyID() == serviceCompanyID) {
					serviceCompanies.remove( i );
					return serviceCompany;
				}
			}
		}
		
		return null;
	}
	
	public LiteCustomerContact getCustomerContact(int contactID) {
		ArrayList contactList = getAllCustomerContacts();
		synchronized (contactList) {
			for (int i = 0; i < contactList.size(); i++) {
				LiteCustomerContact liteContact = (LiteCustomerContact) contactList.get(i);
				if (liteContact.getContactID() == contactID)
					return liteContact;
			}
		}
		
		LiteContact lContact = ContactFuncs.getContact( contactID );
		if (lContact != null) {
			LiteCustomerContact liteContact = new LiteCustomerContact();
			liteContact.setContactID( lContact.getContactID() );
			liteContact.setLastName( lContact.getContLastName() );
			liteContact.setFirstName( lContact.getContFirstName() );
			liteContact.setLoginID( lContact.getLoginID() );
			for (int i = 0; i < lContact.getLiteContactNotifications().size(); i++) {
				LiteContactNotification lNotif = (LiteContactNotification) lContact.getLiteContactNotifications().get(i);
				if (lNotif.getNotificationCategoryID() == SOAPServer.YUK_LIST_ENTRY_ID_HOME_PHONE)
					liteContact.setHomePhone( lNotif.getNotification() );
				else if (lNotif.getNotificationCategoryID() == SOAPServer.YUK_LIST_ENTRY_ID_WORK_PHONE)
					liteContact.setWorkPhone( lNotif.getNotification() );
				else if (lNotif.getNotificationCategoryID() == SOAPServer.YUK_LIST_ENTRY_ID_EMAIL)
					liteContact.setEmail( LiteCustomerContact.ContactNotification.newInstance(
							lNotif.getDisableFlag().equals("N"), lNotif.getNotification()) );
			}
			
			synchronized (contactList) { contactList.add( liteContact ); }
			return liteContact;
		}
/*		
		try {
			com.cannontech.database.db.contact.Contact contact = new com.cannontech.database.db.contact.Contact();
			contact.setContactID( new Integer(contactID) );
			contact = (com.cannontech.database.db.contact.Contact)
					Transaction.createTransaction( Transaction.RETRIEVE, contact ).execute();
			LiteCustomerContact liteContact = (LiteContact) StarsLiteFactory.createLite( contact );
			
			synchronized (contactList) { contactList.add( liteContact ); }
			return liteContact;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
*/		
		return null;
	}
	
	public void addCustomerContact(LiteCustomerContact liteContact) {
		ArrayList contactList = getAllCustomerContacts();
		synchronized (contactList) { contactList.add( liteContact ); }
		ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_ADD );
	}

	public LiteCustomerContact deleteCustomerContact(int contactID) {
		ArrayList contactList = getAllCustomerContacts();
		synchronized (contactList) {
			for (int i = 0; i < contactList.size(); i++) {
				LiteCustomerContact liteContact = (LiteCustomerContact) contactList.get(i);
				if (liteContact.getContactID() == contactID) {
					contactList.remove(i);
					ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_DELETE );
					return liteContact;
				}
			}
		}
		
		return null;
	}
	
	public LiteAddress getAddress(int addressID) {
		ArrayList addressList = getAllAddresses();
		LiteAddress liteAddr = null;
		
		synchronized (addressList) {
			for (int i = 0; i < addressList.size(); i++) {
				liteAddr = (LiteAddress) addressList.get(i);
				if (liteAddr.getAddressID() == addressID)
					return liteAddr;
			}
		}
		
		try {
			com.cannontech.database.db.customer.Address addr = new com.cannontech.database.db.customer.Address();
			addr.setAddressID( new Integer(addressID) );
			addr = (com.cannontech.database.db.customer.Address)
					Transaction.createTransaction( Transaction.RETRIEVE, addr ).execute();
			liteAddr = (LiteAddress) StarsLiteFactory.createLite( addr );
			
			synchronized (addressList) { addressList.add( liteAddr ); }
			return liteAddr;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void addAddress(LiteAddress liteAddr) {
		ArrayList addressList = getAllAddresses();
		synchronized (addressList) { addressList.add( liteAddr ); }
	}
	
	public LiteAddress deleteAddress(int addressID) {
		ArrayList addressList = getAllAddresses();
		synchronized (addressList) {
			for (int i = 0; i < addressList.size(); i++) {
				LiteAddress liteAddr = (LiteAddress) addressList.get(i);
				if (liteAddr.getAddressID() == addressID) {
					addressList.remove(i);
					return liteAddr;
				}
			}
		}
		
		return null;
	}
	
	public LiteLMProgram getLMProgram(int programID) {
		ArrayList lmProgramList = getAllLMPrograms();
		synchronized (lmProgramList) {
			for (int i = 0; i < lmProgramList.size(); i++) {
				LiteLMProgram liteProg = (LiteLMProgram) lmProgramList.get(i);
				if (liteProg.getProgramID() == programID)
					return liteProg;
			}
		}
		
		return null;
	}
	
	public void addLMProgram(LiteLMProgram liteProg) {
		ArrayList lmProgramList = getAllLMPrograms();
		synchronized (lmProgramList) { lmProgramList.add(liteProg); }
	}
	
	public LiteLMProgram deleteLMProgram(int programID) {
		ArrayList lmProgramList = getAllLMPrograms();
		synchronized (lmProgramList) {
			for (int i = 0; i < lmProgramList.size(); i++) {
				LiteLMProgram liteProg = (LiteLMProgram) lmProgramList.get(i);
				if (liteProg.getProgramID() == programID) {
					lmProgramList.remove(i);
					return liteProg;
				}
			}
		}
		
		return null;
	}
	
	public LiteLMHardwareBase getLMHardware(int inventoryID, boolean autoLoad) {
		ArrayList lmHardwareList = getAllLMHardwares();
		LiteLMHardwareBase liteHw = null;
		
		synchronized (lmHardwareList) {
			for (int i = 0; i < lmHardwareList.size(); i++) {
				liteHw = (LiteLMHardwareBase) lmHardwareList.get(i);
				if (liteHw.getInventoryID() == inventoryID)
					return liteHw;
			}
		}
		
		if (autoLoad) {
			try {
				com.cannontech.database.data.stars.hardware.LMHardwareBase hw = new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				hw.setInventoryID( new Integer(inventoryID) );
				hw = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
						Transaction.createTransaction( Transaction.RETRIEVE, hw ).execute();
				liteHw = (LiteLMHardwareBase) StarsLiteFactory.createLite( hw );
				
            	if (ServerUtils.isOneWayThermostat(liteHw, this))
                	liteHw.setThermostatSettings( getThermostatSettings(liteHw, false) );
            	else if (ServerUtils.isTwoWayThermostat(liteHw, this))
                	liteHw.setThermostatSettings( getThermostatSettings(liteHw, true) );
				
				synchronized (lmHardwareList) { lmHardwareList.add( liteHw ); }
				return liteHw;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public void addLMHardware(LiteLMHardwareBase liteHw) {
		ArrayList lmHardwareList = getAllLMHardwares();
		synchronized (lmHardwareList) { lmHardwareList.add( liteHw ); }
	}
	
	public LiteLMHardwareBase deleteLMHardware(int invID) {
		ArrayList lmHardwareList = getAllLMHardwares();
		LiteLMHardwareBase liteHw = null;
		
		synchronized (lmHardwareList) {
			for (int i = 0; i < lmHardwareList.size(); i++) {
				liteHw = (LiteLMHardwareBase) lmHardwareList.get(i);
				if (liteHw.getInventoryID() == invID) {
					lmHardwareList.remove(i);
					return liteHw;
				}
			}
		}
		
		return null;
	}
	
	public LiteStarsLMControlHistory getLMControlHistory(int groupID) {
		if (groupID == CtiUtilities.NONE_ID) return null;
		
		TimeZone tz = TimeZone.getTimeZone( getEnergyCompanySetting(EnergyCompanyRole.DEFAULT_TIME_ZONE) );
		LiteStarsLMControlHistory lmCtrlHist = null;
		
		ArrayList lmCtrlHistList = getAllLMControlHistory();
		synchronized (lmCtrlHistList) {
			for (int i = 0; i < lmCtrlHistList.size(); i++) {
				lmCtrlHist = (LiteStarsLMControlHistory) lmCtrlHistList.get(i);
				if (lmCtrlHist.getGroupID() == groupID) {
					//SOAPServer.updateLMControlHistory( lmCtrlHist );
					//lmCtrlHist.updateStartIndices( tz );
					return lmCtrlHist;
				}
			}
		}
		
		ArrayList ctrlHistList = new ArrayList();
		com.cannontech.database.db.pao.LMControlHistory[] ctrlHist = com.cannontech.stars.util.LMControlHistoryUtil.getLMControlHistory(
				new Integer(groupID), com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod.ALL );
		for (int i = 0; i < ctrlHist.length; i++)
			ctrlHistList.add( StarsLiteFactory.createLite(ctrlHist[i]) );
		
		lmCtrlHist = new LiteStarsLMControlHistory( groupID );
		lmCtrlHist.setLmControlHistory( ctrlHistList );
		//lmCtrlHist.updateStartIndices( tz );
		
		synchronized (lmCtrlHistList) { lmCtrlHistList.add( lmCtrlHist ); }
		return lmCtrlHist;
	}
	
	public LiteWorkOrderBase getWorkOrderBase(int orderID) {
        ArrayList workOrders = getAllWorkOrders();
        LiteWorkOrderBase workOrder = null;
        
        synchronized (workOrders) {
	        for (int i = 0; i < workOrders.size(); i++) {
	        	workOrder = (LiteWorkOrderBase) workOrders.get(i);
	        	if (workOrder.getOrderID() == orderID)
	        		return workOrder;
	        }
        }
        
        try {
        	com.cannontech.database.db.stars.report.WorkOrderBase order = new com.cannontech.database.db.stars.report.WorkOrderBase();
        	order.setOrderID( new Integer(orderID) );
        	order = (com.cannontech.database.db.stars.report.WorkOrderBase)
        			Transaction.createTransaction( Transaction.RETRIEVE, order ).execute();
        	workOrder = (LiteWorkOrderBase) StarsLiteFactory.createLite( order );
        	
        	synchronized (workOrders) { workOrders.add( workOrder ); }
        	return workOrder;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        return null;
	}
	
	public void addWorkOrderBase(LiteWorkOrderBase liteOrder) {
        ArrayList workOrders = getAllWorkOrders();
    	synchronized (workOrders) { workOrders.add( liteOrder ); }
	}
	
	public void deleteWorkOrderBase(int orderID) {
        ArrayList workOrders = getAllWorkOrders();
        
        synchronized (workOrders) {
	        for (int i = 0; i < workOrders.size(); i++) {
	        	LiteWorkOrderBase workOrder = (LiteWorkOrderBase) workOrders.get(i);
	        	if (workOrder.getOrderID() == orderID) {
	        		workOrders.remove( i );
	        		return;
	        	}
	        }
        }
	}
	
	public LiteStarsThermostatSettings getThermostatSettings(LiteLMHardwareBase liteHw, boolean isTwoWay) {
		LiteStarsThermostatSettings settings = new LiteStarsThermostatSettings();
        settings.setInventoryID( liteHw.getInventoryID() );
        
        com.cannontech.database.data.stars.hardware.LMThermostatSeason[] seasons =
        		com.cannontech.database.data.stars.hardware.LMThermostatSeason.getAllLMThermostatSeasons( new Integer(liteHw.getInventoryID()) );
        if (seasons != null) {
	        settings.setThermostatSeasons( new ArrayList() );
	        
	        for (int i = 0; i < seasons.length; i++) {
	        	LiteLMThermostatSeason liteSeason = (LiteLMThermostatSeason) StarsLiteFactory.createLite( seasons[i] );
	        	settings.getThermostatSeasons().add( liteSeason );
	        }
        }
        
        com.cannontech.database.data.stars.event.LMThermostatManualEvent[] events =
        		com.cannontech.database.data.stars.event.LMThermostatManualEvent.getAllLMThermostatManualEvents( new Integer(liteHw.getInventoryID()) );
        if (events != null) {
        	for (int i = 0; i < events.length; i++)
        		settings.getThermostatManualEvents().add(
        			(LiteLMThermostatManualEvent) StarsLiteFactory.createLite(events[i]) );
        }
        
        if (isTwoWay) {
        	settings.setDynamicData( new LiteStarsGatewayEndDevice() );
        	settings.updateThermostatSettings( liteHw, this );
        }
        
        return settings;
	}
	
	public void updateThermostatSettings(LiteStarsCustAccountInformation liteAcctInfo) {
		StarsCustAccountInformation starsAcctInfo = getStarsCustAccountInformation( liteAcctInfo );
		
		for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
			int invID = ((Integer) liteAcctInfo.getInventories().get(i)).intValue();
			LiteLMHardwareBase liteHw = getLMHardware( invID, true );
			if (!ServerUtils.isTwoWayThermostat(liteHw, this)) continue;
			
			LiteStarsThermostatSettings liteSettings = liteHw.getThermostatSettings();
			liteSettings.updateThermostatSettings( liteHw, this );
			
			for (int j = 0; j < starsAcctInfo.getStarsInventories().getStarsLMHardwareCount(); j++) {
				if (starsAcctInfo.getStarsInventories().getStarsLMHardware(j).getInventoryID() == invID) {
					StarsThermostatSettings starsSettings = starsAcctInfo.getStarsInventories().getStarsLMHardware(j).getStarsThermostatSettings();
					
					starsSettings.removeAllStarsThermostatSeason();
					for (int k = 0; k < liteSettings.getThermostatSeasons().size(); k++) {
						LiteLMThermostatSeason liteSeason = (LiteLMThermostatSeason) liteSettings.getThermostatSeasons().get(k);
						StarsThermostatSeason starsSeason = StarsLiteFactory.createStarsThermostatSeason( liteSeason, this );
						starsSettings.addStarsThermostatSeason( starsSeason );
					}
					if (starsSettings.getStarsThermostatDynamicData() != null) {
						StarsLiteFactory.setStarsThermostatDynamicData(
								starsSettings.getStarsThermostatDynamicData(), liteSettings.getDynamicData(), this );
					}
					
					break;
				}
			}
		}
	}
	
	public LiteStarsCustAccountInformation getCustAccountInformation(int accountID, boolean autoLoad) {
		ArrayList custAcctInfoList = getAllCustAccountInformation();
		LiteStarsCustAccountInformation accountInfo = null;
		
		synchronized (custAcctInfoList) {
			for (int i = 0; i < custAcctInfoList.size(); i++) {
				accountInfo = (LiteStarsCustAccountInformation) custAcctInfoList.get(i);
				if (accountInfo.getCustomerAccount().getAccountID() == accountID) {
					//updateCustAccountInformation( accountInfo );
					return accountInfo;
				}
			}
		}
		
		if (autoLoad) {
			try {
				com.cannontech.database.db.stars.customer.CustomerAccount accountDB =
						com.cannontech.database.db.stars.customer.CustomerAccount.getCustomerAccount( new Integer(accountID) );
				if (accountDB == null) return null;
				
				com.cannontech.database.data.stars.customer.CustomerAccount account =
						new com.cannontech.database.data.stars.customer.CustomerAccount();
				account.setCustomerAccount( accountDB );
				account = (com.cannontech.database.data.stars.customer.CustomerAccount)
						Transaction.createTransaction( Transaction.RETRIEVE, account ).execute();
						
				return addCustAccountInformation( account );
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public LiteStarsCustAccountInformation addCustAccountInformation(com.cannontech.database.data.stars.customer.CustomerAccount account) {
		ArrayList custAcctInfoList = getAllCustAccountInformation();
		
		try {
            com.cannontech.database.db.stars.customer.CustomerAccount accountDB = account.getCustomerAccount();
            com.cannontech.database.data.customer.Customer customer = account.getCustomer();
            com.cannontech.database.db.customer.Customer customerDB = customer.getCustomer();
            com.cannontech.database.data.stars.customer.AccountSite site = account.getAccountSite();
            com.cannontech.database.db.stars.customer.AccountSite siteDB = site.getAccountSite();
            com.cannontech.database.data.stars.customer.SiteInformation siteInfo = site.getSiteInformation();
            com.cannontech.database.db.stars.customer.SiteInformation siteInfoDB = siteInfo.getSiteInformation();
            
            LiteStarsCustAccountInformation accountInfo = new LiteStarsCustAccountInformation( accountDB.getAccountID().intValue() );
            accountInfo.setCustomerAccount( (LiteCustomerAccount) StarsLiteFactory.createLite(accountDB) );
            accountInfo.setCustomer( (LiteCustomer) StarsLiteFactory.createLite(customer) );
            accountInfo.setAccountSite( (LiteAccountSite) StarsLiteFactory.createLite(siteDB) );
            accountInfo.setSiteInformation( (LiteSiteInformation) StarsLiteFactory.createLite(siteInfoDB) );
                
            com.cannontech.database.db.customer.Address streetAddr = site.getStreetAddress();
            LiteAddress liteAddr = (LiteAddress) StarsLiteFactory.createLite( streetAddr );
            addAddress( liteAddr );

            com.cannontech.database.db.customer.Address billAddr = account.getBillingAddress();
            liteAddr = (LiteAddress) StarsLiteFactory.createLite( billAddr );
            addAddress( liteAddr );
            
            com.cannontech.database.db.stars.customer.CustomerResidence residence =
            		com.cannontech.database.db.stars.customer.CustomerResidence.getCustomerResidence( siteDB.getAccountSiteID() );
            if (residence != null)
            	accountInfo.setCustomerResidence( (LiteCustomerResidence) StarsLiteFactory.createLite(residence) );
			
            Vector applianceVector = account.getApplianceVector();            
            accountInfo.setAppliances( new ArrayList() );

            for (int i = 0; i < applianceVector.size(); i++) {
                com.cannontech.database.data.stars.appliance.ApplianceBase appliance =
                		(com.cannontech.database.data.stars.appliance.ApplianceBase) applianceVector.elementAt(i);
                LiteStarsAppliance liteApp = null;
                
                LiteApplianceCategory liteAppCat = getApplianceCategory( appliance.getApplianceBase().getApplianceCategoryID().intValue() );
                if (liteAppCat.getCategoryID() == getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER).getEntryID()) {
                	ApplianceAirConditioner app = ApplianceAirConditioner.getApplianceAirConditioner( appliance.getApplianceBase().getApplianceID() );
                	if (app != null) {
	                	liteApp = new LiteStarsAppAirConditioner();
	                	StarsLiteFactory.setLiteAppAirConditioner( (LiteStarsAppAirConditioner) liteApp, app );
                	}
                }
                else if (liteAppCat.getCategoryID() == getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER).getEntryID()) {
                	ApplianceWaterHeater app = ApplianceWaterHeater.getApplianceWaterHeater( appliance.getApplianceBase().getApplianceID() );
                	if (app != null) {
	                	liteApp = new LiteStarsAppWaterHeater();
	                	StarsLiteFactory.setLiteAppWaterHeater( (LiteStarsAppWaterHeater) liteApp, app );
                	}
                }
                else if (liteAppCat.getCategoryID() == getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL).getEntryID()) {
                	ApplianceDualFuel app = ApplianceDualFuel.getApplianceDualFuel( appliance.getApplianceBase().getApplianceID() );
                	if (app != null) {
	                	liteApp = new LiteStarsAppDualFuel();
	                	StarsLiteFactory.setLiteAppDualFuel( (LiteStarsAppDualFuel) liteApp, app );
                	}
                }
                else if (liteAppCat.getCategoryID() == getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER).getEntryID()) {
                	ApplianceGenerator app = ApplianceGenerator.getApplianceGenerator( appliance.getApplianceBase().getApplianceID() );
                	if (app != null) {
	                	liteApp = new LiteStarsAppGenerator();
	                	StarsLiteFactory.setLiteAppGenerator( (LiteStarsAppGenerator) liteApp, app );
                	}
                }
                else if (liteAppCat.getCategoryID() == getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER).getEntryID()) {
                	ApplianceGrainDryer app = ApplianceGrainDryer.getApplianceGrainDryer( appliance.getApplianceBase().getApplianceID() );
                	if (app != null) {
	                	liteApp = new LiteStarsAppGrainDryer();
	                	StarsLiteFactory.setLiteAppGrainDryer( (LiteStarsAppGrainDryer) liteApp, app );
                	}
                }
                else if (liteAppCat.getCategoryID() == getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT).getEntryID()) {
                	ApplianceStorageHeat app = ApplianceStorageHeat.getApplianceStorageHeat( appliance.getApplianceBase().getApplianceID() );
                	if (app != null) {
	                	liteApp = new LiteStarsAppStorageHeat();
	                	StarsLiteFactory.setLiteAppStorageHeat( (LiteStarsAppStorageHeat) liteApp, app );
                	}
                }
                else if (liteAppCat.getCategoryID() == getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP).getEntryID()) {
                	ApplianceHeatPump app = ApplianceHeatPump.getApplianceHeatPump( appliance.getApplianceBase().getApplianceID() );
                	if (app != null) {
	                	liteApp = new LiteStarsAppHeatPump();
	                	StarsLiteFactory.setLiteAppHeatPump( (LiteStarsAppHeatPump) liteApp, app );
                	}
                }
                else if (liteAppCat.getCategoryID() == getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION).getEntryID()) {
                	ApplianceIrrigation app = ApplianceIrrigation.getApplianceIrrigation( appliance.getApplianceBase().getApplianceID() );
                	if (app != null) {
	                	liteApp = new LiteStarsAppIrrigation();
	                	StarsLiteFactory.setLiteAppIrrigation( (LiteStarsAppIrrigation) liteApp, app );
                	}
                }
                
                if (liteApp == null)
                	liteApp = new LiteStarsAppliance();
                StarsLiteFactory.setLiteApplianceBase( liteApp, appliance );
                
                accountInfo.getAppliances().add( liteApp );
            }

            Vector inventoryVector = account.getInventoryVector();
            accountInfo.setInventories( new ArrayList() );

            for (int i = 0; i < inventoryVector.size(); i++) {
                com.cannontech.database.db.stars.hardware.InventoryBase inventory =
                		(com.cannontech.database.db.stars.hardware.InventoryBase) inventoryVector.elementAt(i);
                LiteLMHardwareBase liteHw = getLMHardware( inventory.getInventoryID().intValue(), true );
                accountInfo.getInventories().add( inventory.getInventoryID() );
            }

            accountInfo.setLmPrograms( new ArrayList() );

            for (int i = 0; i < applianceVector.size(); i++) {
                com.cannontech.database.data.stars.appliance.ApplianceBase appliance =
                        (com.cannontech.database.data.stars.appliance.ApplianceBase) applianceVector.elementAt(i);
                int progID = appliance.getApplianceBase().getLMProgramID().intValue();
                if (progID == 0) continue;
                
                LiteLMProgram liteProg = getLMProgram( progID );
                LiteStarsLMProgram prog = new LiteStarsLMProgram( liteProg );
                prog.setGroupID( appliance.getLMHardwareConfig().getAddressingGroupID().intValue() );
                
                com.cannontech.database.data.stars.event.LMProgramEvent[] events =
                		com.cannontech.database.data.stars.event.LMProgramEvent.getAllLMProgramEvents( accountDB.getAccountID(), new Integer(progID) );
                if (events != null) {
                	for (int j = 0; j < events.length; j++) {
                		LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( events[j] );
                		prog.getProgramHistory().add( liteEvent );
                	}
                	prog.updateProgramStatus();
                }
                
                getLMControlHistory( appliance.getLMHardwareConfig().getAddressingGroupID().intValue() );
                
                accountInfo.getLmPrograms().add( prog );
            }
            
			StarsCallReport[] calls = StarsFactory.getStarsCallReports( accountDB.getAccountID() );
			if (calls != null) {
				accountInfo.setCallReportHistory( new ArrayList() );
				for (int i = 0; i < calls.length; i++)
					accountInfo.getCallReportHistory().add( calls[i] );
			}
				
	        com.cannontech.database.db.stars.report.WorkOrderBase[] orders =
	        		com.cannontech.database.db.stars.report.WorkOrderBase.getAllWorkOrders( accountDB.getAccountID() );
	        if (orders != null) {
	        	accountInfo.setServiceRequestHistory( new ArrayList() );
	        	for (int i = 0; i < orders.length; i++) {
	        		LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) StarsLiteFactory.createLite( orders[i] );
	        		addWorkOrderBase( liteOrder );
	        		accountInfo.getServiceRequestHistory().add( orders[i].getOrderID() );
	        	}
	        }

            synchronized (custAcctInfoList) { custAcctInfoList.add( accountInfo ); }
            return accountInfo;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void deleteCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo) {
		// Remove from opt out event queue
		getOptOutEventQueue().removeEvents( liteAcctInfo.getAccountID() );
		
		// Remove from GatewayEndDevice account list
		ArrayList accountList = getAccountsWithGatewayEndDevice();
		synchronized (accountList) {
			if (accountList.contains(liteAcctInfo)) accountList.remove( liteAcctInfo );
		}
    	
		// Remote all contacts from customerContacts
		deleteCustomerContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
		ArrayList contacts = liteAcctInfo.getCustomer().getAdditionalContacts();
		for (int i = 0; i < contacts.size(); i++)
			deleteCustomerContact( ((Integer) contacts.get(i)).intValue() );
		
		// Remove all addresses from addresses
		deleteAddress( liteAcctInfo.getCustomerAccount().getBillingAddressID() );
		deleteAddress( liteAcctInfo.getAccountSite().getStreetAddressID() );
		
		// Update all hardwares from lmHardwares (set accountID = 0)
		for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
			int invID = ((Integer) liteAcctInfo.getInventories().get(i)).intValue();
			LiteLMHardwareBase liteHw = getLMHardware( invID, false );
			liteHw.setAccountID( com.cannontech.database.db.stars.customer.CustomerAccount.NONE_INT );
/*			
			ArrayList lmHarewares = getAllLMHardwares();
			synchronized (lmHardwares) {
				for (int j = 0; j < lmHardwares.size(); j++) {
					LiteLMHardwareBase liteHw = (LiteLMHardwareBase) lmHardwares.get(j);
					if (liteHw.getInventoryID() == invID) {
						lmHardwares.remove(j);
						break;
					}
				}
			}*/
		}
		
		// Remove all work orders from workOrders
		ArrayList workOrders = getAllWorkOrders();
		synchronized (workOrders) {
			for (int i = 0; i < liteAcctInfo.getServiceRequestHistory().size(); i++) {
				int orderID = ((Integer) liteAcctInfo.getServiceRequestHistory().get(i)).intValue();
				
				for (int j = 0; j < workOrders.size(); j++) {
					LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) workOrders.get(j);
					if (liteOrder.getOrderID() == orderID) {
						workOrders.remove(j);
						break;
					}
				}
			}
		}
		
		// Remove the customer account from custAccountInfos
		getAllCustAccountInformation().remove( liteAcctInfo );
	}
	
	public void updateCustAccountInformation(LiteStarsCustAccountInformation accountInfo) {
		for (int i = 0; i < accountInfo.getInventories().size(); i++) {
			Integer invID = (Integer) accountInfo.getInventories().get(i);
			LiteLMHardwareBase liteHw = getLMHardware( invID.intValue(), true );
			
			com.cannontech.database.data.stars.event.LMHardwareEvent[] events =
					com.cannontech.database.data.stars.event.LMHardwareEvent.getAllLMHardwareEvents( invID );
			ArrayList hwHist = new ArrayList();
			for (int j = 0; j < events.length; j++) {
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( events[j] );
				hwHist.add( liteEvent );
			}
			
			liteHw.setLmHardwareHistory( hwHist );
		}
		
		for (int i = 0; i < accountInfo.getLmPrograms().size(); i++) {
			LiteStarsLMProgram liteProg = (LiteStarsLMProgram) accountInfo.getLmPrograms().get(i);
                
            com.cannontech.database.data.stars.event.LMProgramEvent[] events =
            		com.cannontech.database.data.stars.event.LMProgramEvent.getAllLMProgramEvents(
            			new Integer(accountInfo.getCustomerAccount().getAccountID()), new Integer(liteProg.getLmProgram().getProgramID()) );
            if (events != null) {
	            ArrayList progHist = new ArrayList();
            	for (int j = 0; j < events.length; j++) {
            		LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( events[j] );
            		progHist.add( liteEvent );
            	}
            	
            	liteProg.setProgramHistory( progHist );
            }
            
            getLMControlHistory( liteProg.getGroupID() );
		}
	}
	
	public LiteStarsCustAccountInformation searchByAccountNumber(String accountNo) {
		ArrayList custAcctInfoList = getAllCustAccountInformation();
		for (int i = 0; i < custAcctInfoList.size(); i++) {
			LiteStarsCustAccountInformation accountInfo = (LiteStarsCustAccountInformation) custAcctInfoList.get(i);
			if (accountInfo.getCustomerAccount().getAccountNumber().equalsIgnoreCase( accountNo ))
				return accountInfo;
		}
		
		try {
			com.cannontech.database.db.stars.customer.CustomerAccount[] accounts =
					com.cannontech.database.db.stars.customer.CustomerAccount.searchByAccountNumber( getEnergyCompanyID(), accountNo );
			if (accounts == null || accounts.length == 0) return null;
			
			// If there are more than one account with the same account # (this shouldn't happen), we will only return the first of them
			com.cannontech.database.data.stars.customer.CustomerAccount account =
					new com.cannontech.database.data.stars.customer.CustomerAccount();
			account.setCustomerAccount( accounts[0] );
			account = (com.cannontech.database.data.stars.customer.CustomerAccount)
					Transaction.createTransaction( Transaction.RETRIEVE, account ).execute();
			
			return addCustAccountInformation( account );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/* The following methods are only used when SOAPClient exists locally */
	
	public StarsEnergyCompany getStarsEnergyCompany() {
		if (starsEnergyCompany == null)
			starsEnergyCompany = StarsLiteFactory.createStarsEnergyCompany( this );
		return starsEnergyCompany;
	}
	
	private Hashtable getStarsCustSelectionLists() {
		if (starsCustSelLists == null)
			starsCustSelLists = new Hashtable();
		return starsCustSelLists;
	}
	
	private StarsCustSelectionList getStarsCustSelectionList(String listName) {
		Hashtable starsCustSelLists = getStarsCustSelectionLists();
		synchronized (starsCustSelLists) {
			StarsCustSelectionList starsList = (StarsCustSelectionList) starsCustSelLists.get( listName );
			if (starsList == null) {
				YukonSelectionList yukonList = getYukonSelectionList( listName );
				if (yukonList != null) {
					starsList = StarsLiteFactory.createStarsCustSelectionList( yukonList );
					starsCustSelLists.put( starsList.getListName(), starsList );
				}
			}
			
			return starsList;
		}
	}
	
	public StarsCustomerSelectionLists getStarsCustomerSelectionLists(StarsYukonUser starsUser) {
		StarsCustomerSelectionLists starsLists = new StarsCustomerSelectionLists();
		LiteYukonUser liteUser = starsUser.getYukonUser();
		
		boolean energySourceListAdded = false;
		boolean horsePowerListAdded = false;
		boolean deviceLocationListAdded = false;
		boolean deviceVoltageListAdded = false;
		
		if (ServerUtils.isOperator( starsUser )) {
			starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE) );
	
			if (AuthFuncs.checkRole( liteUser, OddsForControlRole.ROLEID ) != null) {
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL) );
			}
			if (AuthFuncs.checkRoleProperty( liteUser, ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_GENERAL )) {
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION) );
			}
			if (AuthFuncs.checkRoleProperty( liteUser, ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_CALL_TRACKING )) {
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE) );
			}
			if (AuthFuncs.checkRoleProperty( liteUser, ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT )) {
				StarsCustSelectionList list = getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD);
				if (list != null) starsLists.addStarsCustSelectionList( list );
			}
			if (AuthFuncs.checkRoleProperty( liteUser, ConsumerInfoRole.CONSUMER_INFO_APPLIANCES )) {
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY) );
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER) );
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION) );
				
				ArrayList categories = getAllApplianceCategories();
				for (int i = 0; i < categories.size(); i++) {
					LiteApplianceCategory liteAppCat = (LiteApplianceCategory) categories.get(i);
					if (liteAppCat.getCategoryID() == getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER).getEntryID()) {
						starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_TONNAGE) );
						starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE) );
					}
					else if (liteAppCat.getCategoryID() == getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER).getEntryID()) {
						starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_GALLONS) );
						if (!energySourceListAdded) {
							starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_ENERGY_SOURCE) );
							energySourceListAdded = true;
						}
					}
					else if (liteAppCat.getCategoryID() == getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL).getEntryID()) {
						starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SWITCH_OVER_TYPE) );
						if (!energySourceListAdded) {
							starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_ENERGY_SOURCE) );
							energySourceListAdded = true;
						}
					}
					else if (liteAppCat.getCategoryID() == getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR).getEntryID()) {
						starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_TRANSFER_SWITCH_TYPE) );
						starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_TRANSFER_SWITCH_MFG) );
					}
					else if (liteAppCat.getCategoryID() == getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER).getEntryID()) {
						starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DRYER_TYPE) );
						starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_BIN_SIZE) );
						if (!energySourceListAdded) {
							starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_ENERGY_SOURCE) );
							energySourceListAdded = true;
						}
						if (!horsePowerListAdded) {
							starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HORSE_POWER) );
							horsePowerListAdded = true;
						}
						starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_SOURCE) );
					}
					else if (liteAppCat.getCategoryID() == getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT).getEntryID()) {
						starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_TYPE) );
					}
					else if (liteAppCat.getCategoryID() == getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP).getEntryID()) {
						starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_PUMP_TYPE) );
						starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_STANDBY_SOURCE) );
					}
					else if (liteAppCat.getCategoryID() == getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION).getEntryID()) {
						starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE) );
						if (!horsePowerListAdded) {
							starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HORSE_POWER) );
							horsePowerListAdded = true;
						}
						if (!energySourceListAdded) {
							starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_ENERGY_SOURCE) );
							energySourceListAdded = true;
						}
						starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SOIL_TYPE) );
						if (!deviceLocationListAdded) {
							starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_LOCATION) );
							deviceLocationListAdded = true;
						}
						if (!deviceVoltageListAdded) {
							starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE) );
							deviceVoltageListAdded = true;
						}
					}
				}
			}
			if (AuthFuncs.checkRoleProperty( liteUser, ConsumerInfoRole.CONSUMER_INFO_HARDWARES )) {
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE) );
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS) );
				if (!deviceLocationListAdded) {
					starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_LOCATION) );
					deviceLocationListAdded = true;
				}
				if (!deviceVoltageListAdded) {
					starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE) );
					deviceVoltageListAdded = true;
				}
			}
			if (AuthFuncs.checkRoleProperty( liteUser, ConsumerInfoRole.CONSUMER_INFO_WORK_ORDERS )) {
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE) );
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS) );
			}
			if (AuthFuncs.checkRoleProperty( liteUser, ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_RESIDENCE )) {
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_RESIDENCE_TYPE) );
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CONSTRUCTION_MATERIAL) );
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DECADE_BUILT) );
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SQUARE_FEET) );
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_INSULATION_DEPTH) );
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GENERAL_CONDITION) );
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_COOLING_SYSTEM) );
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HEATING_SYSTEM) );
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_OCCUPANTS) );
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OWNERSHIP_TYPE) );
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_FUEL_TYPE) );
			}
			if (AuthFuncs.checkRoleProperty( liteUser, ConsumerInfoRole.SUPER_OPERATOR )) {
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY) );
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_ANSWER_TYPE) );
			}
		}
		else if (ServerUtils.isResidentialCustomer( starsUser )) {
			// Currently the consumer side only need opt out period list and hardware status list
			if (AuthFuncs.checkRoleProperty( liteUser, ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT )
				&& !AuthFuncs.checkRoleProperty( liteUser, ResidentialCustomerRole.HIDE_OPT_OUT_BOX )) {
				StarsCustSelectionList list = getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD_CUS);
				if (list == null) {
					list = getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD);
					if (list != null) {
						StarsCustSelectionList cusList = new StarsCustSelectionList();
						cusList.setListID( list.getListID() );
						cusList.setListName( YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD_CUS );
						cusList.setStarsSelectionListEntry( list.getStarsSelectionListEntry() );
						
						Hashtable starsCustSelLists = getStarsCustSelectionLists();
						synchronized (starsCustSelLists) { starsCustSelLists.put(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD_CUS, cusList); }
					}
				}
				if (list != null) starsLists.addStarsCustSelectionList( list );
				
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS) );
			}
		}
		
		return starsLists;
	}
	
	public StarsEnrollmentPrograms getStarsEnrollmentPrograms() {
		if (starsEnrPrograms == null)
			starsEnrPrograms = StarsLiteFactory.createStarsEnrollmentPrograms(
					getAllApplianceCategories(), getLiteID() );
		return starsEnrPrograms;
	}
	
	public StarsCustomerFAQs getStarsCustomerFAQs() {
		if (starsCustFAQs == null)
			starsCustFAQs = StarsLiteFactory.createStarsCustomerFAQs( getAllCustomerFAQs() );
		return starsCustFAQs;
	}
	
	public StarsServiceCompanies getStarsServiceCompanies() {
		if (starsServCompanies == null) {
			starsServCompanies = new StarsServiceCompanies();
			
			// Always add a "(none)" to the service company list
			StarsServiceCompany starsServCompany = new StarsServiceCompany();
			starsServCompany.setCompanyID( 0 );
			starsServCompany.setCompanyName( "(none)" );
			starsServCompanies.addStarsServiceCompany( starsServCompany );
			
			ArrayList servCompanies = getAllServiceCompanies();
			for (int i = 0; i < servCompanies.size(); i++) {
				LiteServiceCompany liteServCompany = (LiteServiceCompany) servCompanies.get(i);
				starsServCompanies.addStarsServiceCompany(
						StarsLiteFactory.createStarsServiceCompany(liteServCompany, getLiteID()) );
			}
		}
		
		return starsServCompanies;
	}
	
	public StarsExitInterviewQuestions getStarsExitInterviewQuestions() {
		if (starsExitQuestions == null) {
			starsExitQuestions = new StarsExitInterviewQuestions();
            
            int exitQType = getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_QUE_TYPE_EXIT).getEntryID();
            LiteInterviewQuestion[] liteQuestions = getInterviewQuestions( exitQType );
            for (int i = 0; i < liteQuestions.length; i++) {
            	StarsExitInterviewQuestion starsQuestion = new StarsExitInterviewQuestion();
            	StarsLiteFactory.setStarsQuestionAnswer( starsQuestion, liteQuestions[i] );
            	starsExitQuestions.addStarsExitInterviewQuestion( starsQuestion );
            }
		}
		
		return starsExitQuestions;
	}
	
	public StarsDefaultThermostatSettings getStarsDefaultThermostatSettings() {
		if (starsThermSettings == null) {
			starsThermSettings = new StarsDefaultThermostatSettings();
			StarsLiteFactory.setStarsThermostatSettings( starsThermSettings, getDefaultThermostatSettings(), getLiteID() );
		}
		
		return starsThermSettings;
	}
	
	private Hashtable getStarsWebConfigs() {
		if (starsWebConfigs == null)
			starsWebConfigs = new Hashtable();
		return starsWebConfigs;
	}
	
	public StarsWebConfig getStarsWebConfig(int webConfigID) {
		Hashtable starsWebConfigs = getStarsWebConfigs();
		synchronized (starsWebConfigs) {
			Integer configID = new Integer(webConfigID);
			StarsWebConfig starsWebConfig = (StarsWebConfig) starsWebConfigs.get( configID );
			if (starsWebConfig == null) {
				LiteWebConfiguration liteWebConfig = SOAPServer.getWebConfiguration( webConfigID );
				starsWebConfig = StarsLiteFactory.createStarsWebConfig( liteWebConfig );
				starsWebConfigs.put( configID, starsWebConfig );
			}
			
			return starsWebConfig;
		}
	}
	
	public void updateStarsWebConfig(int webConfigID) {
		Hashtable starsWebConfigs = getStarsWebConfigs();
		LiteWebConfiguration liteWebConfig = SOAPServer.getWebConfiguration( webConfigID );
		StarsWebConfig starsWebConfig = StarsLiteFactory.createStarsWebConfig( liteWebConfig );
		synchronized (starsWebConfigs) { starsWebConfigs.put(new Integer(webConfigID), starsWebConfig); }
	}
	
	public void deleteStarsWebConfig(int webConfigID) {
		Hashtable starsWebConfigs = getStarsWebConfigs();
		synchronized (starsWebConfigs) { starsWebConfigs.remove( new Integer(webConfigID) ); }
	}

	private Hashtable getStarsCustAcctInfos() {
		if (starsCustAcctInfos == null)
			starsCustAcctInfos = new Hashtable();
		return starsCustAcctInfos;
	}
	
	public StarsCustAccountInformation getStarsCustAccountInformation(int accountID) {
		return (StarsCustAccountInformation)getStarsCustAcctInfos().get( new Integer(accountID) );
	}
	
	public StarsCustAccountInformation getStarsCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo) {
		Hashtable starsCustAcctInfos = getStarsCustAcctInfos();
		synchronized (starsCustAcctInfos) {
			Integer accountID = new Integer(liteAcctInfo.getAccountID());
			StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation) starsCustAcctInfos.get( accountID );
			if (starsAcctInfo == null) {
				starsAcctInfo = StarsLiteFactory.createStarsCustAccountInformation( liteAcctInfo, getLiteID(), true );
				starsCustAcctInfos.put( accountID, starsAcctInfo );
			}
			
			return starsAcctInfo;
		}
	}
	
	public StarsCustAccountInformation updateStarsCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo) {
		Hashtable starsCustAcctInfos = getStarsCustAcctInfos();
		synchronized (starsCustAcctInfos) {
			Integer accountID = new Integer(liteAcctInfo.getAccountID());
			StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation) starsCustAcctInfos.get( accountID );
			if (starsAcctInfo != null)
				StarsLiteFactory.setStarsCustAccountInformation( starsAcctInfo, liteAcctInfo, getLiteID(), true );
			
			return starsAcctInfo;
		}
	}
	
	public void deleteStarsCustAccountInformation(int accountID) {
		Hashtable starsCustAcctInfos = getStarsCustAcctInfos();
		synchronized (starsCustAcctInfos) { starsCustAcctInfos.remove( new Integer(accountID) ); }
	}
	
	public Hashtable getStarsLMCtrlHists() {
		if (starsLMCtrlHists == null)
			starsLMCtrlHists = new Hashtable();
		return starsLMCtrlHists;
	}
	
	private void setControlSummary(StarsLMControlHistory starsCtrlHist, TimeZone tz) {
		ControlSummary summary = new ControlSummary();
		int dailyTime = 0;
		int monthlyTime = 0;
		int seasonalTime = 0;
		int annualTime = 0;
		
		Date pastDay = LMControlHistoryUtil.getPeriodStartTime( StarsCtrlHistPeriod.PASTDAY, tz );
		Date pastMonth = LMControlHistoryUtil.getPeriodStartTime( StarsCtrlHistPeriod.PASTMONTH, tz );
		Date pastYear = LMControlHistoryUtil.getPeriodStartTime( StarsCtrlHistPeriod.PASTYEAR, tz );
		
		for (int i = 0; i < starsCtrlHist.getControlHistoryCount(); i++) {
			ControlHistory ctrlHist = starsCtrlHist.getControlHistory(i);
			seasonalTime += ctrlHist.getControlDuration();
			if (ctrlHist.getStartDateTime().after( pastYear )) {
				annualTime += ctrlHist.getControlDuration();
				if (ctrlHist.getStartDateTime().after( pastMonth )) {
					monthlyTime += ctrlHist.getControlDuration();
					if (ctrlHist.getStartDateTime().after( pastDay ))
						dailyTime += ctrlHist.getControlDuration();
				}
			}
		}
		
		summary.setDailyTime( dailyTime );
		summary.setMonthlyTime( monthlyTime );
		summary.setSeasonalTime( seasonalTime );
		summary.setAnnualTime( annualTime );
		starsCtrlHist.setControlSummary( summary );
	}
	
	public StarsLMControlHistory getStarsLMControlHistory(LiteStarsLMControlHistory liteCtrlHist) {
		Hashtable starsLMCtrlHists = getStarsLMCtrlHists();
		synchronized (starsLMCtrlHists) {
			Integer groupID = new Integer(liteCtrlHist.getGroupID());
			StarsLMControlHistory starsCtrlHist = (StarsLMControlHistory) starsLMCtrlHists.get( groupID );
			if (starsCtrlHist == null) {
				starsCtrlHist = StarsLiteFactory.createStarsLMControlHistory(
						liteCtrlHist, StarsCtrlHistPeriod.ALL, false );
				setControlSummary( starsCtrlHist, TimeZone.getTimeZone(getEnergyCompanySetting(EnergyCompanyRole.DEFAULT_TIME_ZONE)) );
				starsLMCtrlHists.put( groupID, starsCtrlHist );
			}
			
			return starsCtrlHist;
		}
	}
	
	public StarsLMControlHistory updateStarsLMControlHistory(LiteStarsLMControlHistory liteCtrlHist) {
		Hashtable starsLMCtrlHists = getStarsLMCtrlHists();
		synchronized (starsLMCtrlHists) {
			Integer groupID = new Integer(liteCtrlHist.getGroupID());
			StarsLMControlHistory starsCtrlHist = (StarsLMControlHistory) starsLMCtrlHists.get( groupID );
			if (starsCtrlHist != null) {
				StarsLiteFactory.setStarsLMControlHistory( starsCtrlHist, liteCtrlHist, StarsCtrlHistPeriod.ALL, false );
				setControlSummary( starsCtrlHist, TimeZone.getTimeZone(getEnergyCompanySetting(EnergyCompanyRole.DEFAULT_TIME_ZONE)) );
			}
			
			return starsCtrlHist;
		}
	}

}
