package com.cannontech.common.chart.model;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class ChartValue<T> {
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS a");

    private long id = 0;
    private T value = null;
    private long time = 0;
    private String formattedValue = null;
    private String units = null;
    private String pointName = null;
    private String optionalData = null;

    public ChartValue() {
    }

    public ChartValue(long id, T value) {
        this.id = id;
        this.value = value;
    }

    public ChartValue(ChartValue<T> copyFrom) {
        this.id = copyFrom.id;
        this.value = copyFrom.value;
        this.time = copyFrom.time;
        this.formattedValue = copyFrom.formattedValue;
        this.units = copyFrom.units;
        this.pointName = copyFrom.pointName;
        this.optionalData =  copyFrom.optionalData;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFormattedValue() {
        return formattedValue;
    }

    public void setFormattedValue(String formattedValue) {
        this.formattedValue = formattedValue;
    }

    /**
     * @param units  will be used as part of description:
     * {@code <div>units</div><div>time</div><div>pointName</div>optionalData}
     */
    public void setUnits(String units) {
        this.units = units;
    }

    /**
     * @param pointName will be used as part of description:
     * {@code <div>units</div><div>time</div><div>pointName</div>optionalData}
     */
    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    /**
     * @param optionalData will be used as part of description:
     * {@code <div>units</div><div>time</div><div>pointName</div>optionalData}
     */
    public void setOptionalData(String optionalData) {
        this.optionalData = optionalData;
    }

    /**
     * Returns the html formatted description with formattedValue prepended.
     */
    public String getDescription() {
        return "<div>" + formattedValue + "</div>" + getDescriptionWithoutFormattedVal();
    }

    /**
     * Returns the html formatted description using chartValue meta-data.
     * {@code <div>units</div><div>time</div><div>pointName</div>optionalData}
     */
    public String getDescriptionWithoutFormattedVal() {
        long timeStamp = time;
        timeStamp -= TimeZone.getDefault().getOffset(timeStamp);
        return "<div>" + units + "</div><div>" + timeFormat.format(timeStamp) + "</div><div>" + pointName + "</div>" +
                (optionalData != null ? optionalData : "");
    }

    @Override
    public String toString() {
        return "(Value:" + formattedValue + ", Time:" + new DateTime(time).toString(DateTimeFormat.mediumDateTime()) + ")";
    }
}