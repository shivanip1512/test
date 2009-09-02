package com.cannontech.common.bulk.filter;

import com.cannontech.common.util.SqlFragmentSource;

public interface SqlFilter {
    public SqlFragmentSource getWhereClauseFragment();
}
