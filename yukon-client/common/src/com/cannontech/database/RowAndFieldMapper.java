package com.cannontech.database;

import org.springframework.jdbc.core.RowMapper;

public interface RowAndFieldMapper<T> extends RowMapper<T>, FieldMapper<T> {

}
