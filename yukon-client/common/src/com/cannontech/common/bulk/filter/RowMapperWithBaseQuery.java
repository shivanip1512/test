package com.cannontech.common.bulk.filter;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.database.YukonRowMapper;

public interface RowMapperWithBaseQuery<T> extends YukonRowMapper<T>{
    public SqlFragmentSource getBaseQuery();
    public SqlFragmentSource getOrderBy();
    public boolean needsWhere();
}
