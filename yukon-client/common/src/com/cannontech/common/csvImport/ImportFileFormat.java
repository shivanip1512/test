package com.cannontech.common.csvImport;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.csvImport.types.StrictBoolean;
import com.cannontech.common.util.PositiveDouble;
import com.cannontech.common.util.PositiveInteger;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Defines the format of an import file as a collection of ImportColumnDefinitions.
 * 
 * Once an ImportFileFormat has been created, it can be used in conjunction with 
 * ImportFileParser to obtain a collection of ImportData. The data can then be validated via
 * the ImportDataValidator.
 * 
 * @see ImportColumnDefinition 
 * @see CsvImportFileParser
 * @see ImportDataValidator
 */
public class ImportFileFormat implements Cloneable {
    private Set<ImportColumnDefinition> columns = Sets.newHashSet();
    private Set<ImportColumnDefinition> requiredColumns = Sets.newHashSet();
    private Set<ImportColumnDefinition> optionalColumns = Sets.newHashSet();
    private Multimap<String, ImportColumnDefinition> groupedColumns = ArrayListMultimap.create();
    private Multimap<ImportColumnDefinition, ImportValueDependentColumnDefinition> valueDependentColumns = ArrayListMultimap.create();
    private boolean ignoreInvalidHeaders = false;
    
    private static Map<Class<?>, String> typeDescriptionKeys = Maps.newHashMap();
    static {
        typeDescriptionKeys.put(Integer.class, "yukon.web.import.typeDescription.integer");
        typeDescriptionKeys.put(PositiveInteger.class, "yukon.web.import.typeDescription.positiveInteger");
        typeDescriptionKeys.put(Long.class, "yukon.web.import.typeDescription.integer");
        typeDescriptionKeys.put(Short.class, "yukon.web.import.typeDescription.short");
        typeDescriptionKeys.put(Byte.class, "yukon.web.import.typeDescription.byte");
        typeDescriptionKeys.put(Double.class, "yukon.web.import.typeDescription.float");
        typeDescriptionKeys.put(Float.class, "yukon.web.import.typeDescription.float");
        typeDescriptionKeys.put(PositiveDouble.class, "yukon.web.import.typeDescription.positiveDouble");
        typeDescriptionKeys.put(String.class, "yukon.web.import.typeDescription.string");
        typeDescriptionKeys.put(Character.class, "yukon.web.import.typeDescription.character");
        typeDescriptionKeys.put(Boolean.class, "yukon.web.import.typeDescription.boolean");
        typeDescriptionKeys.put(StrictBoolean.class, "yukon.web.import.typeDescription.boolean");
    }
    
    public static Map<Class<?>, String> getTypeDescriptionKeys() {
        return Collections.unmodifiableMap(typeDescriptionKeys);
    }
    
    /**
     * @return A deep copy of this ImportFileFormat.
     */
    public ImportFileFormat clone() {
        ImportFileFormat copy = new ImportFileFormat();
        copy.columns = Sets.newHashSet(columns);
        copy.requiredColumns = Sets.newHashSet(requiredColumns);
        copy.optionalColumns = Sets.newHashSet(optionalColumns);
        copy.groupedColumns = ArrayListMultimap.create(groupedColumns);
        copy.valueDependentColumns = ArrayListMultimap.create(valueDependentColumns);
        copy.ignoreInvalidHeaders = ignoreInvalidHeaders;
        return copy;
    }
    
    /**
     * Adds a required column definition to the format. Required columns must be present in a
     * file, and every row must have a value in the required column for it to conform to this 
     * format.
     * 
     * @param name The column name.
     * @param typeClass The type of values permitted in this column. This class should have a static
     *         valueOf(Object) or valueOf(String) method, for validation purposes.
     * @param nullable Whether or not "NULL" values are permitted in this column.
     * @see ImportRequiredColumnDefinition
     */
    public void addRequiredColumn(String name, Class<?> typeClass, boolean nullable) {
        ImportColumnDefinition column = new ImportRequiredColumnDefinition(name, typeClass, nullable);
        validate(column);
        columns.add(column);
        requiredColumns.add(column);
    }
    
    /**
     * Adds an optional column definition to the format. Optional columns need not be present in a 
     * file conforming to this format, and individual rows are permitted to not have a value in
     * the optional column if it is present.
     * 
     * @param name The column name.
     * @param typeClass The type of values permitted in this column. This class should have a static
     *         valueOf(Object) or valueOf(String) method, for validation purposes.
     * @param nullable Whether or not "NULL" values are permitted in this column.
     * @see ImportOptionalColumnDefinition
     */
    public void addOptionalColumn(String name, Class<?> typeClass, boolean nullable) {
        ImportColumnDefinition column = new ImportOptionalColumnDefinition(name, typeClass, nullable);
        validate(column);
        columns.add(column);
        optionalColumns.add(column);
    }
    
    /**
     * Adds an optional, grouped column to the format. Grouped columns are optional. However, 
     * grouped columns are "all-or-nothing": for a file to conform to the format, it must 
     * contain all the columns in a given group, or none of them. Likewise, individual rows must
     * contain values for all of the grouped columns or none.
     *     
     * @param name The column name.
     * @param typeClass The type of values permitted in this column. This class should have a static
     *         valueOf(Object) or valueOf(String) method, for validation purposes.
     * @param nullable Whether or not "NULL" values are permitted in this column.
     * @param groupName The name identifying all columns in this group.
     * @see ImportGroupedColumnDefinition
     */
    public void addOptionalGroupedColumn(String name, Class<?> typeClass, boolean nullable, String groupName) {
        ImportColumnDefinition column = new ImportGroupedColumnDefinition(name, typeClass, nullable, groupName);
        validate(column);
        columns.add(column);
        groupedColumns.put(groupName, column);
    }
    
