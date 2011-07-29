package com.cannontech.stars.dr.hardware.model;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryCategory;

/**
 * Thermostat data transfer object
 */
public class Thermostat {

    private Integer id;
    private String serialNumber;
    private String deviceLabel;
    private HardwareType type;
    private InventoryCategory category;
    private int routeId;
    private HardwareStatus status;
    private Set<SchedulableThermostatType> schedulableThermostatTypes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDeviceLabel() {
        return deviceLabel;
    }

    public void setDeviceLabel(String deviceLabel) {
        this.deviceLabel = deviceLabel;
    }

    public HardwareType getType() {
        return type;
    }

    public void setType(HardwareType type) {
        this.type = type;
        this.schedulableThermostatTypes = ThermostatScheduleCompatibility.getCompatibleTypes(type);
    }

    public InventoryCategory getCategory() {
        return category;
    }

    public void setCategory(InventoryCategory category) {
        this.category = category;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public HardwareStatus getStatus() {
        return status;
    }

    public void setStatus(HardwareStatus status) {
        this.status = status;
    }

    /**
     * Method to get the thermostat label which is the device label or the
     * serial number
     * @return Label
     */
    public String getLabel() {

        if (!StringUtils.isBlank(deviceLabel)) {
            return deviceLabel;
        }

        return serialNumber;
    }

    /**
     * Method used to determine if this device is available
     * @return
     */
    public boolean isAvailable() {
        return status != HardwareStatus.UNAVAILABLE;
    }

    public Set<SchedulableThermostatType> getCompatibleSchedulableThermostatTypes(){
        return this.schedulableThermostatTypes;
    }
}
