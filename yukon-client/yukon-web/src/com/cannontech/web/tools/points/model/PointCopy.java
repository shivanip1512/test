package com.cannontech.web.tools.points.model;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.point.Point;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PointCopy implements DBPersistentConverter<PointBase> {
    private String pointName;
    private Integer pointOffset;
    private Integer paoId;

    public Integer getPaoId() {
        return paoId;
    }

    public void setPaoId(Integer paoId) {
        this.paoId = paoId;
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
    
    @Override
    public void buildDBPersistent(PointBase point) {
        Point pt = point.getPoint();
        if (getPointName() != null) {
            pt.setPointName(getPointName());
        }

        if (getPaoId() != null) {
            pt.setPaoID(getPaoId());
        }

        if (getPointOffset() != null) {
            pt.setPointOffset(getPointOffset());
        }
    }

    @Override
    public void buildModel(PointBase object) {
    }
}
