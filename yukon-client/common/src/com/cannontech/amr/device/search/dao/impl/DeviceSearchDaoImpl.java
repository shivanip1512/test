package com.cannontech.amr.device.search.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cannontech.amr.device.search.dao.DeviceSearchDao;
import com.cannontech.amr.device.search.model.DeviceSearchResultEntry;
import com.cannontech.amr.device.search.model.FilterBy;
import com.cannontech.amr.device.search.model.OrderByField;
import com.cannontech.amr.device.search.model.SearchField;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;

@Repository
public class DeviceSearchDaoImpl implements DeviceSearchDao {
    @Autowired private SimpleJdbcTemplate jdbcTemplate;
    @Autowired private DeviceSearchRowMapper deviceSearchRowMapper;
    
    @Override
    public SearchResult<DeviceSearchResultEntry> search(List<SearchField> fields, List<FilterBy> filters, OrderByField orderBy, int start, int count) {
        // whereClause
        SqlFragmentCollection whereClause = SqlFragmentCollection.newAndCollection();
        for(FilterBy filter : filters) {
            if(filter.hasValue()) {
                whereClause.add(filter.getWhereClauseFragment());
            }
        }
        
        // Get count
        int totalCount = 0;
        SqlStatementBuilder countSql = new SqlStatementBuilder();
        countSql.append(deviceSearchRowMapper.getSelectFragment(null));
        if (!whereClause.isEmpty()) {
            countSql.append("WHERE").append(whereClause);
        }
        totalCount = jdbcTemplate.getJdbcOperations().queryForInt(countSql.getSql(), countSql.getArguments());
        
        // Get devices
        SqlStatementBuilder meterSql = new SqlStatementBuilder();
        meterSql.append(deviceSearchRowMapper.getSelectFragment(fields));
        if (!whereClause.isEmpty()) {
            meterSql.append("WHERE").append(whereClause);
        }
        meterSql.append("ORDER BY").append(orderBy.getSqlOrderByClause());
        
        List<DeviceSearchResultEntry> resultList = null;
        resultList = jdbcTemplate.getJdbcOperations().<List<DeviceSearchResultEntry>>query(meterSql.getSql(), meterSql.getArguments(), new SearchResultSetExtractor(start, count));
        
        SearchResult<DeviceSearchResultEntry> searchResult = new SearchResult<DeviceSearchResultEntry>();
        searchResult.setBounds(start, count, totalCount);
        searchResult.setResultList(resultList);
        
        return searchResult;
    }

    /**
     * Inner class used to create a list of entries from a result set
     */
    private class SearchResultSetExtractor implements ResultSetExtractor<List<DeviceSearchResultEntry>> {
        private int pageCount = 0;
        private int start = 0; 
        
        public SearchResultSetExtractor(int start, int pageCount) {
            this.pageCount = pageCount;
            this.start = start;
        }
        
        public List<DeviceSearchResultEntry> extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<DeviceSearchResultEntry> entryList = new ArrayList<DeviceSearchResultEntry>();
            
            // Move the cursor to the correct spot in the result set so we only
            // process the results we want
            for(int i = start; i > 0; i--){
                rs.next();
            }
            
            while(rs.next() && pageCount-- > 0){
                
                DeviceSearchResultEntry entry = deviceSearchRowMapper.mapRow(new YukonResultSet(rs));
                entryList.add(entry);
            }
            
            return entryList;
        }
    }
}
