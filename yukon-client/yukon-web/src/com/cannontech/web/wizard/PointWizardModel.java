package com.cannontech.web.wizard;

import com.cannontech.database.data.point.PointTypes;

public class PointWizardModel {

    private String name = null;
    private Boolean disabled = new Boolean(false);
    private int pointType = PointTypes.INVALID_POINT;
    private Integer parentId = new Integer(0);

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPointType() {
        return pointType;
    }

    public void setPointType(int wizPointType) {
        this.pointType = wizPointType;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public void reset() {
        name = null;
        disabled = new Boolean(false);
        pointType = PointTypes.INVALID_POINT;
        parentId = new Integer(0);
    }
}
