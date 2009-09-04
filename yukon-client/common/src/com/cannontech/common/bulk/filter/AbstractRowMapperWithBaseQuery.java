package com.cannontech.common.bulk.filter;

import com.cannontech.common.util.SqlFragmentSource;

public abstract class AbstractRowMapperWithBaseQuery<T> implements
        RowMapperWithBaseQuery<T> {

    @Override
    public SqlFragmentSource getOrderBy() {
        return null;
    }

    @Override
    public boolean needsWhere() {
        return false;
    }
}
