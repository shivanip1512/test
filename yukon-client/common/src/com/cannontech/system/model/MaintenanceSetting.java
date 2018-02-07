package com.cannontech.system.model;

import com.cannontech.maintenance.MaintenanceSettingType;
import com.cannontech.maintenance.MaintenanceTaskType;

public class MaintenanceSetting {
    private MaintenanceTaskType taskType;
    private MaintenanceSettingType attribute;
    private Object attributeValue;

    public MaintenanceSetting() {

    }

    public MaintenanceSetting(MaintenanceSettingType attribute, Object attributeValue) {
        this.attribute = attribute;
        this.attributeValue = attributeValue;
    }

    public MaintenanceTaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(MaintenanceTaskType taskType) {
        this.taskType = taskType;
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
