package com.cannontech.common.events.dao.impl;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import com.cannontech.common.events.dao.EventLogDao;
import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.StringRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;

@Repository
public class EventLogDaoImpl implements EventLogDao {
    
    private JdbcTemplate jdbcTemplate;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;

    private final int countOfTotalArguments;
    private List<ArgumentColumn> argumentColumns;
    private int countOfNonVariableColumns = 3; // 3 <-- id + type + datetime
    private int[] totalArgumentTypes;
    {
        Builder<ArgumentColumn> builder = ImmutableList.builder();
        builder.add(new ArgumentColumn("String1", Types.VARCHAR));
        builder.add(new ArgumentColumn("String2", Types.VARCHAR));
        builder.add(new ArgumentColumn("String3", Types.VARCHAR));
        builder.add(new ArgumentColumn("String4", Types.VARCHAR));
        builder.add(new ArgumentColumn("String5", Types.VARCHAR));
        builder.add(new ArgumentColumn("String6", Types.VARCHAR));
        builder.add(new ArgumentColumn("Int7", Types.NUMERIC));
        builder.add(new ArgumentColumn("Int8", Types.NUMERIC));
        builder.add(new ArgumentColumn("Int9", Types.NUMERIC));
        builder.add(new ArgumentColumn("Int10", Types.NUMERIC));
        builder.add(new ArgumentColumn("Date11", Types.TIMESTAMP));
        builder.add(new ArgumentColumn("Date12", Types.TIMESTAMP));
        argumentColumns = builder.build();

        countOfTotalArguments = argumentColumns.size() + countOfNonVariableColumns; 
        
        totalArgumentTypes = new int[countOfTotalArguments];
        totalArgumentTypes[0] = Types.NUMERIC;
        totalArgumentTypes[1] = Types.VARCHAR;
        totalArgumentTypes[2] = Types.TIMESTAMP;
        for (int i = 0; i < argumentColumns.size(); ++i) {
            totalArgumentTypes[i + countOfNonVariableColumns] = argumentColumns.get(i).sqlType;
        }
    }
    
    private String insertSql;
    {
        insertSql = "insert into EventLog values (";
        List<String> questionMarks = Collections.nCopies(countOfTotalArguments, "?");
        insertSql += StringUtils.join(questionMarks, ",");
        insertSql += ")";
    }
    
    private ParameterizedRowMapper<EventLog> eventLogRowMapper = new ParameterizedRowMapper<EventLog>() {
        public EventLog mapRow(ResultSet rs, int rowNum) throws java.sql.SQLException {
            EventLog eventLog = new EventLog();
            eventLog.setEventLogId(rs.getInt(1));
            eventLog.setEventType(rs.getString(2));
            eventLog.setDateTime(rs.getTimestamp(3));
            
            Object[] arguments = new Object[argumentColumns.size()];
            for (int i = 0; i < argumentColumns.size(); ++i) {
                arguments[i] = JdbcUtils.getResultSetValue(rs, i + countOfNonVariableColumns + 1); // columns are 1-based
            }
            eventLog.setArguments(arguments);
            
            return eventLog;
        };
    };
    
    @Override
    public void insert(EventLog eventLog) {
        Object[] totalArguments = new Object[countOfTotalArguments];
        
        totalArguments[0] = nextValueHelper.getNextValue("EventLog"); //EventLogId
        totalArguments[1] = eventLog.getEventType(); // Type
        totalArguments[2] = eventLog.getDateTime(); // DateTime
        
        System.arraycopy(eventLog.getArguments(), 0, totalArguments, 3, argumentColumns.size());
        
        jdbcTemplate.update(insertSql, totalArguments, totalArgumentTypes);
    }
    
    
    /**
     * This doesn't really get all types.  
     * This only gets the types that have already been logged.
     */
    public Set<String> getAllTypes() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select distinct EventType from EventLog");
        
