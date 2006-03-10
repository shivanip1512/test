package com.cannontech.web.wizard;

import com.cannontech.database.data.point.PointTypes;

public class PointWizardModel {

    private String name = null;
    private Boolean disabled = new Boolean(false);
    private Integer pointType = new Integer (PointTypes.INVALID_POINT);
    private int subType = PointTypes.CALCULATED_POINT;
    private Integer parentId = new Integer (0);
    public PointWizardModel() {
        super();
       
    }


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

    public Integer getPointType() {            
            return pointType;
    }

    public void setPointType(Integer wizPointType) {
        if (isSubtypeNeeded()){
            this.pointType = new Integer ( getSubType() );            
        }
        else
            this.pointType = wizPointType;        
    }
    
    public boolean isSubtypeNeeded(){
        if (getPointType().intValue() == PointTypes.CALCULATED_POINT)
            return true;
        else
            return false;
    }

    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    
}
