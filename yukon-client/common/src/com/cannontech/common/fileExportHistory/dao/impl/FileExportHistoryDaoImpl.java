package com.cannontech.common.fileExportHistory.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.fileExportHistory.dao.FileExportHistoryDao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class FileExportHistoryDaoImpl implements FileExportHistoryDao {
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    private FileExportHistoryRowMapper fileExportHistoryRowMapper = new FileExportHistoryRowMapper();

    @Override
    public int insertEntry(String originalFileName, String fileName, FileExportType type, String initiator,
            String exportPath) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("FileExportHistory");
        int entryId = nextValueHelper.getNextValue("FileExportHistory");

        sink.addValue("EntryId", entryId);
        sink.addValue("OriginalFileName", originalFileName);
        sink.addValue("FileName", fileName);
        sink.addValue("FileExportType", type);
        sink.addValue("Initiator", initiator);
        sink.addValue("ExportDate", Instant.now());
        sink.addValue("ExportPath", exportPath);
        sink.addValue("ArchiveFileExists", true);

        yukonJdbcTemplate.update(sql);

        return entryId;
    }

    @Override
    public Multimap<String, ExportHistoryEntry> getEntriesWithArchiveByInitiator() {
        final Multimap<String, ExportHistoryEntry> entries = ArrayListMultimap.create();
        SqlStatementBuilder sql = getBaseSelectSql();
        sql.append("WHERE ArchiveFileExists").eq(true);

        yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                ExportHistoryEntry entry = processEntry(rs);
                entries.put(entry.getInitiator(), entry);
            }
        });
        return entries;
    }

    @Override
    public Multimap<Instant, ExportHistoryEntry> getEntriesWithArchiveByDate() {
        final Multimap<Instant, ExportHistoryEntry> entries = ArrayListMultimap.create();
        SqlStatementBuilder sql = getBaseSelectSql();
        sql.append("WHERE ArchiveFileExists").eq(true);

        yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                ExportHistoryEntry entry = processEntry(rs);
                entries.put(entry.getDate(), entry);
            }
        });
        return entries;
    }

    @Override
    public boolean markArchiveDeleted(int entryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.update("FileExportHistory");
        sink.addValue("ArchiveFileExists", false);
        sql.append("WHERE EntryId").eq(entryId);

        int rowsModified = yukonJdbcTemplate.update(sql);
        return rowsModified > 0;
    }

    @Override
    public List<ExportHistoryEntry> getAllEntries() {
        SqlStatementBuilder sql = getBaseSelectSql();

        List<ExportHistoryEntry> entries = yukonJdbcTemplate.query(sql, fileExportHistoryRowMapper);
        return entries;
    }

    @Override
    public List<ExportHistoryEntry> getFilteredEntries(String nameFragment, String initiatorFragment) {
        String clause2Start = "WHERE ";

        SqlStatementBuilder sql = getBaseSelectSql();

        if (StringUtils.isNotBlank(nameFragment)) {
            sql.append("WHERE OriginalFileName").contains(nameFragment);
            clause2Start = "AND ";
        }
        if (StringUtils.isNotBlank(initiatorFragment)) {
            sql.append(clause2Start).append("Initiator").contains(initiatorFragment);
        }

        List<ExportHistoryEntry> entries = yukonJdbcTemplate.query(sql, fileExportHistoryRowMapper);
        return entries;
    }

    @Override
    public ExportHistoryEntry getEntry(int entryId) {
        SqlStatementBuilder sql = getBaseSelectSql();
        sql.append("WHERE EntryId").eq(entryId);

        ExportHistoryEntry entry = yukonJdbcTemplate.queryForObject(sql, fileExportHistoryRowMapper);
        return entry;
    }

    private SqlStatementBuilder getBaseSelectSql() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EntryId, OriginalFileName, FileName, FileExportType, Initiator, ExportDate, ExportPath, ArchiveFileExists");
        sql.append("FROM FileExportHistory");
        return sql;
    }

    private static ExportHistoryEntry processEntry(YukonResultSet rs) throws SQLException {
        int entryId = rs.getInt("EntryId");
        String originalFileName = rs.getString("OriginalFileName");
        String fileName = rs.getString("FileName");
        FileExportType type = rs.getEnum("FileExportType", FileExportType.class);
        String initiator = rs.getString("Initiator");
        Instant date = rs.getInstant("ExportDate");
        String exportPath = rs.getString("ExportPath");
        boolean archiveFileExists = rs.getBoolean("ArchiveFileExists");
        return new ExportHistoryEntry(entryId, originalFileName, fileName, type, initiator, date, exportPath,
            archiveFileExists);
    }

    private class FileExportHistoryRowMapper implements YukonRowMapper<ExportHistoryEntry> {
        @Override
        public ExportHistoryEntry mapRow(YukonResultSet rs) throws SQLException {
            return processEntry(rs);
        }
    }
}
