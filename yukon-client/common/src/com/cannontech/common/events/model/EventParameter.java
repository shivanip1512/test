package com.cannontech.common.events.model;

public class EventParameter {
    private String name;
    private Class<?> mappedType;
    private boolean named;
    
    private EventParameter(String name, Class<?> mappedType, boolean named) {
        super();
        this.name = name;
        this.mappedType = mappedType;
        this.named = named;
    }

    public static EventParameter createNamed(String name, Class<?> mappedClass) {
        return new EventParameter(name, mappedClass, true);
    }
    
    public static EventParameter createDefault(int paramNumber, Class<?> mappedClass) {
        return new EventParameter(Integer.toString(paramNumber), mappedClass, false);
    }

    public String getName() {
        return name;
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
