package com.cannontech.stars.core.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.collect.Lists;


public class YukonEnergyCompanyServiceImpl implements YukonEnergyCompanyService {
    private Logger log = YukonLogManager.getLogger(YukonEnergyCompanyServiceImpl.class);
    private ECMappingDao ecMappingDao;
    private StarsDatabaseCache starsDatabaseCache;
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    public YukonEnergyCompany getEnergyCompanyByAccountId(int accountId) {
        int energyCompanyId = ecMappingDao.getEnergyCompanyIdForAccountId(accountId);
        YukonEnergyCompany yukonEnergyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId); 
        return yukonEnergyCompany;
    }

    @Override
    public LiteStarsEnergyCompany getEnergyCompanyByOperator(LiteYukonUser operator) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ECOLL.EnergyCompanyId");
        sql.append("FROM EnergyCompanyOperatorLoginList ECOLL");
        sql.append("WHERE ECOLL.OperatorLoginId").eq(operator.getUserID());

        int energyCompanyId = 0;
        try {
            energyCompanyId = yukonJdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {
            log.warn("No energy company found for user id: " + operator.getUserID() + ". Using default energy company.");
            energyCompanyId = StarsDatabaseCache.DEFAULT_ENERGY_COMPANY_ID;
        }
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        return energyCompany;
    }

    @Override
    public YukonEnergyCompany getEnergyCompanyByInventoryId(int inventoryId) {
        int energyCompanyId = ecMappingDao.getEnergyCompanyIdForInventoryId(inventoryId);
        YukonEnergyCompany yukonEnergyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId); 
        return yukonEnergyCompany;
    }
    
    @Override
    public List<YukonEnergyCompany> getAllEnergyCompanies() {
        List<YukonEnergyCompany> energyCompanies = Lists.<YukonEnergyCompany>newArrayList(starsDatabaseCache.getAllEnergyCompanies());
        return energyCompanies;
    }

    @Override
    public boolean isDefaultEnergyCompany(YukonEnergyCompany energyCompany) {
        return energyCompany.getEnergyCompanyId() == StarsDatabaseCache.DEFAULT_ENERGY_COMPANY_ID;
    }
    
    // DI Setters
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}