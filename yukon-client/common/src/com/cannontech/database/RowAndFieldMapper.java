package com.cannontech.database;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public interface RowAndFieldMapper<T> extends ParameterizedRowMapper<T>, FieldMapper<T> {

}
