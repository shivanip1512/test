package com.cannontech.common.bulk.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.model.FdrExportData;
import com.cannontech.common.bulk.model.FdrExportDataRow;
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

    public static final String POINTTYPE = "POINTTYPE";

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
     * Adds all exported default column headers to the specified list.
     * @see FdrCsvHeader
     */
    public void addDefaultExportColumnsToList(List<String> list) {
        for(FdrCsvHeader header : FdrCsvHeader.values()) {
            if(header.isExported()) {
                list.add(header.toString());
            }
        }
        list.add(FdrCsvHeader.DEVICE_NAME.toString());
        list.add(FdrCsvHeader.DEVICE_TYPE.toString());
        list.add(FdrCsvHeader.POINT_NAME.toString());
        list.add(FdrCsvHeader.DIRECTION.toString());
    }
    
    /**
     * Determines if the given string matches any of the default FDR column names.
     * @see FdrCsvHeader
     */
    public boolean matchesDefaultColumn(String header) {
        for(FdrCsvHeader defaultHeader : FdrCsvHeader.values()) {
            if(defaultHeader.toString().equals(header)) {
                return true;
            }
        }
        return false;
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
     * Takes an FdrExportData that already contains a header row and adds all
     * values from the translation list as FdrExportDataRows.
     */
    public void populateExportData(FdrExportData data, List<FdrTranslation> translationsList) {
        for(FdrTranslation translation : translationsList) {
            LitePoint point = pointDao.getLitePoint(translation.getPointId());
            LiteYukonPAObject pao = paoDao.getLiteYukonPAO(point.getPaobjectID());
            
            //Add default column values
            FdrExportDataRow row = new FdrExportDataRow();
            row.setDeviceName(pao.getPaoName());
            row.setDeviceType(pao.getPaoType().getPaoTypeName());
            row.setDirection(translation.getDirection().getValue());
            row.setPointName(point.getPointName());
            
            //Iterate through interface-specific columns
            for(String formattedColumnHeader : data.getInterfaceHeaders()) {
                //If the translation has a value for a given column, add it
                boolean matchFound = false;
                for(Map.Entry<String, String> option : translation.getParameterMap().entrySet()) {
                    String translationName = translation.getFdrInterfaceType().toString();
                    String translationPart = formatOptionForColumnHeader(option.getKey(), translationName);
                    if(translationPart.equals(formattedColumnHeader)) {
                        row.addInterfaceValue(option.getValue());
                        matchFound = true;
                    }
                }
                
                //If the translation does not have a value for a given column, add an empty string
                if(!matchFound) {
                    row.addInterfaceValue("");
                }
            }
            data.addRow(row);
        }
    }
    
    /**
     * Checks for the presence of the default import headers in a list of strings.
     * Returns a comma-delimited string of missing default headers, or null if all headers are present.
     */
    public String checkForMissingDefaultImportHeaders(List<String> headers) {
        String returnValue = null;
        for(FdrCsvHeader defaultHeader : FdrCsvHeader.values()) {
            if(!headers.contains(defaultHeader.toString())) {
                if(returnValue != null) {
                    returnValue += ", " + defaultHeader;
                } else {
                    returnValue = defaultHeader.toString();
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
            FdrInterfaceOption[] options = fdrInterface.getInterfaceOptions();
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
