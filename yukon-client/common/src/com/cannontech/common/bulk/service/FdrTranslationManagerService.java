package com.cannontech.common.bulk.service;

import java.util.List;

import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.user.YukonUserContext;
import com.cannontech.common.bulk.model.FdrImportFileInterfaceInfo;
import com.cannontech.common.bulk.model.FdrInterfaceDisplayable;
import com.cannontech.common.exception.ImportFileFormatException;

public interface FdrTranslationManagerService {
    
    /**
     * Uppercase the option label
     * Replace spaces and slashes with underscores 
     * Remove parens
     * Prepend the interface name followed by an underscore
     */
    public String formatOptionForColumnHeader(String optionString, String interfaceName);
    
    /**
     * Adds DEVICE_NAME, DEVICE_TYPE, POINT_NAME, DIRECTION (but not ACTION) to the specified list. 
     */
    public void addDefaultColumnsToList(List<String> list);
    
    /**
     * Determines if the given string matches any of the default fdr column names.
     */
    public boolean matchesDefaultColumn(String header);
    
    /**
     * Gets a List of FdrTranslations from the database. If the filterString is
     * "AllInterfaces", all interfaces will be returned. Otherwise, filterString
     * must specify an interface type to return.
     */
    public List<FdrTranslation> getFilteredTranslationList(String filterString);
    
    /**
     * Takes a list of headers and a list of translations, and adds all headers
     * required for those translations' types to the headers list (the list is modified
     * in-place).
     */
    public void addHeadersFromTranslations(List<String> headers, List<FdrTranslation> translations);
    
    /**
     * Takes a 2D String array (which must already contain a header row) and adds all
     * values from the translation list.
     */
    public void populateExportArray(String[][] dataGrid, List<FdrTranslation> translationsList);
    
    /**
     * Gets a List of FdrInterfaceDisplayables containing all fdr interfaces.
     */
    public List<FdrInterfaceDisplayable> getAllInterfaceDisplayables(YukonUserContext userContext);
    
    /**
     * Checks for the presence of the default import headers in a list of strings.
     * Returns the first missing default header, or null if all headers are present.
     */
    public String checkForMissingDefaultImportHeaders(List<String> headers);
    
    /**
     * Removes whitespace from headers array, makes sure that there are no duplicates, and returns them
     * as a List<String>.
     */
    public List<String> cleanAndValidateHeaders(String[] inputHeaders) throws ImportFileFormatException;
    
    /**
     * Iterates over import file headers and confirms that they're legitimate default or
     * interface-specific headers. If ignoreInvalidColumns is false, any invalid headers will cause
     * an ImportFileFormatException.
     * @return An FdrImportFileInterfaceInfo object, which contains a list of all the FDRInterfaces
     * to whom these headers belong, and a list of column numbers to be ignored due to invalid
     * headers.
     */
    public FdrImportFileInterfaceInfo getInterfaceInfo(List<String> headers, boolean ignoreInvalidColumns) throws ImportFileFormatException;
    
    /**
     * Ensures that the list of headers contains every header required for the interfaces in the 
     * FdrImportFileInterfaceInfo. If a header is missing, a ImportFileFormatException is thrown,
     * containing the missing header name and the interface it belongs to.
     */
    public void validateInterfaceHeadersPresent(FdrImportFileInterfaceInfo interfaceInfo, List<String> headers) throws ImportFileFormatException;
    
    /**
     * Takes headers, ignored columns and rows of import file and begins import.
     * Returns the resultId for this import operation.
     */
    public String startImport(List<String> headers, List<Integer> columnsToIgnore, List<String[]> fileLines, YukonUserContext userContext);
}
