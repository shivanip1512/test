package com.cannontech.messaging.message.capcontrol.streamable;

import com.cannontech.common.util.CtiUtilities;

public abstract class StreamableCapObject {

    private int ccId;
    private String ccCategory = null;
    private String ccClass = null;
    private String ccName = null;
    private String ccType = null;
    private String ccDescription = null;
    private boolean ccDisableFlag;
    private int parentId = CtiUtilities.NONE_ZERO_ID;

    protected StreamableCapObject() {
        super();
    }

    public StreamableCapObject(int id, String category, String clazz, String name, String type, String description,
                               boolean disabled) {
        super();

        setCcId(id);
        setCcCategory(category);
        setCcClass(clazz);
        setCcName(name);
        setCcType(type);
        setCcDescription(description);
        setCcDisableFlag(disabled);
    }

    public boolean equals(Object obj) {
        if (obj instanceof StreamableCapObject) {
            return getCcId() == ((StreamableCapObject) obj).getCcId();
        }
        else {
            return super.equals(obj);
        }
    }

    public int hashCode() {
        return getCcId();
    }

    public String toString() {
        if (getCcName() != null) {
            return getCcName();
        }
        else {
            return super.toString();
        }
    }

    public int getCcId() {
        return ccId;
    }

    public void setCcId(int ccId) {
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

    public String getCcDescription() {
        return ccDescription;
    }

    public void setCcDescription(String description) {
        this.ccDescription = description;
    }

    public boolean getCcDisableFlag() {
        return ccDisableFlag;
    }

    public void setCcDisableFlag(Boolean ccDisableFlag) {
        this.ccDisableFlag = ccDisableFlag;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentdId) {
        this.parentId = parentdId;
    }

}
