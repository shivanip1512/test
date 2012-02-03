package com.cannontech.common.bulk.model;

import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.cannontech.common.bulk.service.FdrCsvHeader;
import com.cannontech.common.fdr.FdrInterfaceOption;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Object to encapsulate a row of FDR translation import data.
 */
public class FdrImportDataRow {
    private Map<FdrCsvHeader, String> defaultColumns = Maps.newEnumMap(FdrCsvHeader.class);
    private FdrInterfaceType interfaceType;
    private Map<FdrInterfaceOption, String> interfaceColumns = Maps.newEnumMap(FdrInterfaceOption.class);
    
    /**
     * Sets the value for the specified default column.
     */
    public void setDefaultColumn(FdrCsvHeader column, String value) {
        defaultColumns.put(column, value);
    }
    
    /**
     * Sets the value for the specified interface-specific column. The
     * value will only be inserted if it belongs to the same interface as
     * all previously inserted columns.
     * @return true if the insertion was successful, otherwise false.
     */
    public boolean setInterfaceColumn(FdrInterfaceOption column, String value) {
        //On first insert, determine the interface.
        if(interfaceType == null) {
            interfaceType = FdrUtils.OPTION_TO_INTERFACE_MAP.get(column);
        }
        //Check that the option being inserted matches the fdr interface
        //of previously inserted options
        if(interfaceType.getInterfaceOptionsList().contains(column)) {
            interfaceColumns.put(column, value);
            return true;
        }
        return false;
    }
    
    public Map<FdrInterfaceOption, String> getInterfaceColumns() {
        return interfaceColumns;
    }
    
    public String getDeviceName() {
        return defaultColumns.get(FdrCsvHeader.DEVICE_NAME);
    }
    
    public String getDeviceType() {
        return defaultColumns.get(FdrCsvHeader.DEVICE_TYPE);
    }
    
    public String getPointName() {
        return defaultColumns.get(FdrCsvHeader.POINT_NAME);
    }
    
    public String getDirection() {
        return defaultColumns.get(FdrCsvHeader.DIRECTION);
    }
    
    public String getAction() {
        return defaultColumns.get(FdrCsvHeader.ACTION);
    }
    
    public FdrInterfaceType getInterface() {
        return interfaceType;
    }
    
    /**
     * Checks that all default and interface columns have been inserted. 
     * @return the String representation of a missing column, if found, otherwise null.
     */
    public String getMissingColumn() {
        List<String> missingColumns = getMissingDefaultColumn();
        missingColumns.addAll(getMissingInterfaceColumn());
        
        if(missingColumns.size() == 0) return null;
        
        String concatenatedOutput = StringUtils.collectionToCommaDelimitedString(missingColumns);
        
        return concatenatedOutput;
    }
    
    private List<String> getMissingInterfaceColumn() {
        List<String> missingColumns = Lists.newArrayList();
        for(FdrInterfaceOption option : interfaceType.getInterfaceOptions()) {
            if(!interfaceColumns.containsKey(option)) {
                missingColumns.add(option.toString());
            }
        }
        return missingColumns;
    }
    
    private List<String> getMissingDefaultColumn() {
        List<String> missingColumns = Lists.newArrayList();
        for(FdrCsvHeader header : FdrCsvHeader.values()) {
            if(!defaultColumns.containsKey(header)) {
                missingColumns.add(header.toString());
            }
        }
        return missingColumns;
    }
}
