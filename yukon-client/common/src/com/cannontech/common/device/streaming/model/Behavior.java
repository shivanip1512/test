package com.cannontech.common.device.streaming.model;

import java.util.HashMap;
import java.util.Map;

public class Behavior {
    private int id;
    private BehaviorType type;
    private Map<String, String> nameValueMap = new HashMap<>();
    
    public BehaviorType getType() {
        return type;
    }

    public void setType(BehaviorType type) {
        this.type = type;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * @return a copy of the values map.
     */
    public Map<String, String> getValuesMap() {
        return new HashMap<>(nameValueMap);
    }
    
    /**
     * Set the values map. This new set of values will replace any values previously added.
     */
    public void setValues(Map<String, String> values) {
        nameValueMap = new HashMap<>(values);
    }
    
    public void addValue(String name, String value) {
        nameValueMap.put(name, value);
    }
    
    public void addValue(String name, boolean value) {
        nameValueMap.put(name, Boolean.toString(value));
    }

    public void addValue(String name, int value) {
        addValue(name, Integer.toString(value));
    }
    
    public <T extends Enum<?>> void addValue(String name, T Enumeration) {
        addValue(name, Enumeration.name());
    }
    
    public String getValue(String name) {
        return nameValueMap.get(name);
    }
    
    public Integer getIntValue(String name) {
        String stringValue = nameValueMap.get(name);
        return Integer.parseInt(stringValue);
    }
    
    public <T extends Enum<T>> T getEnumValue(String name, Class<T> enumClass) {
        String stringValue = nameValueMap.get(name);
        return Enum.valueOf(enumClass, stringValue);
    }
}
