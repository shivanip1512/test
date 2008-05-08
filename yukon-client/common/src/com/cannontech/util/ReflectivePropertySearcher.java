package com.cannontech.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReflectivePropertySearcher {
    private List<String> searchPath;
    private Map<String, String> nameLookupCache = new TreeMap<String, String>();
    private static Map<String, Object> valueLookupCache = new TreeMap<String, Object>();
    private static ReflectivePropertySearcher standardInstance = null;
    
    private ReflectivePropertySearcher(List<String> searchPath) {
        this.searchPath = searchPath;
    }
    
    private ReflectivePropertySearcher() {};

    public static synchronized ReflectivePropertySearcher getRoleProperty() {
        if (standardInstance == null) {
            String[] path = new String[] {
                "com.cannontech", 
                "com.cannontech.roles", 
                "com.cannontech.roles.application",
                "com.cannontech.roles.capcontrol",
                "com.cannontech.roles.cicustomer",
                "com.cannontech.roles.consumer",
                "com.cannontech.roles.loadcontrol",
                "com.cannontech.roles.notifications",
                "com.cannontech.roles.operator",
                "com.cannontech.roles.yukon"};
            standardInstance = new ReflectivePropertySearcher(Arrays.asList(path));
        }
        return standardInstance;
    }
    
    /**
     * Uses a predefined search path to look for a constant. For instance,
     *   getIntForShortName(SomeClass.SOMEFIELD)
     * might return
     *   10000
     * if SomeClass is defined in one of the packages specified for the
     * search path.
     * @param property Class name and static integer field name
     * @return the integer value
     * @throws IllegalArgumentException if the property can't be found
     */
    public synchronized int getIntForName(String property) {
        if (nameLookupCache.containsKey(property)) {
            return getIntForFQN(nameLookupCache.get(property));
        }
        // see if it a FQN
        try {
            return getIntForFQN(property);
        } catch (IllegalArgumentException e) {
            // guess not
        }
        Integer result = null;
        String resultFqn = null;
        for (String packagePrefix : searchPath) {
            String fullQualifiedName = packagePrefix + "." + property;
            int tempResult;
            try {
                tempResult = getIntForFQN(fullQualifiedName);
            } catch (IllegalArgumentException e) {
                continue;
            }
            // if we got here, it must have worked
            // but let's make sure it's unique
            if (result != null) {
                // we have other matches
                throw new IllegalArgumentException("\"" + property + "\" is not unique");
            }
            result = tempResult;
            resultFqn = fullQualifiedName;
        }
        if (result != null) {
            nameLookupCache.put(property, resultFqn);
            return result;
        } else {
            throw new IllegalArgumentException("Unable to find integer value for " 
                                               + property
                                               + ", check ReflectivePropertySearcher for a list " 
                                               + "of paths that were searched.");
        }
    }

    /**
     * Uses reflection to look up the value of an fully qualified constant. For instance,
     *   getIntForFQN("com.cannontech.whatever.SomeClass.SOMEFIELD")
     * might return
     *   10000.
     * The field should be declared to be "final static" and must have been initialized
     * before this method is called (so, it should be initialized when the class is loaded).
     * @param fqn a package, class name, and integer field name all separated by periods
     * @return the integer value
     * @throws IllegalArgumentException if the fqn isn't valid (see nested cause for
     *   more detail, usually a reflection problem)
     */
    public static synchronized int getIntForFQN(String fqn) {
        Object result = getObjectForFQN(fqn);
        if(result instanceof Integer){
            return ((Integer)result).intValue();
        } else {
            throw new IllegalArgumentException(fqn + " is not an int.");
        }
    }

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
            Class theClass = Class.forName(className);
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
    
    /**
     * Uses reflection to look up the value of an fully qualified constant. For instance,
     *   getIntForFQN("com.cannontech.whatever.SomeClass", "SOMEFIELD")
     * might return
     *   10000.
     * The field should be declared to be "final static" and must have been initialized
     * before this method is called (so, it should be initialized when the class is loaded).
     * @param fqn a package and class name
     * @param an integer field name
     * @return the integer value
     * @throws IllegalArgumentException if the fqn isn't valid (see nested cause for
     *   more detail, usually a reflection problem)
     */
    public static synchronized int getIntForFQN(String clazz, String constant) throws IllegalArgumentException {
        return getIntForFQN(clazz + "." + constant);
    }


}
