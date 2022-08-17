package com.cannontech.common.dr.setup;

import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "id" }, allowGetters = true)
public class MacroLoadGroup {

    private Integer id;
    private String name;
    private PaoType type;
    private List<LMPaoDto> assignedLoadGroups;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
    }

    public List<LMPaoDto> getAssignedLoadGroups() {
        return assignedLoadGroups;
    }

    public void setAssignLoadGroups(List<LMPaoDto> assignedLoadGroups) {
        this.assignedLoadGroups = assignedLoadGroups;
    }

}
