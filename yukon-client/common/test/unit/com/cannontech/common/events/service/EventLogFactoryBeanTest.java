package com.cannontech.common.events.service;


import java.sql.Types;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.common.events.dao.EventLogDao;
import com.cannontech.common.events.dao.EventLogDao.ArgumentColumn;
import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.util.TransactionExecutor;
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

    @Before
    public void setUp() throws Exception {
        insertedEventLogs = Lists.newArrayList();
        
        Builder<ArgumentColumn> builder = ImmutableList.builder();
        builder.add(new ArgumentColumn("String1", Types.VARCHAR));
        builder.add(new ArgumentColumn("String2", Types.VARCHAR));
        builder.add(new ArgumentColumn("Int3", Types.BIGINT));
        builder.add(new ArgumentColumn("Int4", Types.BIGINT));
        final List<ArgumentColumn> argumentColumns = builder.build();
        
        
        EventLogFactoryBean eventLogFactoryBean = new EventLogFactoryBean();
        eventLogFactoryBean.setServiceInterface(TestEventLogInterface.class);
        eventLogFactoryBean.setBeanClassLoader(this.getClass().getClassLoader());
        eventLogFactoryBean.setTransactionExecutor(new CurrentThreadExecutor());
        eventLogFactoryBean.setEventLogDao(new EventLogDao() {
            @Override
            public List<ArgumentColumn> getArgumentColumns() {
                return argumentColumns;
            }
            @Override
            public void insert(EventLog eventLog) {
                insertedEventLogs.add(eventLog);
            }
            @Override
            public List<EventLog> findAllByCategories(
                    Iterable<EventCategory> eventCategory) {
                return null;
            }
            @Override
            public Set<EventCategory> getAllCategories() {
                return null;
            }
        });
        
        eventLogFactoryBean.afterPropertiesSet();
        testEventLog = (TestEventLogInterface) eventLogFactoryBean.getObject();
    }
    
    @Test
    public void logUserWithLong() {
        testEventLog.logUserWithLong(new LiteYukonUser(5,"testUser","ENABLED"), 79);
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
}
