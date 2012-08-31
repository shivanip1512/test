package com.cannontech.database.data.lite;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.google.common.base.Function;

public abstract class LiteBase implements java.io.Serializable, Comparable<Object> {
    private int liteType;
    private int liteId;

    @Override
    public int compareTo(Object other) {
        return liteId - ((LiteBase) other).liteId;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof LiteBase) {
            return ((LiteBase) other).liteId == liteId && ((LiteBase) other).liteType == liteType;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(6709, 3511).append(liteType).append(liteId).toHashCode();
    }

    public int getLiteID() {
        return liteId;
    }

    public int getLiteType() {
        return liteType;
    }

    protected void setLiteID(int newValue) {
        this.liteId = newValue;
    }

    protected void setLiteType(int newValue) {
        this.liteType = newValue;
    }

    public final static Function<LiteBase, Integer> ID_FUNCTION = new Function<LiteBase, Integer>() {
        @Override
        public Integer apply(LiteBase obj) {
            return obj.liteId;
        }
    };
}
