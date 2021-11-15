package com.cannontech.clientutils.logger.dao.impl;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.logger.dao.YukonLoggerDao;
import com.cannontech.clientutils.logger.service.YukonLoggerService.SortBy;
import com.cannontech.common.log.model.LoggerLevel;
import com.cannontech.common.log.model.YukonLogger;
import com.cannontech.common.model.Direction;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class YukonLoggerDaoImpl implements YukonLoggerDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    private final YukonRowMapper<YukonLogger> rowMapper = createRowMapper();
    public static final String TABLE_NAME = "YukonLogging";

    @Override
    public YukonLogger getLogger(int loggerId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * FROM");
        sql.append(TABLE_NAME);
        sql.append("WHERE LoggerId").eq(loggerId);
        return jdbcTemplate.queryForObject(sql, rowMapper);
    }

    @Override
    public int addLogger(YukonLogger logger) {
        int loggerId = nextValueHelper.getNextValue(TABLE_NAME);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto(TABLE_NAME);
        sink.addValue("LoggerId", loggerId);
        sink.addValue("LoggerName", logger.getLoggerName());
        sink.addValue("LoggerLevel", logger.getLevel());
        sink.addValue("ExpirationDate", logger.getExpirationDate());
        sink.addValue("Notes", logger.getNotes());
        jdbcTemplate.update(sql);
        return loggerId;
    }

    @Override
    public void updateLogger(int loggerId, YukonLogger logger) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.update(TABLE_NAME);
        sink.addValue("LoggerLevel", logger.getLevel());
        sink.addValue("ExpirationDate", logger.getExpirationDate());
        sink.addValue("Notes", logger.getNotes());
        sql.append("WHERE LoggerId").eq(loggerId);
        jdbcTemplate.update(sql);
    }

    @Override
    public void deleteLogger(int loggerId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM");
        sql.append(TABLE_NAME);
        sql.append("WHERE LoggerId").eq_k(loggerId);
        jdbcTemplate.update(sql);
    }

    @Override
    public List<YukonLogger> getLoggers(String loggerName, SortBy sortBy, Direction direction, List<LoggerLevel> loggerLevels) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * FROM");
        sql.append(TABLE_NAME);

        if (CollectionUtils.isNotEmpty(loggerLevels)) {
            sql.append("WHERE LoggerLevel").in(loggerLevels);
        }
        if (StringUtils.isNotEmpty(loggerName)) {
            if (loggerName.contains("_")) {
                loggerName = loggerName.replaceAll("_", "/_");
            }

            if (CollectionUtils.isEmpty(loggerLevels)) {
                sql.append("WHERE UPPER(LoggerName) LIKE");
                sql.append("'%" + loggerName.toUpperCase() + "%' ESCAPE '/' ");
            } else {
                sql.append("AND UPPER(LoggerName) LIKE");
                sql.append("'%" + loggerName.toUpperCase() + "%' ESCAPE '/' ");
            }
        }
        if (sortBy != null) {
            sql.append("ORDER BY");
            sql.append(sortBy.getDbString());
            if (direction != null) {
                sql.append(direction);
            }
        }

        return jdbcTemplate.query(sql, rowMapper);
    }

    private YukonRowMapper<YukonLogger> createRowMapper() {
        final YukonRowMapper<YukonLogger> mapper = new YukonRowMapper<YukonLogger>() {
            @Override
            public YukonLogger mapRow(YukonResultSet rs) throws SQLException {
                final YukonLogger logger = new YukonLogger();
                logger.setLoggerId(rs.getInt("LoggerId"));
                logger.setLoggerName(rs.getString("LoggerName"));
                logger.setLevel(rs.getEnum("LoggerLevel", LoggerLevel.class));
                logger.setExpirationDate(rs.getDate("ExpirationDate"));
                logger.setNotes(rs.getString("Notes"));
                return logger;
            }
        };
        return mapper;
    }

    @Override
    public boolean deleteExpiredLoggers() {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yy");
        String formattedDate = format.format(new java.util.Date());
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM");
        sql.append(TABLE_NAME);
        sql.append("WHERE ExpirationDate").lt(formattedDate);
        return jdbcTemplate.update(sql) > 0;
    }

}