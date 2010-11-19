package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.joda.time.DurationFieldType;
import org.joda.time.ReadablePeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.util.SimplePeriodFormat;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.hardware.dao.CommandScheduleDao;
import com.cannontech.stars.dr.hardware.model.CommandSchedule;

public class CommandScheduleDaoImpl implements CommandScheduleDao {
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;

    private SimpleTableAccessTemplate<CommandSchedule> dbTemplate;
    private final static FieldMapper<CommandSchedule> fieldMapper = new FieldMapper<CommandSchedule>() {
        @Override
        public Number getPrimaryKey(CommandSchedule schedule) {
            return schedule.getCommandScheduleId();
        }

        @Override
        public void setPrimaryKey(CommandSchedule schedule,
                int commandScheduleId) {
            schedule.setCommandScheduleId(commandScheduleId);
        }

        @Override
        public void extractValues(MapSqlParameterSource parameterHolder,
                CommandSchedule schedule) {
            parameterHolder.addValue("startTimeCronString", schedule.getStartTimeCronString());
            ReadablePeriod period = schedule.getRunPeriod();
            String periodStr = period.get(DurationFieldType.hours()) + "h " +
                period.get(DurationFieldType.minutes()) + "m";
            parameterHolder.addValue("period", periodStr);
            parameterHolder.addValue("pagingDelayInSeconds", schedule.getDelayPeriod());
            parameterHolder.addValue("enabled", schedule.isEnabled());
        }
    };
    private final static YukonRowMapper<CommandSchedule> rowMapper = new YukonRowMapper<CommandSchedule>() {
        @Override
        public CommandSchedule mapRow(YukonResultSet rs)
                throws SQLException {
            CommandSchedule retVal = new CommandSchedule();
            retVal.setCommandScheduleId(rs.getInt("commandScheduleId"));
            retVal.setStartTimeCronString(rs.getString("startTimeCronString"));
            String periodStr = rs.getString("period");
            ReadablePeriod period = SimplePeriodFormat.getConfigPeriodFormatter().parsePeriod(periodStr);
            retVal.setRunPeriod(period);
            periodStr = rs.getString("delayPeriod");
            period = SimplePeriodFormat.getConfigPeriodFormatter().parsePeriod(periodStr);
            retVal.setDelayPeriod(period);
            retVal.setEnabled(rs.getBoolean("enabled"));
            return retVal;
        }
    };

    @Override
    public CommandSchedule getById(int commandScheduleId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT commandScheduleId, startTimeCronString,");
        sql.append(  "period, pagingDelayInSeconds, enabled");
        sql.append("FROM commandSchedule");
        sql.append("WHERE commandScheduleId").eq(commandScheduleId);
        return yukonJdbcTemplate.queryForObject(sql, rowMapper);
    }

    @Override
    public List<CommandSchedule> getAll() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT commandScheduleId, startTimeCronString,");
        sql.append(  "period, pagingDelayInSeconds, enabled");
        sql.append("FROM commandSchedule");
        return yukonJdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public List<CommandSchedule> getAllEnabled() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT commandScheduleId, startTimeCronString,");
        sql.append(  "period, pagingDelayInSeconds, enabled");
        sql.append("FROM commandSchedule");
        sql.append("WHERE enabledj = 'y'");
        return yukonJdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public void save(CommandSchedule schedule) {
        dbTemplate.save(schedule);
    }

    @PostConstruct
    public void init() {
        dbTemplate = new SimpleTableAccessTemplate<CommandSchedule>(yukonJdbcTemplate,
                                                                            nextValueHelper);
        dbTemplate.withTableName("commandSchedule");
        dbTemplate.withFieldMapper(fieldMapper);
        dbTemplate.withPrimaryKeyField("commandScheduleId");
        dbTemplate.withPrimaryKeyValidOver(0);
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
}
