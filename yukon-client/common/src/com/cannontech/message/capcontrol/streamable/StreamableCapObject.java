package com.cannontech.message.capcontrol.streamable;

import com.cannontech.common.util.CtiUtilities;

public abstract class StreamableCapObject {

    private int ccId;
    private String ccCategory;
    private String ccClass;
    private String ccName;
    private String ccType;
    private String ccArea;
    private boolean ccDisableFlag;
    private int parentID = CtiUtilities.NONE_ZERO_ID;

    protected StreamableCapObject() {
        super();
    }

    public StreamableCapObject(int id, 
                               String category, 
                               String clazz, 
                               String name, 
                               String type, 
                               String description, 
                               boolean disabled) {
        super();

        setCcId(id);
        setCcCategory(category);
        setCcClass(clazz);
        setCcName(name);
        setCcType(type);
        setCcArea(description);
        setCcDisableFlag(disabled);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof StreamableCapObject) {
            return getCcId().equals(((StreamableCapObject)obj).getCcId());
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        if( getCcId() != null) {
            return getCcId().intValue();
        } else {
            return super.hashCode();
        }
    }

    @Override
    public String toString() {
        if(getCcName() != null) {
            return getCcName();
        } else {
            return super.toString();
        }
    }

    public Integer getCcId() {
        return ccId;
    }

    public void setCcId(Integer ccId) {
        this.ccId = ccId;
    }

    public String getCcCategory() {
        return ccCategory;
    }

    public void setCcCategory(String ccCategory) {
        this.ccCategory = ccCategory;
    }

    public String getCcClass() {
        return ccClass;
    }

    public void setCcClass(String ccClass) {
        this.ccClass = ccClass;
    }

    public String getCcName() {
        return ccName;
    }

    public void setCcName(String ccName) {
        this.ccName = ccName;
    }

    public String getCcType() {
        return ccType;
    }

    public void setCcType(String ccType) {
        this.ccType = ccType;
    }

    public String getCcArea() {
        return ccArea;
    }

    public void setCcArea(String ccArea) {
        this.ccArea = ccArea;
    }

    public boolean getCcDisableFlag() {
        return ccDisableFlag;
    }

    public void setCcDisableFlag(boolean ccDisableFlag) {
        this.ccDisableFlag = ccDisableFlag;
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

}