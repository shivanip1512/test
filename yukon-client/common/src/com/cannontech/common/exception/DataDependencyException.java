package com.cannontech.common.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataDependencyException extends Exception {

    public enum DependencyType {
        DEVICE_DATA_MONITOR,
        EXPORT_FORMAT,
        SCHEDULED_EXPORT;
    }

    private String dependentObject;
    private Map<DependencyType, List<String>> dependencies = new HashMap<>();

    private static final long serialVersionUID = 1L;

    public DataDependencyException(String dependentObject, String message) {
        super(message);
        this.dependentObject = dependentObject;
    }

    public void addDependency(DependencyType type, List<String> dependancy) {
        dependencies.put(type, dependancy);
    }

    public String getDependentObject() {
        return dependentObject;
    }
    
    public Map<DependencyType, List<String>> getDependencies(){
        return dependencies;
    }
    
    public List<String> getDependency(DependencyType type) {
        return dependencies.get(type) == null ? null : dependencies.get(type);
    }
}
