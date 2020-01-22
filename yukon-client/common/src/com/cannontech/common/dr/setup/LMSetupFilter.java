package com.cannontech.common.dr.setup;

import java.util.List;

import com.cannontech.common.dr.gear.setup.OperationalState;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class LMSetupFilter {

    private LmSetupFilterType filterByType;
    private String name;
    private List<PaoType> types;
    private List<GearControlMethod> gearTypes;
    private List<Integer> programIds;
    private List<OperationalState> operationalStates;

    public LmSetupFilterType getFilterByType() {
        return filterByType;
    }

    public void setFilterByType(LmSetupFilterType filterByType) {
        this.filterByType = filterByType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PaoType> getTypes() {
        return types;
    }

    public void setTypes(List<PaoType> types) {
        this.types = types;
    }

    public List<GearControlMethod> getGearTypes() {
        return gearTypes;
    }

    public void setGearTypes(List<GearControlMethod> gearTypes) {
        this.gearTypes = gearTypes;
    }

    public List<Integer> getProgramIds() {
        return programIds;
    }

    public void setProgramIds(List<Integer> programIds) {
        this.programIds = programIds;
    }

    public List<OperationalState> getOperationalStates() {
        return operationalStates;
    }

    public void setOperationalStates(List<OperationalState> operationalStates) {
        this.operationalStates = operationalStates;
    }
}
