package com.cannontech.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

/**
 * Used for cti:constantValue() func
 */
public class ReflectivePropertySearcher {

    private static Map<String, Object> valueLookupCache = new TreeMap<>();

    private ReflectivePropertySearcher() {}

    /**
     * Uses reflection to look up the value of a fully qualified constant. For instance,
     *   getObjectForFQN("com.cannontech.whatever.SomeClass.SOMEFIELD")
     * might return
     *   VALUE.
     * The field should be declared to be "final static" and must have been initialized
     * before this method is called (so, it should be initialized when the class is loaded).
     * @param fqn a package, class name, and integer field name all separated by periods
     * @return the value
     * @throws IllegalArgumentException if the fqn isn't valid (see nested cause for
     *   more detail, usually a reflection problem)
     */
    public static synchronized Object getObjectForFQN(String fqn) throws IllegalArgumentException {
        if (valueLookupCache.containsKey(fqn)) {
            return valueLookupCache.get(fqn);
        }
        int lastDot = fqn.lastIndexOf(".");
        String className = fqn.substring(0, lastDot);
        String fieldName = fqn.substring(lastDot + 1);
        try {
            Class<?> theClass = Class.forName(className);
            Field field = theClass.getField(fieldName);
            Object result = field.get(null);
            valueLookupCache.put(fqn, result);
            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to find value of " 
                                               + fieldName + " in class " + className
                                               + ": " + e.getMessage());
        }
    }
}
