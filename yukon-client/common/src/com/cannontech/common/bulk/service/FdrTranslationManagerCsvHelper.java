package com.cannontech.common.bulk.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.model.FdrImportFileInterfaceInfo;
import com.cannontech.common.exception.ImportFileFormatException;
import com.cannontech.common.fdr.FdrInterfaceOption;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.google.common.collect.Lists;

public class FdrTranslationManagerCsvHelper {
    private PaoDao paoDao;
    private PointDao pointDao;
    
    public static final String ACTION = "ACTION";
    public static final String DEVICE_NAME = "DEVICE_NAME";
    public static final String DEVICE_TYPE = "DEVICE_TYPE";
    public static final String POINT_NAME = "POINT_NAME";
    public static final String DIRECTION = "DIRECTION";
    public static final String POINTTYPE = "POINTTYPE";
    
    private static final int DEVICE_NAME_COL = 0;
    private static final int DEVICE_TYPE_COL = 1;
    private static final int POINT_NAME_COL = 2;
    private static final int DIRECTION_COL = 3;
    private static final int DEFAULT_COLS_FOR_EXPORT = 4;
    
    private static final String[] defaultImportColumnHeaders = {ACTION, DEVICE_NAME, DEVICE_TYPE, POINT_NAME, DIRECTION};
    
    /**
     * Uppercase the option label
     * Replace spaces and slashes with underscores 
     * Remove parens
     * Prepend the interface name followed by an underscore
     */
    public String formatOptionForColumnHeader(String optionString, String interfaceName) {
        return interfaceName
               + "_"
               + optionString.toUpperCase()
                             .replaceAll("[ /]", "_")
                             .replaceAll("[()]", "");
    }
    
    /**
     * Adds DEVICE_NAME, DEVICE_TYPE, POINT_NAME, DIRECTION (but not ACTION) to the specified list. 
     */
    public void addDefaultColumnsToList(List<String> list) {
        list.add(DEVICE_NAME);
        list.add(DEVICE_TYPE);
        list.add(POINT_NAME);
        list.add(DIRECTION);
    }
    
    /**
     * Determines if the given string matches any of the default fdr column names.
     */
    public boolean matchesDefaultColumn(String header) {
        return header.equals(ACTION)
            || header.equals(DEVICE_NAME) 
            || header.equals(DEVICE_TYPE) 
            || header.equals(POINT_NAME) 
            || header.equals(DIRECTION);
    }
    
    /**
     * Takes a list of headers and a list of translations, and adds all headers
     * required for those translations' types to the headers list (the list is modified
     * in-place).
     */
    public void addHeadersFromTranslations(List<String> headers, List<FdrTranslation> translations) {
        List<FdrInterfaceType> interfaceTypesAdded = Lists.newArrayList(); 
        for(FdrTranslation translation : translations) {
            FdrInterfaceType thisInterfaceType = translation.getFdrInterfaceType();
            if(!interfaceTypesAdded.contains(thisInterfaceType)) {
                interfaceTypesAdded.add(thisInterfaceType);
                String interfaceName = translation.getFdrInterfaceType().toString();
                Set<String> thisTranslationColumns = translation.getParameterMap().keySet();
                for(String unformattedColumnHeader : thisTranslationColumns) {
                    //do not add POINTTYPE to import files
                    if(!unformattedColumnHeader.equals(POINTTYPE)) {
                        String formattedColumnHeader = formatOptionForColumnHeader(unformattedColumnHeader, interfaceName);
                        headers.add(formattedColumnHeader);
                    }
                }
            }
        }
    }
    
