package com.cannontech.capcontrol.creation;

import java.util.List;
import java.util.Locale;

import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.csvImport.types.RegulatorType;
import com.cannontech.common.csvImport.types.StrictBoolean;
import com.google.common.collect.Lists;

public enum RegulatorImportField {
    ACTION(InputType.REQUIRED, "ACTION", ImportAction.class, false, true),
    NAME(InputType.REQUIRED, "NAME", String.class),

    TYPE(InputType.VALUE_DEPENDENT, "TYPE", RegulatorType.class, false, true, "ACTION", ImportAction.ADD),

    DESCRIPTION(InputType.OPTIONAL, "DESCRIPTION", String.class),
    DISABLED(InputType.OPTIONAL, "DISABLED", StrictBoolean.class),
    KEEP_ALIVE_TIMER(InputType.OPTIONAL, "KEEP ALIVE TIMER", Integer.class),
    KEEP_ALIVE_CONFIG(InputType.OPTIONAL, "KEEP ALIVE CONFIG", Integer.class),
    VOLT_CHANGE_PER_TAP(InputType.OPTIONAL, "VOLT CHANGE PER TAP", Double.class),
    ;

    public enum InputType{ REQUIRED, VALUE_DEPENDENT, OPTIONAL};

    private InputType inputType;
    private String name;
    private Class<?> typeClass;
    private boolean nullable;
    private boolean uppercaseValue;
    private String dependedColumnName;
    private Object[] dependedColumnValues;

    RegulatorImportField(InputType inputType, String name, Class<?> typeClass){
        this(inputType, name, typeClass, false, false);
    }

    RegulatorImportField(InputType inputType, String name, Class<?> typeClass, boolean nullable, boolean uppercaseValue){
        this(inputType, name, typeClass, nullable, uppercaseValue, null);
    }

    RegulatorImportField(InputType inputType, String name, Class<?> typeClass, boolean nullable, boolean uppercaseValue, 
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
        for(RegulatorImportField format : values()){
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
        for(RegulatorImportField format : values()){
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
        for(RegulatorImportField format : values()){
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

    public static RegulatorImportField getByFieldName(String field) {
        return valueOf(field.replaceAll(" ", "_").toUpperCase(Locale.US));
    }
}
