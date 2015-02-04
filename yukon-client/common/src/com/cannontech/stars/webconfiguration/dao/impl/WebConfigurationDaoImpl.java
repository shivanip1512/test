package com.cannontech.stars.webconfiguration.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.stars.webconfiguration.dao.WebConfigurationDao;
import com.cannontech.stars.webconfiguration.model.WebConfiguration;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;

public class WebConfigurationDaoImpl implements WebConfigurationDao {
    private YukonJdbcTemplate yukonJdbcTemplate;
    private LoadingCache<Integer, WebConfiguration> computingCache=CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<Integer, WebConfiguration>() {

        @Override
        public WebConfiguration load(Integer arg0) throws Exception {
            return null;
        }
        
    });

    private RowMapperWithBaseQuery<WebConfiguration> rowMapper =
        new AbstractRowMapperWithBaseQuery<WebConfiguration>() {
        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT configurationId, logoLocation,");
            retVal.append("description, alternateDisplayName, url");
            retVal.append("FROM yukonWebConfiguration");
            return retVal;
        }

        @Override
        public WebConfiguration mapRow(YukonResultSet rs)
                throws SQLException {
            int configurationId = rs.getInt("configurationId");
            String logoLocation = SqlUtils.convertDbValueToString(rs.getString("logoLocation"));
            String description = SqlUtils.convertDbValueToString(rs.getString("description"));
            String displayName = SqlUtils.convertDbValueToString(rs.getString("alternateDisplayName"));
            String url = SqlUtils.convertDbValueToString(rs.getString("url"));
            WebConfiguration webConfiguration =
                new WebConfiguration(configurationId, logoLocation, description,
                                     displayName, url);
            return webConfiguration;
        }
    };

    @Override
    public WebConfiguration getById(int webConfigurationId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(rowMapper.getBaseQuery());
        sql.append("WHERE configurationId").eq(webConfigurationId);

        WebConfiguration webConfiguration = 
            yukonJdbcTemplate.queryForObject(sql, rowMapper);
        return webConfiguration;
    }

    @Override
    public WebConfiguration getForApplianceCateogry(int applianceCategoryId) {
        WebConfiguration cachedWebConfiguration = computingCache.getIfPresent(applianceCategoryId);
        if (cachedWebConfiguration != null) {
            return cachedWebConfiguration;
        } else {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(rowMapper.getBaseQuery());
            sql.append("WHERE configurationId IN (SELECT webConfigurationId");
            sql.append("FROM applianceCategory");
            sql.append("WHERE applianceCategoryId").eq(applianceCategoryId).append(")");

            WebConfiguration webConfiguration = yukonJdbcTemplate.queryForObject(sql, rowMapper);

            computingCache.put(applianceCategoryId, webConfiguration);
            return webConfiguration;
        }
    }

    @Override
    public WebConfiguration getForAssignedProgram(int assignedProgramId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(rowMapper.getBaseQuery());
        sql.append("WHERE configurationId IN (SELECT webSettingsId");
        sql.append("FROM lmProgramWebPublishing");
        sql.append("WHERE programId").eq(assignedProgramId).append(")");

        WebConfiguration webConfiguration = 
            yukonJdbcTemplate.queryForObject(sql, rowMapper);
        return webConfiguration;
    }

    @Override
    public Map<Integer, WebConfiguration> getForAssignedPrograms(
            Iterable<Integer> assignedProgramIds) {
        ChunkingSqlTemplate template =
            new ChunkingSqlTemplate(yukonJdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append(rowMapper.getBaseQuery());
                sql.append("WHERE configurationId IN");
                sql.append(    "(SELECT webSettingsId");
                sql.append(     "FROM lmProgramWebPublishing");
                sql.append(     "WHERE programId IN (").appendList(subList).append("))");
                return sql;
            }
        };

        List<WebConfiguration> webConfigurations =
            template.query(sqlGenerator, assignedProgramIds, rowMapper);
        Map<Integer, WebConfiguration> retVal = Maps.newHashMap();
        for (WebConfiguration webConfiguration : webConfigurations) {
            retVal.put(webConfiguration.getConfigurationId(), webConfiguration);
        }
        return retVal;
    }

    @Override
    public Map<Integer, WebConfiguration> getForProgramsForApplianceCateogry(
            int applianceCategoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(rowMapper.getBaseQuery());
        sql.append("WHERE configurationId IN (SELECT webSettingsId");
        sql.append("FROM lmProgramWebPublishing");
        sql.append("WHERE applianceCategoryId").eq(applianceCategoryId);
        sql.append(")");

        List<WebConfiguration> webConfigurations = 
            yukonJdbcTemplate.query(sql, rowMapper);

        Map<Integer, WebConfiguration> retVal = Maps.newHashMap();
        for (WebConfiguration webConfiguration : webConfigurations) {
            retVal.put(webConfiguration.getConfigurationId(), webConfiguration);
        }
        return retVal;
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
