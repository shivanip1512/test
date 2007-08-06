package com.cannontech.yukon.cbc;

public class CBCArea extends StreamableCapObject {

    private Boolean disableFlag;
    private Boolean ovUvDisabledFlag;
    private String paoDescription;
    private String paoType;
    private String paoName;
    private String paoCategory;
    private Integer paoID;
    private String paoClass;

    public void setPaoID(Integer integer) {
        paoID = integer;
    }

    public void setPaoCategory(String string) {
        paoCategory = string;
    }

    public void setPaoName(String string) {
        paoName = string;
    }

    public void setPaoType(String string) {
        paoType = string;
    }

    public void setPaoDescription(String string) {
        paoDescription = string;
    }

    public void setDisableFlag(Boolean b) {
        disableFlag = b;
    }

    public Boolean getDisableFlag() {
        return disableFlag;
    }

    public String getPaoCategory() {
        return paoCategory;
    }

    public String getPaoDescription() {
        return paoDescription;
    }

    public Integer getPaoID() {
        return paoID;
    }

    public String getPaoName() {
        return paoName;
    }

    public String getPaoType() {
        return paoType;
    }

    public void setPaoClass(String string) {
        paoClass = string;
    }

    public String getPaoClass() {
        return paoClass;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CBCArea) {
            CBCArea area = (CBCArea) obj;
            return area.getPaoID().equals(getPaoID());
        }
        return false;
    }

    public CBCArea copy() {
        CBCArea copy = new CBCArea();
        copy.setPaoID(getPaoID());
        copy.setPaoCategory(getPaoCategory());
        copy.setPaoClass(getPaoClass());
        copy.setPaoName(getPaoName());
        copy.setPaoType(getPaoType());
        copy.setPaoDescription(getPaoDescription());
        copy.setDisableFlag(getDisableFlag());
        return copy;
    }

	public Boolean getOvUvDisabledFlag() {
		return ovUvDisabledFlag;
	}

	public void setOvUvDisabledFlag(Boolean ovUvDisabledFlag) {
		this.ovUvDisabledFlag = ovUvDisabledFlag;
	}

}
