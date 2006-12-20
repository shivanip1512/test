package com.cannontech.common.device.attribute;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Class that represents an attribute of a device
 */
public class Attribute {

    public static final Attribute LOAD = new Attribute("load", "load");
    public static final Attribute USAGE = new Attribute("usage", "usage");
    public static final Attribute DEMAND = new Attribute("demand", "demand");

    private String name = null;
    private String key = null;

    public Attribute(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return new EqualsBuilder().append(key, attribute.getKey())
                                  .append(name, attribute.getName())
                                  .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(39, 49).append(key).append(name).toHashCode();
    }
}
