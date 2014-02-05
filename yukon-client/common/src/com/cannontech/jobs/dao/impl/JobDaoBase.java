package com.cannontech.jobs.dao.impl;

import java.sql.SQLException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.user.YukonUserContext;

public class JobDaoBase {
    protected YukonJdbcTemplate yukonJdbcTemplate;

    protected static YukonRowMapper<JobDisabledStatus> jobDisabledStatusRowMapper = 
        new YukonRowMapper<JobDisabledStatus>() {
        @Override
        public JobDisabledStatus mapRow(YukonResultSet rs) throws SQLException {
            JobDisabledStatus jobDisabledStatus = rs.getEnum("disabled", JobDisabledStatus.class);
            return jobDisabledStatus;
        }
    };

    private FieldMapper<YukonJob> jobFieldMapper = new FieldMapper<YukonJob>() {

        public void extractValues(MapSqlParameterSource p, YukonJob job) {
            String beanNaem = job.getBeanName();
            p.addValue("beanName", beanNaem);
            
            String disabled = String.valueOf(CtiUtilities.getBooleanCharacter(job.isDisabled()));
            if (job.isDeleted()) {
            	disabled = "D";
            }
            p.addValue("disabled", disabled);
            
            YukonUserContext context = job.getUserContext();
            if (context != null) {
                int userId = context.getYukonUser().getLiteID();
                p.addValue("userId", userId);
                String locale = context.getLocale().toString();
                p.addValue("locale", locale);
                String timeZone = context.getTimeZone().getID();
                p.addValue("timezone", timeZone);
                String themeName = context.getThemeName();
                p.addValue("themeName", themeName);
            }
        }

        public Number getPrimaryKey(YukonJob job) {
            return job.getId();
        }

        public void setPrimaryKey(YukonJob job, int value) {
            job.setId(value);
        }
    };

    protected NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<YukonJob> template;

    @PostConstruct
    public void init() throws Exception {
        template = new SimpleTableAccessTemplate<YukonJob>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("Job");
        template.setPrimaryKeyField("jobId");
        template.setFieldMapper(jobFieldMapper);
    }

    protected void insertJob(YukonJob job) {
        template.insert(job);

        reInsertProperties(job);
    }

    private void reInsertProperties(YukonJob job) {
        // delete old JobProperty entries
        SqlStatementBuilder deleteSql = new SqlStatementBuilder();
        deleteSql.append("delete from JobProperty");
        deleteSql.append("where jobId = ?");
        yukonJdbcTemplate.update(deleteSql.toString(), job.getId());
        
        
        // create JobProperty entries
        SqlStatementBuilder insertSql = new SqlStatementBuilder();
        insertSql.append("insert into JobProperty");
        insertSql.append("(jobPropertyId, jobId, name, value)");
        insertSql.append("values (?,?,?,?)");
        for (Map.Entry<String, String> jobProperty : job.getJobProperties().entrySet()) {
            int jobPropertyId = nextValueHelper.getNextValue("JobProperty");
            int jobId = job.getId();
            String name = jobProperty.getKey();
            String value = SqlUtils.convertStringToDbValue(jobProperty.getValue());
            yukonJdbcTemplate.update(insertSql.toString(), jobPropertyId, jobId, name, value);
        }
    }

    protected void updateJob(YukonJob job) {
        template.update(job);
        
        reInsertProperties(job);
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    @Required
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }


}
