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
import com.cannontech.stars.dr.hardware.dao.InventoryPagingScheduleDao;
import com.cannontech.stars.dr.hardware.model.InventoryPagingSchedule;

public class InventoryPagingScheduleDaoImpl implements
        InventoryPagingScheduleDao {
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;

    private SimpleTableAccessTemplate<InventoryPagingSchedule> dbTemplate;
    private final static FieldMapper<InventoryPagingSchedule> fieldMapper = new FieldMapper<InventoryPagingSchedule>() {
        @Override
        public Number getPrimaryKey(InventoryPagingSchedule schedule) {
            return schedule.getInventoryPagingScheduleId();
        }

        @Override
        public void setPrimaryKey(InventoryPagingSchedule schedule,
                int inventoryPagingScheduleId) {
            schedule.setInventoryPagingScheduleId(inventoryPagingScheduleId);
        }

        @Override
        public void extractValues(MapSqlParameterSource parameterHolder,
                InventoryPagingSchedule schedule) {
            parameterHolder.addValue("startTimeCronString", schedule.getStartTimeCronString());
            ReadablePeriod period = schedule.getPeriod();
            String periodStr = period.get(DurationFieldType.hours()) + "h " +
                period.get(DurationFieldType.minutes()) + "m";
            parameterHolder.addValue("period", periodStr);
            parameterHolder.addValue("pagingDelayInSeconds", schedule.getPagingDelayInSeconds());
            parameterHolder.addValue("enabled", schedule.isEnabled());
        }
    };
    private final static YukonRowMapper<InventoryPagingSchedule> rowMapper = new YukonRowMapper<InventoryPagingSchedule>() {
        @Override
        public InventoryPagingSchedule mapRow(YukonResultSet rs)
                throws SQLException {
            InventoryPagingSchedule retVal = new InventoryPagingSchedule();
            retVal.setInventoryPagingScheduleId(rs.getInt("inventoryPagingScheduleId"));
            retVal.setStartTimeCronString(rs.getString("startTimeCronString"));
            String periodStr = rs.getString("period");
            ReadablePeriod period = SimplePeriodFormat.getConfigPeriodFormatter().parsePeriod(periodStr);
            retVal.setPeriod(period);
            retVal.setPagingDelayInSeconds(rs.getInt("pagingDelayInSeconds"));
            retVal.setEnabled(rs.getBoolean("enabled"));
            return retVal;
        }
    };

    @Override
    public InventoryPagingSchedule getById(int inventoryPagingScheduleId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT inventoryPagingScheduleId, startTimeCronString,");
        sql.append(  "period, pagingDelayInSeconds, enabled");
        sql.append("FROM inventoryPagingSchedule");
        sql.append("WHERE inventoryPagingScheduleId").eq(inventoryPagingScheduleId);
        return yukonJdbcTemplate.queryForObject(sql, rowMapper);
    }

    @Override
    public List<InventoryPagingSchedule> getAll() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT inventoryPagingScheduleId, startTimeCronString,");
        sql.append(  "period, pagingDelayInSeconds, enabled");
        sql.append("FROM inventoryPagingSchedule");
        return yukonJdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public List<InventoryPagingSchedule> getAllEnabled() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT inventoryPagingScheduleId, startTimeCronString,");
        sql.append(  "period, pagingDelayInSeconds, enabled");
        sql.append("FROM inventoryPagingSchedule");
        sql.append("WHERE enabledj = 'y'");
        return yukonJdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public void save(InventoryPagingSchedule schedule) {
        dbTemplate.save(schedule);
    }

    @PostConstruct
    public void init() {
        dbTemplate = new SimpleTableAccessTemplate<InventoryPagingSchedule>(yukonJdbcTemplate,
                                                                            nextValueHelper);
        dbTemplate.withTableName("inventoryPagingSchedule");
        dbTemplate.withFieldMapper(fieldMapper);
        dbTemplate.withPrimaryKeyField("inventoryPagingScheduleId");
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
