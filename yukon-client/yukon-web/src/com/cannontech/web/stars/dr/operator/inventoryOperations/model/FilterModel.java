package com.cannontech.web.stars.dr.operator.inventoryOperations.model;

import java.util.List;

import com.cannontech.common.util.LazyList;

public class FilterModel {
    
    private FilterMode filterMode = FilterMode.INTERSECT;
    private List<RuleModel> filterRules = LazyList.ofInstance(RuleModel.class);
    
    public FilterMode getFilterMode() {
        return filterMode;
    }

    public void setFilterMode(FilterMode filterMode) {
        this.filterMode = filterMode;
    }

    public List<RuleModel> getFilterRules() {
        return filterRules;
    }

    public void setFilterRules(List<RuleModel> filterRules) {
        this.filterRules = filterRules;
    }
    
}