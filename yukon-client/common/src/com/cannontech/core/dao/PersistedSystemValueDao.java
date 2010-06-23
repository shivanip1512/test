package com.cannontech.core.dao;

import org.joda.time.Instant;


public interface PersistedSystemValueDao {
    
    /**
     * Sets the system value as specified.
     * 
     * @param property a property
     * @param value a value that is compatible with the property's type
     */
    public void setValue(PersistedSystemValueKey property, Object value);
    
    /**
     * Returns the user's value of the specified property as a String.
     * 
     * Unlike the other methods, this can be called regardless of the type
     * of the property, it essentially calls a toString on the value and
     * returns that.
     * 
     * @param property any property
     * @return the value of the property the default if uninitialized
     */
    public String getStringValue(PersistedSystemValueKey property);
    
    /**
     * Returns the user's value of the specified property as a boolean.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Boolean.
     * 
     * @param property any property with a Boolean return type
     * @return  the value of the property or the default if uninitialized
     */
    public boolean getBooleanValue(PersistedSystemValueKey property);
    
    /**
     * Returns the user's value of the specified property as a long.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Number. Rounding or truncation may occur to return an long.
     * 
     * @param property any property with a Number return type
     * @return  the value of the property or the default if uninitialized
     */
    public long getLongValue(PersistedSystemValueKey property);
    
    /**
     * Returns the user's value of the specified property as an integer.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Number. Rounding or truncation may occur to return an int.
     * 
     * @param property any property with a Number return type
     * @return  the value of the property or the default if uninitialized
     */
    public int getIntegerValue(PersistedSystemValueKey property);
    
    /**
     * Returns the user's value of the specified property as a float.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Number. Rounding or truncation may occur to return an float.
     * 
     * @param property any property with a Number return type
     * @return  the value of the property or 0 the default if uninitialized
     */
    public float getFloatValue(PersistedSystemValueKey property);
    
    /**
     * Returns the user's value of the specified property as a double.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Number. Rounding or truncation may occur to return a double.
     * 
     * @param property any property with a Number return type
     * @return  the value of the property or the default if uninitialized
     */
    public double getDoubleValue(PersistedSystemValueKey property);
    
    /**
     * Returns an Instant specified by the property key.
     * 
     * This method should only be called with keys based on the InstantType.
     */
    public Instant getInstantValue(PersistedSystemValueKey property);

}
