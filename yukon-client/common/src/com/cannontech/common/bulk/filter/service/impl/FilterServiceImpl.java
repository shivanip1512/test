package com.cannontech.common.bulk.filter.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlStatementBuilder;

public class FilterServiceImpl implements FilterService {
    private SimpleJdbcTemplate simpleJdbcTemplate;

    @Override
    public <T> SearchResult<T> filter(UiFilter<? super T> filter,
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

        
        SearchResult<T> retVal = new SearchResult<T>();

        List<T> objectsFromDb = simpleJdbcTemplate.query(sql.getSql(),
                                                         rowMapper,
                                                         sql.getArguments());
        List<T> objectsThatPassedFilters = new ArrayList<T>();
        if (filter == null || filter.getPostProcessingFilters() == null
                || !filter.getPostProcessingFilters().iterator().hasNext()) {
            objectsThatPassedFilters = objectsFromDb;
        } else {
            for (T obj : objectsFromDb) {
                boolean useIt = true;
                for (PostProcessingFilter<? super T> postProcessingFilter
                        : filter.getPostProcessingFilters()) {
                    if (!postProcessingFilter.matches(obj)) {
                        useIt = false;
                        break;
                    }
                }
                if (useIt) {
                    objectsThatPassedFilters.add(obj);
                }
            }
        }

        if (sorter != null) {
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

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
