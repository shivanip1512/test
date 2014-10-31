package com.cannontech.jobs.dao.impl;

import java.sql.SQLException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.user.YukonUserContext;

public abstract class JobDaoBase {
    @Autowired protected YukonJdbcTemplate jdbcTemplate;
    @Autowired protected NextValueHelper nextValueHelper;

    protected static YukonRowMapper<JobDisabledStatus> jobDisabledStatusRowMapper = 
        new YukonRowMapper<JobDisabledStatus>() {
        @Override
        public JobDisabledStatus mapRow(YukonResultSet rs) throws SQLException {
            JobDisabledStatus jobDisabledStatus = rs.getEnum("disabled", JobDisabledStatus.class);
            return jobDisabledStatus;
        }
    };

    private FieldMapper<YukonJob> jobFieldMapper = new FieldMapper<YukonJob>() {
        @Override
        public void extractValues(MapSqlParameterSource p, YukonJob job) {
            String beanName = job.getBeanName();
            p.addValue("beanName", beanName);
            p.addValue("jobGroupId", job.getJobGroupId());
               
            String disabled = String.valueOf(CtiUtilities.getBooleanCharacter(job.isDisabled()));
            if (job.isDeleted()) {
            	disabled = "D";
            }
            p.addValue("disabled", disabled);
            
            if (!job.isSystemUser()) {
                // Not system user, should have all the context goodies.
                YukonUserContext context = job.getUserContext();
                p.addValue("userId", context.getYukonUser().getLiteID());
                String locale = context.getLocale().toString();
                p.addValue("locale", locale);
                String timeZone = context.getTimeZone().getID();
                p.addValue("timezone", timeZone);
                String themeName = context.getThemeName();
                p.addValue("themeName", themeName);
            }
        }

        @Override
        public Number getPrimaryKey(YukonJob job) {
            return job.getId();
        }

        @Override
        public void setPrimaryKey(YukonJob job, int value) {
            job.setId(value);
        }
    };

    private SimpleTableAccessTemplate<YukonJob> template;

    @PostConstruct
    public void init() throws Exception {
        template = new SimpleTableAccessTemplate<YukonJob>(jdbcTemplate, nextValueHelper);
        template.setTableName("Job");
        template.setPrimaryKeyField("jobId");
        template.setFieldMapper(jobFieldMapper);
    }

    protected void insertJob(YukonJob job) {
        template.insert(job);
        reinsertProperties(job);
    }

    protected void updateJob(YukonJob job) {
        template.update(job);
        reinsertProperties(job);
    }

    private void reinsertProperties(YukonJob job) {
        // delete old JobProperty entries
        SqlStatementBuilder deleteSql = new SqlStatementBuilder();
        deleteSql.append("DELETE FROM JobProperty");
        deleteSql.append("WHERE JobId").eq(job.getId());
        
        jdbcTemplate.update(deleteSql);
        
        // create JobProperty entries
        for (Map.Entry<String, String> jobProperty : job.getJobProperties().entrySet()) {
            SqlStatementBuilder insertSql = new SqlStatementBuilder();
            SqlParameterSink params = insertSql.insertInto("JobProperty");
            params.addValue("JobPropertyId", nextValueHelper.getNextValue("JobProperty"));
            params.addValue("JobId", job.getId());
            params.addValue("Name", jobProperty.getKey());
            params.addValue("Value", SqlUtils.convertStringToDbValue(jobProperty.getValue()));
            
            jdbcTemplate.update(insertSql);
        }
    }
}