package com.cannontech.database;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.joda.time.Instant;

/**
 * @deprecated Use {@link RowMapper#INSTANT}
 */
@Deprecated
public final class InstantRowMapper implements YukonRowMapper<Instant> {

    @Override
    public final Instant mapRow(YukonResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getResultSet().getTimestamp(1);
        if (timestamp == null) {
            return null;
        }
        return new Instant(timestamp);
    }
    
}