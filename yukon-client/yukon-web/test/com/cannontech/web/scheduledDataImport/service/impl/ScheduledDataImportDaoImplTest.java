package com.cannontech.web.scheduledDataImport.service.impl;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.joda.time.Instant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.scheduledFileImport.ScheduleImportHistoryEntry;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.web.stars.scheduledDataImport.dao.ScheduledDataImportDao;
import com.cannontech.web.stars.scheduledDataImport.dao.ScheduledDataImportDao.SortBy;
import com.cannontech.web.stars.scheduledDataImport.dao.impl.ScheduledDataImportDaoImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/com/cannontech/common/daoTestContext.xml"})
public class ScheduledDataImportDaoImplTest {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    private ScheduledDataImportDao scheduledDataImportDao;

    @Before
    public void setUp() throws Exception {
        scheduledDataImportDao = new ScheduledDataImportDaoImpl();
        ReflectionTestUtils.setField(scheduledDataImportDao, "yukonJdbcTemplate", yukonJdbcTemplate);

        prepareBaseData();
    }

    @After
    public void cleanUp() throws Exception {
        cleanBaseData();
    }

    private void cleanBaseData() {
        yukonJdbcTemplate.update("DELETE FROM ScheduledDataImportHistory");
    }

    private void prepareBaseData() {
        // ScheduledDataImportHistory
        String insertScheduledDataImportHistorySql =
            "INSERT INTO ScheduledDataImportHistory (EntryId, FileName, FileImportType, ImportDate, ArchiveFileName, ArchiveFilePath, ArchiveFileExists, FailedFileName, FailedFilePath, SuccessCount, FailureCount, JobGroupId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        yukonJdbcTemplate.update(insertScheduledDataImportHistorySql, 1, "Import file 1.csv", "AssetImport",
            "2019-01-01 01:00:02.447", "Import file 1.csv_ugfjhb87tg87", "C:\\Yukon\\ExportArchive", 1, null, null, 10,
            10, 3061);
        yukonJdbcTemplate.update(insertScheduledDataImportHistorySql, 2, "B Import File.csv", "AssetImport",
            "2019-01-04 01:00:02.447", "B Import File.csv_ugfjhb87tg87", "C:\\Yukon\\ExportArchive", 1, null, null, 12,
            3, 3061);
        yukonJdbcTemplate.update(insertScheduledDataImportHistorySql, 3, "C Import file.csv", "AssetImport",
            "2019-01-09 01:00:02.447", "C Import file.csv_ugfjhb87tg87", "C:\\Yukon\\ExportArchive", 1, null, null, 20,
            0, 3061);
        yukonJdbcTemplate.update(insertScheduledDataImportHistorySql, 4, "Import file 2.csv", "AssetImport",
            "2019-01-05 01:00:02.447", "Import file 2.csv_ugfjhb87tg87", "C:\\Yukon\\ExportArchive", 1, null, null, 30,
            1, 3061);
        yukonJdbcTemplate.update(insertScheduledDataImportHistorySql, 5, "Import file 3.csv", "AssetImport",
            "2019-01-17 01:00:02.447", "Import file 3.csv_ugfjhb87tg87", "C:\\Yukon\\ExportArchive", 1, null, null, 14,
            31, 3061);

    }

    @Test
    public void test_getImportHistory() {

        Instant to = new Instant("2019-01-31T14:17:58.206Z");
        Instant from = new Instant("2018-12-31T14:17:58.206Z");

        PagingParameters paging = PagingParameters.of(25, 1);
        SearchResults<ScheduleImportHistoryEntry> searchResults =
            scheduledDataImportDao.getImportHistory(3061, from, to, SortBy.TOTAL, Direction.desc, paging);
        assertTrue(searchResults.getHitCount() == 5);
    }

    @Test
    public void test_dateFilter() {

        Instant to = new Instant("2019-01-31T14:17:58.206Z");
        Instant from = new Instant("2019-01-20T14:17:58.206Z");

        PagingParameters paging = PagingParameters.of(25, 1);
        SearchResults<ScheduleImportHistoryEntry> searchResults =
            scheduledDataImportDao.getImportHistory(3061, from, to, SortBy.TOTAL, Direction.desc, paging);
        assertTrue(searchResults.getHitCount() == 0);
    }

    @Test
    public void test_pagination() {

        Instant to = new Instant("2019-01-31T14:17:58.206Z");
        Instant from = new Instant("2018-12-31T14:17:58.206Z");

        PagingParameters paging = PagingParameters.of(2, 1);
        SearchResults<ScheduleImportHistoryEntry> searchResults =
            scheduledDataImportDao.getImportHistory(3061, from, to, SortBy.TOTAL, Direction.desc, paging);
        assertTrue(searchResults.getCount() == 2);
    }

    @Test
    public void test_sortByTotal() {

        Instant to = new Instant("2019-01-31T14:17:58.206Z");
        Instant from = new Instant("2018-01-20T14:17:58.206Z");

        PagingParameters paging = PagingParameters.of(25, 1);
        SearchResults<ScheduleImportHistoryEntry> searchResults =
            scheduledDataImportDao.getImportHistory(3061, from, to, SortBy.TOTAL, Direction.desc, paging);
        assertTrue(searchResults.getResultList().get(0).getEntryId() == 5);
    }

    @Test
    public void test_sortByFileName() {

        Instant to = new Instant("2019-01-31T14:17:58.206Z");
        Instant from = new Instant("2018-01-20T14:17:58.206Z");

        PagingParameters paging = PagingParameters.of(25, 1);
        SearchResults<ScheduleImportHistoryEntry> searchResults =
            scheduledDataImportDao.getImportHistory(3061, from, to, SortBy.FILENAME, Direction.asc, paging);
        assertTrue(searchResults.getResultList().get(0).getFileName().equals("B Import File.csv"));
    }

    @Test
    public void test_sortByImportDate() {

        Instant to = new Instant("2019-01-31T14:17:58.206Z");
        Instant from = new Instant("2018-01-20T14:17:58.206Z");
        Instant resultDate = new Instant("2018-12-31T19:30:02.447Z");

        PagingParameters paging = PagingParameters.of(25, 1);
        SearchResults<ScheduleImportHistoryEntry> searchResults =
            scheduledDataImportDao.getImportHistory(3061, from, to, SortBy.DATETIME, Direction.asc, paging);
        assertTrue(searchResults.getResultList().get(0).getImportDate().equals(resultDate));
    }

    @Test
    public void test_sortBySuccessCount() {

        Instant to = new Instant("2019-01-31T14:17:58.206Z");
        Instant from = new Instant("2018-01-20T14:17:58.206Z");

        PagingParameters paging = PagingParameters.of(25, 1);
        SearchResults<ScheduleImportHistoryEntry> searchResults =
            scheduledDataImportDao.getImportHistory(3061, from, to, SortBy.SUCCESS, Direction.asc, paging);
        assertTrue(searchResults.getResultList().get(0).getSuccessCount() == 10);
    }

    @Test
    public void test_sortByNull() {

        Instant to = new Instant("2019-01-31T14:17:58.206Z");
        Instant from = new Instant("2018-01-20T14:17:58.206Z");

        PagingParameters paging = PagingParameters.of(25, 1);
        SearchResults<ScheduleImportHistoryEntry> searchResults =
            scheduledDataImportDao.getImportHistory(3061, from, to, null, Direction.desc, paging);
        assertTrue(searchResults.getResultList().get(0).getFileName().equals("Import file 3.csv"));
    }

    @Test
    public void test_directionNull() {

        Instant to = new Instant("2019-01-31T14:17:58.206Z");
        Instant from = new Instant("2018-01-20T14:17:58.206Z");

        PagingParameters paging = PagingParameters.of(25, 1);
        SearchResults<ScheduleImportHistoryEntry> searchResults =
            scheduledDataImportDao.getImportHistory(3061, from, to, SortBy.SUCCESS, null, paging);
        assertTrue(searchResults.getResultList().get(0).getSuccessCount() == 30);
    }

    @Test
    public void test_pagingNull() {

        Instant to = new Instant("2019-01-31T14:17:58.206Z");
        Instant from = new Instant("2018-01-20T14:17:58.206Z");

        SearchResults<ScheduleImportHistoryEntry> searchResults =
            scheduledDataImportDao.getImportHistory(3061, from, to, SortBy.FAILURE, Direction.desc, null);
        assertTrue(searchResults.getHitCount() == 5);
    }

    @Test
    public void test_dateRangeNull() {

        PagingParameters paging = PagingParameters.of(25, 1);
        SearchResults<ScheduleImportHistoryEntry> searchResults =
            scheduledDataImportDao.getImportHistory(3061, null, null, SortBy.FAILURE, Direction.desc, paging);
        assertTrue(searchResults.getHitCount() == 5);
    }

    @Test
    public void test_getHistoryEntryById() {

        Map<String, String> results = scheduledDataImportDao.getHistoryEntryById(1, true);
        assertTrue(results.isEmpty() == false);
    }
}