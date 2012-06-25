package com.cannontech.common.pao.definition.model;

import java.io.Serializable;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;

public final class PaoPointIdentifier implements Serializable {
    private PaoIdentifier paoIdentifier;
    private PointIdentifier pointIdentifier;
    
    public PaoPointIdentifier(PaoIdentifier paoIdentifier,
            PointIdentifier pointIdentifier) {
		this.paoIdentifier = paoIdentifier;
        this.pointIdentifier = pointIdentifier;
    }
    public PaoIdentifier getPaoIdentifier() {
		return paoIdentifier;
	}
    public PointIdentifier getPointIdentifier() {
        return pointIdentifier;
    }
    public PaoTypePointIdentifier getPaoTypePointIdentifier() {
        return new PaoTypePointIdentifier(paoIdentifier.getPaoType(), pointIdentifier);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((paoIdentifier == null) ? 0 : paoIdentifier.hashCode());
        result = prime * result + ((pointIdentifier == null) ? 0 : pointIdentifier.hashCode());
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
        PaoPointIdentifier other = (PaoPointIdentifier) obj;
        if (paoIdentifier == null) {
            if (other.paoIdentifier != null)
                return false;
        } else if (!paoIdentifier.equals(other.paoIdentifier))
            return false;
        if (pointIdentifier == null) {
            if (other.pointIdentifier != null)
                return false;
        } else if (!pointIdentifier.equals(other.pointIdentifier))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return paoIdentifier + ":" + pointIdentifier;
    }

    /**
     * Helper method to create a paoPointIdentifier from a litePoint and YukonPao.
     * @param litePoint
     * @param yukonPao
     * @return
     */
    public static PaoPointIdentifier createPaoPointIdentifier(LitePoint litePoint, YukonPao yukonPao) {
        PointType pointType = PointType.getForId(litePoint.getPointType());
        PointIdentifier pointIdentifier = new PointIdentifier(pointType, litePoint.getPointOffset());
        PaoPointIdentifier paoPointIdentifier = new PaoPointIdentifier(yukonPao.getPaoIdentifier(), pointIdentifier);
        return paoPointIdentifier;
    }
    
    /**
     * Helper method to create a paoPointIdentifier from required pao and point identifier fields.
     * @param paobjectId
     * @param paoType
     * @param pointType
     * @param pointOffset
     * @return
     */
    public static PaoPointIdentifier createPaoPointIdentifier(int paobjectId, PaoType paoType, PointType pointType, int pointOffset) {
        PointIdentifier pointIdentifier = new PointIdentifier(pointType, pointOffset);
        PaoIdentifier paoIdentifier = new PaoIdentifier(paobjectId, paoType);
        PaoPointIdentifier paoPointIdentifier = new PaoPointIdentifier(paoIdentifier, pointIdentifier);
        return paoPointIdentifier;
    }
}