package com.cannontech.common.device.config.model;

/**
 * A small model object for the Heartbeat configuration data.
 */
public class HeartbeatConfiguration extends LightDeviceConfiguration {
    private final static long serialVersionUID = 1L;

    private String mode;
    private int period;
    private int value;
    
    public HeartbeatConfiguration(Integer configurationId, String name, String description) {
        super(configurationId, name, description);
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}