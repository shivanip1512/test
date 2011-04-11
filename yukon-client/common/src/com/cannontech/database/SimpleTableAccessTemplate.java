package com.cannontech.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.util.IterableUtils;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterChildHelper.ChildPair;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Lists;

public final class SimpleTableAccessTemplate<T> {

    private String tableName;
    private final YukonJdbcTemplate yukonJdbcTemplate;
    private BaseFieldMapper<T> baseFieldMapper;
    private FieldMapper<T> fieldMapper;
    private AdvancedFieldMapper<T> advancedFieldMapper;
    private final NextValueHelper nextValueHelper;
    private String primaryKeyField;
    private PrimaryKeyChecker<T> primaryKeyNeeded;
    private CascadeMode cascadeMode = CascadeMode.NONE;
    private String parentForeignKeyField;
    
    {
        // setup default primary key checker
        primaryKeyNeeded = new PrimaryKeyChecker<T>() {
            @Override
            public boolean needsPrimaryKey(T value, BaseFieldMapper<T> baseFieldMapper) {
                return baseFieldMapper.getPrimaryKey(value) == null;
            }
        };
    }

    private class HolderOfParameters {
        MapSqlParameterSource parameterSource;
        SqlParameterChildHelper helper;
    }

    public static enum CascadeMode {
        DELETE_ALL_CHILDREN_BEFORE_UPDATE, NONE;
    }
    
