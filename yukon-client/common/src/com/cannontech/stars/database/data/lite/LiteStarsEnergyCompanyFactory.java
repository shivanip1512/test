package com.cannontech.stars.database.data.lite;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.core.dao.WarehouseDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.stars.service.DefaultRouteService;
import com.cannontech.stars.service.EnergyCompanyService;

public class LiteStarsEnergyCompanyFactory {
    @Autowired private AddressDao addressDao;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private DefaultRouteService defaultRouteService;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao;
    @Autowired private EnergyCompanyService energyCompanyService;
    @Autowired private PaoDao paoDao;
    @Autowired private RoleDao roleDao;
    @Autowired private StarsCustAccountInformationDao starsCustAccountInformationDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private StarsSearchDao starsSearchDao;
    @Autowired private StarsWorkOrderBaseDao starsWorkOrderBaseDao;
    @Autowired private SystemDateFormattingService systemDateFormattingService;
    @Autowired private WarehouseDao warehouseDao;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private YukonGroupDao yukonGroupDao;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private YukonListDao yukonListDao;
	
    public LiteStarsEnergyCompany createEnergyCompany(EnergyCompany energyCompany) {
        LiteStarsEnergyCompany liteStarsEnergyCompany = new LiteStarsEnergyCompany(energyCompany);
        applyPropertySetters(liteStarsEnergyCompany);
        return liteStarsEnergyCompany;
    }
    
    public LiteStarsEnergyCompany createEnergyCompany(int energyCompanyId) {
        LiteStarsEnergyCompany energyCompany = new LiteStarsEnergyCompany(energyCompanyId);
        applyPropertySetters(energyCompany);
        return energyCompany;
    }
    
    private void applyPropertySetters(final LiteStarsEnergyCompany energyCompany) {
        energyCompany.setAddressDao(addressDao);
        energyCompany.setDbPersistentDao(dbPersistentDao);
        energyCompany.setDbChangeManager(dbChangeManager);
        energyCompany.setEcMappingDao(ecMappingDao);
        energyCompany.setEnergyCompanyRolePropertyDao(energyCompanyRolePropertyDao);
        energyCompany.setDefaultRouteService(defaultRouteService);
        energyCompany.setStarsCustAccountInformationDao(starsCustAccountInformationDao);
        energyCompany.setSystemDateFormattingService(systemDateFormattingService);
        energyCompany.setStarsDatabaseCache(starsDatabaseCache);
        energyCompany.setStarsSearchDao(starsSearchDao);
        energyCompany.setStarsWorkOrderBaseDao(starsWorkOrderBaseDao);
        energyCompany.setWarehouseDao(warehouseDao);
        energyCompany.setYukonJdbcTemplate(yukonJdbcTemplate);
        energyCompany.setYukonGroupDao(yukonGroupDao);
        energyCompany.setYukonListDao(yukonListDao);
        energyCompany.setPaoDao(paoDao);
        energyCompany.setEnergyCompanyDao(energyCompanyDao);
        energyCompany.setEnergyCompanyService(energyCompanyService);
        energyCompany.setRoleDao(roleDao);
        energyCompany.setYukonEnergyCompanyService(yukonEnergyCompanyService);

        energyCompany.initialize();

        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.ENERGY_COMPANY_ROUTE,
                                                              new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                if (event.getPrimaryKey() == energyCompany.getEnergyCompanyId()) {
                    energyCompany.resetAllStoredRoutes();
                }
            }
        });

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