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

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.incrementer.NextValueHelper;

public final class SimpleTableAccessTemplate<T> {

    private String tableName;
    private final YukonJdbcTemplate yukonJdbcTemplate;
    private FieldMapper<T> fieldMapper;
    private final NextValueHelper nextValueHelper;
    private String primaryKeyField;
    private Integer primaryKeyValidOver = null;

    public SimpleTableAccessTemplate(final YukonJdbcTemplate yukonJdbcTemplate, 
                                      final NextValueHelper nextValueHelper) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
        this.nextValueHelper = nextValueHelper;
    }

    public SimpleTableAccessTemplate<T> setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }
    
    public SimpleTableAccessTemplate<T> setFieldMapper(FieldMapper<T> fieldMapper) {
        this.fieldMapper = fieldMapper;
        return this;
    }
    
    public SimpleTableAccessTemplate<T> setPrimaryKeyField(String primaryKeyField) {
        this.primaryKeyField = primaryKeyField;
        return this;
    }
    
    public SimpleTableAccessTemplate<T> setPrimaryKeyValidOver(int i) {
        this.primaryKeyValidOver  = i;
        return this;
    }
    
    public final void save(T object) {
        if (needsPrimaryKey(object)) {
            insert(object);
        } else {
            update(object);
        }
    }
    
    public final boolean saveWillUpdate(T object) {
        return !needsPrimaryKey(object);
    }

    protected boolean needsPrimaryKey(T object) {
        if (primaryKeyValidOver == null) {
            return fieldMapper.getPrimaryKey(object) == null;
        } else {
            if (fieldMapper.getPrimaryKey(object) == null) {
                return true;
            } else {
                return fieldMapper.getPrimaryKey(object).longValue() <= primaryKeyValidOver;
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public final void insert(T object) {
        final MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        fieldMapper.extractValues(parameterSource, object);

        // validation to warn if the primary key field has been passed in as a normal field in the fieldmapper.  
        if(parameterSource.getValues().keySet().contains(this.primaryKeyField)) {
            throw new IllegalArgumentException("Primary key field \"" + this.primaryKeyField + "\" should not beincluded in the FieldMapper.");
        }
        
        int nextId;
        if (needsPrimaryKey(object)) {
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
        
        yukonJdbcTemplate.getJdbcOperations().update(sql.toString(), pss);
        fieldMapper.setPrimaryKey(object, nextId);
    }
    
    @SuppressWarnings("unchecked")
    public final void update(T object) {
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
        
        yukonJdbcTemplate.getJdbcOperations().update(sql.toString(), pss);
    }

    private PreparedStatementSetter createPreparedStatementSetter(final MapSqlParameterSource parameterSource, 
                                                                   final List<String> fieldNameList) {
        PreparedStatementSetter pss = new PreparedStatementSetter() {
            
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                int position = 1;
                for (String fieldName : fieldNameList) {
                    Object value = parameterSource.getValue(fieldName);
                    Object jdbcValue = SqlStatementBuilder.convertArgumentToJdbcObject(value);
                    int sqlType = parameterSource.getSqlType(fieldName);
                    StatementCreatorUtils.setParameterValue(ps, position++, sqlType, null, jdbcValue);
                }
            }

        };
        return pss;
    }

}
