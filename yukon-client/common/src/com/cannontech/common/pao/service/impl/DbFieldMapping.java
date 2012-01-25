package com.cannontech.common.pao.service.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonResultSet;
import com.google.common.base.Function;

public class DbFieldMapping {
    private static final Logger log = YukonLogManager.getLogger(DbFieldMapping.class);
    
    public static Function<DbFieldMapping, String> colNameOfField =
        new Function<DbFieldMapping, String>() {
        @Override
        public String apply(DbFieldMapping input) {
            return input.dbColumnName;
        }
    };

    private PropertyDescriptor propertyDescriptor;
    private String dbColumnName;
    private boolean isPaoPart;

    void updateField(Object newInstance, YukonResultSet rs) throws SQLException {
        Method setter = propertyDescriptor.getWriteMethod();
        if (setter == null) {
            // There is a getter but no setter, this means we don't need to set (e.g. PaoType).
            return;
        }
        Class<?> parameterType = setter.getParameterTypes()[0];

        Object value = null;
        if (parameterType == String.class) {
            value = rs.getStringSafe(dbColumnName);
        } else if (parameterType == Double.class || parameterType == Double.TYPE) {
            value = rs.getDouble(dbColumnName);
        } else if (parameterType == Float.class || parameterType == Float.TYPE) {
            value = rs.getFloat(dbColumnName);
        } else if (parameterType == Integer.class || parameterType == Integer.TYPE) {
            value = rs.getInt(dbColumnName);
        } else if (parameterType == Long.class || parameterType == Long.TYPE) {
            value = rs.getLong(dbColumnName);
        } else if (parameterType == Boolean.class || parameterType == Boolean.TYPE) {
            value = rs.getEnum(dbColumnName, YNBoolean.class).getBoolean();
        } else if (Date.class.isAssignableFrom(parameterType)) {
            value = rs.getDate(dbColumnName);
        } else if (Instant.class.isAssignableFrom(parameterType)) {
            value = rs.getInstant(dbColumnName);
        } else if (Enum.class.isAssignableFrom(parameterType)) {
            value = rs.getEnum(dbColumnName, (Class<? extends Enum>) parameterType);
        } else {
            log.error("handling " + parameterType.getName() + " not implemented");
            throw new RuntimeException();
        }

        try {
            setter.invoke(newInstance, value);
        } catch (IllegalArgumentException e) {
            log.error("caught exception in updateField", e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            log.error("caught exception in updateField", e);
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            log.error("caught exception in updateField", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "DbFieldMapping [propertyDescriptor=" + propertyDescriptor + ", dbColumnName="
               + dbColumnName + "]";
    }

    public PropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor;
    }

    public void setPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
        this.propertyDescriptor = propertyDescriptor;
    }

    public String getDbColumnName() {
        return dbColumnName;
    }

    public void setDbColumnName(String dbColumnName) {
        this.dbColumnName = dbColumnName;
    }

    public boolean isPaoPart() {
        return isPaoPart;
    }

    public void setPaoPart(boolean isPaoPart) {
        this.isPaoPart = isPaoPart;
    }
}