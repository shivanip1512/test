package com.cannontech.database;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.joda.time.Instant;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.ISOPeriodFormat;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.database.data.point.PointType;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.MapMaker;

public class YukonResultSet {
    private ResultSet rs;

    public YukonResultSet(ResultSet rs) {
        this.rs = rs;
    }
    
    public ResultSet getResultSet() {
        return rs;
    }
    
    public String getStringSafe(String columnLabel) throws SQLException {
        return SqlUtils.convertDbValueToString(rs, columnLabel);
    }

    public Array getArray(String columnLabel) throws SQLException {
        return rs.getArray(columnLabel);
    }

    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return rs.getAsciiStream(columnLabel);
    }

    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return rs.getBigDecimal(columnLabel);
    }

    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return rs.getBinaryStream(columnLabel);
    }

    public Blob getBlob(String columnLabel) throws SQLException {
        return rs.getBlob(columnLabel);
    }

    public byte getByte(String columnLabel) throws SQLException {
        return rs.getByte(columnLabel);
    }

    public byte[] getBytes(String columnLabel) throws SQLException {
        return rs.getBytes(columnLabel);
    }

    public double getDouble(String columnLabel) throws SQLException {
        return rs.getDouble(columnLabel);
    }

    public float getFloat(String columnLabel) throws SQLException {
        return rs.getFloat(columnLabel);
    }

    public int getInt(String columnLabel) throws SQLException {
        return rs.getInt(columnLabel);
    }

    public Integer getNullableInt(String columnLabel) throws SQLException {
        int retVal = rs.getInt(columnLabel);
        return rs.wasNull() ? null : retVal;
    }

    public Double getNullableDouble(String columnLabel) throws SQLException {
        double retVal = rs.getDouble(columnLabel);
        return rs.wasNull() ? null : retVal;
    }
    
    public long getLong(String columnLabel) throws SQLException {
        return rs.getLong(columnLabel);
    }

    public short getShort(String columnLabel) throws SQLException {
        return rs.getShort(columnLabel);
    }

    public String getString(String columnLabel) throws SQLException {
        return rs.getString(columnLabel);
    }

    public Date getDate(String columnLabel) throws SQLException {
        return rs.getTimestamp(columnLabel);
    }

    public PaoIdentifier getPaoIdentifier(String paObjectIdColumnLabel, String paoTypeColumnLabel)
            throws SQLException {
        int paoId = getInt(paObjectIdColumnLabel);
        PaoType paoType = getEnum(paoTypeColumnLabel, PaoType.class);
        return new PaoIdentifier(paoId, paoType);
    }
    
    public PointIdentifier getPointIdentifier(String pointTypeColumnLabel, String pointOffsetColumnLabel)
            throws SQLException {
        PointType pointType = getEnum(pointTypeColumnLabel, PointType.class);
        int offset = getInt(pointOffsetColumnLabel);
        return new PointIdentifier(pointType, offset);
    }

    public Instant getInstant(String columnLabel) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnLabel);
        if (timestamp == null) {
            return null;
        }
        return new Instant(timestamp);
    }

    public ReadablePeriod getPeriod(String columnLabel) throws SQLException {
        String periodStr = rs.getString(columnLabel);
        return ISOPeriodFormat.standard().parsePeriod(periodStr);
    }

    private static Map<Class<Enum<?>>, Map<String, Enum<?>>> enumValueLookup = 
        new MapMaker().concurrencyLevel(10).makeComputingMap(new Function<Class<Enum<?>>, Map<String, Enum<?>>>() {
            public Map<String, Enum<?>> apply(Class<Enum<?>> from) {
                Enum<?>[] enumConstants = from.getEnumConstants();
                Builder<String, Enum<?>> builder = ImmutableMap.builder();
                for (Enum<?> enum1 : enumConstants) {
                    DatabaseRepresentationSource drs = (DatabaseRepresentationSource) enum1;
                    Object databaseRepresentation = drs.getDatabaseRepresentation();
                    String stringRepresentation;
                    if (databaseRepresentation instanceof Number) {
                        long longValue = ((Number) databaseRepresentation).longValue();
                        stringRepresentation = Long.toString(longValue);
                    } else if (databaseRepresentation instanceof String){
                        stringRepresentation = databaseRepresentation.toString();
                    } else {
                        throw new UnsupportedOperationException("DatabaseRepresentationSource for " + from + " returned a non-Number, non-String for " + enum1);
                    }
                    builder.put(stringRepresentation, enum1);
                }
                return builder.build();
            }
    });
    
    /**
     * This method will attempt to return the Enum that may be stored in a column. There are two specific
     * cases that this method handles:
     * 
     * 1. An enum that does not implement the DatabaseRepresentationSource and is stored as 
     *    the value returned by its name() method.
     * 2. An enum that does implement the DatabaseRepresentationSource and is stored by the
     *     Integer, Long, or String value returned by its getDatabaseRepresentation() method.
     * 
     * @param columnLabel
     * @param enumClass
     * @return
     * @throws SQLException
     */
    public <E extends Enum<E>> E getEnum(String columnLabel, Class<E> enumClass) throws SQLException {
        String stringValue = getString(columnLabel);
        if (stringValue == null) {
            return null;
        }
        E result;
        if (DatabaseRepresentationSource.class.isAssignableFrom(enumClass)) {
            Map<String, Enum<?>> map = enumValueLookup.get(enumClass);
            Enum<?> enum1 = map.get(stringValue);
            result = enumClass.cast(enum1);
        } else {
            result = Enum.valueOf(enumClass, stringValue);
        }
        
        if (result == null) {
            throw new SQLException(stringValue + " is not a legal representation of " + enumClass);
        }
        
        return result;
    }

    public boolean wasNull() throws SQLException {
        return rs.wasNull();
    }
    
    
}
