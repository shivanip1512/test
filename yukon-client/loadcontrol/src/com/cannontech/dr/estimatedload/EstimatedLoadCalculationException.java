package com.cannontech.dr.estimatedload;

public class EstimatedLoadCalculationException extends Exception {

    private Type type;
    private Object[] args;

    public EstimatedLoadCalculationException(Throwable e) {
        this.type = ((EstimatedLoadCalculationException) e).getType();
        this.args = ((EstimatedLoadCalculationException) e).getResolvableArgs();
    }

    public EstimatedLoadCalculationException(Type type, Object... args) {
        this.type = type;
        this.args = args;
    }
    
    public Type getType() {
        return type;
    }
    
    public String getResolvableKey() {
        return type.getKey();
    }
    
    public Object[] getResolvableArgs() {
        return args;
    }
    
    public enum Type {
        APPLIANCE_CATEGORY_INFO_NOT_FOUND("yukon.web.modules.dr.estimatedLoad.calcErrors.appCatInfoNotFound"),
        CONTROL_AREA_HAS_ERRORS("yukon.web.modules.dr.estimatedLoad.calcErrors.controlAreaHasErrors"),
        GEAR_NUMBER_NOT_FOUND("yukon.web.modules.dr.estimatedLoad.calcErrors.gearNumberNotFound"),
        INPUT_OUT_OF_RANGE("yukon.web.modules.dr.estimatedLoad.calcErrors.inputOutOfRange"),
        INPUT_VALUE_NOT_FOUND("yukon.web.modules.dr.estimatedLoad.calcErrors.inputValueNotFound"),
        INVALID_PAO_TYPE("yukon.web.modules.dr.estimatedLoad.calcErrors.invalidPaoType"),
        LOAD_MANAGEMENT_SERVER_NOT_CONNECTED("yukon.web.modules.dr.estimatedLoad.calcErrors.loadManagementServerNotConnected"),
        LOAD_MANAGEMENT_DATA_NOT_FOUND("yukon.web.modules.dr.estimatedLoad.calcErrors.loadManagementDataNotFound"),
        NO_CONTRIBUTING_PROGRAMS("yukon.web.modules.dr.estimatedLoad.calcErrors.noContributingPrograms"),
        NO_FORMULA_FOR_APPLIANCE_CATEGORY("yukon.web.modules.dr.estimatedLoad.calcErrors.acFormulaAssignmentNotFound"),
        NO_FORMULA_FOR_GEAR("yukon.web.modules.dr.estimatedLoad.calcErrors.gearFormulaAssignmentNotFound"),
        SCENARIO_HAS_ERRORS("yukon.web.modules.dr.estimatedLoad.calcErrors.scenarioHasErrors"),
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
