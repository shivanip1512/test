package com.cannontech.dr.ecobee.message;

public class ZeusThermostatGroup {
    String name;
    String programId;

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

    CriteriaSelector criteriaSelector;

    public ZeusThermostatGroup(String name, String programId, CriteriaSelector criteriaSelector) {
        super();
        this.name = name;
        this.programId = programId;
        this.criteriaSelector = criteriaSelector;
    }

}
