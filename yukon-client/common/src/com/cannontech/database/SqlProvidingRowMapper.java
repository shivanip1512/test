package com.cannontech.database;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlProvider;

public interface SqlProvidingRowMapper<E> extends SqlProvider,
        RowMapper<E> {

}
