package com.cannontech.web.widget.meterInfo.model;

import com.cannontech.amr.meter.model.VirtualMeter;

public class VirtualMeterModel extends MeterModel {

    public static VirtualMeterModel of(VirtualMeter meter) {

        VirtualMeterModel virtualMeterModel = new VirtualMeterModel();
        virtualMeterModel.setDeviceId(meter.getDeviceId());
        virtualMeterModel.setDisabled(meter.isDisabled());
        virtualMeterModel.setMeterNumber(meter.getMeterNumber());
        virtualMeterModel.setName(meter.getName());
        return virtualMeterModel;
    }
}
