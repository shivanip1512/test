package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZeusThermostatGroup {
    String name;
    @JsonProperty("program_id")
    String programId;
    @JsonProperty("criteria_selector")
    CriteriaSelector criteriaSelector;

    public ZeusThermostatGroup(String name, String programId, CriteriaSelector criteriaSelector) {
        this.name = name;
        this.programId = programId;
        this.criteriaSelector = criteriaSelector;
    }

    public ZeusThermostatGroup() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public CriteriaSelector getCriteriaSelector() {
        return criteriaSelector;
    }

    public void setCriteriaSelector(CriteriaSelector criteriaSelector) {
        this.criteriaSelector = criteriaSelector;
    }
}
