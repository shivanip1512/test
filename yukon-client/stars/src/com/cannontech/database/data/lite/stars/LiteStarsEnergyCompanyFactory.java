package com.cannontech.database.data.lite.stars;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.core.dao.AddressDao;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsRowCountDao;

public class LiteStarsEnergyCompanyFactory {
    private AddressDao addressDao;
    private StarsRowCountDao starsRowCountDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private StarsCustAccountInformationDao starsCustAccountInformationDao;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
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
        energyCompany.setStarsRowCountDao(starsRowCountDao);
        energyCompany.setStarsInventoryBaseDao(starsInventoryBaseDao);
        energyCompany.setStarsCustAccountInformationDao(starsCustAccountInformationDao);
        energyCompany.setSimpleJdbcTemplate(simpleJdbcTemplate);
    }
    
    @Autowired
    public void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }
    
    @Autowired
    public void setStarsRowCountDao(StarsRowCountDao starsRowCountDao) {
        this.starsRowCountDao = starsRowCountDao;
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
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
}
