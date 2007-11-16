package com.cannontech.jobs.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonJobDefinitionFactory;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.spring.SeparableRowMapper;

final class YukonJobBaseRowMapper extends SeparableRowMapper<YukonJob> {


    private JdbcTemplate jdbcTemplate;
    private YukonUserDao yukonUserDao;
    private YukonJobDefinitionFactory<? extends YukonTask> beanDefinitionFactory;

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
        jdbcTemplate.query(sql, params, handler);
        job.setJobProperties(propertyMap);
    }
    
    @Required
    public void setBeanDefinitionFactory(YukonJobDefinitionFactory<? extends YukonTask> beanDefinitionFactory) {
        this.beanDefinitionFactory = beanDefinitionFactory;
    }
    
    @Required
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Required
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

}