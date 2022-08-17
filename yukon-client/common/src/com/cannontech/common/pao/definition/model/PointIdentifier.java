package com.cannontech.common.pao.definition.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.CompareToBuilder;

import com.cannontech.common.point.YukonPoint;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;

public final class PointIdentifier implements Comparable<PointIdentifier>, Serializable, YukonPoint {

    private final int offset;
    private final PointType type;

    public PointIdentifier(PointType type, int offset) {
        this.offset = offset;
        this.type = type;
    }

    public int getOffset() {
        return offset;
    }

    public PointType getPointType() {
        return type;
    }

    /**
     * @deprecated Use {@link #getPointType()}
     */
    @Deprecated
    public int getType() {
        return type.getPointTypeId();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + offset;
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
        PointIdentifier other = (PointIdentifier) obj;
        if (offset != other.offset)
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return type + ":" + offset;
    }

    @Override
    public int compareTo(PointIdentifier o) {
        return new CompareToBuilder()
            .append(getPointType(), o.getPointType())
            .append(getOffset(), o.getOffset())
            .toComparison();
    }

    /**
     * Creates a pointIdentifier from a litePoint
     */
    public static PointIdentifier createPointIdentifier(LitePoint litePoint) {
        PointType pointType = PointType.getForId(litePoint.getPointType());
        PointIdentifier pointIdentifier = new PointIdentifier(pointType, litePoint.getPointOffset());
        return pointIdentifier;
    }

    @Override
    public PointIdentifier getPointIdentifier() {
        return this;
    }
}