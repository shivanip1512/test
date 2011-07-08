package com.cannontech.stars.core.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.collect.Lists;


public class YukonEnergyCompanyServiceImpl implements YukonEnergyCompanyService {

    private ECMappingDao ecMappingDao;
    private StarsDatabaseCache starsDatabaseCache;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao;
    
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

        int energyCompanyId = yukonJdbcTemplate.queryForInt(sql);
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
    
    @Override
    public Set<ThermostatScheduleMode> getAllowedThermostatScheduleModes(YukonEnergyCompany yukonEnergyCompany){
        Set<ThermostatScheduleMode> allowedModes = new HashSet<ThermostatScheduleMode>();
        
        for(ThermostatScheduleMode mode : ThermostatScheduleMode.values()){
          //check to see if this mode is allowed by the energy company
            if(energyCompanyRolePropertyDao.getPropertyBooleanValue(mode.getAssociatedRoleProperty(), yukonEnergyCompany)){
                allowedModes.add(mode);
            }
        }
        return allowedModes;
    }
    
    @Override
    public Set<ThermostatScheduleMode> getAllowedThermostatScheduleModesByAccountId(int accountId) {
        YukonEnergyCompany yukonEnergyCompany = getEnergyCompanyByAccountId(accountId);
        return getAllowedThermostatScheduleModes(yukonEnergyCompany);
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
    
    @Autowired
    public void setEnergyCompanyRolePropertyDao(EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao) {
        this.energyCompanyRolePropertyDao = energyCompanyRolePropertyDao;
    }
}