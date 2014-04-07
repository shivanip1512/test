package com.cannontech.amr.archivedValueExporter.model;

import java.util.List;

import com.cannontech.common.util.LazyList;

public class AttributeList {

    private List<ExportAttribute> attributes =  LazyList.ofInstance(ExportAttribute.class);
    
    public List<ExportAttribute> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(List<ExportAttribute> attributes) {
        this.attributes = attributes;
    }
    
}