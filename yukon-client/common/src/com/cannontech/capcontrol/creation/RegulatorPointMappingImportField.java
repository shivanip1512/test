package com.cannontech.capcontrol.creation;

import java.util.List;

import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.pao.ImportPaoType;
import com.cannontech.enums.RegulatorPointMapping;
import com.google.common.collect.Lists;

public enum RegulatorPointMappingImportField {
    ACTION(InputType.REQUIRED, "ACTION", ImportAction.class, false, true),
    REGULATOR_NAME(InputType.REQUIRED, "REGULATOR NAME", String.class),
    MAPPING(InputType.REQUIRED, "MAPPING", RegulatorPointMapping.class, false, true),

    DEVICE_TYPE(InputType.VALUE_DEPENDENT, "DEVICE TYPE", ImportPaoType.class, "ACTION", ImportAction.ADD, ImportAction.UPDATE),
    DEVICE_NAME(InputType.VALUE_DEPENDENT, "DEVICE NAME", String.class, "ACTION", ImportAction.ADD, ImportAction.UPDATE),
    POINT_NAME(InputType.VALUE_DEPENDENT, "POINT NAME", String.class, "ACTION", ImportAction.ADD, ImportAction.UPDATE),
    ;

    public enum InputType{ REQUIRED, VALUE_DEPENDENT, OPTIONAL};

    private InputType inputType;
    private String name;
    private Class<?> typeClass;
    private boolean nullable;
    private boolean uppercaseValue;
    private String dependedColumnName;
    private Object[] dependedColumnValues;

    RegulatorPointMappingImportField(InputType inputType, String name, Class<?> typeClass){
        this(inputType, name, typeClass, false, false);
    }

    RegulatorPointMappingImportField(InputType inputType, String name, Class<?> typeClass, boolean nullable, boolean uppercaseValue){
        this(inputType, name, typeClass, false, false, null);
    }

    RegulatorPointMappingImportField(InputType inputType, String name, Class<?> typeClass, String dependedColumnName, Object... dependentValues){
        this(inputType, name, typeClass, false, false, dependedColumnName, dependentValues);
    }

    RegulatorPointMappingImportField(InputType inputType, String name, Class<?> typeClass, boolean nullable, boolean uppercaseValue, 
            String dependedColumnName, Object... dependentValues){
        this.inputType = inputType;
        this.name = name;
        this.typeClass = typeClass;
        this.nullable = nullable;
        this.uppercaseValue = uppercaseValue;
        this.dependedColumnName = dependedColumnName;
        this.dependedColumnValues = dependentValues;
    }

    public InputType getInputType() {
        return inputType;
    }

    public String getName() {
        return name;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isUppercaseValue() {
        return uppercaseValue;
    }

    public String getDependedColumnName() {
        return dependedColumnName;
    }

    public Object[] getDependedColumnValues() {
        return dependedColumnValues;
    }

    public static List<String> getRequiredFieldNames(){
        List<String> requiredFields = Lists.newArrayList();
        for(RegulatorPointMappingImportField format : values()){
            switch(format.getInputType()){
            case REQUIRED:
                requiredFields.add(format.getName());
                break;
            default:
                break;
            }
        }
        return requiredFields;
    }

    public static List<String> getValueDependentFieldNames(){
        List<String> valueDependentFields = Lists.newArrayList();
        for(RegulatorPointMappingImportField format : values()){
            switch(format.getInputType()){
            case VALUE_DEPENDENT:
                valueDependentFields.add(format.getName());
                break;
            default:
                break;
            }
        }
        return valueDependentFields;
    }

    public static List<String> getOptionalFieldNames(){
        List<String> optionalFields = Lists.newArrayList();
        for(RegulatorPointMappingImportField format : values()){
            switch(format.getInputType()){
            case OPTIONAL:
                optionalFields.add(format.getName());
                break;
            default:
                break;
            }
        }
        return optionalFields;
    }
}
