package com.cannontech.common.exception;

import java.util.HashMap;
import java.util.Map;

public class DataDependencyException extends Exception {

    public enum DependencyType {
        DEVICE_DATA_MONITOR,
        // List<String> formats = exception.getDependancy(DependancyType.EXPORT_FORMAT, List .class);
        EXPORT_FORMAT,
        // List<String> exports = exception.getDependancy(DependancyType.SCHEDULED_EXPORT, List.class);
        SCHEDULED_EXPORT;
    }

    private Object dependentObject;
    private Map<DependencyType, Object> dependencies = new HashMap<>();

    private static final long serialVersionUID = 1L;

    public DataDependencyException(Object dependentObject, String message) {
        super(message);
        this.dependentObject = dependentObject;
    }

    public void addDependency(DependencyType type, Object dependancy) {
        dependencies.put(type, dependancy);
    }

    public <T> T getDependency(DependencyType type, Class<T> to) {
        return dependencies.get(type) == null ? null : to.cast(dependencies.get(type));
    }

    public Object getDependentObject() {
        return dependentObject;
    }
}
