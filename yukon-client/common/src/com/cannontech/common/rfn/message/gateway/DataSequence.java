package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;
import java.util.Set;

public class DataSequence implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private DataType type;
    private double completionPercentage;
    private Set<SequenceBlock> blocks;
    
    public DataType getType() {
        return type;
    }
    
    public void setType(DataType type) {
        this.type = type;
    }
    
    public double getCompletionPercentage() {
        return completionPercentage;
    }
    
    public void setCompletionPercentage(double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
    
    public Set<SequenceBlock> getBlocks() {
        return blocks;
    }
    
    public void setBlocks(Set<SequenceBlock> blocks) {
        this.blocks = blocks;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((blocks == null) ? 0 : blocks.hashCode());
        long temp;
        temp = Double.doubleToLongBits(completionPercentage);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        DataSequence other = (DataSequence) obj;
        if (blocks == null) {
            if (other.blocks != null)
                return false;
        } else if (!blocks.equals(other.blocks))
            return false;
        if (Double.doubleToLongBits(completionPercentage) != Double
                .doubleToLongBits(other.completionPercentage))
            return false;
        if (type != other.type)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return String.format(
                "DataSequence [type=%s, completionPercentage=%s, blocks=%s]", type, completionPercentage, blocks);
    }
    
}