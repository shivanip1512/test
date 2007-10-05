package com.cannontech.common.device.config.model;

import java.text.SimpleDateFormat;

import com.cannontech.common.device.config.dao.ConfigurationType;

/**
 * Base class for device configuration backing beans
 */
public abstract class ConfigurationBase {

    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("mm/dd/yyyy");

    private Integer id = null;
    private String name = "";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract ConfigurationType getType();

}
