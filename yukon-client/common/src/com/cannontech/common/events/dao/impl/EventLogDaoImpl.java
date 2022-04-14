package com.cannontech.common.events.dao.impl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.events.dao.EventLogDao;
import com.cannontech.common.events.model.ArgumentColumn;
import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class EventLogDaoImpl implements EventLogDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    
    private static final Logger log = YukonLogManager.getLogger(EventLogDaoImpl.class);

    private List<ArgumentColumn> argumentColumns = List.of(
            new ArgumentColumn("String1", Types.VARCHAR),
            new ArgumentColumn("String2", Types.VARCHAR),
            new ArgumentColumn("String3", Types.VARCHAR),
            new ArgumentColumn("String4", Types.VARCHAR),
            new ArgumentColumn("String5", Types.VARCHAR),
            new ArgumentColumn("String6", Types.VARCHAR),
            new ArgumentColumn("Int7", Types.NUMERIC),
            new ArgumentColumn("Int8", Types.NUMERIC),
            new ArgumentColumn("Int9", Types.NUMERIC),
            new ArgumentColumn("Int10", Types.NUMERIC),
            new ArgumentColumn("Date11", Types.TIMESTAMP),
            new ArgumentColumn("Date12", Types.TIMESTAMP));
    
    private RowMapperWithBaseQuery<EventLog> eventLogRowMapper = new AbstractRowMapperWithBaseQuery<EventLog>() {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT EventLogId, EventType, EventTime, String1, String2, String3, String4, String5, String6, Int7, Int8, Int9, Int10, Date11, Date12");
            retVal.append("FROM EventLog");
            retVal.append("JOIN EventLogType ON EventLog.EventTypeId=EventLogType.EventTypeId");
            return retVal;
        }

        @Override
        public boolean needsWhere() {
            return true;
        }
        
        @Override
        public SqlFragmentSource getOrderBy() {
            return new SimpleSqlFragment("ORDER BY EventTime DESC");
        }
        
        @Override
        public EventLog mapRow(YukonResultSet rs) throws java.sql.SQLException {
            EventLog eventLog = new EventLog();
            eventLog.setEventLogId(rs.getInt("EventLogId"));
            eventLog.setEventType(rs.getString("EventType"));
            eventLog.setDateTime(rs.getDate("EventTime"));

            List<Object> arguments = new ArrayList<>();
            argumentColumns.forEach(argument -> {
                try {
                    switch (argument.getSqlType()) {
                    case Types.VARCHAR:
                        arguments.add(rs.getStringSafe(argument.getColumnName()));
                        break;
                    case Types.NUMERIC:
                        arguments.add(rs.getLong(argument.getColumnName()));
                        break;
                    case Types.TIMESTAMP:
                        arguments.add(rs.getDate(argument.getColumnName()));
                        break;
                    default:
                        log.error("{} not supported", argument.getSqlType());
                    }
                } catch (Exception e) {
                    log.error("Error parsing the value", e);
                }
            });
            eventLog.setArguments(arguments.toArray());
            return eventLog;
        };
    };
    
    @Override
    public RowMapperWithBaseQuery<EventLog> getEventLogRowMapper() {
        return eventLogRowMapper;
    }
    
    @Override
    public void insert(EventLog eventLog) {        
        try {            
            SqlStatementBuilder createSql = new SqlStatementBuilder();
            SqlParameterSink params = createSql.insertInto("EventLog");
            params.addValue("EventLogId", nextValueHelper.getNextValue("EventLog"));
            params.addValue("EventTypeId", getEventTypeId(eventLog));
            params.addValue("EventTime", eventLog.getDateTime());
            AtomicInteger counter = new AtomicInteger(0);
            argumentColumns.forEach(argument -> {
                Object value = eventLog.getArguments()[counter.getAndIncrement()];
                if (value != null) {
                    params.addValue(argument.getColumnName(), value);
                }
            });
            jdbcTemplate.update(createSql);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Unable to create EventLog entry", e);
        }
    }

    /**
     * If Event Type Id is not found, creates one
     */
    private int getEventTypeId(EventLog eventLog) {
        int eventTypeId;
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EventTypeId");
        sql.append("FROM EventLogType");
        sql.append("WHERE EventType").eq(eventLog.getEventType());
        try {
            eventTypeId = jdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {
            SqlStatementBuilder createSql = new SqlStatementBuilder();
            SqlParameterSink params = createSql.insertInto("EventLogType");
            eventTypeId = nextValueHelper.getNextValue("EventLogType");
            params.addValue("EventTypeId", eventTypeId);
            params.addValue("EventType", eventLog.getEventType());
            jdbcTemplate.update(createSql);
            log.info("Created new event log type: {}", eventLog.getEventType());
        }
        return eventTypeId;
    }
    
    private Set<EventCategory> getCategories() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EventType"); 
        sql.append("FROM EventLogType");
        Set<String> allTypes = Sets.newHashSet(jdbcTemplate.query(sql, TypeRowMapper.STRING));
        
        Set<EventCategory> allCategories = Sets.newHashSet();
        
        for (String type : allTypes) {
            EventCategory eventCategory = EventLog.createCategoryForFullType(type);
            allCategories.add(eventCategory);
        }
        
        return allCategories;
    }
    
    @Override
    public Set<EventCategory> getAllCategories() {
        Set<EventCategory> allCategoryLeafs = getCategories();

        Set<EventCategory> categories = Sets.newHashSet(allCategoryLeafs);

        for (EventCategory eventCategory : allCategoryLeafs) {
            collectParentCategories(eventCategory, categories);
        }
        return categories;

    }

    private void collectParentCategories(EventCategory eventCategory, Set<EventCategory> allCategories) {
        EventCategory parent = eventCategory.getParent();
        if (parent != null) {
            allCategories.add(parent);
            collectParentCategories(parent, allCategories);
        }
    }

    @Override
    public SearchResults<EventLog> getFilteredPagedSearchResultByCategories(Iterable<EventCategory> eventCategories,
            ReadableInstant startDate,
            ReadableInstant stopDate,
            PagingParameters paging,
            String filterString) {

        SqlStatementBuilder catSql = null;
      
        // YUK-26003 the controller will set eventCategories = null if user selects ALL so the getAllCategories() will not be used
        /*if(eventCategories != null) {
            Set<EventCategory> slimEventCategories = removeDuplicates(eventCategories);
            catSql = new SqlStatementBuilder();
            catSql.append("AND (");

            SqlFragmentCollection sqlFragmentCollection = getEventCategorySqlFragment(slimEventCategories);
            catSql.appendFragment(sqlFragmentCollection);

            catSql.append(")");
        }*/
        if (eventCategories != null && IterableUtils.size(eventCategories) > 0
                && !Sets.newHashSet(eventCategories).containsAll(getAllCategories())) {
            Set<EventCategory> slimEventCategories = removeDuplicates(eventCategories);
            catSql = new SqlStatementBuilder();
            catSql.append("AND (");

            SqlFragmentCollection sqlFragmentCollection = getEventCategorySqlFragment(slimEventCategories);
            catSql.appendFragment(sqlFragmentCollection);

            catSql.append(")");
        }
        
        SqlStatementBuilder countSql = findAllSqlStatementBuilder(startDate, stopDate, catSql, true, filterString);

        SqlStatementBuilder selectSql = findAllSqlStatementBuilder(startDate, stopDate, catSql, false, filterString);
        
        return SearchResults.pageBasedForOffset(jdbcTemplate, paging, selectSql, countSql, eventLogRowMapper);
    }

    private SqlStatementBuilder findAllSqlStatementBuilder(ReadableInstant startDate,
            ReadableInstant stopDate,
            SqlStatementBuilder catSql,
            boolean isCount,
            String filterString) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        if (isCount) {
            sql.append("SELECT count(*) ");
            sql.append("FROM EventLog");
            sql.append("JOIN EventLogType ON EventLog.EventTypeId=EventLogType.EventTypeId");
        } else {
            sql.append(eventLogRowMapper.getBaseQuery());
        }

        sql.append("WHERE EventTime").lt(stopDate);
        sql.append("AND EventTime").gte(startDate);

        if (catSql != null) {
            sql.append(catSql);
        }
        
        if (!StringUtils.isEmpty(filterString)) {
            sql.append("AND").appendFragment(getEventLogColumnSqlFragment(filterString));
        }
        
        if (!isCount) {
            sql.append("ORDER BY EventTime DESC, EventLogId DESC"); 
        }
        
        return sql;
    }

    /**
     * This method returns an sql fragment that contains an ORed liked list of all
     * the supplied event categories.
     * 
     * EX: If you supplied the event categories Account, DR, and System you would receive
     * 
     *  ( EventType like 'Account%'  OR  
     *    EventType like 'DR%'  OR  
     *    EventType like 'System%' )
     * 
     */
    private SqlFragmentCollection getEventCategorySqlFragment(Set<EventCategory> slimEventCategories) {
        SqlFragmentCollection sqlFragmentCollection = SqlFragmentCollection.newOrCollection();
        for (EventCategory eventCategory : slimEventCategories) {
            SqlStatementBuilder whereFragment = new SqlStatementBuilder();
            whereFragment.append("EventType LIKE ").appendArgument(eventCategory.getFullName() + "%");
            sqlFragmentCollection.add(whereFragment);
            
        }
        return sqlFragmentCollection;
    }

    /**
     * This method will return all needed sql fragments to find if a given value is in one of the
     * EventLog columns.
     * 
     * EX: The user wants to see if the number 987654 exists in the EventLog table.  In order
     * to figure this out they would supply the values ("987654",987654, null) as method parameters.
     *     In return they would get the sql fragment (UPPER(String1) = UPPER('987654' ) OR  
     *                                                UPPER(String2) = UPPER('987654' ) OR  
     *                                                UPPER(String3) = UPPER('987654' ) OR  
     *                                                UPPER(String4) = UPPER('987654' ) OR  
     *                                                UPPER(String5) = UPPER('987654' ) OR  
     *                                                UPPER(String6) = UPPER('987654' ) OR 
     *                                                Int7 = 987654  OR  
     *                                                Int8 = 987654  OR  
     *                                                Int9 = 987654  OR  
     *                                                Int10 = 987654 )) 
     *     
     */
    private SqlFragmentCollection getEventLogColumnSqlFragment(String filterText) {
        if (filterText == null) {
            return null;
        }

        SqlFragmentCollection sqlFragmentCollection = SqlFragmentCollection.newOrCollection();

        // we can represent everything as a varchar
        String varCharValue = filterText;


        // now let's see if we can represent it as a numeric as well
        Double numericValue = null;
        try {
            numericValue = Double.parseDouble(filterText);
        } catch (NumberFormatException e) {
            // This is fine.  It just means the filter text cannot be used a numeric value.
        }

        // Build up SQL fragment
        for (ArgumentColumn argumentColumn : argumentColumns) {
            // Removes case sensitivity for strings.
            SqlStatementBuilder sql = new SqlStatementBuilder();
            switch(argumentColumn.getSqlType()) {
            case Types.VARCHAR:
                if (varCharValue != null) {
                    sql.append("UPPER("+argumentColumn.getColumnName()+")").eq(String.valueOf(varCharValue).toUpperCase());
                    sqlFragmentCollection.add(sql);
                }
                break;
            case Types.NUMERIC:
                if (numericValue != null) {
                    sql.append(argumentColumn.getColumnName()).eq(numericValue);
                    sqlFragmentCollection.add(sql);
                }
                break;
            }

        }

        return sqlFragmentCollection;
    }

    private Set<EventCategory> removeDuplicates(Iterable<EventCategory> eventCategories) {
        Set<EventCategory> result = Sets.newHashSet(eventCategories);
        Iterator<EventCategory> iter = result.iterator();
        while (iter.hasNext()) {
            EventCategory myParent = iter.next().getParent();
            while (myParent != null) {
                if (result.contains(myParent)) {
                    iter.remove();
                    break;
                }
                myParent = myParent.getParent();
            }
        }
        return result;
    }

    @Override
    public List<com.cannontech.common.events.model.ArgumentColumn> getArgumentColumns() {
        return argumentColumns;
    }
}