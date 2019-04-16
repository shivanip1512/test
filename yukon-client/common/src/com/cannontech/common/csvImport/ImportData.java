package com.cannontech.common.csvImport;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

/**
 * Object representing all the data from an import. 
 * 
 * Column names are stored separately from the data rows, and are automatically uppercased and 
 * trimmed of leading and trailing whitespace. Data is stored and retrieved as rows, but actions
 * can also be taken on columns.
 * 
 * Additionally, the data object stores its expected format, which can be validated against.
 */
public class ImportData {
    private ImportFileFormat format;
    private List<String[]> originalData;
    private List<String> columnNames = Lists.newArrayList();
    private List<ImportRow> rows = Lists.newArrayList();
    private String importType;
    private String originalFileName;
    
    public ImportData(List<String[]> stringData, ImportFileFormat format) {
        List<String[]> stringDataCopy = Lists.newArrayList(stringData);
        originalData = Lists.newArrayList(stringDataCopy);
        this.format = format;
        
        columnNames = Lists.newArrayList();
        for(String header : stringDataCopy.get(0)) {
            columnNames.add(header.trim().toUpperCase());
        }
        stringDataCopy.remove(0);
        
        for(String[] stringRow : stringDataCopy) {
            if(isRowBlank(stringRow)) continue; //ignore blank lines
            ImportRow newImportRow = new ImportRow(format, columnNames, stringRow);
            rows.add(newImportRow);
        }
    }
    
    public ImportFileFormat getFormat() {
        return format;
    }
    
    /**
     * @return A list of column names, ordered as they were in the import file. Column names are 
     * automatically made upper-case and trimmed of leading and trailing whitespace, regardless of 
     * their original formatting.
     */
    public List<String> getColumnNames() {
        return columnNames;
    }
    
    public List<ImportRow> getRows() {
        return rows;
    }
    
    /**
     * @return An Iterator for the ImportRows contained in this ImportData object. The header
     * row is not included.
     */
    public Iterator<ImportRow> iterator() {
        return rows.iterator();
    }
    
    /**
     * @return The number of rows in this data set (not including the headers row).
     */
    public int size() {
        return rows.size();
    }
    
    /**
     * @return The original Strings this data object was built from. This includes column headers
     * and remains identical to the contents of the original file, regardless of any operations
     * performed on the ImportData object.
     */
    public List<String[]> getOriginalData() {
        return originalData;
    }
    
    /**
     * @return The original Strings this data object was built from, in the form of a List of String
     * Lists. This includes column headers and remains identical to the contents of the original 
     * file, regardless of any operations performed on the ImportData object.
     */
    public List<List<String>> getOriginalDataAsLists() {
        List<List<String>> returnList = Lists.newArrayList();
        for(String[] line : originalData) {
            returnList.add(Lists.newArrayList(line));
        }
        return returnList;
    }
    
    /**
     * @return The value string from the specified column and row.
     */
    public String getValue(String columnName, int rowNum) {
        return rows.get(rowNum).getValue(columnName);
    }
    
    /**
     * @return True if a value is present in the specified column and row, otherwise false. NULL
     * is considered a value, but "" (empty string) is not.
     */
    public boolean hasValue(String columnName, int rowNum) {
        return rows.get(rowNum).hasValue(columnName);
    }
    
    /**
     * Completely removes a column based on its name. This includes removing the name from the list
     * of column names, as well as removing the values in that column from all data rows.
     * 
     * @return True if the column was successfully removed, or false if an invalid column name was
     * supplied.
     */
    public boolean removeColumn(String columnName) {
        Integer columnIndex = getColumnIndex(columnName);
        return removeColumn(columnName, columnIndex);
    }
    
    /**
     * Completely removes a column based on its index. This includes removing the name from the list
     * of column names, as well as removing the values in that column from all data rows.
     * 
     * @return True if the column was successfully removed, or false if an invalid column index was
     * supplied.
     */
    public boolean removeColumn(int columnIndex) {
        String columnName = columnNames.get(columnIndex);
        return removeColumn(columnName, columnIndex);
    }
    
    private boolean removeColumn(String columnName, int columnIndex) {
        columnNames.remove(columnIndex);
        for(ImportRow row : rows) {
            row.removeColumn(columnName, columnIndex);
        }
        return true;
    }
    
    //find the index of a column based on its name. Returns null if the column cannot be found.
    private Integer getColumnIndex(String columnName) {
        Integer index = null;
        for(int i = 0; i < columnNames.size(); i++) {
            if(columnNames.get(i).equalsIgnoreCase(columnName)) {
                index = i;
            }
        }
        return index;
    }
    
    private boolean isRowBlank(String[] row) {
        if(row.length == 0 || (row.length == 1 && StringUtils.isBlank(row[0]))) {
            return true;
        }
        return false;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

}
