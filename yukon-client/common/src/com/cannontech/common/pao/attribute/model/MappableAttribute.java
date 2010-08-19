package com.cannontech.common.pao.attribute.model;

import com.cannontech.common.search.FilterType;

public class MappableAttribute {

    Attribute attribute;
    FilterType filterType;
    
    public MappableAttribute(Attribute attribute, FilterType filterType) {
        this.attribute = attribute;
        this.filterType = filterType;
    }
    
    public Attribute getAttribute() {
        return attribute;
    }
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
    public FilterType getFilterType() {
        return filterType;
    }
    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }
}
