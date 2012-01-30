package com.cannontech.common.pao.service.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.pao.annotation.YukonPaoPart;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.google.common.base.Function;

/**
 * This class is used to store the information about methods annotated with the 
 * {@link YukonPaoField} annotation.
 */
/*package*/ class PaoFieldMetaData {
    private static final Logger log = YukonLogManager.getLogger(PaoFieldMetaData.class);

    public static Function<PaoFieldMetaData, String> colNameOfField =
        new Function<PaoFieldMetaData, String>() {
            @Override
            public String apply(PaoFieldMetaData input) {
                return input.dbColumnName;
            }
        };

    /**
     * Stores the information about the annotated property of the Complete class (generally a 
     * getter method) and is used to retrieve the getter and setter methods for the retrieval 
     * and saving processes for a Complete class object.
     */
    private PropertyDescriptor propertyDescriptor;
    
    /**
     * Used to map a class's annotated property to a specific column name in the database. 
     * This value is retrieved from the <code>columnName</code> property of the 
     * {@link YukonPaoField} annotation. In many cases, it is omitted on the YukonPaoField 
     * annotation and the default value of <code>propertyDescriptor.getName()</code> is used,
     * but in special cases where the column name doesn't match what we refer to the field
     * as in the code, dbColumnName tells the {@link PaoPersistenceService} how to get the 
     * data from the Complete object to its rightful column in the database.
     */
    private String dbColumnName;
    
    /**
     * Specifies whether or not the annotated field corresponds to a class which is defined
     * as a {@link YukonPaoPart}.
     */
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
        return "PaoFieldMetaData [propertyDescriptor=" + propertyDescriptor + ", dbColumnName="
               + dbColumnName + ", isPaoPart=" + isPaoPart + "]";
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