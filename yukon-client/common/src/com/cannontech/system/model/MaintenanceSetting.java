package com.cannontech.system.model;

import com.cannontech.maintenance.MaintenanceSettingType;

public class MaintenanceSetting {
    private Integer taskPropertyId;
    private Integer taskId;
    private MaintenanceSettingType attribute;
    private Object attributeValue;

    public MaintenanceSetting() {

    }

    public MaintenanceSetting(MaintenanceSettingType attribute, Object attributeValue) {
        this.attribute = attribute;
        this.attributeValue = attributeValue;
    }

    public Integer getTaskPropertyId() {
        return taskPropertyId;
    }

    public void setTaskPropertyId(Integer taskPropertyId) {
        this.taskPropertyId = taskPropertyId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Object getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(Object attributeValue) {
        this.attributeValue = attributeValue;
    }

    public MaintenanceSettingType getAttribute() {
        return attribute;
    }

    public void setAttribute(MaintenanceSettingType attribute) {
        this.attribute = attribute;
    }

}
