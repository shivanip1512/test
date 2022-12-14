package com.cannontech.common.dr.program.setup.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value={ "subordinateProgName"}, allowGetters= true, ignoreUnknown = true)
public class ProgramDirectMemberControl {

    private Integer subordinateProgId;
    private String subordinateProgName;

    public Integer getSubordinateProgId() {
        return subordinateProgId;
    }

    public void setSubordinateProgId(Integer subordinateProgId) {
        this.subordinateProgId = subordinateProgId;
    }

    public String getSubordinateProgName() {
        return subordinateProgName;
    }

    public void setSubordinateProgName(String subordinateProgName) {
        this.subordinateProgName = subordinateProgName;
    }

}
