package com.cannontech.database;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.joda.time.Instant;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;

public interface TypeRowMapper {
    
    /* NUMBERS */
    
    public final static YukonRowMapper<Integer> INTEGER = new YukonRowMapper<Integer>() {
        @Override
        public Integer mapRow(YukonResultSet rs) throws SQLException {
            return rs.getResultSet().getInt(1);
        }
    };
    
    public final static YukonRowMapper<Integer> INTEGER_NULLABLE = new YukonRowMapper<Integer>() {
        @Override
        public Integer mapRow(YukonResultSet rs) throws SQLException {
            int retVal = rs.getResultSet().getInt(1);
            return rs.wasNull() ? null : retVal;
        }
    };
    
    public final static YukonRowMapper<Double> DOUBLE = new YukonRowMapper<Double>() {
        @Override
        public Double mapRow(YukonResultSet rs) throws SQLException {
            return rs.getResultSet().getDouble(1);
        }
    };
    
    public final static YukonRowMapper<Double> DOUBLE_NULLABLE = new YukonRowMapper<Double>() {
        @Override
        public Double mapRow(YukonResultSet rs) throws SQLException {
            double retVal = rs.getResultSet().getDouble(1);
            return rs.wasNull() ? null : retVal;
        }
    };
    
    public final static YukonRowMapper<Long> LONG = new YukonRowMapper<Long>() {
        @Override
        public Long mapRow(YukonResultSet rs) throws SQLException {
            return rs.getResultSet().getLong(1);
        }
    };
    
    public final static YukonRowMapper<Long> LONG_NULLABLE = new YukonRowMapper<Long>() {
        @Override
        public Long mapRow(YukonResultSet rs) throws SQLException {
            long retVal = rs.getResultSet().getLong(1);
            return rs.wasNull() ? null : retVal;
        }
    };
    
    /* DATES & TIMES */
    
    public final static YukonRowMapper<Instant> INSTANT = new YukonRowMapper<Instant>() {
        @Override
        public Instant mapRow(YukonResultSet rs) throws SQLException {
            Timestamp timestamp = rs.getResultSet().getTimestamp(1);
            if (timestamp == null) {
                throw new EmptyResultDataAccessException("Null timestamp", 1);
            }
            return new Instant(timestamp);
        }
    };
    
    public final static YukonRowMapper<Instant> INSTANT_NULLABLE = new YukonRowMapper<Instant>() {
        @Override
        public Instant mapRow(YukonResultSet rs) throws SQLException {
            Timestamp timestamp = rs.getResultSet().getTimestamp(1);
            if (timestamp == null) {
                return null;
            }
            return new Instant(timestamp);
        }
    };
    
    /* OTHER OBJECTS */
    
    public final static YukonRowMapper<Boolean> BOOLEAN = new YukonRowMapper<Boolean>() {
        @Override
        public Boolean mapRow(YukonResultSet rs) throws SQLException {
            return rs.getResultSet().getBoolean(1);
        }
    };
    
    public final static YukonRowMapper<String> STRING = new YukonRowMapper<String>() {
        @Override
        public String mapRow(YukonResultSet rs) throws SQLException {
            return rs.getResultSet().getString(1);
        }
    };

    public final static YukonRowMapper<PaoType> PAO_TYPE = new YukonRowMapper<PaoType>() {
        @Override
        public PaoType mapRow(YukonResultSet rs) throws SQLException {
            return rs.getEnum("type", PaoType.class);
        }
    };

    public final static YukonRowMapper<PaoIdentifier> PAO_IDENTIFIER = new YukonRowMapper<PaoIdentifier>() {
        @Override
        public PaoIdentifier mapRow(YukonResultSet rs) throws SQLException {
            return rs.getPaoIdentifier("paobjectId", "type");
        }
    };
}
