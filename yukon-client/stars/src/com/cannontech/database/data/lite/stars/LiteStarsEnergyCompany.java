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
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.stars.hardware.LMThermostatSchedule;
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
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.OptOutEventQueue;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.task.LoadInventoryTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCallReport;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsCustomerFAQs;
import com.cannontech.stars.xml.serialize.StarsCustomerSelectionLists;
import com.cannontech.stars.xml.serialize.StarsDefaultThermostatSchedules;
import com.cannontech.stars.xml.serialize.StarsEnergyCompany;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestion;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestions;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.StarsServiceCompanies;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.stars.xml.serialize.StarsThermostatProgram;
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
	private int userID = com.cannontech.user.UserUtils.USER_STARS_DEFAULT_ID;
	
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
	
	// List of operator login IDs (Integer)
	private ArrayList operatorLoginIDs = null;
	
	// Map of hardware type yukondefid (Integer) to LiteLMThermostatSchedule
	private Hashtable dftThermSchedules = null;
	
	private int nextCallNo = 0;
	private int nextOrderNo = 0;
	private boolean initiated = false;
	private boolean inventoryLoaded = false;
	private boolean workOrdersLoaded = false;
	private boolean hierarchyLoaded = false;
	
	// When the energy company is initiated, this object is created to load the inventory
	// It will be set to null and inventoryLoaded set to true after the loading is done
	private LoadInventoryTask loadInvTask = null;
	
	private int dftRouteID = CtiUtilities.NONE_ID;
	private TimeZone dftTimeZone = null;
	private int operDftGroupID = com.cannontech.database.db.user.YukonGroup.EDITABLE_MIN_GROUP_ID - 1;
	
	private OptOutEventQueue optOutEventQueue = null;
	private SwitchCommandQueue switchCommandQueue = null;
	
	// Map of contact ID to customer account information (Integer, LiteStarsCustAccountInformation)
	private Hashtable contactCustAccountInfoMap = null;
	
	// Cached XML messages
	private StarsEnergyCompany starsEnergyCompany = null;
	private StarsEnrollmentPrograms starsEnrPrograms = null;
	private StarsCustomerFAQs starsCustFAQs = null;
	private StarsServiceCompanies starsServCompanies = null;
	private StarsExitInterviewQuestions starsExitQuestions = null;
	private StarsDefaultThermostatSchedules starsDftThermSchedules = null;
	private StarsEnergyCompanySettings starsOperECSettings = null;
	private StarsEnergyCompanySettings starsCustECSettings = null;
	private StarsCustomerSelectionLists starsOperSelLists = null;
	private StarsCustomerSelectionLists starsCustSelLists = null;
	
	private Hashtable starsSelectionLists = null;	// Map String(list name) to StarsSelectionListEntry
	private Hashtable starsWebConfigs = null;		// Map Integer(web config ID) to StarsWebConfig
	private Hashtable starsCustAcctInfos = null;	// Map Integer(account ID) to StarsCustAccountInformation
	private Hashtable starsLMCtrlHists = null;		// Map Integer(group ID) to StarsLMControlHistory
	
	private ArrayList activeAccounts = null;		// List of StarsCustAccountInformation
	
	// Energy company hierarchy
	private LiteStarsEnergyCompany parent = null;
	private ArrayList children = null;
	private ArrayList memberLoginIDs = null;
	
	
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
	
	public void setDefaultRouteID(int routeID) {
		dftRouteID = routeID;
	}
	
	public TimeZone getDefaultTimeZone() {
		if (dftTimeZone == null) {
			String tz = getEnergyCompanySetting(EnergyCompanyRole.DEFAULT_TIME_ZONE);
			if (tz != null)
				dftTimeZone = TimeZone.getTimeZone( tz );
			if (dftTimeZone == null)
				dftTimeZone = TimeZone.getDefault();
		}
		
		return dftTimeZone;
	}

	/**
	 * Returns the opt out event queue.
	 * @return OptOutEventQueue
	 */
	public OptOutEventQueue getOptOutEventQueue() {
		if (optOutEventQueue == null) {
			try {
				optOutEventQueue = OptOutEventQueue.getInstance();
			}
			catch (IOException e) {
				CTILogger.error( e.getMessage(), e );
			}
		}
		
		return optOutEventQueue;
	}
	
	/**
	 * Returns the switch command queue
	 * @return SwitchCommandQueue
	 */
	public SwitchCommandQueue getSwitchCommandQueue() {
		if (switchCommandQueue == null) {
			try {
				switchCommandQueue = SwitchCommandQueue.getInstance();
			}
			catch (IOException e) {
				CTILogger.error( e.getMessage(), e );
			}
		}
		
		return switchCommandQueue;
	}
	
	
	public synchronized boolean isInitiated() {
		return initiated;
	}
	
	public synchronized void init() {
		if (initiated) return;
		
		getAllSelectionLists();
		
		if (!ECUtils.isDefaultEnergyCompany( this )) {
			getAllApplianceCategories();
			getAllServiceCompanies();
			getAllInterviewQuestions();
			getAllCustomerFAQs();
			
			// Load the inventory when energy company is initiated
			if (!inventoryLoaded && loadInvTask == null) {
				loadInvTask = new LoadInventoryTask( this );
				new Thread( loadInvTask ).start();
			}
		}
		
		initiated = true;
	}
	
	public void clear() {
		// If inventory loading task is present, cancel it first
		if (loadInvTask != null) {
			loadInvTask.cancel();
			
			// Wait up to 3 seconds for it to stop
			for (int i = 0; i < 30; i++) {
				if (loadInvTask.getStatus() == LoadInventoryTask.STATUS_FINISHED ||
					loadInvTask.getStatus() == LoadInventoryTask.STATUS_CANCELED ||
					loadInvTask.getStatus() == LoadInventoryTask.STATUS_ERROR)
					break;
				
				try {
					Thread.sleep( 100 );
				}
				catch (InterruptedException e) {}
			}
			
			loadInvTask = null;
		}
		
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
		operatorLoginIDs = null;
		dftThermSchedules = null;
		
		nextCallNo = 0;
		nextOrderNo = 0;
		inventoryLoaded = false;
		workOrdersLoaded = false;
		hierarchyLoaded = false;
		
		dftRouteID = CtiUtilities.NONE_ID;
		dftTimeZone = null;
		operDftGroupID = com.cannontech.database.db.user.YukonGroup.EDITABLE_MIN_GROUP_ID - 1;
		
		optOutEventQueue = null;
		switchCommandQueue = null;
		
		contactCustAccountInfoMap = null;
		
		starsEnergyCompany = null;
		starsEnrPrograms = null;
		starsCustFAQs = null;
		starsServCompanies = null;
		starsExitQuestions = null;
		starsDftThermSchedules = null;
		starsOperECSettings = null;
		starsCustECSettings = null;
		starsOperSelLists = null;
		starsCustSelLists = null;
		
		starsSelectionLists = null;
		starsWebConfigs = null;
		starsCustAcctInfos = null;
		starsLMCtrlHists = null;
		
		activeAccounts = null;
		
		parent = null;
		children = null;
		memberLoginIDs = null;
	}
	
	public String getEnergyCompanySetting(int rolePropertyID) {
		String value = AuthFuncs.getRolePropertyValue( YukonUserFuncs.getLiteYukonUser(getUserID()), rolePropertyID );
		if (value != null && value.equalsIgnoreCase(CtiUtilities.STRING_NONE))
			value = "";
		return value;
	}
	
	public LiteYukonGroup[] getResidentialCustomerGroups() {
		String[] custGroupIDs = getEnergyCompanySetting( EnergyCompanyRole.CUSTOMER_GROUP_IDS ).split(",");
		ArrayList custGroupList = new ArrayList();
		
		for (int i = 0; i < custGroupIDs.length; i++) {
			String groupID = custGroupIDs[i].trim();
			if (groupID.equals("")) continue;
			LiteYukonGroup liteGroup = AuthFuncs.getGroup( Integer.parseInt(groupID) );
			if (liteGroup != null) custGroupList.add( liteGroup );
		}
		
		LiteYukonGroup[] custGroups = new LiteYukonGroup[ custGroupList.size() ];
		custGroupList.toArray( custGroups );
		return custGroups;
	}
	
	public LiteYukonGroup[] getWebClientOperatorGroups() {
		String[] operGroupIDs = getEnergyCompanySetting( EnergyCompanyRole.OPERATOR_GROUP_IDS ).split(",");
		ArrayList operGroupList = new ArrayList();
		
		for (int i = 0; i < operGroupIDs.length; i++) {
			String groupID = operGroupIDs[i].trim();
			if (groupID.equals("")) continue;
			LiteYukonGroup liteGroup = AuthFuncs.getGroup( Integer.parseInt(groupID) );
			if (liteGroup != null) operGroupList.add( liteGroup );
		}
		
		LiteYukonGroup[] operGroups = new LiteYukonGroup[ operGroupList.size() ];
		operGroupList.toArray( operGroups );
		return operGroups;
	}
	
	public LiteYukonGroup getOperatorDefaultGroup() {
		if (operDftGroupID < com.cannontech.database.db.user.YukonGroup.EDITABLE_MIN_GROUP_ID) {
			LiteYukonUser dftUser = YukonUserFuncs.getLiteYukonUser( getUserID() );
			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
			
			synchronized (cache) {
				java.util.List groups = (java.util.List) cache.getYukonUserGroupMap().get( dftUser );
				for (int i = 0; i < groups.size(); i++) {
					LiteYukonGroup group = (LiteYukonGroup) groups.get(i);
					if (AuthFuncs.getRolePropValueGroup(group, AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY, null) != null) {
						operDftGroupID = group.getGroupID();
						return group;
					}
				}
			}
		}
		
		return AuthFuncs.getGroup( operDftGroupID );
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
    			
				for (int j = 0; j < pubProgs.length; j++) {
					LiteLMProgram program = (LiteLMProgram) StarsLiteFactory.createLite(pubProgs[j]);
					lmPrograms.add( program );
					appCat.getPublishedPrograms().add( program );;
				}
    			
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
			if (!ECUtils.isDefaultEnergyCompany( this )) {
				YukonSelectionList dftList = SOAPServer.getDefaultEnergyCompany().getYukonSelectionList( listName, false );
				if (dftList != null) {
					// If the list is user updatable, returns a copy of the default list; otherwise returns the default list itself
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
					if (dftEntry.getEntryOrder() < 0) continue;
					
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
			CTILogger.error( e.getMessage(), e );
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
		if (!ECUtils.isDefaultEnergyCompany( this ))
			return SOAPServer.getDefaultEnergyCompany().getYukonListEntry( yukonDefID );
		
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
	
	public synchronized ArrayList getOperatorLoginIDs() {
		if (operatorLoginIDs == null) {
			SqlStatement stmt = new SqlStatement(
					"SELECT OperatorLoginID FROM EnergyCompanyOperatorLoginList WHERE EnergyCompanyID=" + getEnergyCompanyID(),
					CtiUtilities.getDatabaseAlias() );
			
			try {
				stmt.execute();
				
				operatorLoginIDs = new ArrayList( stmt.getRowCount() );
				for (int i = 0; i < stmt.getRowCount(); i++) {
					int userID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
					operatorLoginIDs.add( new Integer(userID) );
				}
				
				CTILogger.info( "All operator logins loaded for energy company #" + getEnergyCompanyID() );
			}
			catch (CommandExecutionException e) {
				CTILogger.error(e.getMessage(), e);
			}
		}
		
		return operatorLoginIDs;
	}
	
	public synchronized LiteLMThermostatSchedule getDefaultThermostatSchedule(int hwTypeDefID) {
		// For default energy company, the same settings is returned for any hardware types
		if (ECUtils.isDefaultEnergyCompany( this ))
			hwTypeDefID = 0;
		
		try {
			if (dftThermSchedules == null) {
				dftThermSchedules = new Hashtable();
				ECToGenericMapping[] items = ECToGenericMapping.getAllMappingItems(
						getEnergyCompanyID(), com.cannontech.database.db.stars.hardware.LMThermostatSchedule.TABLE_NAME );
				
				for (int i = 0; i < items.length; i++) {
					LMThermostatSchedule schedule = new LMThermostatSchedule();
					schedule.setScheduleID( items[i].getItemID() );
					schedule = (LMThermostatSchedule) Transaction.createTransaction( Transaction.RETRIEVE, schedule ).execute();
					
					LiteLMThermostatSchedule liteSchedule = StarsLiteFactory.createLiteLMThermostatSchedule( schedule );
					
					int defID = YukonListFuncs.getYukonListEntry( liteSchedule.getThermostatTypeID() ).getYukonDefID();
					dftThermSchedules.put( new Integer(defID), liteSchedule );
				}
			}
			
			LiteLMThermostatSchedule dftThermSchedule =
					(LiteLMThermostatSchedule) dftThermSchedules.get( new Integer(hwTypeDefID) );
			if (dftThermSchedule != null) return dftThermSchedule;
			
			if (ECUtils.isDefaultEnergyCompany( this )) {
				CTILogger.info("No default thermostat settings found for yukondefid = " + hwTypeDefID);
				return null;
			}
			
			dftThermSchedule = CreateLMHardwareAction.initThermostatSchedule( hwTypeDefID );
			
			ECToGenericMapping mapping = new ECToGenericMapping();
			mapping.setEnergyCompanyID( getEnergyCompanyID() );
			mapping.setItemID( new Integer(dftThermSchedule.getScheduleID()) );
			mapping.setMappingCategory( com.cannontech.database.db.stars.hardware.LMThermostatSchedule.TABLE_NAME );
			Transaction.createTransaction( Transaction.INSERT, mapping ).execute();
			
			dftThermSchedules.put( new Integer(hwTypeDefID), dftThermSchedule );
			
			CTILogger.info( "Default LM hardware loaded for energy company #" + getEnergyCompanyID() );
			return dftThermSchedule;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
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
				if (ECUtils.isDefaultEnergyCompany( this )) return customerFAQs;
				
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
						CTILogger.error( e.getMessage(), e );
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
			SqlStatement stmt = new SqlStatement(
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
				CTILogger.error( e.getMessage(), e );
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
			SqlStatement stmt = new SqlStatement(
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
				CTILogger.error( e.getMessage(), e );
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
		ArrayList programs = getAllLMPrograms();
		
		synchronized (appCats) {
			synchronized (programs) {
				for (int i = 0; i < appCats.size(); i++) {
					LiteApplianceCategory appCat = (LiteApplianceCategory) appCats.get(i);
					if (appCat.getApplianceCategoryID() == applianceCategoryID) {
						for (int j = 0; j < appCat.getPublishedPrograms().size(); j++)
							programs.remove( appCat.getPublishedPrograms().get(j) );
						appCats.remove( i );
						return appCat;
					}
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
		if (liteAcctInfo != null) {
			Hashtable contactAcctInfoMap = getContactCustAccountInfoMap();
			synchronized (contactAcctInfoMap) { contactAcctInfoMap.put(new Integer(liteContact.getContactID()), liteAcctInfo); }
		}
		
		ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_ADD );
	}

	public LiteContact deleteContact(int contactID) {
		LiteContact liteContact = ContactFuncs.getContact( contactID );
		
		if (liteContact != null) {
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
				CTILogger.error( e.getMessage(), e );
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
	
	public void addLMProgram(LiteLMProgram liteProg, LiteApplianceCategory liteAppCat) {
		ArrayList programs = getAllLMPrograms();
		synchronized (programs) { programs.add(liteProg); }
		
		liteAppCat.getPublishedPrograms().add( liteProg );
	}
	
	public LiteLMProgram deleteLMProgram(int programID) {
		ArrayList programs = getAllLMPrograms();
		ArrayList appCats = getAllApplianceCategories();
		
		synchronized (programs) {
			synchronized (appCats) {
				for (int i = 0; i < programs.size(); i++) {
					LiteLMProgram liteProg = (LiteLMProgram) programs.get(i);
					if (liteProg.getProgramID() == programID) {
						programs.remove(i);
						
						for (int j = 0; j < appCats.size(); j++) {
							LiteApplianceCategory liteAppCat = (LiteApplianceCategory) appCats.get(j);
							if (liteAppCat.getPublishedPrograms().remove( liteProg ))
								break;
						}
						
						return liteProg;
					}
				}
			}
		}
		
		return null;
	}
	
	public LiteInventoryBase loadInventory(int invID) {
		try {
			com.cannontech.database.db.stars.hardware.InventoryBase invDB =
					new com.cannontech.database.db.stars.hardware.InventoryBase();
			invDB.setInventoryID( new Integer(invID) );
			
			invDB = (com.cannontech.database.db.stars.hardware.InventoryBase)
					Transaction.createTransaction( Transaction.RETRIEVE, invDB ).execute();
			
			LiteInventoryBase liteInv = null;
			
			if (ECUtils.isLMHardware( invDB.getCategoryID().intValue() )) {
				com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
						new com.cannontech.database.data.stars.hardware.LMHardwareBase();
				hardware.setInventoryBase( invDB );
				
				com.cannontech.database.db.stars.hardware.LMHardwareBase hardwareDB = hardware.getLMHardwareBase();
				hardwareDB.setInventoryID( invDB.getInventoryID() );
				
				hardware = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
						Transaction.createTransaction( Transaction.RETRIEVE, hardware ).execute();
				
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
		catch (TransactionException e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}
	
	public void setInventoryLoaded() {
		inventoryLoaded = true;
	}
	
	public synchronized ArrayList loadAllInventory() {
		if (inventoryLoaded) return getAllInventory();
		
		try {
			if (loadInvTask == null) {
				loadInvTask = new LoadInventoryTask( this );
				new Thread( loadInvTask ).start();
			}
			
			while (true) {
				if (loadInvTask.getStatus() == LoadInventoryTask.STATUS_FINISHED) {
					loadInvTask = null;
					inventoryLoaded = true;
					return getAllInventory();
				}
				else if (loadInvTask.getStatus() == LoadInventoryTask.STATUS_ERROR) {
					loadInvTask = null;
					throw new Exception( loadInvTask.getErrorMsg() );
				}
				
				try {
					Thread.sleep( 1000 );
				}
				catch (InterruptedException e) {}
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}
	
	public LiteInventoryBase getInventoryBrief(int inventoryID, boolean autoLoad) {
		ArrayList inventory = getAllInventory();
		
		synchronized (inventory) {
			for (int i = 0; i < inventory.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(i);
				if (liteInv.getInventoryID() == inventoryID)
					return liteInv;
			}
		}
		
		if (autoLoad && !inventoryLoaded)
			return loadInventory( inventoryID );
		
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
	 * @return Pair(LiteStarsLMHardware, LiteStarsEnergyCompany)
	 */
	private Pair searchForLMHardware(int devTypeDefID, String serialNo, LiteStarsEnergyCompany referer)
		throws ObjectInOtherEnergyCompanyException
	{
		ArrayList inventory = getAllInventory();
		
		synchronized (inventory) {
			for (int i = 0; i < inventory.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(i);
				if (liteInv.getInventoryID() < 0) continue;
				if (!(liteInv instanceof LiteStarsLMHardware)) continue;
				
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) liteInv;
				if (YukonListFuncs.getYukonListEntry( liteHw.getLmHardwareTypeID() ).getYukonDefID() == devTypeDefID &&
					liteHw.getManufacturerSerialNumber().equalsIgnoreCase( serialNo ))
				{
					return new Pair(liteHw, this);
				}
			}
		}
		
		if (!inventoryLoaded) {
			try {
				com.cannontech.database.db.stars.hardware.LMHardwareBase[] hardwares =
						com.cannontech.database.db.stars.hardware.LMHardwareBase.searchBySerialNumber( serialNo, getLiteID() );
				
				for (int i = 0; i < hardwares.length; i++) {
					if (YukonListFuncs.getYukonListEntry( hardwares[i].getLMHardwareTypeID().intValue() ).getYukonDefID() == devTypeDefID) {
						com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
								new com.cannontech.database.data.stars.hardware.LMHardwareBase();
						hw.setInventoryID( hardwares[i].getInventoryID() );
						
						hw = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
								Transaction.createTransaction( Transaction.RETRIEVE, hw ).execute();
						
						LiteStarsLMHardware liteHw = new LiteStarsLMHardware();
						StarsLiteFactory.setLiteStarsLMHardware( liteHw, hw );
						addInventory( liteHw );
						
						return new Pair(liteHw, this);
					}
				}
			}
			catch (TransactionException e) {
				CTILogger.error( e.getMessage(), e );
				return null;
			}
		}
		
		// Search the LM hardware in the child energy companies
		ArrayList children = getChildren();
		synchronized (children) {
			for (int i = 0; i < children.size(); i++) {
				LiteStarsEnergyCompany liteCompany = (LiteStarsEnergyCompany) children.get(i);
				if (!liteCompany.equals( referer )) {
					Pair p = liteCompany.searchForLMHardware( devTypeDefID, serialNo, this );
					if (p != null)
						throw new ObjectInOtherEnergyCompanyException( (LiteStarsLMHardware)p.getFirst(), (LiteStarsEnergyCompany)p.getSecond() );
				}
			}
		}
		
		// Search the LM hardware in the parent energy company
		if (getParent() != null && !getParent().equals( referer )) {
			Pair p = getParent().searchForLMHardware( devTypeDefID, serialNo, this );
			if (p != null)
				throw new ObjectInOtherEnergyCompanyException( (LiteStarsLMHardware)p.getFirst(), (LiteStarsEnergyCompany)p.getSecond() );
		}
		
		return null;
	}
	
	/**
	 * Search for LM hardware with specified device type and serial #.
	 * If this energy company is a part of an energy company hierarchy,
	 * and the hardware belongs to another company in the hierarchy,
	 * the ObjectInOtherEnergyCompanyException is thrown. 
	 */
	public LiteStarsLMHardware searchForLMHardware(int deviceType, String serialNo)
		throws ObjectInOtherEnergyCompanyException
	{
		int devTypeDefID = YukonListFuncs.getYukonListEntry( deviceType ).getYukonDefID();
		Pair p = searchForLMHardware( devTypeDefID, serialNo, this );
		if (p != null) return (LiteStarsLMHardware)p.getFirst();
		return null;
	}
	
	/**
	 * @return Pair(LiteInventoryBase, LiteStarsEnergyCompany)
	 */
	private Pair searchForDevice(int categoryID, String deviceName, LiteStarsEnergyCompany referer)
		throws ObjectInOtherEnergyCompanyException
	{
		ArrayList inventory = getAllInventory();
		
		synchronized (inventory) {
			for (int i = 0; i < inventory.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(i);
				if (liteInv.getInventoryID() < 0) continue;
				if (liteInv.getDeviceID() == CtiUtilities.NONE_ID) continue;
				
				if (liteInv.getCategoryID() == categoryID &&
					PAOFuncs.getYukonPAOName( liteInv.getDeviceID() ).equalsIgnoreCase( deviceName ))
					return new Pair(liteInv, this);
			}
		}
		
		if (!inventoryLoaded) {
			try {
				int[] invIDs = com.cannontech.database.db.stars.hardware.InventoryBase.searchForDevice(
						categoryID, deviceName, getLiteID() );
				
				if (invIDs != null && invIDs.length > 0) {
					com.cannontech.database.db.stars.hardware.InventoryBase inv =
							new com.cannontech.database.db.stars.hardware.InventoryBase();
					inv.setInventoryID( new Integer(invIDs[0]) );
					
					inv = (com.cannontech.database.db.stars.hardware.InventoryBase)
							Transaction.createTransaction( Transaction.RETRIEVE, inv ).execute();
					
					LiteInventoryBase liteInv = new LiteInventoryBase();
					StarsLiteFactory.setLiteInventoryBase( liteInv, inv );
					addInventory( liteInv );
					
					return new Pair(liteInv, this);
				}
			}
			catch (TransactionException e) {
				CTILogger.error( e.getMessage(), e );
				return null;
			}
		}
		
		ArrayList children = getChildren();
		synchronized (children) {
			for (int i = 0; i < children.size(); i++) {
				LiteStarsEnergyCompany liteCompany = (LiteStarsEnergyCompany) children.get(i);
				if (!liteCompany.equals( referer )) {
					Pair p = liteCompany.searchForDevice( categoryID, deviceName, this );
					if (p != null)
						throw new ObjectInOtherEnergyCompanyException( (LiteInventoryBase)p.getFirst(), (LiteStarsEnergyCompany)p.getSecond() );
				}
			}
		}
		
		if (getParent() != null && !getParent().equals( referer )) {
			Pair p = getParent().searchForDevice( categoryID, deviceName, this );
			if (p != null)
				throw new ObjectInOtherEnergyCompanyException( (LiteInventoryBase)p.getFirst(), (LiteStarsEnergyCompany)p.getSecond() );
		}
		
		return null;
	}
	
	/**
	 * Search for device with the specified category and device name.
	 * If this energy company is a part of an energy company hierarchy,
	 * and the device belongs to another company in the hierarchy,
	 * the ObjectInOtherEnergyCompanyException is thrown. 
	 */
	public LiteInventoryBase searchForDevice(int categoryID, String deviceName)
		throws ObjectInOtherEnergyCompanyException
	{
		Pair p = searchForDevice( categoryID, deviceName, this );
		if (p != null) return (LiteInventoryBase)p.getFirst();
		
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
	 * @return Pair(LiteInventoryBase, LiteStarsEnergyCompany)
	 */
	private Pair getDevice(int deviceID, LiteStarsEnergyCompany referer)
		throws ObjectInOtherEnergyCompanyException
	{
		ArrayList inventory = getAllInventory();
		
		synchronized (inventory) {
			for (int i = 0; i < inventory.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(i);
				if (liteInv.getDeviceID() == deviceID)
					return new Pair(liteInv, this);
			}
		}
		
		if (!inventoryLoaded) {
			com.cannontech.database.db.stars.hardware.InventoryBase inv =
					com.cannontech.database.db.stars.hardware.InventoryBase.searchByDeviceID( deviceID, getLiteID() );
			
			if (inv != null) {
				LiteInventoryBase liteInv = new LiteInventoryBase();
				StarsLiteFactory.setLiteInventoryBase( liteInv, inv );
				return new Pair(liteInv, this);
			}
		}
		
		ArrayList children = getChildren();
		synchronized (children) {
			for (int i = 0; i < children.size(); i++) {
				LiteStarsEnergyCompany liteCompany = (LiteStarsEnergyCompany) children.get(i);
				if (!liteCompany.equals( referer )) {
					Pair p = liteCompany.getDevice( deviceID, this );
					if (p != null)
						throw new ObjectInOtherEnergyCompanyException( (LiteInventoryBase)p.getFirst(), (LiteStarsEnergyCompany)p.getSecond() );
				}
			}
		}
		
		if (getParent() != null && !getParent().equals( referer )) {
			Pair p = getParent().getDevice( deviceID, this );
			if (p != null)
				throw new ObjectInOtherEnergyCompanyException( (LiteInventoryBase)p.getFirst(), (LiteStarsEnergyCompany)p.getSecond() );
		}
		
		return null;
	}
	
	/**
	 * Search the inventory for device with the specified device ID.
	 * If this energy company is a part of an energy company hierarchy,
	 * and the device belongs to another company in the hierarchy,
	 * the ObjectInOtherEnergyCompanyException is thrown. 
	 */
	public LiteInventoryBase getDevice(int deviceID) throws ObjectInOtherEnergyCompanyException {
		Pair p = getDevice( deviceID, this );
		if (p != null) return (LiteInventoryBase)p.getFirst();
		return null;
	}
	
	/**
	 * Search the inventory by serial number. If searchMembers is true,
	 * it returns a list of Pair(LiteInventoryBase, LiteStarsEnergyCompany);
	 * otherwise it returns a list of LiteInventoryBase.
	 */
	public ArrayList searchInventoryBySerialNo(String serialNo, boolean searchMembers) {
		ArrayList hwList = new ArrayList();
		ArrayList inventory = loadAllInventory();
		
		synchronized (inventory) {
			for (int i = 0; i < inventory.size(); i++) {
				if (inventory.get(i) instanceof LiteStarsLMHardware) {
					LiteStarsLMHardware liteHw = (LiteStarsLMHardware) inventory.get(i);
					if (liteHw.getManufacturerSerialNumber().equalsIgnoreCase( serialNo )) {
						if (searchMembers)
							hwList.add( new Pair(liteHw, this) );
						else
							hwList.add( liteHw );
					}
				}
			}
		}
		
		if (searchMembers) {
			ArrayList children = getChildren();
			synchronized (children) {
				for (int i = 0; i < children.size(); i++) {
					LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) children.get(i);
					ArrayList memberList = company.searchInventoryBySerialNo( serialNo, searchMembers );
					hwList.addAll( memberList );
				}
			}
		}
		
		return hwList;
	}
	
	/**
	 * Search the inventory by account #. If searchMembers is true,
	 * it returns a list of Pair(LiteInventoryBase, LiteStarsEnergyCompany);
	 * otherwise it returns a list of LiteInventoryBase.
	 */
	public ArrayList searchInventoryByAccountNo(String accountNo, boolean searchMembers) {
		ArrayList invList = new ArrayList();
		ArrayList inventory = loadAllInventory();
		
		LiteStarsCustAccountInformation liteAcctInfo = searchByAccountNo( accountNo );
		if (liteAcctInfo != null) {
			synchronized (inventory) {
				for (int i = 0; i < inventory.size(); i++) {
					LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(i);
					if (liteInv.getAccountID() == liteAcctInfo.getAccountID()) {
						if (searchMembers)
							invList.add( new Pair(liteInv, this) );
						else
							invList.add( liteInv );
					}
				}
			}
		}
		
		if (searchMembers) {
			ArrayList children = getChildren();
			synchronized (children) {
				for (int i = 0; i < children.size(); i++) {
					LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) children.get(i);
					ArrayList memberList = company.searchInventoryByAccountNo( accountNo, searchMembers );
					invList.addAll( memberList );
				}
			}
		}
		
		return invList;
	}
	
	private ArrayList searchInventoryByContactIDs(int[] contactIDs, boolean searchMembers) {
		ArrayList invList = new ArrayList();
		ArrayList inventory = loadAllInventory();
		ArrayList acctList = searchAccountByContactIDs( contactIDs, false );
		
		synchronized (inventory) {
			for (int i = 0; i < inventory.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(i);
				for (int j = 0; j < acctList.size(); j++) {
					LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) acctList.get(j);
					if (liteInv.getAccountID() == liteAcctInfo.getAccountID()) {
						if (searchMembers)
							invList.add( new Pair(liteInv, this) );
						else
							invList.add( liteInv );
						break;
					}
				}
			}
		}
		
		if (searchMembers) {
			ArrayList children = getChildren();
			synchronized (children) {
				for (int i = 0; i < children.size(); i++) {
					LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) children.get(i);
					ArrayList memberList = company.searchInventoryByContactIDs( contactIDs, searchMembers );
					invList.addAll( memberList );
				}
			}
		}
		
		return invList;
	}
	
	/**
	 * Search the inventory by phone #. If searchMembers is true,
	 * it returns a list of Pair(LiteInventoryBase, LiteStarsEnergyCompany);
	 * otherwise it returns a list of LiteInventoryBase.
	 */
	public ArrayList searchInventoryByPhoneNo(String phoneNo, boolean searchMembers) {
		LiteContact[] contacts = ContactFuncs.getContactsByPhoneNo(
				phoneNo, new int[] {YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE} );
		
		int[] contactIDs = new int[ contacts.length ];
		for (int i = 0; i < contacts.length; i++)
			contactIDs[i] = contacts[i].getContactID();
		
		return searchInventoryByContactIDs( contactIDs, searchMembers );
	}
	
	/**
	 * Search the inventory by last name. If searchMembers is true,
	 * it returns a list of Pair(LiteInventoryBase, LiteStarsEnergyCompany);
	 * otherwise it returns a list of LiteInventoryBase.
	 */
	public ArrayList searchInventoryByLastName(String lastName, boolean searchMembers) {
		LiteContact[] contacts = ContactFuncs.getContactsByLName( lastName );
		
		int[] contactIDs = new int[ contacts.length ];
		for (int i = 0; i < contacts.length; i++)
			contactIDs[i] = contacts[i].getContactID();
		
		return searchInventoryByContactIDs( contactIDs, searchMembers );
	}
	
	/**
	 * Search the inventory by order #. If searchMembers is true,
	 * it returns a list of Pair(LiteInventoryBase, LiteStarsEnergyCompany);
	 * otherwise it returns a list of LiteInventoryBase.
	 */
	public ArrayList searchInventoryByOrderNo(String orderNo, boolean searchMembers) {
		ArrayList invList = new ArrayList();
		ArrayList inventory = loadAllInventory();
		ArrayList workOrders = loadWorkOrders();
		LiteWorkOrderBase liteOrder = null;
		
		synchronized (workOrders) {
			for (int i = 0; i < workOrders.size(); i++) {
				LiteWorkOrderBase lOrder = (LiteWorkOrderBase) workOrders.get(i);
				if (liteOrder.getOrderNumber().equalsIgnoreCase( orderNo )) {
					liteOrder = lOrder;
					break;
				}
			}
		}
		
		if (liteOrder != null) {
			synchronized (inventory) {
				for (int i = 0; i < inventory.size(); i++) {
					LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(i);
					if (liteInv.getAccountID() == liteOrder.getAccountID()) {
						if (searchMembers)
							invList.add( new Pair(liteInv, this) );
						else
							invList.add( liteInv );
					}
				}
			}
		}
		
		if (searchMembers) {
			ArrayList children = getChildren();
			synchronized (children) {
				for (int i = 0; i < children.size(); i++) {
					LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) children.get(i);
					ArrayList memberList = company.searchInventoryByOrderNo( orderNo, searchMembers );
					invList.addAll( memberList );
				}
			}
		}
		
		return invList;
	}
	
	public LiteStarsLMControlHistory getLMControlHistory(int groupID) {
		if (groupID == CtiUtilities.NONE_ID) return null;
		
		LiteStarsLMControlHistory lmCtrlHist = null;
		
		ArrayList lmCtrlHistList = getAllLMControlHistory();
		synchronized (lmCtrlHistList) {
			for (int i = 0; i < lmCtrlHistList.size(); i++) {
				lmCtrlHist = (LiteStarsLMControlHistory) lmCtrlHistList.get(i);
				if (lmCtrlHist.getGroupID() == groupID)
					return lmCtrlHist;
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
	
	public LiteStarsThermostatSettings getThermostatSettings(LiteStarsLMHardware liteHw) {
		try {
			LiteStarsThermostatSettings settings = new LiteStarsThermostatSettings();
			settings.setInventoryID( liteHw.getInventoryID() );
        	
			// Check to see if thermostat schedule is valid, if not, recreate the schedule
			LiteLMThermostatSchedule liteSched = null;
			com.cannontech.database.data.stars.hardware.LMThermostatSchedule schedule = null;
			
			com.cannontech.database.db.stars.hardware.LMThermostatSchedule scheduleDB =
					com.cannontech.database.db.stars.hardware.LMThermostatSchedule.getThermostatSchedule( liteHw.getInventoryID() );
			
			if (scheduleDB != null) {
				schedule = new com.cannontech.database.data.stars.hardware.LMThermostatSchedule();
				schedule.setScheduleID( scheduleDB.getScheduleID() );
				schedule = (com.cannontech.database.data.stars.hardware.LMThermostatSchedule)
						Transaction.createTransaction( Transaction.RETRIEVE, schedule ).execute();
				
				liteSched = StarsLiteFactory.createLiteLMThermostatSchedule( schedule );
			}
			
			if (!ECUtils.isValidThermostatSchedule( liteSched )) {
				if (schedule != null)
					Transaction.createTransaction( Transaction.DELETE, schedule ).execute();
				
				if (liteHw.getInventoryID() >= 0)
					settings.setThermostatSchedule( CreateLMHardwareAction.initThermostatSchedule(liteHw, this) );
				else
					settings.setThermostatSchedule( CreateLMHardwareAction.initThermostatSchedule(liteHw, SOAPServer.getDefaultEnergyCompany()) );
			}
			else
				settings.setThermostatSchedule( liteSched );
        	
			com.cannontech.database.data.stars.event.LMThermostatManualEvent[] events =
					com.cannontech.database.data.stars.event.LMThermostatManualEvent.getAllLMThermostatManualEvents( liteHw.getInventoryID() );
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
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}
	
	public synchronized ArrayList loadWorkOrders() {
		ArrayList workOrders = getAllWorkOrders();
		if (workOrdersLoaded) return workOrders;
		
		try {
			String sql = "SELECT WorkOrderID FROM ECToWorkOrderMapping WHERE EnergyCompanyID=" + getEnergyCompanyID();
			SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
			stmt.execute();
			
			for (int i = 0; i < stmt.getRowCount(); i++) {
				int orderID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
				if (getWorkOrderBase(orderID, false) != null) continue;
				
				com.cannontech.database.db.stars.report.WorkOrderBase order =
						new com.cannontech.database.db.stars.report.WorkOrderBase();
				order.setOrderID( new Integer(orderID) );
				
				order = (com.cannontech.database.db.stars.report.WorkOrderBase)
						Transaction.createTransaction( Transaction.RETRIEVE, order ).execute();
				
				LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) StarsLiteFactory.createLite( order );
				addWorkOrderBase( liteOrder );
			}
			
			workOrdersLoaded = true;
			CTILogger.info( "All work orders loaded for energy company #" + getEnergyCompanyID() );
			
			return workOrders;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
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
				CTILogger.error( e.getMessage(), e );
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
	
	private LiteStarsCustAccountInformation addBriefCustAccountInfo(com.cannontech.database.data.stars.customer.CustomerAccount account) {
		com.cannontech.database.data.stars.customer.AccountSite site = account.getAccountSite();
        
		LiteStarsCustAccountInformation liteAcctInfo = new LiteStarsCustAccountInformation( account.getCustomerAccount().getAccountID().intValue() );
		liteAcctInfo.setCustomerAccount( (LiteCustomerAccount) StarsLiteFactory.createLite(account.getCustomerAccount()) );
		liteAcctInfo.setAccountSite( (LiteAccountSite) StarsLiteFactory.createLite(site.getAccountSite()) );
		liteAcctInfo.setSiteInformation( (LiteSiteInformation) StarsLiteFactory.createLite(site.getSiteInformation().getSiteInformation()) );
		
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized (cache) {
			liteAcctInfo.setCustomer( (LiteCustomer)cache.getAllCustomersMap().get(account.getCustomerAccount().getCustomerID()) );
		}
		
		Hashtable contactAcctInfoMap = getContactCustAccountInfoMap();
		synchronized (contactAcctInfoMap) {
			contactAcctInfoMap.put( new Integer(liteAcctInfo.getCustomer().getPrimaryContactID()), liteAcctInfo );
			
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
		try {
			com.cannontech.database.db.stars.customer.CustomerResidence residence =
					com.cannontech.database.db.stars.customer.CustomerResidence.getCustomerResidence( liteAcctInfo.getAccountSite().getAccountSiteID() );
			if (residence != null)
				liteAcctInfo.setCustomerResidence( (LiteCustomerResidence) StarsLiteFactory.createLite(residence) );
			
			ArrayList appliances = liteAcctInfo.getAppliances();
			for (int i = 0; i < appliances.size(); i++) {
				LiteStarsAppliance liteApp = (LiteStarsAppliance) appliances.get(i);
	            
				com.cannontech.database.data.stars.appliance.ApplianceBase appliance =
						new com.cannontech.database.data.stars.appliance.ApplianceBase();
				appliance.setApplianceID( new Integer(liteApp.getApplianceID()) );
				
				appliance = (com.cannontech.database.data.stars.appliance.ApplianceBase)
						Transaction.createTransaction( Transaction.RETRIEVE, appliance ).execute();
	            
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
					com.cannontech.database.data.stars.event.LMProgramEvent.getAllLMProgramEvents( new Integer(liteAcctInfo.getLiteID()) );
			
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
				
				boolean progExists = false;
				for (int j = 0; j < programs.size(); j++) {
					if (((LiteStarsLMProgram) programs.get(j)).getLmProgram().getProgramID() == progID) {
						progExists = true;
						break;
					}
				}
				if (progExists) continue;
	            
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
			
			int[] orderIDs = com.cannontech.database.db.stars.report.WorkOrderBase.searchByAccountID( liteAcctInfo.getLiteID() );
			if (orderIDs != null) {
				liteAcctInfo.setServiceRequestHistory( new ArrayList() );
				for (int i = 0; i < orderIDs.length; i++) {
					getWorkOrderBase(orderIDs[i], true);
					liteAcctInfo.getServiceRequestHistory().add( new Integer(orderIDs[i]) );
				}
			}
			
			com.cannontech.database.db.stars.hardware.LMThermostatSchedule[] schedules =
					com.cannontech.database.db.stars.hardware.LMThermostatSchedule.getAllThermostatSchedules( liteAcctInfo.getAccountID() );
			if (schedules != null) {
				for (int i = 0; i < schedules.length; i++) {
					if (schedules[i].getInventoryID().intValue() == 0) {
						LMThermostatSchedule schedule = new LMThermostatSchedule();
						schedule.setScheduleID( schedules[i].getScheduleID() );
						schedule = (LMThermostatSchedule) Transaction.createTransaction( Transaction.RETRIEVE, schedule ).execute();
						
						LiteLMThermostatSchedule liteSchedule = StarsLiteFactory.createLiteLMThermostatSchedule( schedule );
						liteAcctInfo.getThermostatSchedules().add( liteSchedule );
					}
				}
			}
			
			liteAcctInfo.setExtended( true );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
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
				CTILogger.error( e.getMessage(), e );
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
		
		// Remove from active account list
		ArrayList accountList = getActiveAccounts();
		synchronized (accountList) {
			if (accountList.contains(liteAcctInfo)) accountList.remove( liteAcctInfo );
		}
		
		// Remove customer from the cache
		ServerUtils.handleDBChange( liteAcctInfo.getCustomer(), DBChangeMsg.CHANGE_TYPE_DELETE );
    	
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
			loadInventory( invID );
		}
		
		// Remove all work orders from the cache
		for (int i = 0; i < liteAcctInfo.getServiceRequestHistory().size(); i++) {
			int orderID = ((Integer) liteAcctInfo.getServiceRequestHistory().get(i)).intValue();
			deleteWorkOrderBase( orderID );
		}
		
		// Remove the customer account from custAccountInfos
		getAllCustAccountInformation().remove( liteAcctInfo );
	}
	
	private LiteStarsCustAccountInformation searchByAccountNo(String accountNo) {
		ArrayList custAcctInfoList = getAllCustAccountInformation();
		synchronized (custAcctInfoList) {
			for (int i = 0; i < custAcctInfoList.size(); i++) {
				LiteStarsCustAccountInformation accountInfo = (LiteStarsCustAccountInformation) custAcctInfoList.get(i);
				if (accountInfo.getCustomerAccount().getAccountNumber().equalsIgnoreCase( accountNo ))
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
			
			return addBriefCustAccountInfo( account );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}
	
	/**
	 * Search customer account by account # within the energy company.
	 */
	public LiteStarsCustAccountInformation searchAccountByAccountNo(String accountNo) {
		LiteStarsCustAccountInformation liteAcctInfo = searchByAccountNo( accountNo );
		if (liteAcctInfo != null && !liteAcctInfo.isExtended())
			extendCustAccountInfo( liteAcctInfo );
		
		return liteAcctInfo;
	}
	
	/**
	 * Search customer accounts by account #. Wildcard character "*" is allowed.
	 * If searchMembers is true, it returns a list of Pair(LiteStarsCustAccountInformation, LiteStarsEnergyCompany);
	 * otherwise it returns a list of LiteStarsCustAccountInformation.
	 */
	public ArrayList searchAccountByAccountNo(String accountNo, boolean searchMembers) {
		ArrayList accountList = new ArrayList();
		
		if (accountNo.indexOf('*') == -1) {
			LiteStarsCustAccountInformation liteAcctInfo = searchByAccountNo( accountNo );
			if (liteAcctInfo != null)  {
				if (searchMembers)
					accountList.add( new Pair(liteAcctInfo, this) );
				else
					accountList.add( liteAcctInfo );
			} 
		}
		else {
			int[] accountIDs = com.cannontech.database.db.stars.customer.CustomerAccount.searchByAccountNumber(
					getEnergyCompanyID(), accountNo.replace('*','%') );
			if (accountIDs != null) {
				for (int i = 0; i < accountIDs.length; i++) {
					LiteStarsCustAccountInformation liteAcctInfo = getBriefCustAccountInfo( accountIDs[i], true );
					if (searchMembers)
						accountList.add( new Pair(liteAcctInfo, this) );
					else
						accountList.add( liteAcctInfo );
				}
			}
		}
		
		if (searchMembers) {
			ArrayList children = getChildren();
			synchronized (children) {
				for (int i = 0; i < children.size(); i++) {
					LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) children.get(i);
					ArrayList memberList = company.searchAccountByAccountNo( accountNo, searchMembers );
					accountList.addAll( memberList );
				}
			}
		}
		
		return accountList;
	}
	
	/**
	 * Search customer accounts by hardware serial #.
	 * If searchMembers is true, it returns a list of Pair(LiteStarsCustAccountInformation, LiteStarsEnergyCompany);
	 * otherwise it returns a list of LiteStarsCustAccountInformation.
	 */
	public ArrayList searchAccountBySerialNo(String serialNo, boolean searchMembers) {
		ArrayList invList = searchInventoryBySerialNo( serialNo, false );
		ArrayList accountList = new ArrayList();
		
		for (int i = 0; i < invList.size(); i++) {
			LiteInventoryBase inv = (LiteInventoryBase) invList.get(i);
			if (inv.getAccountID() > 0) {
				LiteStarsCustAccountInformation liteAcctInfo = getBriefCustAccountInfo( inv.getAccountID(), true );
				if (liteAcctInfo != null) {
					if (searchMembers)
						accountList.add( new Pair(liteAcctInfo, this) );
					else
						accountList.add( liteAcctInfo );
				} 
			}
		}
		
		if (searchMembers) {
			ArrayList children = getChildren();
			synchronized (children) {
				for (int i = 0; i < children.size(); i++) {
					LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) children.get(i);
					ArrayList memberList = company.searchAccountBySerialNo( serialNo, searchMembers );
					accountList.addAll( memberList );
				}
			}
		}
		
		return accountList;
	}
	
	/**
	 * Search customer account by residence map #.
	 * If searchMembers is true, the return type is Pair(LiteStarsCustAccountInformation, LiteStarsEnergyCompany);
	 * otherwise the return type is LiteStarsCustAccountInformation.
	 */
	public Object searchAccountByMapNo(String mapNo, boolean searchMembers) {
		ArrayList acctInfoList = getAllCustAccountInformation();
		synchronized (acctInfoList) {
			for (int i = 0; i < acctInfoList.size(); i++) {
				LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) acctInfoList.get(i);
				if (liteAcctInfo.getAccountSite().getSiteNumber().equalsIgnoreCase( mapNo )) {
					if (searchMembers)
						return new Pair(liteAcctInfo, this);
					else
						return liteAcctInfo;
				}
			}
		}
		
		try {
			Integer accountID = com.cannontech.database.db.stars.customer.AccountSite.getAccountIDBySiteNo( mapNo, getLiteID() );
			if (accountID != null) {
				com.cannontech.database.data.stars.customer.CustomerAccount account =
						new com.cannontech.database.data.stars.customer.CustomerAccount();
				account.setAccountID( accountID );
				account = (com.cannontech.database.data.stars.customer.CustomerAccount)
						Transaction.createTransaction(Transaction.RETRIEVE, account).execute();
				
				LiteStarsCustAccountInformation liteAcctInfo = addBriefCustAccountInfo( account );
				if (searchMembers)
					return new Pair(liteAcctInfo, this);
				else
					return liteAcctInfo;
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			return null;
		}
		
		if (searchMembers) {
			ArrayList children = getChildren();
			synchronized (children) {
				for (int i = 0; i < children.size(); i++) {
					LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) children.get(i);
					Object obj = company.searchAccountByMapNo( mapNo, searchMembers );
					if (obj != null) return obj;
				}
			}
		}
		
		return null;
	}
	
	private ArrayList searchAccountByContactIDs(int[] contactIDs, boolean searchMembers) {
		ArrayList accountList = new ArrayList();
		
		int[] accountIDs = com.cannontech.database.db.stars.customer.CustomerAccount.searchByPrimaryContactIDs(
				contactIDs, getLiteID() );
		if (accountIDs != null) {
			for (int i = 0; i < accountIDs.length; i++) {
				LiteStarsCustAccountInformation liteAcctInfo = getBriefCustAccountInfo( accountIDs[i], true );
				if (searchMembers)
					accountList.add( new Pair(liteAcctInfo, this) );
				else
					accountList.add( liteAcctInfo );
			}
		}
		
		if (searchMembers) {
			ArrayList children = getChildren();
			synchronized (children) {
				for (int i = 0; i < children.size(); i++) {
					LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) children.get(i);
					ArrayList memberList = company.searchAccountByContactIDs( contactIDs, searchMembers );
					accountList.addAll( memberList );
				}
			}
		}
		
		return accountList;
	}
	
	/**
	 * Search customer accounts by phone number.
	 * If searchMembers is true, it returns a list of Pair(LiteStarsCustAccountInformation, LiteStarsEnergyCompany);
	 * otherwise it returns a list of LiteStarsCustAccountInformation.
	 */
	public ArrayList searchAccountByPhoneNo(String phoneNo, boolean searchMembers) {
		LiteContact[] contacts = ContactFuncs.getContactsByPhoneNo(
				phoneNo, new int[] {YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE} );
		
		int[] contactIDs = new int[ contacts.length ];
		for (int i = 0; i < contacts.length; i++)
			contactIDs[i] = contacts[i].getContactID();
		
		return searchAccountByContactIDs( contactIDs, searchMembers );
	}
	
	/**
	 * Search customer accounts by last name. The search is based on partial match, and is case-insensitive.
	 * If searchMembers is true, it returns a list of Pair(LiteStarsCustAccountInformation, LiteStarsEnergyCompany);
	 * otherwise it returns a list of LiteStarsCustAccountInformation.
	 */
	public ArrayList searchAccountByLastName(String lastName, boolean searchMembers) {
		ArrayList contactList = new ArrayList();
		
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized( cache ) {
			java.util.List cstCnts = cache.getAllContacts();
			
			for( int j = 0; j < cstCnts.size(); j++ ) {
				LiteContact contact = (LiteContact) cstCnts.get(j);
				if(contact.getContLastName().toUpperCase().startsWith( lastName.toUpperCase() ))
					contactList.add( contact );
			}
		}
		
		int[] contactIDs = new int[ contactList.size() ];
		for (int i = 0; i < contactList.size(); i++)
			contactIDs[i] = ((LiteContact)contactList.get(i)).getContactID();
		
		return searchAccountByContactIDs( contactIDs, searchMembers );
	}
	
	
	/* The following methods are only used when SOAPClient exists locally */
	
	public synchronized StarsEnergyCompanySettings getStarsEnergyCompanySettings(StarsYukonUser user) {
		if (ServerUtils.isOperator(user)) {
			if (starsOperECSettings == null) {
				starsOperECSettings = new StarsEnergyCompanySettings();
				starsOperECSettings.setEnergyCompanyID( user.getEnergyCompanyID() );
				starsOperECSettings.setStarsEnergyCompany( getStarsEnergyCompany() );
				starsOperECSettings.setStarsEnrollmentPrograms( getStarsEnrollmentPrograms() );
				starsOperECSettings.setStarsCustomerSelectionLists( getStarsCustomerSelectionLists(user) );
				starsOperECSettings.setStarsServiceCompanies( getStarsServiceCompanies() );
				starsOperECSettings.setStarsCustomerFAQs( getStarsCustomerFAQs() );
				starsOperECSettings.setStarsExitInterviewQuestions( getStarsExitInterviewQuestions() );
				starsOperECSettings.setStarsDefaultThermostatSchedules( getStarsDefaultThermostatSchedules() );
			}
			
			return starsOperECSettings;
		}
		else if (ServerUtils.isResidentialCustomer(user)) {
			if (starsCustECSettings == null) {
				starsCustECSettings = new StarsEnergyCompanySettings();
				starsCustECSettings.setEnergyCompanyID( user.getEnergyCompanyID() );
				starsCustECSettings.setStarsEnergyCompany( getStarsEnergyCompany() );
				starsCustECSettings.setStarsEnrollmentPrograms( getStarsEnrollmentPrograms() );
				starsCustECSettings.setStarsCustomerSelectionLists( getStarsCustomerSelectionLists(user) );
				starsCustECSettings.setStarsCustomerFAQs( getStarsCustomerFAQs() );
				starsCustECSettings.setStarsExitInterviewQuestions( getStarsExitInterviewQuestions() );
				starsCustECSettings.setStarsDefaultThermostatSchedules( getStarsDefaultThermostatSchedules() );
			}
			
			return starsCustECSettings;
		}
		
		return null;
	}
	
	public synchronized StarsEnergyCompany getStarsEnergyCompany() {
		if (starsEnergyCompany == null) {
			starsEnergyCompany = new StarsEnergyCompany();
			StarsLiteFactory.setStarsEnergyCompany( starsEnergyCompany, this );
		}
		return starsEnergyCompany;
	}
	
	private Hashtable getStarsCustSelectionLists() {
		if (starsSelectionLists == null)
			starsSelectionLists = new Hashtable();
		return starsSelectionLists;
	}
	
	private StarsCustSelectionList getStarsCustSelectionList(String listName) {
		Hashtable starsSelectionLists = getStarsCustSelectionLists();
		synchronized (starsSelectionLists) {
			StarsCustSelectionList starsList = (StarsCustSelectionList) starsSelectionLists.get( listName );
			if (starsList == null) {
				YukonSelectionList yukonList = getYukonSelectionList( listName );
				if (yukonList != null) {
					starsList = StarsLiteFactory.createStarsCustSelectionList( yukonList );
					starsSelectionLists.put( starsList.getListName(), starsList );
				}
			}
			
			return starsList;
		}
	}
	
	private void updateOperSelectionLists() {
		starsOperSelLists.removeAllStarsCustSelectionList();
		
		for (int i = 0; i < OPERATOR_SELECTION_LISTS.length; i++) {
			StarsCustSelectionList list = getStarsCustSelectionList(OPERATOR_SELECTION_LISTS[i]);
			if (list != null) starsOperSelLists.addStarsCustSelectionList( list );
		}
	}
	
	private void updateCustSelectionLists() {
		starsCustSelLists.removeAllStarsCustSelectionList();
		
		// Currently the consumer side only need opt out period list and hardware status list
		StarsCustSelectionList list = getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD_CUS);
		if (list == null) {
			list = getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD);
			if (list != null) {
				StarsCustSelectionList cusList = new StarsCustSelectionList();
				cusList.setListID( list.getListID() );
				cusList.setListName( YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD_CUS );
				cusList.setStarsSelectionListEntry( list.getStarsSelectionListEntry() );
				list = cusList;
				
				Hashtable starsSelectionLists = getStarsCustSelectionLists();
				synchronized (starsSelectionLists) { starsSelectionLists.put(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD_CUS, cusList); }
			}
		}
		if (list != null) starsCustSelLists.addStarsCustSelectionList( list );
		
		list = getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS);
		if (list != null) starsCustSelLists.addStarsCustSelectionList( list );
	}
	
	public synchronized void updateStarsCustomerSelectionLists() {
		Hashtable starsSelectionLists = getStarsCustSelectionLists();
		synchronized (starsSelectionLists) { starsSelectionLists.clear(); }
		
		if (starsOperSelLists != null)
			updateOperSelectionLists();
		if (starsCustSelLists != null)
			updateCustSelectionLists();
	}
	
	public synchronized StarsCustomerSelectionLists getStarsCustomerSelectionLists(StarsYukonUser starsUser) {
		LiteYukonUser liteUser = starsUser.getYukonUser();
		
		if (ServerUtils.isOperator( starsUser )) {
			if (starsOperSelLists == null) {
				starsOperSelLists = new StarsCustomerSelectionLists();
				updateOperSelectionLists();
			}
			
			return starsOperSelLists;
		}
		else if (ServerUtils.isResidentialCustomer( starsUser )) {
			if (starsCustSelLists == null) {
				starsCustSelLists = new StarsCustomerSelectionLists();
				updateCustSelectionLists();
			}
			
			return starsCustSelLists;
		}
		
		return null;
	}
	
	public synchronized StarsEnrollmentPrograms getStarsEnrollmentPrograms() {
		if (starsEnrPrograms == null)
			starsEnrPrograms = StarsLiteFactory.createStarsEnrollmentPrograms(
					getAllApplianceCategories(), this );
		return starsEnrPrograms;
	}
	
	public synchronized StarsCustomerFAQs getStarsCustomerFAQs() {
		if (starsCustFAQs == null)
			starsCustFAQs = StarsLiteFactory.createStarsCustomerFAQs( getAllCustomerFAQs() );
		return starsCustFAQs;
	}
	
	public synchronized StarsServiceCompanies getStarsServiceCompanies() {
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
	
	public synchronized StarsExitInterviewQuestions getStarsExitInterviewQuestions() {
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
	
	public synchronized void updateStarsDefaultThermostatSchedules() {
		boolean hasBasic = false;
		boolean hasEpro = false;
		boolean hasComm = false;
		
		YukonSelectionList devTypeList = getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, false);
		for (int i = 0; i < devTypeList.getYukonListEntries().size(); i++) {
			YukonListEntry entry = (YukonListEntry) devTypeList.getYukonListEntries().get(i);
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT)
				hasBasic = true;
			else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO)
				hasEpro = true;
			else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT)
				hasComm = true;
		}
		
		starsDftThermSchedules = new StarsDefaultThermostatSchedules();
		if (hasBasic) {
			StarsThermostatProgram starsThermProg = StarsLiteFactory.createStarsThermostatProgram(
					getDefaultThermostatSchedule(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT), this );
			starsDftThermSchedules.addStarsThermostatProgram( starsThermProg );
		}
		if (hasEpro) {
			StarsThermostatProgram starsThermProg = StarsLiteFactory.createStarsThermostatProgram(
					getDefaultThermostatSchedule(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO), this );
			starsDftThermSchedules.addStarsThermostatProgram( starsThermProg );
		}
		if (hasComm) {
			StarsThermostatProgram starsThermProg = StarsLiteFactory.createStarsThermostatProgram(
					getDefaultThermostatSchedule(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT), this );
			starsDftThermSchedules.addStarsThermostatProgram( starsThermProg );
		}
		
		if (starsOperECSettings != null)
			starsOperECSettings.setStarsDefaultThermostatSchedules( starsDftThermSchedules );
		if (starsCustECSettings != null)
			starsCustECSettings.setStarsDefaultThermostatSchedules( starsDftThermSchedules );
	}
	
	public synchronized StarsDefaultThermostatSchedules getStarsDefaultThermostatSchedules() {
		if (starsDftThermSchedules == null)
			updateStarsDefaultThermostatSchedules();
		return starsDftThermSchedules;
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
		Hashtable starsCustAcctInfos = getStarsCustAcctInfos();
		synchronized (starsCustAcctInfos) {
			return (StarsCustAccountInformation)starsCustAcctInfos.get( new Integer(accountID) );
		}
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
		StarsCustAccountInformation starsAcctInfo = getStarsCustAccountInformation( accountID );
		if (starsAcctInfo != null) {
			Hashtable starsCustAcctInfos = getStarsCustAcctInfos();
			synchronized (starsCustAcctInfos) { starsCustAcctInfos.remove( new Integer(accountID) ); }
			
			unregisterActiveAccount( starsAcctInfo );
		}
	}
	
	public ArrayList getActiveAccounts() {
		if (activeAccounts == null)
			activeAccounts = new ArrayList();
		return activeAccounts;
	}
	
	public void registerActiveAccount(StarsCustAccountInformation starsAcctInfo) {
		starsAcctInfo.setLastActiveTime( new Date() );
		
		ArrayList activeAccounts = getActiveAccounts();
		synchronized (activeAccounts) {
			if (!activeAccounts.contains( starsAcctInfo ))
				activeAccounts.add( starsAcctInfo );
		}
	}
	
	public void unregisterActiveAccount(StarsCustAccountInformation starsAcctInfo) {
		ArrayList activeAccounts = getActiveAccounts();
		synchronized (activeAccounts) { activeAccounts.remove( starsAcctInfo ); }
	}
	
	public void clearActiveAccounts() {
		activeAccounts = null;
		lmCtrlHists = null;
		starsLMCtrlHists = null;
	}
	
	private Hashtable getStarsLMCtrlHists() {
		if (starsLMCtrlHists == null)
			starsLMCtrlHists = new Hashtable();
		return starsLMCtrlHists;
	}
	
	public StarsLMControlHistory getStarsLMControlHistory(int groupID) {
		Hashtable starsLMCtrlHists = getStarsLMCtrlHists();
		synchronized (starsLMCtrlHists) {
			StarsLMControlHistory starsCtrlHist = (StarsLMControlHistory) starsLMCtrlHists.get( new Integer(groupID) );
			
			if (starsCtrlHist == null) {
				LiteStarsLMControlHistory liteCtrlHist = getLMControlHistory( groupID );
				if (liteCtrlHist != null) {
					starsCtrlHist = StarsLiteFactory.createStarsLMControlHistory(
							liteCtrlHist, StarsCtrlHistPeriod.ALL, false );
					starsLMCtrlHists.put( new Integer(groupID), starsCtrlHist );
				}
				else
					starsCtrlHist = new StarsLMControlHistory();
			}
			
			return starsCtrlHist;
		}
	}
	
	public StarsLMControlHistory updateLMControlHistory(LiteStarsLMControlHistory liteCtrlHist) {
		ArrayList ctrlHist = liteCtrlHist.getLmControlHistory();
		
		int lastCtrlHistID = 0;
		if (ctrlHist.size() > 0)
			lastCtrlHistID = ((LiteLMControlHistory) ctrlHist.get(ctrlHist.size() - 1)).getLmCtrlHistID();
		
		com.cannontech.database.db.pao.LMControlHistory[] ctrlHists = com.cannontech.stars.util.LMControlHistoryUtil.getLMControlHistory(
				new Integer(liteCtrlHist.getGroupID()), new Integer(lastCtrlHistID) );
		if (ctrlHists != null) {
			for (int i= 0; i < ctrlHists.length; i++)
				ctrlHist.add( (LiteLMControlHistory) StarsLiteFactory.createLite(ctrlHists[i]) );
		}
		
		Hashtable starsLMCtrlHists = getStarsLMCtrlHists();
		synchronized (starsLMCtrlHists) {
			StarsLMControlHistory starsCtrlHist = (StarsLMControlHistory)
					starsLMCtrlHists.get( new Integer(liteCtrlHist.getGroupID()) );
			if (starsCtrlHist != null)
				StarsLiteFactory.setStarsLMControlHistory( starsCtrlHist, liteCtrlHist, StarsCtrlHistPeriod.ALL, false );
			
			return starsCtrlHist;
		}
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
					
					starsSettings.setStarsThermostatProgram( StarsLiteFactory.createStarsThermostatProgram(liteSettings.getThermostatSchedule(), this) );
					if (starsSettings.getStarsThermostatDynamicData() != null) {
						StarsLiteFactory.setStarsThermostatDynamicData(
								starsSettings.getStarsThermostatDynamicData(), liteSettings.getDynamicData(), this );
					}
					
					break;
				}
			}
		}
	}
	
	public void resetOddsForControl() {
		YukonSelectionList ctrlOddsList = getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL );
		YukonListEntry dftEntry = null;
		if (ctrlOddsList.getYukonListEntries().size() > 0)
			dftEntry = (YukonListEntry) ctrlOddsList.getYukonListEntries().get(0);
		
		ArrayList programs = getAllLMPrograms();
		synchronized (programs) {
			for (int i = 0; i < programs.size(); i++) {
				//TODO: Change LiteLMProgram to LiteProgramWebPublishing
			}
		}
	}
	
	private void loadEnergyCompanyHierarchy() {
		String sql = "SELECT EnergyCompanyID, ItemID " +
				"FROM ECToGenericMapping " +
				"WHERE MappingCategory='EnergyCompany' " +
				"AND (EnergyCompanyID=" + getEnergyCompanyID() +
				" OR ItemID=" + getEnergyCompanyID() + ")";
		
		try {
			SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
			stmt.execute();
			
			children = new ArrayList();
			
			for (int i = 0; i < stmt.getRowCount(); i++) {
				int parentID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
				int childID = ((java.math.BigDecimal) stmt.getRow(i)[1]).intValue();
				
				if (parentID == getLiteID())
					children.add( SOAPServer.getEnergyCompany(childID) );
				else	// childID == getLiteID()
					parent = SOAPServer.getEnergyCompany( parentID );
			}
			
			hierarchyLoaded = true;
			CTILogger.info( "Energy company hierarchy loaded for energy company #" + getEnergyCompanyID() );
		}
		catch (CommandExecutionException e) {
			CTILogger.error( e.getMessage(), e );
		}
	}
	
	public synchronized LiteStarsEnergyCompany getParent() {
		if (!hierarchyLoaded)
			loadEnergyCompanyHierarchy();
		return parent;
	}
	
	public synchronized ArrayList getChildren() {
		if (!hierarchyLoaded)
			loadEnergyCompanyHierarchy();
		return children;
	}
	
	public synchronized ArrayList getMemberLoginIDs() {
		if (memberLoginIDs == null) {
			String sql = "SELECT ItemID FROM ECToGenericMapping " +
					"WHERE MappingCategory='MemberLogin' " +
					"AND EnergyCompanyID=" + getEnergyCompanyID();
			
			try {
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
				
				memberLoginIDs = new ArrayList();
				
				for (int i = 0; i < stmt.getRowCount(); i++) {
					memberLoginIDs.add(
						new Integer(((java.math.BigDecimal)stmt.getRow(i)[0]).intValue()) );
				}
				
				CTILogger.info( "All member logins loaded for energy company #" + getEnergyCompanyID() );
			}
			catch (CommandExecutionException e) {
				CTILogger.error( e.getMessage(), e );
			}
		}
		
		return memberLoginIDs;
	}

}
