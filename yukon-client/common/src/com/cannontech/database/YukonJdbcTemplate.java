package com.cannontech.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.logging.log4j.Logger;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.ISOPeriodFormat;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.SqlStatementBuilder.SqlBatchUpdater;
import com.cannontech.spring.LoggingJdbcTemplate;
import com.google.common.collect.Lists;

public class YukonJdbcTemplate extends JdbcTemplate {
    private static final Logger log = YukonLogManager.getLogger(YukonJdbcTemplate.class);
    
    private static final int defaultBatchSize = 50000;

    public YukonJdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }

    public YukonJdbcTemplate(LoggingJdbcTemplate loggingJdbcTemplate) {
        super(loggingJdbcTemplate.getDataSource());
    }

    public <R> void queryInto(SqlFragmentSource sql, final YukonRowMapper<R> rowMapper, Collection<? super R> resultSink) {
        CollectionRowCallbackHandler<R> rch = new CollectionRowCallbackHandler<>(rowMapper, resultSink);
        query(sql, rch);
    }

    public void query(SqlFragmentSource sql, RowCallbackHandler rch) throws DataAccessException {
        query(sql.getSql(), sql.getArguments(), rch);
    }

    public void query(SqlFragmentSource sql, YukonRowCallbackHandler rch)
            throws DataAccessException {
        query(sql.getSql(), sql.getArguments(), new YukonRowCallbackHandlerAdapter(rch));
    }

    public <T> Object query(SqlFragmentSource sql, ResultSetExtractor<T> rse) throws DataAccessException {
        return query(sql.getSql(), sql.getArguments(), rse);
    }

    public <T> List<T> query(SqlFragmentSource sql, RowMapper<T> rm)
            throws DataAccessException {
        return query(sql.getSql(), rm, sql.getArguments());
    }

    public <T> List<T> query(SqlFragmentSource sql, YukonRowMapper<T> rm)
            throws DataAccessException {
        return query(sql, new YukonRowMapperAdapter<>(rm));
    }

    public <T> List<T> queryForLimitedResults(SqlFragmentSource sql, RowMapper<T> rm, int maxResults) {

        MaxListResultSetExtractor<T> rse = new MaxListResultSetExtractor<>(rm, maxResults);
        query(sql.getSql(), sql.getArguments(), rse);
        return rse.getResult();
    }

    public <T> List<T> queryForLimitedResults(SqlFragmentSource sql,
            YukonRowMapper<T> rm,
            int maxResults) throws DataAccessException {

        MaxListResultSetExtractor<T> rse = new MaxListResultSetExtractor<>(new YukonRowMapperAdapter<>(rm), maxResults);
        query(sql.getSql(), sql.getArguments(), rse);
        return rse.getResult();
    }

    public <T> void query(SqlFragmentSource sql, RowMapper<T> rm, Collection<? super T> result) {
        query(sql.getSql(), sql.getArguments(), new CollectionRowCallbackHandler<>(rm, result));
    }

    public int queryForInt(SqlFragmentSource sql) throws DataAccessException {
        Integer number = queryForObject(sql.getSql(), sql.getArguments(), Integer.class);
        return number != null ? number.intValue() : 0;
    }

    public long queryForLong(SqlFragmentSource sql) throws DataAccessException {
        Long number = queryForObject(sql.getSql(), sql.getArguments(), Long.class);
        return number != null ? number.longValue() : 0;
    }

    public <T> T queryForObject(SqlFragmentSource sql,
            RowMapper<T> rm) throws DataAccessException {
        return queryForObject(sql.getSql(), rm, sql.getArguments());
    }

    public <T> T queryForObject(SqlFragmentSource sql,
            YukonRowMapper<T> rm) throws DataAccessException {
        return queryForObject(sql, new YukonRowMapperAdapter<>(rm));
    }

    public String queryForString(SqlFragmentSource sql)
            throws DataAccessException {
        return queryForObject(sql.getSql(), String.class, sql.getArguments());
    }

    public int update(SqlFragmentSource sql) throws DataAccessException {
        return update(sql.getSql(), sql.getArguments());
    }

    /**
     * Performs a batched update utilizing prepared statements, with the default batch size.
     * 
     * @param sql An SqlStatementBuilder configured for batch update via the batchInsertInto method.
     */
    public void yukonBatchUpdate(SqlStatementBuilder sql) {
        yukonBatchUpdate(sql, defaultBatchSize);
    }

    /**
     * Performs a batched update utilizing prepared statements.
     * 
     * @param sql       An SqlStatementBuilder configured for batch update via the batchInsertInto method.
     * @param batchSize The number of updates to combine into each batch.
     */
    public void yukonBatchUpdate(SqlStatementBuilder sql, int batchSize) {
        if (!sql.isBatchUpdate()) {
            throw new IllegalArgumentException("SqlStatementBuilder is not configured for batch update. "
                    + "Use batchInsertInto first.");
        }
        SqlBatchUpdater batchUpdater = sql.getBatchUpdater();

        log.debug("Inserting " + batchUpdater.getColumnValues().size() + " rows as batched update");

        String deleteByColumn = batchUpdater.getDeleteBeforeInsertColumn();
        int deleteColumnIndex = batchUpdater.getColumnNames().indexOf(deleteByColumn);

        SqlStatementBuilder insertSql = new SqlStatementBuilder();
        insertSql.append("INSERT INTO").append(batchUpdater.getTableName());
        insertSql.append("(");
        insertSql.appendList(batchUpdater.getColumnNames());
        insertSql.append(")");
        insertSql.append("VALUES");
        insertSql.append("(");
        insertSql.appendPlaceholders(batchUpdater.getColumnNames().size());
        insertSql.append(")");
        log.trace("Insert sql: " + insertSql.toString());

        class RowsInserted {
            Integer count = 0;

            public void increment(int size) {
                count = count + size;
            }
        }

        int total = batchUpdater.getColumnValues().size();

        RowsInserted inserted = new RowsInserted();
        Lists.partition(batchUpdater.getColumnValues(), batchSize)
                .forEach(batchList -> {
                    try {
                        if (log.isDebugEnabled()) {
                            inserted.increment(batchList.size());
                            log.trace("Inserting {} rows, inserted {} out of {}", batchList.size(), inserted.count, total);
                        }
                        // If deleting before insert, delete the batch of rows first
                        if (batchUpdater.isDeleteBeforeInsert()) {
                            List<Object> deleteValues = batchList.stream()
                                    .map(values -> values.get(deleteColumnIndex))
                                    .collect(Collectors.toList());

                            ChunkingSqlTemplate chunkingJdbcTemplate = new ChunkingSqlTemplate(this);
                            chunkingJdbcTemplate.update(new DeleteBeforeInsertSqlGenerator(sql), deleteValues);
                        }

                        // Insert the batch of rows
                        batchUpdate(insertSql.toString(), new BatchPreparedStatementSetter() {
                            @Override
                            public void setValues(PreparedStatement ps, int rowIndex) throws SQLException {
                                List<Object> values = batchList.get(rowIndex);
                                for (int valueIndex = 0; valueIndex < values.size(); valueIndex++) {
                                    Object value = values.get(valueIndex);
                                    if (value == null) {
                                        ps.setNull(valueIndex + 1, Types.NULL);
                                    } else {
                                        Object jdbcFriendlyValue;
                                        if (value instanceof DatabaseRepresentationSource) {
                                            jdbcFriendlyValue = ((DatabaseRepresentationSource) value)
                                                    .getDatabaseRepresentation();
                                        } else if (value instanceof Enum<?>) {
                                            Enum<?> e = (Enum<?>) value;
                                            jdbcFriendlyValue = e.name();
                                        } else if (value instanceof ReadableInstant) {
                                            jdbcFriendlyValue = new Timestamp(((ReadableInstant) value).getMillis());
                                        } else if (value instanceof ReadablePeriod) {
                                            jdbcFriendlyValue = ISOPeriodFormat.standard().print((ReadablePeriod) value);
                                        } else if (value instanceof Date) {
                                            // Unlike the normal jdbcTemplate, the prepared statement doesn't seem to
                                            // automatically convert java.util.date into a java.sql.* type, so we do it here.
                                            jdbcFriendlyValue = new Timestamp(((Date) value).getTime());
                                        } else {
                                            jdbcFriendlyValue = value;
                                        }

                                        ps.setObject(valueIndex + 1, jdbcFriendlyValue);
                                    }
                                }
                            }

                            @Override
                            public int getBatchSize() {
                                return batchList.size();
                            }
                        });
                    } catch (Exception e) {
                        log.error("Batch insert failed. Inserted  " + inserted.count + " out of " + total + ".", e);
                        throw e;
                    }
                });
        log.debug("Inserted {} out of {}", inserted.count, total);
    }

    class DeleteBeforeInsertSqlGenerator implements SqlFragmentGenerator<Object> {

        SqlBatchUpdater batchUpdater = null;

        public DeleteBeforeInsertSqlGenerator(SqlStatementBuilder sql) {
            batchUpdater = sql.getBatchUpdater();
        }

        @Override
        public SqlFragmentSource generate(List<Object> deviceIds) {
            SqlStatementBuilder deleteSql = new SqlStatementBuilder();
            deleteSql.append("DELETE FROM").append(batchUpdater.getTableName());
            deleteSql.append("WHERE").append(batchUpdater.getDeleteBeforeInsertColumn()).in(deviceIds);
            deleteSql.appendFragment(batchUpdater.getDeleteBeforeInsertClauses());
            return deleteSql;
        }
    }
}
