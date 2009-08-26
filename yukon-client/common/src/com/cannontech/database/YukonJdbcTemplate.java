package com.cannontech.database;

import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.util.SqlFragmentSource;

public class YukonJdbcTemplate extends SimpleJdbcTemplate implements
        YukonJdbcOperations {

    public YukonJdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }
    
    public YukonJdbcTemplate(JdbcOperations classicJdbcTemplate) {
        super(classicJdbcTemplate);
    }

    @Override
    public <T> List<T> query(SqlFragmentSource sql, ParameterizedRowMapper<T> rm)
            throws DataAccessException {
        return query(sql.getSql(), rm, sql.getArguments());
    }
    
    public <T> List<T> queryForLimitedResults(SqlFragmentSource sql, ParameterizedRowMapper<T> rm, int maxResults)
            throws DataAccessException {
        MaxListResultSetExtractor<T> rse = new MaxListResultSetExtractor<T>(rm, maxResults);
        getJdbcOperations().query(sql.getSql(), sql.getArguments(), rse);
        return query(sql.getSql(), rm, sql.getArguments());
    }

    @Override
    public <T> void query(SqlFragmentSource sql, ParameterizedRowMapper<T> rm, Collection<? super T> result)
    throws DataAccessException {
        getJdbcOperations().query(sql.getSql(), sql.getArguments(), new CollectionRowCallbackHandler<T>(rm, result));
    }
    
    @Override
    public int queryForInt(SqlFragmentSource sql) throws DataAccessException {
        return queryForInt(sql.getSql(), sql.getArguments());
    }

    @Override
    public <T> T queryForObject(SqlFragmentSource sql,
            ParameterizedRowMapper<T> rm) throws DataAccessException {
        return queryForObject(sql.getSql(), rm, sql.getArguments());
    }

    @Override
    public String queryForString(SqlFragmentSource sql)
            throws DataAccessException {
        return queryForObject(sql.getSql(), String.class, sql.getArguments());
    }

    @Override
    public int update(SqlFragmentSource sql) throws DataAccessException {
        return update(sql.getSql(), sql.getArguments());
    }

}
