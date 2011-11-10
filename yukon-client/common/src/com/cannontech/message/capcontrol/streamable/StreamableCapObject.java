package com.cannontech.message.capcontrol.streamable;

import com.cannontech.common.util.CtiUtilities;

public abstract class StreamableCapObject {
	
	private Integer ccId = null;
	private String ccCategory = null;
	private String ccClass = null;
	private String ccName = null;
	private String ccType = null;
	private String ccArea = null;
	private Boolean ccDisableFlag = null;
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
    
    public boolean equals(Object obj) {
    	if(obj instanceof StreamableCapObject) {
    		return getCcId().equals(((StreamableCapObject)obj).getCcId());
    	} else {
    		return super.equals(obj);
    	}
    }
    
    public int hashCode() {
    	if( getCcId() != null) {
    		return getCcId().intValue();
    	} else {
    		return super.hashCode();
    	}
    }
    
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

    public Boolean getCcDisableFlag() {
        return ccDisableFlag;
    }

    public void setCcDisableFlag(Boolean ccDisableFlag) {
        this.ccDisableFlag = ccDisableFlag;
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }
    
}