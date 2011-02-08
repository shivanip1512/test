package com.cannontech.stars.dr.appliance.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.ApplianceTypeEnum;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.webconfiguration.dao.WebConfigurationDao;
import com.cannontech.stars.webconfiguration.model.WebConfiguration;
import com.google.common.collect.Maps;

public class ApplianceCategoryDaoImpl implements ApplianceCategoryDao {
    private ECMappingDao ecMappingDao;
    private YukonEnergyCompanyService yukonEnergyCompanyService;
    private EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao;
    private StarsDatabaseCache starsDatabaseCache;
    private WebConfigurationDao webConfigurationDao;
    private YukonJdbcTemplate yukonJdbcTemplate;

    private static class RowMapper extends
            AbstractRowMapperWithBaseQuery<ApplianceCategory> {
        private Map<Integer, WebConfiguration> webConfigurations;
        private RowMapper() {
            webConfigurations = Maps.newHashMap();
        }
        private RowMapper(Map<Integer, WebConfiguration> webConfigurations) {
            this.webConfigurations = webConfigurations;
        }

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT ac.applianceCategoryId,");
            retVal.append(    "ac.description, ac.webConfigurationId,");
            retVal.append(    "ac.consumerSelectable, yle.yukonDefinitionId");
            retVal.append("FROM applianceCategory ac");
            retVal.append(    "JOIN yukonListEntry yle ON ac.categoryId = yle.entryId");
            return retVal;
        }

        @Override
        public ApplianceCategory mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            int applianceCategoryId = rs.getInt("applianceCategoryId");
            String name = rs.getString("description");
            int applianceTypeDefinitionId = rs.getInt("yukonDefinitionId");
            ApplianceTypeEnum applianceType =
                ApplianceTypeEnum.getByDefinitionId(applianceTypeDefinitionId);
            boolean consumerSelectable =
                rs.getString("consumerSelectable").equalsIgnoreCase("y");
            Integer webConfigurationId = rs.getInt("webConfigurationId");
            WebConfiguration webConfiguration =
                webConfigurations.get(webConfigurationId);

            ApplianceCategory applianceCategory =
                new ApplianceCategory(applianceCategoryId, name, applianceType,
                                      consumerSelectable, webConfiguration);

