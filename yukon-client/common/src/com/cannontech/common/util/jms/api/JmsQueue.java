package com.cannontech.common.util.jms.api;

/**
 * Simple object to describe a JMS queue. It either has a queue name, or is a reference to TEMP_QUEUE.
 */
public class JmsQueue {
    private final String name;
    public static final JmsQueue TEMP_QUEUE = new JmsQueue("(temporary queue)");
    
    public JmsQueue(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isTempQueue() {
        return this == TEMP_QUEUE;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        JmsQueue other = (JmsQueue) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
    
}
