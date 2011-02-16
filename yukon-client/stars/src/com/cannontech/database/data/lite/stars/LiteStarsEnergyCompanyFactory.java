package com.cannontech.database.data.lite.stars;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.core.dao.WarehouseDao;

public class LiteStarsEnergyCompanyFactory {
    private AddressDao addressDao;
    private AsyncDynamicDataSource asyncDynamicDataSource;
    private DBPersistentDao dbPersistentDao;
    private ECMappingDao ecMappingDao;
    private EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao;
    private PaoPermissionService paoPermissionService;
    private StarsCustAccountInformationDao starsCustAccountInformationDao;
    private StarsSearchDao starsSearchDao;
    private StarsWorkOrderBaseDao starsWorkOrderBaseDao;
	private SystemDateFormattingService systemDateFormattingService;
	private WarehouseDao warehouseDao;
	private YukonJdbcTemplate yukonJdbcTemplate;
	private YukonGroupDao yukonGroupDao;
	private YukonListDao yukonListDao;
	
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
        energyCompany.setEcMappingDao(ecMappingDao);
        energyCompany.setEnergyCompanyRolePropertyDao(energyCompanyRolePropertyDao);
        energyCompany.setPaoPermissionService(paoPermissionService);
        energyCompany.setStarsCustAccountInformationDao(starsCustAccountInformationDao);
        energyCompany.setSystemDateFormattingService(systemDateFormattingService);
        energyCompany.setStarsSearchDao(starsSearchDao);
        energyCompany.setStarsWorkOrderBaseDao(starsWorkOrderBaseDao);
        energyCompany.setWarehouseDao(warehouseDao);
        energyCompany.setYukonJdbcTemplate(yukonJdbcTemplate);
        energyCompany.setYukonGroupDao(yukonGroupDao);
        energyCompany.setYukonListDao(yukonListDao);

        energyCompany.initialize();

        // Adding Database Change listeners
        DatabaseChangeEventListener defaultRouteEventListener = new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                if (event.getPrimaryKey() == energyCompany.getEnergyCompanyId()) {
                    energyCompany.resetDefaultRouteId();
                }
            }
        };

        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.ENERGY_COMPANY_DEFAULT_ROUTE,
                                                              defaultRouteEventListener);

        DatabaseChangeEventListener ecRouteEventListener = new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                if (event.getPrimaryKey() == energyCompany.getEnergyCompanyId()) {
                    energyCompany.resetRouteIds();
                }
            }
        };

        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.ENERGY_COMPANY_ROUTES,
                                                              ecRouteEventListener);

        DatabaseChangeEventListener ecSubstationEventListener = new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                if (event.getPrimaryKey() == energyCompany.getEnergyCompanyId()) {
                    energyCompany.resetSubstations();
                }
            }
        };

        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.ENERGY_COMPANY_SUBSTATIONS,
                                                              ecSubstationEventListener);

        
        
        DatabaseChangeEventListener applianceCategoryEventListener = new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                energyCompany.resetApplianceCategoryList();
            }
        };

        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.APPLIANCE,
                                                              applianceCategoryEventListener);
    }
    
    // DI Setter
    @Autowired
    public void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }

    @Autowired
    public void setAsyncDynamicDataSource(AsyncDynamicDataSource asyncDynamicDataSource) {
        this.asyncDynamicDataSource = asyncDynamicDataSource;
    }
    
    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
    
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setEnergyCompanyRolePropertyDao(EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao) {
        this.energyCompanyRolePropertyDao = energyCompanyRolePropertyDao;
    }

    @Autowired
    public void setPaoPermissionService(PaoPermissionService paoPermissionService) {
        this.paoPermissionService = paoPermissionService;
    }
    
    @Autowired
    public void setStarsCustAccountInformationDao(StarsCustAccountInformationDao starsCustAccountInformationDao) {
        this.starsCustAccountInformationDao = starsCustAccountInformationDao;
    }
    
    @Autowired
    public void setStarsSearchDao(StarsSearchDao starsSearchDao) {
        this.starsSearchDao = starsSearchDao;
    }
    
    @Autowired
    public void setStarsWorkOrderBaseDao(StarsWorkOrderBaseDao starsWorkOrderBaseDao) {
        this.starsWorkOrderBaseDao = starsWorkOrderBaseDao;
    }
    
    @Autowired
    public void setSystemDateFormattingService(SystemDateFormattingService systemDateFormattingService) {
		this.systemDateFormattingService = systemDateFormattingService;
	}
    
    @Autowired
    public void setWarehouseDao(WarehouseDao warehouseDao) {
        this.warehouseDao = warehouseDao;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
    }

    @Autowired
    public void setYukonListDao(YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }
}