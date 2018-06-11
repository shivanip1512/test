package com.cannontech.common.chart.model;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public class AttributeGraphType {

    private BuiltInAttribute attribute = null;
    private GraphType graphType = null;
    private ConverterType converterType = null;
    private String baseKey = "yukon.web.attributeGraphType.";

    public BuiltInAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(BuiltInAttribute attribute) {
        this.attribute = attribute;
    }

    public String getLabel() {
        return baseKey + getMessageKey() + ".label";
    }

    private String getMessageKey() {
        if (getConverterType() == ConverterType.DAILY_USAGE) {
            return ConverterType.DAILY_USAGE.name();
        } else {
            return attribute.name();
        }
    }

    public String getDescription() {
    	return baseKey +  getMessageKey() + ".description";
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
