package com.cannontech.web.stars.scheduledDataImport.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.scheduledFileImport.ScheduleImportHistoryEntry;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.web.scheduledDataImport.ScheduledFileImportResult;
import com.cannontech.web.stars.scheduledDataImport.dao.ScheduledDataImportDao;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class ScheduledDataImportDaoImpl implements ScheduledDataImportDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;

    private ScheduledDataImportHistoryRowMapper scheduledDataImportHistoryRowMapper =
        new ScheduledDataImportHistoryRowMapper();

    @Override
    public int insertEntry(ScheduledFileImportResult fileImportResult, int jobGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("ScheduledDataImportHistory");
        int entryId = nextValueHelper.getNextValue("ScheduledDataImportHistory");
        
        sink.addValue("EntryId", entryId);
        sink.addValue("FileName", fileImportResult.getFileName());
        sink.addValue("FileImportType", fileImportResult.getScheduledImportType());
        sink.addValue("ImportDate", fileImportResult.getImportDate());
        sink.addValue("ArchiveFileName", fileImportResult.getArchiveFileName());
        sink.addValue("ArchiveFileExists", fileImportResult.isArchiveFileExists());
        sink.addValue("FailedFileName", fileImportResult.getFailedFileName());
        sink.addValue("FailedFilePath", fileImportResult.getFailedFilePath());
        sink.addValue("SuccessCount", fileImportResult.getSuccessCount());
        sink.addValue("FailureCount", fileImportResult.getFailureCount());
        sink.addValue("JobGroupId", jobGroupId);

        yukonJdbcTemplate.update(sql);

        return entryId;
    }
    
    @Override
    public SearchResults<ScheduleImportHistoryEntry> getImportHistory(int jobGroupId, Date startDate, Date endDate,
                                                          SortBy sortBy,
                                                          Direction direction,
                                                          PagingParameters paging) {
        
        if (direction == null) {
            direction = Direction.desc;
        }
        if (paging == null) {
            paging = PagingParameters.of(25, 1);
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EntryId, FileName, ImportDate, ArchiveFileName,");
        sql.append("    ArchiveFileExists, FailedFileName, FailedFilePath, SuccessCount, FailureCount");
        sql.append(getAllFileHistorySql(jobGroupId, startDate, endDate));
        sql.append("ORDER BY").append(getOrderBy(sortBy, direction));

        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();

        PagingResultSetExtractor<ScheduleImportHistoryEntry> rse = new PagingResultSetExtractor<>(start, count, scheduledDataImportHistoryRowMapper);
        yukonJdbcTemplate.query(sql, rse);

        SearchResults<ScheduleImportHistoryEntry> searchResults = new SearchResults<>();
        searchResults.setBounds(start, count, getAllFileHistoryByFilterCount(jobGroupId, startDate, endDate));
        searchResults.setResultList(rse.getResultList());

        return searchResults;
    }

    private SqlStatementBuilder getOrderBy(SortBy sortBy, Direction direction) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        if (sortBy == null) {
            sql.append(SortBy.DATETIME.getDbString()).append(direction);
        } else if (sortBy == SortBy.TOTAL) {
            sql.append(SortBy.SUCCESS.getDbString());
            sql.append("+");
            sql.append(SortBy.FAILURE.getDbString()).append(direction);
        } else {
            sql.append(sortBy.getDbString()).append(direction);
        }
        return sql;
    }

    private int getAllFileHistoryByFilterCount(int jobGroupId, Date startDate, Date endDate) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append(getAllFileHistorySql(jobGroupId, startDate, endDate));
        return yukonJdbcTemplate.queryForInt(sql);
    }

    private SqlStatementBuilder getAllFileHistorySql(int jobGroupId, Date startDate, Date endDate) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("FROM ScheduledDataImportHistory");

        if (startDate != null && endDate != null) {
            sql.append("WHERE ImportDate").gte(startDate);
            sql.append("AND ImportDate").lte(endDate);
            sql.append("AND JobGroupId").eq(jobGroupId);
        } else {
            sql.append("WHERE JobGroupId").eq(jobGroupId);
        }

        return sql;
    }

    private class ScheduledDataImportHistoryRowMapper implements YukonRowMapper<ScheduleImportHistoryEntry> {

        @Override
        public ScheduleImportHistoryEntry mapRow(YukonResultSet rs) throws SQLException {

            return processEntry(rs);
        }
    }

    
    private static ScheduleImportHistoryEntry processEntry(YukonResultSet rs) throws SQLException {
        int entryId = rs.getInt("EntryId");
        String fileName = rs.getString("FileName");
        Instant importDate = rs.getInstant("ImportDate");
        String archiveFileName = rs.getString("ArchiveFileName");
        boolean archiveFileExists = rs.getBoolean("ArchiveFileExists");
        String failedFileName = rs.getString("FailedFileName");
        String failedFilePath = rs.getString("FailedFilePath");
        int successCount = rs.getInt("SuccessCount");
        int failureCount = rs.getInt("FailureCount");
        
        String archiveFilePath = CtiUtilities.getImportArchiveDirPath();
        return new ScheduleImportHistoryEntry(entryId, fileName, importDate, archiveFileName, archiveFilePath,
            archiveFileExists, failedFileName, failedFilePath, successCount, failureCount);
    }
    
    @Override
    public Map<String, String> getHistoryEntryById(int entryID, boolean isSuccessFile) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        if (isSuccessFile) {
            sql.append("SELECT FileName, ArchiveFileName");
        } else {
            sql.append("SELECT FailedFileName, FailedFilePath");
        }
        sql.append(", JobGroupId");
        sql.append("FROM ScheduledDataImportHistory");
        sql.append("WHERE EntryId").eq(entryID);
        HashMap<String, String> results = new HashMap<>();
        yukonJdbcTemplate.query(sql, (YukonResultSet rs) -> {
            if (isSuccessFile) {
                results.put("fileName", rs.getString("FileName"));
                results.put("archiveFileName", rs.getString("ArchiveFileName"));
            } else {
                results.put("failedFileName", rs.getString("FailedFileName"));
                results.put("failedFilePath", rs.getString("FailedFilePath"));
            }
            results.put("jobGroupId", rs.getString("JobGroupId"));
        });
        return results;
    }
    
    @Override
    public Multimap<Instant, ScheduleImportHistoryEntry> getEntriesWithArchiveByDate() {
        final Multimap<Instant, ScheduleImportHistoryEntry> entries = ArrayListMultimap.create();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EntryId, FileName, ImportDate, ArchiveFileName,");
        sql.append("    ArchiveFileExists, FailedFileName, FailedFilePath, SuccessCount, FailureCount");
        sql.append("FROM ScheduledDataImportHistory");
        sql.append("WHERE  ArchiveFileExists").eq(true);

        yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                ScheduleImportHistoryEntry entry = processEntry(rs);
                entries.put(entry.getImportDate(), entry);
            }
        });
        return entries;
    }

    @Override
    public boolean markArchiveDeleted(int entryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.update("ScheduledDataImportHistory");
        sink.addValue("ArchiveFileExists", false);
        sql.append("WHERE  EntryId").eq(entryId);

        int rowsModified = yukonJdbcTemplate.update(sql);
        return rowsModified > 0;
    }

}
