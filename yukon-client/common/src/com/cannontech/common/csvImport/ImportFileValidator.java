package com.cannontech.common.csvImport;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.DuplicateColumnNameException;
import com.cannontech.common.exception.InvalidColumnNameException;
import com.cannontech.common.exception.RequiredColumnMissingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Validates ImportData objects against ImportFileFormats.
 * 
 * There are two types of validation - file validation, and row validation. File validation checks
 * that the file structure is correct (appropriate columns). Row validation checks that individual
 * rows of data conform to the format. Rows may be validated individually (i.e. during import), or 
 * in bulk (i.e. before import).
 * 
 * Validation failures throw different exceptions, so they can each be caught and dealt with
 * individually. All of these errors inherit from ImportFileFormatException, so they can also be
 * dealt with in bulk.
 */
public class ImportFileValidator {
    private static Logger log = YukonLogManager.getLogger(ImportFileValidator.class);
    private ImportFileValidator(){} //static use only
    
    /**
     * Validates the file structure, but not individual data rows. Checks for the following:
     * 
     * Invalid columns (these are removed and do not throw an error if the format has
     *  ignoreInvalidHeaders == true),
     * Duplicate column names,
     * Missing required columns,
     * Missing grouped columns,
     */
    public static void validateFileStructure(ImportData data) throws DuplicateColumnNameException, 
                                                                        InvalidColumnNameException,
                                                                        RequiredColumnMissingException {
        validateNoInvalidColumns(data);
        checkForDuplicateColumns(data);
        validateRequiredColumns(data);
        validateOptionalGroupedColumns(data);
    }
    
    /**
     * Validates all rows in the data set. This is a convenience method, equivalent to calling the
     * validateRow method on each row in the data.
     */
    public static List<ImportValidationResult> validateRows(ImportData data) {
        ImportFileFormat format = data.getFormat();
        
        List<ImportValidationResult> results = Lists.newArrayList();
        for(ImportRow row : data.getRows()) {
            ImportValidationResult result = validateRow(row, format);
            results.add(result);
        }
        return results;
    }
    
    /**
     * Validates an individual row of data. Checks for the following:
     * 
     * Nulls in non-nullable columns,
     * Missing values in required columns,
     * Values present in some, but not all, of the columns in a group,
     * Missing values in value-dependent columns, where the depended-upon column contains a
     *  depended-upon value.
     */
    public static ImportValidationResult validateRow(ImportRow row, ImportFileFormat format) {
        Set<String> invalidNulls = validateNulls(row, format);
        if(!invalidNulls.isEmpty()) {
            return new ImportValidationResult(invalidNulls, ValidationResultType.INVALID_NULL);
        }
        
        Set<String> invalidValues = validateValueTypes(row, format);
        if(!invalidValues.isEmpty()) {
            return new ImportValidationResult(invalidValues, ValidationResultType.BAD_TYPE);
        }
        
        Set<String> invalidRequired = validateRequiredColumnValues(row, format);
        if(!invalidRequired.isEmpty()) {
            return new ImportValidationResult(invalidRequired, ValidationResultType.MISSING_REQUIRED);
        }
        
        Set<String> invalidOptionalGrouped = validateOptionalGroupedColumnValues(row, format);
        if(!invalidOptionalGrouped.isEmpty()) {
            return new ImportValidationResult(invalidOptionalGrouped, ValidationResultType.MISSING_GROUPED);
        }
        
        Set<String> invalidDependents = validateValueDependentColumnValues(row, format);
        if(!invalidDependents.isEmpty()) {
            return new ImportValidationResult(invalidDependents, ValidationResultType.MISSING_VALUE_DEPENDENT);
        }
        
        return new ImportValidationResult(null, ValidationResultType.VALID);
    }
    
    /*
     * Checks for duplicate column names. Works under the assumption that headers will be trimmed and 
     * upper-cased. Throws an DuplicateColumnNameException if any duplicate column names are found.
     */
    private static void checkForDuplicateColumns(ImportData data) throws DuplicateColumnNameException {
        List<String> checkedHeaders = Lists.newArrayList();
        Set<String> badHeaders = Sets.newHashSet();
        for(String header : data.getColumnNames()) {
            if(checkedHeaders.contains(header)) {
                badHeaders.add(header);
            } else {
                checkedHeaders.add(header);
            }
        }
        if(!badHeaders.isEmpty()) {
            throw new DuplicateColumnNameException(badHeaders);
        }
    }
    