            return applianceCategory;
        }
    };

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Integer> getApplianceCategoryIdsByEC(int energyCompanyId) {

        // Getting available appliance category energy companies.
        YukonEnergyCompany yukonEnergyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        Set<Integer> appCatEnergyCompanyIds = getAppCatEnergyCompanyIds(yukonEnergyCompany);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ItemId "); // ItemId represents applianceCategoryId in this case.
        sql.append("FROM ECToGenericMapping ");
        sql.append("WHERE MappingCategory").eq(YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY);
        sql.append("AND EnergyCompanyId").in(appCatEnergyCompanyIds);
        
        List<Integer> applianceCategoryIdList = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
        return applianceCategoryIdList;
    }    
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ApplianceCategory> findApplianceCategories(int customerAccountId) {

        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByAccountId(customerAccountId);
        List<Integer> applianceCategoryIdList = getApplianceCategoryIdsByEC(yukonEnergyCompany.getEnergyCompanyId());

        final Set<ApplianceCategory> set = new HashSet<ApplianceCategory>(applianceCategoryIdList.size());
        
        for (final Integer applianceCategoryId : applianceCategoryIdList) {
            final ApplianceCategory applianceCategory = getById(applianceCategoryId);
            set.add(applianceCategory);
        }
        return new ArrayList<ApplianceCategory>(set);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ApplianceCategory getById(int applianceCategoryId) {
        RowMapper rowMapper = new RowMapper();
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(rowMapper.getBaseQuery());
        sql.append("WHERE ac.applianceCategoryId").eq(applianceCategoryId);

        ApplianceCategory applianceCategory =
            yukonJdbcTemplate.queryForObject(sql.getSql(), rowMapper,
                                             sql.getArguments());
        WebConfiguration webConfiguration =
            webConfigurationDao.getForApplianceCateogry(applianceCategoryId);
        applianceCategory.setWebConfiguration(webConfiguration);

        return applianceCategory;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ApplianceCategory> getByApplianceCategoryName(String applianceCategoryName,
                                                              Set<Integer> energyCompanyIds) {
        RowMapper rowMapper = new RowMapper();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(rowMapper.getBaseQuery());
        sql.append("INNER JOIN YukonWebConfiguration ywc ON ac.webConfigurationId = ywc.configurationId");
        sql.append("INNER JOIN ECToGenericMapping ectgm ON (ectgm.itemId = ac.applianceCategoryId");
        sql.append("                                        AND ectgm.mappingCategory = 'ApplianceCategory')");
        sql.append("WHERE (AC.description").eq(applianceCategoryName);
        sql.append("       OR ywc.alternateDisplayName").eq(applianceCategoryName).append(")");
        sql.append("AND ectgm.energyCompanyId IN (", energyCompanyIds,")");

        List<ApplianceCategory> applianceCategories = 
            yukonJdbcTemplate.query(sql.getSql(), rowMapper, sql.getArguments());
        for (ApplianceCategory applianceCategory : applianceCategories) {
            WebConfiguration webConfiguration =
                webConfigurationDao.getForApplianceCateogry(applianceCategory.getApplianceCategoryId());
            applianceCategory.setWebConfiguration(webConfiguration);
        }

        if (applianceCategories.size() > 0) {
            return applianceCategories;
        } else {
            throw new NotFoundException("The appliance category name supplied does not exist.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getEnergyCompaniesByApplianceCategoryId(int applianceCategoryId){

        final SqlStatementBuilder ecIdFromAppCatQuery = new SqlStatementBuilder();
        ecIdFromAppCatQuery.append("SELECT EnergyCompanyId");
        ecIdFromAppCatQuery.append("FROM ECtoGenericMapping");
        ecIdFromAppCatQuery.append("WHERE ItemId = ?");
        ecIdFromAppCatQuery.append("AND MappingCategory = 'ApplianceCategory'");
        
        List<Integer> energyCompanyIds = 
            yukonJdbcTemplate.query(ecIdFromAppCatQuery.toString(),
                                     new IntegerRowMapper(),
                                     applianceCategoryId);

        if (energyCompanyIds.size() > 0) {
            return energyCompanyIds;
        } else {
            throw new NotFoundException("The supplied appliance category is not attached to any energy companies.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, ApplianceCategory> getByApplianceCategoryIds(
            Collection<Integer> applianceCategoryIds) {
        RowMapper rowMapper = new RowMapper();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(rowMapper.getBaseQuery());
        sql.append("AND ac.applianceCategoryId").in(applianceCategoryIds);

        List<ApplianceCategory> applianceCategories =
            yukonJdbcTemplate.query(sql, rowMapper);

        Map<Integer, ApplianceCategory> retVal = Maps.newHashMap();
        for (ApplianceCategory applianceCategory : applianceCategories) {
            int applianceCategoryId = applianceCategory.getApplianceCategoryId();
            WebConfiguration webConfiguration =
                webConfigurationDao.getForApplianceCateogry(applianceCategoryId);
            applianceCategory.setWebConfiguration(webConfiguration);
            retVal.put(applianceCategoryId, applianceCategory);
        }

        return retVal;
    }

    @Override
    public Set<Integer> getAppCatEnergyCompanyIds(YukonEnergyCompany yukonEnergyCompany) {

        if (energyCompanyRolePropertyDao.checkProperty(YukonRoleProperty.INHERIT_PARENT_APP_CATS, yukonEnergyCompany)) {
            return ecMappingDao.getParentEnergyCompanyIds(yukonEnergyCompany.getEnergyCompanyId());
        } else {
            return Collections.singleton(yukonEnergyCompany.getEnergyCompanyId());
        }
        
    }
    
    // DI Setters
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setYukonEnergyCompanyService(YukonEnergyCompanyService yukonEnergyCompanyService) {
        this.yukonEnergyCompanyService = yukonEnergyCompanyService;
    }
    
    @Autowired
    public void setEnergyCompanyRolePropertyDao(EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao) {
        this.energyCompanyRolePropertyDao = energyCompanyRolePropertyDao;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setWebConfigurationDao(WebConfigurationDao webConfigurationDao) {
        this.webConfigurationDao = webConfigurationDao;
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

}
