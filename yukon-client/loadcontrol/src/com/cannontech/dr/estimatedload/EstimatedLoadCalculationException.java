package com.cannontech.dr.estimatedload;

public class EstimatedLoadCalculationException extends Exception {

    private Type type;
    private Object[] args;
    
    public EstimatedLoadCalculationException(Type type, Object... args) {
        this.type = type;
        this.args = args;
    }
    
    public String getResolvableKey() {
        return type.getKey();
    }
    
    public Object[] getResolvableArgs() {
        return args;
    }
    
    public enum Type {
        APPLIANCE_CATEGORY_INFO_NOT_FOUND("yukon.web.modules.dr.estimatedLoad.calcErrors.appCatInfoNotFound"),
        GEAR_NOT_FOUND("yukon.web.modules.dr.estimatedLoad.calcErrors.gearNotFound"),
        INPUT_OUT_OF_RANGE("yukon.web.modules.dr.estimatedLoad.calcErrors.inputOutOfRange"),
        INPUT_VALUE_NOT_FOUND("yukon.web.modules.dr.estimatedLoad.calcErrors.inputValueNotFound"),
        INVALID_PAO_TYPE("yukon.web.modules.dr.estimatedLoad.calcErrors.invalidPaoType"),
        NO_AVERAGE_KW_LOAD("yukon.web.modules.dr.estimatedLoad.calcErrors.noAverageKwLoad"),
        NO_FORMULA_FOR_APPLIANCE_CATEGORY("yukon.web.modules.dr.estimatedLoad.calcErrors.error"),
        NO_FORMULA_FOR_GEAR("yukon.web.modules.dr.estimatedLoad.calcErrors.error"),
        ;
        
        private String key;
        
        private Type(String key) {
            this.key = key;
        }
        
        public String getKey() {
            return key;
        }
    }
}
