package com.cannontech.web.picker.v2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public abstract class DatabasePicker<T> extends BasePicker<T> {
    private List<SqlFilter> filters;
    private RowMapperWithBaseQuery<T> rowMapper;
    private String[] searchColumnNames;

    private FilterService filterService;

    protected DatabasePicker(RowMapperWithBaseQuery<T> rowMapper,
            String[] searchColumnNames) {
        this.filters = Lists.newArrayList();
        this.rowMapper = rowMapper;
        this.searchColumnNames = searchColumnNames;
    }

    /**
     * Subclasses can override this method to convert extraArgs into specific
     * filters.
     */
    @Override
    public SearchResult<T> search(final String ss, int start, int count,
            String extraArgs, YukonUserContext userContext) {
        List<SqlFilter> extraFilters = Lists.newArrayList();
        return search(ss, start, count, extraFilters, userContext);
    }
    
    public SearchResult<T> search(final String ss, int start, int count,
            List<SqlFilter> extraFilters, YukonUserContext userContext) {
        final List<SqlFilter> myFilters = Lists.newArrayList();
        if (filters != null) {
            myFilters.addAll(filters);
        }
        myFilters.add(new SqlFilter() {
            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlFragmentCollection retVal = SqlFragmentCollection.newOrCollection();
                for (String searchColumnName : searchColumnNames) {
                    SqlStatementBuilder fragment = new SqlStatementBuilder();
                    fragment.append("LOWER(" + searchColumnName + ") LIKE LOWER(").appendArgument('%' + ss + '%').append(")");
                    retVal.add(fragment);
                }
                return retVal;
            }
        });
        myFilters.addAll(extraFilters);

        UiFilter<T> filter = new UiFilter<T>() {
            @Override
            public Iterable<PostProcessingFilter<T>> getPostProcessingFilters() {
                // Since this is used in a picker we most certainly do NOT
                // want any post processing filters. We don't want to force
                // the filter to retrieve all of the results.
                return null;
            }

            @Override
            public Iterable<SqlFilter> getSqlFilters() {
                return myFilters;
            }
        };

        // We can't use a sorter either or the filter service would need to
        // get everything from the database.
        return filterService.filter(filter, null, start, count, rowMapper);
    }

    public void setFilters(List<SqlFilter> filters) {
        this.filters = filters;
    }

    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }
}
