package com.cannontech.common.device.virtualDevice;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.data.device.VirtualDevice;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(value= {"id"}, allowGetters=true, ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@JsonDeserialize
public class VirtualDeviceModel extends VirtualDeviceBaseModel<VirtualDevice> implements DBPersistentConverter<VirtualDevice> {

    @Override
    public void buildModel(VirtualDevice virtualDevice) {
        super.buildModel(virtualDevice);
    }

    @Override
    public void buildDBPersistent(VirtualDevice virtualDevice) {
        super.buildDBPersistent(virtualDevice);
    }
}
