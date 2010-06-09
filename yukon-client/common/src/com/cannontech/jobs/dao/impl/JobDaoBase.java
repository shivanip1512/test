package com.cannontech.jobs.dao.impl;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.jobs.model.YukonJob;

public class JobDaoBase implements InitializingBean {
    protected YukonJdbcTemplate yukonJdbcTemplate;
    
    private FieldMapper<YukonJob> jobFieldMapper = new FieldMapper<YukonJob>() {

        public void extractValues(MapSqlParameterSource p, YukonJob job) {
            String beanNaem = job.getBeanName();
            p.addValue("beanName", beanNaem);
            
            String disabled = String.valueOf(CtiUtilities.getBooleanCharacter(job.isDisabled()));
            if (job.isDeleted()) {
            	disabled = "D";
            }
            p.addValue("disabled", disabled);
            
            int userId = job.getUserContext().getYukonUser().getLiteID();
            p.addValue("userId", userId);
            String locale = job.getUserContext().getLocale().toString();
            p.addValue("locale", locale);
            String timeZone = job.getUserContext().getTimeZone().getID();
            p.addValue("timezone", timeZone);
            String themeName = job.getUserContext().getThemeName();
            p.addValue("themeName", themeName);
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


    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<YukonJob>(yukonJdbcTemplate, nextValueHelper);
        template.withTableName("Job");
        template.withPrimaryKeyField("jobId");
        template.withFieldMapper(jobFieldMapper);
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
            String value = jobProperty.getValue();
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
