package com.cannontech.common.device.attribute.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Class that represents an attribute of a device
 */
public class Attribute {

    public static final Attribute LOAD = new Attribute("load");
    /**
     * Attribute representing total usage
     */
    public static final Attribute USAGE = new Attribute("usage");
    /**
     * Attribute representing total demand
     */
    public static final Attribute DEMAND = new Attribute("demand");

    private String key = null;
    private String description = null;

    public Attribute(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String name) {
        this.description = name;
    }

    public String toString() {
        return this.key;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Attribute == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        Attribute attribute = (Attribute) obj;
        return new EqualsBuilder().append(key, attribute.getKey()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(39, 49).append(key).toHashCode();
    }
}
