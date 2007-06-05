package com.cannontech.amr.csr.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.amr.csr.dao.CsrSearchDao;
import com.cannontech.amr.csr.model.CsrSearchField;
import com.cannontech.amr.csr.model.FilterBy;
import com.cannontech.amr.csr.model.OrderBy;
import com.cannontech.amr.csr.model.SearchPao;
import com.cannontech.common.search.SearchResult;

public class CsrSearchDaoImpl implements CsrSearchDao {

    private SimpleJdbcTemplate jdbcTemplate = null;

    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings("unchecked")
    public SearchResult<SearchPao> search(List<FilterBy> filterByList,
                                          OrderBy orderBy, 
                                          final int start, 
                                          final int count) {

        // Check to see if we're filtering by route
        boolean filterByRoute = false;
        for(FilterBy filter : filterByList){
            if(filter.getField().equals(CsrSearchField.ROUTE)){
                filterByRoute = true;
                break;
            }
        }
        
        int totalCount = 0;
        List<SearchPao> resultList = null;
        
        // Special case for route because we need to join yukonpaobject to itself to get route name
        // which can be a performance hit.
        if(orderBy.getField() == CsrSearchField.ROUTE || filterByRoute){
            
            // Get total number of records for search
            String sqlCount = "SELECT                                               " + 
            "       count(*)                                                        " + 
            "   FROM                                                                " + 
            "       yukonpaobject ypo                                               " + 
            "       LEFT JOIN devicecarriersettings dcs                             " + 
            "           ON dcs.deviceid = ypo.paobjectid                            " + 
            "       LEFT JOIN devicemetergroup dmg                                  " + 
            "           ON dmg.deviceid = ypo.paobjectid                            " + 
            "       LEFT JOIN deviceroutes dr                                       " + 
            "           on ypo.paobjectid = dr.deviceid                             " +
            "       JOIN yukonpaobject rypo                                         " +
            "           on dr.routeid = rypo.paobjectid                             " +
            ((filterByList.size() > 0) ? " WHERE " + StringUtils.join(filterByList, " AND ") : "");
            
            totalCount = jdbcTemplate.queryForInt(sqlCount);
            
            // Get all of the devices that match the search criteria
            String sql = "SELECT                                                    " + 
            "       ypo.paobjectid,                                                 " + 
            "       ypo.paoname as devicename,                                      " + 
            "       ypo.type as devicetype,                                         " + 
            "       dcs.address,                                                    " + 
            "       dmg.meternumber,                                                " + 
            "       dmg.collectiongroup,                                            " + 
            "       dmg.billinggroup,                                               " + 
            "       rypo.paoname as route                                           " + 
            "   FROM                                                                " + 
            "       yukonpaobject ypo                                               " + 
            "       LEFT JOIN devicecarriersettings dcs                             " + 
            "           ON dcs.deviceid = ypo.paobjectid                            " + 
            "       LEFT JOIN devicemetergroup dmg                                  " + 
            "           ON dmg.deviceid = ypo.paobjectid                            " + 
            "       LEFT JOIN deviceroutes dr                                       " + 
            "           on ypo.paobjectid = dr.deviceid                             " +
            "       JOIN yukonpaobject rypo                                         " +
            "           on dr.routeid = rypo.paobjectid                             " +
            ((filterByList.size() > 0) ? " WHERE " + StringUtils.join(filterByList, " AND ") : "") +
            "   ORDER BY                                                            " +
            orderBy.toString();
            
            
            resultList = (List<SearchPao>) jdbcTemplate.getJdbcOperations()
                                     .query(sql,
                                            new Object[] {},
                                            new SearchPaoResultSetExtractor(start, count));
        } else {
            
            // Get total number of records for search
            String sqlCount = "SELECT                                               " + 
            "       count(*)                                                        " + 
            "   FROM                                                                " + 
            "       yukonpaobject ypo                                               " + 
            "       LEFT JOIN devicecarriersettings dcs                             " + 
            "           ON dcs.deviceid = ypo.paobjectid                            " + 
            "       LEFT JOIN devicemetergroup dmg                                  " + 
            "           ON dmg.deviceid = ypo.paobjectid                            " + 
            "       LEFT JOIN deviceroutes dr                                       " + 
            "           on ypo.paobjectid = dr.deviceid                             " +
            ((filterByList.size() > 0) ? " WHERE " + StringUtils.join(filterByList, " AND ") : "");
            
            System.out.println(sqlCount);
            totalCount = jdbcTemplate.queryForInt(sqlCount);

            
            // Get all of the devices that match the search criteria
            String sql = "SELECT                                                    " + 
            "       ypo.paobjectid,                                                 " + 
            "       ypo.paoname as devicename,                                      " + 
            "       ypo.type as devicetype,                                         " + 
            "       dcs.address,                                                    " + 
            "       dmg.meternumber,                                                " + 
            "       dmg.collectiongroup,                                            " + 
            "       dmg.billinggroup,                                               " + 
            "       dr.routeid as route                                             " + 
            "   FROM                                                                " + 
            "       yukonpaobject ypo                                               " + 
            "       LEFT JOIN devicecarriersettings dcs                             " + 
            "           ON dcs.deviceid = ypo.paobjectid                            " + 
            "       LEFT JOIN devicemetergroup dmg                                  " + 
            "           ON dmg.deviceid = ypo.paobjectid                            " + 
            "       LEFT JOIN deviceroutes dr                                       " + 
            "           on ypo.paobjectid = dr.deviceid                             " +
            ((filterByList.size() > 0) ? " WHERE " + StringUtils.join(filterByList, " AND ") : "") +
            "   ORDER BY                                                            " +
            orderBy.toString();
            
            
            resultList = (List<SearchPao>) jdbcTemplate.getJdbcOperations()
                                     .query(sql,
                                            new Object[] {},
                                            new SearchPaoResultSetExtractor(start, count));
            
            // Get the route ids for the devices from the previous query
            List<Integer> routeIdList = new ArrayList<Integer>();
            for(SearchPao pao : resultList) {
                
                String routeId = pao.getRoute();
                if(routeId != null && routeId.length() > 0){
                    routeIdList.add(Integer.valueOf(routeId));
                }
            }
            
            Map<String, String> routeIdNameMap = new HashMap<String, String>();
            
            // Only query for the route names if any of the selected devices have a
            // route
            if(routeIdList.size() > 0) {
                
                sql = "SELECT                                                           " + 
                "       paobjectid,                                                     " + 
                "       paoname                                                         " +
                "   FROM                                                                " +
                "       yukonpaobject                                                   " +
                "   WHERE                                                               " +
                "       paobjectid in (" + StringUtils.join(routeIdList, ",") + ")      ";
                
                routeIdNameMap = (Map<String, String>) jdbcTemplate.getJdbcOperations()
                   .query(sql,
                          new Object[] {},
                          new ResultSetExtractor() {
             
                              public Object extractData(
                                      ResultSet rs)
                                      throws SQLException,
                                      DataAccessException {
             
                                  Map<String, String> routeIdNameMap = new HashMap<String, String>();
             
                                  while (rs.next()) {
                                      routeIdNameMap.put(rs.getString("paobjectid"),
                                                         rs.getString("paoname"));
                                  }
                                  return routeIdNameMap;
                              }
                          }
                   );
            }
            
            // Attach the routes to the devices
            for(SearchPao pao : resultList) {
                if(routeIdNameMap.containsKey(pao.getRoute())){
                    // Replace route id with route name
                    pao.setRoute(routeIdNameMap.get(pao.getRoute()));
                } else {
                    // pao has no route
                    pao.setRoute("");
                }
            }
            
        }
        
        SearchResult<SearchPao> searchResult = new SearchResult<SearchPao>();
        searchResult.setBounds(start, count, totalCount);
        searchResult.setResultList((List<SearchPao>) resultList);
        
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
            
            List<SearchPao> paoList = new ArrayList<SearchPao>();
            
            // Move the cursor to the correct spot in the result set so we only
            // process the results we want
            for(int i = start; i > 0; i--){
                rs.next();
            }
            
            while(rs.next() && pageCount-- > 0){
                
                SearchPao pao = new SearchPao();
                pao.setPaoId(rs.getInt("paobjectid"));
                pao.setPaoName(rs.getString("devicename"));
                pao.setType(rs.getString("devicetype"));
                pao.setAddress(rs.getString("address"));
                pao.setMeterNumber(rs.getString("meternumber"));
                pao.setCollectionGroup(rs.getString("collectiongroup"));
                pao.setBillingGroup(rs.getString("billinggroup"));
                pao.setRoute(rs.getString("route"));
                
                paoList.add(pao);
            }

            return paoList;
        }
    }
}
