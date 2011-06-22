package com.cannontech.stars.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteSubstation;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.stars.hardware.LMHardwareBase;
import com.cannontech.database.db.stars.ECToGenericMapping;
import com.cannontech.database.db.stars.report.ServiceCompanyDesignationCode;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.roles.YukonGroupRoleDefs;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.action.HardwareAction;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventory;

public class StarsAdminUtil {

	public static final String ENERGY_COMPANY_TEMP = "ENERGY_COMPANY_TEMP";
	public static final String SERVICE_COMPANY_TEMP = "SERVICE_COMPANY_TEMP";
	
	public static final String FIRST_TIME = "FIRST_TIME";

	public static void deleteApplianceCategory(int appCatID, LiteStarsEnergyCompany energyCompany, LiteYukonUser user)
		throws TransactionException
	{
		LiteApplianceCategory liteAppCat = energyCompany.getApplianceCategory(appCatID);

        // Delete all appliances in the category
        com.cannontech.database.db.stars.appliance.ApplianceBase.deleteAppliancesByCategory( appCatID );
        
		// Delete all programs in the category
		Iterable<LiteLMProgramWebPublishing> publishedPrograms = liteAppCat.getPublishedPrograms();
		for (LiteLMProgramWebPublishing program : publishedPrograms) {
		    deleteLMProgramWebPublishing(program.getProgramID(), energyCompany, user);
		}
		
		// Delete the appCategory
		com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
				new com.cannontech.database.data.stars.appliance.ApplianceCategory();
		StarsLiteFactory.setApplianceCategory( appCat.getApplianceCategory(), liteAppCat );
		Transaction.createTransaction( Transaction.DELETE, appCat ).execute();
		
		energyCompany.deleteApplianceCategory( liteAppCat.getApplianceCategoryID() );
		StarsDatabaseCache.getInstance().deleteWebConfiguration( liteAppCat.getWebConfigurationID() );
	}

	public static void deleteLMProgramWebPublishing(int programID, LiteStarsEnergyCompany energyCompany, LiteYukonUser user) 
	    throws TransactionException {
	    LiteLMProgramWebPublishing liteProg = energyCompany.getProgram(programID);
        // Delete all events of this program
        com.cannontech.database.data.stars.event.LMProgramEvent.deleteAllLMProgramEvents( liteProg.getProgramID() );

        // Set ProgramID = 0 for all appliances assigned to this program
        com.cannontech.database.db.stars.appliance.ApplianceBase.resetAppliancesByProgram( liteProg.getProgramID() );

        // Reset Enrollment, OptOut entries for the program
        LMHardwareControlGroupDao lmHardwareControlGroupDao = YukonSpringHook.getBean("lmHardwareControlGroupDao", LMHardwareControlGroupDao.class);
        lmHardwareControlGroupDao.resetEntriesForProgram(programID, user);
        
        com.cannontech.database.data.stars.LMProgramWebPublishing pubProg =
            new com.cannontech.database.data.stars.LMProgramWebPublishing();
        pubProg.setProgramID( new Integer(liteProg.getProgramID()) );
        pubProg.getLMProgramWebPublishing().setWebSettingsID( new Integer(liteProg.getWebSettingsID()) );

        Transaction.createTransaction( Transaction.DELETE, pubProg ).execute();

        energyCompany.deleteProgram( liteProg.getProgramID() );
        StarsDatabaseCache.getInstance().deleteWebConfiguration( liteProg.getWebSettingsID() );	    
	}
	
	public static LiteServiceCompany createServiceCompany(String companyName, LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		com.cannontech.database.data.stars.report.ServiceCompany company =
				new com.cannontech.database.data.stars.report.ServiceCompany();
		com.cannontech.database.db.stars.report.ServiceCompany companyDB = company.getServiceCompany();
		
		companyDB.setCompanyName( companyName );
		company.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
		
		company = Transaction.createTransaction( Transaction.INSERT, company ).execute();
		
		LiteContact liteContact = (LiteContact) StarsLiteFactory.createLite(company.getPrimaryContact());
		ServerUtils.handleDBChange( liteContact, DbChangeType.ADD );
		
		LiteServiceCompany liteCompany = (LiteServiceCompany) StarsLiteFactory.createLite( companyDB );
		energyCompany.addServiceCompany( liteCompany );
		return liteCompany;
	}
	
