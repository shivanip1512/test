package com.cannontech.web.capcontrol.importer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cannontech.capcontrol.creation.CapControlImporterCbcField;
import com.cannontech.capcontrol.creation.CapControlImporterHierarchyField;
import com.cannontech.capcontrol.creation.RegulatorImportField;
import com.cannontech.capcontrol.creation.RegulatorPointMappingImportField;
import com.cannontech.capcontrol.creation.CapControlIvvcZoneImportField;
import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.Maps;

public enum ImportType implements DisplayableEnum {
    
    CBC( CapControlImporterCbcField.getRequiredFieldNames(), new ArrayList<String>(),
         CapControlImporterCbcField.getOptionalFieldNames() ),
         
    HIERARCHY(CapControlImporterHierarchyField.getRequiredFieldNames(), new ArrayList<String>(),
               CapControlImporterHierarchyField.getOptionalFieldNames() ),
               
    REGULATOR(RegulatorImportField.getRequiredFieldNames(),
              RegulatorImportField.getValueDependentFieldNames(),
              RegulatorImportField.getOptionalFieldNames() ),
              
    POINT_MAPPING(RegulatorPointMappingImportField.getRequiredFieldNames(),
                  RegulatorPointMappingImportField.getValueDependentFieldNames(),
                  RegulatorPointMappingImportField.getOptionalFieldNames() ),
    // Will be added back in as part of YUK-23010 in 9.1.0
/*    IVVC_ZONE(CapControlIvvcZoneImportField.getRequiredFieldNames(),
              CapControlIvvcZoneImportField.getValueDependentFieldNames(),
              CapControlIvvcZoneImportField.getOptionalFieldNames())*/
    ;
    
    private List<String> requiredColumns;
    private List<String> valueDependentColumns;
    private List<String> optionalColumns;
    
    private ImportType(List<String> requiredColumns, List<String> valueDependentColumns, List<String> optionalColumns){
        this.requiredColumns = requiredColumns;
        this.valueDependentColumns = valueDependentColumns;
        this.optionalColumns = optionalColumns;
    }
    
    public List<String> getRequiredColumns() {
        return requiredColumns;
    }
    
    public List<String> getValueDependentColumns() {
        return valueDependentColumns;
    }
    
    public List<String> getOptionalColumns() {
        return optionalColumns;
    }
    
    public Map<String,List<String>> getColumnTypes(){
        Map<String,List<String>> results = Maps.newLinkedHashMap();
        results.put("required", getRequiredColumns());
        results.put("valueDependent", getValueDependentColumns());
        results.put("optional", getOptionalColumns());
        return results;
    }
    
    public String getFormatKey() {
        return "yukon.web.modules.capcontrol.import.importTypes." + name();
    }
    
}