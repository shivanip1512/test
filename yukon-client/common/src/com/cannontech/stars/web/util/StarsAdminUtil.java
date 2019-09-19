package com.cannontech.stars.web.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.ConfigurationException;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.UserGroup;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.hardware.LMHardwareBase;
import com.cannontech.stars.database.data.lite.LiteApplianceCategory;
import com.cannontech.stars.database.data.lite.LiteLMProgramWebPublishing;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteServiceCompany;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.database.db.ECToGenericMapping;
import com.cannontech.stars.database.db.report.ServiceCompanyDesignationCode;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.EcMappingCategory;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.action.HardwareAction;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.google.common.collect.Lists;

public class StarsAdminUtil {
    public static final String ROLE_GROUP_EXTENSION = "Admin Role Grp";
    public static final String USER_GROUP_EXTENSION = "Admin User Grp";

    public static final String ENERGY_COMPANY_TEMP = "ENERGY_COMPANY_TEMP";
    public static final String SERVICE_COMPANY_TEMP = "SERVICE_COMPANY_TEMP";

    public static final String FIRST_TIME = "FIRST_TIME";

    public static void deleteApplianceCategory(int appCatID, LiteStarsEnergyCompany energyCompany, LiteYukonUser user)
            throws TransactionException {
        LiteApplianceCategory liteAppCat = energyCompany.getApplianceCategory(appCatID);

        // Delete all appliances in the category
        com.cannontech.stars.database.db.appliance.ApplianceBase.deleteAppliancesByCategory(appCatID);

        // Delete all programs in the category
        Iterable<LiteLMProgramWebPublishing> publishedPrograms = liteAppCat.getPublishedPrograms();
        for (LiteLMProgramWebPublishing program : publishedPrograms) {
            deleteLMProgramWebPublishing(program.getProgramID(), energyCompany, user);
        }

        // Delete the appCategory
        com.cannontech.stars.database.data.appliance.ApplianceCategory appCat =
            new com.cannontech.stars.database.data.appliance.ApplianceCategory();
        StarsLiteFactory.setApplianceCategory(appCat.getApplianceCategory(), liteAppCat);
        Transaction.createTransaction(Transaction.DELETE, appCat).execute();

        energyCompany.deleteApplianceCategory(liteAppCat.getApplianceCategoryID());
        StarsDatabaseCache.getInstance().deleteWebConfiguration(liteAppCat.getWebConfigurationID());
    }

    public static void deleteLMProgramWebPublishing(int programID, LiteStarsEnergyCompany energyCompany,
            LiteYukonUser user) throws TransactionException {
        LiteLMProgramWebPublishing liteProg = energyCompany.getProgram(programID);
        // Delete all events of this program
        com.cannontech.stars.database.data.event.LMProgramEvent.deleteAllLMProgramEvents(liteProg.getProgramID());

        // Set ProgramID = 0 for all appliances assigned to this program
        com.cannontech.stars.database.db.appliance.ApplianceBase.resetAppliancesByProgram(liteProg.getProgramID());

        // Reset Enrollment, OptOut entries for the program
        LMHardwareControlGroupDao lmHardwareControlGroupDao =
            YukonSpringHook.getBean("lmHardwareControlGroupDao", LMHardwareControlGroupDao.class);
        lmHardwareControlGroupDao.resetEntriesForProgram(programID, user);

        com.cannontech.stars.database.data.LMProgramWebPublishing pubProg =
            new com.cannontech.stars.database.data.LMProgramWebPublishing();
        pubProg.setProgramID(new Integer(liteProg.getProgramID()));
        pubProg.getLMProgramWebPublishing().setWebSettingsID(new Integer(liteProg.getWebSettingsID()));

        Transaction.createTransaction(Transaction.DELETE, pubProg).execute();

        energyCompany.deleteProgram(liteProg.getProgramID());
        StarsDatabaseCache.getInstance().deleteWebConfiguration(liteProg.getWebSettingsID());
    }

    public static LiteServiceCompany createServiceCompany(String companyName, LiteStarsEnergyCompany energyCompany)
            throws TransactionException {
        com.cannontech.stars.database.data.report.ServiceCompany company =
            new com.cannontech.stars.database.data.report.ServiceCompany();
        com.cannontech.stars.database.db.report.ServiceCompany companyDB = company.getServiceCompany();

        companyDB.setCompanyName(companyName);
        company.setEnergyCompanyID(energyCompany.getEnergyCompanyId());

        company = Transaction.createTransaction(Transaction.INSERT, company).execute();

        LiteContact liteContact = (LiteContact) StarsLiteFactory.createLite(company.getPrimaryContact());
        handleDBChange(liteContact, DbChangeType.ADD);

        LiteServiceCompany liteCompany = (LiteServiceCompany) StarsLiteFactory.createLite(companyDB);
        energyCompany.addServiceCompany(liteCompany);
        return liteCompany;
    }

