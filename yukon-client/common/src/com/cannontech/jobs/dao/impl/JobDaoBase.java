package com.cannontech.jobs.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonJobDefinitionFactory;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.spring.SeparableRowMapper;

public class JobDaoBase implements InitializingBean {
    private class JobPropertyRowCallbackHandler implements RowCallbackHandler {
        private final Map<String, String> propertyMap;

        public JobPropertyRowCallbackHandler(Map<String, String> propertyMap) {
            this.propertyMap = propertyMap;
        }

        public void processRow(ResultSet rs) throws SQLException {
            String name = rs.getString("name");
            String value = rs.getString("value");
            propertyMap.put(name, value);
        }
    }
    protected final class JobStatusRowMapper<T extends YukonJob> implements
        ParameterizedRowMapper<JobStatus<T>> {
        private final ParameterizedRowMapper<T> internalJobRowMapper;

        public JobStatusRowMapper(ParameterizedRowMapper<T> jobRowMapper) {
            internalJobRowMapper = jobRowMapper;
        }

        public JobStatus<T> mapRow(ResultSet rs, int rowNum) throws SQLException {
            JobStatus<T> jobStatus = new JobStatus<T>();
            jobStatus.setId(rs.getInt("jobStatusId"));
            String jobStateStr = rs.getString("jobState");
            JobState jobState = JobState.valueOf(jobStateStr);
            jobStatus.setJobState(jobState);
            jobStatus.setStartTime(rs.getDate("startTime"));
            jobStatus.setStopTime(rs.getDate("stopTime"));
            jobStatus.setMessage(rs.getString("message"));

            T job = internalJobRowMapper.mapRow(rs, rowNum);
            jobStatus.setJob(job);

            return jobStatus;
        }
    }

    protected SimpleJdbcOperations jdbcTemplate;
    private YukonJobDefinitionFactory<? extends YukonTask> beanDefinitionFactory;
    private FieldMapper<YukonJob> jobFieldMapper = new FieldMapper<YukonJob>() {

        public void extractValues(MapSqlParameterSource p, YukonJob job) {
            String beanNaem = job.getBeanName();
            p.addValue("beanName", beanNaem);
            String disabled = String.valueOf(CtiUtilities.getBooleanCharacter(job.isDisabled()));
            p.addValue("disabled", disabled);
            int userId = job.getRunAsUser().getLiteID();
            p.addValue("userId", userId);
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

    protected SeparableRowMapper<YukonJob> yukonJobMapper = new SeparableRowMapper<YukonJob>() {
        @Override
        protected YukonJob createObject(ResultSet rs) throws SQLException {
            return new YukonJob();
        }
        @Override
        protected void mapRow(ResultSet rs, YukonJob job) throws SQLException {
            job.setId(rs.getInt("jobId"));
            job.setBeanName(rs.getString("beanName"));
            YukonJobDefinition<? extends YukonTask> jobDefinition =
                beanDefinitionFactory.getJobDefinition(job.getBeanName());
            job.setJobDefinition(jobDefinition);
            job.setDisabled(CtiUtilities.isTrue(rs.getString("disabled").charAt(0)));
            int userId = rs.getInt("userId");
            LiteYukonUser liteYukonUser = yukonUserDao.getLiteYukonUser(userId);
            job.setRunAsUser(liteYukonUser);
            String sql =
                "select * from JobProperty " +
                "where jobId = ?";
            HashMap<String, String> propertyMap = new HashMap<String, String>();
            JobPropertyRowCallbackHandler handler = new JobPropertyRowCallbackHandler(propertyMap);
            Object[] params = new Object[]{job.getId()};
            jdbcTemplate.getJdbcOperations().query(sql, params, handler);
            job.setJobProperties(propertyMap);
        }

    };

    private YukonUserDao yukonUserDao;;


    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<YukonJob>(jdbcTemplate, nextValueHelper);
        template.withTableName("Job");
        template.withPrimaryKeyField("jobId");
        template.withFieldMapper(jobFieldMapper);
    }

    protected void saveJob(YukonJob job) {
        template.insert(job);

        // create JobProperty entries
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("insert into JobProperty");
        sql.append("(jobPropertyId, jobId, name, value)");
        sql.append("values (?,?,?,?)");
        for (Map.Entry<String, String> jobProperty : job.getJobProperties().entrySet()) {
            int jobPropertyId = nextValueHelper.getNextValue("JobProperty");
            int jobId = job.getId();
            String name = jobProperty.getKey();
            String value = jobProperty.getValue();
            jdbcTemplate.update(sql.toString(), jobPropertyId, jobId, name, value);
        }
    }

    @Required
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Required
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    @Required
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    @Required
    public void setBeanDefinitionFactory(YukonJobDefinitionFactory<? extends YukonTask> beanDefinitionFactory) {
        this.beanDefinitionFactory = beanDefinitionFactory;
    }

}
