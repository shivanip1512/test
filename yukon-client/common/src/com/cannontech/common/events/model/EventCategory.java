package com.cannontech.common.events.model;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class EventCategory implements Comparable<EventCategory> {
    
    private final EventCategory parent;
    private final String name;

    private EventCategory(EventCategory parent, String name) {
        this.parent = parent;
        this.name = name;
    }
    
    
    public String getFullName() {
        if (parent == null) {
            return name;
        } else {
            return parent.getFullName() + "." + name;
        }
    }
    
    public String getName() {
        return name;
    }
    
    public EventCategory getParent() {
        return parent;
    }


    public static EventCategory createCategory(String name) {
        String[] split = StringUtils.split(name, ".");
        
        Validate.isTrue(split.length >= 1, "name must contain at least 1 segments", name);
        EventCategory result = createCategory(split);
        Validate.isTrue(result.getFullName().equals(name), "parse category name didn't fully match input", name);
        
        return result;
    }


    private static EventCategory createCategory(String[] split) {
        if (split.length == 0) return null;
        
        String[] copyOfRange = Arrays.copyOfRange(split, 0, split.length -1 );
        EventCategory newParent = createCategory(copyOfRange);
        String newName = split[split.length - 1];
        EventCategory result = new EventCategory(newParent, newName);
        return result;
        
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
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
        EventCategory other = (EventCategory) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (parent == null) {
            if (other.parent != null)
                return false;
        } else if (!parent.equals(other.parent))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return getFullName();
    }
    
    @Override
    public int compareTo(EventCategory o) {
        return getFullName().compareToIgnoreCase(o.getFullName());
    }
    
}