	public static void deleteServiceCompany(int companyID, LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		// set InstallationCompanyID = 0 for all inventory assigned to this service company
		com.cannontech.database.db.stars.hardware.InventoryBase.resetInstallationCompany( companyID );
		
        List<LiteStarsEnergyCompany> descendants = ECUtils.getAllDescendants( energyCompany );
		for (int i = 0; i < descendants.size(); i++) {
			// set ServiceCompanyID = 0 for all work orders assigned to this service company
			com.cannontech.database.db.stars.report.WorkOrderBase.resetServiceCompany( companyID );
		}
        
        /**
         * Needed to delete any zip codes if they exist
         */
        List<ServiceCompanyDesignationCode> codes = ServiceCompanyDesignationCode.getServiceCompanyDesignationCodes(companyID);
        for(int x = 0; x < codes.size(); x++)
        {
            Transaction.createTransaction(Transaction.DELETE, codes.get(x)).execute();
        }
		
		LiteServiceCompany liteCompany = energyCompany.getServiceCompany( companyID );
		
		com.cannontech.database.data.stars.report.ServiceCompany servCompany =
				new com.cannontech.database.data.stars.report.ServiceCompany();
		StarsLiteFactory.setServiceCompany( servCompany, liteCompany );

		LiteContact liteContact = DaoFactory.getContactDao().getContact( liteCompany.getPrimaryContactID() );
		
		Transaction.createTransaction( Transaction.DELETE, servCompany ).execute();
		
		energyCompany.deleteServiceCompany( companyID );
		
		ServerUtils.handleDBChange( liteContact, DbChangeType.DELETE );
	}
	
	public static void deleteAllServiceCompanies(LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
        List<LiteServiceCompany> companies = energyCompany.getServiceCompanies();
		for (int i = companies.size() - 1; i >= 0; i--) {
			LiteServiceCompany liteCompany = companies.get(i);
			deleteServiceCompany( liteCompany.getCompanyID(), energyCompany );
		}
	}
	
	public static LiteSubstation createSubstation(String subName, LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		com.cannontech.database.data.stars.Substation sub = new com.cannontech.database.data.stars.Substation();
		com.cannontech.database.db.stars.Substation subDB = sub.getSubstation();
		
		subDB.setSubstationName( subName );
		sub.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
		
		sub = Transaction.createTransaction( Transaction.INSERT, sub ).execute();
		
		LiteSubstation liteSub = (LiteSubstation) StarsLiteFactory.createLite( subDB );
		energyCompany.addSubstation( liteSub );
		return liteSub;
	}
	
	public static void deleteSubstation(int subID, LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
		// set SubstationID = 0 for all sites using this substation
		com.cannontech.database.db.stars.customer.SiteInformation.resetSubstation( subID );
		
		LiteSubstation liteSub = energyCompany.getSubstation( subID );
		
		com.cannontech.database.data.stars.Substation sub = new com.cannontech.database.data.stars.Substation();
		StarsLiteFactory.setSubstation( sub.getSubstation(), liteSub );
		Transaction.createTransaction( Transaction.DELETE, sub ).execute();
		
		energyCompany.deleteSubstation( subID );
	}
	
	public static void deleteAllSubstations(LiteStarsEnergyCompany energyCompany)
		throws TransactionException
	{
        List<LiteSubstation> substations = energyCompany.getSubstations();
		for (int i = substations.size() - 1; i >= 0; i--) {
			LiteSubstation liteSub = substations.get(i);
			deleteSubstation( liteSub.getSubstationID(), energyCompany );
		}
	}

