package com.cannontech.common.exception;

import java.util.HashMap;
import java.util.Map;

public class DataDependencyException extends Exception {

    public enum DependancyType {
        //CustomAttribute attr = exception.getDependancy(DependancyType.ATTRIBUTE, CustomAttribute.class);
        ATTRIBUTE,
        DEVICE_DATA_MONITOR_NAMES,
        //Set<String> exports = exception.getDependancy(DependancyType.EXPORT_FORMAT_NAMES, Set.class);
        EXPORT_FORMAT_NAMES;
    }
    
    private Map<DependancyType, Object> dependencies = new HashMap<>();

    private static final long serialVersionUID = 1L;

    public DataDependencyException(String message) {
        super(message);
    }
    
    public void addDependancy(DependancyType type, Object dependancy) {
        dependencies.put(type, dependancy);
    }
    
    public <T> T getDependancy(DependancyType type, Class<T> to) {
        return dependencies.get(type) == null ? null : to.cast(dependencies.get(type));
    }
}
