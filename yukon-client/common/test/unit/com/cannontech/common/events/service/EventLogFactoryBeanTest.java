package com.cannontech.common.events.service;


import java.sql.Types;
import java.util.List;
import java.util.Set;

import org.joda.time.ReadableInstant;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.config.MockConfigurationSource;
import com.cannontech.common.events.dao.EventLogDao;
import com.cannontech.common.events.model.ArgumentColumn;
import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.events.service.impl.EventLogServiceImpl;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.TransactionExecutor;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;

public class EventLogFactoryBeanTest {

    private final class CurrentThreadExecutor implements TransactionExecutor {
        @Override
        public void execute(Runnable runnable, ExecutorTransactionality transactionality) {
            runnable.run();
        }
    }
    
    private List<EventLog> insertedEventLogs;
    private TestEventLogInterface testEventLog;

    private static class EventLogMockConfigurationService extends MockConfigurationSource{
        @Override
        public String getString(String key) {
            return "";
        }
    }
    
    @Before
    public void setUp() throws Exception {
        insertedEventLogs = Lists.newArrayList();
        
        Builder<ArgumentColumn> builder = ImmutableList.builder();
        builder.add(new ArgumentColumn("String1", Types.VARCHAR));
        builder.add(new ArgumentColumn("String2", Types.VARCHAR));
        builder.add(new ArgumentColumn("Number3", Types.NUMERIC));
        builder.add(new ArgumentColumn("Number4", Types.NUMERIC));
        final List<ArgumentColumn> argumentColumns = builder.build();
        
        EventLogFactoryBean eventLogFactoryBean = new EventLogFactoryBean();
        eventLogFactoryBean.setServiceInterface(TestEventLogInterface.class);
        eventLogFactoryBean.setBeanClassLoader(this.getClass().getClassLoader());
        eventLogFactoryBean.setTransactionExecutor(new CurrentThreadExecutor());
        
        EventLogDao eventLogDaoMock = new EventLogDao() {
            @Override
            public List<ArgumentColumn> getArgumentColumns() {
                return argumentColumns;
            }
            @Override
            public void insert(EventLog eventLog) {
                insertedEventLogs.add(eventLog);
            }
            @Override
            public List<EventLog> findAllByCategories(Iterable<EventCategory> eventCategory, 
                                                      ReadableInstant startDate, 
                                                      ReadableInstant stopDate) {
                return null;
            }
            @Override
            public Set<EventCategory> getAllCategories() {
                return null;
            }
            @Override
            public SearchResult<EventLog> getPagedSearchResultByCategories(Iterable<EventCategory> eventCategories,
                                                                           ReadableInstant startDate, 
                                                                           ReadableInstant stopDate, 
                                                                           Integer start, 
                                                                           Integer pageCount) {
                return null;
            }
            @Override
            public SearchResult<EventLog> getPagedSearchResultByLogTypes(Iterable<String> eventLogTypes,
                                                                         ReadableInstant startDate,
                                                                         ReadableInstant stopDate,
                                                                         Integer start,
                                                                         Integer pageCount) {
                return null;
            }
            @Override
            public SearchResult<EventLog> getFilteredPagedSearchResultByCategories(
                                                                                   Iterable<EventCategory> eventCategories,
                                                                                   ReadableInstant startDate,
                                                                                   ReadableInstant stopDate,
                                                                                   Integer start,
                                                                                   Integer pageCount,
                                                                                   String filterText) {
                return null;
            }
            @Override
            public RowMapperWithBaseQuery<EventLog> getEventLogRowMapper() {
                return null;
            }
            @Override
            public SearchResult<EventLog> findEventsByStringAndPaginate(String searchString, Integer firstRowIndex, Integer pageRowCount) {
                return null;
            }
        };
        
        eventLogFactoryBean.setEventLogDao(eventLogDaoMock);

        EventLogServiceImpl eventLogService = new EventLogServiceImpl();
        eventLogService.setEventLogDao(eventLogDaoMock);
        eventLogService.setConfigurationSource(new EventLogMockConfigurationService());
        eventLogService.setupExcludedEventLogPaths();
        
        eventLogFactoryBean.setEventLogService(eventLogService);
        eventLogFactoryBean.afterPropertiesSet();
        testEventLog = (TestEventLogInterface) eventLogFactoryBean.getObject();
    }
    
