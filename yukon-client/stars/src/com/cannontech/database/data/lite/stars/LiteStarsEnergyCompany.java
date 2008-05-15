package com.cannontech.database.data.lite.stars;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.stars.hardware.LMThermostatSchedule;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.customer.Customer;
import com.cannontech.database.db.macro.MacroTypes;
import com.cannontech.database.db.stars.ECToGenericMapping;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.database.db.stars.hardware.MeterHardwareBase;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.ECSearchDao;
import com.cannontech.stars.core.dao.StarsRowCountDao;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.OptOutEventQueue;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.StarsUtils;
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
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestion;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestions;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsServiceCompanies;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.stars.xml.serialize.StarsSubstation;
import com.cannontech.stars.xml.serialize.StarsSubstations;
import com.cannontech.stars.xml.serialize.StarsThermostatProgram;
import com.cannontech.stars.xml.serialize.StarsThermostatSettings;
import com.cannontech.yukon.IDatabaseCache;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteStarsEnergyCompany extends LiteBase {
    
    public static final int FAKE_LIST_ID = -9999;   // Magic number for YukonSelectionList ID, used for substation and service company list
    public static final int INVALID_ROUTE_ID = -1;  // Mark that a valid default route id is not found, and prevent futher attempts
    private EnergyCompanyLatch energyCompanyLatch = new EnergyCompanyLatch();
    
    private static final String[] OPERATOR_SELECTION_LISTS = {
        YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE,
        YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL,
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
        YukonSelectionListDefs.YUK_LIST_NAME_RATE_SCHEDULE,
        YukonSelectionListDefs.YUK_LIST_NAME_SETTLEMENT_TYPE,
        YukonSelectionListDefs.YUK_LIST_NAME_CI_CUST_TYPE
    };
    
    public EnergyCompanyLatch getEnergyCompanyLatch(){
        return energyCompanyLatch;
    }
    
    private String name = null;
    private int primaryContactID = CtiUtilities.NONE_ZERO_ID;
    private int userID = com.cannontech.user.UserUtils.USER_DEFAULT_ID;
    
    private Map<Integer,LiteStarsCustAccountInformation> custAccountInfos = null;
    private Map<Integer,LiteInventoryBase> inventory = null;
    private Map<Integer,LiteWorkOrderBase> workOrders = null;
    
    private List<LiteLMProgramWebPublishing> pubPrograms = null;
    private List<LiteApplianceCategory> appCategories = null;
    private List<LiteServiceCompany> serviceCompanies = null;
    private List<LiteSubstation> substations = null;
    private List<YukonSelectionList> selectionLists = null;
    private List<LiteInterviewQuestion> interviewQuestions = null;
    
    private List<Integer> operatorLoginIDs = null;
    private List<Integer> routeIDs = null;
    
    private Map<Integer,LiteLMThermostatSchedule> dftThermSchedules = null;
    
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
    
    private int dftRouteID = CtiUtilities.NONE_ZERO_ID;
    private int operDftGroupID = com.cannontech.database.db.user.YukonGroup.EDITABLE_MIN_GROUP_ID - 1;
    
    private Map<Integer,Integer> contactAccountIDMap = null;
    
    // Cached XML messages
    private StarsEnergyCompany starsEnergyCompany = null;
    private StarsEnrollmentPrograms starsEnrPrograms = null;
    private StarsCustomerFAQs starsCustFAQs = null;
    private StarsServiceCompanies starsServCompanies = null;
    private StarsSubstations starsSubstations = null;
    private StarsExitInterviewQuestions starsExitQuestions = null;
    private StarsDefaultThermostatSchedules starsDftThermSchedules = null;
    private StarsEnergyCompanySettings starsOperECSettings = null;
    private StarsEnergyCompanySettings starsCustECSettings = null;
    private StarsCustomerSelectionLists starsOperSelLists = null;
    private StarsCustomerSelectionLists starsCustSelLists = null;
    
    private Map<String,StarsCustSelectionList> starsSelectionLists = null;
    private Map<Integer,StarsCustAccountInformation> starsCustAcctInfos = null;
    
    // Energy company hierarchy
    private LiteStarsEnergyCompany parent = null;
    private List<LiteStarsEnergyCompany> children = null;
    private List<Integer> memberLoginIDs = null;

    private static final AddressDao addressDao = YukonSpringHook.getBean("addressDao", AddressDao.class);
    private static final ECSearchDao ecSearchDao = YukonSpringHook.getBean("ecSearchDao", ECSearchDao.class);
    private static final StarsRowCountDao starsRowCountDao = YukonSpringHook.getBean("starsRowCountDao", StarsRowCountDao.class);
 
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
        
        if (dftRouteID == CtiUtilities.NONE_ZERO_ID) {
            String dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();
            
            PaoPermissionService pService = (PaoPermissionService) YukonSpringHook.getBean("paoPermissionService");
            Set<Integer> permittedPaoIDs = pService.getPaoIdsForUserPermission(new LiteYukonUser(getUserID()), Permission.DEFAULT_ROUTE);
            if(! permittedPaoIDs.isEmpty()) {
                String sql = "SELECT exc.LMGroupID, macro.OwnerID FROM LMGroupExpressCom exc, GenericMacro macro " +
                    "WHERE macro.MacroType = '" + MacroTypes.GROUP + "' AND macro.ChildID = exc.LMGroupID AND exc.SerialNumber = '0'";
                sql += " AND macro.OwnerID in (";
                Integer[] permittedIDs = new Integer[permittedPaoIDs.size()];
                permittedIDs = permittedPaoIDs.toArray(permittedIDs);
                for(Integer paoID : permittedIDs) {
                    sql += paoID.toString() + ",";
                }
                sql = sql.substring(0, sql.length() - 1);
                sql += ")";
                
                Object[][] serialGroupIDs = com.cannontech.util.ServletUtil.executeSQL(
                        dbAlias, sql, new Class[] { Integer.class, Integer.class } );
                
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
    
    private synchronized void startLoadAccountsTask() {
        if (!isAccountsLoaded() && loadAccountsTaskID == 0 && !ECUtils.isDefaultEnergyCompany( this )) {
            loadAccountsTaskID = ProgressChecker.addTask( new LoadCustAccountsTask(this) );
            CTILogger.info( "*** Start loading customer accounts for energy company #" + getEnergyCompanyID() );
        }
    }
    
    private synchronized boolean isLoadAccountsTaskRunning() {
        TimeConsumingTask task = ProgressChecker.getTask( loadAccountsTaskID );
        if (task == null) return false;
        
        if (task.getStatus() == TimeConsumingTask.STATUS_FINISHED
            || task.getStatus() == TimeConsumingTask.STATUS_ERROR
            || task.getStatus() == TimeConsumingTask.STATUS_CANCELED)
        {
            ProgressChecker.removeTask( loadAccountsTaskID );
            loadAccountsTaskID = 0;
            return false;
        }
        
        return true;
    }
    
    public List<LiteStarsCustAccountInformation> loadAllCustomerAccounts(boolean blockOnWait) {
        if (isAccountsLoaded()) return getAllCustAccountInformation();
        startLoadAccountsTask();
        
        if (!blockOnWait) return null;

        while (true) {
            if (isLoadAccountsTaskRunning()) {
                try {
                    Thread.sleep( 1000 );
                }
                catch (InterruptedException e) {}
            }
            else {
                if (isAccountsLoaded())
                    return getAllCustAccountInformation();

                return null;
            }
        }
    }
    
    private synchronized void startLoadInventoryTask() {
        if (loadInvTaskID == 0 && !ECUtils.isDefaultEnergyCompany( this )) {
            loadInvTaskID = ProgressChecker.addTask( new LoadInventoryTask(this) );
            CTILogger.info( "*** Start loading inventory for energy company #" + getEnergyCompanyID() );
        }
    }
    
    
    
    private synchronized boolean isLoadInventoryTaskRunning() {
        TimeConsumingTask task = ProgressChecker.getTask( loadInvTaskID );
        if (task == null) return false;
        
        if (task.getStatus() == TimeConsumingTask.STATUS_FINISHED
            || task.getStatus() == TimeConsumingTask.STATUS_ERROR
            || task.getStatus() == TimeConsumingTask.STATUS_CANCELED)
        {
            ProgressChecker.removeTask( loadInvTaskID );
            loadInvTaskID = 0;
            return false;
        }
        
        return true;
    }
    
    public List<LiteInventoryBase> loadAllInventory(boolean blockOnWait) {
        if (isInventoryLoaded()) return getAllInventory();
        startLoadInventoryTask();
        
        if (!blockOnWait) return null;
        
        while (true) {
            if (isLoadInventoryTaskRunning()) {
                try {
                    Thread.sleep( 1000 );
                }
                catch (InterruptedException e) {}
            }
            else {
                if (isInventoryLoaded())
                    return getAllInventory();

                return null;
            }
        }
    }
    
    private synchronized void startLoadWorkOrdersTask() {
        if (!isWorkOrdersLoaded() && loadOrdersTaskID == 0 && !ECUtils.isDefaultEnergyCompany( this )) {
            loadOrdersTaskID = ProgressChecker.addTask( new LoadWorkOrdersTask(this) );
            CTILogger.info( "*** Start loading work orders for energy company #" + getEnergyCompanyID() );
        }
    }
    
    private synchronized boolean isLoadWorkOrdersTaskRunning() {
        TimeConsumingTask task = ProgressChecker.getTask( loadOrdersTaskID );
        if (task == null) return false;
        
        if (task.getStatus() == TimeConsumingTask.STATUS_FINISHED
            || task.getStatus() == TimeConsumingTask.STATUS_ERROR
            || task.getStatus() == TimeConsumingTask.STATUS_CANCELED)
        {
            ProgressChecker.removeTask( loadOrdersTaskID );
            loadOrdersTaskID = 0;
            return false;
        }
        
        return true;
    }
    
    public List<LiteWorkOrderBase> loadAllWorkOrders(boolean blockOnWait) {
        if (isWorkOrdersLoaded()) return getAllWorkOrders();
        startLoadWorkOrdersTask();
        
        if (!blockOnWait) return null;
        
        while (true) {
            if (isLoadWorkOrdersTaskRunning()) {
                try {
                    Thread.sleep( 1000 );
                }
                catch (InterruptedException e) {}
            }
            else {
                if (isWorkOrdersLoaded())
                    return getAllWorkOrders();
                
                return null;
            }
        }
    }
    
    public void init() {
        if (!isInventoryLoaded())
            loadAllInventory( false );
        if (!isWorkOrdersLoaded())
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
        pubPrograms = null;
        inventory = null;
        appCategories = null;
        workOrders = null;
        serviceCompanies = null;
        substations = null;
        selectionLists = null;
        interviewQuestions = null;
        
        operatorLoginIDs = null;
        routeIDs = null;
        dftThermSchedules = null;
        
        nextCallNo = 0;
        nextOrderNo = 0;
        hierarchyLoaded = false;
        
        dftRouteID = CtiUtilities.NONE_ZERO_ID;
        operDftGroupID = com.cannontech.database.db.user.YukonGroup.EDITABLE_MIN_GROUP_ID - 1;
        
        contactAccountIDMap = null;
        
        starsEnergyCompany = null;
        starsEnrPrograms = null;
        starsCustFAQs = null;
        starsServCompanies = null;
        starsSubstations = null;
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
    
    public void clearInventory() {
        energyCompanyLatch.resetInventoryLatch();
                
        // If the inventory loading task is alive, cancel it first
        TimeConsumingTask loadInvTask = ProgressChecker.getTask( loadInvTaskID );
        if (loadInvTask != null) loadInvTask.cancel();
        
        // Wait up to 3 seconds for it to stop
        for (int i = 0; i < 6; i++) {
                if ((loadInvTask == null
                    || loadInvTask.getStatus() == LoadInventoryTask.STATUS_FINISHED
                    || loadInvTask.getStatus() == LoadInventoryTask.STATUS_CANCELED
                    || loadInvTask.getStatus() == LoadInventoryTask.STATUS_ERROR))
                break;
            
            try {
                Thread.sleep( 500 );
            }
            catch (InterruptedException e) {}
        }
        
        if (loadInvTaskID > 0) {
            ProgressChecker.removeTask( loadInvTaskID );
            loadInvTaskID = 0;
        }
        
        inventoryLoaded = false;
        inventory = null;
    }
    
    public String getEnergyCompanySetting(int rolePropertyID) {
        String value = DaoFactory.getAuthDao().getRolePropertyValue( DaoFactory.getYukonUserDao().getLiteYukonUser(getUserID()), rolePropertyID );
        if (value != null && value.equalsIgnoreCase(CtiUtilities.STRING_NONE))
            value = "";
        return value;
    }
    
    public LiteYukonGroup[] getResidentialCustomerGroups() {
        String[] custGroupIDs = getEnergyCompanySetting( EnergyCompanyRole.CUSTOMER_GROUP_IDS ).split(",");
        List<LiteYukonGroup> custGroupList = new ArrayList<LiteYukonGroup>();
        
        for (int i = 0; i < custGroupIDs.length; i++) {
            String groupID = custGroupIDs[i].trim();
            if (groupID.equals("")) continue;
            LiteYukonGroup liteGroup = DaoFactory.getAuthDao().getGroup( Integer.parseInt(groupID) );
            if (liteGroup != null) custGroupList.add( liteGroup );
        }
        
        LiteYukonGroup[] custGroups = new LiteYukonGroup[ custGroupList.size() ];
        custGroupList.toArray( custGroups );
        return custGroups;
    }
    
    public LiteYukonGroup[] getWebClientOperatorGroups() {
        String[] operGroupIDs = getEnergyCompanySetting( EnergyCompanyRole.OPERATOR_GROUP_IDS ).split(",");
        List<LiteYukonGroup> operGroupList = new ArrayList<LiteYukonGroup>();
        
        for (int i = 0; i < operGroupIDs.length; i++) {
            String groupID = operGroupIDs[i].trim();
            if (groupID.equals("")) continue;
            LiteYukonGroup liteGroup = DaoFactory.getAuthDao().getGroup( Integer.parseInt(groupID) );
            if (liteGroup != null) operGroupList.add( liteGroup );
        }
        
        LiteYukonGroup[] operGroups = new LiteYukonGroup[ operGroupList.size() ];
        operGroupList.toArray( operGroups );
        return operGroups;
    }
    
    public LiteYukonGroup getOperatorAdminGroup() {
        if (operDftGroupID < com.cannontech.database.db.user.YukonGroup.EDITABLE_MIN_GROUP_ID) {
            LiteYukonUser dftUser = DaoFactory.getYukonUserDao().getLiteYukonUser( getUserID() );
            IDatabaseCache cache = DefaultDatabaseCache.getInstance();
            
            synchronized (cache) {
                List<LiteYukonGroup> groups = cache.getYukonUserGroupMap().get( dftUser );
                for (int i = 0; i < groups.size(); i++) {
                    LiteYukonGroup group = groups.get(i);
                    if (DaoFactory.getAuthDao().getRolePropValueGroup(group, AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY, null) != null) {
                        operDftGroupID = group.getGroupID();
                        return group;
                    }
                }
            }
        }
        
        return DaoFactory.getAuthDao().getGroup( operDftGroupID );
    }
    
    /**
     * Get programs published by this energy company only
     */
    public synchronized List<LiteLMProgramWebPublishing> getPrograms() {
        if (pubPrograms == null)
            getApplianceCategories();
        return pubPrograms;
    }
    
    /**
     * Get all published programs including those inherited from the parent company
     */
    public synchronized List<LiteLMProgramWebPublishing> getAllPrograms() {
        List<LiteLMProgramWebPublishing> pubProgs = new ArrayList<LiteLMProgramWebPublishing>( getPrograms() );
        if (getParent() != null)
            pubProgs.addAll( 0, getParent().getPrograms() );
        
        return pubProgs;
    }
    
    /**
     * Get appliance categories created in this energy company only
     */
    public synchronized List<LiteApplianceCategory> getApplianceCategories() {
        if (appCategories == null) {
            appCategories = new ArrayList<LiteApplianceCategory>();
            pubPrograms = new ArrayList<LiteLMProgramWebPublishing>();
            
            com.cannontech.database.db.stars.appliance.ApplianceCategory[] appCats =
                    com.cannontech.database.db.stars.appliance.ApplianceCategory.getAllApplianceCategories( getEnergyCompanyID() );
                    
            for (int i = 0; i < appCats.length; i++) {
                LiteApplianceCategory appCat = (LiteApplianceCategory) StarsLiteFactory.createLite( appCats[i] );
                
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
     * Get all appliance categories including those inherited from the parent company if allowed to do so
     */
    public synchronized List<LiteApplianceCategory> getAllApplianceCategories() {
        List<LiteApplianceCategory> appCats = new ArrayList<LiteApplianceCategory>( getApplianceCategories() );
        boolean inheritCats = getEnergyCompanySetting( EnergyCompanyRole.INHERIT_PARENT_APP_CATS ).equalsIgnoreCase("true");
        if (getParent() != null && inheritCats)
            appCats.addAll( 0, getParent().getAllApplianceCategories() );
        
        return appCats;
    }

    public synchronized List<YukonSelectionList> getAllSelectionLists() {
        if (selectionLists == null) {
            selectionLists = new ArrayList<YukonSelectionList>();
            
            ECToGenericMapping[] items = ECToGenericMapping.getAllMappingItems( getEnergyCompanyID(), YukonSelectionList.TABLE_NAME );
            if (items != null) {
                for (int i = 0; i < items.length; i++)
                    selectionLists.add( DaoFactory.getYukonListDao().getYukonSelectionList(items[i].getItemID().intValue()) );
            }
        }
        
        return selectionLists;
    }
    
    public YukonSelectionList getYukonSelectionList(String listName, boolean useInherited, boolean useDefault) {
        List<YukonSelectionList> selectionLists = getAllSelectionLists();
        synchronized (selectionLists) {
            for (final YukonSelectionList list : selectionLists) {
                if (list.getListName().equalsIgnoreCase(listName))
                    return list;
            }
        }
        
        // If parent company exists, then search the parenet company for the list
        if (getParent() != null && useInherited)
            return getParent().getYukonSelectionList(listName, useInherited, useDefault);
        
        if (useDefault && !ECUtils.isDefaultEnergyCompany( this )) {
            YukonSelectionList dftList = StarsDatabaseCache.getInstance().getDefaultEnergyCompany().getYukonSelectionList( listName, false, false );
            if (dftList != null) {
                // If the list is user updatable, returns a copy of the default list; otherwise returns the default list itself
                if (dftList.getUserUpdateAvailable().equalsIgnoreCase("Y"))
                    return addYukonSelectionList( listName, dftList, true );

                return dftList;
            }
        }
        
        return null;
    }
    
    public YukonSelectionList getYukonSelectionList(String listName) {
        return getYukonSelectionList(listName, true, true);
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
            
            DaoFactory.getYukonListDao().getYukonSelectionLists().put( listDB.getListID(), cList );
            getAllSelectionLists().add( cList );
            
            if (populateDefault) {
                for (int i = 0; i < dftList.getYukonListEntries().size(); i++) {
                    YukonListEntry dftEntry = dftList.getYukonListEntries().get(i);
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
                    
                    DaoFactory.getYukonListDao().getYukonListEntries().put( entry.getEntryID(), cEntry );
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
    
    public void deleteYukonSelectionList(YukonSelectionList list) {
        getAllSelectionLists().remove( list );
        
        Map<Integer,YukonListEntry> entries = DaoFactory.getYukonListDao().getYukonListEntries();
        synchronized (entries) {
            for (int i = 0; i < list.getYukonListEntries().size(); i++) {
                YukonListEntry entry = list.getYukonListEntries().get(i);
                entries.remove( new Integer(entry.getEntryID()) );
            }
        }
        
        DaoFactory.getYukonListDao().getYukonSelectionLists().remove( new Integer(list.getListID()) );
    }
    
    public YukonListEntry getYukonListEntry(String listName, int yukonDefID) {
        YukonSelectionList list = getYukonSelectionList( listName );
        for (int i = 0; i < list.getYukonListEntries().size(); i++) {
            YukonListEntry entry = list.getYukonListEntries().get(i);
            if (entry.getYukonDefID() == yukonDefID)
                return entry;
        }
        
        return new YukonListEntry();
    }
    
    public YukonListEntry getYukonListEntry(int yukonDefID) {
        String listName = DaoFactory.getYukonListDao().getYukonListName( yukonDefID );
        if (listName == null) return null;
        
        return getYukonListEntry(listName, yukonDefID);
    }
    
    public synchronized List<LiteServiceCompany> getServiceCompanies() {
        if (serviceCompanies == null) {
            com.cannontech.database.data.stars.report.ServiceCompany[] companies =
                    com.cannontech.database.data.stars.report.ServiceCompany.retrieveAllServiceCompanies( getEnergyCompanyID() );
            
            serviceCompanies = new ArrayList<LiteServiceCompany>();
            for (int i = 0; i < companies.length; i++)
                serviceCompanies.add( (LiteServiceCompany)StarsLiteFactory.createLite(companies[i]) );
            
            CTILogger.info( "All service companies loaded for energy company #" + getEnergyCompanyID() );
        }
        
        return serviceCompanies;
    }
    
    public List<LiteServiceCompany> getAllServiceCompanies() {
        List<LiteServiceCompany> companies = new ArrayList<LiteServiceCompany>( getServiceCompanies() );
        if (getParent() != null)
            companies.addAll( 0, getParent().getAllServiceCompanies() );
        
        return companies;
    }
    
    public List<LiteServiceCompany> getAllServiceCompaniesDownward() 
    {
        List<LiteServiceCompany> companies = new ArrayList<LiteServiceCompany>( getAllServiceCompanies() );
        
        if(getChildren() != null)
        {
            for(int j = 0; j < getChildren().size(); j++)
            {
                companies.addAll( 0, getChildren().get(j).getServiceCompanies());
            }
        }
        
        return companies;
    }
    
    public List<Integer> getAllEnergyCompaniesDownward()
    {
        List<Integer> allEnergyCompanies = new ArrayList<Integer>();
        allEnergyCompanies.add(new Integer(getLiteID()));
        List<LiteStarsEnergyCompany> children = getChildren();
        synchronized (children) {
            for (final LiteStarsEnergyCompany company : children) {
                List<Integer> memberList = company.getAllEnergyCompaniesDownward();
                allEnergyCompanies.addAll( memberList );
            }
        }
        return allEnergyCompanies;
    }

    public List<Warehouse> getAllWarehousesDownward() 
    {
        List<Warehouse> warehouses = Warehouse.getAllWarehousesForEnergyCompany(getEnergyCompanyID().intValue());
        
        if(getChildren() != null)
        {
            for(int j = 0; j < getChildren().size(); j++)
            {
                warehouses.addAll( 0, Warehouse.getAllWarehousesForEnergyCompany((getChildren().get(j)).getLiteID()));
            }
        }
        
        return warehouses;
    }
    
    public synchronized List<LiteSubstation> getSubstations() {
        if (substations == null) {
            com.cannontech.database.db.stars.Substation[] subs =
                    com.cannontech.database.db.stars.Substation.getAllSubstations( getEnergyCompanyID() );
            
            substations = new ArrayList<LiteSubstation>();
            for (int i = 0; i < subs.length; i++) {
                LiteSubstation liteSub = (LiteSubstation) StarsLiteFactory.createLite(subs[i]);
                substations.add( liteSub );
            }
            
            CTILogger.info( "All substations loaded for energy company #" + getEnergyCompanyID() );
        }
        
        return substations;
    }
    
    public List<LiteSubstation> getAllSubstations() {
        List<LiteSubstation> substations = new ArrayList<LiteSubstation>(getSubstations() );
        if (getParent() != null)
            substations.addAll( 0, getParent().getAllSubstations() );
        
        return substations;
    }
    
    public synchronized List<Integer> getOperatorLoginIDs() {
        if (operatorLoginIDs == null) {
            operatorLoginIDs = new ArrayList<Integer>();
            
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
    
    @SuppressWarnings("unchecked")
    public LiteYukonPAObject[] getRoutes(LiteYukonPAObject[] inheritedRoutes) {
        List<LiteYukonPAObject> routeList = new ArrayList<LiteYukonPAObject>();
        List<Integer> routeIDs = getRouteIDs();
        
        synchronized (routeIDs) {
            Iterator it = routeIDs.iterator();
            while (it.hasNext()) {
                Integer routeID = (Integer) it.next();
                LiteYukonPAObject liteRoute = DaoFactory.getPaoDao().getLiteYukonPAO( routeID.intValue() );
                
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
        if (ECUtils.isSingleEnergyCompany(this)) {
            return DaoFactory.getPaoDao().getAllLiteRoutes();
        }
        List<LiteYukonPAObject> routeList = new ArrayList<LiteYukonPAObject>();

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
    
    public synchronized List<Integer> getRouteIDs() {
        if (routeIDs == null) {
            routeIDs = new ArrayList<Integer>();
            
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
                dftThermSchedules = new Hashtable<Integer,LiteLMThermostatSchedule>();
                ECToGenericMapping[] items = ECToGenericMapping.getAllMappingItems(
                        getEnergyCompanyID(), com.cannontech.database.db.stars.hardware.LMThermostatSchedule.TABLE_NAME );
                
                for (int i = 0; i < items.length; i++) {
                    LMThermostatSchedule schedule = new LMThermostatSchedule();
                    schedule.setScheduleID( items[i].getItemID() );
                    schedule = (LMThermostatSchedule) Transaction.createTransaction( Transaction.RETRIEVE, schedule ).execute();
                    
                    LiteLMThermostatSchedule liteSchedule = StarsLiteFactory.createLiteLMThermostatSchedule( schedule );
                    
                    int defID = DaoFactory.getYukonListDao().getYukonListEntry( liteSchedule.getThermostatTypeID() ).getYukonDefID();
                    dftThermSchedules.put( new Integer(defID), liteSchedule );
                }
            }
            
            LiteLMThermostatSchedule dftThermSchedule = dftThermSchedules.get( new Integer(hwTypeDefID) );
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
    
    public synchronized List<LiteInterviewQuestion> getAllInterviewQuestions() {
        if (interviewQuestions == null) {
            interviewQuestions = new ArrayList<LiteInterviewQuestion>();
            
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
    
    public List<LiteAddress> getAllAddresses() {
        List<LiteAddress> addressList = addressDao.getAll();
        return addressList;
    }
    
    private synchronized Map<Integer,LiteInventoryBase> getInventoryMap() {
        if (inventory == null) {
            int count = starsRowCountDao.getInventoryRowCount(getEnergyCompanyID());
            int initialCap = (int) (count / 0.75f);
            inventory = new Hashtable<Integer,LiteInventoryBase>(initialCap);
        }
        return inventory;
    }
    
    public List<LiteInventoryBase> getAllInventory() {
        Collection<LiteInventoryBase> values = getInventoryMap().values();
        return new ArrayList<LiteInventoryBase>(values);
    }
    
    private synchronized Map<Integer,LiteWorkOrderBase> getWorkOrderMap() {
        if (workOrders == null) {
            int count = starsRowCountDao.getWorkOrdersRowCount(getEnergyCompanyID());
            int initialCap = (int) (count / 0.75f);
            workOrders = new Hashtable<Integer,LiteWorkOrderBase>(initialCap);
        }
        return workOrders;
    }
    
    public List<LiteWorkOrderBase> getAllWorkOrders() {
        Collection<LiteWorkOrderBase> values = getWorkOrderMap().values();
        return new ArrayList<LiteWorkOrderBase>(values);
    }
    
    private synchronized Map<Integer,LiteStarsCustAccountInformation> getCustAccountInfoMap() {
        if (custAccountInfos == null) {
            int count = starsRowCountDao.getCustAccountsRowCount(getEnergyCompanyID());
            int initialCap = (int) (count / 0.75f);
            custAccountInfos = new Hashtable<Integer,LiteStarsCustAccountInformation>(initialCap);
        }
        return custAccountInfos;
    }
    
    public List<LiteStarsCustAccountInformation> getAllCustAccountInformation() {
        Collection<LiteStarsCustAccountInformation> values = getCustAccountInfoMap().values(); 
        return new ArrayList<LiteStarsCustAccountInformation>(values);
    }
    
    
    public LiteInterviewQuestion[] getInterviewQuestions(int qType) {
        List<LiteInterviewQuestion> qList = new ArrayList<LiteInterviewQuestion>();
        List<LiteInterviewQuestion> questions = getAllInterviewQuestions();
        
        synchronized (questions) {
            for (final LiteInterviewQuestion liteQ : questions) {
                if (liteQ.getQuestionType() == qType) qList.add( liteQ );
            }
        }
        
        LiteInterviewQuestion[] qs = new LiteInterviewQuestion[ qList.size() ];
        qList.toArray( qs );
        
        return qs;
    }

    
    public LiteApplianceCategory getApplianceCategory(int applianceCategoryID) {
        List<LiteApplianceCategory> appCats = getAllApplianceCategories();
        for (final LiteApplianceCategory appCat : appCats) {
            if (appCat.getApplianceCategoryID() == applianceCategoryID) return appCat;
        }
        return null;
    }
    
    public void addApplianceCategory(LiteApplianceCategory appCat) {
        List<LiteApplianceCategory> appCats = getApplianceCategories();
        synchronized (appCats) { appCats.add( appCat ); }
    }
    
    public LiteApplianceCategory deleteApplianceCategory(int applianceCategoryID) {
        List<LiteApplianceCategory> appCats = getApplianceCategories();
        List<LiteLMProgramWebPublishing> programs = getPrograms();
        
        synchronized (appCats) {
            synchronized (programs) {
                for (int i = 0; i < appCats.size(); i++) {
                    LiteApplianceCategory appCat = appCats.get(i);
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
        List<LiteServiceCompany> serviceCompanies = getAllServiceCompanies();
        for (int i = 0; i < serviceCompanies.size(); i++) {
            LiteServiceCompany serviceCompany = serviceCompanies.get(i);
            if (serviceCompany.getCompanyID() == serviceCompanyID)
                return serviceCompany;
        }
        
        return null;
    }
    
    public void addServiceCompany(LiteServiceCompany serviceCompany) {
        List<LiteServiceCompany> serviceCompanies = getServiceCompanies();
        synchronized (serviceCompanies) { serviceCompanies.add(serviceCompany); }
    }
    
    public LiteServiceCompany deleteServiceCompany(int serviceCompanyID) {
        List<LiteServiceCompany> serviceCompanies = getServiceCompanies();
        synchronized (serviceCompanies) {
            for (final LiteServiceCompany serviceCompany : serviceCompanies) {
                if (serviceCompany.getCompanyID() == serviceCompanyID) {
                    serviceCompanies.remove(serviceCompany);
                    return serviceCompany;
                }
            }
        }
        
        return null;
    }
    
    public LiteSubstation getSubstation(int substationID) {
        List<LiteSubstation> substations = getAllSubstations();
        for (final LiteSubstation liteSub : substations) {
            if (liteSub.getSubstationID() == substationID) return liteSub;
        }
        
        return null;
    }
    
    public void addSubstation(LiteSubstation substation) {
        List<LiteSubstation> substations = getSubstations();
        synchronized (substations) { substations.add(substation); }
    }
    
    public LiteSubstation deleteSubstation(int substationID) {
        List<LiteSubstation> substations = getSubstations();
        synchronized (substations) {
            for (final LiteSubstation liteSub : substations) {
                if (liteSub.getSubstationID() == substationID) {
                    substations.remove(liteSub);
                    return liteSub;
                }
            }
        }
        
        return null;
    }
    
    public LiteAddress getAddress(int addressID) {
        LiteAddress address = addressDao.getByAddressId(addressID); 
        return address;
    }
    
    public LiteLMProgramWebPublishing getProgram(int programID) {
        List<LiteLMProgramWebPublishing> programs = getAllPrograms();
        for (final LiteLMProgramWebPublishing liteProg : programs) {
            if (liteProg.getProgramID() == programID) return liteProg;
        }
        return null;
    }
    
    public void addProgram(LiteLMProgramWebPublishing liteProg, LiteApplianceCategory liteAppCat) {
        List<LiteLMProgramWebPublishing> programs = getPrograms();
        synchronized (programs) { programs.add(liteProg); }
        
        liteAppCat.getPublishedPrograms().add( liteProg );
    }
    
    public LiteLMProgramWebPublishing deleteProgram(int programID) {
        List<LiteLMProgramWebPublishing> programs = getPrograms();
        List<LiteApplianceCategory> appCats = getApplianceCategories();
        
        synchronized (programs) {
            synchronized (appCats) {
                for (int i = 0; i < programs.size(); i++) {
                    LiteLMProgramWebPublishing liteProg = programs.get(i);
                    if (liteProg.getProgramID() == programID) {
                        programs.remove(i);
                        
                        for (int j = 0; j < appCats.size(); j++) {
                            LiteApplianceCategory liteAppCat = appCats.get(j);
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
        LiteInventoryBase liteInv = getInventoryMap().get( new Integer(inventoryID) );
        /*
         * Should never return for non-Yukon meters.  Will always go to the db.
         */
        if (liteInv != null) return liteInv;
        
        if (autoLoad) {
            try {
                com.cannontech.database.db.stars.hardware.InventoryBase invDB =
                        new com.cannontech.database.db.stars.hardware.InventoryBase();
                invDB.setInventoryID( new Integer(inventoryID) );
                invDB = (com.cannontech.database.db.stars.hardware.InventoryBase)
                        Transaction.createTransaction( Transaction.RETRIEVE, invDB ).execute();
                
                if (InventoryUtils.isLMHardware( invDB.getCategoryID().intValue() )) {
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
                /*
                 * Should always get here for a non yukon meter.  They are not loaded into cache
                 * with other inventory.
                 */
                else if (InventoryUtils.isNonYukonMeter( invDB.getCategoryID().intValue()))
                {
                    MeterHardwareBase mhbDB = new MeterHardwareBase();
                    mhbDB.setInventoryID( invDB.getInventoryID() );
                    
                    mhbDB = (MeterHardwareBase) Transaction.createTransaction( Transaction.RETRIEVE, mhbDB ).execute();
                    
                    com.cannontech.database.data.stars.hardware.MeterHardwareBase hardware =
                            new com.cannontech.database.data.stars.hardware.MeterHardwareBase();
                    hardware.setInventoryBase( invDB );
                    hardware.setMeterHardwareBase( mhbDB );
                    
                    liteInv = new LiteMeterHardwareBase();
                    StarsLiteFactory.setLiteMeterHardwareBase( (LiteMeterHardwareBase)liteInv, hardware );
                }
                else {
                    liteInv = new LiteInventoryBase();
                    StarsLiteFactory.setLiteInventoryBase( liteInv, invDB );
                }
                
                addInventory( liteInv );
                return liteInv;
            } catch (TransactionException e) {
                CTILogger.error(e.getMessage(), e);
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
    
    public LiteInventoryBase getInventoryFromMap(int inventoryID)
    {
        return getInventoryMap().get( new Integer(inventoryID) );
    }
    
    public void addInventory(LiteInventoryBase liteInv) {
        Map<Integer,LiteInventoryBase> invMap = getInventoryMap();
        synchronized (invMap) {
            invMap.put( new Integer(liteInv.getInventoryID()), liteInv );
        }
    }
    
    public LiteInventoryBase deleteInventory(int invID) {
        Map<Integer,LiteInventoryBase> invMap = getInventoryMap();
        synchronized (invMap) {
            return invMap.remove( new Integer(invID) );
        }
    }
    
    public LiteInventoryBase reloadInventory(int invID) {
        Map<Integer,LiteInventoryBase> invMap = getInventoryMap();
        synchronized (invMap) {
            invMap.remove( new Integer(invID) );
            return getInventoryBrief( invID, true );
        }
    }
    
    /*
     * TODO: This method was changed after 3.2 so it does not depend on the yukondefinitionID and can instead
     * rely on the entryID of the type.  This allows change types between different aliases to be possible.
     * The downside is that the yukdefID was used to find occurrences in other energy companies 
     * (dev types in different ecs naturally have different entry IDs).  Currently this will not catch
     * these occurrences.  Is that a bad thing?
     */
    private Pair<LiteStarsLMHardware,LiteStarsEnergyCompany> searchForLMHardware(int devTypeEntryID, String serialNo, LiteStarsEnergyCompany referer)
        throws ObjectInOtherEnergyCompanyException
    {
        List<LiteInventoryBase> inventory = getAllInventory();
        for (final LiteInventoryBase liteInv : inventory) {
            if (liteInv.getInventoryID() < 0) continue;
            if (!(liteInv instanceof LiteStarsLMHardware)) continue;
            
            LiteStarsLMHardware liteHw = (LiteStarsLMHardware) liteInv;
            if ((devTypeEntryID == -1 || DaoFactory.getYukonListDao().getYukonListEntry( liteHw.getLmHardwareTypeID() ).getEntryID() == devTypeEntryID)
                && liteHw.getManufacturerSerialNumber().equalsIgnoreCase( serialNo ))
            {
                return new Pair<LiteStarsLMHardware,LiteStarsEnergyCompany>(liteHw, this);
            }
        }
        
        if (!isInventoryLoaded()) {
            try {
                com.cannontech.database.db.stars.hardware.LMHardwareBase[] hardwares =
                        com.cannontech.database.db.stars.hardware.LMHardwareBase.searchBySerialNumber( serialNo, getLiteID() );
                if (hardwares == null) return null;
                
                for (int i = 0; i < hardwares.length; i++) {
                    if (devTypeEntryID == -1 || DaoFactory.getYukonListDao().getYukonListEntry( hardwares[i].getLMHardwareTypeID().intValue() ).getEntryID() == devTypeEntryID) {
                        com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
                                new com.cannontech.database.data.stars.hardware.LMHardwareBase();
                        hw.setInventoryID( hardwares[i].getInventoryID() );
                        
                        hw = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
                                Transaction.createTransaction( Transaction.RETRIEVE, hw ).execute();
                        
                        LiteStarsLMHardware liteHw = new LiteStarsLMHardware();
                        StarsLiteFactory.setLiteStarsLMHardware( liteHw, hw );
                        addInventory( liteHw );
                        
                        return new Pair<LiteStarsLMHardware,LiteStarsEnergyCompany>(liteHw, this);
                    }
                }
            }
            catch (TransactionException e) {
                CTILogger.error( e.getMessage(), e );
                return null;
            }
        }
        
        // Search the LM hardware in the child energy companies
        List<LiteStarsEnergyCompany> children = getChildren();
        synchronized (children) {
            for (final LiteStarsEnergyCompany liteCompany : children) {
                if (!liteCompany.equals( referer )) {
                    Pair<LiteStarsLMHardware,LiteStarsEnergyCompany> p = liteCompany.searchForLMHardware( devTypeEntryID, serialNo, this );
                    if (p != null)
                        throw new ObjectInOtherEnergyCompanyException( (LiteStarsLMHardware)p.getFirst(), (LiteStarsEnergyCompany)p.getSecond() );
                }
            }
        }
        
        // Search the LM hardware in the parent energy company
        if (getParent() != null && !getParent().equals( referer )) {
            Pair<LiteStarsLMHardware,LiteStarsEnergyCompany> p = getParent().searchForLMHardware( devTypeEntryID, serialNo, this );
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
        int devTypeEntryID = DaoFactory.getYukonListDao().getYukonListEntry( deviceType ).getEntryID();
        Pair<LiteStarsLMHardware,LiteStarsEnergyCompany> p = searchForLMHardware( devTypeEntryID, serialNo, this );
        if (p != null) return (LiteStarsLMHardware)p.getFirst();
        return null;
    }
    
    public LiteStarsLMHardware searchUsingOnlySerialNum(String serialNo) throws ObjectInOtherEnergyCompanyException {
        Pair<LiteStarsLMHardware,LiteStarsEnergyCompany> p = searchForLMHardware( -1, serialNo, this );
        if (p != null) return (LiteStarsLMHardware)p.getFirst();
        return null;
    }
    /**
     * @return Pair(LiteInventoryBase, LiteStarsEnergyCompany)
     */
    private Pair<LiteInventoryBase,LiteStarsEnergyCompany> searchForDevice(int categoryID, String deviceName, LiteStarsEnergyCompany referer)
        throws ObjectInOtherEnergyCompanyException
    {
        List<LiteInventoryBase> inventory = getAllInventory();
        for (final LiteInventoryBase liteInv: inventory) {
            String paoName;
            try
            {
                paoName = DaoFactory.getPaoDao().getYukonPAOName(liteInv.getDeviceID());
            }
            catch(NotFoundException e)
            {
                paoName = "noneFound";
            }
            if (liteInv.getDeviceID() > 0 && liteInv.getCategoryID() == categoryID
                    && paoName.toUpperCase().startsWith( deviceName.toUpperCase() ))
            {
                return new Pair<LiteInventoryBase,LiteStarsEnergyCompany>(liteInv, this);
            }   
        }
        
        if (!isInventoryLoaded())
        {
            com.cannontech.database.db.stars.hardware.InventoryBase[] invList =
                com.cannontech.database.db.stars.hardware.InventoryBase.searchForDevice( deviceName + "%", getLiteID() );
            if (invList == null) return null;
            
            for (int i = 0; i < invList.length; i++) {
                if (invList[i].getCategoryID().intValue() == categoryID) {
                    LiteInventoryBase liteInv = new LiteInventoryBase();
                    StarsLiteFactory.setLiteInventoryBase( liteInv, invList[i] );
                    addInventory( liteInv );
                    
                    return new Pair<LiteInventoryBase,LiteStarsEnergyCompany>( liteInv, this );
                }
            }
        }
        
        // Search the device in the child energy companies
        List<LiteStarsEnergyCompany> children = getChildren();
        synchronized (children) {
            for (final LiteStarsEnergyCompany liteCompany : children) {
                if (!liteCompany.equals( referer )) {
                    Pair<LiteInventoryBase,LiteStarsEnergyCompany> p = liteCompany.searchForDevice( categoryID, deviceName, this );
                    if (p != null)
                        throw new ObjectInOtherEnergyCompanyException( (LiteInventoryBase)p.getFirst(), (LiteStarsEnergyCompany)p.getSecond() );
                }
            }
        }
        
        // Search the device in the parent energy company
        if (getParent() != null && !getParent().equals( referer )) {
            Pair<LiteInventoryBase,LiteStarsEnergyCompany> p = getParent().searchForDevice( categoryID, deviceName, this );
            if (p != null)
                throw new ObjectInOtherEnergyCompanyException( (LiteInventoryBase)p.getFirst(), (LiteStarsEnergyCompany)p.getSecond() );
        }
        
        return null;
    }
    
    /**
     * Search for device with the specified category and device name (based on partial match).
     * If this energy company is a part of an energy company hierarchy, and the device belongs to
     * another company in the hierarchy, the ObjectInOtherEnergyCompanyException is thrown. 
     */
    public LiteInventoryBase searchForDevice(int categoryID, String deviceName)
        throws ObjectInOtherEnergyCompanyException
    {
        Pair<LiteInventoryBase,LiteStarsEnergyCompany> p = searchForDevice( categoryID, deviceName, this );
        if (p != null) return (LiteInventoryBase)p.getFirst();
        
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        
        if (InventoryUtils.isMCT( categoryID )) {
            synchronized (cache) {
                List<LiteYukonPAObject> mctList = cache.getAllMCTs();
                for (int i = 0; i < mctList.size(); i++) {
                    LiteYukonPAObject litePao = mctList.get(i);
                    
                    if (litePao.getPaoName().toUpperCase().startsWith( deviceName.toUpperCase() )) {
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
        List<LiteInventoryBase> inventory = getAllInventory();
        for (final LiteInventoryBase liteInv : inventory) {
            if (liteInv.getDeviceID() == deviceID) return liteInv;
        }
        
        if (!isInventoryLoaded() || !ECUtils.isSingleEnergyCompany(this))
        {
            int[] val = com.cannontech.database.db.stars.hardware.InventoryBase.searchByDeviceID( deviceID );
            
            if (val != null) {
                if (val[1] == getLiteID()) {
                    return getInventoryBrief( val[0], true );
                }

                LiteStarsEnergyCompany company = StarsDatabaseCache.getInstance().getEnergyCompany( val[1] );
                LiteInventoryBase liteInv = company.getInventoryBrief( val[0], true );
                throw new ObjectInOtherEnergyCompanyException( liteInv, company );

            }
        }
        
        return null;
    }
    
    /**
     * Search the inventory by serial number. If searchMembers is true,
     * it returns a list of Pair(LiteInventoryBase, LiteStarsEnergyCompany);
     * otherwise it returns a list of LiteInventoryBase.
     */
    public List<Object> searchInventoryBySerialNo(String serialNo, boolean searchMembers) {
        List<Object> hwList = new ArrayList<Object>();
        
        if (isInventoryLoaded()) {
            List<LiteInventoryBase> inventory = getAllInventory();
            for (final LiteInventoryBase liteInv : inventory) {
                if (liteInv instanceof LiteStarsLMHardware) {
                    LiteStarsLMHardware liteHw = (LiteStarsLMHardware) liteInv;
                    if (liteHw.getManufacturerSerialNumber().equalsIgnoreCase( serialNo )) {
                        if (searchMembers)
                            hwList.add( new Pair<LiteStarsLMHardware,LiteStarsEnergyCompany>(liteHw, this) );
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
                    hwList.add( new Pair<LiteStarsLMHardware,LiteStarsEnergyCompany>(liteHw, this) );
                else
                    hwList.add( liteHw );
            }
        }
        
        if (searchMembers) {
            List<LiteStarsEnergyCompany> children = getChildren();
            synchronized (children) {
                for (final LiteStarsEnergyCompany company : children) {
                    List<Object> memberList = company.searchInventoryBySerialNo( serialNo, searchMembers );
                    hwList.addAll( memberList );
                }
            }
        }
        
        return hwList;
    }
    
    /**
     * Search the inventory by alternate tracking #. If searchMembers is true,
     * it returns a list of Pair(LiteInventoryBase, LiteStarsEnergyCompany);
     * otherwise it returns a list of LiteInventoryBase.
     */
    public List<Object> searchInventoryByAltTrackNo(String altTrackNo, boolean searchMembers) {
        List<Object> invList = new ArrayList<Object>();
        
        if (isInventoryLoaded()) {
            List<LiteInventoryBase> inventory = getAllInventory();
            for (final LiteInventoryBase liteInv : inventory) {
                if (liteInv.getAlternateTrackingNumber() != null &&
                        liteInv.getAlternateTrackingNumber().equalsIgnoreCase( altTrackNo ))
                {
                    if (searchMembers)
                        invList.add( new Pair<LiteInventoryBase,LiteStarsEnergyCompany>(liteInv, this) );
                    else
                        invList.add( liteInv );
                }
            }
        }
        else {
            int[] invIDs = com.cannontech.database.db.stars.hardware.InventoryBase.searchByAltTrackingNo( altTrackNo, getLiteID() );
            if (invIDs == null) return null;
            
            for (int i = 0; i < invIDs.length; i++) {
                LiteInventoryBase liteInv = getInventoryBrief( invIDs[i], true );
                if (searchMembers)
                    invList.add( new Pair<LiteInventoryBase,LiteStarsEnergyCompany>(liteInv, this) );
                else
                    invList.add( liteInv );
            }
        }
        
        if (searchMembers) {
            List<LiteStarsEnergyCompany> children = getChildren();
            synchronized (children) {
                for (final LiteStarsEnergyCompany company : children) {
                    List<Object> memberList = company.searchInventoryByAltTrackNo( altTrackNo, searchMembers );
                    invList.addAll( memberList );
                }
            }
        }
        
        return invList;
    }
    
    /**
     * Search the inventory by device name (based on partial match). The return value is
     * a list of Pair(LiteInventoryBase, LiteStarsEnergyCompany) if searchMembers is true,
     * a list of LiteInventoryBase otherwise.
     */
    public List<Object> searchInventoryByDeviceName(String deviceName, boolean searchMembers) {
        List<Object> devList = new ArrayList<Object>();
        
        if (isInventoryLoaded()) {
            List<LiteInventoryBase> inventory = getAllInventory();
            for (final LiteInventoryBase liteInv : inventory) {
                if (liteInv.getDeviceID() > 0
                        && DaoFactory.getPaoDao().getYukonPAOName(liteInv.getDeviceID()).toUpperCase().startsWith( deviceName.toUpperCase() ))
                    {
                        if (searchMembers)
                            devList.add( new Pair<LiteInventoryBase,LiteStarsEnergyCompany>(liteInv, this) );
                        else
                            devList.add( liteInv );
                    }
            }
        }
        else {
            com.cannontech.database.db.stars.hardware.InventoryBase[] invList =
                com.cannontech.database.db.stars.hardware.InventoryBase.searchForDevice( deviceName + "%", getLiteID() );
            if (invList == null) return null;
            
            for (int i = 0; i < invList.length; i++) {
                LiteInventoryBase liteInv = getInventoryBrief( invList[i].getInventoryID().intValue(), true );
                if (searchMembers)
                    devList.add( new Pair<LiteInventoryBase,LiteStarsEnergyCompany>(liteInv, this) );
                else
                    devList.add( liteInv );
            }
        }
        
        if (searchMembers) {
            List<LiteStarsEnergyCompany> children = getChildren();
            synchronized (children) {
                for (final LiteStarsEnergyCompany company : children) {
                    List<Object> memberList = company.searchInventoryByDeviceName( deviceName, searchMembers );
                    devList.addAll( memberList );
                }
            }
        }
        
        return devList;
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
            
            if (!InventoryUtils.isValidThermostatSchedule( liteSched )) {
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
        LiteWorkOrderBase liteWorkOrder = getWorkOrderMap().get( new Integer(orderID) );
        if (liteWorkOrder != null) return liteWorkOrder;
        
        if (autoLoad) {
            try {
                com.cannontech.database.data.stars.report.WorkOrderBase order = new com.cannontech.database.data.stars.report.WorkOrderBase();
                order.setOrderID( new Integer(orderID) );
                order = (com.cannontech.database.data.stars.report.WorkOrderBase)
                        Transaction.createTransaction( Transaction.RETRIEVE, order ).execute();
                liteWorkOrder = (LiteWorkOrderBase) StarsLiteFactory.createLite( order );
                
                addWorkOrderBase( liteWorkOrder );
                return liteWorkOrder;
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
    public List<Object> searchWorkOrderByOrderNo(String orderNo, boolean searchMembers) {
        List<Object> orderList = new ArrayList<Object>();
        
        if (isWorkOrdersLoaded()) {
            List<LiteWorkOrderBase> workOrders = getAllWorkOrders();
            
            for (int i = 0; i < workOrders.size(); i++) {
                LiteWorkOrderBase liteOrder = workOrders.get(i);
                if (liteOrder.getOrderNumber().equalsIgnoreCase(orderNo)) {
                    if (searchMembers)
                        orderList.add( new Pair<LiteWorkOrderBase,LiteStarsEnergyCompany>(liteOrder, this) );
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
                    orderList.add( new Pair<LiteWorkOrderBase,LiteStarsEnergyCompany>(liteOrder, this) );
                else
                    orderList.add( liteOrder );
            }
        }
        
        if (searchMembers) {
            List<LiteStarsEnergyCompany> children = getChildren();
            synchronized (children) {
                for (final LiteStarsEnergyCompany company : children) {
                    List<Object> memberList = company.searchWorkOrderByOrderNo( orderNo, searchMembers );
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
        
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            liteAcctInfo.setCustomer(cache.getACustomerByCustomerID(account.getCustomerAccount().getCustomerID().intValue()) );
        }
        
        List<LiteStarsAppliance> appliances = new ArrayList<LiteStarsAppliance>();
        for (int i = 0; i < account.getApplianceVector().size(); i++) {
            LiteStarsAppliance liteApp = new LiteStarsAppliance();
            liteApp.setApplianceID( account.getApplianceVector().get(i).intValue() );
            appliances.add( liteApp );
        }
        liteAcctInfo.setAppliances( appliances );
        
        List<Integer> inventories = new ArrayList<Integer>();
        for (int i = 0; i < account.getInventoryVector().size(); i++)
            inventories.add( account.getInventoryVector().get(i) );
        liteAcctInfo.setInventories( inventories );
        
        addCustAccountInformation( liteAcctInfo );
        
        return liteAcctInfo;
    }

    /**
     * Alternate method to addBriefCustAccountInfo when you already have a liteStarsCustAccountInformation object.
     * @param liteStarsCustAccountInfo
     */
    private void addBriefCustAccountInfo(LiteStarsCustAccountInformation liteStarsCustAccountInfo) {
        //Don't get these addresses unless they are needed!
        /*LiteAddress streetLiteAddress = new LiteAddress(liteStarsCustAccountInfo.getAccountSite().getStreetAddressID());
        streetLiteAddress.retrieve();
        addAddress( streetLiteAddress );
        
        LiteAddress billingLiteAddress = new LiteAddress(liteStarsCustAccountInfo.getCustomerAccount().getBillingAddressID());
        billingLiteAddress.retrieve();
        addAddress( billingLiteAddress );*/

        addCustAccountInformation( liteStarsCustAccountInfo);
    }

    /*Have to use Yao's idea of extending customer account information but I don't want all of 
     * it, just appliances and inventory.
     */
    public LiteStarsCustAccountInformation limitedExtendCustAccountInfo(LiteStarsCustAccountInformation liteAcctInfo) {
        try {
            List<LiteStarsAppliance> appliances = liteAcctInfo.getAppliances();
            for (int i = 0; i < appliances.size(); i++) {
                LiteStarsAppliance liteApp = appliances.get(i);
                
                com.cannontech.database.data.stars.appliance.ApplianceBase appliance =
                        new com.cannontech.database.data.stars.appliance.ApplianceBase();
                appliance.setApplianceID( new Integer(liteApp.getApplianceID()) );
                
                appliance = (com.cannontech.database.data.stars.appliance.ApplianceBase)
                        Transaction.createTransaction( Transaction.RETRIEVE, appliance ).execute();
                
                liteApp = StarsLiteFactory.createLiteStarsAppliance( appliance, this );
                appliances.set(i, liteApp);
            }
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }
        
        return liteAcctInfo;
    }
    
    private void extendCustAccountInfo(LiteStarsCustAccountInformation liteAcctInfo) {
        try {
            com.cannontech.database.db.stars.customer.CustomerResidence residence =
                    com.cannontech.database.db.stars.customer.CustomerResidence.getCustomerResidence( liteAcctInfo.getAccountSite().getAccountSiteID() );
            if (residence != null)
                liteAcctInfo.setCustomerResidence( (LiteCustomerResidence) StarsLiteFactory.createLite(residence) );
            
            List<LiteStarsAppliance> appliances = liteAcctInfo.getAppliances();
            for (int i = 0; i < appliances.size(); i++) {
                LiteStarsAppliance liteApp = appliances.get(i);
                
                com.cannontech.database.data.stars.appliance.ApplianceBase appliance =
                        new com.cannontech.database.data.stars.appliance.ApplianceBase();
                appliance.setApplianceID( new Integer(liteApp.getApplianceID()) );
                
                appliance = (com.cannontech.database.data.stars.appliance.ApplianceBase)
                        Transaction.createTransaction( Transaction.RETRIEVE, appliance ).execute();
                
                liteApp = StarsLiteFactory.createLiteStarsAppliance( appliance, this );
                appliances.set(i, liteApp);
            }
            
            List<Integer> inventories = liteAcctInfo.getInventories();
            for (final Integer invID : inventories) {
                getInventory( invID.intValue(), true );
            }
            
            List<LiteLMProgramWebPublishing> allProgs = getAllPrograms();
            int[] allProgIDs = new int[ allProgs.size() ];
            for (int i = 0; i < allProgs.size(); i++)
                allProgIDs[i] = allProgs.get(i).getProgramID();
            Arrays.sort( allProgIDs );
            
            List<LiteLMProgramEvent> progHist = new ArrayList<LiteLMProgramEvent>();
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
            
            List<LiteStarsLMProgram> programs = new ArrayList<LiteStarsLMProgram>();
            for (int i = 0; i < appliances.size(); i++) {
                LiteStarsAppliance liteApp = appliances.get(i);
                int progID = liteApp.getProgramID();
                if (progID == 0) continue;
                
                boolean progExists = false;
                for (int j = 0; j < programs.size(); j++) {
                    if ((programs.get(j)).getProgramID() == progID) {
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
                liteAcctInfo.setCallReportHistory( new ArrayList<StarsCallReport>() );
                for (int i = 0; i < calls.length; i++)
                    liteAcctInfo.getCallReportHistory().add( calls[i] );
            }
            
            int[] orderIDs = com.cannontech.database.db.stars.report.WorkOrderBase.searchByAccountID( liteAcctInfo.getLiteID() );
            if (orderIDs != null) {
                liteAcctInfo.setServiceRequestHistory( new ArrayList<Integer>() );
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
        LiteStarsCustAccountInformation liteAcctInfo = getCustAccountInfoMap().get( new Integer(accountID) );
        if (liteAcctInfo != null) return liteAcctInfo;
        
        if (autoLoad) {
            try {
                liteAcctInfo = new LiteStarsCustAccountInformation(accountID);                
                liteAcctInfo.retrieveLiteStarsCustAccountInfo();
                
                if (liteAcctInfo.getCustomerAccount() == null) return null; //retrieveLiteStarsCustAccountInfo failed
                
                addBriefCustAccountInfo( liteAcctInfo);
                return liteAcctInfo;
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
        Map<Integer,LiteStarsCustAccountInformation> custAcctMap = getCustAccountInfoMap();
        synchronized (custAcctMap) {
            custAcctMap.put( new Integer(liteAcctInfo.getAccountID()), liteAcctInfo );
        }
    }

    public void deleteCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo) {
        if (liteAcctInfo == null) return;
        
        // Remove from opt out event queue
        OptOutEventQueue.getInstance().removeEvents( liteAcctInfo.getAccountID() );
        
        // Remove customer from the cache
        ServerUtils.handleDBChange( liteAcctInfo.getCustomer(), DBChangeMsg.CHANGE_TYPE_DELETE );
        
        // Remote all contacts from the cache
        Map<Integer,Integer> contAcctIDMap = getContactAccountIDMap();
        synchronized (contAcctIDMap) {
            contAcctIDMap.remove( new Integer(liteAcctInfo.getCustomer().getPrimaryContactID()) );
            
            Vector<LiteContact> contacts = liteAcctInfo.getCustomer().getAdditionalContacts();
            for (int i = 0; i < contacts.size(); i++)
                contAcctIDMap.remove( new Integer(contacts.get(i).getContactID()) );
        }
        
        // Refresh all inventory information
        for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
            int invID = liteAcctInfo.getInventories().get(i).intValue();
            reloadInventory( invID );
        }
        
        // Remove all work orders from the cache
        for (int i = 0; i < liteAcctInfo.getServiceRequestHistory().size(); i++) {
            int orderID = liteAcctInfo.getServiceRequestHistory().get(i).intValue();
            deleteWorkOrderBase( orderID );
        }
        
        // Remove the customer account from cache
        Map<Integer,LiteStarsCustAccountInformation> custAcctMap = getCustAccountInfoMap();
        synchronized (custAcctMap) {
            custAcctMap.remove( new Integer(liteAcctInfo.getAccountID()) );
        }
    }
    
    public LiteStarsCustAccountInformation reloadCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo) {
        // Reload the customer account into cache
        Map<Integer,LiteStarsCustAccountInformation> custAcctMap = getCustAccountInfoMap();
        synchronized (custAcctMap) {
            custAcctMap.remove( new Integer(liteAcctInfo.getAccountID()) );
            return getBriefCustAccountInfo( liteAcctInfo.getAccountID(), true );
        }
    }
    
    /**
     * Search customer account by account # within the energy company.
     * int totalComparableDigits is for use with rotation digit billing systems: the field
     * represents the total length of the account number to be considered in the search (i.e. 
     * the account number sans the rotation digits, most commonly the last two digits).  If
     * normal length, then 0 or lower will result in the whole account number being used.  This would
     * be default.  
     */
    public LiteStarsCustAccountInformation searchAccountByAccountNo(String accountNo) 
    {
        List<LiteStarsCustAccountInformation> custAcctInfoList = getAllCustAccountInformation();
        String comparableDigitProperty = DaoFactory.getAuthDao().getRolePropertyValue(DaoFactory.getYukonUserDao().getLiteYukonUser(getUserID()), ConsumerInfoRole.ACCOUNT_NUMBER_LENGTH);
        int comparableDigitEndIndex = 0;
        String rotationDigitProperty = DaoFactory.getAuthDao().getRolePropertyValue(DaoFactory.getYukonUserDao().getLiteYukonUser(getUserID()), ConsumerInfoRole.ROTATION_DIGIT_LENGTH);
        int accountNumSansRotationDigitsIndex = accountNo.length();
        boolean adjustForRotationDigits = false, adjustForAccountLength = false;
        if(rotationDigitProperty != null && rotationDigitProperty.compareTo(CtiUtilities.STRING_NONE) != 0 && Integer.parseInt(rotationDigitProperty) > 0
                && Integer.parseInt(rotationDigitProperty) < accountNo.length())
        {
            accountNumSansRotationDigitsIndex = accountNo.length() - Integer.parseInt(rotationDigitProperty);
            accountNo = accountNo.substring(0, accountNumSansRotationDigitsIndex);
            adjustForRotationDigits = true;
        }
        if(comparableDigitProperty != null && comparableDigitProperty.compareTo(CtiUtilities.STRING_NONE) != 0 && Integer.parseInt(comparableDigitProperty) > 0)
        {
            comparableDigitEndIndex = Integer.parseInt(comparableDigitProperty);
            if(accountNo.length() >= comparableDigitEndIndex)
                accountNo = accountNo.substring(0, comparableDigitEndIndex);
            adjustForAccountLength = true;
        }
        
		for (int i = 0; i < custAcctInfoList.size(); i++) {
            LiteStarsCustAccountInformation liteAcctInfo = custAcctInfoList.get(i);
            String comparableAcctNum = liteAcctInfo.getCustomerAccount().getAccountNumber();
            if(accountNumSansRotationDigitsIndex > 0 && comparableAcctNum.length() >= accountNumSansRotationDigitsIndex && adjustForRotationDigits)
                comparableAcctNum = comparableAcctNum.substring(0, accountNumSansRotationDigitsIndex);
            if(comparableDigitEndIndex > 0 && comparableAcctNum.length() >= comparableDigitEndIndex && adjustForAccountLength)
                comparableAcctNum = comparableAcctNum.substring(0, comparableDigitEndIndex);
            if (comparableAcctNum.equalsIgnoreCase( accountNo ))
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
    public List<Object> searchAccountByAccountNumber(String accountNumber, boolean searchMembers, boolean partialMatch) {
        List<Object> accountList = new ArrayList<Object>();
        if (isAccountsLoaded()) {
            return searchLoadedAccountsByAccountNumber(accountNumber, searchMembers);
        }

        List<Integer> allEnergyCompanyIDs = new ArrayList<Integer>();
        if( searchMembers)
            allEnergyCompanyIDs = getAllEnergyCompaniesDownward();
        else
            allEnergyCompanyIDs.add(new Integer(getLiteID()) );

        accountList = searchByAccountNumber( accountNumber, partialMatch, allEnergyCompanyIDs, searchMembers);

        return accountList;
    }
    
    private List<Object> searchLoadedAccountsByAccountNumber(String accountNo, boolean searchMembers) {
        List<Object> accountList = new ArrayList<Object>();

        List<LiteStarsCustAccountInformation> custAcctInfoList = getAllCustAccountInformation();
        for (final LiteStarsCustAccountInformation liteAcctInfo : custAcctInfoList) {
            if (liteAcctInfo.getCustomerAccount().getAccountNumber().toUpperCase().startsWith( accountNo.toUpperCase() ))
            {
                if (searchMembers)
                    accountList.add( new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(liteAcctInfo, this) );
                else
                    accountList.add( liteAcctInfo );
            }
        }
        
        if (searchMembers) {
            List<LiteStarsEnergyCompany> children = getChildren();
            synchronized (children) {
                for (final LiteStarsEnergyCompany company : children) {
                    List<Object> memberList = company.searchLoadedAccountsByAccountNumber( accountNo, searchMembers );
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
    public List<Object> searchAccountBySerialNo(String serialNo, boolean searchMembers) {
        List<Object> invList = searchInventoryBySerialNo( serialNo, false );
        List<Object> accountList = new ArrayList<Object>();
        
        for (int i = 0; i < invList.size(); i++) {
            LiteInventoryBase inv = (LiteInventoryBase) invList.get(i);
            if (inv.getAccountID() > 0) {
                LiteStarsCustAccountInformation liteAcctInfo = getBriefCustAccountInfo( inv.getAccountID(), true );
                if (searchMembers)
                    accountList.add( new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(liteAcctInfo, this) );
                else
                    accountList.add( liteAcctInfo );
            }
        }
        
        if (searchMembers) {
           List<LiteStarsEnergyCompany> children = getChildren();
            synchronized (children) {
                for (final LiteStarsEnergyCompany company : children) {
                    List<Object> memberList = company.searchAccountBySerialNo( serialNo, searchMembers );
                    accountList.addAll( memberList );
                }
            }
        }
        
        return accountList;
    }
    
    /**
     * Search customer accounts by hardware alternate tracking #.
     * If searchMembers is true, it returns a list of Pair(LiteStarsCustAccountInformation, LiteStarsEnergyCompany);
     * otherwise it returns a list of LiteStarsCustAccountInformation.
     */
    public List<Object> searchAccountByAltTrackNo(String altTrackNo, boolean searchMembers) {
        List<Object> invList = searchInventoryByAltTrackNo( altTrackNo, false );
        List<Object> accountList = new ArrayList<Object>();
        
        for (int i = 0; i < invList.size(); i++) {
            LiteInventoryBase inv = (LiteInventoryBase) invList.get(i);
            if (inv.getAccountID() > 0) {
                LiteStarsCustAccountInformation liteAcctInfo = getBriefCustAccountInfo( inv.getAccountID(), true );
                if (searchMembers)
                    accountList.add( new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(liteAcctInfo, this) );
                else
                    accountList.add( liteAcctInfo );
            }
        }
        
        if (searchMembers) {
            List<LiteStarsEnergyCompany> children = getChildren();
            synchronized (children) {
                for (final LiteStarsEnergyCompany company : children) {
                    List<Object> memberList = company.searchAccountByAltTrackNo( altTrackNo, searchMembers );
                    accountList.addAll( memberList );
                }
            }
        }
        
        return accountList;
    }
    
    /**
    * Search customer account by company name, search results based on partial match
    * If searchMembers is true, the return type is Pair(LiteStarsCustAccountInformation, LiteStarsEnergyCompany);
    * otherwise the return type is LiteStarsCustAccountInformation.
    */
    public List<Object> searchAccountByCompanyName(String searchName, boolean searchMembers) {
        List<Object> accountList = new ArrayList<Object>();
        
        if (isAccountsLoaded()) 
        {
            List<LiteStarsCustAccountInformation> custAcctInfoList = getAllCustAccountInformation();
            for (final LiteStarsCustAccountInformation liteAcctInfo : custAcctInfoList) {
                LiteCustomer liteDude = liteAcctInfo.getCustomer();
                if (liteDude instanceof LiteCICustomer) 
                {
                    LiteCICustomer liteCICust = (LiteCICustomer) liteDude;
                    if (liteCICust.getCompanyName().toUpperCase().startsWith( searchName.toUpperCase() ))
                    {
                        if (searchMembers)
                            accountList.add( new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(liteAcctInfo, this) );
                        else
                            accountList.add( liteAcctInfo );
                    }
                }
            }
        }
        else 
        {
            int[] accountIDs = com.cannontech.database.db.stars.customer.CustomerAccount.searchByCompanyName( searchName + "%", getLiteID() );
            if (accountIDs != null) {
               for (int i = 0; i < accountIDs.length; i++) {
                   LiteStarsCustAccountInformation liteAcctInfo = getBriefCustAccountInfo( accountIDs[i], true );
                   if (searchMembers)
                       accountList.add( new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(liteAcctInfo, this) );
                   else
                       accountList.add( liteAcctInfo );
               }
           }
       }
        
       if (searchMembers) {
           List<LiteStarsEnergyCompany> children = getChildren();
           synchronized (children) {
               for (final LiteStarsEnergyCompany company : children) {
                   List<Object> memberList = company.searchAccountByCompanyName( searchName, searchMembers );
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
    public List<Object> searchAccountByMapNo(String mapNo, boolean searchMembers) {
        List<Object> accountList = new ArrayList<Object>();
        
        if (isAccountsLoaded()) {
            List<LiteStarsCustAccountInformation> custAcctInfoList = getAllCustAccountInformation();
            for (final LiteStarsCustAccountInformation liteAcctInfo : custAcctInfoList) {
                if (liteAcctInfo.getAccountSite().getSiteNumber().toUpperCase().startsWith( mapNo.toUpperCase() ))
                {
                    if (searchMembers)
                        accountList.add( new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(liteAcctInfo, this) );
                    else
                        accountList.add( liteAcctInfo );
                }
            }
        }
        else {
            int[] accountIDs = com.cannontech.database.db.stars.customer.CustomerAccount.searchByMapNumber( mapNo + "%", getLiteID() );
            if (accountIDs != null) {
                for (int i = 0; i < accountIDs.length; i++) {
                    LiteStarsCustAccountInformation liteAcctInfo = getBriefCustAccountInfo( accountIDs[i], true );
                    if (searchMembers)
                        accountList.add( new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(liteAcctInfo, this) );
                    else
                        accountList.add( liteAcctInfo );
                }
            }
        }
        
        if (searchMembers) {
            List<LiteStarsEnergyCompany> children = getChildren();
            synchronized (children) {
                for (final LiteStarsEnergyCompany company : children) {
                    List<Object> memberList = company.searchAccountByMapNo( mapNo, searchMembers );
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
    public List<Object> searchAccountByAddress(String address, boolean searchMembers, boolean partialMatch) {
        List<Object> accountList = new ArrayList<Object>();
        if (isAccountsLoaded()) {
            return searchLoadedAccountsByAddress(address, searchMembers);
        }

        List<Integer> allEnergyCompanyIDs = new ArrayList<Integer>();
        if( searchMembers)
            allEnergyCompanyIDs = getAllEnergyCompaniesDownward();
        else
            allEnergyCompanyIDs.add(new Integer(getLiteID()) );

        accountList = searchByStreetAddress( address, partialMatch, allEnergyCompanyIDs, searchMembers);

        return accountList;
    }
    
    private List<Object> searchLoadedAccountsByAddress(String address, boolean searchMembers) {
        List<Object> accountList = new ArrayList<Object>();
        
        List<LiteStarsCustAccountInformation> custAcctInfoList = getAllCustAccountInformation();
        List<LiteStarsCustAccountInformation> matchedCustAcctInfoList = ecSearchDao.searchAddressByLocationAddress1(address, custAcctInfoList);
        for (final LiteStarsCustAccountInformation liteAcctInfo : matchedCustAcctInfoList) {
            if (searchMembers) {
                accountList.add( new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(liteAcctInfo, this) );
            } else {
                accountList.add( liteAcctInfo );
            }
        }
        
        if (searchMembers) {
            List<LiteStarsEnergyCompany> children = getChildren();
            synchronized (children) {
                for (final LiteStarsEnergyCompany company : children) {
                    List<Object> memberList = company.searchLoadedAccountsByAddress( address, searchMembers );
                    accountList.addAll( memberList );
                }
            }
        }
        
        return accountList;
    }
    
    @SuppressWarnings("unchecked")
    public List<Object> searchAccountByOrderNo(String orderNo, boolean searchMembers) {
        List<Object> orderList = searchWorkOrderByOrderNo( orderNo, false );
        List<Object> accountList = new ArrayList<Object>();

        for (int i = 0; i < orderList.size(); i++) {
            Object liteOrderObject = orderList.get(i);
            LiteWorkOrderBase liteOrder = null;
            
            if (liteOrderObject instanceof LiteWorkOrderBase) {
                liteOrder = (LiteWorkOrderBase) liteOrderObject;
            }
            
            if (liteOrderObject instanceof Pair) {
                Pair<LiteWorkOrderBase,LiteStarsEnergyCompany> pair = (Pair<LiteWorkOrderBase,LiteStarsEnergyCompany>) liteOrderObject;
                liteOrder = (LiteWorkOrderBase) pair.getFirst();
            }
            
            if (liteOrder != null && liteOrder.getAccountID() > 0) {
                LiteStarsCustAccountInformation liteAcctInfo = getBriefCustAccountInfo( liteOrder.getAccountID(), true );
                if (searchMembers)
                    accountList.add( new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(liteAcctInfo, this) );
                else
                    accountList.add( liteAcctInfo );
            }    
        }
        
        if (searchMembers) {
            List<LiteStarsEnergyCompany> children = getChildren();
            synchronized (children) {
                for (LiteStarsEnergyCompany company : children) {
                    List<Object> memberList = company.searchAccountByOrderNo( orderNo, searchMembers );
                    accountList.addAll( memberList );
                }
            }
        }
        
        return accountList;
    }
    
	private List<Object> searchAccountByContactIDs(int[] contactIDs, boolean searchMembers) {
        List<Object> accountList = new ArrayList<Object>();
        if (isAccountsLoaded()) {
            List<LiteStarsCustAccountInformation> custAcctInfoList = getAllCustAccountInformation();
            for (final LiteStarsCustAccountInformation liteAcctInfo: custAcctInfoList) {
                for (int j = 0; j < contactIDs.length; j++) {
                    if (liteAcctInfo.getCustomer().getPrimaryContactID() == contactIDs[j]) {
                        if (searchMembers)
                            accountList.add( new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(liteAcctInfo, this) );
                        else
                            accountList.add( liteAcctInfo );
                        break;
                    }
                }
            }
		}
        else {
            int[] accountIDs = com.cannontech.database.db.stars.customer.CustomerAccount.searchByPrimaryContactIDs( contactIDs, getLiteID() );
            if (accountIDs != null) {
                for (int i = 0; i < accountIDs.length; i++) {
                    LiteStarsCustAccountInformation liteAcctInfo = getBriefCustAccountInfo( accountIDs[i], true );
                    if (searchMembers)
                        accountList.add( new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(liteAcctInfo, this) );
                    else
                        accountList.add( liteAcctInfo );
                }
            }
        }
        
        if (searchMembers) {
            List<LiteStarsEnergyCompany> children = getChildren();
            synchronized (children) {
                for (final LiteStarsEnergyCompany company : children) {
                    List<Object> memberList = company.searchAccountByContactIDs( contactIDs, searchMembers );
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
    public List<Object> searchAccountByPhoneNo(String phoneNo, boolean searchMembers) {
        LiteContact[] contacts = DaoFactory.getContactDao().getContactsByPhoneNo(
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
    public List<Object> searchAccountByLastName(String lastName, boolean searchMembers, boolean partialMatch) {
        List<Object> accountList = new ArrayList<Object>();
        if (isAccountsLoaded()) {
            int[] contactIDs = DaoFactory.getContactDao().retrieveContactIDsByLastName( lastName, partialMatch);
            return searchAccountByContactIDs( contactIDs, searchMembers );
        }

        List<Integer> allEnergyCompanyIDs = new ArrayList<Integer>();
        if( searchMembers)
            allEnergyCompanyIDs = getAllEnergyCompaniesDownward();
        else
            allEnergyCompanyIDs.add(new Integer(getLiteID()) );

        accountList = searchByPrimaryContactLastName( lastName, partialMatch, allEnergyCompanyIDs, searchMembers);

        return accountList;
    }
    /* The following methods are only used when SOAPClient exists locally */
    
    public synchronized StarsEnergyCompanySettings getStarsEnergyCompanySettings(StarsYukonUser user) {
        if (StarsUtils.isOperator(user.getYukonUser())) {
            if (starsOperECSettings == null) {
                starsOperECSettings = new StarsEnergyCompanySettings();
                starsOperECSettings.setEnergyCompanyID( user.getEnergyCompanyID() );
                starsOperECSettings.setStarsEnergyCompany( getStarsEnergyCompany() );
                starsOperECSettings.setStarsEnrollmentPrograms( getStarsEnrollmentPrograms() );
                starsOperECSettings.setStarsCustomerSelectionLists( getStarsCustomerSelectionLists(user) );
                starsOperECSettings.setStarsServiceCompanies( getStarsServiceCompanies() );
                starsOperECSettings.setStarsSubstations( getStarsSubstations() );
                starsOperECSettings.setStarsExitInterviewQuestions( getStarsExitInterviewQuestions() );
                starsOperECSettings.setStarsDefaultThermostatSchedules( getStarsDefaultThermostatSchedules() );
            }
            
            return starsOperECSettings;
        }
        else if (StarsUtils.isResidentialCustomer(user.getYukonUser())) {
            if (starsCustECSettings == null) {
                starsCustECSettings = new StarsEnergyCompanySettings();
                starsCustECSettings.setEnergyCompanyID( user.getEnergyCompanyID() );
                starsCustECSettings.setStarsEnergyCompany( getStarsEnergyCompany() );
                starsCustECSettings.setStarsEnrollmentPrograms( getStarsEnrollmentPrograms() );
                starsCustECSettings.setStarsCustomerSelectionLists( getStarsCustomerSelectionLists(user) );
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
    
    private Map<String,StarsCustSelectionList> getStarsCustSelectionLists() {
        if (starsSelectionLists == null)
            starsSelectionLists = new Hashtable<String,StarsCustSelectionList>();
        return starsSelectionLists;
    }
    
    private StarsCustSelectionList getStarsCustSelectionList(String listName) {
        Map<String,StarsCustSelectionList> starsSelectionLists = getStarsCustSelectionLists();
        synchronized (starsSelectionLists) {
            StarsCustSelectionList starsList = starsSelectionLists.get( listName );
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
        
        // Currently the consumer side only need chance of control and opt out period list
        StarsCustSelectionList list = getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL);
        if (list != null) starsCustSelLists.addStarsCustSelectionList( list );
        list = getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD);
        if (list != null) starsCustSelLists.addStarsCustSelectionList( list );
        
        }
    
    public synchronized void updateStarsCustomerSelectionLists() {
        Map<String,StarsCustSelectionList> starsSelectionLists = getStarsCustSelectionLists();
        synchronized (starsSelectionLists) { starsSelectionLists.clear(); }
        
        if (starsOperSelLists != null)
            updateOperSelectionLists();
        if (starsCustSelLists != null)
            updateCustSelectionLists();
    }
    
    public synchronized StarsCustomerSelectionLists getStarsCustomerSelectionLists(StarsYukonUser starsUser) {
        if (StarsUtils.isOperator( starsUser.getYukonUser() )) {
            if (starsOperSelLists == null) {
                starsOperSelLists = new StarsCustomerSelectionLists();
                updateOperSelectionLists();
            }
            
            return starsOperSelLists;
        }
        else if (StarsUtils.isResidentialCustomer( starsUser.getYukonUser() )) {
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
    
    public synchronized void updateStarsServiceCompanies() {
        if (starsServCompanies == null) return;
        
        starsServCompanies.removeAllStarsServiceCompany();
        
        // Always add a "(none)" to the service company list
        StarsServiceCompany starsServCompany = new StarsServiceCompany();
        starsServCompany.setCompanyID( 0 );
        starsServCompany.setCompanyName( "(none)" );
        starsServCompanies.addStarsServiceCompany( starsServCompany );
        
        List<LiteServiceCompany> servCompanies = getAllServiceCompanies();
        Collections.sort( servCompanies, StarsUtils.SERVICE_COMPANY_CMPTR );
        
        for (int i = 0; i < servCompanies.size(); i++) {
            LiteServiceCompany liteServCompany = servCompanies.get(i);
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
    
    @SuppressWarnings("unchecked")
    public synchronized void updateStarsSubstations() {
        if (starsSubstations == null) return;
        
        starsSubstations.removeAllStarsSubstation();
        
        // Always add a "(none)" to the service company list
        StarsSubstation starsSub = new StarsSubstation();
        starsSub.setSubstationID( 0 );
        starsSub.setSubstationName( "(none)" );
        starsSub.setRouteID( 0 );
        starsSubstations.addStarsSubstation( starsSub );
        
        List<LiteSubstation> substations = getAllSubstations();
        Collections.sort( substations, StarsUtils.SUBSTATION_CMPTR );
        
        for (int i = 0; i < substations.size(); i++) {
            LiteSubstation liteSub = substations.get(i);
            starsSub = new StarsSubstation();
            StarsLiteFactory.setStarsSubstation( starsSub, liteSub, this );
            starsSubstations.addStarsSubstation( starsSub );
        }
    }
    
    public synchronized StarsSubstations getStarsSubstations() {
        if (starsSubstations == null) {
            starsSubstations = new StarsSubstations();
            updateStarsSubstations();
        }
        
        return starsSubstations;
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
        boolean hasPump = false;
        
        YukonSelectionList devTypeList = getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE);
        for (int i = 0; i < devTypeList.getYukonListEntries().size(); i++) {
            YukonListEntry entry = devTypeList.getYukonListEntries().get(i);
            if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT)
                hasBasic = true;
            else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO)
                hasEpro = true;
            else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT)
                hasComm = true;
            else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_HEATPUMP)
                hasPump = true;
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
        if (hasPump) {
            StarsThermostatProgram starsThermProg = StarsLiteFactory.createStarsThermostatProgram(
                    getDefaultThermostatSchedule(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_HEATPUMP), this );
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

    private Map<Integer,StarsCustAccountInformation> getStarsCustAcctInfos() {
        if (starsCustAcctInfos == null)
            starsCustAcctInfos = new Hashtable<Integer,StarsCustAccountInformation>();
        return starsCustAcctInfos;
    }
    
    public StarsCustAccountInformation getStarsCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo) {
        Map<Integer,StarsCustAccountInformation> starsCustAcctInfos = getStarsCustAcctInfos();
        synchronized (starsCustAcctInfos) {
            Integer accountID = new Integer(liteAcctInfo.getAccountID());
            StarsCustAccountInformation starsAcctInfo = starsCustAcctInfos.get( accountID );
            if (starsAcctInfo == null) {
                starsAcctInfo = StarsLiteFactory.createStarsCustAccountInformation( liteAcctInfo, this, true );
                starsAcctInfo.setLastActiveTime( new Date() );
                starsCustAcctInfos.put( accountID, starsAcctInfo );
            }
            
            return starsAcctInfo;
        }
    }
    
    public StarsCustAccountInformation getStarsCustAccountInformation(int accountID, boolean autoLoad) {
        Map<Integer,StarsCustAccountInformation> starsCustAcctInfos = getStarsCustAcctInfos();
        synchronized (starsCustAcctInfos) {
            StarsCustAccountInformation starsAcctInfo = starsCustAcctInfos.get( new Integer(accountID) );
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
        Map<Integer,StarsCustAccountInformation> starsCustAcctInfos = getStarsCustAcctInfos();
        synchronized (starsCustAcctInfos) {
            Integer accountID = new Integer(liteAcctInfo.getAccountID());
            StarsCustAccountInformation starsAcctInfo = starsCustAcctInfos.get( accountID );
            if (starsAcctInfo != null)
                StarsLiteFactory.setStarsCustAccountInformation( starsAcctInfo, liteAcctInfo, this, true );
            
            return starsAcctInfo;
        }
    }
    
    public void deleteStarsCustAccountInformation(int accountID) {
        StarsCustAccountInformation starsAcctInfo = getStarsCustAccountInformation( accountID );
        if (starsAcctInfo != null) {
            Map<Integer,StarsCustAccountInformation> starsCustAcctInfos = getStarsCustAcctInfos();
            synchronized (starsCustAcctInfos) { starsCustAcctInfos.remove( new Integer(accountID) ); }
        }
    }
    
    public List<StarsCustAccountInformation> getActiveAccounts() {
        Collection<StarsCustAccountInformation> values = getStarsCustAcctInfos().values(); 
        return new ArrayList<StarsCustAccountInformation>(values);
    }
    
    public synchronized Map<Integer,Integer> getContactAccountIDMap() {
        if (contactAccountIDMap == null)
            contactAccountIDMap = new Hashtable<Integer,Integer>();
        
        return contactAccountIDMap;
    }
    
    /**
     * Register the StarsCustAccountInformation object as "active"
     * If the return value is false, it means the StarsCustAccountInformation object
     * is out of date, user should store a new object in the session by calling
     * getStarsCustAccountInformation(accountID, true)
     */
    public boolean registerActiveAccount(StarsCustAccountInformation starsAcctInfo) {
        Map<Integer,StarsCustAccountInformation> starsCustAcctInfos = getStarsCustAcctInfos();
        synchronized (starsCustAcctInfos) {
            Integer accountID = new Integer( starsAcctInfo.getStarsCustomerAccount().getAccountID() );
            StarsCustAccountInformation storedAcctInfo = starsCustAcctInfos.get( accountID );
            if (storedAcctInfo == null || !storedAcctInfo.equals( starsAcctInfo ))
                return false;
        }
        
        starsAcctInfo.setLastActiveTime( new Date() );
        
        // Add contact ID to account ID mapping into the table
        Integer accountID = new Integer( starsAcctInfo.getStarsCustomerAccount().getAccountID() );
        Map<Integer,Integer> contAcctIDMap = getContactAccountIDMap();
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
            int invID = liteAcctInfo.getInventories().get(i).intValue();
            
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
        children = new ArrayList<LiteStarsEnergyCompany>();
        memberLoginIDs = new ArrayList<Integer>();
        
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
                else    // childID == getLiteID()
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
    
    public synchronized List<LiteStarsEnergyCompany> getChildren() {
        if (!hierarchyLoaded)
            loadEnergyCompanyHierarchy();
        return children;
    }
    
    public synchronized List<Integer> getMemberLoginIDs() {
        if (!hierarchyLoaded)
            loadEnergyCompanyHierarchy();
        return memberLoginIDs;
    }
    
    public synchronized void clearHierarchy() {
        hierarchyLoaded = false;
    }

    private static List<Object> searchByPrimaryContactLastName(String lastName_, boolean partialMatch, List<Integer> energyCompanyIDList, boolean searchMembers) {
        if (lastName_ == null || lastName_.length() == 0) return null;
        if (energyCompanyIDList == null || energyCompanyIDList.size() == 0) return null;
        
        Date timerStart = new Date();
        String lastName = lastName_.trim();
        String firstName = null;
        int commaIndex = lastName_.indexOf(",");
        if( commaIndex > 0 )
        {
            firstName = lastName_.substring(commaIndex+1).trim();
            lastName = lastName_.substring(0, commaIndex).trim();
        }
        String sql = "SELECT map.energycompanyID, acct.AccountID, contlastname, contfirstname, " + //1-4" +
                    " acct.AccountSiteID, acct.AccountNumber, acct.CustomerID, acct.BillingAddressID, acct.AccountNotes, " + //5-9 
                    " acs.SiteInformationID, acs.SiteNumber, acs.StreetAddressID, acs.PropertyNotes, acs.CustAtHome, acs.CustomerStatus, " + //10-15
                    " si.Feeder, si.Pole, si.TransformerSize, si.ServiceVoltage, si.SubstationID, " + //16-20
                    " PrimaryContactID, CustomerTypeID, TimeZone, CustomerNumber, RateScheduleID, AltTrackNum, TemperatureUnit, " + //21-27
                    " LocationAddress1, LocationAddress2, CityName, StateCode, ZipCode, County " + //28-33
                    " FROM ECToAccountMapping map, " + CustomerAccount.TABLE_NAME + " acct, " + 
                    Customer.TABLE_NAME + " cust, " +  Contact.TABLE_NAME + " cont, " +
                    " AccountSite acs, SiteInformation si, Address adr " +  
                    " WHERE map.AccountID = acct.AccountID " +
                    " AND acct.AccountSiteID = acs.AccountSiteID " +
                    " AND acs.streetaddressID = adr.addressID " +
                    " AND acs.SiteInformationID = si.SiteID " + 
                    " AND acct.CustomerID = cust.CustomerID " + 
                    " AND cust.primarycontactid = cont.contactid " +
                    " AND UPPER(cont.contlastname) like ? " ;
                    if (firstName != null && firstName.length() > 0)
                        sql += " AND UPPER(CONTFIRSTNAME) LIKE ? ";

                    // Hey, if we have all the ECIDs available, don't bother adding this criteria!
                    if( energyCompanyIDList.size() != StarsDatabaseCache.getInstance().getAllEnergyCompanies().size());
                    {
                        sql += " AND (map.EnergyCompanyID = " + energyCompanyIDList.get(0).toString(); 
                        for (int i = 1; i < energyCompanyIDList.size(); i++)
                             sql += " OR map.EnergyCompanyID = " + energyCompanyIDList.get(i).toString();
                        sql += " ) ";
                    }
                    sql += " order by contlastname, contfirstname";                    
        
        List<Object> accountList = new ArrayList<Object>();
        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        int count = 0; 
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString( 1, lastName.toUpperCase()+(partialMatch? "%":""));
            if (firstName != null && firstName.length() > 0)
                pstmt.setString(2, firstName.toUpperCase() + "%");
            rset = pstmt.executeQuery();

            CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " After execute" );
  
            LiteStarsEnergyCompany liteStarsEC = null;
            LiteStarsCustAccountInformation liteAcctInfo = null;
            while(rset.next())
            {
                Integer energyCompanyID = new Integer(rset.getInt(1));
                liteStarsEC = StarsDatabaseCache.getInstance().getEnergyCompany(energyCompanyID.intValue());
                
                Integer accountID = new Integer(rset.getInt(2));
                liteAcctInfo = liteStarsEC.getCustAccountInfoMap().get( new Integer(accountID.intValue()) );
                if (liteAcctInfo == null){
            
                    liteAcctInfo = new LiteStarsCustAccountInformation(accountID.intValue());                
    
                    LiteCustomerAccount customerAccount = new LiteCustomerAccount( accountID.intValue() );
                    customerAccount.setAccountSiteID( rset.getInt(5));
                    customerAccount.setAccountNumber( rset.getString(6));
                    customerAccount.setCustomerID( rset.getInt(7) );
                    customerAccount.setBillingAddressID( rset.getInt(8));
                    customerAccount.setAccountNotes( rset.getString(9) );
                    liteAcctInfo.setCustomerAccount(customerAccount);
                    
                    LiteAccountSite accountSite = new LiteAccountSite( customerAccount.getAccountSiteID());
                    accountSite.setSiteInformationID( rset.getInt(10) );
                    accountSite.setSiteNumber( rset.getString(11) );
                    accountSite.setStreetAddressID( rset.getInt(12) );
                    accountSite.setPropertyNotes( rset.getString(13));
                    accountSite.setCustAtHome( rset.getString(14));
                    accountSite.setCustStatus( rset.getString(15) );
                    liteAcctInfo.setAccountSite(accountSite);
    
                    LiteSiteInformation siteInformation = new LiteSiteInformation(accountSite.getAccountSiteID() );
                    siteInformation.setFeeder( rset.getString(16) );
                    siteInformation.setPole( rset.getString(17));
                    siteInformation.setTransformerSize( rset.getString(18) );
                    siteInformation.setServiceVoltage( rset.getString(19) );
                    siteInformation.setSubstationID( rset.getInt(20) );
                    liteAcctInfo.setSiteInformation(siteInformation);
                 
                    LiteCustomer customer = new LiteCustomer(customerAccount.getCustomerID());
                    customer.setPrimaryContactID(rset.getInt(21) );
                    customer.setCustomerTypeID( rset.getInt(22) );
                    customer.setTimeZone( rset.getString(23) );
                    customer.setCustomerNumber( rset.getString(24) );
                    customer.setRateScheduleID( rset.getInt(25) );
                    customer.setAltTrackingNumber( rset.getString(26) );
                    customer.setTemperatureUnit( rset.getString(27) );
                    
                    if(customer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI)
                    {   //retrieve the CICustomerBase object instead.
                        customer = new LiteCICustomer(customer.getCustomerID());
                        customer.retrieve(CtiUtilities.getDatabaseAlias());
                    }
                    liteAcctInfo.setCustomer(customer);
                                        
                    LiteAddress address = new LiteAddress(accountSite.getStreetAddressID());
                    address.setLocationAddress1( rset.getString(28) );
                    address.setLocationAddress2( rset.getString(29) );
                    address.setCityName( rset.getString(30) );
                    address.setStateCode( rset.getString(31) );
                    address.setZipCode( rset.getString(32) );
                    address.setCounty( rset.getString(33) );
                    
                    if( count < 250)//preload only the first SearchResults.jsp page contacts
                        DaoFactory.getContactDao().getContact(customer.getPrimaryContactID());
                }                
                if (searchMembers)
                    accountList.add(new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(liteAcctInfo, liteStarsEC) );
                else
                    accountList.add(liteAcctInfo);

                count++;
                liteStarsEC.addBriefCustAccountInfo( liteAcctInfo);
            }
        }
        catch( Exception e ){
            CTILogger.error( "Error retrieving contacts with last name " + lastName_+ ": " + e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            }
            catch (java.sql.SQLException e) {}
        }

        CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " Secs for '" + lastName_ + "' Search (" + count + " AccountIDS loaded; EC=" + (energyCompanyIDList.size() == StarsDatabaseCache.getInstance().getAllEnergyCompanies().size()? "ALL" : energyCompanyIDList.toString()) + ")" );
        return accountList;
    }
    private static List<Object> searchByStreetAddress(String address_, boolean partialMatch, List<Integer> energyCompanyIDList, boolean searchMembers) {
        if (address_ == null || address_.length() == 0) return null;
        if (energyCompanyIDList == null || energyCompanyIDList.size() == 0) return null;
        
        Date timerStart = new Date();
        String address = address_.trim();
        
        String sql = "SELECT DISTINCT map.energycompanyID, acct.AccountID, " + //1-2" +
                    " acct.AccountSiteID, acct.AccountNumber, acct.CustomerID, acct.BillingAddressID, acct.AccountNotes, " + //3-7 
                    " acs.SiteInformationID, acs.SiteNumber, acs.StreetAddressID, acs.PropertyNotes, acs.CustAtHome, acs.CustomerStatus, " + //8-13
                    " si.Feeder, si.Pole, si.TransformerSize, si.ServiceVoltage, si.SubstationID, " + //14-18
                    " PrimaryContactID, CustomerTypeID, TimeZone, CustomerNumber, RateScheduleID, AltTrackNum, TemperatureUnit, " + //19-25
                    " LocationAddress1, LocationAddress2, CityName, StateCode, ZipCode, County " + //26-31
                    " FROM ECToAccountMapping map, " + CustomerAccount.TABLE_NAME + " acct, " + 
                    Customer.TABLE_NAME + " cust, AccountSite acs, SiteInformation si, Address adr " +  
                    " WHERE map.AccountID = acct.AccountID " +
                    " AND acct.AccountSiteID = acs.AccountSiteID " +
                    " AND acs.streetaddressID = adr.addressID " +
                    " AND acs.SiteInformationID = si.SiteID " + 
                    " AND acct.CustomerID = cust.CustomerID " + 
                    " AND UPPER(adr.LocationAddress1) LIKE ? ";
                    // Hey, if we have all the ECIDs available, don't bother adding this criteria!
                    if( energyCompanyIDList.size() != StarsDatabaseCache.getInstance().getAllEnergyCompanies().size());
                    {
                        sql += " AND (map.EnergyCompanyID = " + energyCompanyIDList.get(0).toString(); 
                        for (int i = 1; i < energyCompanyIDList.size(); i++)
                             sql += " OR map.EnergyCompanyID = " + energyCompanyIDList.get(i).toString();
                        sql += " ) ";
                    }
                    sql += " order by adr.LocationAddress1, adr.LocationAddress2, CityName, StateCode";                          
        
        List<Object> accountList = new ArrayList<Object>();
        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        int count = 0; 
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString( 1, address.toUpperCase()+(partialMatch? "%":""));
            rset = pstmt.executeQuery();

            CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " After execute" );
  
            LiteStarsEnergyCompany liteStarsEC = null;
            LiteStarsCustAccountInformation liteAcctInfo = null;
            while(rset.next())
            {
                Integer energyCompanyID = new Integer(rset.getInt(1));
                liteStarsEC = StarsDatabaseCache.getInstance().getEnergyCompany(energyCompanyID.intValue());
                
                Integer accountID = new Integer(rset.getInt(2));
                liteAcctInfo = liteStarsEC.getCustAccountInfoMap().get( new Integer(accountID.intValue()) );
                if (liteAcctInfo == null){
            
                    liteAcctInfo = new LiteStarsCustAccountInformation(accountID.intValue());                
    
                    LiteCustomerAccount customerAccount = new LiteCustomerAccount( accountID.intValue() );
                    customerAccount.setAccountSiteID( rset.getInt(3));
                    customerAccount.setAccountNumber( rset.getString(4));
                    customerAccount.setCustomerID( rset.getInt(5) );
                    customerAccount.setBillingAddressID( rset.getInt(6));
                    customerAccount.setAccountNotes( rset.getString(7) );
                    liteAcctInfo.setCustomerAccount(customerAccount);
                    
                    LiteAccountSite accountSite = new LiteAccountSite( customerAccount.getAccountSiteID());
                    accountSite.setSiteInformationID( rset.getInt(8) );
                    accountSite.setSiteNumber( rset.getString(9) );
                    accountSite.setStreetAddressID( rset.getInt(10) );
                    accountSite.setPropertyNotes( rset.getString(11));
                    accountSite.setCustAtHome( rset.getString(12));
                    accountSite.setCustStatus( rset.getString(13) );
                    liteAcctInfo.setAccountSite(accountSite);
    
                    LiteSiteInformation siteInformation = new LiteSiteInformation(accountSite.getAccountSiteID() );
                    siteInformation.setFeeder( rset.getString(14) );
                    siteInformation.setPole( rset.getString(15));
                    siteInformation.setTransformerSize( rset.getString(16) );
                    siteInformation.setServiceVoltage( rset.getString(17) );
                    siteInformation.setSubstationID( rset.getInt(18) );
                    liteAcctInfo.setSiteInformation(siteInformation);
                 
                    LiteCustomer customer = new LiteCustomer(customerAccount.getCustomerID());
                    customer.setPrimaryContactID(rset.getInt(19) );
                    customer.setCustomerTypeID( rset.getInt(20) );
                    customer.setTimeZone( rset.getString(21) );
                    customer.setCustomerNumber( rset.getString(22) );
                    customer.setRateScheduleID( rset.getInt(23) );
                    customer.setAltTrackingNumber( rset.getString(24) );
                    customer.setTemperatureUnit( rset.getString(25) );
                    
                    if(customer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI)
                    {   //retrieve the CICustomerBase object instead.
                        customer = new LiteCICustomer(customer.getCustomerID());
                        customer.retrieve(CtiUtilities.getDatabaseAlias());
                    }
                    liteAcctInfo.setCustomer(customer);
                                        
                    LiteAddress liteAddress = new LiteAddress(accountSite.getStreetAddressID());
                    liteAddress.setLocationAddress1( rset.getString(26) );
                    liteAddress.setLocationAddress2( rset.getString(27) );
                    liteAddress.setCityName( rset.getString(28) );
                    liteAddress.setStateCode( rset.getString(29) );
                    liteAddress.setZipCode( rset.getString(30) );
                    liteAddress.setCounty( rset.getString(31) );
                    
                    if( count < 250)//preload only the first SearchResults.jsp page contacts
                        DaoFactory.getContactDao().getContact(customer.getPrimaryContactID());
                }                
                if (searchMembers)
                    accountList.add(new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(liteAcctInfo, liteStarsEC) );
                else
                    accountList.add(liteAcctInfo);

                count++;
                liteStarsEC.addBriefCustAccountInfo( liteAcctInfo);
            }
        }
        catch( Exception e ){
            CTILogger.error( "Error retrieving contacts with last name " + address_ + ": " + e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            }
            catch (java.sql.SQLException e) {}
        }

        CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " Secs for '" + address_  + "' Search (" + count + " AccountIDS loaded; EC=" + (energyCompanyIDList.size() == StarsDatabaseCache.getInstance().getAllEnergyCompanies().size()? "ALL" : energyCompanyIDList.toString()) + ")" );
        return accountList;
    }
    
    private static List<Object> searchByAccountNumber(String accountNumber_, boolean partialMatch, List<Integer> energyCompanyIDList, boolean searchMembers) {
        if (accountNumber_ == null || accountNumber_.length() == 0) return null;
        if (energyCompanyIDList == null || energyCompanyIDList.size() == 0) return null;
        
        Date timerStart = new Date();
        String accountNumber = accountNumber_.trim();
        
//        SELECT acct.AccountID FROM ECToAccountMapping map, " + TABLE_NAME + " acct "
//        + "WHERE map.EnergyCompanyID = ? AND map.AccountID = acct.AccountID"
//        + " AND UPPER(acct.AccountNumber) LIKE UPPER(?)" +
        String sql = "SELECT DISTINCT map.energycompanyID, acct.AccountID, " + //1-2" +
                    " acct.AccountSiteID, acct.AccountNumber, acct.CustomerID, acct.BillingAddressID, acct.AccountNotes, " + //3-7 
                    " acs.SiteInformationID, acs.SiteNumber, acs.StreetAddressID, acs.PropertyNotes, acs.CustAtHome, acs.CustomerStatus, " + //8-13
                    " si.Feeder, si.Pole, si.TransformerSize, si.ServiceVoltage, si.SubstationID, " + //14-18
                    " PrimaryContactID, CustomerTypeID, TimeZone, CustomerNumber, RateScheduleID, AltTrackNum, TemperatureUnit, " + //19-25
                    " LocationAddress1, LocationAddress2, CityName, StateCode, ZipCode, County " + //26-31
                    " FROM ECToAccountMapping map, " + CustomerAccount.TABLE_NAME + " acct, " + 
                    Customer.TABLE_NAME + " cust, AccountSite acs, SiteInformation si, Address adr " +  
                    " WHERE map.AccountID = acct.AccountID " +
                    " AND acct.AccountSiteID = acs.AccountSiteID " +
                    " AND acs.streetaddressID = adr.addressID " +
                    " AND acs.SiteInformationID = si.SiteID " + 
                    " AND acct.CustomerID = cust.CustomerID " + 
                    " AND UPPER(acct.AccountNumber) LIKE ? ";
                    // Hey, if we have all the ECIDs available, don't bother adding this criteria!
                    if( energyCompanyIDList.size() != StarsDatabaseCache.getInstance().getAllEnergyCompanies().size());
                    {
                        sql += " AND (map.EnergyCompanyID = " + energyCompanyIDList.get(0).toString(); 
                        for (int i = 1; i < energyCompanyIDList.size(); i++)
                             sql += " OR map.EnergyCompanyID = " + energyCompanyIDList.get(i).toString();
                        sql += " ) ";
                    }
                    sql += " order by adr.LocationAddress1, adr.LocationAddress2, CityName, StateCode";                          
        
        List<Object> accountList = new ArrayList<Object>();
        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        int count = 0; 
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString( 1, accountNumber.toUpperCase()+(partialMatch? "%":""));
            rset = pstmt.executeQuery();

            CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " After execute" );
  
            LiteStarsEnergyCompany liteStarsEC = null;
            LiteStarsCustAccountInformation liteAcctInfo = null;
            while(rset.next())
            {
                Integer energyCompanyID = new Integer(rset.getInt(1));
                liteStarsEC = StarsDatabaseCache.getInstance().getEnergyCompany(energyCompanyID.intValue());
                
                Integer accountID = new Integer(rset.getInt(2));
                liteAcctInfo = liteStarsEC.getCustAccountInfoMap().get(accountID);
                if (liteAcctInfo == null){
            
                    liteAcctInfo = new LiteStarsCustAccountInformation(accountID.intValue());                
    
                    LiteCustomerAccount customerAccount = new LiteCustomerAccount( accountID.intValue() );
                    customerAccount.setAccountSiteID( rset.getInt(3));
                    customerAccount.setAccountNumber( rset.getString(4));
                    customerAccount.setCustomerID( rset.getInt(5) );
                    customerAccount.setBillingAddressID( rset.getInt(6));
                    customerAccount.setAccountNotes( rset.getString(7) );
                    liteAcctInfo.setCustomerAccount(customerAccount);
                    
                    LiteAccountSite accountSite = new LiteAccountSite( customerAccount.getAccountSiteID());
                    accountSite.setSiteInformationID( rset.getInt(8) );
                    accountSite.setSiteNumber( rset.getString(9) );
                    accountSite.setStreetAddressID( rset.getInt(10) );
                    accountSite.setPropertyNotes( rset.getString(11));
                    accountSite.setCustAtHome( rset.getString(12));
                    accountSite.setCustStatus( rset.getString(13) );
                    liteAcctInfo.setAccountSite(accountSite);
    
                    LiteSiteInformation siteInformation = new LiteSiteInformation(accountSite.getAccountSiteID() );
                    siteInformation.setFeeder( rset.getString(14) );
                    siteInformation.setPole( rset.getString(15));
                    siteInformation.setTransformerSize( rset.getString(16) );
                    siteInformation.setServiceVoltage( rset.getString(17) );
                    siteInformation.setSubstationID( rset.getInt(18) );
                    liteAcctInfo.setSiteInformation(siteInformation);
                 
                    LiteCustomer customer = new LiteCustomer(customerAccount.getCustomerID());
                    customer.setPrimaryContactID(rset.getInt(19) );
                    customer.setCustomerTypeID( rset.getInt(20) );
                    customer.setTimeZone( rset.getString(21) );
                    customer.setCustomerNumber( rset.getString(22) );
                    customer.setRateScheduleID( rset.getInt(23) );
                    customer.setAltTrackingNumber( rset.getString(24) );
                    customer.setTemperatureUnit( rset.getString(25) );
                    
                    if(customer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI)
                    {   //retrieve the CICustomerBase object instead.
                        customer = new LiteCICustomer(customer.getCustomerID());
                        customer.retrieve(CtiUtilities.getDatabaseAlias());
                    }
                    liteAcctInfo.setCustomer(customer);
                                        
                    LiteAddress address = new LiteAddress(accountSite.getStreetAddressID());
                    address.setLocationAddress1( rset.getString(26) );
                    address.setLocationAddress2( rset.getString(27) );
                    address.setCityName( rset.getString(28) );
                    address.setStateCode( rset.getString(29) );
                    address.setZipCode( rset.getString(30) );
                    address.setCounty( rset.getString(31) );
                    
                    if( count < 250)//preload only the first SearchResults.jsp page contacts
                        DaoFactory.getContactDao().getContact(customer.getPrimaryContactID());
                }                
                if (searchMembers)
                    accountList.add(new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(liteAcctInfo, liteStarsEC) );
                else
                    accountList.add(liteAcctInfo);

                count++;
                liteStarsEC.addBriefCustAccountInfo( liteAcctInfo);
            }
        }
        catch( Exception e ){
            CTILogger.error( "Error retrieving contacts with last name " + accountNumber_ + ": " + e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            }
            catch (java.sql.SQLException e) {}
        }

        CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " Secs for '" + accountNumber_  + "' Search (" + count + " AccountIDS loaded; EC=" + (energyCompanyIDList.size() == StarsDatabaseCache.getInstance().getAllEnergyCompanies().size()? "ALL" : energyCompanyIDList.toString()) + ")" );
        return accountList;
    }
    
    public class EnergyCompanyLatch {
        private CountDownLatch energyCompanyLatch = new CountDownLatch(3);
        private CountDownLatch accountsLatch = new CountDownLatch(1);
        private CountDownLatch inventoryLatch = new CountDownLatch(1);
        private CountDownLatch workOrderLatch = new CountDownLatch(1);
        
        /** 
         * This function blocks until the energy company CountDownLatch 
         * has reached zero or is interrupted.
         *
         * @throws InterruptedException
         */
        public void awaitEnergyCompany() throws InterruptedException{
            energyCompanyLatch.await();
        }
        
        /**
          * Checks to see if the energy company has been fully loaded.
          *  
          * @return boolean
          */
        public boolean isLoadedEnergyCompany() {
            if (energyCompanyLatch.getCount() == 0){
                return true;
            } else {
                return false;
            }
        }

        /**
         * Sets all the latches to there initial values.
         *  
         */
        public void resetAllLatches() {
           energyCompanyLatch = new CountDownLatch(3);
           accountsLatch = new CountDownLatch(1);
           inventoryLatch = new CountDownLatch(1);
           workOrderLatch = new CountDownLatch(1);
        }

        /** 
         * This function blocks until the accounts CountDownLatch 
         * has reached zero or is interrupted.
         *
         * @throws InterruptedException
         */
        public void awaitAccounts() throws InterruptedException{
            accountsLatch.await();
        }
        
        /**
         * This function counts down the accounts CountDownLatch and also the 
         * energy company CountDownLatch
         */
        public void countDownAccounts() {
            accountsLatch.countDown();
            energyCompanyLatch.countDown();
            if(starsEnergyCompany != null) {
                System.out.println("Accounts Latch Decremented EC="+starsEnergyCompany.getCompanyName());
            }
        }
        
        /**
         * Checks to see if the accounts for the given energy company
         * has been loaded.
         *  
         * @return boolean
         */
        public boolean isLoadedAccounts() {
            if (accountsLatch.getCount() == 0){
                return true;
            } else {
                return false;
            }
        }

        /**
         * Sets the account latch to its initial value and increments
         * the energy company latch.
         *  
         */
        public void resetAccountLatch() {
            accountsLatch = new CountDownLatch(1);
            int initialCount = ((Long)energyCompanyLatch.getCount()).intValue();          
            energyCompanyLatch = new CountDownLatch(initialCount + 1);
        }
        
        /** 
         * This function blocks until the inventory CountDownLatch 
         * has reached zero or is interrupted.
         *
         * @throws InterruptedException
         */
        public void awaitInventory() throws InterruptedException{
            inventoryLatch.await();
        }
       
        /**
         * This function counts down the inventory CountDownLatch and also the 
         * energy company CountDownLatch
         */
        public void countDownInventory() {
            inventoryLatch.countDown();
            energyCompanyLatch.countDown();
            if(starsEnergyCompany != null) {
                System.out.println("Inventory Latch Decremented EC="+starsEnergyCompany.getCompanyName());
            }
        }
        
        /**
         * Checks to see if the inventory for the given energy company
         * has been loaded.
         *  
         * @return boolean
         */
        public boolean isLoadedInventory() {
            if (inventoryLatch.getCount() == 0){
                return true;
            } else {
                return false;
            }
        }
        
        /**
         * Sets the account latch to its initial value and increments
         * the energy company latch.
         *  
         */
        public void resetInventoryLatch() {
            inventoryLatch = new CountDownLatch(1);
            int initialCount = ((Long)energyCompanyLatch.getCount()).intValue();          
            energyCompanyLatch = new CountDownLatch(initialCount + 1);
        }
        
        /** 
         * This function blocks until the work order CountDownLatch 
         * has reached zero or is interrupted.
         *
         * @throws InterruptedException
         */
        public void awaitWorkOrder() throws InterruptedException{
            workOrderLatch.await();
        }
        
        /**
         * This function counts down the work order CountDownLatch and also the 
         * energy company CountDownLatch
         */
        public void countDownWorkOrder() {
            workOrderLatch.countDown();
            energyCompanyLatch.countDown();
            if(starsEnergyCompany != null) {
                System.out.println("Work Order Latch Decremented EC="+starsEnergyCompany.getCompanyName());
            }
        }
        
        /**
         * Checks to see if the work order for the given energy company
         * has been loaded.
         *  
         * @return boolean
         */
        public boolean isLoadedWorkOrder() {
            if (workOrderLatch.getCount() == 0){
                return true;
            } else {
                return false;
            }
        }
        
        /**
         * Sets the account latch to its initial value and increments
         * the energy company latch.
         *  
         */
        public void resetWorkOrderLatch() {
            workOrderLatch = new CountDownLatch(1);
            int initialCount = ((Long)energyCompanyLatch.getCount()).intValue();          
            energyCompanyLatch = new CountDownLatch(initialCount + 1);
        }
    }
}