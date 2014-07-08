package com.cannontech.spring;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.StatementCallback;

import com.cannontech.clientutils.YukonLogManager;

public class LoggingJdbcTemplate extends JdbcTemplate {
    private final Logger log = YukonLogManager.getLogger(LoggingJdbcTemplate.class);
    @Override
    public Object execute(StatementCallback action) throws DataAccessException {
        long startTime = System.nanoTime();
        Object object = super.execute(action);
        long totalTime = System.nanoTime() - startTime;
        if (log.isDebugEnabled()) {
            String time = formatNanoTime(totalTime);
            log.debug("SQL SC Execution (" + time + "): " + getSql(action));
        }
        return object;
    }

    @Override
    public Object execute(PreparedStatementCreator psc, PreparedStatementCallback action) throws DataAccessException {
        long startTime = System.nanoTime();
        Object object = super.execute(psc,action);
        long totalTime = System.nanoTime() - startTime;
        if (log.isDebugEnabled()) {    
            String time = formatNanoTime(totalTime);
            log.debug("SQL PSC Execution (" + time + "): " + getSql(psc));
        }
        return object;
    }

    private String formatNanoTime(long totalTime) {
        String time = DecimalFormat.getNumberInstance().format((totalTime / 1000000f));
        return time + "ms";
    }

    @Override
    public Object execute(CallableStatementCreator csc, CallableStatementCallback action) throws DataAccessException {
        long startTime = System.nanoTime();
        Object object = super.execute(csc,action);
        long totalTime = System.nanoTime() - startTime;
        if (log.isDebugEnabled()) {
            String time = formatNanoTime(totalTime);
            log.debug("SQL CSC Execution (" + time + "): " + getSql(csc));
        }
        return object;
    }

    private String getSql(Object o) {
        if (o instanceof SqlProvider) {
            SqlProvider sqlProvider = (SqlProvider) o;
            String sql = sqlProvider.getSql();
            sql = sql.replaceAll("\\s+", " ");
            return sql;
        } else {
            return "unknown sql";
        }
    }
}
