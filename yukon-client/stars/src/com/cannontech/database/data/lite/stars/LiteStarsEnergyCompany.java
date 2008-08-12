package com.cannontech.database.data.lite.stars;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

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
import com.cannontech.database.IntegerRowMapper;
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
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsRowCountDao;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.OptOutEventQueue;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.task.LoadInventoryTask;
import com.cannontech.stars.util.task.LoadWorkOrdersTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsCustomerSelectionLists;
import com.cannontech.stars.xml.serialize.StarsDefaultThermostatSchedules;
import com.cannontech.stars.xml.serialize.StarsEnergyCompany;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestion;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestions;
import com.cannontech.stars.xml.serialize.StarsServiceCompanies;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.stars.xml.serialize.StarsSubstation;
import com.cannontech.stars.xml.serialize.StarsSubstations;
import com.cannontech.stars.xml.serialize.StarsThermostatProgram;
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
    
    private static final IntegerRowMapper integerRowMapper = new IntegerRowMapper();
    
    private String name = null;
    private int primaryContactID = CtiUtilities.NONE_ZERO_ID;
    private int userID = com.cannontech.user.UserUtils.USER_DEFAULT_ID;
    
    private Map<Integer,LiteInventoryBase> inventory = null;
    private Map<Integer,LiteWorkOrderBase> workOrders = null;
    
    private List<LiteLMProgramWebPublishing> pubPrograms = null;
    private List<LiteApplianceCategory> appCategories = null;
    private List<LiteServiceCompany> serviceCompanies = null;
    private List<LiteSubstation> substations = null;
    private List<YukonSelectionList> selectionLists = null;
    private List<LiteInterviewQuestion> interviewQuestions = null;
    
    private List<Integer> routeIDs = null;
    
    private Map<Integer,LiteLMThermostatSchedule> dftThermSchedules = null;
    
    private long nextCallNo = 0;
    private long nextOrderNo = 0;
    
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
    
    // Energy company hierarchy
    private LiteStarsEnergyCompany parent = null;
    private List<LiteStarsEnergyCompany> children = null;
    private List<Integer> memberLoginIDs = null;

    private AddressDao addressDao;
    private StarsRowCountDao starsRowCountDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private StarsCustAccountInformationDao starsCustAccountInformationDao;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    protected LiteStarsEnergyCompany() {
        super();
        setLiteType( LiteTypes.ENERGY_COMPANY );
    }
    
    protected LiteStarsEnergyCompany(int companyID) {
        super();
        setLiteID( companyID );
        setLiteType( LiteTypes.ENERGY_COMPANY );
    }
    
    protected LiteStarsEnergyCompany(com.cannontech.database.db.company.EnergyCompany energyCompany) {
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
                        dbAlias, sql, new Class<?>[] { Integer.class, Integer.class } );
                
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
                            dbAlias, sql, new Class<?>[] { String.class, Integer.class, Integer.class, Integer.class } );
                    
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
                            dbAlias, sql, new Class<?>[] { String.class, Integer.class, Integer.class, Integer.class } );
                    
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
        LiteYukonUser liteYukonUser = DaoFactory.getYukonUserDao().getLiteYukonUser(getUserID());
        return DaoFactory.getAuthDao().getUserTimeZone(liteYukonUser);
    }
    
    public String getAdminEmailAddress() {
        String adminEmail = getEnergyCompanySetting( EnergyCompanyRole.ADMIN_EMAIL_ADDRESS );
        if (adminEmail == null || adminEmail.trim().length() == 0)
            adminEmail = StarsUtils.ADMIN_EMAIL_ADDRESS;
        
        return adminEmail;
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
            if ((loadInvTask == null
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
        
        inventoryLoaded = false;
        workOrdersLoaded = false;
        
        pubPrograms = null;
        inventory = null;
        appCategories = null;
        workOrders = null;
        serviceCompanies = null;
        substations = null;
        selectionLists = null;
        interviewQuestions = null;
        
        routeIDs = null;
        dftThermSchedules = null;
        
        nextCallNo = 0;
        nextOrderNo = 0;
        hierarchyLoaded = false;
        
        dftRouteID = CtiUtilities.NONE_ZERO_ID;
        operDftGroupID = com.cannontech.database.db.user.YukonGroup.EDITABLE_MIN_GROUP_ID - 1;
        
        contactAccountIDMap = null;
        
        parent = null;
        children = null;
        memberLoginIDs = null;
    }
    
    public void clearInventory() {
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
            LiteYukonGroup liteGroup = DaoFactory.getRoleDao().getGroup( Integer.parseInt(groupID) );
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
            LiteYukonGroup liteGroup = DaoFactory.getRoleDao().getGroup( Integer.parseInt(groupID) );
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
                    if (DaoFactory.getRoleDao().getRolePropValueGroup(group, AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY, null) != null) {
                        operDftGroupID = group.getGroupID();
                        return group;
                    }
                }
            }
        }
        
        return DaoFactory.getRoleDao().getGroup( operDftGroupID );
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
            
            list = Transaction.createTransaction(Transaction.INSERT, list).execute();
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
                    entry = Transaction.createTransaction(Transaction.INSERT, entry).execute();
                    
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
    
    public List<Integer> getOperatorLoginIDs() {
        String sql = "SELECT OperatorLoginID FROM EnergyCompanyOperatorLoginList WHERE EnergyCompanyID = ?";
        List<Integer> list = simpleJdbcTemplate.query(sql, integerRowMapper, getEnergyCompanyID());
        return list;
    }
    
    public LiteYukonPAObject[] getRoutes(LiteYukonPAObject[] inheritedRoutes) {
        List<LiteYukonPAObject> routeList = new ArrayList<LiteYukonPAObject>();
        List<Integer> routeIDs = getRouteIDs();
        
        synchronized (routeIDs) {
            Iterator<Integer> it = routeIDs.iterator();
            while (it.hasNext()) {
                Integer routeID = it.next();
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
                    schedule = Transaction.createTransaction( Transaction.RETRIEVE, schedule ).execute();
                    
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
    
    public synchronized LiteInventoryBase getInventoryBrief(int inventoryID, boolean autoLoad) {
        LiteInventoryBase liteInv = getInventoryMap().get(inventoryID);
        /*
         * Should never return for non-Yukon meters.  Will always go to the db.
         */
        if (liteInv != null) return liteInv;
        
        if (autoLoad) {
            liteInv = starsInventoryBaseDao.getById(inventoryID);
            
            if (liteInv != null) addInventory( liteInv );
            return liteInv;
        }
        
        return null;
    }
    
    public synchronized LiteInventoryBase getInventory(int inventoryID, boolean autoLoad) {
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
                schedule = Transaction.createTransaction( Transaction.RETRIEVE, schedule ).execute();
                
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
                order = Transaction.createTransaction( Transaction.RETRIEVE, order ).execute();
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
                
                appliance = Transaction.createTransaction( Transaction.RETRIEVE, appliance ).execute();
                
                liteApp = StarsLiteFactory.createLiteStarsAppliance( appliance, this );
                appliances.set(i, liteApp);
            }
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }
        
        return liteAcctInfo;
    }    
    
    @Deprecated
    public LiteStarsCustAccountInformation getCustAccountInformation(int accountID, boolean autoLoad) {
        LiteStarsCustAccountInformation liteAcctInfo = starsCustAccountInformationDao.getById(accountID, getEnergyCompanyID());
        return liteAcctInfo;
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
        List<LiteStarsCustAccountInformation> custAcctInfoList = starsCustAccountInformationDao.getAll(getEnergyCompanyID());
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
                return liteAcctInfo;
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

        List<Integer> allEnergyCompanyIDs = new ArrayList<Integer>();
        if( searchMembers)
            allEnergyCompanyIDs = getAllEnergyCompaniesDownward();
        else
            allEnergyCompanyIDs.add(new Integer(getLiteID()) );

        accountList = searchByAccountNumber( accountNumber, partialMatch, allEnergyCompanyIDs, searchMembers);

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
       
        final Set<Integer> accountIds = new HashSet<Integer>();
        for (int i = 0; i < invList.size(); i++) {
            LiteInventoryBase inv = (LiteInventoryBase) invList.get(i);
            int accountId = inv.getAccountID();
            if (accountId > 0) accountIds.add(accountId);
        }
        
        final Map<Integer, LiteStarsCustAccountInformation> accountMap = 
            starsCustAccountInformationDao.getByIds(accountIds, getEnergyCompanyID());
        
        for (LiteStarsCustAccountInformation account : accountMap.values()) {
            if (searchMembers)
                accountList.add( new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(account, this) );
            else
                accountList.add( account );
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
     * Search customer accounts by account alternate tracking # (AltTrackingNum from Customer table).
     * If searchMembers is true, it returns a list of Pair(LiteStarsCustAccountInformation, LiteStarsEnergyCompany);
     * otherwise it returns a list of LiteStarsCustAccountInformation.
     */
    public List<Object> searchAccountByAltTrackNo(String altTrackNo, boolean searchMembers) {
        List<Object> accountList = new ArrayList<Object>();
        
        int[] accountIDs = com.cannontech.database.db.stars.customer.CustomerAccount.searchByCustomerAltTrackingNumber( altTrackNo + "%", getLiteID() );
        if (accountIDs != null) {
            final Set<Integer> accountIds = new HashSet<Integer>(accountIDs.length);
            for (final int accountId : accountIDs) {
                accountIds.add(accountId);
            }
            
            Map<Integer,LiteStarsCustAccountInformation> accountMap = 
                starsCustAccountInformationDao.getByIds(accountIds, getEnergyCompanyID());
            for (LiteStarsCustAccountInformation liteAcctInfo : accountMap.values()) {
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
        
        int[] accountIDs = com.cannontech.database.db.stars.customer.CustomerAccount.searchByCompanyName( searchName + "%", getLiteID() );
        if (accountIDs != null) {
            final Set<Integer> accountIds = new HashSet<Integer>(accountIDs.length);
            for (final int accountId : accountIDs) {
                accountIds.add(accountId);
            }
            
            Map<Integer,LiteStarsCustAccountInformation> accountMap = 
                starsCustAccountInformationDao.getByIds(accountIds, getEnergyCompanyID());
            for (LiteStarsCustAccountInformation liteAcctInfo : accountMap.values()) {
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
        
        int[] accountIDs = com.cannontech.database.db.stars.customer.CustomerAccount.searchByMapNumber( mapNo + "%", getLiteID() );
        if (accountIDs != null) {
            final Set<Integer> accountIds = new HashSet<Integer>(accountIDs.length);
            for (final int accountId : accountIDs) {
                accountIds.add(accountId);
            }
            
            final Map<Integer,LiteStarsCustAccountInformation> accountMap = 
                starsCustAccountInformationDao.getByIds(accountIds, getEnergyCompanyID());
            for (final LiteStarsCustAccountInformation liteAcctInfo : accountMap.values()) {
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

        List<Integer> allEnergyCompanyIDs = new ArrayList<Integer>();
        if( searchMembers)
            allEnergyCompanyIDs = getAllEnergyCompaniesDownward();
        else
            allEnergyCompanyIDs.add(new Integer(getLiteID()) );

        accountList = searchByStreetAddress( address, partialMatch, allEnergyCompanyIDs, searchMembers);

        return accountList;
    }
    
    @SuppressWarnings("unchecked")
    public List<Object> searchAccountByOrderNo(String orderNo, boolean searchMembers) {
        List<Object> orderList = searchWorkOrderByOrderNo( orderNo, false );
        List<Object> accountList = new ArrayList<Object>();

        final Set<Integer> accountIds = new HashSet<Integer>();
        
        for (final Object obj : orderList) {
            LiteWorkOrderBase liteOrder = null;
            
            if (obj instanceof LiteWorkOrderBase) {
                liteOrder = (LiteWorkOrderBase) obj;
            }
            if (obj instanceof Pair) {
                Pair<LiteWorkOrderBase,LiteStarsEnergyCompany> pair = (Pair<LiteWorkOrderBase,LiteStarsEnergyCompany>) obj;
                liteOrder = (LiteWorkOrderBase) pair.getFirst();
            }
            
            if (liteOrder != null) {
                int accountId = liteOrder.getAccountID();
                if (accountId > 0) accountIds.add(accountId);
            }
        }
        
        final Map<Integer, LiteStarsCustAccountInformation> accountMap = 
            starsCustAccountInformationDao.getByIds(accountIds, getEnergyCompanyID());
        
        for (final LiteStarsCustAccountInformation account : accountMap.values()) {
            if (searchMembers) {
                accountList.add( new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(account, this) );
            } else {
                accountList.add(account);
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

        int[] accountIDs = com.cannontech.database.db.stars.customer.CustomerAccount.searchByPrimaryContactIDs( contactIDs, getLiteID() );
        if (accountIDs != null) {
            Set<Integer> accountIdSet = new HashSet<Integer>(accountIDs.length);
            for (final int accountId : accountIDs) {
                accountIdSet.add(accountId);
            }
            
            Map<Integer, LiteStarsCustAccountInformation> accountsMap = 
                starsCustAccountInformationDao.getByIds(accountIdSet, getEnergyCompanyID());
            
            for (final LiteStarsCustAccountInformation account : accountsMap.values()) {
                if (searchMembers) {
                    accountList.add(
                        new Pair<LiteStarsCustAccountInformation, LiteStarsEnergyCompany>(account, this));
                } else {
                    accountList.add(account);
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

        List<Integer> allEnergyCompanyIDs = new ArrayList<Integer>();
        if( searchMembers)
            allEnergyCompanyIDs = getAllEnergyCompaniesDownward();
        else
            allEnergyCompanyIDs.add(new Integer(getLiteID()) );

        accountList = searchByPrimaryContactLastName( lastName, partialMatch, allEnergyCompanyIDs, searchMembers);

        return accountList;
    }
    /* The following methods are only used when SOAPClient exists locally */
    
    public StarsEnergyCompanySettings getStarsEnergyCompanySettings(StarsYukonUser user) {
        if (StarsUtils.isOperator(user.getYukonUser())) {
            StarsEnergyCompanySettings starsOperECSettings = new StarsEnergyCompanySettings();
            starsOperECSettings.setEnergyCompanyID( user.getEnergyCompanyID() );
            starsOperECSettings.setStarsEnergyCompany( getStarsEnergyCompany() );
            starsOperECSettings.setStarsEnrollmentPrograms( getStarsEnrollmentPrograms() );
            starsOperECSettings.setStarsCustomerSelectionLists( getStarsCustomerSelectionLists(user) );
            starsOperECSettings.setStarsServiceCompanies( getStarsServiceCompanies() );
            starsOperECSettings.setStarsSubstations( getStarsSubstations() );
            starsOperECSettings.setStarsExitInterviewQuestions( getStarsExitInterviewQuestions() );
            starsOperECSettings.setStarsDefaultThermostatSchedules( getStarsDefaultThermostatSchedules() );
            return starsOperECSettings;
        }
        else if (StarsUtils.isResidentialCustomer(user.getYukonUser())) {
            StarsEnergyCompanySettings starsCustECSettings = new StarsEnergyCompanySettings();
            starsCustECSettings.setEnergyCompanyID( user.getEnergyCompanyID() );
            starsCustECSettings.setStarsEnergyCompany( getStarsEnergyCompany() );
            starsCustECSettings.setStarsEnrollmentPrograms( getStarsEnrollmentPrograms() );
            starsCustECSettings.setStarsCustomerSelectionLists( getStarsCustomerSelectionLists(user) );
            starsCustECSettings.setStarsExitInterviewQuestions( getStarsExitInterviewQuestions() );
            starsCustECSettings.setStarsDefaultThermostatSchedules( getStarsDefaultThermostatSchedules() );
            return starsCustECSettings;
        }
        
        return null;
    }
    
    public StarsEnergyCompany getStarsEnergyCompany() {
        StarsEnergyCompany starsEnergyCompany = new StarsEnergyCompany();
        StarsLiteFactory.setStarsEnergyCompany( starsEnergyCompany, this );
        return starsEnergyCompany;
    }
    
    private StarsCustSelectionList getStarsCustSelectionList(String listName) {
        StarsCustSelectionList starsList = null;
        YukonSelectionList yukonList = getYukonSelectionList( listName );
        if (yukonList != null) {
            starsList = StarsLiteFactory.createStarsCustSelectionList( yukonList );
        }
        return starsList;
    }
    
    public synchronized StarsCustomerSelectionLists getStarsCustomerSelectionLists(StarsYukonUser starsUser) {
        if (StarsUtils.isOperator( starsUser.getYukonUser() )) {
            StarsCustomerSelectionLists starsOperSelLists = new StarsCustomerSelectionLists();
            
            for (int i = 0; i < OPERATOR_SELECTION_LISTS.length; i++) {
                StarsCustSelectionList list = getStarsCustSelectionList(OPERATOR_SELECTION_LISTS[i]);
                if (list != null) starsOperSelLists.addStarsCustSelectionList( list );
            }
            
            return starsOperSelLists;
        }
        else if (StarsUtils.isResidentialCustomer( starsUser.getYukonUser() )) {
            StarsCustomerSelectionLists starsCustSelLists = new StarsCustomerSelectionLists();
            // Currently the consumer side only need chance of control and opt out period list
            StarsCustSelectionList list = getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL);
            if (list != null) starsCustSelLists.addStarsCustSelectionList( list );
            list = getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD);
            if (list != null) starsCustSelLists.addStarsCustSelectionList( list );
            return starsCustSelLists;
        }
        
        return null;
    }
    
    public synchronized StarsEnrollmentPrograms getStarsEnrollmentPrograms() {
        StarsEnrollmentPrograms starsEnrPrograms = new StarsEnrollmentPrograms();
        StarsLiteFactory.setStarsEnrollmentPrograms( starsEnrPrograms, getAllApplianceCategories(), this );
        return starsEnrPrograms;
    }
    
    public synchronized StarsServiceCompanies getStarsServiceCompanies() {
        StarsServiceCompanies starsServCompanies = new StarsServiceCompanies();
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

        return starsServCompanies;
    }
    
    public synchronized StarsSubstations getStarsSubstations() {
        StarsSubstations starsSubstations = new StarsSubstations();
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
        return starsSubstations;
    }
    
    public synchronized StarsExitInterviewQuestions getStarsExitInterviewQuestions() {
        StarsExitInterviewQuestions starsExitQuestions = new StarsExitInterviewQuestions();

        int exitQType = getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_QUE_TYPE_EXIT).getEntryID();
        LiteInterviewQuestion[] liteQuestions = getInterviewQuestions( exitQType );
        for (int i = 0; i < liteQuestions.length; i++) {
            StarsExitInterviewQuestion starsQuestion = new StarsExitInterviewQuestion();
            StarsLiteFactory.setStarsQuestionAnswer( starsQuestion, liteQuestions[i] );
            starsExitQuestions.addStarsExitInterviewQuestion( starsQuestion );
        }

        return starsExitQuestions;
    }
    
    public synchronized StarsDefaultThermostatSchedules getStarsDefaultThermostatSchedules() {
        boolean hasBasic = false;
        boolean hasEpro = false;
        boolean hasComm = false;
        boolean hasPump = false;
        boolean hasUPro = false;
        
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
            else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_UTILITYPRO)
                hasUPro = true;
        }
        
        StarsDefaultThermostatSchedules starsDftThermSchedules = new StarsDefaultThermostatSchedules();
        
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
        if (hasUPro) {
            StarsThermostatProgram starsThermProg = StarsLiteFactory.createStarsThermostatProgram(
                    getDefaultThermostatSchedule(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_UTILITYPRO), this );
            starsDftThermSchedules.addStarsThermostatProgram( starsThermProg );
        }
        
        return starsDftThermSchedules;
    }
    
    public StarsCustAccountInformation getStarsCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo) {
            StarsCustAccountInformation starsAcctInfo = StarsLiteFactory.createStarsCustAccountInformation( liteAcctInfo, this, true );
            return starsAcctInfo;
    }
    
    public StarsCustAccountInformation getStarsCustAccountInformation(int accountID, boolean autoLoad) {
        LiteStarsCustAccountInformation liteAcctInfo = starsCustAccountInformationDao.getById(accountID, getEnergyCompanyID());
        if (liteAcctInfo != null) return getStarsCustAccountInformation( liteAcctInfo );
        return null;
    }
    
    public StarsCustAccountInformation getStarsCustAccountInformation(int accountID) {
        return getStarsCustAccountInformation( accountID, false );
    }
    
    public synchronized Map<Integer,Integer> getContactAccountIDMap() {
        if (contactAccountIDMap == null)
            contactAccountIDMap = new Hashtable<Integer,Integer>();
        
        return contactAccountIDMap;
    }
    
    public void updateThermostatSettings(LiteStarsCustAccountInformation liteAcctInfo) {
        for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
            int invID = liteAcctInfo.getInventories().get(i).intValue();
            
            LiteInventoryBase liteInv = getInventory( invID, true );
            if (!(liteInv instanceof LiteStarsLMHardware)) continue;
            
            LiteStarsLMHardware liteHw = (LiteStarsLMHardware) liteInv;
            if (!liteHw.isTwoWayThermostat()) continue;
            
            LiteStarsThermostatSettings liteSettings = liteHw.getThermostatSettings();
            liteSettings.updateThermostatSettings( liteHw, this );
        }
    }
    
    private void loadEnergyCompanyHierarchy() {
        parent = null;
        memberLoginIDs = new ArrayList<Integer>();
        children = new ArrayList<LiteStarsEnergyCompany>();
        
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

    private List<Object> searchByPrimaryContactLastName(String lastName_, boolean partialMatch, List<Integer> energyCompanyIDList, boolean searchMembers) {
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
  
            while(rset.next()) {
                Integer energyCompanyID = new Integer(rset.getInt(1));
                final LiteStarsEnergyCompany liteStarsEC = StarsDatabaseCache.getInstance().getEnergyCompany(energyCompanyID.intValue());
                
                Integer accountID = new Integer(rset.getInt(2));

                final LiteStarsCustAccountInformation liteAcctInfo = new LiteStarsCustAccountInformation(accountID.intValue(), energyCompanyID);                

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

                if (searchMembers)
                    accountList.add(new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(liteAcctInfo, liteStarsEC) );
                else
                    accountList.add(liteAcctInfo);

                count++;
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
            
                liteAcctInfo = new LiteStarsCustAccountInformation(accountID.intValue(), energyCompanyID);                

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

                if (searchMembers)
                    accountList.add(new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(liteAcctInfo, liteStarsEC) );
                else
                    accountList.add(liteAcctInfo);

                count++;
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
            
                liteAcctInfo = new LiteStarsCustAccountInformation(accountID.intValue(), energyCompanyID);                

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
                                
                if (searchMembers)
                    accountList.add(new Pair<LiteStarsCustAccountInformation,LiteStarsEnergyCompany>(liteAcctInfo, liteStarsEC) );
                else
                    accountList.add(liteAcctInfo);

                count++;
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
    
    void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }
    
    void setStarsRowCountDao(StarsRowCountDao starsRowCountDao) {
        this.starsRowCountDao = starsRowCountDao;
    }
    
    void setStarsInventoryBaseDao(
            StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }
    
    void setStarsCustAccountInformationDao(
            StarsCustAccountInformationDao starsCustAccountInformationDao) {
        this.starsCustAccountInformationDao = starsCustAccountInformationDao;
    }
    
    void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
}