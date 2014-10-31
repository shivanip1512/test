package com.cannontech.jobs.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.ThemeUtils;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.support.YukonJobDefinitionFactory;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.spring.SeparableRowMapper;
import com.cannontech.user.SimpleYukonUserContext;

final class YukonJobBaseRowMapper extends SeparableRowMapper<YukonJob> {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private AuthDao authDao;
    @Autowired private YukonJobDefinitionFactory<? extends YukonTask> beanDefinitionFactory;

    @Override
    protected YukonJob createObject(YukonResultSet rs) throws SQLException {
        return new YukonJob();
    }

    @Override
    protected void mapRow(YukonResultSet rs, YukonJob job) throws SQLException {
        job.setId(rs.getInt("jobId"));
        
        job.setBeanName(rs.getString("beanName"));
        YukonJobDefinition<? extends YukonTask> jobDefinition =
            beanDefinitionFactory.getJobDefinition(job.getBeanName());
        job.setJobDefinition(jobDefinition);
        JobDisabledStatus jobDisabledStatus = JobDisabledStatus.valueOf(rs.getString("disabled"));
        job.setDisabled(!jobDisabledStatus.equals(JobDisabledStatus.N));
        job.setDeleted(jobDisabledStatus.equals(JobDisabledStatus.D));
        job.setJobGroupId(rs.getInt("jobGroupId"));
        
        Integer userId = rs.getNullableInt("userId");
        if (userId == null) {
            job.setUserContext(null);
        } else {
            // Assume the rest is there.
            LiteYukonUser liteYukonUser = yukonUserDao.getLiteYukonUser(userId);
            String localeStr = rs.getString("locale");
            Locale locale = LocaleUtils.toLocale(localeStr);
            String timezoneStr = rs.getString("timezone");
            String themeName = rs.getString("themeName");
            // Because this table was expanded to include a timezone column (YUK-5204),
            // we must handle the default case where timezone is blank.
            
            // I considered not storing the time zone. In that case I would
            // just get it off of the YukonUserDao every time. This gets interesting
            // if the user changes their timezone after a job is scheduled. But
            // I think that could get a little weird, so I'm sticking with storing it.
            TimeZone timezone;
            if (StringUtils.isBlank(timezoneStr)) {
                // To replicate the old behavior, we must 
                // look up the timezone for the user.
                timezone = authDao.getUserTimeZone(liteYukonUser);
            } else {
                timezone = TimeZone.getTimeZone(timezoneStr);
            }
            
            // handle blank themeName
            if (StringUtils.isBlank(themeName)) {
                themeName = ThemeUtils.getDefaultThemeName();
            }
            
            SimpleYukonUserContext userContext = new SimpleYukonUserContext(liteYukonUser, locale, timezone, themeName);
            job.setUserContext(userContext);
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder("SELECT * FROM JobProperty WHERE JobId").eq(job.getId());
        HashMap<String, String> propertyMap = new HashMap<String, String>();
        JobPropertyRowCallbackHandler handler = new JobPropertyRowCallbackHandler(propertyMap);
        jdbcTemplate.query(sql, handler);
        job.setJobProperties(propertyMap);
    }
}