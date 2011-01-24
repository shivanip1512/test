package com.cannontech.stars.core.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.YukonEnergyCompany;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.service.EnergyCompanyService;
import com.cannontech.stars.util.ECUtils;
import com.google.common.collect.Lists;


public class EnergyCompanyServiceImpl implements EnergyCompanyService {

    private ECMappingDao ecMappingDao;
    private RolePropertyDao rolePropertyDao;
    private StarsDatabaseCache starsDatabaseCache;
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    public List<YukonEnergyCompany> getAccessibleChildEnergyCompanies(int energyCompanyId) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        
        // The supplied energy company does not have access to the member energy companies.
        // Return the supplied energy company id.
        boolean manageMembersEnabled = 
            rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, energyCompany.getEnergyCompanyUser());
        if (!manageMembersEnabled) {
            return Collections.singletonList((YukonEnergyCompany) energyCompany);
        }

        // Get all of the child energy companies including itself for the supplied energy company id.
        List<Integer> childEnergyCompanyIds = energyCompany.getAllEnergyCompaniesDownward();
        List<YukonEnergyCompany> availableChildEnergyCompaniss = Lists.newArrayListWithExpectedSize(childEnergyCompanyIds.size());
        for (int childEnergyCompanyId : childEnergyCompanyIds) {
            YukonEnergyCompany childEnergyCompany = starsDatabaseCache.getEnergyCompany(childEnergyCompanyId);
            availableChildEnergyCompaniss.add(childEnergyCompany);
        }
        
        return availableChildEnergyCompaniss;
    }

    @Override
    public List<YukonEnergyCompany> getAccessibleParentEnergyCompanies(int energyCompanyId) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        
        //  Get all the energy companies to the supplied energy company id.
        List<LiteStarsEnergyCompany> allParentEnergyCompanies = ECUtils.getAllAscendants(energyCompany);

        List<YukonEnergyCompany> availableEnergyCompanies = Lists.newArrayListWithCapacity(allParentEnergyCompanies.size());
        availableEnergyCompanies.addAll(allParentEnergyCompanies);
        
        return availableEnergyCompanies;

    }
    
    @Override
    public YukonEnergyCompany getEnergyCompanyByAccountId(int accountId) {
        int energyCompanyId = ecMappingDao.getEnergyCompanyIdForAccountId(accountId);
        YukonEnergyCompany yukonEnergyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId); 
        return yukonEnergyCompany;
    }

    /**
     * gets the primary energy company that the operator is directly associated with.  Once you have
     * the primary energy company you can figure out the inherited energy companies and therefore
     * see what energy companies the operator has access to.
     */
    @Override
    public LiteStarsEnergyCompany getPrimaryEnergyCompanyByOperator(LiteYukonUser operator) {
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
    
    // DI Setters
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
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