    /*
     * Checks the import data for any headers that are invalid according to the specified format.
     * If the format is set to ignore invalid headers, they will be removed. Otherwise, an exception
     * is thrown, listing the invalid headers.
     */
    private static void validateNoInvalidColumns(ImportData data) throws InvalidColumnNameException {
        ImportFileFormat format = data.getFormat();
        Set<String> invalidHeaders = Sets.newHashSet();
        List<String> fileHeaders = data.getColumnNames();
                
        //find any column names in the data that don't match any columns in the format
        fileHeadersLoop:
        for(int i = 0; i < fileHeaders.size(); i++) {
            for(ImportColumnDefinition column : format.getColumns()) {
                //stop searching if we find a match in the format's columns
                if(column.getName().equalsIgnoreCase(fileHeaders.get(i))) {
                    continue fileHeadersLoop;
                }
            }
            
            //checked all format columns, match not found
            //if invalid headers are allowed, ignore them by removing column from the data
            //if not allowed, add them to the list for exception throwing
            if(format.isIgnoreInvalidHeaders()) {
                data.removeColumn(i);
                i--; //decrement i to account for removed column
            } else {
                invalidHeaders.add(fileHeaders.get(i));
            }
        }
        
        if(!invalidHeaders.isEmpty()) {
            throw new InvalidColumnNameException(invalidHeaders);
        }
    }
    
    //Checks that all required columns are present.
    private static void validateRequiredColumns(ImportData data) throws RequiredColumnMissingException {
        ImportFileFormat format = data.getFormat();
        List<String> headers = data.getColumnNames();
        Set<String> missingColumns = Sets.newHashSet();
        
        for(ImportColumnDefinition columnDefinition : format.getRequiredColumns()) {
            if(!headers.contains(columnDefinition.getName())) {
                missingColumns.add(columnDefinition.getName());
            }
        }
        
        if(!missingColumns.isEmpty()) {
            throw new RequiredColumnMissingException(missingColumns);
        }
    }
    
    //Checks that for column groups, either all columns in the group are present, or all are absent.
    private static void validateOptionalGroupedColumns(ImportData data) throws RequiredColumnMissingException {
        ImportFileFormat format = data.getFormat();
        Set<String> groups = Sets.newHashSet();
        Set<String> missingColumns = Sets.newHashSet();
        
        //find all groups in the format
        for(String columnName : format.getColumnGroupNames()) {
            if(data.getColumnNames().contains(columnName)) {
                groups.add(columnName);
            }
        }
        
        //for each group in the format, check that all or none of the columns exist
        Multimap<String, ImportColumnDefinition> groupedColumns = format.getGroupedColumns();
        for(String groupName : groups) {
            Collection<ImportColumnDefinition> columnsInGroup =  groupedColumns.get(groupName);
            
            for(ImportColumnDefinition column : columnsInGroup) {
                if(data.getColumnNames().contains(column.getName())) {
                    columnsInGroup.remove(column);
                }
            }
            if(!columnsInGroup.isEmpty()) {
                for(ImportColumnDefinition column : columnsInGroup) {
                    missingColumns.add(column.getName());
                }
            }
        }
        
        if(!missingColumns.isEmpty()) {
            throw new RequiredColumnMissingException(missingColumns);
        }
    }
    
    /*
     * Checks that any NULL values are in columns where they are permitted by the format.
     * Returns a list of column names where inappropriate nulls were found in the row. If none are
     * found, the returned list will have a size of 0.
     */
    private static Set<String> validateNulls(ImportRow row, ImportFileFormat format) {
        Set<String> invalidNullColumns = Sets.newHashSet();
        for(Entry<String, String> columnValueEntry : row.getValueMap().entrySet()) {
            String columnName = columnValueEntry.getKey();
            boolean isNullable = format.getColumnByName(columnName).isNullable();
            if(columnValueEntry.getValue().equalsIgnoreCase("NULL") && !isNullable){
                invalidNullColumns.add(columnName);
            }
        }
        return invalidNullColumns;
    }
    
    /*
     * Checks that all required columns are populated. Returns a list of column names where empty
     * required columns were found. If none were found, the list will have a size of 0.
     */
    private static Set<String> validateRequiredColumnValues(ImportRow row, ImportFileFormat format) {
        Set<String> emptyRequiredColumns = Sets.newHashSet();
        for(ImportColumnDefinition column : format.getColumns()) {
            if(column.isRequired()) {
                if(!row.hasValue(column.getName())) {
                    emptyRequiredColumns.add(column.getName());
                }
            }
        }
        return emptyRequiredColumns;
    }
    
