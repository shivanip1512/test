package com.cannontech.common.events.model;

public class EventParameter {
    private String name;
    private Class<?> mappedType;
    private boolean named;
    private String annotatedName;
    private int argumentNumber;
    
    private EventParameter(String annotatedName, int argumentNumber, Class<?> mappedType, boolean named) {
        this.annotatedName = annotatedName;
        this.argumentNumber = argumentNumber;
        this.mappedType = mappedType;
        this.named = named;
    }

    private EventParameter(int argumentNumber, Class<?> mappedClass, boolean named) {
        this.argumentNumber = argumentNumber;
        this.mappedType = mappedClass;
        this.named = named;
    }

    public static EventParameter createNamed(String annotatedName, int argumentNumber, Class<?> mappedClass) {
        return new EventParameter(annotatedName, argumentNumber, mappedClass, true);
    }
    
    public static EventParameter createDefault(int argumentNumber, Class<?> mappedClass) {
        return new EventParameter(argumentNumber, mappedClass, false);
    }

    public String getName() {
        return isNamed() ? annotatedName : String.valueOf(argumentNumber);
    }
    
    public int getArgumentNumber() {
        return argumentNumber;
    }
    
    public String getAnnotatedName() {
        return annotatedName;
    }
    
    public Class<?> getMappedType() {
        return mappedType;
    }

    public boolean isNamed() {
        return named;
    }
    
    public String toString() {
        String retVal;
        
        retVal = name+" ["+mappedType+"] ";
        
        return retVal;
    }
}
