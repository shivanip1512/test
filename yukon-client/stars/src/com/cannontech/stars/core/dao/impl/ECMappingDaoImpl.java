package com.cannontech.stars.core.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;

public class ECMappingDaoImpl implements ECMappingDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private StarsDatabaseCache starsDatabaseCache;
    
    @Override
    public LiteStarsEnergyCompany getCustomerAccountEC(final CustomerAccount account) {
        String sql = "SELECT EnergyCompanyID FROM ECToAccountMapping WHERE AccountID = ?";
        int energyCompanyId = simpleJdbcTemplate.queryForInt(sql, account.getAccountId());
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        return energyCompany;
    }
    
    @Override
    public LiteStarsEnergyCompany getInventoryEC(int inventoryId) {
        String sql = "SELECT EnergyCompanyID FROM ECToInventoryMapping WHERE InventoryID = ?";
        int energyCompanyId = simpleJdbcTemplate.queryForInt(sql, inventoryId);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        return energyCompany;
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
}
