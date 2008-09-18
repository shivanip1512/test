package com.cannontech.database;

import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public interface SqlProvidingRowMapper<E> extends SqlProvider,
        ParameterizedRowMapper<E> {

}
