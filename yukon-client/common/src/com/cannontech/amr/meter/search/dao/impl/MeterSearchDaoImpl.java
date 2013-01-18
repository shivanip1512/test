package com.cannontech.amr.meter.search.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.meter.search.dao.MeterSearchDao;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.MeterSearchOrderBy;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlProvidingRowMapper;

public class MeterSearchDaoImpl implements MeterSearchDao {

    private SimpleJdbcTemplate jdbcTemplate = null;
    private SqlProvidingRowMapper<Meter> meterRowMapper;

    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings("unchecked")
    public SearchResult<Meter> search(List<FilterBy> filterByList,
                                          MeterSearchOrderBy orderBy, 
                                          final int start, 
                                          final int count) {

        int totalCount = 0;
        List<Meter> resultList = null;
        
        // filterBySqlFragmentSources
        List<SqlFragmentSource> filterBySqlFragmentSources = new ArrayList<SqlFragmentSource>();
        for (FilterBy filterBy : filterByList) {
        	filterBySqlFragmentSources.add(filterBy.getSqlWhereClause());
        }
        
        // whereClause
        SqlFragmentCollection whereClause = null;
        if (filterBySqlFragmentSources.size() > 0) {
        	whereClause = SqlFragmentCollection.newAndCollection();
        	for (SqlFragmentSource filterBySqlFragmentSource : filterBySqlFragmentSources) {
        		whereClause.add(filterBySqlFragmentSource);
        	}
        }
        
        // GET COUNT
        SqlStatementBuilder countSql = new SqlStatementBuilder();
        countSql.append("SELECT");
        countSql.append("COUNT(*)");
        countSql.append("FROM");
        countSql.append("DeviceMeterGroup device");
        countSql.append("JOIN yukonpaobject ypo ON device.deviceid = ypo.paobjectid");
        countSql.append("LEFT OUTER JOIN DeviceCarrierSettings dcs ON dcs.deviceid = ypo.paobjectid");
        countSql.append("LEFT OUTER JOIN DeviceRoutes dr ON ypo.paobjectid = dr.deviceid");
        countSql.append("LEFT OUTER JOIN yukonpaobject rypo ON dr.routeid = rypo.paobjectid");
        if (whereClause != null) {
        	countSql.append("WHERE").append(whereClause);
        }

        totalCount = jdbcTemplate.getJdbcOperations().queryForInt(countSql.getSql(), countSql.getArguments());

        // GET METERS
        SqlStatementBuilder meterSql = new SqlStatementBuilder();
        meterSql.append(meterRowMapper.getSql());
        if (whereClause != null) {
        	meterSql.append("WHERE").append(whereClause);
        }
        meterSql.append("ORDER BY").append(orderBy.toString());

        resultList = (List<Meter>) jdbcTemplate.getJdbcOperations().query(meterSql.getSql(), meterSql.getArguments(), new SearchPaoResultSetExtractor(start, count));

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
