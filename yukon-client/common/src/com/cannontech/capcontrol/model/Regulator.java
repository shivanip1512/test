package com.cannontech.capcontrol.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.capcontrol.VoltageRegulator;

public class Regulator {

    private Integer id;
    private String name;
    private PaoType type;
    private String description;
    private double voltChangePerTap = 0.75;
    private int keepAliveConfig;
    private int keepAliveTimer;
    private boolean disabled;
    private int configId;
    private Map<RegulatorPointMapping, Integer> mappings = new LinkedHashMap<>();

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
    public PaoType getType() {
        return type;
    }
    public void setType(PaoType type) {
        this.type = type;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public double getVoltChangePerTap() {
        return voltChangePerTap;
    }
    public void setVoltChangePerTap(double voltChangePerTap) {
        this.voltChangePerTap = voltChangePerTap;
    }
    public int getKeepAliveConfig() {
        return keepAliveConfig;
    }
    public void setKeepAliveConfig(int keepAliveConfig) {
        this.keepAliveConfig = keepAliveConfig;
    }
    public int getKeepAliveTimer() {
        return keepAliveTimer;
    }
    public void setKeepAliveTimer(int keepAliveTimer) {
        this.keepAliveTimer = keepAliveTimer;
    }
    public boolean isDisabled() {
        return disabled;
    }
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    public int getConfigId() {
        return configId;
    }
    public void setConfigId(int configId) {
        this.configId = configId;
    }
    public Map<RegulatorPointMapping, Integer> getMappings() {
        return mappings;
    }
    public void setMappings(Map<RegulatorPointMapping, Integer> mappings) {
        this.mappings = mappings;
    }

    public VoltageRegulator asDbPersistent() {

        VoltageRegulator voltageRegulator = new VoltageRegulator(getType());

        voltageRegulator.setCapControlPAOID(getId());
        voltageRegulator.setDisabled(isDisabled());
        voltageRegulator.setKeepAliveConfig(getKeepAliveConfig());
        voltageRegulator.setKeepAliveTimer(getKeepAliveTimer());
        voltageRegulator.setName(getName());
        voltageRegulator.setPAODescription(getDescription());
        voltageRegulator.setVoltChangePerTap(getVoltChangePerTap());
        voltageRegulator.setPointMappings(getMappings());

        return voltageRegulator;
    }

    public static Regulator fromDbPersistent(VoltageRegulator voltageRegulator) {

        Regulator regulator = new Regulator();

        regulator.setType(voltageRegulator.getPaoType());
        regulator.setId(voltageRegulator.getPAObjectID());
        regulator.setDisabled(voltageRegulator.isDisabled());
        regulator.setKeepAliveConfig(voltageRegulator.getKeepAliveConfig());
        regulator.setKeepAliveTimer(voltageRegulator.getKeepAliveTimer());
        regulator.setName(voltageRegulator.getPAOName());
        regulator.setDescription(voltageRegulator.getPAODescription());
        regulator.setVoltChangePerTap(voltageRegulator.getVoltChangePerTap());
        regulator.setMappings(voltageRegulator.getPointMappings());

        return regulator;
    }

    @Override
    public String toString() {
        return String.format(
            "Regulator [id=%s, name=%s, type=%s, description=%s, voltChangePerTap=%s, keepAliveConfig=%s, keepAliveTimer=%s, disabled=%s, mappings=%s]",
            id, name, type, description, voltChangePerTap, keepAliveConfig, keepAliveTimer, disabled, mappings);
    }
}
