package com.cannontech.common.csvImport;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
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
public class ImportFileFormat {
    private Set<ImportColumnDefinition> columns = Sets.newHashSet();
    private Set<ImportColumnDefinition> requiredColumns = Sets.newHashSet();
    private Multimap<String, ImportColumnDefinition> groupedColumns = ArrayListMultimap.create();
    private Multimap<ImportColumnDefinition, ImportValueDependentColumnDefinition> valueDependentColumns = ArrayListMultimap.create();
    private boolean ignoreInvalidHeaders = false;
    
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
    
    public Set<ImportColumnDefinition> getColumns() {
        return Collections.unmodifiableSet(columns);
    }
    
    public Set<ImportColumnDefinition> getRequiredColumns() {
        return Collections.unmodifiableSet(requiredColumns);
    }
    
    public Multimap<String, ImportColumnDefinition> getGroupedColumns() {
        return ImmutableMultimap.copyOf(groupedColumns);
    }
    
    public Multimap<ImportColumnDefinition, ImportValueDependentColumnDefinition> getValueDependentColumns() {
        return ImmutableMultimap.copyOf(valueDependentColumns);
    }
    
    public void setIgnoreInvalidHeaders(boolean ignoreInvalidHeaders) {
        this.ignoreInvalidHeaders = ignoreInvalidHeaders;
    }
    
    public Set<String> getColumnGroupNames() {
        return Collections.unmodifiableSet(groupedColumns.keySet());
    }
    
    public boolean isIgnoreInvalidHeaders() {
        return ignoreInvalidHeaders;
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
