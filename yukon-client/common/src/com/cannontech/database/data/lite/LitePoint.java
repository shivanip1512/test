package com.cannontech.database.data.lite;

import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointTypes;
import com.google.common.base.Function;

public class LitePoint extends LiteBase {

    private String pointName;
    private int pointType;
    private int paobjectID;
    private int pointOffset;
    private int stateGroupID;
    private int uofmID = -1;
    private int decimalDigits;
    private Double multiplier;
    private Double dataOffset;

    private boolean showPointOffsets = true;

    // tags is used as a bit represention of data about this point
    private long tags = 0x00000000; // not used
    
    // used to represent a NON filler location for lite points
    public final static LitePoint NONE_LITE_PT = new LitePoint(
            PointTypes.SYS_PID_SYSTEM,
            "System Point",
            PointTypes.INVALID_POINT,
            0, 0, 0);

    public final static long POINT_UOFM_GRAPH = 0x00000001; // KW, KVAR, MVAR...
    public final static long POINT_UOFM_USAGE = 0x00000002; // KWH, KVAH, KVARH...
    
    public static Function<LitePoint, Integer> POINT_ID_FUNCTION = new Function<LitePoint, Integer>() {
        @Override
        public Integer apply(LitePoint litePoint) {
            return litePoint.getPointID();
        }
    };
    
    public LitePoint(int id) {
        super();
        setLiteID(id);
        setLiteType(LiteTypes.POINT);
    }

    public LitePoint(int id, String name, int type, int paoId, int offset, int stateGroupId) {
        super();

        setLiteID(id);
        setPointName(name);
        setPointType(type);
        setPaobjectID(paoId);
        setPointOffset(offset);
        setLiteType(LiteTypes.POINT);
        setStateGroupID(stateGroupId);
    }

    public LitePoint(int id, String name) {
        this(id);
        setPointName(name);
    }

    public LitePoint(int id, String name, int type, int paoId, int offset, int stateGroupId, long tag, int uomId) {
        super();

        setLiteID(id);
        setPointName(name);
        setPointType(type);
        setPaobjectID(paoId);
        setPointOffset(offset);
        setLiteType(LiteTypes.POINT);
        setStateGroupID(stateGroupId);
        setUofmID(uomId);
        setTags(tag);
    }
    
    public LitePoint(int id, String name, PointType pointType) {
        this(id);
        setPointName(name);
        setPointType(pointType.getPointTypeId());
    }

    public int getPointID() {
        return getLiteID();
    }
    
    public void setPointID(int newValue) {
        setLiteID(newValue);
    }

    public PointType getPointTypeEnum() {
        return PointType.getForId(pointType);
    }
    
    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public int getPointType() {
        return pointType;
    }

    public void setPointType(int pointType) {
        this.pointType = pointType;
    }

    public int getPaobjectID() {
        return paobjectID;
    }

    public void setPaobjectID(int paobjectID) {
        this.paobjectID = paobjectID;
    }

    public int getPointOffset() {
        return pointOffset;
    }

    public void setPointOffset(int pointOffset) {
        this.pointOffset = pointOffset;
    }

    public int getStateGroupID() {
        return stateGroupID;
    }

    public void setStateGroupID(int stateGroupID) {
        this.stateGroupID = stateGroupID;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }

    public Double getDataOffset() {
        return dataOffset;
    }

    public void setDataOffset(Double dataOffset) {
        this.dataOffset = dataOffset;
    }

    public boolean isShowPointOffsets() {
        return showPointOffsets;
    }

    public void setShowPointOffsets(boolean showPointOffsets) {
        this.showPointOffsets = showPointOffsets;
    }
    
    public int getUofmID() {
        return uofmID;
    }

    public void setUofmID(int i) {
        uofmID = i;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public long getTags() {
        return tags;
    }

    public void setTags(long tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        if (showPointOffsets) {
            if (getPointTypeEnum() == PointType.CalcAnalog
                    || getPointTypeEnum() == PointType.CalcStatus) {
                return getPointName();
            } else {
                if (getPointOffset() == 0) {
                    return "#p" + " " + pointName;
                } else {
                    return "#" + getPointOffset() + " " + pointName;
                }
            }

        } else {
            return getPointName();
        }
    }

}