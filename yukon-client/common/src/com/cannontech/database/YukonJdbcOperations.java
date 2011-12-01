package com.cannontech.database;

import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.util.SqlFragmentSource;

public interface YukonJdbcOperations extends SimpleJdbcOperations {
    public void query(SqlFragmentSource sql, RowCallbackHandler rch) throws DataAccessException;
    public void query(SqlFragmentSource sql, YukonRowCallbackHandler rch) throws DataAccessException;
    public <T> Object query(SqlFragmentSource sql, ResultSetExtractor<T> rse) throws DataAccessException;
    public <T> List<T> query(SqlFragmentSource sql, ParameterizedRowMapper<T> rm) throws DataAccessException;
    public <T> List<T> query(SqlFragmentSource sql, YukonRowMapper<T> rm) throws DataAccessException;
    public <T> List<T> queryForLimitedResults(SqlFragmentSource sql, ParameterizedRowMapper<T> rm, int maxResults) throws DataAccessException;
    public <T> void query(SqlFragmentSource sql, ParameterizedRowMapper<T> rm, Collection<? super T> result) throws DataAccessException;
    public int queryForInt(SqlFragmentSource sql) throws DataAccessException;
    public long queryForLong(SqlFragmentSource sql) throws DataAccessException;
    public String queryForString(SqlFragmentSource sql) throws DataAccessException;
    public <T> T queryForObject(SqlFragmentSource sql, ParameterizedRowMapper<T> rm) throws DataAccessException;
    public <T> T queryForObject(SqlFragmentSource sql, YukonRowMapper<T> rm) throws DataAccessException;
    public int update(SqlFragmentSource sql) throws DataAccessException;
    public <R> void queryInto(SqlFragmentSource sql, YukonRowMapper<R> rowMapper, Collection<? super R> resultSink);
}
