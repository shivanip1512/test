package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import com.cannontech.database.cache.StarsDatabaseCache;
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
import com.cannontech.database.db.macro.MacroTypes;
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
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.task.LoadCustAccountsTask;
import com.cannontech.stars.util.task.LoadInventoryTask;
import com.cannontech.stars.util.task.LoadWorkOrdersTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
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
import com.cannontech.stars.xml.serialize.StarsServiceCompanies;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.stars.xml.serialize.StarsThermostatProgram;
import com.cannontech.stars.xml.serialize.StarsThermostatSettings;

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
	
	private Hashtable custAccountInfos = null;	// Map of Integer(AccountID) to LiteStarsCustAccountInformation
	private Hashtable addresses = null;			// Map of Integer(AddressID) to LiteAddress
	private Hashtable inventory = null;			// Map of Integer(InventoryID) to LiteInventoryBase
	private Hashtable workOrders = null;		// Map of Integer(OrderID) to LiteWorkOrderBase
	
	private ArrayList pubPrograms = null;		// List of LiteLMProgramWebPublishing
	private ArrayList appCategories = null;		// List of LiteApplianceCategory
	private ArrayList serviceCompanies = null;	// List of LiteServiceCompany
	private ArrayList selectionLists = null;	// List of YukonSelectionList
	private ArrayList interviewQuestions = null;	// List of LiteInterviewQuestion
	private ArrayList customerFAQs = null;		// List of LiteCustomerFAQ
	
	private ArrayList operatorLoginIDs = null;	// List of operator login IDs (Integer)
	private ArrayList routeIDs = null;			// List of route IDs (Integer)
	
	// Map of hardware type yukondefid (Integer) to LiteLMThermostatSchedule
	private Hashtable dftThermSchedules = null;
	
	private long nextCallNo = 0;
	private long nextOrderNo = 0;
	
	private boolean accountsLoaded = false;
	private boolean inventoryLoaded = false;
	private boolean workOrdersLoaded = false;
	private boolean hierarchyLoaded = false;
	
	// IDs of data loading tasks
	private long loadAccountsTaskID = 0;
	private long loadInvTaskID = 0;
	private long loadOrdersTaskID = 0;
	
	private int dftRouteID = CtiUtilities.NONE_ID;
	private int operDftGroupID = com.cannontech.database.db.user.YukonGroup.EDITABLE_MIN_GROUP_ID - 1;
	
	private OptOutEventQueue optOutEventQueue = null;
	private SwitchCommandQueue switchCommandQueue = null;
	
	// Map of contact ID to account ID (Integer, Integer)
	private Hashtable contactAccountIDMap = null;
	
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
	private Hashtable starsCustAcctInfos = null;	// Map Integer(account ID) to StarsCustAccountInformation
	
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
			
			String sql = 
				"select gm.CHILDID from UserPaoOwner us, GENERICMACRO gm " +
				"WHERE gm.OWNERID=us.PaoID AND us.UserID=" + getUserID() +
				" AND gm.MacroType = '" + MacroTypes.GROUP + "'" +
				" ORDER BY gm.CHILDORDER";

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
		TimeZone dftTimeZone = TimeZone.getDefault();
		String tz = getEnergyCompanySetting(EnergyCompanyRole.DEFAULT_TIME_ZONE);
		if (tz != null)
			dftTimeZone = TimeZone.getTimeZone( tz );
		
		return dftTimeZone;
	}
	
	public String getAdminEmailAddress() {
		String adminEmail = getEnergyCompanySetting( EnergyCompanyRole.ADMIN_EMAIL_ADDRESS );
		if (adminEmail == null || adminEmail.trim().length() == 0)
			adminEmail = StarsUtils.ADMIN_EMAIL_ADDRESS;
		
		return adminEmail;
	}
	
	public boolean isAccountsLoaded() {
		return accountsLoaded;
	}
	
	public void setAccountsLoaded(boolean loaded) {
		accountsLoaded = loaded;
	}
	
	public boolean isInventoryLoaded() {
		return inventoryLoaded;
	}
	
	public void setInventoryLoaded(boolean loaded) {
		inventoryLoaded = loaded;
	}
	
	public boolean isWorkOrdersLoaded() {
		return workOrdersLoaded;
	}
	
	public void setWorkOrdersLoaded(boolean loaded) {
		workOrdersLoaded = loaded;
	}
	
	public ArrayList loadAllCustomerAccounts(boolean blockOnWait) {
		synchronized (LoadCustAccountsTask.class) {
			if (isAccountsLoaded()) return getAllCustAccountInformation();
			
			if (!isAccountsLoaded() && loadAccountsTaskID == 0 && !ECUtils.isDefaultEnergyCompany( this )) {
				loadAccountsTaskID = ProgressChecker.addTask( new LoadCustAccountsTask(this) );
				CTILogger.info( "*** Start loading customer accounts for energy company #" + getEnergyCompanyID() );
			}
			
			if (!blockOnWait) return null;
			
			while (true) {
				TimeConsumingTask task = ProgressChecker.getTask( loadAccountsTaskID );
				if (task == null) return null;
				
				if (task.getStatus() == TimeConsumingTask.STATUS_FINISHED
					|| task.getStatus() == TimeConsumingTask.STATUS_ERROR
					|| task.getStatus() == TimeConsumingTask.STATUS_CANCELED)
				{
					ProgressChecker.removeTask( loadAccountsTaskID );
					loadAccountsTaskID = 0;
					
					if (isAccountsLoaded())
						return getAllCustAccountInformation();
					else
						return null;
				}
				
				try {
					Thread.sleep( 1000 );
				}
				catch (InterruptedException e) {}
			}
		}
	}
	
	public ArrayList loadAllInventory(boolean blockOnWait) {
		synchronized (LoadInventoryTask.class) {
			if (isInventoryLoaded()) return getAllInventory();
			
			if (loadInvTaskID == 0 && !ECUtils.isDefaultEnergyCompany( this )) {
				loadInvTaskID = ProgressChecker.addTask( new LoadInventoryTask(this) );
				CTILogger.info( "*** Start loading inventory for energy company #" + getEnergyCompanyID() );
			}
			
			if (!blockOnWait) return null;
			
			while (true) {
				TimeConsumingTask task = ProgressChecker.getTask( loadInvTaskID );
				if (task == null) return null;
				
				if (task.getStatus() == TimeConsumingTask.STATUS_FINISHED
					|| task.getStatus() == TimeConsumingTask.STATUS_ERROR
					|| task.getStatus() == TimeConsumingTask.STATUS_CANCELED)
				{
					ProgressChecker.removeTask( loadInvTaskID );
					loadInvTaskID = 0;
					
					if (isInventoryLoaded())
						return getAllInventory();
					else
						return null;
				}
				
				try {
					Thread.sleep( 1000 );
				}
				catch (InterruptedException e) {}
			}
		}
	}
	
	public ArrayList loadAllWorkOrders(boolean blockOnWait) {
		synchronized (LoadWorkOrdersTask.class) {
			if (isWorkOrdersLoaded()) return getAllWorkOrders();
			
			if (!isWorkOrdersLoaded() && loadOrdersTaskID == 0 && !ECUtils.isDefaultEnergyCompany( this )) {
				loadOrdersTaskID = ProgressChecker.addTask( new LoadWorkOrdersTask(this) );
				CTILogger.info( "*** Start loading work orders for energy company #" + getEnergyCompanyID() );
			}
			
			if (!blockOnWait) return null;
			
			while (true) {
				TimeConsumingTask task = ProgressChecker.getTask( loadOrdersTaskID );
				if (task == null) return null;
				
				if (task.getStatus() == TimeConsumingTask.STATUS_FINISHED
					|| task.getStatus() == TimeConsumingTask.STATUS_ERROR
					|| task.getStatus() == TimeConsumingTask.STATUS_CANCELED)
				{
					ProgressChecker.removeTask( loadOrdersTaskID );
					loadOrdersTaskID = 0;
					
					if (isWorkOrdersLoaded())
						return getAllWorkOrders();
					else
						return null;
				}
				
				try {
					Thread.sleep( 1000 );
				}
				catch (InterruptedException e) {}
			}
		}
	}
	
	public synchronized void init() {
		loadAllInventory( false );
		loadAllWorkOrders( false );
	}
	
	public void clear() {
		// If any of the data loading tasks are alive, cancel them first
		TimeConsumingTask loadAccountsTask = ProgressChecker.getTask( loadAccountsTaskID );
		if (loadAccountsTask != null) loadAccountsTask.cancel();
		TimeConsumingTask loadInvTask = ProgressChecker.getTask( loadInvTaskID );
		if (loadInvTask != null) loadInvTask.cancel();
		TimeConsumingTask loadOrdersTask = ProgressChecker.getTask( loadOrdersTaskID );
		if (loadOrdersTask != null) loadOrdersTask.cancel();
		
		// Wait up to 3 seconds for them to stop
		for (int i = 0; i < 6; i++) {
			if ((loadAccountsTask == null
					|| loadAccountsTask.getStatus() == LoadCustAccountsTask.STATUS_FINISHED
					|| loadAccountsTask.getStatus() == LoadCustAccountsTask.STATUS_CANCELED
					|| loadAccountsTask.getStatus() == LoadCustAccountsTask.STATUS_ERROR)
				&& (loadInvTask == null
					|| loadInvTask.getStatus() == LoadInventoryTask.STATUS_FINISHED
					|| loadInvTask.getStatus() == LoadInventoryTask.STATUS_CANCELED
					|| loadInvTask.getStatus() == LoadInventoryTask.STATUS_ERROR)
				&& (loadOrdersTask == null
					|| loadOrdersTask.getStatus() == LoadWorkOrdersTask.STATUS_FINISHED
					|| loadOrdersTask.getStatus() == LoadWorkOrdersTask.STATUS_CANCELED
					|| loadOrdersTask.getStatus() == LoadWorkOrdersTask.STATUS_ERROR))
				break;
			
			try {
				Thread.sleep( 500 );
			}
			catch (InterruptedException e) {}
		}
		
		if (loadAccountsTaskID > 0) {
			ProgressChecker.removeTask( loadAccountsTaskID );
			loadAccountsTaskID = 0;
		}
		if (loadInvTaskID > 0) {
			ProgressChecker.removeTask( loadInvTaskID );
			loadInvTaskID = 0;
		}
		if (loadOrdersTaskID > 0) {
			ProgressChecker.removeTask( loadOrdersTaskID );
			loadOrdersTaskID = 0;
		}
		
		accountsLoaded = false;
		inventoryLoaded = false;
		workOrdersLoaded = false;
		
		custAccountInfos = null;
		addresses = null;
		pubPrograms = null;
		inventory = null;
		appCategories = null;
		workOrders = null;
		serviceCompanies = null;
		selectionLists = null;
		interviewQuestions = null;
		customerFAQs = null;
		
		operatorLoginIDs = null;
		routeIDs = null;
		dftThermSchedules = null;
		
		nextCallNo = 0;
		nextOrderNo = 0;
		hierarchyLoaded = false;
		
		dftRouteID = CtiUtilities.NONE_ID;
		operDftGroupID = com.cannontech.database.db.user.YukonGroup.EDITABLE_MIN_GROUP_ID - 1;
		
		optOutEventQueue = null;
		switchCommandQueue = null;
		
		contactAccountIDMap = null;
		
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
		starsCustAcctInfos = null;
		
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
	
	public LiteYukonGroup getOperatorAdminGroup() {
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
	
	/**
	 * Get programs published by this energy company only
	 */
	public synchronized ArrayList getPrograms() {
		if (pubPrograms == null)
			getApplianceCategories();
		return pubPrograms;
	}
    
	/**
	 * Get all published programs including those inherited from the parent company
	 */
	public synchronized ArrayList getAllPrograms() {
		ArrayList pubProgs = new ArrayList( getPrograms() );
		if (getParent() != null)
			pubProgs.addAll( 0, getParent().getPrograms() );
		
		return pubProgs;
	}
	
	/**
	 * Get appliance categories created in this energy company only
	 */
	public synchronized ArrayList getApplianceCategories() {
		if (appCategories == null) {
			appCategories = new ArrayList();
			pubPrograms = new ArrayList();
    		
			com.cannontech.database.db.stars.appliance.ApplianceCategory[] appCats =
					com.cannontech.database.db.stars.appliance.ApplianceCategory.getAllApplianceCategories( getEnergyCompanyID() );
    				
			for (int i = 0; i < appCats.length; i++) {
				LiteApplianceCategory appCat = (LiteApplianceCategory) StarsLiteFactory.createLite( appCats[i] );
				appCat.setDirectOwner( this );
    			
				com.cannontech.database.db.stars.LMProgramWebPublishing[] pubProgs =
						com.cannontech.database.db.stars.LMProgramWebPublishing.getAllLMProgramWebPublishing( appCats[i].getApplianceCategoryID().intValue() );
    			
				for (int j = 0; j < pubProgs.length; j++) {
					LiteLMProgramWebPublishing program = (LiteLMProgramWebPublishing) StarsLiteFactory.createLite(pubProgs[j]);
					pubPrograms.add( program );
					appCat.getPublishedPrograms().add( program );
				}
    			
				appCategories.add( appCat );
			}
	    	
			CTILogger.info( "All appliance categories loaded for energy company #" + getEnergyCompanyID() );
		}
		
		return appCategories;
	}
    
	/**
	 * Get all appliance categories including those inherited from the parent company
	 * (may need to add a role property to control this in the future).
	 */
	public synchronized ArrayList getAllApplianceCategories() {
		ArrayList appCats = new ArrayList( getApplianceCategories() );
		if (getParent() != null)
			appCats.addAll( 0, getParent().getAllApplianceCategories() );
    	
		return appCats;
	}

	public synchronized ArrayList getAllSelectionLists() {
		if (getParent() != null)
			return getParent().getAllSelectionLists();
		
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
			subList.setOrdering( "A" );
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
				
				// Order the substation list alphabetically
				Collections.sort( entries, StarsUtils.YUK_LIST_ENTRY_ALPHA_CMPTR );
				
				selectionLists.add( subList );
			}
		}
		
		return selectionLists;
	}
	
	public ArrayList getAllSelectionLists(StarsYukonUser user) {
		ArrayList selectionLists = getAllSelectionLists();
		ArrayList userLists = new ArrayList();
		
		synchronized (selectionLists) {
			if (ECUtils.isOperator( user )) {
				TreeMap listMap = new TreeMap();
				listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE,
						getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE) );
				
				if (AuthFuncs.checkRole(user.getYukonUser(), OddsForControlRole.ROLEID) != null)
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL) );
				
				if (AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_GENERAL))
					listMap.put( com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION,
							getYukonSelectionList(com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION) );
				
				if (AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_CALL_TRACKING))
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE) );
				
				if (AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT))
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD) );
				
				if (AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_APPLIANCES) ||
					AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_APPLIANCES_CREATE))
				{
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION) );
					
					ArrayList categories = getAllApplianceCategories();
					ArrayList catDefIDs = new ArrayList();
					
					for (int i = 0; i < categories.size(); i++) {
						LiteApplianceCategory liteAppCat = (LiteApplianceCategory) categories.get(i);
						int catDefID = YukonListFuncs.getYukonListEntry( liteAppCat.getCategoryID() ).getYukonDefID();
						if (catDefIDs.contains( new Integer(catDefID) )) continue;
						catDefIDs.add( new Integer(catDefID) );
						
						if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER) {
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_AC_TONNAGE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_AC_TONNAGE) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE) );
						}
						else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER) {
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_WH_NUM_OF_GALLONS,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_WH_NUM_OF_GALLONS) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_WH_ENERGY_SOURCE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_WH_ENERGY_SOURCE) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_WH_LOCATION,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_WH_LOCATION) );
						}
						else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL) {
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DF_SWITCH_OVER_TYPE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DF_SWITCH_OVER_TYPE) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DF_SECONDARY_SOURCE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DF_SECONDARY_SOURCE) );
						}
						else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR) {
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_TYPE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_TYPE) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_MFG,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_MFG) );
						}
						else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER) {
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GRAIN_DRYER_TYPE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GRAIN_DRYER_TYPE) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GD_BIN_SIZE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GD_BIN_SIZE) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GD_ENERGY_SOURCE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GD_ENERGY_SOURCE) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GD_HORSE_POWER,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GD_HORSE_POWER) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GD_HEAT_SOURCE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GD_HEAT_SOURCE) );
						}
						else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT) {
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_HEAT_TYPE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_HEAT_TYPE) );
						}
						else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP) {
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_TYPE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_TYPE) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_SIZE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_SIZE) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_HP_STANDBY_SOURCE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HP_STANDBY_SOURCE) );
						}
						else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION) {
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_IRR_HORSE_POWER,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_HORSE_POWER) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_IRR_ENERGY_SOURCE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_ENERGY_SOURCE) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_IRR_SOIL_TYPE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_SOIL_TYPE) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_LOCATION,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_LOCATION) );
							listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_VOLTAGE,
									getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_VOLTAGE) );
						}
					}
				}
				
				if (AuthFuncs.checkRole(user.getYukonUser(), InventoryRole.ROLEID) != null ||
					AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_HARDWARES) ||
					AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_HARDWARES_CREATE))
				{
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_LOCATION,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_LOCATION) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE) );
				}
				
				if (AuthFuncs.checkRole(user.getYukonUser(), WorkOrderRole.ROLEID) != null ||
					AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_WORK_ORDERS))
				{
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS) );
				}
				
				if (AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_RESIDENCE)) {
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_RESIDENCE_TYPE,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_RESIDENCE_TYPE) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_CONSTRUCTION_MATERIAL,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CONSTRUCTION_MATERIAL) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_DECADE_BUILT,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DECADE_BUILT) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SQUARE_FEET,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SQUARE_FEET) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_INSULATION_DEPTH,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_INSULATION_DEPTH) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_GENERAL_CONDITION,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_GENERAL_CONDITION) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_COOLING_SYSTEM,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_COOLING_SYSTEM) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_HEATING_SYSTEM,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_HEATING_SYSTEM) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_OCCUPANTS,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_OCCUPANTS) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_OWNERSHIP_TYPE,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OWNERSHIP_TYPE) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_FUEL_TYPE,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_FUEL_TYPE) );
				}
				
				if (AuthFuncs.checkRole(user.getYukonUser(), InventoryRole.ROLEID) != null) {
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_INV_SEARCH_BY,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_INV_SEARCH_BY) );
					if (AuthFuncs.checkRoleProperty(user.getYukonUser(), InventoryRole.INVENTORY_SHOW_ALL)) {
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_INV_SORT_BY,
								getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_INV_SORT_BY) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY,
								getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY) );
					}
				}
				
				if (AuthFuncs.checkRole(user.getYukonUser(), WorkOrderRole.ROLEID) != null) {
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SO_SEARCH_BY,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SO_SEARCH_BY) );
					if (AuthFuncs.checkRoleProperty(user.getYukonUser(), WorkOrderRole.WORK_ORDER_SHOW_ALL)) {
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SO_SORT_BY,
								getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SO_SORT_BY) );
						listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_SO_FILTER_BY,
								getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SO_FILTER_BY) );
					}
				}
				
				if (AuthFuncs.checkRoleProperty(user.getYukonUser(), AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY)) {
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY) );
					listMap.put( YukonSelectionListDefs.YUK_LIST_NAME_ANSWER_TYPE,
							getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_ANSWER_TYPE) );
				}
				
				Iterator it = listMap.values().iterator();
				while (it.hasNext())
					userLists.add( it.next() );
			}
			else if (ECUtils.isResidentialCustomer( user )) {
				userLists.add( getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS) );
				
				if (AuthFuncs.checkRoleProperty(user.getYukonUser(), ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT)) {
					YukonSelectionList list = getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD_CUS);
					if (list != null) userLists.add( list );
				}
			}
		}
		
		return userLists;
	}
	
	public YukonSelectionList getYukonSelectionList(String listName, boolean useDefault) {
		// If parent company exists, inherite the selection list from it
		if (getParent() != null)
			return getParent().getYukonSelectionList(listName, useDefault);
		
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
				YukonSelectionList dftList = StarsDatabaseCache.getInstance().getDefaultEnergyCompany().getYukonSelectionList( listName, false );
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
	
	public YukonListEntry getYukonListEntry(int yukonDefID, boolean useDefault) {
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
		if (useDefault && !ECUtils.isDefaultEnergyCompany( this ))
			return StarsDatabaseCache.getInstance().getDefaultEnergyCompany().getYukonListEntry( yukonDefID );
		
		return null;
	}
	
	public YukonListEntry getYukonListEntry(int yukonDefID) {
		return getYukonListEntry(yukonDefID, true);
	}
	
	public synchronized ArrayList getServiceCompanies() {
		if (serviceCompanies == null) {
			serviceCompanies = new ArrayList();
			
			com.cannontech.database.db.stars.report.ServiceCompany[] companies =
					com.cannontech.database.db.stars.report.ServiceCompany.getAllServiceCompanies( getEnergyCompanyID() );
			for (int i = 0; i < companies.length; i++) {
				LiteServiceCompany liteCompany = (LiteServiceCompany) StarsLiteFactory.createLite(companies[i]);
				liteCompany.setDirectOwner( this );
				serviceCompanies.add( liteCompany );
			}
			
			CTILogger.info( "All service companies loaded for energy company #" + getEnergyCompanyID() );
		}
		
		return serviceCompanies;
	}
	
	public synchronized ArrayList getAllServiceCompanies() {
		ArrayList companies = new ArrayList( getServiceCompanies() );
		if (getParent() != null)
			companies.addAll( 0, getParent().getAllServiceCompanies() );
		
		return companies;
	}
	
	public synchronized ArrayList getOperatorLoginIDs() {
		if (operatorLoginIDs == null) {
			operatorLoginIDs = new ArrayList();
			
			SqlStatement stmt = new SqlStatement(
					"SELECT OperatorLoginID FROM EnergyCompanyOperatorLoginList WHERE EnergyCompanyID=" + getEnergyCompanyID(),
					CtiUtilities.getDatabaseAlias() );
			
			try {
				stmt.execute();
				
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
	
	public LiteYukonPAObject[] getRoutes(LiteYukonPAObject[] inheritedRoutes) {
		ArrayList routeList = new ArrayList();
		ArrayList routeIDs = getRouteIDs();
		
		synchronized (routeIDs) {
			Iterator it = routeIDs.iterator();
			while (it.hasNext()) {
				Integer routeID = (Integer) it.next();
				LiteYukonPAObject liteRoute = PAOFuncs.getLiteYukonPAO( routeID.intValue() );
				
				// Check to see if the route is already assigned to the parent company, if so, remove it from the member
				boolean foundInParent = false;
				if (inheritedRoutes != null) {
					for (int j = 0; j < inheritedRoutes.length; j++) {
						if (inheritedRoutes[j].equals( liteRoute )) {
							foundInParent = true;
							break;
						}
					}
				}
				
				if (foundInParent) {
					ECToGenericMapping map = new ECToGenericMapping();
					map.setEnergyCompanyID( getEnergyCompanyID() );
					map.setItemID( routeID );
					map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_ROUTE );
					
					try {
						Transaction.createTransaction( Transaction.DELETE, map ).execute();
					}
					catch (TransactionException e) {
						CTILogger.error( e.getMessage(), e );
					}
					
					it.remove();
				}
				else
					routeList.add( liteRoute );
			}
		}
		
		java.util.Collections.sort( routeList, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		
		LiteYukonPAObject[] routes = new LiteYukonPAObject[ routeList.size() ];
		routeList.toArray( routes );
		return routes;
	}
	
	/**
	 * Returns all routes assigned to this energy company (or all routes in yukon
	 * if it is a single energy company system), ordered alphabetically.
	 */
	public LiteYukonPAObject[] getAllRoutes() {
		if (Boolean.valueOf(getEnergyCompanySetting( EnergyCompanyRole.SINGLE_ENERGY_COMPANY )).booleanValue()) {
			return PAOFuncs.getAllLiteRoutes();
		}
		else {
			ArrayList routeList = new ArrayList();
			
			LiteYukonPAObject[] inheritedRoutes = null;
			if (getParent() != null)
				inheritedRoutes = getParent().getAllRoutes();
			
			LiteYukonPAObject[] routes = getRoutes( inheritedRoutes );
			for (int i = 0; i < routes.length; i++)
				routeList.add( routes[i] );
			
			if (inheritedRoutes != null) {
				for (int i = 0; i < inheritedRoutes.length; i++)
					routeList.add( inheritedRoutes[i] );
			}
			
			java.util.Collections.sort( routeList, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
			
			LiteYukonPAObject[] allRoutes = new LiteYukonPAObject[ routeList.size() ];
			routeList.toArray( allRoutes );
			return allRoutes;
		}
	}
	
	public synchronized ArrayList getRouteIDs() {
		if (routeIDs == null) {
			routeIDs = new ArrayList();
			
			ECToGenericMapping[] items = ECToGenericMapping.getAllMappingItems(
					getEnergyCompanyID(), ECToGenericMapping.MAPPING_CATEGORY_ROUTE );
			
			if (items != null) {
				for (int i = 0; i < items.length; i++)
					routeIDs.add( items[i].getItemID() );
			}
			
			if (getDefaultRouteID() > 0) {
				// Make sure the default route ID is in the list
				Integer dftRouteID = new Integer( getDefaultRouteID() );
				
				if (!routeIDs.contains( dftRouteID )) {
					ECToGenericMapping map = new ECToGenericMapping();
					map.setEnergyCompanyID( getEnergyCompanyID() );
					map.setItemID( dftRouteID );
					map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_ROUTE );
					
					try {
						Transaction.createTransaction( Transaction.INSERT, map ).execute();
						routeIDs.add( dftRouteID );
					}
					catch (TransactionException e) {
						CTILogger.error( e.getMessage(), e );
					}
				}
			}
		}
		
		return routeIDs;
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
				YukonSelectionList dftList = StarsDatabaseCache.getInstance().getDefaultEnergyCompany().getYukonSelectionList( listName, false );
				list = addYukonSelectionList( listName, dftList, true );
				ArrayList dftFAQs = StarsDatabaseCache.getInstance().getDefaultEnergyCompany().getAllCustomerFAQs();
				
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
					   + "WHERE map.EnergyCompanyID = " + getEnergyCompanyID() + " AND call.CallID = map.CallReportID";
			SqlStatement stmt = new SqlStatement(
					sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
			
			try {
				stmt.execute();
				long maxCallNo = 0;
				
				for (int i = 0; i < stmt.getRowCount(); i++) {
					try {
						long callNo = Long.parseLong( (String)stmt.getRow(i)[0] );
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
		
		try {
			String val = getEnergyCompanySetting( ConsumerInfoRole.CALL_NUMBER_AUTO_GEN );
			if (val != null) {
				long initCallNo = Long.parseLong( val );
				if (nextCallNo < initCallNo) nextCallNo = initCallNo;
			}
		}
		catch (NumberFormatException e) {}
		
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
					   + "WHERE map.EnergyCompanyID = " + getEnergyCompanyID() + " AND service.OrderID = map.WorkOrderID";
			SqlStatement stmt = new SqlStatement(
					sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
			
			try {
				stmt.execute();
				long maxOrderNo = 0;
				
				for (int i = 0; i < stmt.getRowCount(); i++) {
					try {
						long orderNo = Long.parseLong( (String)stmt.getRow(i)[0] );
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
		
		try {
			String val = getEnergyCompanySetting( ConsumerInfoRole.ORDER_NUMBER_AUTO_GEN );
			if (val != null) {
				long initOrderNo = Long.parseLong( val );
				if (nextOrderNo < initOrderNo) nextOrderNo = initOrderNo;
			}
		}
		catch (NumberFormatException e) {}
		
		return String.valueOf( nextOrderNo++ );
	}
	
	public synchronized void resetNextOrderNumber() {
		nextOrderNo = 0;
	}
	
	private synchronized Hashtable getAddressMap() {
		if (addresses == null)
			addresses = new Hashtable();
		
		return addresses;
	}
	
	public ArrayList getAllAddresses() {
		return new ArrayList( getAddressMap().values() );
	}
	
	private synchronized Hashtable getInventoryMap() {
		if (inventory == null)
			inventory = new Hashtable();
		
		return inventory;
	}
	
	public ArrayList getAllInventory() {
		return new ArrayList( getInventoryMap().values() );
	}
	
	private synchronized Hashtable getWorkOrderMap() {
		if (workOrders == null)
			workOrders = new Hashtable();
		
		return workOrders;
	}
	
	public ArrayList getAllWorkOrders() {
		return new ArrayList( getWorkOrderMap().values() );
	}
	
	private synchronized Hashtable getCustAccountInfoMap() {
		if (custAccountInfos == null)
			custAccountInfos = new Hashtable();
		
		return custAccountInfos;
	}
	
	public ArrayList getAllCustAccountInformation() {
		return new ArrayList( getCustAccountInfoMap().values() );
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
		for (int i = 0; i < appCats.size(); i++) {
			LiteApplianceCategory appCat = (LiteApplianceCategory) appCats.get(i);
			if (appCat.getApplianceCategoryID() == applianceCategoryID)
				return appCat;
		}
		
		return null;
	}
	
	public void addApplianceCategory(LiteApplianceCategory appCat) {
		appCat.setDirectOwner( this );
		ArrayList appCats = getApplianceCategories();
		synchronized (appCats) { appCats.add( appCat ); }
	}
	
	public LiteApplianceCategory deleteApplianceCategory(int applianceCategoryID) {
		ArrayList appCats = getApplianceCategories();
		ArrayList programs = getPrograms();
		
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
		serviceCompany.setDirectOwner( this );
		ArrayList serviceCompanies = getServiceCompanies();
		synchronized (serviceCompanies) { serviceCompanies.add(serviceCompany); }
	}
	
	public LiteServiceCompany deleteServiceCompany(int serviceCompanyID) {
		ArrayList serviceCompanies = getServiceCompanies();
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
	
	private LiteAddress getAddress(int addressID, boolean autoLoad) {
		LiteAddress liteAddr = (LiteAddress) getAddressMap().get( new Integer(addressID) );
		if (liteAddr != null) return liteAddr;
		
		if (autoLoad) {
			try {
				com.cannontech.database.db.customer.Address addr = new com.cannontech.database.db.customer.Address();
				addr.setAddressID( new Integer(addressID) );
				addr = (com.cannontech.database.db.customer.Address)
						Transaction.createTransaction( Transaction.RETRIEVE, addr ).execute();
				
				liteAddr = (LiteAddress) StarsLiteFactory.createLite( addr );
				addAddress( liteAddr );
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
		getAddressMap().put( new Integer(liteAddr.getAddressID()), liteAddr );
	}
	
	public LiteAddress deleteAddress(int addressID) {
		return (LiteAddress) getAddressMap().remove( new Integer(addressID) );
	}
	
	public LiteLMProgramWebPublishing getProgram(int programID) {
		ArrayList programs = getAllPrograms();
		for (int i = 0; i < programs.size(); i++) {
			LiteLMProgramWebPublishing liteProg = (LiteLMProgramWebPublishing) programs.get(i);
			if (liteProg.getProgramID() == programID)
				return liteProg;
		}
		
		return null;
	}
	
	public void addProgram(LiteLMProgramWebPublishing liteProg, LiteApplianceCategory liteAppCat) {
		ArrayList programs = getPrograms();
		synchronized (programs) { programs.add(liteProg); }
		
		liteAppCat.getPublishedPrograms().add( liteProg );
	}
	
	public LiteLMProgramWebPublishing deleteProgram(int programID) {
		ArrayList programs = getPrograms();
		ArrayList appCats = getApplianceCategories();
		
		synchronized (programs) {
			synchronized (appCats) {
				for (int i = 0; i < programs.size(); i++) {
					LiteLMProgramWebPublishing liteProg = (LiteLMProgramWebPublishing) programs.get(i);
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
	
	public LiteInventoryBase getInventoryBrief(int inventoryID, boolean autoLoad) {
		LiteInventoryBase liteInv = (LiteInventoryBase) getInventoryMap().get( new Integer(inventoryID) );
		if (liteInv != null) return liteInv;
		
		if (autoLoad) {
			try {
				com.cannontech.database.db.stars.hardware.InventoryBase invDB =
						new com.cannontech.database.db.stars.hardware.InventoryBase();
				invDB.setInventoryID( new Integer(inventoryID) );
				invDB = (com.cannontech.database.db.stars.hardware.InventoryBase)
						Transaction.createTransaction( Transaction.RETRIEVE, invDB ).execute();
				
				if (ECUtils.isLMHardware( invDB.getCategoryID().intValue() )) {
					com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB =
							new com.cannontech.database.db.stars.hardware.LMHardwareBase();
					hwDB.setInventoryID( invDB.getInventoryID() );
					
					hwDB = (com.cannontech.database.db.stars.hardware.LMHardwareBase)
							Transaction.createTransaction( Transaction.RETRIEVE, hwDB ).execute();
					
					com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
							new com.cannontech.database.data.stars.hardware.LMHardwareBase();
					hardware.setInventoryBase( invDB );
					hardware.setLMHardwareBase( hwDB );
					
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
		Hashtable invMap = getInventoryMap();
		synchronized (invMap) {
			invMap.put( new Integer(liteInv.getInventoryID()), liteInv );
		}
	}
	
	public LiteInventoryBase deleteInventory(int invID) {
		Hashtable invMap = getInventoryMap();
		synchronized (invMap) {
			return (LiteInventoryBase) invMap.remove( new Integer(invID) );
		}
	}
	
	public LiteInventoryBase reloadInventory(int invID) {
		Hashtable invMap = getInventoryMap();
		synchronized (invMap) {
			invMap.remove( new Integer(invID) );
			return getInventoryBrief( invID, true );
		}
	}
	
	/**
	 * @return Pair(LiteStarsLMHardware, LiteStarsEnergyCompany)
	 */
	private Pair searchForLMHardware(int devTypeDefID, String serialNo, LiteStarsEnergyCompany referer)
		throws ObjectInOtherEnergyCompanyException
	{
		ArrayList inventory = getAllInventory();
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
		
		if (!isInventoryLoaded()) {
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
	 * Search for device with the specified category and device name.
	 * If the device belongs to another energy company, the ObjectInOtherEnergyCompanyException is thrown. 
	 */
	public LiteInventoryBase searchForDevice(int categoryID, String deviceName)
		throws ObjectInOtherEnergyCompanyException
	{
		ArrayList inventory = getAllInventory();
		for (int i = 0; i < inventory.size(); i++) {
			LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(i);
			if (liteInv.getInventoryID() < 0) continue;
			if (liteInv.getDeviceID() == CtiUtilities.NONE_ID) continue;
			
			if (liteInv.getCategoryID() == categoryID &&
				PAOFuncs.getYukonPAOName( liteInv.getDeviceID() ).equalsIgnoreCase( deviceName ))
				return liteInv;
		}
		
		if (!isInventoryLoaded()
			|| !Boolean.valueOf(getEnergyCompanySetting(EnergyCompanyRole.SINGLE_ENERGY_COMPANY)).booleanValue())
		{ 
			int[] val = com.cannontech.database.db.stars.hardware.InventoryBase.searchForDevice( categoryID, deviceName );
			
			if (val != null) {
				if (val[1] == getLiteID()) {
					return getInventoryBrief( val[0], true );
				}
				else {
					LiteStarsEnergyCompany company = StarsDatabaseCache.getInstance().getEnergyCompany( val[1] );
					LiteInventoryBase liteInv = company.getInventoryBrief( val[0], true );
					throw new ObjectInOtherEnergyCompanyException( liteInv, company );
				}
			}
		}
		
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		if (ECUtils.isMCT( categoryID )) {
			synchronized (cache) {
				java.util.List mctList = cache.getAllMCTs();
				for (int i = 0; i < mctList.size(); i++) {
					LiteYukonPAObject litePao = (LiteYukonPAObject) mctList.get(i);
					
					if (litePao.getPaoName().equalsIgnoreCase( deviceName )) {
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
	 * Search the inventory for device with the specified device ID.
	 * If the device belongs to another energy company, the ObjectInOtherEnergyCompanyException is thrown. 
	 */
	public LiteInventoryBase getDevice(int deviceID) throws ObjectInOtherEnergyCompanyException {
		ArrayList inventory = getAllInventory();
		for (int i = 0; i < inventory.size(); i++) {
			LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(i);
			if (liteInv.getDeviceID() == deviceID)
				return liteInv;
		}
		
		if (!isInventoryLoaded()
			|| !Boolean.valueOf(getEnergyCompanySetting(EnergyCompanyRole.SINGLE_ENERGY_COMPANY)).booleanValue()) 
		{
			int[] val = com.cannontech.database.db.stars.hardware.InventoryBase.searchByDeviceID( deviceID );
			
			if (val != null) {
				if (val[1] == getLiteID()) {
					return getInventoryBrief( val[0], true );
				}
				else {
					LiteStarsEnergyCompany company = StarsDatabaseCache.getInstance().getEnergyCompany( val[1] );
					LiteInventoryBase liteInv = company.getInventoryBrief( val[0], true );
					throw new ObjectInOtherEnergyCompanyException( liteInv, company );
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Search the inventory by serial number. If searchMembers is true,
	 * it returns a list of Pair(LiteInventoryBase, LiteStarsEnergyCompany);
	 * otherwise it returns a list of LiteInventoryBase.
	 */
	public ArrayList searchInventoryBySerialNo(String serialNo, boolean searchMembers) {
		ArrayList hwList = new ArrayList();
		
		if (isInventoryLoaded()) {
			ArrayList inventory = getAllInventory();
			
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
		else {
			com.cannontech.database.db.stars.hardware.LMHardwareBase[] hardwares =
					com.cannontech.database.db.stars.hardware.LMHardwareBase.searchBySerialNumber( serialNo, getLiteID() );
			if (hardwares == null) return null;
			
			for (int i = 0; i < hardwares.length; i++) {
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) getInventoryBrief( hardwares[i].getInventoryID().intValue(), true );
				if (searchMembers)
					hwList.add( new Pair(liteHw, this) );
				else
					hwList.add( liteHw );
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
					settings.setThermostatSchedule( CreateLMHardwareAction.initThermostatSchedule(liteHw, StarsDatabaseCache.getInstance().getDefaultEnergyCompany()) );
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
	
	public LiteWorkOrderBase getWorkOrderBase(int orderID, boolean autoLoad) {
		LiteWorkOrderBase workOrder = (LiteWorkOrderBase) getWorkOrderMap().get( new Integer(orderID) );
		if (workOrder != null) return workOrder;
        
		if (autoLoad) {
			try {
				com.cannontech.database.db.stars.report.WorkOrderBase order = new com.cannontech.database.db.stars.report.WorkOrderBase();
				order.setOrderID( new Integer(orderID) );
				order = (com.cannontech.database.db.stars.report.WorkOrderBase)
						Transaction.createTransaction( Transaction.RETRIEVE, order ).execute();
				workOrder = (LiteWorkOrderBase) StarsLiteFactory.createLite( order );
        		
				addWorkOrderBase( workOrder );
				return workOrder;
			}
			catch (Exception e) {
				CTILogger.error( e.getMessage(), e );
			}
		}
        
		return null;
	}
	
	public void addWorkOrderBase(LiteWorkOrderBase liteOrder) {
		getWorkOrderMap().put( new Integer(liteOrder.getOrderID()), liteOrder );
	}
	
	public void deleteWorkOrderBase(int orderID) {
		getWorkOrderMap().remove( new Integer(orderID) );
	}
	
	/**
	 * Search the work orders by order number. If searchMembers is true,
	 * it returns a list of Pair(LiteWorkOrderBase, LiteStarsEnergyCompany);
	 * otherwise it returns a list of LiteWorkOrderBase.
	 */
	public ArrayList searchWorkOrderByOrderNo(String orderNo, boolean searchMembers) {
		ArrayList orderList = new ArrayList();
		
		if (isWorkOrdersLoaded()) {
			ArrayList workOrders = getAllWorkOrders();
			
			for (int i = 0; i < workOrders.size(); i++) {
				LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) workOrders.get(i);
				if (liteOrder.getOrderNumber().equalsIgnoreCase(orderNo)) {
					if (searchMembers)
						orderList.add( new Pair(liteOrder, this) );
					else
						orderList.add( liteOrder );
					break;
				}
			}
		}
		else {
			int[] orderIDs = com.cannontech.database.db.stars.report.WorkOrderBase.searchByOrderNumber( orderNo, getLiteID() );
			if (orderIDs == null) return null;
			
			for (int i = 0; i < orderIDs.length; i++) {
				LiteWorkOrderBase liteOrder = getWorkOrderBase( orderIDs[i], true );
				if (searchMembers)
					orderList.add( new Pair(liteOrder, this) );
				else
					orderList.add( liteOrder );
			}
		}
		
		if (searchMembers) {
			ArrayList children = getChildren();
			synchronized (children) {
				for (int i = 0; i < children.size(); i++) {
					LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) children.get(i);
					ArrayList memberList = company.searchWorkOrderByOrderNo( orderNo, searchMembers );
					orderList.addAll( memberList );
				}
			}
		}
		
		return orderList;
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
		addAddress( (LiteAddress) StarsLiteFactory.createLite(streetAddr) );
		
		com.cannontech.database.db.customer.Address billAddr = account.getBillingAddress();
		addAddress( (LiteAddress) StarsLiteFactory.createLite(billAddr) );
        
		addCustAccountInformation( liteAcctInfo );
		
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
			
			ArrayList allProgs = getAllPrograms();
			int[] allProgIDs = new int[ allProgs.size() ];
			for (int i = 0; i < allProgs.size(); i++)
				allProgIDs[i] = ((LiteLMProgramWebPublishing) allProgs.get(i)).getProgramID();
			Arrays.sort( allProgIDs );
			
			ArrayList progHist = new ArrayList();
			com.cannontech.database.data.stars.event.LMProgramEvent[] events =
					com.cannontech.database.data.stars.event.LMProgramEvent.getAllLMProgramEvents( liteAcctInfo.getLiteID() );
			
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
				int progID = liteApp.getProgramID();
				if (progID == 0) continue;
				
				boolean progExists = false;
				for (int j = 0; j < programs.size(); j++) {
					if (((LiteStarsLMProgram) programs.get(j)).getProgramID() == progID) {
						progExists = true;
						break;
					}
				}
				if (progExists) continue;
	            
				LiteLMProgramWebPublishing liteProg = getProgram( progID );
				LiteStarsLMProgram prog = new LiteStarsLMProgram( liteProg );
				
				prog.setGroupID( liteApp.getAddressingGroupID() );
				prog.updateProgramStatus( progHist );
				
				programs.add( prog );
			}
			liteAcctInfo.setPrograms( programs );
	        
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
		LiteStarsCustAccountInformation liteAcctInfo =
				(LiteStarsCustAccountInformation) getCustAccountInfoMap().get( new Integer(accountID) );
		if (liteAcctInfo != null) return liteAcctInfo;
		
		if (autoLoad && !isAccountsLoaded()) {
			try {
				com.cannontech.database.db.stars.customer.CustomerAccount accountDB =
						com.cannontech.database.db.stars.customer.CustomerAccount.getCustomerAccount( new Integer(accountID) );
				if (accountDB != null) {
					com.cannontech.database.data.stars.customer.CustomerAccount account =
							new com.cannontech.database.data.stars.customer.CustomerAccount();
					account.setAccountID( accountDB.getAccountID() );
					account = (com.cannontech.database.data.stars.customer.CustomerAccount)
							Transaction.createTransaction( Transaction.RETRIEVE, account ).execute();
					
					return addBriefCustAccountInfo( account );
				}
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
	
	public void addCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo) {
		Hashtable custAcctMap = getCustAccountInfoMap();
		synchronized (custAcctMap) {
			custAcctMap.put( new Integer(liteAcctInfo.getAccountID()), liteAcctInfo );
		}
	}
	
	public void deleteCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo) {
		// Remove from opt out event queue
		OptOutEventQueue.getInstance().removeEvents( liteAcctInfo.getAccountID() );
		
		// Remove customer from the cache
		ServerUtils.handleDBChange( liteAcctInfo.getCustomer(), DBChangeMsg.CHANGE_TYPE_DELETE );
    	
		// Remote all contacts from the cache
		Hashtable contAcctIDMap = getContactAccountIDMap();
		synchronized (contAcctIDMap) {
			contAcctIDMap.remove( new Integer(liteAcctInfo.getCustomer().getPrimaryContactID()) );
			
			Vector contacts = liteAcctInfo.getCustomer().getAdditionalContacts();
			for (int i = 0; i < contacts.size(); i++)
				contAcctIDMap.remove(new Integer( ((LiteContact)contacts.get(i)).getContactID() ));
		}
		
		// Remove all addresses from the cache
		deleteAddress( liteAcctInfo.getCustomerAccount().getBillingAddressID() );
		deleteAddress( liteAcctInfo.getAccountSite().getStreetAddressID() );
		
		// Refresh all inventory information
		for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
			int invID = ((Integer) liteAcctInfo.getInventories().get(i)).intValue();
			reloadInventory( invID );
		}
		
		// Remove all work orders from the cache
		for (int i = 0; i < liteAcctInfo.getServiceRequestHistory().size(); i++) {
			int orderID = ((Integer) liteAcctInfo.getServiceRequestHistory().get(i)).intValue();
			deleteWorkOrderBase( orderID );
		}
		
		// Remove the customer account from cache
		Hashtable custAcctMap = getCustAccountInfoMap();
		synchronized (custAcctMap) {
			custAcctMap.remove( new Integer(liteAcctInfo.getAccountID()) );
		}
	}
	
	public LiteStarsCustAccountInformation reloadCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo) {
		// Remove all addresses from the cache
		deleteAddress( liteAcctInfo.getCustomerAccount().getBillingAddressID() );
		deleteAddress( liteAcctInfo.getAccountSite().getStreetAddressID() );
		
		// Reload the customer account into cache
		Hashtable custAcctMap = getCustAccountInfoMap();
		synchronized (custAcctMap) {
			custAcctMap.remove( new Integer(liteAcctInfo.getAccountID()) );
			return getBriefCustAccountInfo( liteAcctInfo.getAccountID(), true );
		}
	}
	
	/**
	 * Search customer account by account # within the energy company.
	 */
	public LiteStarsCustAccountInformation searchAccountByAccountNo(String accountNo) {
		ArrayList custAcctInfoList = getAllCustAccountInformation();
		for (int i = 0; i < custAcctInfoList.size(); i++) {
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) custAcctInfoList.get(i);
			if (liteAcctInfo.getCustomerAccount().getAccountNumber().equalsIgnoreCase( accountNo ))
			{
				if (!liteAcctInfo.isExtended()) extendCustAccountInfo( liteAcctInfo );
				return liteAcctInfo;
			}
		}
		
		if (!isAccountsLoaded()) {
			try {
				int[] accountIDs = com.cannontech.database.db.stars.customer.CustomerAccount.searchByAccountNumber( accountNo, getLiteID() );
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
				CTILogger.error( e.getMessage(), e );
			}
		}
		
		return null;
	}
	
	/**
	 * Search customer accounts by account #, search results based on partial match.
	 * If searchMembers is true, it returns a list of Pair(LiteStarsCustAccountInformation, LiteStarsEnergyCompany);
	 * otherwise it returns a list of LiteStarsCustAccountInformation.
	 */
	public ArrayList searchAccountByAccountNo(String accountNo, boolean searchMembers) {
		ArrayList accountList = new ArrayList();
		
		if (accountNo.equals("*")) accountNo = "";
		
		if (isAccountsLoaded()) {
			ArrayList custAcctInfoList = getAllCustAccountInformation();
			for (int i = 0; i < custAcctInfoList.size(); i++) {
				LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) custAcctInfoList.get(i);
				if (liteAcctInfo.getCustomerAccount().getAccountNumber().toUpperCase().startsWith( accountNo.toUpperCase() ))
				{
					if (searchMembers)
						accountList.add( new Pair(liteAcctInfo, this) );
					else
						accountList.add( liteAcctInfo );
				}
			}
		}
		else {
			if (accountNo.indexOf('%') < 0) accountNo += "%";
			
			int[] accountIDs = com.cannontech.database.db.stars.customer.CustomerAccount.searchByAccountNumber( accountNo, getLiteID() );
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
					ArrayList memberList = company.searchAccountBySerialNo( serialNo, searchMembers );
					accountList.addAll( memberList );
				}
			}
		}
		
		return accountList;
	}
	
	/**
	 * Search customer account by residence map #, search results based on partial match
	 * If searchMembers is true, the return type is Pair(LiteStarsCustAccountInformation, LiteStarsEnergyCompany);
	 * otherwise the return type is LiteStarsCustAccountInformation.
	 */
	public ArrayList searchAccountByMapNo(String mapNo, boolean searchMembers) {
		ArrayList accountList = new ArrayList();
		
		if (isAccountsLoaded()) {
			ArrayList custAcctInfoList = getAllCustAccountInformation();
			for (int i = 0; i < custAcctInfoList.size(); i++) {
				LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) custAcctInfoList.get(i);
				if (liteAcctInfo.getAccountSite().getSiteNumber().toUpperCase().startsWith( mapNo.toUpperCase() ))
				{
					if (searchMembers)
						accountList.add( new Pair(liteAcctInfo, this) );
					else
						accountList.add( liteAcctInfo );
				}
			}
		}
		else {
			int[] accountIDs = com.cannontech.database.db.stars.customer.CustomerAccount.searchByMapNumber( mapNo, getLiteID() );
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
					ArrayList memberList = company.searchAccountByMapNo( mapNo, searchMembers );
					accountList.addAll( memberList );
				}
			}
		}
		
		return accountList;
	}
	
	/**
	 * Search customer accounts by service address. The search is based on partial match, and is case-insensitive.
	 * If searchMembers is true, it returns a list of Pair(LiteStarsCustAccountInformation, LiteStarsEnergyCompany);
	 * otherwise it returns a list of LiteStarsCustAccountInformation.
	 */
	public ArrayList searchAccountByAddress(String address, boolean searchMembers) {
		ArrayList accountList = new ArrayList();
		
		if (isAccountsLoaded()) {
			ArrayList custAcctInfoList = getAllCustAccountInformation();
			for (int i = 0; i < custAcctInfoList.size(); i++) {
				LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) custAcctInfoList.get(i);
				LiteAddress servAddr = getAddress( liteAcctInfo.getAccountSite().getStreetAddressID() );
				if (servAddr != null && servAddr.getLocationAddress1().toUpperCase().startsWith( address.toUpperCase() ))
				{
					if (searchMembers)
						accountList.add( new Pair(liteAcctInfo, this) );
					else
						accountList.add( liteAcctInfo );
				}
			}
		}
		else {
			try {
				int[] accountIDs = com.cannontech.database.db.stars.customer.CustomerAccount.searchByAddress( address + "%", getLiteID() );
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
			catch (Exception e) {
				CTILogger.error( e.getMessage(), e );
				return null;
			}
		}
		
		if (searchMembers) {
			ArrayList children = getChildren();
			synchronized (children) {
				for (int i = 0; i < children.size(); i++) {
					LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) children.get(i);
					ArrayList memberList = company.searchAccountByAddress( address, searchMembers );
					accountList.addAll( memberList );
				}
			}
		}
		
		return accountList;
	}
	
	public ArrayList searchAccountByOrderNo(String orderNo, boolean searchMembers) {
		ArrayList orderList = searchWorkOrderByOrderNo( orderNo, false );
		ArrayList accountList = new ArrayList();
		
		for (int i = 0; i < orderList.size(); i++) {
			LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) orderList.get(i);
			if (liteOrder.getAccountID() > 0) {
				LiteStarsCustAccountInformation liteAcctInfo = getBriefCustAccountInfo( liteOrder.getAccountID(), true );
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
					ArrayList memberList = company.searchAccountByOrderNo( orderNo, searchMembers );
					accountList.addAll( memberList );
				}
			}
		}
		
		return accountList;
	}
	
	private ArrayList searchAccountByContactIDs(int[] contactIDs, boolean searchMembers) {
		ArrayList accountList = new ArrayList();
		
		if (isAccountsLoaded()) {
			ArrayList custAcctInfoList = getAllCustAccountInformation();
			for (int i = 0; i < custAcctInfoList.size(); i++) {
				LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) custAcctInfoList.get(i);
				for (int j = 0; j < contactIDs.length; j++) {
					if (liteAcctInfo.getCustomer().getPrimaryContactID() == contactIDs[j]) {
						if (searchMembers)
							accountList.add( new Pair(liteAcctInfo, this) );
						else
							accountList.add( liteAcctInfo );
						break;
					}
				}
			}
		}
		else {
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
				phoneNo, new int[] {YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE}, true );
		
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
		LiteContact[] contacts = ContactFuncs.getContactsByLName( lastName, true );
		
		int[] contactIDs = new int[ contacts.length ];
		for (int i = 0; i < contacts.length; i++)
			contactIDs[i] = contacts[i].getContactID();
		
		return searchAccountByContactIDs( contactIDs, searchMembers );
	}
	
	
	/* The following methods are only used when SOAPClient exists locally */
	
	public synchronized StarsEnergyCompanySettings getStarsEnergyCompanySettings(StarsYukonUser user) {
		if (ECUtils.isOperator(user)) {
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
		else if (ECUtils.isResidentialCustomer(user)) {
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
		
		// Currently the consumer side only need chance of control list
		StarsCustSelectionList list = getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL);
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
		
		if (ECUtils.isOperator( starsUser )) {
			if (starsOperSelLists == null) {
				starsOperSelLists = new StarsCustomerSelectionLists();
				updateOperSelectionLists();
			}
			
			return starsOperSelLists;
		}
		else if (ECUtils.isResidentialCustomer( starsUser )) {
			if (starsCustSelLists == null) {
				starsCustSelLists = new StarsCustomerSelectionLists();
				updateCustSelectionLists();
			}
			
			return starsCustSelLists;
		}
		
		return null;
	}
	
	public synchronized void updateStarsEnrollmentPrograms() {
		if (starsEnrPrograms == null) return;
		
		StarsLiteFactory.setStarsEnrollmentPrograms( starsEnrPrograms, getAllApplianceCategories(), this );
	}
	
	public synchronized StarsEnrollmentPrograms getStarsEnrollmentPrograms() {
		if (starsEnrPrograms == null) {
			starsEnrPrograms = new StarsEnrollmentPrograms();
			updateStarsEnrollmentPrograms();
		}
		return starsEnrPrograms;
	}
	
	public synchronized StarsCustomerFAQs getStarsCustomerFAQs() {
		if (starsCustFAQs == null)
			starsCustFAQs = StarsLiteFactory.createStarsCustomerFAQs( getAllCustomerFAQs() );
		return starsCustFAQs;
	}
	
	public synchronized void updateStarsServiceCompanies() {
		if (starsServCompanies == null) return;
		
		starsServCompanies.removeAllStarsServiceCompany();
		
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
	
	public synchronized StarsServiceCompanies getStarsServiceCompanies() {
		if (starsServCompanies == null) {
			starsServCompanies = new StarsServiceCompanies();
			updateStarsServiceCompanies();
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
		
		YukonSelectionList devTypeList = getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE);
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

	private Hashtable getStarsCustAcctInfos() {
		if (starsCustAcctInfos == null)
			starsCustAcctInfos = new Hashtable();
		return starsCustAcctInfos;
	}
	
	public StarsCustAccountInformation getStarsCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo) {
		Hashtable starsCustAcctInfos = getStarsCustAcctInfos();
		synchronized (starsCustAcctInfos) {
			Integer accountID = new Integer(liteAcctInfo.getAccountID());
			StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation) starsCustAcctInfos.get( accountID );
			if (starsAcctInfo == null) {
				starsAcctInfo = StarsLiteFactory.createStarsCustAccountInformation( liteAcctInfo, this, true );
				starsAcctInfo.setLastActiveTime( new Date() );
				starsCustAcctInfos.put( accountID, starsAcctInfo );
			}
			
			return starsAcctInfo;
		}
	}
	
	public StarsCustAccountInformation getStarsCustAccountInformation(int accountID, boolean autoLoad) {
		Hashtable starsCustAcctInfos = getStarsCustAcctInfos();
		synchronized (starsCustAcctInfos) {
			StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
					starsCustAcctInfos.get( new Integer(accountID) );
			if (starsAcctInfo != null) return starsAcctInfo;
		}
		
		if (autoLoad) {
			LiteStarsCustAccountInformation liteAcctInfo = getCustAccountInformation( accountID, true );
			if (liteAcctInfo != null)
				return getStarsCustAccountInformation( liteAcctInfo );
		}
		
		return null;
	}
	
	public StarsCustAccountInformation getStarsCustAccountInformation(int accountID) {
		return getStarsCustAccountInformation( accountID, false );
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
		}
	}
	
	public ArrayList getActiveAccounts() {
		return new ArrayList( getStarsCustAcctInfos().values() );
	}
	
	public synchronized Hashtable getContactAccountIDMap() {
		if (contactAccountIDMap == null)
			contactAccountIDMap = new Hashtable();
		
		return contactAccountIDMap;
	}
	
	/**
	 * Register the StarsCustAccountInformation object as "active"
	 * If the return value is false, it means the StarsCustAccountInformation object
	 * is out of date, user should store a new object in the session by calling
	 * getStarsCustAccountInformation(accountID, true)
	 */
	public boolean registerActiveAccount(StarsCustAccountInformation starsAcctInfo) {
		Hashtable starsCustAcctInfos = getStarsCustAcctInfos();
		synchronized (starsCustAcctInfos) {
			Integer accountID = new Integer( starsAcctInfo.getStarsCustomerAccount().getAccountID() );
			StarsCustAccountInformation storedAcctInfo = (StarsCustAccountInformation) starsCustAcctInfos.get( accountID );
			if (storedAcctInfo == null || !storedAcctInfo.equals( starsAcctInfo ))
				return false;
		}
		
		starsAcctInfo.setLastActiveTime( new Date() );
		
		// Add contact ID to account ID mapping into the table
		Integer accountID = new Integer( starsAcctInfo.getStarsCustomerAccount().getAccountID() );
		Hashtable contAcctIDMap = getContactAccountIDMap();
		synchronized (contAcctIDMap) {
			contAcctIDMap.put( new Integer(starsAcctInfo.getStarsCustomerAccount().getPrimaryContact().getContactID()), accountID );
			
			for (int i = 0; i < starsAcctInfo.getStarsCustomerAccount().getAdditionalContactCount(); i++)
				contAcctIDMap.put( new Integer(starsAcctInfo.getStarsCustomerAccount().getAdditionalContact(i).getContactID()), accountID );
		}
		
		return true;
	}
	
	public void clearActiveAccounts() {
		starsCustAcctInfos = null;
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
	
	private void loadEnergyCompanyHierarchy() {
		parent = null;
		children = new ArrayList();
		memberLoginIDs = new ArrayList();
		
		String sql = "SELECT EnergyCompanyID, ItemID FROM ECToGenericMapping " +
				"WHERE MappingCategory='" + ECToGenericMapping.MAPPING_CATEGORY_MEMBER + "' " +
				"AND (EnergyCompanyID=" + getEnergyCompanyID() + " OR ItemID=" + getEnergyCompanyID() + ")";
		
		try {
			SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
			stmt.execute();
			
			for (int i = 0; i < stmt.getRowCount(); i++) {
				int parentID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
				int childID = ((java.math.BigDecimal) stmt.getRow(i)[1]).intValue();
				
				if (parentID == getLiteID())
					children.add( StarsDatabaseCache.getInstance().getEnergyCompany(childID) );
				else	// childID == getLiteID()
					parent = StarsDatabaseCache.getInstance().getEnergyCompany( parentID );
			}
			
			ECToGenericMapping[] items = ECToGenericMapping.getAllMappingItems(
					getEnergyCompanyID(), ECToGenericMapping.MAPPING_CATEGORY_MEMBER_LOGIN );
			
			if (items != null) {
				for (int i = 0; i < items.length; i++)
					memberLoginIDs.add( items[i].getItemID() );
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
		if (!hierarchyLoaded)
			loadEnergyCompanyHierarchy();
		return memberLoginIDs;
	}
	
	public synchronized void clearHierarchy() {
		hierarchyLoaded = false;
	}

}