    @Test
    public void logUserWithLong() {
        testEventLog.logUserWithLong(new LiteYukonUser(5,"testUser",LoginStatusEnum.ENABLED), 79);
        EventLog eventLog = Iterables.getOnlyElement(insertedEventLogs);
        Assert.assertArrayEquals(new Object[] {"testUser",null,79l,null}, eventLog.getArguments());
    }

    @Test
    public void logStringIntStringLong() {
        testEventLog.logStringIntStringLong("String A", 6, "String B", 99);
        EventLog eventLog = Iterables.getOnlyElement(insertedEventLogs);
        Assert.assertArrayEquals(new Object[] {"String A","String B",6,99l}, eventLog.getArguments());
    }
    
    @Test
    public void logIntegerDouble() {
        testEventLog.logIntegerDouble(55,88.23);
        EventLog eventLog = Iterables.getOnlyElement(insertedEventLogs);
        Assert.assertArrayEquals(new Object[] {null,null,55,88.23d}, eventLog.getArguments());
    }
    
    @Test(expected=BadConfigurationException.class)
    public void testBadInterfaceThrowsException() throws Exception {
        Builder<ArgumentColumn> builder = ImmutableList.builder();
        builder.add(new ArgumentColumn("String1", Types.VARCHAR));
        builder.add(new ArgumentColumn("String2", Types.VARCHAR));
        builder.add(new ArgumentColumn("Number3", Types.NUMERIC));
        builder.add(new ArgumentColumn("Number4", Types.NUMERIC));
        final List<ArgumentColumn> argumentColumns = builder.build();
        
        
        EventLogFactoryBean eventLogFactoryBean = new EventLogFactoryBean();
        eventLogFactoryBean.setServiceInterface(BadTestEventLogInterface.class);
        eventLogFactoryBean.setBeanClassLoader(this.getClass().getClassLoader());
        eventLogFactoryBean.setTransactionExecutor(new CurrentThreadExecutor());
        
        EventLogDao eventLogDaoMock = new EventLogDao() {
            @Override
            public List<ArgumentColumn> getArgumentColumns() {
                return argumentColumns;
            }
            @Override
            public void insert(EventLog eventLog) {
            }
            @Override
            public List<EventLog> findAllByCategories(Iterable<EventCategory> eventCategory, 
                                                      ReadableInstant startDate, 
                                                      ReadableInstant stopDate) {
                return null;
            }
            @Override
            public Set<EventCategory> getAllCategories() {
                return null;
            }
            @Override
            public SearchResult<EventLog> getPagedSearchResultByCategories(Iterable<EventCategory> eventCategories,
                                                                           ReadableInstant startDate,
                                                                           ReadableInstant stopDate,
                                                                           Integer start,
                                                                           Integer pageCount) {
                return null;
            }
            @Override
            public SearchResult<EventLog> getPagedSearchResultByLogTypes(
                                                                         Iterable<String> eventLogTypes,
                                                                         ReadableInstant startDate,
                                                                         ReadableInstant stopDate,
                                                                         Integer start,
                                                                         Integer pageCount) {
                return null;
            }
            @Override
            public SearchResult<EventLog> getFilteredPagedSearchResultByCategories(
                                                                                   Iterable<EventCategory> eventCategories,
                                                                                   ReadableInstant startDate,
                                                                                   ReadableInstant stopDate,
                                                                                   Integer start,
                                                                                   Integer pageCount,
                                                                                   String filterText) {
                return null;
            }
            @Override
            public RowMapperWithBaseQuery<EventLog> getEventLogRowMapper() {
                return null;
            }
            @Override
            public SearchResult<EventLog> findEventsByStringAndPaginate(String searchString, Integer firstRowIndex, Integer pageRowCount) {
                return null;
            }
        };
        
        eventLogFactoryBean.setEventLogDao(eventLogDaoMock);
        
        EventLogServiceImpl eventLogService = new EventLogServiceImpl();
        eventLogService.setEventLogDao(eventLogDaoMock);
        eventLogService.setConfigurationSource(new EventLogMockConfigurationService());
        eventLogFactoryBean.setEventLogService(eventLogService);
        
        eventLogFactoryBean.afterPropertiesSet();
        eventLogFactoryBean.getObject();
    }
}