        Set<String> result = Sets.newHashSet();
        yukonJdbcTemplate.query(sql, new StringRowMapper(), result);
        return result;
    }
    
    public Set<EventCategory> getAllCategoryLeafs() {
        Set<String> allTypes = getAllTypes();
        
        Set<EventCategory> allCategories = Sets.newHashSet();
        
        for (String type : allTypes) {
            EventCategory eventCategory = EventLog.createCategoryForFullType(type);
            allCategories.add(eventCategory);
        }
        
        return allCategories;
    }
    
    public Set<EventCategory> getAllCategories() {
        Set<EventCategory> allCategoryLeafs = getAllCategoryLeafs();
        
        Set<EventCategory> allCategories = Sets.newHashSet(allCategoryLeafs);
        
        for (EventCategory eventCategory : allCategoryLeafs) {
            collectParentCategories(eventCategory, allCategories);
        }
        
        return allCategories;
    }
    
    private void collectParentCategories(EventCategory eventCategory, Set<EventCategory> allCategories) {
        EventCategory parent = eventCategory.getParent();
        if (parent != null) {
            allCategories.add(parent);
            collectParentCategories(parent, allCategories);
        }
    }

    public List<EventLog> findAllByCategory(EventCategory eventCategory) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from EventLog");
        sql.append("where EventType like ").appendArgument(eventCategory.getFullName() + "%");
        sql.append("order by EventTime, EventLogId");
        
        List<EventLog> result = yukonJdbcTemplate.query(sql, eventLogRowMapper);
        return result;
    }
    
    @Override
    public List<EventLog> findAllByCategories(Iterable<EventCategory> eventCategories, 
                                              ReadableInstant startDate, 
                                              ReadableInstant stopDate) {
        Set<EventCategory> slimEventCategories = removeDuplicates(eventCategories);
        
        if (slimEventCategories.isEmpty()) return Collections.emptyList();
        
        SqlStatementBuilder sql = findAllSqlStatementBuilder(startDate, stopDate, slimEventCategories);
        sql.append("ORDER BY EventTime, EventLogId");

        List<EventLog> result = yukonJdbcTemplate.query(sql, eventLogRowMapper);
        return result;
    }

    
    @Override
    public SearchResult<EventLog> getPagedSearchResultByCategories(Iterable<EventCategory> eventCategories, 
                                                                   ReadableInstant startDate, 
                                                                   ReadableInstant stopDate, 
                                                                   Integer start, 
                                                                   Integer pageCount) {
        SearchResult<EventLog> result = new SearchResult<EventLog>();
        Set<EventCategory> slimEventCategories = removeDuplicates(eventCategories);
        if (slimEventCategories.isEmpty()){
            result.setBounds(0, pageCount, 0);
            result.setResultList(new ArrayList<EventLog> ());
            return result;
        }
        
        /* Get row count. */
        SqlStatementBuilder countSql = new SqlStatementBuilder();
        countSql.append("select count(*) from EventLog");
        countSql.append("where (");
        SqlFragmentCollection sqlFragmentCollection = getEventCategorySqlFragment(slimEventCategories);
        countSql.appendFragment(sqlFragmentCollection);
        countSql.append(  ") and EventTime").lt(stopDate);
        countSql.append(  "and EventTime").gte(startDate);
        
        int hitCount = yukonJdbcTemplate.queryForInt(countSql);
        
        /* Get paged data. */
        SqlStatementBuilder sql = findAllSqlStatementBuilder(startDate, stopDate, slimEventCategories);
        sql.append("ORDER BY EventTime, EventLogId");
        
        PagingResultSetExtractor<EventLog> rse = new PagingResultSetExtractor<EventLog>(start, pageCount, eventLogRowMapper);
        yukonJdbcTemplate.query(sql, rse);
        result.setResultList(rse.getResultList());
        result.setBounds(start, pageCount, hitCount);
        
        return result;
    }

    @Override
    public SearchResult<EventLog> getPagedSearchResultByLogTypes(Iterable<String> eventLogTypes,
                                                                 ReadableInstant startDate,
                                                                 ReadableInstant stopDate,
                                                                 Integer start,
                                                                 Integer pageCount) {
        SearchResult<EventLog> result = new SearchResult<EventLog>();
//        Set<String> slimEventCategories = removeDuplicates(eventLogTypes);
        Iterator<String> iterator = eventLogTypes.iterator();
        if (!iterator.hasNext()){
            result.setBounds(0, pageCount, 0);
            result.setResultList(new ArrayList<EventLog> ());
            return result;
        }
        
        /* Get row count. */
        SqlStatementBuilder countSql = new SqlStatementBuilder();
        countSql.append("select count(*) from EventLog");
        countSql.append("where EventType IN (",eventLogTypes.iterator(),")");
        countSql.append(  "and EventTime").lt(stopDate);
        countSql.append(  "and EventTime").gte(startDate);
        
        int hitCount = yukonJdbcTemplate.queryForInt(countSql);
        
        /* Get paged data. */
        SqlStatementBuilder sql = findAllSqlStatementBuilder(startDate, 
                                                             stopDate,
                                                             eventLogTypes);
        sql.append("order by EventTime, EventLogId");

        PagingResultSetExtractor<EventLog> rse = new PagingResultSetExtractor<EventLog>(start, pageCount, eventLogRowMapper);
        yukonJdbcTemplate.query(sql, rse);
        result.setResultList(rse.getResultList());
        result.setBounds(start, pageCount, hitCount);
        
        return result;
    }


    @Override
    public SearchResult<EventLog> 
                getFilteredPagedSearchResultByCategories(Iterable<EventCategory> eventCategories,
                                                         ReadableInstant startDate,
                                                         ReadableInstant stopDate,
                                                         Integer start,
                                                         Integer pageCount,
                                                         String filterString,
                                                         Double filterDouble,
                                                         ReadableInstant filterDate) {

        SearchResult<EventLog> result = new SearchResult<EventLog>();
        Set<EventCategory> slimEventCategories = removeDuplicates(eventCategories);
        if (slimEventCategories.isEmpty()){
            result.setBounds(0, pageCount, 0);
            result.setResultList(new ArrayList<EventLog> ());
            return result;
        }
        
        /* Get row count. */
        SqlStatementBuilder countSql = new SqlStatementBuilder();
        countSql.append("SELECT COUNT(*)");
        countSql.append("FROM EventLog EL");
        countSql.append("WHERE").appendFragment(getEventCategorySqlFragment(slimEventCategories));
        countSql.append("AND EL.EventTime").lt(stopDate);
        countSql.append("AND EL.EventTime").gte(startDate);
        countSql.append("AND").appendFragment(getEventLogColumnSqlFragment(filterString,
                                                                           filterDouble,
                                                                           filterDate));
        
        int hitCount = yukonJdbcTemplate.queryForInt(countSql);
        
        /* Get paged data. */
        SqlStatementBuilder sql = findAllSqlStatementBuilder(startDate, stopDate, slimEventCategories);
        sql.append("AND").appendFragment(getEventLogColumnSqlFragment(filterString,
                                                                      filterDouble,
                                                                      filterDate));
        sql.append("ORDER BY EventTime, EventLogId");
        
        PagingResultSetExtractor<EventLog> rse = 
            new PagingResultSetExtractor<EventLog>(start, pageCount, eventLogRowMapper);
        yukonJdbcTemplate.query(sql, rse);
        result.setResultList(rse.getResultList());
        result.setBounds(start, pageCount, hitCount);
        
        return result;
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
            whereFragment.append("EventType like ").appendArgument(eventCategory.getFullName() + "%");
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
     *     In return they would get the sql fragment (( UPPER(String1) = UPPER('987654' )  OR  
     *                                                  UPPER(String2) = UPPER('987654' )  OR  
     *                                                  UPPER(String3) = UPPER('987654' )  OR  
     *                                                  UPPER(String4) = UPPER('987654' )  OR  
     *                                                  UPPER(String5) = UPPER('987654' )  OR  
     *                                                  UPPER(String6) = UPPER('987654' ) ) OR 
     *                                                ( Int7 = 987654  OR  
     *                                                  Int8 = 987654  OR  
     *                                                  Int9 = 987654  OR  
     *                                                  Int10 = 987654 )) 
     *     
     */
    private SqlFragmentCollection getEventLogColumnSqlFragment(String filterString,
                                                               Double filterDouble,
                                                               ReadableInstant filterDate) {
        SqlFragmentCollection sqlFragmentCollection = SqlFragmentCollection.newOrCollection();
        
        sqlFragmentCollection.add(getColumnsSql(filterString, Types.VARCHAR));
        sqlFragmentCollection.add(getColumnsSql(filterDouble, Types.NUMERIC));
        sqlFragmentCollection.add(getColumnsSql(filterDate, Types.TIMESTAMP));
        
        return sqlFragmentCollection;
    }

    /**
     * This method will return all needed sql fragments to compare the value to a supplied 
     * Types value.
     * 
     * EX: If you supply (25, Types.NUMERIC) you will get (Int7 = 25 OR
     *                                                     Int8 = 25 OR
     *                                                     Int9 = 25 OR
     *                                                     Int10 = 25)
     * 
     * NOTE: This method is case insensitive for Varchars. 
     * EX. (user = USER)
     * 
     */
    private SqlFragmentCollection getColumnsSql(Object filter, int type) {
        
        if (filter == null) {
            return null;
        }
        
        SqlFragmentCollection sqlFragmentCollection = SqlFragmentCollection.newOrCollection();
        for (ArgumentColumn argumentColumn : argumentColumns) {
            if (argumentColumn.sqlType != type ) {
                continue;
            }

            // Removes case sensitivity for strings.
            SqlStatementBuilder sql = new SqlStatementBuilder();
            if (type == Types.VARCHAR) {
                sql.append("UPPER("+argumentColumn.columnName+")").eqUppercase(String.valueOf(filter));
                sqlFragmentCollection.add(sql);
            } else {
                sql.append(argumentColumn.columnName).eq(filter);
                sqlFragmentCollection.add(sql);
            }
        }
        
        return sqlFragmentCollection;
    }
    
    private SqlStatementBuilder findAllSqlStatementBuilder(ReadableInstant startDate,
                                                           ReadableInstant stopDate,
                                                           Iterable<String> eventLogTypes) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM EventLog EL");
        sql.append("WHERE EL.EventType IN ('",eventLogTypes,"')");
        sql.append("AND EL.EventTime").lt(stopDate);
        sql.append("AND EL.EventTime").gte(startDate);

        return sql;
    }

    private SqlStatementBuilder findAllSqlStatementBuilder(ReadableInstant startDate, 
                                                           ReadableInstant stopDate, 
                                                           Set<EventCategory> slimEventCategories) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * ");
        sql.append("FROM EventLog EL");
        sql.append("WHERE (");
        
        SqlFragmentCollection sqlFragmentCollection = getEventCategorySqlFragment(slimEventCategories);
        sql.appendFragment(sqlFragmentCollection);
        
        sql.append(")");
        sql.append("AND EL.EventTime").lt(stopDate);
        sql.append("AND EL.EventTime").gte(startDate);
        return sql;
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
    public List<com.cannontech.common.events.dao.EventLogDao.ArgumentColumn> getArgumentColumns() {
        return argumentColumns;
    }
    
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
}
