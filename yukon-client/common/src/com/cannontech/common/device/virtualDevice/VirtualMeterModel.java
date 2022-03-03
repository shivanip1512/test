package com.cannontech.common.device.virtualDevice;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.data.device.VirtualMeter;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(value= {"id"}, allowGetters=true, ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@JsonDeserialize
public class VirtualMeterModel extends VirtualDeviceBaseModel<VirtualMeter> implements DBPersistentConverter<VirtualMeter> {

    private String meterNumber;

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    @Override
    public void buildModel(VirtualMeter virtualMeter) {
        super.buildModel(virtualMeter);
        setMeterNumber(virtualMeter.getDeviceMeterGroup().getMeterNumber());
    }

    @Override
    public void buildDBPersistent(VirtualMeter virtualMeter) {
        super.buildDBPersistent(virtualMeter);
        if (getMeterNumber() != null) {
            DeviceMeterGroup deviceMeterGroup = new DeviceMeterGroup();
            deviceMeterGroup.setDeviceID(getDeviceId());
            deviceMeterGroup.setMeterNumber(getMeterNumber());
            virtualMeter.setDeviceMeterGroup(deviceMeterGroup);
        }
    }

}
