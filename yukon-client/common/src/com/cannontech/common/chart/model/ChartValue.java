package com.cannontech.common.chart.model;

import java.text.Format;

public class ChartValue<T> {
    private long id = 0;
    private T value = null;
    private String description = null;
    private long time = 0;

    private Format format = null;

    public ChartValue() {
    }

    public ChartValue(long id, T value) {
        this.id = id;
        this.value = value;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public String getFormattedValue() {
        return format.format(value);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
