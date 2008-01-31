package com.cannontech.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * Insert the type's description here.
 * Creation date: (4/3/2002 11:50:36 AM)
 * @author: 
 */
public final class SqlUtils {
    public static final int ORACLE_FLOAT_PRECISION = 126;	
    /**
     * SqlUtils constructor comment.
     */
    private SqlUtils() {
        super();
    }
    
    /**
     * 
     * @param args Parameter order should be java.sql.ResultSet, java.sql.Statement, java.sql.Connection.
     */
    public static final void close(final Object... args) {
        for (int x = 0; x < args.length; x++) {
            Object o = args[x];
            if (o != null) {
                if (o instanceof ResultSet) JdbcUtils.closeResultSet((ResultSet) o);
                if (o instanceof Statement) JdbcUtils.closeStatement((Statement) o);
                if (o instanceof Connection) JdbcUtils.closeConnection((Connection) o);
            }
        }
    }
    
    /**
     * @see #convertDbValueToString(String)
     * @param rs
     * @param columnNumber
     * @return
     * @throws SQLException
     */
    public static String convertDbValueToString(ResultSet rs, int columnNumber) throws SQLException {
        String string = rs.getString(columnNumber);
        return convertDbValueToString(string);
    }
    
    /**
     * @see #convertDbValueToString(String)
     * @param rs
     * @param columnName
     * @return
     * @throws SQLException
     */
    public static String convertDbValueToString(ResultSet rs, String columnName) throws SQLException {
        String string = rs.getString(columnName);
        return convertDbValueToString(string);
    }
    
    /**
     * This is a helper function for retrieving String values for the database
     * where the Oracle ""/null issue will be a problem. This function and the
     * convertStringToDbValue create a contract where "" Strings are stored as 
     * " ".
     * 
     * Oracle:
     *   With a nulls allowed column:
     *     When the column contains a null: null
     *     When the column contains an empty string (same as above): null *won't ever be inserted*
     *     When the column contains " ": ""
     *     When the column contains "   ": ""
     *     When the column contains "foobar": "foobar"
     *   With a nulls not allowed column:
     *     When the column contains a null: *illegal*
     *     When the column contains an empty string (same as above): *illegal*
     *     When the column contains " ": ""
     *     When the column contains "   ": ""
     *     When the column contains "foobar": "foobar"
     * MS SQL:
     *   With a nulls allowed column:
     *     When the column contains a null: null
     *     When the column contains an empty string: ""
     *     When the column contains " ": ""
     *     When the column contains "   ": ""
     *     When the column contains "foobar": "foobar"
     *   With a nulls not allowed column:
     *     When the column contains a null:  *illegal*
     *     When the column contains an empty string: ""  *won't ever be inserted*
     *     When the column contains " ": ""
     *     When the column contains "   ": ""
     *     When the column contains "foobar": "foobar"
     *     
     * As you can see, the only difference will be when storing an empty String. For that
     * reason, use the convertStringToDbValue function to write to the database and 
     * an empty string will never be inserted.
     * 
     * @param rs
     * @param columnNumber
     * @return
     * @throws SQLException
     */
    public static String convertDbValueToString(String value) {
        if (StringUtils.isWhitespace(value)) {
            return "";
        }
        return value;
    }
    
    public static String convertStringToDbValue(String value) {
        if ("".equals(value)) {
            return " "; // single space
        }
        return value;
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (4/3/2002 11:51:10 AM)
     * @return java.lang.String
     */
    public static String createSqlString( String preText, String[] constraintColumnNames, 
            String[] tableNames,String postText)
    {
        StringBuffer sql = new StringBuffer(preText);
        for( int i = 0; i < constraintColumnNames.length; i++ )
            sql.append( constraintColumnNames[i] + "," );
        sql.deleteCharAt( sql.length()-1); //delete the extra , on the end

        sql.append(" from ");

        for( int i = 0; i < tableNames.length; i++ )
            sql.append( tableNames[i] + "," );
        sql.deleteCharAt( sql.length()-1); //delete the extra , on the end


        if( postText != null )
            sql.append(postText);

        return sql.toString();
    }

    /*
     * Return an Object[][] of rows and columns from sqlString('s) query results.
     * This method takes special care of the BigDecimal problem with Oracle/SQLServer
     */
    public static Object[][] getResultObjects(String sqlString, String dbAlias ) throws SQLException
    {
        Statement stmt = null;
        ResultSet rset = null;
        Connection conn = null;
        Object returnObjects[][] = null;
        try
        {
            conn = PoolManager.getInstance().getConnection(dbAlias);
            stmt = conn.createStatement();
            rset = stmt.executeQuery(sqlString);

            //Get all the rows
            Vector rows = new Vector();
            while (rset.next())
            {
                Vector columns = new Vector();
                for (int i = 1; i <= rset.getMetaData().getColumnCount(); i++)
                    columns.addElement( rset.getObject(i) );

                rows.addElement(columns);
            }
            returnObjects = new Object[rows.size()][rset.getMetaData().getColumnCount()];

            for (int i = 0; i < rows.size(); i++)
            {
                for (int j = 0; j < rset.getMetaData().getColumnCount(); j++)
                {
                    Object temp = ((java.util.Vector) rows.elementAt(i)).elementAt(j);
                    if (temp instanceof BigDecimal)
                    {
                        // >>>>>>>>>> Watch this - synchronize with above!
                        if (rset.getMetaData().getPrecision(j + 1) == ORACLE_FLOAT_PRECISION )
                            temp = new Double(((BigDecimal) temp).doubleValue());
                        else
                            temp = new Integer(((BigDecimal) temp).intValue());
                    }				

                    returnObjects[i][j] = temp;
                }
            }
        }
        catch (SQLException e)
        {
            throw e;
        }
        finally
        {
            if( rset != null ) rset.close();
            if( stmt != null ) stmt.close();
            if( conn != null ) conn.close();
        }
        return returnObjects;
    }
}
