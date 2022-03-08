package com.cannontech.common.events.dao.impl;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;

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
import com.cannontech.database.SqlUtils;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Sets;

public class EventLogDaoImpl implements EventLogDao {
    
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    
    private final Cache<String, Set<EventCategory>> allCategories = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES).build();
    
    private final int countOfTotalArguments;
    private List<ArgumentColumn> argumentColumns;
    private int countOfNonVariableColumns = 3; // 3 <-- id + type + datetime
    private int[] totalArgumentTypes;
    {
        Builder<ArgumentColumn> builder = ImmutableList.builder();
        builder.add(new ArgumentColumn("String1",Types.VARCHAR));
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
            totalArgumentTypes[i + countOfNonVariableColumns] = argumentColumns.get(i).getSqlType();
        }
    }
    
    private String insertSql;
    {
        insertSql = "INSERT INTO EventLog VALUES (";
        List<String> questionMarks = Collections.nCopies(countOfTotalArguments, "?");
        insertSql += StringUtils.join(questionMarks, ",");
        insertSql += ")";
    }
    
    private RowMapperWithBaseQuery<EventLog> eventLogRowMapper = new AbstractRowMapperWithBaseQuery<EventLog>() {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT EventLogId, EventType, EventTime, String1, String2, String3, String4, String5, String6, Int7, Int8, Int9, Int10, Date11, Date12");
            retVal.append("FROM EventLog");
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
        public EventLog mapRow(YukonResultSet yrs) throws java.sql.SQLException {
            ResultSet rs = yrs.getResultSet();
            EventLog eventLog = new EventLog();
            eventLog.setEventLogId(rs.getInt(1));
            eventLog.setEventType(rs.getString(2));
            eventLog.setDateTime(rs.getTimestamp(3));
            
            Object[] arguments = new Object[argumentColumns.size()];
            for (int i = 0; i < argumentColumns.size(); ++i) {
                Object arg;
                int columnIndex = i + countOfNonVariableColumns + 1;    //columns are 1-based
                if (argumentColumns.get(i).getSqlType() == Types.VARCHAR) {
                    String rawString = rs.getString(columnIndex);
                    arg = SqlUtils.convertDbValueToString(rawString);
                } else {
                    arg = JdbcUtils.getResultSetValue(rs, columnIndex);
                }
                arguments[i] = arg;
            }
            
            eventLog.setArguments(arguments);
            
            return eventLog;
        };
    };
    
    @Override
    public RowMapperWithBaseQuery<EventLog> getEventLogRowMapper() {
        return eventLogRowMapper;
    }
    
    @Override
    public void insert(EventLog eventLog) {
        Object[] totalArguments = new Object[countOfTotalArguments];
        
        totalArguments[0] = nextValueHelper.getNextValue("EventLog"); //EventLogId
        totalArguments[1] = eventLog.getEventType(); // Type
        totalArguments[2] = eventLog.getDateTime(); // DateTime
        
        for (int i = 0; i < argumentColumns.size(); ++i) {
            int inputIndex = i;
            int outputIndex = i + countOfNonVariableColumns;
            
            Object value = eventLog.getArguments()[inputIndex];
            if (value != null && argumentColumns.get(i).getSqlType() == Types.VARCHAR) {
                value = SqlUtils.convertStringToDbValue(value.toString());
            }
            totalArguments[outputIndex] = value;
        } 

        jdbcTemplate.update(insertSql, totalArguments, totalArgumentTypes);
        Set<EventCategory> categories = allCategories.getIfPresent("allCategories");
        if(categories != null && !categories.contains(eventLog.getEventCategory())) {
            allCategories.invalidateAll();
        }
    }
    
    
    /**
     * This doesn't really get all types.  
     * This only gets the types that have already been logged.
     */
    private Set<String> getAllLoggedTypes() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT EventType"); 
        sql.append("FROM EventLog");
        return Sets.newHashSet( yukonJdbcTemplate.query(sql, TypeRowMapper.STRING));
    }
    
    private Set<EventCategory> getAllCategoryLeafs() {
        Set<String> allTypes = getAllLoggedTypes();
        
        Set<EventCategory> allCategories = Sets.newHashSet();
        
        for (String type : allTypes) {
            EventCategory eventCategory = EventLog.createCategoryForFullType(type);
            allCategories.add(eventCategory);
        }
        
        return allCategories;
    }
    
    @Override
    public Set<EventCategory> getAllCategories() {
        //Set<EventCategory> categories = allCategories.getIfPresent("allCategories");
        Set<EventCategory> categories = null;
        if (categories != null) {
            return categories;
        }

        Set<EventCategory> allCategoryLeafs = getAllCategoryLeafs();

        categories = Sets.newHashSet(allCategoryLeafs);

        for (EventCategory eventCategory : allCategoryLeafs) {
            collectParentCategories(eventCategory, categories);
        }
        allCategories.put("allCategories", categories);
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
      
        if (!Sets.newHashSet(eventCategories).containsAll(getAllCategories())) {
            Set<EventCategory> slimEventCategories = removeDuplicates(eventCategories);
            catSql = new SqlStatementBuilder();
            catSql.append("AND (");

            SqlFragmentCollection sqlFragmentCollection = getEventCategorySqlFragment(slimEventCategories);
            catSql.appendFragment(sqlFragmentCollection);

            catSql.append(")");
        }
        
        SqlStatementBuilder countSql = findAllSqlStatementBuilder(startDate, stopDate, catSql, true, filterString);
        SqlStatementBuilder sql = findAllSqlStatementBuilder(startDate, stopDate, catSql, false, filterString);
        
        return SearchResults.pageBasedForOffset(yukonJdbcTemplate, paging, sql, eventLogRowMapper, countSql);
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