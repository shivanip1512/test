package com.cannontech.common.device.virtualMeter;

import org.apache.commons.lang3.BooleanUtils;

import com.cannontech.common.device.model.DeviceBaseModel;
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
public class VirtualMeterModel extends DeviceBaseModel implements DBPersistentConverter<VirtualMeter>{

    private String meterNumber;
    
    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }
    
    public String getMeterNumber() {
        return meterNumber;
    }

    @Override
    public void buildModel(VirtualMeter virtualMeter) {
        setId(virtualMeter.getPAObjectID());
        setName(virtualMeter.getPAOName());
        setEnable(virtualMeter.getPAODisableFlag() == 'N' ? true : false);
        setType(virtualMeter.getPaoType());
        setMeterNumber(virtualMeter.getDeviceMeterGroup().getMeterNumber());
    }

    @Override
    public void buildDBPersistent(VirtualMeter virtualMeter) {
        if (getId() != null) {
            virtualMeter.setDeviceID(getId());
        }
        if (getName() != null) {
            virtualMeter.setPAOName(getName());
        }
        if (getEnable() != null) {
            virtualMeter.setDisableFlag(BooleanUtils.isFalse(getEnable()) ? 'Y' : 'N');
        }
        if (getMeterNumber() != null) {
            DeviceMeterGroup deviceMeterGroup = new DeviceMeterGroup();
            deviceMeterGroup.setDeviceID(getId());
            deviceMeterGroup.setMeterNumber(getMeterNumber());
            virtualMeter.setDeviceMeterGroup(deviceMeterGroup);
        }
    }

}