	private static String getReferenceColumn(String listName) {
		if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE))
			return "VoltageID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE))
			return "LMHardwareTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE))
			return "CallTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE))
			return "WorkTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER))
			return "ManufactureID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION))
			return "LocationID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL))
			return "ChanceOfControlID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_RESIDENCE_TYPE))
			return "ResidenceTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_CONSTRUCTION_MATERIAL))
			return "ConstructionMaterialID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DECADE_BUILT))
			return "DecadeBuiltID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_SQUARE_FEET))
			return "SquareFeetID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_INSULATION_DEPTH))
			return "InsulationDepthID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GENERAL_CONDITION))
			return "GeneralConditionID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_COOLING_SYSTEM))
			return "MainCoolingSystemID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HEATING_SYSTEM))
			return "MainHeatingSystemID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_OCCUPANTS))
			return "NumberOfOccupantsID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_OWNERSHIP_TYPE))
			return "OwnershipTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_FUEL_TYPE))
			return "MainFuelTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_AC_TONNAGE))
			return "TonnageID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE))
			return "TypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_WH_NUM_OF_GALLONS))
			return "NumberOfGallonsID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_WH_ENERGY_SOURCE))
			return "EnergySourceId";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_WH_LOCATION))
			return null;
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DF_SECONDARY_SOURCE))
			return "SecondaryEnergySourceID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DF_SWITCH_OVER_TYPE))
			return "SwitchOverTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GRAIN_DRYER_TYPE))
			return "DryerTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_BIN_SIZE))
			return "BinSizeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_ENERGY_SOURCE))
			return "BlowerEnergySourceID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_HEAT_SOURCE))
			return "BlowerHeatSourceID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_HORSE_POWER))
			return "BlowerHorsePowerID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_HEAT_TYPE))
			return "StorageTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_SIZE))
			return "PumpSizeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_TYPE))
			return "PumpTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HP_STANDBY_SOURCE))
			return "StandbySourceID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_ENERGY_SOURCE))
			return "EnergySourceID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_HORSE_POWER))
			return "HorsePowerID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_LOCATION))
			return "MeterLocationID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_VOLTAGE))
			return "MeterVoltageID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_SOIL_TYPE))
			return "SoilTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE))
			return "IrrigationTypeID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_MFG))
			return "TransferSwitchMfgID";
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_TYPE))
			return "TransferSwitchTypeID";
		return null;
	}
	
	private static String[] getReferenceTableConstraint(String listName) {
		if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE))
			return new String[] {
				"InventoryBase",
				"InventoryID IN (SELECT InventoryID FROM ECToInventoryMapping WHERE EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE))
			return new String[] {
				"LMHardwareBase",
				"InventoryID IN (SELECT InventoryID FROM ECToInventoryMapping WHERE EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE))
			return new String[] {
				"CallReportBase",
				"AccountID IN (SELECT AccountID FROM ECToAccountMapping WHERE EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE))
			return new String[] {
				"WorkOrderBase",
				"OrderID IN (SELECT WorkOrderID FROM ECToWorkOrderMapping WHERE EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION))
			return new String[] {
				"ApplianceBase",
				"AccountID IN (SELECT AccountID FROM ECToAccountMapping WHERE EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL))
			return new String[] {
				"LMProgramWebPublishing",
				"ApplianceCategoryID IN (SELECT ItemID FROM ECToGenericMapping " +
					"WHERE MappingCategory = 'ApplianceCategory' AND EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_RESIDENCE_TYPE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_CONSTRUCTION_MATERIAL)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DECADE_BUILT)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_SQUARE_FEET)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_INSULATION_DEPTH)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GENERAL_CONDITION)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_COOLING_SYSTEM)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HEATING_SYSTEM)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_OCCUPANTS)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_OWNERSHIP_TYPE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_FUEL_TYPE))
			return new String[] {
				"CustomerResidence",
				"AccountSiteID IN (SELECT AccountSiteID FROM CustomerAccount acc, ECToAccountMapping map " +
					"WHERE acc.AccountID = map.AccountID AND map.EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_AC_TONNAGE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE))
			return new String[] {
				"ApplianceAirConditioner",
				"ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map " +
					"WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_WH_NUM_OF_GALLONS)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_WH_ENERGY_SOURCE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_WH_LOCATION))
			return new String[] {
				"ApplianceWaterHeater",
				"ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map " +
					"WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DF_SECONDARY_SOURCE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DF_SWITCH_OVER_TYPE))
			return new String[] {
				"ApplianceDualFuel",
				"ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map " +
					"WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GRAIN_DRYER_TYPE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_BIN_SIZE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_ENERGY_SOURCE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_HEAT_SOURCE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GD_HORSE_POWER))
			return new String[] {
				"ApplianceGrainDryer",
				"ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map " +
					"WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_HEAT_TYPE))
			return new String[] {
				"ApplianceStorageHeat",
				"ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map " +
					"WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_SIZE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_TYPE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_HP_STANDBY_SOURCE))
			return new String[] {
				"ApplianceHeatPump",
				"ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map " +
					"WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_ENERGY_SOURCE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_HORSE_POWER)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_LOCATION)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_METER_VOLTAGE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRR_SOIL_TYPE)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE))
			return new String[] {
				"ApplianceIrrigation",
				"ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map " +
					"WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)"
			};
		else if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_MFG)
			|| listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_TYPE))
			return new String[] {
				"ApplianceGenerator",
				"ApplianceID IN (SELECT ApplianceID FROM ApplianceBase app, ECToAccountMapping map " +
					"WHERE app.AccountID = map.AccountID AND map.EnergyCompanyID = ?)"
			};
		return null;
	}
	
	public static void updateListEntryReferences(LiteStarsEnergyCompany energyCompany, YukonSelectionList newList)
		throws WebClientException, java.sql.SQLException
	{
		String columnName = getReferenceColumn( newList.getListName() );
		if (columnName == null) return;
		
		String[] res = getReferenceTableConstraint( newList.getListName() );
		String tableName = res[0];
		String constraint = res[1];
		
		String sql1 = "SELECT DISTINCT " + columnName + " FROM " + tableName + " WHERE " + constraint;
		String sql2 = "UPDATE " + tableName + " SET " + columnName + " = ? WHERE " + columnName + " = ? AND " + constraint;
		
		Map<Integer,Integer> entryIDMap = new Hashtable<Integer,Integer>();
		
        List<LiteStarsEnergyCompany> descendants = ECUtils.getAllDescendants( energyCompany );
		List<LiteStarsEnergyCompany> companies = new ArrayList<LiteStarsEnergyCompany>();
		for (int i = 0; i < descendants.size(); i++) {
			LiteStarsEnergyCompany company = descendants.get(i);
			if (company.equals(energyCompany) || company.getYukonSelectionList(newList.getListName(), false, false) == null)
				companies.add( company );
		}
		
		java.sql.Connection conn = null;
		boolean autoCommit = true;
		java.sql.PreparedStatement pstmt1 = null;
		java.sql.PreparedStatement pstmt2 = null;
        java.sql.ResultSet rset = null;
        
		try {
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			pstmt1 = conn.prepareStatement( sql1 );
			
			for (int i = 0; i < companies.size(); i++) {
				LiteStarsEnergyCompany company = companies.get(i);
				
				pstmt1.setInt( 1, company.getLiteID() );
				rset = pstmt1.executeQuery();
				
				while (rset.next()) {
					int oldEntryID = rset.getInt(1);
					if (oldEntryID == 0) continue;
					
					YukonListEntry oldEntry = DaoFactory.getYukonListDao().getYukonListEntry( oldEntryID );
					int newEntryID = 0;
					
					for (int j = 0; j < newList.getYukonListEntries().size(); j++) {
						YukonListEntry newEntry = newList.getYukonListEntries().get(j);
						if (newEntry.getYukonDefID() == oldEntry.getYukonDefID()
							&& newEntry.getEntryText().trim().equalsIgnoreCase(oldEntry.getEntryText().trim()))
						{
							newEntryID = newEntry.getEntryID();
							break;
						}
					}
					
					if (newEntryID == 0)
						throw new WebClientException("Cannot find a matching entry for \"" + oldEntry.getEntryText() + "\" in the new list");
					
					entryIDMap.put( new Integer(oldEntryID), new Integer(newEntryID) );
				}
			}
			
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit( false );
			pstmt2 = conn.prepareStatement( sql2 );
			
			try {
				for (int i = 0; i < companies.size(); i++) {
					LiteStarsEnergyCompany company = companies.get(i);
					
					Iterator<Integer> it = entryIDMap.keySet().iterator();
					while (it.hasNext()) {
						Integer oldEntryID = it.next();
						Integer newEntryID = entryIDMap.get( oldEntryID );
						pstmt2.setInt( 1, newEntryID.intValue() );
						pstmt2.setInt( 2, oldEntryID.intValue() );
						pstmt2.setInt( 3, company.getLiteID() );
						pstmt2.execute();
					}
				}
			}
			catch (java.sql.SQLException e) {
				CTILogger.error( e.getMessage(), e );
				conn.rollback();
				throw new WebClientException("Failed to update refenreces from the old list to the new list");
			}
			
			conn.commit();
		}
		finally {
            SqlUtils.close(rset);
			if (pstmt1 != null) pstmt1.close();
			if (pstmt2 != null) pstmt2.close();
			if (conn != null) {
				conn.setAutoCommit( autoCommit );
				conn.close();
			} 
		}
	}
	
	public static void removeRoute(LiteStarsEnergyCompany energyCompany, int routeId)
	    throws TransactionException {
	    List<Integer> routeIds = energyCompany.getRouteIDs();
	    if (!routeIds.contains(routeId)) return;

	    StarsSearchDao starsSearchDao = YukonSpringHook.getBean(StarsSearchDao.class);
	    DBPersistentDao dbPersistentDao = YukonSpringHook.getBean(DBPersistentDao.class);
	    List<LiteStarsLMHardware> inventory = 
	        starsSearchDao.searchLMHardwareByRoute(routeId, 
	                                               Collections.singletonList(energyCompany));
	    for (LiteStarsLMHardware liteHw : inventory) {

	        LMHardwareBase hw = new LMHardwareBase();
	        StarsLiteFactory.setLMHardwareBase(hw, liteHw);
	        hw.getLMHardwareBase().setRouteID(CtiUtilities.NONE_ZERO_ID);

	        dbPersistentDao.performDBChange(hw.getLMHardwareBase(), TransactionType.UPDATE);
	        liteHw.setRouteID( CtiUtilities.NONE_ZERO_ID );

	        if (liteHw.getAccountID() > 0) {
	            StarsCustAccountInformation starsAcctInfo = 
	                energyCompany.getStarsCustAccountInformation(liteHw.getAccountID());
	            if (starsAcctInfo != null) {
	                StarsInventory starsInv = StarsLiteFactory.createStarsInventory(liteHw, 
	                                                                                energyCompany);
	                HardwareAction.removeRouteResponse(liteHw.getInventoryID(), 
	                                                   starsInv, 
	                                                   starsAcctInfo, 
	                                                   null);
	            }
	        }
	    }

	    ECToGenericMapping map = new ECToGenericMapping();
	    map.setEnergyCompanyID(energyCompany.getEnergyCompanyId());
	    map.setItemID(routeId);
	    map.setMappingCategory(ECToGenericMapping.MAPPING_CATEGORY_ROUTE);
	    dbPersistentDao.performDBChangeWithNoMsg(map, TransactionType.DELETE);
	    dbPersistentDao.processDatabaseChange(DbChangeType.DELETE, DbChangeCategory.ENERGY_COMPANY_ROUTE, energyCompany.getEnergyCompanyId());
	}
	
	public static void addMember(LiteStarsEnergyCompany energyCompany, LiteStarsEnergyCompany member, int loginID) throws TransactionException {
		ECToGenericMapping map = new ECToGenericMapping();
		map.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
		map.setItemID( member.getEnergyCompanyId() );
		map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER );
		Transaction.createTransaction( Transaction.INSERT, map ).execute();
		
		if (loginID != -1) {
			map.setItemID( new Integer(loginID) );
			map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER_LOGIN );
			Transaction.createTransaction( Transaction.INSERT, map ).execute();
		}
		
		energyCompany.clearHierarchy();
		member.clearHierarchy();
		
		// Modify energy company hierarchy related role properties
		LiteYukonGroup adminGroup = energyCompany.getOperatorAdminGroup();
		boolean adminGroupUpdated = false;
		adminGroupUpdated |= DaoFactory.getRoleDao().updateGroupRoleProperty( adminGroup, EnergyCompanyRole.ROLEID, EnergyCompanyRole.SINGLE_ENERGY_COMPANY, CtiUtilities.FALSE_STRING );
		adminGroupUpdated |= DaoFactory.getRoleDao().updateGroupRoleProperty( adminGroup, AdministratorRole.ROLEID, AdministratorRole.ADMIN_MANAGE_MEMBERS, CtiUtilities.TRUE_STRING );
		
		if (adminGroupUpdated)
			ServerUtils.handleDBChange( adminGroup, DbChangeType.UPDATE );
		
		adminGroup = member.getOperatorAdminGroup();
		String value = null;
		if (DaoFactory.getRoleDao().updateGroupRoleProperty( adminGroup, EnergyCompanyRole.ROLEID, EnergyCompanyRole.SINGLE_ENERGY_COMPANY, CtiUtilities.FALSE_STRING)
			|| ((value = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING )) != null
				&& DaoFactory.getRoleDao().updateGroupRoleProperty( adminGroup, EnergyCompanyRole.ROLEID, EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING, value ))
			|| ((value = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.OPTIONAL_PRODUCT_DEV )) != null
				&& DaoFactory.getRoleDao().updateGroupRoleProperty( adminGroup, EnergyCompanyRole.ROLEID, EnergyCompanyRole.OPTIONAL_PRODUCT_DEV, value )))
			ServerUtils.handleDBChange( adminGroup, DbChangeType.UPDATE );
	}
	
	public static void removeMember(LiteStarsEnergyCompany energyCompany, int memberID) throws TransactionException {
        List<Integer> loginIDs = energyCompany.getMemberLoginIDs();
		
		Iterator<LiteStarsEnergyCompany> it = energyCompany.getChildren().iterator();
		while (it.hasNext()) {
			LiteStarsEnergyCompany member = it.next();
			if (memberID != -1 && member.getLiteID() != memberID) continue;
			
			ECToGenericMapping map = new ECToGenericMapping();
			map.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
			map.setItemID( member.getEnergyCompanyId() );
			map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER );
			Transaction.createTransaction( Transaction.DELETE, map ).execute();
			
			member.clearHierarchy();
			
			for (int i = 0; i < loginIDs.size(); i++) {
				Integer loginID = loginIDs.get(i);
				LiteYukonUser liteUser = DaoFactory.getYukonUserDao().getLiteYukonUser( loginID.intValue() );
				
				if (DaoFactory.getEnergyCompanyDao().getEnergyCompany( liteUser ).getEnergyCompanyID() == member.getLiteID()) {
					map.setItemID( loginID );
					map.setMappingCategory( ECToGenericMapping.MAPPING_CATEGORY_MEMBER_LOGIN );
					Transaction.createTransaction( Transaction.DELETE, map ).execute();
					break;
				}
			}
		}
		
		energyCompany.clearHierarchy();
	}
	
	public static LiteYukonGroup copyYukonGroup(LiteYukonGroup srcGrp, String grpName)
		throws TransactionException
	{
		com.cannontech.database.data.user.YukonGroup newGroup = new com.cannontech.database.data.user.YukonGroup();
		com.cannontech.database.db.user.YukonGroup groupDB = newGroup.getYukonGroup();
		
		groupDB.setGroupName( grpName );
		groupDB.setGroupDescription( srcGrp.getGroupDescription() );
		
        Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>> roleMap = DefaultDatabaseCache.getInstance().getYukonGroupRolePropertyMap().get( srcGrp );
		Iterator<Map<LiteYukonRoleProperty, String>> roleIt = roleMap.values().iterator();
		while (roleIt.hasNext()) {
            Map<LiteYukonRoleProperty, String> rolePropMap = roleIt.next();
			Iterator<LiteYukonRoleProperty> rolePropIt = rolePropMap.keySet().iterator();
			while (rolePropIt.hasNext()) {
				LiteYukonRoleProperty liteRoleProp = rolePropIt.next();
				String value = rolePropMap.get(liteRoleProp);
				
				com.cannontech.database.db.user.YukonGroupRole groupRole = new com.cannontech.database.db.user.YukonGroupRole();
				groupRole.setRoleID( new Integer(liteRoleProp.getRoleID()) );
				groupRole.setRolePropertyID( new Integer(liteRoleProp.getRolePropertyID()) );
				groupRole.setValue( value );
				newGroup.getYukonGroupRoles().add( groupRole );
			}
		}
		
		newGroup = Transaction.createTransaction(Transaction.INSERT, newGroup).execute();
		
		LiteYukonGroup liteGroup = new LiteYukonGroup( newGroup.getGroupID().intValue() );
		ServerUtils.handleDBChange( liteGroup, DbChangeType.ADD );
		
		return DaoFactory.getRoleDao().getGroup( liteGroup.getGroupID() );
	}
	
	public static LiteYukonGroup createOperatorAdminGroup(final String grpName, final boolean topLevelEc)
		throws TransactionException
	{
		com.cannontech.database.data.user.YukonGroup adminGrp = new com.cannontech.database.data.user.YukonGroup();
		com.cannontech.database.db.user.YukonGroup groupDB = adminGrp.getYukonGroup();
		
		groupDB.setGroupName( grpName );
		groupDB.setGroupDescription( "Privilege group for the energy company's default operator login" );
		
		LiteYukonRoleProperty[] roleProps = DaoFactory.getRoleDao().getRoleProperties( EnergyCompanyRole.ROLEID );
		for (int i = 0; i < roleProps.length; i++) {
			com.cannontech.database.db.user.YukonGroupRole groupRole = new com.cannontech.database.db.user.YukonGroupRole();
			
			groupRole.setRoleID( new Integer(EnergyCompanyRole.ROLEID) );
			groupRole.setRolePropertyID( new Integer(roleProps[i].getRolePropertyID()) );
		    groupRole.setValue(" ");	// default value is a single space
			
			adminGrp.getYukonGroupRoles().add( groupRole );
		}
		
		roleProps = DaoFactory.getRoleDao().getRoleProperties( AdministratorRole.ROLEID );
		for (int i = 0; i < roleProps.length; i++) {
			com.cannontech.database.db.user.YukonGroupRole groupRole = new com.cannontech.database.db.user.YukonGroupRole();
			
			groupRole.setRoleID( new Integer(AdministratorRole.ROLEID) );
			groupRole.setRolePropertyID( new Integer(roleProps[i].getRolePropertyID()) );
			YukonRoleProperty roleProperty = YukonRoleProperty.getForId(roleProps[i].getRolePropertyID());
            if (topLevelEc && roleProperty == YukonRoleProperty.ADMIN_EDIT_ENERGY_COMPANY) {
                groupRole.setValue(CtiUtilities.TRUE_STRING);
            } else {
				groupRole.setValue(" ");	// default value is a single space
            }
			
			adminGrp.getYukonGroupRoles().add( groupRole );
		}
		
		adminGrp = Transaction.createTransaction(Transaction.INSERT, adminGrp).execute();
		
		LiteYukonGroup liteGroup = new LiteYukonGroup( adminGrp.getGroupID().intValue() );
		ServerUtils.handleDBChange( liteGroup, DbChangeType.ADD );
		
		return DaoFactory.getRoleDao().getGroup( liteGroup.getGroupID() );
	}
	
	public static LiteYukonUser createOperatorLogin(String username, String password, LoginStatusEnum status, LiteYukonGroup[] operGroups,
		LiteStarsEnergyCompany energyCompany) throws TransactionException, WebClientException, CommandExecutionException
	{
	    AuthenticationService authenticationService = (AuthenticationService) YukonSpringHook.getBean("authenticationService");
	    RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);

	    AuthType defaultAuthType = rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.DEFAULT_AUTH_TYPE, AuthType.class, null );
		if (username.length() == 0)
			throw new WebClientException( "Username cannot be empty" );
		if (password.length() == 0)
			throw new WebClientException( "Password cannot be empty" );
		if (DaoFactory.getYukonUserDao().findUserByUsername( username ) != null)
			throw new WebClientException( "Username already exists" );
		
		com.cannontech.database.data.user.YukonUser yukonUser = new com.cannontech.database.data.user.YukonUser();
		com.cannontech.database.db.user.YukonUser userDB = yukonUser.getYukonUser();
		
		userDB.setUsername( username );
        userDB.setAuthType(defaultAuthType);
		userDB.setLoginStatus(status);
		
		for (int i = 0; i < operGroups.length; i++) {
			com.cannontech.database.db.user.YukonGroup group =
					new com.cannontech.database.db.user.YukonGroup();
			group.setGroupID( new Integer(operGroups[i].getGroupID()) );
			yukonUser.getYukonGroups().add( group );
		}
		
		yukonUser = Transaction.createTransaction(Transaction.INSERT, yukonUser).execute();
		
		if (energyCompany != null) {
			SqlStatement stmt = new SqlStatement(
					"INSERT INTO EnergyCompanyOperatorLoginList VALUES(" +
						energyCompany.getEnergyCompanyId() + ", " + userDB.getUserID() + ")",
					CtiUtilities.getDatabaseAlias()
					);
			stmt.execute();
		}
		
		LiteYukonUser liteUser = new LiteYukonUser(
				userDB.getUserID().intValue(),
				userDB.getUsername(),
				userDB.getLoginStatus()
				);
        liteUser.setAuthType(defaultAuthType);
		ServerUtils.handleDBChange( liteUser, DbChangeType.ADD );
        
        if (authenticationService.supportsPasswordSet(defaultAuthType)) {
            authenticationService.setPassword(liteUser, password);
        }
		
		return liteUser;
	}
	
	public static void updateLogin(LiteYukonUser liteUser, String username, String password, LoginStatusEnum status,
		LiteYukonGroup loginGroup, LiteStarsEnergyCompany energyCompany, boolean authTypeChange) 
	throws WebClientException, TransactionException {
	    AuthenticationService authenticationService = (AuthenticationService) YukonSpringHook.getBean("authenticationService");
	    
		if (!liteUser.getUsername().equalsIgnoreCase(username) && DaoFactory.getYukonUserDao().findUserByUsername(username) != null)
			throw new WebClientException( "Username '" + username + "' already exists" );
		
		//only update try to update the password if specified
		if (password.length() != 0) {
			if (!authenticationService.supportsPasswordSet(liteUser.getAuthType())) {
                throw new WebClientException( "Password cannot be changed when authentication type is " + liteUser.getAuthType() );
            }
            authenticationService.setPassword(liteUser, password);
		}
		
		com.cannontech.database.data.user.YukonUser user = new com.cannontech.database.data.user.YukonUser();
		com.cannontech.database.db.user.YukonUser dbUser = user.getYukonUser();
		
		StarsLiteFactory.setYukonUser( dbUser, liteUser );
		dbUser.setUsername( username );
        
        if(authTypeChange) {
            liteUser.setAuthType(AuthType.NONE);
            dbUser.setAuthType(AuthType.NONE);
        }
        
		if (status != null) dbUser.setLoginStatus(status);
		
		boolean groupChanged = false;
		if (loginGroup != null) {
		    YukonGroupDao yukonGroupDao = DaoFactory.getYukonGroupDao();
		    List<LiteYukonGroup> userGroups = yukonGroupDao.getGroupsForUser(liteUser);
		    for (int i = 0; i < userGroups.size(); i++) {
		        LiteYukonGroup liteGroup = userGroups.get(i);
		        if (liteGroup.getGroupID() == YukonGroupRoleDefs.GRP_YUKON)
		            continue;
		        if (liteUser.getUserID() == energyCompany.getUser().getUserID() && liteGroup.equals(energyCompany.getOperatorAdminGroup()))
		            continue;
		        if (liteGroup.getGroupID() != loginGroup.getGroupID()) {
		            groupChanged = true;
		            break;
		        }
		    }
		}
		
		if (groupChanged) {
			com.cannontech.database.db.user.YukonGroup group =
					new com.cannontech.database.db.user.YukonGroup( new Integer(loginGroup.getGroupID()) );
			user.getYukonGroups().addElement( group );
			
			if (liteUser.getUserID() == energyCompany.getUser().getUserID()) {
				group = new com.cannontech.database.db.user.YukonGroup( new Integer(energyCompany.getOperatorAdminGroup().getGroupID()) );
				user.getYukonGroups().addElement( group );
			}
			
			Transaction.createTransaction( Transaction.UPDATE, user ).execute();
		}
		else {
			Transaction.createTransaction( Transaction.UPDATE, dbUser ).execute();
		}
		
		ServerUtils.handleDBChange( liteUser, DbChangeType.UPDATE );
	}
}