    /**
     * Adds a value-dependent column to the format. Value-dependent columns are dependent on the
     * value of another column for their optionality. The column is required if any row contains
     * the specified dependentValue in the specified depended column. Otherwise it is optional.
     * 
     * For example, a value-dependent column that depends on "column X, value 10" must contain a value
     * for any rows in which column X contains the value 10 for the file to conform to this format.
     * If no rows contain the value 10 in column X, the value-dependent column may be omitted.
     * 
     * @param The column name.
     * @param typeClass The type of values permitted in this column. This class should have a static
     *         valueOf(Object) or valueOf(String) method, for validation purposes.
     * @param nullable Whether or not "NULL" values are permitted in this column.
     * @param dependedColumnName The name of the column this one depends on.
     * @param dependentValues The values in the depended upon column for which this column is required.
     * @see ImportValueDependentColumnDefinition
     */
    public void addValueDependentColumn(String name, Class<?> typeClass, boolean nullable, String dependedColumnName, Object... dependentValues) {
        List<String> dependentValueStrings = Lists.newArrayList();
        ImportColumnDefinition columnDependedUpon = getColumnByName(dependedColumnName);
        if(columnDependedUpon == null) {
            throw new IllegalArgumentException("Column has a nonexistent dependency.");
        } else {
            //Check that the dependentValue is a valid type for the columnDependedUpon
            for(Object dependentValue : dependentValues ) {
                if(!columnDependedUpon.getType().isInstance(dependentValue)) {
                    throw new IllegalArgumentException("Dependent value is not a valid type for column.");
                }
                dependentValueStrings.add(String.valueOf(dependentValue));
            }
        }
        
        ImportValueDependentColumnDefinition column = new ImportValueDependentColumnDefinition(name, typeClass, nullable, columnDependedUpon, dependentValueStrings);
        validate(column);
        
        columns.add(column);
        valueDependentColumns.put(columnDependedUpon, column);
    }    

    /**
     * @return The column definition with the specified name, if it exists. Otherwise null.
     */
    public ImportColumnDefinition getColumnByName(String name) {
        for(ImportColumnDefinition column : columns) {
            if(column.getName().equalsIgnoreCase(name)) {
                return column;
            }
        }
        return null;
    }
    
    /**
     * @return an immutable copy of the set of all column definitions
     */
    public Set<ImportColumnDefinition> getColumns() {
        return Collections.unmodifiableSet(columns);
    }
    
    /**
     * @return an immutable copy of the set of required column definitions
     */
    public Set<ImportColumnDefinition> getRequiredColumns() {
        return Collections.unmodifiableSet(requiredColumns);
    }
    
    /**
     * @return an immutable copy of the set of optional column definitions
     */
    public Set<ImportColumnDefinition> getOptionalColumns() {
        return Collections.unmodifiableSet(optionalColumns);
    }
    
    /**
     * @return an immutable copy of the multimap of group names to grouped column definitions
     */
    public Multimap<String, ImportColumnDefinition> getGroupedColumns() {
        return ImmutableMultimap.copyOf(groupedColumns);
    }
    
    /**
     * @return an immutable map of group names to grouped column definitions
     */
    public Map<String, Collection<ImportColumnDefinition>> getGroupedColumnsAsMap() {
        return getGroupedColumns().asMap();
    }
    
    /**
     * @return an immutable copy of the multimap of column definitions to value dependent column definitions
     */
    public Multimap<ImportColumnDefinition, ImportValueDependentColumnDefinition> getValueDependentColumns() {
        return ImmutableMultimap.copyOf(valueDependentColumns);
    }
    
    /**
     * @return an immutable map of the column definitions to value dependent column definitions
     */
    public Map<ImportColumnDefinition, Collection<ImportValueDependentColumnDefinition>> getValueDependentColumnsAsMap() {
        return getValueDependentColumns().asMap();
    }
    
    public void setIgnoreInvalidHeaders(boolean ignoreInvalidHeaders) {
        this.ignoreInvalidHeaders = ignoreInvalidHeaders;
    }
    
    public Set<String> getColumnGroupNames() {
        Set<String> groupedColumnNames = groupedColumns.keySet();
        Set<String> copy = Sets.newHashSetWithExpectedSize(groupedColumnNames.size());
        for(String name : groupedColumnNames) {
            copy.add(name);
        }
        
        return copy;
    }
    
    public boolean isIgnoreInvalidHeaders() {
        return ignoreInvalidHeaders;
    }
    
    /**
     * Sets the description i18n key for the specified column.
     */
    public void setDescriptionKey(String columnName, String key) {
        ImportColumnDefinition column = getColumnByName(columnName);
        if(column == null) {
            throw new IllegalArgumentException("Unable to set description. No column named \"" + columnName + "\" in this ImportFileFormat.");
        }
        column.setDescriptionKey(key);
    }
    
    /**
     * Sets the valid values i18n key for the specified column.
     */
    public void setValidValuesKey(String columnName, String key) {
        ImportColumnDefinition column = getColumnByName(columnName);
        if(column == null) {
            throw new IllegalArgumentException("Unable to set valid values. No column named \"" + columnName + "\" in this ImportFileFormat.");
        }
        column.setValidValuesKey(key);
    }
    
    //Ensures the column has a name and does not duplicate an existing column name in the format.
    private void validate(ImportColumnDefinition column) {
        if(StringUtils.isBlank(column.getName())) {
            throw new IllegalArgumentException("Name cannot be null, empty or only whitespace.");
        }
        if(getColumnByName(column.getName()) != null) {
            throw new IllegalArgumentException("Column names must be unique.");
        }
    }
    
    
}
