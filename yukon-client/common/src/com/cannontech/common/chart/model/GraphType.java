package com.cannontech.common.chart.model;

import com.cannontech.common.chart.service.ChartDataConverter;
import com.cannontech.common.chart.service.impl.ChartDefaultConverter;
import com.cannontech.common.chart.service.impl.ChartNormalizedDeltaConverter;

public enum GraphType {

    RAW_LINE("Raw") {
        public ChartDataConverter getDataConverter() {
            return new ChartDefaultConverter();
        }
    },
    NORMALIZED_DELTA_LINE("Normalized") {
        public ChartDataConverter getDataConverter() {
            return new ChartNormalizedDeltaConverter();
        }
    };

    private String label = null;

    private GraphType(String label) {
        this.label = label;
    }

    public abstract ChartDataConverter getDataConverter();

    public String getLabel() {
        return label;
    }
}