    /**
     * Takes a 2D String array (which must already contain a header row) and adds all
     * values from the translation list.
     */
    public void populateExportArray(String[][] dataGrid, List<FdrTranslation> translationsList) {
        for(int i = 0; i < translationsList.size(); i++) {
            FdrTranslation translation = translationsList.get(i);
            String translationName = translation.getFdrInterfaceType().toString();
            
            //Add default column values
            LitePoint point = pointDao.getLitePoint(translation.getPointId());
            LiteYukonPAObject pao = paoDao.getLiteYukonPAO(point.getPaobjectID());
            String deviceName = pao.getPaoName();
            String deviceType = pao.getPaoType().getPaoTypeName();
            String pointName = point.getPointName();
            String direction = translation.getDirection().getValue();
            dataGrid[i+1][DEVICE_NAME_COL] = deviceName;
            dataGrid[i+1][DEVICE_TYPE_COL] = deviceType;
            dataGrid[i+1][POINT_NAME_COL] = pointName;
            dataGrid[i+1][DIRECTION_COL] = direction;
            
            //Iterate through interface-specific columns
            for(int j = DEFAULT_COLS_FOR_EXPORT; j < dataGrid[0].length; j++) {
                //If the translation has a value for a given column, add it
                String formattedColumnHeader = dataGrid[0][j];
                boolean matchFound = false;
                for(Map.Entry<String, String> option : translation.getParameterMap().entrySet()) {
                    String translationPart = formatOptionForColumnHeader(option.getKey(), translationName);
                    if(translationPart.equals(formattedColumnHeader)) {
                        dataGrid[i+1][j] = option.getValue();
                        matchFound = true;
                    }
                }
                
                //If the translation does not have a value for a given column, add an empty string
                if(!matchFound) {
                    dataGrid[i+1][j] = "";
                }
            }
        }
    }
    
    /**
     * Checks for the presence of the default import headers in a list of strings.
     * Returns a comma-delimited string of missing default headers, or null if all headers are present.
     */
    public String checkForMissingDefaultImportHeaders(List<String> headers) {
        String returnValue = null;
        for(String defaultHeader : defaultImportColumnHeaders) {
            if(!headers.contains(defaultHeader)) {
                if(returnValue != null) {
                    returnValue += ", " + defaultHeader;
                } else {
                    returnValue = defaultHeader;
                }
            }
        }
        return returnValue;
    }
    
    /**
     * Removes whitespace from headers array, makes sure that there are no duplicates, and returns them
     * as a List<String>.
     */
    public List<String> cleanAndValidateHeaders(String[] inputHeaders) throws ImportFileFormatException {
        List<String> outputHeaders = Lists.newArrayList();
        
        for(String inputHeader : inputHeaders) {
            String noWhiteSpaceHeaderToAdd = StringUtils.deleteWhitespace(inputHeader).toUpperCase();
            if(outputHeaders.contains(noWhiteSpaceHeaderToAdd)) {
                ImportFileFormatException exception =  new ImportFileFormatException("Duplicate column header: " + noWhiteSpaceHeaderToAdd);
                exception.setHeaderName(noWhiteSpaceHeaderToAdd);
                throw exception;
            } else {
                outputHeaders.add(noWhiteSpaceHeaderToAdd);
            }
        }
        
        return outputHeaders;
    }
    
    /**
     * Ensures that the list of headers contains every header required for the interfaces in the 
     * FdrImportFileInterfaceInfo. If a header is missing, a ImportFileFormatException is thrown,
     * containing the missing header name and the interface it belongs to.
     */
    public void validateInterfaceHeadersPresent(FdrImportFileInterfaceInfo interfaceInfo, List<String> headers) throws ImportFileFormatException {
        for(FdrInterfaceType fdrInterface : interfaceInfo.getInterfaces()) {
            String interfaceName = fdrInterface.toString();
            //a. Get list of all columns required for each interface
            List<FdrInterfaceOption> options = FdrInterfaceOption.getByInterface(fdrInterface);
            //b. Check headers for all columns for each interface
            for(FdrInterfaceOption option : options) {
                if(!headers.contains(option.toString())) {
                    ImportFileFormatException exception = new ImportFileFormatException("Missing column header \"" + option + "\" for interface \"" + interfaceName);
                    exception.setHeaderName(option.toString());
                    exception.setInterfaceName(interfaceName);
                    throw exception;
                }
            }
        }
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
}
