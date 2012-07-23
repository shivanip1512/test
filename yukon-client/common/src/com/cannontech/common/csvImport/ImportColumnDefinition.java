package com.cannontech.common.csvImport;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

/**
 * Base class for all import column definitions. The shared characteristics of all columns are:
 * 
 * Name: A case-insensitive string identifier.
 * Type Class: The type of values permitted in this column. The class passed in this parameter should
 *             have a static valueOf(String) method for validation purposes.
 * Nullable: Whether or not "NULL" values are permitted in this column. Values with the exact
 *           string "NULL" will be transformed to "" (empty string) when parsed into ImportData.
 * Required: Whether the column is required or optional.
 * 
 * New column definition types should extend this class. New column definitions will also need to
 * have validation added to ImportFileValidator.
 */
public abstract class ImportColumnDefinition {
    private final String name;
    private final Class<?> typeClass;
    private boolean nullable;
    private boolean required;
    
    private String descriptionKey = "";
    private String validValuesKey = null;
    
    protected ImportColumnDefinition(String name, Class<?> typeClass, boolean nullable, boolean required) {
        this.name = name.toUpperCase();
        this.typeClass = typeClass;
        this.nullable = nullable;
        this.required = required;
    }
    
    public String getName() {
        return this.name;
    }
    
    /**
     * Sets the type of values permitted in this column.
     */
    public Class<?> getType() {
        return typeClass;
    }

    /**
     * Sets whether the column permits null values.
     */
    public boolean isNullable() {
        return nullable;
    }
    
    /**
     * Sets whether the column is required or optional.
     */
    public boolean isRequired() {
        return required;
    }
    
    /**
     * Sets the description i18n key for the specified column.
     */
    public void setDescriptionKey(String key) {
        if(StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Cannot set empty description key for import column.");
        }
        this.descriptionKey = key;
    }
    
    /**
     * @return The i18n key describing this column, if one has been set, Otherwise, the empty String.
     */
    public String getDescriptionKey() {
        return descriptionKey;
    }
    
    /**
     * @return The i18n key describing what values are valid in this column.
     * If a key has been explicitly specified for this column (through the setValidValuesKey()
     * method), then that value is returned.
     * If a key has not been explicitly specified, the typeClass of the column is then checked
     * against the type descriptions map in ImportFileFormat. If a description is mapped to that
     * class, it is returned.
     * If neither of these attempts returns a value, then null is returned.
     */
    public String getValidValuesKey() {
        if(validValuesKey != null) {
            return validValuesKey;
        }
        return ImportFileFormat.getTypeDescriptionKeys().get(typeClass);
    }
    
    /**
     * Explicitly specify the i18n key for valid values on this column. This will be used instead of
     * the default ImportFileFormat class keys or enum list (even if this column's typeClass is an
     * enum).
     */
    public void setValidValuesKey(String validValuesKey) {
        if(StringUtils.isEmpty(validValuesKey)) {
            throw new IllegalArgumentException("Cannot set empty valid values key for import column.");
        }
        this.validValuesKey = validValuesKey;
    }
    
    /**
     * @return A list of valid value strings for the specified column. This method is specifically
     * for use with columns whose value types are enums. If the column's value type is not an enum,
     * a null list will be returned.
     */
    public List<String> getValidValuesList() {
        if(typeClass.isEnum()) {
            List<String> validValues = Lists.newArrayList();
            try {
                //attempt to invoke the .values() method on the column's type class
                //this should always work, since we already know it's an enum
                Object[] enumValues =  (Object[]) typeClass.getMethod("values", null).invoke(null, new Object[0]);
                for(Object enumValue : enumValues) {
                    validValues.add(enumValue.toString());
                }
            } catch(NoSuchMethodException nsme) {
                return null;
            } catch(InvocationTargetException ite) {
                return null;
            } catch(IllegalAccessException iae) {
                return null;
            }
            return validValues;
        }
        return null;
    }
    
    /**
     * @return A String containing a comma-separated list of valid value strings for the specified
     * column. This method returns the same results as getValidValuesList, formatted differently.
     */
    public String getValidValuesString() {
        List<String> validValuesList = getValidValuesList();
        return StringUtils.join(validValuesList, ", ");
    }
}