    public static interface PrimaryKeyChecker<T> {
        public boolean needsPrimaryKey(T value, BaseFieldMapper<T> baseFieldMapper);
    }

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
        Validate.isTrue(advancedFieldMapper == null, "only one type of mapper may be set");
        this.fieldMapper = fieldMapper;
        this.baseFieldMapper = fieldMapper;
        return this;
    }
    
    public SimpleTableAccessTemplate<T> setPrimaryKeyField(String primaryKeyField) {
        this.primaryKeyField = primaryKeyField;
        return this;
    }
    
    public SimpleTableAccessTemplate<T> setPrimaryKeyValidOver(final int primaryKeyValidOver) {
        this.primaryKeyNeeded = new PrimaryKeyChecker<T>() {
            public boolean needsPrimaryKey(T value, BaseFieldMapper<T> baseFieldMapper) {
                if (baseFieldMapper.getPrimaryKey(value) == null) {
                    return true;
                } else {
                    return baseFieldMapper.getPrimaryKey(value).longValue() <= primaryKeyValidOver;
                }
                
            };
        };
        return this;
    }
    
    public SimpleTableAccessTemplate<T> setPrimaryKeyValidNotEqualTo(final int invalidPrimaryKey) {
        this.primaryKeyNeeded = new PrimaryKeyChecker<T>() {
            public boolean needsPrimaryKey(T value, BaseFieldMapper<T> baseFieldMapper) {
                if (baseFieldMapper.getPrimaryKey(value) == null) {
                    throw new IllegalStateException("found null when a non-null value was assumed");
                } else {
                    return baseFieldMapper.getPrimaryKey(value).longValue() == invalidPrimaryKey;
                }
                
            };
        };
        return this;
    }
    
    public void setParentForeignKeyField(String parentForeignKeyField, CascadeMode cascadeMode) {
        Validate.isTrue(cascadeMode != CascadeMode.NONE);
        this.parentForeignKeyField = parentForeignKeyField;
        this.cascadeMode = cascadeMode;
    }

    public void setAdvancedFieldMapper(AdvancedFieldMapper<T> advancedFieldMapper) {
        this.baseFieldMapper = advancedFieldMapper;
        this.advancedFieldMapper = advancedFieldMapper;
    }

    public HolderOfParameters getHolderOfParameters(T object, Number parentKeyFieldId) {
        final MapSqlParameterSource parameterSource;
        final SqlParameterChildHelper helper = new SqlParameterChildHelper();
        if (advancedFieldMapper != null) {
            advancedFieldMapper.extractValues(helper, object);
            parameterSource = helper.getMapSqlParameterSource();
        } else if (fieldMapper != null) {
            parameterSource = new MapSqlParameterSource();
            fieldMapper.extractValues(parameterSource, object);
        } else {
            throw new IllegalStateException();
        }

        if (parentForeignKeyField != null || parentKeyFieldId != null) {
            Validate.notNull(parentForeignKeyField);
            Validate.notNull(parentKeyFieldId);
            parameterSource.addValue(parentForeignKeyField, parentKeyFieldId);
        }

        HolderOfParameters result = new HolderOfParameters();
        result.parameterSource = parameterSource;
        result.helper = helper;

        return result;
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
        return this.primaryKeyNeeded.needsPrimaryKey(object, baseFieldMapper);
    }
    
    public final void insert(T object) {
        insert(object, null);
    }
    
    private final void insert(T object, Number parentKeyFieldId) {
        HolderOfParameters holder = getHolderOfParameters(object, parentKeyFieldId);
        final MapSqlParameterSource parameterSource = holder.parameterSource;
        final SqlParameterChildHelper helper = holder.helper;

        // validation to warn if the primary key field has been passed in as a normal field in the fieldmapper.  
        if(parameterSource.getValues().keySet().contains(this.primaryKeyField)) {
            throw new IllegalArgumentException("Primary key field \"" + this.primaryKeyField + "\" should not beincluded in the FieldMapper.");
        }
        
        int nextId;
        if (needsPrimaryKey(object)) {
            nextId = nextValueHelper.getNextValue(tableName);
        } else {
            nextId = baseFieldMapper.getPrimaryKey(object).intValue();
        }
        parameterSource.addValue(primaryKeyField, nextId);

        // generate SQL
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(tableName);
        sql.append(" (");
        // append all of the field names
        
        // create list to guarantee order is consistent
        Set<?> uglySet = parameterSource.getValues().keySet();
        final List<String> fieldNameList = Lists.newArrayList(IterableUtils.castIterable(uglySet, String.class));
        String fields = StringUtils.join(fieldNameList, ",");
        sql.append(fields);
        sql.append(") VALUES (");
        // append the correct number of ?
        String[] questionArray = new String[parameterSource.getValues().size()];
        Arrays.fill(questionArray, "?");
        String questions = StringUtils.join(questionArray, ",");
        sql.append(questions);
        sql.append(")");
        
        PreparedStatementSetter pss = createPreparedStatementSetter(parameterSource, fieldNameList);
        
        yukonJdbcTemplate.getJdbcOperations().update(sql.toString(), pss);
        baseFieldMapper.setPrimaryKey(object, nextId);
        
        if (helper != null) {
            // because the parent was an insert, insert the children
            List<ChildPair<?>> pairList = helper.getPairList();
            for (ChildPair<?> childPair : pairList) {
                if (childPair.template.cascadeMode == CascadeMode.DELETE_ALL_CHILDREN_BEFORE_UPDATE) {
                    insertChildPair(childPair, nextId);
                }
            }
        }
    }
    
    private <C> void insertChildPair(ChildPair<C> childPair, Number nextId) {
        List<? extends C> children = childPair.children;
        for (C child : children) {
            childPair.template.insert(child, nextId);
        }
    }

    public final void update(T object) {
        update(object, null);
    }
    
    public final void update(T object, Number parentKeyFieldId) {
        HolderOfParameters holder = getHolderOfParameters(object, parentKeyFieldId);
        final MapSqlParameterSource parameterSource = holder.parameterSource;
        final SqlParameterChildHelper helper = holder.helper;

        // generate SQL
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(tableName);
        sql.append(" SET ");
        // append all of the field names
       
        // create list to guarantee order is consistent
        Set<?> uglySet = parameterSource.getValues().keySet();
        final List<String> fieldNameList = Lists.newArrayList(IterableUtils.castIterable(uglySet, String.class));
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
        sql.append(" WHERE ");
        sql.append(primaryKeyField);
        sql.append(" = ?");
        
        // add the where clause parameter to the list
        Number primaryKeyId = baseFieldMapper.getPrimaryKey(object);
        Validate.isTrue(primaryKeyId != null, "pk on update is null");
        fieldNameList.add(primaryKeyField);
        parameterSource.addValue(primaryKeyField, primaryKeyId);
        
        // get property setter
        PreparedStatementSetter pss = createPreparedStatementSetter(parameterSource, fieldNameList);
        
        yukonJdbcTemplate.getJdbcOperations().update(sql.toString(), pss);
        
        if (helper != null) {
            // children handling
            List<ChildPair<?>> pairList = helper.getPairList();
            for (ChildPair<?> childPair : pairList) {
                if (childPair.template.cascadeMode == CascadeMode.DELETE_ALL_CHILDREN_BEFORE_UPDATE) {
                    SqlStatementBuilder deleteSql = new SqlStatementBuilder();
                    deleteSql.append("DELETE FROM").append(childPair.template.tableName);
                    deleteSql.append("WHERE").append(childPair.template.parentForeignKeyField).eq(primaryKeyId);
                    yukonJdbcTemplate.update(deleteSql);
                    insertChildPair(childPair, primaryKeyId);
                }
            }
        }
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
