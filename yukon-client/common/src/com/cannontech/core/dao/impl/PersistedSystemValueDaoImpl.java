package com.cannontech.core.dao.impl;

import java.beans.PropertyEditor;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.database.YukonJdbcTemplate;

public class PersistedSystemValueDaoImpl implements PersistedSystemValueDao {
    private final Logger log = YukonLogManager.getLogger(PersistedSystemValueDaoImpl.class);

    private YukonJdbcTemplate yukonJdbcTemplate;
    
    public void setValue(PersistedSystemValueKey property, Object value) {
        PropertyEditor propertyEditor = property.getInputType().getPropertyEditor();
        propertyEditor.setValue(value);
        String asText = propertyEditor.getAsText();
        
        // try update
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("update PersistedSystemValue");
        sql.append("set value").eq(asText);
        sql.append("where Name").eq(property);
        int rows = yukonJdbcTemplate.update(sql);
        if (rows == 1) {
            return;
        }
        // try insert
        SqlStatementBuilder sql2 = new SqlStatementBuilder();
        sql2.append("insert into PersistedSystemValue");
        sql2.append("values (").appendArgument(property).append(",").appendArgument(asText).append(")");
        yukonJdbcTemplate.update(sql2);
    }
    
    @Override
    public boolean getBooleanValue(PersistedSystemValueKey property) {
        Boolean convertedValue = getConvertedValue(property, Boolean.class);
        return convertedValue.booleanValue();
    }

    @Override
    public double getDoubleValue(PersistedSystemValueKey property) {
        Number convertedValue = getConvertedValue(property, Number.class);
        return convertedValue.doubleValue();
    }

    @Override
    public float getFloatValue(PersistedSystemValueKey property) {
        Number convertedValue = getConvertedValue(property, Number.class);
        return convertedValue.floatValue();
    }

    @Override
    public int getIntegerValue(PersistedSystemValueKey property) {
        Number convertedValue = getConvertedValue(property, Number.class);
        return convertedValue.intValue();
    }

    @Override
    public long getLongValue(PersistedSystemValueKey property) {
        Number convertedValue = getConvertedValue(property, Number.class);
        return convertedValue.longValue();
    }

    @Override
    public String getStringValue(PersistedSystemValueKey property) {
        Object convertedValue = getConvertedValue(property, Object.class);
        return convertedValue.toString();
    }
    
    @Override
    public Instant getInstantValue(PersistedSystemValueKey property) {
    	Instant convertedValue = getConvertedValue(property, Instant.class);
        return convertedValue;
    }
    
    public <T> T getConvertedValue(PersistedSystemValueKey property, Class<T> returnType) throws BadSystemValueTypeException {
        if (log.isDebugEnabled()) {
            log.debug("getting converted value of " + property + " as " + returnType.getSimpleName());
        }
        Validate.isTrue(returnType.isAssignableFrom(property.getInputType().getTypeClass()), "can't convert " + property + " to " + returnType);
        String stringValue = getPropertyValue(property);
        Object convertedValue = convertPropertyValue(property, stringValue);
        if (convertedValue == null) {
            log.debug("convertedValue was null, using default");
            convertedValue = property.getDefaultValue();
        }
        if (log.isDebugEnabled()) {
            log.debug("returning: " + convertedValue);
        }
        T result = returnType.cast(convertedValue);
        return result;
    }
    
    public String getPropertyValue(PersistedSystemValueKey property) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select value"); 
        sql.append("from PersistedSystemValue");
        sql.append("where Name").eq(property);
        
        String result;
        try {
            result = yukonJdbcTemplate.queryForString(sql);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        
        return result;
    }
    
    public Object convertPropertyValue(PersistedSystemValueKey property, String value) throws BadSystemValueTypeException {
        try {
            return InputTypeFactory.convertPropertyValue(property.getInputType(), value);
        } catch (Exception e) {
            throw new BadSystemValueTypeException(property, value, e);
        }
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

}
