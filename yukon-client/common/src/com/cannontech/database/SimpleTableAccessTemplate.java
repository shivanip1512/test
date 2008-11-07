package com.cannontech.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.database.incrementer.NextValueHelper;

public class SimpleTableAccessTemplate<T> {

    private String tableName;
    private final SimpleJdbcOperations jdbcTemplate;
    private FieldMapper<T> fieldMapper;
    private final NextValueHelper nextValueHelper;
    private String primaryKeyField;

    public SimpleTableAccessTemplate(final SimpleJdbcOperations jdbcTemplate, final NextValueHelper nextValueHelper) {
        this.jdbcTemplate = jdbcTemplate;
        this.nextValueHelper = nextValueHelper;
    }

    public SimpleTableAccessTemplate<T> withTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }
    
    public SimpleTableAccessTemplate<T> withFieldMapper(FieldMapper<T> fieldMapper) {
        this.fieldMapper = fieldMapper;
        return this;
    }
    
    public SimpleTableAccessTemplate<T> withPrimaryKeyField(String primaryKeyField) {
        this.primaryKeyField = primaryKeyField;
        return this;
    }
    
    public void save(T object) {
        if (fieldMapper.getPrimaryKey(object) == null) {
            insert(object);
        } else {
            update(object);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void insert(T object) {
        final MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        fieldMapper.extractValues(parameterSource, object);
        /*TODO: Add before this is committed -- we should have a validation here
         * to warn if the primary key field has been passed in as a normal field in
         * the fieldmapper.  
         */
        
        int nextId;
        if (fieldMapper.getPrimaryKey(object) == null) {
            nextId = nextValueHelper.getNextValue(tableName);
        } else {
            nextId = fieldMapper.getPrimaryKey(object).intValue();
        }
        parameterSource.addValue(primaryKeyField, nextId);

        // generate SQL
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ");
        sql.append(tableName);
        sql.append(" (");
        // append all of the field names
        
        // create list to guarantee order is consistent
        final List<String> fieldNameList = new ArrayList<String>(parameterSource.getValues().keySet());
        String fields = StringUtils.join(fieldNameList, ",");
        sql.append(fields);
        sql.append(") values (");
        // append the correct number of ?
        String[] questionArray = new String[parameterSource.getValues().size()];
        Arrays.fill(questionArray, "?");
        String questions = StringUtils.join(questionArray, ",");
        sql.append(questions);
        sql.append(")");
        
        PreparedStatementSetter pss = createPreparedStatementSetter(parameterSource, fieldNameList);
        
        jdbcTemplate.getJdbcOperations().update(sql.toString(), pss);
        fieldMapper.setPrimaryKey(object, nextId);
    }
    
    @SuppressWarnings("unchecked")
    public void update(T object) {
        final MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        fieldMapper.extractValues(parameterSource, object);
        
        // generate SQL
        StringBuilder sql = new StringBuilder();
        sql.append("update ");
        sql.append(tableName);
        sql.append(" set ");
        // append all of the field names
       
        // create list to guarantee order is consistent
        final List<String> fieldNameList = new ArrayList<String>(parameterSource.getValues().keySet());
        boolean first = true;
        for (String fieldName : fieldNameList) {
            if (!first) {
                sql.append(",");
            }
            first = false;
            sql.append(fieldName);
            sql.append(" = ?");
        }
        
        // where clause
        sql.append(" where ");
        sql.append(primaryKeyField);
        sql.append(" = ?");
        
        // add the where clause parameter to the list
        Number primaryKeyId = fieldMapper.getPrimaryKey(object);
        Validate.isTrue(primaryKeyId != null, "pk on update is null");
        fieldNameList.add(primaryKeyField);
        parameterSource.addValue(primaryKeyField, primaryKeyId);
        
        // get property setter
        PreparedStatementSetter pss = createPreparedStatementSetter(parameterSource, fieldNameList);
        
        jdbcTemplate.getJdbcOperations().update(sql.toString(), pss);
    }

    private PreparedStatementSetter createPreparedStatementSetter(final MapSqlParameterSource parameterSource, final List<String> fieldNameList) {
        PreparedStatementSetter pss = new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
                int position = 1;
                for (String fieldName : fieldNameList) {
                    Object value = parameterSource.getValue(fieldName);
                    int sqlType = parameterSource.getSqlType(fieldName);
                    StatementCreatorUtils.setParameterValue(ps, position++, sqlType, null, value);
                }
            }

        };
        return pss;
    }

}
