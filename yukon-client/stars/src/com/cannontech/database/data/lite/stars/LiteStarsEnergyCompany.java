package com.cannontech.database.data.lite.stars;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.joda.time.DateTimeZone;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.TransactionType;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.customer.Customer;
import com.cannontech.database.db.macro.MacroTypes;
import com.cannontech.database.db.stars.ECToGenericMapping;
import com.cannontech.database.db.stars.LMProgramWebPublishing;
import com.cannontech.database.db.stars.appliance.ApplianceCategory;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.database.db.user.YukonGroup;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.core.dao.WarehouseDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsCustomerSelectionLists;
import com.cannontech.stars.xml.serialize.StarsEnergyCompany;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsServiceCompanies;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.stars.xml.serialize.StarsSubstation;
import com.cannontech.stars.xml.serialize.StarsSubstations;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class LiteStarsEnergyCompany extends LiteBase implements YukonEnergyCompany {
    private AddressDao addressDao;
    private DBPersistentDao dbPersistentDao;
    private ECMappingDao ecMappingDao;
    private EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao;
    private PaoPermissionService paoPermissionService;
    private StarsCustAccountInformationDao starsCustAccountInformationDao;
    private StarsSearchDao starsSearchDao;
    private StarsWorkOrderBaseDao starsWorkOrderBaseDao;
    private SystemDateFormattingService systemDateFormattingService;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private YukonListDao yukonListDao;
    private YukonGroupDao yukonGroupDao;
    
	private final static long serialVersionUID = 1L;
	
    public static final int FAKE_LIST_ID = -9999;   // Magic number for YukonSelectionList ID, used for substation and service company list
    public static final int INVALID_ROUTE_ID = -1;  // Mark that a valid default route id is not found, and prevent futher attempts

    private static final String[] OPERATOR_SELECTION_LISTS = {
        YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE,
        YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL,
        YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE,
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
    private LiteYukonUser user = null;

    private List<LiteServiceCompany> serviceCompanies = null;
    private volatile List<Warehouse> warehouses = null;
    private List<LiteSubstation> substations = null;

    private final Map<Integer, Integer> programIdToAppCatIdMap = new ConcurrentHashMap<Integer, Integer>();
    private final Map<Integer, LiteApplianceCategory> appCategoryMap = 
    	new ConcurrentHashMap<Integer, LiteApplianceCategory>();
    
    private List<Integer> routeIDs = null;
    
    private long nextCallNo = 0;
    private long nextOrderNo = 0;
    
    private volatile int dftRouteID = CtiUtilities.NONE_ZERO_ID;
    private int operDftGroupID = com.cannontech.database.db.user.YukonGroup.EDITABLE_MIN_GROUP_ID - 1;
    
    private class EnergyCompanyHierarchy {
        // Energy company hierarchy
        LiteStarsEnergyCompany parent = null;
        final List<LiteStarsEnergyCompany> children = new ArrayList<LiteStarsEnergyCompany>();
        final List<Integer> memberLoginIDs = new ArrayList<Integer>();
    }
    
    private volatile EnergyCompanyHierarchy energyCompanyHierarchy = null;

    private WarehouseDao warehouseDao;
    
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
        setUser(DaoFactory.getYukonUserDao().getLiteYukonUser(energyCompany.getUserID()));
    }
    
    public void initialize() {
    	initApplianceCategories();
    }
    
    public int getEnergyCompanyId() {
        return getLiteID();
    }

    @Deprecated
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
     * Returns the user.
     * @return LiteYukonUser
     */
    public LiteYukonUser getUser() {
        return user;
    }

    /**
     * Sets the user.
     * @param user The user to set
     */
    public void setUser(LiteYukonUser user) {
        this.user = user;
    }

    @Deprecated
    public int getDefaultRouteID() {
        return getDefaultRouteId();
    }
    
    /**
     * This method gets the default route.  It will either use the cached value stored in this class 
     * or try to figure out the default route from the database if the routeId is CtiUtilities.NONE_ZERO_ID(0).
     */
    public int getDefaultRouteId() {
        if (dftRouteID == INVALID_ROUTE_ID) return dftRouteID;
        
        if (dftRouteID == CtiUtilities.NONE_ZERO_ID) {
            
            Set<Integer> permittedPaoIDs = paoPermissionService.getPaoIdsForUserPermission(user, Permission.DEFAULT_ROUTE);
            if(! permittedPaoIDs.isEmpty()) {
                
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT LMGEC.LMGroupId");
                sql.append("FROM LMGroupExpressCom LMGEC");
                sql.append("JOIN GenericMacro GM ON GM.ChildId = LMGEC.LMGroupId");
                sql.append("WHERE GM.MacroType").eq(MacroTypes.GROUP);
                sql.append("  AND LMGEC.SerialNumber = '0'");
                sql.append("  AND GM.OwnerId").in(permittedPaoIDs);
                
                List<Integer> serialGroupIds = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
                
                // get a serial group whose serial number is set to 0, the route id of this group is the default route id
                if (serialGroupIds != null && serialGroupIds.size() > 0) {
                    
                    // get versacom serial groups
                    SqlStatementBuilder sql2 = new SqlStatementBuilder();
                    sql2.append("SELECT LMGV.RouteId");
                    sql2.append("FROM YukonPAObject PAO");
                    sql2.append("JOIN LMGroupVersacom LMGV ON PAO.PAObjectId = LMGV.DeviceId");

                    SqlFragmentCollection lmGroupVersacomDeviceIdOred = SqlFragmentCollection.newOrCollection();
                    for (int serialGroupId : serialGroupIds) {
                        SqlStatementBuilder lmGroupVersecomSql = new SqlStatementBuilder();
                        lmGroupVersecomSql.append("LMGV.DeviceId").eq(serialGroupId);
                        lmGroupVersacomDeviceIdOred.add(lmGroupVersecomSql);
                    }
                    sql2.append("WHERE").appendFragment(lmGroupVersacomDeviceIdOred);
                    
                    List<Integer> versacomDefaultRouteIds = yukonJdbcTemplate.query(sql2, new IntegerRowMapper());
                    if (versacomDefaultRouteIds != null && versacomDefaultRouteIds.size() > 0) {
                        dftRouteID = versacomDefaultRouteIds.get(0);
                        return dftRouteID;
                    }
                    
                    // get expresscom serial groups 
                    SqlStatementBuilder sql3 = new SqlStatementBuilder();
                    sql3.append("SELECT LMGE.RouteId");
                    sql3.append("FROM YukonPAObject PAO");
                    sql3.append("JOIN LMGroupExpresscom LMGE ON PAO.PAObjectId = LMGE.LMGroupId");
                    
                    SqlFragmentCollection lmGroupExpresscomLmGroupIdOred = SqlFragmentCollection.newOrCollection();
                    for (int serialGroupId : serialGroupIds) {
                        SqlStatementBuilder lmGroupExpresscomSql = new SqlStatementBuilder();
                        lmGroupExpresscomSql.append("LMGE.LmGroupId").eq(serialGroupId);
                        lmGroupExpresscomLmGroupIdOred.add(lmGroupExpresscomSql);
                    }
                    sql3.append("WHERE").appendFragment(lmGroupExpresscomLmGroupIdOred);
                    
                    List<Integer> expresscomDefaultRouteIds = yukonJdbcTemplate.query(sql3, new IntegerRowMapper());
                    if (expresscomDefaultRouteIds != null && expresscomDefaultRouteIds.size() > 0) {
                        dftRouteID = expresscomDefaultRouteIds.get(0);
                        return dftRouteID;
                    }
                }
            }
            
            CTILogger.info( "WARNING: no default route id found for energy company #" + getLiteID() );
            dftRouteID = INVALID_ROUTE_ID;
        }
        
        return dftRouteID;
    }
    
    /**
     * This method resets the default routeId for the given energy company.  This will cause the
     * get method to look up the value next time it is needed instead of using the cached value.
     */
    public void resetDefaultRouteId() {
        dftRouteID = CtiUtilities.NONE_ZERO_ID;
    }
    
    /**
     *  This method clears out both of the cache objects held by the appliance categories
     */
    public void resetApplianceCategoryList() {
        programIdToAppCatIdMap.clear();
        appCategoryMap.clear();
        initApplianceCategories();
    }
    
    public TimeZone getDefaultTimeZone() {
        TimeZone timeZone;
        String timeZoneStr = energyCompanyRolePropertyDao.getPropertyStringValue(YukonRoleProperty.ENERGY_COMPANY_DEFAULT_TIME_ZONE, this);
        
        if (StringUtils.isNotBlank(timeZoneStr)) {
            try {
                timeZone = CtiUtilities.getValidTimeZone(timeZoneStr);
                CTILogger.debug("Energy Company Role Default TimeZone found: " + timeZone.getDisplayName());
            } catch (BadConfigurationException e) {
                throw new BadConfigurationException (e.getMessage() + ". Invalid value in Energy Company Role Default TimeZone property.");
            }
        } else {
            timeZone = systemDateFormattingService.getSystemTimeZone();
        }
        return timeZone;
    }
    
    public DateTimeZone getDefaultDateTimeZone() {
        return DateTimeZone.forTimeZone(getDefaultTimeZone()); 
    }
    
    public String getAdminEmailAddress() {
        String adminEmail = getEnergyCompanySetting( EnergyCompanyRole.ADMIN_EMAIL_ADDRESS );
        if (adminEmail == null || adminEmail.trim().length() == 0)
            adminEmail = StarsUtils.ADMIN_EMAIL_ADDRESS;
        
        return adminEmail;
    }
    
    /**
     * @Deprecated The energyCompanyRolePropertyDao should be used instead of this method.
     */
    @Deprecated
    public String getEnergyCompanySetting(int rolePropertyID) {
        String value = DaoFactory.getAuthDao().getRolePropertyValue(user, rolePropertyID);
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
        if (operDftGroupID < YukonGroup.EDITABLE_MIN_GROUP_ID) {

            List<LiteYukonGroup> groups = yukonGroupDao.getGroupsForUser(user);
            for (int i = 0; i < groups.size(); i++) {
                LiteYukonGroup group = groups.get(i);
                if (DaoFactory.getRoleDao().getRolePropValueGroup(group, AdministratorRole.ADMIN_EDIT_ENERGY_COMPANY, null) != null) {
                    operDftGroupID = group.getGroupID();
                    return group;
                }
            }
        }

        return yukonGroupDao.getLiteYukonGroup(operDftGroupID);
    }
    
    /**
     * Get programs published by this energy company only
     */
    public Iterable<LiteLMProgramWebPublishing> getPrograms() {
    	Iterable<LiteLMProgramWebPublishing> programs = Collections.emptyList();
    	
    	Iterable<LiteApplianceCategory> categories = getApplianceCategories();
        for(LiteApplianceCategory category : categories) {
            Iterable<LiteLMProgramWebPublishing> categoryPrograms = category.getPublishedPrograms();
            programs = Iterables.concat(programs, categoryPrograms);
        }
        
        return programs;
    }
    
    /**
     * Get all published programs including those inherited from the parent company
     */
    public Iterable<LiteLMProgramWebPublishing> getAllPrograms() {
        Iterable<LiteLMProgramWebPublishing> pubProgs = getPrograms();
        
        if (getParent() != null) {
        	pubProgs = Iterables.concat(pubProgs, getParent().getAllPrograms());
        }
        
        return pubProgs;
    }
    
    /**
     * Get appliance categories created in this energy company only
     */
    public Iterable<LiteApplianceCategory> getApplianceCategories() {
        return appCategoryMap.values();
    }
    
    /**
     * Get all appliance categories including those inherited from the parent company if allowed to do so
     */
    public Iterable<LiteApplianceCategory> getAllApplianceCategories() {
        
    	Iterable<LiteApplianceCategory> allApplianceCategories = getApplianceCategories();
        
        boolean inheritCats = 
        	energyCompanyRolePropertyDao.checkProperty(YukonRoleProperty.INHERIT_PARENT_APP_CATS, this);
        
        if (getParent() != null && inheritCats) {
            Iterable<LiteApplianceCategory> parentCategories = 
            	getParent().getAllApplianceCategories();
            allApplianceCategories = Iterables.concat(allApplianceCategories, parentCategories);
        }
        
        return allApplianceCategories;
    }

    public YukonSelectionList getYukonSelectionList(String listName, boolean useInherited, boolean useDefault) {
        
    	YukonSelectionList yukonSelectionList = 
    	    yukonListDao.findSelectionListByEnergyCompanyIdAndListName(getEnergyCompanyId(), listName);
    	if(yukonSelectionList != null) {
    		return yukonSelectionList;
    	}
        
        // If parent company exists, then search the parent company for the list
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
    
    public List<HardwareType> getAvailableThermostatTypes() {
    	List<HardwareType> typeList = new ArrayList<HardwareType>();
    	
    	YukonSelectionList selectionList = this.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE);
    	for(YukonListEntry entry : selectionList.getYukonListEntries()) {
    		HardwareType type = HardwareType.valueOf(entry.getYukonDefID());
    		if(type.isThermostat()) {
	    		typeList.add(type);
    		}
    	}
    	
    	return typeList;
    	
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
            listDB.setEnergyCompanyId( getEnergyCompanyId() );
            
            dbPersistentDao.performDBChange(list, TransactionType.INSERT);
            listDB = list.getYukonSelectionList();
            
            YukonSelectionList cList = new YukonSelectionList();
            StarsLiteFactory.setConstantYukonSelectionList(cList, listDB);
            
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
                    dbPersistentDao.performDBChange(entry, TransactionType.INSERT);
                    
                    YukonListEntry cEntry = new YukonListEntry();
                    StarsLiteFactory.setConstantYukonListEntry( cEntry, entry );
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
                    com.cannontech.database.data.stars.report.ServiceCompany.retrieveAllServiceCompanies( getEnergyCompanyId() );
            
            serviceCompanies = new ArrayList<LiteServiceCompany>();
            for (int i = 0; i < companies.length; i++)
                serviceCompanies.add( (LiteServiceCompany)StarsLiteFactory.createLite(companies[i]) );
            
            CTILogger.info( "All service companies loaded for energy company #" + getEnergyCompanyId() );
        }
        
        return serviceCompanies;
    }
    
    /**
     * Returns list of inherited and energy company's own service companies,
     * that are allowed to be assigned to a device, work order etc
     * 
     * @return A copy of the serviceCompany list 
     */
    public List<LiteServiceCompany> getAllServiceCompanies() {
        List<LiteServiceCompany> allCompanies = new ArrayList<LiteServiceCompany>();
        if (getParent() != null) {
            allCompanies.addAll(getParent().getAllServiceCompanies());
        }
        List<LiteServiceCompany> companies = getServiceCompanies();
        synchronized (companies) {
            allCompanies.addAll(companies);
        }
        
        return allCompanies;
    }

    /**
     * Returns list of inherited, energy company's own and its descendants' service companies,
     * used to search/display list of devices, work orders etc for viewing purposes.
     * 
     * @return A copy of the serviceCompany list 
     */
    public List<LiteServiceCompany> getAllServiceCompaniesUpDown() {
        List<LiteServiceCompany> allCompanies = new ArrayList<LiteServiceCompany>();
        if (getParent() != null) {
            allCompanies.addAll(getParent().getAllServiceCompanies());
        }
        List<LiteServiceCompany> companies = getServiceCompanies();
        synchronized (companies) {
            allCompanies.addAll(companies);
        }
        for (LiteStarsEnergyCompany child : getChildren()) {
            allCompanies.addAll(child.getAllServiceCompaniesDownward());
        }
        
        return allCompanies;
    }
    
    public List<LiteServiceCompany> getAllServiceCompaniesDownward() 
    {
        List<LiteServiceCompany> descCompanies = new ArrayList<LiteServiceCompany>();
        List<LiteServiceCompany> companies = getServiceCompanies();
        synchronized (companies) {
            descCompanies.addAll(companies);
        }        
        for (LiteStarsEnergyCompany child : getChildren()) {
            descCompanies.addAll(child.getAllServiceCompaniesDownward());
        }
        
        return descCompanies;
    }
    
    /**
     * This method returns all the energy companies an energy company can access including
     * itself.
     */
    public List<Integer> getAllEnergyCompaniesDownward()
    {
        List<Integer> descEnergyCompanies = new ArrayList<Integer>();
        descEnergyCompanies.add(new Integer(getLiteID()));
        for (final LiteStarsEnergyCompany company : getChildren()) {
            List<Integer> memberList = company.getAllEnergyCompaniesDownward();
            descEnergyCompanies.addAll( memberList );
        }
        return descEnergyCompanies;
    }

    /**
     * Returns list of energy company's own warehouses,
     * that are allowed to be assigned to a device, shipment, work order etc
     * 
     * @return A copy of the warehouse list 
     */
    public List<Warehouse> getWarehouses() {
        List<Warehouse> tempWarehouses = warehouses;
        if (tempWarehouses == null) {
            tempWarehouses = new ArrayList<Warehouse>();
            tempWarehouses = warehouseDao.getAllWarehousesForEnergyCompanyId(getEnergyCompanyId());
            CTILogger.info( "All Warehouses loaded for energy company #" + getEnergyCompanyId() );            

            warehouses = tempWarehouses;
        }
        return Collections.unmodifiableList(tempWarehouses);        
    }
    
    /**
     * Returns list of energy company's own and its descendants' warehouses,
     * used to search/display list of devices, work orders etc for viewing purposes.
     * 
     * @return A copy of the warehouse list 
     */
    public List<Warehouse> getAllWarehousesDownward() 
    {
        List<Warehouse> descWarehouses = new ArrayList<Warehouse>(); 
        descWarehouses.addAll(getWarehouses());
                 
        for (LiteStarsEnergyCompany child : getChildren()) {
            descWarehouses.addAll(child.getAllWarehousesDownward());
        }
        
        return descWarehouses;
    }
    
    public synchronized List<LiteSubstation> getSubstations() {
        if (substations == null) {
            com.cannontech.database.db.stars.Substation[] subs =
                    com.cannontech.database.db.stars.Substation.getAllSubstations( getEnergyCompanyId() );
            
            substations = Collections.synchronizedList(new ArrayList<LiteSubstation>());
            for (int i = 0; i < subs.length; i++) {
                LiteSubstation liteSub = (LiteSubstation) StarsLiteFactory.createLite(subs[i]);
                substations.add( liteSub );
            }
            
            CTILogger.info( "All substations loaded for energy company #" + getEnergyCompanyId() );
        }
        
        return substations;
    }
    
    /**
     * @return A copy of the substations list
     */
    public List<LiteSubstation> getAllSubstations() {
        List<LiteSubstation> allSubstations = new ArrayList<LiteSubstation>();
        if (getParent() != null) {
            allSubstations.addAll(getParent().getAllSubstations() );
        }
        List<LiteSubstation> substations = getSubstations();
        synchronized (substations) {
            allSubstations.addAll(substations);
        }
        
        return allSubstations;
    }
    
    public List<Integer> getOperatorLoginIDs() {
        String sql = "SELECT OperatorLoginID FROM EnergyCompanyOperatorLoginList WHERE EnergyCompanyID = ?";
        List<Integer> list = yukonJdbcTemplate.query(sql, integerRowMapper, getEnergyCompanyId());
        return list;
    }
    
    public synchronized LiteYukonPAObject[] getRoutes(LiteYukonPAObject[] inheritedRoutes) {
        List<LiteYukonPAObject> routeList = new ArrayList<LiteYukonPAObject>();
        List<Integer> routeIDs = getRouteIDs();
        
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
                ecMappingDao.deleteECToRouteMapping(getEnergyCompanyId(), routeID);
                it.remove();
                
                dbPersistentDao.processDatabaseChange(DbChangeType.DELETE, 
                                                      DbChangeCategory.ENERGY_COMPANY_ROUTES,
                                                      getEnergyCompanyId());
            } else {
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
            routeIDs = Collections.synchronizedList(new ArrayList<Integer>());;
            
            routeIDs = ecMappingDao.getRouteIdsForEnergyCompanyId(getEnergyCompanyId());
            List<Integer> listCopy = Lists.newArrayList(routeIDs);  /* Need this until route event listener stops blowing away routeIDs */
            if (getDefaultRouteId() > 0) {
                // Make sure the default route ID is in the list
                Integer dftRouteID = new Integer( getDefaultRouteId() );
                
                if (!routeIDs.contains( dftRouteID )) {
                    routeIDs.add(dftRouteID);
                    listCopy.add(dftRouteID);

                    ecMappingDao.addEcToRouteMapping(getEnergyCompanyId(), dftRouteID);
                    dbPersistentDao.processDatabaseChange(DbChangeType.ADD, 
                                                          DbChangeCategory.ENERGY_COMPANY_ROUTES,
                                                          getEnergyCompanyId());
                }
            }
            return listCopy;
        }
        
        return routeIDs;
    }
    
    /**
     * Find the next to the largest call number with pattern "CTI#(NUMBER)", e.g. "CTI#10"
     */
    public synchronized String getNextCallNumber() {
        if (nextCallNo == 0) {
            String sql = "SELECT CallNumber FROM CallReportBase call, ECToCallReportMapping map "
                       + "WHERE map.EnergyCompanyID = " + getEnergyCompanyId() + " AND call.CallID = map.CallReportID";
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
                       + "WHERE map.EnergyCompanyID = " + getEnergyCompanyId() + " AND service.OrderID = map.WorkOrderID";
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
    
    public LiteApplianceCategory getApplianceCategory(int applianceCategoryID) {
        Iterable<LiteApplianceCategory> allCategories = getAllApplianceCategories();
        for (LiteApplianceCategory category : allCategories) {
            if (category.getApplianceCategoryID() == applianceCategoryID) {
                return category;
            }
        }
        return null;
    }
    
    public boolean isApplianceCategoryInherited(int applianceCategoryID) {
        return (appCategoryMap.get(applianceCategoryID) == null) ? true : false;
    }
    
    public void addApplianceCategory(LiteApplianceCategory appCat) {
    	Validate.notNull(appCat, "Appliance category cannot be null");
        appCategoryMap.put(appCat.getApplianceCategoryID(), appCat);
    }
    
    public void updateApplianceCategory(LiteApplianceCategory appCat) {
        Validate.notNull(appCat, "Appliance category cannot be null");
        appCategoryMap.put(appCat.getApplianceCategoryID(), appCat);
    }
    
    public void deleteApplianceCategory(int applianceCategoryID) {
    	appCategoryMap.remove(applianceCategoryID);
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
    
    public Warehouse getWarehouse(int warehouseId) {
        List<Warehouse> warehouses = getWarehouses();
        for (Warehouse warehouse : warehouses) {
            if (warehouse.getWarehouseID() == warehouseId)
                return warehouse;
        }
        return null;
    }
    
    public synchronized void clearWarehouseCache() {
        warehouses = null;
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
    
    /**
     * This method resets the substation variable to null for the given energy company.  This will cause the
     * get methods to look up the substations from the database next time instead of using the cached value.
     */
    public synchronized void resetSubstations() {
        substations = null;
    }
    
    /**
     * This method resets the routes variable to null for the given energy company.  This will cause the
     * get methods to look up the routes from the database next time instead of using the cached value.
     */
    public synchronized void resetRouteIds() {
        routeIDs = null;
    }

    
    public LiteAddress getAddress(int addressID) {
        LiteAddress address = addressDao.getByAddressId(addressID); 
        return address;
    }
    
    public LiteLMProgramWebPublishing getProgram(int programID) {
        Iterable<LiteLMProgramWebPublishing> allPrograms = getAllPrograms();
        for (LiteLMProgramWebPublishing program : allPrograms) {
            if (program.getProgramID() == programID) {
                return program;
            }
        }
        return null;
    }
    
    public void addProgram(LiteLMProgramWebPublishing liteProg, LiteApplianceCategory liteAppCat) {
        LiteApplianceCategory applianceCategory = 
        	appCategoryMap.get(liteAppCat.getApplianceCategoryID());
        applianceCategory.addProgram(liteProg);
        
        programIdToAppCatIdMap.put(liteProg.getProgramID(), liteAppCat.getApplianceCategoryID());
    }
    
    public void updateProgram(LiteLMProgramWebPublishing liteProg, LiteApplianceCategory liteAppCat) {
        LiteApplianceCategory applianceCategory = 
            appCategoryMap.get(liteAppCat.getApplianceCategoryID());
        applianceCategory.updateProgram(liteProg);
        
        programIdToAppCatIdMap.put(liteProg.getProgramID(), liteAppCat.getApplianceCategoryID());
    }
    
    public void deleteProgram(int programID) {
    	Integer appCatId = programIdToAppCatIdMap.get(programID);
    	programIdToAppCatIdMap.remove(programID);
    	if(appCatId != null) {
	    	LiteApplianceCategory applianceCategory = appCategoryMap.get(appCatId);
	    	applianceCategory.removeProgram(programID);
    	}
    }
    
    public LiteStarsThermostatSettings getThermostatSettings(LiteStarsLMHardware liteHw) {
        try {
            LiteStarsThermostatSettings settings = new LiteStarsThermostatSettings();
            settings.setInventoryID( liteHw.getInventoryID() );
            
            com.cannontech.database.data.stars.event.LMThermostatManualEvent[] events =
                    com.cannontech.database.data.stars.event.LMThermostatManualEvent.getAllLMThermostatManualEvents( liteHw.getInventoryID() );
            if (events != null) {
                for (int i = 0; i < events.length; i++)
                    settings.getThermostatManualEvents().add(
                        (LiteLMThermostatManualEvent) StarsLiteFactory.createLite(events[i]) );
            }
            
            return settings;
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }
        
        return null;
    }
    
    /**
     * Search the work orders by order number. If searchMembers is true,
     * it returns a list of Pair(LiteWorkOrderBase, LiteStarsEnergyCompany);
     * otherwise it returns a list of LiteWorkOrderBase.
     */
    public List<Object> searchWorkOrderByOrderNo(String orderNo, boolean searchMembers) {
        List<Object> orderList = new ArrayList<Object>();
        
        int[] orderIDs = com.cannontech.database.db.stars.report.WorkOrderBase.searchByOrderNumber( orderNo, getLiteID() );
        if (orderIDs == null) return null;

        List<Integer> workOrderIds = Arrays.asList(ArrayUtils.toObject(orderIDs));
        List<LiteWorkOrderBase> workOrderList = starsWorkOrderBaseDao.getByIds(workOrderIds);
        
        for (final LiteWorkOrderBase workOrderBase : workOrderList) {
            if (searchMembers) {
                orderList.add(new Pair<LiteWorkOrderBase,LiteStarsEnergyCompany>(workOrderBase, this));
            } else {
                orderList.add(workOrderBase);
            }
        }
        
        if (searchMembers) {
            for (final LiteStarsEnergyCompany company : getChildren()) {
                List<Object> memberList = company.searchWorkOrderByOrderNo( orderNo, searchMembers );
                orderList.addAll( memberList );
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
                dbPersistentDao.performDBChange(appliance, TransactionType.RETRIEVE);
                
                liteApp = StarsLiteFactory.createLiteStarsAppliance( appliance, this );
                appliances.set(i, liteApp);
            }
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }
        
        return liteAcctInfo;
    }    
    
    /**
     * @deprecated Use starsCustAccountInformationDao.getById() instead of this method.
     */
    @Deprecated
    public LiteStarsCustAccountInformation getCustAccountInformation(int accountID, boolean autoLoad) {
        LiteStarsCustAccountInformation liteAcctInfo = starsCustAccountInformationDao.getById(accountID, getEnergyCompanyId());
        return liteAcctInfo;
    }

    public void deleteCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo) {
        if (liteAcctInfo == null) return;
        
        // Remove customer from the cache
        ServerUtils.handleDBChange( liteAcctInfo.getCustomer(), DbChangeType.DELETE );
        
    }
    
    /**
     * Search customer account by account # within the energy company.
     * int totalComparableDigits is for use with rotation digit billing systems: the field
     * represents the total length of the account number to be considered in the search (i.e. 
     * the account number sans the rotation digits, most commonly the last two digits).  If
     * normal length, then 0 or lower will result in the whole account number being used.  This would
     * be default.  
     */
    @Deprecated
    /**
     * Use LiteStarsEnergyCompany.searchAccountByAccountNumber or CustomerAccountDao.getByAccountNumber
     */
    public LiteStarsCustAccountInformation searchAccountByAccountNo(String accountNo) 
    {
    	List<Object> accounts = searchAccountByAccountNumber(accountNo, false, true);
    	int accountNumberLength = 
    	    energyCompanyRolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.ACCOUNT_NUMBER_LENGTH, this);
    	int rotationDigitLength = 
    	    energyCompanyRolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.ROTATION_DIGIT_LENGTH, this);
        int accountNumSansRotationDigitsIndex = accountNo.length();
        boolean adjustForRotationDigits = false;
        boolean adjustForAccountLength = false;
        if (rotationDigitLength > 0 && rotationDigitLength < accountNo.length()) {
            accountNumSansRotationDigitsIndex = accountNo.length() - rotationDigitLength;
            accountNo = accountNo.substring(0, accountNumSansRotationDigitsIndex);
            adjustForRotationDigits = true;
        }
        
        int comparableDigitEndIndex = 0;
        if (accountNumberLength > 0) {
            comparableDigitEndIndex = accountNumberLength;
            if (accountNo.length() >= comparableDigitEndIndex) {
                accountNo = accountNo.substring(0, comparableDigitEndIndex);
            }
            adjustForAccountLength = true;
        }

        
        for (Object object : accounts) {
        	if (object instanceof Integer) {
        		Integer accountId = (Integer) object;
        		LiteStarsCustAccountInformation liteAcctInfo = starsCustAccountInformationDao.getById(accountId, this.getEnergyCompanyId());
        	
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
        
    	List<LiteInventoryBase> invList = 
    		starsSearchDao.searchLMHardwareBySerialNumber(serialNo, Collections.singletonList(this));
        List<Object> accountList = new ArrayList<Object>();
       
        for (int i = 0; i < invList.size(); i++) {
            LiteInventoryBase inv = invList.get(i);
            Integer accountId = inv.getAccountID();
            if (accountId > 0) {
                if (searchMembers)
                    accountList.add( new Pair<Integer,LiteStarsEnergyCompany>(accountId, this) );
                else
                    accountList.add( accountId );
            }
        }
        
        if (searchMembers) {
            for (final LiteStarsEnergyCompany company : getChildren()) {
                List<Object> memberList = company.searchAccountBySerialNo( serialNo, searchMembers );
                accountList.addAll( memberList );
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
        
        // Gets all the possible energy company ids
        List<Integer> energyCompanyIdList = new ArrayList<Integer>();
        if (searchMembers) {
            energyCompanyIdList = getAllEnergyCompaniesDownward();
        } else {
        	energyCompanyIdList.add(getLiteID());
        }
        
        accountList.addAll(CustomerAccount.searchByCustomerAltTrackingNumber( altTrackNo + "%", energyCompanyIdList ));
        return accountList;
    }
    
    /**
    * Search customer account by company name, search results based on partial match
    * If searchMembers is true, the return type is Pair(LiteStarsCustAccountInformation, LiteStarsEnergyCompany);
    * otherwise the return type is LiteStarsCustAccountInformation.
    */
    public List<Object> searchAccountByCompanyName(String searchName, boolean searchMembers) {
        List<Object> accountList = new ArrayList<Object>();
        
        // Gets all the possible energy company ids
        List<Integer> energyCompanyIdList = new ArrayList<Integer>();
        if (searchMembers) {
            energyCompanyIdList = getAllEnergyCompaniesDownward();
        } else {
        	energyCompanyIdList.add(getLiteID());
        }
        
        accountList.addAll(CustomerAccount.searchByCompanyName( searchName + "%", energyCompanyIdList, searchMembers));
       return accountList;
   }
    
    /**
     * Search customer account by residence map #, search results based on partial match
     * If searchMembers is true, the return type is Pair(LiteStarsCustAccountInformation, LiteStarsEnergyCompany);
     * otherwise the return type is LiteStarsCustAccountInformation.
     */
    public List<Object> searchAccountByMapNo(String mapNo, boolean searchMembers) {
        List<Object> accountList = new ArrayList<Object>();

        // Gets all the possible energy company ids
        List<Integer> energyCompanyIdList = new ArrayList<Integer>();
        if (searchMembers) {
            energyCompanyIdList = getAllEnergyCompaniesDownward();
        } else {
        	energyCompanyIdList.add(getLiteID());
        }
        
        // Uses the the energy company ids to find all the desired accounts
        accountList.addAll(CustomerAccount.searchBySiteNumber( mapNo + "%", energyCompanyIdList));
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

        for (final Object obj : orderList) {
            LiteWorkOrderBase liteOrder = null;
            
            if (obj instanceof LiteWorkOrderBase) {
                liteOrder = (LiteWorkOrderBase) obj;
            }
            if (obj instanceof Pair) {
                Pair<LiteWorkOrderBase,LiteStarsEnergyCompany> pair = (Pair<LiteWorkOrderBase,LiteStarsEnergyCompany>) obj;
                liteOrder = pair.getFirst();
            }
            
            if (liteOrder != null) {
                int accountId = liteOrder.getAccountID();
                if (accountId > 0){
                    if (searchMembers) {
                        accountList.add( new Pair<Integer,LiteStarsEnergyCompany>(accountId, this) );
                    } else {
                        accountList.add(accountId);
                    }
                }
            }
        }
        
        if (searchMembers) {
            for (LiteStarsEnergyCompany company : getChildren()) {
                List<Object> memberList = company.searchAccountByOrderNo( orderNo, searchMembers );
                accountList.addAll( memberList );
            }
        }

        return accountList;
    }
    
	private List<Object> searchAccountByContactIDs(int[] contactIDs, boolean searchMembers) {
        List<Object> accountList = new ArrayList<Object>();

        int[] accountIDs = com.cannontech.database.db.stars.customer.CustomerAccount.searchByPrimaryContactIDs( contactIDs, getLiteID() );
        if (accountIDs != null) {
            for (final int accountId : accountIDs) {
                if (searchMembers) {
                    accountList.add(
                        new Pair<Integer, LiteStarsEnergyCompany>(accountId, this));
                } else {
                    accountList.add(accountId);
                }
            }
        }
        
        if (searchMembers) {
            for (final LiteStarsEnergyCompany company : getChildren()) {
                List<Object> memberList = company.searchAccountByContactIDs( contactIDs, searchMembers );
                accountList.addAll( memberList );
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
            return starsOperECSettings;
        } else if (StarsUtils.isResidentialCustomer(user.getYukonUser())) { 
            StarsEnergyCompanySettings starsCustECSettings = new StarsEnergyCompanySettings();
            starsCustECSettings.setEnergyCompanyID( user.getEnergyCompanyID() );
            starsCustECSettings.setStarsEnergyCompany( getStarsEnergyCompany() );
            starsCustECSettings.setStarsEnrollmentPrograms( getStarsEnrollmentPrograms() );
            starsCustECSettings.setStarsCustomerSelectionLists( getStarsCustomerSelectionLists(user) );
            return starsCustECSettings;
        }
        
        return null;
    }
    
    public StarsEnergyCompany getStarsEnergyCompany() {
        StarsEnergyCompany starsEnergyCompany = new StarsEnergyCompany();
        StarsLiteFactory.setStarsEnergyCompany( starsEnergyCompany, this );
        return starsEnergyCompany;
    }
    
    public StarsCustSelectionList getStarsCustSelectionList(String listName) {
        StarsCustSelectionList starsList = null;
        YukonSelectionList yukonList = getYukonSelectionList( listName );
        if (yukonList != null) {
            starsList = StarsLiteFactory.createStarsCustSelectionList( yukonList );
        }
        return starsList;
    }
    
    public StarsCustomerSelectionLists getStarsCustomerSelectionLists(LiteYukonUser yukonUser) {
    	
    	if (StarsUtils.isOperator(yukonUser)) {
            StarsCustomerSelectionLists starsOperSelLists = new StarsCustomerSelectionLists();
            
            for (int i = 0; i < OPERATOR_SELECTION_LISTS.length; i++) {
                StarsCustSelectionList list = getStarsCustSelectionList(OPERATOR_SELECTION_LISTS[i]);
                if (list != null) starsOperSelLists.addStarsCustSelectionList( list );
            }
            
            return starsOperSelLists;
        }
        else if (StarsUtils.isResidentialCustomer(yukonUser)) {
            StarsCustomerSelectionLists starsCustSelLists = new StarsCustomerSelectionLists();
            // Currently the consumer side only need chance of control and opt out period list
            StarsCustSelectionList list = getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL);
            if (list != null) starsCustSelLists.addStarsCustSelectionList( list );
            return starsCustSelLists;
        }
        
        return null;
    }
    
    public StarsCustomerSelectionLists getStarsCustomerSelectionLists(StarsYukonUser starsUser) {

    	return getStarsCustomerSelectionLists(starsUser.getYukonUser());
    }
    
    public StarsEnrollmentPrograms getStarsEnrollmentPrograms() {
        StarsEnrollmentPrograms starsEnrPrograms = new StarsEnrollmentPrograms();
        StarsLiteFactory.setStarsEnrollmentPrograms( starsEnrPrograms, getAllApplianceCategories(), this );
        return starsEnrPrograms;
    }
    
    public StarsServiceCompanies getStarsServiceCompanies() {
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
    
    public StarsSubstations getStarsSubstations() {
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
    
    public StarsCustAccountInformation getStarsCustAccountInformation(LiteStarsCustAccountInformation liteAcctInfo) {
            StarsCustAccountInformation starsAcctInfo = StarsLiteFactory.createStarsCustAccountInformation( liteAcctInfo, this, true );
            return starsAcctInfo;
    }
    
    public StarsCustAccountInformation getStarsCustAccountInformation(int accountID, boolean autoLoad) {
        LiteStarsCustAccountInformation liteAcctInfo = starsCustAccountInformationDao.getById(accountID, getEnergyCompanyId());
        if (liteAcctInfo != null) return getStarsCustAccountInformation( liteAcctInfo );
        return null;
    }
    
    public StarsCustAccountInformation getStarsCustAccountInformation(int accountID) {
        return getStarsCustAccountInformation( accountID, false );
    }
    
    private EnergyCompanyHierarchy loadEnergyCompanyHierarchy() {
        EnergyCompanyHierarchy ech = new EnergyCompanyHierarchy();
        
        String sql = "SELECT EnergyCompanyID, ItemID FROM ECToGenericMapping " +
                "WHERE MappingCategory='" + ECToGenericMapping.MAPPING_CATEGORY_MEMBER + "' " +
                "AND (EnergyCompanyID=" + getEnergyCompanyId() + " OR ItemID=" + getEnergyCompanyId() + ")";
        
        try {
            SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
            stmt.execute();
            
            for (int i = 0; i < stmt.getRowCount(); i++) {
                int parentID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
                int childID = ((java.math.BigDecimal) stmt.getRow(i)[1]).intValue();
                
                if (parentID == getLiteID())
                    ech.children.add( StarsDatabaseCache.getInstance().getEnergyCompany(childID) );
                else    // childID == getLiteID()
                    ech.parent = StarsDatabaseCache.getInstance().getEnergyCompany( parentID );
            }
            
            ECToGenericMapping[] items = ECToGenericMapping.getAllMappingItems(
                    getEnergyCompanyId(), ECToGenericMapping.MAPPING_CATEGORY_MEMBER_LOGIN );
            
            if (items != null) {
                for (int i = 0; i < items.length; i++)
                    ech.memberLoginIDs.add( items[i].getItemID() );
            }
            
            CTILogger.info( "Energy company hierarchy loaded for energy company #" + getEnergyCompanyId() );
        }
        catch (CommandExecutionException e) {
            throw new RuntimeException(e);
        }
        return ech;
    }
    
    private EnergyCompanyHierarchy getEnergyCompanyHierarchy() {
        EnergyCompanyHierarchy value = energyCompanyHierarchy;
        if (value == null) {
            EnergyCompanyHierarchy loadedValue = loadEnergyCompanyHierarchy();
            energyCompanyHierarchy = loadedValue;
            return loadedValue;
        }
        return value;
    }
    
    public LiteStarsEnergyCompany getParent() {
        return getEnergyCompanyHierarchy().parent;
    }
    
    public Iterable<LiteStarsEnergyCompany> getChildren() {
        return getEnergyCompanyHierarchy().children;
    }
    
    public boolean hasChildEnergyCompanies() {
        return !getEnergyCompanyHierarchy().children.isEmpty();
    }
    
    public List<Integer> getMemberLoginIDs() {
        return getEnergyCompanyHierarchy().memberLoginIDs;
    }
    
    public void clearHierarchy() {
        energyCompanyHierarchy = null;
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
        String sql = "SELECT map.energycompanyID, acct.AccountID " + 
                    " FROM ECToAccountMapping map, " + CustomerAccount.TABLE_NAME + " acct, " + 
                    Customer.TABLE_NAME + " cust, " +  Contact.TABLE_NAME + " cont " +
                    " WHERE map.AccountID = acct.AccountID " +
                    " AND acct.CustomerID = cust.CustomerID " + 
                    " AND cust.primarycontactid = cont.contactid " +
                    " AND UPPER(cont.contlastname) like ? " ;
                    if (firstName != null && firstName.length() > 0)
                        sql += " AND UPPER(CONTFIRSTNAME) LIKE ? ";

                    // Hey, if we have all the ECIDs available, don't bother adding this criteria!
                    if( energyCompanyIDList.size() != StarsDatabaseCache.getInstance().getAllEnergyCompanies().size())
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

                if (searchMembers)
                    accountList.add(new Pair<Integer,LiteStarsEnergyCompany>(accountID, liteStarsEC) );
                else
                    accountList.add(accountID);

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
        
        String sql = "SELECT map.energycompanyID, acct.AccountID " + 
                    " FROM ECToAccountMapping map, " + CustomerAccount.TABLE_NAME + " acct, " + 
                    " AccountSite acs, Address adr " +  
                    " WHERE map.AccountID = acct.AccountID " +
                    " AND acct.AccountSiteID = acs.AccountSiteID " +
                    " AND acs.streetaddressID = adr.addressID " +
                    " AND UPPER(adr.LocationAddress1) LIKE ? ";
                    // Hey, if we have all the ECIDs available, don't bother adding this criteria!
                    if( energyCompanyIDList.size() != StarsDatabaseCache.getInstance().getAllEnergyCompanies().size())
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
            while(rset.next())
            {
                Integer energyCompanyID = new Integer(rset.getInt(1));
                liteStarsEC = StarsDatabaseCache.getInstance().getEnergyCompany(energyCompanyID.intValue());
                
                Integer accountID = new Integer(rset.getInt(2));

                if (searchMembers)
                    accountList.add(new Pair<Integer,LiteStarsEnergyCompany>(accountID, liteStarsEC) );
                else
                    accountList.add(accountID);

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
        
        String sql = "SELECT map.energycompanyID, acct.AccountID " + //1-2" +
                    " FROM ECToAccountMapping map, " + CustomerAccount.TABLE_NAME + " acct " + 
                    " WHERE map.AccountID = acct.AccountID " +
                    " AND UPPER(acct.AccountNumber) LIKE ? ";
                    // Hey, if we have all the ECIDs available, don't bother adding this criteria!
                    if( energyCompanyIDList.size() != StarsDatabaseCache.getInstance().getAllEnergyCompanies().size())
                    {
                        sql += " AND (map.EnergyCompanyID = " + energyCompanyIDList.get(0).toString(); 
                        for (int i = 1; i < energyCompanyIDList.size(); i++)
                             sql += " OR map.EnergyCompanyID = " + energyCompanyIDList.get(i).toString();
                        sql += " ) ";
                    }
                    sql += " order by accountNumber";                          
        
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
            Integer accountId = null;
            while(rset.next())
            {
                Integer energyCompanyID = new Integer(rset.getInt(1));
                liteStarsEC = StarsDatabaseCache.getInstance().getEnergyCompany(energyCompanyID.intValue());
                
                accountId = new Integer(rset.getInt(2));

                if (searchMembers)
                    accountList.add(new Pair<Integer,LiteStarsEnergyCompany>(accountId, liteStarsEC) );
                else
                    accountList.add(accountId);

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
    
    private void initApplianceCategories(){

    	ApplianceCategory[] appCats = ApplianceCategory.getAllApplianceCategories(getEnergyCompanyId());
                
        for (ApplianceCategory category : appCats) {
            LiteApplianceCategory appCat = 
            	(LiteApplianceCategory) StarsLiteFactory.createLite(category);
            
            LMProgramWebPublishing[] pubProgs =
                    LMProgramWebPublishing.getAllLMProgramWebPublishing(category.getApplianceCategoryID());
            
            for (LMProgramWebPublishing pubProg : pubProgs) {
                LiteLMProgramWebPublishing program = 
                	(LiteLMProgramWebPublishing) StarsLiteFactory.createLite(pubProg);
                appCat.addProgram(program);
                programIdToAppCatIdMap.put(program.getProgramID(), appCat.getApplianceCategoryID());
            }
            appCategoryMap.put(appCat.getApplianceCategoryID(), appCat);
        }
        
        CTILogger.info( "All appliance categories loaded for energy company #" + getEnergyCompanyId() );
    }
    
    @Override
    public LiteYukonUser getEnergyCompanyUser() {
        return getUser();
    }

    /**
     * This is a helper method to get the Function to transform YukonEnergyCompanies to energyCompanyIds.
     */
    public static Function<YukonEnergyCompany, Integer> getEnergyCompanyToEnergyCompanyIdFunction(){
        return new Function<YukonEnergyCompany, Integer>() {
            @Override
            public Integer apply(YukonEnergyCompany yukonEnergyCompany) {
                return yukonEnergyCompany.getEnergyCompanyId();
            }
        };
    }
    
    
    // DI Setters    
    public void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }
    
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
    
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
        
    public void setEnergyCompanyRolePropertyDao(EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao) {
        this.energyCompanyRolePropertyDao = energyCompanyRolePropertyDao;
    }

    public void setPaoPermissionService(PaoPermissionService paoPermissionService) {
        this.paoPermissionService = paoPermissionService;
    }
    
    public void setStarsCustAccountInformationDao(StarsCustAccountInformationDao starsCustAccountInformationDao) {
        this.starsCustAccountInformationDao = starsCustAccountInformationDao;
    }

    public void setStarsWorkOrderBaseDao(StarsWorkOrderBaseDao starsWorkOrderBaseDao) {
        this.starsWorkOrderBaseDao = starsWorkOrderBaseDao;
    }
    
    public void setStarsSearchDao(StarsSearchDao starsSearchDao) {
        this.starsSearchDao = starsSearchDao;
    }
    
    public void setSystemDateFormattingService(SystemDateFormattingService systemDateFormattingService) {
		this.systemDateFormattingService = systemDateFormattingService;
	}
    
    public void setWarehouseDao(WarehouseDao warehouseDao) {
        this.warehouseDao = warehouseDao;
    }

    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
    }
    
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    public void setYukonListDao(YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }
}