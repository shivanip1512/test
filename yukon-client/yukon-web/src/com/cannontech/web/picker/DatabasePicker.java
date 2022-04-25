package com.cannontech.web.picker;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterDao;
import com.cannontech.common.model.Direction;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

/**
 * A picker that queries from the database for instances of T.
 */
public abstract class DatabasePicker<T> extends BasePicker<T> {
    
    @Autowired private FilterDao filterDao;
    
    private List<SqlFilter> sqlFilters;
    private List<PostProcessingFilter<T>> postProcessingFilters;
    private RowMapperWithBaseQuery<T> rowMapper;
    private String[] searchColumnNames;
    
    protected DatabasePicker(RowMapperWithBaseQuery<T> rowMapper, String[] searchColumnNames) {
        this.rowMapper = rowMapper;
        this.searchColumnNames = searchColumnNames;
    }
    
    /**
     * Subclasses can override this method to add their own filters, probably based on extraArgs or the user
     * context.
     * 
     * @param sqlFilters Add any new SQL filters to this list.
     * @param postProcessingFilters Add any new post processing filters to this list.
     * @param userContext The {@link YukonUserContext} for the logged in user.
     * @param extraArgs Any extra arguments passed to the picker by the JSP tag.
     */
    protected void updateFilters(List<SqlFilter> sqlFilters, List<PostProcessingFilter<T>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        // Default implementation adds no new filtering.
    }
    
    /**
     * Subclasses need to override this method if the id field name is not the
     * same as the database field.
     */
    protected String getDatabaseIdFieldName() {
        return getIdFieldName();
    }
    
    /**
     * Generate a SQL filter for initially selected items.
     */
    private void addInitialSearchFilters(List<SqlFilter> sqlFilters, final Iterable<Integer> initialIds) {
        
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
    public final SearchResults<T> search(final String ss, int start, int count, String extraArgs,
            YukonUserContext userContext) {
        
        final List<SqlFilter> mySqlFilters = Lists.newArrayList();
        final List<PostProcessingFilter<T>> myPostProcessingFilters = Lists.newArrayList();
        updateFilters(mySqlFilters, myPostProcessingFilters, extraArgs, userContext);
        
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
                    fragment.append("UPPER(" + searchColumnName + ") LIKE UPPER(")
                    .appendArgument('%' + ss + '%').append(")");
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
        SearchResults<T> dbResults =
            filterDao.filter(filter, null, start, count, rowMapper);
        return dbResults;
    }
    
    @Override
    public SearchResults<T> search(Collection<Integer> initialIds, String extraArgs, String sortBy,
            Direction direction, YukonUserContext userContext) {
       throw new UnsupportedOperationException();
    }
    
    
    @Override
    public SearchResults<T> search(String ss, int start, int count,
            String extraArgs, String sortBy, Direction direction, YukonUserContext userContext) {
       throw new UnsupportedOperationException();
    }
    
    @Override
    public final SearchResults<T> search(Collection<Integer> initialIds, String extraArgs, YukonUserContext userContext) {
        
        if (initialIds == null || !initialIds.iterator().hasNext()) {
            return null;
        }
        
        final List<SqlFilter> mySqlFilters = Lists.newArrayList();
        final List<PostProcessingFilter<T>> myPostProcessingFilters = Lists.newArrayList();
        updateFilters(mySqlFilters, myPostProcessingFilters, extraArgs, userContext);
        addInitialSearchFilters(mySqlFilters, initialIds);
        
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
        
        SearchResults<T> dbResults =
            filterDao.filter(filter, null, 0, Integer.MAX_VALUE, rowMapper);
        return dbResults;
    }
    
    public void setSqlFilters(List<SqlFilter> sqlFilters) {
        this.sqlFilters = sqlFilters;
    }
    
    public void setPostProcessingFilters(List<PostProcessingFilter<T>> postProcessingFilters) {
        this.postProcessingFilters = postProcessingFilters;
    }
    
}