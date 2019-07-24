package com.cannontech.common.dr.setup;

import java.util.List;

import com.cannontech.common.pao.PaoType;

public class LMSetupFilter {

    private LmSetupFilterType filterByType;
    private String name;
    private List<PaoType> types;

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

}