    /*
     * Checks that grouped columns are appropriately populated. Returns a list of column names whose
     * values should have been populated but were not.
     */
    private static Set<String> validateOptionalGroupedColumnValues(ImportRow row, ImportFileFormat format) {
        Set<String> groups = Sets.newHashSet();
        Set<String> missingValues = Sets.newHashSet();
        
        //find all groups that have at least one column populated
        for(String columnName : format.getColumnGroupNames()) {
            if(row.hasValue(columnName)) {
                groups.add(columnName);
            }
        }
        
        //for each group, check that all the columns exist
        Multimap<String, ImportColumnDefinition> groupedColumns = format.getGroupedColumns();
        for(String groupName : groups) {
            Collection<ImportColumnDefinition> columnsInGroup =  groupedColumns.get(groupName);
            
            for(ImportColumnDefinition column : columnsInGroup) {
                if(row.hasValue(column.getName())) {
                    columnsInGroup.remove(column);
                }
            }
            if(!columnsInGroup.isEmpty()) {
                for(ImportColumnDefinition column : columnsInGroup) {
                    missingValues.add(column.getName());
                }
            }
        }
        
        return missingValues;
    }
    
    /*
     * Checks that value dependent columns contain a value if the depended-upon column contains a
     * depended-upon value.
     */
    private static Set<String> validateValueDependentColumnValues(ImportRow row, ImportFileFormat format) {
        Set<String> missingValues = Sets.newHashSet();
        
        Multimap<ImportColumnDefinition, ImportValueDependentColumnDefinition> dependentColumnsMap = format.getValueDependentColumns();
        Collection<ImportValueDependentColumnDefinition> dependentColumns = dependentColumnsMap.values();
        for(ImportValueDependentColumnDefinition dependentColumn : dependentColumns) {
            String dependedUponColumnName = dependentColumn.getDependedUponColumn().getName();
            Set<String> dependedUponValues = dependentColumn.getDependedUponValue();
            //if there is a value for the depended-upon column, and the value is one of the
            //depended-upon values, but there is no value in the dependent column, it's an error.
            if(row.hasValue(dependedUponColumnName) 
               && dependedUponValues.contains(row.getValue(dependedUponColumnName))
               && !row.hasValue(dependentColumn.getName())) {
                missingValues.add(dependentColumn.getName());
            }
        }
        return missingValues;
    }
    
    /*
     * Checks non-empty, non-NULL values to ensure that the value can be converted to the type
     * specified for this column by the format. Attempts to use valueOf(Object) and valueOf(String)
     * methods on the format-specified class. 
     * 
     * If an invocationTargetException is caught, this indicates that the method was successfully
     * called, but the value could not be converted. 
     * 
     * Any other exceptions indicate that the method  was not called successfully, and are ignored. 
     * They could be reflection issues, but they most likely indicate that the class specified in 
     * the format lacks a valueOf(...) method, in which case we cannot validate - it's up to the 
     * programmer to do so elsewhere.
     */
    private static Set<String> validateValueTypes(ImportRow row, ImportFileFormat format) {
        Set<String> invalidValues = Sets.newHashSet();
        for(ImportColumnDefinition column : format.getColumns()) {
            String columnName = column.getName();
            String value = row.getValue(columnName);
            
            if(row.hasValue(columnName) && !value.equals("")) {
                //first, try valueOf(String)
                try {
                    column.getType().getMethod("valueOf", String.class).invoke(null, value);
                    continue;
                } catch(InvocationTargetException ite) {
                    //method exists, but conversion fails
                    invalidValues.add(columnName);
                    continue;
                } catch (Exception e) {
                    //method doesn't exist
                }
                
                //next, try valueOf(Object)
                try {
                    column.getType().getMethod("valueOf", Object.class).invoke(null, value);
                } catch(InvocationTargetException ite) {
                    //method exists, but conversion fails
                    invalidValues.add(columnName);
                } catch (Exception e) {
                    //method doesn't exist
                    String message = "Unable to validate import, column: " + columnName 
                                     + ", value: " + value
                                     + ", class: " + column.getType().getName();
                    log.warn(message);
                }
            }
        }
        return invalidValues;
    }
}
