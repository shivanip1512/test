package com.cannontech.database;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public interface FieldMapper<T> extends BaseFieldMapper<T> {
    public void extractValues(MapSqlParameterSource parameterHolder, T object);

}
