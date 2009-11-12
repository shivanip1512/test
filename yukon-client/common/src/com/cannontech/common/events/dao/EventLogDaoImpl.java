package com.cannontech.common.events.dao;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.StringRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;

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
        builder.add(new ArgumentColumn("Number7", Types.NUMERIC));
        builder.add(new ArgumentColumn("Number8", Types.NUMERIC));
        builder.add(new ArgumentColumn("Number9", Types.NUMERIC));
        builder.add(new ArgumentColumn("Number10", Types.NUMERIC));
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
    public List<EventLog> findAllByCategories(
            Iterable<EventCategory> eventCategories, Date startDate, Date stopDate) {
        Set<EventCategory> slimEventCategories = removeDuplicates(eventCategories);
        
        if (slimEventCategories.isEmpty()) return Collections.emptyList();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from EventLog");
        sql.append("where");
        SqlFragmentCollection sqlFragmentCollection = SqlFragmentCollection.newOrCollection();
        for (EventCategory eventCategory : slimEventCategories) {
            SqlStatementBuilder whereFragment = new SqlStatementBuilder();
            whereFragment.append("EventType like ").appendArgument(eventCategory.getFullName() + "%");
            sqlFragmentCollection.add(whereFragment);
            
        }
        sql.appendFragment(sqlFragmentCollection);
        sql.append(  "and EventTime").lt(stopDate);
        sql.append(  "and EventTime").gte(startDate);
        sql.append("order by EventTime, EventLogId");
        
        List<EventLog> result = yukonJdbcTemplate.query(sql, eventLogRowMapper);
        return result;
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
