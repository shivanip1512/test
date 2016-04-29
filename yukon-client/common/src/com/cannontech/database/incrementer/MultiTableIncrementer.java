package com.cannontech.database.incrementer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sql.DataSource;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.SqlUtils;

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
    // Store identity Column only in the case of exception and remove after successful initiation in second attempt
    private ConcurrentMap<String, String> tableIdentityColumnInfo = new ConcurrentHashMap<>();

    public MultiTableIncrementer(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    private void initializeSql() {
        if (dirty) {
            insertSql = "insert into " + sequenceTableName + " (" 
                + keyColumnName + ", " + valueColumnName + ") values " 
                + "(\'" + sequenceKey + "\', ?)";
            
            valueSql = "select " + valueColumnName + " from " + sequenceTableName
                + " where " + keyColumnName 
                + " = '" + sequenceKey + "'";
    
            incrementSql = "update " + sequenceTableName + " set " 
                + valueColumnName +  "=" + valueColumnName + "+ ? "
                + " where " + keyColumnName + " = '" + sequenceKey + "'";
            dirty = false;
        }
    }
    
    public int getNextValue(String tableName) {
        return getNextValue(1, tableName);
    }
    
    protected int getNextValue(int incrementBy, String tableName) {
        String identityColumn = tableIdentityColumnInfo.get(tableName);

        if (identityColumn != null) {
            initializationException = null;
            initializeSequence(tableName, identityColumn);
            if (initializationException != null) {
                throw new RuntimeException("Exception during initialization.", initializationException);
            }
            tableIdentityColumnInfo.remove(tableName);
        }
        initializeSql();
        Connection con = null;
        Statement vStatement = null;
        PreparedStatement iStatement = null;
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
            if (affected == 0) {
                iStatement = con.prepareStatement(insertSql);
                iStatement.setInt(1, incrementBy);
                iStatement.executeUpdate();
                return incrementBy;
            } else if (affected != 1) {
                throw new IncorrectUpdateSemanticsDataAccessException("More than one row affected :" + affected);
            }

            vStatement = con.createStatement();
            resultSet = vStatement.executeQuery(valueSql);
            if (resultSet.next()) {
                int  lastValue = resultSet.getInt(1);
                return lastValue;
            }
            
            throw new DataRetrievalFailureException("Select after update for next value returned nothing");
        } catch (SQLException e){
            SQLErrorCodeSQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
            throw translator.translate("Unable to getNextValue", null, e);
        } finally {
            try {
                con.commit();
                con.setAutoCommit(previousAutoCommit);
            } catch (Exception e) {
                CTILogger.error("Exception in finally block", e);
            } finally{
            	SqlUtils.close(resultSet, vStatement, pStatement, con);
            }
        }

    }

    public void initializeSequence(final String tableName, final String identityColumn) {
        try {
            initializeSql();
            final JdbcTemplate jdbc = new JdbcTemplate(dataSource);
            PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
            TransactionTemplate tt = new TransactionTemplate(transactionManager);
            tt.execute(new TransactionCallback<Object>() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    // get current max
                    String maxSql = "select max(" + identityColumn + ") from " + tableName;
                    Long tableMax = jdbc.queryForObject(maxSql, Long.class);
                    long tableMaxValue  =  tableMax != null ? tableMax.longValue() : 0;
                    long currentMax = Math.max(tableMaxValue, 1);   //use 1 if the current max value is negative.

                    // make sure row exists
                    String checkSql = "select " + keyColumnName + " from " + sequenceTableName
                        + " where " + keyColumnName + " = '" + sequenceKey + "'";
                    List<String> matches = jdbc.queryForList(checkSql, String.class);
                    if (matches.isEmpty()) {
                        try {
                            jdbc.update(insertSql, 1);
                        } catch (DataIntegrityViolationException e) {
                            //ignore and proceed with the update attempt that follows
                        }
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
            tableIdentityColumnInfo.put(tableName, identityColumn);
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
