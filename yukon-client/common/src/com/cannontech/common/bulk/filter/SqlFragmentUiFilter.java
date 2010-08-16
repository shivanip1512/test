package com.cannontech.common.bulk.filter;

import java.util.Collections;

import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public abstract class SqlFragmentUiFilter<T> implements UiFilter<T> {

    @Override
    public Iterable<PostProcessingFilter<T>> getPostProcessingFilters() {
        return null;
    }

    @Override
    public Iterable<SqlFilter> getSqlFilters() {
        return Collections.<SqlFilter>singleton(new SqlFilter() {
            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlStatementBuilder sqlStatementBuilder = new SqlStatementBuilder();
                getSqlFragment(sqlStatementBuilder);
                return sqlStatementBuilder;
            }
        });
    }

    protected abstract void getSqlFragment(SqlBuilder sqlStatementBuilder);
    
    public static <T> UiFilter<T> forFragment(final SqlFragmentSource fragment) {
        return new SqlFragmentUiFilter<T>() {
            @Override
            protected void getSqlFragment(SqlBuilder sqlStatementBuilder) {
                sqlStatementBuilder.appendFragment(fragment);
            }
        };
    }
}
