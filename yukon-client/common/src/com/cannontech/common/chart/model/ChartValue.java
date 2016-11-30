package com.cannontech.common.chart.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;


public class ChartValue<T> {
    private long id = 0;
    private T value = null;
    private String formattedValue = null;
    private String description = null;
    private long time = 0;

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

    /**
     * Returns the description as set by setDescription with formattedValue prepended.
     */
    public String getDescription() {
        return "<div>" + formattedValue + "</div>" + description;
    }

    /**
     * Returns the description as set by setDescription.
     */
    public String getDescriptionWithoutFormattedVal() {
        return  description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormattedValue() {
        return formattedValue;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

	public void setFormattedValue(String formattedValue) {
		this.formattedValue = formattedValue;
	}
	
	@Override
	public String toString() {
		return "(Value:" + formattedValue + ", Time:" + new DateTime(time).toString(DateTimeFormat.mediumDateTime()) + ")";
	}

}
