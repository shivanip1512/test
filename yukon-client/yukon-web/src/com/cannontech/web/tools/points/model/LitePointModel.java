package com.cannontech.web.tools.points.model;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class LitePointModel {

    private Integer pointId;
    private String pointName;
    private PointType pointType;
    @JsonIgnore
    private boolean physicalOffset;
    private Integer pointOffset;
    private Integer paoId;

    public LitePointModel() {
        super();
    }

    public LitePointModel(String pointName, Integer pointId, boolean physicalOffset, Integer pointOffset,
            PointType pointType, Integer paoId) {
        super();
        this.pointName = StringUtils.trim(pointName);
        this.pointId = pointId;
        this.physicalOffset = physicalOffset;
        this.pointOffset = pointOffset;
        this.pointType = pointType;
        this.paoId = paoId;
    }

    public PointType getPointType() {
        return pointType;
    }

    public void setPointType(PointType pointType) {
        this.pointType = pointType;
    }

    public Integer getPaoId() {
        return paoId;
    }

    public void setPaoId(Integer paoId) {
        this.paoId = paoId;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = StringUtils.trim(pointName);
    }

    public Integer getPointId() {
        return pointId;
    }

    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }

    public Integer getPointOffset() {
        return pointOffset;
    }

    public void setPointOffset(Integer pointOffset) {
        this.pointOffset = pointOffset;
    }

    public boolean isPhysicalOffset() {
        return physicalOffset;
    }

    public void setPhysicalOffset(boolean physicalOffset) {
        this.physicalOffset = physicalOffset;
    }
    
    public static LitePointModel of(PointBase base) {
        return new LitePointModel(StringUtils.trim(base.getPoint().getPointName()), 
                                  base.getPoint().getPointID(),
                                  base.getPoint().isPhysicalOffset(), 
                                  base.getPoint().getPointOffset(),
                                  PointType.valueOf(base.getPoint().getPointType()), 
                                  base.getPoint().getPaoID());
    }

}
