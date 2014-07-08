package com.cannontech.amr.meter.search.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.impl.MeterRowMapper;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.meter.search.dao.MeterSearchDao;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.MeterSearchOrderBy;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.YukonJdbcTemplate;

public class MeterSearchDaoImpl implements MeterSearchDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private MeterRowMapper meterRowMapper;

    @Override
    public SearchResults<YukonMeter> search(List<FilterBy> filterByList,
                                          MeterSearchOrderBy orderBy, 
                                          final int start, 
                                          final int count) {

        int totalCount = 0;
        
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
        countSql.append("FROM YukonPaobject ypo JOIN Device d ON ypo.paobjectId = d.deviceId");
        countSql.append("JOIN DeviceMeterGroup device ON d.deviceId = device.deviceId");
        countSql.append("LEFT JOIN DeviceCarrierSettings dcs ON dcs.deviceid = ypo.paobjectid");
        countSql.append("LEFT JOIN DeviceRoutes dr ON ypo.paobjectid = dr.deviceid");
        countSql.append("LEFT JOIN yukonpaobject rypo ON dr.routeid = rypo.paobjectid");
        countSql.append("LEFT JOIN RFNAddress rfna ON rfna.deviceId = d.deviceId");
        if (whereClause != null) {
        	countSql.append("WHERE").append(whereClause);
        }

        totalCount = jdbcTemplate.queryForInt(countSql);

        // GET METERS
        SqlStatementBuilder meterSql = new SqlStatementBuilder();
        meterSql.append(meterRowMapper.getSql());
        if (whereClause != null) {
        	meterSql.append("WHERE").append(whereClause);
        }
        meterSql.append("ORDER BY").append(orderBy.toString());

        PagingResultSetExtractor<YukonMeter> rse = new PagingResultSetExtractor<YukonMeter>(start, count, meterRowMapper);
        jdbcTemplate.query(meterSql, rse);

        SearchResults<YukonMeter> searchResult = new SearchResults<YukonMeter>();
        searchResult.setBounds(start, count, totalCount);
        searchResult.setResultList(rse.getResultList());
        
        return searchResult;
    }
    
}