package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZeusGroup {

    @JsonProperty("id") private String groupId;
    private String name;
    @JsonProperty("program_id") private String programId;
    @JsonProperty("parent_tstatgroup_id") private String parentGroupId;

    public ZeusGroup(String name, String programId) {
        this.name = name;
        this.programId = programId;
    }

    public ZeusGroup() {
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

    public String getParentGroupId() {
        return parentGroupId;
    }

    public void setParentGroupId(String parentGroupId) {
        this.parentGroupId = parentGroupId;
    }

}