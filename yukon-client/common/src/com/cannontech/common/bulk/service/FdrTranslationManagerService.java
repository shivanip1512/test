package com.cannontech.common.bulk.service;

import java.util.List;

import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.user.YukonUserContext;
import com.cannontech.common.bulk.model.FdrImportFileInterfaceInfo;
import com.cannontech.common.bulk.model.FdrInterfaceDisplayable;
import com.cannontech.common.exception.ImportFileFormatException;

public interface FdrTranslationManagerService {
    /**
     * Gets a List of FdrTranslations from the database. If the filterString is
     * "AllInterfaces", all interfaces will be returned. Otherwise, filterString
     * must specify an interface type to return.
     */
    public List<FdrTranslation> getFilteredTranslationList(String filterString);
    
    /**
     * Gets a List of FdrInterfaceDisplayables containing all fdr interfaces.
     */
    public List<FdrInterfaceDisplayable> getAllInterfaceDisplayables(YukonUserContext userContext);
    
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
     * Takes headers, ignored columns and rows of import file and begins import.
     * Returns the resultId for this import operation.
     */
    public String startImport(List<String> headers, List<Integer> columnsToIgnore, List<String[]> fileLines, YukonUserContext userContext, String originalFileName);
}
