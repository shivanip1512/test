package com.cannontech.database.data.lite.stars;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.stars.ECToGenericMapping;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.operator.InventoryRole;
import com.cannontech.roles.operator.OddsForControlRole;
import com.cannontech.roles.operator.WorkOrderRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.util.OptOutEventQueue;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.ControlHistory;
import com.cannontech.stars.xml.serialize.ControlSummary;
import com.cannontech.stars.xml.serialize.StarsCallReport;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsCustomerFAQs;
import com.cannontech.stars.xml.serialize.StarsCustomerSelectionLists;
import com.cannontech.stars.xml.serialize.StarsDefaultThermostatSettings;
import com.cannontech.stars.xml.serialize.StarsEnergyCompany;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestion;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestions;
import com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.StarsServiceCompanies;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.stars.xml.serialize.StarsThermostatSeason;
import com.cannontech.stars.xml.serialize.StarsThermostatSettings;
import com.cannontech.stars.xml.serialize.StarsWebConfig;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;

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
	
	private static final String[] OPERATOR_SELECTION_LISTS = {
		YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE,
		YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL,
		com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION,
		YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE,
		YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD,
		YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
		YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER,
		YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION,
		YukonSelectionListDefs.YUK_LIST_NAME_AC_TONNAGE,
		YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE,
		YukonSelectionListDefs.YUK_LIST_NAME_WH_NUM_OF_GALLONS,
		YukonSelectionListDefs.YUK_LIST_NAME_WH_ENERGY_SOURCE,
		YukonSelectionListDefs.YUK_LIST_NAME_WH_LOCATION,
		YukonSelectionListDefs.YUK_LIST_NAME_DF_SWITCH_OVER_TYPE,
		YukonSelectionListDefs.YUK_LIST_NAME_DF_SECONDARY_SOURCE,
		YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_TYPE,
		YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_MFG,
		YukonSelectionListDefs.YUK_LIST_NAME_GRAIN_DRYER_TYPE,
		YukonSelectionListDefs.YUK_LIST_NAME_GD_BIN_SIZE,
		YukonSelectionListDefs.YUK_LIST_NAME_GD_ENERGY_SOURCE,
		YukonSelectionListDefs.YUK_LIST_NAME_GD_HORSE_POWER,
		YukonSelectionListDefs.YUK_LIST_NAME_GD_HEAT_SOURCE,
		YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_HEAT_TYPE,
		YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_TYPE,
		YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_SIZE,
		YukonSelectionListDefs.YUK_LIST_NAME_HP_STANDBY_SOURCE,
		YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE,
		YukonSelectionListDefs.YUK_LIST_NAME_IRR_HORSE_POWER,
		YukonSelectionListDefs.YUK_LIST_NAME_IRR_ENERGY_SOURCE,
		YukonSelectionListDefs.YUK_LIST_NAME_IRR_SOIL_TYPE,
		YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_LOCATION,
		YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_VOLTAGE,
		YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE,
		YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS,
		YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_LOCATION,
		YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE,
		YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE,
		YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS,
		YukonSelectionListDefs.YUK_LIST_NAME_RESIDENCE_TYPE,
		YukonSelectionListDefs.YUK_LIST_NAME_CONSTRUCTION_MATERIAL,
		YukonSelectionListDefs.YUK_LIST_NAME_DECADE_BUILT,
		YukonSelectionListDefs.YUK_LIST_NAME_SQUARE_FEET,
		YukonSelectionListDefs.YUK_LIST_NAME_INSULATION_DEPTH,
		YukonSelectionListDefs.YUK_LIST_NAME_GENERAL_CONDITION,
		YukonSelectionListDefs.YUK_LIST_NAME_COOLING_SYSTEM,
		YukonSelectionListDefs.YUK_LIST_NAME_HEATING_SYSTEM,
		YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_OCCUPANTS,
		YukonSelectionListDefs.YUK_LIST_NAME_OWNERSHIP_TYPE,
		YukonSelectionListDefs.YUK_LIST_NAME_FUEL_TYPE,
		YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
		YukonSelectionListDefs.YUK_LIST_NAME_ANSWER_TYPE,
		YukonSelectionListDefs.YUK_LIST_NAME_INV_SEARCH_BY,
		YukonSelectionListDefs.YUK_LIST_NAME_INV_SORT_BY,
		YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY,
		YukonSelectionListDefs.YUK_LIST_NAME_SO_SEARCH_BY,
		YukonSelectionListDefs.YUK_LIST_NAME_SO_SORT_BY,
		YukonSelectionListDefs.YUK_LIST_NAME_SO_FILTER_BY,
	};
	
	
	private String name = null;
	private int primaryContactID = CtiUtilities.NONE_ID;
	private int userID = com.cannontech.user.UserUtils.USER_YUKON_ID;
	
	private ArrayList custAccountInfos = null;	// List of LiteStarsCustAccountInformation
	private ArrayList addresses = null;			// List of LiteAddress
	private ArrayList lmPrograms = null;			// List of LiteLMProgram
	private ArrayList inventory = null;			// List of LiteInventoryBase
	private ArrayList lmCtrlHists = null;			// List of LiteStarsLMControlHistory
	private ArrayList appCategories = null;		// List of LiteApplianceCategory
	private ArrayList workOrders = null;			// List of LiteWorkOrderBase
	private ArrayList serviceCompanies = null;	// List of LiteServiceCompany
	private ArrayList selectionLists = null;		// List of YukonSelectionList
	private ArrayList interviewQuestions = null;	// List of LiteInterviewQuestion
	private ArrayList customerFAQs = null;		// List of LiteCustomerFAQ
	
	// Map of hardware type yukondefid (Integer) to LiteStarsLMHardware
	private Hashtable dftLMHardwares = null;
	
	private int nextCallNo = 0;
	private int nextOrderNo = 0;
	private boolean inventoryLoaded = false;
	private boolean workOrdersLoaded = false;
	
	private int dftRouteID = CtiUtilities.NONE_ID;
	private TimeZone dftTimeZone = null;
	private OptOutEventQueue optOutEventQueue = null;
	private SwitchCommandQueue switchCommandQueue = null;
	private ArrayList accountsWithGatewayEndDevice = null;	// List of LiteStarsCustAccountInformation
	
	// Map of contact ID to customer account information (Integer, LiteStarsCustAccountInformation)
	private Hashtable contactCustAccountInfoMap = null;
	
	
	// Cached XML messages
	private StarsEnergyCompany starsEnergyCompany = null;
	private StarsEnrollmentPrograms starsEnrPrograms = null;
	private StarsCustomerFAQs starsCustFAQs = null;
	private StarsServiceCompanies starsServCompanies = null;
	private StarsExitInterviewQuestions starsExitQuestions = null;
	private StarsDefaultThermostatSettings[] starsDftThermSettings = null;
	private StarsGetEnergyCompanySettingsResponse starsOperECSettings = null;
	private StarsGetEnergyCompanySettingsResponse starsCustECSettings = null;
	
	private Hashtable starsCustSelLists = null;		// Map String(list name) to StarsSelectionListEntry
	private Hashtable starsWebConfigs = null;		// Map Integer(web config ID) to StarsWebConfig
	private Hashtable starsCustAcctInfos = null;	// Map Integer(account ID) to StarsCustAccountInformation
	private Hashtable starsLMCtrlHists = null;		// Map Integer(group ID) to StarsLMControlHistory
	
	
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
	
	public TimeZone getDefaultTimeZone() {
		if (dftTimeZone == null) {
			dftTimeZone = TimeZone.getTimeZone( getEnergyCompanySetting(EnergyCompanyRole.DEFAULT_TIME_ZONE) );
			if (dftTimeZone == null)
				dftTimeZone = TimeZone.getDefault();
		}
		return dftTimeZone;
	}

	/**
	 * Returns the optOutEventQueue.
	 * @return OptOutEventQueue
	 */
	public OptOutEventQueue getOptOutEventQueue() {
		if (optOutEventQueue == null) {
			try {
				String filename = getEnergyCompanySetting(EnergyCompanyRole.OPTOUT_COMMAND_FILE);
				if (filename != null) {
					optOutEventQueue = new OptOutEventQueue( filename );
					optOutEventQueue.syncFromFile();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return optOutEventQueue;
	}
	
	public SwitchCommandQueue getSwitchCommandQueue() {
		if (switchCommandQueue == null) {
			try {
				switchCommandQueue = new SwitchCommandQueue( getEnergyCompanySetting(EnergyCompanyRole.SWITCH_COMMAND_FILE) );
				switchCommandQueue.syncFromFile();
			}
			catch (IOException e) {
			}
		}
		
		return switchCommandQueue;
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
		
		if (getLiteID() != SOAPServer.DEFAULT_ENERGY_COMPANY_ID) {
			getAllApplianceCategories();
			getAllServiceCompanies();
			getAllInterviewQuestions();
			getAllCustomerFAQs();
		}
	}
	
	public void clear() {
		custAccountInfos = null;
		addresses = null;
		lmPrograms = null;
		inventory = null;
		lmCtrlHists = null;
		appCategories = null;
		workOrders = null;
		serviceCompanies = null;
		selectionLists = null;
		interviewQuestions = null;
		customerFAQs = null;
		dftLMHardwares = null;
		
		nextCallNo = 0;
		nextOrderNo = 0;
		inventoryLoaded = false;
		workOrdersLoaded = false;
		
		dftRouteID = CtiUtilities.NONE_ID;
		dftTimeZone = null;
		optOutEventQueue = null;
		switchCommandQueue = null;
		accountsWithGatewayEndDevice = null;
		
		contactCustAccountInfoMap = null;
		
		starsEnergyCompany = null;
		starsEnrPrograms = null;
		starsCustFAQs = null;
		starsServCompanies = null;
		starsExitQuestions = null;
		starsDftThermSettings = null;
		starsOperECSettings = null;
		starsCustECSettings = null;
		
		starsCustSelLists = null;
		starsWebConfigs = null;
		starsCustAcctInfos = null;
		starsLMCtrlHists = null;
	}
	
	public String getEnergyCompanySetting(int rolePropertyID, String defaultValue) {
		return AuthFuncs.getRolePropertyValue(
				YukonUserFuncs.getLiteYukonUser(getUserID()), rolePropertyID, defaultValue);
	}
	
	public String getEnergyCompanySetting(int rolePropertyID) {
		return getEnergyCompanySetting(rolePropertyID, null);
	}
	
	public LiteYukonGroup getResidentialCustomerGroup() {
		String customerGroupName = getEnergyCompanySetting( EnergyCompanyRole.CUSTOMER_GROUP_NAME );
		return AuthFuncs.getGroup( customerGroupName );
	}
	
	public LiteYukonGroup getWebClientOperatorGroup() {
		String operatorGroupName = getEnergyCompanySetting( EnergyCompanyRole.OPERATOR_GROUP_NAME );
		return AuthFuncs.getGroup( operatorGroupName );
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
	        
			ECToGenericMapping[] items = ECToGenericMapping.getAllMappingItems( getEnergyCompanyID(), YukonSelectionList.TABLE_NAME );
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
	
	public ArrayList getAllSelectionLists(StarsYukonUser user) {
		ArrayList selectionLists = getAllSelectionLists();
		ArrayList userLists = new ArrayList();
		
		synchronized (selectionLists) {
			if (ServerUtils.isOperator( user )) {
				TreeMap listMap = new TreeMap();
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE,
						getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE, true) );
				
				if (AuthFuncs.checkRole(user.getYukonUser(), OddsForControlRole.ROLEID) != null)
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL, true) );
				
				if (AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_GENERAL))
					listMap.put( com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION,
							getYukonSelectionList(com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION, true) );
				
				if (AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_CALL_TRACKING))
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE, true) );
				
				if (AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT))
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD, true) );
				
				if (AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_APPLIANCES) ||
					AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_APPLIANCES_CREATE))
				{
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY, true) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER, true) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION, true) );
					
					ArrayList categories = getAllApplianceCategories();
					ArrayList catDefIDs = new ArrayList();
					
					for (int i = 0; i < categories.size(); i++) {
						LiteApplianceCategory liteAppCat = (LiteApplianceCategory) categories.get(i);
						int catDefID = YukonListFuncs.getYukonListEntry( liteAppCat.getCategoryID() ).getYukonDefID();
						if (catDefIDs.contains( new Integer(catDefID) )) continue;
						catDefIDs.add( new Integer(catDefID) );
						
						if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER) {
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_AC_TONNAGE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_AC_TONNAGE, true) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE, true) );
						}
						else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER) {
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_WH_NUM_OF_GALLONS,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_WH_NUM_OF_GALLONS, true) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_WH_ENERGY_SOURCE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_WH_ENERGY_SOURCE, true) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_WH_LOCATION,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_WH_LOCATION, true) );
						}
						else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL) {
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DF_SWITCH_OVER_TYPE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DF_SWITCH_OVER_TYPE, true) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DF_SECONDARY_SOURCE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DF_SECONDARY_SOURCE, true) );
						}
						else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR) {
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_TYPE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_TYPE, true) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_MFG,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_MFG, true) );
						}
						else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER) {
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GRAIN_DRYER_TYPE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GRAIN_DRYER_TYPE, true) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GD_BIN_SIZE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GD_BIN_SIZE, true) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GD_ENERGY_SOURCE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GD_ENERGY_SOURCE, true) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GD_HORSE_POWER,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GD_HORSE_POWER, true) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GD_HEAT_SOURCE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GD_HEAT_SOURCE, true) );
						}
						else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT) {
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_HEAT_TYPE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_HEAT_TYPE, true) );
						}
						else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP) {
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_TYPE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_TYPE, true) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_SIZE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_SIZE, true) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_HP_STANDBY_SOURCE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HP_STANDBY_SOURCE, true) );
						}
						else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION) {
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE, true) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_IRR_HORSE_POWER,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_HORSE_POWER, true) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_IRR_ENERGY_SOURCE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_ENERGY_SOURCE, true) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_IRR_SOIL_TYPE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_SOIL_TYPE, true) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_LOCATION,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_LOCATION, true) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_VOLTAGE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_VOLTAGE, true) );
						}
					}
				}
				
				if (AuthFuncs.checkRole(user.getYukonUser(), InventoryRole.ROLEID) != null ||
					AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_HARDWARES) ||
					AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_HARDWARES_CREATE))
				{
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, true) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS, true) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_LOCATION,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_LOCATION, true) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE, true) );
				}
				
				if (AuthFuncs.checkRole(user.getYukonUser(), WorkOrderRole.ROLEID) != null ||
					AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_WORK_ORDERS))
				{
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE, true) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS, true) );
				}
				
				if (AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_RESIDENCE)) {
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_RESIDENCE_TYPE,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_RESIDENCE_TYPE, true) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_CONSTRUCTION_MATERIAL,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CONSTRUCTION_MATERIAL, true) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DECADE_BUILT,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DECADE_BUILT, true) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SQUARE_FEET,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SQUARE_FEET, true) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_INSULATION_DEPTH,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_INSULATION_DEPTH, true) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GENERAL_CONDITION,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GENERAL_CONDITION, true) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_COOLING_SYSTEM,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_COOLING_SYSTEM, true) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_HEATING_SYSTEM,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HEATING_SYSTEM, true) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_OCCUPANTS,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_OCCUPANTS, true) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_OWNERSHIP_TYPE,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OWNERSHIP_TYPE, true) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_FUEL_TYPE,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_FUEL_TYPE, true) );
				}
				
				if (AuthFuncs.checkRole(user.getYukonUser(), InventoryRole.ROLEID) != null) {
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_INV_SEARCH_BY,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_INV_SEARCH_BY, true) );
					if (AuthFuncs.checkRoleProperty(user.getYukonUser(), InventoryRole.INVENTORY_SHOW_ALL)) {
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_INV_SORT_BY,
								getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_INV_SORT_BY, true) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY,
								getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY, true) );
					}
				}
				
				if (AuthFuncs.checkRole(user.getYukonUser(), WorkOrderRole.ROLEID) != null) {
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SO_SEARCH_BY,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SO_SEARCH_BY, true) );
					if (AuthFuncs.checkRoleProperty(user.getYukonUser(), WorkOrderRole.WORK_ORDER_SHOW_ALL)) {
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SO_SORT_BY,
								getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SO_SORT_BY, true) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SO_FILTER_BY,
								getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SO_FILTER_BY, true) );
					}
				}
				
				if (AuthFuncs.checkRoleProperty(user.getYukonUser(), AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY)) {
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY, true) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_ANSWER_TYPE,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_ANSWER_TYPE, true) );
				}
				
				Iterator it = listMap.values().iterator();
				while (it.hasNext())
					userLists.add( it.next() );
			}
			else if (ServerUtils.isResidentialCustomer( user )) {
				userLists.add( getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS, true) );
				
				if (AuthFuncs.checkRoleProperty(user.getYukonUser(), ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT)) {
					YukonSelectionList list = getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD_CUS, true);
					if (list != null) userLists.add( list );
				}
			}
		}
		
		return userLists;
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
	
	public synchronized LiteStarsLMHardware getDefaultLMHardware(int hwTypeDefID) {
		if (dftLMHardwares == null)
			dftLMHardwares = new Hashtable();
		
		// For default energy company, the same settings is returned for any hardware types
		if (getLiteID() == SOAPServer.DEFAULT_ENERGY_COMPANY_ID)
			hwTypeDefID = 0;
		
		LiteStarsLMHardware dftLMHardware =
				(LiteStarsLMHardware) dftLMHardwares.get( new Integer(hwTypeDefID) );
		if (dftLMHardware != null) return dftLMHardware;
		
		String sql = "SELECT inv.InventoryID, hw.LMHardwareTypeID FROM ECToInventoryMapping map, "
				   + com.cannontech.database.db.stars.hardware.InventoryBase.TABLE_NAME + " inv, "
				   + com.cannontech.database.db.stars.hardware.LMHardwareBase.TABLE_NAME + " hw "
				   + "WHERE inv.InventoryID = map.InventoryID AND map.EnergyCompanyID = " + getEnergyCompanyID()
				   + " AND hw.InventoryID = inv.InventoryID AND inv.InventoryID < 0";
		
		java.sql.Connection conn = null;
		
		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			if (conn == null)
				throw new java.sql.SQLException("Cannot get database connection");
			
			java.sql.Statement stmt = conn.createStatement();
			java.sql.ResultSet rset = stmt.executeQuery( sql );
			
			while (rset.next()) {
				int invID = rset.getInt(1);
				int hwTypeID = rset.getInt(2);
				
				if (hwTypeDefID == 0 || YukonListFuncs.getYukonListEntry(hwTypeID).getYukonDefID() == hwTypeDefID) {
					com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
							new com.cannontech.database.data.stars.hardware.LMHardwareBase();
					hw.setInventoryID( new Integer(invID) );
					hw.setDbConnection( conn );
					hw.retrieve();
					
					dftLMHardware = new LiteStarsLMHardware();
					StarsLiteFactory.setLiteStarsLMHardware( dftLMHardware, hw );
					dftLMHardwares.put( new Integer(hwTypeDefID), dftLMHardware );
					
					break;
				}
			}
			
			if (rset != null) rset.close();
			
			if (dftLMHardware == null) {
				if (getLiteID() == SOAPServer.DEFAULT_ENERGY_COMPANY_ID) {
					CTILogger.info("No default thermostat settings found for yukondefid = " + hwTypeDefID);
					return null;
				}
	        	
				// Create a default LM hardware (InventoryID < 0),
				// populate the thermostat schedule and manual event table with default values
				int dftInvID = -1;
				
				sql = "SELECT MIN(InventoryID) - 1 FROM InventoryBase";
				rset = stmt.executeQuery( sql );
				if (rset.next())
					dftInvID = rset.getInt(1);
	        	
				if (rset != null) rset.close();
				if (stmt != null) stmt.close();
				
				// Use the hardware type id of the default energy company,
				// so that we won't have a problem deleting a device type
				int hwTypeID = SOAPServer.getDefaultEnergyCompany().getYukonListEntry(hwTypeDefID).getEntryID();
	        	int categoryID = ECUtils.getInventoryCategoryID(hwTypeID, this);
	        	
				com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
						new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				hardware.setInventoryID( new Integer(dftInvID) );
				hardware.getInventoryBase().setCategoryID( new Integer(categoryID) );
				hardware.getInventoryBase().setNotes( "Default Thermostat" );
				hardware.getLMHardwareBase().setLMHardwareTypeID( new Integer(hwTypeID) );
				hardware.getLMHardwareBase().setManufacturerSerialNumber( "0" );
				hardware.setEnergyCompanyID( getEnergyCompanyID() );
				hardware.setDbConnection( conn );
				hardware.add();
				
				if (hwTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_THERMOSTAT) {
					com.cannontech.database.data.stars.event.LMThermostatManualEvent event =
							new com.cannontech.database.data.stars.event.LMThermostatManualEvent();
					event.getLMCustomerEventBase().setEventTypeID( new Integer(
							getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMTHERMOSTAT_MANUAL).getEntryID()) );
					event.getLMCustomerEventBase().setActionID( new Integer(
							getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_MANUAL_OPTION).getEntryID()) );
					event.getLMCustomerEventBase().setEventDateTime( new Date() );
					
					event.getLmThermostatManualEvent().setInventoryID( new Integer(dftInvID) );
					event.getLmThermostatManualEvent().setPreviousTemperature( new Integer(72) );
					event.getLmThermostatManualEvent().setHoldTemperature( "N" );
					event.getLmThermostatManualEvent().setOperationStateID( new Integer(
							getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_DEFAULT).getEntryID()) );
					event.getLmThermostatManualEvent().setFanOperationID( new Integer(
							getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_DEFAULT).getEntryID()) );
					
					event.setEnergyCompanyID( getEnergyCompanyID() );
					event.setDbConnection( conn );
					event.add();
				}
	        	
				dftLMHardware = new LiteStarsLMHardware();
				StarsLiteFactory.setLiteStarsLMHardware( dftLMHardware, hardware );
				dftLMHardwares.put( new Integer(hwTypeDefID), dftLMHardware );
				
				CreateLMHardwareAction.populateThermostatTables( dftLMHardware, SOAPServer.getDefaultEnergyCompany(), conn );
			}
			
			LiteStarsThermostatSettings dftThermSettings = getThermostatSettings( dftLMHardware );
			dftLMHardware.setThermostatSettings( dftThermSettings );
			
			if (dftThermSettings == null || dftThermSettings.getThermostatSeasons().size() == 0 || dftThermSettings.getThermostatManualEvents().size() == 0) {
				LiteStarsLMHardware dftHw = null;
				if (getLiteID() != SOAPServer.DEFAULT_ENERGY_COMPANY_ID)
					dftHw = SOAPServer.getDefaultEnergyCompany().getDefaultLMHardware(0);
				if (dftHw == null) {
					CTILogger.info( "Default thermostat settings not found!!!" );
					return null;
				}
				
				LiteStarsThermostatSettings dftSettings = dftHw.getThermostatSettings();
				if (dftThermSettings == null) {
					dftThermSettings = new LiteStarsThermostatSettings();
					dftThermSettings.setInventoryID( dftSettings.getInventoryID() );
					dftLMHardware.setThermostatSettings( dftThermSettings );
				}
				if (dftThermSettings.getThermostatSeasons().size() == 0)
					dftThermSettings.setThermostatSeasons( dftSettings.getThermostatSeasons() );
				if (dftThermSettings.getThermostatManualEvents().size() == 0)
					dftThermSettings.setThermostatManualEvents( dftSettings.getThermostatManualEvents() );
			}
			
			CTILogger.info( "Default LM hardware loaded for energy company #" + getEnergyCompanyID() );
		}
		catch (Exception e) {
			e.printStackTrace();
			dftLMHardware = null;
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
		
		return dftLMHardware;
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
					   + "AND CallNumber like '" + ServerUtils.AUTO_GEN_NUM_PREC + "%' ORDER BY CallID DESC";
			com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
					sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
					
			try {
				stmt.execute();
				int maxCallNo = 0;
				
				for (int i = 0; i < stmt.getRowCount(); i++) {
					try {
						String callNoStr = (String) stmt.getRow(i)[0];
						int callNo = Integer.parseInt( callNoStr.substring(ServerUtils.AUTO_GEN_NUM_PREC.length()) );
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
					   + "AND OrderNumber like '" + ServerUtils.AUTO_GEN_NUM_PREC + "%' ORDER BY OrderID DESC";
			com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
					sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
					
			try {
				stmt.execute();
				int maxOrderNo = 0;
				
				for (int i = 0; i < stmt.getRowCount(); i++) {
					try {
						String orderNoStr = (String) stmt.getRow(i)[0];
						int orderNo = Integer.parseInt( orderNoStr.substring(ServerUtils.AUTO_GEN_NUM_PREC.length()) );
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
	
	public ArrayList getAllAddresses() {
		if (addresses == null)
			addresses = new ArrayList();
		
		return addresses;
	}
	
	public ArrayList getAllInventory() {
		if (inventory == null)
			inventory = new ArrayList();
			
		return inventory;
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
	
	public LiteContact getContact(int contactID, LiteStarsCustAccountInformation liteAcctInfo) {
		if (liteAcctInfo != null) {
			Hashtable contactAcctInfoMap = getContactCustAccountInfoMap();
			synchronized (contactAcctInfoMap) { contactAcctInfoMap.put(new Integer(contactID), liteAcctInfo); }
		}
		
		return ContactFuncs.getContact( contactID );
	}
	
	public void addContact(LiteContact liteContact, LiteStarsCustAccountInformation liteAcctInfo) {
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized (cache) {
			cache.getAllContacts().add( liteContact );
		}
		
		if (liteAcctInfo != null) {
			Hashtable contactAcctInfoMap = getContactCustAccountInfoMap();
			synchronized (contactAcctInfoMap) { contactAcctInfoMap.put(new Integer(liteContact.getContactID()), liteAcctInfo); }
		}
		
		ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_ADD );
	}

	public LiteContact deleteContact(int contactID) {
		LiteContact liteContact = ContactFuncs.getContact( contactID );
		
		if (liteContact != null) {
			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
			synchronized (cache) {
				cache.getAllContacts().remove( liteContact );
			}
			
			Hashtable contactAcctInfoMap = getContactCustAccountInfoMap();
			synchronized (contactAcctInfoMap) { contactAcctInfoMap.remove(new Integer(contactID)); }
			
			ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_DELETE );
		}
		
		return liteContact;
	}
	
	private Hashtable getContactCustAccountInfoMap() {
		if (contactCustAccountInfoMap == null)
			contactCustAccountInfoMap = new Hashtable();
		
		return contactCustAccountInfoMap;
	}
	
	public LiteStarsCustAccountInformation getCustAccountInfoByContact(int contactID) {
		Hashtable contactAcctInfoMap = getContactCustAccountInfoMap();
		
		synchronized (contactAcctInfoMap) {
			return (LiteStarsCustAccountInformation) contactAcctInfoMap.get(new Integer(contactID));
		}
	}
	
	private LiteAddress getAddress(int addressID, boolean autoLoad) {
		ArrayList addressList = getAllAddresses();
		LiteAddress liteAddr = null;
		
		synchronized (addressList) {
			for (int i = 0; i < addressList.size(); i++) {
				liteAddr = (LiteAddress) addressList.get(i);
				if (liteAddr.getAddressID() == addressID)
					return liteAddr;
			}
		}
		
		if (autoLoad) {
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
		}
		
		return null;
	}
	
	public LiteAddress getAddress(int addressID) {
		return getAddress( addressID, true );
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
	
	private LiteInventoryBase loadInventory(int invID, java.sql.Connection conn) throws java.sql.SQLException {
		com.cannontech.database.db.stars.hardware.InventoryBase invDB =
				new com.cannontech.database.db.stars.hardware.InventoryBase();
		invDB.setInventoryID( new Integer(invID) );
		invDB.setDbConnection( conn );
		invDB.retrieve();
		
		LiteInventoryBase liteInv = null;
		
		if (ECUtils.isLMHardware( invDB.getCategoryID().intValue() )) {
			com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
					new com.cannontech.database.data.stars.hardware.LMHardwareBase();
			hardware.setInventoryBase( invDB );
			
			com.cannontech.database.db.stars.hardware.LMHardwareBase hardwareDB = hardware.getLMHardwareBase();
			hardwareDB.setInventoryID( invDB.getInventoryID() );
			hardwareDB.setDbConnection( conn );
			hardwareDB.retrieve();
			
			liteInv = new LiteStarsLMHardware();
			StarsLiteFactory.setLiteStarsLMHardware( (LiteStarsLMHardware)liteInv, hardware );
		}
		else {
			liteInv = new LiteInventoryBase();
			StarsLiteFactory.setLiteInventoryBase( liteInv, invDB );
		}
		
		addInventory( liteInv );
		return liteInv;
	}
	
	public ArrayList loadAllInventory() {
		ArrayList inventory = getAllInventory();
		if (inventoryLoaded) return inventory;
		
		java.sql.Connection conn = null;
		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			String sql = "SELECT InventoryID FROM ECToInventoryMapping WHERE EnergyCompanyID = ? AND InventoryID >= 0";
			java.sql.PreparedStatement stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, getLiteID() );
			java.sql.ResultSet rset = stmt.executeQuery();
			
			while (rset.next()) {
				int invID = rset.getInt(1);
				if (getInventoryBrief(invID, false) != null) continue;
				
				LiteInventoryBase liteInv = loadInventory( invID, conn );
			}
		}
		catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
		
		inventoryLoaded = true;
		return inventory;
	}
	
	public LiteInventoryBase getInventoryBrief(int inventoryID, boolean autoLoad) {
		ArrayList inventory = getAllInventory();
		LiteInventoryBase liteInv = null;
		
		synchronized (inventory) {
			for (int i = 0; i < inventory.size(); i++) {
				liteInv = (LiteInventoryBase) inventory.get(i);
				if (liteInv.getInventoryID() == inventoryID)
					return liteInv;
			}
		}
		
		if (autoLoad) {
			java.sql.Connection conn = null;
			
			try {
				conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
				liteInv = loadInventory( inventoryID, conn );
				return liteInv;
			}
			catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
			finally {
				try {
					if (conn != null) conn.close();
				}
				catch (java.sql.SQLException e) {}
			}
		}
		
		return null;
	}
	
	public LiteInventoryBase getInventory(int inventoryID, boolean autoLoad) {
		LiteInventoryBase liteInv = getInventoryBrief(inventoryID, autoLoad);
		
		if (liteInv != null && !liteInv.isExtended())
			StarsLiteFactory.extendLiteInventoryBase( liteInv, this );
		
		return liteInv;
	}
	
	public void addInventory(LiteInventoryBase liteInv) {
		ArrayList inventory = getAllInventory();
		synchronized (inventory) { inventory.add( liteInv ); }
	}
	
	public LiteInventoryBase deleteInventory(int invID) {
		ArrayList inventory = getAllInventory();
		
		synchronized (inventory) {
			for (int i = 0; i < inventory.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(i);
				if (liteInv.getInventoryID() == invID) {
					inventory.remove(i);
					return liteInv;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Search for LM hardware with specified device type and serial # 
	 */
	public LiteStarsLMHardware searchForLMHardware(int deviceType, String serialNo) {
		ArrayList inventory = getAllInventory();
		java.sql.Connection conn = null;
		
		synchronized (inventory) {
			for (int i = 0; i < inventory.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(i);
				if (liteInv.getInventoryID() < 0) continue;
				if (!(liteInv instanceof LiteStarsLMHardware)) continue;
				
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) liteInv;
				if (liteHw.getLmHardwareTypeID() == deviceType &&
					liteHw.getManufacturerSerialNumber().equalsIgnoreCase( serialNo ))
				{
					return liteHw;
				}
			}
		}
		
		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			int[] invIDs = com.cannontech.database.db.stars.hardware.LMHardwareBase.searchForLMHardware(
					deviceType, serialNo, getLiteID(), conn );
			
			if (invIDs != null && invIDs.length > 0) {
				com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
						new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				hw.setInventoryID( new Integer(invIDs[0]) );
				hw.setDbConnection( conn );
				hw.retrieve();
		
				LiteStarsLMHardware liteHw = new LiteStarsLMHardware();
				StarsLiteFactory.setLiteStarsLMHardware( liteHw, hw );
				addInventory( liteHw );
				
				return liteHw;
			}
			
			return null;
		}
		catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
		
		return null;
	}
	
	/**
	 * Search for device with the specified category and device name.
	 */
	public LiteInventoryBase searchForDevice(int categoryID, String deviceName) {
		ArrayList inventory = getAllInventory();
		
		synchronized (inventory) {
			for (int i = 0; i < inventory.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(i);
				if (liteInv.getInventoryID() < 0) continue;
				if (liteInv.getDeviceID() == CtiUtilities.NONE_ID) continue;
				
				if (liteInv.getCategoryID() == categoryID &&
					PAOFuncs.getYukonPAOName( liteInv.getDeviceID() ).equalsIgnoreCase( deviceName ))
					return liteInv;
			}
		}
		
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		if (ECUtils.isMCT( categoryID )) {
			synchronized (cache) {
				java.util.List mctList = cache.getAllMCTs();
				
				for (int i = 0; i < mctList.size(); i++) {
					LiteYukonPAObject litePao = (LiteYukonPAObject) mctList.get(i);
					
					if (PAOFuncs.getYukonPAOName( litePao.getYukonID() ).equalsIgnoreCase( deviceName )) {
						// Create a temporary LiteInventoryBase object
						LiteInventoryBase liteInv = new LiteInventoryBase();
						liteInv.setInventoryID( -1 );
						liteInv.setDeviceID( litePao.getYukonID() );
						liteInv.setCategoryID( categoryID );
						return liteInv;
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Search the inventory for device with the specified device ID 
	 */
	public LiteInventoryBase getDevice(int deviceID) {
		ArrayList inventory = getAllInventory();
		
		synchronized (inventory) {
			for (int i = 0; i < inventory.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(i);
				if (liteInv.getDeviceID() == deviceID)
					return liteInv;
			}
		}
		
		return null;
	}
	
	public LiteStarsLMControlHistory getLMControlHistory(int groupID) {
		if (groupID == CtiUtilities.NONE_ID) return null;
		
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
	
	public ArrayList loadWorkOrders() {
		ArrayList workOrders = getAllWorkOrders();
		if (workOrdersLoaded) return workOrders;
		
		java.sql.Connection conn = null;
		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			String sql = "SELECT WorkOrderID FROM ECToWorkOrderMapping WHERE EnergyCompanyID = ?";
			java.sql.PreparedStatement stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, getLiteID() );
			java.sql.ResultSet rset = stmt.executeQuery();
			
			while (rset.next()) {
				int orderID = rset.getInt(1);
				if (getWorkOrderBase(orderID, false) != null) continue;
				
				com.cannontech.database.db.stars.report.WorkOrderBase order =
						new com.cannontech.database.db.stars.report.WorkOrderBase();
				order.setOrderID( new Integer(orderID) );
				order.setDbConnection( conn );
				order.retrieve();
				
				LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) StarsLiteFactory.createLite( order );
				addWorkOrderBase( liteOrder );
			}
		}
		catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
		
		workOrdersLoaded = true;
		return workOrders;
	}
	
	public LiteWorkOrderBase getWorkOrderBase(int orderID, boolean autoLoad) {
		ArrayList workOrders = getAllWorkOrders();
		LiteWorkOrderBase workOrder = null;
        
		synchronized (workOrders) {
			for (int i = 0; i < workOrders.size(); i++) {
				workOrder = (LiteWorkOrderBase) workOrders.get(i);
				if (workOrder.getOrderID() == orderID)
					return workOrder;
			}
		}
        
        if (autoLoad) {
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
	
	public LiteStarsThermostatSettings getThermostatSettings(LiteStarsLMHardware liteHw) {
		LiteStarsThermostatSettings settings = new LiteStarsThermostatSettings();
		settings.setInventoryID( liteHw.getInventoryID() );
        
		com.cannontech.database.data.stars.hardware.LMThermostatSeason[] seasons =
				com.cannontech.database.data.stars.hardware.LMThermostatSeason.getAllLMThermostatSeasons( new Integer(liteHw.getInventoryID()) );
		
		// Check to see if thermostat season entries are complete, and if not, re-populate thermostat tables
		boolean thermTableComplete = true;
		if (seasons == null || seasons.length != 2)
			thermTableComplete = false;
		else {
			for (int i = 0; i < seasons.length; i++) {
				int numSeasonEntries = seasons[i].getLMThermostatSeasonEntries().size();
				if (liteHw.isOneWayThermostat() && numSeasonEntries != 12 ||
					liteHw.isTwoWayThermostat() && numSeasonEntries != 28)
				{
					thermTableComplete = false;
					break;
				}
			}
		}
		
		if (!thermTableComplete) {
			java.sql.Connection conn = null;
			try {
				conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
				if (seasons != null) {
					for (int i = 0; i < seasons.length; i++) {
						seasons[i].setDbConnection( conn );
						seasons[i].delete();
					}
				}
				
				if (liteHw.getInventoryID() >= 0)
					seasons = CreateLMHardwareAction.populateThermostatTables( liteHw, this, conn );
				else
					seasons = CreateLMHardwareAction.populateThermostatTables( liteHw, SOAPServer.getDefaultEnergyCompany(), conn );
			}
			catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
			finally {
				try {
					if (conn != null) conn.close();
				}
				catch (java.sql.SQLException e) {}
			}
		}
        
		if (seasons != null) {
			for (int i = 0; i < seasons.length; i++) {
				LiteLMThermostatSeason liteSeason = StarsLiteFactory.createLiteLMThermostatSeason( seasons[i] );
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
        
		if (liteHw.isTwoWayThermostat()) {
			settings.setDynamicData( new LiteStarsGatewayEndDevice() );
			settings.updateThermostatSettings( liteHw, this );
		}
        
		return settings;
	}
	
	public void updateThermostatSettings(LiteStarsCustAccountInformation liteAcctInfo) {
		StarsCustAccountInformation starsAcctInfo = getStarsCustAccountInformation( liteAcctInfo );
		
		for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
			int invID = ((Integer) liteAcctInfo.getInventories().get(i)).intValue();
			
			LiteInventoryBase liteInv = getInventory( invID, true );
			if (!(liteInv instanceof LiteStarsLMHardware)) continue;
			
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) liteInv;
			if (!liteHw.isTwoWayThermostat()) continue;
			
			LiteStarsThermostatSettings liteSettings = liteHw.getThermostatSettings();
			liteSettings.updateThermostatSettings( liteHw, this );
			
			StarsInventories starsInvs = starsAcctInfo.getStarsInventories();
			for (int j = 0; j < starsInvs.getStarsInventoryCount(); j++) {
				if (starsInvs.getStarsInventory(j).getInventoryID() == invID) {
					StarsThermostatSettings starsSettings = starsInvs.getStarsInventory(j).getLMHardware().getStarsThermostatSettings();
					
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
	
	private LiteStarsCustAccountInformation addBriefCustAccountInfo(com.cannontech.database.data.stars.customer.CustomerAccount account) {
		com.cannontech.database.data.stars.customer.AccountSite site = account.getAccountSite();
        
		LiteStarsCustAccountInformation liteAcctInfo = new LiteStarsCustAccountInformation( account.getCustomerAccount().getAccountID().intValue() );
		liteAcctInfo.setCustomerAccount( (LiteCustomerAccount) StarsLiteFactory.createLite(account.getCustomerAccount()) );
		liteAcctInfo.setAccountSite( (LiteAccountSite) StarsLiteFactory.createLite(site.getAccountSite()) );
		liteAcctInfo.setSiteInformation( (LiteSiteInformation) StarsLiteFactory.createLite(site.getSiteInformation().getSiteInformation()) );
		
		if (account.getCustomer() instanceof com.cannontech.database.data.customer.CICustomerBase) {
			LiteCICustomer liteCI = new LiteCICustomer();
			StarsLiteFactory.setLiteCICustomer( liteCI, (com.cannontech.database.data.customer.CICustomerBase)account.getCustomer() );
			liteAcctInfo.setCustomer( liteCI );
		}
		else {
			LiteCustomer liteCustomer = new LiteCustomer();
			StarsLiteFactory.setLiteCustomer( liteCustomer, account.getCustomer() );
			liteAcctInfo.setCustomer( liteCustomer );
		}
		
		Hashtable contactAcctInfoMap = getContactCustAccountInfoMap();
		synchronized (contactAcctInfoMap) {
			Vector contacts = liteAcctInfo.getCustomer().getAdditionalContacts();
			for (int i = 0; i < contacts.size(); i++)
				contactAcctInfoMap.put( new Integer(((LiteContact)contacts.get(i)).getContactID()), liteAcctInfo );
		}
        
		ArrayList appliances = new ArrayList();
		for (int i = 0; i < account.getApplianceVector().size(); i++) {
			LiteStarsAppliance liteApp = new LiteStarsAppliance();
			liteApp.setApplianceID( ((Integer) account.getApplianceVector().get(i)).intValue() );
			appliances.add( liteApp );
		}
		liteAcctInfo.setAppliances( appliances );
        
		ArrayList inventories = new ArrayList();
		for (int i = 0; i < account.getInventoryVector().size(); i++)
			inventories.add( account.getInventoryVector().get(i) );
		liteAcctInfo.setInventories( inventories );
        
		com.cannontech.database.db.customer.Address streetAddr = site.getStreetAddress();
		LiteAddress liteAddr = (LiteAddress) StarsLiteFactory.createLite( streetAddr );
		addAddress( liteAddr );
		
		com.cannontech.database.db.customer.Address billAddr = account.getBillingAddress();
		liteAddr = (LiteAddress) StarsLiteFactory.createLite( billAddr );
		addAddress( liteAddr );
        
		ArrayList custAcctInfoList = getAllCustAccountInformation();
		synchronized (custAcctInfoList) { custAcctInfoList.add( liteAcctInfo ); }
		return liteAcctInfo;
	}
	
	private void extendCustAccountInfo(LiteStarsCustAccountInformation liteAcctInfo) {
		java.sql.Connection conn = null;
		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			com.cannontech.database.db.stars.customer.CustomerResidence residence =
					com.cannontech.database.db.stars.customer.CustomerResidence.getCustomerResidence( new Integer(liteAcctInfo.getAccountSite().getAccountSiteID()) );
			if (residence != null)
				liteAcctInfo.setCustomerResidence( (LiteCustomerResidence) StarsLiteFactory.createLite(residence) );
			
			ArrayList appliances = liteAcctInfo.getAppliances();
			for (int i = 0; i < appliances.size(); i++) {
				LiteStarsAppliance liteApp = (LiteStarsAppliance) appliances.get(i);
	            
				com.cannontech.database.data.stars.appliance.ApplianceBase appliance =
						new com.cannontech.database.data.stars.appliance.ApplianceBase();
				appliance.setApplianceID( new Integer(liteApp.getApplianceID()) );
				appliance.setDbConnection( conn );
				appliance.retrieve();
	            
				liteApp = StarsLiteFactory.createLiteStarsAppliance( appliance, this );
				appliances.set(i, liteApp);
			}
	
			ArrayList inventories = liteAcctInfo.getInventories();
			for (int i = 0; i < inventories.size(); i++) {
				Integer invID = (Integer) inventories.get(i);
				getInventory( invID.intValue(), true );
			}
			
			ArrayList allProgs = getAllLMPrograms();
			int[] allProgIDs = new int[ allProgs.size() ];
			for (int i = 0; i < allProgs.size(); i++)
				allProgIDs[i] = ((LiteLMProgram) allProgs.get(i)).getProgramID();
			Arrays.sort( allProgIDs );
			
			ArrayList progHist = new ArrayList();
			com.cannontech.database.data.stars.event.LMProgramEvent[] events =
					com.cannontech.database.data.stars.event.LMProgramEvent.getAllLMProgramEvents( new Integer(liteAcctInfo.getLiteID()), conn );
			
			if (events != null) {
				for (int i = 0; i < events.length; i++) {
					LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) StarsLiteFactory.createLite( events[i] );
					if (Arrays.binarySearch( allProgIDs, liteEvent.getProgramID() ) >= 0)
						progHist.add( liteEvent );
				}
			}
			liteAcctInfo.setProgramHistory( progHist );
			
			ArrayList programs = new ArrayList();
			for (int i = 0; i < appliances.size(); i++) {
				LiteStarsAppliance liteApp = (LiteStarsAppliance) appliances.get(i);
				int progID = liteApp.getLmProgramID();
				if (progID == 0) continue;
	            
				LiteLMProgram liteProg = getLMProgram( progID );
				LiteStarsLMProgram prog = new LiteStarsLMProgram( liteProg );
				
				prog.setGroupID( liteApp.getAddressingGroupID() );
	            prog.updateProgramStatus( progHist );
				getLMControlHistory( liteApp.getAddressingGroupID() );
				
				programs.add( prog );
			}
			liteAcctInfo.setLmPrograms( programs );
	        
			StarsCallReport[] calls = StarsFactory.getStarsCallReports( new Integer(liteAcctInfo.getLiteID()) );
			if (calls != null) {
				liteAcctInfo.setCallReportHistory( new ArrayList() );
				for (int i = 0; i < calls.length; i++)
					liteAcctInfo.getCallReportHistory().add( calls[i] );
			}
				
			int[] orderIDs = com.cannontech.database.db.stars.report.WorkOrderBase.searchByAccountID(liteAcctInfo.getLiteID(), conn);
			if (orderIDs != null) {
				liteAcctInfo.setServiceRequestHistory( new ArrayList() );
				for (int i = 0; i < orderIDs.length; i++) {
					getWorkOrderBase(orderIDs[i], true);
					liteAcctInfo.getServiceRequestHistory().add( new Integer(orderIDs[i]) );
				}
			}
	
			liteAcctInfo.setExtended( true );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
	}
	
	public LiteStarsCustAccountInformation getBriefCustAccountInfo(int accountID, boolean autoLoad) {
		ArrayList custAcctInfoList = getAllCustAccountInformation();
		LiteStarsCustAccountInformation liteAcctInfo = null;
		
		synchronized (custAcctInfoList) {
			for (int i = 0; i < custAcctInfoList.size(); i++) {
				liteAcctInfo = (LiteStarsCustAccountInformation) custAcctInfoList.get(i);
				if (liteAcctInfo.getCustomerAccount().getAccountID() == accountID)
					return liteAcctInfo;
			}
		}
		
		if (autoLoad) {
			try {
				com.cannontech.database.data.stars.customer.CustomerAccount account =
						new com.cannontech.database.data.stars.customer.CustomerAccount();
				account.setAccountID( new Integer(accountID) );
				account = (com.cannontech.database.data.stars.customer.CustomerAccount)
						Transaction.createTransaction( Transaction.RETRIEVE, account ).execute();
				
				return addBriefCustAccountInfo( account );
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public LiteStarsCustAccountInformation getCustAccountInformation(int accountID, boolean autoLoad) {
		LiteStarsCustAccountInformation liteAcctInfo = getBriefCustAccountInfo( accountID, autoLoad );
		if (liteAcctInfo != null && !liteAcctInfo.isExtended())
			extendCustAccountInfo( liteAcctInfo );
		
		return liteAcctInfo;
	}
	
	public LiteStarsCustAccountInformation addCustAccountInformation(com.cannontech.database.data.stars.customer.CustomerAccount account) {
		LiteStarsCustAccountInformation liteAcctInfo = addBriefCustAccountInfo( account );
		if (liteAcctInfo != null)
			extendCustAccountInfo( liteAcctInfo );
        
		return liteAcctInfo;
	}
	
	public void deleteCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo) {
		// Remove from opt out event queue
		getOptOutEventQueue().removeEvents( liteAcctInfo.getAccountID() );
		
		// Remove from GatewayEndDevice account list
		ArrayList accountList = getAccountsWithGatewayEndDevice();
		synchronized (accountList) {
			if (accountList.contains(liteAcctInfo)) accountList.remove( liteAcctInfo );
		}
    	
		// Remote all contacts from the cache
		deleteContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
		Vector contacts = liteAcctInfo.getCustomer().getAdditionalContacts();
		for (int i = 0; i < contacts.size(); i++)
			deleteContact( ((LiteContact)contacts.get(i)).getContactID() );
		
		// Remove all addresses from the cache
		deleteAddress( liteAcctInfo.getCustomerAccount().getBillingAddressID() );
		deleteAddress( liteAcctInfo.getAccountSite().getStreetAddressID() );
		
		// Refresh all inventory information
		for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
			int invID = ((Integer) liteAcctInfo.getInventories().get(i)).intValue();
			deleteInventory( invID );
			getInventoryBrief( invID, true );
		}
		
		// Remove all work orders from the cache
		for (int i = 0; i < liteAcctInfo.getServiceRequestHistory().size(); i++) {
			int orderID = ((Integer) liteAcctInfo.getServiceRequestHistory().get(i)).intValue();
			deleteWorkOrderBase( orderID );
		}
		
		// Remove the customer account from custAccountInfos
		getAllCustAccountInformation().remove( liteAcctInfo );
	}
	
	public LiteStarsCustAccountInformation searchByAccountNumber(String accountNo) {
		ArrayList custAcctInfoList = getAllCustAccountInformation();
		for (int i = 0; i < custAcctInfoList.size(); i++) {
			LiteStarsCustAccountInformation accountInfo = (LiteStarsCustAccountInformation) custAcctInfoList.get(i);
			if (accountInfo.getCustomerAccount().getAccountNumber().equalsIgnoreCase( accountNo )) {
				if ( !accountInfo.isExtended() )
					extendCustAccountInfo( accountInfo );
				return accountInfo;
			}
		}
		
		try {
			int[] accountIDs = com.cannontech.database.db.stars.customer.CustomerAccount.searchByAccountNumber( getEnergyCompanyID(), accountNo );
			if (accountIDs == null || accountIDs.length == 0) return null;
			
			// There shouldn't be more than one customer accounts with the same account number
			com.cannontech.database.data.stars.customer.CustomerAccount account =
					new com.cannontech.database.data.stars.customer.CustomerAccount();
			account.setAccountID( new Integer(accountIDs[0]) );
			account = (com.cannontech.database.data.stars.customer.CustomerAccount)
					Transaction.createTransaction(Transaction.RETRIEVE, account).execute();
			
			return addCustAccountInformation( account );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/* The following methods are only used when SOAPClient exists locally */
	
	public StarsGetEnergyCompanySettingsResponse getStarsEnergyCompanySettings(StarsYukonUser user) {
		if (ServerUtils.isOperator(user)) {
			if (starsOperECSettings == null) {
				starsOperECSettings = new StarsGetEnergyCompanySettingsResponse();
				starsOperECSettings.setStarsEnergyCompany( getStarsEnergyCompany() );
				starsOperECSettings.setStarsEnrollmentPrograms( getStarsEnrollmentPrograms() );
				starsOperECSettings.setStarsCustomerSelectionLists( getStarsCustomerSelectionLists(user) );
				starsOperECSettings.setStarsServiceCompanies( getStarsServiceCompanies() );
				starsOperECSettings.setStarsCustomerFAQs( getStarsCustomerFAQs() );
				starsOperECSettings.setStarsExitInterviewQuestions( getStarsExitInterviewQuestions() );
				starsOperECSettings.setStarsDefaultThermostatSettings( getStarsDefaultThermostatSettings() );
			}
			
			return starsOperECSettings;
		}
		else if (ServerUtils.isResidentialCustomer(user)) {
			if (starsCustECSettings == null) {
				starsCustECSettings = new StarsGetEnergyCompanySettingsResponse();
				starsCustECSettings.setStarsEnergyCompany( getStarsEnergyCompany() );
				starsCustECSettings.setStarsEnrollmentPrograms( getStarsEnrollmentPrograms() );
				starsCustECSettings.setStarsCustomerSelectionLists( getStarsCustomerSelectionLists(user) );
				starsCustECSettings.setStarsCustomerFAQs( getStarsCustomerFAQs() );
				starsCustECSettings.setStarsExitInterviewQuestions( getStarsExitInterviewQuestions() );
				starsCustECSettings.setStarsDefaultThermostatSettings( getStarsDefaultThermostatSettings() );
			}
			
			return starsCustECSettings;
		}
		
		return null;
	}
	
	public StarsEnergyCompany getStarsEnergyCompany() {
		if (starsEnergyCompany == null) {
			starsEnergyCompany = new StarsEnergyCompany();
			StarsLiteFactory.setStarsEnergyCompany( starsEnergyCompany, this );
		}
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
		
		if (ServerUtils.isOperator( starsUser )) {
			for (int i = 0; i < OPERATOR_SELECTION_LISTS.length; i++)
				starsLists.addStarsCustSelectionList( getStarsCustSelectionList(OPERATOR_SELECTION_LISTS[i]) );
		}
		else if (ServerUtils.isResidentialCustomer( starsUser )) {
			// Currently the consumer side only need opt out period list and hardware status list
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
		
		return starsLists;
	}
	
	public StarsEnrollmentPrograms getStarsEnrollmentPrograms() {
		if (starsEnrPrograms == null)
			starsEnrPrograms = StarsLiteFactory.createStarsEnrollmentPrograms(
					getAllApplianceCategories(), this );
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
				
				starsServCompany = new StarsServiceCompany();
				StarsLiteFactory.setStarsServiceCompany(starsServCompany, liteServCompany, this);
				starsServCompanies.addStarsServiceCompany( starsServCompany );
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
	
	private void updateStarsDefaultThermostatSettings() {
		boolean hasBasic = false;
		boolean hasEpro = false;
		
		YukonSelectionList devTypeList = getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, false);
		for (int i = 0; i < devTypeList.getYukonListEntries().size(); i++) {
			YukonListEntry entry = (YukonListEntry) devTypeList.getYukonListEntries().get(i);
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_THERMOSTAT)
				hasBasic = true;
			else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO)
				hasEpro = true;
		}
		
		ArrayList thermSettingsList = new ArrayList();
		if (hasBasic) {
			StarsDefaultThermostatSettings starsThermSettings = new StarsDefaultThermostatSettings();
			StarsLiteFactory.setStarsThermostatSettings(
					starsThermSettings, getDefaultLMHardware(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_THERMOSTAT), this );
			thermSettingsList.add( starsThermSettings );
		}
		if (hasEpro) {
			StarsDefaultThermostatSettings starsThermSettings = new StarsDefaultThermostatSettings();
			StarsLiteFactory.setStarsThermostatSettings(
					starsThermSettings, getDefaultLMHardware(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO), this );
			thermSettingsList.add( starsThermSettings );
		}
		
		starsDftThermSettings = new StarsDefaultThermostatSettings[thermSettingsList.size()];
		thermSettingsList.toArray( starsDftThermSettings );
		
		if (starsOperECSettings != null)
			starsOperECSettings.setStarsDefaultThermostatSettings( starsDftThermSettings );
		if (starsCustECSettings != null)
			starsCustECSettings.setStarsDefaultThermostatSettings( starsDftThermSettings );
	}
	
	public StarsDefaultThermostatSettings[] getStarsDefaultThermostatSettings() {
		if (starsDftThermSettings == null)
			updateStarsDefaultThermostatSettings();
		return starsDftThermSettings;
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
				starsAcctInfo = StarsLiteFactory.createStarsCustAccountInformation( liteAcctInfo, this, true );
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
				StarsLiteFactory.setStarsCustAccountInformation( starsAcctInfo, liteAcctInfo, this, true );
			
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
	
	private void setControlSummary(StarsLMControlHistory starsCtrlHist) {
		ControlSummary summary = new ControlSummary();
		int dailyTime = 0;
		int monthlyTime = 0;
		int seasonalTime = 0;
		int annualTime = 0;
		
		Date pastDay = LMControlHistoryUtil.getPeriodStartTime( StarsCtrlHistPeriod.PASTDAY, getDefaultTimeZone() );
		Date pastMonth = LMControlHistoryUtil.getPeriodStartTime( StarsCtrlHistPeriod.PASTMONTH, getDefaultTimeZone() );
		Date pastYear = LMControlHistoryUtil.getPeriodStartTime( StarsCtrlHistPeriod.PASTYEAR, getDefaultTimeZone() );
		
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
				setControlSummary( starsCtrlHist );
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
				setControlSummary( starsCtrlHist );
			}
			
			return starsCtrlHist;
		}
	}

}
