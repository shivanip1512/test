package com.cannontech.common.device.virtualDevice;

import org.apache.commons.lang3.BooleanUtils;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.common.device.port.JsonDeserializePaoTypeLookup;
import com.cannontech.database.data.device.VirtualBase;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(value= {"id"}, allowGetters=true, ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializePaoTypeLookup.class)
public class VirtualDeviceBaseModel<T extends VirtualBase> extends DeviceBaseModel implements DBPersistentConverter<T> {

    @Override
    public void buildModel(T virtualDevice) {
        setDeviceId(virtualDevice.getPAObjectID());
        setDeviceName(virtualDevice.getPAOName());
        setEnable(virtualDevice.getPAODisableFlag() == 'N' ? true : false);
        setDeviceType(virtualDevice.getPaoType());
    }

    @Override
    public void buildDBPersistent(T virtualDevice) {
        if (getDeviceId() != null) {
            virtualDevice.setDeviceID(getDeviceId());
        }
        if (getDeviceName() != null) {
            virtualDevice.setPAOName(getDeviceName());
        }
        if (getEnable() != null) {
            virtualDevice.setDisableFlag(BooleanUtils.isFalse(getEnable()) ? 'Y' : 'N');
        }
    }

}
