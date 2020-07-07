package com.cannontech.web.tools.points.model;

import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.point.Point;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(value = { "pointId" }, allowGetters = true, ignoreUnknown = true)

public class CopyPoint {
    private String pointName;
    private Integer pointOffset;
    private Integer pointId;
    private Integer paoId;

    public Integer getPaoId() {
        return paoId;
    }

    public void setPaoId(Integer paoId) {
        this.paoId = paoId;
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

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public void buildModel(PointBase point) {
       // Point pt = ((PointBase) point).getPoint();
        Point pt =  point.getPoint();
        setPointName(pt.getPointName());
        setPointId(pt.getPointID());
        setPaoId(pt.getPaoID());
        setPointOffset(pt.getPointOffset());
    }

    public void buildDBPersistent(PointBase point) {
        //Point pt = ((PointBase) point).getPoint();
        Point pt =  point.getPoint();
        if (getPointName() != null) {
            pt.setPointName(getPointName());
        }
        if (getPointId() != null) {
            pt.setPointID(getPointId());
        }
        if (getPaoId() != null) {
            pt.setPaoID(getPaoId());
        }
        
        if (getPointOffset() != null) {
            pt.setPointOffset(getPointOffset());
        }
    }

}
