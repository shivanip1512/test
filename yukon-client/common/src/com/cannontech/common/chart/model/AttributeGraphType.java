package com.cannontech.common.chart.model;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public class AttributeGraphType {

    private BuiltInAttribute attribute = null;
    private GraphType graphType = null;
    private ConverterType converterType = null;
    private String label = null;
    private String description = null;
    private String baseKey = "yukon.web.attributeGraphType.";

    public BuiltInAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(BuiltInAttribute attribute) {
        this.attribute = attribute;
    }

    public String getLabel() {
        return baseKey + attribute.name() + ".label";
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
    	return baseKey +  attribute.name() + ".description";
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setConverterType(ConverterType converterType) {
        this.converterType = converterType;
    }
    
    public ConverterType getConverterType() {
        return converterType;
    }
    
    public void setGraphType(GraphType graphType) {
        this.graphType = graphType;
    }
    
    public GraphType getGraphType() {
        return graphType;
    }
    
    public String toString() {
        return this.attribute.name();
    }
}
