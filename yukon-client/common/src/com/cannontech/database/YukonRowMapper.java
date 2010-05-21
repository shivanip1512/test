package com.cannontech.database;

import java.sql.SQLException;

public interface YukonRowMapper<T> {

    /**
     * Implementations should return the object representation of
     * the current row in the supplied {@link YukonResultSet}.
     * @see org.springframework.jdbc.core.RowMapper#mapRow
     */
    T mapRow(YukonResultSet rs) throws SQLException;

}
