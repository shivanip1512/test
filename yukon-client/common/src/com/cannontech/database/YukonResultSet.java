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

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.ISOPeriodFormat;
import java.sql.ResultSetMetaData;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.database.data.point.PointType;
import com.cannontech.web.input.type.EnumInputType;
import com.cannontech.web.input.type.InputType;
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

    public Object getObjectOfInputType(String columnLabel, InputType<?> type) throws SQLException {
    	Class<?> valueType = type.getTypeClass();

    	String valueStr = getString(columnLabel);
    	if (StringUtils.isBlank(valueStr)) {
    	    return null;
    	}
    	if(valueType == Boolean.class) {
        	return getBoolean(columnLabel);
        } else if(valueType == String.class) {
        	return valueStr;
        } else if(valueType == Integer.class) {
        	return getInt(columnLabel);
        } else if (valueType == Double.class) {
        	return getDouble(columnLabel);
        } else if (valueType.isEnum()) {
        	EnumInputType<?> inputType = (EnumInputType<?>) type;
        	return getEnum(columnLabel, inputType.getTypeClass());
        } 
    	throw new UnsupportedOperationException("Unsupported InputType. This InputType will need to be added to this method");
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
    
    /**
     * Returns a {@link PaoIdentifier} using 'PAObjectId' as the pao id column name and 
     * 'Type' as the pao type column name.  See {#getPaoIdentifier(String paObjectIdColumnLabel, String paoTypeColumnLabel)}
     * if you need to use different column names.
     */
    public PaoIdentifier getPaoIdentifier() throws SQLException {
        
        int paoId = getInt("PAObjectId");
        PaoType paoType = getEnum("Type", PaoType.class);
        
        return new PaoIdentifier(paoId, paoType);
    }
    
    /**
     * Returns a {@link RfnIdentifier} using 'SerialNumber', 'Manufacturer' and 'Model' as the column names.
     */
    public RfnIdentifier getRfnIdentifier() throws SQLException {
        return new RfnIdentifier(getStringSafe("SerialNumber"), 
                                 getStringSafe("Manufacturer"), 
                                 getStringSafe("Model"));
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
	 *
	 * Deprecated in favor of {@link #getBoolean()} which supports Y/N
     */
    @Deprecated
    public boolean getBooleanYN(String columnLabel) throws SQLException {
        return getEnum(columnLabel, YNBoolean.class).getBoolean();
    }
    
    /**
     * Returns boolean value for columnLabel. Returns null if value is SQL NULL or empty string.
     * Column true values are expected to be "Y", "true", or 1. False values are expected to be "N", "false", or 0.
     */
    public Boolean getNullableBoolean(String columnLabel) throws SQLException {
    	CompatibleBoolean booleanValue = CompatibleBoolean.valueOfStr(getString(columnLabel));
        return booleanValue.getBoolean();
    }

    /**
     * Returns boolean value for columnLabel. Returns false if value is SQL NULL or empty string.
     * Column true values are expected to be "Y", "true", or 1. False values are expected to be "N", "false", or 0.
     */
    public boolean getBoolean(String columnLabel) throws SQLException {
    	CompatibleBoolean booleanValue = CompatibleBoolean.valueOfStr(getString(columnLabel));
    	return booleanValue == CompatibleBoolean.TRUE;
    }

    public boolean wasNull() throws SQLException {
        return rs.wasNull();
    }
    
    public boolean hasColumn(String columnName) throws SQLException {
        ResultSetMetaData metadata = rs.getMetaData();
        int columns = metadata.getColumnCount();
        for (int index = 1; index <= columns; index++) {
            if (columnName.equalsIgnoreCase(metadata.getColumnName(index))) {
                return true;
            }
        }
        return false;
    }
}