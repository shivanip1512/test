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
    private SchedulableThermostatType schedulableThermostatType;

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

    public void setType(HardwareType hardwareType) {
        this.type = hardwareType;
        this.schedulableThermostatType = SchedulableThermostatType.getByHardwareType(hardwareType);
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
        return ThermostatScheduleCompatibility.getCompatibleTypes(this.type);
    }

    public void setSchedulableThermostatType(SchedulableThermostatType schedulableThermostatType) {
        this.schedulableThermostatType = schedulableThermostatType;
    }

    public SchedulableThermostatType getSchedulableThermostatType() {
        return schedulableThermostatType;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((deviceLabel == null) ? 0 : deviceLabel.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + routeId;
        result = prime * result + ((schedulableThermostatType == null) ? 0 : schedulableThermostatType.hashCode());
        result = prime * result + ((serialNumber == null) ? 0 : serialNumber.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Thermostat other = (Thermostat) obj;
        if (category != other.category)
            return false;
        if (deviceLabel == null) {
            if (other.deviceLabel != null)
                return false;
        } else if (!deviceLabel.equals(other.deviceLabel))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (routeId != other.routeId)
            return false;
        if (schedulableThermostatType != other.schedulableThermostatType)
            return false;
        if (serialNumber == null) {
            if (other.serialNumber != null)
                return false;
        } else if (!serialNumber.equals(other.serialNumber))
            return false;
        if (status != other.status)
            return false;
        if (type != other.type)
            return false;
        return true;
    }
}
