package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZeusThermostatGroup {

    @JsonProperty("group") private ZeusGroup group;
    @JsonProperty("criteria") private CriteriaSelector criteriaSelector;

    public ZeusThermostatGroup() {
    }

    public ZeusThermostatGroup(ZeusGroup group, CriteriaSelector criteriaSelector) {
        this.group = group;
        this.criteriaSelector = criteriaSelector;
    }

    public ZeusGroup getGroup() {
        return group;
    }

    public CriteriaSelector getCriteriaSelector() {
        return criteriaSelector;
    }

    public void setGroup(ZeusGroup group) {
        this.group = group;
    }

    public void setCriteriaSelector(CriteriaSelector criteriaSelector) {
        this.criteriaSelector = criteriaSelector;
    }

}
