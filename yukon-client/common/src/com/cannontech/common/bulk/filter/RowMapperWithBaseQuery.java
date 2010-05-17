package com.cannontech.common.bulk.filter;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.util.SqlFragmentSource;

public interface RowMapperWithBaseQuery<T> extends ParameterizedRowMapper<T>{
    public SqlFragmentSource getBaseQuery();
    public SqlFragmentSource getOrderBy();
    public SqlFragmentSource getGroupBy();
    public boolean needsWhere();
}
