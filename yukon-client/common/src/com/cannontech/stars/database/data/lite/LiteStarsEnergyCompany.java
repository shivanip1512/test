package com.cannontech.stars.database.data.lite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.Validate;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.user.YukonGroup;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.WarehouseDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.appliance.ApplianceBase;
import com.cannontech.stars.database.data.event.LMThermostatManualEvent;
import com.cannontech.stars.database.data.report.ServiceCompany;
import com.cannontech.stars.database.db.LMProgramWebPublishing;
import com.cannontech.stars.database.db.appliance.ApplianceCategory;
import com.cannontech.stars.database.db.hardware.Warehouse;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.EcMappingCategory;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
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
    
    private DBPersistentDao dbPersistentDao;
    private DbChangeManager dbChangeManager;
    private ECMappingDao ecMappingDao;
    private StarsCustAccountInformationDao starsCustAccountInformationDao;
    private StarsDatabaseCache starsDatabaseCache;
    private SelectionListService selectionListService;
    private YukonGroupDao yukonGroupDao;
    private EnergyCompanyService energyCompanyService;
    private RoleDao roleDao;
    private EnergyCompanyDao ecDao;
    private EnergyCompanySettingDao energyCompanySettingDao;

    private final static long serialVersionUID = 1L;

    // Magic number for YukonSelectionList ID, used for substation and service company list
    public static final int FAKE_LIST_ID = -9999;

    // @formatter:off
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
    };// @formatter:on

    private String name = null;
    private int primaryContactID = CtiUtilities.NONE_ZERO_ID;
    private LiteYukonUser user = null;

    private List<LiteServiceCompany> serviceCompanies = null;
    private volatile List<Warehouse> warehouses = null;
    private List<LiteSubstation> substations = null;

    private final Map<Integer, Integer> programIdToAppCatIdMap = new ConcurrentHashMap<>();
    private final Map<Integer, LiteApplianceCategory> appCategoryMap = new ConcurrentHashMap<>();

    private int operDftGroupID = YukonGroup.EDITABLE_MIN_GROUP_ID - 1;

    private class EnergyCompanyHierarchy {
        LiteStarsEnergyCompany parent = null;
        final List<LiteStarsEnergyCompany> children = new ArrayList<>();
        final List<Integer> memberLoginIDs = new ArrayList<>();
    }

    private volatile EnergyCompanyHierarchy energyCompanyHierarchy = null;

    private WarehouseDao warehouseDao;

    protected LiteStarsEnergyCompany() {
        super();
        setLiteType(LiteTypes.ENERGY_COMPANY);
    }

    protected LiteStarsEnergyCompany(int companyID) {
        super();
        setLiteID(companyID);
        setLiteType(LiteTypes.ENERGY_COMPANY);
    }

    protected LiteStarsEnergyCompany(int energyCompanyId, String name, int contactId, int userId) {
        setLiteType(LiteTypes.ENERGY_COMPANY);
        setLiteID(energyCompanyId);
        setName(name);
        setPrimaryContactID(contactId);
        setUser(YukonSpringHook.getBean(YukonUserDao.class).getLiteYukonUser(userId));
    }

    protected LiteStarsEnergyCompany(com.cannontech.database.db.company.EnergyCompany energyCompany) {
        this(energyCompany.getEnergyCompanyId(), energyCompany.getName(), energyCompany.getPrimaryContactId(),
            energyCompany.getUserId());
    }

    public void initialize() {
        initApplianceCategories();
    }

    @Override
    public int getEnergyCompanyId() {
        return getLiteID();
    }

    @Deprecated
    public Integer getEnergyCompanyID() {
        return getEnergyCompanyId();
    }

    @Override
    public String getName() {
        if (name == null) {
            setName(ecDao.getEnergyCompany(getEnergyCompanyId()).getName());
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrimaryContactID() {
        return primaryContactID;
    }

    public void setPrimaryContactID(int primaryContactID) {
        this.primaryContactID = primaryContactID;
    }

    public LiteYukonUser getUser() {
        return user;
    }

    public void setUser(LiteYukonUser user) {
        this.user = user;
    }

    /**
     * Clears out both of the cache objects held by the appliance categories
     */
    public void resetApplianceCategoryList() {
        programIdToAppCatIdMap.clear();
        appCategoryMap.clear();
        initApplianceCategories();
    }

    public LiteYukonGroup getOperatorAdminGroup() {
        LiteYukonGroup adminGroup = null;
        if (operDftGroupID < YukonGroup.EDITABLE_MIN_GROUP_ID) {

            List<LiteYukonGroup> groups = yukonGroupDao.getGroupsForUser(user);
            for (int i = 0; i < groups.size(); i++) {
                LiteYukonGroup group = groups.get(i);
                if (roleDao.getRolesForGroup(group.getGroupID()).contains(YukonRole.OPERATOR_ADMINISTRATOR)) {
                    operDftGroupID = group.getGroupID();
                    adminGroup = group;
                    break;
                }
            }
        } else {
            adminGroup = yukonGroupDao.getLiteYukonGroup(operDftGroupID);
        }
        return adminGroup;
    }

    /**
     * Get programs published by this energy company only
     */
    public Iterable<LiteLMProgramWebPublishing> getPrograms() {
        Iterable<LiteLMProgramWebPublishing> programs = Collections.emptyList();

        Iterable<LiteApplianceCategory> categories = getApplianceCategories();
        for (LiteApplianceCategory category : categories) {
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
            energyCompanySettingDao.getBoolean(EnergyCompanySettingType.INHERIT_PARENT_APP_CATS,
                this.getEnergyCompanyId());

        if (getParent() != null && inheritCats) {
            Iterable<LiteApplianceCategory> parentCategories = getParent().getAllApplianceCategories();
            allApplianceCategories = Iterables.concat(allApplianceCategories, parentCategories);
        }

        return allApplianceCategories;
    }

    public List<HardwareType> getAvailableThermostatTypes() {
        List<HardwareType> typeList = new ArrayList<>();
        YukonSelectionList selectionList =
            selectionListService.getSelectionList(this, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE);
        for (YukonListEntry entry : selectionList.getYukonListEntries()) {
            HardwareType type = HardwareType.valueOf(entry.getYukonDefID());
            if (type.isThermostat()) {
                typeList.add(type);
            }
        }
        return typeList;
    }

    public synchronized List<LiteServiceCompany> getServiceCompanies() {
        if (serviceCompanies == null) {
            serviceCompanies = new ArrayList<>();

            List<ServiceCompany> companies = ServiceCompany.retrieveAllServiceCompanies(getEnergyCompanyId());
            for (ServiceCompany serviceCompany : companies) {
                serviceCompanies.add((LiteServiceCompany) StarsLiteFactory.createLite(serviceCompany));
            }

            CTILogger.info("All service companies loaded for energy company #" + getEnergyCompanyId());
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
        List<LiteServiceCompany> allCompanies = new ArrayList<>();
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
        List<LiteServiceCompany> allCompanies = new ArrayList<>();
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

    public List<LiteServiceCompany> getAllServiceCompaniesDownward() {
        List<LiteServiceCompany> descCompanies = new ArrayList<>();
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
     * Returns list of energy company's own warehouses,
     * that are allowed to be assigned to a device, shipment, work order etc
     * 
     * @return A copy of the warehouse list
     */
    public List<Warehouse> getWarehouses() {
        List<Warehouse> tempWarehouses = warehouses;
        if (tempWarehouses == null) {
            tempWarehouses = new ArrayList<>();
            tempWarehouses = warehouseDao.getAllWarehousesForEnergyCompanyId(getEnergyCompanyId());
            CTILogger.info("All Warehouses loaded for energy company #" + getEnergyCompanyId());

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
    public List<Warehouse> getAllWarehousesDownward() {
        List<Warehouse> descWarehouses = new ArrayList<>();
        descWarehouses.addAll(getWarehouses());

        for (LiteStarsEnergyCompany child : getChildren()) {
            descWarehouses.addAll(child.getAllWarehousesDownward());
        }

        return descWarehouses;
    }

    public synchronized List<LiteSubstation> getSubstations() {
        if (substations == null) {
            com.cannontech.stars.database.db.Substation[] subs =
                com.cannontech.stars.database.db.Substation.getAllSubstations(getEnergyCompanyId());

            substations = Collections.synchronizedList(new ArrayList<LiteSubstation>());
            for (int i = 0; i < subs.length; i++) {
                LiteSubstation liteSub = (LiteSubstation) StarsLiteFactory.createLite(subs[i]);
                substations.add(liteSub);
            }

            CTILogger.info("All substations loaded for energy company #" + getEnergyCompanyId());
        }

        return substations;
    }

    /**
     * @return A copy of the substations list
     */
    public List<LiteSubstation> getAllSubstations() {
        List<LiteSubstation> allSubstations = new ArrayList<>();
        if (getParent() != null) {
            allSubstations.addAll(getParent().getAllSubstations());
        }
        List<LiteSubstation> substations = getSubstations();
        synchronized (substations) {
            allSubstations.addAll(substations);
        }

        return allSubstations;
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
            if (serviceCompany.getCompanyID() == serviceCompanyID) {
                return serviceCompany;
            }
        }

        return null;
    }

    public void addServiceCompany(LiteServiceCompany serviceCompany) {
        List<LiteServiceCompany> serviceCompanies = getServiceCompanies();
        synchronized (serviceCompanies) {
            serviceCompanies.add(serviceCompany);
        }
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

    public void clearWarehouseCache() {
        warehouses = null;
    }

    public LiteSubstation getSubstation(int substationID) {
        List<LiteSubstation> substations = getAllSubstations();
        for (final LiteSubstation liteSub : substations) {
            if (liteSub.getSubstationID() == substationID) {
                return liteSub;
            }
        }

        return null;
    }

    public void addSubstation(LiteSubstation substation) {
        List<LiteSubstation> substations = getSubstations();
        synchronized (substations) {
            substations.add(substation);
        }
    }

    /**
     * This method resets the substation variable to null for the given energy company. This will cause the
     * get methods to look up the substations from the database next time instead of using the cached value.
     */
    public synchronized void resetSubstations() {
        substations = null;
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
        LiteApplianceCategory applianceCategory = appCategoryMap.get(liteAppCat.getApplianceCategoryID());
        applianceCategory.addProgram(liteProg);

        programIdToAppCatIdMap.put(liteProg.getProgramID(), liteAppCat.getApplianceCategoryID());
    }

    public void updateProgram(LiteLMProgramWebPublishing liteProg, LiteApplianceCategory liteAppCat) {
        LiteApplianceCategory applianceCategory = appCategoryMap.get(liteAppCat.getApplianceCategoryID());
        applianceCategory.updateProgram(liteProg);

        programIdToAppCatIdMap.put(liteProg.getProgramID(), liteAppCat.getApplianceCategoryID());
    }

    public void deleteProgram(int programID) {
        Integer appCatId = programIdToAppCatIdMap.get(programID);
        programIdToAppCatIdMap.remove(programID);
        if (appCatId != null) {
            LiteApplianceCategory applianceCategory = appCategoryMap.get(appCatId);
            applianceCategory.removeProgram(programID);
        }
    }

    public LiteStarsThermostatSettings getThermostatSettings(LiteLmHardwareBase liteHw) {
        try {
            LiteStarsThermostatSettings settings = new LiteStarsThermostatSettings();
            settings.setInventoryID(liteHw.getInventoryID());

            LMThermostatManualEvent[] events =
                LMThermostatManualEvent.getAllLMThermostatManualEvents(liteHw.getInventoryID());
            if (events != null) {
                for (int i = 0; i < events.length; i++) {
                    settings.getThermostatManualEvents().add(
                        (LiteLMThermostatManualEvent) StarsLiteFactory.createLite(events[i]));
                }
            }

            return settings;
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
        }

        return null;
    }

    /*
     * Have to use Yao's idea of extending customer account information but I don't want all of
     * it, just appliances and inventory.
     */
    public LiteAccountInfo limitedExtendCustAccountInfo(LiteAccountInfo liteAcctInfo) {
        try {
            List<LiteStarsAppliance> appliances = liteAcctInfo.getAppliances();
            for (int i = 0; i < appliances.size(); i++) {
                LiteStarsAppliance liteApp = appliances.get(i);

                ApplianceBase appliance = new ApplianceBase();
                appliance.setApplianceID(new Integer(liteApp.getApplianceID()));
                dbPersistentDao.performDBChange(appliance, TransactionType.RETRIEVE);

                liteApp = StarsLiteFactory.createLiteStarsAppliance(appliance, this);
                appliances.set(i, liteApp);
            }
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
        }

        return liteAcctInfo;
    }

    public void deleteCustAccountInformation(LiteAccountInfo liteAcctInfo) {
        if (liteAcctInfo == null) {
            return;
        }

        // Remove customer from the cache
        dbChangeManager.processDbChange(liteAcctInfo.getCustomer().getLiteID(), DBChangeMsg.CHANGE_CUSTOMER_DB,
            DBChangeMsg.CAT_CUSTOMER, DBChangeMsg.CAT_CUSTOMER, DbChangeType.DELETE);
    }

    public StarsEnergyCompanySettings getStarsEnergyCompanySettings(StarsYukonUser user) {
        if (energyCompanyService.isOperator(user.getYukonUser())) {
            StarsEnergyCompanySettings starsOperECSettings = new StarsEnergyCompanySettings();
            starsOperECSettings.setEnergyCompanyID(user.getEnergyCompanyID());
            starsOperECSettings.setStarsEnergyCompany(getStarsEnergyCompany());
            starsOperECSettings.setStarsEnrollmentPrograms(getStarsEnrollmentPrograms());
            starsOperECSettings.setStarsCustomerSelectionLists(getStarsCustomerSelectionLists(user.getYukonUser()));
            starsOperECSettings.setStarsServiceCompanies(getStarsServiceCompanies());
            starsOperECSettings.setStarsSubstations(getStarsSubstations());
            return starsOperECSettings;
        } else if (energyCompanyService.isResidentialUser(user.getYukonUser())) {
            StarsEnergyCompanySettings starsCustECSettings = new StarsEnergyCompanySettings();
            starsCustECSettings.setEnergyCompanyID(user.getEnergyCompanyID());
            starsCustECSettings.setStarsEnergyCompany(getStarsEnergyCompany());
            starsCustECSettings.setStarsEnrollmentPrograms(getStarsEnrollmentPrograms());
            starsCustECSettings.setStarsCustomerSelectionLists(getStarsCustomerSelectionLists(user.getYukonUser()));
            return starsCustECSettings;
        }

        return null;
    }

    public StarsEnergyCompany getStarsEnergyCompany() {
        StarsEnergyCompany starsEnergyCompany = new StarsEnergyCompany();
        StarsLiteFactory.setStarsEnergyCompany(starsEnergyCompany, this);
        return starsEnergyCompany;
    }

    public StarsCustSelectionList getStarsCustSelectionList(String listName) {
        StarsCustSelectionList starsList = null;

        YukonSelectionList yukonList = selectionListService.getSelectionList(this, listName);
        if (yukonList != null) {
            starsList = StarsLiteFactory.createStarsCustSelectionList(yukonList);
        }
        return starsList;
    }

    private StarsCustomerSelectionLists getStarsCustomerSelectionLists(LiteYukonUser yukonUser) {

        if (energyCompanyService.isOperator(yukonUser)) {
            StarsCustomerSelectionLists starsOperSelLists = new StarsCustomerSelectionLists();

            for (int i = 0; i < OPERATOR_SELECTION_LISTS.length; i++) {
                StarsCustSelectionList list = getStarsCustSelectionList(OPERATOR_SELECTION_LISTS[i]);
                if (list != null) {
                    starsOperSelLists.addStarsCustSelectionList(list);
                }
            }

            return starsOperSelLists;
        } else if (energyCompanyService.isResidentialUser(yukonUser)) {
            StarsCustomerSelectionLists starsCustSelLists = new StarsCustomerSelectionLists();
            // Currently the consumer side only need chance of control and opt out period list
            StarsCustSelectionList list =
                getStarsCustSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL);
            if (list != null) {
                starsCustSelLists.addStarsCustSelectionList(list);
            }
            return starsCustSelLists;
        }

        return null;
    }

    public StarsEnrollmentPrograms getStarsEnrollmentPrograms() {
        StarsEnrollmentPrograms starsEnrPrograms = new StarsEnrollmentPrograms();
        StarsLiteFactory.setStarsEnrollmentPrograms(starsEnrPrograms, getAllApplianceCategories(), this);
        return starsEnrPrograms;
    }

    public StarsServiceCompanies getStarsServiceCompanies() {
        StarsServiceCompanies starsServCompanies = new StarsServiceCompanies();
        // Always add a "(none)" to the service company list
        StarsServiceCompany starsServCompany = new StarsServiceCompany();
        starsServCompany.setCompanyID(0);
        starsServCompany.setCompanyName("(none)");
        starsServCompanies.addStarsServiceCompany(starsServCompany);

        List<LiteServiceCompany> servCompanies = getAllServiceCompanies();
        Collections.sort(servCompanies, StarsUtils.SERVICE_COMPANY_CMPTR);

        for (int i = 0; i < servCompanies.size(); i++) {
            LiteServiceCompany liteServCompany = servCompanies.get(i);
            starsServCompany = new StarsServiceCompany();
            StarsLiteFactory.setStarsServiceCompany(starsServCompany, liteServCompany, this);
            starsServCompanies.addStarsServiceCompany(starsServCompany);
        }

        return starsServCompanies;
    }

    public StarsSubstations getStarsSubstations() {
        StarsSubstations starsSubstations = new StarsSubstations();
        // Always add a "(none)" to the service company list
        StarsSubstation starsSub = new StarsSubstation();
        starsSub.setSubstationID(0);
        starsSub.setSubstationName("(none)");
        starsSub.setRouteID(0);
        starsSubstations.addStarsSubstation(starsSub);

        List<LiteSubstation> substations = getAllSubstations();
        Collections.sort(substations, StarsUtils.SUBSTATION_CMPTR);

        for (int i = 0; i < substations.size(); i++) {
            LiteSubstation liteSub = substations.get(i);
            starsSub = new StarsSubstation();
            StarsLiteFactory.setStarsSubstation(starsSub, liteSub, this);
            starsSubstations.addStarsSubstation(starsSub);
        }
        return starsSubstations;
    }

    private StarsCustAccountInformation getStarsCustAccountInformation(LiteAccountInfo liteAcctInfo) {
        StarsCustAccountInformation starsAcctInfo =
            StarsLiteFactory.createStarsCustAccountInformation(liteAcctInfo, this, true);
        return starsAcctInfo;
    }

    public StarsCustAccountInformation getStarsCustAccountInformation(int accountId, boolean autoLoad) {
        LiteAccountInfo liteAcctInfo = starsCustAccountInformationDao.getByAccountId(accountId);
        if (liteAcctInfo != null) {
            return getStarsCustAccountInformation(liteAcctInfo);
        }
        return null;
    }

    public StarsCustAccountInformation getStarsCustAccountInformation(int accountID) {
        return getStarsCustAccountInformation(accountID, false);
    }

    private EnergyCompanyHierarchy loadEnergyCompanyHierarchy() {
        EnergyCompanyHierarchy ech = new EnergyCompanyHierarchy();

        int energyCompanyId = getEnergyCompanyId();
        Integer parentEnergyCompanyId = ecDao.findParentEnergyCompany(energyCompanyId);
        List<Integer> childEnergyCompanyIds = ecDao.getDirectChildEnergyCompanies(energyCompanyId);

        // Translates all of the energy company ids into LiteStarsEnergyCompanies.
        if (parentEnergyCompanyId != null) {
            ech.parent = starsDatabaseCache.getEnergyCompany(parentEnergyCompanyId);
        }

        List<LiteStarsEnergyCompany> childEnergyCompanies =
            Lists.transform(childEnergyCompanyIds, new Function<Integer, LiteStarsEnergyCompany>() {
                @Override
                public LiteStarsEnergyCompany apply(Integer energyCompanyId) {
                    return starsDatabaseCache.getEnergyCompany(energyCompanyId);
                }
            });
        ech.children.addAll(childEnergyCompanies);

        // Getting member logins
        List<Integer> loginIds =
            ecMappingDao.getItemIdsForEnergyCompanyAndCategory(energyCompanyId, EcMappingCategory.MEMBER_LOGIN);
        ech.memberLoginIDs.addAll(loginIds);

        CTILogger.info("Energy company hierarchy loaded for energy company #" + energyCompanyId);

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

    private void initApplianceCategories() {

        List<ApplianceCategory> appCats = ApplianceCategory.getAllApplianceCategories(getEnergyCompanyId());

        for (ApplianceCategory category : appCats) {
            LiteApplianceCategory appCat = (LiteApplianceCategory) StarsLiteFactory.createLite(category);

            LMProgramWebPublishing[] pubProgs =
                LMProgramWebPublishing.getAllLMProgramWebPublishing(category.getApplianceCategoryID());

            for (LMProgramWebPublishing pubProg : pubProgs) {
                LiteLMProgramWebPublishing program = (LiteLMProgramWebPublishing) StarsLiteFactory.createLite(pubProg);
                appCat.addProgram(program);
                programIdToAppCatIdMap.put(program.getProgramID(), appCat.getApplianceCategoryID());
            }
            appCategoryMap.put(appCat.getApplianceCategoryID(), appCat);
        }

        CTILogger.info("All appliance categories loaded for energy company #" + getEnergyCompanyId());
    }

    @Override
    public LiteYukonUser getEnergyCompanyUser() {
        return getUser();
    }

    public void resetServiceCompanyInfo() {
        this.serviceCompanies = null;
    }

    public void resetEnergyCompanyInfo() {
        this.name = null;
    }

    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }

    public void setDbChangeManager(DbChangeManager dbChangeManager) {
        this.dbChangeManager = dbChangeManager;
    }

    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }

    public void setStarsCustAccountInformationDao(StarsCustAccountInformationDao starsCustAccountInformationDao) {
        this.starsCustAccountInformationDao = starsCustAccountInformationDao;
    }

    public void setWarehouseDao(WarehouseDao warehouseDao) {
        this.warehouseDao = warehouseDao;
    }

    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
    }

    public void setSelectionListService(SelectionListService selectionListService) {
        this.selectionListService = selectionListService;
    }

    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public void setEnergyCompanyDao(EnergyCompanyDao ecDao) {
        this.ecDao = ecDao;
    }

    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }

    public void setEnergyCompanySettingDao(EnergyCompanySettingDao energyCompanySettingDao) {
        this.energyCompanySettingDao = energyCompanySettingDao;
    }
}