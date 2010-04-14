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

	private List<SqlFilter> sqlFilters;
    private List<PostProcessingFilter<T>> postProcessingFilters;
    private RowMapperWithBaseQuery<T> rowMapper;
    private String[] searchColumnNames;

    private FilterService filterService;

    protected DatabasePicker(RowMapperWithBaseQuery<T> rowMapper, String[] searchColumnNames) {
    	
        this.rowMapper = rowMapper;
        this.searchColumnNames = searchColumnNames;
    }

    /**
     * Subclasses can override this method to convert extraArgs into specific
     * filters.
     */
    @Override
    public SearchResult<T> search(final String ss, int start, int count, String extraArgs, YukonUserContext userContext) {
        return search(ss, start, count, null, null, userContext);
    }
    
    public SearchResult<T> search(final String ss, int start, int count,
            					  List<SqlFilter> extraSqlFilters,
            					  List<PostProcessingFilter<T>> extraPostProcessingFilters,
            					  YukonUserContext userContext) {
    	
    	// sql filters
        final List<SqlFilter> mySqlFilters = Lists.newArrayList();
        if (sqlFilters != null) {
        	mySqlFilters.addAll(sqlFilters);
        }
        mySqlFilters.add(new SqlFilter() {
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
        if (extraSqlFilters != null) {
        	mySqlFilters.addAll(extraSqlFilters);
        }
        
        // post processing filters
        final List<PostProcessingFilter<T>> myPostProcessingFilters = Lists.newArrayList();
        if (postProcessingFilters != null) {
        	myPostProcessingFilters.addAll(postProcessingFilters);
        }
        if (extraPostProcessingFilters != null) {
        	myPostProcessingFilters.addAll(extraPostProcessingFilters);
        }

        // combine sql and post processing filter into a single UiFilter
        UiFilter<T> filter = new UiFilter<T>() {
            @Override
            public Iterable<PostProcessingFilter<T>> getPostProcessingFilters() {
                return myPostProcessingFilters;
            }

            @Override
            public Iterable<SqlFilter> getSqlFilters() {
                return mySqlFilters;
            }
        };

        // no sorter support
        return filterService.filter(filter, null, start, count, rowMapper);
    }

    public void setSqlFilters(List<SqlFilter> sqlFilters) {
        this.sqlFilters = sqlFilters;
    }
    
    public void setPostProcessingFilters(List<PostProcessingFilter<T>> postProcessingFilters) {
		this.postProcessingFilters = postProcessingFilters;
	}

    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }
}
