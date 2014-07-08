package com.cannontech.common.bulk.filter.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterDao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;

public class FilterDaoImpl implements FilterDao {
	
    private YukonJdbcTemplate yukonJdbcTemplate;
    private Logger log = YukonLogManager.getLogger(FilterDaoImpl.class);

    @Override
    public <T> SearchResults<T> filter(UiFilter<T> filter,
            Comparator<? super T> sorter, int startIndex, int count,
            RowMapperWithBaseQuery<T> rowMapper) {
        SqlFragmentCollection whereClause = SqlFragmentCollection.newAndCollection();
        if (filter != null) {
            Iterable<SqlFilter> sqlFilters = filter.getSqlFilters();
            if (sqlFilters != null) {
                for (SqlFilter sqlFilter : sqlFilters) {
                    whereClause.add(sqlFilter.getWhereClauseFragment());
                }
            }
        }
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(rowMapper.getBaseQuery());
        String filterSql = whereClause.getSql();
        if (!StringUtils.isEmpty(filterSql))
        {
            if (rowMapper.needsWhere()) {
                sql.append("WHERE");
            } else {
                sql.append("AND");
            }
            sql.append(whereClause);
        }

        SqlFragmentSource orderByClause = rowMapper.getOrderBy();
        if (orderByClause != null) {
            sql.append(orderByClause);
        }

        SearchResults<T> retVal = new SearchResults<T>();

        List<T> objectsFromDb = yukonJdbcTemplate.query(sql, rowMapper);
        
        log.debug("Retrieved " + objectsFromDb.size() + " objects from database.");
        
        List<T> objectsThatPassedFilters = new ArrayList<T>();
        if (filter == null || filter.getPostProcessingFilters() == null
                || !filter.getPostProcessingFilters().iterator().hasNext()) {
            objectsThatPassedFilters = objectsFromDb;
        } else {
        	
        	log.debug("Begin applying post processing filter(s).");
        	
        	for (PostProcessingFilter<T> postProcessingFilter : 
        	    filter.getPostProcessingFilters()) {
                
        	    List<T> processedList = postProcessingFilter.process(objectsFromDb);
                objectsFromDb = processedList;
            }
        	
        	objectsThatPassedFilters = objectsFromDb;
            log.debug("Finished applying post processing filter(s): " + objectsThatPassedFilters.size() + "/" + objectsFromDb.size() + " objects passed filter.");
        }

        if (sorter != null) {
        	log.debug("Sorting objects.");
            Collections.sort(objectsThatPassedFilters, sorter);
        }

        List<T> resultList = new ArrayList<T>();
        for (int index = startIndex;
            index < objectsThatPassedFilters.size() && index - startIndex < count;
            index++) {
            resultList.add(objectsThatPassedFilters.get(index));
        }

        retVal.setResultList(resultList);
        retVal.setBounds(startIndex, count, objectsThatPassedFilters.size());

        return retVal;
    }
    
    @Override
    public <T> List<T> filter(UiFilter<T> filter, Comparator<? super T> sorter, RowMapperWithBaseQuery<T> rowMapper) {
        
        SqlFragmentCollection whereClause = SqlFragmentCollection.newAndCollection();
        if (filter != null) {
            Iterable<SqlFilter> sqlFilters = filter.getSqlFilters();
            if (sqlFilters != null) {
                for (SqlFilter sqlFilter : sqlFilters) {
                    whereClause.add(sqlFilter.getWhereClauseFragment());
                }
            }
        }
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(rowMapper.getBaseQuery());
        String filterSql = whereClause.getSql();
        if (!StringUtils.isEmpty(filterSql))
        {
            if (rowMapper.needsWhere()) {
                sql.append("WHERE");
            } else {
                sql.append("AND");
            }
            sql.append(whereClause);
        }

        SqlFragmentSource orderByClause = rowMapper.getOrderBy();
        if (orderByClause != null) {
            sql.append(orderByClause);
        }

        List<T> objectsFromDb = yukonJdbcTemplate.query(sql, rowMapper);
        
        log.debug("Retrieved " + objectsFromDb.size() + " objects from database.");
        
        List<T> objectsThatPassedFilters = new ArrayList<T>();
        if (filter == null || filter.getPostProcessingFilters() == null
                || !filter.getPostProcessingFilters().iterator().hasNext()) {
            objectsThatPassedFilters = objectsFromDb;
        } else {
            
            log.debug("Begin applying post processing filter(s).");
            
            for (PostProcessingFilter<T> postProcessingFilter : 
                filter.getPostProcessingFilters()) {
                
                List<T> processedList = postProcessingFilter.process(objectsFromDb);
                objectsFromDb = processedList;
            }
            
            objectsThatPassedFilters = objectsFromDb;
            log.debug("Finished applying post processing filter(s): " + objectsThatPassedFilters.size() + "/" + objectsFromDb.size() + " objects passed filter.");
        }

        if (sorter != null) {
            log.debug("Sorting objects.");
            Collections.sort(objectsThatPassedFilters, sorter);
        }

        return objectsThatPassedFilters;
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
