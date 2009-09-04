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
    public <T, U extends T> SearchResult<U> filter(List<UiFilter<T>> filters,
            Comparator<T> primarySorter, Comparator<T> secondarySorter,
            int startIndex, int count,
            RowMapperWithBaseQuery<U> rowMapper) {
        SqlFragmentCollection whereClause = SqlFragmentCollection.newAndCollection();
        if (filters != null) {
            for (UiFilter<T> filter : filters) {
                List<SqlFilter> sqlFilters = filter.getSqlFilters();
                if (sqlFilters != null) {
                    for (SqlFilter sqlFilter : sqlFilters) {
                        whereClause.add(sqlFilter.getWhereClauseFragment());
                    }
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

        
        SearchResult<U> retVal = new SearchResult<U>();

        List<U> objectsFromDb = simpleJdbcTemplate.query(sql.getSql(),
                                                         rowMapper,
                                                         sql.getArguments());
        List<U> objectsThatPassedFilters = new ArrayList<U>();
        if (filters == null || filters.size() == 0) {
            objectsThatPassedFilters = objectsFromDb;
        } else {
            for (U obj : objectsFromDb) {
                boolean useIt = true;
                out: for (UiFilter<T> filter : filters) {
                    List<PostProcessingFilter<T>> postProcessingFilters = filter.getPostProcessingFilters();
                    if (postProcessingFilters != null) {
                        for (PostProcessingFilter<T> postProcessingFilter : postProcessingFilters) {
                            if (!postProcessingFilter.matches(obj)) {
                                useIt = false;
                                break out;
                            }
                        }
                    }
                }
                if (useIt) {
                    objectsThatPassedFilters.add(obj);
                }
            }
        }

        // We're making use of the fact that Collections.sort is stable. (Items
        // that are equal as per the primarySorter are left as sorted by the
        // secondarySorter which we applied first.)
        if (secondarySorter != null) {
            Collections.sort(objectsThatPassedFilters, secondarySorter);
        }
        if (primarySorter != null) {
            Collections.sort(objectsThatPassedFilters, primarySorter);
        }

        List<U> resultList = new ArrayList<U>();
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
