package com.cannontech.stars.dr.appliance.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.RowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.ApplianceTypeEnum;
import com.cannontech.stars.energyCompany.EcMappingCategory;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.webconfiguration.dao.WebConfigurationDao;
import com.cannontech.stars.webconfiguration.model.WebConfiguration;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ApplianceCategoryDaoImpl implements ApplianceCategoryDao {

    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private WebConfigurationDao webConfigurationDao;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private EnergyCompanySettingDao energyCompanySettingDao;

    private static class ApplianceCategoryRowMapper extends
            AbstractRowMapperWithBaseQuery<ApplianceCategory> {
        private final Map<Integer, WebConfiguration> webConfigurations;
        private ApplianceCategoryRowMapper() {
            webConfigurations = Maps.newHashMap();
        }
        private ApplianceCategoryRowMapper(Map<Integer, WebConfiguration> webConfigurations) {
            this.webConfigurations = webConfigurations;
        }

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT ac.applianceCategoryId, ac.description, ac.webConfigurationId,");
            retVal.append(    "ecm.energyCompanyId, ac.consumerSelectable, ac.averageKwLoad, yle.yukonDefinitionId");
            retVal.append("FROM applianceCategory ac");
            retVal.append(    "JOIN yukonListEntry yle ON ac.categoryId = yle.entryId");
            retVal.append(    "JOIN ecToGenericMapping ecm ON ac.applianceCategoryId = ecm.itemId AND ecm.mappingCategory").eq_k(EcMappingCategory.APPLIANCE_CATEGORY);
            return retVal;
        }

        @Override
        public ApplianceCategory mapRow(YukonResultSet rs)
                throws SQLException {
            int applianceCategoryId = rs.getInt("applianceCategoryId");
            String name = rs.getString("description");
            int applianceTypeDefinitionId = rs.getInt("yukonDefinitionId");
            ApplianceTypeEnum applianceType =
                ApplianceTypeEnum.getByDefinitionId(applianceTypeDefinitionId);
            boolean consumerSelectable =
                rs.getString("consumerSelectable").equalsIgnoreCase("y");
            int energyCompanyId = rs.getInt("energyCompanyId");
            Integer webConfigurationId = rs.getInt("webConfigurationId");
            Double applianceLoad = rs.getDouble("averageKwLoad");
            WebConfiguration webConfiguration =
                webConfigurations.get(webConfigurationId);

            ApplianceCategory applianceCategory =
                new ApplianceCategory(applianceCategoryId, name, applianceType,
                                      consumerSelectable, energyCompanyId, applianceLoad, webConfiguration);

            return applianceCategory;
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Integer> getApplianceCategoryIdsByEC(int energyCompanyId) {

        // Getting available appliance category energy companies.
        YukonEnergyCompany yukonEnergyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        Set<Integer> appCatEnergyCompanyIds = getAppCatEnergyCompanyIds(yukonEnergyCompany);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ItemId "); // ItemId represents applianceCategoryId in this case.
        sql.append("FROM ECToGenericMapping ");
        sql.append("WHERE MappingCategory").eq_k(EcMappingCategory.APPLIANCE_CATEGORY);
        sql.append("AND EnergyCompanyId").in(appCatEnergyCompanyIds);
        
        List<Integer> applianceCategoryIdList = yukonJdbcTemplate.query(sql, RowMapper.INTEGER);
        return applianceCategoryIdList;
    }    

    @Override
    public int getEnergyCompanyForApplianceCategory(int applianceCategoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EnergyCompanyId FROM ECToGenericMapping");
        sql.append("WHERE MappingCategory").eq_k(EcMappingCategory.APPLIANCE_CATEGORY);
        sql.append("AND ItemId").eq(applianceCategoryId);
        return yukonJdbcTemplate.queryForInt(sql);
    }

    @Override
    public Map<Integer, Integer> getEnergyCompanyIdsForApplianceCategoryIds(List<Integer> appCatIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EnergyCompanyId, ItemId FROM ECToGenericMapping");
        sql.append("WHERE MappingCategory").eq_k(EcMappingCategory.APPLIANCE_CATEGORY);
        sql.append("AND ItemId").in(appCatIds);

        final Map<Integer, Integer> energyCompanyMap = new HashMap<>();
        yukonJdbcTemplate.query(sql , new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                int appCatId = rs.getInt("ItemId");
                int ecId = rs.getInt("EnergyCompanyId");
                energyCompanyMap.put(appCatId, ecId);
            }
        });
        return energyCompanyMap;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
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
    @Transactional(propagation = Propagation.SUPPORTS)
    public ApplianceCategory getById(int applianceCategoryId) {
        ApplianceCategoryRowMapper rowMapper = new ApplianceCategoryRowMapper();
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(rowMapper.getBaseQuery());
        sql.append("WHERE ac.applianceCategoryId").eq(applianceCategoryId);

        ApplianceCategory applianceCategory = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        WebConfiguration webConfiguration =
            webConfigurationDao.getForApplianceCateogry(applianceCategoryId);
        applianceCategory.setWebConfiguration(webConfiguration);

        return applianceCategory;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ApplianceCategory> getByApplianceCategoryName(String applianceCategoryName,
                                                              Set<Integer> energyCompanyIds) {
        ApplianceCategoryRowMapper rowMapper = new ApplianceCategoryRowMapper();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(rowMapper.getBaseQuery());
        sql.append("JOIN YukonWebConfiguration ywc ON ac.webConfigurationId = ywc.configurationId");
        sql.append("WHERE (AC.description").eq(applianceCategoryName);
        sql.append("       OR ywc.alternateDisplayName").eq(applianceCategoryName).append(")");
        sql.append("AND ecm.energyCompanyId").in(energyCompanyIds);

        List<ApplianceCategory> applianceCategories = yukonJdbcTemplate.query(sql, rowMapper);
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
    public List<Integer> getEnergyCompaniesByApplianceCategoryId(int applianceCategoryId){

        final SqlStatementBuilder ecIdFromAppCatQuery = new SqlStatementBuilder();
        ecIdFromAppCatQuery.append("SELECT EnergyCompanyId");
        ecIdFromAppCatQuery.append("FROM ECtoGenericMapping");
        ecIdFromAppCatQuery.append("WHERE ItemId").eq(applianceCategoryId);
        ecIdFromAppCatQuery.append("AND MappingCategory").eq_k(EcMappingCategory.APPLIANCE_CATEGORY);
        
        List<Integer> energyCompanyIds =
            yukonJdbcTemplate.query(ecIdFromAppCatQuery, RowMapper.INTEGER);

        if (energyCompanyIds.size() > 0) {
            return energyCompanyIds;
        } else {
            throw new NotFoundException("The supplied appliance category is not attached to any energy companies.");
        }
    }

    @Override
    public Map<Integer, ApplianceCategory> getByApplianceCategoryIds(Iterable<Integer> applianceCategoryIds) {
        ApplianceCategoryRowMapper rowMapper = new ApplianceCategoryRowMapper();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(rowMapper.getBaseQuery());
        sql.append("WHERE ac.applianceCategoryId").in(applianceCategoryIds);

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
        boolean inheritParentAppCats = energyCompanySettingDao.getBoolean(EnergyCompanySettingType.INHERIT_PARENT_APP_CATS, yukonEnergyCompany.getEnergyCompanyId());
        if (inheritParentAppCats) {
            return ecMappingDao.getParentEnergyCompanyIds(yukonEnergyCompany.getEnergyCompanyId());
        } else {
            return Collections.singleton(yukonEnergyCompany.getEnergyCompanyId());
        }
        
    }

    @Override
    public List<ApplianceCategory> getApplianceCategoriesByEcId(int energyCompanyId) {
        List<Integer> applianceCategoryIds = getApplianceCategoryIdsByEC(energyCompanyId);
        Map<Integer, ApplianceCategory> applianceCategoriesById = getByApplianceCategoryIds(applianceCategoryIds);
        List<ApplianceCategory> applianceCategories = Lists.newArrayList(applianceCategoriesById.values());

        Collections.sort(applianceCategories, new Comparator<ApplianceCategory>(){
            @Override
            public int compare(ApplianceCategory ac1, ApplianceCategory ac2) {
                return ac1.getName().compareToIgnoreCase(ac2.getName());
            }});
        
        return applianceCategories;
    }
}
