package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YNBoolean;
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
        public void extractValues(MapSqlParameterSource parameterHolder, CommandSchedule schedule) {
            
            parameterHolder.addValue("startTimeCronString", schedule.getStartTimeCronString());
            parameterHolder.addValue("runPeriod", schedule.getRunPeriod());
            parameterHolder.addValue("delayPeriod", schedule.getDelayPeriod());
            parameterHolder.addValue("enabled", YNBoolean.valueOf(schedule.isEnabled()));
            parameterHolder.addValue("energyCompanyId", schedule.getEnergyCompanyId());
        }
    };
    private final static YukonRowMapper<CommandSchedule> rowMapper = new YukonRowMapper<CommandSchedule>() {
        @Override
        public CommandSchedule mapRow(YukonResultSet rs) throws SQLException {
            CommandSchedule retVal = new CommandSchedule();
            retVal.setCommandScheduleId(rs.getInt("commandScheduleId"));
            retVal.setStartTimeCronString(rs.getString("startTimeCronString"));
            retVal.setRunPeriod(rs.getPeriod("runPeriod"));
            retVal.setDelayPeriod(rs.getPeriod("delayPeriod"));
            retVal.setEnabled(rs.getEnum("enabled", YNBoolean.class).getBoolean());
            retVal.setEnergyCompanyId(rs.getInt("energyCompanyId"));
            return retVal;
        }
    };

    @Override
    public CommandSchedule getById(int commandScheduleId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT commandScheduleId, startTimeCronString,");
        sql.append(  "runPeriod, delayPeriod, enabled, energyCompanyId");
        sql.append("FROM commandSchedule");
        sql.append("WHERE commandScheduleId").eq(commandScheduleId);
        return yukonJdbcTemplate.queryForObject(sql, rowMapper);
    }

    @Override
    public List<CommandSchedule> getAll(int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT commandScheduleId, startTimeCronString,");
        sql.append(  "runPeriod, delayPeriod, enabled, energyCompanyId");
        sql.append("FROM commandSchedule");
        sql.append("WHERE energyCompanyId").eq(energyCompanyId);
        return yukonJdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public List<CommandSchedule> getAllEnabled(int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT commandScheduleId, startTimeCronString,");
        sql.append(  "runPeriod, delayPeriod, enabled, energyCompanyId");
        sql.append("FROM commandSchedule");
        sql.append("WHERE enabled").eq(YNBoolean.YES);
        sql.append(  "AND energyCompanyId").eq(energyCompanyId);
        return yukonJdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public void save(CommandSchedule schedule) {
        dbTemplate.save(schedule);
    }
    
    @Override
    public int delete(int scheduleId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CommandSchedule");
        sql.append("WHERE CommandScheduleId").eq(scheduleId);
        
        return yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void enable(int scheduleId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE CommandSchedule");
        sql.append("SET Enabled").eq(YNBoolean.YES);
        sql.append("WHERE CommandScheduleId").eq(scheduleId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void disable(int scheduleId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE CommandSchedule");
        sql.append("SET Enabled").eq(YNBoolean.NO);
        sql.append("WHERE CommandScheduleId").eq(scheduleId);
        
        yukonJdbcTemplate.update(sql);
    }

    @PostConstruct
    public void init() {
        dbTemplate = new SimpleTableAccessTemplate<CommandSchedule>(yukonJdbcTemplate, nextValueHelper);
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
