package com.cannontech.web.amr.meterEventsReport.model;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.PointValueHolder;

public class MeterReportEvent {

    private Meter meter;
    private BuiltInAttribute attribute;
    private PointValueHolder pointValueHolder;
    private String formattedValue;

    public Meter getMeter() {
        return meter;
    }

    public void setMeter(Meter meter) {
        this.meter = meter;
    }

    public BuiltInAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(BuiltInAttribute attribute) {
        this.attribute = attribute;
    }

    public PointValueHolder getPointValueHolder() {
        return pointValueHolder;
    }

    public void setPointValueHolder(PointValueHolder pointValueHolder) {
        this.pointValueHolder = pointValueHolder;
    }

    public String getFormattedValue() {
        return formattedValue;
    }

    public void setFormattedValue(String formattedValue) {
        this.formattedValue = formattedValue;
    }

}
