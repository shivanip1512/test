package com.cannontech.common.bulk.collection.device.model;

public final class DeviceCollectionField {

    private final String name;
    private final String value;
    
    private DeviceCollectionField(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    public static DeviceCollectionField of(String name, String value) {
        return new DeviceCollectionField(name, value);
    }
    
    public String getName() {
        return name;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DeviceCollectionField other = (DeviceCollectionField) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "DeviceCollectionField [name=" + name + ", value=" + value + "]";
    }
    
}