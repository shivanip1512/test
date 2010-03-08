package com.cannontech.stars.webconfiguration.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.webconfiguration.dao.WebConfigurationDao;
import com.cannontech.stars.webconfiguration.model.WebConfiguration;
import com.google.common.collect.Maps;

public class WebConfigurationDaoImpl implements WebConfigurationDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;

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
        public WebConfiguration mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            int configurationId = rs.getInt("configurationId");
            String logoLocation = rs.getString("logoLocation");
            String description = rs.getString("description");
            String displayName = rs.getString("alternateDisplayName");
            String url = rs.getString("url");
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
            simpleJdbcTemplate.queryForObject(sql.getSql(), rowMapper,
                                              sql.getArguments());
        return webConfiguration;
    }

    @Override
    public WebConfiguration getForApplianceCateogry(int applianceCategoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(rowMapper.getBaseQuery());
        sql.append("WHERE configurationId IN (SELECT webConfigurationId");
        sql.append("FROM applianceCategory");
        sql.append("WHERE applianceCategoryId").eq(applianceCategoryId).append(")");

        WebConfiguration webConfiguration = 
            simpleJdbcTemplate.queryForObject(sql.getSql(), rowMapper,
                                              sql.getArguments());
        return webConfiguration;
    }

    @Override
    public WebConfiguration getForAssignedProgram(int assignedProgramId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(rowMapper.getBaseQuery());
        sql.append("WHERE configurationId IN (SELECT webSettingsId");
        sql.append("FROM lmProgramWebPublishing");
        sql.append("WHERE programId").eq(assignedProgramId).append(")");

        WebConfiguration webConfiguration = 
            simpleJdbcTemplate.queryForObject(sql.getSql(), rowMapper,
                                              sql.getArguments());
        return webConfiguration;
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
            simpleJdbcTemplate.query(sql.getSql(), rowMapper,
                                              sql.getArguments());

        Map<Integer, WebConfiguration> retVal = Maps.newHashMap();
        for (WebConfiguration webConfiguration : webConfigurations) {
            retVal.put(webConfiguration.getConfigurationId(), webConfiguration);
        }
        return retVal;
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
