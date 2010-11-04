package com.cannontech.database.data.lite.stars;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.core.dao.WarehouseDao;

public class LiteStarsEnergyCompanyFactory {
    private AddressDao addressDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private StarsCustAccountInformationDao starsCustAccountInformationDao;
    private StarsWorkOrderBaseDao starsWorkOrderBaseDao;
    private SimpleJdbcTemplate simpleJdbcTemplate;
	private YukonListDao yukonListDao;
	private EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao;
	private SystemDateFormattingService systemDateFormattingService;
	private WarehouseDao warehouseDao;
	private YukonGroupDao yukonGroupDao;
	
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
    
    private void applyPropertySetters(LiteStarsEnergyCompany energyCompany) {
        energyCompany.setAddressDao(addressDao);
        energyCompany.setStarsInventoryBaseDao(starsInventoryBaseDao);
        energyCompany.setStarsCustAccountInformationDao(starsCustAccountInformationDao);
        energyCompany.setStarsWorkOrderBaseDao(starsWorkOrderBaseDao);
        energyCompany.setSimpleJdbcTemplate(simpleJdbcTemplate);
        energyCompany.setYukonListDao(yukonListDao);
        energyCompany.setEnergyCompanyRolePropertyDao(energyCompanyRolePropertyDao);
        energyCompany.setSystemDateFormattingService(systemDateFormattingService);
        energyCompany.setWarehouseDao(warehouseDao);
        energyCompany.setYukonGroupDao(yukonGroupDao);
        
        energyCompany.initialize();
    }
    
    @Autowired
    public void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }
    
    @Autowired
    public void setStarsInventoryBaseDao(
            StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }
    
    @Autowired
    public void setStarsCustAccountInformationDao(
            StarsCustAccountInformationDao starsCustAccountInformationDao) {
        this.starsCustAccountInformationDao = starsCustAccountInformationDao;
    }
    
    @Autowired
    public void setStarsWorkOrderBaseDao(
            StarsWorkOrderBaseDao starsWorkOrderBaseDao) {
        this.starsWorkOrderBaseDao = starsWorkOrderBaseDao;
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Autowired
    public void setYukonListDao(YukonListDao yukonListDao) {
		this.yukonListDao = yukonListDao;
	}
    
    @Autowired
    public void setEnergyCompanyRolePropertyDao(
            EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao) {
        this.energyCompanyRolePropertyDao = energyCompanyRolePropertyDao;
    }
    
    @Autowired
    public void setSystemDateFormattingService(
			SystemDateFormattingService systemDateFormattingService) {
		this.systemDateFormattingService = systemDateFormattingService;
	}
    
    @Autowired
    public void setWarehouseDao(WarehouseDao warehouseDao) {
        this.warehouseDao = warehouseDao;
    }
    
    @Autowired
    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
    }
}