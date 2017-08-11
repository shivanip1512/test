package com.cannontech.amr.meter.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.database.data.device.IEDMeter;

/** 
 * This is the basic "ied" (intelligent electronic device) type meter (Examples: See those that extend {@link IEDMeter})
 */
public class IedMeter extends YukonMeter {
    private Integer portId;
    private String port = "";
    public IedMeter() {
        super();
    }

    public IedMeter(PaoIdentifier paoIdentifier, String meterNumber, String name, boolean disabled) {
        super(paoIdentifier, meterNumber, name, disabled);
    }

    /**
     * Not implemented for this object. Could be part of the DeviceIED data.
     */
    @Override
    public String getSerialOrAddress() {
        return null;
    }

    /**
     * Ied meters do not have a route, returns null
     */
    @Override
    public String getRoute() {
        return null;
    }

    public Integer getPortId() {
        return portId;
    }

    public void setPortId(Integer portId) {
        this.portId = portId;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.appendSuper(super.toString());
        return tsb.toString();
    }
}