package com.cannontech.common.events.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventLog;

public interface EventLogDao {

    public void insert(EventLog eventLog);
    public List<ArgumentColumn> getArgumentColumns();
    public List<EventLog> findAllByCategories(Iterable<EventCategory> eventCategory, Date startDate, Date stopDate);
    public Set<EventCategory> getAllCategories();
    
    public static class ArgumentColumn {
        public ArgumentColumn(String columnName, int sqlType) {
            this.columnName = columnName;
            this.sqlType = sqlType;
        }
        public final String columnName;
        public final int sqlType;
        
        @Override
        public String toString() {
            return columnName;
        }
    }

}
