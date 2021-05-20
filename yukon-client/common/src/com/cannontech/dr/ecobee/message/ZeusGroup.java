package com.cannontech.dr.ecobee.message;

import com.cannontech.system.GlobalSettingType;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ZeusGroup {
    
    @JsonProperty("id")
    private String groupId;
    private String name;
    @JsonProperty("program_id") 
    private GlobalSettingType programId;

    public ZeusGroup(String name, GlobalSettingType ecobeeZeusProgramId) {
        this.name = name;
        this.programId = ecobeeZeusProgramId;
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

    public GlobalSettingType getProgramId() {
        return programId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProgramId(GlobalSettingType programId) {
        this.programId = programId;
    }

}