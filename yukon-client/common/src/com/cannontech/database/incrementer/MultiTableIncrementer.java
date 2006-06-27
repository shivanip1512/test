package com.cannontech.database.incrementer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.clientutils.CTILogger;

public class MultiTableIncrementer {
    private DataSource dataSource;
    private String sequenceTableName;
    private String valueColumnName;
    private String keyColumnName;
    private String sequenceKey;
    private String insertSql;
    private String valueSql;
    private String incrementSql;
    private boolean dirty = false;
    
    public MultiTableIncrementer(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    private void initializeSql() {
        if (dirty) {
            insertSql = "insert into " + sequenceTableName + " (" 
                + keyColumnName + ", " + valueColumnName + ") values " 
                + "(\'" + sequenceKey + "\', 0)";
            
            valueSql = "select " + valueColumnName + " from " + sequenceTableName
                + " where " + keyColumnName 
                + " = '" + sequenceKey + "'";
    
            incrementSql = "update " + sequenceTableName + " set " 
                + valueColumnName +  "= ? "
                + " where " + keyColumnName + " = '" + sequenceKey + "'"
                + " and " + valueColumnName +  " = ?";
            dirty = false;
        }
    }
    
    public int getNextValue() {
        initializeSql();
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Number result = (Number)jdbc.execute(new ConnectionCallback() {
            public Object doInConnection(Connection con) throws SQLException, DataAccessException {
                boolean previousAutoCommit = con.getAutoCommit();
                con.setAutoCommit(false);
                //int previousIsolationLevel = con.getTransactionIsolation();
                //con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                Statement statement = null;
                PreparedStatement pStatement = null;
                ResultSet resultSet = null;
                try {
                    statement = con.createStatement();
                    resultSet = statement.executeQuery(valueSql);
                    int  lastValue;
                    if (resultSet.next()) {
                        lastValue = resultSet.getInt(1);
                    } else {
                        statement.executeUpdate(insertSql);
                        lastValue = 0;
                    }
                    
                    // update the table
                    pStatement = con.prepareStatement(incrementSql);
                    pStatement.setInt(1, lastValue + 1);
                    pStatement.setInt(2, lastValue);
                    int affected = pStatement.executeUpdate();
                    if (affected != 1) {
                        throw new IncorrectUpdateSemanticsDataAccessException("More than one row affected :" + affected);
                    }
                    // get the current value
                    return lastValue + 1;
                } finally {
                    try {
                        resultSet.close();
                        statement.close();
                        pStatement.close();
                        con.commit();
                        //con.setTransactionIsolation(previousIsolationLevel);
                        con.setAutoCommit(previousAutoCommit);
                    } catch (Exception e) {
                        CTILogger.error("Exception in finally block", e);
                    }
                }
            }

        });
        return result.intValue();
    }

    public void initializeSequence(final String tableName, final String identityColumn) {
        initializeSql();
        final JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        TransactionTemplate tt = new TransactionTemplate(transactionManager);
        tt.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                // get current max
                String maxSql = "select max(" + identityColumn + ") from " + tableName;
                long currentMax = jdbc.queryForLong(maxSql);
                
                // make sure row exists
                String checkSql = "select " + keyColumnName + " from " + sequenceTableName + 
                                  " where " + keyColumnName + " = '" + sequenceKey + "'";
                List matches = jdbc.queryForList(checkSql, String.class);
                if (matches.isEmpty()) {
                    jdbc.execute(insertSql);
                }
                
                String updateSql = "update " + sequenceTableName + " set " 
                    + valueColumnName +  " = " + currentMax 
                    + " where " + keyColumnName + " = '" + sequenceKey + "'"
                    + " and " + currentMax + " > " + valueColumnName;
                jdbc.execute(updateSql);

                return null;
            }
        });
    }
    
    public String getKeyColumnName() {
        return keyColumnName;
    }

    public void setKeyColumnName(String keyColumnName) {
        this.keyColumnName = keyColumnName;
        dirty = true;
    }

    public String getValueColumnName() {
        return valueColumnName;
    }

    public void setValueColumnName(String valueColumnName) {
        this.valueColumnName = valueColumnName;
        dirty = true;
    }

    public String getSequenceTableName() {
        return sequenceTableName;
    }

    public void setSequenceTableName(String sequenceTableName) {
        this.sequenceTableName = sequenceTableName;
        dirty = true;
    }

    public String getSequenceKey() {
        return sequenceKey;
    }

    public void setSequenceKey(String sequenceKey) {
        this.sequenceKey = sequenceKey;
        dirty = true;
    }


}
