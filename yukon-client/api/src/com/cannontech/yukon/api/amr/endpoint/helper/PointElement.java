package com.cannontech.yukon.api.amr.endpoint.helper;

import java.util.List;

public class PointElement {
    private PointSelector pointSelector;
    private List<PointValueSelector> pointValueSelectors;

    public PointElement(PointSelector pointSelector, List<PointValueSelector> pointValueSelectors) {
        this.pointSelector = pointSelector;
        this.pointValueSelectors = pointValueSelectors;
    }

    public PointSelector getPointSelector() {
        return pointSelector;
    }

    public void setPointSelector(PointSelector pointSelector) {
        this.pointSelector = pointSelector;
    }

    public List<PointValueSelector> getPointValueSelectors() {
        return pointValueSelectors;
    }

    public void setPointValueSelectors(List<PointValueSelector> pointValueSelectors) {
        this.pointValueSelectors = pointValueSelectors;
    }
}
