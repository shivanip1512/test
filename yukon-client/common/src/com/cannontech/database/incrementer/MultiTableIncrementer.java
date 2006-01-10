package com.cannontech.database.incrementer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

public class MultiTableIncrementer implements KeyedIncrementer {
    private DataSource dataSource;
    private String sequenceTableName;
    private String valueColumnName;
    private String keyColumnName;
    
    public MultiTableIncrementer(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public MultiTableIncrementer() {
    }

    public int getNextValue(final String key) {
        final String tableName = getSequenceTableName();
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Number result = (Number)jdbc.execute(new ConnectionCallback() {
            public Object doInConnection(Connection con) throws SQLException, DataAccessException {
                boolean previousAutoCommit = con.getAutoCommit();
                con.setAutoCommit(false);
                int previousIsolationLevel = con.getTransactionIsolation();
                con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                Statement incStatement = con.createStatement();
                Statement valueStatement = con.createStatement();
                try {
                    // update the table
                    String incrementSql = "update " + tableName + " set " 
                                         + valueColumnName +  "= (" + valueColumnName + " + 1)"
                                         + " where " + keyColumnName + " = '" + key + "'";
                    incStatement.executeUpdate(incrementSql);
                    
                    // get the current value
                    String valueSql = "select " + valueColumnName + " from " + tableName
                                       + " where " + keyColumnName 
                                       + " = '" + key + "'";
                    ResultSet resultSet = 
                        valueStatement.executeQuery(valueSql);
                    resultSet.next();
                    return resultSet.getLong(1);
                } finally {
                    incStatement.close();
                    valueStatement.close();
                    con.commit();
                    con.setTransactionIsolation(previousIsolationLevel);
                    con.setAutoCommit(previousAutoCommit);
                }
            }
        });
        return result.intValue();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getSequenceTableName() {
        return sequenceTableName;
    }

    public void setSequenceTableName(String sequenceTableName) {
        this.sequenceTableName = sequenceTableName;
    }

    public String getKeyColumnName() {
        return keyColumnName;
    }

    public void setKeyColumnName(String keyColumnName) {
        this.keyColumnName = keyColumnName;
    }

    public String getValueColumnName() {
        return valueColumnName;
    }

    public void setValueColumnName(String valueColumnName) {
        this.valueColumnName = valueColumnName;
    }

}
