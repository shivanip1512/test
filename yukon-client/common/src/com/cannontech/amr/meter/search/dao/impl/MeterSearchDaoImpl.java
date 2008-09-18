package com.cannontech.amr.meter.search.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.meter.search.dao.MeterSearchDao;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.OrderBy;
import com.cannontech.common.search.SearchResult;
import com.cannontech.database.SqlProvidingRowMapper;

public class MeterSearchDaoImpl implements MeterSearchDao {

    private SimpleJdbcTemplate jdbcTemplate = null;
    private SqlProvidingRowMapper<Meter> meterRowMapper;

    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings("unchecked")
    public SearchResult<Meter> search(List<FilterBy> filterByList,
                                          OrderBy orderBy, 
                                          final int start, 
                                          final int count) {

        int totalCount = 0;
        List<Meter> resultList = null;
        
        // Get total number of records for search
        String sqlCount = "SELECT                                               " + 
        "       count(*)                                                        " + 
        "   FROM                                                                " + 
        "       devicemetergroup device                                                        " +
        "       JOIN yukonpaobject ypo                                          " + 
        "           ON device.deviceid = ypo.paobjectid                         " + 
        "       LEFT OUTER JOIN devicecarriersettings dcs                       " + 
        "           ON dcs.deviceid = ypo.paobjectid                            " + 
        "       LEFT OUTER JOIN deviceroutes dr                                 " + 
        "           on ypo.paobjectid = dr.deviceid                             " +
        "       LEFT OUTER JOIN yukonpaobject rypo                              " +
        "           on dr.routeid = rypo.paobjectid                             " +
        ((filterByList.size() > 0) ? " WHERE " + StringUtils.join(filterByList, " AND ") : "");

        // Add the filter by values to a list to be used in the prepared statement
        List<String> filterList = new ArrayList<String>();
        if(filterByList.size() > 0) {
            for(FilterBy filter : filterByList) {
                filterList.addAll(filter.getFilterValues());
            }
        }

        totalCount = jdbcTemplate.getJdbcOperations().queryForInt(sqlCount, filterList.toArray());

        String sql = meterRowMapper.getSql() +
        ((filterByList.size() > 0) ? " WHERE " + StringUtils.join(filterByList, " AND ") : "") +
        "   ORDER BY                                                            " +
        orderBy.toString();


        resultList = (List<Meter>) jdbcTemplate.getJdbcOperations()
            .query(sql,
                   filterList.toArray(),
                   new SearchPaoResultSetExtractor(start, count));

        SearchResult<Meter> searchResult = new SearchResult<Meter>();
        searchResult.setBounds(start, count, totalCount);
        searchResult.setResultList((List<Meter>) resultList);
        
        return searchResult;
    }
    
    /**
     * Inner class used to create a list of SearchPaos from a result set
     */
    private class SearchPaoResultSetExtractor implements ResultSetExtractor {
        
        private int pageCount = 0;
        private int start = 0; 

        public SearchPaoResultSetExtractor(int start, int pageCount) {
            this.pageCount = pageCount;
            this.start = start;
        }

        public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
            
            List<Meter> paoList = new ArrayList<Meter>();
            
            // Move the cursor to the correct spot in the result set so we only
            // process the results we want
            for(int i = start; i > 0; i--){
                rs.next();
            }
            
            while(rs.next() && pageCount-- > 0){
                
                Meter meter = meterRowMapper.mapRow(rs, rs.getRow());
                paoList.add(meter);
            }

            return paoList;
        }
    }
    
    public void setMeterRowMapper(
            SqlProvidingRowMapper<Meter> meterRowMapper) {
        this.meterRowMapper = meterRowMapper;
    }
}
