package com.cannontech.web.stars.scheduledDataImport.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.scheduledFileImport.ScheduleImportHistoryEntry;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.web.stars.scheduledDataImport.dao.ScheduledDataImportDao;

public class ScheduledDataImportDaoImpl implements ScheduledDataImportDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    private ScheduledDataImportHistoryRowMapper scheduledDataImportHistoryRowMapper =
        new ScheduledDataImportHistoryRowMapper();

    @Override
    public List<ScheduleImportHistoryEntry> getImportHistory(int jobGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EntryId, FileName, ImportDate, ArchiveFileName, ArchiveFilePath,");
        sql.append("    ArchiveFileExists, FailedFileName, FailedFilePath, SuccessCount, FailureCount");
        sql.append("FROM ScheduledDataImportHistory");
        sql.append("WHERE JobGroupId").eq(jobGroupId);
        sql.append("ORDER BY ImportDate DESC");
        return yukonJdbcTemplate.query(sql, scheduledDataImportHistoryRowMapper);
    }

    private class ScheduledDataImportHistoryRowMapper implements YukonRowMapper<ScheduleImportHistoryEntry> {

        @Override
        public ScheduleImportHistoryEntry mapRow(YukonResultSet rs) throws SQLException {

            int entryId = rs.getInt("EntryId");
            String fileName = rs.getString("FileName");
            Instant importDate = rs.getInstant("ImportDate");
            String archiveFileName = rs.getString("ArchiveFileName");
            String archiveFilePath = rs.getString("ArchiveFilePath");
            boolean archiveFileExists = rs.getBoolean("ArchiveFileExists");
            String failedFileName = rs.getString("FailedFileName");
            String failedFilePath = rs.getString("FailedFilePath");
            int successCount = rs.getInt("SuccessCount");
            int failureCount = rs.getInt("FailureCount");
            return new ScheduleImportHistoryEntry(entryId, fileName, importDate, archiveFileName, archiveFilePath,
                archiveFileExists, failedFileName, failedFilePath, successCount, failureCount);
        }
    }

}
