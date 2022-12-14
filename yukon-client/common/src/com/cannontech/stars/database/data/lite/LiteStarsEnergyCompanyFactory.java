package com.cannontech.stars.database.data.lite;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.WarehouseDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.service.EnergyCompanyService;

public class LiteStarsEnergyCompanyFactory {
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private EnergyCompanyService energyCompanyService;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private RoleDao roleDao;
    @Autowired private StarsCustAccountInformationDao starsCustAccountInformationDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private WarehouseDao warehouseDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private YukonGroupDao groupDao;
    @Autowired private SelectionListService selectionListService;

    public LiteStarsEnergyCompany createEnergyCompany(EnergyCompany energyCompany) {
        LiteStarsEnergyCompany liteStarsEnergyCompany = new LiteStarsEnergyCompany(energyCompany);
        applyPropertySetters(liteStarsEnergyCompany);
        return liteStarsEnergyCompany;
    }

    public LiteStarsEnergyCompany createEnergyCompany(int energyCompanyId, String name, int contactId, int userId) {
        LiteStarsEnergyCompany liteStarsEnergyCompany =
                new LiteStarsEnergyCompany(energyCompanyId, name, contactId, userId);
        applyPropertySetters(liteStarsEnergyCompany);
        return liteStarsEnergyCompany;
    }

    public LiteStarsEnergyCompany createEnergyCompany(int energyCompanyId) {
        LiteStarsEnergyCompany energyCompany = new LiteStarsEnergyCompany(energyCompanyId);
        applyPropertySetters(energyCompany);
        return energyCompany;
    }
    
    private void applyPropertySetters(final LiteStarsEnergyCompany energyCompany) {
        energyCompany.setDbPersistentDao(dbPersistentDao);
        energyCompany.setDbChangeManager(dbChangeManager);
        energyCompany.setEcMappingDao(ecMappingDao);
        energyCompany.setStarsCustAccountInformationDao(starsCustAccountInformationDao);
        energyCompany.setStarsDatabaseCache(starsDatabaseCache);
        energyCompany.setWarehouseDao(warehouseDao);
        energyCompany.setYukonGroupDao(groupDao);
        energyCompany.setSelectionListService(selectionListService);
        energyCompany.setEnergyCompanyService(energyCompanyService);
        energyCompany.setRoleDao(roleDao);
        energyCompany.setEnergyCompanyDao(ecDao);
        energyCompany.setEnergyCompanySettingDao(ecSettingDao);

        energyCompany.initialize();

        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.ENERGY_COMPANY_SUBSTATIONS,
                                                              new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                if (event.getPrimaryKey() == energyCompany.getEnergyCompanyId()) {
                    energyCompany.resetSubstations();
                }
            }
        });

        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.APPLIANCE,
                                                              new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                energyCompany.resetApplianceCategoryList();
            }
        });
        
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.WAREHOUSE,
                                                              new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                energyCompany.clearWarehouseCache();
            }
        });

        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.ENERGY_COMPANY,
                                                              new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                energyCompany.resetEnergyCompanyInfo();
                energyCompany.clearHierarchy();
            }
        });

        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.SERVICE_COMPANY,
                                                              new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                energyCompany.resetServiceCompanyInfo();
            }
        });
    }
}