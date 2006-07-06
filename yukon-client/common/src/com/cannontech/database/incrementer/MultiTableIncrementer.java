package com.cannontech.database.incrementer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
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
    private Exception initializationException = null;
    
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
                + valueColumnName +  "=" + valueColumnName + "+ ? "
                + " where " + keyColumnName + " = '" + sequenceKey + "'";
            dirty = false;
        }
    }
    
    public int getNextValue() {
        return getNextValue(1);
    }
    
    protected int getNextValue(int incrementBy) {
        if (initializationException != null) {
            throw new RuntimeException("Exception during initialization.", initializationException);
        }
        initializeSql();
        Connection con = null;
        Statement statement = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;
        boolean previousAutoCommit = false;
        try {
            con = dataSource.getConnection();
            previousAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            
            // update the table
            pStatement = con.prepareStatement(incrementSql);
            pStatement.setInt(1, incrementBy);
            int affected = pStatement.executeUpdate();
            if (affected != 1) {
                throw new IncorrectUpdateSemanticsDataAccessException("More than one row affected :" + affected);
            }

            statement = con.createStatement();
            resultSet = statement.executeQuery(valueSql);
            int  lastValue;
            if (resultSet.next()) {
                lastValue = resultSet.getInt(1);
            } else {
                statement.executeUpdate(insertSql);
                lastValue = 0;
            }
            
            // get the current value
            return lastValue;
        } catch (SQLException e){
            SQLErrorCodeSQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
            throw translator.translate("Unable to getNextValue", null, e);
        } finally {
            try {
                con.commit();
                resultSet.close();
                statement.close();
                pStatement.close();
                con.setAutoCommit(previousAutoCommit);
                con.close();
            } catch (Exception e) {
                CTILogger.error("Exception in finally block", e);
            }
        }

    }

    public void initializeSequence(final String tableName, final String identityColumn) {
        try {
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
                    String checkSql = "select " + keyColumnName + " from " + sequenceTableName
                        + " where " + keyColumnName + " = '" + sequenceKey + "'";
                    List matches = jdbc.queryForList(checkSql, String.class);
                    if (matches.isEmpty()) {
                        jdbc.execute(insertSql);
                    }

                    String updateSql = "update " + sequenceTableName + " set " + valueColumnName
                        + " = " + currentMax + " where " + keyColumnName + " = '" + sequenceKey
                        + "'" + " and " + currentMax + " > " + valueColumnName;
                    jdbc.execute(updateSql);

                    return null;
                }
            });
        } catch (Exception e) {
            initializationException = e;
            CTILogger.warn("Unable to initialize " + sequenceKey + " sequence: " + e.getMessage() + 
                           ". An exception will be thrown if this sequence is used.");
        }
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
