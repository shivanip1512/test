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

/**
 * A picker that queries from the database for instances of T.
 *
 * @param <T>
 */
public abstract class DatabasePicker<T> extends BasePicker<T> {
	private List<SqlFilter> sqlFilters;
    private List<PostProcessingFilter<T>> postProcessingFilters;
    private RowMapperWithBaseQuery<T> rowMapper;
    private String[] searchColumnNames;

    private FilterService filterService;

    protected DatabasePicker(RowMapperWithBaseQuery<T> rowMapper,
            String[] searchColumnNames) {
        this.rowMapper = rowMapper;
        this.searchColumnNames = searchColumnNames;
    }

    /**
     * Subclasses can override this method to add their own filters, probably
     * based on extraArgs or the user context.
     */
    protected void updateFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<T>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
    }

    /**
     * Subclasses need to override this method if the id field name is not the
     * same as the database field.
     */
    protected String getDatabaseIdFieldName() {
        return getIdFieldName();
    }

    /**
     * Subclasses need to override this method if the id field name is not the
     * same as the database field.
     */
    protected void addInitialSearchFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<T>> postProcessingFilters,
            final Iterable<Integer> initialIds) {
        SqlFilter initialSqlFilter = new SqlFilter() {
            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append(getDatabaseIdFieldName()).in(initialIds);
                return sql;
            }};
        sqlFilters.add(initialSqlFilter);
    }

    @Override
    public final SearchResult<T> search(final String ss, int start, int count,
            String extraArgs, YukonUserContext userContext) {
        final List<SqlFilter> mySqlFilters = Lists.newArrayList();
        final List<PostProcessingFilter<T>> myPostProcessingFilters = Lists.newArrayList();
        updateFilters(mySqlFilters, myPostProcessingFilters, extraArgs,
                      userContext);

        // sql filters
        if (sqlFilters != null) {
        	mySqlFilters.addAll(sqlFilters);
        }
        mySqlFilters.add(new SqlFilter() {
            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlFragmentCollection retVal = SqlFragmentCollection.newOrCollection();
                for (String searchColumnName : searchColumnNames) {
                    SqlStatementBuilder fragment = new SqlStatementBuilder();
                    fragment.append("UPPER(" + searchColumnName + ") LIKE UPPER(").appendArgument('%' + ss + '%').append(")");
                    retVal.add(fragment);
                }
                return retVal;
            }
        });

        // post processing filters
        if (postProcessingFilters != null) {
        	myPostProcessingFilters.addAll(postProcessingFilters);
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
        SearchResult<T> dbResults =
            filterService.filter(filter, null, start, count, rowMapper);
        return dbResults;
    }

    @Override
    public final SearchResult<T> search(Iterable<Integer> initialIds,
            String extraArgs, YukonUserContext userContext) {
        if (initialIds == null || !initialIds.iterator().hasNext()) {
            return null;
        }

        final List<SqlFilter> mySqlFilters = Lists.newArrayList();
        final List<PostProcessingFilter<T>> myPostProcessingFilters = Lists.newArrayList();
        updateFilters(mySqlFilters, myPostProcessingFilters, extraArgs, userContext);
        addInitialSearchFilters(mySqlFilters, myPostProcessingFilters, initialIds);

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

        SearchResult<T> dbResults =
            filterService.filter(filter, null, 0, Integer.MAX_VALUE, rowMapper);
        return dbResults;
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
