package com.cannontech.common.chart.model;

import com.cannontech.common.device.attribute.model.BuiltInAttribute;

public class AttributeGraphType {

    private BuiltInAttribute attribute = null;
    private GraphType graphType = null;
    private String label = null;
    private String description = null;

    public BuiltInAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(BuiltInAttribute attribute) {
        this.attribute = attribute;
    }

    public GraphType getGraphType() {
        return graphType;
    }

    public void setGraphType(GraphType graphType) {
        this.graphType = graphType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return this.label;
    }
}
