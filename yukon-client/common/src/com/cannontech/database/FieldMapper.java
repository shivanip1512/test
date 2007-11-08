package com.cannontech.database;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public interface FieldMapper<T> {
    public void extractValues(MapSqlParameterSource parameterHolder, T object);

    public Number getPrimaryKey(T object);
    public void setPrimaryKey(T object, int value);
}
