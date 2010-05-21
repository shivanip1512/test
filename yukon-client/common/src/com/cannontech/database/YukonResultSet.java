package com.cannontech.database;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public boolean getBoolean(String columnLabel) throws SQLException {
        return rs.getBoolean(columnLabel);
    }

    public byte getByte(String columnLabel) throws SQLException {
        return rs.getByte(columnLabel);
    }

    public byte[] getBytes(String columnLabel) throws SQLException {
        return rs.getBytes(columnLabel);
    }

//    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
//        return rs.getDate(columnLabel, cal);
//    }
//
//    public Date getDate(String columnLabel) throws SQLException {
//        return rs.getDate(columnLabel);
//    }

    public double getDouble(String columnLabel) throws SQLException {
        return rs.getDouble(columnLabel);
    }

    public float getFloat(String columnLabel) throws SQLException {
        return rs.getFloat(columnLabel);
    }

    public int getInt(String columnLabel) throws SQLException {
        return rs.getInt(columnLabel);
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

//    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
//        return rs.getTime(columnLabel, cal);
//    }
//
//    public Time getTime(String columnLabel) throws SQLException {
//        return rs.getTime(columnLabel);
//    }
//
//    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
//        return rs.getTimestamp(columnIndex, cal);
//    }
//
//    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
//        return rs.getTimestamp(columnLabel, cal);
//    }
//
//    public Timestamp getTimestamp(String columnLabel) throws SQLException {
//        return rs.getTimestamp(columnLabel);
//    }

    public boolean wasNull() throws SQLException {
        return rs.wasNull();
    }
    
    
}
