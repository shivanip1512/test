package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZeusGroup {
    private String name;
    @JsonProperty("program_id") private String programId;

    public ZeusGroup(String name, String programId) {
        this.name = name;
        this.programId = programId;
    }

    public ZeusGroup() {
    }

    public String getName() {
        return name;
    }

    public String getProgramId() {
        return programId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

}