package com.cannontech.common.chart.model;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormattedValue() {
        return this.formattedValue;
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

}
