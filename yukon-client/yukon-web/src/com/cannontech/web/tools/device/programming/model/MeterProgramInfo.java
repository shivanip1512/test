package com.cannontech.web.tools.device.programming.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.device.programming.model.MeterProgramSource;

public class MeterProgramInfo {
    // guid is null if the guid doesn't match guid in MeterProgram table
    private String guid;
    // name is always pre-filled with either a name from MeterProgram or
    // MeterProgramSource enum value
    private String name;
    private MeterProgramSource source;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MeterProgramSource getSource() {
        return source;
    }

    public void setSource(MeterProgramSource source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