    public static void deleteServiceCompany(int companyID, LiteStarsEnergyCompany energyCompany)
            throws TransactionException {
        // set InstallationCompanyID = 0 for all inventory assigned to this service company
        com.cannontech.stars.database.db.hardware.InventoryBase.resetInstallationCompany(companyID);

        List<LiteStarsEnergyCompany> descendants = ECUtils.getAllDescendants(energyCompany);
        for (int i = 0; i < descendants.size(); i++) {
            // set ServiceCompanyID = 0 for all work orders assigned to this service company
            com.cannontech.stars.database.db.report.WorkOrderBase.resetServiceCompany(companyID);
        }

        /**
         * Needed to delete any zip codes if they exist
         */
        List<ServiceCompanyDesignationCode> codes =
            ServiceCompanyDesignationCode.getServiceCompanyDesignationCodes(companyID);
        for (int x = 0; x < codes.size(); x++) {
            Transaction.createTransaction(Transaction.DELETE, codes.get(x)).execute();
        }

        LiteServiceCompany liteCompany = energyCompany.getServiceCompany(companyID);

        com.cannontech.stars.database.data.report.ServiceCompany servCompany =
            new com.cannontech.stars.database.data.report.ServiceCompany();
        StarsLiteFactory.setServiceCompany(servCompany, liteCompany);

        LiteContact liteContact =
            YukonSpringHook.getBean(ContactDao.class).getContact(liteCompany.getPrimaryContactID());

        Transaction.createTransaction(Transaction.DELETE, servCompany).execute();

        energyCompany.deleteServiceCompany(companyID);

        handleDBChange(liteContact, DbChangeType.DELETE);
    }

    public static void deleteAllServiceCompanies(LiteStarsEnergyCompany energyCompany) throws TransactionException {
        List<LiteServiceCompany> companies = energyCompany.getServiceCompanies();
        for (int i = companies.size() - 1; i >= 0; i--) {
            LiteServiceCompany liteCompany = companies.get(i);
            deleteServiceCompany(liteCompany.getCompanyID(), energyCompany);
        }
    }

