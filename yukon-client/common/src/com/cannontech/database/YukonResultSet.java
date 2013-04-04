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
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

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
    
    public Long getNullableLong(String columnLabel) throws SQLException {
        long retVal = rs.getLong(columnLabel);
        return rs.wasNull() ? null : retVal;
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

    private static LoadingCache<Class<Enum<?>>, Map<String, Enum<?>>> enumValueLookup = 
            CacheBuilder.newBuilder().concurrencyLevel(10).build(new CacheLoader<Class<Enum<?>>, Map<String, Enum<?>>>() {
                @Override
                public Map<String, Enum<?>> load(Class<Enum<?>> key) throws Exception {
                    Enum<?>[] enumConstants = key.getEnumConstants();
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
                            throw new UnsupportedOperationException("DatabaseRepresentationSource for " + key + " returned a non-Number, non-String for " + enum1);
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
            @SuppressWarnings("unchecked") // This cast is needed since the LoadingCache class uses k and not ? extends k.  When they fix that we can remove this suppress warning.
            Map<String, Enum<?>> map = enumValueLookup.getUnchecked((Class<Enum<?>>) enumClass);
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

    /**
     * Returns boolean value for columnLabel.
     * Column values are expected to be "Y" or "N" (as defined by YNBoolean.getDatabaseRepresentation()).
     * @param columnLabel
     * @return
     */
    public boolean getBooleanYN(String columnLabel) throws SQLException {
        return getEnum(columnLabel, YNBoolean.class).getBoolean();
    }
    
    public boolean wasNull() throws SQLException {
        return rs.wasNull();
    }
}