package com.cannontech.common.events.service;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.sql.Types;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cannontech.common.config.MockConfigurationSource;
import com.cannontech.common.events.dao.EventLogDao;
import com.cannontech.common.events.model.ArgumentColumn;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.events.service.impl.EventLogServiceImpl;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.util.TransactionExecutor;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class EventLogFactoryBeanTest {

    private final class CurrentThreadExecutor implements TransactionExecutor {
        @Override
        public void execute(Runnable runnable, ExecutorTransactionality transactionality) {
            runnable.run();
        }
    }

    private List<EventLog> insertedEventLogs;
    private TestEventLogInterface testEventLog;

    private static class EventLogMockConfigurationService extends MockConfigurationSource {
        @Override
        public String getString(String key) {
            return "";
        }
    }

    @BeforeEach
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

        EventLogDao eventLogDaoMock = createMock(EventLogDao.class);
        expect(eventLogDaoMock.getArgumentColumns()).andReturn(argumentColumns).atLeastOnce();
        eventLogDaoMock.insert(EasyMock.anyObject(EventLog.class));
        expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                insertedEventLogs.add((EventLog) getCurrentArguments()[0]);
                return null;
            }
        });
        replay(eventLogDaoMock);

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
        testEventLog.logUserWithLong(new LiteYukonUser(5, "testUser", LoginStatusEnum.ENABLED), 79);
        EventLog eventLog = Iterables.getOnlyElement(insertedEventLogs);
        assertArrayEquals(new Object[] { "testUser", null, 79l, null }, eventLog.getArguments());
    }

    @Test
    public void logStringIntStringLong() {
        testEventLog.logStringIntStringLong("String A", 6, "String B", 99);
        EventLog eventLog = Iterables.getOnlyElement(insertedEventLogs);
        assertArrayEquals(new Object[] { "String A", "String B", 6, 99l }, eventLog.getArguments());
    }

    @Test
    public void logIntegerDouble() {
        testEventLog.logIntegerDouble(55, 88.23);
        EventLog eventLog = Iterables.getOnlyElement(insertedEventLogs);
        assertArrayEquals(new Object[] { null, null, 55, 88.23d }, eventLog.getArguments());
    }

    @Test
    public void testBadInterfaceThrowsException() throws Exception {
        Assertions.assertThrows(BadConfigurationException.class, () -> {
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

            EventLogDao eventLogDaoMock = createMock(EventLogDao.class);
            expect(eventLogDaoMock.getArgumentColumns()).andReturn(argumentColumns).atLeastOnce();
            replay(eventLogDaoMock);
            eventLogFactoryBean.setEventLogDao(eventLogDaoMock);

            EventLogServiceImpl eventLogService = new EventLogServiceImpl();
            eventLogService.setEventLogDao(eventLogDaoMock);
            eventLogService.setConfigurationSource(new EventLogMockConfigurationService());
            eventLogFactoryBean.setEventLogService(eventLogService);

            eventLogFactoryBean.afterPropertiesSet();
            eventLogFactoryBean.getObject();
        });

    }
}