    private static String getReferenceColumn(String listName) {
        if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE)) {
            return "VoltageID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE)) {
            return "LMHardwareTypeID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE)) {
            return "CallTypeID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE)) {
            return "WorkTypeID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER)) {
            return "ManufactureID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION)) {
            return "LocationID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL)) {
            return "ChanceOfControlID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_RESIDENCE_TYPE)) {
            return "ResidenceTypeID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_CONSTRUCTION_MATERIAL)) {
            return "ConstructionMaterialID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DECADE_BUILT)) {
            return "DecadeBuiltID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_SQUARE_FEET)) {
            return "SquareFeetID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_INSULATION_DEPTH)) {
            return "InsulationDepthID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GENERAL_CONDITION)) {
            return "GeneralConditionID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_COOLING_SYSTEM)) {
            return "MainCoolingSystemID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HEATING_SYSTEM)) {
            return "MainHeatingSystemID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_OCCUPANTS)) {
            return "NumberOfOccupantsID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_OWNERSHIP_TYPE)) {
            return "OwnershipTypeID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_FUEL_TYPE)) {
            return "MainFuelTypeID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_AC_TONNAGE)) {
            return "TonnageID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE)) {
            return "TypeID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_WH_NUM_OF_GALLONS)) {
            return "NumberOfGallonsID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_WH_ENERGY_SOURCE)) {
            return "EnergySourceId";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_WH_LOCATION)) {
            return null;
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DF_SECONDARY_SOURCE)) {
            return "SecondaryEnergySourceID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DF_SWITCH_OVER_TYPE)) {
            return "SwitchOverTypeID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GRAIN_DRYER_TYPE)) {
            return "DryerTypeID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_BIN_SIZE)) {
            return "BinSizeID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_ENERGY_SOURCE)) {
            return "BlowerEnergySourceID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_HEAT_SOURCE)) {
            return "BlowerHeatSourceID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_HORSE_POWER)) {
            return "BlowerHorsePowerID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_HEAT_TYPE)) {
            return "StorageTypeID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_SIZE)) {
            return "PumpSizeID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_TYPE)) {
            return "PumpTypeID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HP_STANDBY_SOURCE)) {
            return "StandbySourceID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_ENERGY_SOURCE)) {
            return "EnergySourceID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_HORSE_POWER)) {
            return "HorsePowerID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_LOCATION)) {
            return "MeterLocationID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_VOLTAGE)) {
            return "MeterVoltageID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_SOIL_TYPE)) {
            return "SoilTypeID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE)) {
            return "IrrigationTypeID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_MFG)) {
            return "TransferSwitchMfgID";
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_TYPE)) {
            return "TransferSwitchTypeID";
        }
        return null;
    }

    private static String[] getReferenceTableConstraint(String listName) {
        if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE)) {
            return new String[] { "InventoryBase",
                "InventoryID IN (SELECT InventoryID FROM ECToInventoryMapping WHERE EnergyCompanyID = ?)" };
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE)) {
            return new String[] { "LMHardwareBase",
                "InventoryID IN (SELECT InventoryID FROM ECToInventoryMapping WHERE EnergyCompanyID = ?)" };
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE)) {
            return new String[] { "CallReportBase",
                "AccountID IN (SELECT AccountID FROM ECToAccountMapping WHERE EnergyCompanyID = ?)" };
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE)) {
            return new String[] { "WorkOrderBase",
                "OrderID IN (SELECT WorkOrderID FROM ECToWorkOrderMapping WHERE EnergyCompanyID = ?)" };
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION)) {
            return new String[] { "ApplianceBase",
                "AccountID IN (SELECT AccountID FROM ECToAccountMapping WHERE EnergyCompanyID = ?)" };
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL)) {
            return new String[] {
                "LMProgramWebPublishing",
                "ApplianceCategoryID IN (SELECT ItemID FROM ECToGenericMapping " + "WHERE MappingCategory = '"
                    + EcMappingCategory.APPLIANCE_CATEGORY.getDatabaseRepresentation() + "' "
                    + "AND EnergyCompanyID = ?)" };
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_RESIDENCE_TYPE)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_CONSTRUCTION_MATERIAL)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DECADE_BUILT)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_SQUARE_FEET)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_INSULATION_DEPTH)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GENERAL_CONDITION)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_COOLING_SYSTEM)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HEATING_SYSTEM)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_OCCUPANTS)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_OWNERSHIP_TYPE)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_FUEL_TYPE)) {
            return new String[] {
                "CustomerResidence",
                "AccountSiteID IN (SELECT AccountSiteID FROM CustomerAccount acc, ECToAccountMapping map "
                    + "WHERE acc.AccountID = map.AccountID AND map.EnergyCompanyID = ?)" };
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_AC_TONNAGE)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE)) {
            return new String[] {
                "ApplianceAirConditioner",
                "ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map "
                    + "WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)" };
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_WH_NUM_OF_GALLONS)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_WH_ENERGY_SOURCE)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_WH_LOCATION)) {
            return new String[] {
                "ApplianceWaterHeater",
                "ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map "
                    + "WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)" };
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DF_SECONDARY_SOURCE)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DF_SWITCH_OVER_TYPE)) {
            return new String[] {
                "ApplianceDualFuel",
                "ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map "
                    + "WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)" };
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GRAIN_DRYER_TYPE)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_BIN_SIZE)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_ENERGY_SOURCE)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_HEAT_SOURCE)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_HORSE_POWER)) {
            return new String[] {
                "ApplianceGrainDryer",
                "ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map "
                    + "WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)" };
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_HEAT_TYPE)) {
            return new String[] {
                "ApplianceStorageHeat",
                "ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map "
                    + "WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)" };
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_SIZE)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_TYPE)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HP_STANDBY_SOURCE)) {
            return new String[] {
                "ApplianceHeatPump",
                "ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map "
                    + "WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)" };
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_ENERGY_SOURCE)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_HORSE_POWER)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_LOCATION)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_VOLTAGE)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_SOIL_TYPE)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE)) {
            return new String[] {
                "ApplianceIrrigation",
                "ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map "
                    + "WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)" };
        } else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_MFG)
            || listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_TYPE)) {
            return new String[] {
                "ApplianceGenerator",
                "ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map "
                    + "WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)" };
        }
        return null;
    }

    public static void updateListEntryReferences(LiteStarsEnergyCompany energyCompany, YukonSelectionList newList)
            throws WebClientException, java.sql.SQLException {
        String columnName = getReferenceColumn(newList.getListName());
        if (columnName == null) {
            return;
        }

        String[] res = getReferenceTableConstraint(newList.getListName());
        String tableName = res[0];
        String constraint = res[1];

        String sql1 = "SELECT DISTINCT " + columnName + " FROM " + tableName + " WHERE " + constraint;
        String sql2 =
            "UPDATE " + tableName + " SET " + columnName + " = ? WHERE " + columnName + " = ? AND " + constraint;

        Map<Integer, Integer> entryIDMap = new Hashtable<Integer, Integer>();

        List<LiteStarsEnergyCompany> descendants = ECUtils.getAllDescendants(energyCompany);
        List<LiteStarsEnergyCompany> companies = new ArrayList<LiteStarsEnergyCompany>();
        SelectionListService selectionListService = YukonSpringHook.getBean(SelectionListService.class);
        for (int i = 0; i < descendants.size(); i++) {
            LiteStarsEnergyCompany company = descendants.get(i);
            if (company.equals(energyCompany)
                || selectionListService.getSelectionList(company, newList.getListName(), false, false) == null) {
                companies.add(company);
            }
        }

        java.sql.Connection conn = null;
        boolean autoCommit = true;
        java.sql.PreparedStatement pstmt1 = null;
        java.sql.PreparedStatement pstmt2 = null;
        java.sql.ResultSet rset = null;

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

            pstmt1 = conn.prepareStatement(sql1);

            for (int i = 0; i < companies.size(); i++) {
                LiteStarsEnergyCompany company = companies.get(i);

                pstmt1.setInt(1, company.getLiteID());
                rset = pstmt1.executeQuery();

                while (rset.next()) {
                    int oldEntryID = rset.getInt(1);
                    if (oldEntryID == 0) {
                        continue;
                    }

                    YukonListEntry oldEntry = YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(oldEntryID);
                    int newEntryID = 0;

                    for (int j = 0; j < newList.getYukonListEntries().size(); j++) {
                        YukonListEntry newEntry = newList.getYukonListEntries().get(j);
                        if (newEntry.getYukonDefID() == oldEntry.getYukonDefID()
                            && newEntry.getEntryText().trim().equalsIgnoreCase(oldEntry.getEntryText().trim())) {
                            newEntryID = newEntry.getEntryID();
                            break;
                        }
                    }

                    if (newEntryID == 0) {
                        throw new WebClientException("Cannot find a matching entry for \"" + oldEntry.getEntryText()
                            + "\" in the new list");
                    }

                    entryIDMap.put(new Integer(oldEntryID), new Integer(newEntryID));
                }
            }

            autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            pstmt2 = conn.prepareStatement(sql2);

            try {
                for (int i = 0; i < companies.size(); i++) {
                    LiteStarsEnergyCompany company = companies.get(i);

                    Iterator<Integer> it = entryIDMap.keySet().iterator();
                    while (it.hasNext()) {
                        Integer oldEntryID = it.next();
                        Integer newEntryID = entryIDMap.get(oldEntryID);
                        pstmt2.setInt(1, newEntryID.intValue());
                        pstmt2.setInt(2, oldEntryID.intValue());
                        pstmt2.setInt(3, company.getLiteID());
                        pstmt2.execute();
                    }
                }
            } catch (java.sql.SQLException e) {
                CTILogger.error(e.getMessage(), e);
                conn.rollback();
                throw new WebClientException("Failed to update refenreces from the old list to the new list");
            }

            conn.commit();
        } finally {
            SqlUtils.close(rset);
            if (pstmt1 != null) {
                pstmt1.close();
            }
            if (pstmt2 != null) {
                pstmt2.close();
            }
            if (conn != null) {
                conn.setAutoCommit(autoCommit);
                conn.close();
            }
        }
    }

    public static void removeRoute(LiteStarsEnergyCompany energyCompany, int routeId) {
        EnergyCompanyDao ecDao = YukonSpringHook.getBean(EnergyCompanyDao.class);
        List<Integer> routeIds = ecDao.getRouteIds(energyCompany.getEnergyCompanyId());
        if (!routeIds.contains(routeId)) {
            return;
        }

        StarsSearchDao starsSearchDao = YukonSpringHook.getBean(StarsSearchDao.class);
        DBPersistentDao dbPersistentDao = YukonSpringHook.getBean(DBPersistentDao.class);
        List<YukonEnergyCompany> ecList = Lists.newArrayList();
        ecList.add(energyCompany);
        List<LiteLmHardwareBase> inventory = starsSearchDao.searchLMHardwareByRoute(routeId, ecList);
        for (LiteLmHardwareBase liteHw : inventory) {

            LMHardwareBase hw = new LMHardwareBase();
            StarsLiteFactory.setLMHardwareBase(hw, liteHw);
            hw.getLMHardwareBase().setRouteID(CtiUtilities.NONE_ZERO_ID);

            dbPersistentDao.performDBChange(hw.getLMHardwareBase(), TransactionType.UPDATE);
            liteHw.setRouteID(CtiUtilities.NONE_ZERO_ID);

            if (liteHw.getAccountID() > 0) {
                StarsCustAccountInformation starsAcctInfo =
                    energyCompany.getStarsCustAccountInformation(liteHw.getAccountID());
                if (starsAcctInfo != null) {
                    StarsInventory starsInv = StarsLiteFactory.createStarsInventory(liteHw, energyCompany);
                    HardwareAction.removeRouteResponse(liteHw.getInventoryID(), starsInv, starsAcctInfo);
                }
            }
        }
        ECMappingDao ecMappingDao = YukonSpringHook.getBean(ECMappingDao.class);
        ecMappingDao.deleteECToRouteMapping(energyCompany.getEnergyCompanyId(), routeId);

        DbChangeManager dbChangeManager = YukonSpringHook.getBean(DbChangeManager.class);
        dbChangeManager.processDbChange(DbChangeType.DELETE, DbChangeCategory.ENERGY_COMPANY_ROUTE,
            energyCompany.getEnergyCompanyId());
    }

    public static void addMember(LiteStarsEnergyCompany energyCompany, LiteStarsEnergyCompany member, int loginID,
            LiteYukonUser user) throws TransactionException {
        EnergyCompany energyCompanyMap = new EnergyCompany();
        energyCompanyMap.setName(member.getName());
        energyCompanyMap.setPrimaryContactId(member.getEnergyCompanyId());
        energyCompanyMap.setUserId(member.getUser().getUserID());
        energyCompanyMap.setEnergyCompanyId(member.getEnergyCompanyId());
        energyCompanyMap.setParentEnergyCompanyId(energyCompany.getEnergyCompanyId());
        energyCompanyMap.setPrimaryContactId(member.getPrimaryContactID());
        Transaction.createTransaction(Transaction.UPDATE, energyCompanyMap).execute();

        if (loginID != -1) {
            ECToGenericMapping map = new ECToGenericMapping();
            map.setEnergyCompanyID(energyCompany.getEnergyCompanyId());
            map.setItemID(new Integer(loginID));
            map.setMappingCategory(EcMappingCategory.MEMBER_LOGIN);
            Transaction.createTransaction(Transaction.INSERT, map).execute();
        }

        energyCompany.clearHierarchy();
        member.clearHierarchy();

        // Modify energy company hierarchy related role properties
        EnergyCompanySettingDao energyCompanySettingDao = YukonSpringHook.getBean(EnergyCompanySettingDao.class);

        energyCompanySettingDao.updateSettingValue(EnergyCompanySettingType.SINGLE_ENERGY_COMPANY, false, user,
            energyCompany.getEnergyCompanyId());
        energyCompanySettingDao.updateSettingValue(EnergyCompanySettingType.SINGLE_ENERGY_COMPANY, false, user,
            member.getEnergyCompanyId());

        if (energyCompany.getOperatorAdminGroup() != null) {
            YukonSpringHook.getBean(RoleDao.class).updateGroupRoleProperty(energyCompany.getOperatorAdminGroup(),
                YukonRole.OPERATOR_ADMINISTRATOR.getRoleId(), YukonRoleProperty.ADMIN_MANAGE_MEMBERS.getPropertyId(),
                CtiUtilities.TRUE_STRING);
        }

        handleDBChange(energyCompany.getOperatorAdminGroup(), DbChangeType.UPDATE);

        // Get parents settings
        Boolean parentValue =
            energyCompanySettingDao.getBoolean(EnergyCompanySettingType.TRACK_HARDWARE_ADDRESSING,
                energyCompany.getEnergyCompanyId());
        String parentValueStr =
            energyCompanySettingDao.getString(EnergyCompanySettingType.OPTIONAL_PRODUCT_DEV,
                energyCompany.getEnergyCompanyId());

        // Transfer them over to child energy company
        energyCompanySettingDao.updateSettingValue(EnergyCompanySettingType.TRACK_HARDWARE_ADDRESSING, parentValue,
            user, member.getEnergyCompanyId());
        energyCompanySettingDao.updateSettingValue(EnergyCompanySettingType.OPTIONAL_PRODUCT_DEV, parentValueStr, user,
            member.getEnergyCompanyId());

        handleDBChange(member.getOperatorAdminGroup(), DbChangeType.UPDATE);
    }

    public static void removeMember(LiteStarsEnergyCompany energyCompany, int memberID) throws TransactionException {
        List<Integer> loginIDs = energyCompany.getMemberLoginIDs();

        Iterator<LiteStarsEnergyCompany> it = energyCompany.getChildren().iterator();
        while (it.hasNext()) {
            LiteStarsEnergyCompany member = it.next();
            if (memberID != -1 && member.getLiteID() != memberID) {
                continue;
            }

            EnergyCompany energyComp = new EnergyCompany();
            energyComp.setName(member.getName());
            energyComp.setUserId(member.getUser().getUserID());
            energyComp.setEnergyCompanyId(member.getEnergyCompanyId());
            energyComp.setPrimaryContactId(member.getPrimaryContactID());
            Transaction.createTransaction(Transaction.UPDATE, energyComp).execute();

            member.clearHierarchy();

            for (int i = 0; i < loginIDs.size(); i++) {
                Integer loginID = loginIDs.get(i);
                LiteYukonUser liteUser =
                    YukonSpringHook.getBean(YukonUserDao.class).getLiteYukonUser(loginID.intValue());

                if (YukonSpringHook.getBean(EnergyCompanyDao.class).getEnergyCompany(liteUser).getId() == member.getLiteID()) {
                    ECToGenericMapping map = new ECToGenericMapping();
                    map.setEnergyCompanyID(energyCompany.getEnergyCompanyId());
                    map.setItemID(loginID);
                    map.setMappingCategory(EcMappingCategory.MEMBER_LOGIN);
                    Transaction.createTransaction(Transaction.DELETE, map).execute();
                    break;
                }
            }
        }

        energyCompany.clearHierarchy();
    }

    public static LiteYukonGroup copyYukonGroup(LiteYukonGroup srcGrp, String grpName) throws TransactionException,
            ConfigurationException {

        com.cannontech.database.data.user.YukonGroup newGroup = new com.cannontech.database.data.user.YukonGroup();
        com.cannontech.database.db.user.YukonGroup groupDB = newGroup.getYukonGroup();

        groupDB.setGroupName(grpName);
        groupDB.setGroupDescription(srcGrp.getGroupDescription());

        Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>> roleMap =
            DefaultDatabaseCache.getInstance().getYukonGroupRolePropertyMap().get(srcGrp);
        Iterator<Map<LiteYukonRoleProperty, String>> roleIt = roleMap.values().iterator();
        while (roleIt.hasNext()) {
            Map<LiteYukonRoleProperty, String> rolePropMap = roleIt.next();
            Iterator<LiteYukonRoleProperty> rolePropIt = rolePropMap.keySet().iterator();
            while (rolePropIt.hasNext()) {
                LiteYukonRoleProperty liteRoleProp = rolePropIt.next();
                String value = rolePropMap.get(liteRoleProp);

                com.cannontech.database.db.user.YukonGroupRole groupRole =
                    new com.cannontech.database.db.user.YukonGroupRole();
                groupRole.setRoleID(new Integer(liteRoleProp.getRoleID()));
                groupRole.setRolePropertyID(new Integer(liteRoleProp.getRolePropertyID()));
                groupRole.setValue(value);
                newGroup.addYukonGroupRole(groupRole);
            }
        }

        newGroup = Transaction.createTransaction(Transaction.INSERT, newGroup).execute();

        LiteYukonGroup liteGroup = new LiteYukonGroup(newGroup.getGroupID().intValue());
        handleDBChange(liteGroup, DbChangeType.ADD);

        return YukonSpringHook.getBean(RoleDao.class).getGroup(liteGroup.getGroupID());
    }

    public static com.cannontech.database.db.user.UserGroup createOperatorAdminUserGroup(final String userGroupName,
            int primaryOperatorUserGroupId, final boolean topLevelEc, LiteYukonUser user) throws TransactionException,
            ConfigurationException, SQLException {
        RoleDao roleDao = YukonSpringHook.getBean("roleDao", RoleDao.class);
        UserGroupDao userGroupDao = YukonSpringHook.getBean("userGroupDao", UserGroupDao.class);
        UsersEventLogService usersEventLogService = YukonSpringHook.getBean("usersEventLogService", UsersEventLogService.class);

        // Creating the admin role group for the new energy company
        com.cannontech.database.data.user.YukonGroup adminGrp = new com.cannontech.database.data.user.YukonGroup();
        com.cannontech.database.db.user.YukonGroup groupDB = adminGrp.getYukonGroup();
        groupDB.setGroupName(userGroupName + " " + ROLE_GROUP_EXTENSION);
        groupDB.setGroupDescription("Privilege group for the energy company's default operator login");

        // Including the Admin Role to the EC admin role group
        LiteYukonRoleProperty[] roleProps = roleDao.getRoleProperties(YukonRole.OPERATOR_ADMINISTRATOR.getRoleId());
        for (int i = 0; i < roleProps.length; i++) {
            com.cannontech.database.db.user.YukonGroupRole groupRole =
                new com.cannontech.database.db.user.YukonGroupRole();

            groupRole.setRoleID(YukonRole.OPERATOR_ADMINISTRATOR.getRoleId());
            groupRole.setRolePropertyID(roleProps[i].getRolePropertyID());
            YukonRoleProperty roleProperty = YukonRoleProperty.getForId(roleProps[i].getRolePropertyID());
            if (topLevelEc && roleProperty == YukonRoleProperty.ADMIN_EDIT_ENERGY_COMPANY) {
                groupRole.setValue(CtiUtilities.TRUE_STRING);
            } else {
                groupRole.setValue(" "); // default value is a single space
            }

            adminGrp.addYukonGroupRole(groupRole);
        }

        adminGrp = Transaction.createTransaction(Transaction.INSERT, adminGrp).execute();
        usersEventLogService.roleGroupCreated(adminGrp.getYukonGroup().getGroupName(), user);
        
        LiteYukonGroup ecAdminGroup =
            new LiteYukonGroup(adminGrp.getGroupID(), adminGrp.getYukonGroup().getGroupName());

        // Create the new admin user group including the links to the newly created role group.
        UserGroup userGroup = new UserGroup();
        userGroup.getUserGroup().setUserGroupName(userGroupName + " " + USER_GROUP_EXTENSION);
        userGroup.getUserGroup().setUserGroupDescription(
            "Privilege user group for the energy company's default operator login");
        userGroup.addRoleGroups(ecAdminGroup);

        // Adding the primary operator user groups role groups
        UserGroup primaryOperatorUserGroup = userGroupDao.getUserGroup(primaryOperatorUserGroupId);
        userGroup.putAllRolesToGroupMap(primaryOperatorUserGroup.getRolesToGroupMap());
        userGroup.add();
        usersEventLogService.userGroupCreated(userGroup.getUserGroup().getUserGroupName(), user);
        usersEventLogService.roleGroupAdded(userGroup.getUserGroup().getUserGroupName(),
            ecAdminGroup.getGroupName(), user);

        handleDBChange(ecAdminGroup, DbChangeType.ADD);
        return userGroup.getUserGroup();
    }

    public static LiteYukonUser createOperatorLogin(String username, String password, LoginStatusEnum status,
            com.cannontech.database.db.user.UserGroup liteUserGroup, LiteStarsEnergyCompany energyCompany,
            LiteYukonUser user)
            throws TransactionException, CommandExecutionException {
        AuthenticationService authenticationService = YukonSpringHook.getBean(AuthenticationService.class);
        ECMappingDao ecMappingDao = YukonSpringHook.getBean(ECMappingDao.class);
        UsersEventLogService usersEventLogService = YukonSpringHook.getBean("usersEventLogService", UsersEventLogService.class);
        
        com.cannontech.database.data.user.YukonUser yukonUser = new com.cannontech.database.data.user.YukonUser();
        com.cannontech.database.db.user.YukonUser userDB = yukonUser.getYukonUser();

        userDB.setUsername(username);
        userDB.setLoginStatus(status);
        userDB.setForceReset(false);
        userDB.setUserGroupId(liteUserGroup.getUserGroupId());

        yukonUser = Transaction.createTransaction(Transaction.INSERT, yukonUser).execute();
        usersEventLogService.userCreated(username, liteUserGroup.getUserGroupName(),
            energyCompany != null ? energyCompany.getName() : CtiUtilities.STRING_NONE, status, user);
        if (energyCompany != null) {
            ecMappingDao.addEnergyCompanyOperatorLoginListMapping(userDB.getUserID(),
                energyCompany.getEnergyCompanyId());
        }

        // Setting the password for the new account.
        LiteYukonUser liteUser =
            new LiteYukonUser(userDB.getUserID().intValue(), userDB.getUsername(), userDB.getLoginStatus());
        handleDBChange(liteUser, DbChangeType.ADD);

        // EC Operators are always created as Encrypted
        AuthenticationCategory authenticationCategory = AuthenticationCategory.ENCRYPTED;
        if (authenticationService.supportsPasswordSet(authenticationCategory) || !StringUtils.isBlank(password)) {
            if (StringUtils.isBlank(password)) {
                throw new IllegalArgumentException("password cannot be blank");
            }
            authenticationService.setPassword(liteUser, authenticationCategory, password, user);
        }

        return liteUser;
    }

    public static void updateLogin(LiteYukonUser liteUser, String username, String password, LoginStatusEnum status,
            LiteUserGroup userGroup, LiteYukonUser createdByUser) throws WebClientException, TransactionException {
        AuthenticationService authenticationService = YukonSpringHook.getBean(AuthenticationService.class);
        YukonUserDao yukonUserDao = YukonSpringHook.getBean(YukonUserDao.class);
        UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(liteUser.getUserID());
        UsersEventLogService usersEventLogService = YukonSpringHook.getBean("usersEventLogService", UsersEventLogService.class);
        EnergyCompanyDao energyCompanyDao = YukonSpringHook.getBean(EnergyCompanyDao.class);
        
        if (!liteUser.getUsername().equalsIgnoreCase(username) && yukonUserDao.findUserByUsername(username) != null) {
            throw new WebClientException("Username '" + username + "' already exists");
        }

        AuthenticationCategory authenticationCategory = userAuthenticationInfo.getAuthenticationCategory();
        if (StringUtils.isNotEmpty(password) && !authenticationService.supportsPasswordSet(authenticationCategory)) {
            throw new WebClientException("Password cannot be changed when authentication type is "
                + authenticationCategory);
        }

        com.cannontech.database.data.user.YukonUser user = new com.cannontech.database.data.user.YukonUser();
        com.cannontech.database.db.user.YukonUser dbUser = user.getYukonUser();

        StarsLiteFactory.setYukonUser(dbUser, liteUser);
        dbUser.setUsername(username);

        if (status != null) {
            dbUser.setLoginStatus(status);
        }

        if (userGroup != null) {
            dbUser.setUserGroupId(userGroup.getUserGroupId());
        }

        Transaction.createTransaction(Transaction.UPDATE, dbUser).execute();
        String ecName = energyCompanyDao.getEnergyCompany(createdByUser).getName();
        
        usersEventLogService.userUpdated(username,
            userGroup != null ? userGroup.getUserGroupName() : CtiUtilities.STRING_NONE, ecName, status,
            createdByUser);
        handleDBChange(liteUser, DbChangeType.UPDATE);
        // only update try to update the password if specified
        if (StringUtils.isNotEmpty(password)) {
            authenticationService.setPassword(liteUser, password, createdByUser);
        }
    }

    public static void handleDBChange(LiteBase lite, DbChangeType dbChangeType) {
        DbChangeManager dbChangeManager = YukonSpringHook.getBean(DbChangeManager.class);

        if (lite == null) {
            dbChangeManager.processDbChange(0, Integer.MAX_VALUE, "", "", dbChangeType);
        } else if (lite.getLiteType() == LiteTypes.STARS_CUST_ACCOUNT_INFO) {
            dbChangeManager.processDbChange(lite.getLiteID(), DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB,
                DBChangeMsg.CAT_CUSTOMER_ACCOUNT, DBChangeMsg.CAT_CUSTOMER_ACCOUNT, dbChangeType);
        } else if (lite.getLiteType() == LiteTypes.YUKON_USER) {
            dbChangeManager.processDbChange(lite.getLiteID(), DBChangeMsg.CHANGE_YUKON_USER_DB,
                DBChangeMsg.CAT_YUKON_USER, DBChangeMsg.CAT_YUKON_USER, dbChangeType);
        } else if (lite.getLiteType() == LiteTypes.CONTACT) {
            dbChangeManager.processDbChange(lite.getLiteID(), DBChangeMsg.CHANGE_CONTACT_DB,
                DBChangeMsg.CAT_CUSTOMERCONTACT, DBChangeMsg.CAT_CUSTOMERCONTACT, dbChangeType);
        } else if (lite.getLiteType() == LiteTypes.YUKON_GROUP) {
            dbChangeManager.processDbChange(lite.getLiteID(), DBChangeMsg.CHANGE_YUKON_USER_DB,
                DBChangeMsg.CAT_YUKON_USER_GROUP, DBChangeMsg.CAT_YUKON_USER_GROUP, dbChangeType);
        } else if (lite.getLiteType() == LiteTypes.ENERGY_COMPANY) {
            dbChangeManager.processDbChange(lite.getLiteID(), DBChangeMsg.CHANGE_ENERGY_COMPANY_DB,
                DBChangeMsg.CAT_ENERGY_COMPANY, DBChangeMsg.CAT_ENERGY_COMPANY, dbChangeType);
        } else if (lite.getLiteType() == LiteTypes.CUSTOMER) {
            dbChangeManager.processDbChange(lite.getLiteID(), DBChangeMsg.CHANGE_CUSTOMER_DB, DBChangeMsg.CAT_CUSTOMER,
                DBChangeMsg.CAT_CUSTOMER, dbChangeType);
        } else if (lite.getLiteType() == LiteTypes.CUSTOMER_CI) {
            dbChangeManager.processDbChange(lite.getLiteID(), DBChangeMsg.CHANGE_CUSTOMER_DB,
                DBChangeMsg.CAT_CI_CUSTOMER, DBChangeMsg.CAT_CI_CUSTOMER, dbChangeType);
        } else if (lite.getLiteType() == LiteTypes.STARS_SERVICE_COMPANY_DESIGNATION_CODE) {
            dbChangeManager.processDbChange(lite.getLiteID(), DBChangeMsg.CHANGE_SERVICE_COMPANY_DESIGNATION_CODE_DB,
                DBChangeMsg.CAT_SERVICE_COMPANY_DESIGNATION_CODE, DBChangeMsg.CAT_SERVICE_COMPANY_DESIGNATION_CODE,
                dbChangeType);
        }
    }
}